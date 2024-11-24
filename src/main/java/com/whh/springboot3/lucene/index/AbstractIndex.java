package com.whh.springboot3.lucene.index;

import com.google.common.collect.Lists;
import com.whh.springboot3.lucene.Cons;
import lombok.Getter;
import lombok.Setter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author wanghonghui
 * @Description
 * @Date 2024/11/23 21:42
 */
@Getter
@Setter
public abstract class AbstractIndex<T>{

    private double ramBufferSizeMB = 256.0;

    private boolean create = true;

    private IndexWriter indexWriter;

    private Directory indexDir;

    protected final void indexMulti(List<T> list) throws IOException {
        IndexWriter writer = getWriter();
        try {
            List<Document> authorDocs = new ArrayList<>();
            for (T t : list) {
                Document doc = addDoc(t);
                authorDocs.add(doc);
            }
            List<List<Document>> partition = Lists.partition(authorDocs, 500);
            for (List<Document> documents : partition) {
                writer.addDocuments(documents);
                writer.commit();
            }
        } finally {
            close();
        }
    }

    protected final void indexSingle(T t) throws IOException {
        IndexWriter writer = getWriter();
        try {
            Document document = addDoc(t);
            writer.addDocument(document);
            writer.commit();
        } finally {
            close();
        }
    }

    private IndexWriter getWriter() throws IOException {
        if(indexWriter == null) {
            indexDir = FSDirectory.open(Paths.get(Cons.INDEX_PATH));
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(isCreate() ? IndexWriterConfig.OpenMode.CREATE : IndexWriterConfig.OpenMode.APPEND);
            iwc.setRAMBufferSizeMB(getRamBufferSizeMB());
            indexWriter = new IndexWriter(indexDir, iwc);
        }
        return indexWriter;
    }

    public void close() throws IOException {
        if (indexWriter != null) {
            indexWriter.close();
        }
        if(indexDir != null) {
            indexDir.close();
        }
    }

    public abstract Document addDoc(T t);

}
