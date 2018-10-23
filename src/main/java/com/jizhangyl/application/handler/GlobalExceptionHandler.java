package com.jizhangyl.application.handler;

import com.jizhangyl.application.VO.ResultVO;
import com.jizhangyl.application.config.ProjectUrlConfig;
import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.AuthorizeException;
import com.jizhangyl.application.exception.GlobalException;
import com.jizhangyl.application.exception.MobileException;
import com.jizhangyl.application.utils.LogUtil;
import com.jizhangyl.application.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 杨贤达
 * @date 2018/7/27 21:12
 * @description
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private ProjectUrlConfig projectUrlConfig;

    @ExceptionHandler(value = MobileException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResultVO handlerMobileException(Exception e) {
        log.error(LogUtil.getTrace(e));
        return ResultVOUtil.error(ResultEnum.MOBILE_TOKEN_ERROR);
    }

    // 拦截登录异常
    @ExceptionHandler(value = AuthorizeException.class)
    public String handlerAuthorizeException(Exception e) {
//        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        log.error(LogUtil.getTrace(e));
        return "redirect:"
                .concat(projectUrlConfig.getWechatOpenAuthorize())
                .concat("/jizhangyl/wechat/qrAuthorize")
                .concat("?returnUrl=")
                .concat(projectUrlConfig.getJizhangyl())
                .concat("/jizhangyl/user/login");
    }

    @ResponseBody
//    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = Exception.class)
    public ResultVO handlerException(HttpServletRequest request,
                                     Exception e) {
        e.printStackTrace();
        log.error(LogUtil.getTrace(e));

//        if (e instanceof AuthorizeException) {
//            return ResultVOUtil.error(ResultEnum.NOT_LOGIN);
//        } else
        if (e instanceof GlobalException) {
            GlobalException exception = (GlobalException) e;
            return ResultVOUtil.error(exception.getCode(), exception.getMessage());
        } else if (e instanceof BindException) {
            BindException exception = (BindException) e;
            List<ObjectError> errors = exception.getAllErrors();
            ObjectError error = errors.get(0);
            String message = error.getDefaultMessage();
            return ResultVOUtil.error(ResultEnum.PARAM_ERROR.fillArgs(message));
        } else {
//            return ResultVOUtil.error(ResultEnum.SERVER_ERROR);
            return ResultVOUtil.error(ResultEnum.SERVER_ERROR.getCode(), e.getMessage());
        }
    }
}