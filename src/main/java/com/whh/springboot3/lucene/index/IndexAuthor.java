package com.whh.springboot3.lucene.index;

import com.alibaba.fastjson2.JSON;
import com.whh.springboot3.lucene.Cons;
import com.whh.springboot3.lucene.entity.Author;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

/**
 * @Author wanghonghui
 * @Description
 * @Date 2024/11/23 22:52
 */
public class IndexAuthor extends AbstractIndex<Author> {

    public static void main(String[] args) throws Exception {
        IndexAuthor indexAuthor = new IndexAuthor();
        indexAuthor.indexTangAuthors();
    }

    public void indexTangAuthors() throws IOException, URISyntaxException {
        String content = Files.readString(Paths.get(Objects.requireNonNull(this.getClass().getResource("/data/authors.tang.json")).toURI()));
        List<Author> authors = JSON.parseArray(content, Author.class);
        indexMulti(authors);
    }

    @Override
    public Document addDoc(Author author) {
        Document document = new Document();
        document.add(new StringField(Cons.AUTHOR_ID, author.getId(), Field.Store.YES));
        document.add(new StringField(Cons.AUTHOR_NAME, author.getName(), Field.Store.YES));
        document.add(new TextField(Cons.AUTHOR_DESC, author.getDesc(), Field.Store.YES));
        return document;
    }


}
