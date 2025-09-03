package com.crrc.sys.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.crrc.sys.config.exception.BizException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Slf4j
public class CommonProcessExcelListener<T, R> extends AnalysisEventListener<T> {

    private final List<T> dataList = new ArrayList<>();
    private final Function<List<T>, R> serviceMethod;

    private List<String> errList = new ArrayList<>();
    private R result;

    public CommonProcessExcelListener(Function<List<T>, R> serviceMethod) {
        this.serviceMethod = serviceMethod;
    }

    @Override
    public void invoke(T t, AnalysisContext context) {
        Integer row = context.readRowHolder().getRowIndex() + 1;
        if (t == null) {
            errList.add(String.format("第%d行 数据为空", row));
        }
        dataList.add(t);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!errList.isEmpty()) {
            throw new BizException(errList.toString());
        }
        if (serviceMethod != null) {
            result = serviceMethod.apply(dataList);
        }
    }

    public R getResult() {
        return result;
    }

    public List<T> getData() {
        return dataList;
    }
}