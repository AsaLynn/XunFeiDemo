package com.think.xunfei;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.demonstrate.DemonstrateUtil;
import com.example.demonstrate.DialogUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    protected Button btn;
    protected TextView tv;
    private SpeechRecognizer speechRecognizer;
    private RecognizerDialog recognizerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initXunFei();
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);

        initView();
    }

    //初始化语音识别对象的监听.
    InitListener initListener = new InitListener() {
        @Override
        public void onInit(int i) {
            DemonstrateUtil.showLogResult("返回的code" + i);
            if (i != ErrorCode.SUCCESS) {
                DemonstrateUtil.showToastResult(MainActivity.this, "初始化失败，错误码：" + i);
            } else {
                DemonstrateUtil.showLogResult("初始化陈功,返回的code" + i);
            }
        }
    };

    //初始化sdk
    private void initXunFei() {
        // 将“12345678”替换成您申请的APPID，申请地址：http://www.xfyun.cn
        // 请勿在“=”与appid之间添加任何空字符或者转义符
        //appid,8位16进制数字字符串，应用的唯一标识，与下载的SDK一一对应
//        SpeechUtility.createUtility(this, "appid=" + getString(R.string.app_id));

        //获取语音识别对象
        speechRecognizer = SpeechRecognizer.createRecognizer(MainActivity.this, initListener);

        //创建语音识别对话框实例
        recognizerDialog = new RecognizerDialog(MainActivity.this, initListener);

        //

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn) {
            DialogUtil.showListDialog(MainActivity.this, "我的语音助手", new String[]{
                    "0,语音转文字",}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case 0:
                            tv.setText("");
                            setParam();
                            // 显示听写对话框
                            recognizerDialog.setListener(new RecognizerDialogListener() {

                                //识别成功的时候回调处理.
                                @Override
                                public void onResult(RecognizerResult recognizerResult, boolean b) {
                                    String resultString = recognizerResult.getResultString();
                                    DemonstrateUtil.showAllResult(resultString,tv);
                                }

                                //识别错误的时候回调处理.
                                @Override
                                public void onError(SpeechError speechError) {
                                    DemonstrateUtil.showToastResult(MainActivity.this,"识别出错了"+speechError.getErrorCode());
                                }
                            });
                            recognizerDialog.show();
                            DemonstrateUtil.showToastResult(MainActivity.this,"开始说话...");
                            break;
                    }
                }
            });
        }
    }

    private void setParam() {
        // 清空参数
        speechRecognizer.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎,在线引擎.
        speechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式
        speechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");

        // 设置语言
        speechRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        speechRecognizer.setParameter(SpeechConstant.ACCENT, "mandarin");


        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        speechRecognizer.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        speechRecognizer.setParameter(SpeechConstant.VAD_EOS, "1000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        speechRecognizer.setParameter(SpeechConstant.ASR_PTT,  "1");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        speechRecognizer.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        speechRecognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/mvoices/2.wav");
    }

    private void initView() {
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(MainActivity.this);
        tv = (TextView) findViewById(R.id.tv);
    }
}
