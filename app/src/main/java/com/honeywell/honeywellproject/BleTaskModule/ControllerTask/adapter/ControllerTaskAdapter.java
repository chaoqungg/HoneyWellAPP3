package com.honeywell.honeywellproject.BleTaskModule.ControllerTask.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.honeywell.honeywellproject.BaseAdapter.BaseSlideRecycleView.ISlideHelper;
import com.honeywell.honeywellproject.BaseAdapter.BaseSlideRecycleView.holder.SlideViewHolder;
import com.honeywell.honeywellproject.BleTaskModule.ControllerTask.Event.ControllerTaskSaveEvent;
import com.honeywell.honeywellproject.BleTaskModule.ControllerTask.Event.ControllerTaskSelectEvent;
import com.honeywell.honeywellproject.BleTaskModule.ControllerTask.data.ControllerBean;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.EditTextCursorUtil;
import com.honeywell.honeywellproject.Util.EventBusUtil;
import com.honeywell.honeywellproject.Util.PhoneUtil;
import com.honeywell.honeywellproject.Util.ResourceUtil;
import com.honeywell.honeywellproject.Util.TextUtil;
import com.honeywell.honeywellproject.WidgeView.SwipeItemLayout;

import java.util.List;

/**
 * Created by QHT on 2017-12-26.
 */
public class ControllerTaskAdapter extends BaseQuickAdapter<ControllerBean,ControllerTaskAdapter.OneSlideViewHolder> {

    private ISlideHelper mISlideHelper = new ISlideHelper();
    private Context context;
    public  boolean KeyShow=false;

