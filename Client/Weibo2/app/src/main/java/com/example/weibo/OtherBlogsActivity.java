package com.example.weibo;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class OtherBlogsActivity extends AppCompatActivity {
    public static String OTHER_USER_ID = "com.example.otherUserId";
    private String otherUserId;
    private RecyclerView otherBlogsRecyclerView;
    private OtherBlogsAdapter otherBlogsAdapter;
    private Toolbar mToolbar;
    private SwipeRefreshLayout swipRefreshLayout = null;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_blogs_activity);
        otherUserId=(String)this.getIntent().getSerializableExtra(OTHER_USER_ID);
        otherBlogsRecyclerView=(RecyclerView)findViewById(R.id.my_blogs_recycler_view);
        otherBlogsRecyclerView.setLayoutManager(new LinearLayoutManager(OtherBlogsActivity.this));
        swipRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_layout2);
        try {
            updateUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
        swipRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipRefreshLayout.setRefreshing(true);
                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipRefreshLayout.setRefreshing(false);
                        try {
                            updateUI();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 1000);
            }
        });
    }

    public static Intent newIntent(Context packageContext, String otherUserId) {
        Intent intent = new Intent(packageContext, OtherBlogsActivity.class);
        intent.putExtra(OTHER_USER_ID, otherUserId);
        return intent;
    }

    private class OtherBlogsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Blog mblog;
        private ImageView profileImageView;
        private TextView userNameTextView;
        private TextView contentTextView;
        private TextView commentNumberTextView;
        private TextView applaudNumberTextView;
        private TextView dateTextView;

        public OtherBlogsHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.one_blog_abb, parent, false));
            itemView.setOnClickListener((this));
            profileImageView = (ImageView) itemView.findViewById(R.id.profile_abb_imageView);
            userNameTextView = (TextView) itemView.findViewById(R.id.username_abb_textView);
            contentTextView = (TextView) itemView.findViewById(R.id.content_abb_textview);
            commentNumberTextView = (TextView) itemView.findViewById(R.id.comment_number_abb_textview);
            applaudNumberTextView = (TextView) itemView.findViewById(R.id.applaud_number);
            dateTextView = (TextView) itemView.findViewById(R.id.publish_date_abb_textview);
        }

        public void bind(Blog blog) {
            mblog = blog;
            if (mblog.getProfilePhoto() != null)
                profileImageView.setImageDrawable(mblog.getProfilePhoto());
            userNameTextView.setText(mblog.getNickName());
            String content = mblog.getContent();
            if (content.length() > 50) {
                content = content.substring(0, 100) + "...";
            }
            contentTextView.setText(content);
            commentNumberTextView.setText(mblog.getCommentsNumber() + "");
            applaudNumberTextView.setText(mblog.getApplaudNumber() + "");
            dateTextView.setText(mblog.getDate().split("\\.")[0]);
        }

        public void onClick(View view) {
            Log.d("MainActivity", "click");
            Intent intent = BlogActivity.newIntent(OtherBlogsActivity.this, mblog.getBlogId(), mblog.getCommentsNumber() + "");
            startActivity(intent);
        }
    }
    private class OtherBlogsAdapter extends RecyclerView.Adapter<OtherBlogsHolder>{
        private List<Blog> mBlogs;
        public OtherBlogsAdapter(List<Blog> blogs){
            mBlogs=blogs;
        }
        public OtherBlogsHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater layoutInflater=LayoutInflater.from(OtherBlogsActivity.this);
            return new OtherBlogsHolder(layoutInflater,parent);
        }
        public void onBindViewHolder(OtherBlogsHolder holder, int position){
            Blog blog=mBlogs.get(position);
            holder.bind(blog);
        }
        public int getItemCount(){
            return mBlogs.size();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onResume() {
        super.onResume();
        try {
            updateUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateUI() throws Exception {
        ConnectionTools connectionTools=ConnectionTools.get();
        BlogLab blogLab=BlogLab.get(OtherBlogsActivity.this,connectionTools.getAllBlogs());
        blogLab.updateBlogs(OtherBlogsActivity.this,connectionTools.getAllBlogs());
        List<Blog> myBlogs=new ArrayList<>();
        for(int i=0;i<blogLab.getBlogs().size();i++){
            Blog blog=blogLab.getBlogs().get(i);
            if(blog.getUserId().equals(otherUserId)){
                Log.e("onceuser", otherUserId );
                myBlogs.add(blog);
            }
        }
        otherBlogsAdapter=new OtherBlogsAdapter(myBlogs);
        otherBlogsRecyclerView.setAdapter(otherBlogsAdapter);
    }

}
