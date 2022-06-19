package ca.barraco.carlo.ada.recognition;

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

import ca.barraco.carlo.ada.Logger;
import ca.barraco.carlo.ada.ui.MainActivity;
import ca.barraco.carlo.ada.R;

public class VoiceRecognitionService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    private AdaRecognitionListener adaRecognitionListener;

    @Override
    public int onStartCommand(@NonNull Intent intent, int flags, int startId) {
        Logger.debug("Received voice recognition request");

        Notification notification = buildNotification();
        startForeground(1, notification);

        // voice recognition must be done on main thread
        new Handler(Looper.getMainLooper()).post(() -> {
            Context context = getApplicationContext();
            SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
            Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
            adaRecognitionListener = new AdaRecognitionListener(context);
            speechRecognizer.setRecognitionListener(adaRecognitionListener);
            speechRecognizer.startListening(recognizerIntent);
        });
        return START_NOT_STICKY;
    }

    @NonNull
    private Notification buildNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }

        Context applicationContext = getApplicationContext();
        Intent notificationIntent = new Intent(applicationContext, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(applicationContext,
                0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Running in foreground")
                .setSmallIcon(R.drawable.baseline_mic_24dp)
                .setContentIntent(pendingIntent)
                .build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
