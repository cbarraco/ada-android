<<<<<<<< HEAD:app/src/main/java/ca/barraco/carlo/rhasspy/recognition/voiceinteraction/MyVoiceInteractionSessionService.java
package ca.barraco.carlo.rhasspy.recognition.voiceinteraction;
========
package ca.barraco.carlo.ada.recognition.voiceinteraction;
>>>>>>>> 4aee302ab6d01e5b14631b0f01f397b4aeea01dc:app/src/main/java/ca/barraco/carlo/ada/recognition/voiceinteraction/AdaVoiceInteractionSessionService.java

import android.os.Bundle;
import android.service.voice.VoiceInteractionSession;
import android.service.voice.VoiceInteractionSessionService;

public class MyVoiceInteractionSessionService extends VoiceInteractionSessionService {
    @Override
    public VoiceInteractionSession onNewSession(Bundle args) {
        return (new MyVoiceInteractionSession(this));
    }
}
