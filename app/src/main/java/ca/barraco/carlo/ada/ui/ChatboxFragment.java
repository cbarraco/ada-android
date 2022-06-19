package ca.barraco.carlo.ada.ui;

import android.content.Context;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import ca.barraco.carlo.ada.Logger;
import ca.barraco.carlo.ada.R;
import ca.barraco.carlo.ada.events.ShowPartialResultEvent;
import ca.barraco.carlo.ada.events.ShowErrorEvent;
import ca.barraco.carlo.ada.events.ShowRecognitionEvent;
import ca.barraco.carlo.ada.events.ShowReplyEvent;
import ca.barraco.carlo.ada.events.StartListeningEvent;

public class ChatboxFragment extends Fragment {
    private LinearLayout chatLayout;
    private TextView currentRequestTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatbox, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        chatLayout = view.findViewById(R.id.chatLayout);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleStartListening(StartListeningEvent startListeningEvent) {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleShowRecognitionResult(@NonNull ShowRecognitionEvent showRecognitionEvent) {
        Logger.debug("Received message: %s", showRecognitionEvent.getMessage());
        currentRequestTextView.setText(showRecognitionEvent.getMessage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleShowReply(@NonNull ShowReplyEvent showReplyEvent) {
        Logger.debug("Received message: %s", showReplyEvent.getReply());
        TextView replyTextView = createTextView(showReplyEvent.getReply());
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleShowError(ShowErrorEvent showErrorEvent) {
        if (currentRequestTextView != null) {
            Logger.debug("Received message: %s", showErrorEvent.getMessage());
            currentRequestTextView.setText(showErrorEvent.getMessage());
            currentRequestTextView.setBackgroundColor(getResources().getColor(R.color.RecognitionFailure));
            currentRequestTextView.setTextColor(getResources().getColor(R.color.White));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleShowPartialResult(@NonNull ShowPartialResultEvent showPartialResultEvent) {
        Logger.debug("Received partial results: %s", showPartialResultEvent.getMessage());
        if (currentRequestTextView != null) {
            currentRequestTextView.setText(showPartialResultEvent.getMessage());
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
