package ca.barraco.carlo.ada.voiceinteraction;

import android.content.Context;
import android.content.Intent;
import android.service.voice.VoiceInteractionSession;

import androidx.annotation.NonNull;

import ca.barraco.carlo.ada.Logger;
import ca.barraco.carlo.ada.MainActivity;

public class AdaVoiceInteractionSession extends VoiceInteractionSession {

    public AdaVoiceInteractionSession(Context context) {
        super(context);
    }

    @Override
    public void onHandleAssist(@NonNull AssistState state) {
        super.onHandleAssist(state);
        try {
            Logger.information("Starting MainActivity for handling assist");
            Intent intent = new Intent(getContext(), MainActivity.class)
                    .setAction("ca.barraco.carlo.ada.MAIN")
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startVoiceActivity(intent);
        } catch (Exception exception) {
            Logger.error("Error starting MainActivity when handling assist", exception);
        }
    }
}
