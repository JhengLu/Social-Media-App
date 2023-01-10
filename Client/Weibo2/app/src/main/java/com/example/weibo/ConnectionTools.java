package com.example.weibo;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;


import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ConnectionTools {
    private URL url = null;
    private HttpURLConnection connection = null;
    private InputStream inputStream=null;
    public static ConnectionTools sConnectionTools;
    public static ConnectionTools get() throws MalformedURLException {
        if(sConnectionTools==null){
            sConnectionTools=new ConnectionTools();
        }
        return sConnectionTools;
    }
    public ConnectionTools() throws MalformedURLException {
        url=new URL("http://10.0.2.2:8888/server_weibo/HelloServlet");
//        url=new URL("http://10.0.2.2:8888/LoginDemo_war_exploded/hello-servlet");
    }
    public JSONObject getUserInfo(String userId) throws InterruptedException {
            GetUserInfoThread getUserInfoThread = new GetUserInfoThread(userId);
            getUserInfoThread.start();
            getUserInfoThread.join();
            return getUserInfoThread.resultJson;
    }
    public JSONObject getBlogInfo(String blogId) throws InterruptedException{
        GetBlogInfo getBlogInfo = new GetBlogInfo(blogId);
        getBlogInfo.start();
        getBlogInfo.join();
        return getBlogInfo.resultJson;
    }
    public JSONObject getUserId(String nickname) throws InterruptedException {
        GetUserId getId = new GetUserId(nickname);
        getId.start();
        getId.join();
        return getId.resultJson;
    }
    public JSONArray getAllBlogs() throws InterruptedException {
        GetAllBlogs getAllBlogs=new GetAllBlogs();
        getAllBlogs.start();
        getAllBlogs.join();
        return getAllBlogs.resultJsons;
    }

    public String getBlog(String blogId) throws InterruptedException {
        GetBlog getBlog=new GetBlog(blogId);
        getBlog.start();
        getBlog.join();
        return getBlog.resultString;
    }
    public String getVlog(String blogId) throws InterruptedException {
        GetVlog getVlog=new GetVlog(blogId);
        getVlog.start();
        getVlog.join();
        return getVlog.resultString;
    }
    public String sendBlog(String content, ArrayList<String> pictures) throws InterruptedException {
        SendBlog sendBlog=new SendBlog(content,pictures);
        sendBlog.start();
        sendBlog.join();
        return sendBlog.resultString;
    }
    public String sendVlog(String content,String profilestring)throws InterruptedException {
        SendVlog sendVlog=new SendVlog(content,profilestring);
        sendVlog.start();
        sendVlog.join();
        return sendVlog.resultString;
    }


    public String sendComment(String content,String blogId) throws InterruptedException{
        SendComment sendComment=new SendComment(content,blogId);
        sendComment.start();
        sendComment.join();
        return sendComment.resultString;
    }
    public String adjustApplaud(String judge,String blogId) throws InterruptedException {
        AdjustApplaud adjustApplaud = new AdjustApplaud(judge,blogId);
        adjustApplaud.start();
        adjustApplaud.join();
        return adjustApplaud.resultString;
    }
    public JSONArray getComments(String blogId) throws InterruptedException {
        GetComments getComments=new GetComments(blogId);
        getComments.start();
        getComments.join();
        return getComments.resultJsons;
    }
    public String changeInfo(JSONObject jsonObject) throws InterruptedException {
        ChangeInfo changeInfo=new ChangeInfo(jsonObject);
        changeInfo.start();
        changeInfo.join();
        return changeInfo.resultString;
    }
    public String logIn(String userId,String passwordId) throws InterruptedException {
        LogIn logIn=new LogIn(userId,passwordId);
        logIn.start();
        logIn.join();
        return logIn.resultString;
    }
    public String checkNickname(String nickname) throws InterruptedException{
        CheckUserNickname checkUserNickname = new CheckUserNickname(nickname);
        checkUserNickname.start();
        checkUserNickname.join();
        return checkUserNickname.resultString;
    }
    public String checkUserId(String userId) throws InterruptedException {
        CheckUserId checkUserId=new CheckUserId(userId);
        checkUserId.start();
        checkUserId.join();
        return checkUserId.resultString;
    }
    public String register(JSONObject jsonObject) throws InterruptedException {
        Register register=new Register(jsonObject);
        register.start();
        register.join();
        return register.resultString;
    }
    public void deleteBlog(String blogId) throws InterruptedException {
        DeleteBlog deleteBlog=new DeleteBlog(blogId);
        deleteBlog.start();
        deleteBlog.join();
    }
    public String editBlog(String blogId,String content,ArrayList<String> pictures) throws InterruptedException {
        EditBlog editBlog=new EditBlog(blogId,content,pictures);
        editBlog.start();
        editBlog.join();
        return editBlog.resultString;
    }
    class EditBlog extends Thread{
        String blogId;
        String mContent;
        String resultString;
        ArrayList<String> pictures;
        EditBlog(String blogId,String content,ArrayList<String> pictures){
            this.blogId=blogId;
            this.mContent=content;
            this.pictures=pictures;
            mContent=content.replaceAll("\n","\\\\n");
            mContent= URLEncoder.encode(mContent);
        }
        public void run(){
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");//获取服务器数据
            } catch (Exception e) {
                e.printStackTrace();
            }
            connection.setReadTimeout(8000);//设置读取超时的毫秒数
            connection.setConnectTimeout(8000);//设置连接超时的毫秒数
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept","application/json;charset=UTF-8");
            connection.setRequestProperty("action", "EDIT_BLOG");
            connection.setRequestProperty("blogId",blogId);
            JSONObject jsonObject=new JSONObject();
            for(int i=0;i<pictures.size();i++){
                jsonObject.put(i+"",pictures.get(i));
            }
            try {
                OutputStream out = connection.getOutputStream();
                mContent+="$picture$"+jsonObject.toString();
                out.write(mContent.getBytes());
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream=connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                resultString = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class DeleteBlog extends Thread{
        String blogId;
        DeleteBlog(String blogId){
            this.blogId=blogId;
        }
        public void run(){
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");//获取服务器数据
            } catch (Exception e) {
                e.printStackTrace();
            }
            connection.setReadTimeout(8000);//设置读取超时的毫秒数
            connection.setConnectTimeout(8000);//设置连接超时的毫秒数
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept","application/json;charset=UTF-8");
            connection.setRequestProperty("action", "DELETE_BLOG");
            connection.setRequestProperty("blogId",blogId);
            connection.setRequestProperty("userId",UserInfo.userId);
            try {
                inputStream=connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class Register extends Thread{
        private String resultString=null;
        String mString=null;
        Register(JSONObject jsonObject){
            mString=URLEncoder.encode(jsonObject.toString());
        }
        public void run(){
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");//获取服务器数据
            } catch (Exception e) {
                e.printStackTrace();
            }
            connection.setReadTimeout(8000);//设置读取超时的毫秒数
            connection.setConnectTimeout(8000);//设置连接超时的毫秒数
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept","application/json;charset=UTF-8");
            connection.setRequestProperty("action", "REGISTER");
            try {
                OutputStream out = connection.getOutputStream();
                out.write(mString.getBytes());
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream=connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                resultString = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class CheckUserNickname extends Thread{
        String resultString=null;
        private HttpURLConnection connection=null;
        private InputStream inputStream=null;
        private String mnickname=null;
        CheckUserNickname(String nickname){
            mnickname=nickname;
        }
        public void run(){
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");//获取服务器数据
            } catch (Exception e) {
                e.printStackTrace();
            }
            connection.setReadTimeout(8000);//设置读取超时的毫秒数
            connection.setConnectTimeout(8000);//设置连接超时的毫秒数
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept","application/json;charset=UTF-8");
            connection.setRequestProperty("action", "CHECK_NICKNAME");
            connection.setRequestProperty("nickname",mnickname);
            try {
                inputStream=connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                resultString = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class CheckUserId extends Thread{
        String resultString=null;
        private HttpURLConnection connection=null;
        private InputStream inputStream=null;
        private String mUserId=null;
        CheckUserId(String userId){
            mUserId=userId;
        }
        public void run(){
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");//获取服务器数据
            } catch (Exception e) {
                e.printStackTrace();
            }
            connection.setReadTimeout(8000);//设置读取超时的毫秒数
            connection.setConnectTimeout(8000);//设置连接超时的毫秒数
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept","application/json;charset=UTF-8");
            connection.setRequestProperty("action", "CHECK_USERID");
            connection.setRequestProperty("userId",mUserId);
            try {
                inputStream=connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                resultString = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class LogIn extends Thread{
        String resultString=null;
        private HttpURLConnection connection=null;
        private InputStream inputStream=null;
        private String mUserId=null;
        private String mUserPassword=null;
        LogIn(String userId,String userPassword){
            mUserId=userId;
            mUserPassword=userPassword;
        }
        public void run(){
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");//获取服务器数据
            } catch (Exception e) {
                e.printStackTrace();
            }
            connection.setReadTimeout(8000);//设置读取超时的毫秒数
            connection.setConnectTimeout(8000);//设置连接超时的毫秒数
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept","application/json;charset=UTF-8");
            connection.setRequestProperty("action", "LOG_IN");
            connection.setRequestProperty("userId",mUserId);
            connection.setRequestProperty("userPassword",mUserPassword);
            try {
                inputStream=connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                resultString = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class ChangeInfo extends Thread{
        private String resultString=null;
        String mString=null;
        ChangeInfo(JSONObject jsonObject){
            mString=URLEncoder.encode(jsonObject.toString());
        }
        public void run(){
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");//获取服务器数据
            } catch (Exception e) {
                e.printStackTrace();
            }
            connection.setReadTimeout(8000);//设置读取超时的毫秒数
            connection.setConnectTimeout(8000);//设置连接超时的毫秒数
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept","application/json;charset=UTF-8");
            connection.setRequestProperty("action", "CHANGE_INFO");
            connection.setRequestProperty("userId", UserInfo.userId);
            try {
                OutputStream out = connection.getOutputStream();
                out.write(mString.getBytes());
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream=connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                resultString = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class GetComments extends Thread{
        private JSONArray resultJsons=null;
        private HttpURLConnection connection=null;
        private InputStream inputStream=null;
        String mBlogId=null;
        GetComments(String blogId){
            mBlogId=blogId;
        }
        public void run(){
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");//获取服务器数据
            } catch (Exception e) {
                e.printStackTrace();
            }
            connection.setReadTimeout(8000);//设置读取超时的毫秒数
            connection.setConnectTimeout(8000);//设置连接超时的毫秒数
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept","application/json;charset=UTF-8");
            connection.setRequestProperty("action", "GET_COMMENTS");
            connection.setRequestProperty("blogId",mBlogId);
            String resultString= null;
            try {
                inputStream=connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                resultString = reader.readLine();
                resultJsons=JSONArray.parseArray(resultString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class AdjustApplaud extends Thread{
        private HttpURLConnection connection=null;
        private OutputStreamWriter outputStreamWriter=null;
        private InputStream inputStream=null;
        private String mjudge;
        private String mBlogId=null;
        private String resultString=null;
        AdjustApplaud(String judge,String blogId){
            mjudge = judge;
            mBlogId = blogId;

        }
        public void run(){
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");//获取服务器数据
            } catch (Exception e) {
                e.printStackTrace();
            }
            connection.setReadTimeout(8000);//设置读取超时的毫秒数
            connection.setConnectTimeout(8000);//设置连接超时的毫秒数
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept","application/json;charset=UTF-8");
            connection.setRequestProperty("action", "ADJUST_APPLAUD");
            connection.setRequestProperty("userId", UserInfo.userId);
            connection.setRequestProperty("blogId",mBlogId);
            connection.setRequestProperty("judge",mjudge);//increase,decrease

            try {
                inputStream=connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                resultString = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class SendComment extends Thread{
        private String resultString=null;
        private HttpURLConnection connection=null;
        private OutputStreamWriter outputStreamWriter=null;
        private InputStream inputStream=null;
        private String mContent=null;
        private String mBlogId=null;
        SendComment(String content,String blogId){
            mBlogId=blogId;
            mContent=content.replaceAll("\n","\\\\n");
            mContent= URLEncoder.encode(mContent);
        }
        public void run(){
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");//获取服务器数据
            } catch (Exception e) {
                e.printStackTrace();
            }
            connection.setReadTimeout(8000);//设置读取超时的毫秒数
            connection.setConnectTimeout(8000);//设置连接超时的毫秒数
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept","application/json;charset=UTF-8");
            connection.setRequestProperty("action", "PUBLISH_COMMENT");
            connection.setRequestProperty("userId", UserInfo.userId);
            connection.setRequestProperty("blogId",mBlogId);
            try {
                OutputStream out = connection.getOutputStream();
                out.write(mContent.getBytes());
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream=connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                resultString = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class SendVlog extends Thread{
        private String mContent=null;
        private String mprofilesring=null;
        private String resultString=null;
        private HttpURLConnection connection=null;
        private OutputStreamWriter outputStreamWriter=null;
        private InputStream inputStream=null;
        SendVlog(String content,String profilestring){
            mContent=content.replaceAll("\n","\\\\n");
            mContent= URLEncoder.encode(mContent);
            mprofilesring=profilestring;

        }
        public void run() {
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");//获取服务器数据
            } catch (Exception e) {
                e.printStackTrace();
            }
            connection.setReadTimeout(8000);//设置读取超时的毫秒数
            connection.setConnectTimeout(8000);//设置连接超时的毫秒数
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept","application/json;charset=UTF-8");
            connection.setRequestProperty("action", "PUBLISH_VLOG");
            connection.setRequestProperty("userId", UserInfo.userId);
            try {
                OutputStream out = connection.getOutputStream();
                mContent+="$picture$"+mprofilesring;//前面是内容，后面是图片的json
                out.write(mContent.getBytes());
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream=connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                resultString = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class SendBlog extends Thread{
        private String resultString=null;
        private HttpURLConnection connection=null;
        private OutputStreamWriter outputStreamWriter=null;
        private InputStream inputStream=null;
        private String mContent=null;
        private ArrayList<String> pictrues;
        SendBlog(String content,ArrayList<String> pictures){
            mContent=content.replaceAll("\n","\\\\n");
            mContent= URLEncoder.encode(mContent);
            this.pictrues=pictures;
        }
        public void run(){
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");//获取服务器数据
            } catch (Exception e) {
                e.printStackTrace();
            }
            connection.setReadTimeout(8000);//设置读取超时的毫秒数
            connection.setConnectTimeout(8000);//设置连接超时的毫秒数
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept","application/json;charset=UTF-8");
            connection.setRequestProperty("action", "PUBLISH_BLOG");
            connection.setRequestProperty("userId", UserInfo.userId);
            JSONObject jsonObject=new JSONObject();
            for(int i=0;i<pictrues.size();i++){
                jsonObject.put(i+"",pictrues.get(i));
            }
            try {
                OutputStream out = connection.getOutputStream();
                mContent+="$picture$"+jsonObject.toString();//前面是内容，后面是图片的json
                out.write(mContent.getBytes());
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream=connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                resultString = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class GetVlog extends Thread{
        private JSONObject resultJson=null;
        private HttpURLConnection connection=null;
        private InputStream inputStream=null;
        private String mBlogId=null;
        private String resultString= null;
        GetVlog(String blogId){
            mBlogId=blogId;
        }
        public void run(){
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");//获取服务器数据
            } catch (Exception e) {
                e.printStackTrace();
            }
            connection.setReadTimeout(8000);//设置读取超时的毫秒数
            connection.setConnectTimeout(8000);//设置连接超时的毫秒数
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept","application/json;charset=UTF-8");
            connection.setRequestProperty("action", "GET_VLOG");
            connection.setRequestProperty("blogId",mBlogId);
            try {
                inputStream=connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                resultString = reader.readLine();
                inputStream.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    class GetBlog extends Thread{
        private JSONObject resultJson=null;
        private HttpURLConnection connection=null;
        private InputStream inputStream=null;
        private String mBlogId=null;
        private String resultString= null;
        GetBlog(String blogId){
            mBlogId=blogId;
        }
        public void run(){
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");//获取服务器数据
            } catch (Exception e) {
                e.printStackTrace();
            }
            connection.setReadTimeout(8000);//设置读取超时的毫秒数
            connection.setConnectTimeout(8000);//设置连接超时的毫秒数
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept","application/json;charset=UTF-8");
            connection.setRequestProperty("action", "GET_BLOG");
            connection.setRequestProperty("blogId",mBlogId);
            try {
                inputStream=connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                resultString = reader.readLine();
                inputStream.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    class GetAllBlogs extends Thread{
        private JSONArray resultJsons=null;
        private HttpURLConnection connection=null;
        private InputStream inputStream=null;
        public void run(){
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");//获取服务器数据
            } catch (Exception e) {
                e.printStackTrace();
            }
            connection.setReadTimeout(8000);//设置读取超时的毫秒数
            connection.setConnectTimeout(8000);//设置连接超时的毫秒数
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept","application/json;charset=UTF-8");
            connection.setRequestProperty("action", "GET_ALL_BLOGS");
            String resultString= null;
            try {
                inputStream=connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                resultString = reader.readLine();
                resultJsons=JSONArray.parseArray(resultString);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    class GetUserId extends Thread{
        private String mNickname;
        private JSONObject  resultJson = null;
        GetUserId(String nickname){
            mNickname = nickname;
        }
        public void run(){
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");//获取服务器数据
            } catch (Exception e) {
                e.printStackTrace();
            }
            connection.setReadTimeout(8000);//设置读取超时的毫秒数
            connection.setConnectTimeout(8000);//设置连接超时的毫秒数
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept","application/json;charset=UTF-8");
            connection.setRequestProperty("action", "SEARCH_USER");
            connection.setRequestProperty("nick",mNickname);
            try {
                inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                String resultString=reader.readLine();
                resultJson = JSONArray.parseArray(resultString).getJSONObject(0);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    class GetBlogInfo extends Thread{
        private String mblogId;
        private JSONObject  resultJson = null;
        GetBlogInfo(String blogId){mblogId=blogId;}
        public void run(){
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");//获取服务器数据
            } catch (Exception e) {
                e.printStackTrace();
            }
            connection.setReadTimeout(8000);//设置读取超时的毫秒数
            connection.setConnectTimeout(8000);//设置连接超时的毫秒数
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept","application/json;charset=UTF-8");
            connection.setRequestProperty("action", "BLOG_INFO");
            connection.setRequestProperty("BlogId", mblogId);

            try {
                inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                String resultString=reader.readLine();
                resultJson = JSONArray.parseArray(resultString).getJSONObject(0);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    class GetUserInfoThread extends Thread {
        private JSONObject  resultJson = null;
        private String mUserId;
        GetUserInfoThread(String userId) {
            mUserId = userId;
        }
        public void run() {
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");//获取服务器数据
            } catch (Exception e) {
                e.printStackTrace();
            }
            connection.setReadTimeout(8000);//设置读取超时的毫秒数
            connection.setConnectTimeout(8000);//设置连接超时的毫秒数
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept","application/json;charset=UTF-8");
            connection.setRequestProperty("action", "USER_INFO");
            connection.setRequestProperty("UserId", mUserId);

            try {
                inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                String resultString=reader.readLine();
                resultJson = JSONArray.parseArray(resultString).getJSONObject(0);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

