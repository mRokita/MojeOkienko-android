package pl.mrokita.mojeokienko.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;

import pl.mrokita.mojeokienko.Constants;
import pl.mrokita.mojeokienko.R;

public class TicketNotificationService extends Service {
    private int count = 0;
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    private String mOfficeId;
    private String mQueueId;
    private String mTicketId;
    ArrayList<Messenger> mClients = new ArrayList<>();
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg){
            Bundle data = msg.getData();
            switch(msg.what){
                case Constants.ACTION.REGISTER_CONNECTION:
                    mClients.add(msg.replyTo);
                    break;
                case Constants.ACTION.UNREGISTER_CONNECTION:
                    mClients.remove(msg.replyTo);
                    break;
                case Constants.ACTION.LOG_SOMETHING:
                    Log.e("CON", data.getString("text"));
                    break;
                case Constants.ACTION.SET_NOTIFICATION:
                    startNotifing();
                    break;
                default:
                    super.handleMessage(msg);
            }
            Log.e("Message", "Received");
        }
    }
    public void sendMessage(Messenger messenger, int action, Bundle data) throws RemoteException{
        Message msg = Message.obtain(null, action);
        msg.setData(data);
        messenger.send(msg);
    }

    public void sendMessageToAll(int action, Bundle data){
        for (int i = mClients.size()-1; i>=0; i--) {
            try {
                sendMessage(mClients.get(i), action, data);
            }
            catch (RemoteException e) {
                mClients.remove(i);
            }
        }
    }

    public IBinder onBind(Intent intent){
        return mMessenger.getBinder();
    }

    public void startNotifing(){
        Intent stopNotifingIntent = new Intent(this, TicketNotificationService.class);
        stopNotifingIntent.setAction(Constants.ACTION.STOP_NOTIFING);
        PendingIntent pstopNotifingIntent = PendingIntent.getService(this, 0, stopNotifingIntent, 0);
        Notification notification = new NotificationCompat.Builder(TicketNotificationService.this)
                .setContentTitle("hello")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("hello")
                .setOngoing(true)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Zakończ", pstopNotifingIntent)
                .build();
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
    }
    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        return START_STICKY;
    }

}