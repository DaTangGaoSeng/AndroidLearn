package com.example.androidlearn;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
//下拉刷新监听类
abstract public class OnLastitemListener extends RecyclerView.OnScrollListener {
    private boolean upslide = true;//是否是在向上滑动

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if(upslide && !recyclerView.canScrollVertically(1)){
            loadmore();
        }
    }
    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        upslide = dy>0;
    }
    abstract void loadmore();//加载更多
}
