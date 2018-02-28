package com.zhidao.sgr.zhidao3d.image_handle;

/**
 * 作者：ZhouYou
 * 日期：2016/12/2.
 * 贴纸操作模式
 */
public interface ActionMode {

    int NONE = 0; // 无模式
    int TRANS = 1; // 拖拽图片模式
    int TRANS_SELRCT = 2; //拖拽选区模式模式
    int ZOOM_SINGLE = 3; // 单点缩放模式
    int ZOOM_MULTI = 4; // 多点缩放模式
    int DRAG = 5; // 移动缩放模式
}
