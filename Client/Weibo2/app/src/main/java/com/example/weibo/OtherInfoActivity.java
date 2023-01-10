package com.example.weibo;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;

import java.net.MalformedURLException;

public class OtherInfoActivity extends AppCompatActivity{
    public static String OTHER_USER_ID="com.example.otherUserId";
    private String otherUserId;
    ImageView profileImageView=null;
    TextView mottoTextView=null;
    TextView nickNameTextView=null;
    TextView uidTextView=null;
    TextView genderTextView=null;
    TextView birthdayTextView=null;
    TextView locationTextView=null;
    TextView blogsNumberTextView=null;
    private Button applyFriendButton=null;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_info);
        otherUserId=(String)this.getIntent().getSerializableExtra(OTHER_USER_ID);
        profileImageView=(ImageView)findViewById(R.id.other_profile_photo_imageview);
        mottoTextView=(TextView)findViewById(R.id.other_motto_textview);
        nickNameTextView=(TextView)findViewById(R.id.other_nickname_textview);
        uidTextView=(TextView)findViewById(R.id.other_udi_textview);
        genderTextView=(TextView)findViewById(R.id.other_gender_textview);
        birthdayTextView=(TextView)findViewById(R.id.other_birthday_textview);
        locationTextView=(TextView)findViewById(R.id.other_location_textview);
        blogsNumberTextView=(TextView)findViewById(R.id.other_bolgs_number_textview);
        blogsNumberTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent=OtherBlogsActivity.newIntent(OtherInfoActivity.this,otherUserId);
                startActivity(intent);
            }
        });
    }
    public static Intent newIntent(Context packageContext,String otherUserId){
        Intent intent=new Intent(packageContext,OtherInfoActivity.class);
        intent.putExtra(OTHER_USER_ID,otherUserId);
        return intent;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onResume() {
        super.onResume();
        try {
            updateUI();//刷新数据
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateUI(){
        try {
            ConnectionTools connectionTools=ConnectionTools.get();
            JSONObject resultJson = (JSONObject) connectionTools.getUserInfo(otherUserId);
            String profile=resultJson.getString("profile");
            if(!profile.equals("")) profileImageView.setImageDrawable(ImageTools.stringToDrawable(profile));

            String motto=resultJson.getString("motto");
            if(!motto.equals("")) mottoTextView.setText(motto);
            else mottoTextView.setText("该用户有点懒，没有填写！");

            String nickName=resultJson.getString("nickName");
            nickNameTextView.setText("昵称："+nickName);;

            String userId=resultJson.getString("userId");
            uidTextView.setText("帐号："+userId);

            String gender=resultJson.getString("gender");
            if(!gender.equals("")) genderTextView.setText("性别："+gender);
            else genderTextView.setText("性别：未知");

            String birthday=resultJson.getString("birthday");
            if(!birthday.equals("")) birthdayTextView.setText("生日："+birthday);
            else birthdayTextView.setText("生日：未知");

            String location=resultJson.getString("location");
            if(!location.equals("")) locationTextView.setText("所在地："+location);
            else locationTextView.setText("所在地：未知");

            blogsNumberTextView.setText("发博数："+resultJson.getString("blogsNumber"));
//            if(UserInfo.userId.equals(otherUserId)){
//                applyFriendButton.setEnabled(false);
//            }
        } catch (MalformedURLException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
