package com.honeywell.honeywellproject.BleTaskModule.QrCode;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.honeywell.honeywellproject.BaseActivity.ToolBarActivity;
import com.honeywell.honeywellproject.BleTaskModule.SonTask.data.SonTaskBean;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.PhoneUtil;
import com.honeywell.honeywellproject.Util.TextUtil;
import com.honeywell.honeywellproject.Util.ToastUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class QrCodeInputActivity extends ToolBarActivity {


    @BindView(R.id.qr_number2)
    TextView qrNumber2;
    @BindView(R.id.qr_type2)
    TextView qrType2;
    @BindView(R.id.qr_series2)
    EditText qrSeries2;
    @BindView(R.id.qr_add2)
    EditText qrAdd2;
    @BindView(R.id.qr_description2)
    EditText qrDescription2;
    private String  tasknumber, id, series,digitaladdr,position;
    private boolean isEditAddr;
    private String logininfo_id, project_id, controller_id, fathertask_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_qr_code_input;
    }

    private void initView() {
        getToolbarTitle().setText("手动输入序列号");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            position = bundle.getString("position");
            series = bundle.getString("series");
            tasknumber = bundle.getString("tasknumber");
            digitaladdr = bundle.getString("digitaladdr");
            logininfo_id = bundle.getString("logininfo_id");
            project_id = bundle.getString("project_id");
            controller_id = bundle.getString("controller_id");
            fathertask_id = bundle.getString("fathertask_id");
            id = bundle.getString("id");
        }
        qrNumber2.setText(tasknumber);
        qrSeries2.setText(series);
        qrAdd2.setText(digitaladdr);
        qrAdd2.setSelection(qrAdd2.getText().toString().length());
        qrSeries2.requestFocus();
        qrSeries2.postDelayed(new Runnable() {
            @Override
            public void run() {
                PhoneUtil.showInputWindow(QrCodeInputActivity.this, qrSeries2);
            }
        }, 200);
        qrAdd2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                isEditAddr=true;
                if(TextUtil.isEmpty(editable.toString())){
                    return;
                }
                if(Integer.parseInt(editable.toString())>239){
                    editable.delete(2,3);
                }
            }
        });
    }



    @OnClick(R.id.qr_sure)
    public void onViewClicked() {
        if(TextUtil.isEmpty(qrAdd2.getText().toString())){
            ToastUtil.showToastShort("地址不能为空");
            return;
        }
        if(qrSeries2.getText().toString().length()!=10){
            ToastUtil.showToastShort("序列号格式非法，请重新填写");
            return;
        }
        List<SonTaskBean> list = DataSupport.where("logininfo_id= ? and project_id = ? and controller_id= ? and fathertask_id= ?", logininfo_id, project_id, controller_id, fathertask_id).find(SonTaskBean.class);
        //数字地址查重
        if(isEditAddr){
            //地址不能重复
            int id=0;
            for (SonTaskBean bean : list) {
                id++;
                if(id-1==Integer.parseInt(position)){
                    //不包括自身以前的地址
                    continue;
                }
                if (bean.getTaskdigitaladdress()==Integer.parseInt(qrAdd2.getText().toString())) {
                    ToastUtil.showToastShort("地址存在重复,请修改");
                    return;
                }
            }
        }
        for (SonTaskBean bean : list) {
            if (!TextUtil.isEmpty(bean.getTaskserialnumber())  && bean.getTaskserialnumber().equals(qrSeries2.getText().toString())) {
                ToastUtil.showToastShort("序列号存在重复,请重新输入序列号");
                return;
            }
        }
        Intent mIntent = new Intent();
        Bundle bundle=new Bundle();
        bundle.putString("series", qrSeries2.getText().toString());
        bundle.putString("tasknumber", tasknumber);
        bundle.putString("devicetype", qrType2.getText().toString());
        bundle.putString("digitaladdr", qrAdd2.getText().toString());
        bundle.putString("description", qrDescription2.getText().toString());
        mIntent.putExtras(bundle);
        this.setResult(-1, mIntent);
        this.finish();
        ToastUtil.showToastShort("添加成功");
    }

}
