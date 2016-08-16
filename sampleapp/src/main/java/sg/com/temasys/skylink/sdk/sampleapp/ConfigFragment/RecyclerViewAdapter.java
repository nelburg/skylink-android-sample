package sg.com.temasys.skylink.sdk.sampleapp.ConfigFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
 * Created by phyo.pwint on 29/7/16.
 */


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.rvViewHolder> {

    final private String PREFS_NAME = "KeyInfo";
    final private String TAG = "SharedPreferenceTest";
    private List<KeyInfo> keyInfos;
    Context context;

    EditText keyText;
    EditText secretText;
    EditText descText;
    Button btnSave;
    Button btnCancel;
    RadioButton rbMCU;
    RadioButton rbNoMCU;
    RadioGroup rgMCUorNot;
    final Boolean[] isMCU = {null};
    Dialog dialog;
    private RadioButton lastCheckedRB = null;

    public RecyclerViewAdapter(Context context1, List<KeyInfo> keys) {
        keyInfos = new ArrayList<KeyInfo>();
        keyInfos = keys;
        context = context1;
    }

    @Override
    public rvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.recyclerview_items, parent, false);
        return new rvViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(rvViewHolder holder, int position) {
        final int pos = position;
        final TextView textView = holder.textView;
        TextView textView2 = holder.textView2;
        ImageView editImage = holder.editImg;
        ImageView deleteImage = holder.deleteImg;
        RadioButton radioButtonDefault = holder.rdDefault;
        RadioGroup radioGroupDefault = holder.rgDefault;

        textView.setText(keyInfos.get(pos).getKey());
        textView2.setText(keyInfos.get(pos).getDescription());

        radioGroupDefault.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checked_rb = (RadioButton) group.findViewById(checkedId);
                checked_rb.setChecked(true);
                if (lastCheckedRB != null) {
                    lastCheckedRB.setChecked(false);
                }
                lastCheckedRB = checked_rb;
            }
        });

        if (position == 0) {
            holder.itemView.setSelected(true);
            editImage.setVisibility(View.INVISIBLE);
            deleteImage.setVisibility(View.INVISIBLE);
        }

        radioButtonDefault.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                try {
                    JSONObject selectedItem;
                    boolean isMCU = ManageKeyFragment.mcuSelect;
                    if (isMCU) {
                        selectedItem = ManageKeyFragment.arrayMCU.getJSONObject(pos);
                    } else {
                        selectedItem = ManageKeyFragment.arrayNoMCU.getJSONObject(pos);
                    }
                    Config.APP_KEY = selectedItem.getString("key");
                    Config.APP_SECRET = selectedItem.getString("secret");
                    Config.APP_KEY_DESCRIPTION = selectedItem.getString("description");
                    ManageKeyFragment.setTextViews();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return false;
            }
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editKey(pos);
            }
        });

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);
                alertDialogBuilder.setTitle("Alert");
                alertDialogBuilder
                        .setMessage("Are you sure to delete?")
                        .setCancelable(false)
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                KeyInfo info = keyInfos.get(pos);
                                if (info.isMCU()) {
                                    removeItemFromJSONArrayAtIndex(ManageKeyFragment.arrayMCU, pos);
                                } else {
                                    removeItemFromJSONArrayAtIndex(ManageKeyFragment.arrayNoMCU, pos);
                                }
                                keyInfos.remove(pos);
                                notifyDataSetChanged();
                                saveUserInfo();
                            }
                        })
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                // End
            }
        });
    }

    public void editKey(int position) {
        final int pos = position;
        getDialogBox(pos);
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
                final KeyInfo keyInfo = keyInfos.get(pos);
                if (ManageKeyFragment.checkKeyAndSecret(keyText.getText().toString(), secretText.getText().toString())
                        && !(isMCU[0] == null)) {
                    try {
                        JSONObject jsonKeyInfo;
                        if (keyInfo.isMCU()) {
                            jsonKeyInfo = ManageKeyFragment.arrayMCU.getJSONObject(pos);
                        } else {
                            jsonKeyInfo = ManageKeyFragment.arrayNoMCU.getJSONObject(pos);
                        }
                        jsonKeyInfo.put("key", keyText.getText().toString());
                        jsonKeyInfo.put("secret", secretText.getText().toString());
                        jsonKeyInfo.put("description", descText.getText().toString());
                        if (isMCU[0] != keyInfo.isMCU()) { // move to another array
                            JSONObject typeChangeObject = new JSONObject();
                            typeChangeObject.put("key", keyText.getText().toString());
                            typeChangeObject.put("secret", secretText.getText().toString());
                            typeChangeObject.put("description", descText.getText().toString());
                            typeChangeObject.put("MCU", isMCU[0]);
                            if (keyInfo.isMCU()) {
                                ManageKeyFragment.arrayNoMCU.put(typeChangeObject);
                                removeItemFromJSONArrayAtIndex(ManageKeyFragment.arrayMCU, pos);
                            } else {
                                ManageKeyFragment.arrayMCU.put(typeChangeObject);
                                removeItemFromJSONArrayAtIndex(ManageKeyFragment.arrayNoMCU, pos);
                            }
                        }
                        if (keyInfo.getKey().equals(Config.APP_KEY)) { // key u edit is current key
                            Config.APP_KEY = keyText.getText().toString();
                            Config.APP_SECRET = secretText.getText().toString();
                            Config.APP_KEY_DESCRIPTION = descText.getText().toString();
                        }
                        saveUserInfo();
                        if (keyInfo.isMCU()) {
                            keyInfos = ManageKeyFragment.convertJSONArrayToKeyInfoList(ManageKeyFragment.arrayMCU);
                        } else {
                            keyInfos = ManageKeyFragment.convertJSONArrayToKeyInfoList(ManageKeyFragment.arrayNoMCU);
                        }
                        notifyDataSetChanged();
                        Toast.makeText(context, "Edit Succeeded", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(context, "Incorrect key, secret and/or you need to choose MCU or not", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void getDialogBox(int position) {
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
        // set default values
        final KeyInfo keyInfo = keyInfos.get(position);
        keyText.setText(keyInfo.getKey());
        secretText.setText(keyInfo.getSecret());
        descText.setText(keyInfo.getDescription());
        rgMCUorNot.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if (checkedId == rbMCU.getId()) isMCU[0] = true;
                else if (checkedId == rbNoMCU.getId()) isMCU[0] = false;
            }
        });
        if (keyInfo.isMCU()) {
            rgMCUorNot.check(rbMCU.getId());
        } else {
            rgMCUorNot.check(rbNoMCU.getId());
        }

        dialog.show();
    }

    private void saveUserInfo() {
        SharedPreferences userInfo = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.OnSharedPreferenceChangeListener changeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {

            @Override
            public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {

            }
        };
        userInfo.registerOnSharedPreferenceChangeListener(changeListener);
        SharedPreferences.Editor editor = userInfo.edit();

        editor.putString("mcuArray", ManageKeyFragment.arrayMCU.toString());
        editor.putString("nomcuArray", ManageKeyFragment.arrayNoMCU.toString());
        editor.commit();
        Log.i(TAG, ManageKeyFragment.arrayMCU.toString());
        Log.i(TAG, ManageKeyFragment.arrayNoMCU.toString());
        Log.i(TAG, "Save Success");
    }


    private JSONArray removeItemFromJSONArrayAtIndex(JSONArray ja, int position) {
        JSONArray jsArray = new JSONArray();
        Log.e("Position", String.valueOf(position));
        try {
            boolean MCU = ja.getJSONObject(0).getBoolean("MCU");
            int len = ja.length();
            if (ja != null) {
                for (int i = 0; i < len; i++) {
                    if (i != position) {
                        JSONObject jo = new JSONObject();
                        jo.put("key", ja.getJSONObject(i).getString("key"));
                        jo.put("secret", ja.getJSONObject(i).getString("secret"));
                        jo.put("description", ja.getJSONObject(i).getString("description"));
                        jo.put("MCU", ja.getJSONObject(i).getBoolean("MCU"));
                        jsArray.put(jo);
                    }
                }
            }
            if (MCU) {
                ManageKeyFragment.arrayMCU = jsArray;
                Log.e("No of arraryMCU1", String.valueOf(ManageKeyFragment.arrayMCU));
                Log.e("No of arraryNoMCU1", String.valueOf(ManageKeyFragment.arrayNoMCU));
            } else {
                ManageKeyFragment.arrayNoMCU = jsArray;
                Log.e("No of arraryNoMCU2", String.valueOf(ManageKeyFragment.arrayNoMCU));
                Log.e("No of arraryMCU2", String.valueOf(ManageKeyFragment.arrayMCU));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsArray;
    }

    @Override
    public int getItemCount() {
        return keyInfos.size();
    }

    public class rvViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        TextView textView2;
        ImageView editImg;
        ImageView deleteImg;
        RadioButton rdDefault;
        RadioGroup rgDefault;

        public rvViewHolder(View itemView) {
            super(itemView);
            View v = itemView;
            textView = (TextView) v.findViewById(R.id.subTextview);
            textView2 = (TextView) v.findViewById(R.id.descTextview);
            // When Click Edit Button
            editImg = (ImageView) v.findViewById(R.id.editImage);
            deleteImg = (ImageView) v.findViewById(R.id.deleteImage);
            rdDefault = (RadioButton) v.findViewById(R.id.rbDefault);
            rgDefault = (RadioGroup) v.findViewById(R.id.rgDefault);
            // Setup the click listener
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }
}


