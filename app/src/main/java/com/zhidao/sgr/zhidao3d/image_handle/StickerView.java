package com.zhidao.sgr.zhidao3d.image_handle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.zhidao.sgr.zhidao3d.BaseApplication;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 作者：ZhouYou
 * 日期：2016/12/2.
 */
@SuppressLint("AppCompatCustomView")
public class StickerView extends View {

    private Context context;
    // 被操作的贴纸对象
    private Sticker sticker;
   // 手指按下时图片的矩阵
    private Matrix downMatrix = new Matrix();
    // 手指移动时图片的矩阵
    private Matrix moveMatrix = new Matrix();
    // 多点触屏时的中心点
    private PointF midPoint = new PointF();
    // 图片的中心点坐标
    private PointF imageMidPoint = new PointF();
    private Point2 pianyi=null;//偏移量
    public static int mTotalWidth,mTotalHeight;
    private Point2 madjustPoint=null;

    // 删除操作图片
    private StickerActionIcon removeIcon;
    // 删除操作图片
    private StickerActionIcon dragIcon;
    // 绘制图片的边框
    private Paint paintEdge;

    private Path path = new Path();

    private Path pathLine = new Path();

    // 触控模式
    public static int mode;

    //功能模式
    public  int functionMode;
    // 是否正在处于编辑
    private boolean isEdit = true;
    private int width;//设置高
    private int height;//设置高
    private boolean isClose=false;//判断选择区域是否闭合，如果闭合那么不能在画了
//    private boolean isSelect=false;//判断是否画选择区域，如果是isselect为true，那么表示的是墙纸，饰品的操作，如果为false则为窗帘的操作，窗帘只有四个点
private Paint brush;//调整时四个点和边框的画笔
    // 贴纸的操作监听
    private OnStickerActionListener listener;

    private Point2[] pointArr;//图形四点的坐标

    private Point2 downPoint = null;
    private Point2 besidePointLine = null;
    private Point2 orgBesidePointLine = null;
    private Point2 besidePoint = null;

    private Point2 orgBesidePoint = null;
    private float defaultWidth = 300;
    int screenWidth;
    int screenHeight;
    private int position=-1;//记录需要变动的点的位置
    private List<Point2> pointLine=new ArrayList() ;//画线的数组坐标
    private Point2 CurrentCirclePonit=null;//红心圆点的圆心
    private Paint mimagePaint;//封闭后画笔
    private Path mAdjustpath;//调整是的路径
    private boolean isAdjust=false;//true表示调整
    private boolean isSelect=false;//选区
    private boolean isIssale=false;//缩放
    private boolean isAdjustshow=false;//表示调整时显示的样式
    private Path mPath;//选择区时候的路径
    private Bitmap adjustImage = null;
    private Bitmap mBitmap;
    Matrix matrix = new Matrix();
    private int isMove;//是否是可以移动的点
    Bitmap   tempbmp;
    RectF bounds;//调整时的矩形区域
    private  float scale=0;
    private boolean issale=false;//缩放
    private Path mendPath;//最后拖动时的path，要保持不变
    private long startClickTime;
    private boolean isdrage=false;//表示拖动调整区
    private Point2 orgBesidePointAdjust=null;
    private static final int MAX_CLICK_DURATION = 200;


    public void setOnStickerActionListener(OnStickerActionListener listener) {
        this.listener = listener;
    }

    public StickerView(Context context,int type) {
        this(context, null,type);
    }

    public StickerView(Context context, AttributeSet attrs,int type) {
        this(context, attrs, 0,type);
    }

    public StickerView(Context context, AttributeSet attrs, int defStyleAttr,int type) {
        super(context, attrs, defStyleAttr);
            init(context,type);


    }

