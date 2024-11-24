package com.whh.springboot3.lucene.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author wanghonghui
 * @Description
 * @Date 2024/11/23 22:29
 */
@Data
@Accessors(chain = true)
public class Author {
    private String id;
    private String name;
    private String desc;
}
