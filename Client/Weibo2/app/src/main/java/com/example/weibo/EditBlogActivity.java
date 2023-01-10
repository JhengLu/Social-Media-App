package com.example.weibo;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
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

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EditBlogActivity extends AppCompatActivity {
    private static final int PICK_PHOTO = 102;
    private Button publishButton=null;
    private EditText contentEditText=null;
    private String profileString=null;
    private String blogId=null;
    private GridView gridView=null;
    private ImageView imageView=null;
    private List<Drawable> pictures=new ArrayList<>();
    private ArrayList<String> pictureStrings=new ArrayList<>();
    private PictureAdapter pictureAdapter=null;
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_blog);
        initView();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initView(){
        blogId=getIntent().getStringExtra("blogId");
        publishButton=findViewById(R.id.edit_content_button);
        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=contentEditText.getText().toString();
                Log.d("M",content);
                ConnectionTools connectionTools=null;
                try {
                    connectionTools=ConnectionTools.get();
                    String result=connectionTools.editBlog(blogId,content,pictureStrings);
                    if(result.equals("success")){
                        Toast.makeText(EditBlogActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        contentEditText=findViewById(R.id.edit_content_editText);
        gridView=findViewById(R.id.photo_edit_gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == parent.getChildCount() - 1) {//添加图片
                    if (ContextCompat.checkSelfPermission(EditBlogActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EditBlogActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
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
        //长按删除
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                pictures.remove(position);
                pictureStrings.remove(position);
                pictureAdapter=new PictureAdapter(pictures, EditBlogActivity.this);
                gridView.setAdapter(pictureAdapter);
                return true;
            }
        });
        pictureAdapter=new PictureAdapter(pictures, EditBlogActivity.this);
        ConnectionTools connectionTools=null;
        try {
            connectionTools=ConnectionTools.get();
            String resultString=connectionTools.getBlog(blogId);
            String blogString=resultString.split("\\$picture\\$")[0];
            String pictureString=resultString.split("\\$picture\\$")[1];
            JSONObject resultJson= JSONArray.parseArray(blogString).getJSONObject(0);
            JSONObject pictureJson=JSONObject.parseObject(pictureString);
            contentEditText.setText(resultJson.getString("blogContent").replace("\\n","\n"));
            for(int i=0;i<pictureJson.size();i++){
                String drawableString=pictureJson.getString(i+"");
                pictureStrings.add(drawableString);
                Drawable drawable=ImageTools.stringToDrawable(drawableString);
                pictures.add(drawable);
            }
            PictureAdapter pictureAdapter=new PictureAdapter(pictures,EditBlogActivity.this);
            gridView.setAdapter(pictureAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void viewPluImg(int position) {
        Intent intent = PictureActivity.newIntent(EditBlogActivity.this,pictureStrings.get(position));
        startActivity(intent);
    }

    public static Intent newIntent(String blogId,Context packageContext){
        Intent intent=new Intent(packageContext, EditBlogActivity.class);
        intent.putExtra("blogId",blogId);
        return intent;
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
                    profileString=ImageTools.getImageFromPhoto(data, EditBlogActivity.this);
                    String picturePath=ImageTools.getDrawablePath(data, EditBlogActivity.this);
                    Drawable picture=ImageTools.stringToDrawable(profileString);
                    pictures.add(picture);
                    pictureStrings.add(profileString);
                    PictureAdapter pictureAdapter=new PictureAdapter(pictures, EditBlogActivity.this);
                    gridView.setAdapter(pictureAdapter);
                }
                break;
            default:
                break;
        }
    }

}