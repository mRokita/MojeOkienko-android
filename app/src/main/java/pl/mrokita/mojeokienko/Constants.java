package pl.mrokita.mojeokienko;

public class Constants {
    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }
    public interface ACTION {
        int SET_NOTIFICATION = 0;
        int REGISTER_CONNECTION = 1;
        int UNREGISTER_CONNECTION = 2;
        int LOG_SOMETHING = 3;
        String STOP_NOTIFING = "stopNotifing";
    }

}