    private void init(Context context,int type) {
        isMove= ViewConfiguration.get(context).getScaledTouchSlop();
        functionMode=type;
        this.context = context;
        if(functionMode==FunctionMode.CURTAIN){
            removeIcon = new StickerActionIcon(context);
            dragIcon = new StickerActionIcon(context);
            // 设置屏幕高宽
            if (BaseApplication.screenWidth == 0)
                BaseApplication.screenWidth = 480;
            if (BaseApplication.screenHeight == 0)
                BaseApplication.screenHeight =800;
        }else if(functionMode==FunctionMode.WALLPAPER){
            setSelect();
            mPath = new Path();
            mAdjustpath = new Path();
            mimagePaint=new Paint();
            mimagePaint.setColor(Color.YELLOW);
            mimagePaint.setAntiAlias(true);
            mimagePaint.setStyle(Paint.Style.STROKE);
            mimagePaint.setStrokeJoin(Paint.Join.ROUND);
            mimagePaint.setStrokeWidth(5f);
            brush = new Paint();//画四个点
        }
//        setScaleType(ImageView.ScaleType.MATRIX);

        paintEdge = new Paint();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);//关闭硬件加速

    }
    @Override
    protected void onDraw(Canvas canvas) {
        if(functionMode==FunctionMode.WALLPAPER){  //画墙纸，需要先选择区域，而且不确定几个点，起码三个
            if(isAdjust){
                double w = adjustImage.getWidth();
                double h = adjustImage.getWidth();
                Point2 p0 = pointArr[0];
                Point2 p1 = pointArr[1];
                Point2 p2 = pointArr[2];
                Point2 p3 = pointArr[3];
                float minX = Math.min(Math.min(p0.x, p1.x), Math.min(p2.x, p3.x));
                float minY = Math.min(Math.min(p0.y, p1.y), Math.min(p2.y, p3.y));
                if(isAdjustshow){
                    RectF tempRect=new RectF();
                    mPath.computeBounds(tempRect,true);

                    MatrixCalculator t = MatrixCalculator.calculate(
                            0, 0, w,
                            0, w, h,
                            0, h,
                            p0.x, p0.y,
                            p1.x, p1.y,
                            p2.x, p2.y,
                            p3.x, p3.y);
                    float[] values = { (float) t.m00, (float) t.m01, (float) t.m02, (float) t.m10, (float) t.m11, (float) t.m12, (float) t.m20, (float) t.m21,
                            (float) t.m22 };
                    matrix.setValues(values);
                    tempbmp = Bitmap.createBitmap(adjustImage, 0, 0, adjustImage.getWidth(),adjustImage.getHeight(), matrix, true);
                    canvas.drawBitmap( DrawImagePathadjust(tempbmp,mendPath,bounds,minX,minY),0,0, null);

                    if (!tempbmp.isRecycled()) {
                        // 记得释放资源，否则会内存溢出
                        tempbmp.recycle();
                    }

                    if (isEdit) {
                            build4Path();
                            canvas.drawPath(mAdjustpath, mimagePaint);
                            makGallyPoints(canvas);
                    }

                }else if(isSelect){
                    tempbmp = Bitmap.createBitmap(adjustImage, 0, 0, adjustImage.getWidth(),adjustImage.getHeight(), matrix, true);
                    canvas.drawBitmap( DrawImagePathadjust(tempbmp,mendPath,bounds,minX,minY),0,0, null);
                    Bitmap mBitmaps= ViewUtil.scaleBitmap(tempbmp,scale);
                    if (isEdit) {
                        buildGallyLinePath();
                        canvas.drawPath(mPath,mimagePaint);//选区
                    }
                } else if(issale){
//                    if(scale!=0){
//                        matrix.postScale( scale,scale);

                        tempbmp = Bitmap.createBitmap(adjustImage, 0, 0, adjustImage.getWidth(),adjustImage.getHeight(), matrix, true);
                        Bitmap mBitmaps= ViewUtil.scaleBitmap(tempbmp,scale);
                   if(mBitmaps==null){
                       canvas.drawBitmap( DrawImagePathadjust(tempbmp,mendPath,bounds,minX,minY),0,0, null);
                   }else{
                       canvas.drawBitmap( DrawImagePathadjust(mBitmaps,mendPath,bounds,minX,minY),0,0, null);
                   }
//                    canvas.drawBitmap();

//                    }

                }

            }else{
                if(pointLine.size()!=0){
                    showGallyLines(canvas);
                }
            }



        }else if(functionMode==FunctionMode.CURTAIN){ //画窗帘，确定是四个点，无需选择区域
            if (sticker == null) return;

            buildPath();
            if(mode!=ActionMode.TRANS){
                sticker.SetMatrix(pointArr);
            }
            RectF bounds = new RectF();
            path.computeBounds(bounds,true);
            sticker.draw(canvas,path,bounds);

            if (isEdit) {
                dragIcon.draw(canvas,(pointArr[0].x+pointArr[1].x)/2,(pointArr[0].y+pointArr[3].y)/2);
//                removeIcon.drawDel(canvas, (pointArr[0].x+pointArr[1].x)/2, (pointArr[0].y+pointArr[1].y)/2);
                showPoints(canvas);
            }
        }
    }

    private void showPoints(Canvas canvas) {

        paintEdge.setColor(Color.YELLOW);
        paintEdge.setAntiAlias(true);
        // 绘制边框
        paintEdge.setStyle(Paint.Style.STROKE);
        paintEdge.setStrokeJoin(Paint.Join.ROUND);
        paintEdge.setStrokeWidth(5f);
        canvas.drawPath(path, paintEdge);
        // 绘制四个点
        makPoints(canvas);
    }
