package sg.com.temasys.skylink.sdk.sampleapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.temasys.skylink.sampleapp.R;

import java.util.Date;

import sg.com.temasys.skylink.sdk.listener.LifeCycleListener;
import sg.com.temasys.skylink.sdk.listener.MediaListener;
import sg.com.temasys.skylink.sdk.listener.RemotePeerListener;
import sg.com.temasys.skylink.sdk.rtc.Errors;
import sg.com.temasys.skylink.sdk.rtc.SkylinkConfig;
import sg.com.temasys.skylink.sdk.rtc.SkylinkConnection;
import sg.com.temasys.skylink.sdk.sampleapp.ConfigFragment.Config;
import sg.com.temasys.skylink.sdk.sampleapp.ConfigFragment.ConfigFragment;

import static sg.com.temasys.skylink.sdk.sampleapp.Constants.ROOM_NAME_AUDIO_DEFAULT;
import static sg.com.temasys.skylink.sdk.sampleapp.Constants.USER_NAME_AUDIO_DEFAULT;

/**
 * This class is used to demonstrate the AudioCall between two clients in WebRTC Created by
 * lavanyasudharsanam on 20/1/15.
 */
public class AudioCallFragment extends Fragment
        implements LifeCycleListener, MediaListener, RemotePeerListener {
    // Inflate the layout for this fragment

    private String ROOM_NAME;
    private String MY_USER_NAME;

    private static final String TAG = AudioCallFragment.class.getCanonicalName();
    private static final String ARG_SECTION_NUMBER = "section_number";

    // Constants for configuration change
    private static final String BUNDLE_IS_PEER_JOINED = "peerJoined";
    private static final String BUNDLE_PEER_ID = "peerId";
    private static final String BUNDLE_PEER_NAME = "remotePeerName";
    private static SkylinkConnection skylinkConnection;
    private TextView tvRoomDetails;
    private Button btnAudioCall;
    private String remotePeerId;
    private String remotePeerName;
    private boolean peerJoined;
    private boolean orientationChange;
    private Activity parentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_audio_call, container, false);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        tvRoomDetails = (TextView) rootView.findViewById(R.id.tv_room_details);
        btnAudioCall = (Button) rootView.findViewById(R.id.btn_audio_call);
        //Config.ROOM_NAME_AUDIO = sharedPref.getString("AudioRoomNameSaved", null);
        String value_audio_room = sharedPref.getString("AudioRoomNameSaved", null);
        if (value_audio_room == null) {
            Config.ROOM_NAME_AUDIO = ROOM_NAME_AUDIO_DEFAULT;
        }
        else {
            Config.ROOM_NAME_AUDIO = value_audio_room;
        }

        //Config.USER_NAME_AUDIO = sharedPref.getString("AudioUserNameSaved", null);
        String value_audio_user = sharedPref.getString("AudioUserNameSaved", null);
        if (value_audio_user == null) {
            Config.USER_NAME_AUDIO = USER_NAME_AUDIO_DEFAULT;
        }
        else {
            Config.USER_NAME_AUDIO = value_audio_user;
        }
        ROOM_NAME = Config.ROOM_NAME_AUDIO;
        MY_USER_NAME = Config.USER_NAME_AUDIO;

        // Check if it was an orientation change
        if (savedInstanceState != null) {
            // Set the appropriate UI if already isConnected().
            if (isConnected()) {
                // Set listeners to receive callbacks when events are triggered
                setListeners();
                peerJoined = savedInstanceState.getBoolean(BUNDLE_IS_PEER_JOINED);
                remotePeerId = savedInstanceState.getString(BUNDLE_PEER_ID, null);
                remotePeerName = savedInstanceState.getString(BUNDLE_PEER_NAME, null);
                // Set the appropriate UI if already isConnected().
                onConnectUIChange();
            } else {
                onDisconnectUIChange();
            }
        }

        btnAudioCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String appKey = Config.APP_KEY;
                String appSecret = Config.APP_SECRET;
                connectToRoom(appKey, appSecret);

                onConnectUIChange();

            }
        });
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Allow volume to be controlled using volume keys
        getActivity().setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //update actionbar title
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        parentActivity = getActivity();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Note that orientation change is happening.
        orientationChange = true;
        // Save states for fragment restart
        outState.putBoolean(BUNDLE_IS_PEER_JOINED, peerJoined);
        outState.putString(BUNDLE_PEER_ID, remotePeerId);
        outState.putString(BUNDLE_PEER_NAME, remotePeerName);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        disconnectFromRoom();
    }

    private void disconnectFromRoom() {
        // Close the room connection when this sample app is finished, so the streams can be closed.
        // I.e. already isConnected() and not changing orientation.
        if (!orientationChange && skylinkConnection != null && isConnected()) {
            skylinkConnection.disconnectFromRoom();
            AudioRouter.stopAudioRouting(parentActivity.getApplicationContext());
        }
    }

    /***
     * Skylink Helper methods
     */

    /**
     * Check if we are currently connected to the room.
     *
     * @return True if we are connected and false otherwise.
     */
    private boolean isConnected() {
        if (skylinkConnection != null) {
            return skylinkConnection.isConnected();
        }
        return false;
    }

    private void connectToRoom(String appKey, String appSecret) {
        // Initialize the skylink connection
        initializeSkylinkConnection();

        // Obtaining the Skylink connection string done locally
        // In a production environment the connection string should be given
        // by an entity external to the App, such as an App server that holds the Skylink
        // App secret
        // In order to avoid keeping the App secret within the application
        String skylinkConnectionString = Utils.
                getSkylinkConnectionString(ROOM_NAME,
                        appKey,
                        appSecret, new Date(),
                        SkylinkConnection
                                .DEFAULT_DURATION);

        boolean connectFailed;
        connectFailed = !skylinkConnection.connectToRoom(skylinkConnectionString, MY_USER_NAME);
        if (connectFailed) {
            Toast.makeText(parentActivity, "Unable to connect to room!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(parentActivity, "Connecting....",
                    Toast.LENGTH_SHORT).show();
        }

        // Initialize and use the Audio router to switch between headphone and headset
        AudioRouter.startAudioRouting(parentActivity);

    }

    private SkylinkConfig getSkylinkConfig() {
        SkylinkConfig config = new SkylinkConfig();
        config.setAudioVideoSendConfig(SkylinkConfig.AudioVideoConfig.AUDIO_ONLY);
        config.setAudioVideoReceiveConfig(SkylinkConfig.AudioVideoConfig.AUDIO_ONLY);
        config.setHasPeerMessaging(true);
        config.setHasFileTransfer(true);
        config.setTimeout(ConfigFragment.TIME_OUT);
        // To enable logs from Skylink SDK (e.g. during debugging),
        // Uncomment the following. Do not enable logs for production apps!
        // config.setEnableLogs(true);

        // Allow only 1 remote Peer to join.
        config.setMaxPeers(1);

        return config;
    }

    private void initializeSkylinkConnection() {
        skylinkConnection = SkylinkConnection.getInstance();
        skylinkConnection.init(Config.APP_KEY, getSkylinkConfig(),
                this.parentActivity.getApplicationContext());

        // Set listeners to receive callbacks when events are triggered
        setListeners();
    }

    /**
     * Set listeners to receive callbacks when events are triggered.
     * SkylinkConnection instance must not be null or listeners cannot be set.
     *
     * @return false if listeners could not be set.
     */
    private boolean setListeners() {
        if (skylinkConnection != null) {
            skylinkConnection.setLifeCycleListener(this);
            skylinkConnection.setMediaListener(this);
            skylinkConnection.setRemotePeerListener(this);
            return true;
        } else {
            return false;
        }
    }


    /***
     * UI helper methods
     */

    /**
     * Change certain UI elements once isConnected() to room or when Peer(s) join or leave.
     */
    private void onConnectUIChange() {
        btnAudioCall.setEnabled(false);
        Utils.setRoomDetails(isConnected(), peerJoined, tvRoomDetails, remotePeerName, ROOM_NAME,
                MY_USER_NAME);
    }

    /**
     * Change certain UI elements when disconnecting from room.
     */
    private void onDisconnectUIChange() {
        btnAudioCall.setEnabled(true);
        Utils.setRoomDetails(isConnected(), peerJoined, tvRoomDetails, remotePeerName, ROOM_NAME,
                MY_USER_NAME);
    }

    /***
     * Lifecycle Listener
     */

    /**
     * Triggered when connection is successful
     *
     * @param isSuccess
     * @param message
     */

    @Override
    public void onConnect(boolean isSuccess, String message) {
        if (isSuccess) {
            Log.d(TAG, "Skylink Connected");
            Utils.setRoomDetails(isConnected(), peerJoined, tvRoomDetails, remotePeerName, ROOM_NAME,
                    MY_USER_NAME);
        } else {
            Log.d(TAG, "Skylink failed to connect!");
            Toast.makeText(parentActivity, "Skylink failed to connect!\nReason : "
                    + message, Toast.LENGTH_SHORT).show();
            onDisconnectUIChange();
        }
    }

    @Override
    public void onWarning(int errorCode, String message) {
        Log.d(TAG, message + "warning");
    }

    @Override
    public void onDisconnect(int errorCode, String message) {
        onDisconnectUIChange();

        String log = message;
        if (errorCode == Errors.DISCONNECT_FROM_ROOM) {
            log = "[onDisconnect] We have successfully disconnected from the room. Server message: "
                    + message;
        }
        Toast.makeText(parentActivity, "[onDisconnect] " + log, Toast.LENGTH_LONG).show();
        Log.d(TAG, log);
    }

    @Override
    public void onReceiveLog(String message) {
        Log.d(TAG, message + "onReceiveLog");
    }

    @Override
    public void onLockRoomStatusChange(String remotePeerId, boolean lockStatus) {
        Toast.makeText(parentActivity, "Peer " + remotePeerId +
                " has changed Room locked status to " + lockStatus, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocalMediaCapture(GLSurfaceView glSurfaceView) {
        Log.d(TAG, "onLocalMediaCapture");
    }

    @Override
    public void onVideoSizeChange(String peerId, Point size) {
        String peer = "Peer " + peerId;
        // If peerId is null, this call is for our local video.
        if (peerId == null) {
            peer = "We've";
        }
        Log.d(TAG, peer + " got video size changed to: " + size.toString() + ".");
    }

    @Override
    public void onRemotePeerAudioToggle(String s, boolean b) {
        Log.d(TAG, "onRemotePeerAudioToggle");
    }

    @Override
    public void onRemotePeerVideoToggle(String s, boolean b) {
        Log.d(TAG, "onRemotePeerVideoToggle");
    }

    @Override
    public void onRemotePeerMediaReceive(String s, GLSurfaceView glSurfaceView) {
        Log.d(TAG, "onRemotePeerMediaReceive");
    }

    @Override
    public void onRemotePeerJoin(String remotePeerId, Object userData, boolean hasDataChannel) {
        // When remote peer joins room, keep track of user and update text-view to display details
        this.remotePeerId = remotePeerId;
        peerJoined = true;
        if (userData instanceof String) {
            remotePeerName = (String) userData;
        }
        Utils.setRoomDetails(isConnected(), peerJoined, tvRoomDetails, remotePeerName, ROOM_NAME,
                MY_USER_NAME);
    }

    @Override
    public void onRemotePeerUserDataReceive(String s, Object o) {
        Log.d(TAG, "onRemotePeerUserDataReceive");
    }

    @Override
    public void onOpenDataConnection(String s) {
        Log.d(TAG, "onOpenDataConnection");

    }

    @Override
    public void onRemotePeerLeave(String remotePeerId, String message) {
        Toast.makeText(parentActivity, "Your peer has left the room", Toast.LENGTH_SHORT).show();
        //reset peerId
        peerJoined = false;
        this.remotePeerId = null;
        remotePeerName = null;
        //update textview to show room status
        Utils.setRoomDetails(isConnected(), peerJoined, tvRoomDetails, remotePeerName, ROOM_NAME,
                MY_USER_NAME);
    }
}
