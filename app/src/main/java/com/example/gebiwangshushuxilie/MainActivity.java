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
 * ????????????????????????
 * ????????????????????????????????????
 * ????????????????????????
 * try catch
 * ???????????????????????????????????????????????????
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etUname,etPaw;
    private TextView tvStart,tvStop,tvLog,tvBrow,tvGetTitle,tvTitle,tvAppDown,tvAppOpen;
    private Handler mHandler;
    /*
    ???????????????????????????????????????3??????
    ??????????????????count+1???
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
    //??????????????????????????????             ?????????????????????sontaskId             /index/browse/browse_receive_task.html
    //????????????image??????   /index/browse/browse_referring.html?sontaskId=38734
    ///public/index.php/index/Browse/browse_obtain?t=0.8302394486326041 ???????????????????????????

    private static final String TB_GET_IMAGE = "/Index/apprentice/referring_task.html";
    private static final String JD_GET_IMAGE = "/index/Jdapprentice/jreferring_task.html";


    /**
     * ????????????????????????
     * 1???MainActivity
     * 2???build.gradle????????????
     * 3???AndroidMainfest.xml??????
     * 4???Update??????
     * 5???KeepAlive??????
     */
    private static final String PT_NAME = "geBiWangShuShu";
    private static final String TITLE = "?????????????????????";
    private static String TI_SHI = "???????????????App?????????";
    private static final String SUCCESS_TI_SHI = "???????????????????????????";
    private static final String CHANNELID = "gbwssSuccess";
    private static String APK_PACKAGE = "com.lzm.gbwss";
    private static int ICON = R.mipmap.gbwss;
    private static final int JIE_DAN_SUCCESS = R.raw.gbwss_success;
    private static final int JIE_DAN_FAIL = R.raw.gbwss_fail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //???????????????
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, KeepAliveService.class);
        startService(intent);//??????????????????
        ignoreBatteryOptimization();//??????????????????
        if(!checkFloatPermission(this)){
            //??????????????????
            requestSettingCanDrawOverlays();
        }
        initView();
    }


    private void initView(){
        //????????????
        UpdateApk.update(MainActivity.this);
        //????????????????????????
        openNotification();
        //???????????????????????????
        WindowPermissionCheck.checkPermission(this);
        //??????????????????
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
        getUserInfo();//??????????????????
        //??????textView??????????????????
        tvLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        tvLog.setTextIsSelectable(true);
        tvStart.setOnClickListener(this);
        tvStop.setOnClickListener(this);
        tvBrow.setOnClickListener(this);
        tvAppOpen.setOnClickListener(this);
        tvAppDown.setOnClickListener(this);
        tvGetTitle = findViewById(R.id.tv_getTitle);
        tvGetTitle.setOnClickListener(this);
        tvLog.setText("?????????????????????????????????~"+"\n");
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
                ?????????????????????Handler??????Runnable????????????????????????????????????????????????
                 */
                mHandler.removeCallbacksAndMessages(null);
                if(LOGIN_URL == ""){
                    tvLog.setText("?????????????????????,???3????????????...");
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
                    tvLog.setText("?????????????????????,???3????????????...");
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
                    tvLog.setText("?????????????????????,???3????????????...");
                }else {
                    cookie = "";
                    userLogin(etUname.getText().toString().trim(),etPaw.getText().toString().trim(),"getShopTitle");
                }
                break;
        }
    }


    /**
     * ????????????
     */
    public void announcementDialog(String[] lesson){

        dialog = new AlertDialog
                .Builder(this)
                .setTitle("??????")
                .setCancelable(false) //??????????????????????????????????????????????????? false
                .setPositiveButton("????????????", null)
                //.setMessage("")
                .setItems(lesson,null)
                .create();
        dialog.show();
    }


    private void browOpen(){
        if(BROW_OPEN == "") {
            tvLog.setText("?????????????????????,???3????????????...");
        }
        Uri uri = Uri.parse(BROW_OPEN);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }


    /**
     * ??????activity???onKeyDown????????????????????????????????????activity
     * ????????????https://blog.csdn.net/qq_36713816/article/details/71511860
     * ?????????????????????????????????onBackPressed??????????????????????????????????????????????????????
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
     * ????????????
     * @param username
     * @param password
     */
    private void userLogin(String username, String password,String mark){

        tvLog.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()) + ": ???????????????..."+"\n");

        HttpClient.getInstance().post(LOGIN, LOGIN_URL)
                .params("userName", username)
                .params("password", password)
                .params("user_os", "windows")
                .params("user_model", "chrome")
                .params("user_code", "b4431ab36e95278dd695d8ac3a4274df")
                .params("user_identifying", "76c31cb90b00c1ce5f4232e55d72c4da")
                .params("ip", "111.17.70.38")
                .params("ip_data", "???????????????????????????")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            /**
                             * {"msg":"???????????????","status":300}
                             * {"msg":"????????????","errorStatus":101,"status":300}
                             * {"msg":"???????????????","status":200,"login_sign":1,"apprenticeId":78534,"identity_sign":2,
                             * "receiving_sign":0,"user_sjcode":"76c31cb90b00c1ce5f4232e55d72c4da"}
                             */
                            JSONObject obj = JSONObject.parseObject(response.body());
                            //????????????
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
                            sendLog("?????????"+e.getMessage());
                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        sendLog("??????ERR???"+response.getException());
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
                            String url = response.getRawResponse().request().url().toString();
                            if(url.contains("evaluate.html")){
                                sendLog("???????????????????????????");
                                playMusic(JIE_DAN_FAIL,3000,0);
                            }else {
                                Object o = JSONObject.parse(response.body());
                                Document document = Jsoup.parse(o.toString());
                                String title = document.title();
                                if("login".equals(biaoZhi)){
                                    if("????????????".equals(title)){
                                        List tb = document.select("div[class=display-flexbox tb_task_wrapp]").select("em").textNodes();
                                        List jd = document.select("div[class=display-flexbox jd_task_wrapp]").select("em").textNodes();
                                        if(todayCount.equals(tb.get(0).toString())){
                                            tbIsStart = false;
                                            sendLog("??????????????????");
                                        }else if(theWeekCount.equals(tb.get(1).toString())){
                                            tbIsStart = false;
                                            sendLog("??????????????????");
                                        }else if(theMonthCount.equals(tb.get(2).toString())){
                                            tbIsStart = false;
                                            sendLog("??????????????????");
                                        }
                                        if(todayCount.equals(jd.get(0).toString())){
                                            jdIsStart = false;
                                            sendLog("??????????????????");
                                        }else if(theWeekCount.equals(jd.get(1).toString())){
                                            jdIsStart = false;
                                            sendLog("??????????????????");
                                        }else if(theMonthCount.equals(jd.get(2).toString())){
                                            jdIsStart = false;
                                            sendLog("??????????????????");
                                        }
                                        getAllTask();
                                    }else if("????????????".equals(title)){
                                        playMusic(JIE_DAN_SUCCESS,3000,2);
                                        String img = document.select("img[class=main_link]").attr("src");
                                        String guanJianZi = document.select("input[class=key_word_hidden]").attr("value");
                                        sendLog2("-------------------------");
                                        sendLog2("??????????????????"+guanJianZi);
                                        sendLog2("-------------------------");
                                        sendLog2("????????????"+img);
                                    }else {
                                        sendLog("?????????????????????????????????????????????");
                                    }
                                }else {
                                    if("????????????".equals(title)){
                                        sendLog("??????????????????????????????????????????");
                                    }else if("????????????".equals(title)){
                                        String img = document.select("img[class=main_link]").attr("src");
                                        String guanJianZi = document.select("input[class=key_word_hidden]").attr("value");
                                        sendLog2("-------------------------");
                                        sendLog2("??????????????????"+guanJianZi);
                                        sendLog2("-------------------------");
                                        sendLog2("????????????"+img);
                                    }else {
                                        sendLog("?????????????????????????????????????????????");
                                    }
                                }
                            }
                        }catch (Exception e){
                            sendLog("?????????????????????"+e.getMessage());
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
                             * {"status":300,"errStatus":118,"msg":"????????????"}
                             * {"status":300,"errorStatus":115,"msg":"????????????"}
                             * {"status":200,"task":"\/Index\/Apprentice\/wangwang_info","stop":1,"msg":"????????????"}
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
                                    //?????????????????????????????????   ????????????????????????
                                    tbLqTask(orderId);
                                    isStart = false;
                                }
                                return;
                            }
                            sendLog("?????????"+o.getString("msg"));
                        }catch (Exception e){
                            sendLog("?????????"+e.getMessage());
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
                             * {"status":300,"msg":"????????????"}      ????????????????????????????????????
                             * {"status":300,"errStatus":11811111,"msg":"????????????"}
                            ????????? {"status":200,"task":"/index/Apprentice/evaluate?sunsheet_id=427774","stop":1}
                            ????????????????????? {"status":300,"task":"\/index\/Jdapprentice","errStatus":10001}
                             ???????????????6??????
                             */
                            JSONObject o = JSONObject.parseObject(response.body());
                            if(200 == o.getInteger("status")){
                                if(o.containsKey("msg")){
                                    sendLog(o.getString("msg"));
                                }else if (o.getString("task").contains("sunsheet_id")){
                                    sendLog("??????????????????????????????");
                                }else {
                                    int index = o.getString("task").indexOf("=");
                                    String orderId = o.getString("task").substring(index+1);
                                    jdLqTask(orderId);
                                    isStart = false;
                                }
                                return;
                            }else if(o.containsKey("task")){
                                sendLog("?????????"+o.getString("msg"));
                                jdIsStart = false;
                            }else {
                                sendLog("?????????"+o.getString("msg"));
                            }
                        }catch (Exception e){
                            sendLog("?????????"+e.getMessage());
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
                             * {"status":200,"msg":"????????????"}
                             * {"status":300,"msg":"????????????????????????"}
                             */
                            JSONObject o = JSONObject.parseObject(response.body());
                            sendLog2("?????????"+o.getString("msg"));
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
                             * {"status":200,"msg":"????????????"}
                             * {"status":300,"msg":"????????????????????????"}
                             */
                            JSONObject o = JSONObject.parseObject(response.body());
                            sendLog2("?????????"+o.getString("msg"));
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
                            sendLog2("??????????????????"+guanJianZi);
                            sendLog2("-------------------------");
                            sendLog2("????????????"+img);
                        }catch (Exception e){
                            sendLog("?????????????????????"+e.getMessage());
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
                            sendLog2("??????????????????"+guanJianZi);
                            sendLog2("-------------------------");
                            sendLog2("????????????"+img);
                        }catch (Exception e){
                            sendLog("?????????????????????"+e.getMessage());
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
     * ????????????
     */
    public void stop(){
        OkGo.getInstance().cancelAll();
        //Handler????????????????????????removeCallbacksAndMessages?????????Message???Runnable
        mHandler.removeCallbacksAndMessages(null);
        sendLog("???????????????");
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
                                Toast.makeText(MainActivity.this, "????????????????????????????????????", Toast.LENGTH_LONG).show();
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
                            //????????????
                            String[] gongGao = ptAddrObj.getString("ptAnnoun").split(";");
                            announcementDialog(gongGao);
                        }catch (Exception e){
                            sendLog("???????????????"+e.getMessage());
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        sendLog("????????????????????????~");
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
     * ???????????????????????????
     * @param voiceResId ????????????
     * @param milliseconds ????????????????????????
     */
    private void playMusic(int voiceResId, long milliseconds,int total){

        count = total;//?????????????????????

        //????????????
        MediaPlayer player = MediaPlayer.create(MainActivity.this, voiceResId);
        player.start();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //??????????????????
                if(count != 0){
                    player.start();
                }
                count --;
            }
        });

        //??????
        Vibrator vib = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
        //??????????????????
        vib.vibrate(milliseconds);
    }



    /**
     * ????????????
     * @param log
     */
    public void sendLog(String log){
        scrollToTvLog();
        if(tvLog.getLineCount() > 40){
            tvLog.setText("");
        }
        tvLog.append(new SimpleDateFormat("HH:mm:ss").format(new Date()) + ": "+log+"\n");
        //??????????????????100???????????????
    }

    public void sendLog2(String log){
        scrollToTvLog();
        tvLog.append(new SimpleDateFormat("HH:mm:ss").format(new Date()) + ": "+log+"\n");
    }




    /**
     * ??????????????????
     */

    public void ignoreBatteryOptimization() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean hasIgnored = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasIgnored = powerManager.isIgnoringBatteryOptimizations(getPackageName());
            //  ????????????APP??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            if(!hasIgnored) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:"+getPackageName()));
                startActivity(intent);
            }
        }
    }


    private void openNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //???????????????????????????????????????
            NotificationSetUtil.OpenNotificationSetting(this);
        }
    }



    //????????????
    private void requestSettingCanDrawOverlays() {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.O) {//8.0??????
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivityForResult(intent, 1);
        } else if (sdkInt >= Build.VERSION_CODES.M) {//6.0-8.0
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1);
        } else {//4.4-6.0??????
            //???????????????
        }
    }




    //?????????????????????????????????   context???????????????Activity.??????tiis
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
     * ??????????????????
     */
    private void saveUserInfo(String username,String password){
        userInfo = getSharedPreferences("userData", MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();//??????Editor
        //??????Editor?????????????????????????????????
        editor.putString("username",username);
        editor.putString("password", password);
        editor.commit();//????????????

    }



    /**
     * ????????????????????????
     */
    protected void receiveSuccess(String bj,String yj){
        //???????????????id????????????
        String channelId = CHANNELID;
        //??????????????????????????????
        String channelName = "???????????????????????????";
        //???????????????????????????????????????????????????????????????
        int importance = NotificationManager.IMPORTANCE_HIGH;

        // 2. ??????????????????????????????
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        // 3. ??????NotificationChannel(???????????????channelId?????????????????????channelId????????????????????????????????????????????????)
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(channelId,channelName, importance);
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(channel);
        }
        //???????????????????????????Activity
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        // 1. ??????????????????(????????????channelId)
        @SuppressLint("WrongConstant") Notification notification = new NotificationCompat.Builder(this,channelId)
                .setContentTitle(SUCCESS_TI_SHI)
                .setContentText("???????????????????????????")
                .setSmallIcon(ICON)
                .setContentIntent(pendingIntent)//??????????????????Activity
                .setPriority(NotificationCompat.PRIORITY_MAX) //?????????????????????????????????
                .setCategory(Notification.CATEGORY_TRANSPORT) //??????????????????
                .setVisibility(Notification.VISIBILITY_PUBLIC)  //????????????????????????????????????????????????
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),ICON))   //???????????????
                .build();

        // 4. ????????????
        notificationManager.notify(2, notification);
    }


    public void onResume() {
        super.onResume();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //???????????????id????????? (??????????????????Context????????????Notification)
        notificationManager.cancel(2);
        //??????????????????
        //notificationManager.cancelAll();
    }




    /**
     * ??????????????????
     */
    private void getUserInfo(){
        userInfo = getSharedPreferences("userData", MODE_PRIVATE);
        String username = userInfo.getString("username", null);//??????username
        String passwrod = userInfo.getString("password", null);//??????password
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
        //???????????????????????? ?????????????????????????????????
        dialog.dismiss();
    }
}