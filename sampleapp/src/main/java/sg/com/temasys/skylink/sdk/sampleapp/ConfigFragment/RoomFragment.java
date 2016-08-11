package sg.com.temasys.skylink.sdk.sampleapp.ConfigFragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.temasys.skylink.sampleapp.R;

import sg.com.temasys.skylink.sdk.sampleapp.Constants;

import static sg.com.temasys.skylink.sdk.sampleapp.Constants.ROOM_NAME_AUDIO_DEFAULT;
import static sg.com.temasys.skylink.sdk.sampleapp.Constants.ROOM_NAME_CHAT_DEFAULT;
import static sg.com.temasys.skylink.sdk.sampleapp.Constants.ROOM_NAME_DATA_DEFAULT;
import static sg.com.temasys.skylink.sdk.sampleapp.Constants.ROOM_NAME_FILE_DEFAULT;
import static sg.com.temasys.skylink.sdk.sampleapp.Constants.ROOM_NAME_MULTI_DEFAULT;
import static sg.com.temasys.skylink.sdk.sampleapp.Constants.ROOM_NAME_VIDEO_DEFAULT;
import static sg.com.temasys.skylink.sdk.sampleapp.Constants.USER_NAME_AUDIO_DEFAULT;
import static sg.com.temasys.skylink.sdk.sampleapp.Constants.USER_NAME_CHAT_DEFAULT;
import static sg.com.temasys.skylink.sdk.sampleapp.Constants.USER_NAME_DATA_DEFAULT;
import static sg.com.temasys.skylink.sdk.sampleapp.Constants.USER_NAME_FILE_DEFAULT;
import static sg.com.temasys.skylink.sdk.sampleapp.Constants.USER_NAME_MULTI_DEFAULT;
import static sg.com.temasys.skylink.sdk.sampleapp.Constants.USER_NAME_VIDEO_DEFAULT;

/**
 * A simple {@link Fragment} subclass.
 */
public class RoomFragment extends Fragment {

    private EditText edtAudioRoomName;
    private EditText edtAudioUserName;

    private EditText edtVideoUserName;
    private EditText edtVideoRoomName;

    private EditText edtChatUserName;
    private EditText edtChatRoomName;

    private EditText edtMultipartyVideoUserName;
    private EditText edtMultipartyVideoRoomName;

    private EditText edtDataTransferUserName;
    private EditText edtDataTransferRoomName;

    private EditText edtFileTransferUserName;
    private EditText edtFileTransferRoomName;

    private String AudioRoomName;
    private String AudioUserName;

    private String VideoUserName;
    private String VideoRoomName;

    private String ChatUserName;
    private String ChatRoomName;

    private String MultipartyVideoUserName;
    private String MultipartyVideoRoomName;

    private String DataTransferUserName;
    private String DataTransferRoomName;

    private String FileTransferUserName;
    private String FileTransferRoomName;

    public RoomFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        View view = inflater.inflate(R.layout.fragment_room, container, false);

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

        //Config.USER_NAME_CHAT = sharedPref.getString("ChatUserNameSaved", null);
        String value_chat_user = sharedPref.getString("ChatUserNameSaved", null);
        if (value_chat_user == null) {
            Config.USER_NAME_CHAT = USER_NAME_CHAT_DEFAULT;
        }
        else {
            Config.USER_NAME_CHAT = value_chat_user;
        }

        //Config.ROOM_NAME_CHAT = sharedPref.getString("ChatRoomNameSaved", null);
        String value_chat_room = sharedPref.getString("ChatRoomNameSaved", null);
        if (value_chat_room == null) {
            Config.ROOM_NAME_CHAT = ROOM_NAME_CHAT_DEFAULT;
        }
        else {
            Config.ROOM_NAME_CHAT = value_chat_room;
        }

