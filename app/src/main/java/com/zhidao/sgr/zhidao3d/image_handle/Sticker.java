package com.zhidao.sgr.zhidao3d.image_handle;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.provider.Settings;
import android.view.MotionEvent;

/**
 * 作者：ZhouYou
 * 日期：2016/12/2.
 */
public class Sticker {
    // 绘制图片的矩阵
    private Matrix matrix;
    // 原图片
    private Bitmap srcImage;
    int w,h;
    float minX;
    float minY;

    Sticker(Bitmap bitmap) {
        this.srcImage = bitmap;
        matrix = new Matrix();
     
    }
void SetMatrix(Point2[] pointArr){
            w=srcImage.getWidth();
            h=srcImage.getHeight();
            
            //为了控制图片绘画出界
            minX = Math.min(Math.min( pointArr[0].x, pointArr[1].x), Math.min( pointArr[2].x,  pointArr[3].x));
    		minY = Math.min(Math.min( pointArr[0].y, pointArr[1].y), Math.min(pointArr[2].y, pointArr[3].y));

            
    MatrixCalculator t = MatrixCalculator.calculate(
            0, 0,
            w, 0,
            w, h,
            0, h,
            pointArr[0].x, pointArr[0].y,
            pointArr[1].x, pointArr[1].y,
            pointArr[2].x,pointArr[2].y,
            pointArr[3].x,pointArr[3].y);
    float[] values = { (float) t.m00, (float) t.m01, (float) t.m02, (float) t.m10, (float) t.m11, (float) t.m12, (float) t.m20, (float) t.m21,
            (float) t.m22 };
    matrix.setValues(values);
}

    /**
     * 绘制图片
     *
     * @param canvas
     */
    void draw(Canvas canvas,Path path,RectF rectF) {
   /*     Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));*/
//        canvas.drawColor(Color.TRANSPARENT);
        Bitmap dstbmp = Bitmap.createBitmap(srcImage, 0, 0, srcImage.getWidth(), srcImage.getHeight(), matrix, true);
    	if(StickerView.mode == ActionMode.DRAG){
            canvas.drawBitmap( DrawImagePathadjust(dstbmp,path,rectF,minX,minY,StickerView.mTotalWidth, StickerView.mTotalHeight),0,0, null);
//            canvas.drawBitmap(dstbmp, minX, minY, null);
            System.out.println("canvas.drawBitmap(dstbmp, minX, minY, null);");
    	}else{
    		canvas.drawBitmap(srcImage, matrix, null);
            System.out.println("canvas.drawBitmap(srcImage, matrix, null);");

    	}
		
        
    }


    // 将path和图片（填充）bitmap取交集
    private Bitmap DrawImagePathadjust(Bitmap source, Path path,RectF rectF,float minx,float miny,int mTotalWidth,int mTotalHeight ) {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);

        Bitmap target = Bitmap.createBitmap(mTotalWidth, mTotalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);

//        paint.setColor(Color.TRANSPARENT);
//        canvas.drawPath(path,paint);

        canvas.drawBitmap(makeDst(path,mTotalWidth,mTotalHeight), 0, 0, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(source,minx,miny,paint);
//        canvas.drawPath(path,paint);
//        Shader  mBitmapShader = new BitmapShader(source, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
//        paint.setShader(mBitmapShader);
        //w和h分别是屏幕的宽和高，也就是你想让图片显示的宽和高


//        canvas.drawRect(rectF,paint);
        //还原Xfermode
        paint.setXfermode(null);

        return target;
    }

    private Bitmap makeDst(Path path,int mTotalWidth,int mTotalHeight) {
        Bitmap bm = Bitmap.createBitmap(mTotalWidth, mTotalHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
//        p.setColor(Color.TRANSPARENT);
        c.drawPath(path,p);
        return bm;
    }
    /**
     * 获取手势中心点
     *
     * @param event
     */
    PointF getMidPoint(MotionEvent event) {
        PointF point = new PointF();
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
        return point;
    }

    /**
     * 获取图片中心点
     */
    PointF getImageMidPoint(Matrix matrix) {
        PointF point = new PointF();
        float[] points = PointUtils.getBitmapPoints(srcImage, matrix);
        float x1 = points[0];
        float x2 = points[2];
        float y2 = points[3];
        float y4 = points[7];
        point.set((x1 + x2) / 2, (y2 + y4) / 2);
        return point;
    }

    /**
     * 获取手指的旋转角度
     *
     * @param event
     * @return
     */
    float getSpaceRotation(MotionEvent event, PointF imageMidPoint) {
        double deltaX = event.getX(0) - imageMidPoint.x;
        double deltaY = event.getY(0) - imageMidPoint.y;
        double radians = Math.atan2(deltaY, deltaX);
        return (float) Math.toDegrees(radians);
    }

    /**
     * 【多点缩放】获取手指间的距离
     *
     * @param event
     * @return
     */
    float getMultiTouchDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 【单点缩放】获取手指和图片中心点的距离
     *
     * @param event
     * @return
     */
    float getSingleTouchDistance(MotionEvent event, PointF imageMidPoint) {
        float x = event.getX(0) - imageMidPoint.x;
        float y = event.getY(0) - imageMidPoint.y;
        return (float) Math.sqrt(x * x + y * y);
    }

    RectF getSrcImageBound() {//加上四个小圆点
        RectF dst = new RectF();
        matrix.mapRect(dst, new RectF(-30, -30, getStickerWidth()+30, getStickerHeight()+30));
        return dst;
    }

    int getStickerWidth() {
        return srcImage == null ? 0 : srcImage.getWidth();
    }

    int getStickerHeight() {
        return srcImage == null ? 0 : srcImage.getHeight();
    }

    Matrix getMatrix() {
        return matrix;
    }

    Bitmap getSrcImage() {
        return srcImage;
    }
}
