package com.whh.springboot3.lucene.search.base;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @Author wanghonghui
 * @Description
 * @Date 2024/11/24 21:07
 */
public class LuceneQueryBuilder {
    public final BooleanQuery.Builder queryBuilder;
    private final Analyzer analyzer;

    public LuceneQueryBuilder(Analyzer analyzer) {
        this.queryBuilder = new BooleanQuery.Builder();
        this.analyzer = analyzer;
    }

    /**
     * 精确匹配查询（不分词）
     */
    public LuceneQueryBuilder term(String field, String value) {
        return term(field, value, BooleanClause.Occur.MUST);
    }

    public LuceneQueryBuilder term(String field, String value, BooleanClause.Occur occur) {
        if (value != null && !value.trim().isEmpty()) {
            queryBuilder.add(new TermQuery(new Term(field, value)), occur);
        }
        return this;
    }

    /**
     * 分词全文检索
     */
    public LuceneQueryBuilder match(String field, String text) {
        return match(field, text, BooleanClause.Occur.MUST);
    }

    public LuceneQueryBuilder match(String field, String text, BooleanClause.Occur occur) {
        if (text != null && !text.trim().isEmpty()) {
            try {
                QueryParser parser = new QueryParser(field, analyzer);
                queryBuilder.add(parser.parse(text), occur);
            } catch (Exception e) {
                throw new RuntimeException("Parse query error", e);
            }
        }
        return this;
    }

    /**
     * 前缀查询
     */
    public LuceneQueryBuilder prefix(String field, String prefix) {
        return prefix(field, prefix, BooleanClause.Occur.MUST);
    }

    public LuceneQueryBuilder prefix(String field, String prefix, BooleanClause.Occur occur) {
        if (prefix != null && !prefix.trim().isEmpty()) {
            queryBuilder.add(new PrefixQuery(new Term(field, prefix)), occur);
        }
        return this;
    }

    /**
     * 通配符查询
     */
    public LuceneQueryBuilder wildcard(String field, String wildcard) {
        return wildcard(field, wildcard, BooleanClause.Occur.MUST);
    }

    public LuceneQueryBuilder wildcard(String field, String wildcard, BooleanClause.Occur occur) {
        if (wildcard != null && !wildcard.trim().isEmpty()) {
            queryBuilder.add(new WildcardQuery(new Term(field, wildcard)), occur);
        }
        return this;
    }

    /**
     * 模糊查询
     */
    public LuceneQueryBuilder fuzzy(String field, String term) {
        return fuzzy(field, term, 2, BooleanClause.Occur.MUST);
    }

    public LuceneQueryBuilder fuzzy(String field, String term, int maxEdits, BooleanClause.Occur occur) {
        if (term != null && !term.trim().isEmpty()) {
            queryBuilder.add(new FuzzyQuery(new Term(field, term), maxEdits), occur);
        }
        return this;
    }

    /**
     * 短语查询
     */
    public LuceneQueryBuilder phrase(String field, String[] terms) {
        return phrase(field, terms, 0, BooleanClause.Occur.MUST);
    }

    public LuceneQueryBuilder phrase(String field, String[] terms, int slop, BooleanClause.Occur occur) {
        if (terms != null && terms.length > 0) {
            PhraseQuery.Builder phraseBuilder = new PhraseQuery.Builder();
            for (String term : terms) {
                phraseBuilder.add(new Term(field, term));
            }
            phraseBuilder.setSlop(slop);
            queryBuilder.add(phraseBuilder.build(), occur);
        }
        return this;
    }

    /**
     * 数值范围查询 - Integer
     */
    public LuceneQueryBuilder intRange(String field, Integer lower, Integer upper) {
        return intRange(field, lower, upper, BooleanClause.Occur.MUST);
    }

    public LuceneQueryBuilder intRange(String field, Integer lower, Integer upper, BooleanClause.Occur occur) {
        if (lower != null || upper != null) {
            int min = lower != null ? lower : Integer.MIN_VALUE;
            int max = upper != null ? upper : Integer.MAX_VALUE;
            queryBuilder.add(IntPoint.newRangeQuery(field, min, max), occur);
        }
        return this;
    }

    /**
     * 数值范围查询 - Long
     */
    public LuceneQueryBuilder longRange(String field, Long lower, Long upper) {
        return longRange(field, lower, upper, BooleanClause.Occur.MUST);
    }

    public LuceneQueryBuilder longRange(String field, Long lower, Long upper, BooleanClause.Occur occur) {
        if (lower != null || upper != null) {
            long min = lower != null ? lower : Long.MIN_VALUE;
            long max = upper != null ? upper : Long.MAX_VALUE;
            queryBuilder.add(LongPoint.newRangeQuery(field, min, max), occur);
        }
        return this;
    }

    /**
     * 日期范围查询
     */
    public LuceneQueryBuilder dateRange(String field, LocalDateTime start, LocalDateTime end) {
        return dateRange(field, start, end, BooleanClause.Occur.MUST);
    }

    public LuceneQueryBuilder dateRange(String field, LocalDateTime start, LocalDateTime end, BooleanClause.Occur occur) {
        if (start != null || end != null) {
            long startTime = start != null ? start.toInstant(ZoneOffset.UTC).toEpochMilli() : Long.MIN_VALUE;
            long endTime = end != null ? end.toInstant(ZoneOffset.UTC).toEpochMilli() : Long.MAX_VALUE;
            queryBuilder.add(LongPoint.newRangeQuery(field, startTime, endTime), occur);
        }
        return this;
    }

    /**
     * 多字段匹配，至少匹配一个字段
     */
    public LuceneQueryBuilder multiMatch(String[] fields, String text) {
        if (text != null && !text.trim().isEmpty() && fields != null && fields.length > 0) {
            BooleanQuery.Builder multiBuilder = new BooleanQuery.Builder();
            for (String field : fields) {
                multiBuilder.add(new TermQuery(new Term(field, text)), BooleanClause.Occur.SHOULD);
            }
            queryBuilder.add(multiBuilder.build(), BooleanClause.Occur.MUST);
        }
        return this;
    }

}


