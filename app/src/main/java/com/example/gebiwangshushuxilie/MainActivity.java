package com.example.gebiwangshushuxilie;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.example.gebiwangshushuxilie.service.KeepAliveService;
import com.example.gebiwangshushuxilie.util.CipherUtils;
import com.example.gebiwangshushuxilie.util.HttpClient;
import com.example.gebiwangshushuxilie.util.NotificationSetUtil;
import com.example.gebiwangshushuxilie.util.UpdateApk;
import com.example.gebiwangshushuxilie.util.WindowPermissionCheck;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


/**
 * 佣金支持卡小数点
 * 停止接单取消所有网络请求
 * 远程公告、频率等
 * try catch
 * 多买号情况下，不选择买号接单的问题
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etUname,etPaw;
    private TextView tvStart,tvStop,tvLog,tvBrow,tvGetTitle,tvTitle,tvAppDown,tvAppOpen;
    private Handler mHandler;
    /*
    接单成功音乐提示播放次数（3次）
    播放的次数是count+1次
     */
    private int count;
    private SharedPreferences userInfo;
    private int minPl;
    private String cookie;
    private Dialog dialog;
    private static String LOGIN_URL = "";
    private static String DOWNLOAD = "";
    private static String BROW_OPEN = "";
    private boolean isStart;
    private boolean tbIsStart;
    private boolean jdIsStart;

    private String todayCount;
    private String theWeekCount;
    private String theMonthCount;


    private static final String LOGIN = "/index/Apprentice/getlogin.html";
    private static final String IS_EXIXT_TASK = "/public/index.php/index/Apprentice/receive_task";

    private static final String GET_TAOBAO_TASK = "/public/index.php/index/Apprentice/pickup_task";
    private static final String GET_JINGDONG_TASK = "/public/index.php/index/jdapprentice/jd_obtain";

    private static final String JINGDONG_LQ_TASK = "/index/Jdapprentice/jreceive_btn_task.html";
    private static final String TAOBAO_LQ_TASK = "/index/Apprentice/cfreceive_btn_task.html";
    //隔天任务领取任务网址             隔天任务参数是sontaskId             /index/browse/browse_receive_task.html
    //隔天任务image地址   /index/browse/browse_referring.html?sontaskId=38734
    ///public/index.php/index/Browse/browse_obtain?t=0.8302394486326041 隔天任务接取请求？

    private static final String TB_GET_IMAGE = "/Index/apprentice/referring_task.html";
    private static final String JD_GET_IMAGE = "/index/Jdapprentice/jreferring_task.html";


    /**
     * 需要更改的地方：
     * 1、MainActivity
     * 2、build.gradle配置文件
     * 3、AndroidMainfest.xml文件
     * 4、Update文件
     * 5、KeepAlive文件
     */
    private static final String PT_NAME = "geBiWangShuShu";
    private static final String TITLE = "隔壁王叔叔助手";
    private static String TI_SHI = "隔壁王叔叔App未安装";
    private static final String SUCCESS_TI_SHI = "隔壁王叔叔接单成功";
    private static final String CHANNELID = "gbwssSuccess";
    private static String APK_PACKAGE = "com.lzm.gbwss";
    private static int ICON = R.mipmap.gbwss;
    private static final int JIE_DAN_SUCCESS = R.raw.gbwss_success;
    private static final int JIE_DAN_FAIL = R.raw.gbwss_fail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, KeepAliveService.class);
        startService(intent);//启动保活服务
        ignoreBatteryOptimization();//忽略电池优化
        if(!checkFloatPermission(this)){
            //权限请求方法
            requestSettingCanDrawOverlays();
        }
        initView();
    }


    private void initView(){
        //检查更新
        UpdateApk.update(MainActivity.this);
        //是否开启通知权限
        openNotification();
        //是否开启悬浮窗权限
        WindowPermissionCheck.checkPermission(this);
        //获取平台地址
        getPtAddress();
        mHandler = new Handler();
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(TITLE);
        tvBrow = findViewById(R.id.tv_brow);
        etUname = findViewById(R.id.et_username);
        etPaw = findViewById(R.id.et_password);
        tvAppDown = findViewById(R.id.tv_appDown);
        tvAppOpen = findViewById(R.id.tv_appOpen);
        tvStart = findViewById(R.id.tv_start);
        tvStop = findViewById(R.id.tv_stop);
        tvLog = findViewById(R.id.tv_log);
        getUserInfo();//读取用户信息
        //设置textView为可滚动方式
        tvLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        tvLog.setTextIsSelectable(true);
        tvStart.setOnClickListener(this);
        tvStop.setOnClickListener(this);
        tvBrow.setOnClickListener(this);
        tvAppOpen.setOnClickListener(this);
        tvAppDown.setOnClickListener(this);
        tvGetTitle = findViewById(R.id.tv_getTitle);
        tvGetTitle.setOnClickListener(this);
        tvLog.setText("建议绑定淘宝和京东账号~"+"\n");
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_start:
                isStart = true;
                tbIsStart = true;
                jdIsStart = true;
                cookie = "";
                /*
                先清除掉之前的Handler中的Runnable，不然会和之前的任务一起执行多个
                 */
                mHandler.removeCallbacksAndMessages(null);
                if(LOGIN_URL == ""){
                    tvLog.setText("获取最新网址中,请3秒后重试...");
                }else {
                    userLogin(etUname.getText().toString().trim(),etPaw.getText().toString().trim(),"login");
                }
                break;
            case R.id.tv_stop:
                stop();
                break;
            case R.id.tv_brow:
                browOpen();
                break;

            case R.id.tv_appDown:
                if(DOWNLOAD == ""){
                    tvLog.setText("获取最新网址中,请3秒后重试...");
                    return;
                }
                Uri uri = Uri.parse(DOWNLOAD);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.tv_appOpen:
                openApp(APK_PACKAGE);
                break;

            case R.id.tv_getTitle:
                if(LOGIN_URL == ""){
                    tvLog.setText("获取最新网址中,请3秒后重试...");
                }else {
                    cookie = "";
                    userLogin(etUname.getText().toString().trim(),etPaw.getText().toString().trim(),"getShopTitle");
                }
                break;
        }
    }


    /**
     * 弹窗公告
     */
    public void announcementDialog(String[] lesson){

        dialog = new AlertDialog
                .Builder(this)
                .setTitle("公告")
                .setCancelable(false) //触摸窗口边界以外是否关闭窗口，设置 false
                .setPositiveButton("我知道了", null)
                //.setMessage("")
                .setItems(lesson,null)
                .create();
        dialog.show();
    }


    private void browOpen(){
        if(BROW_OPEN == "") {
            tvLog.setText("获取最新网址中,请3秒后重试...");
        }
        Uri uri = Uri.parse(BROW_OPEN);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }


    /**
     * 重写activity的onKeyDown方法，点击返回键后不销毁activity
     * 可参考：https://blog.csdn.net/qq_36713816/article/details/71511860
     * 另外一种解决办法：重写onBackPressed方法，里面不加任务内容，屏蔽返回按钮
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }




    /**
     * 用户登录
     * @param username
     * @param password
     */
    private void userLogin(String username, String password,String mark){

        tvLog.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()) + ": 正在登陆中..."+"\n");

        HttpClient.getInstance().post(LOGIN, LOGIN_URL)
                .params("userName", username)
                .params("password", password)
                .params("user_os", "windows")
                .params("user_model", "chrome")
                .params("user_code", "b4431ab36e95278dd695d8ac3a4274df")
                .params("user_identifying", "76c31cb90b00c1ce5f4232e55d72c4da")
                .params("ip", "111.17.70.38")
                .params("ip_data", "中国山东济南历城区")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            /**
                             * {"msg":"账号不存在","status":300}
                             * {"msg":"密码出错","errorStatus":101,"status":300}
                             * {"msg":"登陆成功！","status":200,"login_sign":1,"apprenticeId":78534,"identity_sign":2,
                             * "receiving_sign":0,"user_sjcode":"76c31cb90b00c1ce5f4232e55d72c4da"}
                             */
                            JSONObject obj = JSONObject.parseObject(response.body());
                            //登录成功
                            if(200 == obj.getInteger("status")){
                                saveUserInfo(username,password);
                                sendLog(obj.getString("msg"));
                                List<String> list = response.headers().values("Set-Cookie");
                                for (String str : list) {
                                    cookie += str.substring(0, str.indexOf(";")) + "; ";
                                }
                                isExixtTask(mark);
                                return;
                            }
                            sendLog(obj.getString("msg"));
                        }catch (Exception e){
                            sendLog("登录："+e.getMessage());
                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        sendLog("登录ERR："+response.getException());
                    }
                });
    }


    private void isExixtTask(String biaoZhi) {
        HttpClient.getInstance().get(IS_EXIXT_TASK, LOGIN_URL)
                .headers("X-Requested-With","XMLHttpRequest")
                .headers("Cookie",cookie)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            Object o = JSONObject.parse(response.body());
                            Document document = Jsoup.parse(o.toString());
                            String title = document.title();
                            if("login".equals(biaoZhi)){
                                if("任务大厅".equals(title)){
                                    List tb = document.select("div[class=display-flexbox tb_task_wrapp]").select("em").textNodes();
                                    List jd = document.select("div[class=display-flexbox jd_task_wrapp]").select("em").textNodes();
                                    if(todayCount.equals(tb.get(0).toString())){
                                        tbIsStart = false;
                                        sendLog("淘宝日已接满");
                                    }else if(theWeekCount.equals(tb.get(1).toString())){
                                        tbIsStart = false;
                                        sendLog("淘宝周已接满");
                                    }else if(theMonthCount.equals(tb.get(2).toString())){
                                        tbIsStart = false;
                                        sendLog("淘宝月已接满");
                                    }

                                    if(todayCount.equals(jd.get(0).toString())){
                                        jdIsStart = false;
                                        sendLog("京东日已接满");
                                    }else if(theWeekCount.equals(jd.get(1).toString())){
                                        jdIsStart = false;
                                        sendLog("京东周已接满");
                                    }else if(theMonthCount.equals(jd.get(2).toString())){
                                        jdIsStart = false;
                                        sendLog("京东月已接满");
                                    }
                                    getAllTask();
                                }else if("领取任务".equals(title)){
                                    playMusic(JIE_DAN_SUCCESS,3000,2);
                                    String img = document.select("img[class=main_link]").attr("src");
                                    String guanJianZi = document.select("input[class=key_word_hidden]").attr("value");
                                    sendLog2("-------------------------");
                                    sendLog2("搜索关键字："+guanJianZi);
                                    sendLog2("-------------------------");
                                    sendLog2("商品图："+img);
                                }else {
                                    sendLog("未知标题，请截图联系软件开发者");
                                }
                            }else {
                                if("任务大厅".equals(title)){
                                    sendLog("无可操作任务，请先领取任务！");
                                }else if("领取任务".equals(title)){
                                    String img = document.select("img[class=main_link]").attr("src");
                                    String guanJianZi = document.select("input[class=key_word_hidden]").attr("value");
                                    sendLog2("-------------------------");
                                    sendLog2("搜索关键字："+guanJianZi);
                                    sendLog2("-------------------------");
                                    sendLog2("商品图："+img);
                                }else {
                                    sendLog("未知标题，请截图联系软件开发者");
                                }
                            }
                        }catch (Exception e){
                            sendLog("是否存在任务："+e.getMessage());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }

                });
    }



    private void getAllTask(){
        if(tbIsStart)
            getTaoBaoTask();
        if (jdIsStart)
            getJongDongTask();
    }


    private void getTaoBaoTask() {
        HttpClient.getInstance().get(GET_TAOBAO_TASK, LOGIN_URL)
                .params("t", Math.random())
                .headers("X-Requested-With","XMLHttpRequest")
                .headers("Cookie",cookie)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            /**
                             * {"status":300,"errStatus":118,"msg":"没有任务"}
                             * {"status":300,"errorStatus":115,"msg":"没有任务"}
                             * {"status":200,"task":"\/Index\/Apprentice\/wangwang_info","stop":1,"msg":"旺旺降权"}
                             *{"status":200,"task":"\/Index\/Apprentice\/obta_task?sontaskId=15190557","stop":1}
                             *
                             */
                            JSONObject o = JSONObject.parseObject(response.body());
                            if(200 == o.getInteger("status")){
                                if(o.containsKey("msg")){
                                    sendLog(o.getString("msg"));
                                    tbIsStart = false;
                                }else {
                                    int index = o.getString("task").indexOf("=");
                                    String orderId = o.getString("task").substring(index+1);
                                    //存在任务在领取任务会报   任务不在领取状态
                                    tbLqTask(orderId);
                                    isStart = false;
                                }
                                return;
                            }
                            sendLog("淘宝："+o.getString("msg"));
                        }catch (Exception e){
                            sendLog("淘宝："+e.getMessage());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        jieDan();
                    }
                });
    }

    private void getJongDongTask() {
        HttpClient.getInstance().get(GET_JINGDONG_TASK, LOGIN_URL)
                .params("t", Math.random())
                .headers("X-Requested-With","XMLHttpRequest")
                .headers("Cookie",cookie)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            /**
                             *
                             * {"status":300,"msg":"没有任务"}      设置了不再接取京东任务后
                             * {"status":300,"errStatus":11811111,"msg":"没有任务"}
                            评价： {"status":200,"task":"/index/Apprentice/evaluate?sunsheet_id=427774","stop":1}
                            没绑定京东号： {"status":300,"task":"\/index\/Jdapprentice","errStatus":10001}
                             京东订单是6位数
                             */
                            JSONObject o = JSONObject.parseObject(response.body());
                            if(200 == o.getInteger("status")){
                                if(o.containsKey("msg")){
                                    sendLog(o.getString("msg"));
                                }else if (o.getString("task").contains("sunsheet_id")){
                                    sendLog("请完成京东评价任务！");
                                }else {
                                    int index = o.getString("task").indexOf("=");
                                    String orderId = o.getString("task").substring(index+1);
                                    jdLqTask(orderId);
                                    isStart = false;
                                }
                                return;
                            }else if(o.containsKey("task")){
                                sendLog("京东："+o.getString("msg"));
                                jdIsStart = false;
                            }else {
                                sendLog("京东："+o.getString("msg"));
                            }
                        }catch (Exception e){
                            sendLog("京东："+e.getMessage());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if(!tbIsStart){
                            jieDan();
                        }
                    }
                });
    }



    private void jdLqTask(String orderId) {
        HttpClient.getInstance().post(JINGDONG_LQ_TASK, LOGIN_URL)
                .params("jsontaskId",orderId)
                .headers("X-Requested-With","XMLHttpRequest")
                .headers("Cookie",cookie)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            /**
                             * {"status":200,"msg":"领取成功"}
                             * {"status":300,"msg":"任务不在领取状态"}
                             */
                            JSONObject o = JSONObject.parseObject(response.body());
                            sendLog2("京东："+o.getString("msg"));
                            if(200 == o.getInteger("status")){
                                playMusic(JIE_DAN_SUCCESS,3000,2);
                                getJdImage(orderId);
                                return;
                            }
                        }catch (Exception e){

                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }

    private void tbLqTask(String orderId) {
        HttpClient.getInstance().post(TAOBAO_LQ_TASK, LOGIN_URL)
                .params("sontaskId",orderId)
                .headers("X-Requested-With","XMLHttpRequest")
                .headers("Cookie",cookie)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            /**
                             * {"status":200,"msg":"领取成功"}
                             * {"status":300,"msg":"任务不在领取状态"}
                             */
                            JSONObject o = JSONObject.parseObject(response.body());
                            sendLog2("淘宝："+o.getString("msg"));
                            if(200 == o.getInteger("status")){
                                playMusic(JIE_DAN_SUCCESS,3000,2);
                                getTbImage(orderId);
                                return;
                            }
                        }catch (Exception e){

                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }


    private void getTbImage(String orderId) {
        HttpClient.getInstance().get(TB_GET_IMAGE, LOGIN_URL)
                .params("sontaskId",orderId)
                .headers("X-Requested-With","XMLHttpRequest")
                .headers("Cookie",cookie)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            Object o = JSONObject.parse(response.body());
                            Document document = Jsoup.parse(o.toString());
                            String img = document.select("img[class=main_link]").attr("src");
                            String guanJianZi = document.select("input[class=key_word_hidden]").attr("value");

                            sendLog2("-------------------------");
                            sendLog2("搜索关键字："+guanJianZi);
                            sendLog2("-------------------------");
                            sendLog2("商品图："+img);
                        }catch (Exception e){
                            sendLog("获取商品详情："+e.getMessage());
                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }


    private void getJdImage(String orderId) {
        HttpClient.getInstance().get(JD_GET_IMAGE, LOGIN_URL)
                .params("jsontaskId",orderId)
                .headers("X-Requested-With","XMLHttpRequest")
                .headers("Cookie",cookie)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            Object o = JSONObject.parse(response.body());
                            Document document = Jsoup.parse(o.toString());
                            String img = document.select("img[class=main_link]").attr("src");
                            String guanJianZi = document.select("input[class=key_word_hidden]").attr("value");

                            sendLog2("-------------------------");
                            sendLog2("搜索关键字："+guanJianZi);
                            sendLog2("-------------------------");
                            sendLog2("商品图："+img);
                        }catch (Exception e){
                            sendLog("获取商品详情："+e.getMessage());
                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }




    private void jieDan(){
        if(isStart){
            mHandler.postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void run() {
                    getAllTask();
                }
            }, minPl);
        }
    }



    /**
     * 停止接单
     */
    public void stop(){
        OkGo.getInstance().cancelAll();
        //Handler中已经提供了一个removeCallbacksAndMessages去清除Message和Runnable
        mHandler.removeCallbacksAndMessages(null);
        sendLog("已停止接单");
    }


    public void getPtAddress(){
        HttpClient.getInstance().get("/ptVersion/checkUpdate","http://47.94.255.103")
                .params("ptName",PT_NAME)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject ptAddrObj = JSONObject.parseObject(response.body());
                            if(ptAddrObj == null){
                                Toast.makeText(MainActivity.this, "没有配置此平台更新信息！", Toast.LENGTH_LONG).show();
                                return;
                            }
                            LOGIN_URL = ptAddrObj.getString("ptUrl");
                            BROW_OPEN = ptAddrObj.getString("openUrl");
                            minPl = Integer.parseInt(ptAddrObj.getString("pinLv"));
                            DOWNLOAD = ptAddrObj.getString("apkDownload");
                            String[] jieDan = ptAddrObj.getString("apkVersion").split(",");
                            todayCount = jieDan[0];
                            theWeekCount = jieDan[1];
                            theMonthCount = jieDan[2];
                            //公告弹窗
                            String[] gongGao = ptAddrObj.getString("ptAnnoun").split(";");
                            announcementDialog(gongGao);
                        }catch (Exception e){
                            sendLog("获取网址："+e.getMessage());
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        sendLog("服务器出现问题啦~");
                    }
                });
    }



    private void openApp(String packName){
        PackageManager packageManager = this.getPackageManager();
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packName);
        List<ResolveInfo> apps = packageManager.queryIntentActivities(resolveIntent, 0);
        if (apps.size() == 0) {
            Toast.makeText(this, TI_SHI, Toast.LENGTH_LONG).show();
            return;
        }
        ResolveInfo resolveInfo = apps.iterator().next();
        if (resolveInfo != null) {
            String className = resolveInfo.activityInfo.name;
            Intent intent2 = new Intent(Intent.ACTION_MAIN);
            intent2.addCategory(Intent.CATEGORY_LAUNCHER);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName cn = new ComponentName(packName, className);
            intent2.setComponent(cn);
            this.startActivity(intent2);
        }
    }


    /**
     * 接单成功后通知铃声
     * @param voiceResId 音频文件
     * @param milliseconds 需要震动的毫秒数
     */
    private void playMusic(int voiceResId, long milliseconds,int total){

        count = total;//不然会循环播放

        //播放语音
        MediaPlayer player = MediaPlayer.create(MainActivity.this, voiceResId);
        player.start();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //播放完成事件
                if(count != 0){
                    player.start();
                }
                count --;
            }
        });

        //震动
        Vibrator vib = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
        //延迟的毫秒数
        vib.vibrate(milliseconds);
    }



    /**
     * 日志更新
     * @param log
     */
    public void sendLog(String log){
        scrollToTvLog();
        if(tvLog.getLineCount() > 40){
            tvLog.setText("");
        }
        tvLog.append(new SimpleDateFormat("HH:mm:ss").format(new Date()) + ": "+log+"\n");
        //如果日志大于100条，则清空
    }

    public void sendLog2(String log){
        scrollToTvLog();
        tvLog.append(new SimpleDateFormat("HH:mm:ss").format(new Date()) + ": "+log+"\n");
    }




    /**
     * 忽略电池优化
     */

    public void ignoreBatteryOptimization() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean hasIgnored = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasIgnored = powerManager.isIgnoringBatteryOptimizations(getPackageName());
            //  判断当前APP是否有加入电池优化的白名单，如果没有，弹出加入电池优化的白名单的设置对话框。
            if(!hasIgnored) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:"+getPackageName()));
                startActivity(intent);
            }
        }
    }


    private void openNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //判断是否需要开启通知栏功能
            NotificationSetUtil.OpenNotificationSetting(this);
        }
    }



    //权限打开
    private void requestSettingCanDrawOverlays() {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.O) {//8.0以上
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivityForResult(intent, 1);
        } else if (sdkInt >= Build.VERSION_CODES.M) {//6.0-8.0
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1);
        } else {//4.4-6.0以下
            //无需处理了
        }
    }




    //判断是否开启悬浮窗权限   context可以用你的Activity.或者tiis
    public static boolean checkFloatPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return true;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                Class cls = Class.forName("android.content.Context");
                Field declaredField = cls.getDeclaredField("APP_OPS_SERVICE");
                declaredField.setAccessible(true);
                Object obj = declaredField.get(cls);
                if (!(obj instanceof String)) {
                    return false;
                }
                String str2 = (String) obj;
                obj = cls.getMethod("getSystemService", String.class).invoke(context, str2);
                cls = Class.forName("android.app.AppOpsManager");
                Field declaredField2 = cls.getDeclaredField("MODE_ALLOWED");
                declaredField2.setAccessible(true);
                Method checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
                int result = (Integer) checkOp.invoke(obj, 24, Binder.getCallingUid(), context.getPackageName());
                return result == declaredField2.getInt(cls);
            } catch (Exception e) {
                return false;
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                if (appOpsMgr == null)
                    return false;
                int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), context
                        .getPackageName());
                return mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED;
            } else {
                return Settings.canDrawOverlays(context);
            }
        }
    }




    /**
     * 保存用户信息
     */
    private void saveUserInfo(String username,String password){
        userInfo = getSharedPreferences("userData", MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();//获取Editor
        //得到Editor后，写入需要保存的数据
        editor.putString("username",username);
        editor.putString("password", password);
        editor.commit();//提交修改

    }



    /**
     * 接单成功执行逻辑
     */
    protected void receiveSuccess(String bj,String yj){
        //前台通知的id名，任意
        String channelId = CHANNELID;
        //前台通知的名称，任意
        String channelName = "接单成功状态栏通知";
        //发送通知的等级，此处为高，根据业务情况而定
        int importance = NotificationManager.IMPORTANCE_HIGH;

        // 2. 获取系统的通知管理器
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        // 3. 创建NotificationChannel(这里传入的channelId要和创建的通知channelId一致，才能为指定通知建立通知渠道)
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(channelId,channelName, importance);
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(channel);
        }
        //点击通知时可进入的Activity
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        // 1. 创建一个通知(必须设置channelId)
        @SuppressLint("WrongConstant") Notification notification = new NotificationCompat.Builder(this,channelId)
                .setContentTitle(SUCCESS_TI_SHI)
                .setContentText("隔壁王叔叔接单成功")
                .setSmallIcon(ICON)
                .setContentIntent(pendingIntent)//点击通知进入Activity
                .setPriority(NotificationCompat.PRIORITY_MAX) //设置通知的优先级为最大
                .setCategory(Notification.CATEGORY_TRANSPORT) //设置通知类别
                .setVisibility(Notification.VISIBILITY_PUBLIC)  //控制锁定屏幕中通知的可见详情级别
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),ICON))   //设置大图标
                .build();

        // 4. 发送通知
        notificationManager.notify(2, notification);
    }


    public void onResume() {
        super.onResume();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //移除标记为id的通知 (只是针对当前Context下的所有Notification)
        notificationManager.cancel(2);
        //移除所有通知
        //notificationManager.cancelAll();
    }




    /**
     * 读取用户信息
     */
    private void getUserInfo(){
        userInfo = getSharedPreferences("userData", MODE_PRIVATE);
        String username = userInfo.getString("username", null);//读取username
        String passwrod = userInfo.getString("password", null);//读取password
        if(username!=null && passwrod!=null){
            etUname.setText(username);
            etPaw.setText(passwrod);
        }
    }


    public void scrollToTvLog(){
        int tvHeight = tvLog.getHeight();
        int tvHeight2 = getTextViewHeight(tvLog);
        if(tvHeight2>tvHeight){
            tvLog.scrollTo(0,tvHeight2-tvLog.getHeight());
        }
    }

    private int getTextViewHeight(TextView textView) {
        Layout layout = textView.getLayout();
        int desired = layout.getLineTop(textView.getLineCount());
        int padding = textView.getCompoundPaddingTop() +
                textView.getCompoundPaddingBottom();
        return desired + padding;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭弹窗，不然会 报错（虽然不影响使用）
        dialog.dismiss();
    }
}