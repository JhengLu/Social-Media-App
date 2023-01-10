package com.example.weibo;

import java.util.List;

public class UserInfo {
    public static String profile;
    public static String motto;
    public static String nickName;
    public static String userId;
    public static String gender;
    public static String birthday;
    public static String location;
    public static List<String> applaudList;
    public static void setProfile(String nProfile){
        profile=nProfile;
    }
    public static void setMotto(String nMotto){
        motto=nMotto;
    }
    public static void setNickName(String nNickName){
        nickName=nNickName;
    }
    public static void setUserId(String nUserId){
        userId=nUserId;
    }
    public static void setGender(String nGender){
        gender=nGender;
    }
    public static void setBirthday(String nBirthday){
        birthday=nBirthday;
    }
    public static void setLocation(String nLocation){
        location=nLocation;
    }
    public static void clean(){
        profile=null;
        motto=null;
        nickName=null;
        userId=null;
        gender=null;
        birthday=null;
        location=null;
        applaudList=null;
    }
}
