package com.example.weibo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.MalformedURLException;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {
    EditText userIdEditText=null;
    EditText userPasswordEditText=null;
    Button resetButton=null;
    Button registerButton=null;
    Button logInButton=null;
    String userId=null;
    String password=null;
    Intent intent=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        initView();
    }
    private void initView(){
        userIdEditText=(EditText)findViewById(R.id.user_id_editText);
        userPasswordEditText=(EditText)findViewById(R.id.user_password_editText);
        resetButton=(Button)findViewById(R.id.reset_button);
        resetButton.setOnClickListener(this);
        registerButton=(Button)findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);
        logInButton=(Button)findViewById(R.id.log_in_button);
        logInButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.reset_button:
                userIdEditText.setText("");
                userPasswordEditText.setText("");
                break;
            case R.id.register_button:
                intent=new Intent(LogInActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.log_in_button:
                userId=userIdEditText.getText().toString();
                password=userPasswordEditText.getText().toString();
                ConnectionTools connectionTools= null;
                try {
                    connectionTools = ConnectionTools.get();
                    String result=connectionTools.logIn(userId,password);
                    Log.d("M",password);
                    if(result.equals("success")){
                        UserInfo.userId=userId;
                        intent=new Intent(LogInActivity.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else{
                        Toast.makeText(this,"帐号或密码错误",Toast.LENGTH_SHORT).show();
                        userPasswordEditText.setText("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }


    }
}