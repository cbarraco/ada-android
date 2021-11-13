package ca.barraco.carlo.ada.assistant;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.voice.VoiceInteractionSession;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import ca.barraco.carlo.ada.Logger;
import ca.barraco.carlo.ada.MainActivity;
import ca.barraco.carlo.ada.R;

public class AdaVoiceInteractionSession extends VoiceInteractionSession {

    private View view;

    public AdaVoiceInteractionSession(Context context) {
        super(context);
    }

//    @Override
//    public void onHandleAssist(@NonNull AssistState state) {
//        super.onHandleAssist(state);
//        try {
//            Intent intent = new Intent(getContext(), MainActivity.class)
//                    .setAction("ca.barraco.carlo.ada.MAIN")
//                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            Logger.information("Starting MainActivity for handling assistant button");
//            startVoiceActivity(intent);
//        } catch (Exception exception) {
//            Logger.error("Error starting MainActivity when handling assistant button", exception);
//        }
//    }

    @Override
    public View onCreateContentView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        view = inflater.inflate(R.layout.fragment_chatbox, null);
        return view;
    }

    @Override
    public void onShow(Bundle args, int showFlags) {
        view.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHide() {
        view.setVisibility(View.GONE);
    }
}
