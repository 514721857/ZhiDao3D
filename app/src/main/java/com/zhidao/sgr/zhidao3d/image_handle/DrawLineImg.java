package com.zhidao.sgr.zhidao3d.image_handle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by 沈国荣 on 2017/11/22.
 * QQ:514721857
 * Description:自定义抠图，根据形状抠图
 */

public class DrawLineImg extends View {
    private int width;//设置高
    private int height;//设置高
    private Bitmap mBitmap;
    //画图模式
    private int mode;
    private Paint mPaint;//封闭前的画笔
    private Path mPath;//选择区时候的路径

    private Path mLinePath;//直线
    private Path mCurvePath;//曲线
    private Path mDrawPointPath ;//根据手势画线

    private static final int MAX_CLICK_DURATION = 200;
    private long startClickTime;
    private Point2 downPoint = null;//点下的点
//    private List<Point2> pointLine=new ArrayList() ;//画线的数组坐标
    private Point2 CurrentCirclePonit=null;//红心圆点的圆心
    private Point2 besidePointLine = null;
    private Point2 orgBesidePointLine = null;
    private int Lineposition=-1;//记录需要变动的直线点的位置
    private int Curveposition=-1;//记录需要变动的曲线点的位置
    private int CurveAdjustposition=-1;//记录需要变动的曲线点的位置
    private int Allposition=-1;//记录需要变动的直线点的位置
    private int isMove;//是否是可以移动的点
    List<ViewMode>  DrawViewMode;//绘制集合
    ViewMode CurrentViewMode;//当前绘制的对象
    Path CurrentPath;//当前绘制的对象
    List<Point4> tempListPoint;//当前绘制画线的 集合
    private List<Point2> tempLinePoint;//当前绘制直线的 集合

    List<Point2> tempCurvePoint;//当前绘制曲线的 集合
    List<Point2> tempCurveAdjustPoint;//当前绘制曲线调整点的 集合
    Point4 point4;
    public DrawLineImg(Context context) {
        super(context);
    }

