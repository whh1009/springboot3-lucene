package com.whh.springboot3.lucene.search;

import com.alibaba.fastjson2.JSON;
import com.whh.springboot3.lucene.Cons;
import com.whh.springboot3.lucene.search.base.LuceneQueryBuilder;
import com.whh.springboot3.lucene.search.base.PageResult;
import com.whh.springboot3.lucene.search.base.SearchResult;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.Query;

/**
 * @Author wanghonghui
 * @Description
 * @Date 2024/11/24 18:15
 */
public class SearchTest extends AbstractSearch {



    public static void main(String[] args) throws Exception {
        SearchTest search = new SearchTest();
        PageResult<SearchResult> pages = search.search("名不", Cons.AUTHOR_DESC, 1, 15, null, null);
        String jsonString = JSON.toJSONString(pages);
        System.err.println(jsonString);
    }

    /**
     * 获取分词器
     */
    @Override
    public Analyzer getAnalyzer() {
//        return new StandardAnalyzer();
        return new CJKAnalyzer();
    }

    /**
     * 创建查询对象
     *
     * @param searchContent 查询字符串
     * @param searchField   搜索字段
     * @return Query
     */
    @Override
    public Query getQuery(String searchContent, String searchField) throws Exception {
        return new LuceneQueryBuilder(getAnalyzer())
                //.term(searchField, searchContent)
                .phrase(searchField, searchContent.split(""))
                .queryBuilder
                .build();
    }

    /**
     * 设置高亮字段
     */
    @Override
    public String[] getHighlightFields() {
        return new String[]{Cons.AUTHOR_NAME, Cons.AUTHOR_DESC};
    }
}
