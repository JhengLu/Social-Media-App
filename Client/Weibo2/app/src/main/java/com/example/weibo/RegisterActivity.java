package com.example.weibo;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class RegisterActivity extends AppCompatActivity {
    ImageView profile=null;
    EditText passwordText=null;
    EditText checkPasswordText=null;
    EditText nickNameEditText=null;
    Spinner genderSpinner=null;
    EditText birthdayEditText=null;
    EditText locationEditText=null;

    Button submitButton=null;
    public static final int PICK_PHOTO = 102;
    String profileString="";
    String password=null;
    String nickName=null;
    String gender=null;
    String birthday=null;
    String location=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        try {
            initView();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    private void initView() throws MalformedURLException {
        profile=(ImageView)findViewById(R.id.profile_reg_imageview);
        //点击头像可以触发从相册中选择图片的功能
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(RegisterActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
                } else {
                    //打开相册
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    //Intent.ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT"
                    intent.setType("image/*");
                    startActivityForResult(intent, PICK_PHOTO); // 打开相册
                }
            }
        });

        passwordText=(EditText)findViewById(R.id.password_reg_editText);
        checkPasswordText=(EditText)findViewById(R.id.check_password_reg_editText);
        nickNameEditText=(EditText)findViewById(R.id.nickname_reg_editText);
        genderSpinner=(Spinner)findViewById(R.id.gender_reg_spinner);
        String[] array = {"","男","女","其他"};
        // 创建ArrayAdapter对象
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array);
        genderSpinner.setAdapter(adapter);

        birthdayEditText=(EditText)findViewById(R.id.birthday_reg_editText);
        locationEditText=(EditText)findViewById(R.id.location_reg_editText);
        submitButton=(Button)findViewById(R.id.submit_reg_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            ConnectionTools connectionTools=ConnectionTools.get();
            @Override
            public void onClick(View v) {
                password=passwordText.getText().toString();
                if(!checkPasswordText.getText().toString().equals(password)){
                    Toast.makeText(RegisterActivity.this,"两次密码不一致！",Toast.LENGTH_SHORT).show();
                    checkPasswordText.setText("");
                    return;
                }
                String resultString=null;
                //检查昵称
                try {
                    nickName=nickNameEditText.getText().toString();
                    resultString=connectionTools.checkNickname(nickName);
                    if(!resultString.equals("success")){
                        Toast.makeText(RegisterActivity.this,"这个昵称已经被使用！",Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(passwordText.getText().toString().equals("")||checkPasswordText.getText().toString().equals("")){
                    Toast.makeText(RegisterActivity.this,"请输入密码！",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(nickNameEditText.getText().toString().equals("")){
                    Toast.makeText(RegisterActivity.this,"请输入昵称！",Toast.LENGTH_SHORT).show();
                    return;
                }
                password=passwordText.getText().toString();
                birthday=birthdayEditText.getText().toString();
                gender=genderSpinner.getSelectedItem().toString();
                location=locationEditText.getText().toString();
                nickName=nickNameEditText.getText().toString();

                JSONObject jsonObject=new JSONObject();
                jsonObject.put("profile",profileString);
                jsonObject.put("userPassword",password);
                jsonObject.put("nickName",nickName);
                jsonObject.put("gender",gender);
                jsonObject.put("birthday",birthday);
                jsonObject.put("location",location);
                String result= null;
                try {
                    result = connectionTools.register(jsonObject);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String judge = result.split(":")[0];
                String userId = result.split(":")[1];
                if (judge.equals("success")) {
                    Toast.makeText(RegisterActivity.this, "注册成功！您的账号是"+userId, Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(RegisterActivity.this,LogInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_PHOTO:
                if (resultCode == RESULT_OK) { // 判断手机系统版本号
                    Log.d("image", "here ");
                    profileString=ImageTools.getImageFromPhoto(data,RegisterActivity.this);
                    Log.d("image",profileString);
                    profile.setImageDrawable(ImageTools.stringToDrawable(profileString));
                }
                break;
            default:
                break;
        }
    }
}