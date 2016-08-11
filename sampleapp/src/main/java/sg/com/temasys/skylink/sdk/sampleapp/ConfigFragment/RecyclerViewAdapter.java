package sg.com.temasys.skylink.sdk.sampleapp.ConfigFragment;

import android.animation.Keyframe;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by phyo.pwint on 29/7/16.
 */


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.rvViewHolder> {


    final private String PREFS_NAME = "KeyInfo";
    final private String TAG = "SharedPreferenceTest";
    List<KeyInfo> SubjectValues;
    Context context;

    public RecyclerViewAdapter(Context context1, List<KeyInfo> SubjectValues1) {
        SubjectValues = new ArrayList<KeyInfo>();
        SubjectValues = SubjectValues1;
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
        TextView textView = holder.textView;
        TextView textView2 = holder.textView2;
        ImageView editImage = holder.editImg;
        ImageView deleteImage = holder.deleteImg;

        textView.setText(SubjectValues.get(pos).getKey());
        textView2.setText(SubjectValues.get(pos).getDescription());

        if(position == 0) {
            holder.itemView.setSelected(true);
            editImage.setVisibility(View.INVISIBLE);
            deleteImage.setVisibility(View.INVISIBLE);
        }

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyInfoEditDialog(pos);
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
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                KeyInfo ki = SubjectValues.get(pos);
                                Log.e("ki", String.valueOf(ki));
                                Log.e("ki.isMCU()", String.valueOf(ki.isMCU()));
                                if (ki.isMCU()) {
                                    removeItemFromJSONArrayAtIndex(KeyFragment.arrayMCU, pos);
                                    Log.e("No of arraryMCU", String.valueOf(KeyFragment.arrayMCU.length()));
                                } else {
                                    removeItemFromJSONArrayAtIndex(KeyFragment.arrayNoMCU, pos);
                                    Log.e("No of arraryNoMCU", String.valueOf(KeyFragment.arrayNoMCU.length()));
                                }
                                SubjectValues.remove(pos);
                                notifyDataSetChanged();
                                saveUserInfo();
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                // End
            }
        });
    }

    private void keyInfoEditDialog(int pos) {
        final int position = pos;
        final KeyInfo ki = SubjectValues.get(position);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Edit Key Info").setMessage("Please fill the information");
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        //Key TextView + EditText
        TextView keyTxtView = new TextView(context);
        keyTxtView.setText("Key");
        keyTxtView.setTextSize(18);
        keyTxtView.setPadding(10, 10, 10, 10);
        layout.addView(keyTxtView);

        final EditText keyBox = new EditText(context);
        keyBox.setHint("Key");
        layout.addView(keyBox);
        keyBox.setText(ki.getKey());

        //Secret TextView + EditText
        TextView secretTxtView = new TextView(context);
        secretTxtView.setText("Secret");
        secretTxtView.setTextSize(18);
        secretTxtView.setPadding(10, 10, 10, 10);
        layout.addView(secretTxtView);

        final EditText secretBox = new EditText(context);
        secretBox.setHint("Secret");
        layout.addView(secretBox);
        secretBox.setText(ki.getSecret());
        secretBox.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);

        //Description TextView + EditText
        TextView descTxtView = new TextView(context);
        descTxtView.setText("Description");
        descTxtView.setTextSize(18);
        descTxtView.setPadding(10, 10, 10, 10);
        layout.addView(descTxtView);

        final EditText descriptionBox = new EditText(context);
        descriptionBox.setHint("Description");
        layout.addView(descriptionBox);
        descriptionBox.setText(ki.getDescription());

        LinearLayout layoutForRadioGroup = new LinearLayout(context);
        layoutForRadioGroup.setOrientation(LinearLayout.VERTICAL);
        final RadioGroup rg = new RadioGroup(context);
        final RadioButton mcuRadio = new RadioButton(context);
        mcuRadio.setText("MCU");
        final RadioButton nomcuRadio = new RadioButton(context);
        nomcuRadio.setText("No MCU");
        final Boolean[] isMCU = {null};
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                // btn_confirm.setEnabled(true);
                if(checkedId == mcuRadio.getId()) isMCU[0] = true;
                else if(checkedId == nomcuRadio.getId()) isMCU[0] = false;
            }
        });
        rg.addView(mcuRadio);
        rg.addView(nomcuRadio);
        if (ki.isMCU()) {
            rg.check(mcuRadio.getId());
        } else {
            rg.check(nomcuRadio.getId());
        }

        layoutForRadioGroup.addView(rg);
        layout.addView(layoutForRadioGroup);

        alert.setView(layout);
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(KeyFragment.checkKeyAndSecret(keyBox.getText().toString(),secretBox.getText().toString())
                        && !isMCU[0].equals(null)){
                    try {
                        JSONObject jo;
                        if(ki.isMCU()) {
                            jo = KeyFragment.arrayMCU.getJSONObject(position);
                        } else {
                            jo = KeyFragment.arrayNoMCU.getJSONObject(position);
                        }
                        jo.put("key", keyBox.getText().toString());
                        jo.put("secret", secretBox.getText().toString());
                        jo.put("description", descriptionBox.getText().toString());
                        if(isMCU[0] != ki.isMCU()) {
                            JSONObject typeChangeObject = new JSONObject();
                            typeChangeObject.put("key", keyBox.getText().toString());
                            typeChangeObject.put("secret", secretBox.getText().toString());
                            typeChangeObject.put("description", descriptionBox.getText().toString());
                            typeChangeObject.put("MCU", isMCU[0]);
                            Log.e("ki.isMCU()", String.valueOf(ki.isMCU()));
                            if(ki.isMCU()){
                                Log.e("number of arrayNoMCU1", String.valueOf(KeyFragment.arrayNoMCU.length()));
                                KeyFragment.arrayNoMCU.put(typeChangeObject);
                                Log.e("arrayNoMCU2", String.valueOf(KeyFragment.arrayNoMCU));
                                removeItemFromJSONArrayAtIndex(KeyFragment.arrayMCU, position);
                                Log.e("arrayMCU", String.valueOf(KeyFragment.arrayMCU));
                            } else {
                                Log.e("number of arrayMCU1", String.valueOf(KeyFragment.arrayMCU.length()));
                                KeyFragment.arrayMCU.put(typeChangeObject);
                                Log.e("arrayMCU2", String.valueOf(KeyFragment.arrayMCU));
                                removeItemFromJSONArrayAtIndex(KeyFragment.arrayNoMCU, position);
                                Log.e("arrayNoMCU", String.valueOf(KeyFragment.arrayNoMCU));
                            }
                        }
                        if(ki.getKey().equals(Config.APP_KEY)){ // key u edit is current key
                            Config.APP_KEY = keyBox.getText().toString();
                            Config.APP_SECRET = secretBox.getText().toString();
                            Config.APP_KEY_DESCRIPTION = descriptionBox.getText().toString();
                        }
                        saveUserInfo();
                        if(ki.isMCU()) {
                            SubjectValues = ManageKeyFragment.convertJSONArrayToKeyInfoList(KeyFragment.arrayMCU);
                        } else {
                            SubjectValues = ManageKeyFragment.convertJSONArrayToKeyInfoList(KeyFragment.arrayNoMCU);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(context,"Incorrect key, secret and/or you need to choose MCU or not",Toast.LENGTH_LONG).show();
                }

            }
        })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
        alert.show();
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

        editor.putString("mcuArray", KeyFragment.arrayMCU.toString());
        editor.putString("nomcuArray", KeyFragment.arrayNoMCU.toString());
        editor.commit();
        Log.i(TAG, KeyFragment.arrayMCU.toString());
        Log.i(TAG, KeyFragment.arrayNoMCU.toString());
        Log.i(TAG, "Save Success");
    }


    private JSONArray removeItemFromJSONArrayAtIndex (JSONArray ja, int position) {
        ArrayList<String> list = new ArrayList<String>();
        JSONArray jsArray = new JSONArray();
        Log.e("Position",String.valueOf(position));
        try {
            boolean MCU = ja.getJSONObject(0).getBoolean("MCU");
            int len = ja.length();
            if (ja != null) {
                for (int i=0;i<len;i++){
//                    list.add(ja.get(i).toString());
                    if(i != position) {
                        JSONObject jo = new JSONObject();
                        jo.put("key", ja.getJSONObject(i).getString("key"));
                        jo.put("secret", ja.getJSONObject(i).getString("secret"));
                        jo.put("description", ja.getJSONObject(i).getString("description"));
                        jo.put("MCU", ja.getJSONObject(i).getBoolean("MCU"));
                        jsArray.put(jo);
                    }
                }
            }
            if(MCU){
                KeyFragment.arrayMCU = jsArray;
                Log.e("No of arraryMCU1",String.valueOf(KeyFragment.arrayMCU));
                Log.e("No of arraryNoMCU1",String.valueOf(KeyFragment.arrayNoMCU));
            } else {
                KeyFragment.arrayNoMCU = jsArray;
                Log.e("No of arraryNoMCU2",String.valueOf(KeyFragment.arrayNoMCU));
                Log.e("No of arraryMCU2",String.valueOf(KeyFragment.arrayMCU));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsArray;
    }

    @Override
    public int getItemCount() {
        return SubjectValues.size();
    }



    public class rvViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        TextView textView2;
        ImageView editImg;
        ImageView deleteImg;

        public rvViewHolder(View itemView) {
            super(itemView);
            View v = itemView;
            textView = (TextView) v.findViewById(R.id.subTextview);
            textView2 = (TextView) v.findViewById(R.id.descTextview);
            // When Click Edit Button
            editImg = (ImageView) v.findViewById(R.id.editImage);
            deleteImg = (ImageView) v.findViewById(R.id.deleteImage);
            // Setup the click listener
            v.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {

        }
    }
}


