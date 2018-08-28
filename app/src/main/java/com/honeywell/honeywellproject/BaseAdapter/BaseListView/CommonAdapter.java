package com.honeywell.honeywellproject.BaseAdapter.BaseListView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by QHT on 2017-09-08.
 */
public abstract class CommonAdapter<E> extends BaseAdapter {

    private List<E> list;
    private Context context;
    private int     LayoutID;

    public CommonAdapter(Context context, List<E> list, int LayoutID){
        this.context=context;
        this.list=list;
        this.LayoutID=LayoutID;
    }
    @Override
    public E getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        return null==list ? 0:list.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonViewHolder vh=CommonViewHolder.get(context,LayoutID,convertView,parent);
        convert(vh,getItem(position),position);
        return vh.getConvertView();
    }
    public abstract void convert(CommonViewHolder vh,E item,int position);
}
