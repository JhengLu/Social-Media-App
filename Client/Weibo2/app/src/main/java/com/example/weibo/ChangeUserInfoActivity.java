package com.example.weibo;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import java.net.MalformedURLException;

public class ChangeUserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int PICK_PHOTO = 102;
    ImageView profile=null;
    EditText mottoEditText=null;
    EditText nickNameEditText=null;
    Spinner genderSpinner=null;
    EditText birthdayEditText=null;
    EditText locationEditText=null;
    Button submitButton=null;

    String profileString=null;
    String motto=null;
    String nickName=null;
    String gender=null;
    String birthday=null;
    String location=null;
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_info);
        initView();
    }
    public static Intent newIntent(Context packageContext){
        Intent intent=new Intent(packageContext,ChangeUserInfoActivity.class);
        return intent;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initView(){
        profile=(ImageView)findViewById(R.id.profile_change_imageview);
        if(!UserInfo.profile.equals("")) profile.setImageDrawable(ImageTools.stringToDrawable(UserInfo.profile));
        else profile.setImageDrawable(getDrawable(R.drawable.default_profile_photo));
        profile.setOnClickListener(this);
        mottoEditText=(EditText)findViewById(R.id.motto_editText);
        if(!UserInfo.motto.equals(""))mottoEditText.setText(UserInfo.motto);
        else mottoEditText.setText("快来填写你的个性签名吧！");
        nickNameEditText=(EditText)findViewById(R.id.nickname_editText);
        nickNameEditText.setText(UserInfo.nickName);

        genderSpinner=(Spinner)findViewById(R.id.gender_spinner);
        String[] array = {"","男","女","其他"};
        // 创建ArrayAdapter对象
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array);
        genderSpinner.setAdapter(adapter);
        birthdayEditText=(EditText)findViewById(R.id.birthday_editText);
        birthdayEditText.setText(UserInfo.birthday);
        locationEditText=(EditText)findViewById(R.id.location_editText);
        locationEditText.setText(UserInfo.location);
        submitButton=(Button)findViewById(R.id.submit_change_info_button);
        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.profile_change_imageview){
            //动态申请获取访问 读写磁盘的权限
            if (ContextCompat.checkSelfPermission(ChangeUserInfoActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ChangeUserInfoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            } else {
                //打开相册
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //Intent.ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT"
                intent.setType("image/*");
                startActivityForResult(intent, PICK_PHOTO); // 打开相册
            }
        }
        else if(v.getId()==R.id.submit_change_info_button){
            try {
                motto=mottoEditText.getText().toString();
                nickName=nickNameEditText.getText().toString();
                gender=genderSpinner.getSelectedItem().toString();
                birthday=birthdayEditText.getText().toString();
                location=locationEditText.getText().toString();
                ConnectionTools connectionTools=ConnectionTools.get();
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("profile",profileString);
                jsonObject.put("motto",motto);
                jsonObject.put("nickName",nickName);
                jsonObject.put("gender",gender);
                jsonObject.put("birthday",birthday);
                jsonObject.put("location",location);
                String result=connectionTools.changeInfo(jsonObject);
                if (result.equals("success")) {
                    Toast.makeText(this, "修改成功！", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_PHOTO:
                if (resultCode == RESULT_OK) { // 判断手机系统版本号
                    profileString=ImageTools.getImageFromPhoto(data,ChangeUserInfoActivity.this);
                    profile.setImageDrawable(ImageTools.stringToDrawable(profileString));
                }
                break;
            default:
                break;
        }
    }

}