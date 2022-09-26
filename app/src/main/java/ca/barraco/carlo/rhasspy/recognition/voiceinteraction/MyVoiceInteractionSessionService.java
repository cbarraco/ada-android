package ca.barraco.carlo.rhasspy.recognition.voiceinteraction;

import android.os.Bundle;
import android.service.voice.VoiceInteractionSession;
import android.service.voice.VoiceInteractionSessionService;

public class MyVoiceInteractionSessionService extends VoiceInteractionSessionService {
    @Override
    public VoiceInteractionSession onNewSession(Bundle args) {
        return (new MyVoiceInteractionSession(this));
    }
}
