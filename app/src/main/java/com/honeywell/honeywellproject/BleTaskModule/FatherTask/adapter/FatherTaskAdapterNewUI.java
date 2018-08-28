package com.honeywell.honeywellproject.BleTaskModule.FatherTask.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.honeywell.honeywellproject.BaseAdapter.BaseSlideRecycleView.ISlideHelper;
import com.honeywell.honeywellproject.BaseAdapter.BaseSlideRecycleView.holder.SlideViewHolder;
import com.honeywell.honeywellproject.BleTaskModule.FatherTask.Event.FatherTaskSaveEvent;
import com.honeywell.honeywellproject.BleTaskModule.FatherTask.Event.FatherTaskSelectEvent;
import com.honeywell.honeywellproject.BleTaskModule.FatherTask.data.FatherTaskBean;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.EditTextCursorUtil;
import com.honeywell.honeywellproject.Util.EventBusUtil;
import com.honeywell.honeywellproject.Util.PhoneUtil;
import com.honeywell.honeywellproject.Util.ResourceUtil;
import com.honeywell.honeywellproject.Util.TextUtil;
import com.honeywell.honeywellproject.WidgeView.SwipeItemLayout;

import java.util.List;

/**
 * Created by QHT on 2017-10-10.
 *
 * 参考:  https://github.com/ruzhan123/ViewHolder-Slide-Helper
 * 自己结合 BaseQuickAdapter
 */
public class FatherTaskAdapterNewUI extends BaseQuickAdapter<FatherTaskBean,FatherTaskAdapterNewUI.OneSlideViewHolder> {

