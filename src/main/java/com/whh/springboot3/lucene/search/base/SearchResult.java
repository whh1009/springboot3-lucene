package com.whh.springboot3.lucene.search.base;

import lombok.Getter;
import org.apache.lucene.document.Document;

import java.util.Map;

/**
 * @Author wanghonghui
 * @Description
 * @Date 2024/11/24 20:25
 */
@Getter
public class SearchResult {
    private final Document document;
    private final Map<String, String> highlightedFields;
    private final float score;

    public SearchResult(Document document, Map<String, String> highlightedFields, float score) {
        this.document = document;
        this.highlightedFields = highlightedFields;
        this.score = score;
    }
}
