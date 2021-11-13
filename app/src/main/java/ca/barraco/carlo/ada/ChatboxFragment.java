package ca.barraco.carlo.ada;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ChatboxFragment extends Fragment {
    public static final String ACTION_START_LISTENING = "Ada.ACTION_START_LISTENING";
    public static final String ACTION_SHOW_RECOGNITION_RESULT = "Ada.ACTION_SHOW_RECOGNITION_RESULT";
    public static final String ACTION_SHOW_REPLY = "Ada.ACTION_SHOW_REPLY";
    public static final String ACTION_SHOW_ERROR = "Ada.ACTION_SHOW_ERROR";
    public static final String ACTION_SHOW_PARTIAL_RESULT = "Ada.ACTION_SHOW_PARTIAL_RESULT";
    public static final String EXTRA_MESSAGE = "Ada.EXTRA_MESSAGE";
    private static final String[] Actions = {
            ACTION_START_LISTENING,
            ACTION_SHOW_RECOGNITION_RESULT,
            ACTION_SHOW_REPLY,
            ACTION_SHOW_ERROR,
            ACTION_SHOW_PARTIAL_RESULT
    };
    private LinearLayout chatLayout;
    private ChatboxBroadcastReceiver chatboxBroadcastReceiver;
    private TextView currentRequestTextView;

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

    public static void showError(Context context) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(ACTION_SHOW_ERROR);
        localBroadcastManager.sendBroadcast(intent);
    }

    public static void showPartialResult(Context context, String reply) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(ACTION_SHOW_PARTIAL_RESULT);
        intent.putExtra(EXTRA_MESSAGE, reply);
        localBroadcastManager.sendBroadcast(intent);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatbox, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        chatLayout = getView().findViewById(R.id.chatLayout);
        setUpBroadcastReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        localBroadcastManager.unregisterReceiver(chatboxBroadcastReceiver);
    }

    private void setUpBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        for (String action : Actions) {
            intentFilter.addAction(action);
        }

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        chatboxBroadcastReceiver = new ChatboxBroadcastReceiver();
        localBroadcastManager.registerReceiver(chatboxBroadcastReceiver, intentFilter);
    }

    public class ChatboxBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                Logger.warning("Received null action");
                return;
            }

            Logger.debug("Handling %s", action);
            if (action.equals(ACTION_START_LISTENING)) {
                handleStartListening();
            } else if (action.equals(ACTION_SHOW_RECOGNITION_RESULT)) {
                handleShowRecognitionResult(intent);
            } else if (action.equals(ACTION_SHOW_REPLY)) {
                handleShowReply(intent);
            } else if (action.equals(ACTION_SHOW_ERROR)) {
                handleShowError();
            } else if (action.equals(ACTION_SHOW_PARTIAL_RESULT)) {
                handleShowPartialResult(intent);
            }
        }

        private void handleStartListening() {
            currentRequestTextView = createTextView("...");

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.END;
            params.topMargin = spToPixels(chatLayout.getContext(), 6);
            currentRequestTextView.setLayoutParams(params);
            currentRequestTextView.setBackgroundColor(getResources().getColor(R.color.AdaRequest));
            currentRequestTextView.setTextColor(getResources().getColor(R.color.Black));

            chatLayout.addView(currentRequestTextView);
        }

        private void handleShowRecognitionResult(@NonNull Intent intent) {
            String message = intent.getStringExtra(EXTRA_MESSAGE);
            if (message == null) {
                Logger.warning("Received null message");
                return;
            }

            Logger.debug("Received message: %s", message);
            currentRequestTextView.setText(message);
        }

        private void handleShowReply(@NonNull Intent intent) {
            String message = intent.getStringExtra(EXTRA_MESSAGE);
            if (message == null) {
                Logger.warning("Received null message");
                return;
            }

            Logger.debug("Received message: %s", message);
            TextView replyTextView = createTextView(message);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.START;
            params.topMargin = spToPixels(replyTextView.getContext(), 6);
            replyTextView.setLayoutParams(params);
            replyTextView.setBackgroundColor(getResources().getColor(R.color.HomeAssistant));
            replyTextView.setTextColor(getResources().getColor(R.color.White));
            replyTextView.setFocusable(true);
            replyTextView.setFocusableInTouchMode(true);
            chatLayout.addView(replyTextView);
            replyTextView.requestFocus();
        }

        private void handleShowError() {
            currentRequestTextView.setText("I did not hear anything");
            currentRequestTextView.setBackgroundColor(getResources().getColor(R.color.RecognitionFailure));
            currentRequestTextView.setTextColor(getResources().getColor(R.color.White));
        }

        private void handleShowPartialResult(Intent intent) {
            String message = intent.getStringExtra(EXTRA_MESSAGE);
            if (message == null) {
                Logger.warning("Received null message");
                return;
            }

            Logger.debug("Received partial results: %s", message);
            currentRequestTextView.setText(message);
        }

        @NonNull
        private TextView createTextView(String message) {
            TextView textView = new TextView(chatLayout.getContext());
            int padding = spToPixels(chatLayout.getContext(), 6);
            textView.setPadding(padding, padding, padding, padding);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            textView.setText(message);
            return textView;
        }

        private int spToPixels(@NonNull Context context, int sp) {
            float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
            return (int) (sp * scaledDensity);
        }
    }
}
