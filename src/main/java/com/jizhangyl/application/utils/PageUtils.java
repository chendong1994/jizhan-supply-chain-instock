package com.jizhangyl.application.utils;

import java.util.ArrayList;
import java.util.List;

import com.jizhangyl.application.entity.PageEntity;
import com.jizhangyl.application.entity.PageParam;


/**
 * @author 曲健磊
 * @date 2018年10月16日 下午10:41:12
 * @description 假分页的主要实现类
 */
public class PageUtils {

    /**
     * 默认第一页的页码
     */
    private static final Integer DEFAULT_FIRST_PAGE = 1;

    /**
     * 对list中的元素按照pageParam的规则进行分页.
     * 
     * @param list 待分页的list集合
     * @param pageParam 分页参数对象,包括第几页,每页多少条
     * @return 分页后的对象
     */
    @SuppressWarnings("rawtypes")
    public static <E> PageEntity startPage(List<E> list, PageParam pageParam) {
        PageEntity<E> pageEntity = new PageEntity<>();

        int pageNumber = pageParam.getPageNum(); // 0  1
        int pageSize = pageParam.getPageSize(); // 20 20

        // 1.设置当前的页码
        pageEntity.setNumber(pageNumber + DEFAULT_FIRST_PAGE); // 1:第一页

        // 2.计算共多少条记录
        int totalSize = list.size();
        pageEntity.setTotalElements(totalSize);

        // 3.计算共多少页
        int totalPages = totalSize / pageSize; // (0/2 = 0, 1/2 = 0 √), (2/2 = 1 √), (3/2 = 2, 5/2 = 3 √)
        if (totalSize < pageSize || totalSize % pageSize != 0) {
            totalPages += 1;
        }
        pageEntity.setTotalPages(totalPages);

        // 4.判断这一页是否有内容
        int startNum = pageNumber * pageSize; // 20
        if (startNum + 1 <= totalSize) {
            pageEntity.setHasContent(true);
        } else {
            pageEntity.setHasContent(false);
        }

        // 5.判断是否有上一页
        if (totalSize == 0 || pageEntity.getNumber() <= DEFAULT_FIRST_PAGE) {
            pageEntity.setHasPrevious(false);
        } else {
            pageEntity.setHasPrevious(true);
        }

        // 6.判断是否有下一页
        if (totalSize == 0 || pageEntity.getNumber() >= pageEntity.getTotalPages()) {
            pageEntity.setHasNext(false);
        } else {
            pageEntity.setHasNext(true);
        }

        // 7.判断是否是首页
        if (pageEntity.getNumber() == DEFAULT_FIRST_PAGE) {
            pageEntity.setFirst(true);
        }

        // 8.判断是否是尾页
        if (pageEntity.getNumber() == pageEntity.getTotalPages()) {
            pageEntity.setLast(true);
        }

        // 9.计算这一页的列表
        List<E> pageList = new ArrayList<>(pageSize);
        int endNum = (pageNumber + 1) * pageSize > pageEntity.getTotalElements() ? pageEntity.getTotalElements() : (pageNumber + 1) * pageSize;
        for (int i = startNum; i < endNum; i++) {
            pageList.add(list.get(i));
        }

        pageEntity.setPageList(pageList);

        // 10.计算这一页有多少条记录
        pageEntity.setSize(pageList.size());

        return pageEntity;
    }
}
