package ua.com.octopus_aqua.notification;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import ua.com.octopus_aqua.inteface.ListViewFragment;
import ua.com.octopus_aqua.MainActivity;
import ua.com.octopus_aqua.inteface.PlantInfoActivity;
import ua.com.octopus_aqua.inteface.R;

public class NotificationMaker {

    private static NotificationMaker instance;

    private NotificationManager notManager;
    private Context context;

    private int notNumber = 0;

    private NotificationMaker(){
        this.context = MainActivity.getAppContext();
        notManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static synchronized NotificationMaker getInstance(){
        if(instance == null){
            instance =  new NotificationMaker();
        }
        return instance;
    }

    public void makeNotification(Intent intent,String title,String text,int imgId,String ticker){
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setContentTitle("Smth is watered");
        builder.setContentText("Some extremely important plant was watered");
        builder.setSmallIcon(R.drawable.pencil);
        builder.setTicker("WOOOOOOOOOOOOOOOOOOOOOW!!1");
        builder.setContentIntent(pendIntent);

        builder.setNumber(notNumber++);
        builder.setStyle(new android.support.v4.app.NotificationCompat.InboxStyle());

        //Notification notification = builder.getNotification();//may be probably needed for old API
        Notification notification = builder.build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notManager.notify(1,notification);

    }

    public void makePlantInfoNotification(long id){
        Intent intent = new Intent(context,PlantInfoActivity.class);
        intent.putExtra(ListViewFragment.ID,id);

        makeNotification(intent,"WOW, plant has happend","unbelievable",R.drawable.null_img,"WOOooW!!1");
    }
}
