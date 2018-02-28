package com.zhidao.sgr.zhidao3d;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.zhidao.sgr.zhidao3d.image_handle.ImgUtil;
import com.zhidao.sgr.zhidao3d.image_handle.ViewUtil;

import java.io.File;


/**
 * 选择照片Activity
 * @author weijm
 *
 */
public class SelectPictureActivity extends Activity implements OnClickListener {
    /** 临时文件名称 */
    public static final String IMAGE_FILE_NAME = "tmp_image.png";

    /** 请求码 */
    public static final int IMAGE_REQUEST_CODE = 0;
    public static final int CAMERA_REQUEST_CODE = 1;
    public static final int RESULT_REQUEST_CODE = 2;



    View capture_view;
    View albums_view;
    View cancel_view;

    private String imagePath;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_picture);
        initViews();

    }
    private void initViews() {
        capture_view = findViewById(R.id.capture_view);
        albums_view = findViewById(R.id.albums_view);
        cancel_view = findViewById(R.id.cancel_view);
        capture_view.setOnClickListener(this);
        albums_view.setOnClickListener(this);
        cancel_view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v==this.capture_view){
            try {
                Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 判断存储卡是否可以用，可用进行存储
                String state = Environment.getExternalStorageState();
                if (state.equals(Environment.MEDIA_MOUNTED)) {
                    File fileDir = new File(BaseApplication.defaultFileSavePath);
                    if (!fileDir.exists()) {
                        fileDir.mkdirs();
                    }

                    String filePath = fileDir + "/" + IMAGE_FILE_NAME;
                    File imageFile = new File(filePath);
                    imageFile.deleteOnExit();
                    File vFile = new File(fileDir, IMAGE_FILE_NAME);
                    Uri uri = Uri.fromFile(vFile);

                    intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                    // 由于小米手机无法通过这种方式传递，所以这里先记录路径
                    BaseApplication.getInstance().intentUri = filePath+"";
                    this.imagePath = BaseApplication.getInstance().intentUri;
                }

                startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
            } catch (Exception e) {
                Toast.makeText(this,"拍照出现错误：",Toast.LENGTH_SHORT).show();

            }
        }
        else if (v==albums_view){

            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            startActivityForResult(intent, IMAGE_REQUEST_CODE);

		/*	Intent intentFromGallery = new Intent();
			intentFromGallery.setType("image/*"); // 设置文件类型
			intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);*/

        }
        else if (v==cancel_view){
            this.finish();
        }

    }
/*


    /**
     * 一键成图选择的回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 结果码不等于取消时候
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:

                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上系统使用这个方法处理图片
                        BaseApplication.getInstance().setIntentBitmap(ImgUtil.handleImageOnKitKat(this, data));        //ImgUtil是自己实现的一个工具类
                    } else {
                        //4.4以下系统使用这个方法处理图片
                        BaseApplication.getInstance().setIntentBitmap(ImgUtil.handleImageBeforeKitKat(this, data));
                    }

                    System.out.println("相册返回的btmap"+ BaseApplication.getInstance().getIntentBitmap());
                   if(BaseApplication.SelectMode==1){
                       Intent intent = new Intent();
                       setResult(1, intent);

                       //  结束当前页面(关闭当前界面)
                       finish();
                   }else{
                       Intent intent = new Intent(this, CurtainDesignActivity.class);
                       Bundle bundle = new Bundle();

                       startActivity(intent);

                       this.finish();
                   }




                    break;
                case CAMERA_REQUEST_CODE://照相机拍照返回的结果
                    try {
                        // 判断存储卡是否可以用，可用进行存储
                        String state = Environment.getExternalStorageState();
                        if (state.equals(Environment.MEDIA_MOUNTED)) {
                            processImage(data, 1);
                        } else {
                            Toast.makeText(getApplicationContext(), "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                        }
                        this.finish();
                        break;
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "拍照出现异常，错误：", Toast.LENGTH_SHORT).show();
//                        new MyDialog(SelectPictureActivity.this).showSimpleDialog("拍照出现异常，错误：" + e.getMessage());
                        this.finish();
                    }

            }
        }
        else {
            this.finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 处理一键成图图片
     *
     * @param data
     * @param from
     *           1-拍照
     */
    private void processImage(Intent data, int from) {
        String picturePath = "";

        try {

            if (from == 0) {
                Uri photoUri = data.getData();
                String[] pojo = { MediaStore.Images.Media.DATA };
                CursorLoader cursorLoader = new CursorLoader(this, photoUri, pojo, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();
                cursor.moveToFirst();
                String path = cursor.getString(cursor.getColumnIndex(pojo[0]));
                if (path != null && path.length() > 0) {
                    picturePath = path;
                }
            } else {
                picturePath = BaseApplication.getInstance().intentUri;
            }

            if (picturePath == null || picturePath.length() == 0) {
                Toast.makeText(getApplicationContext(), "对不起，无法获取选中的图片信息", Toast.LENGTH_SHORT).show();

                return;
            }
          /*        float imageWidth = 480;
            float scale = imageWidth / (float) BaseApplication.screenWidth;
            float imageHeight = (float) BaseApplication.screenHeight * scale;*/

            //获取图片转角
            int degree = ViewUtil.getBitmapDegree(picturePath);
            Bitmap tmpBmp = ViewUtil.getBitmapFromFile(picturePath, (int)BaseApplication.screenWidth, (int) BaseApplication.screenHeight);
            //转角不为0时需要跳转角度
            if (degree>0){
                tmpBmp = ViewUtil.rotateBitmapByDegree(tmpBmp, degree);
            }
            BaseApplication.getInstance().setIntentBitmap(tmpBmp);
            System.out.println("拍照返回的btmap"+ BaseApplication.getInstance().getIntentBitmap());

            if(BaseApplication.SelectMode==1){
                Intent intent = new Intent();
                setResult(1, intent);

                //  结束当前页面(关闭当前界面)
                finish();
            }else{
                Intent intent = new Intent(this, CurtainDesignActivity.class);
                startActivity(intent);
                this.finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
