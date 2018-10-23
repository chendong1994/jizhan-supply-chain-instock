package com.jizhangyl.application.comparator.menu;

import java.util.Comparator;

import com.jizhangyl.application.dto.SysMenuDTO;

/**
 * @author 曲健磊
 * @date 2018年10月21日 下午2:44:11
 * @description 按照菜单的sort属性进行排序，如果sort相同则按照拼音进行排序
 */
public class MenuComparator implements Comparator<SysMenuDTO> {

    @Override
    public int compare(SysMenuDTO o1, SysMenuDTO o2) {
        int s1 = o1.getSort().intValue();
        int s2 = o2.getSort().intValue();
        if (s1 == s2) {
            return o1.getName().compareTo(o2.getName());
        }
        return s1 > s2 ? 1 : -1;
    }

}
