package com.psl.inventorydemo;

import static com.psl.inventorydemo.helper.AssetUtils.hideProgressDialog;
import static com.psl.inventorydemo.helper.AssetUtils.showProgress;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.psl.inventorydemo.adapter.InventoryAdapter;
import com.psl.inventorydemo.database.DatabaseHandler;
import com.psl.inventorydemo.databinding.ActivityCheckinoutBinding;
import com.psl.inventorydemo.helper.APIConstants;
import com.psl.inventorydemo.helper.AppConstants;
import com.psl.inventorydemo.helper.AssetUtils;
import com.psl.inventorydemo.helper.ConnectionDetector;
import com.psl.inventorydemo.helper.SharedPreferencesManager;
import com.psl.inventorydemo.model.AssetMaster;
import com.psl.inventorydemo.rfid.RFIDInterface;
import com.psl.inventorydemo.rfid.SeuicGlobalRfidHandler;
import com.seuic.uhf.EPC;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class CheckinoutActivity extends AppCompatActivity {
    private Context context = this;
    ActivityCheckinoutBinding binding;
    private SeuicGlobalRfidHandler rfidHandler;
    private InventoryAdapter adapter;
    DatabaseHandler db;
    ConnectionDetector cd;
    private boolean allow_trigger_to_press = true;
    private ArrayList<HashMap<String, String>> tagList = new ArrayList<>();
    HashMap<String, String> hashMap = new HashMap<>();
    private int TagCount = 0;
    List<String> epcList;
    boolean isChecked = false;
    boolean isCheckedIN = false;
    boolean isCheckedOUT = false;
    boolean IS_ASSET_TAG_SCANNED = false;
    boolean isScanning = false;
    private Timer beepTimer;
    private int valid_speed = 0;
    Dialog customConfirmationDialog;
    String activity_type = "";
    String START_DATE = "";
    String END_DATE = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_checkinout);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_checkinout);
        db = new DatabaseHandler(context);
        cd = new ConnectionDetector(context);
        epcList = new ArrayList<>();

        adapter = new InventoryAdapter(context, tagList);
        binding.LvTags.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        binding.checkinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleRadioButtonClick(view);
            }
        });

        binding.checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleRadioButtonClick(view);
            }
        });
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tagList.size() > 0) {
                    showCustomConfirmationDialog("Do you want to upload?", "UPLOAD");
                }
            }
        });

        binding.btnPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allow_trigger_to_press) {
                    AssetUtils.openPowerSettingDialog(context, rfidHandler);
                }
            }
        });
        binding.btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isScanning) {
                    if (epcList.size() > 0) {
                        showCustomConfirmationDialog("Do you want to clear data?", "CLEAR");
                    }
                }
            }
        });
        binding.btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allow_trigger_to_press) {
                    takeInventoryAction();
                }
            }
        });
        binding.textCount.setText("0");
        beepTimer = new Timer();
        beepTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //Called each time when 1000 milliseconds (1 second) (the period parameter)
                if (isScanning) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.btnStartStop.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_red));
                            binding.btnStartStop.setText("Stop");
                            if (valid_speed > 0) {
                                rfidHandler.playSound();
                            }
                            valid_speed = 0;
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.btnStartStop.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_green));
                            binding.btnStartStop.setText("Start");
                        }
                    });
                }
            }

        }, 0, 1000);
        //showProgress(context, getResources().getString(R.string.uhf_initialization));
        rfidHandler = new SeuicGlobalRfidHandler();
        rfidHandler.onCreate(context, new RFIDInterface() {
            @Override
            public void handleTriggerPress(boolean pressed) {
                runOnUiThread(() -> {
                    if (pressed) {
                        binding.btnStartStop.performClick();
                    }

                });
            }

            @Override
            public void RFIDInitializationStatus(boolean status) {
                runOnUiThread(() -> {
                    hideProgressDialog();
                    if (status) {

                    } else {

                    }
                });
            }

            @Override
            public void handleLocateTagResponse(int value, int tagSize) {
                runOnUiThread(() -> {

                });
            }

            @Override
            public void onDataReceived(List<EPC> rfifList) {
                runOnUiThread(() -> {
                    if (rfifList != null) {
                        if (rfifList.size() > 0) {
                            for (int i = 0; i < rfifList.size(); i++) {
                                String epc = rfifList.get(i).getId();
                                if (epc != null) {
                                    if (!epc.equalsIgnoreCase("")) {
                                        if (epc.length() >= 24) {
                                            epc = epc.substring(0, 24);
                                            doDataValidations(epc);
                                            Log.e("EPC", epc);
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            }
        });
    }
    private void doDataValidations(String epc){
        if (!this.epcList.contains(epc)) {
            if(!db.getAssetNameByTagID(epc).equalsIgnoreCase(AppConstants.UNKNOWN_ASSET)){
                String assttypeid = epc.substring(2,4);
                if(!assttypeid.equalsIgnoreCase("06")) {
                    valid_speed++;
                    epcList.add(epc);
                    hashMap = new HashMap<>();
                    hashMap.put("EPC", epc);
                    hashMap.put("AssetName", db.getAssetNameByTagID(epc));
                    tagList.add(hashMap);
                }
             }
            binding.textCount.setText(String.valueOf(epcList.size()));
            TagCount = epcList.size();
            adapter.notifyDataSetChanged();
        }
    }
    private void takeInventoryAction() {
        allow_trigger_to_press = true;
        if (isScanning) {
            isScanning = false;
            rfidHandler.stopInventory();
            binding.btnStartStop.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_green));
            binding.btnStartStop.setText("Start");
        } else {
            isScanning = true;
            START_DATE = AssetUtils.getSystemDateTimeInFormatt();
            rfidHandler.startInventory();
            binding.btnStartStop.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_red));
            binding.btnStartStop.setText("Stop");
        }

    }
    private void handleRadioButtonClick(View view) {
        // Check if the asset tag has been scanned
        if (!(tagList.size() >0)) {
            // Show an error dialog and revert the button state
            AssetUtils.showCommonBottomSheetErrorDialog(context, "Please scan asset tags");

            // Revert the checked state of the radio buttons
            if (view.getId() == R.id.checkinBtn) {
                binding.checkinBtn.setChecked(false);
            } else if (view.getId() == R.id.checkoutBtn) {
                binding.checkoutBtn.setChecked(false);
            }
            return; // Exit the method early
        }

        // If we reach here, it means the asset tag has been scanned
        isChecked = true;
        boolean checked = ((RadioButton) view).isChecked();

        if (view.getId() == R.id.checkinBtn) {
            if (checked) {
                isCheckedIN = true;
                isCheckedOUT = false;
                binding.checkoutBtn.setChecked(false);
                activity_type = "IN";
            }
        } else if (view.getId() == R.id.checkoutBtn) {
            if (checked) {
                isCheckedIN = false;
                isCheckedOUT = true;
                binding.checkinBtn.setChecked(false);
                activity_type = "OUT";
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        rfidHandler.onResume();
        setDefault();
    }
    @Override
    public void onDestroy() {
        rfidHandler.onDestroy();
        setDefault();
        super.onDestroy();
        finish();
    }
    @Override
    public void onPause() {
        super.onPause();
        rfidHandler.onPause();
    }

    @Override
    public void onBackPressed() {
        rfidHandler.stopInventory();
        if (epcList.size() > 0) {
            showCustomConfirmationDialog("Do you want to clear and go back?", "BACK");
        } else {
        super.onBackPressed();
        }
    }
    public void showCustomConfirmationDialog(String msg, final String action) {
        if (customConfirmationDialog != null) {
            customConfirmationDialog.dismiss();
        }
        customConfirmationDialog = new Dialog(context);
        if (customConfirmationDialog != null) {
            customConfirmationDialog.dismiss();
        }
        customConfirmationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customConfirmationDialog.setCancelable(false);
        customConfirmationDialog.setContentView(R.layout.custom_alert_dialog_layout2);
        TextView text = (TextView) customConfirmationDialog.findViewById(R.id.text_dialog);
        text.setText(msg);
        Button dialogButton = (Button) customConfirmationDialog.findViewById(R.id.btn_dialog);
        Button dialogButtonCancel = (Button) customConfirmationDialog.findViewById(R.id.btn_dialog_cancel);
        dialogButton.setText("YES");
        dialogButtonCancel.setText("NO");
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customConfirmationDialog.dismiss();
                if (action.equals("UPLOAD")) {
                    allow_trigger_to_press = false;
                    END_DATE = AssetUtils.getSystemDateTimeInFormatt();
                    uploadInventoryToServer();
                } else if (action.equals("CLEAR")) {
                    setDefault();
                } else if (action.equals("BACK")) {
                    setDefault();
                    finish();
                }
            }
        });
        dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customConfirmationDialog.dismiss();
            }
        });
        // customConfirmationDialog.getWindow().getAttributes().windowAnimations = R.style.SlideBottomUpAnimation;
        customConfirmationDialog.show();
    }
    private void setDefault() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                allow_trigger_to_press = true;
                if (beepTimer != null) {
                    beepTimer.cancel();
                }
                if (epcList != null) {
                    epcList.clear();
                }
                isChecked = false;
                isCheckedIN = false;
                isCheckedOUT = false;
                IS_ASSET_TAG_SCANNED = false;
                isScanning = false;
                if(tagList != null){
                    tagList.clear();
                }
                binding.textCount.setText("");
                binding.checkinBtn.setChecked(false);
                binding.checkoutBtn.setChecked(false);
                adapter.notifyDataSetChanged();
            }
        });
    }
    private void uploadInventoryToServer() {

        if (tagList.size() > 0) {
            new CollectInventoryData().execute("ABC");
        } else {
            allow_trigger_to_press = true;
            AssetUtils.showCommonBottomSheetErrorDialog(context, "No data to upload");
        }

    }

    public class CollectInventoryData extends AsyncTask<String, String, JSONObject> {
        protected void onPreExecute() {
            showProgress(context, "Collectiong Data To Upload");
            super.onPreExecute();
        }

        protected JSONObject doInBackground(String... params) {
            if (tagList.size() > 0) {
                try {
                    JSONObject jsonobject = null;
                    jsonobject = new JSONObject();
                    //jsonobject.put(APIConstants.K_USER_ID, SharedPreferencesManager.getSavedUserId(context));
                    jsonobject.put(APIConstants.K_DEVICE_ID, SharedPreferencesManager.getDeviceId(context));
                    jsonobject.put(APIConstants.K_ACTIVITY_TYPE, activity_type);
                    jsonobject.put(APIConstants.K_INVENTORY_START_DATE_TIME, START_DATE);
                    jsonobject.put(APIConstants.K_INVENTORY_END_DATE_TIME, END_DATE);
                    jsonobject.put(APIConstants.K_INVENTORY_COUNT, ""+tagList.size());
                    jsonobject.put(APIConstants.K_ACTIVITY_ASSET_TYPE, "");
                    jsonobject.put(APIConstants.K_BIN_TAG_ID, "");
                    jsonobject.put(APIConstants.K_BIN_NAME, "");
                    JSONArray js = new JSONArray();
                    for (int i = 0; i < tagList.size(); i++) {
                        JSONObject barcodeObject = new JSONObject();
                        String epc = tagList.get(i).get("EPC");
                        AssetMaster assetMasterList;
                        assetMasterList = db.getAssetMasterByTagId(epc);
                        barcodeObject.put(APIConstants.K_ASSET_TYPE, assetMasterList.getAssetType());
                        barcodeObject.put(APIConstants.K_ASSET_ID, assetMasterList.getAssetID());
                        barcodeObject.put(APIConstants.K_ASSET_NAME, assetMasterList.getAssetName());
                        barcodeObject.put(APIConstants.K_ASSET_SERIAL_NUMBER, assetMasterList.getAssetSerialNo());
                        barcodeObject.put(APIConstants.K_TRANSACTION_DATE_TIME, AssetUtils.getSystemDateTimeInFormatt());

                        js.put(barcodeObject);
                    }
                    jsonobject.put(APIConstants.K_DATA, js);

                    return jsonobject;

                } catch (JSONException e) {

                    return null;
                }
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            if (result != null) {
                if (cd.isConnectingToInternet()) {
                    try {
                        allow_trigger_to_press = false;
                        hideProgressDialog();
                        uploadInventory(result, APIConstants.M_UPLOAD_TRANSACTION, "Please wait...\n" + " Mapping is in progress");

                    } catch (OutOfMemoryError e) {
                        hideProgressDialog();
                        allow_trigger_to_press = false;
                        AssetUtils.showCommonBottomSheetErrorDialog(context, "Huge Data cannot be uploaded");
                    }

                } else {
                    hideProgressDialog();
                    allow_trigger_to_press = true;
                    AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.internet_error));
                }
            } else {
                hideProgressDialog();
                allow_trigger_to_press = true;
                ;
                AssetUtils.showCommonBottomSheetErrorDialog(context, "Something went wrong");
            }

        }

    }
    public void uploadInventory(final JSONObject loginRequestObject, String METHOD_NAME, String progress_message) {
        showProgress(context, progress_message);
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(APIConstants.API_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(APIConstants.API_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(APIConstants.API_TIMEOUT, TimeUnit.SECONDS)
                .build();

        Log.e("ASSETPALLETMAPURL", APIConstants.M_URL + METHOD_NAME);
        Log.e("ASSETPALLETMAPRES", loginRequestObject.toString());
        AndroidNetworking.post(APIConstants.M_URL + METHOD_NAME).addJSONObjectBody(loginRequestObject)
                .setTag("test")
                .setPriority(Priority.LOW)
                .setOkHttpClient(okHttpClient) // passing a custom okHttpClient
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject result) {
                        hideProgressDialog();
                        allow_trigger_to_press = true;
                        if (result != null) {
                            try {
                                Log.e("ASSETPALLETMAPRES", result.toString());
                                String status = result.getString(APIConstants.K_STATUS);
                                String message = result.getString(APIConstants.K_MESSAGE);

                                if (status.equalsIgnoreCase("true")) {
                                    allow_trigger_to_press = false;
                                    //TODO do validations
                                    // JSONArray data = result.getJSONArray(APIConstants.K_DATA);
                                    //  checkResponseAndDovalidations(data);

                                    //TODO
                                    setDefault();
                                    AssetUtils.showCommonBottomSheetSuccessDialog(context, "Mapping Done Successfully");

                                } else {
                                    allow_trigger_to_press = true;
                                    AssetUtils.showCommonBottomSheetErrorDialog(context, message);
                                }
                            } catch (JSONException e) {
                                hideProgressDialog();
                                allow_trigger_to_press = true;
                                AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.something_went_wrong_error));
                            }
                        } else {
                            hideProgressDialog();
                            allow_trigger_to_press = true;
                            AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.communication_error));
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        hideProgressDialog();
                        allow_trigger_to_press = true;
                        //Log.e("ERROR", anError.getErrorDetail());
                        if (anError.getErrorDetail().equalsIgnoreCase("responseFromServerError")) {
                            AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.communication_error));
                        } else if (anError.getErrorDetail().equalsIgnoreCase("connectionError")) {
                            AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.internet_error));
                        } else {
                            AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.internet_error));
                        }
                    }
                });
    }
}