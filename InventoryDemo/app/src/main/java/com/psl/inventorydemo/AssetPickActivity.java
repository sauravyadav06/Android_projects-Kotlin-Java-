package com.psl.inventorydemo;

import static com.psl.inventorydemo.helper.AssetUtils.hideProgressDialog;
import static com.psl.inventorydemo.helper.AssetUtils.showProgress;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
import com.psl.inventorydemo.database.DatabaseHandler;
import com.psl.inventorydemo.databinding.ActivityAssetPickBinding;
import com.psl.inventorydemo.databinding.ActivityAssetPutBinding;
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
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class AssetPickActivity extends AppCompatActivity {
    private Context context = this;
    ActivityAssetPickBinding binding;
    private SeuicGlobalRfidHandler rfidHandler;
    private boolean allow_trigger_to_press = true;
    private String SCANNED_EPC = "";
    private String CURRENT_EPC = "";
    DatabaseHandler db;
    ConnectionDetector cd;
    boolean IS_BIN_TAG_SCANNED = false;
    boolean IS_ASSET_TAG_SCANNED = false;
    private String BinTagID = "";
    private String BinName = "";
    private String AssetTagID = "";
    private String AssetName = "";
    AssetMaster bins, assets;
    Dialog customConfirmationDialog;
    String START_DATE = "";
    String END_DATE = "";
    private ArrayList<HashMap<String, String>> tagList = new ArrayList<>();
    HashMap<String, String> hashMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_asset_put);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_asset_pick);
        db = new DatabaseHandler(context);
        cd = new ConnectionDetector(context);

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IS_BIN_TAG_SCANNED) {
                    if(IS_ASSET_TAG_SCANNED){
                        showCustomConfirmationDialog("Do you want to upload?", "UPLOAD");
                    }
                    else{
                        AssetUtils.showCommonBottomSheetErrorDialog(context, "Please scan asset");
                    }
                }
                else{
                    AssetUtils.showCommonBottomSheetErrorDialog(context, "Please scan bin");
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
                showCustomConfirmationDialog("Do you want to clear the activity?", "CLEAR");
            }
        });
        rfidHandler = new SeuicGlobalRfidHandler();
        rfidHandler.onCreate(context, new RFIDInterface() {
            @Override
            public void handleTriggerPress(boolean pressed) {
                runOnUiThread(() -> {
                    if (pressed) {
                        if(allow_trigger_to_press){
                            START_DATE = AssetUtils.getSystemDateTimeInFormatt();
                            startInventory();
                            new Handler().postDelayed(() -> {
                                hideProgressDialog();
                                allow_trigger_to_press = true;
                                stopInventory();
                                doValidation();
                            }, 2000);
                        }
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
            public void onDataReceived(List<EPC> epcList) {
                runOnUiThread(() -> {
                    if (epcList != null) {
                        if (epcList.size() > 0) {
                            int maxRssi = Integer.MIN_VALUE;//changed
                            String maxRssiEpc = null;//changed
                            for (int i = 0; i < epcList.size(); i++) {
                                String epc = epcList.get(i).getId();
                                String tid = "FFFFFFFFFFFFFFFFFFFFFFFF";
                                int rssivalue = epcList.get(i).rssi;//changed
                                if (rssivalue > maxRssi) {
                                    maxRssi = rssivalue;
                                    maxRssiEpc = epc;
                                }//changed


                            }
                            if (maxRssiEpc != null) {
                                if (!allow_trigger_to_press) {
                                    SCANNED_EPC = maxRssiEpc;
                                    Log.e("epc", SCANNED_EPC);

                                }
                            }
                        }
                    }
                });
            }
        });

    }
    public void doValidation() {
        hideProgressDialog();
        allow_trigger_to_press = true;
        try {
            if (SCANNED_EPC != null && !SCANNED_EPC.isEmpty() && SCANNED_EPC.length() >= 24) {
                if (!SCANNED_EPC.isEmpty()) {
                    if (SCANNED_EPC.length() >= 24) {
                        CURRENT_EPC = SCANNED_EPC;
                        CURRENT_EPC = CURRENT_EPC.substring(0, 24);
                        String companycode = CURRENT_EPC.substring(0, 2);
                        String companycode1 = AssetUtils.hexToNumber(companycode);
                        String assettpid = CURRENT_EPC.substring(2, 4);
                        String serialnumber = CURRENT_EPC.substring(4, 12);
                        if (companycode1.equalsIgnoreCase("21")) {
                            if (db.IsExistAsset(assettpid.replace("0",""))) {
                                if(assettpid.equalsIgnoreCase("06")){
                                    if(!IS_BIN_TAG_SCANNED){
                                        IS_BIN_TAG_SCANNED = true;
                                        BinTagID = CURRENT_EPC;
                                        BinName = db.getAssetNameByTagID(CURRENT_EPC);
                                        if(!BinName.equalsIgnoreCase(AppConstants.UNKNOWN_ASSET)){
                                            binding.binTagID.setText(BinName);
                                        }
                                    }
                                } else{
                                    Log.e("asst", assettpid);
                                    if(IS_BIN_TAG_SCANNED){
                                        IS_ASSET_TAG_SCANNED = true;
                                        AssetTagID = CURRENT_EPC;
                                        Log.e("AssTTag", AssetTagID);
                                        AssetName = db.getAssetNameByTagID(CURRENT_EPC);
                                        Log.e("AsstName", AssetName);
                                        if(!AssetName.equalsIgnoreCase(AppConstants.UNKNOWN_ASSET)){
                                            binding.assetTagID.setText(AssetName);
                                            hashMap = new HashMap<>();
                                            hashMap.put("EPC", AssetTagID);
                                            hashMap.put("AssetName", AssetName);
                                            tagList.add(hashMap);
                                        }
                                    }
                                    else{
                                        AssetUtils.showCommonBottomSheetErrorDialog(context, "Please scan the bin tag");
                                    }
                                }
                            } else {
                                AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.invalid_rfid_error));
                            }
                        } else {

                            AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.invalid_rfid_error));
                        }
                    } else {
                        AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.invalid_rfid_error));
                    }

                } else {
                    AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.invalid_rfid_error));
                }
            } else {
                AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.invalid_rfid_error));

            }
        } catch (Exception e) {
            Log.e("INEXCEPTION", "" + e.getMessage());
            AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.no_rfid_error));
        }
    }
    private void startInventory() {
        if (allow_trigger_to_press) {
            allow_trigger_to_press = false;
            CURRENT_EPC = "";
            showProgress(context, "Please wait...Scanning Rfid Tag");
            rfidHandler.startInventory();
        } else {
            hideProgressDialog();
        }
    }
    private void stopInventory() {
        rfidHandler.stopInventory();
        allow_trigger_to_press = true;
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
        //super.onBackPressed();
        showCustomConfirmationDialog("Do you want to go back from this activity?", "BACK");
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
                CURRENT_EPC = "";
                SCANNED_EPC = "";
                IS_ASSET_TAG_SCANNED = false;
                IS_BIN_TAG_SCANNED = false;
                AssetName = "";
                BinName = "";
                AssetTagID = "";
                BinTagID = "";
                allow_trigger_to_press = true;
                binding.binTagID.setText("");
                binding.assetTagID.setText("");
            }
        });
    }
    private void uploadInventoryToServer() {

        if (IS_ASSET_TAG_SCANNED && IS_BIN_TAG_SCANNED) {
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
            try {
                JSONObject jsonobject = null;
                jsonobject = new JSONObject();
                //jsonobject.put(APIConstants.K_USER_ID, SharedPreferencesManager.getSavedUserId(context));
                jsonobject.put(APIConstants.K_DEVICE_ID, SharedPreferencesManager.getDeviceId(context));
                jsonobject.put(APIConstants.K_ACTIVITY_TYPE, "PICK");
                jsonobject.put(APIConstants.K_INVENTORY_START_DATE_TIME, START_DATE);
                jsonobject.put(APIConstants.K_INVENTORY_END_DATE_TIME, END_DATE);
                jsonobject.put(APIConstants.K_INVENTORY_COUNT, ""+tagList.size());
                jsonobject.put(APIConstants.K_ACTIVITY_ASSET_TYPE, db.getAssetTypeNameByAssetTagID(AssetTagID));
                jsonobject.put(APIConstants.K_BIN_TAG_ID, BinTagID);
                jsonobject.put(APIConstants.K_BIN_NAME, BinName);
                JSONArray js = new JSONArray();
                for (int i = 0; i < tagList.size(); i++) {
                    JSONObject barcodeObject = new JSONObject();
                    AssetMaster assetMasterList;
                    assetMasterList = db.getAssetMasterByTagId(AssetTagID);
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