    public DrawLineImg(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mode=PaintMode.LINE;
        mPath = new Path();
        DrawViewMode=new  ArrayList<ViewMode>();
        //初始化画笔
        mPaint = new Paint();
        mPaint.setColor(Color.GRAY);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(5f);
        tempListPoint=new ArrayList<Point4>();//画线的临时坐标点
        tempLinePoint=new ArrayList<Point2>();//画直线的临时坐标点
        tempCurvePoint =new ArrayList<Point2>();//画曲线的临时坐标点
        tempCurveAdjustPoint=new ArrayList<Point2>();//画曲线调整的临时坐标点
        CurrentViewMode=new ViewMode();
        CurrentViewMode.setMode(PaintMode.LINE);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);//关闭硬件加速
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mBitmap!=null){
            RectF rectF = new RectF(0, 0, width, height);   //w和h分别是屏幕的宽和高，也就是你想让图片显示的宽和高
            canvas.drawBitmap(mBitmap, null, rectF, null);
        }
            showLines(canvas);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        float pointX = event.getX();
        float pointY = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startClickTime = Calendar.getInstance().getTimeInMillis();
                downPoint = new Point2(pointX, pointY);
                if(mode==PaintMode.DRAWPOINT){
                    if(tempListPoint.size()==0){
                        Point2 tempPoint=null;
                if(DrawViewMode.size()!=0){
                    if(DrawViewMode.get(DrawViewMode.size()-1).getMode()==PaintMode.LINE){ //把画直线的最后一点作为曲线的起点
                        int lastLinePointPosition=   DrawViewMode.get(DrawViewMode.size()-1).getListPoint().size();
                        tempPoint=DrawViewMode.get(DrawViewMode.size()-1).getListPoint().get(lastLinePointPosition-1);
                    }else if(DrawViewMode.get(DrawViewMode.size()-1).getMode()==PaintMode.CURVE){//把画线的最后一点作为曲线的起点
                        tempPoint=DrawViewMode.get(DrawViewMode.size()-1).getListPoint().get(DrawViewMode.get(DrawViewMode.size()-1).getListPoint().size()-1);
                    }
                }else{
                    tempPoint=downPoint;//画当前起始点
                }
                tempListPoint.add(new Point4(tempPoint,downPoint));
                    }
                }
                besidePointLine = null;//该点与点击点最近的点
                if(mode!=PaintMode.DRAWPOINT){
                    calcuteMin(downPoint);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN: // 多点触控
                break;
            case MotionEvent.ACTION_MOVE:
                if(ViewUtil.isMoveAction(pointX - downPoint.x,pointY - downPoint.y,isMove)){ //如果移动了
                    if(mode==PaintMode.DRAWPOINT){
                        float endX = (downPoint.x+event.getX())/2;
                        float endY = (downPoint.y+event.getY())/2;
                        point4= new Point4(new Point2(pointX,pointY),new Point2(endX,endY));
                        tempListPoint.add(point4);
                        downPoint.x = event.getX();
                        downPoint.y =event.getY();
                        postInvalidate();
                    }else {
                        if(besidePointLine!=null){
                            setData(pointX,pointY);
                            invalidate();
                        }
                    }

                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                Lineposition=-1;
                Curveposition=-1;
                CurveAdjustposition=-1;
                long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                if(clickDuration < MAX_CLICK_DURATION) {

                    if(mode==PaintMode.LINE){
                        if(DrawViewMode.size()==0){
                            tempLinePoint.add(downPoint);
                        }else{
                            if(tempLinePoint.size()==0){

                                if(DrawViewMode.get(DrawViewMode.size()-1).getMode()==PaintMode.DRAWPOINT){
                                    tempLinePoint.add(DrawViewMode.get(DrawViewMode.size()-1).getListPoint4().get( DrawViewMode.get(DrawViewMode.size()-1).getListPoint4().size()-1).y);

                                }else if(DrawViewMode.get(DrawViewMode.size()-1).getMode()==PaintMode.CURVE){
                                    tempLinePoint.add(DrawViewMode.get(DrawViewMode.size()-1).getListPoint().get(DrawViewMode.get(DrawViewMode.size()-1).getListPoint().size()-1));
                                }
                                tempLinePoint.add(downPoint);
                            }else{
                                tempLinePoint.add(downPoint);
                            }

                        }




                    }else if(mode==PaintMode.CURVE) {

                        if(DrawViewMode.size()==0){
                            tempCurvePoint.add(downPoint);
                            int length = tempCurvePoint.size();
                            if (length >= 2) {
                                Point2 PointAdjust1 = new Point2(tempCurvePoint.get(length - 2).x,
                                        tempCurvePoint.get(length - 1).y);
                                Point2 PointAdjust2 = new Point2(tempCurvePoint.get(length - 1).x,
                                        tempCurvePoint.get(length - 2).y);
                           /* System.out.println("增加调整点1"+PointAdjust1.x+"y"+PointAdjust1.y);
                            System.out.println("增加点整点2"+PointAdjust2.x+"y"+PointAdjust2.y);*/
                                tempCurveAdjustPoint.add(PointAdjust1);
                                tempCurveAdjustPoint.add(PointAdjust2);
                            }
                        }else{
                            if(tempCurvePoint.size()==0){  //一点都还没有添加的时候
                                if(DrawViewMode.get(DrawViewMode.size()-1).getMode()==PaintMode.LINE){ //把画直线的最后一点作为曲线的起点
                                  int lastLinePointPosition=   DrawViewMode.get(DrawViewMode.size()-1).getListPoint().size();
                                    tempCurvePoint.add(DrawViewMode.get(DrawViewMode.size()-1).getListPoint().get(lastLinePointPosition-1));

                                }else if(DrawViewMode.get(DrawViewMode.size()-1).getMode()==PaintMode.DRAWPOINT){//把画线的最后一点作为曲线的起点
                                    tempCurvePoint.add(DrawViewMode.get(DrawViewMode.size()-1).getListPoint4().get( DrawViewMode.get(DrawViewMode.size()-1).getListPoint4().size()-1).y);
                                }
                                tempCurvePoint.add(downPoint);
                                int length = tempCurvePoint.size();
                                if (length >= 2) {
                                    Point2 PointAdjust1 = new Point2(tempCurvePoint.get(length - 2).x,
                                            tempCurvePoint.get(length - 1).y);
                                    Point2 PointAdjust2 = new Point2(tempCurvePoint.get(length - 1).x,
                                            tempCurvePoint.get(length - 2).y);
                           /* System.out.println("增加调整点1"+PointAdjust1.x+"y"+PointAdjust1.y);
                            System.out.println("增加点整点2"+PointAdjust2.x+"y"+PointAdjust2.y);*/
                                    tempCurveAdjustPoint.add(PointAdjust1);
                                    tempCurveAdjustPoint.add(PointAdjust2);
                                }
                            }else{
                                tempCurvePoint.add(downPoint);
                                int length = tempCurvePoint.size();
                                if (length >= 2) {
                                    Point2 PointAdjust1 = new Point2(tempCurvePoint.get(length - 2).x,
                                            tempCurvePoint.get(length - 1).y);
                                    Point2 PointAdjust2 = new Point2(tempCurvePoint.get(length - 1).x,
                                            tempCurvePoint.get(length - 2).y);
                           /* System.out.println("增加调整点1"+PointAdjust1.x+"y"+PointAdjust1.y);
                            System.out.println("增加点整点2"+PointAdjust2.x+"y"+PointAdjust2.y);*/
                                    tempCurveAdjustPoint.add(PointAdjust1);
                                    tempCurveAdjustPoint.add(PointAdjust2);
                                }
                            }

                        }


                     /*   System.out.println("增加点"+"x="+downPoint.x);
                        System.out.println("增加点"+"y="+downPoint.y);*/

                        //初始化调整的点



                    }
                    else if (mode == PaintMode.DRAWPOINT) {

                    }
                    invalidate();

                }
                break;
            default:
                break;
        }
        return true;
    }
    //测量大小
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }
    private void DrawAllPath(){
        if(mPath!=null){//这一步很重要，如果不添加会出现重复绘制的情况
            mPath = new Path();

        }


        int length= DrawViewMode.size();
        if(length==0){  //只画当前的的ViewMode 区别在于起点不同的画法
            if(CurrentViewMode.getMode()==PaintMode.LINE){ //画当前线
                if(tempLinePoint.size()>0){
                    if(tempLinePoint.size()==1){ //画一个红点
                        CurrentCirclePonit=tempLinePoint.get(0);
                    }else{
                        for(int j=0;j<tempLinePoint.size();j++){
                            if(j==0){
                                mPath.moveTo(tempLinePoint.get(j).x, tempLinePoint.get(j).y);

                            }else{
                                if(tempLinePoint.size()-1==j){//最后一点要画红点
                                    CurrentCirclePonit=tempLinePoint.get(tempLinePoint.size()-1);

                                }
                                mPath.lineTo(tempLinePoint.get(j).x, tempLinePoint.get(j).y);

                            }

                        }
                    }
                }

            }else if(CurrentViewMode.getMode()==PaintMode.CURVE){ //画当前曲线

                int Curvelength = tempCurvePoint.size();
                if(Curvelength>0){
                    if(Curvelength==1){
                        CurrentCirclePonit=tempCurvePoint.get(0);
                    }else{
                        System.out.println("画当前曲线"+Curvelength);
                        System.out.println("画当前曲调整点"+tempCurveAdjustPoint.size());


                            for (int i = 1; i < Curvelength; i++) {

                                mPath.moveTo(tempCurvePoint.get(i-1).x, tempCurvePoint.get(i-1).y);
                                System.out.println("moveTo"+"x="+tempCurvePoint.get(i-1).x);
                                System.out.println("moveTo"+"y="+tempCurvePoint.get(i-1).y);
                                mPath.cubicTo(tempCurveAdjustPoint.get(i*2-2).x, tempCurveAdjustPoint.get(i*2-2).y,
                                        tempCurveAdjustPoint.get(i*2-1).x, tempCurveAdjustPoint.get(i*2-1).y,
                                        tempCurvePoint.get(i ).x, tempCurvePoint.get(i).y);
                                System.out.println("cubicTo1"+"x="+ tempCurveAdjustPoint.get(i*2-2).x);
                                System.out.println("cubicTo1"+"y="+ tempCurveAdjustPoint.get(i*2-2).y);

                                System.out.println("cubicTo2"+"x="+ tempCurveAdjustPoint.get(i*2-1).x);
                                System.out.println("cubicTo2"+"y="+ tempCurveAdjustPoint.get(i*2-1).y);

                                System.out.println("moveTo"+"endx="+tempCurvePoint.get(i).x);
                                System.out.println("moveTo"+"endy="+tempCurvePoint.get(i).y);
                            }
                            CurrentCirclePonit=tempCurvePoint.get(tempCurvePoint.size()-1);


                    }
                }



            }else if(CurrentViewMode.getMode()==PaintMode.DRAWPOINT){  //画当前画线
                System.out.println("mPath"+mPath);
                System.out.println("tempListPoint.get(0).x"+tempListPoint.get(0).x);
                if(tempListPoint.get(0).x!=null){
                    mPath.moveTo(tempListPoint.get(0).x.x,tempListPoint.get(0).x.y);
                    for (Point4 pointTo : tempListPoint) {
                        mPath.quadTo(pointTo.x.x,pointTo.x.y,pointTo.y.x,pointTo.y.y);
                    }
                }

            }
        }else{ //起点要连之前画的起点

             for(int i=0;i<length;i++){   //一个一个的画

            System.out.println("当前有几段"+length);

                    if(DrawViewMode.get(i).getMode()==PaintMode.LINE) { //画直线
                        for(int j=0;j<DrawViewMode.get(i).getListPoint().size();j++){
                            if(j==0){
                                    mPath.moveTo(DrawViewMode.get(i).getListPoint().get(j).x, DrawViewMode.get(i).getListPoint().get(j).y);
                            }else{
                                mPath.lineTo(DrawViewMode.get(i).getListPoint().get(j).x, DrawViewMode.get(i).getListPoint().get(j).y);
                            }

                        }
                    }else if (DrawViewMode.get(i).getMode()==PaintMode.DRAWPOINT){ //画线

                        if(i==0){
                            mPath.lineTo(DrawViewMode.get(i).getListPoint4().get(0).x.x,DrawViewMode.get(i).getListPoint4().get(0).x.y);
                            mPath.moveTo(DrawViewMode.get(i).getListPoint4().get(0).x.x,DrawViewMode.get(i).getListPoint4().get(0).x.y);
                        }else{//连接之前的点

                            mPath.moveTo(DrawViewMode.get(i).getListPoint4().get(0).x.x,DrawViewMode.get(i).getListPoint4().get(0).x.y);
                        }

                        for (Point4 pointTo : DrawViewMode.get(i).getListPoint4()) {
                            mPath.quadTo(pointTo.x.x,pointTo.x.y,pointTo.y.x,pointTo.y.y);
                        }
                    }else if(DrawViewMode.get(i).getMode()==PaintMode.CURVE){ //画曲线
                     int  Curvelength=DrawViewMode.get(i).getListPoint().size();
                        for (int j = 1; j< Curvelength; j++) {
                            mPath.moveTo(DrawViewMode.get(i).getListPoint().get(j-1).x, DrawViewMode.get(i).getListPoint().get(j-1).y);
                            mPath.cubicTo(DrawViewMode.get(i).getAdjustPoint().get(j*2-2).x, DrawViewMode.get(i).getAdjustPoint().get(j*2-2).y,
                            DrawViewMode.get(i).getAdjustPoint().get(j*2-1).x, DrawViewMode.get(i).getAdjustPoint().get(j*2-1).y,
                            DrawViewMode.get(i).getListPoint().get(j ).x, DrawViewMode.get(i).getListPoint().get(j).y);

                        }
                    }
        }


         //接下来画当前对象的点

            if(CurrentViewMode.getMode()==PaintMode.LINE){ //画当前线
                System.out.println("画当前直线");
                if(tempLinePoint.size()>0){
                    System.out.println("画当前直线的坐标长度："+tempLinePoint.size());
                    if(tempLinePoint.size()==1){ //画一个红点
                        CurrentCirclePonit=tempLinePoint.get(0);
                    }else{
                        for(int j=0;j<tempLinePoint.size();j++){
                            if(j==0){

                                mPath.moveTo(tempLinePoint.get(j).x, tempLinePoint.get(j).y);

                            }else{
                                if(tempLinePoint.size()-1==j){//最后一点要画红点
                                    CurrentCirclePonit=tempLinePoint.get(tempLinePoint.size()-1);
                                    mPath.lineTo(tempLinePoint.get(tempLinePoint.size()-1).x,tempLinePoint.get(tempLinePoint.size()-1).y);
                                }else{
                                    mPath.lineTo(tempLinePoint.get(j).x, tempLinePoint.get(j).y);
                                }
                            }

                        }
                    }
                }
            }else if(CurrentViewMode.getMode()==PaintMode.CURVE){ //画当前曲线

                int Curvelength = tempCurvePoint.size();
                if(Curvelength>0){
                    if(Curvelength==1){
                        CurrentCirclePonit=tempCurvePoint.get(0);
                        mPath.moveTo(tempCurvePoint.get(0).x, tempCurvePoint.get(0).y);
                    } else{
                        System.out.println("画当前曲线size>0"+Curvelength);
                        System.out.println("画当前曲调整点"+tempCurveAdjustPoint.size());
                            for (int i = 1; i < Curvelength; i++) {
                                mPath.moveTo(tempCurvePoint.get(i-1).x, tempCurvePoint.get(i-1).y);
                                mPath.cubicTo(tempCurveAdjustPoint.get(i*2-2).x, tempCurveAdjustPoint.get(i*2-2).y,
                                        tempCurveAdjustPoint.get(i*2-1).x, tempCurveAdjustPoint.get(i*2-1).y,
                                        tempCurvePoint.get(i ).x, tempCurvePoint.get(i).y);
                            }
                        CurrentCirclePonit=tempCurvePoint.get(tempCurvePoint.size()-1);
                    }
                }


            }else if(CurrentViewMode.getMode()==PaintMode.DRAWPOINT){  //画当前画线

                for(int j=0;j<tempListPoint.size();j++){
                    if(j!=0){
                        mPath.quadTo(tempListPoint.get(j).x.x,tempListPoint.get(j).x.y,tempListPoint.get(j).y.x,tempListPoint.get(j).y.y);
                    }else{
                        mPath.moveTo(tempListPoint.get(0).x.x,tempListPoint.get(0).x.y);
                    }


                }

                CurrentCirclePonit=tempListPoint.get(tempListPoint.size()-1).x;
            }
        }
    }
    //画线
    private void showLines(Canvas canvas) {
        DrawAllPath();
        canvas.drawPath(mPath,mPaint);
        showApoint(canvas);
    }
    //画一个圆点
    private void showApoint(Canvas canvas){
        Paint paint = new Paint();
        paint.setStrokeWidth(6);
        paint.setColor(Color.RED);
        if(CurrentCirclePonit!=null){
            canvas.drawCircle(CurrentCirclePonit.x,CurrentCirclePonit.y, 6, paint);
        }

    }
    // 设置填充图片
    public void setImageBitmap(Bitmap bitmap){
        mBitmap=bitmap;
        invalidate();
    }
    // 画直线
    public void setLine(){

        if(mode==PaintMode.DRAWPOINT){ //如果之前是画线那么，要保存之前的坐标
            if(tempListPoint.size()>0){
                CurrentViewMode.setListPoint4(tempListPoint);
                DrawViewMode.add(CurrentViewMode);
                tempListPoint=null;

            }
        }else if(mode==PaintMode.CURVE){
            if(tempCurvePoint.size()>0){
                CurrentViewMode.setListPoint(tempCurvePoint);
                CurrentViewMode.setAdjustPoint(tempCurveAdjustPoint);
                DrawViewMode.add(CurrentViewMode);
                tempCurveAdjustPoint=null;
                tempCurvePoint=null;
            }
        }

        if(tempCurveAdjustPoint==null){
            tempCurveAdjustPoint=new ArrayList<Point2>();

        }
        if(tempCurvePoint==null){
            tempCurvePoint=new ArrayList<Point2>();
        }
        if(tempListPoint==null){
            tempListPoint=new ArrayList<Point4>();
        }


        if(mode==PaintMode.LINE){

        }else{
            CurrentViewMode=null;
            CurrentViewMode=new ViewMode();
        }

        mode=PaintMode.LINE;
        CurrentViewMode.setMode(PaintMode.LINE);

    }
    // 画曲线
    public void setCurve(){

        if(mode==PaintMode.DRAWPOINT){//如果之前是画线那么，要保存之前的坐标
            if(tempListPoint.size()>0){
                CurrentViewMode.setListPoint4(tempListPoint);
                DrawViewMode.add(CurrentViewMode);
                tempListPoint=null;
            }

        }else if(mode==PaintMode.LINE) {
            System.out.println("tempLinePoint有几个直线点" + tempLinePoint.size());
            if (tempLinePoint.size() > 0) {
                List<Point2> temp=tempLinePoint;
                CurrentViewMode.setListPoint(temp);
                    DrawViewMode.add(CurrentViewMode);
                tempLinePoint=null;
//                    tempLinePoint.clear();//如果清空数组，那么之前的赋值就会无效，我也不知道为什么会这样
                    int lastLinePointPosition=   DrawViewMode.get(DrawViewMode.size()-1).getListPoint().size();
                    System.out.println("t赋值之后有几个直线点" + lastLinePointPosition);


            }
        }

        if(tempListPoint==null){
            tempListPoint=new ArrayList<Point4>();
        }
        if(tempLinePoint==null){
            tempLinePoint=  new ArrayList<Point2>();
        }

        if(mode==PaintMode.CURVE){

        }else{
            CurrentViewMode=null;
            CurrentViewMode=new ViewMode();
        }
        mode=PaintMode.CURVE;

        CurrentViewMode.setMode(PaintMode.CURVE);

    }
    public Bitmap Dofinsh(){
        mPath.close();
      return   DrawImagePath(mBitmap,mPath);
    }

    //重新绘制
    public void retset(){
        mPath.reset();

        invalidate();
    }

    // 画线
    public void setDrawPoint(){

       if(mode==PaintMode.LINE){//如果之前是画直线那么，要保存之前的坐标
           if(tempLinePoint.size()>0){
               CurrentViewMode.setListPoint(tempLinePoint);
               DrawViewMode.add(CurrentViewMode);
               tempLinePoint=null;
           }


        }else if(mode==PaintMode.CURVE){
           if(tempCurvePoint.size()>0){
               CurrentViewMode.setListPoint(tempCurvePoint);
               CurrentViewMode.setAdjustPoint(tempCurveAdjustPoint);
               DrawViewMode.add(CurrentViewMode);
               tempCurveAdjustPoint=null;
               tempCurvePoint=null;
           }


       }

        if(tempCurveAdjustPoint==null){
            tempCurveAdjustPoint=new ArrayList<Point2>();

        }
        if(tempCurvePoint==null){
            tempCurvePoint=new ArrayList<Point2>();
        }
        if(tempLinePoint==null){
            tempLinePoint=new ArrayList<Point2>();
        }

        if(mode==PaintMode.DRAWPOINT){

        }else{
            CurrentViewMode=null;
            CurrentViewMode=new ViewMode();
        }
        mode=PaintMode.DRAWPOINT;

        CurrentViewMode.setMode(PaintMode.DRAWPOINT);
    }

    // 计算最近的点
    private void calcuteMin(Point2 downPoint) {
        double minDis = 0;
        double tmpDis = 0;
        if(DrawViewMode.size()==0){  //只需要判断当前画的点即可
            Allposition=-1;
          if( CurrentViewMode.getMode()==PaintMode.LINE) {
              if (tempLinePoint.size() != 0) {
                  for(int i=0;i<tempLinePoint.size();i++){
                      if (i == 0) {
                          besidePointLine = tempLinePoint.get(0);
                          Lineposition=0;
                          minDis = ViewUtil.distanceBetween(tempLinePoint.get(0), downPoint);
                      } else {
                          tmpDis = ViewUtil.distanceBetween(tempLinePoint.get(i), downPoint);
                          if (tmpDis < minDis) {
                              besidePointLine = tempLinePoint.get(i);
                              Lineposition=i;
                              CurveAdjustposition=-1;
                              Curveposition=-1;
                              minDis = tmpDis;
                          }
                      }
                  }

              }
          }else if(CurrentViewMode.getMode()==PaintMode.CURVE){

              // 起点和终点的坐标
              if (tempCurvePoint.size() != 0) {
                  for (int i = 0; i < tempCurvePoint.size(); i++) {
                      if (i == 0) {
                          besidePointLine = tempCurvePoint.get(0);
                          Curveposition=0;
                          minDis =ViewUtil. distanceBetween(tempCurvePoint.get(0), downPoint);
                      } else {
                          tmpDis = ViewUtil.distanceBetween(tempCurvePoint.get(i), downPoint);
                          if (tmpDis < minDis) {
                              besidePointLine = tempCurvePoint.get(i);
                              Curveposition=i;
                              Lineposition=-1;
                              CurveAdjustposition=-1;
                              minDis = tmpDis;
                          }
                      }
                  }
              }
              // 调整角度的坐标
              if (tempCurveAdjustPoint.size() != 0) {
                  for (int i = 0; i < tempCurveAdjustPoint.size(); i++) {

                      tmpDis = ViewUtil. distanceBetween(tempCurveAdjustPoint.get(i), downPoint);
                      if (tmpDis < minDis) {
                          besidePointLine = tempCurveAdjustPoint.get(i);
                          CurveAdjustposition=i;
                          Lineposition=-1;
                          Curveposition=-1;
                          minDis = tmpDis;

                      }
                  }

              }



          }
            if (besidePointLine != null) {
                orgBesidePointLine = new Point2(besidePointLine.x,
                        besidePointLine.y);
            }



        }else {//需要判断当前点和之前画的点
            int dvmLength = DrawViewMode.size();
            for (int i = 0; i < dvmLength; i++) {
                if (i == 0) {
                    Allposition = 0;
                    if (DrawViewMode.get(0).getMode() == PaintMode.CURVE) {
                        // 起点和终点的坐标
                        if (DrawViewMode.get(0).getListPoint().size() != 0) {
                            for (int j = 0; j < DrawViewMode.get(0).getListPoint().size(); j++) {
                                if (j == 0) {
                                    besidePointLine = DrawViewMode.get(0).getListPoint().get(0);
                                    Curveposition = 0;
                                    minDis = ViewUtil.distanceBetween(DrawViewMode.get(0).getListPoint().get(0), downPoint);
                                } else {
                                    tmpDis = ViewUtil.distanceBetween(DrawViewMode.get(0).getListPoint().get(j), downPoint);
                                    if (tmpDis < minDis) {
                                        besidePointLine = DrawViewMode.get(0).getListPoint().get(i);
                                        Curveposition = j;
                                        minDis = tmpDis;
                                    }
                                }
                            }
                        }
                        // 调整角度的坐标
                        if (DrawViewMode.get(0).getAdjustPoint().size() != 0) {
                            for (int j = 0; j < DrawViewMode.get(0).getAdjustPoint().size(); j++) {
                                tmpDis = ViewUtil.distanceBetween(DrawViewMode.get(0).getAdjustPoint().get(j), downPoint);
                                if (tmpDis < minDis) {
                                    besidePointLine = DrawViewMode.get(0).getAdjustPoint().get(i);
                                    CurveAdjustposition = i;
                                    Curveposition=-1;
                                    minDis = tmpDis;
                                }
                            }
                        }
                    } else if (DrawViewMode.get(0).getMode() == PaintMode.LINE) {

                        if (DrawViewMode.get(0).getListPoint().size() != 0) {
                            for (int j = 0; j < DrawViewMode.get(0).getListPoint().size(); j++) {
                                if (j == 0) {
                                    besidePointLine = DrawViewMode.get(0).getListPoint().get(0);
                                    Lineposition = 0;
                                    minDis = ViewUtil.distanceBetween(DrawViewMode.get(0).getListPoint().get(0), downPoint);
                                } else {
                                    System.out.println("minDis"+minDis);
                                    tmpDis = ViewUtil.distanceBetween(DrawViewMode.get(0).getListPoint().get(j), downPoint);
                                    System.out.println("tmpDis"+tmpDis);
                                    if (tmpDis < minDis) {
                                        besidePointLine = DrawViewMode.get(0).getListPoint().get(j);
                                        Lineposition = j;
                                        System.out.println("Lineposition"+Lineposition);
                                        minDis = tmpDis;
                                    }
                                }
                            }

                        }
                    }
                } else {

                    if (DrawViewMode.get(i).getMode() == PaintMode.CURVE) {
                        if (DrawViewMode.get(i).getListPoint().size() != 0) {
                            for (int j = 0; j < DrawViewMode.get(i).getListPoint().size(); j++) {
                                tmpDis = ViewUtil.distanceBetween(DrawViewMode.get(i).getListPoint().get(j), downPoint);
                                if (tmpDis < minDis) {
                                    Allposition = i;
                                    besidePointLine = DrawViewMode.get(i).getListPoint().get(j);
                                    Curveposition = j;
                                    Lineposition = -1;
                                    CurveAdjustposition = -1;
                                    minDis = tmpDis;
                                }

                            }

                            // 调整角度的坐标
                            if (DrawViewMode.get(i).getAdjustPoint().size() != 0) {
                                for (int j = 0; j < DrawViewMode.get(i).getAdjustPoint().size(); j++) {
                                    tmpDis = ViewUtil.distanceBetween(DrawViewMode.get(i).getAdjustPoint().get(j), downPoint);
                                    if (tmpDis < minDis) {
                                        besidePointLine = DrawViewMode.get(i).getAdjustPoint().get(j);
                                        CurveAdjustposition = j;
                                        Lineposition = -1;
                                        Curveposition = -1;
                                        Allposition = i;
                                        minDis = tmpDis;
                                    }
                                }
                            }

                        }


                    } else if (DrawViewMode.get(i).getMode() == PaintMode.LINE) {
                        if (DrawViewMode.get(i).getListPoint().size() > 0) {
                            for (int j = 0; j < DrawViewMode.get(i).getListPoint().size(); j++) {
                                tmpDis = ViewUtil.distanceBetween(DrawViewMode.get(i).getListPoint().get(j), downPoint);
                                if (tmpDis < minDis) {
                                    Allposition = i;
                                    besidePointLine = DrawViewMode.get(i).getListPoint().get(j);
                                    Lineposition = j;
                                    CurveAdjustposition = -1;
                                    Curveposition = -1;
                                    minDis = tmpDis;
                                }
                            }

                        }
                    }
                }
            }

//           在与当前画的 相比较
            //只需要判断当前画的点即可
            if (CurrentViewMode.getMode() == PaintMode.LINE) {
                if (tempLinePoint.size() != 0) {
                    for (int i = 0; i < tempLinePoint.size(); i++) {
                        tmpDis = ViewUtil.distanceBetween(tempLinePoint.get(i), downPoint);
                        if (tmpDis < minDis) {
                            besidePointLine = tempLinePoint.get(i);
                            Lineposition = i;
                            minDis = tmpDis;
                            Allposition = -1;
                            Curveposition = -1;
                            CurveAdjustposition = -1;
                        }
                    }

                }
            } else if (CurrentViewMode.getMode() == PaintMode.CURVE) {
                // 起点和终点的坐标
                if (tempCurvePoint.size() != 0) {
                    for (int i = 0; i < tempCurvePoint.size(); i++) {
                        tmpDis = ViewUtil.distanceBetween(tempCurvePoint.get(i), downPoint);
                        if (tmpDis < minDis) {
                            besidePointLine = tempCurvePoint.get(i);
                            CurveAdjustposition = -1;
                            Curveposition = i;
                            minDis = tmpDis;
                            Allposition = -1;
                            Lineposition = -1;
                        }
                    }
                }
                // 调整角度的坐标
                if (tempCurveAdjustPoint.size() != 0) {
                    for (int i = 0; i < tempCurveAdjustPoint.size(); i++) {
                        tmpDis = ViewUtil.distanceBetween(tempCurveAdjustPoint.get(i), downPoint);
                        if (tmpDis < minDis) {
                            besidePointLine = tempCurveAdjustPoint.get(i);
                            CurveAdjustposition = i;
                            Curveposition = -1;
                            minDis = tmpDis;
                            Allposition = -1;
                            Lineposition = -1;
                        }
                    }
                }
            }
            if (besidePointLine != null) {
                orgBesidePointLine = new Point2(besidePointLine.x,
                        besidePointLine.y);
            }
        }
    }
    //计算完需要改变的点之后，需要重新赋值
    public void setData(float pointX,float pointY){
        besidePointLine.x = orgBesidePointLine.x + (pointX - downPoint.x);
        besidePointLine.y = orgBesidePointLine.y + (pointY - downPoint.y);
            if(Allposition==-1){  //只需要改变当前点的坐标
                if(Lineposition!=-1){
                    tempLinePoint.set(Lineposition,new Point2(besidePointLine.x,besidePointLine.y));
                }
                if(Curveposition!=-1){
                    tempCurvePoint.set(Curveposition,new Point2(besidePointLine.x,besidePointLine.y));
                }
                if(CurveAdjustposition!=-1){
                    tempCurveAdjustPoint.set(CurveAdjustposition,new Point2(besidePointLine.x,besidePointLine.y));
                }

            }else{//表示需要修改之前画的点的坐标
                if(Lineposition!=-1){
                    ViewMode tempMode=    DrawViewMode.get(Allposition);
                     List<Point2>  templistpoint=  tempMode.getListPoint();
                    templistpoint.set(Lineposition,new Point2(besidePointLine.x,besidePointLine.y));
                    tempMode.setListPoint(templistpoint);
                    DrawViewMode.set(Allposition,tempMode);
                }
                if(Curveposition!=-1){
                    ViewMode tempMode=    DrawViewMode.get(Allposition);
                    List<Point2>  templistpoint=  tempMode.getListPoint();
                    templistpoint.set(Curveposition,new Point2(besidePointLine.x,besidePointLine.y));
                    tempMode.setListPoint(templistpoint);
                    DrawViewMode.set(Allposition,tempMode);
                }
                if(CurveAdjustposition!=-1){
                    ViewMode tempMode=    DrawViewMode.get(Allposition);
                    List<Point2>  templistpoint=  tempMode.getAdjustPoint();
                    templistpoint.set(CurveAdjustposition,new Point2(besidePointLine.x,besidePointLine.y));
                    tempMode.setListPoint(templistpoint);
                    DrawViewMode.set(Allposition,tempMode);
                }
            }


    }

    // 将path和图片（填充）bitmap取交集
    private Bitmap DrawImagePath(Bitmap source, Path path) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(target);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//        canvas.drawPath(path,paint);
        canvas.drawBitmap(makeDst(path), 0, 0, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Shader mBitmapShader = new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(mBitmapShader);
        RectF rectF = new RectF(0, 0, width, height);   //w和h分别是屏幕的宽和高，也就是你想让图片显示的宽和高
        canvas.drawBitmap(source,null,rectF, paint);
        return target;
    }
    private Bitmap makeDst(Path path) {
        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bm.setHasAlpha(true);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
//        p.setColor(Color.TRANSPARENT);
        c.drawPath(path,p);
        return bm;
    }

}
