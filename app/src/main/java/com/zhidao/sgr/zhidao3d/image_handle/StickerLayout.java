package com.zhidao.sgr.zhidao3d.image_handle;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * 这个类可以得到当前的SstickView ，然后就可以对StickView进行调用当前方法
 * 日期：2016/12/2.
 */
public class StickerLayout extends FrameLayout {

    private Context context;
    // 贴纸的集合
    private List<StickerView> stickerViews;
    //当前StickerView
    private StickerView mStickerView;
    // 贴纸的View参数
    private LayoutParams stickerParams;
    // 背景图片控件
    private ImageView ivImage;

    // 缩放操作图片
    private int removeRes;
    // 缩放操作图片
    private int dragRes;

    public StickerLayout(Context context) {
        this(context, null);
    }

    public StickerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        stickerViews = new ArrayList<StickerView>();
        stickerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addBackgroundImage();
    }

    /**
     * 初始化背景图片控件
     */
    private void addBackgroundImage() {
        LayoutParams bgParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        ivImage = new ImageView(context);
        ivImage.setScaleType(ImageView.ScaleType.FIT_XY);
        ivImage.setLayoutParams(bgParams);
        addView(ivImage);
    }

    /**
     * 设置背景图片
     */
    public void setBackgroundImage(int resource) {
        ivImage.setImageResource(resource);
    }
    /**
     * 设置背景图片
     */
    public void setBackgroundImageBitmp(Bitmap resource) {
        ivImage.setImageBitmap(resource);
    }

    /**
     * 新增贴纸
     */
    public void addSticker(int resource) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resource);
        addSticker(bitmap);
    }

    public void removeAllSticker(){
//    	removeAllViewsInLayout();
    	removeViews(1, stickerViews.size());
//    	removeView();
    	 stickerViews.clear();
         redraw();
    }

    /**
     * 新增贴纸的选区，并没有填充图片
     */
    public StickerView addSelectSticker() {
        final StickerView sv = new StickerView(context,FunctionMode.WALLPAPER);

        sv.setLayoutParams(stickerParams);
        sv.setOnStickerActionListener(new OnStickerActionListener() {
            @Override
            public void onDelete() {
                // 处理删除操作
                removeView(sv);
                stickerViews.remove(sv);
                redraw();
            }

            @Override
            public void onEdit(StickerView stickerView) {

                int position = stickerViews.indexOf(stickerView);
                mStickerView=stickerView;
                stickerView.setEdit(true);
                stickerView.bringToFront();
                int size = stickerViews.size();
                for (int i = 0; i < size; i++) {
                    StickerView item = stickerViews.get(i);
                    if (item == null) continue;
                    if (position != i) {
                        item.setEdit(false);
                    }
                }
            }
        });
        addView(sv);
        stickerViews.add(sv);
        redraw();
        return sv;
    }
//    设置填充图片
    public void SetStickerImg(Bitmap bitmap) {
        if(mStickerView!=null){
            mStickerView.setSelectImageBitmap(bitmap);
        }

    }

    //    设置填充图片
    public void SetAdjust() {
        if(mStickerView!=null){
            mStickerView.setAdjust();
        }

    }
    //    设置缩放
    public void setScale() {

        if(mStickerView!=null){
            mStickerView.setScale();
        }

    }
    //    设置选区
    public void setSelect() {
        if(mStickerView!=null){
            mStickerView.setSelect();
        }

    }

    /**
     * 新增贴纸
     */
    public void addSticker(Bitmap bitmap) {
        final StickerView sv = new StickerView(context,FunctionMode.CURTAIN);
        sv.setImageBitmap(bitmap);
        sv.setLayoutParams(stickerParams);
        sv.setOnStickerActionListener(new OnStickerActionListener() {
            @Override
            public void onDelete() {
                // 处理删除操作
                removeView(sv);
                stickerViews.remove(sv);
                redraw();
            }

            @Override
            public void onEdit(StickerView stickerView) {
                mStickerView=stickerView;
                int position = stickerViews.indexOf(stickerView);
                System.out.println("当前是第几个stickerView"+position);
                stickerView.setEdit(true);
                stickerView.bringToFront();
                int size = stickerViews.size();
                for (int i = 0; i < size; i++) {
                    StickerView item = stickerViews.get(i);
                    if (item == null) continue;
                    if (position != i) {
                        item.setEdit(false);
                    }
                }
            }
        });
        addView(sv);
        stickerViews.add(sv);
        redraw();
    }



    /**
     * 查看贴纸的预览操作
     */
    public void getPreview() {
        for (StickerView item : stickerViews) {
            if (item == null) continue;
            item.setEdit(false);
        }
    }

    /**
     * 重置贴纸的操作列表
     */
    private void redraw() {
        redraw(true);
    }

    /**
     * 重置贴纸的操作列表
     */
    private void redraw(boolean isNotGenerate) {
        int size = stickerViews.size();

        if (size <= 0) return;
        for (int i = size - 1; i >= 0; i--) {
            StickerView item = stickerViews.get(i);

            if (item == null) continue;
            if(item.getMode()==FunctionMode.CURTAIN){
                item.setDragRes(dragRes);
            }
            if (i == size - 1) {
                item.setEdit(isNotGenerate);
            } else {
                item.setEdit(false);
            }
            stickerViews.set(i, item);
        }
    }

    /**
     * 生成合成图片
     *
     * @return
     */
    public Bitmap generateCombinedBitmap() {
        redraw(false);
        Bitmap dst = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(dst);
        draw(canvas);
        return dst;
    }
    public void setRemoveRes(int removeRes) {
        this.removeRes = removeRes;
    }
    public void setDragRes(int dragRes) {
        this.dragRes = dragRes;
    }

}
