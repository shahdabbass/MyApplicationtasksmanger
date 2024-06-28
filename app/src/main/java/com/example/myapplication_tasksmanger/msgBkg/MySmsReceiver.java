package com.example.myapplication_tasksmanger.msgBkg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class MySmsReceiver extends BroadcastReceiver {
    // creating a variable for a message listener interface on below line.
private static MsgListener msgListener;
//استخراج الرساله من الهاتف
    @Override
    public void onReceive(Context context, Intent intent) {
// getting bundle data  from intent.
        Bundle data = intent.getExtras();
        // creating an object
       // يتم استخراج كائنات الـ PDU (Protocol Data Units) من الـ Bundle. الـ PDU هو تنسيق ثنائي يحتوي على بيانات الرسالة.
        Object[] pdus = (Object[]) data.get("pdus");
        // running for loop to read the sms
        //معالجة كل PDU
        for (int i = 0; i < pdus.length; i++) {
            // getting sms message
            //يتم تحويل الـ PDU إلى كائن SmsMessage
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            // extracting the sms from sms message and setting it to string
            //استخراج نص الرسالة ورقم الهاتف
            String message = smsMessage.getMessageBody();
            String phone = smsMessage.getDisplayOriginatingAddress();
            // adding the message to listener
            //يتم استدعاء دالة msgReceived من المستمع (msgListener) وتمرير رقم الهاتف ونص الرسالة إليها.
            msgListener.msgReceived(phone,message);
        }
    }
    //  we are binding the listener.
    public static void bindListener(MsgListener listener) {
        msgListener = listener;

    }
}