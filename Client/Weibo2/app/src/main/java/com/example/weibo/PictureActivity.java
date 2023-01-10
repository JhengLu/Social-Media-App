package com.example.weibo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ImageView;

public class PictureActivity extends AppCompatActivity {
    private ImageView imageView;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        String context= (String) this.getIntent().getSerializableExtra("CONTEXT");
        Drawable drawable=null;
        drawable= ImageTools.stringToDrawable((String)this.getIntent().getSerializableExtra("PHOTO"));
        imageView=findViewById(R.id.body_imageView);
        imageView.setImageDrawable(drawable);
    }
    public static Intent newIntent(Context packageContext, String path){
        Intent intent=new Intent(packageContext,PictureActivity.class);
        intent.putExtra("CONTEXT",packageContext.toString());
        intent.putExtra("PHOTO", path);
        return intent;
    }
}