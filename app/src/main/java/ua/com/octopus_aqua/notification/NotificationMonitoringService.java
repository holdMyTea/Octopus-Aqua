package ua.com.octopus_aqua.notification;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import ua.com.octopus_aqua.inteface.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationMonitoringService extends Service {

    public static String WATER_DATES;

    //private String[] waterDates;
    private String[] waterDates = {"14.03.2016 11:10"};

    private final String TIME_FORMAT = "dd.MM.yyyy HH:mm";
    private final long CHECK_TIME = 20 * 1000;      //pause between checks, in mills
    SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT);

    public NotificationMonitoringService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //waterDates = PlantBaseHandler.getInstance().getColumn("irjgi");

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String currentTime = getCurrentTime();
                        //Log.d("MY_TAG", "Cheq on");
                        for (int i = 0; i < waterDates.length; i++) {
                            if (waterDates[i].equals(currentTime)) {
                                NotificationMaker.getInstance().makePlantInfoNotification(i+1);
                            }
                        }
                        synchronized (this) {
                            if(PreferenceManager.getDefaultSharedPreferences(getApplication()).getBoolean(
                                    getResources().getString(R.string.notif_plant_water), true)){
                                Log.d("MY_TAG", "Notification service keep working");
                            } else{
                                Log.d("MY_TAG", "Notification service is time to die");
                                stopSelf();
                            }
                        }

                    } catch(Exception e){
                        e.printStackTrace();
                        stopSelf();
                    }
                }

            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private String getCurrentTime() {
        return dateFormat.format(new Date().getTime());
    }

}
