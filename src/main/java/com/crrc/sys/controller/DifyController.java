package com.crrc.sys.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.exception.ParamValidateException;
import com.crrc.sys.model.agent.SysAgentPO;
import com.crrc.sys.service.AgentService;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Dify-控制器
 *
 * @author 谭永强
 * @date 2025-11-10
 */
@RestController
@RequestMapping("/dify")
public class DifyController {


    @Resource
    private AgentService agentService;
    private final WebClient webClient = WebClient.builder().build();
    private final static String AGENT_ID_HEADER = "X-Agent-Id";

    /**
     * 发送对话消息
     */
    @PostMapping(value = "/chat-messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Object> chatMessages(HttpServletRequest request, @RequestBody Map<String, Object> paramMap) {
        String agentId = request.getHeader(AGENT_ID_HEADER);
        if (ObjectUtils.isEmpty(agentId)) {
            throw new ParamValidateException("请求头缺少X-Agent-Id参数");
        }
        SysAgentPO agent = agentService.getById(agentId);
        //如果是针对于某一个特定服务操作，则可用直接指定该服务的地址
        return webClient
                .post()
                .uri(agent.getBaseUrl() + "/chat-messages")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + agent.getApiKey())
                .bodyValue(paramMap)
                .retrieve()
                .bodyToFlux(Object.class)
                .onErrorMap(e -> new BizException(e.getMessage()));
    }

    /**
     * 通用请求
     *
     * @param request 请求
     * @return 结果
     * @throws IOException 异常
     */
    @RequestMapping({
            "/chat-messages/{task_id}/stop",
            "/messages/{message_id}/feedbacks",
            "/app/feedbacks",
            "/messages/{message_id}/suggested",
            "messages",
            "conversations",
            "/conversations/{conversation_id}",
            "/conversations/{conversation_id}/name",
            "/conversations/{conversation_id}/variables",
            "/conversations/{conversation_id}/variables/{variable_id}",
            "/audio-to-text",
            "/text-to-audio",
            "/info",
            "/parameters",
            "/meta",
            "/site",
            "/apps/annotations",
            "/apps/annotations/{annotation_id}",
    })
    public Mono<Object> general(HttpServletRequest request) throws IOException {
        String agentId = request.getHeader(AGENT_ID_HEADER);
        if (ObjectUtils.isEmpty(agentId)) {
            throw new ParamValidateException("请求头缺少X-Agent-Id参数");
        }
        //请求地址
        final String oldUri = request.getRequestURI();
        //移除请求前缀
        String newUri = oldUri.replace("/dify", "");
        //请求方式
        HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());
        //请求参数(application/x-www-form-urlencoded、multipart/form-data)
        String urlParam = getUrlParam(request);
        if (!ObjectUtils.isEmpty(urlParam)) {
            newUri = (newUri + "?" + urlParam);
        }
        //请求体(application/json)
        final ServletInputStream inputStream = request.getInputStream();
        final String body = FileCopyUtils.copyToString(new InputStreamReader(inputStream));
        Map<String, Object> paramMap = new HashMap<>();
        if (!ObjectUtils.isEmpty(body)) {
            paramMap = JSON.parseObject(body, new TypeReference<Map<String, Object>>() {
            });
        }
        SysAgentPO agent = agentService.getById(agentId);
        return webClient
                .method(httpMethod)
                .uri(agent.getBaseUrl() + newUri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + agent.getApiKey())
                .bodyValue(paramMap)
                .retrieve()
                .bodyToMono(Object.class)
                .onErrorMap(e -> new BizException(e.getMessage()));
    }

    /**
     * 获取请求参数
     *
     * @param request 请求
     * @return 结果
     */
    private String getUrlParam(HttpServletRequest request) {
        final Map<String, String[]> parameterMap = request.getParameterMap();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            if (values.length > 0 && !ObjectUtils.isEmpty(values[0])) {
                sb.append("&").append(key).append("=").append(values[0]);
            }
        }
        if (ObjectUtils.isEmpty(sb)) {
            return "";
        }
        return sb.substring(1);
    }

    /**
     * 上传文件
     *
     * @param user             用户标识，用于定义终端用户的身份，必须和发送消息接口传入 user 保持一致。
     * @param multipartRequest 附件
     */
    @PostMapping("/files/upload")
    public Mono<Object> update2(String user, MultipartHttpServletRequest multipartRequest) throws Exception {
        HttpHeaders headers = multipartRequest.getRequestHeaders();
        String agentId = headers.getFirst(AGENT_ID_HEADER);
        if (ObjectUtils.isEmpty(agentId)) {
            throw new ParamValidateException("请求头缺少X-Agent-Id参数");
        }
        if (ObjectUtils.isEmpty(user)) {
            throw new ParamValidateException("user不能为空");
        }
        //构建文件上传参数
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        Iterator<String> iterator = multipartRequest.getFileNames();
        while (iterator.hasNext()) {
            //循环取出每一个文件
            MultipartFile multifile = multipartRequest.getFile(iterator.next());
            if (ObjectUtils.isEmpty(multifile)) {
                continue;
            }
            final byte[] byteArray = FileCopyUtils.copyToByteArray(multifile.getInputStream());
            final String originalFilename = multifile.getOriginalFilename();
            MediaType mediaType = ObjectUtils.isEmpty(multifile.getContentType()) ? MediaType.APPLICATION_OCTET_STREAM : MediaType.parseMediaType(multifile.getContentType());
            // 添加文件部分
            bodyBuilder.part("file", byteArray)
                    .filename(ObjectUtils.isEmpty(originalFilename) ? multifile.getName() : originalFilename)
                    .contentType(mediaType);
        }
        final MultiValueMap<String, HttpEntity<?>> entityMultiValueMap = bodyBuilder.build();
        if (CollectionUtils.isEmpty(entityMultiValueMap)) {
            throw new ParamValidateException("文件必填");
        }
        final List<HttpEntity<?>> fileList = entityMultiValueMap.get("file");
        if (fileList.size() > 1) {
            throw new ParamValidateException("只支持单文件上传!");
        }
        //添加其他表单字段
        bodyBuilder.part("user", user);
        final MultiValueMap<String, HttpEntity<?>> paramMap = bodyBuilder.build();
        SysAgentPO agent = agentService.getById(agentId);
        return webClient.post()
                .uri(agent.getBaseUrl() + "/files/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + agent.getApiKey())
                .header("User-Agent", "FileUploadClient/1.0")
                .bodyValue(paramMap)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        response -> Mono.error(new RuntimeException("客户端错误: " + response.statusCode())))
                .onStatus(HttpStatus::is5xxServerError,
                        response -> Mono.error(new RuntimeException("服务器错误: " + response.statusCode())))
                .bodyToMono(Object.class)
                .doOnSuccess(resp -> System.out.println("文件上传成功: " + resp))
                .doOnError(e -> System.err.println("文件上传失败: " + e.getMessage()));
    }
}
