package com.example.weibo;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class WriteVlogActivity extends AppCompatActivity implements View.OnClickListener {
    private Button publishButton=null;
    private EditText contentEditText=null;
    private String profileString=null;
    private Button videoButton=null;
    private GridView gridView=null;
    private ImageView imageView=null;
    private VideoView videoView;
    private Uri videoUri;
    private static final int PICK_VIDEO= 105;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_vlog);
        initView();
    }
    private void initView(){
        videoView=findViewById(R.id.videoView);
        videoView.setVisibility(View.GONE);
        publishButton=findViewById(R.id.publish_content_button);
        publishButton.setOnClickListener(this);
        videoButton=findViewById(R.id.choose_video_button);
        videoButton.setOnClickListener(this);
        contentEditText=findViewById(R.id.content_editText);


    }

    public static Intent newIntent(Context packageContext){
        Intent intent=new Intent(packageContext,WriteVlogActivity.class);
        return intent;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.choose_video_button){

            if (ContextCompat.checkSelfPermission(WriteVlogActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(WriteVlogActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            } else {
                //打开相册
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*");
                startActivityForResult(intent,PICK_VIDEO);

            }
        }
        else if(v.getId()==R.id.publish_content_button){
            String content=contentEditText.getText().toString();
            ConnectionTools connectionTools=null;
            try {
                connectionTools=ConnectionTools.get();
                String result=connectionTools.sendVlog(content,profileString);
                if(result.equals("success")){
                    Toast.makeText(this,"发布成功！",Toast.LENGTH_SHORT).show();
                    contentEditText.setText("");


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            contentEditText.setText("");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("YM","本地视频获取的数据:requestCode->"+requestCode+"-->resultCode:"+resultCode);
        if (requestCode == PICK_VIDEO){
           videoUri = data.getData();
        }
        videoView.setVideoURI(videoUri);
        videoView.setVisibility(View.VISIBLE);
        videoView.start();
        profileString=ImageTools.getCodeFromVideo(data,WriteVlogActivity.this);

    }




}
