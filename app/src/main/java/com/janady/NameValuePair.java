package com.janady;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/16 0016.
 */
public class NameValuePair implements Serializable {
    public String name;
    public Object value;

    public NameValuePair(String name, Object value) {
        this.name = name;
        this.value = value;
    }
}
