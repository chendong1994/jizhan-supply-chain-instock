package com.jizhangyl.application.entity;

import java.util.List;

/**
 * @author 曲健磊
 * @date 2018年10月16日 下午10:49:34
 * @description 分页后的实体对象,包含分页后的list,第几页,总页数,总条数,and so on...
 */
public class PageEntity<E> {

    private List<E> pageList; // 这一页的列表

    private int number; // 当前是第几页

    private int size; // 当前这一页有多少条记录

    private int totalPages; // 总共多少页

    private int totalElements; // 总共多少条记录

    private boolean hasContent; // 这一页是否有内容

    private boolean isFirst; // 这一页是否是第一页

    private boolean isLast; // 这一页是否是最后一页

    private boolean hasPrevious; // 是否有前一页

    private boolean hasNext; // 是否有后一页

    public List<E> getPageList() {
        return pageList;
    }

    public void setPageList(List<E> pageList) {
        this.pageList = pageList;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public boolean hasContent() {
        return hasContent;
    }

    public void setHasContent(boolean hasContent) {
        this.hasContent = hasContent;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean isLast) {
        this.isLast = isLast;
    }

    public boolean hasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }
}
