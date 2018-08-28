package com.honeywell.honeywellproject.BleTaskModule.SonTask.widge;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.honeywell.honeywellproject.BaseActivity.ToolBarActivity;
import com.honeywell.honeywellproject.BleTaskModule.AddressDownLoad.widge.AddressDownLoadActivityNewUI;
import com.honeywell.honeywellproject.BleTaskModule.QrCode.CaptureActivity;
import com.honeywell.honeywellproject.BleTaskModule.SonTask.Event.SonTaskSaveEvent;
import com.honeywell.honeywellproject.BleTaskModule.SonTask.Event.SonTaskSelectEvent;
import com.honeywell.honeywellproject.BleTaskModule.SonTask.adapter.SonTaskAdapterNewUI;
import com.honeywell.honeywellproject.BleTaskModule.SonTask.data.SonTaskBean;
import com.honeywell.honeywellproject.R;
import com.honeywell.honeywellproject.Util.EventBusUtil;
import com.honeywell.honeywellproject.Util.PhoneUtil;
import com.honeywell.honeywellproject.Util.SoftKeyBoardListener;
import com.honeywell.honeywellproject.Util.TextUtil;
import com.honeywell.honeywellproject.Util.ToastUtil;
import com.honeywell.honeywellproject.WidgeView.SwipeItemLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.honeywell.honeywellproject.Util.DialogUtil.showAlertDialog;

public class SonTaskActivityNewUI extends ToolBarActivity {

