<<<<<<<< HEAD:app/src/main/java/ca/barraco/carlo/rhasspy/recognition/voiceinteraction/MyVoiceInteractionSession.java
package ca.barraco.carlo.rhasspy.recognition.voiceinteraction;
========
package ca.barraco.carlo.ada.recognition.voiceinteraction;
>>>>>>>> 4aee302ab6d01e5b14631b0f01f397b4aeea01dc:app/src/main/java/ca/barraco/carlo/ada/recognition/voiceinteraction/AdaVoiceInteractionSession.java

import android.content.Context;
import android.content.Intent;
import android.service.voice.VoiceInteractionSession;

import androidx.annotation.NonNull;

<<<<<<<< HEAD:app/src/main/java/ca/barraco/carlo/rhasspy/recognition/voiceinteraction/MyVoiceInteractionSession.java
import ca.barraco.carlo.rhasspy.Logger;
import ca.barraco.carlo.rhasspy.ui.MainActivity;
========
import ca.barraco.carlo.ada.Logger;
import ca.barraco.carlo.ada.ui.MainActivity;
>>>>>>>> 4aee302ab6d01e5b14631b0f01f397b4aeea01dc:app/src/main/java/ca/barraco/carlo/ada/recognition/voiceinteraction/AdaVoiceInteractionSession.java

public class MyVoiceInteractionSession extends VoiceInteractionSession {

    public MyVoiceInteractionSession(Context context) {
        super(context);
    }

    @Override
    public void onHandleAssist(@NonNull AssistState state) {
        super.onHandleAssist(state);
        try {
            Logger.information("Starting MainActivity for handling assist");
            Intent intent = new Intent(getContext(), MainActivity.class)
                    .setAction("ca.barraco.carlo.rhasspy.MAIN")
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startVoiceActivity(intent);
        } catch (Exception exception) {
            Logger.error("Error starting MainActivity when handling assist", exception);
        }
    }
}
