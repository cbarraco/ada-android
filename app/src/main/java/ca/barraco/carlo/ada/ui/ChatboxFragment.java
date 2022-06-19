package ca.barraco.carlo.ada.ui;

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

import ca.barraco.carlo.ada.Logger;
import ca.barraco.carlo.ada.R;

public class ChatboxFragment extends Fragment {
    private static final String[] Actions = {
            AdaActions.ACTION_START_LISTENING,
            AdaActions.ACTION_SHOW_RECOGNITION_RESULT,
            AdaActions.ACTION_SHOW_REPLY,
            AdaActions.ACTION_SHOW_ERROR,
            AdaActions.ACTION_SHOW_PARTIAL_RESULT
    };
    private LinearLayout chatLayout;
    private ChatboxBroadcastReceiver chatboxBroadcastReceiver;
    private TextView currentRequestTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatbox, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        chatLayout = view.findViewById(R.id.chatLayout);
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
            if (action.equals(AdaActions.ACTION_START_LISTENING)) {
                handleStartListening();
            } else if (action.equals(AdaActions.ACTION_SHOW_RECOGNITION_RESULT)) {
                handleShowRecognitionResult(intent);
            } else if (action.equals(AdaActions.ACTION_SHOW_REPLY)) {
                handleShowReply(intent);
            } else if (action.equals(AdaActions.ACTION_SHOW_ERROR)) {
                handleShowError(intent);
            } else if (action.equals(AdaActions.ACTION_SHOW_PARTIAL_RESULT)) {
                handleShowPartialResult(intent);
            }
        }

        private void handleStartListening() {
            currentRequestTextView = createTextView("...");
            if (currentRequestTextView == null) {
                return;
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.END;
            params.topMargin = spToPixels(chatLayout.getContext(), 6);
            currentRequestTextView.setLayoutParams(params);
            currentRequestTextView.setBackgroundColor(getResources().getColor(R.color.AdaRequest));
            currentRequestTextView.setTextColor(getResources().getColor(R.color.Black));
            currentRequestTextView.setFocusable(true);
            currentRequestTextView.setFocusableInTouchMode(true);
            chatLayout.addView(currentRequestTextView);
            currentRequestTextView.requestFocus();
        }

        private void handleShowRecognitionResult(@NonNull Intent intent) {
            String message = intent.getStringExtra(AdaActions.EXTRA_MESSAGE);
            if (message == null) {
                Logger.warning("Received null message");
                return;
            }

            Logger.debug("Received message: %s", message);
            currentRequestTextView.setText(message);
        }

        private void handleShowReply(@NonNull Intent intent) {
            String message = intent.getStringExtra(AdaActions.EXTRA_MESSAGE);
            if (message == null) {
                Logger.warning("Received null message");
                return;
            }

            Logger.debug("Received message: %s", message);
            TextView replyTextView = createTextView(message);
            if (replyTextView == null) {
                return;
            }

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

        private void handleShowError(Intent intent) {
            if (currentRequestTextView != null) {
                String message = intent.getStringExtra(AdaActions.EXTRA_MESSAGE);
                if (message == null) {
                    Logger.warning("Received null message");
                    return;
                }

                Logger.debug("Received message: %s", message);

                currentRequestTextView.setText(message);
                currentRequestTextView.setBackgroundColor(getResources().getColor(R.color.RecognitionFailure));
                currentRequestTextView.setTextColor(getResources().getColor(R.color.White));
            }
        }

        private void handleShowPartialResult(Intent intent) {
            String message = intent.getStringExtra(AdaActions.EXTRA_MESSAGE);
            if (message == null) {
                Logger.warning("Received null message");
                return;
            }

            Logger.debug("Received partial results: %s", message);
            if (currentRequestTextView != null) {
                currentRequestTextView.setText(message);
            }
        }

        @Nullable
        private TextView createTextView(String message) {
            if (chatLayout != null && chatLayout.getContext() != null) {
                Context chatLayoutContext = chatLayout.getContext();
                TextView textView = new TextView(chatLayoutContext);
                int padding = spToPixels(chatLayoutContext, 6);
                textView.setPadding(padding, padding, padding, padding);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                textView.setText(message);
                return textView;
            } else {
                return null;
            }
        }

        private int spToPixels(@NonNull Context context, int sp) {
            float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
            return (int) (sp * scaledDensity);
        }
    }
}
