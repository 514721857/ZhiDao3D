package com.zhidao.sgr.zhidao3d.image_handle;

import android.app.Application;
import android.os.Environment;

import com.zhidao.sgr.zhidao3d.BaseApplication;

import java.io.File;



/**
 * 作者：ZhouYou
 * 日期：2016/12/2.
 */
public class Lib {

    private static BaseApplication app;

    public static void init(BaseApplication app) {
        Lib.app = app;
    }

    public static Application getInstance() {
        if (Lib.app == null) {
            throw new IllegalArgumentException("LBase application is null");
        }
        return Lib.app;
    }
}
