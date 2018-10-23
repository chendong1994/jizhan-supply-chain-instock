package com.jizhangyl.application.VO;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jizhangyl.application.utils.serializer.CustomDateSerializer;
import lombok.Data;

import java.util.Date;

/**
 * @author 杨贤达
 * @date 2018/8/24 11:37
 * @description
 */
@Data
public class ExpressPreviewVo {

    private String expNum;

    private JSONObject expLastRecord;

    private Integer expStatus;

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date queryDate;
}
