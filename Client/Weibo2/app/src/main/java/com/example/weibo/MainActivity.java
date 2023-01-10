package com.example.weibo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.app.admin.FactoryResetProtectionPolicy;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton allBlogsButton;
    private ImageButton myBlogsButton;
    private ImageButton myInfoButton;
    private DrawerLayout mDrawerLayout;
    private Fragment allBlogsFragment;
    private Fragment myBlogsFragment;
    private Fragment myInfoFragment;
    private Fragment mContent;
    private Button changeInfoButton;
    private Button logOutButton;
    ImageView profileImageView;
    TextView mottoTextView;
    TextView nickNameTextView;
    TextView uidTextView;
    TextView genderTextView;
    TextView birthdayTextView;
    TextView locationTextView;
    TextView blogsNumberTextView;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        try {
            initInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void swithchFragment(Fragment from, Fragment to) {
        // 当当前 Fragment 与要切换的 Fragment不相同时,才切换
        if (from != to) {
            mContent = to;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            //判断有没有被添加
            if (!to.isAdded()) {
                if (from != null) {
                    ft.hide(from);
                }
                if (to != null) {
                    ft.add(R.id.blank_fragment, to).commit();
                }
            } else {
                if (from != null) {
                    ft.hide(from);
                }
                if (to != null) {
                    ft.show(to).commit();
                }
            }
        }
    }
    private  void initInfo() throws Exception {

        ConnectionTools connectionTools=ConnectionTools.get();
        JSONObject resultJson = (JSONObject) connectionTools.getUserInfo(UserInfo.userId);

        String profile=resultJson.getString("profile");
        UserInfo.setProfile(profile);

        String motto=resultJson.getString("motto");
        if(motto==null)motto="";
        else UserInfo.setMotto(motto);

        String nickName=resultJson.getString("nickName");
        UserInfo.setNickName(nickName);

        String birthday=resultJson.getString("birthday");
        UserInfo.setBirthday(birthday);

        String location=resultJson.getString("location");
        UserInfo.setLocation(location);
        UserInfo.applaudList=new ArrayList<>();

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateInfo(){
        ConnectionTools connectionTools= null;
        try {
            connectionTools = ConnectionTools.get();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        JSONObject resultJson = null;
        try {
            resultJson = (JSONObject) connectionTools.getUserInfo(UserInfo.userId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("122","here");
        if(resultJson == null)
        {
            Log.d("124line","null here");
        }
        String profile=resultJson.getString("profile");
//        Log.d("127profile",profile);
        UserInfo.setProfile(profile);
        if(!profile.equals("")) profileImageView.setImageDrawable(ImageTools.stringToDrawable(profile));
        String motto=resultJson.getString("motto");
        UserInfo.setMotto(motto);
        if(!motto.equals("")) mottoTextView.setText(motto);
        else mottoTextView.setText("快来填写你的个性签名吧！");

        String nickName=resultJson.getString("nickName");
        nickNameTextView.setText("昵称："+nickName);
        UserInfo.setNickName(nickName);

        uidTextView.setText("帐号："+UserInfo.userId);

        String gender=resultJson.getString("gender");
        if(!gender.equals("")) genderTextView.setText("性别："+gender);
        else genderTextView.setText("性别：未知");
        UserInfo.setGender(gender);

        String birthday=resultJson.getString("birthday");
        if(!birthday.equals("")) birthdayTextView.setText("生日："+birthday);
        else birthdayTextView.setText("生日：未知");
        UserInfo.setBirthday(birthday);

        String location=resultJson.getString("location");
        if(!location.equals("")) locationTextView.setText("所在地："+location);
        else locationTextView.setText("所在地：未知");
        UserInfo.setLocation(location);

        blogsNumberTextView.setText("发博数："+resultJson.getString("blogsNumber"));
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initView(){
        allBlogsButton=findViewById(R.id.all_blogs_button);
        allBlogsButton.setOnClickListener(this);
        allBlogsButton.setImageDrawable(getDrawable(R.drawable.home_choose));

        myBlogsButton=findViewById(R.id.my_blogs_button);
        myBlogsButton.setOnClickListener(this);

        myInfoButton=findViewById(R.id.my_info_button);
        myInfoButton.setOnClickListener(this);

        allBlogsFragment=new AllBlogsFragment();
        myBlogsFragment=new MyBlogsFragment();
        myInfoFragment=new MyInfoFragment();

        swithchFragment(null,allBlogsFragment);
        mDrawerLayout=findViewById(R.id.drawer_layout);
        View view=mDrawerLayout.getRootView();
        profileImageView=(ImageView)view.findViewById(R.id.profile_photo_imageview);
        mottoTextView=(TextView)view.findViewById(R.id.motto_textview);
        nickNameTextView=(TextView)view.findViewById(R.id.nickname_textview);
        uidTextView=(TextView)view.findViewById(R.id.udi_textview);
        genderTextView=(TextView)view.findViewById(R.id.gender_textview);
        birthdayTextView=(TextView)view.findViewById(R.id.birthday_textview);
        locationTextView=(TextView)view.findViewById(R.id.location_textview);
        blogsNumberTextView=(TextView)view.findViewById(R.id.bolgs_number_textview);
        changeInfoButton=findViewById(R.id.change_info_button);
        changeInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=ChangeUserInfoActivity.newIntent(MainActivity.this);
                startActivity(intent);
            }
        });
        logOutButton=findViewById(R.id.log_out_button);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,LogInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                UserInfo.clean();
            }
        });
        updateInfo();
        //这边要重写是因为这边设置了左右边的滑动
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDrawerOpened(@NonNull View view) {
                updateInfo();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }
            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setButton(String button) {
        switch (button) {
            case "allBlogsButton":
                allBlogsButton.setImageDrawable(getDrawable(R.drawable.home_choose));
                myBlogsButton.setImageDrawable(getDrawable(R.drawable.integral));
                myInfoButton.setImageDrawable(getDrawable(R.drawable.people));
                break;
            case "myBlogsButton":
                allBlogsButton.setImageDrawable(getDrawable(R.drawable.home));
                myBlogsButton.setImageDrawable(getDrawable(R.drawable.integral_choose));
                myInfoButton.setImageDrawable(getDrawable(R.drawable.people));
                break;
            case "myinfoButton" :
                allBlogsButton.setImageDrawable(getDrawable(R.drawable.home));
                myBlogsButton.setImageDrawable(getDrawable(R.drawable.integral));
                myInfoButton.setImageDrawable(getDrawable(R.drawable.people_choosed));

        }

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        Fragment fragment=null;
        if(v.getId()==R.id.all_blogs_button){
            setButton("allBlogsButton");
            fragment= allBlogsFragment;
        }
        else if(v.getId()==R.id.my_blogs_button){
            setButton("myBlogsButton");
            fragment=myBlogsFragment;
        }
        else if(v.getId() == R.id.my_info_button){
            setButton("myinfoButton");
            fragment=myInfoFragment;
        }
        swithchFragment(mContent,fragment);
    }
}