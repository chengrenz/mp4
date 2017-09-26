package com.demo;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.ionicframework.starter.R;
import java.io.File;
import java.io.IOException;
import java.util.Date;


public class MiniMp4Activity extends Activity
        implements View.OnClickListener, SurfaceHolder.Callback,RadioGroup.OnCheckedChangeListener {


    public static final int OK = 11;
    private int limit = 10;
    private RelativeLayout footer, footer_base,rl_sd;
    private LinearLayout ll_time;
    private RadioGroup rg_sd ;
    // 程序中的两个按钮
    private ImageView record, record_stop,switchbtn,iv_sd;
    // 系统的视频文件
    private File videoFile;
    private MediaRecorder mRecorder;
    // 显示视频预览的SurfaceView
    private SurfaceView sView;
    private SurfaceHolder holder;
    private TextView tv_time, tv_reset, tv_save,tv_cancel;
    private Camera.Parameters parameters;
    // 记录是否正在进行录制
    private boolean isRecording = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent!=null){
            limit = intent.getIntExtra("limit",10);
        }
        // 去掉标题栏 ,必须放在setContentView之前
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main2);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 选择支持半透明模式,在有surfaceview的activity中使用。
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        footer = (RelativeLayout) findViewById(R.id.footer);
        footer_base = (RelativeLayout) findViewById(R.id.footer_base);
        rl_sd = (RelativeLayout) findViewById(R.id.rl_sd);
        ll_time = (LinearLayout) findViewById(R.id.ll_time);
        // 获取程序界面中的按钮
        iv_sd = (ImageView) findViewById(R.id.iv_sd);
        record = (ImageView) findViewById(R.id.record);
        record_stop = (ImageView) findViewById(R.id.record_stop);
        switchbtn = (ImageView) findViewById(R.id.switchbtn);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_reset = (TextView) findViewById(R.id.tv_reset);
        tv_save = (TextView) findViewById(R.id.tv_save);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        rg_sd = (RadioGroup) findViewById(R.id.rg_sd);
        // 为两个按钮的单击事件绑定监听器
        iv_sd.setOnClickListener(this);
        record.setOnClickListener(this);
        record_stop.setOnClickListener(this);
        switchbtn.setOnClickListener(this);
        tv_reset.setOnClickListener(this);
        tv_save.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        rg_sd.setOnCheckedChangeListener(this);

        // 获取程序界面中的SurfaceView
        sView = (SurfaceView) this.findViewById(R.id.sView);
        sView.setOnClickListener(this);
        // 设置分辨率
        holder = sView.getHolder();
        holder.addCallback(this);//添加回调
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//surfaceview不维护自己的缓冲区，等待屏幕渲染引擎将内容推送到用户面前
        holder.setFixedSize(720, 1280);
        // 设置该组件让屏幕不会自动关闭
        holder.setKeepScreenOn(true);

        //设置摄像头以及摄像头的方向
