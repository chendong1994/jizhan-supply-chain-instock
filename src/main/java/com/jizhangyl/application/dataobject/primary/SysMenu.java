package com.jizhangyl.application.dataobject.primary;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;

/**
 * @author 曲健磊
 * @date 2018年10月8日 下午3:34:42
 * @description 菜单实体类
 */
@Data
@Entity
@DynamicUpdate
public class SysMenu implements Serializable {
    private static final long serialVersionUID = 5701826592025897259L;

    @Id
    @GeneratedValue
    private Integer id;

    private Integer parentId;

    private String name;

    private String icon;

    private Integer type; // 类型   0：总菜单   1：目录   2：菜单  3：按钮  4：文本框

    private Integer sort;

    private String url;

    private String perms;

    private Date createTime;

    private Date updateTime;

    private String updateBy = "oYAqI1BOwVet_Cjl6a-6lqhJUzlY"; // 对应wxuser的openid

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SysMenu other = (SysMenu) obj;
        if (createTime == null) {
            if (other.createTime != null)
                return false;
        } else if (!createTime.equals(other.createTime))
            return false;
        if (icon == null) {
            if (other.icon != null)
                return false;
        } else if (!icon.equals(other.icon))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (parentId == null) {
            if (other.parentId != null)
                return false;
        } else if (!parentId.equals(other.parentId))
            return false;
        if (perms == null) {
            if (other.perms != null)
                return false;
        } else if (!perms.equals(other.perms))
            return false;
        if (sort == null) {
            if (other.sort != null)
                return false;
        } else if (!sort.equals(other.sort))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (updateBy == null) {
            if (other.updateBy != null)
                return false;
        } else if (!updateBy.equals(other.updateBy))
            return false;
        if (updateTime == null) {
            if (other.updateTime != null)
                return false;
        } else if (!updateTime.equals(other.updateTime))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((createTime == null) ? 0 : createTime.hashCode());
        result = prime * result + ((icon == null) ? 0 : icon.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
        result = prime * result + ((perms == null) ? 0 : perms.hashCode());
        result = prime * result + ((sort == null) ? 0 : sort.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((updateBy == null) ? 0 : updateBy.hashCode());
        result = prime * result + ((updateTime == null) ? 0 : updateTime.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }
}
