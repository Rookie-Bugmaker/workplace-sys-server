package com.crrc.sys.controller;

import com.crrc.annotation.AuditLog;
import com.crrc.enums.LogType;
import com.crrc.enums.OperateType;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RefreshScope
public class TestController {


    @PostMapping("/syslog")
    @AuditLog(value = "syslog-QHF审计日志测试接口", logType = LogType.TYPE_OPERATE, operateType = OperateType.TYPE_EXPORT)
    public String test(){
        System.out.println("syslog-QHF测试主服务使用的线程start");
        String workCenterCode = "";
        String startTime = "";
        return "syslog";
    }
}
