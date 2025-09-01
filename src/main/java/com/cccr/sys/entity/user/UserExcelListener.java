package com.cccr.sys.entity.user;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: UserExcelListener
 * @Description: 用户导入监听
 * @Author LiuQiang
 * @Date 2023/7/11
 */
public class UserExcelListener extends AnalysisEventListener<UserExcelEntity> {
    List<UserExcelEntity> list = new ArrayList<>();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public void invoke(UserExcelEntity userExcelEntity, AnalysisContext analysisContext) {
        logger.info("解析到一条数据：{}", JSON.toJSONString(userExcelEntity));
        list.add(userExcelEntity);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        logger.info("解析完成!");
    }

    public List<UserExcelEntity> list () {
        return list;
    }
}
