package com.demo.mqtt_project;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class mqttprojectActivity extends AppCompatActivity {
    private String host = "tcp://114.215.82.51:1883";
    private String userName = "sky";
    private String passWord = "sky";
    private String mqtt_id = "PC";
    private String mqtt_sub_topic = "kfb";
    private String mqtt_pub_topic = "phone";
    private ScheduledExecutorService scheduler;
    private Button btn_on,btn_off,btn_stop,submit,submit2;
    private TextView tex_tmp,tex_hum,tex_light,state,input_sign1,input_sign2;
    private MqttClient client;
    private MqttConnectOptions options;
    private Handler handler;
    private EditText input,input2;
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqttproject);
        gettex();//获取控件
        btn_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mqttprojectActivity.this,"开窗帘按钮" ,Toast.LENGTH_SHORT).show();
                publishmessageplus(mqtt_pub_topic,"ledon");
            }
        });
        btn_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mqttprojectActivity.this,"关窗帘按钮" ,Toast.LENGTH_SHORT).show();
                publishmessageplus(mqtt_pub_topic,"ledoff");
            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mqttprojectActivity.this,"停窗帘按钮" ,Toast.LENGTH_SHORT).show();
                publishmessageplus(mqtt_pub_topic,"stop");
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 input=findViewById(R.id.input);
                 String mess =input.getText().toString().trim();
                 Toast.makeText(mqttprojectActivity.this,"开阈值发送成功" ,Toast.LENGTH_SHORT).show();
                 input_sign1.setText("光强大于"+mess+"将打开窗帘");
                 publishmessageplus(mqtt_pub_topic,mess+"kai");
            }
        });

        submit2 = findViewById(R.id.submit2);
        submit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input2=findViewById(R.id.input2);
                String mess =input2.getText().toString().trim();
                Toast.makeText(mqttprojectActivity.this,"关阈值发送成功" ,Toast.LENGTH_SHORT).show();
                input_sign2.setText("光强小于"+mess+"将关闭窗帘");
                publishmessageplus(mqtt_pub_topic,mess+"kan");
            }
        });
        Mqtt_init();
        startReconnect();
        handler = new Handler() {
            @SuppressLint("SetTextI18n")
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1: //开机校验更新回传
                        break;
                    case 2:  // 反馈回传
                        break;
                    case 3:  //MQTT 收到消息回传
                        String photo[]=msg.obj.toString().split("\"");
                        String photo1[]=photo[3].split("[C]");
                        String photo2[]=photo[7].split("[%]");
                        String photo3[]=photo[11].split("[(]");
                       tex_tmp.setText("温度："+photo1[0]+"℃");
                       tex_hum.setText("湿度："+photo2[0]+"%RH");
                       tex_light.setText("光强："+photo3[0]+" Lux");
                        switch (photo[19]){
                            case "9":
                                state.setText("当前窗帘状态：初始化");
                                break;
                            case "0":
                                state.setText("当前窗帘状态：关");
                                break;
                            case "1":
                                state.setText("当前窗帘状态：开");
                                break;
                        }
                        break;
                    case 30:  //连接失败
                        Toast.makeText(mqttprojectActivity.this,"MQTT连接失败" ,Toast.LENGTH_SHORT).show();
                        break;
                    case 31:   //连接成功
                        Toast.makeText(mqttprojectActivity.this,"MQTT连接成功" ,Toast.LENGTH_SHORT).show();
                        try {
                            client.subscribe(mqtt_sub_topic,1);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        //退出系统
        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mqttprojectActivity.this, loginActivity.class);//从注册页面跳转到登录主页面
                startActivity(intent);
            }
        });

    }

    private void gettex(){
    btn_on = findViewById(R.id.Turn_on);
    btn_off =findViewById(R.id.Turn_off);
    btn_stop =findViewById(R.id.Turn_stop);
    tex_tmp =findViewById(R.id.text_temp);
    tex_hum =findViewById(R.id.text_humi);
    submit = findViewById(R.id.submit);
    tex_light =findViewById(R.id.text_light);
    state = findViewById(R.id.state);
    input=findViewById(R.id.input);
    input_sign1=findViewById(R.id.input_sign1);
    input_sign2=findViewById(R.id.input_sign2);
}

    private void Mqtt_init() {
        try {
            //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(host, mqtt_id,
                    new MemoryPersistence());
            //MQTT的连接设置
            options = new MqttConnectOptions();
            //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(false);
            //设置连接的用户名
            options.setUserName(userName);
            //设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(20);

            //设置回调
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    //连接丢失后，一般在这里面进行重连
                    System.out.println("connectionLost----------");
                    startReconnect();
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //publish后会执行到这里
                    System.out.println("deliveryComplete---------"
                            + token.isComplete());
                }
                @Override
                public void messageArrived(String topicName, MqttMessage message)
                        throws Exception {
                    //subscribe后得到的消息会执行到这里面
                    System.out.println("messageArrived----------");
                    Message msg = new Message();
                    msg.what = 3;   //收到消息标志位
                    msg.obj = topicName + "---" + message.toString();
                    handler.sendMessage(msg);  // hander 回传
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void Mqtt_connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(!(client.isConnected()) )  //如果还未连接
                    {
                        client.connect(options);
                        Message msg = new Message();
                        msg.what = 31;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 30;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }
    private void startReconnect() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!client.isConnected()) {
                    Mqtt_connect();
                }
            }
        }, 0 * 1000, 10 * 1000, TimeUnit.HOURS);
    }
    private void publishmessageplus(String topic,String message2) {
        if (client == null || !client.isConnected()) {
            return;
        }
        MqttMessage message = new MqttMessage();
        message.setPayload(message2.getBytes());
        try {
            client.publish(topic,message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }





}
