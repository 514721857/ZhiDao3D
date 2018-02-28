package com.zhidao.sgr.zhidao3d.image_handle;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;

import java.io.File;
import java.io.IOException;

/**
 * Created by 沈国荣 on 2017/11/18.
 * QQ:514721857
 * Description:
 */

public class ViewUtil {
    /**
     * 【多点缩放】获取手指间的距离
     *
     * @param event
     * @return
     */
   public static float getMultiTouchDistance(MotionEvent event) {
float result = 0;
     
       try {
           float x = event.getX(0) - event.getX(1);
           float y = event.getY(0) - event.getY(1);
           result= (float) Math.sqrt(x * x + y * y);
       } catch (IllegalArgumentException e) {
           // TODO Auto-generated catch block   
           e.printStackTrace();
       }
       return result;
       
    }
    /**
     * 获取手势中心点
     *
     * @param event
     */
    public static PointF getMidPoint(MotionEvent event) {
        PointF point = new PointF();
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
        return point;
    }
    /**
     * 按比例缩放图片
     *
     * @param origin 原图
     * @param ratio  比例
     * @return 新的bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(ratio, ratio);
        Bitmap newBM=null;
        try {
            newBM = Bitmap.createBitmap(origin, 0, 0, width-1, height-1, matrix, true);
        }catch (Exception e){
            System.out.println("异常错误"+e);
        }

        if (origin != null && !origin.isRecycled())
        {
            origin=null;
        }
        return newBM;



/*        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM=null;
        try {
            newBM = Bitmap.createBitmap(origin, 0, 0, width-1, height-1, matrix, true);
        }catch (Exception e){
            System.out.println("异常错误"+e);
        }

        if (origin != null && !origin.isRecycled())
        {
            origin=null;
        }
        return newBM;*/
    }

    /* 计算两点间的距离
  *
  * @param source
  * @param width
  * @param height
  * @return
  */
    public static double distanceBetween(Point2 p1, Point2 p2) {
        double dis = Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
        return dis;
    }

    //判断是否是move移动
    public static boolean isMoveAction(float dx,float dy,int isMoves){
        return Math.sqrt(dx*dx+dy*dy)>isMoves;
    }
//    将图片平铺到指定宽高
    public static Bitmap createRepeater(int hight,int width, Bitmap src){
        int count = (width + src.getWidth()) / src.getWidth();
        int counts = (hight + src.getHeight()) / src.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, hight, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        for(int idx = 0; idx < count; ++ idx){
            canvas.drawBitmap(src, idx * src.getWidth(), 0, null);

            for(int idxh = 0; idxh < counts; ++ idxh){
                canvas.drawBitmap(src, idx * src.getWidth(), idxh * src.getHeight(), null);
            }
        }

        return bitmap;
    }

    /* 缩放图片
*
* @param source
* @param width
* @param height
* @return
*/
    public  static Bitmap transImage(Bitmap source, int width, int height) {
        try {
            int bitmapWidth = source.getWidth();
            int bitmapHeight = source.getHeight();
            // 缩放图片的尺寸
            float scaleWidth =  width / bitmapWidth;
            float scaleHeight = height / bitmapHeight;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            // 产生缩放后的Bitmap对象
            Bitmap resizeBitmap = Bitmap.createBitmap(source, 0, 0, width-1, height, matrix, false);
            if (!source.isRecycled()) {
                // 记得释放资源，否则会内存溢出
                source.recycle();
            }
            return resizeBitmap;

        } catch (Exception e) {
            e.printStackTrace();
            return source;
        }

    }

    /**
     * 从文件获取图片（预防内存溢出）
     *
     * @param filePath
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getBitmapFromFile(String filePath, int width, int height) {
        File dst = new File(filePath);
        Bitmap bmp = null;
        if (null != dst && dst.exists()) {
            BitmapFactory.Options opts = null;
            try {
                if (width > 0 && height > 0) {
                    opts = new BitmapFactory.Options();
                    // 设置inJustDecodeBounds为true后，decodeFile并不分配空间，此时计算原始图片的长度和宽度
                    opts.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(dst.getPath(), opts);

                    opts.inTempStorage = new byte[12 * 1024];
                    opts.inPreferredConfig = Bitmap.Config.RGB_565; // 这里用2字节显示图片，一般是4字节（默认
                    // 判断如果使用内存大于3M，你可以修改这个参数，一般10M应该OK
                    int ratio = opts.outWidth * opts.outHeight * 2 / 3000000;
                    opts.inSampleSize = 1; // 不缩放，保持原来的大小
                    if (ratio >= 1) {
                        opts.inSampleSize = 3; // 宽度和高度将缩小两倍，width/2 and
                        // height/2，所以图片会模糊，不过不会消耗很多内存
                    }
                    Log.i("打开图片路径", filePath);

                    if (ratio >= 4) {
                        opts.inSampleSize = 6;
                    } else {
                        opts.inSampleSize = calculateInSampleSize(opts, width, height);
                    }
                    // opts.inTempStorage = new byte[12 * 1024];

                    opts.inScaled = true;

                    // 这里一定要将其设置回false，因为之前我们将其设置成了true
                    opts.inJustDecodeBounds = false;

                }

                bmp = BitmapFactory.decodeFile(dst.getPath(), opts);
            } catch (Exception e) {
                e.printStackTrace();
                bmp = null;
            }

        }
        return bmp;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // Raw height and width of image

        final int height = options.outHeight;

        final int width = options.outWidth;

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;

            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both

            // height and width larger than the requested height and width.

            while ((halfHeight / inSampleSize) > reqHeight

                    && (halfWidth / inSampleSize) > reqWidth) {

                inSampleSize *= 2;

            }

        }

        return inSampleSize;

    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,

                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions

        final BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set

        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(res, resId, options);

    }

    /**
     * 解决小米手机上获取图片路径为null的情况
     *
     * @param intent
     * @return
     */
    public Uri getCaptureUri(android.content.Intent intent, Activity parentActivity) {
        Uri uri = intent.getData();
        String type = intent.getType();
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = parentActivity.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Images.ImageColumns._ID }, buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    // set _id value
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    // do nothing
                } else {
                    Uri uri_temp = Uri.parse("content://media/external/images/media/" + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path
     *            图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm
     *            需要旋转的图片
     * @param degree
     *            旋转角度
     * @return 旋转后的图片
     */
    public static  Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

}
