package ca.barraco.carlo.ada;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class AdaActions {
    public static final String ACTION_START_LISTENING = "Ada.ACTION_START_LISTENING";
    public static final String ACTION_SHOW_RECOGNITION_RESULT = "Ada.ACTION_SHOW_RECOGNITION_RESULT";
    public static final String ACTION_SHOW_REPLY = "Ada.ACTION_SHOW_REPLY";
    public static final String ACTION_SHOW_ERROR = "Ada.ACTION_SHOW_ERROR";
    public static final String ACTION_SHOW_PARTIAL_RESULT = "Ada.ACTION_SHOW_PARTIAL_RESULT";
    public static final String EXTRA_MESSAGE = "Ada.EXTRA_MESSAGE";

    private AdaActions() {
        // hidden constructor
    }

    public static void startListening(Context context) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(ACTION_START_LISTENING);
        localBroadcastManager.sendBroadcast(intent);
    }

    public static void showRecognitionResult(Context context, String reply) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(ACTION_SHOW_RECOGNITION_RESULT);
        intent.putExtra(EXTRA_MESSAGE, reply);
        localBroadcastManager.sendBroadcast(intent);
    }

    public static void showReply(Context context, String reply) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(ACTION_SHOW_REPLY);
        intent.putExtra(EXTRA_MESSAGE, reply);
        localBroadcastManager.sendBroadcast(intent);
    }

    public static void showError(Context context, String message) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(ACTION_SHOW_ERROR);
        intent.putExtra(EXTRA_MESSAGE, message);
        localBroadcastManager.sendBroadcast(intent);
    }

    public static void showPartialResult(Context context, String reply) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(ACTION_SHOW_PARTIAL_RESULT);
        intent.putExtra(EXTRA_MESSAGE, reply);
        localBroadcastManager.sendBroadcast(intent);
    }
}
