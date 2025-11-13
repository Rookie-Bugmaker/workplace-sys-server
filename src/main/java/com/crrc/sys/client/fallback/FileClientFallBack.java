package com.crrc.sys.client.fallback;

import com.crrc.sys.client.FileClient;
import com.crrc.sys.common.ResultData;
import com.crrc.sys.model.file.SysFileDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 谭永强
 * @date 2025-11-11
 */
@Component
public class FileClientFallBack implements FileClient {

    private final static Logger logger = LoggerFactory.getLogger(FileClientFallBack.class);

    @Override
    public ResultData<List<SysFileDTO>> findByFileHeaderIds(List<Long> fileHeaderIds) {
        logger.error("FileClient.findByFileHeaderIds已进入熔断器,fileHeaderIds={}", fileHeaderIds);
        return ResultData.fallback();
    }
}
