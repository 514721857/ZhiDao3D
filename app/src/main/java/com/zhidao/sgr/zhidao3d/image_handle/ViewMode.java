package com.zhidao.sgr.zhidao3d.image_handle;

import android.graphics.Path;

import java.util.List;

/**
 * Created by 沈国荣 on 2017/11/22.
 * QQ:514721857
 * Description:该对象包括 path 路径，mode模式，直线曲线画线，ListPoint该段的坐标；
 */

public class ViewMode {
    Path path;
    int Mode;
    List<Point2> ListPoint;
    List<Point2> AdjustPoint;
    List<Point4> ListPoint4;

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public int getMode() {
        return Mode;
    }


    public void setMode(int mode) {
        Mode = mode;
    }

    public List<Point2> getAdjustPoint() {
        return AdjustPoint;
    }

    public void setAdjustPoint(List<Point2> adjustPoint) {
        AdjustPoint = adjustPoint;
    }

    public List<Point2> getListPoint() {
        return ListPoint;
    }

    public void setListPoint(List<Point2> listPoint) {
        ListPoint = listPoint;
    }

    public List<Point4> getListPoint4() {
        return ListPoint4;
    }

    public void setListPoint4(List<Point4> listPoint4) {
        ListPoint4 = listPoint4;
    }
}
