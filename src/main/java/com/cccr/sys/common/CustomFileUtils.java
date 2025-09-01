package com.cccr.sys.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cccr.sys.config.exception.BizException;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Iterator;

/**
 * 文件处理工具类
 *
 * @author 谭永强
 * @date 2023-01-16
 */
public class CustomFileUtils {

    /**
     * 对象序列化(将对象转换成字节数组)
     * 注意：序列化的对象必须实现`Serializable`接口
     * 案例：byte[] bytes = CustomFileUtils.objectToByteArray(tableInfoList);
     *
     * @param object 待序列化的对象
     * @return 字节数组
     */
    public static byte[] objectToByteArray(Object object) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BizException("对象序列化异常!");
        } finally {
            if (!ObjectUtils.isEmpty(oos)) {
                try {
                    oos.flush();
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bos.toByteArray();
    }

    /**
     * 文件反序列化
     * 案例：List<CfgTableInfo> tableInfoList = CustomFileUtils.fileToObject(multipartRequest, new TypeReference<List<CfgTableInfo>>() { });
     *
     * @param multipartRequest 经过序列化的文件(注意：只获取第一个有效文件，需要反序列化多个文件，请分别调用)
     * @param typeReference    返回值类型
     * @param <T>              泛型
     * @return 对象
     */
    public static <T> T fileToObject(MultipartHttpServletRequest multipartRequest, TypeReference<T> typeReference) {
        byte[] bytes = null;
        //取得上传文件列表
        Iterator<String> iterator = multipartRequest.getFileNames();
        while (iterator.hasNext()) {
            //循环取出每一个文件
            MultipartFile multiple = multipartRequest.getFile(iterator.next());
            if (ObjectUtils.isEmpty(multiple)) {
                throw new BizException("请选择需要导入的文件?");
            }
            String fileName = multiple.getOriginalFilename();
            if (ObjectUtils.isEmpty(fileName) || multiple.getSize() == 0) {
                throw new BizException("文件异常，请重新导入!");
            }
            int index = fileName.lastIndexOf(".obj");
            if (index > 0) {
                ObjectInputStream ois = null;
                try {
                    InputStream inputStream = multiple.getInputStream();
                    ois = new ObjectInputStream(inputStream);
                    return new ObjectMapper().convertValue(ois.readObject(), typeReference);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BizException("反序列化失败,请检查文件是否正常!");
                } finally {
                    if (!ObjectUtils.isEmpty(ois)) {
                        try {
                            ois.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 文件下载(通过字节数组下载文件)
     * 案例：CustomFileUtils.download(response, fileName, bytes);
     *
     * @param response 响应
     * @param fileName 文件名称
     * @param bytes    文件字节数组
     */
    public static void download(HttpServletResponse response, String fileName, byte[] bytes) {
        response.setCharacterEncoding("UTF-8");
        OutputStream out = null;
        try {
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment;fileName=\"" + URLEncoder.encode(fileName, "utf-8") + "\"");
            out = response.getOutputStream();
            out.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}