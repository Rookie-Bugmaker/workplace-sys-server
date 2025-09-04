package com.crrc.sys.config.exception.handler;

import com.crrc.sys.common.ResultData;
import com.crrc.sys.common.ReturnCode;
import com.crrc.sys.config.exception.BizException;
import com.crrc.sys.config.exception.ParamValidateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

/**
 * 自定义全局异常处理
 *
 * @author 谭永强
 * @date 2024-11-06
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 自定义异常(业务异常)
     *
     * @param e 异常信息
     * @return 统一返回结果
     */
    @ExceptionHandler(value = BizException.class)
    public ResultData<String> bizExceptionHandler(BizException e) {
        logger.error("发生业务异常-->原因是：{}", e.getErrorMsg());
        return ResultData.fail(e.getErrorCode(), e.getErrorMsg());
    }

    /**
     * 参数校验异常(业务异常)
     *
     * @param e 异常信息
     * @return 统一返回结果
     */
    @ExceptionHandler(value = ParamValidateException.class)
    public ResultData<String> pvExceptionHandler(ParamValidateException e) {
        logger.error("参数校验异常-->原因是：{}", e.getErrorMsg());
        return ResultData.fail(e.getErrorCode(), e.getErrorMsg());
    }

    /**
     * 参数校验异常
     *
     * @param e 方法参数不合法异常
     * @return 统一返回结果
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class, BindException.class})
    public ResultData<String> handleParamValidException(Exception e) {
        BindingResult bindingResult = null;
        if (e instanceof MethodArgumentNotValidException) {
            bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
        } else if (e instanceof BindException) {
            bindingResult = ((BindException) e).getBindingResult();
        }
        if (bindingResult == null) {
            return ResultData.fail(ReturnCode.RC205.getCode(), "参数校验异常");
        }
        StringBuilder builder = new StringBuilder();
        bindingResult.getFieldErrors().forEach(item -> {
            //获取错误信息
            builder.append(item.getDefaultMessage()).append(" ");
        });
        return ResultData.fail(ReturnCode.RC205.getCode(), builder + "");
    }

    /**
     * 参数校验异常
     *
     * @param e 方法参数不合法异常
     * @return 统一返回结果
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResultData<String> handleParamValidException(ConstraintViolationException e) {
        return ResultData.fail(ReturnCode.RC205.getCode(), e.getMessage());
    }

    /**
     * Validation参数校验异常
     *
     * @param e 方法参数不合法异常
     * @return 统一返回结果
     */
    @ExceptionHandler(value = ValidationException.class)
    public ResultData<String> handleParamValidException(ValidationException e) {
        return ResultData.fail(ReturnCode.RC205.getCode(), e.getMessage());
    }

    /**
     * SQL异常处理
     *
     * @param e 异常信息
     * @return 统一返回结果
     */
    @ExceptionHandler(DataAccessException.class)
    public ResultData<String> dataAccessException(Exception e) {
        logger.error("SQL异常信息 ex={}", e.getCause().getMessage(), e);
        return ResultData.fail(ReturnCode.RC500.getCode(), ObjectUtils.isEmpty(e.getMessage()) ? ReturnCode.RC500.getMessage() : e.getMessage());
    }

    /**
     * 其他异常处理
     *
     * @param e 异常信息
     * @return 统一返回结果
     */
    @ExceptionHandler(Exception.class)
    public ResultData<String> exception(Exception e) {
        logger.error("全局异常信息 ex={}", e.getMessage(), e);
        return ResultData.fail(ReturnCode.RC500.getCode(), ObjectUtils.isEmpty(e.getMessage()) ? ReturnCode.RC500.getMessage() : e.getMessage());
    }
}