package com.zhidao.sgr.zhidao3d;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.zhidao.sgr.zhidao3d.image_handle.DrawLineImg;
import com.zhidao.sgr.zhidao3d.image_handle.MessaageEvenBus;
import com.zhidao.sgr.zhidao3d.image_handle.ScannerUtils;

import org.greenrobot.eventbus.EventBus;


public class CurtainDesignActivity extends AppCompatActivity {
    private Bitmap bgBitmap;
//    ImageView img;
    DrawLineImg drawLineImg;
    ImageView img;
    Button line,curve,drawpoint,redraw,finsh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curtain_design);
        drawLineImg=(DrawLineImg)findViewById(R.id.drawLineImg);
        bgBitmap = BaseApplication.getInstance().getIntentBitmap();
        System.out.println("bgBitmap"+bgBitmap);
//        img=(ImageView)findViewById(R.id.img);
        if(bgBitmap!=null){

            drawLineImg.setImageBitmap(bgBitmap);
        }
        img=(ImageView)findViewById(R.id.img);
        line=(Button)findViewById(R.id.line);
        curve=(Button)findViewById(R.id.curve);
        redraw=(Button)findViewById(R.id.redraw);
        finsh=(Button)findViewById(R.id.finsh);
        drawpoint=(Button)findViewById(R.id.drawpoint);
        finsh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Bitmap bitmap=    drawLineImg.Dofinsh();
                if(bitmap!=null){
                    img.setVisibility(View.VISIBLE);
                    img.setImageBitmap(bitmap);
                    drawLineImg.setVisibility(View.GONE);
                 ScannerUtils.saveImageToGallery(CurtainDesignActivity.this,bitmap, ScannerUtils.ScannerType.MEDIA);
//                    BaseApplication.getInstance().setHandleBitmap(bitmap);
                    EventBus.getDefault().post(new MessaageEvenBus());
                    Intent intent=new Intent(CurtainDesignActivity.this,MainActivity.class);
                    startActivity(intent);
                    CurtainDesignActivity.this.finish();
                }
            }
        });
        redraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawLineImg.retset();
            }
        });
        curve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawLineImg.setCurve();
            }
        });
        line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawLineImg.setLine();
            }
        });
        drawpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawLineImg.setDrawPoint();
            }
        });

    }
}
