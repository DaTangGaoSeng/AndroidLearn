package com.example.androidlearn;


import java.util.List;
import java.util.Map;
//一个类 用于实例化每个item对象
public class Category {
    private Map<String,String> list;
    private List<String> images = null;

    public Category(Map<String,String> list,List<String> images){
        this.list = list;
        this.images = images;
    }
    public Category(Map<String,String> list){
        this.list = list;
    }

    public Map<String, String> getList() {
        return list;
    }

    public List<String> getImages() {
        return images;
    }


}
