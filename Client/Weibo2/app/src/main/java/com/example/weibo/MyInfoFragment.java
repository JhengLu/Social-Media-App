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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class MyInfoFragment extends Fragment {
    ImageView profileImageView;
    TextView mottoTextView;
    TextView nickNameTextView;
    TextView uidTextView;
    TextView genderTextView;
    TextView birthdayTextView;
    TextView locationTextView;
    TextView blogsNumberTextView;
    private Button changeInfoButton;
    private Button logOutButton;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_info_fragment, container, false);
        profileImageView=(ImageView)view.findViewById(R.id.profile_photo_imageview);
        mottoTextView=(TextView)view.findViewById(R.id.motto_textview);
        nickNameTextView=(TextView)view.findViewById(R.id.nickname_textview);
        uidTextView=(TextView)view.findViewById(R.id.udi_textview);
        genderTextView=(TextView)view.findViewById(R.id.gender_textview);
        birthdayTextView=(TextView)view.findViewById(R.id.birthday_textview);
        locationTextView=(TextView)view.findViewById(R.id.location_textview);
        blogsNumberTextView=(TextView)view.findViewById(R.id.bolgs_number_textview);
        blogsNumberTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=OtherBlogsActivity.newIntent(getActivity(),UserInfo.userId);
                startActivity(intent);
            }
        });
        changeInfoButton=view.findViewById(R.id.change_info_button);
        changeInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=ChangeUserInfoActivity.newIntent(getActivity());
                startActivityForResult(intent,12);
            }
        });
        logOutButton=view.findViewById(R.id.log_out_button);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),LogInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                UserInfo.clean();
            }
        });
        updateInfo();
        return view;
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
    @Override

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
                try {
                    initInfo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                updateInfo();

    }




}





