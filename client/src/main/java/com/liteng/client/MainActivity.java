package com.liteng.client;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    public String action = "com.liteng.service.messenger";
    private static final int MSG_SEND = 0x001;
    private static final int MSG_RECEIVE = 0x002;

    private Messenger mServiceMessenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(action);
        //设置服务端的包名
        intent.setPackage("com.liteng.service");
        //绑定Service
        bindService(intent,mConnection,BIND_AUTO_CREATE);

    }

    public void sendMsg(View view) throws RemoteException {
        //创建Message对象
        Message msg = Message.obtain();
        msg.what = MSG_SEND;
        Bundle bundle  = new Bundle();
        bundle.putString("key","i am from client");
        //Messenger发送的内容必须是经过序列化过的对像,如果要传递对象需要实现Parcelable接口
        msg.setData(bundle);
        //设置我们在客户端接收消息的Messenger.
        msg.replyTo = mClientMessenger;

        mServiceMessenger.send(msg);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //当跟Service的连接建立时,我们通过IBinder创建一个Messenger来跟服务端进行通信
            mServiceMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceMessenger = null;
        }
    };


    private Messenger mClientMessenger = new Messenger(new Handler(){
        @Override
        public void handleMessage(Message msgFromService) {
            super.handleMessage(msgFromService);
            switch (msgFromService.what){
                case MSG_RECEIVE:
                    //获取服务端发送过来的消息
                    String fromService = msgFromService.getData().getString("fromService");
                    //将获取到的消息显示到Button上
                    ((Button)findViewById(R.id.btnSendMsg)).setText(fromService);
                    break;
            }
        }
    });
}