//        int CammeraIndex=FindBackCamera();//网上参考的一个函数，用来获取后置摄像头的info


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        //当surfaceview创建时开启相机
        if (mCamera == null) {
            try {
                mCamera = Camera.open();
                parameters = mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                mCamera.setParameters(parameters);
                mCamera.setDisplayOrientation(90);
                mCamera.lock();
                mCamera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                mCamera.startPreview();//开始预览
                Log.e("TAG", "eeeeeeee");
          } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(MiniMp4Activity.this, "mCamera err 103", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NUMBER_TYPE:
                    tv_time.setText(toDateTime(msg.arg1));
                    break;
                case END_TYPE:
                    record_stop.setVisibility(View.GONE);
                    footer.setVisibility(View.VISIBLE);
                    // 如果正在进行录制
                    if (isRecording) {
                        // 停止录制
                        mRecorder.stop();
                        // 释放资源
                        mRecorder.release();
                        mRecorder = null;
                        mCamera.stopPreview();
                    }
                    break;
            }

        }
    };

    private boolean longClicl = true;
    private final int NUMBER_TYPE = 1;
    private final int END_TYPE = 2;
    private Camera mCamera;

    private void startRecord() {
        footer_base.setVisibility(View.GONE);
        record_stop.setVisibility(View.VISIBLE);
        if (!Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this
                    , "SD卡不存在，请插入SD卡！"
                    , Toast.LENGTH_SHORT).show();
            return;
        }
        String time = String.valueOf(new Date().getTime());
        try {

            videoFile = new File(Environment
                    .getExternalStorageDirectory()
                    .getCanonicalFile() + "/" + time + ".3gp");
            // 创建MediaPlayer对象
            mRecorder = new MediaRecorder();
            mRecorder.reset();
            mCamera.unlock();
            mRecorder.setCamera(mCamera);

            // 设置从麦克风采集声音(或来自录像机的声音AudioSource.CAMCORDER)
            mRecorder.setAudioSource(MediaRecorder
                    .AudioSource.MIC);
            // 设置从摄像头采集图像
            mRecorder.setVideoSource(MediaRecorder
                    .VideoSource.CAMERA);

            // 设置视频文件的输出格式
            // 必须在设置声音编码格式、图像编码格式之前设置
            mRecorder.setOutputFormat(MediaRecorder
                    .OutputFormat.THREE_GPP);
            // 设置声音编码的格式
            mRecorder.setAudioEncoder(MediaRecorder
                    .AudioEncoder.AMR_NB);
            // 设置图像编码的格式
            mRecorder.setVideoEncoder(MediaRecorder
                    .VideoEncoder.H264);
            mRecorder.setVideoSize(720, 1280);
            // 每秒 4帧
            mRecorder.setVideoFrameRate(20);
            mRecorder.setOutputFile(videoFile.getAbsolutePath());
            //设置录制视频的方向
            mRecorder.setOrientationHint(90);
            // 指定使用SurfaceView来预览视频
            mRecorder.setPreviewDisplay(holder.getSurface());
            mRecorder.prepare();
            // 开始录制
            mRecorder.start();
            isRecording = true;
            System.out.println("---recording---");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MiniMp4Activity.this, "mRecorder err 205", Toast.LENGTH_SHORT).show();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = -1;
                while (i < limit && longClicl) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Toast.makeText(MiniMp4Activity.this, "Thread err 217", Toast.LENGTH_SHORT).show();
                    }
                    i++;
                    Message msg = handler.obtainMessage();
                    msg.what = NUMBER_TYPE;
                    msg.arg1 = i;
                    handler.sendMessage(msg);
                }
                Message msg = handler.obtainMessage();
                msg.what = END_TYPE;
                handler.sendMessage(msg);
            }

        }).start();
    }

    private void endRecord() {
        longClicl = false;
        record_stop.setVisibility(View.GONE);
        footer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.record:
                startRecord();
                break;
            case R.id.record_stop:
                endRecord();
                break;
            case R.id.tv_reset:
                clearFile(videoFile.getAbsolutePath());
                reStartActivity();
                break;
            case R.id.switchbtn:
                switchInput();
                break;
            case R.id.tv_cancel:
                this.finish();
                break;
            case R.id.sView:
                closeSd();
                break;
            case R.id.iv_sd:
                openSd();
                break;
            case R.id.tv_save:
                save();
                break;
        }

    }

    private void save(){
        Intent intent = new Intent();
        intent.putExtra("path",videoFile.getAbsolutePath());
        setResult(OK,intent);
        finish();
    }

    private void closeSd(){
        rl_sd.setVisibility(View.GONE);
        ll_time.setVisibility(View.VISIBLE);
    }

    private void openSd(){
        ll_time.setVisibility(View.GONE);
        rl_sd.setVisibility(View.VISIBLE);
    }

    private boolean clearFile(String url) {
        File file = new File(url);
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private String toDateTime(int s) {
        int h = s / 3600;
        s %= 3600;
        int m = s / 60;
        s %= 60;
        return toTwo(h) + ":" + toTwo(m) + ":" + toTwo(s);
    }

    private String toTwo(int n) {
        return n > 9 ? String.valueOf(n) : "0" + n;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCamera.release();
        mCamera = null;
        holder = null;
        sView = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && videoFile!=null) {
            Toast.makeText(getApplicationContext(), "清除了当前视频缓存", Toast.LENGTH_SHORT).show();
            clearFile(videoFile.getAbsolutePath());
        }
        return super.onKeyDown(keyCode, event);
    }

    private void reStartActivity() {
        this.recreate();
    }
    private int cameraPosition = 1;//0代表前置摄像头，1代表后置摄像头
    private void switchInput(){
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数

        for(int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            if(cameraPosition == 1) {
                //现在是后置，变更为前置
                if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    mCamera.stopPreview();//停掉原来摄像头的预览
                    mCamera.release();//释放资源
                    mCamera = null;//取消原来摄像头
                    mCamera = Camera.open(i);//打开当前选中的摄像头
                    try {
                        mCamera.setDisplayOrientation(90);
                        mCamera.lock();
                        mCamera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    mCamera.startPreview();//开始预览
                    cameraPosition = 0;
                    break;
                }
            } else {
                //现在是前置， 变更为后置
                if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    mCamera.stopPreview();//停掉原来摄像头的预览
                    mCamera.release();//释放资源
                    mCamera = null;//取消原来摄像头
                    mCamera = Camera.open(i);//打开当前选中的摄像头
                    try {
                        mCamera.setDisplayOrientation(90);
                        mCamera.lock();
                        mCamera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    mCamera.startPreview();//开始预览
                    cameraPosition = 1;
                    break;
                }
            }

        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int rid) {
        int count = radioGroup.getChildCount();
        int index = 0;
        for(int i=0;i<count;i++){
            RadioButton rb = (RadioButton) radioGroup.getChildAt(i);
            int id = rb.getId();
            if(rid == id){
                rb.setTextColor(Color.parseColor("#f2dc4d"));
                index = i;
            }else{
                rb.setTextColor(Color.parseColor("#ffffff"));
            }
            Log.e("TAG",i+" -- i");

        }
        closeSd();
        switch (index){
            case 0:
                // auto
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                mCamera.setParameters(parameters);
                break;
            case 1:
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
                // open
                break;
            case 2:
                // close
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
                break;
        }
    }
}


