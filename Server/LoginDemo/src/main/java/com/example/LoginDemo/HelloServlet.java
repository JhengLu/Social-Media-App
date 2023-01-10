package com.example.LoginDemo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.URLDecoder;
import java.sql.*;
import java.util.Base64;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

//@WebServlet(name = "helloServlet", value = "/hello-servlet")
@WebServlet(name = "HelloServlet", value = "/HelloServlet")
public class HelloServlet extends HttpServlet {
    private Connection connection=null;
    private PreparedStatement pstmt=null;
    private final static String URL = "jdbc:mysql://127.0.0.1:3306/weibodatabase?&useSSL=false&&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private final static String USERNAME="root";
    private final static String PASSWORD="211302";
    public HelloServlet() {
        super();
        System.out.println("init");
        // TODO Auto-generated constructor stub
    }
    public void init() {
        System.out.println("hello");
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException cne){
            cne.printStackTrace();
        }
        try {
            connection= DriverManager.getConnection(URL,"root","211302");
            System.out.println("here");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException{
        response.setCharacterEncoding("utf-8");
        String action = request.getHeader("action");
        System.out.println(action+" Action");
        try {
            switch (action) {
                case "USER_INFO":
                    getUserInfo(request, response);
                    break;
                case "GET_ALL_BLOGS":
                    getAllBlogs(request, response);
                    break;
                case "PUBLISH_BLOG":
                    publishBlog(request, response);
                    break;
                case "GET_BLOG":
                    getBlog(request, response);
                    break;
                case "PUBLISH_COMMENT":
                    publishComment(request, response);
                    break;
                case "GET_COMMENTS":
                    getComments(request, response);
                    break;
                case "CHANGE_INFO":
                    changeInfo(request, response);
                    break;
                case "LOG_IN":
                    logIn(request, response);
                    break;
                case "CHECK_USERID":
                    checkUserId(request,response);
                    break;
                case "REGISTER":
                    register(request,response);
                    break;
                case "DELETE_BLOG":
                    deleteBlog(request,response);
                    break;
                case "EDIT_BLOG":
                    editBlog(request,response);
                    break;
            }
        } catch (Exception e) {
                e.printStackTrace();
            }
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException {
        try {
            doGet(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void editBlog(HttpServletRequest request,HttpServletResponse response) throws IOException, SQLException {
        ServletInputStream servletInputStream=request.getInputStream();
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(servletInputStream));
        String line;
        String content = "";
        while((line=bufferedReader.readLine())!=null){
            content+=line;
        }
        System.out.println("content"+content);
        String contentString=content.split("\\$picture\\$")[0];
        String contentPicture=content.split("\\$picture\\$")[1];
        contentString= URLDecoder.decode(contentString,"utf-8");
        String blogId=request.getHeader("blogId");
        //先清空图片
        String sql="delete from PictureInfo where blogId=?";
        pstmt=connection.prepareStatement(sql);
        pstmt.setString(1,blogId);
        pstmt.executeUpdate();
        //再重写微博
        sql="update BlogInfo set blogContent=? where blogId=?";
        pstmt=connection.prepareStatement(sql);
        pstmt.setString(1,contentString);
        pstmt.setString(2,blogId);
        pstmt.executeUpdate();
        //再重写
        JSONObject pictures=JSONObject.parseObject(contentPicture);
        for(int i=0;i<pictures.size();i++){
            String path=saveImage(pictures.getString(i+""),"weiboPictures/"+blogId+i);
            sql="insert into PictureInfo values (?,?)";
            pstmt=connection.prepareStatement(sql);
            pstmt.setString(1,blogId);
            pstmt.setString(2,path);
            pstmt.executeUpdate();
            pstmt.close();
        }
        PrintWriter writer=response.getWriter();
        writer.write("success");
        writer.close();
    }
    public void deleteBlog(HttpServletRequest request,HttpServletResponse response) throws SQLException {
        String blogId=request.getHeader("blogId");
        String userId=request.getHeader("userId");
        String sql="delete from CommentInfo where blogId=?";
        pstmt=connection.prepareStatement(sql);
        pstmt.setString(1,blogId);
        pstmt.executeUpdate();
        sql="delete from PictureInfo where blogId=?";
        pstmt=connection.prepareStatement(sql);
        pstmt.setString(1,blogId);
        pstmt.executeUpdate();
        sql="delete from BlogInfo where blogId=?";
        pstmt=connection.prepareStatement(sql);
        pstmt.setString(1,blogId);
        pstmt.executeUpdate();
        sql="update UserInfo set blogsNumber-=1 where userId=?";
        pstmt=connection.prepareStatement(sql);
        pstmt.setString(1,userId);
        pstmt.executeUpdate();
        pstmt.close();
    }
    public void register(HttpServletRequest request,HttpServletResponse response) throws IOException, SQLException {
        ServletInputStream servletInputStream=request.getInputStream();
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(servletInputStream));
        String line;
        String content = "";
        while((line=bufferedReader.readLine())!=null){
            content+=line;
        }
        content= URLDecoder.decode(content,"utf-8");
        JSONObject jsonObject=JSONObject.parseObject(content);
        String userId=jsonObject.getString("userId");
        String passWord=jsonObject.getString("userPassword");
        String profileString=jsonObject.getString("profile");
        String nickName=jsonObject.getString("nickName");
        String gender=jsonObject.getString("gender");
        String birthday=jsonObject.getString("birthday");
        String location=jsonObject.getString("location");

        String sql="insert into UserInfo values (?,?,?,?,?,?,?,?,?)";
        pstmt=connection.prepareStatement(sql);
        pstmt.setString(1,userId);
        pstmt.setString(2,passWord);
        String profileName="profileImage/"+userId;
        String profilePath=saveImage(profileString,profileName);
        pstmt.setString(3,profilePath);
        pstmt.setString(4,nickName);
        pstmt.setString(5,"");
        pstmt.setString(6,gender);
        pstmt.setString(7,birthday);
        pstmt.setString(8,location);
        pstmt.setInt(9,0);
        pstmt.executeUpdate();
        PrintWriter writer=response.getWriter();
        writer.write("success");
        pstmt.close();
    }
    public void checkUserId(HttpServletRequest request,HttpServletResponse response) throws SQLException, IOException {
        String userId=request.getHeader("userId");
        String resultStirng=null;
        String sql="select count(*) from UserInfo where userId=?";
        pstmt=connection.prepareStatement(sql);
        pstmt.setString(1,userId);
        ResultSet resultSet=pstmt.executeQuery();
        if(resultSet.next()){
            if(resultSet.getInt(1)==0) resultStirng="success";
            else resultStirng="failed";
        }
        PrintWriter writer=response.getWriter();
        writer.write(resultStirng);
    }
    public void logIn(HttpServletRequest request,HttpServletResponse response) throws SQLException, IOException {
        String userId=request.getHeader("userId");
        String userPassword=request.getHeader("userPassword");
        String resultStirng=null;
        String sql="select count(*) from UserInfo where userId=? and password=?";
        pstmt=connection.prepareStatement(sql);
        pstmt.setString(1,userId);
        pstmt.setString(2,userPassword);
        ResultSet resultSet=pstmt.executeQuery();
        if(resultSet.next()){
            if(resultSet.getInt(1)==1) {
                System.out.println(resultSet.getInt(1));
                resultStirng = "success";
            }
            else resultStirng="failed";
        }
        PrintWriter writer=response.getWriter();
        writer.write(resultStirng);
    }
    public void changeInfo(HttpServletRequest request,HttpServletResponse response) throws IOException, SQLException {
        String userId=request.getHeader("userId");
        ServletInputStream servletInputStream=request.getInputStream();
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(servletInputStream));
        String line;
        String content = "";
        while((line=bufferedReader.readLine())!=null){
            content+=line;
        }
        content= URLDecoder.decode(content,"utf-8");
        JSONObject jsonObject=JSONObject.parseObject(content);
        String profileString=jsonObject.getString("profile");
        String motto=jsonObject.getString("motto");
        String nickName=jsonObject.getString("nickName");
        String gender=jsonObject.getString("gender");
        String birthday=jsonObject.getString("birthday");
        String location=jsonObject.getString("location");
        String sql=null;
        if(profileString==null){
            sql="update UserInfo set motto= ? ,nickName=?,gender=?,birthday=?,location=? where userId=?";
            pstmt=connection.prepareStatement(sql);
            pstmt.setString(1,motto);
            pstmt.setString(2,nickName);
            pstmt.setString(3,gender);
            pstmt.setString(4,birthday);
            pstmt.setString(5,location);
            pstmt.setString(6,userId);
        }
        else {
            sql="update UserInfo set motto=?,nickName=?,gender=?,birthday=?,location=? ,profile=? where userId=?";
            pstmt=connection.prepareStatement(sql);
            pstmt.setString(1,motto);
            pstmt.setString(2,nickName);
            pstmt.setString(3,gender);
            pstmt.setString(4,birthday);
            pstmt.setString(5,location);
            pstmt.setString(7,userId);
            String profileName=userId+"profile";
            String profilePath=saveImage(profileString,profileName);
            pstmt.setString(6,profilePath);
            ;
        }
        pstmt.executeUpdate();
        PrintWriter writer=response.getWriter();
        writer.write("success");
    }
    public void publishComment(HttpServletRequest request,HttpServletResponse response) throws SQLException, IOException {
        int blogId=Integer.parseInt(request.getHeader("blogId"));
        int commentNumber=0;
        String userId=request.getHeader("userId");
        String sql="select count(*) from commentInfo where blogId=?";
        pstmt=connection.prepareStatement(sql);
        pstmt.setInt(1,blogId);
        ResultSet resultSet=pstmt.executeQuery();
        if(resultSet.next()){
            commentNumber=resultSet.getInt(1);
        }
        ServletInputStream servletInputStream=request.getInputStream();
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(servletInputStream));
        String line;
        String content = "";
        while((line=bufferedReader.readLine())!=null){
            content+=line;
        }
        content= URLDecoder.decode(content,"utf-8");
        String sql2="insert into commentInfo values (?,?,?,?,?)";
        pstmt=connection.prepareStatement(sql2);
        pstmt.setInt(1,blogId);
        pstmt.setInt(2,commentNumber+1);
        pstmt.setString(3,content);
        pstmt.setString(4,new Timestamp(System.currentTimeMillis()).toString());
        pstmt.setString(5,userId);
        pstmt.executeUpdate();
        pstmt=connection.prepareStatement("update BlogInfo set commentNumber+=1 where blogId=?");
        pstmt.setInt(1,blogId);
        pstmt.executeUpdate();
        PrintWriter writer=response.getWriter();
        writer.write("success");
    }
    public void getBlog(HttpServletRequest request,HttpServletResponse response) throws Exception {
        String blogId=request.getHeader("blogId");
        String sql="select BlogInfo.userId,profile,nickName,blogContent,commentNumber,publishTime\n" +
                "        from BlogInfo inner join UserInfo on BlogInfo.userId=UserInfo.userId\n" +
                "        where blogId=?";
        PreparedStatement pstmt=connection.prepareStatement(sql);
        pstmt.setInt(1,Integer.parseInt(blogId));
        ResultSet resultSet=pstmt.executeQuery();
        String resultString=resultSetToJson(resultSet);
        sql="select picture from PictureInfo where blogId=?";
        pstmt=connection.prepareStatement(sql);
        pstmt.setInt(1,Integer.parseInt(blogId));
        ResultSet resultSet2=pstmt.executeQuery();
        JSONObject jsonObject=new JSONObject();
        int i=0;
        while(resultSet2.next()){
            jsonObject.put(i+"",loadImage(resultSet2.getString("picture")));
            i++;
        }
        resultString+="$picture$"+jsonObject.toString();
        PrintWriter writer=response.getWriter();
        writer.write(resultString);
        pstmt.close();
        writer.close();
    }
    public void getComments(HttpServletRequest request,HttpServletResponse response) throws Exception {
        int blogId=Integer.parseInt(request.getHeader("blogId"));
        String sql="select profile,nickName,commentContent,commentDate\n" +
                "from CommentInfo inner join UserInfo on (CommentInfo.commentUserId=UserInfo.userId)\n" +
                "where CommentInfo.blogId=?\n" +
                "order by CommentInfo.commentId desc";
        pstmt=connection.prepareStatement(sql);
        pstmt.setInt(1,blogId);
        ResultSet resultSet=pstmt.executeQuery();
        String resultString=resultSetToJson(resultSet);
        PrintWriter writer=response.getWriter();
        writer.write(resultString);
        writer.close();
        pstmt.close();
    }
    public void getAllBlogs(HttpServletRequest request,HttpServletResponse response) throws Exception {
        String sql="select ui.userId,blogId,profile,nickName,blogContent,commentNumber,publishTime\n" +
                "from BlogInfo as bi inner join UserInfo as ui on (bi.userId=ui.userId) order by blogId desc";
        Statement stmt=connection.createStatement();
        ResultSet resultSet=stmt.executeQuery(sql);
        String resultString=resultSetToJson(resultSet);
        PrintWriter writer=response.getWriter();
        writer.write(resultString);
        writer.close();
        stmt.close();
    }
    public void getUserInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultSet resultSet=null;
        String userId=request.getHeader("UserId");
        String sql="select * from UserInfo where userId=?";
        pstmt=connection.prepareStatement(sql);
        pstmt.setString(1,userId);
        PrintWriter out = response.getWriter();
        resultSet=pstmt.executeQuery();
        String resultString=resultSetToJson(resultSet);
        PrintWriter writer=response.getWriter();
        writer.write(resultString);
    }
    public void publishBlog(HttpServletRequest request,HttpServletResponse response) throws IOException, SQLException {
        ServletInputStream servletInputStream=request.getInputStream();
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(servletInputStream));
        String line;
        String content = "";
        while((line=bufferedReader.readLine())!=null){
            content+=line;
        }
        String contentString=content.split("\\$picture\\$")[0];
        String contentPicture=content.split("\\$picture\\$")[1];
        contentString= URLDecoder.decode(contentString,"utf-8");
        String userId=request.getHeader("userId");
        String sql="SET NOCOUNT ON insert into BlogInfo(userId,blogContent,publishTime) values (?,?,?) select @@IDENTITY as blogId";
        pstmt=connection.prepareStatement(sql);
        pstmt.setString(1,userId);
        pstmt.setString(2,contentString);
        String currentTime = new Timestamp(System.currentTimeMillis()).toString();
        pstmt.setString(3,currentTime);
        ResultSet resultSet=pstmt.executeQuery();
        resultSet.next();
        String blogId=resultSet.getInt("blogId")+"";
        JSONObject pictures=JSONObject.parseObject(contentPicture);
        for(int i=0;i<pictures.size();i++){
            String path=saveImage(pictures.getString(i+""),"weiboPictures/"+blogId+i);
            sql="insert into PictureInfo values (?,?)";
            pstmt=connection.prepareStatement(sql);
            pstmt.setString(1,blogId);
            pstmt.setString(2,path);
            pstmt.executeUpdate();
        }
        pstmt=connection.prepareStatement("update UserInfo set blogsNumber+=1 where userId=?");
        pstmt.setString(1,userId);
        pstmt.executeUpdate();
        pstmt.close();
        PrintWriter writer=response.getWriter();
        writer.write("success");
        writer.close();
    }
    public void destroy() {
    }

    private String loadImage(String path){
        String imgFile = path;
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        //对字节数组Base64编码
        Base64.Encoder encoder=Base64.getEncoder();
        //返回Base64编码过的字节数组字符串
        return encoder.encodeToString(data);
    }
    public String resultSetToJson(ResultSet resultSet) throws Exception {
        JSONArray array = new JSONArray();
        // 获取列数
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        while (resultSet.next()) {
            JSONObject jsonObject = new JSONObject();
            // 遍历每一列
            String value=null;
            for (int i = 1; i <= columnCount; i++) {
                String columnName =metaData.getColumnLabel(i);
                if(columnName.equals("profile")&&resultSet.getString(columnName)!=null){
                    value = loadImage(resultSet.getString(columnName));
                }

                else value=resultSet.getString(columnName);
                if(value==null)value="";
                jsonObject.put(columnName, value);
            }
            array.add(jsonObject);
        }
        return array.toString();
    }
    private String saveImage(String imageString,String imageName) throws IOException {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] b = decoder.decode(imageString);
        String imagePath="C:/Users/Dell/Pictures/课设专用/weibo/"+imageName+".jpg";
        FileOutputStream fileOutputStream = new FileOutputStream(new File(imagePath));
        fileOutputStream.write(b);
        return imagePath;
    }
}
