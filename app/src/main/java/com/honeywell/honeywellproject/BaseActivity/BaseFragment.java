package com.honeywell.honeywellproject.BaseActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.honeywell.honeywellproject.Util.ResourceUtil;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by QHT on 2017-04-08.
 */
public abstract class BaseFragment extends Fragment
{
    /**
     * 依附的activity
     */
    protected FragmentActivity  mActivity;
    /**
     * 根view
     */
    protected View              mRootView;
    protected Unbinder          unbinder;
    private View rootView;//缓存Fragment view

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //不缓存
        rootView=inflater.inflate(getContentViewId(), null);
        onCreateV(rootView);
        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }
    protected View onCreateV(View mRootView) {
        unbinder = ButterKnife.bind(this, mRootView);
        initView(mRootView);
        return mRootView;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        mActivity = getActivity();
    }

    /**
     * 设置根布局资源id
     * @return
     */
    public abstract int getContentViewId();

    /**
     * 初始化资源
     * @return
     */
    public abstract void initView(View view);

    public void gotoActivity(Context context, Class<?> cls) {
        Intent intent=new Intent(context,cls);
        startActivity(intent);
    }


    protected  void setImageDrawable(ImageView imageView, int id){
        imageView.setImageDrawable(ResourceUtil.getDrawable(id));
    }
    protected void openImage(HashMap<Integer, Boolean> imageMap, ImageView imageView, int imageId, int drawableId){
        imageMap.put(imageId, true);
        setImageDrawable(imageView,drawableId);
    }

    protected void closeImage(HashMap<Integer, Boolean> imageMap,ImageView imageView,int imageId,int drawableId){
        if(imageMap.size()==0){return;}
        imageMap.put(imageId, false);
        setImageDrawable(imageView,drawableId);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(unbinder!=null){
            try {
                unbinder.unbind();
            }catch (IllegalStateException e){
            }
        }
    }


}
