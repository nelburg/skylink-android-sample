package sg.com.temasys.skylink.sdk.sampleapp;

/**
 * Created by lavanyasudharsanam on 20/1/15.
 */

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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.temasys.skylink.sampleapp.R;

import java.util.Date;

import sg.com.temasys.skylink.sdk.listener.LifeCycleListener;
import sg.com.temasys.skylink.sdk.listener.MediaListener;
import sg.com.temasys.skylink.sdk.listener.RemotePeerListener;
import sg.com.temasys.skylink.sdk.rtc.Errors;
import sg.com.temasys.skylink.sdk.rtc.SkylinkConfig;
import sg.com.temasys.skylink.sdk.rtc.SkylinkConnection;
import sg.com.temasys.skylink.sdk.rtc.SkylinkException;
import sg.com.temasys.skylink.sdk.rtc.UserInfo;
import sg.com.temasys.skylink.sdk.sampleapp.ConfigFragment.Config;
import sg.com.temasys.skylink.sdk.sampleapp.ConfigFragment.ConfigFragment;

import static sg.com.temasys.skylink.sdk.sampleapp.Constants.ROOM_NAME_VIDEO_DEFAULT;
import static sg.com.temasys.skylink.sdk.sampleapp.Constants.USER_NAME_VIDEO_DEFAULT;

/**
 * This class is used to demonstrate the VideoCall between two clients in WebRTC
 */
