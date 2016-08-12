package sg.com.temasys.skylink.sdk.sampleapp.ConfigFragment;


import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.temasys.skylink.sampleapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class KeyFragment extends Fragment {


    public KeyFragment() {
        // Required empty public constructor
    }

    final private String PREFS_NAME = "KeyInfo";
    final private String TAG = "SharedPreferenceTest";
//    private static String APP_KEY = "a0e40e97-7857-4157-9988-5ce56330347c";
//    private static String APP_SECRET = "e7jeqhu51u1sj";
//    private static String APP_KEY_DESCRIPTION = "Sample with no MCU";

    static List<String> spinnerArrayMCU = new ArrayList<>();
    static List<String> spinnerArrayNoMCU = new ArrayList<>();
    static JSONArray arrayMCU = new JSONArray();
    static JSONArray arrayNoMCU = new JSONArray();
    private TextView currentKey;
    private TextView description;
    private RadioGroup radioGroup;
    private Spinner sItems;
    ArrayAdapter<String> spinnerAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_key, container, false);

        String MCUSample = String.format("{'key':'%s','secret':'%s','description':'%s','MCU':%s}",
                getResources().getString(R.string.app_key_mcu),
                getResources().getString(R.string.app_secret_mcu),
                "Sample App Key with MCU",
                true
        );
        String noMCUSample = String.format("{'key':'%s','secret':'%s','description':'%s','MCU':%s}",
                getResources().getString(R.string.app_key_no_mcu),
                getResources().getString(R.string.app_secret_no_mcu),
                "Sample App Key with no MCU",
                false
        );

        TextView tips = (TextView) view.findViewById(R.id.tips_tv);
        tips.setText("Choose MCU or not first.");
        tips.setTextSize(30);

        currentKey = (TextView) view.findViewById(R.id.current_key);
        description = (TextView) view.findViewById(R.id.description);
        setTextViews();

        radioGroup = (RadioGroup) view.findViewById(R.id.radio_grp_MCU);

//        Log.e("APPKEY",getContext().getString(R.string.app_key_no_mcu));
//        Log.e("APPSECRET",getContext().getString(R.string.app_secret_no_mcu));

        try {
            JSONObject joMCU = new JSONObject(MCUSample);
            JSONObject joNoMCU = new JSONObject(noMCUSample);
            addKeyToSpinnerArray(joMCU);
            addKeyToSpinnerArray(joNoMCU);
            arrayMCU.put(new JSONObject(MCUSample));
            arrayNoMCU.put(new JSONObject(noMCUSample));
//            Log.e("NoMCUSample before", joNoMCU.toString());
//            joNoMCU =  arrayNoMCU.getJSONObject(0);
//            joNoMCU.put("description","1234567890-");
//            Log.e("NoMCUSample after1", joNoMCU.toString());
//            Log.e("NoMCUSample after2", arrayNoMCU.getJSONObject(0).toString());



        } catch (JSONException e) {
            e.printStackTrace();
        }



//        sItems.setVisibility(view.GONE);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(RadioGroup group, final int checkedId) {
                // checkedId is the RadioButton selected

                sItems = (Spinner) view.findViewById(R.id.choose_key);

                if(checkedId == R.id.radio_btn_MCU){
                    spinnerAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, spinnerArrayMCU);
                    sItems.setVisibility(view.VISIBLE);
                }
                else if(checkedId == R.id.radio_btn_no_MCU) {
                    spinnerAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, spinnerArrayNoMCU);
                    sItems.setVisibility(view.VISIBLE);
                }
                else {
                    spinnerAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item);
                    sItems.setVisibility(view.GONE); //hidden spinner view
                }
                sItems.setAdapter(spinnerAdapter);
                spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

                sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        try {
//                            Log.e("position of spinner", String.valueOf(position));
                            JSONObject jo = new JSONObject();
                            if (checkedId == R.id.radio_btn_MCU) {
                                jo = arrayMCU.getJSONObject(position);
                            } else if (checkedId == R.id.radio_btn_no_MCU) {
                                jo = arrayNoMCU.getJSONObject(position);
                            }
                            Config.APP_KEY = jo.getString("key");
                            Config.APP_SECRET = jo.getString("secret");
                            Config.APP_KEY_DESCRIPTION = jo.getString("description");
//                            Log.e("APP_KEY", Config.APP_KEY);
                            setTextViews();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }

                });

            }
        });

//        Log.e("array", arrayMCU.toString());

        try {
            getUserInfo();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Toast.makeText(getContext(), "onCreatView", Toast.LENGTH_SHORT).show();
        // Inflate the layout for this fragment
        return view;
    }

    //
    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(getContext(), "onResume1", Toast.LENGTH_SHORT).show();
        try {
            getUserInfo();
            setTextViews();
            mergeArrayWithSpinnerArray();
            radioGroup.clearCheck();
            sItems.setAdapter(null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//
//    @Override
//    public void onClick(View v) {
//        if (v.equals(btn_use_default_key)) {
//            Toast.makeText(getContext(), "Default", Toast.LENGTH_LONG).show();
//            Config.APP_KEY = APP_KEY;
//            Config.APP_SECRET = APP_SECRET;
//            Config.APP_KEY_DESCRIPTION = APP_KEY_DESCRIPTION;
//            setTextViews();
//            radioGroup.clearCheck();
//            sItems.setAdapter(null);
//        }
//
//    }

    public void setTextViews() {
        currentKey.setText("Current Key: " + Config.APP_KEY);
        description.setText(String.format("Description: %s", Config.APP_KEY_DESCRIPTION));
    }

    public static void mergeArrayWithSpinnerArray() {
        spinnerArrayMCU = new ArrayList<>();
        spinnerArrayNoMCU = new ArrayList<>();
        for (int i = 0; i < arrayMCU.length(); i++) {
            try {
//                Log.e("JSONObject1", arrayMCU.getJSONObject(i).toString());
                JSONObject jo = arrayMCU.getJSONObject(i);
                addKeyToSpinnerArray(jo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < arrayNoMCU.length(); i++) {
            try {
                JSONObject jo = arrayNoMCU.getJSONObject(i);
//                Log.e("JSONObject2", jo.toString());
                addKeyToSpinnerArray(jo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addKeyToSpinnerArray(JSONObject jsonKeyInfo) {
        try {
            if (jsonKeyInfo.getString("MCU").equals("true")) {
                spinnerArrayMCU.add(String.format("%s. %s (%s)", spinnerArrayMCU.size() + 1,
                        jsonKeyInfo.getString("key"), jsonKeyInfo.getString("description")));
            } else {
                spinnerArrayNoMCU.add(String.format("%s. %s (%s)", spinnerArrayNoMCU.size() + 1,
                        jsonKeyInfo.getString("key"), jsonKeyInfo.getString("description")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            mergeArrayWithSpinnerArray();
        } else {
            saveUserInfo();
        }
    }


    /**
     * Clear Data
     */
    private void clearUserInfo() {
        SharedPreferences userInfo = getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();
        editor.clear();
        editor.commit();
        Log.i(TAG, "Clear Data");
    }
}
