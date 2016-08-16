package sg.com.temasys.skylink.sdk.sampleapp.ConfigFragment;


import org.json.JSONArray;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.temasys.skylink.sampleapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ManageKeyFragment extends Fragment {

    private String MCUSample;
    private String noMCUSample;

    EditText keyText;
    EditText secretText;
    EditText descText;
    Button btnSave;
    Button btnCancel;
    RadioButton rbMCU;
    RadioButton rbNoMCU;
    RadioGroup rgMCUorNot;
    Dialog dialog;
    final Boolean[] isMCU = {null}; // Store which radio button user choose in the dialog

    RecyclerView recyclerView;
    static TextView currentKey;
    static TextView description;

    RecyclerView.Adapter recyclerViewAdapter;
    RecyclerView.LayoutManager recyclerViewLayoutManager;

    final private String PREFS_NAME = "KeyInfo";
    final private String TAG = "SharedPreferenceTest";

    List keyInfoList = new ArrayList();
    boolean mcuSelect = Boolean.parseBoolean(null);

    static JSONArray arrayMCU = new JSONArray();
    static JSONArray arrayNoMCU = new JSONArray();

    public ManageKeyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_key, container, false);

        MCUSample = String.format("{'key':'%s','secret':'%s','description':'%s','MCU':%s}",
                getResources().getString(R.string.app_key_mcu),
                getResources().getString(R.string.app_secret_mcu),
                "Sample App Key with MCU",
                true
        );
        noMCUSample = String.format("{'key':'%s','secret':'%s','description':'%s','MCU':%s}",
                getResources().getString(R.string.app_key_no_mcu),
                getResources().getString(R.string.app_secret_no_mcu),
                "Sample App Key with no MCU",
                false
        );
        try {
            arrayMCU.put(new JSONObject(MCUSample));
            arrayNoMCU.put(new JSONObject(noMCUSample));
            getUserInfo();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        currentKey = (TextView) view.findViewById(R.id.currentKey);
        description = (TextView) view.findViewById(R.id.description);
        setTextViews();

        recyclerView = (RecyclerView) view.findViewById(R.id.rvKeyList);
        recyclerViewLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        RadioGroup rGroup = (RadioGroup) view.findViewById(R.id.radio_grp_manage_MCU);
        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.radio_btn_manage_MCU) {
                    mcuSelect = true;
                    keyInfoList = convertJSONArrayToKeyInfoList(arrayMCU);
                } else if (checkedId == R.id.radio_btn_manage_no_MCU) {
                    mcuSelect = false;
                    keyInfoList = convertJSONArrayToKeyInfoList(arrayNoMCU);
                }
                Log.e("keyInfoList", keyInfoList.toString());
                Log.e("mcuSelect", String.valueOf(mcuSelect));
                recyclerViewAdapter = new RecyclerViewAdapter(getContext(), keyInfoList);
                recyclerView.setAdapter(recyclerViewAdapter);
            }
        });
        rGroup.check(R.id.radio_btn_manage_MCU);
        FloatingActionButton createKeyInfo = (FloatingActionButton) view.findViewById(R.id.fbtn_create_key);
        createKeyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createKey();
            }
        });

        return view;
    }

    public void createKey() {
        getDialogBox();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Log.e("ISMCU[0]", String.valueOf(isMCU[0]==null));
                if (checkKeyAndSecret(keyText.getText().toString(), secretText.getText().toString())
                        && !(isMCU[0]==null)) {
                    try {
                        if (!checkKeyInsideOfArray(keyText.getText().toString())) {
                            String newKeyInfo = String.format(
                                    "{'key':'%s','secret':'%s','description':'%s','MCU':%s}",
                                    keyText.getText().toString(),
                                    secretText.getText().toString(),
                                    descText.getText().toString(),
                                    isMCU[0]
                            );
                            JSONObject newObject = new JSONObject(newKeyInfo);
                            if (newObject.getBoolean("MCU")) {
                                arrayMCU.put(newObject);
                            } else if (!newObject.getBoolean("MCU")) {
                                arrayNoMCU.put(newObject);
                            }
                            Toast.makeText(getContext(), "Create succeeded", Toast.LENGTH_LONG).show();
                            saveUserInfo();
                            if (mcuSelect) {
                                keyInfoList = convertJSONArrayToKeyInfoList(arrayMCU);
                            } else {
                                keyInfoList = convertJSONArrayToKeyInfoList(arrayNoMCU);
                            }
                            recyclerViewAdapter = new RecyclerViewAdapter(getContext(), keyInfoList);
                            recyclerView.setAdapter(recyclerViewAdapter);
                            Toast.makeText(getContext(),"Create Succeeded",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "This key already exist!",
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getContext(), "Incorrect key, secret and/or you need to choose MCU or not",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    static public void setTextViews() {
        currentKey.setText("Current Key: " + Config.APP_KEY);
        description.setText(String.format("Description: %s", Config.APP_KEY_DESCRIPTION));
    }

    public void getDialogBox() {
        Context context = getContext();
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_key_info);
        dialog.setTitle("Create New Key");
        //Prepare TextBox Hint Text
        TextInputLayout keyInputLayout = (TextInputLayout) dialog.findViewById(R.id.keyTextLayout);
        keyInputLayout.setHint("Key");
        TextInputLayout secretInputLayout = (TextInputLayout) dialog.findViewById(R.id.secretTextLayout);
        secretInputLayout.setHint("Secret");
        TextInputLayout descInputLayout = (TextInputLayout) dialog.findViewById(R.id.descTextLayout);
        descInputLayout.setHint("Description");
        // Get The Elements
        keyText = (EditText) dialog.findViewById(R.id.keyEditText);
        secretText = (EditText) dialog.findViewById(R.id.secretEditText);
        descText = (EditText) dialog.findViewById(R.id.descEditText);
        btnSave = (Button) dialog.findViewById(R.id.btnSave);
        btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        rbMCU = (RadioButton) dialog.findViewById(R.id.radioMCU);
        rbNoMCU = (RadioButton) dialog.findViewById(R.id.radioNoMCU);
        rgMCUorNot = (RadioGroup) dialog.findViewById(R.id.radio_grp_manage);
        rgMCUorNot.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if (checkedId == rbMCU.getId()) isMCU[0] = true;
                else if (checkedId == rbNoMCU.getId()) isMCU[0] = false;
            }
        });
        dialog.show();
    }

//    public void keyInfoCreateDialog() {
//        Context context = getContext();
//        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getContext());
//        alert.setTitle("Add Key Information").setMessage("Please fill in the blank below");
//        LinearLayout layout = new LinearLayout(context);
//        layout.setOrientation(LinearLayout.VERTICAL);
//
//        //Key TextView + EditText
//        TextView keyTxtView = new TextView(context);
//        keyTxtView.setText("Key");
//        keyTxtView.setTextSize(18);
//        keyTxtView.setPadding(10, 10, 10, 10);
//        layout.addView(keyTxtView);
//
//        final EditText keyBox = new EditText(getContext());
//        keyBox.setHint("Key");
//        layout.addView(keyBox);
//
//        //Secret TextView + EditText
//        TextView secretTxtView = new TextView(context);
//        secretTxtView.setText("Secret");
//        secretTxtView.setTextSize(18);
//        secretTxtView.setPadding(10, 10, 10, 10);
//        layout.addView(secretTxtView);
//
//        final EditText secretBox = new EditText(context);
//        secretBox.setHint("Secret");
//        layout.addView(secretBox);
//        secretBox.setInputType(InputType.TYPE_CLASS_TEXT |
//                InputType.TYPE_TEXT_VARIATION_PASSWORD);
//
//        //Description TextView + EditText
//        TextView descriptionTxtView = new TextView(context);
//        descriptionTxtView.setText("Description");
//        descriptionTxtView.setTextSize(18);
//        descriptionTxtView.setPadding(10, 10, 10, 10);
//        layout.addView(descriptionTxtView);
//
//        final EditText descriptionBox = new EditText(context);
//        descriptionBox.setHint("Description");
//        layout.addView(descriptionBox);
//
//
//        LinearLayout layoutForRadioGroup = new LinearLayout(getContext());
//        layoutForRadioGroup.setOrientation(LinearLayout.HORIZONTAL);
//        final RadioGroup rg = new RadioGroup(getContext());
//        final RadioButton mcuRadio = new RadioButton(getContext());
//        mcuRadio.setText("MCU");
//        final RadioButton nomcuRadio = new RadioButton(getContext());
//        nomcuRadio.setText("No MCU");
//        final Boolean[] isMCU = {null};
//        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                // checkedId is the RadioButton selected
//                ArrayAdapter<String> adapter;
//                if (checkedId == mcuRadio.getId()) isMCU[0] = true;
//                else if (checkedId == nomcuRadio.getId()) isMCU[0] = false;
//            }
//        });
//        rg.addView(mcuRadio);
//        rg.addView(nomcuRadio);
//
//        layoutForRadioGroup.addView(rg);
//        layout.addView(layoutForRadioGroup);
//
//        alert.setView(layout);
//        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                // continue
//                if (checkKeyAndSecret(keyBox.getText().toString(), secretBox.getText().toString())
//                        && !isMCU[0].equals(null)) {
//                    try {
//                        if (!checkKeyInsideOfArray(keyBox.getText().toString())) {
//                            String newKeyInfo = String.format(
//                                    "{'key':'%s','secret':'%s','description':'%s','MCU':%s}",
//                                    keyBox.getText().toString(),
//                                    secretBox.getText().toString(),
//                                    descriptionBox.getText().toString(),
//                                    isMCU[0]
//                            );
//                            JSONObject newObject = new JSONObject(newKeyInfo);
//                            if (newObject.getBoolean("MCU")) {
//                                arrayMCU.put(newObject);
//                            } else if (!newObject.getBoolean("MCU")) {
//                                arrayNoMCU.put(newObject);
//                            }
//                            Toast.makeText(getContext(), "Create succeeded", Toast.LENGTH_LONG).show();
//                            saveUserInfo();
//                            if (mcuSelect) {
//                                keyInfoList = convertJSONArrayToKeyInfoList(arrayMCU);
//                            } else {
//                                keyInfoList = convertJSONArrayToKeyInfoList(arrayNoMCU);
//                            }
//                            recyclerViewAdapter = new RecyclerViewAdapter(getContext(), keyInfoList);
//                            recyclerView.setAdapter(recyclerViewAdapter);
//                        } else {
//                            Toast.makeText(getContext(), "This key already exist!",
//                                    Toast.LENGTH_LONG).show();
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                } else {
//                    Toast.makeText(getContext(), "Incorrect key, secret and/or you need to choose MCU or not",
//                            Toast.LENGTH_LONG).show();
//                }
//
//            }
//        })
//                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // do nothing
//                    }
//                });
//        alert.show();
//    }

    public boolean checkKeyInsideOfArray(String key) throws JSONException {
        for (int i = 0; i < arrayMCU.length(); i++) {
            JSONObject jo = arrayMCU.getJSONObject(i);
            if (jo.getString("key").equals(key)) {
                return true;
            }
        }
        for (int i = 0; i < arrayNoMCU.length(); i++) {
            JSONObject jo = arrayNoMCU.getJSONObject(i);
            if (jo.getString("key").equals(key)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkKeyAndSecret(String key, String secret) {
        boolean correct = false;
        if (key.length() == 36 && secret.length() == 13) {
            correct = true;
        }
        return correct;
    }

    /**
     * Save User Information
     */
    private void saveUserInfo() {
        SharedPreferences userInfo = getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.OnSharedPreferenceChangeListener changeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {

            @Override
            public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
            }
        };
        userInfo.registerOnSharedPreferenceChangeListener(changeListener);
        SharedPreferences.Editor editor = userInfo.edit();

        editor.putString("mcuArray", arrayMCU.toString());
        editor.putString("nomcuArray", arrayNoMCU.toString());
        editor.commit();
        Log.i(TAG, "Save Success");
    }

    /**
     * Read User Information
     */
    private void getUserInfo() throws JSONException {
        SharedPreferences userInfo = getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String mcuArray = userInfo.getString("mcuArray", null);//read MCU JSONArray as string
        String nomcuArray = userInfo.getString("nomcuArray", null);//read No MCU JSONArray as string
        Log.i(TAG, "Read User Info");
        Log.i(TAG, "mcuArray: " + mcuArray);
        Log.i(TAG, "nomcuArray: " + nomcuArray);
        if (mcuArray != null && nomcuArray != null) {
            arrayMCU = new JSONArray(mcuArray);
            arrayNoMCU = new JSONArray(nomcuArray);
            Log.e(TAG, arrayMCU.toString());
            Log.e(TAG, arrayNoMCU.toString());
        } else {
            saveUserInfo();
        }
    }

    public static List<KeyInfo> convertJSONArrayToKeyInfoList(JSONArray ja) {
        List keyInfoList = new ArrayList<>();
        for (int i = 0; i < ja.length(); i++) {
            KeyInfo ki = new KeyInfo();
            Log.e("JSONArray", ja.toString());
            try {
                JSONObject jo = new JSONObject(ja.get(i).toString());
                ki.setKey(jo.getString("key"));
                ki.setSecret(jo.getString("secret"));
                ki.setDescription(jo.getString("description"));
                ki.setMCU(jo.getBoolean("MCU"));
                keyInfoList.add(ki);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return keyInfoList;
    }


}
