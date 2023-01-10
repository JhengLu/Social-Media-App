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
import android.os.Build;
import android.os.Bundle;
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

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class WriteBlogActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PICK_PHOTO = 102;
    private Button publishButton=null;
    private EditText contentEditText=null;
    private String profileString=null;
    private GridView gridView=null;
    private ImageView imageView=null;
    //pictureStrings用来发给服务器
    //pictures用来构造adapter展示缩略图
    private List<Drawable> pictures=new ArrayList<>();
    private ArrayList<String> pictureStrings=new ArrayList<>();
    private PictureAdapter pictureAdapter=null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_blog);
        initView();
    }
    private void initView(){
        publishButton=findViewById(R.id.publish_content_button);
        publishButton.setOnClickListener(this);
        contentEditText=findViewById(R.id.content_editText);
        /*
                parent:被点击的Adapter对象
                view： 被点击的Item（可用于获取该item内的组件）
                position：被点击的是第几个item(从0开始，0算第一个，类似数组)
                id：当前点击的item在listview 里的第几行的位置，通常id与position的值相同
             */
        gridView=findViewById(R.id.photo_write_gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == parent.getChildCount() - 1) {//添加图片
                    if (ContextCompat.checkSelfPermission(WriteBlogActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(WriteBlogActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
                    } else {
                        //打开相册
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, PICK_PHOTO); // 打开相册
                    }
                } else {
                    viewPluImg(position);
                }
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                pictures.remove(position);
                pictureStrings.remove(position);
                pictureAdapter=new PictureAdapter(pictures,WriteBlogActivity.this);
                gridView.setAdapter(pictureAdapter);
                return true;
            }
        });
        pictureAdapter=new PictureAdapter(pictures,WriteBlogActivity.this);
        gridView.setAdapter(pictureAdapter);
    }
    private void viewPluImg(int position) {
        Intent intent = PictureActivity.newIntent(WriteBlogActivity.this,pictureStrings.get(position));
        startActivity(intent);
    }

    public static Intent newIntent(Context packageContext){
        Intent intent=new Intent(packageContext,WriteBlogActivity.class);
        return intent;
    }

    public void onClick(View v) {
            String content=contentEditText.getText().toString();
            Log.d("M",content);
            ConnectionTools connectionTools=null;
            try {
                connectionTools=ConnectionTools.get();
                String result=connectionTools.sendBlog(content,pictureStrings);
                if(result.equals("success")){
                    Toast.makeText(this,"发布成功！",Toast.LENGTH_SHORT).show();
                    contentEditText.setText("");
                    pictures=new ArrayList<>();
                    pictureStrings=new ArrayList<>();
                    PictureAdapter pictureAdapter=new PictureAdapter(pictures,WriteBlogActivity.this);
                    gridView.setAdapter(pictureAdapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            contentEditText.setText("");
    }
    public class PictureAdapter extends BaseAdapter {
        List<Drawable> drawables=new ArrayList<>();
        Context context;
        public PictureAdapter(List<Drawable> drawables, Context context) {
            this.drawables=drawables;
            this.context=context;
        }

        @Override
        public int getCount() {
            int count = drawables == null ? 1 : drawables.size() + 1;
            return count;
        }

        @Override
        public Object getItem(int position) {
            return drawables.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.picture_abb, parent, false);
            imageView=convertView.findViewById(R.id.picture_imageView);
            if (position <drawables.size()) {
                //代表+号之前的需要正常显示图片
                Drawable drawable = drawables.get(position); //图片路径
                imageView.setImageDrawable(drawables.get(position));
            } else {
                imageView.setImageDrawable(getDrawable(R.drawable.addpicture));//最后一个显示加号图片
            }
            return convertView;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_PHOTO:
                if (resultCode == RESULT_OK) { // 判断手机系统版本号
                    profileString=ImageTools.getImageFromPhoto(data,WriteBlogActivity.this);
                    String picturePath=ImageTools.getDrawablePath(data,WriteBlogActivity.this);
                    Drawable picture=ImageTools.stringToDrawable(profileString);
                    pictures.add(picture);
                    pictureStrings.add(profileString);
                    PictureAdapter pictureAdapter=new PictureAdapter(pictures,WriteBlogActivity.this);
                    gridView.setAdapter(pictureAdapter);
                }
                break;
            default:
                break;
        }
    }

}