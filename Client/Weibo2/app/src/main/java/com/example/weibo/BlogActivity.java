package com.example.weibo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BlogActivity extends AppCompatActivity implements View.OnClickListener {
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
    private CommentsAdapter commentsAdapter;
    private GridView gridView;
    private List<String> drawableStrings=new ArrayList<>();
    private List<Drawable> drawables=new ArrayList<>();
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
    private String nickname;
    private String blog_content;
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blogId=(String)this.getIntent().getSerializableExtra(EXTRA_BLOG_ID);


        commentnumber=(String)this.getIntent().getSerializableExtra(EXTRA_COMMENT_NUMBER);
        setContentView(R.layout.activity_blog);
        initView();
        for(String blogid_inner:UserInfo.applaudList){
            if(blogid_inner.equals(blogId)){
                applaud_judge = true;//已经点过赞了
                applaudButton.setImageDrawable(getDrawable(R.drawable.applaud_choose));
            }
        }
        ConnectionTools connectionTools=null;
        try {
            connectionTools=ConnectionTools.get();
            String resultString=connectionTools.getBlog(blogId);
            String blogString=resultString.split("\\$picture\\$")[0];
            String pictureString=resultString.split("\\$picture\\$")[1];
            JSONObject resultJson=JSONArray.parseArray(blogString).getJSONObject(0);
            JSONObject pictureJson=JSONObject.parseObject(pictureString);
            //自己的微博可以删除
            userId=resultJson.getString("userId");
            if(userId.equals(UserInfo.userId)){
                deleteButton.setVisibility(View.VISIBLE);
                deleteButton.setEnabled(true);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(BlogActivity.this);
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
                editButton.setVisibility(View.VISIBLE);
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=EditBlogActivity.newIntent(blogId,BlogActivity.this);
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
            for(int i=0;i<pictureJson.size();i++){
                String drawableString=pictureJson.getString(i+"");
                drawableStrings.add(drawableString);
                Drawable drawable=ImageTools.stringToDrawable(drawableString);
                drawables.add(drawable);
            }
            PictureAdapter pictureAdapter=new PictureAdapter(drawables,BlogActivity.this);
            gridView.setAdapter(pictureAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public class PictureAdapter extends BaseAdapter {
        List<Drawable> drawables=new ArrayList<>();
        Context context;
        public PictureAdapter(List<Drawable> drawables, Context context) {
            this.drawables=drawables;
            this.context=context;
        }

        @Override
        public int getCount() {
            return drawables.size();
        }

        @Override
        public Object getItem(int position) {
            return drawables.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.picture_abb, parent, false);
            ImageView imageView=convertView.findViewById(R.id.picture_imageView);
            imageView.setImageDrawable(drawables.get(position));
            return convertView;
        }
    }

    private void initView(){
        profileCompImageView=findViewById(R.id.profile_comp_imageView);
        profileCompImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=OtherInfoActivity.newIntent(BlogActivity.this,userId);
                startActivity(intent);
            }
        });
        comment_number_int=Integer.parseInt(commentnumber);
        commentNumberTextView=(TextView)findViewById(R.id.comment_number_abb_textview);
        commentNumberTextView.setText(commentnumber);
        applaudNumberTextView=(TextView)findViewById(R.id.applaud_number_textview);
//        applaudNumberTextView.setText(applaud_number);
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
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(BlogActivity.this));
        gridView=findViewById(R.id.photo_gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String drawableString=drawableStrings.get(position);
                Intent intent=PictureActivity.newIntent(BlogActivity.this,drawableString);
                startActivity(intent);
            }
        });
    }
    public static Intent newIntent(Context packageContext, String blogId,String commentNumber){
        Intent intent=new Intent(packageContext,BlogActivity.class);
        intent.putExtra(EXTRA_BLOG_ID,blogId);
        intent.putExtra(EXTRA_COMMENT_NUMBER,commentNumber);
        return intent;
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
    private void share(String content, Uri uri){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if(uri!=null){
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/*");
            //当用户选择短信时使用sms_body取得文字
            shareIntent.putExtra("sms_body", content);
        }else{
            shareIntent.setType("text/plain");
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        //自定义选择框的标题
        startActivity(Intent.createChooser(shareIntent, "邀请好友"));
        //系统默认标题

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.publish_comment_button){
            String content=commentEditText.getText().toString();
            ConnectionTools connectionTools=null;
            try {
                connectionTools=ConnectionTools.get();
                String result=connectionTools.sendComment(content,blogId);
                if(result.equals("success")){
                    Toast.makeText(this,"发布成功！",Toast.LENGTH_SHORT).show();
                    comment_number_int +=1;
                    commentNumberTextView.setText(String.valueOf(comment_number_int));
                    Log.d("comment", String.valueOf(comment_number_int));
                    commentEditText.setText("");
                    updateUI();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            commentEditText.setText("");
        }
        else if(v.getId()==R.id.comment_blog_button){
           linearLayout.setVisibility(View.VISIBLE);

        }
        else if(v.getId()==R.id.applaud_blog_button && applaud_judge == false){
            applaudButton.setImageDrawable(getDrawable(R.drawable.applaud_choose));
            applaud_number_int +=1;
            UserInfo.applaudList.add(blogId);
            applaudNumberTextView.setText(String.valueOf(applaud_number_int));
            ConnectionTools connectionTools=null;
            try {
                connectionTools=ConnectionTools.get();
                String result=connectionTools.adjustApplaud("increase",blogId);
                if(result.equals("success")) {
                    Toast.makeText(this,"点赞",Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            applaud_judge = true;

        }
        else if(v.getId()==R.id.applaud_blog_button && applaud_judge == true){
            applaudButton.setImageDrawable(getDrawable(R.drawable.applaud));
            applaud_number_int -=1;
            Iterator<String> iterator = UserInfo.applaudList.iterator();
            while (iterator.hasNext()){
                String str = iterator.next();
//                System.out.println(str);
                if (str.equals(blogId)){
                    iterator.remove();
                }
            }

            applaudNumberTextView.setText(String.valueOf(applaud_number_int));
            ConnectionTools connectionTools=null;
            try {
                connectionTools=ConnectionTools.get();
                String result=connectionTools.adjustApplaud("decrease",blogId);
                if(result.equals("success")) {
                    Toast.makeText(this,"取赞",Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            applaud_judge = false;
        }else if(v.getId()==R.id.share_button){

            shareContent(nickname,blog_content,blogId);
        }

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
            LayoutInflater layoutInflater=LayoutInflater.from(BlogActivity.this);
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateUI() throws Exception {
        ConnectionTools connectionTools=ConnectionTools.get();
        JSONArray mResultJsons=connectionTools.getComments(blogId);
        commentsAdapter=new CommentsAdapter(mResultJsons);
        commentsRecyclerView.setAdapter(commentsAdapter);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
        try {
            updateUI();//刷新数据
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}