package com.demo.mqtt_project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class loginActivity extends AppCompatActivity {
    private String userName,psw,spPsw;
    private EditText user,password;//引入EditText控件
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user = findViewById(R.id.login_edit_account);
        password = findViewById(R.id.login_edit_pwd);
        Button login_btn_login = (Button) findViewById(R.id.login_btn_login);

        login_btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getText();//获得文本控件
                spPsw = readPsw(userName);
                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(loginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(psw)) {
                    Toast.makeText(loginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                } else if (psw.equals(spPsw)) {
                    Toast.makeText(loginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(loginActivity.this, mqttprojectActivity.class));
                    return;
                } else if ((spPsw != null && !TextUtils.isEmpty(spPsw) && !psw.equals(spPsw))) {
                    Toast.makeText(loginActivity.this, "输入的用户名和密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(loginActivity.this, "此用户名不存在", Toast.LENGTH_SHORT).show();
                }
            }
        });


//注册
TextView login_btn_register = (TextView) findViewById(R.id.login_btn_register);
        login_btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(loginActivity.this, registerActivity.class);//从登录页面跳转到注册页面
                startActivity(intent);
            }
        });

//修改密码页面
TextView change_pwd = (TextView) findViewById(R.id.change_pwd);
        change_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(loginActivity.this, changepwd.class);//从登录页面跳转到修改密码页面
                startActivity(intent);
            }
        });


//注销
TextView login_btn_cancle = (TextView) findViewById(R.id.login_btn_cancle);
        login_btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getText();//获得文本控件
                 spPsw=readPsw(userName);
                if (!TextUtils.isEmpty(psw)&&!TextUtils.isEmpty(userName)&&psw.equals(spPsw)){
                    canclewarn();
                }
                else {Toast.makeText(loginActivity.this, "用户名或密码有误,注销失败!", Toast.LENGTH_SHORT).show();}
            }
        });
    }


//获得控件上的文本
private void getText() {
                userName = user.getText().toString().trim();
                psw = password.getText().toString().trim();
            }

//根据用户名找密码
private String readPsw(String userName) {
                SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
                return sp.getString(userName, "");
            }

//注销用户
private void delete(String userName) {
                SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();//获取编译器
                editor.remove(userName);
                editor.commit();//提交修改
            }

//注销警告
private void canclewarn()   {
    AlertDialog.Builder builder= new AlertDialog.Builder(loginActivity.this);//MainActivity.this为当前环境
    builder.setIcon(R.drawable.warn);//提示图标
    builder.setTitle("警告");//提示框标题
    builder.setMessage("确定要注销该用户吗？");//提示内容
    builder.setPositiveButton("确认",new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            getText();
            delete(userName);
            Toast.makeText(loginActivity.this, "注销成功", Toast.LENGTH_SHORT).show();
        }
    });
    builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
      //      Toast.makeText(loginActivity.this, "点击了取消！", Toast.LENGTH_LONG).show();
        }
    });
    builder.create().show();
}


protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            //是获取注册界面回传过来的用户名
            String userName=data.getStringExtra("userName");
            if(!TextUtils.isEmpty(userName)){

            }
        }
    }

}
