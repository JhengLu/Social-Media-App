package com.example.weibo;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;

import java.util.List;

public class Blog {
    private String mUserId;
    private String mBlogId;
    private Drawable mProfile;
    private String mNickName;
    private String mBlogContent;
    private String mPublishTime;
    private String mtype;
    private int mCommentsNumber;
    private int mApplaudNumber;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Blog(Context context,String userId,String blogId, String nickName, String profile, String blogContent, String publishTime, int commentsNumber,int applaudNumber,String type){
        mUserId=userId;
        mBlogId=blogId;
        if(!profile.equals(""))mProfile=ImageTools.stringToDrawable(profile);
        else mProfile=context.getDrawable(R.drawable.default_profile_photo);
        mNickName=nickName;
        mBlogContent=blogContent;
        mPublishTime=publishTime;
        mCommentsNumber=commentsNumber;
        mApplaudNumber=applaudNumber;
        mtype=type;
    }
    public String getUserId(){
        return mUserId;
    }
    public int getCommentsNumber(){
        return mCommentsNumber;
    }
    public int getApplaudNumber(){
        return mApplaudNumber;
    }
    public String getBlogId(){
        return mBlogId;
    }
    public void setComment(){

    }
    public Drawable getProfilePhoto(){
        return mProfile;
    }
    public String getNickName(){
        return mNickName;
    }
    public String getContent(){
        return mBlogContent;
    }
    public String getDate(){
        return mPublishTime;
    }
    public String getType(){return mtype;}
}
