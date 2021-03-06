package com.sdwfqin.widget.pictureupload;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.ConvertUtils;
import com.sdwfqin.widget.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：九宫格图片上传view
 *
 * @author zhangqin
 * @date 2018/5/31
 */
public class PictureUploadView<T extends PictureUpModel> extends RelativeLayout {

    private Context mContext;

    /**
     * 最大图片数
     */
    private int mMaxSize = 9;
    /**
     * 每行最大图片数
     */
    private int mMaxCol = 3;
    /**
     * 图片数据
     */
    private List<T> mDataList = new ArrayList<>();
    /**
     * 布局
     */
    private int itemLayout = R.layout.quick_item_upload_img;

    private RecyclerView mRecyclerView;
    private PictureUploadAdapter<T> mUploadAdapter;

    private PictureUploadCallback<T> mCallback;
    private GridLayoutManager mGridLayoutManager;

    public PictureUploadView(Context context) {
        this(context, null);
    }

    public PictureUploadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.quick_picture_upload_view, this);
        mRecyclerView = findViewById(R.id.rv);

        mDataList.add(null);
        initList();
    }

    private void initList() {
        mGridLayoutManager = new GridLayoutManager(mContext, mMaxCol);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        // 设置padding
        RecyclerView.ItemDecoration decoration = new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = ConvertUtils.dp2px(3);
                outRect.top = ConvertUtils.dp2px(3);
                outRect.left = ConvertUtils.dp2px(3);
                outRect.right = ConvertUtils.dp2px(3);
            }
        };
        mRecyclerView.addItemDecoration(decoration);
        mUploadAdapter = new PictureUploadAdapter<>(itemLayout, mDataList);
        mRecyclerView.setAdapter(mUploadAdapter);
        mUploadAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            int i = view.getId();
            if (i == R.id.ii_img) {
                int size = mDataList.size();
                if (mDataList.get(size - 1) == null && position == size - 1) {
                    if (mCallback == null) {
                        return;
                    }
                    mCallback.onAddPic(mMaxSize - (mDataList.size() - 1), mUploadAdapter.getData());
                } else {
                    if (mCallback == null) {
                        return;
                    }
                    T item = mUploadAdapter.getItem(position);
                    mCallback.click(position, item, mUploadAdapter.getData());
                }

            } else if (i == R.id.ii_del) {
                if (mDataList.size() == mMaxSize && mDataList.get(mDataList.size() - 1) != null) {
                    mUploadAdapter.remove(position);
                    mDataList.add(null);
                    mUploadAdapter.setNewData(mDataList);
                } else {
                    mUploadAdapter.remove(position);
                    mUploadAdapter.notifyDataSetChanged();
                }
                if (mCallback == null) {
                    return;
                }
                mCallback.remove(position, mUploadAdapter.getData());
            }
        });
    }

    /**
     * 设置每行对多展示多少张图片
     */
    public void setMaxColumn(int maxCol) {
        mMaxCol = maxCol;
        if (mGridLayoutManager != null){
            mGridLayoutManager.setSpanCount(maxCol);
        }
    }

    /**
     * 设置最大图片数
     */
    public void setMaxSize(int maxSize) {
        mMaxSize = maxSize;
    }

    /**
     * 设置布局
     */
    public void setItemLayout(@LayoutRes int itemLayout) {
        this.itemLayout = itemLayout;
    }

    /**
     * 获取所有图片的地址
     */
    public List<T> getData() {
        List<T> temp = mDataList;
        if (temp.get(temp.size() - 1) == null) {
            temp.remove(temp.size() - 1);
        }
        return temp;
    }

    /**
     * 设置图片
     */
    public void setNewData(List<T> data) {
        mDataList.clear();
        int size = mDataList.size();
        if (size < mMaxSize && mDataList.get(size - 1) != null) {
            // 如果数量小于最大值，添加一个null作为占位符
            mDataList.add(null);
        }
        mDataList = data;
        mUploadAdapter.setNewData(mDataList);
    }

    /**
     * 添加图片
     */
    public void setAddData(List<T> data) {
        int size = mDataList.size();
        if (size <= mMaxSize && mDataList.get(size - 1) == null) {
            mDataList.remove(mDataList.size() - 1);
        }
        mDataList.addAll(data);
        if (mDataList.size() < mMaxSize) {
            // 如果数量小于最大值，添加一个null作为占位符
            mDataList.add(null);
        }
        mUploadAdapter.setNewData(mDataList);
    }

    /**
     * 移除全部图片
     */
    public void removeAll() {
        mUploadAdapter.setNewData(null);
        mDataList.clear();
    }

    /**
     * 设置监听器
     */
    public void setPicUploadCallback(PictureUploadCallback<T> pictureUploadCallback) {
        mCallback = pictureUploadCallback;
    }
}