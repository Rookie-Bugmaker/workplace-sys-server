package com.cccr.sys.controller;


import com.cccr.sys.config.exception.ParamValidateException;
import com.cccr.sys.entity.home.HomeNotice;
import com.cccr.sys.socket.HomeNoticeSocket;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 首页通知发送
 *
 * @author 谭永强
 * @date 2023-05-22
 */
@RestController
@RequestMapping("/home/notice/")
public class HomeNoticeController {

    @Resource
    private HomeNoticeSocket homeNoticeSocket;

    /**
     * 通知发送
     *
     * @param rows 参数
     */
    @PostMapping("send")
    public void send(@RequestBody List<HomeNotice> rows) {
        if (CollectionUtils.isEmpty(rows)) {
            throw new ParamValidateException("参数不能为空");
        }
        for (HomeNotice row : rows) {
            if (ObjectUtils.isEmpty(row.getUserId())) {
                throw new ParamValidateException("userId不能为空");
            }
            if (ObjectUtils.isEmpty(row.getMessage())) {
                throw new ParamValidateException("message不能为空");
            }
        }
        homeNoticeSocket.sendMessage(rows);
    }
}