public class VideoCallFragment extends Fragment
        implements LifeCycleListener, MediaListener, RemotePeerListener {

    private String ROOM_NAME;
    private String MY_USER_NAME;

    //set height width for self-video when in call
    public static final int WIDTH = 350;
    public static final int HEIGHT = 350;
    private static final String TAG = VideoCallFragment.class.getCanonicalName();
    private static final String ARG_SECTION_NUMBER = "section_number";
    // Constants for configuration change
    private static final String BUNDLE_CONNECTING = "connecting";
    private static final String BUNDLE_PEER_ID = "peerId";
    private static final String BUNDLE_AUDIO_MUTED = "audioMuted";
    private static final String BUNDLE_VIDEO_MUTED = "videoMuted";

    private static GLSurfaceView videoViewSelf;
    private static GLSurfaceView videoViewRemote;
    private static SkylinkConnection skylinkConnection;
    private LinearLayout linearLayout;
    private Button toggleAudioButton;
    private Button toggleVideoButton;
    private Button toggleCameraButton;
    private Button disconnectButton;
    private Button btnEnterRoom;
    private EditText etRoomName;
    private String connectionStatus;
    private boolean connecting = false;
    private String roomName;
    private String peerId;
    private boolean audioMuted;
    private boolean videoMuted;
    private boolean orientationChange;
    private Activity parentActivity;
    private AudioRouter audioRouter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize views
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        //Config.USER_NAME_VIDEO = sharedPref.getString("VideoUserNameSaved", null);
        String value_video_user = sharedPref.getString("VideoUserNameSaved", null);
        if (value_video_user == null) {
            Config.USER_NAME_VIDEO = USER_NAME_VIDEO_DEFAULT;
        }
        else {
            Config.USER_NAME_VIDEO = value_video_user;
        }

        //Config.ROOM_NAME_VIDEO = sharedPref.getString("VideoRoomNameSaved", null);
        String value_video_room = sharedPref.getString("VideoRoomNameSaved", null);
        if (value_video_room == null) {
            Config.ROOM_NAME_VIDEO = ROOM_NAME_VIDEO_DEFAULT;
        }
        else {
            Config.ROOM_NAME_VIDEO = value_video_room;
        }

        ROOM_NAME = Config.ROOM_NAME_VIDEO;
        MY_USER_NAME = Config.USER_NAME_VIDEO;

        View rootView = inflater.inflate(R.layout.fragment_video_call, container, false);
        linearLayout = (LinearLayout) rootView.findViewById(R.id.ll_video_call);
        btnEnterRoom = (Button) rootView.findViewById(R.id.btn_enter_room);
        etRoomName = (EditText) rootView.findViewById(R.id.et_room_name);
        etRoomName.setText(ROOM_NAME.toString());
        toggleAudioButton = (Button) rootView.findViewById(R.id.toggle_audio);
        toggleVideoButton = (Button) rootView.findViewById(R.id.toggle_video);
        toggleCameraButton = (Button) rootView.findViewById(R.id.toggle_camera);
        disconnectButton = (Button) rootView.findViewById(R.id.disconnect);

        // Check if it was an orientation change
        if (savedInstanceState != null) {
            connecting = savedInstanceState.getBoolean(BUNDLE_CONNECTING);
            // Set the appropriate UI if already connected.
            if (isConnected()) {
                // Set listeners to receive callbacks when events are triggered
                setListeners();
                peerId = savedInstanceState.getString(BUNDLE_PEER_ID, null);
                audioMuted = savedInstanceState.getBoolean(BUNDLE_AUDIO_MUTED);
                videoMuted = savedInstanceState.getBoolean(BUNDLE_VIDEO_MUTED);
                // Set the appropriate UI if already connected.
                onConnectUIChange();
                addSelfView(videoViewSelf);
                addRemoteView(peerId, videoViewRemote);
            } else if (connecting) {
                // Set listeners to receive callbacks when events are triggered
                setListeners();
                onConnectingUIChange();
                addSelfView(videoViewSelf);
            } else {
                onDisconnectUIChange();
            }
        }

        // Set UI elements
        setAudioBtnLabel(false);
        setVideoBtnLabel(false);

        btnEnterRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToRoom();
                onConnectingUIChange();
            }
        });

        toggleAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If audio is enabled, mute audio and if audio is enabled, mute it
                audioMuted = !audioMuted;
                skylinkConnection.muteLocalAudio(audioMuted);

                // Set UI and Toast.
                setAudioBtnLabel(true);
            }
        });

        toggleVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If video is enabled, mute video and if video is enabled, mute it
                videoMuted = !videoMuted;
                skylinkConnection.muteLocalVideo(videoMuted);

                // Set UI and Toast.
                setVideoBtnLabel(true);
            }
        });

        toggleCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toast = "Toggled camera ";
                try {
                    boolean restarted = skylinkConnection.toggleCamera();
                    if (restarted) {
                        toast += "to restarted!";
                    } else {
                        toast += "to stopped!";
                    }
                } catch (SkylinkException e) {
                    toast += "but failed as " + e.getMessage();
                }
                Toast.makeText(parentActivity, toast, Toast.LENGTH_SHORT).show();
            }
        });

        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(parentActivity, "Clicked Disconnect!", Toast.LENGTH_SHORT).show();
                disconnectFromRoom();
                onDisconnectUIChange();
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Allow volume to be controlled using volume keys
        parentActivity.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
        outState.putBoolean(BUNDLE_CONNECTING, connecting);
        outState.putString(BUNDLE_PEER_ID, peerId);
        outState.putBoolean(BUNDLE_AUDIO_MUTED, audioMuted);
        outState.putBoolean(BUNDLE_VIDEO_MUTED, videoMuted);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        disconnectFromRoom();
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

    /**
     * Get room name from text field (or use default if not entered),
     * and connect to that room.
     * Initializes SkylinkConnection if not initialized.
     */
    private void connectToRoom() {
            roomName = etRoomName.getText().toString();

        String toast = "";
        // If roomName is not set through the UI, get the default roomName from Constants
        if (roomName.isEmpty()) {
            roomName = ROOM_NAME;
            etRoomName.setText(roomName);
            toast = "No room name provided, entering default video room \"" + roomName
                    + "\".";
        } else {
            toast = "Entering video room \"" + roomName + "\".";
        }
        Toast.makeText(parentActivity, toast, Toast.LENGTH_SHORT).show();

        String appKey = Config.APP_KEY;
        String appSecret = Config.APP_SECRET;

        // Initialize the skylink connection
        initializeSkylinkConnection();

        // Obtaining the Skylink connection string done locally
        // In a production environment the connection string should be given
        // by an entity external to the App, such as an App server that holds the Skylink
        // App secret
        // In order to avoid keeping the App secret within the application
        String skylinkConnectionString = Utils.
                getSkylinkConnectionString(roomName,
                        appKey,
                        appSecret, new Date(),
                        SkylinkConnection
                                .DEFAULT_DURATION);

        boolean connectFailed;
        connectFailed = !skylinkConnection.connectToRoom(skylinkConnectionString, MY_USER_NAME);
        if (connectFailed) {
            Toast.makeText(parentActivity, "Unable to connect to room!", Toast.LENGTH_SHORT).show();
            return;
        }

        connecting = true;

        // Initialize and use the Audio router to switch between headphone and headset
        AudioRouter.startAudioRouting(parentActivity);
    }

    /**
     * Disconnect from room.
     */
    private void disconnectFromRoom() {
        // Close the room connection when this sample app is finished, so the streams can be closed.
        // I.e. already connecting/connected and not changing orientation.
        if (!orientationChange && skylinkConnection != null && isConnected()) {
            if (skylinkConnection.disconnectFromRoom()) {
                connecting = false;
            }
            AudioRouter.stopAudioRouting(parentActivity.getApplicationContext());
        }
    }

    private SkylinkConfig getSkylinkConfig() {
        SkylinkConfig config = new SkylinkConfig();
        //AudioVideo config options can be NO_AUDIO_NO_VIDEO, AUDIO_ONLY, VIDEO_ONLY,
        // AUDIO_AND_VIDEO;
        config.setAudioVideoSendConfig(SkylinkConfig.AudioVideoConfig.AUDIO_AND_VIDEO);
        config.setAudioVideoReceiveConfig(SkylinkConfig.AudioVideoConfig.AUDIO_AND_VIDEO);
        config.setHasPeerMessaging(true);
        config.setHasFileTransfer(true);
        config.setMirrorLocalView(true);
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
        //the app_key and app_secret is obtained from the temasys developer console.
        skylinkConnection.init(Config.APP_KEY,
                getSkylinkConfig(), this.parentActivity.getApplicationContext());
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
     * Change certain UI elements once connected to room or when Peer(s) join or leave.
     */
    private void onConnectUIChange() {
        btnEnterRoom.setVisibility(View.GONE);
        etRoomName.setEnabled(false);
        toggleAudioButton.setVisibility(View.VISIBLE);
        toggleVideoButton.setVisibility(View.VISIBLE);
        toggleCameraButton.setVisibility(View.VISIBLE);
        disconnectButton.setVisibility(View.VISIBLE);
    }

    /**
     * Change certain UI elements when trying to connect to room.
     */
    private void onConnectingUIChange() {
        btnEnterRoom.setVisibility(View.GONE);
        etRoomName.setEnabled(false);
        toggleAudioButton.setVisibility(View.GONE);
        toggleVideoButton.setVisibility(View.GONE);
        toggleCameraButton.setVisibility(View.GONE);
        disconnectButton.setVisibility(View.VISIBLE);
    }

    /**
     * Change certain UI elements when disconnecting from room.
     */
    private void onDisconnectUIChange() {
        View self = linearLayout.findViewWithTag("self");
        if (self != null) {
            linearLayout.removeView(self);
        }

        View peer = linearLayout.findViewWithTag("peer");
        if (peer != null) {
            linearLayout.removeView(peer);
        }
        videoViewRemote = null;

        btnEnterRoom.setVisibility(View.VISIBLE);
        etRoomName.setEnabled(true);
        toggleAudioButton.setVisibility(View.GONE);
        toggleVideoButton.setVisibility(View.GONE);
        toggleCameraButton.setVisibility(View.GONE);
        disconnectButton.setVisibility(View.GONE);
    }

    /**
     * Add or update our self VideoView into the app.
     *
     * @param videoView
     */
    private void addSelfView(GLSurfaceView videoView) {
        if (videoView != null) {
            // If previous self video exists,
            // Set new video to size of previous self video
            // And remove old self video.
            View self = linearLayout.findViewWithTag("self");
            if (self != null) {
                videoView.setLayoutParams(self.getLayoutParams());
                // Remove the old self video.
                linearLayout.removeView(self);
            }

            // Tag new video as self and add onClickListener.
            videoView.setTag("self");
            // Allow self view to switch between different cameras (if any) when tapped.
            videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    skylinkConnection.switchCamera();
                }
            });

            // If peer video exists, remove it first.
            View peer = linearLayout.findViewWithTag("peer");
            if (peer != null) {
                linearLayout.removeView(peer);
            }

            // Show new video on screen
            // Remove video from previous parent, if any.
            Utils.removeViewFromParent(videoView);

            // And new self video.
            linearLayout.addView(videoView);

            // Return the peer video, if it was there before.
            if (peer != null) {
                linearLayout.addView(peer);
            }
        }
    }

    /**
     * Add or update remote Peer's VideoView into the app.
     *
     * @param remotePeerId
     * @param videoView
     */
    private void addRemoteView(String remotePeerId, GLSurfaceView videoView) {
        if (videoView == null) {
            return;
        }

        // Resize self view
        View self = linearLayout.findViewWithTag("self");
        if (self != null) {
            self.setLayoutParams(new ViewGroup.LayoutParams(WIDTH, HEIGHT));
            linearLayout.removeView(self);
            linearLayout.addView(self);
        }

        // Remove previous peer video if it exists
        View viewToRemove = linearLayout.findViewWithTag("peer");
        if (viewToRemove != null) {
            linearLayout.removeView(viewToRemove);
        }

        // Add new peer video
        videoView.setTag("peer");
        // Remove view from previous parent, if any.
        Utils.removeViewFromParent(videoView);
        // Add view to parent
        linearLayout.addView(videoView);

        this.peerId = remotePeerId;
    }

    /**
     * Set the mute audio button label according to the current state of audio.
     *
     * @param doToast If true, Toast about setting audio to current state.
     */
    private void setAudioBtnLabel(boolean doToast) {
        if (audioMuted) {
            toggleAudioButton.setText(getString(R.string.enable_audio));
            if (doToast) {
                Toast.makeText(parentActivity, getString(R.string.muted_audio),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            toggleAudioButton.setText(getString(R.string.mute_audio));
            if (doToast) {
                Toast.makeText(parentActivity, getString(R.string.enabled_audio),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Set the mute video button label according to the current state of video.
     *
     * @param doToast If true, Toast about setting video to current state.
     */
    private void setVideoBtnLabel(boolean doToast) {
        if (videoMuted) {
            toggleVideoButton.setText(getString(R.string.enable_video));
            if (doToast) {
                Toast.makeText(parentActivity, getString(R.string.muted_video),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            toggleVideoButton.setText(getString(R.string.mute_video));
            if (doToast) {
                Toast.makeText(parentActivity, getString(R.string.enabled_video),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /***
     * Lifecycle Listener Callbacks -- triggered during events that happen during the SDK's
     * lifecycle
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
            connecting = false;
            onConnectUIChange();
            Toast.makeText(parentActivity, "Connected to room " + roomName + " as " + MY_USER_NAME,
                    Toast.LENGTH_SHORT).show();
        } else {
            connecting = false;
            Log.d(TAG, "Skylink failed to connect!");
            Toast.makeText(parentActivity, "Skylink failed to connect!\nReason : "
                    + message, Toast.LENGTH_SHORT).show();
            onDisconnectUIChange();
        }
    }

    @Override
    public void onDisconnect(int errorCode, String message) {
        onDisconnectUIChange();
        connecting = false;
        String log = message;
        if (errorCode == Errors.DISCONNECT_FROM_ROOM) {
            log = "[onDisconnect] We have successfully disconnected from the room. Server message: "
                    + message;
        }
        Toast.makeText(parentActivity, "[onDisconnect] " + log, Toast.LENGTH_LONG).show();
        Log.d(TAG, log);
    }

    @Override
    public void onLockRoomStatusChange(String remotePeerId, boolean lockStatus) {
        Toast.makeText(parentActivity, "Peer " + remotePeerId +
                " has changed Room locked status to " + lockStatus, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceiveLog(String message) {
        Log.d(TAG, message + " on receive log");
    }

    @Override
    public void onWarning(int errorCode, String message) {
        Log.d(TAG, message + "warning");
        Toast.makeText(parentActivity, "Warning is errorCode" + errorCode, Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * Media Listeners Callbacks - triggered when receiving changes to Media Stream from the
     * remote peer
     */

    /**
     * Triggered after the user's local media is captured.
     *
     * @param videoView
     */
    @Override
    public void onLocalMediaCapture(GLSurfaceView videoView) {
        videoViewSelf = videoView;
        addSelfView(videoView);
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
    public void onRemotePeerMediaReceive(String remotePeerId, GLSurfaceView videoView) {
        videoViewRemote = videoView;
        addRemoteView(remotePeerId, videoView);
    }

    @Override
    public void onRemotePeerAudioToggle(String remotePeerId, boolean isMuted) {
        String message = null;
        if (isMuted) {
            message = "Your peer muted their audio";
        } else {
            message = "Your peer unmuted their audio";
        }

        Toast.makeText(parentActivity, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRemotePeerVideoToggle(String peerId, boolean isMuted) {
        String message = null;
        if (isMuted) {
            message = "Your peer muted video";
        } else {
            message = "Your peer unmuted their video";
        }

        Toast.makeText(parentActivity, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Remote Peer Listener Callbacks - triggered during events that happen when data or connection
     * with remote peer changes
     */

    @Override
    public void onRemotePeerJoin(String remotePeerId, Object userData, boolean hasDataChannel) {
        Toast.makeText(parentActivity, "Your peer has just connected", Toast.LENGTH_SHORT).show();
        UserInfo remotePeerUserInfo = skylinkConnection.getUserInfo(remotePeerId);
        Log.d(TAG, "isAudioStereo " + remotePeerUserInfo.isAudioStereo());
        Log.d(TAG, "video height " + remotePeerUserInfo.getVideoHeight());
        Log.d(TAG, "video width " + remotePeerUserInfo.getVideoHeight());
        Log.d(TAG, "video frameRate " + remotePeerUserInfo.getVideoFps());
    }

    @Override
    public void onRemotePeerLeave(String remotePeerId, String message) {
        Toast.makeText(parentActivity, "Your peer has left the room", Toast.LENGTH_SHORT).show();
        if (remotePeerId != null && remotePeerId.equals(this.peerId)) {
            this.peerId = null;
            View peerView = linearLayout.findViewWithTag("peer");
            linearLayout.removeView(peerView);
            videoViewRemote = null;

            // Resize self view to better make use of screen.
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            if (videoViewSelf != null) {
                videoViewSelf.setLayoutParams(params);
                addSelfView(videoViewSelf);
            }
        }
    }

    @Override
    public void onRemotePeerUserDataReceive(String remotePeerId, Object userData) {
        Log.d(TAG, "onRemotePeerUserDataReceive " + remotePeerId);
    }

    @Override
    public void onOpenDataConnection(String peerId) {
        Log.d(TAG, "onOpenDataConnection");
    }
}
