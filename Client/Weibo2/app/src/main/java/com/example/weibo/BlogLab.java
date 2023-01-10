package com.example.weibo;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BlogLab {
    private static BlogLab sBlogLab;
    private List<Blog> blogs;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static BlogLab get(Context context,JSONArray jsonArray){
        if(sBlogLab==null){
            sBlogLab=new BlogLab(context,jsonArray);
        }
        return sBlogLab;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private BlogLab(Context context,JSONArray jsonArray){
       updateBlogs(context,jsonArray);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateBlogs(Context context,JSONArray jsonArray){
        blogs=new ArrayList<>();
        Blog blog=null;
        for(int i=0;i<jsonArray.size();i++){
            JSONObject jsonObject=(JSONObject) jsonArray.get(i);
            String userId=jsonObject.getString("userId");
            String blogId=jsonObject.getString("blogId");
            String nickName=jsonObject.getString("nickName");
            String profile=jsonObject.getString("profile");
            String content=jsonObject.getString("blogContent").replace("\\n","\n");
            String publishTime=jsonObject.getString("publishTime");
            String type=jsonObject.getString("type");
            int commetNumber=jsonObject.getInteger("commentNumber");
            int applaudNumber=jsonObject.getInteger("applaudNumber");
            blog=new Blog(context,userId,blogId,nickName,profile,content,publishTime,commetNumber,applaudNumber,type);
            blogs.add(blog);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<Blog> getBlogs(){
        return blogs;
    }
}
