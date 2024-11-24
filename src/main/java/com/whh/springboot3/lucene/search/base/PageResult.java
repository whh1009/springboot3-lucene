package com.whh.springboot3.lucene.search.base;

import lombok.Getter;

import java.util.List;

/**
 * @Author wanghonghui
 * @Description
 * @Date 2024/11/24 20:24
 */
@Getter
public class PageResult<T> {
    private final List<T> content;      // 当前页数据
    private final long total;           // 总记录数
    private final int pageNumber;       // 当前页码
    private final int pageSize;         // 每页大小
    private final int totalPages;       // 总页数

    public PageResult(List<T> content, long total, int pageNumber, int pageSize) {
        this.content = content;
        this.total = total;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPages = pageSize == 0 ? 1 : (int) Math.ceil((double) total / pageSize);
    }

    public boolean hasNext() {
        return pageNumber < totalPages;
    }

    public boolean hasPrevious() {
        return pageNumber > 1;
    }
}