    private ISlideHelper mISlideHelper = new ISlideHelper();
    private Context context;
    public  boolean KeyShow=false;
    @Override
    public OneSlideViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        OneSlideViewHolder oneSlideViewHolder = new OneSlideViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_fathtask_item_newui, parent, false));
        mISlideHelper.add(oneSlideViewHolder);
        oneSlideViewHolder.setAdapter(this);
        return oneSlideViewHolder;
    }

    @Override
    protected void convert(final OneSlideViewHolder baseViewHolder, final FatherTaskBean fatherTaskBean) {
        ((OneSlideViewHolder) baseViewHolder).bind();
        final SwipeItemLayout swipeItemLayout = (SwipeItemLayout)(baseViewHolder.getView(R.id.swipeitemlayout));
        final RelativeLayout rv_fold = (RelativeLayout)(baseViewHolder.getView(R.id.rl_fathertask_fold));
        baseViewHolder.setText(R.id.tv_name2,fatherTaskBean.getTasknumber()+"");
        baseViewHolder.setText(R.id.tv_amount2,fatherTaskBean.getSontaskamount()+"");
        baseViewHolder.setText(R.id.tv_description2,fatherTaskBean.getFatherdescription());
        baseViewHolder.setChecked(R.id.iv_check,fatherTaskBean.isselect());
        baseViewHolder.addOnClickListener(R.id.delete);
        baseViewHolder.addOnClickListener(R.id.rl_fathertask_top);
        baseViewHolder.addOnClickListener(R.id.rl_fathertask_fold);
        baseViewHolder.getView(R.id.tv_name2).setFocusable(false);
        baseViewHolder.getView(R.id.tv_name2).setClickable(false);
        baseViewHolder.getView(R.id.tv_description2).setFocusable(false);
        baseViewHolder.getView(R.id.tv_description2).setClickable(false);
        baseViewHolder.setText(R.id.tv_data2,fatherTaskBean.getTaskdate());
        (baseViewHolder.getView(R.id.tv_description2)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    view.setClickable(true);
                    view.setFocusableInTouchMode(true);
                    EditText et=(EditText)(view);
                    et.setCursorVisible(true);
                    EditTextCursorUtil.setCursorDrawable(et,R.drawable.editext_cursor);
                    ((EditText)view).setSingleLine(true);
                    ((EditText)view).setInputType(EditorInfo.TYPE_CLASS_TEXT);
                    ((EditText)view).setImeOptions(EditorInfo.IME_ACTION_DONE);
                    showSoftInput(view);
                    ((EditText)baseViewHolder.getView(R.id.tv_description2)).addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                        @Override
                        public void afterTextChanged(Editable editable) {
                            if((baseViewHolder.getView(R.id.tv_description2)).hasFocus()){
                                    EventBusUtil.postSync(new FatherTaskSaveEvent(baseViewHolder.getAdapterPosition(),
                                            ((EditText)baseViewHolder.getView(R.id.tv_name2)).getText().toString(),
                                            ((EditText)baseViewHolder.getView(R.id.tv_description2)).getText().toString()));
                            }
                        }
                    });
                }else{
                    view.clearFocus();
                }
            }
        });
        if(fatherTaskBean.isexpand()){
            rv_fold.setVisibility(View.VISIBLE);
            baseViewHolder.setImageDrawable(R.id.iv_flod_arrow, ResourceUtil.getDrawable(R.drawable.arrow_up_60));
        }else{
            rv_fold.setVisibility(View.GONE);
            baseViewHolder.setImageDrawable(R.id.iv_flod_arrow, ResourceUtil.getDrawable(R.drawable.arrow_down_60));
        }
        baseViewHolder.setOnClickListener(R.id.edit, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swipeItemLayout.close();
                //如果收缩状态，则自动展开
                if(rv_fold.getVisibility()==View.GONE){
                    rv_fold.setVisibility(View.VISIBLE);
                    baseViewHolder.setImageDrawable(R.id.iv_flod_arrow, ResourceUtil.getDrawable(R.drawable.arrow_right_60));
                    fatherTaskBean.setIsexpand(true);
                }
                baseViewHolder.getView(R.id.tv_description2).setFocusable(true);
                baseViewHolder.getView(R.id.tv_description2).setFocusableInTouchMode(true);
                baseViewHolder.getView(R.id.tv_description2).requestFocus();
            }
        });
        baseViewHolder.setOnClickListener(R.id.iv_flod_arrow, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(KeyShow){
                    return;
                }
                if(fatherTaskBean.isexpand()){
                    baseViewHolder.setImageDrawable(R.id.iv_flod_arrow, ResourceUtil.getDrawable(R.drawable.arrow_down_60));
                    rv_fold.setVisibility(View.GONE);
                    fatherTaskBean.setIsexpand(false);
                }else{
                    baseViewHolder.setImageDrawable(R.id.iv_flod_arrow, ResourceUtil.getDrawable(R.drawable.arrow_right_60));
                    rv_fold.setVisibility(View.VISIBLE);
                    fatherTaskBean.setIsexpand(true);
                }
            }
        });

        baseViewHolder.setOnCheckedChangeListener(R.id.iv_check, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                EventBusUtil.postSync(new FatherTaskSelectEvent(baseViewHolder.getAdapterPosition(),b));
            }
        });
    }

    public FatherTaskAdapterNewUI(List<FatherTaskBean> list, Context context) {
        super(list);
        this.context=context;
    }

    public void slideOpen() {
        mISlideHelper.slideOpen();
    }

    public void slideClose() {
        mISlideHelper.slideClose();
    }





    public class OneSlideViewHolder extends SlideViewHolder {

        private View      mContentRl;

        public OneSlideViewHolder(View itemView) {
            super(itemView);
            mContentRl = itemView.findViewById(R.id.rootrl_fathertask);
        }

        @Override
        protected BaseViewHolder setAdapter(BaseQuickAdapter adapter) {
            return super.setAdapter(adapter);
        }

        public void bind() {
            //slide offset
            setOffset(35);
            //slide must call,param is slide view
            onBindSlide(mContentRl);
        }

        @Override
        public void doAnimationSet(int offset, float fraction) {
             mContentRl.scrollTo(offset, 0);
        }

        @Override
        public void onBindSlideClose(int state) {
        }

        @Override
        public void doAnimationSetOpen(int state) {
        }
    }
    private void showSoftInput(final View view){
        if(!TextUtil.isEmpty(((EditText)view).getText().toString())){
            ((EditText)view).setSelection(((EditText)view).getText().toString().length());
        }
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                PhoneUtil.showInputWindow(context,view);
            }
        },200);
    }
    @Override
    public RecyclerView getRecyclerView() {
        return super.getRecyclerView();
    }
}
