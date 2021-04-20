package com.demo.mqtt_project;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class changepwd extends AppCompatActivity {

    private EditText user1,password1,password2,password3;
    private String userName,passWord1,passWord2;
    private String passWord3,spPsw ;
    @SuppressLint({"CutPasteId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepwd);
        user1=findViewById(R.id.edit_name);
        password1=findViewById(R.id.pwd_old);
        password2=findViewById(R.id.pwd_new1);
        password3=findViewById(R.id.pwd_new2);

        Button btn_sure = (Button)findViewById(R.id.changepwd_btn_sure);
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEditText();//获取EditText控件上的文本
                spPsw=readPsw(userName);
                if(TextUtils.isEmpty(userName)){
                    Toast.makeText(changepwd.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(passWord1)){
                    Toast.makeText(changepwd.this, "请输入旧密码", Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(passWord2)){
                    Toast.makeText(changepwd.this, "请输入新密码", Toast.LENGTH_SHORT).show();
                    return;
                } else if(TextUtils.isEmpty(passWord3)){
                    Toast.makeText(changepwd.this, "请再次输入新密码", Toast.LENGTH_SHORT).show();
                    return;
                }else if (!isExitUserName(userName)){
                    Toast.makeText(changepwd.this, "用户名不存在", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if((spPsw!=null&&!TextUtils.isEmpty(spPsw)&&!passWord1.equals(spPsw))){
                    Toast.makeText(changepwd.this, "输入的用户名和密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                } else if(!passWord2.equals(passWord3)) {
                    Toast.makeText(changepwd.this, "两次输入的新密码不一样", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    Toast.makeText(changepwd.this, "修改密码成功", Toast.LENGTH_SHORT).show();
                    //把账号、密码和账号标识保存到sp里面
                    // 保存账号和密码到SharedPreferences中
                    saveRegisterInfo(userName, passWord2);
                    Intent data = new Intent();
                    data.putExtra("userName", userName);
                    setResult(RESULT_OK, data);
                    changepwd.this.finish();
                }
            }
        });
        TextView back = (TextView) findViewById(R.id.btn_cancel);
       back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(changepwd.this, loginActivity.class);//从更改页面跳转到登录主页面
                startActivity(intent);
            }
        });
    }
    //读取EditText中的文本内容
    private void getEditText(){
        userName=user1.getText().toString().trim();
        passWord1=password1.getText().toString().trim();
        passWord2=password2.getText().toString().trim();
        passWord3=password3.getText().toString().trim();
    }

    //将账号密码保存到SharePreferences
    private void saveRegisterInfo(String userName,String psw){
        SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();//获取编译器
        editor.putString(userName, psw);
        editor.commit();//提交修改
    }

    //从SharePreferen中读取用户名，判断用户名是否已经被注册
    private boolean isExitUserName(String userName){
        boolean exit_userName=false;
        SharedPreferences sp = getSharedPreferences("loginInfo",MODE_PRIVATE);//获取SharedPerence的实例对象
        String psw=sp.getString(userName,"");
        if(!TextUtils.isEmpty(psw)){
            exit_userName=true;
        }
        return exit_userName;
    }

    //判断该用户名对应的密码
    private String readPsw(String userName){
        SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        return sp.getString(userName , "");
    }



}
