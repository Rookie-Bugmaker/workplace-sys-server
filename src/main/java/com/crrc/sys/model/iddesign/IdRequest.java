package com.crrc.sys.model.iddesign;

import lombok.Data;
import java.util.Map;

// 请求体DTO类
@Data
public class IdRequest {
    private String businessId;
    private Map<Integer, String> positionToParamMap;
    private Long companyId;
}