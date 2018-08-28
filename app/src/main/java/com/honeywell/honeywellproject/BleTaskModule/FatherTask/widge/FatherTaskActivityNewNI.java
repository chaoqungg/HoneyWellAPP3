package com.honeywell.honeywellproject.BleTaskModule.FatherTask.widge;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.honeywell.honeywellproject.BaseActivity.ToolBarActivity;
import com.honeywell.honeywellproject.BleTaskModule.FatherTask.Event.FatherTaskSaveEvent;
import com.honeywell.honeywellproject.BleTaskModule.FatherTask.Event.FatherTaskSelectEvent;
import com.honeywell.honeywellproject.BleTaskModule.FatherTask.adapter.FatherTaskAdapterNewUI;
import com.honeywell.honeywellproject.BleTaskModule.FatherTask.data.FatherTaskBean;
import com.honeywell.honeywellproject.BleTaskModule.SingleAddress.SingleTaskAddressing.widge.SingleTaskAddressActivity;
import com.honeywell.honeywellproject.BleTaskModule.SonTask.data.SonTaskBean;
import com.honeywell.honeywellproject.BleTaskModule.SonTask.widge.SonTaskActivityNewUI;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.DialogUtil;
import com.honeywell.honeywellproject.Util.EventBusUtil;
import com.honeywell.honeywellproject.Util.LogUtil;
import com.honeywell.honeywellproject.Util.PhoneUtil;
import com.honeywell.honeywellproject.Util.SoftKeyBoardListener;
import com.honeywell.honeywellproject.Util.TextUtil;
import com.honeywell.honeywellproject.Util.ToastUtil;
import com.honeywell.honeywellproject.WidgeView.SwipeItemLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.honeywell.honeywellproject.Util.DialogUtil.showAlertDialog;

public class FatherTaskActivityNewNI extends ToolBarActivity {

    @BindView(R.id.btn_add_task)
    ImageView    btnAddTask;
    @BindView(R.id.rv_fathertask)
    RecyclerView rvFathertask;
    @BindView(R.id.tv_selectall)
    TextView     tvSelectall;
    @BindView(R.id.tv_delete)
    TextView     tvDelete;
    @BindView(R.id.view_empty)
    TextView     viewEmpty;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;