        //Config.USER_NAME_MULTI = sharedPref.getString("MultipartyVideoUserNameSaved", null);
        String value_multi_user = sharedPref.getString("MultipartyVideoUserNameSaved", null);
        if (value_multi_user == null) {
            Config.USER_NAME_MULTI = USER_NAME_MULTI_DEFAULT;
        }
        else {
            Config.USER_NAME_MULTI = value_multi_user;
        }

        //Config.ROOM_NAME_MULTI = sharedPref.getString("MultipartyVideoRoomNameSaved", null);
        String value_multi_room = sharedPref.getString("MultipartyVideoRoomNameSaved", null);
        if (value_multi_room == null) {
            Config.ROOM_NAME_MULTI = ROOM_NAME_MULTI_DEFAULT;
        }
        else {
            Config.ROOM_NAME_MULTI = value_multi_room;
        }

        //Config.USER_NAME_DATA = sharedPref.getString("DataTransferUserNameSaved", null);
        String value_data_user = sharedPref.getString("DataTransferUserNameSaved", null);
        if (value_data_user == null) {
            Config.USER_NAME_DATA = USER_NAME_DATA_DEFAULT;
        }
        else {
            Config.USER_NAME_DATA = value_data_user;
        }

        //Config.ROOM_NAME_DATA = sharedPref.getString("DataTransferRoomNameSaved", null);
        String value_data_room = sharedPref.getString("DataTransferRoomNameSaved", null);
        if (value_data_room == null) {
            Config.ROOM_NAME_DATA = ROOM_NAME_DATA_DEFAULT;
        }
        else {
            Config.ROOM_NAME_DATA = value_data_room;
        }

        //Config.USER_NAME_FILE = sharedPref.getString("FileTransferUserNameSaved", null);
        String value_file_user = sharedPref.getString("FileTransferUserNameSaved", null);
        if (value_file_user == null) {
            Config.USER_NAME_FILE = USER_NAME_FILE_DEFAULT;
        }
        else {
            Config.USER_NAME_FILE = value_file_user;
        }

        //Config.ROOM_NAME_FILE = sharedPref.getString("FileTransferRoomNameSaved", null);
        String value_file_room = sharedPref.getString("FileTransferRoomNameSaved", null);
        if (value_file_room == null) {
            Config.ROOM_NAME_FILE = ROOM_NAME_FILE_DEFAULT;
        }
        else {
            Config.ROOM_NAME_FILE = value_file_room;
        }

        // Chat Function Value Set
        edtChatUserName = (EditText) view.findViewById(R.id.edtChatUserName);
        edtChatUserName.setText(Config.USER_NAME_CHAT);

        edtChatRoomName = (EditText) view.findViewById(R.id.edtChatRoomName);
        edtChatRoomName.setText(Config.ROOM_NAME_CHAT);

        // Audio Function Value Set
        edtAudioRoomName = (EditText) view.findViewById(R.id.edtAudioRoomName);
        edtAudioRoomName.setText(Config.ROOM_NAME_AUDIO);

        edtAudioUserName = (EditText) view.findViewById(R.id.edtAudioUserName);
        edtAudioUserName.setText(Config.USER_NAME_AUDIO);

        // Video Function Value Set
        edtVideoUserName = (EditText) view.findViewById(R.id.edtVideoUserName);
        edtVideoUserName.setText(Config.USER_NAME_VIDEO);

        edtVideoRoomName = (EditText) view.findViewById(R.id.edtVideoRoomName);
        edtVideoRoomName.setText(Config.ROOM_NAME_VIDEO);

        // Data Transfer Function Value Set
        edtDataTransferUserName = (EditText) view.findViewById(R.id.edtDataTransferUserName);
        edtDataTransferUserName.setText(Config.USER_NAME_DATA);

        edtDataTransferRoomName = (EditText) view.findViewById(R.id.edtDataTransferRoomName);
        edtDataTransferRoomName.setText(Config.ROOM_NAME_DATA);

        // File Transfer Function Value Set
        edtFileTransferUserName = (EditText) view.findViewById(R.id.edtFileTransferUserName);
        edtFileTransferUserName.setText(Config.USER_NAME_FILE);