//画线
    private void showLines(Canvas canvas) {
        buildLinePath();
        paintEdge.setColor(Color.YELLOW);
        paintEdge.setAntiAlias(true);
        // 绘制边框
        paintEdge.setStyle(Paint.Style.STROKE);
        paintEdge.setStrokeJoin(Paint.Join.ROUND);
        paintEdge.setStrokeWidth(5f);
        canvas.drawPath(pathLine, paintEdge);
        showApoint(canvas);

    }
    //    将图片平铺和缩放到指定大小
    private Bitmap DrawTileSacle(Bitmap source,RectF rectF){
        return  DrawImagePathAdjust(mBitmap);
    }


    // 将path和图片（填充）bitmap取交集
    private Bitmap DrawImagePathAdjust(Bitmap source) {
        final Paint paint = new Paint();
        RectF rectF = new RectF(0, 0, mTotalWidth, mTotalWidth);
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(mTotalWidth, mTotalWidth, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawRect(rectF,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Shader  mBitmapShader = new BitmapShader(source, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        paint.setShader(mBitmapShader);
        canvas.drawRect(rectF,paint);
//        canvas.drawBitmap(source, 0, 0, paint);
        paint.setXfermode(null);
        return target;
    }

    //画线墙纸
    private void showGallyLines(Canvas canvas) {
        buildGallyLinePath();
        if(mBitmap!=null){
            if(!isAdjust){
                if(issale){
                    if(scale!=0){
                        Bitmap tempbitmap=ViewUtil.scaleBitmap(mBitmap,scale);
                        if(tempbitmap!=null){
                            canvas.drawBitmap( DrawImagePath(tempbitmap,mPath), 0,0, null);
                        }

                    }else{
                        canvas.drawBitmap( DrawImagePath(mBitmap,mPath), 0,0, null);
                    }
                } else{
                    canvas.drawBitmap( DrawImagePath(mBitmap,mPath), 0,0, null);
                }
                if(isSelect){
                    canvas.drawPath(mPath, mimagePaint);
                }
            }
        }else{
            if (isEdit) {
                if(isSelect){
                    canvas.drawPath(mPath, mimagePaint);
                }

            }
        }
            if (isEdit) {


                if(isSelect){
                    showApoint(canvas);
                }


                    }

    }

    // 将path和图片（填充）bitmap取交集
    private Bitmap DrawImagePathadjust(Bitmap source, Path path,RectF rectF,float minx,float miny) {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(mTotalWidth, mTotalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);

//        paint.setColor(Color.TRANSPARENT);
//        canvas.drawPath(path,paint);

        canvas.drawBitmap(makeDst(path), 0, 0, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source,minx,miny,paint);
        paint.setXfermode(null);

        return target;
    }


    private Bitmap makeDst(Path path) {
        Bitmap bm = Bitmap.createBitmap(mTotalWidth, mTotalHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
//        p.setColor(Color.TRANSPARENT);
        c.drawPath(path,p);
        return bm;
    }
    // 将path和图片（填充）bitmap取交集
    private Bitmap DrawImagePath(Bitmap source, Path path) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(mTotalWidth, mTotalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawPath(path,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Shader mBitmapShader = new BitmapShader(source, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        paint.setShader(mBitmapShader);
        RectF rectF = new RectF(0, 0, width, height);   //w和h分别是屏幕的宽和高，也就是你想让图片显示的宽和高
        canvas.drawRect(rectF,paint);
        return target;
    }
    // 画选择区域时的路径
    protected void buildGallyLinePath() {
        if(mPath!=null){
            mPath = new Path();

        }
        if(isClose) {  //拖拉
            for(int j=0;j<pointLine.size();j++){
                if(j==0){
                    mPath.moveTo(pointLine.get(j).x, pointLine.get(j).y);
                }else{
                    if(pointLine.size()-1==j){

                        mPath.close();
                    }else{
                        mPath.lineTo(pointLine.get(j).x, pointLine.get(j).y);

                    }
                }
            }
            CurrentCirclePonit=pointLine.get(pointLine.size()-2);
        }else{//继续画

            if(pointLine.size()==1){ //画一个红点
                CurrentCirclePonit=pointLine.get(0);
            }else{
                for(int j=0;j<pointLine.size();j++){
                    if(j==0){
                        mPath.moveTo(pointLine.get(j).x, pointLine.get(j).y);
                    }else{
                        if(pointLine.size()-1==j){//最后一点先不画
                        }else{
                            mPath.lineTo(pointLine.get(j).x, pointLine.get(j).y);
                        }
                    }

                }
            }

            if(ViewUtil.distanceBetween(pointLine.get(pointLine.size()-1),pointLine.get(0))<30){  //闭合
                mPath.close();
                if(pointLine.size()>=3){
                    CurrentCirclePonit=pointLine.get(pointLine.size()-2);
                    isClose=true;
                }

            }else{
                mPath.lineTo(pointLine.get(pointLine.size()-1).x,pointLine.get(pointLine.size()-1).y);
                CurrentCirclePonit=pointLine.get(pointLine.size()-1);
            }

        }

    }

    //画一个圆点
    private void showApoint(Canvas canvas){
        Paint paint = new Paint();
        paint.setStrokeWidth(6);
        paint.setColor(Color.RED);
        canvas.drawCircle(CurrentCirclePonit.x,CurrentCirclePonit.y, 6, paint);
    }


    protected void buildLinePath() {
        if(pathLine!=null){
            pathLine = new Path();
        }
       if(isClose) {//拖拉

       }else{  //继续画

           if(pointLine.size()==1){//画一个红点
               CurrentCirclePonit=pointLine.get(0);

           }else{
               for(int j=0;j<pointLine.size();j++){
                   if(j==0){
                       pathLine.moveTo(pointLine.get(j).x, pointLine.get(j).y);
                   }else{
                       if(pointLine.size()-1==j){//最后一点先不画

                       }else{
                           pathLine.lineTo(pointLine.get(j).x, pointLine.get(j).y);
                       }

                   }

               }
           }

           if( distanceBetween(pointLine.get(pointLine.size()-1),pointLine.get(0))<30){
               pathLine.close();
               if(pointLine.size()>=3){
                   CurrentCirclePonit=pointLine.get(pointLine.size()-2);
                   isClose=true;
               }

           }else{
               pathLine.lineTo(pointLine.get(pointLine.size()-1).x,pointLine.get(pointLine.size()-1).y);
               CurrentCirclePonit=pointLine.get(pointLine.size()-1);
           }

       }




    }

    //画调整时的路径
    protected void build4Path() {
        if(mAdjustpath!=null){
            mAdjustpath = new Path();
        }
        int i = 0;
        Point2 p0 = null;
        for (Point2 p : pointArr) {
            if (i == 0) {
                p0 = p;
                mAdjustpath.moveTo(p.x, p.y);
            } else {
                mAdjustpath.lineTo(p.x, p.y);
            }

            i++;
        }
        mAdjustpath.close();
    }

    protected void buildPath() {
        if(path!=null){
            path = new Path();
        }
        int i = 0;
        Point2 p0 = null;
        for (Point2 p : pointArr) {
            if (i == 0) {
                p0 = p;
                path.moveTo(p.x, p.y);
            } else {
                path.lineTo(p.x, p.y);
            }

            i++;
        }
        path.close();
    }

    private void makPoints(Canvas canvas) {
        int i = 1;
        for (Point2 p : pointArr) {
            paintEdge.setColor(Color.BLUE);
            paintEdge.setStrokeWidth(8);
            paintEdge.setAlpha(60);
            paintEdge.setStyle(Paint.Style.FILL);
            paintEdge.setColor(Color.YELLOW);
            canvas.drawCircle(p.x, p.y, 30, paintEdge);
            paintEdge.setAlpha(60);
            paintEdge.setColor(Color.BLUE);
            canvas.drawCircle(p.x, p.y, 22, paintEdge);
            paintEdge.setAlpha(60);
            paintEdge.setColor(Color.WHITE);
            paintEdge.setTextSize(24);
            canvas.drawText(i+"", p.x - 5, p.y + 7, paintEdge);
            i++;
        }
    }

    // 绘制四个点和边框
    private void makGallyPoints(Canvas canvas) {
//
        int i = 1;
        for (Point2 p : pointArr) {
            brush.setColor(Color.BLUE);
            brush.setAntiAlias(true);
            brush.setStrokeWidth(8);
            brush.setAlpha(60);
            brush.setStyle(Paint.Style.FILL);
            brush.setColor(Color.YELLOW);
            canvas.drawCircle(p.x, p.y, 30, brush);
            brush.setAlpha(60);
            brush.setColor(Color.BLUE);
            canvas.drawCircle(p.x, p.y, 22, brush);
            brush.setAlpha(60);
            brush.setColor(Color.WHITE);
            brush.setTextSize(24);
            canvas.drawText(i+"", p.x - 5, p.y + 7, brush);
            i++;
        }
    }

/*    // 手指按下屏幕的X坐标
    private float downX;
    // 手指按下屏幕的Y坐标
    private float downY;*/
    // 手指之间的初始距离
    private float oldDistance;
    // 手指之间的初始角度
    private float oldRotation;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        boolean isStickerOnEdit = true;
        float pointX = event.getX();
        float pointY = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downPoint = new Point2(pointX, pointY);
                if(functionMode==FunctionMode.CURTAIN){
                    if (sticker == null) return false;
                    // 删除操作
                    if (removeIcon.isInActionCheck(event)) {
                        if (listener != null) {
                            listener.onDelete();
                        }
                    }else if (dragIcon.isInActionCheck(event)) {
                        mode = ActionMode.TRANS;// 平移手势验证
                    }
                    // 拖拉手势
                    else if (isInStickerArea(sticker, event)) {
                        mode = ActionMode.DRAG;
                        besidePoint = null;//该点与点击点最近的点
                        if (pointArr.length != 0) {
                            double minDis = 0;
                            double tmpDis = 0;
                            for (Point2 p : pointArr) { //第一个点
                                if (besidePoint == null) {
                                    besidePoint = p;
                                    minDis = distanceBetween(p, downPoint);
                                } else {
                                    tmpDis = distanceBetween(p, downPoint);
                                    if (tmpDis < minDis) {
                                        besidePoint = p;
                                        minDis = tmpDis;
                                    }
                                }
                            }
                            if (besidePoint != null) {
                                orgBesidePoint = new Point2(besidePoint.x, besidePoint.y);
                            }
                        }
                    } else {
                        isStickerOnEdit = false;
                        Log.d("onTouchEvent", "Model"+mode);
                    }
                }else if(functionMode==FunctionMode.WALLPAPER){

                    startClickTime = Calendar.getInstance().getTimeInMillis();
                    downPoint = new Point2(pointX, pointY);
                    besidePointLine = null;//该点与点击点最近的点
                    if (pointLine.size() != 0) {
                        double minDis = 0;
                        double tmpDis = 0;
                        for(int i=0;i<pointLine.size();i++){
                            if (besidePointLine == null) {
                                besidePointLine = pointLine.get(0);
                                minDis = ViewUtil.distanceBetween(pointLine.get(0), downPoint);
                            } else {
                                tmpDis = ViewUtil.distanceBetween(pointLine.get(i), downPoint);
                                if (tmpDis < minDis) {
                                    besidePointLine = pointLine.get(i);
                                    minDis = tmpDis;
                                }
                            }
                        }
                        if (besidePointLine != null) {
                            orgBesidePointLine = new Point2(besidePointLine.x, besidePointLine.y);
                        }

                    }



                    if(isClose){
                        if (isInWallpaperArea( event)) {

                            if(isAdjust){
                                isdrage=false;
                                madjustPoint = null;//该点与点击点最近的点
                                if (pointArr.length != 0) {
                                    double minDis = 0;
                                    double tmpDis = 0;
                                    for (Point2 p : pointArr) { //第一个点
                                        if (madjustPoint == null) {
                                            madjustPoint = p;
                                            minDis = ViewUtil.distanceBetween(p, downPoint);
                                        } else {
                                            tmpDis = ViewUtil.distanceBetween(p, downPoint);
                                            if (tmpDis < minDis) {
                                                madjustPoint = p;
                                                minDis = tmpDis;
                                            }
                                        }
                                    }
                                    if (madjustPoint != null) {
                                        orgBesidePointAdjust = new Point2(madjustPoint.x, madjustPoint.y);
                                    }
                                }
                            }
                        }else{
                            isStickerOnEdit = false;
                        }
                    }



       /*             if(isAdjust){
                        isdrage=false;
                        madjustPoint = null;//该点与点击点最近的点
                        if (pointArr.length != 0) {
                            double minDis = 0;
                            double tmpDis = 0;
                            for (Point2 p : pointArr) { //第一个点
                                if (madjustPoint == null) {
                                    madjustPoint = p;
                                    minDis = ViewUtil.distanceBetween(p, downPoint);
                                } else {
                                    tmpDis = ViewUtil.distanceBetween(p, downPoint);
                                    if (tmpDis < minDis) {
                                        madjustPoint = p;
                                        minDis = tmpDis;
                                    }
                                }
                            }
                            if (madjustPoint != null) {
                                orgBesidePointAdjust = new Point2(madjustPoint.x, madjustPoint.y);
                            }
                        }
                    }*/



                 /*   startClickTime = Calendar.getInstance().getTimeInMillis();
                    downPoint = new Point2(pointX, pointY);
                    besidePointLine = null;//该点与点击点最近的点
                       if (pointLine.size() != 0) {
                           double minDis = 0;
                           double tmpDis = 0;
                           for (int i = 0; i < pointLine.size(); i++) {
                               if (besidePointLine == null) {
                                   besidePointLine = pointLine.get(0);
                                   minDis = ViewUtil.distanceBetween(pointLine.get(0), downPoint);
                               } else {
                                   tmpDis = ViewUtil.distanceBetween(pointLine.get(i), downPoint);
                                   if (tmpDis < minDis) {
                                       besidePointLine = pointLine.get(i);
                                       minDis = tmpDis;
                                   }
                               }
                           }
                           if (besidePointLine != null) {
                               orgBesidePointLine = new Point2(besidePointLine.x, besidePointLine.y);
                           }
                       }

                       if (isInWallpaperArea( event)) {

                           if(isAdjust){
                               isdrage=false;
                               madjustPoint = null;//该点与点击点最近的点
                               if (pointArr.length != 0) {
                                   double minDis = 0;
                                   double tmpDis = 0;
                                   for (Point2 p : pointArr) { //第一个点
                                       if (madjustPoint == null) {
                                           madjustPoint = p;
                                           minDis = ViewUtil.distanceBetween(p, downPoint);
                                       } else {
                                           tmpDis = ViewUtil.distanceBetween(p, downPoint);
                                           if (tmpDis < minDis) {
                                               madjustPoint = p;
                                               minDis = tmpDis;
                                           }
                                       }
                                   }
                                   if (madjustPoint != null) {
                                       orgBesidePointAdjust = new Point2(madjustPoint.x, madjustPoint.y);
                                   }
                               }
                           }
                       }else{
                           isStickerOnEdit = false;
                       }*/
                }

                break;

            case MotionEvent.ACTION_POINTER_DOWN: // 多点触控
                if(functionMode==FunctionMode.WALLPAPER){
                    if(issale){
                        oldDistance = ViewUtil.getMultiTouchDistance(event);
                        midPoint = ViewUtil.getMidPoint(event);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(ViewUtil.isMoveAction(pointX - downPoint.x,pointY - downPoint.y,isMove)) { //如果移动了
                    Log.d("ACTION_MOVE", "Model" + mode);
                    if (functionMode == FunctionMode.WALLPAPER) {

                        if(isAdjust){  //按了调整之后

                            if(isAdjustshow){
                                isdrage = true;
                                for (int i = 0; i < pointArr.length; i++) {
                                    if (orgBesidePointAdjust.x == pointArr[i].x && orgBesidePointAdjust.y == pointArr[i].y) {
                                        position = i;
                                    }
                                }

                                if (position != -1) {
                                    madjustPoint.x = orgBesidePointAdjust.x + (pointX - downPoint.x);
                                    madjustPoint.y = orgBesidePointAdjust.y + (pointY - downPoint.y);
                                    pointArr[position] = new Point2(madjustPoint.x, madjustPoint.y);
                                }
                                invalidate();
                            }else if(issale){//缩放adjustbitmap
                                scale = ViewUtil.getMultiTouchDistance(event) / oldDistance;
                                invalidate();
                            }else if(isSelect){//改变path，在进行遮罩
                                System.out.println("调整后");
                                if(orgBesidePointLine!=null){
                                    for (int i = 0; i < pointLine.size(); i++) {
                                        if (orgBesidePointLine.x == pointLine.get(i).x && orgBesidePointLine.y == pointLine.get(i).y) {
                                            position = i;
                                        }
                                    }
                                    if (position != -1) {
                                        Log.d("onTouchEvent", "拖拽");
                                        besidePointLine.x = orgBesidePointLine.x + (pointX - downPoint.x);
                                        besidePointLine.y = orgBesidePointLine.y + (pointY - downPoint.y);
                                        pointLine.set(position, new Point2(besidePointLine.x, besidePointLine.y));
                                        postInvalidate();
                                    }
                                }
                            }


                        }else{//没按调整之前
                            if (issale) { //多点触控缩放
                                // 取得想要缩放的matrix参数
                                scale = ViewUtil.getMultiTouchDistance(event) / oldDistance;
                                invalidate();
                            } else if(isSelect){ //调整选区


                                System.out.println("调整前else");
                                if(orgBesidePointLine!=null){
                                    for (int i = 0; i < pointLine.size(); i++) {
                                        if (orgBesidePointLine.x == pointLine.get(i).x && orgBesidePointLine.y == pointLine.get(i).y) {
                                            position = i;
                                        }
                                    }
                                    if (position != -1) {
                                        Log.d("onTouchEvent", "拖拽");
                                        besidePointLine.x = orgBesidePointLine.x + (pointX - downPoint.x);
                                        besidePointLine.y = orgBesidePointLine.y + (pointY - downPoint.y);
                                        pointLine.set(position, new Point2(besidePointLine.x, besidePointLine.y));
                                        postInvalidate();
                                    }
                                }

                            }
                        }
                    } else if (functionMode == FunctionMode.CURTAIN) {
                        // 平移
                        if (mode == ActionMode.TRANS) {
                            Log.d("onTouchEvent", "平移");
                            pianyi = new Point2(pointX - downPoint.x, pointY - downPoint.y);
                            sticker.getMatrix().postTranslate(pianyi.x, pianyi.y);// 平移
                            path.offset(pianyi.x, pianyi.y);
                            for (int j = 0; j < 4; j++) {
                                pointArr[j] = new Point2(pointArr[j].x + pianyi.x, pointArr[j].y + pianyi.y);
                            }
                            downPoint = new Point2(pointX, pointY);
                            postInvalidate();
                        } else if (mode == ActionMode.DRAG) {
                            Log.d("onTouchEvent", "图片拖拽");
                            for (int i = 0; i < pointArr.length; i++) {
                                if (orgBesidePoint.x == pointArr[i].x && orgBesidePoint.y == pointArr[i].y) {
                                    position = i;
                                }
                            }
                            if (position != -1) {
                                besidePoint.x = orgBesidePoint.x + (pointX - downPoint.x);
                                besidePoint.y = orgBesidePoint.y + (pointY - downPoint.y);
                                pointArr[position] = new Point2(besidePoint.x, besidePoint.y);
                            }
                            buildPath();
                            invalidate();
                        } else if (mode == ActionMode.TRANS_SELRCT) { //选区拖拽
                            Log.d("onTouchEvent", "选区拖拽");
                            for (int i = 0; i < pointLine.size(); i++) {
                                if (orgBesidePointLine.x == pointLine.get(i).x && orgBesidePointLine.y == pointLine.get(i).y) {
                                    position = i;
                                }
                            }
                            if (position != -1) {
                                besidePointLine.x = orgBesidePointLine.x + (pointX - downPoint.x);
                                besidePointLine.y = orgBesidePointLine.y + (pointY - downPoint.y);
                                pointLine.set(position, new Point2(besidePointLine.x, besidePointLine.y));
                            }
                            invalidate();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:

                if(functionMode == FunctionMode.CURTAIN){
                    position=-1;
                    mode = ActionMode.NONE;
                    midPoint = null;
                    imageMidPoint = null;
                }else if(functionMode == FunctionMode.WALLPAPER) {
                    isdrage=false;
                    position=-1;
                    long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                    if(clickDuration < MAX_CLICK_DURATION) {
                        if(!isClose) {//拖拉
                            pointLine.add(downPoint);
                            invalidate();
                        }
                    }
            }
                break;
            default:
                break;

//            return false;
        }
        if (isStickerOnEdit && listener != null) {
            listener.onEdit(this);
        }
        return isStickerOnEdit;
    }


    /* 计算两点间的距离
        *
        * @param source
        * @param width
        * @param height
        * @return
        */
    public double distanceBetween(Point2 p1, Point2 p2) {
        double dis = Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
        return dis;
//		Math.sqrt() 求开方
//		Math.pow() 求某数的任意次方,抛出ArithmeticException处理溢出异常
    }
    /**
     * 判断手指是否在操作区域内
     *
     * @param sticker
     * @param event
     * @return
     */
    private boolean isInStickerArea(Sticker sticker, MotionEvent event) {
        RectF dst = sticker.getSrcImageBound();
        return dst.contains(event.getX(), event.getY());
    }


    /**
     * 判断手指是否在墙纸操作区域内
     *
     *
     * @param event
     * @return
     */
    private boolean isInWallpaperArea( MotionEvent event) {
        RectF      dst = new RectF();
        if(isAdjust){//如果是调整
            mAdjustpath.computeBounds(dst,true);
        }else {//如果还没有调整
            mPath.computeBounds(dst,true);
        }



        return dst.contains(event.getX(), event.getY());
    }
    /**
     * 添加贴纸
     *
     * @param resId
     */

    public void setImageResource(int resId) {
        sticker = new Sticker(BitmapFactory.decodeResource(context.getResources(), resId));
    }

    /**
     * 获取贴纸对象
     *
     * @return
     */
    public Sticker getSticker() {
        return sticker;
    }

    //根据之前选择的区域，填充图片
    public void setSelectImageBitmap(Bitmap bm){
        mBitmap=bm;
        postInvalidate();

    }
    //选择区域 新建
    public void setSelectMode(){
        functionMode=FunctionMode.WALLPAPER;

    }

    //选择区域 新建
    public int getMode(){
       return functionMode;

    }
    public void setSelect(){//设置选区
        isSelect=true;
        issale=false;
        isAdjustshow=false;
        postInvalidate();
    }

    public void setScale(){//设置缩放

        issale=true;
        isAdjustshow=false;
        isSelect=false;
        postInvalidate();
    }
    //触发调整事件
    public void setAdjust(){
        isAdjustshow=true;
        isSelect=false;
        issale=false;
        isAdjust=true;
        issale=false;
        bounds = new RectF();
        mPath.computeBounds(bounds,true);
        mendPath=new Path();
        mendPath.set(mPath);
        adjustImage= DrawTileSacle(mBitmap,bounds);
        pointArr = new Point2[4];
        pointArr[0] = new Point2(bounds.left,bounds.top);
        pointArr[1] = new Point2(bounds.right,bounds.top);
        pointArr[2] = new Point2(bounds.right,bounds.bottom);
        pointArr[3] =new Point2(bounds.left,bounds.bottom);
//        buildGallyLinePath();
        invalidate();
    }

    //设置图片填充的大小，和图片，四个点的坐标
    public void setImageBitmap(Bitmap bm) {
        functionMode=FunctionMode.CURTAIN;
    	 this.defaultWidth = BaseApplication.screenWidth/3;
		double zoomScale = bm.getWidth() / this.defaultWidth;

		float w = (float) (bm.getWidth() / zoomScale);
		float h = (float) (bm.getHeight() / zoomScale);

    	
        sticker = new Sticker(CommonUtil.transImage(bm, (int) w, (int) h));
        pointArr = new Point2[4];
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();

        float density1 = dm.density;
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;


        int offSetX = (BaseApplication.screenWidth - (int) w) / 2;
		int offSetY = (BaseApplication.screenHeight - (int) h) / 4;
		
		offSetX=offSetX<=0?30:offSetX;
		offSetY=offSetY<=0?30:offSetY;


        Point2 p0 = new Point2(offSetX, offSetY);
        Point2 p1 = new Point2(offSetX + w, offSetY);
        Point2 p2 = new Point2(offSetX + w, offSetY + h);
        Point2 p3 = new Point2(offSetX, offSetY + h);

        pointArr = new Point2[4];
        pointArr[0] = p0;
        pointArr[1] = p1;
        pointArr[2] = p2;
        pointArr[3] = p3;

    }
   
    /**
     * 设置是否贴纸正在处于编辑状态
     *
     * @param edit
     */
    public void setEdit(boolean edit) {
        isEdit = edit;
        postInvalidate();
    }


    //测量大小
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)  {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalWidth = w;
        mTotalHeight = h;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);//设置宽和高
    }



    public void setDragRes(int dragRes) {
        dragIcon.setSrcIcon(dragRes);
    }
}
