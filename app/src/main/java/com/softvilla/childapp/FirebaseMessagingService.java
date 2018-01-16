package com.softvilla.childapp;

/**
 * Created by shah on 8/23/2017.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

/**
 * Created by Malik on 8/10/2017.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService  {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        String packageName = remoteMessage.getData().get("packageName");
        String is_lock = remoteMessage.getData().get("is_lock");

      // showNotification(packageName + "\n" + is_lock);
        if(is_lock.equalsIgnoreCase("1")){
            LockedApp lockedApp = new LockedApp();
            lockedApp.packageName = packageName;
            lockedApp.save();
        }
        else {
            List<LockedApp> lockedApps = LockedApp.listAll(LockedApp.class);

            for(LockedApp lockedApp : lockedApps){
                if(lockedApp.packageName.equalsIgnoreCase(packageName)){
                    lockedApp.delete();
                }
            }
        }
    }
    private void showNotification(String message) {
        Intent i = new Intent(this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        long pattern[] = { 0, 100, 200, 300, 400 };
        vibrator.vibrate(pattern,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("SLM Parent Portal")
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                //.setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.mipmap.logo))
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0,builder.build());
        vibrator.cancel();
    }
}