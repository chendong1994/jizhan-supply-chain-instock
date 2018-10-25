package com.jizhangyl.application.dataobject.secondary;

import com.jizhangyl.application.enums.ExpensewWhiteStatusEnum;
import com.jizhangyl.application.enums.UserGradeEnum;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
public class Wxuser {

    @Id
    @GeneratedValue
    private Integer id;

    private String openId;
    
    private String unionId;

    private String nickName;

    private Short gender;

    private String language;

    private String city;

    private String province;

    private String country;

    private String avatarUrl;

    private Date createTime;

    private Date updateTime;

    private String inviteCode;

    private Short isActivate;

    private String parentInviteCode;

    private Integer userGrade = UserGradeEnum.ZERO.getGrade();

    private String ext;
    
    private Integer expenseWhiteStatus = ExpensewWhiteStatusEnum.CLOSE.getCode();
    
}