        edtFileTransferRoomName = (EditText) view.findViewById(R.id.edtFileTransferRoomName);
        edtFileTransferRoomName.setText(Config.ROOM_NAME_FILE);

        // Multi Party Video Function Value Set
        edtMultipartyVideoUserName = (EditText) view.findViewById(R.id.edtMultipartyVideoUserName);
        edtMultipartyVideoUserName.setText(Config.USER_NAME_MULTI);

        edtMultipartyVideoRoomName = (EditText) view.findViewById(R.id.edtMultipartyVideoRoomName);
        edtMultipartyVideoRoomName.setText(Config.ROOM_NAME_MULTI);

        //Audio Call Room And User Name Config

        edtAudioRoomName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AudioRoomName = edtAudioRoomName.getText().toString().trim();
                if (AudioRoomName.equals("")) {
                    AudioRoomName = ROOM_NAME_AUDIO_DEFAULT;
                    edtAudioRoomName.setText(AudioRoomName);
                }
                if (!hasFocus) {
                    Log.e("AudioRoomName", AudioRoomName);
                    Config.ROOM_NAME_AUDIO = AudioRoomName;
                    editor.putString("AudioRoomNameSaved", AudioRoomName).commit();
                }
            }
        });

        edtAudioUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AudioUserName = edtAudioUserName.getText().toString().trim();
                if (AudioUserName.equals("")) {
                    AudioUserName = USER_NAME_AUDIO_DEFAULT;
                    edtAudioUserName.setText(AudioUserName);
                }
                if (!hasFocus) {
                    Config.USER_NAME_AUDIO = AudioUserName;
                    Log.e("AudioUserName", AudioUserName);
                    editor.putString("AudioUserNameSaved", AudioUserName).commit();
                }
            }
        });

        //Video Call Room And User Name Config
        edtVideoUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                VideoUserName = edtVideoUserName.getText().toString().trim();
                if (VideoUserName.equals("")) {
                    VideoUserName = USER_NAME_VIDEO_DEFAULT;
                    edtVideoUserName.setText(VideoUserName);
                }
                if (!hasFocus) {
                    Log.e("VideoUserName", VideoUserName);
                    Config.USER_NAME_VIDEO = VideoUserName;
                    editor.putString("VideoUserNameSaved", VideoUserName).commit();
                }
            }
        });
        edtVideoRoomName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                VideoRoomName = edtVideoRoomName.getText().toString().trim();
                if (VideoRoomName.equals("")) {
                    VideoRoomName = ROOM_NAME_VIDEO_DEFAULT;
                    edtVideoRoomName.setText(VideoRoomName);
                }
                if (!hasFocus) {
                    Log.e("VideoRoomName", VideoRoomName);
                    Config.ROOM_NAME_VIDEO = VideoRoomName;
                    editor.putString("VideoRoomNameSaved", VideoRoomName).commit();
                }
            }
        });


        //Chat Room And User Name Config
        edtChatUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ChatUserName = edtChatUserName.getText().toString().trim();
                if (ChatUserName.equals("")) {
                    ChatUserName = USER_NAME_CHAT_DEFAULT;
                    edtChatUserName.setText(ChatUserName);
                }
                if (!hasFocus) {
                    Log.e("ChatUserName", ChatUserName);
                    Config.USER_NAME_CHAT = ChatUserName;
                    editor.putString("ChatUserNameSaved", ChatUserName).commit();
                }
            }
        });

        edtChatRoomName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ChatRoomName = edtChatRoomName.getText().toString().trim();
                if (ChatRoomName == "") {
                    ChatRoomName = ROOM_NAME_CHAT_DEFAULT;
                    edtChatRoomName.setText(ChatRoomName);
                }
                if (!hasFocus) {
                    Log.e("ChatRoomName", ChatRoomName);
                    Config.ROOM_NAME_CHAT = ChatRoomName;
                    editor.putString("ChatRoomNameSaved", ChatRoomName).commit();
                }
            }
        });

        //Multiparty Video Call Room And User Name Config
        edtMultipartyVideoUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                MultipartyVideoUserName = edtMultipartyVideoUserName.getText().toString().trim();
                if (MultipartyVideoUserName.equals("")) {
                    MultipartyVideoUserName = USER_NAME_MULTI_DEFAULT;
                    edtMultipartyVideoUserName.setText(MultipartyVideoUserName);
                }
                if (!hasFocus) {
                    Log.e("MultipartyVideoUserName", MultipartyVideoUserName);
                    Config.USER_NAME_MULTI = MultipartyVideoUserName;
                    editor.putString("MultipartyVideoUserNameSaved", MultipartyVideoUserName).commit();
                }
            }
        });

        edtMultipartyVideoRoomName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                MultipartyVideoRoomName = edtMultipartyVideoRoomName.getText().toString().trim();
                if (MultipartyVideoRoomName.equals("")) {
                    MultipartyVideoRoomName = ROOM_NAME_MULTI_DEFAULT;
                    edtMultipartyVideoRoomName.setText(MultipartyVideoRoomName);
                }
                if (!hasFocus) {
                    Log.e("MultipartyVideoRoomName", MultipartyVideoRoomName);
                    Config.ROOM_NAME_MULTI = MultipartyVideoRoomName;
                    editor.putString("MultipartyVideoRoomNameSaved", MultipartyVideoRoomName).commit();
                }
            }
        });

        //DataTransfer Room And User Name Config
        edtDataTransferUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                DataTransferUserName = edtDataTransferUserName.getText().toString().trim();
                if (DataTransferUserName.equals("")) {
                    DataTransferUserName = Constants.USER_NAME_DATA_DEFAULT;
                    edtDataTransferUserName.setText(DataTransferUserName);
                }
                if (!hasFocus) {
                    Log.e("DataTransferUserName", DataTransferUserName);
                    Config.USER_NAME_DATA = DataTransferUserName;
                    editor.putString("DataTransferUserNameSaved", DataTransferUserName).commit();
                }
            }
        });

        edtDataTransferRoomName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                DataTransferRoomName = edtDataTransferRoomName.getText().toString().trim();
                if (DataTransferRoomName.equals("")) {
                    DataTransferRoomName = ROOM_NAME_DATA_DEFAULT;
                    edtDataTransferRoomName.setText(DataTransferRoomName);
                }
                if (!hasFocus) {
                    Log.e("DataTransferRoomName", DataTransferRoomName);
                    Config.ROOM_NAME_DATA = DataTransferRoomName;
                    editor.putString("DataTransferRoomNameSaved", DataTransferRoomName).commit();
                }
            }
        });

        //FileTransfer Room And User Name Config
        edtFileTransferUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                FileTransferUserName = edtFileTransferUserName.getText().toString().trim();
                if (FileTransferUserName.equals("")) {
                    FileTransferUserName = USER_NAME_FILE_DEFAULT;
                    edtFileTransferUserName.setText(FileTransferUserName);
                }
                if (!hasFocus) {
                    Log.e("FileTransferUserName", FileTransferUserName);
                    Config.USER_NAME_FILE = FileTransferUserName;
                    editor.putString("FileTransferUserNameSaved", FileTransferUserName).commit();
                }
            }
        });

        edtFileTransferRoomName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                FileTransferRoomName = edtFileTransferRoomName.getText().toString().trim();
                if (FileTransferRoomName.equals("")) {
                    FileTransferRoomName = ROOM_NAME_FILE_DEFAULT;
                    edtFileTransferRoomName.setText(FileTransferRoomName);
                }
                if (!hasFocus) {
                    Log.e("FileTransferRoomName", FileTransferRoomName);
                    Config.ROOM_NAME_FILE = FileTransferRoomName;
                    editor.putString("FileTransferRoomNameSaved", FileTransferRoomName).commit();

                }
            }
        });

        final Button button = (Button) view.findViewById(R.id.btnResetDefault);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                edtFileTransferUserName.setText(USER_NAME_FILE_DEFAULT);
                edtChatUserName.setText(USER_NAME_CHAT_DEFAULT);
                edtAudioUserName.setText(USER_NAME_AUDIO_DEFAULT);
                edtVideoUserName.setText(USER_NAME_VIDEO_DEFAULT);
                edtMultipartyVideoUserName.setText(USER_NAME_MULTI_DEFAULT);
                edtDataTransferUserName.setText(Constants.USER_NAME_DATA_DEFAULT);
                edtChatRoomName.setText(ROOM_NAME_CHAT_DEFAULT);
                edtAudioRoomName.setText(ROOM_NAME_AUDIO_DEFAULT);
                edtVideoRoomName.setText(ROOM_NAME_VIDEO_DEFAULT);
                edtDataTransferRoomName.setText(ROOM_NAME_DATA_DEFAULT);
                edtFileTransferRoomName.setText(ROOM_NAME_FILE_DEFAULT);
                edtMultipartyVideoRoomName.setText(ROOM_NAME_MULTI_DEFAULT);

                ChatUserName = edtChatUserName.getText().toString();
                Config.USER_NAME_CHAT = ChatUserName;
                editor.putString("ChatUserNameSaved", ChatUserName).commit();

                ChatRoomName = edtChatRoomName.getText().toString();
                Config.ROOM_NAME_CHAT = ChatRoomName;
                editor.putString("ChatRoomNameSaved", ChatRoomName).commit();

                AudioRoomName = edtAudioRoomName.getText().toString();
                Config.ROOM_NAME_AUDIO = AudioRoomName;
                editor.putString("AudioRoomNameSaved", AudioRoomName).commit();

                AudioUserName = edtAudioUserName.getText().toString();
                Config.USER_NAME_AUDIO = AudioUserName;
                editor.putString("AudioUserNameSaved", AudioUserName).commit();

                VideoUserName = edtVideoUserName.getText().toString();
                Config.USER_NAME_VIDEO = VideoUserName;
                editor.putString("VideoUserNameSaved", VideoUserName).commit();

                VideoRoomName = edtVideoRoomName.getText().toString();
                Config.ROOM_NAME_VIDEO = VideoRoomName;
                editor.putString("VideoRoomNameSaved", VideoRoomName).commit();

                DataTransferUserName = edtDataTransferUserName.getText().toString();
                Config.USER_NAME_DATA = DataTransferUserName;
                editor.putString("DataTransferUserNameSaved", DataTransferUserName).commit();

                DataTransferRoomName = edtDataTransferRoomName.getText().toString();
                Config.ROOM_NAME_DATA = DataTransferRoomName;
                editor.putString("DataTransferRoomNameSaved", DataTransferRoomName).commit();

                FileTransferUserName = edtFileTransferUserName.getText().toString();
                Config.USER_NAME_FILE = FileTransferUserName;
                editor.putString("FileTransferUserNameSaved", FileTransferUserName).commit();

                FileTransferRoomName = edtFileTransferRoomName.getText().toString();
                Config.ROOM_NAME_FILE = FileTransferRoomName;
                editor.putString("FileTransferRoomNameSaved", FileTransferRoomName).commit();

                MultipartyVideoUserName = edtMultipartyVideoUserName.getText().toString();
                Config.USER_NAME_MULTI = MultipartyVideoUserName;
                editor.putString("MultipartyVideoUserNameSaved", MultipartyVideoUserName).commit();

                MultipartyVideoRoomName = edtMultipartyVideoRoomName.getText().toString();
                Config.ROOM_NAME_MULTI = MultipartyVideoRoomName;
                editor.putString("MultipartyVideoRoomNameSaved", MultipartyVideoRoomName).commit();

            }
        });

        return view;
    }

}
