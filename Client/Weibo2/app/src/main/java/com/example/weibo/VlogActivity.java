package com.example.weibo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

public class VlogActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_BLOG_ID="com.blog.blog_id";
    public static final String EXTRA_COMMENT_NUMBER="com.blog.comment_number";
    private CircleImageView profileCompImageView=null;
    private TextView userNameCompTextView=null;
    private TextView contentCompTextView=null;
    private TextView publishDateCompTextView=null;
    private Button publishCommentButton=null;
    private EditText commentEditText=null;
    private RecyclerView commentsRecyclerView=null;
    private ImageButton deleteButton=null;
    private ImageButton editButton=null;
    private String blogId;
    private String userId;
    private String commentnumber;
    private LinearLayout linearLayout = null;
    private TextView commentNumberTextView;
    private TextView applaudNumberTextView;
    private ImageButton commentButton = null;
    private ImageButton applaudButton = null;
    private ImageButton shareButton = null;
    private int comment_number_int;
    private boolean applaud_judge = false;
    private int applaud_number_int;
    private String applaud_number;
    private VideoView videoView;
    private CommentsAdapter commentsAdapter;
    private String nickname;
    private String blog_content;
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blogId=(String)this.getIntent().getSerializableExtra(EXTRA_BLOG_ID);
        commentnumber=(String)this.getIntent().getSerializableExtra(EXTRA_COMMENT_NUMBER);
        setContentView(R.layout.activity_vlog);
        initView();
        try {
            updateUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(String blogid_inner:UserInfo.applaudList){
            if(blogid_inner.equals(blogId)){
                applaud_judge = true;//已经点过赞了
                applaudButton.setImageDrawable(getDrawable(R.drawable.applaud_choose));
            }
        }
        ConnectionTools connectionTools=null;
        try {
            connectionTools=ConnectionTools.get();
            String resultString=connectionTools.getVlog(blogId);
            String blogString=resultString.split("\\$video\\$")[0];
            String videoString=resultString.split("\\$video\\$")[1];
            JSONObject resultJson=JSONArray.parseArray(blogString).getJSONObject(0);
            JSONObject videoJson=JSONObject.parseObject(videoString);
            //自己的微博可以删除
            userId=resultJson.getString("userId");
            if(userId.equals(UserInfo.userId)){
                deleteButton.setVisibility(View.VISIBLE);
                deleteButton.setEnabled(true);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(VlogActivity.this);
                        builder.setTitle("提示：");
                        builder.setMessage("您确定删除该微博？");
                        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    ConnectionTools.sConnectionTools.deleteBlog(blogId);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                finish();
                            }
                        });
                        //设置取消按钮
                        builder.setPositiveButton("取消",null);
                        builder.show();
                    }
                });
                //这边还需要重新写
                editButton.setVisibility(View.VISIBLE);
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=EditBlogActivity.newIntent(blogId,VlogActivity.this);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            String profile=resultJson.getString("profile");
            if(!profile.equals("")) profileCompImageView.setImageDrawable(ImageTools.stringToDrawable(profile));
            applaud_number=resultJson.getString("applaudNumber");
            applaud_number_int = Integer.parseInt(applaud_number);
            applaudNumberTextView.setText(applaud_number);
            userNameCompTextView.setText(resultJson.getString("nickName"));
            nickname=resultJson.getString("nickName");
            contentCompTextView.setText(resultJson.getString("blogContent").replace("\\n","\n"));
            blog_content=resultJson.getString("blogContent").replace("\\n","\n");
            publishDateCompTextView.setText(resultJson.getString("publishTime").split("\\.")[0]);
            String video_profile=videoJson.getString("0");//这个时候还是base64加密没有解压
            // 创建文件夹，在存储卡下
            String dirName = Environment.getExternalStorageDirectory() + "/" + VlogActivity.this.getPackageName();
            File file = new File(dirName);
            // 文件夹不存在时创建
            if (!file.exists()) {
                file.mkdir();
            }
            String fileName = dirName+"/"+blogId+".mp4";
            Log.e("filename", fileName);
            File file1 = new File(fileName);
            if (file1.exists()) {
                // 如果已经存在, 就不下载了, 去播放
                startVideo(fileName);
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DOWNLOAD(video_profile,fileName);
                    }
                }).start();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    // 下载具体操作
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void DOWNLOAD(String videostring,String fileName) {
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] b = decoder.decode(videostring);
            int len;
            // 打开输入流
            OutputStream os = new FileOutputStream(fileName);
            // 写数据
            os.write(b);
            // 完成后关闭流
            Log.e("download", "download-finish");
            os.close();
            startVideo(fileName);
            //            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("download", "e.getMessage() --- " + e.getMessage());
        }
    }
    // 播放视频
    private void startVideo(String videoURI) {
        // 设置播放加载路径
        videoView.setVideoURI(Uri.parse(videoURI));
        // 播放
        videoView.start();
    }

    private void initView(){
        profileCompImageView=findViewById(R.id.profile_comp_imageView);
        profileCompImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=OtherInfoActivity.newIntent(VlogActivity.this,userId);
                startActivity(intent);
            }
        });

        comment_number_int=Integer.parseInt(commentnumber);
        commentNumberTextView=(TextView)findViewById(R.id.comment_number_abb_textview);
        commentNumberTextView.setText(commentnumber);
        applaudNumberTextView=(TextView)findViewById(R.id.applaud_number_textview);
        linearLayout=findViewById(R.id.ll);
        linearLayout.setVisibility(View.GONE);
        userNameCompTextView=(TextView)findViewById(R.id.username_comp_textView);
        contentCompTextView=(TextView)findViewById(R.id.content_comp_textview);
        deleteButton=findViewById(R.id.delete_blog_button);
        deleteButton.setEnabled(false);
        deleteButton.setVisibility(View.GONE);
        editButton=findViewById(R.id.edit_blog_button);
        editButton.setVisibility(View.GONE);
        publishDateCompTextView=(TextView)findViewById(R.id.publish_date_comp_textview);
        publishCommentButton=(Button)findViewById(R.id.publish_comment_button);
        publishCommentButton.setOnClickListener(this);
        commentButton=(ImageButton) findViewById(R.id.comment_blog_button);
        commentButton.setOnClickListener(this);
        applaudButton=(ImageButton) findViewById(R.id.applaud_blog_button);
        applaudButton.setOnClickListener(this);
        shareButton=(ImageButton)findViewById(R.id.share_button);
        shareButton.setOnClickListener(this);
        commentEditText=(EditText)findViewById(R.id.comment_editText);
        commentsRecyclerView=(RecyclerView)findViewById(R.id.comments_recyclerView);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(VlogActivity.this));

        videoView=findViewById(R.id.videoView);

        }
    public static Intent newIntent(Context packageContext, String blogId,String commentNumber){
        Intent intent=new Intent(packageContext,VlogActivity.class);
        intent.putExtra(EXTRA_BLOG_ID,blogId);
        intent.putExtra(EXTRA_COMMENT_NUMBER,commentNumber);
        return intent;
    }
    private  class CommentHolder extends RecyclerView.ViewHolder{
        private JSONObject mResultJson;
        private ImageView profileImageView;
        private TextView userNameTextView;
        private TextView commentTextView;
        private TextView commentDateTextView;
        public CommentHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.one_comment_abb,parent,false));
            profileImageView=(ImageView)itemView.findViewById(R.id.profile_abb_imageView);
            userNameTextView=(TextView)itemView.findViewById(R.id.username_abb_textView);
            commentTextView=(TextView)itemView.findViewById(R.id.comment_abb_textview);
            commentDateTextView=(TextView)itemView.findViewById(R.id.comment_date_abb_textview);
        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bind(JSONObject jsonObject){
            mResultJson=jsonObject;
            String profile=jsonObject.getString("profile");
            if(!profile.equals(""))profileImageView.setImageDrawable(ImageTools.stringToDrawable(profile));
            userNameTextView.setText(jsonObject.getString("nickName"));
            commentTextView.setText(jsonObject.getString("commentContent").replace("\\n","\n"));
            commentDateTextView.setText(jsonObject.getString("commentDate").split("\\.")[0]);
        }
    }
    class CommentsAdapter extends RecyclerView.Adapter<CommentHolder>{
        private JSONArray mResultJsons;
        public CommentsAdapter(JSONArray jsonArray) throws Exception {
            mResultJsons=jsonArray;
        }
        public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater layoutInflater=LayoutInflater.from(VlogActivity.this);
            return new CommentHolder(layoutInflater,parent);
        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void onBindViewHolder(CommentHolder holder, int position){
            JSONObject jsonObject=mResultJsons.getJSONObject(position);
            holder.bind(jsonObject);
        }
        public int getItemCount(){
            return mResultJsons.size();
        }
    }
    private void shareContent(String nickname,String content,String blogId) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        String title = "标题";
        String firstText="博主:"+nickname+"   "+"更多内容请上Micro Blog搜索博客id:"+blogId+"\n";
        String extraText=content;
        String finalText = firstText+extraText;
        share.putExtra(Intent.EXTRA_TEXT, finalText);
        if (title != null) {
            share.putExtra(Intent.EXTRA_SUBJECT, title);
        }
        startActivity(Intent.createChooser(share, "分享一下"));
    }

    public void updateUI() throws Exception {
        ConnectionTools connectionTools=ConnectionTools.get();
        JSONArray mResultJsons=connectionTools.getComments(blogId);
        commentsAdapter=new VlogActivity.CommentsAdapter(mResultJsons);
        commentsRecyclerView.setAdapter(commentsAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
        public void onClick(View v) {
        if (v.getId() == R.id.publish_comment_button) {
            String content = commentEditText.getText().toString();
            ConnectionTools connectionTools = null;
            try {
                connectionTools = ConnectionTools.get();
                String result = connectionTools.sendComment(content, blogId);
                if (result.equals("success")) {
                    Toast.makeText(this, "发布成功！", Toast.LENGTH_SHORT).show();
                    comment_number_int += 1;
                    commentNumberTextView.setText(String.valueOf(comment_number_int));
                    Log.d("comment", String.valueOf(comment_number_int));
                    commentEditText.setText("");
                    updateUI();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            commentEditText.setText("");
        } else if (v.getId() == R.id.comment_blog_button) {
            linearLayout.setVisibility(View.VISIBLE);

        } else if (v.getId() == R.id.applaud_blog_button && applaud_judge == false) {
            applaudButton.setImageDrawable(getDrawable(R.drawable.applaud_choose));
            applaud_number_int += 1;
            UserInfo.applaudList.add(blogId);
            applaudNumberTextView.setText(String.valueOf(applaud_number_int));
            ConnectionTools connectionTools = null;
            try {
                connectionTools = ConnectionTools.get();
                String result = connectionTools.adjustApplaud("increase", blogId);
                if (result.equals("success")) {
                    Toast.makeText(this, "点赞", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            applaud_judge = true;

        } else if (v.getId() == R.id.applaud_blog_button && applaud_judge == true) {
            applaudButton.setImageDrawable(getDrawable(R.drawable.applaud));
            applaud_number_int -= 1;
            Iterator<String> iterator = UserInfo.applaudList.iterator();
            while (iterator.hasNext()) {
                String str = iterator.next();
//                System.out.println(str);
                if (str.equals(blogId)) {
                    iterator.remove();
                }
            }

            applaudNumberTextView.setText(String.valueOf(applaud_number_int));
            ConnectionTools connectionTools = null;
            try {
                connectionTools = ConnectionTools.get();
                String result = connectionTools.adjustApplaud("decrease", blogId);
                if (result.equals("success")) {
                    Toast.makeText(this, "取赞", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            applaud_judge = false;
        }else if(v.getId()==R.id.share_button){

            shareContent(nickname,blog_content,blogId);
        }
    }

}

