package com.honeywell.honeywellproject.LoginModule.widge;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.honeywell.honeywellproject.BaseActivity.ToolBarActivity;
import com.honeywell.honeywellproject.LoginModule.data.LoginInfoBeanEL;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.DialogUtil;
import com.honeywell.honeywellproject.Util.SharePreferenceUtil;
import com.honeywell.honeywellproject.Util.ToastUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ModifyPassWordELActivity extends ToolBarActivity {


    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_useroldpassword)
    EditText etUseroldpassword;
    @BindView(R.id.et_usernewpassword)
    EditText etUsernewpassword;
    @BindView(R.id.et_usernewpassword2)
    EditText etUsernewpassword2;
    @BindView(R.id.btn_confirm)
    Button   btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initView();
        initdata();
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_modifypassword_el;
    }

    private void initView() {
        getToolbarTitle().setText("密码修改");
        getSubTitle().setVisibility(View.GONE);
    }

    private void initdata() {
        String currentusername = SharePreferenceUtil.getStringSP("currentusername_el","");
        String  currentpsaaword = SharePreferenceUtil.getStringSP("currentpsaaword_el","");
        etUsername.setText(currentusername);
        etUsername.setSelection(etUsername.length());
        etUseroldpassword.setText(currentpsaaword);
        etUseroldpassword.setSelection(currentpsaaword.length());
    }

    @OnClick(R.id.btn_confirm)
    public void onViewClicked() {
        if(etUsernewpassword.getText().toString().isEmpty() || etUsernewpassword2.getText().toString().isEmpty()){
            ToastUtil.showToastShort("请填写完整新密码");
            return;
        }
        if(!etUsernewpassword.getText().toString().equals(etUsernewpassword2.getText().toString())){
            ToastUtil.showToastShort("两次密码输入不同");
            return;
        }
       List<LoginInfoBeanEL> logininfolist= DataSupport.findAll(LoginInfoBeanEL.class);
        boolean hasthisname = false;
        if(!logininfolist.isEmpty()) {
            for (int i = 0; i < logininfolist.size(); i++) {
                LoginInfoBeanEL bean = logininfolist.get(i);
                if(etUsername.getText().toString().equals(bean.getUsername())){
                    hasthisname=true;
                    break;
                }
            }
        }
        if(!hasthisname){
            ToastUtil.showToastShort("此用户名未注册");
            return;
        }
        LoginInfoBeanEL bean=new LoginInfoBeanEL();
        bean.setPassword(etUsernewpassword2.getText().toString());
        bean.updateAll("username = ?", etUsername.getText().toString());
        DialogUtil.showAlertDialog(ModifyPassWordELActivity.this, "提示", "修改密码成功.点击确认返回登录界面", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              finish();
            }
        },null);
    }
}
