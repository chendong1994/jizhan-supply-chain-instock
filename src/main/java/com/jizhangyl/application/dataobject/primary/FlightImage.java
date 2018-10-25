package com.jizhangyl.application.dataobject.primary;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;

@Data
@Entity
@DynamicUpdate
public class FlightImage {
	
	@Id
    @GeneratedValue
    private Integer flightImageId;

    private Integer flightId;

    private Integer type;//1送机费用凭证2航空提货单3清关费用凭证

    private String url;

    private Date createTime;

    private Date updateTime;
    

    public Integer getFlightImageId() {
        return flightImageId;
    }

    public void setFlightImageId(Integer flightImageId) {
        this.flightImageId = flightImageId;
    }

    public Integer getFlightId() {
        return flightId;
    }

    public void setFlightId(Integer flightId) {
        this.flightId = flightId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}