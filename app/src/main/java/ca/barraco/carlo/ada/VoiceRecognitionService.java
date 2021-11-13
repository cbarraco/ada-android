package ca.barraco.carlo.ada;

import static androidx.core.app.JobIntentService.enqueueWork;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class VoiceRecognitionService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    AdaRecognitionListener adaRecognitionListener;

    public static void recognize(Context context, Intent intent) {
        enqueueWork(context, VoiceRecognitionService.class, 1, intent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public int onStartCommand(@NonNull Intent intent, int flags, int startId) {
        Logger.debug("Received voice recognition request");

        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Waiting for voice command")
                .setSmallIcon(R.drawable.baseline_mic_24dp)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                // voice recognition must be done on main thread
                Context context = getApplicationContext();
                SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
                Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
                adaRecognitionListener = new AdaRecognitionListener(context);
                speechRecognizer.setRecognitionListener(adaRecognitionListener);
                speechRecognizer.startListening(recognizerIntent);
            }
        });
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
