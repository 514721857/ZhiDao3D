package com.zhidao.sgr.zhidao3d.Adapter;

import android.graphics.Bitmap;
import android.view.View;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zhidao.sgr.zhidao3d.R;
import com.zhidao.sgr.zhidao3d.View.CommonViewHolder;

import java.util.List;

/**
 * Created by 沈国荣 on 2017/12/2.
 * QQ:514721857
 * Description:
 */


public class ImageListAdapter extends BaseQuickAdapter<Bitmap,CommonViewHolder> implements BaseQuickAdapter.OnItemClickListener {
    public ImageListAdapter(List<Bitmap> bitmapList) {
        super(R.layout.item_img, bitmapList);
    }


    @Override
    protected void convert(CommonViewHolder baseViewHolder, Bitmap personItem) {
       /* baseViewHolder.setText(R.id.tv, personItem.getUserName());
        baseViewHolder.setText(R.id.tv1, personItem.getText());
       */
        baseViewHolder.setImageBitmap(R.id.item_img, personItem);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }
}