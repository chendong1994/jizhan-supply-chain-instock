package com.jizhangyl.application.utils;

import java.util.List;

import com.jizhangyl.application.dataobject.SysMenu;
import com.jizhangyl.application.dto.SysMenuDTO;

/**
 * @author 曲健磊
 * @date 2018年10月11日 下午3:28:54
 * @description 对菜单进行操作的工具类
 */
public class MenuUtil {
    
    /**
     * 对菜单列表进行递归排序
     * @param returnTreeList 返回的排序之后的菜单列表
     * @param treeList  排序前的菜单列表
     * @param parentId  父节点id
     */
    public static void sortTreeList(List<SysMenuDTO> returnTreeList, List<SysMenuDTO> treeList,
        Integer parentId) {
        // 轮询所有的菜单
        for (int i = 0; i < treeList.size(); i++) {
            SysMenuDTO tree = treeList.get(i);
            // 找到第一级菜单
            if (tree.getParentId() != null && tree.getParentId().equals(parentId)) {
                returnTreeList.add(tree);
                for (SysMenu child : treeList) {
                    // 递归
                    if (child.getParentId() != null && (child).getParentId().equals(parentId)) {
                        sortTreeList(returnTreeList, treeList, tree.getId());
                        break;
                    }
                }
            }
        }
    }
}