    @Override
    public OneSlideViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        OneSlideViewHolder oneSlideViewHolder = new OneSlideViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_controllertask_item, parent, false));
        mISlideHelper.add(oneSlideViewHolder);
        oneSlideViewHolder.setAdapter(this);
        return oneSlideViewHolder;
    }

    @Override
    protected void convert(final OneSlideViewHolder baseViewHolder, final ControllerBean controllerTaskBean) {
        ((OneSlideViewHolder) baseViewHolder).bind();
        final SwipeItemLayout swipeItemLayout = (SwipeItemLayout)(baseViewHolder.getView(R.id.swipeitemlayout));
        final RelativeLayout rv_fold = (RelativeLayout)(baseViewHolder.getView(R.id.rl_controllertask_fold));
        baseViewHolder.setText(R.id.tv_name2,controllerTaskBean.getTasknumber()+"");
        baseViewHolder.setText(R.id.tv_looperamount2,controllerTaskBean.getFathertaskamount()+"");
        baseViewHolder.setText(R.id.tv_description2,controllerTaskBean.getControllerdescription());
        baseViewHolder.setChecked(R.id.iv_check,controllerTaskBean.isselect());
        baseViewHolder.setText(R.id.tv_data2,controllerTaskBean.getControllerdata());
        baseViewHolder.addOnClickListener(R.id.delete);
        baseViewHolder.addOnClickListener(R.id.rl_controllertask_top);
        baseViewHolder.addOnClickListener(R.id.rl_controllertask_fold);
        baseViewHolder.getView(R.id.tv_name2).setFocusable(false);
        baseViewHolder.getView(R.id.tv_name2).setClickable(false);
        baseViewHolder.getView(R.id.tv_description2).setFocusable(false);
        baseViewHolder.getView(R.id.tv_description2).setClickable(false);
        (baseViewHolder.getView(R.id.tv_name2)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    view.setClickable(true);
                    view.setFocusableInTouchMode(true);
                    baseViewHolder.getView(R.id.tv_description2).setFocusable(true);
                    baseViewHolder.getView(R.id.tv_description2).setClickable(true);
                    EditText et=(EditText)(baseViewHolder.getView(R.id.tv_description2));
                    et.setCursorVisible(true);
                    ((EditText)view).setSingleLine(true);
                    ((EditText)view).setInputType(EditorInfo.TYPE_CLASS_TEXT);
                    if(controllerTaskBean.isexpand()){
                        ((EditText)view).setImeOptions(EditorInfo.IME_ACTION_NEXT);
                    }else{
                        ((EditText)view).setImeOptions(EditorInfo.IME_ACTION_DONE);
                    }
                    showSoftInput(view);
                    ((EditText)baseViewHolder.getView(R.id.tv_name2)).addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                        @Override
                        public void afterTextChanged(Editable editable) {
                            if((baseViewHolder.getView(R.id.tv_name2)).hasFocus()) {
                                if (!TextUtil.isEmpty(editable.toString())) {
                                    baseViewHolder.getView(R.id.xinghao).setVisibility(View.INVISIBLE);
                                    EventBusUtil.postSync(new ControllerTaskSaveEvent(baseViewHolder.getAdapterPosition(),
                                            ((EditText) baseViewHolder.getView(R.id.tv_name2)).getText().toString(), ((EditText) baseViewHolder.getView(R.id.tv_description2)).getText().toString()));
                                } else {
                                    baseViewHolder.getView(R.id.xinghao).setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
                }else{
                    view.clearFocus();
                }
            }
        });
        ((EditText)baseViewHolder.getView(R.id.tv_name2)).setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                // TODO Auto-generated method stub
                if(arg1 == EditorInfo.IME_ACTION_NEXT) {
                    //然后点击键盘的下一步可以跳到描述上
                    baseViewHolder.getView(R.id.tv_description2).setFocusable(true);
                    baseViewHolder.getView(R.id.tv_description2).requestFocus();
                }
                return false;
            }

        });
        (baseViewHolder.getView(R.id.tv_description2)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    view.setClickable(true);
                    view.setFocusableInTouchMode(true);
                    ((EditText)view).setSingleLine(true);
                    ((EditText)view).setCursorVisible(true);
                    EditTextCursorUtil.setCursorDrawable(((EditText)view),R.drawable.editext_cursor);
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
                                    EventBusUtil.postSync(new ControllerTaskSaveEvent(baseViewHolder.getAdapterPosition(),
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
//        if(TextUtil.isEmpty(controllerTaskBean.getControllername())){
//            baseViewHolder.getView(R.id.xinghao).setVisibility(View.VISIBLE);
//        }else{
            baseViewHolder.getView(R.id.xinghao).setVisibility(View.INVISIBLE);
//        }
        if(controllerTaskBean.isexpand()){
            rv_fold.setVisibility(View.VISIBLE);
            baseViewHolder.setImageDrawable(R.id.iv_flod_arrow, ResourceUtil.getDrawable(R.drawable.arrow_right_60));
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
                    controllerTaskBean.setIsexpand(true);
                }
//                baseViewHolder.getView(R.id.tv_name2).setFocusable(true);
//                baseViewHolder.getView(R.id.tv_name2).setFocusableInTouchMode(true);
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
                if(controllerTaskBean.isexpand()){
                    baseViewHolder.setImageDrawable(R.id.iv_flod_arrow, ResourceUtil.getDrawable(R.drawable.arrow_down_60));
                    rv_fold.setVisibility(View.GONE);
                    controllerTaskBean.setIsexpand(false);
                }else{
                    baseViewHolder.setImageDrawable(R.id.iv_flod_arrow, ResourceUtil.getDrawable(R.drawable.arrow_right_60));
                    rv_fold.setVisibility(View.VISIBLE);
                    controllerTaskBean.setIsexpand(true);
                }
            }
        });
        baseViewHolder.setOnCheckedChangeListener(R.id.iv_check, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                EventBusUtil.postSync(new ControllerTaskSelectEvent(baseViewHolder.getAdapterPosition(),b));
            }
        });
    }

    public ControllerTaskAdapter(List<ControllerBean> list, Context context) {
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
            mContentRl = itemView.findViewById(R.id.rootrl_controllertask);
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
