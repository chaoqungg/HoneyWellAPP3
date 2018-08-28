package com.honeywell.honeywellproject.BleTaskModule.SonTask.adapter;

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
import com.honeywell.honeywellproject.BleTaskModule.SonTask.Event.SonTaskSaveEvent;
import com.honeywell.honeywellproject.BleTaskModule.SonTask.Event.SonTaskSelectEvent;
import com.honeywell.honeywellproject.BleTaskModule.SonTask.data.SonTaskBean;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.EditTextCursorUtil;
import com.honeywell.honeywellproject.Util.EventBusUtil;
import com.honeywell.honeywellproject.Util.PhoneUtil;
import com.honeywell.honeywellproject.Util.ResourceUtil;
import com.honeywell.honeywellproject.Util.TextUtil;
import com.honeywell.honeywellproject.WidgeView.SwipeItemLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by QHT on 2017-10-10.
 */
public class SonTaskAdapterNewUI extends BaseQuickAdapter<SonTaskBean,SonTaskAdapterNewUI.OneSlideViewHolder> {

    private ISlideHelper mISlideHelper = new ISlideHelper();
    private Context context;
    public  boolean KeyShow=false;
    //用map保存对应位置的viewholder
    private Map<Integer, RecyclerView.ViewHolder> viewHolderMap = new HashMap<>();
    @Override
    public OneSlideViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        OneSlideViewHolder oneSlideViewHolder = new OneSlideViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_sontask_item_newui, parent, false));
        //add holder
        mISlideHelper.add(oneSlideViewHolder);
        oneSlideViewHolder.setAdapter(this);
        return oneSlideViewHolder;
    }

    @Override
    protected void convert(final OneSlideViewHolder baseViewHolder, final SonTaskBean sonTaskBean) {
        ((OneSlideViewHolder) baseViewHolder).bind();
        viewHolderMap.put(baseViewHolder.getAdapterPosition(), baseViewHolder);
        final SwipeItemLayout swipeItemLayout = (SwipeItemLayout)(baseViewHolder.getView(R.id.swipeitemlayout));
        final View ll_top2 = baseViewHolder.getView(R.id.ll_top2);
        final RelativeLayout rv_fold = (RelativeLayout)(baseViewHolder.getView(R.id.rl_sontask_fold));
        baseViewHolder.setText(R.id.tv_name1,"编"+ResourceUtil.getString(R.string.backspace_hanzi1)+"号：");
        baseViewHolder.setText(R.id.tv_addr1,"地"+ResourceUtil.getString(R.string.backspace_hanzi1)+"址：");
        baseViewHolder.setText(R.id.tv_description1,"描"+ResourceUtil.getString(R.string.backspace_hanzi1)+"述：");
        baseViewHolder.setText(R.id.tv_leixing2,sonTaskBean.getDevicetype());
        baseViewHolder.setText(R.id.tv_name2,sonTaskBean.getTasknumber()+"");
        baseViewHolder.setChecked(R.id.iv_check,sonTaskBean.isselect());
        baseViewHolder.setText(R.id.tv_series2,sonTaskBean.getTaskserialnumber());
        if(sonTaskBean.getTaskdigitaladdress()<=0){
            baseViewHolder.getView(R.id.xinghao).setVisibility(View.VISIBLE);
            baseViewHolder.getView(R.id.xinghao_top2).setVisibility(View.VISIBLE);
            baseViewHolder.setText(R.id.tv_addr2,"");
            baseViewHolder.setText(R.id.tv_top2_2_2,"");
        }else{
            baseViewHolder.getView(R.id.xinghao).setVisibility(View.INVISIBLE);
            baseViewHolder.getView(R.id.xinghao_top2).setVisibility(View.INVISIBLE);
            baseViewHolder.setText(R.id.tv_addr2,sonTaskBean.getTaskdigitaladdress()+"");
            baseViewHolder.setText(R.id.tv_top2_2_2,sonTaskBean.getTaskdigitaladdress()+"");
        }
        baseViewHolder.setText(R.id.tv_top2_1_2,sonTaskBean.getTaskserialnumber());
        baseViewHolder.setText(R.id.tv_description2,sonTaskBean.getSondescription());
        baseViewHolder.addOnClickListener(R.id.delete);
        baseViewHolder.addOnClickListener(R.id.rl_sontask_top);
        baseViewHolder.addOnClickListener(R.id.rl_sontask_fold);
        baseViewHolder.addOnClickListener(R.id.iv_saomiao);
        baseViewHolder.getView(R.id.tv_name2).setFocusable(false);
        baseViewHolder.getView(R.id.tv_name2).setClickable(false);
        baseViewHolder.getView(R.id.tv_top2_1_2).setFocusable(false);
        baseViewHolder.getView(R.id.tv_top2_1_2).setClickable(false);
        baseViewHolder.getView(R.id.tv_top2_2_2).setFocusable(false);
        baseViewHolder.getView(R.id.tv_top2_2_2).setClickable(false);
        baseViewHolder.getView(R.id.tv_series2).setFocusable(false);
        baseViewHolder.getView(R.id.tv_series2).setClickable(false);
        baseViewHolder.getView(R.id.tv_addr2).setFocusable(false);
        baseViewHolder.getView(R.id.tv_addr2).setClickable(false);
        baseViewHolder.getView(R.id.tv_description2).setFocusable(false);
        baseViewHolder.getView(R.id.tv_description2).setClickable(false);
        (baseViewHolder.getView(R.id.tv_series2)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    view.setClickable(true);
                    view.setFocusableInTouchMode(true);
                    baseViewHolder.getView(R.id.tv_addr2).setFocusable(true);
                    baseViewHolder.getView(R.id.tv_addr2).setClickable(true);
                    baseViewHolder.getView(R.id.tv_description2).setFocusable(true);
                    baseViewHolder.getView(R.id.tv_description2).setClickable(true);
                     EditText et1=(EditText)(baseViewHolder.getView(R.id.tv_addr2));
                    et1.setCursorVisible(true);
                    EditText et2=(EditText)(baseViewHolder.getView(R.id.tv_description2));
                    et2.setCursorVisible(true);
                    EditText et3=(EditText)(view);
                    et3.setCursorVisible(true);
                    EditTextCursorUtil.setCursorDrawable(et1,R.drawable.editext_cursor);
                    EditTextCursorUtil.setCursorDrawable(et2,R.drawable.editext_cursor);
                    EditTextCursorUtil.setCursorDrawable(et3,R.drawable.editext_cursor);
                    ((EditText)view).setSingleLine(true);
                    ((EditText)view).setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                    ((EditText)view).setImeOptions(EditorInfo.IME_ACTION_NEXT);
                    showSoftInput(view);
                    ((EditText)baseViewHolder.getView(R.id.tv_series2)).addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                        @Override
                        public void afterTextChanged(Editable editable) {
                            if((baseViewHolder.getView(R.id.tv_series2)).hasFocus()){
                                    EventBusUtil.postSync(new SonTaskSaveEvent(baseViewHolder.getAdapterPosition(),
                                            ((EditText)baseViewHolder.getView(R.id.tv_series2)).getText().toString(),
                                            ((EditText)baseViewHolder.getView(R.id.tv_addr2)).getText().toString(),
                                            ((EditText)baseViewHolder.getView(R.id.tv_description2)).getText().toString()));
                            }
                        }
                    });
                }else{
                    view.clearFocus();
                }
            }
        });
        ((EditText)baseViewHolder.getView(R.id.tv_series2)).setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                // TODO Auto-generated method stub
                if(arg1 == EditorInfo.IME_ACTION_NEXT) {
                    //然后点击键盘的下一步可以跳到描述上
                    baseViewHolder.getView(R.id.tv_addr2).requestFocus();
                }
                return false;
            }

        });
        (baseViewHolder.getView(R.id.tv_addr2)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    view.setClickable(true);
                    view.setFocusableInTouchMode(true);
                    ((EditText)view).setSingleLine(true);
                    ((EditText)view).setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                    ((EditText)view).setImeOptions(EditorInfo.IME_ACTION_NEXT);
                    showSoftInput(view);
                    ((EditText)baseViewHolder.getView(R.id.tv_addr2)).addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                        @Override
                        public void afterTextChanged(Editable editable) {
                            if((baseViewHolder.getView(R.id.tv_addr2)).hasFocus()){
                                if (!TextUtil.isEmpty(editable.toString())) {
                                    baseViewHolder.getView(R.id.xinghao).setVisibility(View.INVISIBLE);
                                } else {
                                    baseViewHolder.getView(R.id.xinghao).setVisibility(View.VISIBLE);
                                }
                                   EventBusUtil.postSync(new SonTaskSaveEvent(baseViewHolder.getAdapterPosition(),
                                    ((EditText)baseViewHolder.getView(R.id.tv_series2)).getText().toString(),
                                            ((EditText)baseViewHolder.getView(R.id.tv_addr2)).getText().toString(),
                                            ((EditText)baseViewHolder.getView(R.id.tv_description2)).getText().toString()));
                            }
                        }
                    });
                }else{
                    view.clearFocus();
                }
            }
        });
        (baseViewHolder.getView(R.id.tv_description2)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    view.setClickable(true);
                    view.setFocusableInTouchMode(true);
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
                                   EventBusUtil.postSync(new SonTaskSaveEvent(baseViewHolder.getAdapterPosition(),
                                    ((EditText)baseViewHolder.getView(R.id.tv_series2)).getText().toString(),
                                            ((EditText)baseViewHolder.getView(R.id.tv_addr2)).getText().toString(),
                                            ((EditText)baseViewHolder.getView(R.id.tv_description2)).getText().toString()));
                            }
                        }
                    });
                }else{
                    view.clearFocus();
                }
            }
        });
        if(sonTaskBean.isexpand()){
            baseViewHolder.setText(R.id.tv_name1,"编"+ResourceUtil.getString(R.string.backspace_hanzi1)+"号：");
            ll_top2.setVisibility(View.INVISIBLE);
            rv_fold.setVisibility(View.VISIBLE);
            baseViewHolder.setImageDrawable(R.id.iv_flod_arrow, ResourceUtil.getDrawable(R.drawable.arrow_up_60));
        }else{
            baseViewHolder.setText(R.id.tv_name1,"编号：");
            ll_top2.setVisibility(View.VISIBLE);
            rv_fold.setVisibility(View.GONE);
            baseViewHolder.setImageDrawable(R.id.iv_flod_arrow, ResourceUtil.getDrawable(R.drawable.arrow_down_60));
        }
        if(!KeyShow){
            EditText et1=(EditText)(baseViewHolder.getView(R.id.tv_series2));
            EditText et2=(EditText)(baseViewHolder.getView(R.id.tv_description2));
            EditText et3=(EditText)(baseViewHolder.getView(R.id.tv_addr2));
            et1.setCursorVisible(false);
            et2.setCursorVisible(false);
            et3.setCursorVisible(false);
            EditTextCursorUtil.setCursorDrawable(et1,R.drawable.editext_cursor_transparent);
            EditTextCursorUtil.setCursorDrawable(et2,R.drawable.editext_cursor_transparent);
            EditTextCursorUtil.setCursorDrawable(et3,R.drawable.editext_cursor_transparent);
        }else{
            EditText et1=(EditText)(baseViewHolder.getView(R.id.tv_series2));
            EditText et2=(EditText)(baseViewHolder.getView(R.id.tv_description2));
            EditText et3=(EditText)(baseViewHolder.getView(R.id.tv_addr2));
            et1.setCursorVisible(true);
            et2.setCursorVisible(true);
            et3.setCursorVisible(true);
            EditTextCursorUtil.setCursorDrawable(et1,R.drawable.editext_cursor);
            EditTextCursorUtil.setCursorDrawable(et2,R.drawable.editext_cursor);
            EditTextCursorUtil.setCursorDrawable(et3,R.drawable.editext_cursor);
        }
        if(sonTaskBean.isProcess()){
            baseViewHolder.getView(R.id.edit).setClickable(false);
        }else{
            baseViewHolder.setOnClickListener(R.id.edit, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    swipeItemLayout.close();
                    //如果收缩状态，则自动展开
                    if(rv_fold.getVisibility()==View.GONE){
                        rv_fold.setVisibility(View.VISIBLE);
                        ll_top2.setVisibility(View.INVISIBLE);
                        baseViewHolder.setImageDrawable(R.id.iv_flod_arrow, ResourceUtil.getDrawable(R.drawable.arrow_up_60));
                        sonTaskBean.setIsexpand(true);
                    }
                    baseViewHolder.getView(R.id.tv_series2).setFocusable(true);
                    baseViewHolder.getView(R.id.tv_series2).setFocusableInTouchMode(true);
                    baseViewHolder.getView(R.id.tv_addr2).setFocusable(true);
                    baseViewHolder.getView(R.id.tv_addr2).setFocusableInTouchMode(true);
                    baseViewHolder.getView(R.id.tv_description2).setFocusable(true);
                    baseViewHolder.getView(R.id.tv_description2).setFocusableInTouchMode(true);
                    baseViewHolder.getView(R.id.tv_series2).requestFocus();
                }
            });
        }
        baseViewHolder.setOnClickListener(R.id.iv_rightfold, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(KeyShow){
                    return;
                }
                if(sonTaskBean.isexpand()){
                    baseViewHolder.setText(R.id.tv_name1,"编号：");
                    ll_top2.setVisibility(View.VISIBLE);
                    rv_fold.setVisibility(View.GONE);
                    sonTaskBean.setIsexpand(false);
                    baseViewHolder.setImageDrawable(R.id.iv_flod_arrow, ResourceUtil.getDrawable(R.drawable.arrow_down_60));
                    }else{
                    baseViewHolder.setText(R.id.tv_name1,"编"+ResourceUtil.getString(R.string.backspace_hanzi1)+"号：");
                    rv_fold.setVisibility(View.VISIBLE);
                    ll_top2.setVisibility(View.INVISIBLE);
                    sonTaskBean.setIsexpand(true);
                    baseViewHolder.setImageDrawable(R.id.iv_flod_arrow, ResourceUtil.getDrawable(R.drawable.arrow_up_60));
                   }
            }
        });
        baseViewHolder.setOnCheckedChangeListener(R.id.iv_check, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                EventBusUtil.postSync(new SonTaskSelectEvent(baseViewHolder.getAdapterPosition(),b));
            }
        });
    }


    public SonTaskAdapterNewUI(List<SonTaskBean> list, Context context) {
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
            mContentRl = itemView.findViewById(R.id.rootrl_sontask);
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
    public Map getViewHolderMap() {
        return viewHolderMap;
    }
}
