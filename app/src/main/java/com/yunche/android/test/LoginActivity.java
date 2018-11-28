package com.yunche.android.test;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.yunche.android.yunchevideosdk.YCBaseFace;
import com.yunche.android.yunchevideosdk.demo.MemberInfo;
import com.yunche.android.yunchevideosdk.http.ApiService;
import com.yunche.android.yunchevideosdk.http.ResultBody;
import com.yunche.android.yunchevideosdk.http.base.BaseObserver;
import com.yunche.android.yunchevideosdk.http.base.RxSchedulers;
import com.yunche.android.yunchevideosdk.utils.PreferenceUtils;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends RxAppCompatActivity {

    private EditText name,pwd;
    private TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        name = (EditText) findViewById(R.id.name);
        pwd = (EditText) findViewById(R.id.pwd);
        login = (TextView) findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(name.getText().toString())){
                    Toast.makeText(getApplicationContext(),"输入用户名",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(pwd.getText().toString())){
                    Toast.makeText(getApplicationContext(),"输入密码",Toast.LENGTH_SHORT).show();
                    return;
                }
                login(name.getText().toString(),pwd.getText().toString());
            }
        });

    }

    public void login(String name,String pwd){
        //移除cookie

        final Map<String ,String> map = new HashMap<>();
        map.put("username",name);
        map.put("password",pwd);
        map.put("machineId","futgyihijoooji");//极光推送的设备id，这里demo随意的，正式开发替换成极光推送
        map.put("isTerminal","true");//是否为APP端登录 若为APP端登录，则会话有效期为90天;常规登录，则为30分钟

        login.setText("登录中......");

        ApiService.myApi().login(ResultBody.getJson(map))
                .compose(RxSchedulers.compose(this.<ResultBody<MemberInfo>>bindToLifecycle()))
                .subscribe(new BaseObserver<MemberInfo>(getApplicationContext()) {
                    @Override
                    protected void onHandleSuccess(MemberInfo s) {

                        login.setText("登录成功");

                        YCBaseFace.saveLoginInfo(s);

                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);

                        finish();
                    }

                    @Override
                    protected void onHandleError(String code, String msg) {
                        super.onHandleError(code, msg);
                        login.setText("登录失败，点击重试");
                    }
                });
    }


}