    @BindView(R.id.view_empty)
    TextView     viewEmpty;
    @BindView(R.id.rv_sontask)
    RecyclerView rv_sontask;
    @BindView(R.id.tv_selectall)
    TextView     tvSelectall;
    @BindView(R.id.tv_delete)
    TextView     tvDelete;
    @BindView(R.id.ll_bottom1)
    LinearLayout llBottom1;
    @BindView(R.id.ll_bottom2)
    LinearLayout llBottom2;
    private LinearLayoutManager llm;
    private SonTaskAdapterNewUI madapter;
    private List<SonTaskBean> list = new ArrayList<SonTaskBean>();
    boolean openstate;
    boolean select = false;
    private AlphaAnimation mShowAnim, mHiddenAmin;//控件的显示和隐藏动画
    private boolean add;//点击加号则为true，用以列表滚动时标志状态
    private String  fathernumber = "-/-", fathertaskname, fathertaskaddress;
    private String logininfo_id, project_id, controller_id, fathertask_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_sontask_newui;
    }

    private void initView() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            logininfo_id = bundle.getString("logininfo_id");
            project_id = bundle.getString("project_id");
            controller_id = bundle.getString("controller_id");
            fathertask_id = bundle.getString("fathertask_id");
            fathernumber = bundle.getString("fathernumber");
            fathertaskname = bundle.getString("fathertaskname");
            fathertaskaddress = bundle.getString("fathertaskaddress");
        }
        getToolbarTitle().setText("回路序列号编址");
        getSubTitle().setVisibility(View.VISIBLE);
        getSubTitle().setText("编辑");
        getSubTitle().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(list.isEmpty()){
                    return;
                }
                if("保存".equals(getSubTitle().getText().toString())){
                    saveAll();
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
        llm =  new LinearLayoutManager(this);
        rv_sontask.setLayoutManager(llm);
        madapter = new SonTaskAdapterNewUI(list, this);
        rv_sontask.setAdapter(madapter);
        rv_sontask.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(this));
        madapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
                if(madapter.KeyShow ||  !"编辑".equals(getSubTitle().getText().toString())){
                    //如果键盘弹出，不可点击
                    return;
                }
                switch (view.getId()) {
                    case R.id.delete:
                        RecyclerView.ViewHolder vh =  (RecyclerView.ViewHolder)(madapter.getViewHolderMap().get(position));
                        final SwipeItemLayout sl=(SwipeItemLayout)vh.itemView.findViewById(R.id.swipeitemlayout);
                        sl.close();
                        delete(position);
                        madapter.notifyItemRemoved(position);
                        break;
                    case R.id.iv_saomiao:
                        if(list.get(position).isProcess()){
                           return;
                        }
                        //序列号空允许扫描   地址空则不允许扫描
                        if(list.get(position).getTaskdigitaladdress()==-1){
                            ToastUtil.showToastShort("地址不允许空");
                            rv_sontask.smoothScrollToPosition(position);
                            list.get(position).setIsexpand(true);
                            madapter.notifyItemChanged(position);
                            rv_sontask.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    RecyclerView.ViewHolder vh =  (RecyclerView.ViewHolder)(madapter.getViewHolderMap().get(position));
                                    final EditText et=(EditText)vh.itemView.findViewById(R.id.tv_addr2);
                                    et.setFocusable(true);
                                    et.setFocusableInTouchMode(true);
                                    et.requestFocus();
                                    PhoneUtil.showInputWindow(SonTaskActivityNewUI.this,et);
                                }
                            }, 200);
                            return;
                        }
                        //地址序列号重复或者地址空则不允许扫描
                        if(checkRepeat(list.get(position).getTaskserialnumber(),list.get(position).getTaskdigitaladdress()==-1 ? "" : list.get(position).getTaskdigitaladdress()+"",position)){
                            return;
                        }
                        Intent intent = new Intent(SonTaskActivityNewUI.this, CaptureActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("saomiaotype", "1");
                        bundle.putString("fathertask_id", fathertask_id);
                        bundle.putString("logininfo_id", logininfo_id);
                        bundle.putString("project_id", project_id);
                        bundle.putString("controller_id", controller_id);
                        bundle.putString("position", position + "");
                        intent.putExtras(bundle);
                        startActivity(intent);
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
    boolean Repeat;
    private void saveAll() {
        if (ItemResponse != null) {
            if(checkRepeat(ItemResponse.sonSeries,ItemResponse.sonAddr,ItemResponse.position)){
                PhoneUtil.showInputWindow(SonTaskActivityNewUI.this,getSubTitle());
                return;
            }
            if (ItemResponse.position < list.size()) {
                //因为不知何原因 若taskserialnumber为空，则数据库保存不成功，所以暂时绕过
                if(TextUtil.isEmpty(ItemResponse.sonSeries)){
                    list.get(ItemResponse.position).setToDefault("taskserialnumber");
                    list.get(ItemResponse.position).setTaskserialnumber("");
                }else{
                    list.get(ItemResponse.position).setTaskserialnumber(ItemResponse.sonSeries);
                }
                if (TextUtil.isEmpty(ItemResponse.sonAddr)) {
                    list.get(ItemResponse.position).setTaskdigitaladdress(-1);
                } else {
                    list.get(ItemResponse.position).setTaskdigitaladdress(Integer.parseInt(ItemResponse.sonAddr));
                }
                if(TextUtil.isEmpty(ItemResponse.sonDescription)){
                    list.get(ItemResponse.position).setToDefault("sondescription");
                    list.get(ItemResponse.position).setSondescription("");
                }else{
                    list.get(ItemResponse.position).setSondescription(ItemResponse.sonDescription);
                }
                list.get(ItemResponse.position).update(list.get(ItemResponse.position).getId());
            }
            if (openstate) {
                getSubTitle().setText("完成");
            } else {
                getSubTitle().setText("编辑");
            }
            madapter.notifyDataSetChanged();
        }
        PhoneUtil.hideInputWindow(SonTaskActivityNewUI.this,getSubTitle());
    }
private boolean checkRepeat(String Series,String sonAddr,int position){
    //查重
    Repeat = false;
    int id = 0;
    for (SonTaskBean bean : list) {
        id++;
        if(id-1==position){
            continue;
        }
        if (!TextUtil.isEmpty(Series) &&bean.getTaskserialnumber().equals(Series)) {
            ToastUtil.showToastShort("序列号重复");
            return  Repeat = true;
        }
        if (!TextUtil.isEmpty(sonAddr) && bean.getTaskdigitaladdress() == Integer.parseInt(sonAddr)) {
            ToastUtil.showToastShort("地址重复");
            return Repeat = true;
        }
    }
    return Repeat;
}
    private int checkNull(String type){
        int id=0;
        if("addr".equals(type)){
            for (SonTaskBean bean:list) {
                if(bean.getTaskdigitaladdress()==-1) {
                    ToastUtil.showToastShort("地址不允许空");
                    return id;
                }
                id++;
            }
        }else if("series".equals(type)){
            for (SonTaskBean bean:list) {
                if(TextUtil.isEmpty(bean.getTaskserialnumber())) {
                    ToastUtil.showToastShort("序列号不允许空");
                    return id;
                }else if(bean.getTaskserialnumber().length()>=10 &&
                        Long.parseLong(bean.getTaskserialnumber())>4294967295L) {
                    ToastUtil.showToastShort("序列号大小超限");
                    return id;
                }
                id++;
            }
        }else{
            for (SonTaskBean bean:list) {
                if(bean.getTaskdigitaladdress()==-1) {
                    ToastUtil.showToastShort("地址不允许空");
                    return id;
                }
                if(TextUtil.isEmpty(bean.getTaskserialnumber())) {
                    ToastUtil.showToastShort("序列号不允许空");
                    return id;
                }
                id++;
            }
        }
        return -1;
    }
    private void initlist() {
        list.clear();
        List<SonTaskBean> list2 = DataSupport.where("logininfo_id= ? and project_id = ? and controller_id= ? and fathertask_id= ?", logininfo_id, project_id, controller_id, fathertask_id).find(SonTaskBean.class);
        list.addAll(list2);
        if(list.size()<=0){
            viewEmpty.setVisibility(View.VISIBLE);
        }else{
            list.get(0).setIsexpand(true);
            for (int i = 1; i < list.size(); i++) {
                    list.get(i).setIsexpand(false);
            }
            viewEmpty.setVisibility(View.GONE);
            //扫描成功后返回扫描界面会默认编号+1，但如果这条编号用户不想要，也没有进行输入，则再次返回Sontask界面后将其进行删除
            if(TextUtil.isEmpty(list.get(list.size()-1).getTaskserialnumber())){
                DataSupport.delete(SonTaskBean.class,list.get(list.size()-1).getId());
                list.remove(list.size()-1);
            }
        }
        madapter.notifyDataSetChanged();
    }

    @OnClick({ R.id.tv_selectall, R.id.tv_delete, R.id.ll_bianzhi, R.id.ll_saomiao})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_selectall:
                if (tvSelectall.getText().equals("全选")) {
                    select = true;
                    tvSelectall.setText("取消");
                } else {
                    select = false;
                    tvSelectall.setText("全选");
                }
                for (SonTaskBean bean : list) {
                    bean.setIsselect(select);
                }
                madapter.notifyDataSetChanged();
                break;
            case R.id.tv_delete:
                showAlertDialog(this, "删除子任务", "您确定要删除所选任务吗？",
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
                                    ToastUtil.showToastShort("未选择任务");
                                    return;
                                }
                                for (int j = listindex.size() - 1; j >= 0; j--) {
                                    delete(listindex.get(j));
                                }
                                madapter.notifyDataSetChanged();
                                ToastUtil.showToastShort("已经删除");
                                    if (list.size() <= 0) {
                                        viewEmpty.setVisibility(View.VISIBLE);
                                        closeSlider();
                                }
                            }
                        }, null);
                break;
            case R.id.ll_bianzhi:
                if (list.isEmpty()) {
                    return;
                }
                //如果序列号或者地址信息为空则无法进行编址操作
                final int id2=checkNull("series");
                if(id2==-1){
                    //无空，再判断地址
                }else{
                    rv_sontask.smoothScrollToPosition(id2);
                    list.get(id2).setIsexpand(true);
                    madapter.notifyItemChanged(id2);
                    rv_sontask.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            RecyclerView.ViewHolder vh =  (RecyclerView.ViewHolder)(madapter.getViewHolderMap().get(id2));
                            final EditText et=(EditText)vh.itemView.findViewById(R.id.tv_series2);
                            et.setFocusable(true);
                            et.setFocusableInTouchMode(true);
                            et.requestFocus();
                            PhoneUtil.showInputWindow(SonTaskActivityNewUI.this,et);
                        }
                    }, 200);
                    return;
                }

                final  int id1=checkNull("addr");
                if(id1==-1){
                    //无空，跳转编址
                    Intent intent = new Intent(SonTaskActivityNewUI.this, AddressDownLoadActivityNewUI.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("fathertask_id", fathertask_id);
                    bundle.putString("logininfo_id", logininfo_id);
                    bundle.putString("project_id", project_id);
                    bundle.putString("controller_id", controller_id);
                    bundle.putString("fathernumber", fathernumber);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    rv_sontask.smoothScrollToPosition(id1);
                    list.get(id1).setIsexpand(true);
                    madapter.notifyItemChanged(id1);
                    rv_sontask.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            RecyclerView.ViewHolder vh =  (RecyclerView.ViewHolder)(madapter.getViewHolderMap().get(id1));
                            final EditText et=(EditText)vh.itemView.findViewById(R.id.tv_addr2);
                            et.setFocusable(true);
                            et.setFocusableInTouchMode(true);
                            et.requestFocus();
                            PhoneUtil.showInputWindow(SonTaskActivityNewUI.this,et);
                        }
                    }, 200);
                }
                break;
            case R.id.ll_saomiao:
                //序列号空允许新增扫描   地址空则不允许新增扫描
                final  int ida=checkNull("addr");
                if(ida!=-1){
                    rv_sontask.smoothScrollToPosition(ida);
                    list.get(ida).setIsexpand(true);
                    madapter.notifyItemChanged(ida);
                    rv_sontask.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            RecyclerView.ViewHolder vh =  (RecyclerView.ViewHolder)(madapter.getViewHolderMap().get(ida));
                            final EditText et=(EditText)vh.itemView.findViewById(R.id.tv_addr2);
                            et.setFocusable(true);
                            et.setFocusableInTouchMode(true);
                            et.requestFocus();
                            PhoneUtil.showInputWindow(SonTaskActivityNewUI.this,et);
                        }
                    }, 200);
                    return;
                }
                for (int i = 0; i <list.size() ; i++) {
                    if(checkRepeat(list.get(i).getTaskserialnumber(),list.get(i).getTaskdigitaladdress()+"",i)){
                        //序列号或者地址重复
                        rv_sontask.smoothScrollToPosition(i);
                        return;
                    }
                }
                if(list.size()==239){
                    ToastUtil.showToastShort("地址达到上限");
                    return;
                }
                addTask();
                Intent intent = new Intent(SonTaskActivityNewUI.this, CaptureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("saomiaotype", "0");
                bundle.putString("fathertask_id", fathertask_id);
                bundle.putString("logininfo_id", logininfo_id);
                bundle.putString("project_id", project_id);
                bundle.putString("controller_id", controller_id);
                bundle.putString("position", list.size()-1+"");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            default:
        }
    }

    private void addTask() {
        SonTaskBean sb = new SonTaskBean();
        if (list.size() <= 0 || list.isEmpty()) {
            sb.setTasknumber(1);
            sb.setTaskdigitaladdress(001);
            viewEmpty.setVisibility(View.GONE);
        } else {
            sb.setTaskdigitaladdress(list.get(list.size() - 1).getTaskdigitaladdress() + 1);
            sb.setTasknumber(list.get(list.size() - 1).getTasknumber() + 1);
            //编号不可修改所以不可能重复，检查数字地址重复问题
            for (SonTaskBean bean : list) {
                if (bean.getTaskdigitaladdress() == sb.getTaskdigitaladdress()) {
                    sb.setTaskdigitaladdress(sb.getTaskdigitaladdress() + 1);
                }
            }
        }
        sb.setLogininfo_id(logininfo_id);
        sb.setProject_id(project_id);
        sb.setController_id(controller_id);
        sb.setFathertask_id(fathertask_id);
        sb.save();
        list.add(sb);
    }
    private void delete(int id){
        //删除所选子任务
        DataSupport.deleteAll(SonTaskBean.class, "logininfo_id = ? and project_id = ? and controller_id = ? and fathertask_id = ? and id = ?",
                logininfo_id, project_id, controller_id, fathertask_id, String.valueOf(list.get(id).getId()));
        list.remove(id);
    }
    /**
     * From: SonTaskAdapterNewUI
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventCheckBoxSelect(SonTaskSelectEvent response) {
        list.get(response.position).setIsselect(response.checked);
    }
    /**
     * From: Adapter 出现保存按钮
     */
    private SonTaskSaveEvent ItemResponse;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventSave(SonTaskSaveEvent response) {
        ItemResponse=response;
        getSubTitle().setText("保存");
    }
    private void closeSlider() {
        getSubTitle().setText("编辑");
        madapter.slideClose();
        openstate = false;
        llBottom2.setVisibility(View.GONE);
    }
    private void openSlider() {
        getSubTitle().setText("完成");
        madapter.slideOpen();
        openstate = true;
        llBottom2.setVisibility(View.VISIBLE);
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
