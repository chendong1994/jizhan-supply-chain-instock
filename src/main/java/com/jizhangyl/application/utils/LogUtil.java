package com.jizhangyl.application.utils;

import com.jizhangyl.application.enums.ResultEnum;
import com.jizhangyl.application.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author 杨贤达
 * @date 2018/8/25 22:46
 * @description 打印详细堆栈到日志文件
 */
@Slf4j
public class LogUtil {

    /**
     * 在日志中打印出详细堆栈
     * @param throwable
     * @return
     */
    public static String getTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        throwable.printStackTrace(writer);
        StringBuffer sb = stringWriter.getBuffer();
        return sb.toString();
    }

    public static void main(String[] args) {
        try {
            int x = 1 / 0;

        } catch (Exception e) {
            e.printStackTrace();
            log.error("【错误】{}", e.getMessage());
            log.error("-----------------------------");
            log.error("【错误】{}", getTrace(e));
            throw new  GlobalException(ResultEnum.PARAM_EMPTY);
        }
    }
}
