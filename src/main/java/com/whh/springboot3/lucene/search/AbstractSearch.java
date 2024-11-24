package com.whh.springboot3.lucene.search;

import com.whh.springboot3.lucene.Cons;
import com.whh.springboot3.lucene.search.base.PageResult;
import com.whh.springboot3.lucene.search.base.SearchResult;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;
import java.util.*;

/**
 * @Author wanghonghui
 * @Description
 * @Date 2024/11/24 20:05
 */
@Getter
@Setter
@Slf4j
public abstract class AbstractSearch<T> {
    /**
     * 高亮前缀
     */
    private String preTag = "<em>";
    /**
     * 高亮后缀
     */
    private String postTag = "</em>";

    /**
     * 创建查询对象
     *
     * @param searchContent 查询字符串
     * @param searchField   搜索字段
     * @return Query
     */
    public abstract Query getQuery(String searchContent, String searchField) throws Exception;

    /**
     * 设置高亮字段
     */
    public abstract String[] getHighlightFields();

    /**
     * 获取分词器
     */
    public abstract Analyzer getAnalyzer();

    /**
     * 执行分页搜索
     *
     * @param searchContent 搜索词
     * @param pageNumber    页码（从1开始）
     * @param pageSize      每页大小
     * @param sortField     排序字段
     * @param sortOrder     排序方式 (true为升序，false为降序)
     * @param searchField   搜索字段
     * @return 分页搜索结果
     */
    public PageResult<SearchResult> search(String searchContent, String searchField, int pageNumber, int pageSize, String sortField, Boolean sortOrder) throws Exception {

        // 验证分页参数
        if (pageNumber < 1) {
            pageNumber = 1;
        }
        if (pageSize < 1) {
            pageSize = Cons.PAGE_SIZE;
        }

        try (Directory directory = FSDirectory.open(Paths.get(Cons.INDEX_PATH));
             IndexReader reader = DirectoryReader.open(directory)) {

            IndexSearcher searcher = new IndexSearcher(reader);
            Query query = getQuery(searchContent, searchField);
            if (query == null) {
                throw new IllegalArgumentException("请初始化 query 查询器");
            }
            // 创建排序器
            Sort sort = null;
            if (sortField != null && !sortField.isEmpty()) {
                sort = new Sort(new SortField(sortField, SortField.Type.STRING, !sortOrder));
            }

            // 计算起始位置
            int start = (pageNumber - 1) * pageSize;

            // 先获取总数
            TopDocs topDocs;
            if (sort != null) {
                topDocs = searcher.search(query, start + pageSize, sort);
            } else {
                topDocs = searcher.search(query, start + pageSize);
            }

            // 获取总记录数
            long total = topDocs.totalHits.value();

            // 如果起始位置超过总数，返回空结果
            if (start >= total) {
                return new PageResult<>(Collections.emptyList(), total, pageNumber, pageSize);
            }

            // 设置高亮器
            Highlighter highlighter = setupHighlighter(query);

            // 获取当前页的数据
            List<SearchResult> pageContent = new ArrayList<>();
            int end = Math.min(start + pageSize, (int) total);
            StoredFields storedFields = searcher.storedFields();
            for (int i = start; i < end; i++) {
                ScoreDoc scoreDoc = topDocs.scoreDocs[i];
                Document doc = storedFields.document(scoreDoc.doc);
                Map<String, String> highlightedFields = getHighlightedFields(doc, highlighter);
                pageContent.add(new SearchResult(doc, highlightedFields, scoreDoc.score));
            }

            return new PageResult<>(pageContent, total, pageNumber, pageSize);
        }
    }

    /**
     * 设置高亮器
     */
    private Highlighter setupHighlighter(Query query) {
        SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter(getPreTag(), getPostTag());
        QueryScorer scorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(htmlFormatter, scorer);
        highlighter.setTextFragmenter(new SimpleFragmenter(150));
        return highlighter;
    }

    /**
     * 获取高亮字段
     */
    private Map<String, String> getHighlightedFields(Document doc, Highlighter highlighter) {
        Map<String, String> highlightedFields = new HashMap<>();
        if (getHighlightFields() == null || getHighlightFields().length == 0) {
            throw new IllegalArgumentException("高亮字段不可以为空，请调用 setHighlightFields 方法");
        }
        for (String field : getHighlightFields()) {
            String content = doc.get(field);
            if (content != null) {
                try {
                    String highlightedContent = highlighter.getBestFragment(getAnalyzer(), field, content);
                    highlightedFields.put(field, highlightedContent != null ? highlightedContent : content);
                } catch (Exception e) {
                    highlightedFields.put(field, content);
                }
            }
        }

        return highlightedFields;
    }

}
