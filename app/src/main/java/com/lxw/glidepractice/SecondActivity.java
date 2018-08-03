package com.lxw.glidepractice;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.lxw.glide.Glide;
import com.lxw.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.lxw.glide.load.resource.drawable.GlideDrawable;
import com.lxw.glide.request.RequestListener;
import com.lxw.glide.request.target.Target;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView iv = findViewById(R.id.iv);
        ImageView iv2 = findViewById(R.id.iv2);
        String url = "http://120.77.218.76/photoserver/photo/2018/06/01/de4762df-e1b5-4a81-b5e9-bd124b6cb339.jpg";
        String url2 = "http://120.77.218.76/photoserver/photo/2018/06/01/27806746-6997-40bd-89da-77e67f9524b0.jpg";
        Glide.with(this).load(url2).setRequestListener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                GlideBitmapDrawable drawable = (GlideBitmapDrawable) resource;
                Bitmap bitmap = drawable.getBitmap();
                boolean recycled = bitmap.isRecycled();
                System.out.println(recycled+"  recycled:  "+bitmap);
                iv.setImageBitmap(bitmap);
                return true;
            }
        }).into(iv);
//        Glide.with(this).load(url).placeholder(R.mipmap.ic_launcher).listener(new RequestListener<String, GlideDrawable>() {
//            @Override
//            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                return false;
//            }
//
//            @Override
//            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                GlideBitmapDrawable drawable = (GlideBitmapDrawable) resource;
//                Bitmap bitmap = drawable.getBitmap();
//                iv.setImageBitmap(bitmap);
//                System.out.println(bitmap);
//                return false;
//            }
//        }).into(iv);
//        String url2 ="http://120.77.218.76/photoserver/photo/2018/06/01/27806746-6997-40bd-89da-77e67f9524b0.jpg";
//        Glide.with(this).load(url2).into(iv2);
    }
}
