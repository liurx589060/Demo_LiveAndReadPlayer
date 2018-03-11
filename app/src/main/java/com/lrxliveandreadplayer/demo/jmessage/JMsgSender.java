package com.lrxliveandreadplayer.demo.jmessage;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lrxliveandreadplayer.demo.beans.JMsgBean;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by Administrator on 2018/3/10.
 */

public class JMsgSender {
    public static void sendMessage(Context context,int code, String str, String exinfo) {
        String userName = null;
        if(JMessageClient.getMyInfo().getUserName().equals("wys30201")) {
            userName = "wys30202";
        }else if (JMessageClient.getMyInfo().getUserName().equals("wys30202")){
            userName = "wys30201";
        }
        if(userName == null) {
            Toast.makeText(context.getApplicationContext(),"userName is null",Toast.LENGTH_SHORT).show();
            return;
        }
        JMsgBean bean = new JMsgBean();
        bean.setText(str);
        bean.setCode(code);
        bean.setExinfo(exinfo);
        Message message = JMessageClient.createSingleTextMessage(userName,new Gson().toJson(bean));
        message.setOnSendCompleteCallback(new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
//                if(i == 0) {
//                    Toast.makeText(getApplicationContext(),"发送成功",Toast.LENGTH_SHORT).show();
//                }else {
//                    Toast.makeText(getApplicationContext(),"发送失败--" + s,Toast.LENGTH_SHORT).show();
//                }
            }
        });
        MessageSendingOptions options = new MessageSendingOptions();
        options.setShowNotification(false);
        JMessageClient.sendMessage(message,options);
    }
}
