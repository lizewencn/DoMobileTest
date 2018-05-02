package lizewen.sdj.com.domobiletest;

import android.support.v7.widget.RecyclerView;

/**
 * created by Administrator
 * 2018/5/3
 * email：lizewencn@126.com
 * Desc:
 */
public interface ItemTouchHelperListener {

    void onItemMove(RecyclerView.ViewHolder source, RecyclerView.ViewHolder target);

    //数据删除
    void onItemDissmiss(RecyclerView.ViewHolder source);

    //drag或者swipe选中
    void onItemSelect(RecyclerView.ViewHolder source);

    //状态清除
    void onItemClear(RecyclerView.ViewHolder source);
}
