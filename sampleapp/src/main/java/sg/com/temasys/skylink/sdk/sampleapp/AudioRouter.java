package sg.com.temasys.skylink.sdk.sampleapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.util.Log;


/**
 * Simple AudioRouter that switches between the speaker phone and the headset
 */
public class AudioRouter {

    private static AudioRouter instance = null;

    private final String TAG = AudioRouter.class.getName();
    private static BroadcastReceiver headsetBroadcastReceiver;

    private static AudioManager audioManager;

    private AudioRouter() {
        headsetBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                    int state = intent.getIntExtra("state", -1);
                    switch (state) {
                        case 0:
                            Log.d(TAG, "Headset: Unplugged");
                            break;
                        case 1:
                            Log.d(TAG, "Headset: Plugged");
                            break;
                        default:
                            Log.d(TAG, "Headset: Error determining state!");
                    }
                    // Reset audio path
                    setAudioPath();
                }
            }
        };
    }

    /**
     * Gets an instance of the AudioRouter
     *
     * @return
     */
    public static synchronized AudioRouter getInstance() {
        if (instance == null) {
            instance = new AudioRouter();
        }
        return instance;
    }

    /**
     * Initialize the Audio router
     *
     * @param audioManager
     */
    public void init(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    /**
     * Initialize AudioRouter and
     * start routing audio to headset if plugged, else to the speaker phone
     */
    public static void startAudioRouting(Activity parentActivity) {
        initializeAudioRouter(parentActivity);
        Context context = parentActivity.getApplicationContext();
        context.registerReceiver(headsetBroadcastReceiver,
                new IntentFilter(Intent.ACTION_HEADSET_PLUG));
        setAudioPath();
    }

    /**
     * Stop routing audio
     */
    public static void stopAudioRouting(Context context) {
        if (instance != null && context != null) {
            context.unregisterReceiver(headsetBroadcastReceiver);
            instance = null;
        }
    }

    /**
     * Set the audio path according to whether earphone is connected. Use ear piece if earphone is
     * connected. Use speakerphone if no earphone is connected.
     */
    private static void setAudioPath() {
        if (audioManager == null) {
            throw new RuntimeException(
                    "Attempt to set audio path before setting AudioManager");
        }
        boolean isWiredHeadsetOn = audioManager.isWiredHeadsetOn();
        if (isWiredHeadsetOn) {
            audioManager.setSpeakerphoneOn(false);
        } else {
            audioManager.setSpeakerphoneOn(true);
        }
    }

    static void initializeAudioRouter(Activity parentActivity) {
        if (instance == null) {
            getInstance();
            instance.init(((AudioManager) parentActivity.
                    getSystemService(Context.AUDIO_SERVICE)));
        }
    }
}