package com.jizhangyl.application.entity;

/**
 * @author 曲健磊
 * @date 2018年10月16日 下午10:44:56
 * @description 分页参数对象,第一页的页码为0
 */
public class PageParam {

    private Integer pageNum = 0; // 第几页,默认第一页

    private Integer pageSize = 20; // 每页多少条,默认每页20条

    public PageParam() {}

    public PageParam(Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
