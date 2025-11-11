package com.crrc.sys.client;

import com.crrc.sys.client.fallback.FileClientFallBack;
import com.crrc.sys.common.ResultData;
import com.crrc.sys.model.file.SysFileDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author 谭永强
 * @date 2025-11-11
 */
@FeignClient(name = "file-server", fallback = FileClientFallBack.class)
public interface FileClient {

    /**
     * 根据文件头ID查询文件信息(批量)
     *
     * @param fileHeaderIds 文件头ID
     * @return 文件列表
     */
    @PostMapping("findByFileHeaderIds")
    ResultData<List<SysFileDTO>> findByFileHeaderIds(@RequestBody List<Long> fileHeaderIds);
}
