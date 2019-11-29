package pl.mrokita.mojeokienko.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import androidx.core.app.NotificationCompat;

import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import pl.mrokita.mojeokienko.Api;
import pl.mrokita.mojeokienko.Constants;
import pl.mrokita.mojeokienko.R;

public class TicketNotificationService extends Service {
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    private Api.TicketInfo mTicketInfo;
    private Vibrator mVibrator;
    private NotificationManager mNotificationManager;
    private boolean mIsForeground = false;
    ArrayList<Messenger> mClients = new ArrayList<>();

    @Override
    public void onCreate(){
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        super.onCreate();
    }

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
                    msg.getData().setClassLoader(Api.TicketInfo.class.getClassLoader());
                    mTicketInfo = data.getParcelable("ticketInfo");
                    startNotifying();
                    break;
                default:
                    super.handleMessage(msg);
            }
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

    private void updateNotification(){
        Intent stopNotifingIntent = new Intent(this, TicketNotificationService.class);
        stopNotifingIntent.setAction(Constants.ACTION.STOP_NOTIFING);
        PendingIntent pstopNotifingIntent = PendingIntent.getService(this, 1234, stopNotifingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(TicketNotificationService.this)
                .setContentTitle(String.format("Śledzenie nr. %s (Okno: %s)", mTicketInfo.getNumber(), mTicketInfo.getWindowLetter()))
                .setSmallIcon(R.drawable.ic_ticket_confirmation_white_36dp)
                .setContentText(String.format("Zostało ok %d minut (%d os. przed)",
                        mTicketInfo.getExpectedTimeLeft(), mTicketInfo.getClientsBefore()))
                .setOngoing(true)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Zakończ śledzenie", pstopNotifingIntent)
                .build();
        if(!mIsForeground) {
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
            mIsForeground = true;
        } else {
            mNotificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
        }
    }

    public void vibrate(){
        mVibrator.vibrate(
                new long[]{1000, 600, 1000, 600, 1000, 1000, 600, 600},
                -1);
    }

    public void doFinalReminder(Api.TicketInfo ticketInfo){
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            vibrate();
            e.printStackTrace();
        }
        Intent stopNotifingIntent = new Intent(this, TicketNotificationService.class);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Przypomnienie o kolejce")
                .setSmallIcon(R.drawable.ic_account_multiple_white_36dp)
                .setLights(0xff00ff00, 300, 100)
                .setContentText(String.format("Twój numer jest wzywany do okna %s", ticketInfo.getWindowLetter()))
                .build();
        mNotificationManager.notify(Constants.NOTIFICATION_ID.FINAL_NOTIFICATION, notification);
        vibrate();
        stopNotifing();
    }

    public void doReminder(Integer time){
        vibrate();
        Intent stopNotifingIntent = new Intent(this, TicketNotificationService.class);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Przypomnienie o kolejce")
                .setSmallIcon(R.drawable.ic_account_multiple_white_36dp)
                .setLights(0xff00ff00, 300, 100)
                .setContentText(String.format("Pozostało mniej niż %d minut", time))
                .build();
        mNotificationManager.notify(Constants.NOTIFICATION_ID.REMINDER_NOTIFICATION, notification);
    }

    public void startNotifying(){
        updateNotification();
        new Thread(){
            private final Api.TicketInfo ticketInfo = new Api.TicketInfo(mTicketInfo);
            private Integer lastExpectedTime = -1;
            @Override
            public void run(){
                while(ticketInfo.equals(mTicketInfo)){
                    try{
                        Api.TicketInfo tempTicketInfo  = Api.getTicketInfo(ticketInfo.getOfficeId(),
                                ticketInfo.getWindowLetter(),
                                String.valueOf(ticketInfo.getNumber()));
                        if (tempTicketInfo.equals(mTicketInfo)){
                            mTicketInfo = tempTicketInfo;
                            Integer notificationTime = Integer.valueOf(
                                    PreferenceManager.getDefaultSharedPreferences(
                                            TicketNotificationService.this).getString(
                                            Constants.PREFERENCE.NOTIFICATION_TIME, "4"));
                            Integer expectedTime = mTicketInfo.getExpectedTimeLeft();
                            if(!lastExpectedTime.equals(expectedTime))
                                updateNotification();
                            if(!lastExpectedTime.equals(expectedTime) && expectedTime <= notificationTime){
                                if(expectedTime <= 0){
                                    doFinalReminder(ticketInfo);
                                } else {
                                    doReminder(notificationTime);
                                }
                            }
                            lastExpectedTime = expectedTime;
                        }
                    } catch (IOException | JSONException e){
                        e.printStackTrace();
                    }
                    try{
                        Thread.sleep(1000);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void stopNotifing(){
        mTicketInfo = null;
        mIsForeground = false;
        stopForeground(true);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        if (intent!=null && intent.getAction() != null){
            switch(intent.getAction()){
                case Constants.ACTION.STOP_NOTIFING:
                    stopNotifing();
                    break;
            }
        }
        return START_STICKY;
    }

}