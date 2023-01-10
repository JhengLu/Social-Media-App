package com.example.weibo;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class ImageTools {
    //图片编码变图片
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Drawable stringToDrawable(String photoString){
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] b = decoder.decode(photoString);//Bitmap: 位图----最简单的Drawable, PNG or JPEG图片。
        Bitmap bitmap= BitmapFactory.decodeByteArray(b, 0, b.length);
        Drawable drawable=(Drawable)new BitmapDrawable(bitmap);
        return drawable;
    }

    //根据路径获取图片编码
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String loadImage(String path){
        String imgFile = path;
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];//available():返回与之关联的文件的字节数
            in.read(data);
            in.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        //对字节数组Base64编码
        Base64.Encoder encoder=Base64.getEncoder();
        //返回Base64编码过的字节数组字符串
        Log.i("70",data.toString());
        return encoder.encodeToString(data);
    }
//    videoPath = cursor.getString(cursor
//            .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));

    //根据数据获取图片地址
    public static String getDrawablePath(Intent data, Context context){
        String imagePath=null;
        Uri uri = data.getData();
        Log.d("uri_line77",uri.toString());
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的Uri，则通过document id处理

            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                Log.d("85line","is_media_document");
                // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection,context);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Log.d("88","is_downloads.documents");
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content: //downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null,context);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null,context);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        return imagePath;
    }
    //根据数据获取图片编码（结合了一下前两个函数）
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getCodeFromVideo(Intent data,  Context context) {
        String videoPath = null;
        Log.d("getCodeFromVideo", "getCodeFromVideo: ");

        videoPath=getVideoPath(data,context);
        Log.d("videopath", videoPath);
//        return "yes";
        if (videoPath == null)
        {
            Log.i("111-line", "nullhere");
        }
        return loadImage(videoPath);
    }

    //根据数据获取图片编码（结合了一下前两个函数）
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getImageFromPhoto(Intent data,  Context context) {
        String imagePath = null;
        imagePath=getDrawablePath(data,context);
        Log.d("imagepath", imagePath);
        if (imagePath == null)
        {
            Log.i("111-line", "nullhere");
        }

        return loadImage(imagePath);
    }
    public static String getVideoPath(Intent data,Context context){
        Uri selectedUri = data.getData();
        Log.d("135_videopath",selectedUri.toString());
        String mimeType = null;
        String path = null;
        String videoPath=null;
        try {
            Cursor cursor = context.getContentResolver().query(selectedUri, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    videoPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                }
            }
            cursor.close();

        }catch (Exception e){
            Log.e("cursor",Log.getStackTraceString(e));
        }
        return videoPath;


    }

    //中间函数
    public static String getImagePath(Uri uri, String selection, Context context) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    //图片地址变图片
    static Drawable imagePathToDrawable(String imagePath) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            bitmap=compressImage(bitmap);
            Drawable drawable=(Drawable)new BitmapDrawable(bitmap);
            return drawable;
    }
    /**
     * 压缩图片
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100 && options > 0) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
}
