package com.example.weibo;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;


public class MyBlogsFragment extends Fragment {
    private RecyclerView myBlogsRecyclerView;
    private MyBlogsAdapter myBlogsAdapter;
    private Toolbar mToolbar;
    private SwipeRefreshLayout swipRefreshLayout=null;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.my_blogs_fragment,container,false);
        myBlogsRecyclerView=(RecyclerView)view.findViewById(R.id.my_blogs_recycler_view);
        myBlogsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipe_layout2);
        mToolbar=view.findViewById(R.id.my_blogs_toolBar);
        mToolbar.inflateMenu(R.menu.menu_list);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = null;
                switch (item.getItemId()) {
                    case R.id.new_blog_menu:
                        intent = WriteBlogActivity.newIntent(getActivity());
                        startActivity(intent);
                        break;
                    case R.id.new_vblog_menu:
                        intent = WriteVlogActivity.newIntent(getActivity());
                        startActivity(intent);
                        break;

                }
                return true;
            }
        });
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
        return view;
    }


    private class MyBlogsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Blog mblog;
        private ImageView profileImageView;
        private TextView userNameTextView;
        private TextView contentTextView;
        private TextView commentNumberTextView;
        private TextView applaudNumberTextView;
        private TextView dateTextView;
        public MyBlogsHolder(LayoutInflater inflater,ViewGroup parent){
            super(inflater.inflate(R.layout.one_blog_abb,parent,false));
            itemView.setOnClickListener((this));
            profileImageView=(ImageView)itemView.findViewById(R.id.profile_abb_imageView);
            userNameTextView=(TextView)itemView.findViewById(R.id.username_abb_textView);
            contentTextView=(TextView)itemView.findViewById(R.id.content_abb_textview);
            commentNumberTextView=(TextView)itemView.findViewById(R.id.comment_number_abb_textview);
            applaudNumberTextView=(TextView)itemView.findViewById(R.id.applaud_number);
            dateTextView=(TextView)itemView.findViewById(R.id.publish_date_abb_textview);
        }
        public void bind(Blog blog){
            mblog=blog;
            if(mblog.getProfilePhoto()!=null)profileImageView.setImageDrawable(mblog.getProfilePhoto());
            userNameTextView.setText(mblog.getNickName());
            String content=mblog.getContent();
            if(content.length()>50){
                content=content.substring(0,100)+"...";
            }
            contentTextView.setText(content);
            commentNumberTextView.setText(mblog.getCommentsNumber()+"");
            applaudNumberTextView.setText(mblog.getApplaudNumber()+"");
            dateTextView.setText(mblog.getDate().split("\\.")[0]);
        }
        public void onClick(View view){
            Log.d("MainActivity", "click");
            Intent intent=BlogActivity.newIntent(getActivity(),mblog.getBlogId(),mblog.getCommentsNumber()+"");
            startActivity(intent);
        }
    }

    private class MyBlogsAdapter extends RecyclerView.Adapter<MyBlogsHolder>{
        private List<Blog> mBlogs;
        public MyBlogsAdapter(List<Blog> blogs){
            mBlogs=blogs;
        }
        public MyBlogsHolder onCreateViewHolder(ViewGroup parent,int viewType){
            LayoutInflater layoutInflater=LayoutInflater.from(getActivity());
            return new MyBlogsHolder(layoutInflater,parent);
        }
        public void onBindViewHolder(MyBlogsHolder holder,int position){
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
        BlogLab blogLab=BlogLab.get(getActivity(),connectionTools.getAllBlogs());
        blogLab.updateBlogs(getActivity(),connectionTools.getAllBlogs());
        List<Blog> myBlogs=new ArrayList<>();
        for(int i=0;i<blogLab.getBlogs().size();i++){
            Blog blog=blogLab.getBlogs().get(i);
            if(blog.getUserId().equals(UserInfo.userId))myBlogs.add(blog);
        }
        myBlogsAdapter=new MyBlogsAdapter(myBlogs);
        myBlogsRecyclerView.setAdapter(myBlogsAdapter);
    }
}
