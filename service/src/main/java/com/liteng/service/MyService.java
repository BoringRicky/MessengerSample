package com.liteng.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

public class MyService extends Service {

    private static final int MSG_RECEIVE_CLIENT = 0x001;
    private static final int MSG_SEND_CLIENT = 0x002;
    //用来接收客户端发送的消息的Messenger
    private Messenger mMessenger;

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //通过Handler创建Messenger,用来接收客户端发送的消息
        mMessenger = new Messenger(new ReceiveClientMessageHandler());
    }

    @Override
    public IBinder onBind(Intent intent) {
        //当我们绑定Service时,获取一个IBinder,用来跟跟它相关的Handler通信
        return mMessenger.getBinder();
    }

    private class ReceiveClientMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msgFromClient) {
            super.handleMessage(msgFromClient);
            switch (msgFromClient.what) {
                case MSG_RECEIVE_CLIENT:
                    //接收客户端发来的信息
                    //Messenger发送的内容必须是经过序列化过的对像,如果要传递对象需要实现Parcelable接口,不能直接传递String
                    String clientMsg = msgFromClient.getData().getString("key");
                    Toast.makeText(getApplicationContext(), clientMsg, Toast.LENGTH_SHORT).show();

//                  当服务端接收到客户端发送的消息之后,我们回应给客户端
//                  获取到发送给服务端消息的Messenger
                    Messenger clientMessenger = msgFromClient.replyTo;
                    Message message = new Message();
                    message.what = MSG_SEND_CLIENT;
                    Bundle bundle = new Bundle();
                    bundle.putString("fromService", "i am from service");
                    message.setData(bundle);
                    try {
                        clientMessenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
}
