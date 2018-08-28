package com.honeywell.honeywellproject.LoginModule.widge;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.honeywell.honeywellproject.BaseActivity.ToolBarActivity;
import com.honeywell.honeywellproject.LoginModule.data.LoginInfoBean;
import com.honeywell.honeywellproject.LoginModule.data.LoginInfoBeanEL;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.RegexUtil;
import com.honeywell.honeywellproject.Util.SharePreferenceUtil;
import com.honeywell.honeywellproject.Util.TextUtil;
import com.honeywell.honeywellproject.Util.ToastUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegistActivity extends ToolBarActivity {


    @BindView(R.id.radiogroup)
    RadioGroup  radiogroup;
    @BindView(R.id.regist_username2)
    EditText    registUsername2;
    @BindView(R.id.regist_phone2)
    EditText    registPhone2;
    @BindView(R.id.regist_password2)
    EditText    registPassword2;
    @BindView(R.id.regist_passwordcheck2)
    EditText    registPasswordcheck2;
    @BindView(R.id.radiobutton1)
    RadioButton radiobutton1;
    @BindView(R.id.radiobutton2)
    RadioButton radiobutton2;
    private int userType = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        getToolbarTitle().setText("注册");
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radiobutton1) {
                    userType = 1;
                    radiobutton1.setChecked(true);
                    radiobutton2.setChecked(false);
                } else {
                    userType = 0;
                    radiobutton1.setChecked(false);
                    radiobutton2.setChecked(true);
                }
            }
        });
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_regist;
    }

    @OnClick({R.id.btn_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                if (TextUtil.isEmpty(registUsername2.getText().toString())) {
                    ToastUtil.showToastShort("请输入用户名");
                    return;
                }
                if (TextUtil.isEmpty(registPhone2.getText().toString())) {
                    ToastUtil.showToastShort("请输入手机号");
                    return;
                } else if (!RegexUtil.matchPhone(registPhone2.getText().toString())) {
                    ToastUtil.showToastShort("请输入正确格式的手机号");
                    return;
                }
                if (TextUtil.isEmpty(registPassword2.getText().toString())) {
                    ToastUtil.showToastShort("请输入密码");
                    return;
                }
                if (TextUtil.isEmpty(registPasswordcheck2.getText().toString())) {
                    ToastUtil.showToastShort("请输入验证密码");
                    return;
                }
                if (!registPasswordcheck2.getText().toString().equals(registPassword2.getText().toString())) {
                    ToastUtil.showToastShort("两次密码输入不一致");
                    return;
                }


                if ("ble".equals(SharePreferenceUtil.getStringSP("apptype", ""))) {
                    List<LoginInfoBean> list = DataSupport.findAll(LoginInfoBean.class);
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getUsername().equals(registUsername2.getText().toString())
                                && list.get(i).getUsertype()==userType) {
                            ToastUtil.showToastShort("用户名已存在");
                            return;
                        }
                    }
                    LoginInfoBean bean = new LoginInfoBean();
                    bean.setUsertype(userType);
                    bean.setUsername(registUsername2.getText().toString());
                    bean.setPassword(registPassword2.getText().toString());
                    bean.setPhone(registPhone2.getText().toString());
                    bean.save();
                    SharePreferenceUtil.setIntSP("currentusertype", userType);
                    SharePreferenceUtil.setStringSP("currentusername", registUsername2.getText().toString());
                    SharePreferenceUtil.setStringSP("currentpsaaword", registPassword2.getText().toString());
                    SharePreferenceUtil.setStringSP("currentphone", registPhone2.getText().toString());
                } else {
                    List<LoginInfoBeanEL> list = DataSupport.findAll(LoginInfoBeanEL.class);
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getUsername().equals(registUsername2.getText().toString())
                                && list.get(i).getUsertype()==userType) {
                            ToastUtil.showToastShort("用户名已存在");
                            return;
                        }
                    }
                    LoginInfoBeanEL bean = new LoginInfoBeanEL();
                    bean.setUsertype(userType);
                    bean.setUsername(registUsername2.getText().toString());
                    bean.setPassword(registPassword2.getText().toString());
                    bean.setPhone(registPhone2.getText().toString());
                    bean.save();
                    SharePreferenceUtil.setIntSP("currentusertype_el", userType);
                    SharePreferenceUtil.setStringSP("currentusername_el", registUsername2.getText().toString());
                    SharePreferenceUtil.setStringSP("currentpsaaword_el", registPassword2.getText().toString());
                    SharePreferenceUtil.setStringSP("currentphone_el", registPhone2.getText().toString());
                }
                ToastUtil.showToastShort("注册成功");
                finish();
                break;
            default:
        }
    }
}
