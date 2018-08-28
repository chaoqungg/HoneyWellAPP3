package com.honeywell.honeywellproject.LoginModule.widge;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.honeywell.honeywellproject.BaseActivity.statusActivity;
import com.honeywell.honeywellproject.ELModule.ELActivityNewUI;
import com.honeywell.honeywellproject.LoginModule.data.LoginInfoBeanEL;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.LogUtil;
import com.honeywell.honeywellproject.Util.ResourceUtil;
import com.honeywell.honeywellproject.Util.SharePreferenceUtil;
import com.honeywell.honeywellproject.Util.TextUtil;
import com.honeywell.honeywellproject.Util.ToastUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginELActivity extends statusActivity {

    @BindView(R.id.et_usertype)
    EditText  etUsertype;
    @BindView(R.id.et_username)
    EditText  etUsername;
    @BindView(R.id.et_password)
    EditText  etPassword;
    @BindView(R.id.btn_login)
    Button    btnLogin;
    @BindView(R.id.btn_modifypassword)
    TextView  btnModifyPassword;
    @BindView(R.id.iv_usertype_arrow)
    ImageView ivUsertypeArrow;
    @BindView(R.id.iv_username_arrow)
    ImageView ivUsernameArrow;
    @BindView(R.id.iv_password_eye)
    ImageView ivPasswordEye;

    private List<String> list = new ArrayList<String>();
    private ListPopupWindow       listPopupWindow;
    private ArrayAdapter          arrayAdapter;
    private boolean               ispasswordshow;
    private List<LoginInfoBeanEL> logininfolist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initView();
        initdata();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        try{
            //登录页面清除掉flag，白色看不清状态栏时间等信息
            getWindow().setStatusBarColor(ResourceUtil.getColor(R.color.gray_shadow));
        }catch (NoSuchMethodError error){
            LogUtil.e(error.getMessage());
        }
    }

    private void initdata() {
        list.add("开发人员");
        list.add("现场人员");

    }

    @Override
    protected void onResume() {
        super.onResume();
        int currentusertype = SharePreferenceUtil.getIntSP("currentusertype_el");
        String currentusername = SharePreferenceUtil.getStringSP("currentusername_el","");
        String  currentpsaaword = SharePreferenceUtil.getStringSP("currentpsaaword_el","");
        if(TextUtil.isEmpty(currentusername)){return;}
        if(currentusertype==0){
            etUsertype.setText("开发人员");
        }else{
            etUsertype.setText("现场人员");
        }
        logininfolist= DataSupport.findAll(LoginInfoBeanEL.class);
        if(logininfolist.isEmpty()){return;}
        etUsername.setText(currentusername);
        etUsername.setSelection(currentusername.length());
        etPassword.setText(currentpsaaword);
        etPassword.setSelection(currentpsaaword.length());
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_login_el;
    }

    private void initView() {
        etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD) ;//·
        ivPasswordEye.setImageResource(R.drawable.eye_close_60);
        ispasswordshow=false;
        btnLogin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN ||motionEvent.getAction()==MotionEvent.ACTION_MOVE  ){
                    btnLogin.setTextColor(ResourceUtil.getColor(R.color.red_textee3124));
                }else{
                    if(motionEvent.getAction()==MotionEvent.ACTION_UP ){
                        btnLogin.setTextColor(ResourceUtil.getColor(R.color.gray_text686b72));
                    }
                }
                return false;
            }
        });
        if(!SharePreferenceUtil.getBooleanSP("notfirstusebleapp")){
            SharePreferenceUtil.setBooleanSP("notfirstusebleapp",true);
        }
         listPopupWindow = new ListPopupWindow(this);
         arrayAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
         listPopupWindow.setAdapter(arrayAdapter);
         listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EditText et= (EditText) (listPopupWindow.getAnchorView());
                if(et!=null){
                    et.setText(list.get(i));
                    et.setSelection(list.get(i).length());
                    if(et.getId()==R.id.et_username){
                        if(logininfolist!=null && logininfolist.size()>0){
                            for(int k=0;k<logininfolist.size();k++){
                                LoginInfoBeanEL bean = logininfolist.get(k);
                                if(et.getText().toString().equals(bean.getUsername())){
                                    etPassword.setText(bean.getPassword());
                                }
                            }
                        }
                    }
                }
                listPopupWindow.dismiss();
            }
        });
        listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ivUsertypeArrow.setImageResource(R.drawable.arrow_down_60);
                ivUsernameArrow.setImageResource(R.drawable.arrow_down_60);
            }
        });
    }

    @OnClick({R.id.btn_login,
            R.id.iv_usertype_arrow, R.id.iv_username_arrow,R.id.btn_register,
            R.id.iv_password_eye,R.id.btn_modifypassword})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if(checkNull()){return;}
                List<LoginInfoBeanEL> loginList = DataSupport.where("username = ? and usertype = ?", etUsername.getText().toString(),
                        "开发人员".equals(etUsertype.getText().toString()) ? "0" : "1").find(LoginInfoBeanEL.class);
                if(loginList.isEmpty()){
                    //先确认此用户名在数据库中存在
                    ToastUtil.showToastShort("用户名错误");
                    return;
                }else if( loginList.get(0).getPassword().equals(etPassword.getText().toString())){
                    //再对比密码，list的大小只能为1，因为用户名不重复
                    SharePreferenceUtil.setIntSP("currentusertype_el", "开发人员".equals(etUsertype.getText().toString()) ? 0 : 1);
                    SharePreferenceUtil.setStringSP("currentusername_el",etUsername.getText().toString());
                    SharePreferenceUtil.setStringSP("currentpsaaword_el",etPassword.getText().toString());
                    Intent i=new Intent(this, ELActivityNewUI.class);
                    startActivity(i);
                    finish();
                }else{
                    ToastUtil.showToastShort("密码错误");
                    return;
                }
                break;
            case R.id.btn_modifypassword:
                    Intent i=new Intent(this, ModifyPassWordELActivity.class);
                    startActivity(i);
                break;
            case R.id.iv_usertype_arrow:
                if(listPopupWindow.isShowing()){
                    ivUsertypeArrow.setImageResource(R.drawable.arrow_down_60);
                }else{
                    list.clear();
                    list.add("开发人员");
                    list.add("现场人员");
                    showListPopulWindow(etUsertype,list);
                    ivUsertypeArrow.setImageResource(R.drawable.arrow_up_60);
                }
                break;
            case R.id.iv_username_arrow:
                if(listPopupWindow.isShowing()){
                    ivUsernameArrow.setImageResource(R.drawable.arrow_down_60);
                }else{
                    if(logininfolist==null || logininfolist.isEmpty()){
                        return;
                    }
                    list.clear();
                    for (LoginInfoBeanEL bean: logininfolist) {
                        list.add(bean.getUsername());
                    }
                    showListPopulWindow(etUsername,list);
                    ivUsernameArrow.setImageResource(R.drawable.arrow_up_60);
                }
                break;
            case R.id.iv_password_eye:
                if(ispasswordshow){
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD) ;//·
                    ivPasswordEye.setImageResource(R.drawable.eye_close_60);
                    ispasswordshow=false;
                }else{
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) ;//文字
                    ivPasswordEye.setImageResource(R.drawable.eye_open_60);
                    ispasswordshow=true;
                }
                break;
            case R.id.btn_register:
                Intent Iregister=new Intent(this, RegistActivity.class);
                startActivity(Iregister);
                break;
            default:
        }
    }
    private boolean checkNull() {
        if(TextUtil.isEmpty(etUsername.getText().toString())){
            ToastUtil.showToastShort("请输入用户名");
            return true;
        }
        if(TextUtil.isEmpty(etPassword.getText().toString())){
            ToastUtil.showToastShort("请输入密码");
            return true;
        }
        return false;
    }
    private void showListPopulWindow(final EditText edittext, final List<String> list){
        listPopupWindow.setAnchorView(edittext);
        listPopupWindow.setModal(true);
        arrayAdapter.notifyDataSetChanged();
        listPopupWindow.show();
    }
}
