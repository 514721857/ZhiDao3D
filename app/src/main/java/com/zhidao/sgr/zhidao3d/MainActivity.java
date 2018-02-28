package com.zhidao.sgr.zhidao3d;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zhidao.sgr.zhidao3d.Adapter.ImageListAdapter;
import com.zhidao.sgr.zhidao3d.image_handle.FunctionMode;
import com.zhidao.sgr.zhidao3d.image_handle.MessaageEvenBus;
import com.zhidao.sgr.zhidao3d.image_handle.StickerLayout;
import com.zhidao.sgr.zhidao3d.image_handle.StickerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity implements View.OnClickListener ,BaseQuickAdapter.OnItemChildClickListener,BaseQuickAdapter.OnItemClickListener{
    @BindView(R.id.img_list)
    RecyclerView img_list;


    @BindView(R.id.text_select)
    TextView text_select;

    @BindView(R.id.text_cut)
    TextView text_cut;

    @BindView(R.id.text_wall)
    TextView text_wall;

    @BindView(R.id.text_sf)
    TextView text_sf;

    @BindView(R.id.text_adjust)
    TextView text_adjust;

    @BindView(R.id.text_new)
    TextView text_new;

    @BindView(R.id.text_sc)
    TextView text_sc;

    @BindView(R.id.text_select_area)
    TextView text_select_area;


    @BindView(R.id.sticker_layout)
    StickerLayout mstickerLayout;

    ImageListAdapter imageAdapter;
    private Unbinder unbinder;
    //功能模式
    public static int functionMode;


    /** 存放当前文件夹下所有文件的路径的集合 **/
    private static ArrayList<String> paths = new ArrayList<String>();
    List<Bitmap> listData= new ArrayList<Bitmap>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    private void initView() {
        unbinder = ButterKnife.bind(this);
        initAPPInfo();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        img_list.setLayoutManager(linearLayoutManager);
        EventBus.getDefault().register(this);
    }

    @OnClick({R.id.text_sc,R.id.text_new,R.id.text_adjust,R.id.text_sf,R.id.text_wall,R.id.text_cut,R.id.text_select_area,R.id.text_select})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_select://添加背景图片
              BaseApplication.SelectMode=1;
            Intent intent =new Intent(MainActivity.this, SelectPictureActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.text_sc://添加素材

                BaseApplication.SelectMode=2;
                Intent intent1 =new Intent(MainActivity.this, SelectPictureActivity.class);
                startActivity(intent1);
                break;
            case R.id.text_new://新建
                System.out.println("新建");
            mstickerLayout.addSelectSticker();
                break;
            case R.id.text_adjust://调整
                mstickerLayout.SetAdjust();
                break;
            case R.id.text_sf://缩放
                mstickerLayout.setScale();
                break;
            case R.id.text_select_area://选区
                mstickerLayout.setSelect();

                break;

            case R.id.text_wall://墙纸
                functionMode=FunctionMode.WALLPAPER;
                listData.clear();
                if(getBitmapData("哈哈哈").size()>0){
                    imageAdapter = new ImageListAdapter(listData);
                    imageAdapter.setOnItemClickListener(this);
                }
                img_list.setAdapter(imageAdapter);
                break;
            case R.id.text_cut://窗帘
                functionMode=FunctionMode.CURTAIN;
                listData.clear();
//                img_list.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false));
                if(getBitmapData("帘帘拍").size()>0){
                    imageAdapter = new ImageListAdapter(listData);
                    imageAdapter.setOnItemClickListener(this);
                }
                img_list.setAdapter(imageAdapter);
                break;
        }
    }

    /**
     * 初始化APP数据
     */
    public void initAPPInfo() {
        WindowManager wm = this.getWindowManager();
        // 设置屏幕高宽
        if (BaseApplication.screenWidth == 0)
            BaseApplication.screenWidth =  wm.getDefaultDisplay().getWidth();
        if (BaseApplication.screenHeight == 0)
            BaseApplication.screenHeight = wm.getDefaultDisplay().getHeight();
    }


    /**
     * 判断当前存储卡是否可用
     **/
    public boolean checkSDCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取当前需要查询的文件夹
     **/
    public String takePicRootDir(Context context,String fileName) {//相册名字
//        File appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "帘帘拍");

        if (checkSDCardAvailable()) {
            return Environment.getExternalStorageDirectory() + File.separator + fileName;
        } else {
            return context.getFilesDir().getAbsolutePath() + File.separator + fileName;
        }
    }

    /**
     * 描述：  获取指定文件夹下面的所有文件目录
     * 作者：  郭永振
     * 时间：  2017-01-17 17:02:40
     */
    private Map<String, Bitmap> buildThum(String FileName) throws FileNotFoundException {
        File baseFile = new File(takePicRootDir(this,FileName));
        // 使用TreeMap，排序问题就不需要纠结了
        Map<String, Bitmap> maps = new TreeMap<String, Bitmap>();
        if (baseFile != null && baseFile.exists()) {
            paths = imagePath(baseFile);

            if (!paths.isEmpty()) {
                for (int i = 0; i < paths.size(); i++) {
                    Bitmap bitmap = BitmapFactory.decodeFile(paths.get(i));

                    maps.put(paths.get(i), bitmap);
                }
            }
        }

        return maps;
    }

    /**
     * 描述：  获取图片列表
     * 作者：  郭永振
     * 时间：  ${DATE} ${TIME}
     */
    private static ArrayList<String> imagePath(File file) {
        ArrayList<String> list = new ArrayList<String>();

        File[] files = file.listFiles();
        for (File f : files) {
            list.add(f.getAbsolutePath());
        }
        Collections.sort(list);
        return list;
    }


    private List<Bitmap> getBitmapData(String FileName){

        Map<String,Bitmap> maps = new TreeMap<String, Bitmap>();
        try {
            maps = buildThum(FileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        /** 存放当前文件夹下的图片转换成的bitmap **/

        /** 存放所有的ImageView的集合 **/
//        final List<ImageView> images = new ArrayList<>();

        for (Map.Entry<String,Bitmap> entry : maps.entrySet()) {
            listData.add(entry.getValue());
 /*           ImageView iv = new ImageView(mContext);
            iv.setImageBitmap(entry.getValue());
            Glide.with(mContext)
                    .load(entry.getKey())
                    .into(iv);
            images.add(iv);*/
        }
        return listData;
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

    }
    /**
     * 设置跳转  接受返回数据
     * @param requestCode
     *              请求码
     * @param resultCode
     *              返回码
     * @param data
     *              返回数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //  如果请求码与返回码等于预期设置的值  则进行后续操作
        if (requestCode == 1){//返回背景bitmap
            System.out.println("返回背景bitmap"+BaseApplication.getInstance());
            // 获取返回的数据.
            mstickerLayout.setBackgroundImageBitmp(BaseApplication.getInstance().getIntentBitmap());

        }
    }
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if(functionMode==FunctionMode.WALLPAPER){//画廊
            Bitmap decodeResource = BitmapFactory.decodeResource(this.getResources(), R.mipmap.bg);
            mstickerLayout.SetStickerImg(listData.get(position));
        }else if(functionMode==FunctionMode.CURTAIN){//窗帘
            mstickerLayout.setRemoveRes(R.mipmap.ic_remove);
            mstickerLayout.setDragRes(R.mipmap.ic_mirror);
            mstickerLayout.addSticker(listData.get(position));
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 4.事件订阅者处理事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoonEvent(MessaageEvenBus messageEvent) {
        System.out.println("事件订阅");
        listData.clear();
//                img_list.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false));
        if(getBitmapData("帘帘拍").size()>0){
            imageAdapter = new ImageListAdapter(listData);
            imageAdapter.setOnItemClickListener(this);
        }
        img_list.setAdapter(imageAdapter);
    }

}
