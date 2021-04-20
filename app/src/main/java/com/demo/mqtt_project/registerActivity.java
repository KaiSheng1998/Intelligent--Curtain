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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class registerActivity extends AppCompatActivity {

    private EditText user1,password1,password2,telphone,email;
    private String userName,passWord1,passWord2,Telphone,Email;
    private String realCode;
    private String phoneCode;
    private EditText Phonecodes;  //输入的验证码
    private ImageView Showcode;   //展示的验证码

    @SuppressLint({"CutPasteId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        user1=findViewById(R.id.edit_name);
        password1=findViewById(R.id.pwd_once);
        password2=findViewById(R.id.pwd_twice);
        telphone=findViewById(R.id.phone);
        email=findViewById(R.id.email);
        Phonecodes = findViewById(R.id.phoneCodes);
        Showcode = findViewById(R.id.showCode);

        //将验证码用图片的形式显示出来
        Showcode.setImageBitmap(Code.getInstance().createBitmap());
        realCode = Code.getInstance().getCode().toLowerCase();
        Showcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Showcode.setImageBitmap(Code.getInstance().createBitmap());
                realCode = Code.getInstance().getCode().toLowerCase();
            }
        });


        Button b3 = (Button)findViewById(R.id.register_btn_sure);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEditText();//获取EditText控件上的文本
                if(TextUtils.isEmpty(userName)){
                    Toast.makeText(registerActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(passWord1)){
                    Toast.makeText(registerActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(passWord2)){
                    Toast.makeText(registerActivity.this, "请再次输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(Telphone)) {
                    Toast.makeText(registerActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(Email)) {
                    Toast.makeText(registerActivity.this, "请输入邮箱", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!passWord1.equals(passWord2)) {
                    Toast.makeText(registerActivity.this, "输入两次的密码不一样", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (!isMobileNO(Telphone)){
                    Toast.makeText(registerActivity.this, "手机号格式错误", Toast.LENGTH_SHORT).show();
                }
                else if(!isEmail(Email)) {
                    Toast.makeText(registerActivity.this, "邮箱格式错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!phoneCode.equals(realCode)){
                    Toast.makeText(registerActivity.this, "验证码错误，请重新输入", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (isExitUserName(userName)){
                    Toast.makeText(registerActivity.this,"此账户已经存在",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(registerActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    //把账号、密码和账号标识保存到sp里面
                    // 保存账号和密码到SharedPreferences中
                    saveRegisterInfo(userName, passWord1);
                    Intent data = new Intent();
                    data.putExtra("userName", userName);
                    setResult(RESULT_OK, data);
                    registerActivity.this.finish();
                }
            }
        });

        TextView register_cancle = (TextView) findViewById(R.id.btn_cancel);
        register_cancle.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(registerActivity.this, loginActivity.class);//从注册页面跳转到登录主页面
            startActivity(intent);
        }
    });

}

    //读取EditText中的文本内容
    private void getEditText(){
        userName=user1.getText().toString().trim();
        passWord1=password1.getText().toString().trim();
        passWord2=password2.getText().toString().trim();
        Telphone = telphone.getText().toString().trim();
        Email = email.getText().toString().trim();
        phoneCode = Phonecodes.getText().toString().toLowerCase();//输入的验证码
    }

    //将账号密码保存到SharePreferences
    private void saveRegisterInfo(String userName,String psw){
        SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();//获取编译器
        editor.putString(userName, psw);
        editor.commit();//提交修改
    }
/*    //将手机号保存到SharePreferences
    private void saveRegisterphone(String userName,String telphone){
        SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();//获取编译器
        editor.putString(userName, telphone);
        editor.commit();//提交修改
    }*/
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

   //判断手机号是否合法
    public boolean isMobileNO(String mobiles) {
        Pattern p = Pattern
                .compile("^((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(17[0,1,3,5,6,7,8]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    //判断电子邮箱是否合法
    public boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }


}
