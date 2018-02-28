package com.zhidao.sgr.zhidao3d;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.WindowManager;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.zhidao.sgr.zhidao3d.image_handle.Lib;

import static java.security.AccessController.getContext;


public class BaseApplication extends Application {
	public static Context appContext;
	public static int screenHeight;
	public static int screenWidth;
	/**
	 * Intent传递的Uri
	 */
	public String intentUri = null;
	/**
	 * Intent传递用的bitmap
	 */
	private Bitmap intentBitmap = null;
	public static int SelectMode;//当为1时是选择图片，当为2时是跳转到抠图模式
	private Bitmap handleBitmap = null;
	private static BaseApplication instance;
	public static String defaultFileSavePath = Environment.getExternalStorageDirectory() + "/zd3d";
	private RefWatcher mRefWatcher;
	/**
	 * 用户是否已登录
	 * 
	 * @return
	 */

	/***************************************************/

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// 初始化全局变量
		instance = this;
		mRefWatcher = LeakCanary.install(this);
		this.appContext = getApplicationContext();//应用程序的上下文，和单例的生命周期一样长，这样就避免了内存泄漏
		  Lib.init(this);
	}
	public static BaseApplication getInstance() {
		return instance;
	}
	public void setIntentBitmap(Bitmap bmp) {
		try {
			if (this.intentBitmap != null && !this.intentBitmap.isRecycled()) {
				this.intentBitmap.recycle();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		this.intentBitmap = bmp;
	}
	public Bitmap getIntentBitmap() {


		return this.intentBitmap;
	}
	public void setHandleBitmap(Bitmap bmp) {
		try {
			if (this.handleBitmap != null && !this.handleBitmap.isRecycled()) {
				this.handleBitmap.recycle();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		this.handleBitmap = bmp;
	}
	public Bitmap getHandleBitmap() {


		return this.handleBitmap;
	}
}
