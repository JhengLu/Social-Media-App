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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSONObject;

import java.util.List;


public class AllBlogsFragment extends Fragment {
    private RecyclerView allBlogsRecyclerView;
    private AllBlogsAdapter allBlogsAdapter;
    private EditText editText;
    private ImageButton imagebutton;
    private ImageButton changebutton;
    private boolean is_name = true;//判断是搜索nickname还是blogid

    SwipeRefreshLayout swipRefreshLayout;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.all_blogs_fragment,container,false);
        allBlogsRecyclerView=(RecyclerView)view.findViewById(R.id.all_blogs_recycler_view);
        allBlogsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipe_layout);
        editText = (EditText)view.findViewById(R.id.search_text);
        changebutton = (ImageButton)view.findViewById(R.id.change_button);
        changebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_name == true){
                    is_name=false;
                    String str1="输入你想搜索的博客id";
                    editText.setText(str1.toCharArray(), 0, str1.length());
                }else {
                    is_name=true;
                    String str1="输入你想搜索的博主";
                    editText.setText(str1.toCharArray(), 0, str1.length());
                }

            }
        });
        imagebutton = (ImageButton)view.findViewById(R.id.search_button);
        imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_name==true){
                    ConnectionTools connectionTools=null;
                    try {
                        String username = editText.getText().toString();
                        connectionTools = ConnectionTools.get();
                        JSONObject resultJson = null;
                        resultJson = (JSONObject) connectionTools.getUserId(username);
                        if(resultJson == null){
                            Toast.makeText(getActivity(),"没有这个用户！",Toast.LENGTH_SHORT).show();
                        }else {
                            String Id=resultJson.getString("userId");
                            Intent intent=OtherInfoActivity.newIntent(getActivity(),Id);
                            startActivity(intent);
                        }



                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    ConnectionTools connectionTools=null;
                    try {
                        String blog_id = editText.getText().toString();
                        connectionTools = ConnectionTools.get();
                        JSONObject resultJson = null;
                        resultJson=(JSONObject) connectionTools.getBlogInfo(blog_id);
                        if(resultJson == null){
                            Toast.makeText(getActivity(),"没有这个博客！",Toast.LENGTH_SHORT).show();
                        }else {
                            String this_type = resultJson.getString("type");
                            String comment_number = resultJson.getString("commentNumber");
                            if(this_type.equals("blog")){
                                Intent intent=BlogActivity.newIntent(getActivity(),blog_id,comment_number);
                                startActivity(intent);
                            }
                            else if(this_type.equals("vlog")){
                                Intent intent=VlogActivity.newIntent(getActivity(),blog_id,comment_number);
                                startActivity(intent);
                            }
                        }



                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }

            }

        });

        setHasOptionsMenu(true);
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

    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_list,menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent=null;
        switch (item.getItemId()){
            case R.id.new_blog_menu:
                intent=WriteBlogActivity.newIntent(getActivity());
                startActivity(intent);
                break;
        }
        return true;
    }
    private  class AllBlogsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Blog mblog;
        private ImageView profileImageView;
        private TextView userNameTextView;
        private TextView contentTextView;
        private TextView commentNumberTextView;
        private TextView applaudNumberTextView;
        private TextView dateTextView;
        private GridView gridView;

        public AllBlogsHolder(LayoutInflater inflater,ViewGroup parent){
            super(inflater.inflate(R.layout.one_blog_abb,parent,false));
            itemView.setOnClickListener((this));
            profileImageView=(ImageView)itemView.findViewById(R.id.profile_abb_imageView);
            userNameTextView=(TextView)itemView.findViewById(R.id.username_abb_textView);
            contentTextView=(TextView)itemView.findViewById(R.id.content_abb_textview);
            commentNumberTextView=(TextView)itemView.findViewById(R.id.comment_number_abb_textview);
            applaudNumberTextView=(TextView)itemView.findViewById(R.id.applaud_number);
            dateTextView=(TextView)itemView.findViewById(R.id.publish_date_abb_textview);
            gridView = (GridView)itemView.findViewById(R.id.photo_gridView);

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

                Log.d("type", mblog.getType());
                if(mblog.getType().equals("blog")){
                    Intent intent=BlogActivity.newIntent(getActivity(),mblog.getBlogId(),mblog.getCommentsNumber()+"");
                    startActivity(intent);
                }
                else if(mblog.getType().equals("vlog")){
                    Intent intent=VlogActivity.newIntent(getActivity(),mblog.getBlogId(),mblog.getCommentsNumber()+"");
                    startActivity(intent);
                }



        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
        try {
            updateUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class AllBlogsAdapter extends RecyclerView.Adapter<AllBlogsHolder>{
        private List<Blog> mBlogs;
        public AllBlogsAdapter(List<Blog> blogs){
            mBlogs=blogs;
        }
        public AllBlogsHolder onCreateViewHolder(ViewGroup parent,int viewType){
            LayoutInflater layoutInflater=LayoutInflater.from(getActivity());
            return new AllBlogsHolder(layoutInflater,parent);
        }
        public void onBindViewHolder(AllBlogsHolder holder,int position){
            Blog blog=mBlogs.get(position);
            holder.bind(blog);
        }
        public int getItemCount(){
            return mBlogs.size();
        }
    }
    //getActivity() ->Return the Activity this fragment is currently associated with.
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateUI() throws Exception {
        ConnectionTools connectionTools=ConnectionTools.get();
        BlogLab blogLab=BlogLab.get(getActivity(),connectionTools.getAllBlogs());
        blogLab.updateBlogs(getActivity(),connectionTools.getAllBlogs());
        List<Blog> blogs=blogLab.getBlogs();
        allBlogsAdapter=new AllBlogsAdapter(blogs);
        allBlogsRecyclerView.setAdapter(allBlogsAdapter);
    }
}
