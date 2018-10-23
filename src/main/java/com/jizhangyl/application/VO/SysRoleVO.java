package com.jizhangyl.application.VO;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author 曲健磊
 * @date 2018年10月12日 下午2:28:49
 * @description 角色的显示需要将创建时间格式化
 */
@Data
public class SysRoleVO implements Serializable {
    private static final long serialVersionUID = -2918557532001478783L;

    private Integer id;

    private String name;

    private String remark;

    private String createTime;

    private String updateTime;

    private String updateBy;
}
