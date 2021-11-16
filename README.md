# Ada for Android

Ada is a voice assistant built into Home Assistant and provides voice control of Home Assistant integrations.

I decided to implement this Android app to make Ada more accessible and convenient to use. More specifically, I wanted to make Ada available as an Assistant app so it can be started from the Assistant button on compatible headphones.

I'm not affiliated with Home Assistant in any way, I just wanted this to exist so I made it.

Features:

- Familiar UI consistent with Ada on the Lovelace UI.
- Easy to set up, just enter Home Assistant URL and Long-lived access token in Settings.
- Voice recognition runs locally on the device, so no internet connection is required as long as Home Assistant is accessible.
- Ada is recognized by Android as an Assistant app so it can be set as the default Assistant in the Android settings app.
- Voice recognition runs as a Service, so Ada can be accessed when the screen is locked via the Assistant button on compatible headphones.

Ada needs permission to access the microphone to provide voice recognition.

Root access is not required to use this app.