    private FatherTaskAdapterNewUI madapter;
    private List<FatherTaskBean> list = new ArrayList<FatherTaskBean>();
    private boolean openstate;
    private long    mExitTime;
    private boolean select = false;
    private String logininfo_id, project_id, controller_id;
    private SimpleDateFormat simpleDateFormat;
    private String addresstype="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_fathertask;
    }

    private void initView() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            logininfo_id = bundle.getString("logininfo_id");
            project_id = bundle.getString("project_id");
            controller_id = bundle.getString("controller_id");
            addresstype = bundle.getString("addresstype");
        }
        if("gongcheng".equals(addresstype)){
            getToolbarTitle().setText("工程编址回路组选择");
        }else{
            getToolbarTitle().setText("回路组");
        }
        getSubTitle().setText("编辑");
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
        getSubTitle().setVisibility(View.VISIBLE);
        getSubTitle().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(list.isEmpty()){
                    return;
                }
                if("保存".equals(getSubTitle().getText().toString())){
                    saveAll();
                    PhoneUtil.hideInputWindow(FatherTaskActivityNewNI.this,getSubTitle());
                }else {
                    if (madapter.KeyShow) {
                        return;
                    }
                    //点击编辑按钮
                    if (openstate) {
                        closeSlider();
                    } else {
                        openSlider();
                    }
                }
            }
        });
        rvFathertask.setLayoutManager(new LinearLayoutManager(this));
        madapter = new FatherTaskAdapterNewUI(list, this);
        rvFathertask.setAdapter(madapter);
        rvFathertask.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(this));
        madapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
                if(madapter.KeyShow ||  !"编辑".equals(getSubTitle().getText().toString())){
                    //如果键盘弹出，不可点击
                    return;
                }
                switch (view.getId()) {
                    case R.id.rl_fathertask_fold:
//                        if(TextUtil.isEmpty(list.get(position).getControllername())){
//                            return;
//                        }
                        if (openstate) {
                            closeSlider();
                        }
                        if("gongcheng".equals(addresstype)){
                            //工程编址
                            Intent intent = new Intent(FatherTaskActivityNewNI.this, SingleTaskAddressActivity.class);
                            intent.putExtra("logininfo_id", logininfo_id);
                            intent.putExtra("project_id", project_id);
                            intent.putExtra("controller_id", controller_id);
                            intent.putExtra("fathertask_id", list.get(position).getId() + "");
                            intent.putExtra("addresstype", addresstype);
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(FatherTaskActivityNewNI.this, SonTaskActivityNewUI.class);
                            intent.putExtra("logininfo_id", logininfo_id);
                            intent.putExtra("project_id", project_id);
                            intent.putExtra("controller_id", controller_id);
                            intent.putExtra("fathertask_id", list.get(position).getId() + "");
                            intent.putExtra("fathernumber", list.get(position).getTasknumber() + "");
                            intent.putExtra("fathertaskname", list.get(position).getTaskname());
                            intent.putExtra("fathertaskaddress", list.get(position).getTaskaddress());
                            startActivity(intent);
                        }
                        break;
                    case R.id.rl_fathertask_top:
//                        if(TextUtil.isEmpty(list.get(position).getControllername())){
//                            return;
//                        }
                        if (openstate) {
                            closeSlider();
                        }
                        if("gongcheng".equals(addresstype)){
                            //工程编址
                            Intent intent = new Intent(FatherTaskActivityNewNI.this, SingleTaskAddressActivity.class);
                            intent.putExtra("logininfo_id", logininfo_id);
                            intent.putExtra("project_id", project_id);
                            intent.putExtra("controller_id", controller_id);
                            intent.putExtra("fathertask_id", list.get(position).getId() + "");
                            intent.putExtra("addresstype", addresstype);
                            startActivity(intent);
                        }else {
                            Intent intent2 = new Intent(FatherTaskActivityNewNI.this, SonTaskActivityNewUI.class);
                            intent2.putExtra("logininfo_id", logininfo_id);
                            intent2.putExtra("project_id", project_id);
                            intent2.putExtra("controller_id", controller_id);
                            intent2.putExtra("fathertask_id", list.get(position).getId() + "");
                            intent2.putExtra("fathernumber", list.get(position).getTasknumber() + "");
                            intent2.putExtra("fathertaskname", list.get(position).getTaskname());
                            intent2.putExtra("fathertaskaddress", list.get(position).getTaskaddress());
                            startActivity(intent2);
                        }
                        break;
                    case R.id.delete:
                        int Size = list.get(position).getSontasklist().size();
                        if(list.get(position).getSontaskamount()>0){
                            DialogUtil.showAlertDialog(FatherTaskActivityNewNI.this, "提示", "这个回路组下包含有" + Size + "个探头或模块，你确定要执行删除操作吗？", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    delete(position);
                                    madapter.notifyItemRemoved(position);
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ((SwipeItemLayout)rvFathertask.getChildAt(position)).close();
                                }
                            });
                        }else{
                            delete(position);
                            madapter.notifyItemRemoved(position);
                        }
                        break;
                    default:
                }
            }
        });
        SoftKeyBoardListener.setListener(this.getWindow().getDecorView(), new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
//               // "键盘显示 高度" + height
                madapter.KeyShow=true;
            }

            @Override
            public void keyBoardHide(int height) {
                // "键盘隐藏 高度" + height
                madapter.KeyShow=false;
                saveAll();
            }
        });
    }
    /**
     * 点击保存或者键盘隐藏的时候自动保存
     * */
    private void saveAll(){
        if (ItemResponse != null) {

            if(ItemResponse.position>=list.size()){
                LogUtil.e(list.size()+"----"+ItemResponse.position+"");
            }else{
                if(TextUtil.isEmpty(ItemResponse.fatherDescription)){
                    list.get(ItemResponse.position).setToDefault("fatherdescription");
                    list.get(ItemResponse.position).setFatherdescription("");
                }else{
                    list.get(ItemResponse.position).setFatherdescription(ItemResponse.fatherDescription);
                }
                list.get(ItemResponse.position).setTasknumber(Integer.parseInt(ItemResponse.fatherName));
                list.get(ItemResponse.position).update(list.get(ItemResponse.position).getId());
            }
        }
        if(openstate){
            getSubTitle().setText("完成");
        }else{
            getSubTitle().setText("编辑");
        }
        madapter.notifyDataSetChanged();
    }
    private void initlist() {

        list.clear();
        List<FatherTaskBean> list2 = DataSupport.where("logininfo_id= ? and project_id = ? and controller_id= ?", logininfo_id, project_id, controller_id).find(FatherTaskBean.class);
        list.addAll(list2);
        for (int i = 0; i <list.size() ; i++) {
            list.get(i).setSontaskamount(list.get(i).getSontasklist().size());
        }
        DataSupport.saveAll(list);
        if(list.size()<=0){
            viewEmpty.setVisibility(View.VISIBLE);
        }else{
            for (int i = 0; i < list.size(); i++) {
                if(i != 0){
                    list.get(i).setIsexpand(false);
                }  else{
                    list.get(0).setIsexpand(true);
                }
            }
            viewEmpty.setVisibility(View.GONE);
        }
        madapter.notifyDataSetChanged();
    }

    @OnClick({R.id.btn_add_task, R.id.tv_selectall, R.id.tv_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add_task:
                FatherTaskBean fb = new FatherTaskBean();
                if (list.size() <= 0 || list.isEmpty()) {
                    fb.setTasknumber(1);
                    viewEmpty.setVisibility(View.GONE);
                } else {
                    fb.setTasknumber(list.get(list.size() - 1).getTasknumber() + 1);
                }
                fb.setTaskdate(simpleDateFormat.format(new Date()));
                fb.setLogininfo_id(logininfo_id);
                fb.setProject_id(project_id);
                fb.setController_id(controller_id);
                fb.setProgressnpercent("0.00%");
//                fb.setModule("0");
//                fb.setProbe("0");
                fb.save();//添加一条任务保存一条。
                list.add(fb);
                if(list.size()>=2){
                    list.get(list.size()-2).setIsexpand(false);
                }
                list.get(list.size()-1).setIsexpand(true);
                madapter.notifyDataSetChanged();
                rvFathertask.smoothScrollToPosition(list.size() - 1);
                break;
            case R.id.tv_selectall:
                if (tvSelectall.getText().equals("全选")) {
                    select = true;
                    tvSelectall.setText("取消");
                } else {
                    select = false;
                    tvSelectall.setText("全选");
                }
                for (FatherTaskBean bean : list) {
                    bean.setIsselect(select);
                }
                madapter.notifyDataSetChanged();
                break;
            case R.id.tv_delete:
                showAlertDialog(this, "删除回路组", "您确定要删除所选回路吗？",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                List<Integer> listindex = new ArrayList<Integer>();
                                for (int j = 0; j < list.size(); j++) {
                                    if (list.get(j).isselect()) {
                                        listindex.add(j);
                                    }
                                }
                                if (listindex.size() <= 0 || listindex.isEmpty()) {
                                    ToastUtil.showToastShort("未选择回路");
                                    return;
                                }
                                for (int j = listindex.size() - 1; j >= 0; j--) {
                                    delete(listindex.get(j));
                                }
                                madapter.notifyDataSetChanged();
                                ToastUtil.showToastShort("已经删除");
                                if (list.size() <= 0) {
                                    viewEmpty.setVisibility(View.VISIBLE);
                                    getSubTitle().setText("编辑");
                                    closeSlider();
                                    openstate = false;
                                    tvSelectall.setVisibility(View.INVISIBLE);
                                    tvDelete.setVisibility(View.INVISIBLE);
                                    tvSelectall.setClickable(false);
                                    tvDelete.setClickable(false);
                                }
                            }
                        }, null);
                break;
            default:
        }
    }
    private void delete(int id){
        //① 第一步删除所选项目的所有子任务
        //select("id")的目的是判断是否有这个表，以防还没建立过子任务，没有这个表，删除操作报错
        List<SonTaskBean> sonList = DataSupport.select("id").find(SonTaskBean.class);
        if (sonList != null && sonList.size() > 0) {
            DataSupport.deleteAll(SonTaskBean.class, "logininfo_id = ? and project_id = ? and controller_id = ? and fathertask_id = ?",
                    logininfo_id, project_id, controller_id, String.valueOf(list.get(id).getId()));
        }

        //② 第二步删除所选父任务
        DataSupport.deleteAll(FatherTaskBean.class, "logininfo_id = ? and project_id = ? and controller_id = ? and id = ?",
                logininfo_id, project_id, controller_id, String.valueOf(list.get(id).getId()));
        list.remove(id);
    }
    /**
     * From: FatherTaskAdapterNewUI
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventCheckBoxSelect(FatherTaskSelectEvent response) {
        list.get(response.position).setIsselect(response.checked);
    }
    /**
     * From: FatherTaskAdapterNewUI 出现保存按钮
     */
    private FatherTaskSaveEvent ItemResponse;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventSave(FatherTaskSaveEvent response) {
        ItemResponse=response;
        getSubTitle().setText("保存");
    }
    private void closeSlider() {
        getSubTitle().setText("编辑");
        madapter.slideClose();
        openstate = false;
        tvSelectall.setVisibility(View.INVISIBLE);
        tvDelete.setVisibility(View.INVISIBLE);
        tvSelectall.setClickable(false);
        tvDelete.setClickable(false);
    }
    private void openSlider() {
        getSubTitle().setText("完成");
        madapter.slideOpen();
        openstate = true;
        tvSelectall.setVisibility(View.VISIBLE);
        tvDelete.setVisibility(View.VISIBLE);
        tvSelectall.setClickable(true);
        tvDelete.setClickable(true);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ClickNavigation();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void ClickNavigation() {
        if (openstate) {
            closeSlider();
        }
        super.ClickNavigation();
    }
    @Override
    protected void onResume() {
        super.onResume();
        initlist();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBusUtil.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
    }

}
