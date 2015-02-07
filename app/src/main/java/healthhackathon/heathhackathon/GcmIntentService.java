package healthhackathon.heathhackathon;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Locale;

/**
 * Created by radoslawjarzynka on 07.02.15.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    public static final String TAG = "HealthHackathon";
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        Log.d("GCM", messageType);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                String latitudeString = extras.getString("lat");
                String longitudeString = extras.getString("lan");
                String typeString = extras.getString("additional_info");
                Integer type = null;
                Double latitude = null;
                Double longitude = null;
                try {
                    type = Integer.valueOf(typeString);
                    latitude = Double.valueOf(latitudeString);
                    longitude = Double.valueOf(longitudeString);
                } catch (NumberFormatException e) {
                    Log.d("WRONG", "Namber format eksepszyn");
                }

                if (type != null && latitude != null && longitude != null) {
                    startNotification(type, latitude, longitude);
                } else {
                    sendNotification("Something was null");
                }
                sendNotification("Received: " + extras.toString());
                Log.i(TAG, "Received: " + extras.toString());
                //Log.i(TAG, "With data: " + intent.getData().toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        //GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MapActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                       // .setSmallIcon(R.drawable.ic_stat_gcm)
                        .setContentTitle("GCM Notification")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


    //wywołanie notyfikacji
    public void startNotification(int i, double lati, double longi)
    {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.not_icon);


        //TO-DO: dopisać przypisanie aktualnych pozycji

        double latitude, longitude;
        //latitude = 52.179138;
        //longitude = 21.058139;

        latitude = lati;
        longitude = longi;
        String alertType;

        alertType="";
        if(i==1) alertType="Utrata przytomności";
        else
        if(i==2) alertType="Padaczka";
        else
        if(i==3) alertType="Wypadek";
        else
        if(i==4) alertType="Astma";



        String uri = "google.navigation:q=%f, %f";
        Intent navIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String
                .format(Locale.US, uri, latitude, longitude)));
        navIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(navIntent);

        PendingIntent navPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        RemoteViews customNoti = new RemoteViews(getPackageName(),
                R.layout.custom_notification);



        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon_status_bar)
                        .setTicker("Ktoś wzywa pomocy: " + alertType)
                        .setContent(customNoti);

        customNoti.setOnClickPendingIntent(R.id.button1, navPendingIntent);

        customNoti.setTextViewText(R.id.request_type, alertType);
        /*LayoutInflater inflater = LayoutInflater.from(this);
        View header = inflater.inflate(R.layout.custom_notification, null);
        TextView alertTypeLabel= (TextView)header.findViewById(R.id.request_type);
        alertTypeLabel.setText( alertType );*/



        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        mNotificationManager.notify(123, mBuilder.build());

    }
}
