package com.psl.inventorydemo;

import static com.psl.inventorydemo.helper.AssetUtils.hideProgressDialog;
import static com.psl.inventorydemo.helper.AssetUtils.showProgress;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.psl.inventorydemo.adapter.SearchableAdapter;
import com.psl.inventorydemo.database.DatabaseHandler;
import com.psl.inventorydemo.databinding.ActivityAssetRegistrationBinding;
import com.psl.inventorydemo.helper.APIConstants;
import com.psl.inventorydemo.helper.AssetUtils;
import com.psl.inventorydemo.helper.BaseUtil;
import com.psl.inventorydemo.helper.ConnectionDetector;
import com.psl.inventorydemo.helper.SharedPreferencesManager;
import com.psl.inventorydemo.rfid.RFIDInterface;
import com.psl.inventorydemo.rfid.SeuicGlobalRfidHandler;
import com.seuic.uhf.EPC;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.channels.NonReadableChannelException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import okhttp3.OkHttpClient;

public class AssetRegistrationActivity extends AppCompatActivity {
    private final Context context = this;
    private ActivityAssetRegistrationBinding binding;
    private SeuicGlobalRfidHandler rfidHandler;
    DatabaseHandler db;
    ConnectionDetector cd;
    private boolean allow_trigger_to_press = true;
    private String SCANNED_EPC = "";
    private String CURRENT_EPC = "";
    public static final int MAX_LEN = 64;
    String SELECTED_ASSET_TYPE_ID = "";
    String SELECTED_ASSET_TYPE_NAME = "";
    String SELECTED_ASSET_SERIAL_NO = "";
    String NEW_EPC = "";
    SearchableAdapter searchableAssetTypeAdapter;
    Dialog dialog, customConfirmationDialog;
    ArrayList<String> assetTypeList = new ArrayList<>();
    private String default_source_item = "Select Asset Type";
    private boolean isAssetTypeSelected = false;
    private boolean IS_TAG_SCANNED = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_asset_registration);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_asset_registration);
        db = new DatabaseHandler(context);
        cd = new ConnectionDetector(context);
        assetTypeList = db.getAssetType();
        Log.e("AssetList", Arrays.toString(assetTypeList.toArray()));
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String writeData = binding.writeTagId.getText().toString();
                if (!writeData.equalsIgnoreCase("")) {
                    showCustomConfirmationDialog("Do you want to upload?", "UPLOAD");
                }
                else{
                    AssetUtils.showCommonBottomSheetErrorDialog(context, "Please register a tag");
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
        binding.btnWriteTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress(context, "Please wait while writing the tag...");
                if(IS_TAG_SCANNED){
                    if(isAssetTypeSelected){
                        String tagid = binding.readTagid.getText().toString().trim();
                        if(!tagid.equalsIgnoreCase("")){
                            String assettypeid = "";
                            if(!SELECTED_ASSET_TYPE_ID.equalsIgnoreCase("")){
                                assettypeid = AssetUtils.get2DigitAssetTypeId(SELECTED_ASSET_TYPE_ID);
                            }
                            int bank = 1;
                            int address = 5;
                            int length = 1;

                            String str_password = "00000000";
                            Log.e("PASSWORD",str_password);
                            byte[] btPassword = new byte[16];
                            BaseUtil.getHexByteArray(str_password, btPassword, btPassword.length);

                            String str_data = assettypeid.toString();

                            byte[] buffer = new byte[MAX_LEN];

                            if (length > MAX_LEN) {
                                buffer = new byte[length];
                            }
                            BaseUtil.getHexByteArray(str_data, buffer, buffer.length);

                            byte[] finalBuffer = buffer;
                            boolean isdatawritten = rfidHandler.mDevice.writeTagData(BaseUtil.getHexByteArray(CURRENT_EPC), btPassword, bank, address, length, finalBuffer);
                            if (isdatawritten) {
                                startInventory();
                                new Handler().postDelayed(() -> {
                                    allow_trigger_to_press = true;
                                    stopInventory();
                                }, 2000);
                                String assetSer = SELECTED_ASSET_SERIAL_NO;
                                String ser_data = AssetUtils.convertStringToHex(assetSer);
                                int bank1 = 1;
                                int address1 = 8;
                                int length1 = 2;
                                String str_password1 = "00000000";
                                Log.e("PASSWORD",str_password1);
                                byte[] btPassword1 = new byte[16];
                                BaseUtil.getHexByteArray(str_password1, btPassword1, btPassword1.length);
                                byte[] buffer1 = new byte[MAX_LEN];

                                if (length1 > MAX_LEN) {
                                    buffer1 = new byte[length1];
                                }
                                byte[] finalBuffer1 = buffer1;
                                BaseUtil.getHexByteArray(ser_data, buffer1, buffer1.length);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        boolean written = rfidHandler.mDevice.writeTagData(BaseUtil.getHexByteArray(CURRENT_EPC), btPassword1, bank1, address1, length1, finalBuffer1);
                                        if (written) {

                                            NEW_EPC = CURRENT_EPC.substring(0, 2) + str_data + CURRENT_EPC.substring(4, 8) + ser_data + CURRENT_EPC.substring(12);
                                            binding.writeTagId.setText(NEW_EPC);
                                            binding.writeAssetName.setText(SELECTED_ASSET_TYPE_NAME+SELECTED_ASSET_SERIAL_NO);
                                            if(!binding.writeTagId.getText().equals("")){
                                                allow_trigger_to_press = false;
                                                hideProgressDialog();
                                            } else{
                                                showProgress(context, "Please wait while writing the tag...");
                                            }

                                        }
                                        else {
                                            hideProgressDialog();
                                            Log.e("ERROR", "Failed to write second data");
                                        }

                                        Log.e("NEW", NEW_EPC);
                                    }
                                }, 3000);

                            } else{
                                hideProgressDialog();
                                Toast.makeText(context, "Failed writing", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else{
                        hideProgressDialog();
                        AssetUtils.showCommonBottomSheetErrorDialog(context, "Select an asset type");
                    }
                }
                else{
                    hideProgressDialog();
                    AssetUtils.showCommonBottomSheetErrorDialog(context, "Please scan a tag to register");
                }
            }
        });
        binding.searchableAssetType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(IS_TAG_SCANNED){
                    // Initialize dialog
                    dialog = new Dialog(AssetRegistrationActivity.this);

                    // set custom dialog
                    dialog.setContentView(R.layout.dialog_searchable_spinner);

                    // set custom height and width
                    dialog.getWindow().setLayout(650, 800);

                    // set transparent background
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    // show dialog
                    dialog.show();

                    // Initialize and assign variable
                    EditText editText = dialog.findViewById(R.id.edit_text);
                    ListView listView = dialog.findViewById(R.id.list_view);

                    // Initialize array adapter
                    searchableAssetTypeAdapter = new SearchableAdapter(AssetRegistrationActivity.this, assetTypeList);

                    // set adapter
                    listView.setAdapter(searchableAssetTypeAdapter);
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            searchableAssetTypeAdapter.getFilter().filter(s);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // when item selected from list
                            // set selected item on textView
                            // Dismiss dialog
                            dialog.dismiss();
                            SELECTED_ASSET_TYPE_NAME = (String) searchableAssetTypeAdapter.getItem(position);
                            binding.searchableAssetType.setText(SELECTED_ASSET_TYPE_NAME);

                            if (SELECTED_ASSET_TYPE_NAME.equalsIgnoreCase(default_source_item) || SELECTED_ASSET_TYPE_NAME.equalsIgnoreCase("")) {
                                SELECTED_ASSET_TYPE_NAME = "";
                                isAssetTypeSelected = false;

                            } else {
                                isAssetTypeSelected = true;
                                SELECTED_ASSET_TYPE_ID = db.getAssetTypeIDByAssetTypeName(SELECTED_ASSET_TYPE_NAME);
                                Log.e("typeid", SELECTED_ASSET_TYPE_ID);
                                if (cd.isConnectingToInternet()) {
                                    getSerialNo(SELECTED_ASSET_TYPE_ID);
                                }
                                else {
                                    AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.internet_error));
                                }
                            }

                        }
                    });
                }
                else{
                    AssetUtils.showCommonBottomSheetErrorDialog(context, "Please scan a tag to register");
                }

            }
        });
        //showProgress(context, getResources().getString(R.string.uhf_initialization));
        rfidHandler = new SeuicGlobalRfidHandler();
        rfidHandler.onCreate(context, new RFIDInterface() {
            @Override
            public void handleTriggerPress(boolean pressed) {
                runOnUiThread(() -> {
                    if (pressed) {
                        if(allow_trigger_to_press){
                            if(IS_TAG_SCANNED){
                                binding.btnWriteTag.performClick();
                            }
                            else{
                                startInventory();
                                new Handler().postDelayed(() -> {
                                    hideProgressDialog();
                                    allow_trigger_to_press = true;
                                    stopInventory();
                                }, 2000);
                            }
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
                                    CURRENT_EPC = SCANNED_EPC;
                                    Log.e("epc", CURRENT_EPC);
                                    doValidation();
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
            if (SCANNED_EPC != null) {
                if (!SCANNED_EPC.isEmpty()) {
                    if (SCANNED_EPC.length() >= 24) {
                        CURRENT_EPC = SCANNED_EPC;
                        SCANNED_EPC = "";
                        CURRENT_EPC = CURRENT_EPC.substring(0, 24);
                        String companycode = CURRENT_EPC.substring(0, 2);
                        String companycode1 = AssetUtils.hexToNumber(companycode);
                        String assettpid = CURRENT_EPC.substring(2, 4);
                        String serialnumber = CURRENT_EPC.substring(4, 12);
                        if (companycode1.equalsIgnoreCase("21") && !assettpid.equalsIgnoreCase("06")) {
//                            if (db.IsExistAsset(assettpid)) {
                                IS_TAG_SCANNED = true;
                                binding.readTagid.setText(CURRENT_EPC);
//                            } else {
//                                AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.invalid_rfid_error));
//                            }
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
                NEW_EPC = "";
                IS_TAG_SCANNED = false;
                binding.imgStatus.setImageDrawable(getDrawable(R.drawable.rfidscan));
                binding.readTagid.setText("");
                allow_trigger_to_press = true;
                binding.writeTagId.setText("");
                binding.writeAssetName.setText("");
                SELECTED_ASSET_SERIAL_NO = "";
                SELECTED_ASSET_TYPE_ID = "";
                SELECTED_ASSET_TYPE_NAME = "";
                isAssetTypeSelected = false;
                binding.searchableAssetType.setText(default_source_item);
            }
        });
    }
    private void getSerialNo(String assetTypeID) {
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(APIConstants.API_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(APIConstants.API_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(APIConstants.API_TIMEOUT, TimeUnit.SECONDS)
                    .build();
            Log.e("getSerialNoURL", APIConstants.M_URL + APIConstants.M_GET_SERIAL_NO+assetTypeID);
            AndroidNetworking.get(APIConstants.M_URL + APIConstants.M_GET_SERIAL_NO+assetTypeID)
                    .setTag("test")
                    .setPriority(Priority.LOW)
                    .setOkHttpClient(okHttpClient) // passing a custom okHttpClient
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("Response", response.toString());
                            if (response != null) {
                                try {
                                    if (response.getBoolean("status")) {

                                        if(response.has(APIConstants.K_DATA)){
                                            SELECTED_ASSET_SERIAL_NO = response.getString(APIConstants.K_DATA);
                                            Log.e("ser", SELECTED_ASSET_SERIAL_NO);
                                        }
                                    } else {
                                        String message = response.getString("message");
                                        AssetUtils.showCommonBottomSheetErrorDialog(context, message);
                                    }
                                } catch (JSONException e) {
                                    AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.communication_error));
                                }
                            } else {
                                AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.communication_error));
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                           /* String orderDetailsString = AssetUtils.getJsonFromAssets(context, "updateworkorderstatus.json");
                            try {
                                JSONObject mainObject = new JSONObject(orderDetailsString);
                                parseWorkDetailsObjectAndDoAction(mainObject);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }*/
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
    private void uploadInventoryToServer() {
        String writeData = binding.writeTagId.getText().toString();
        if (!writeData.equalsIgnoreCase("")) {
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
                    jsonobject.put(APIConstants.K_ASSET_NAME, SELECTED_ASSET_TYPE_NAME+SELECTED_ASSET_SERIAL_NO);
                    jsonobject.put(APIConstants.K_ASSET_TYPE_ID, SELECTED_ASSET_TYPE_ID);
                    jsonobject.put(APIConstants.K_ASSET_TAG_ID, NEW_EPC);
                    jsonobject.put(APIConstants.K_ASSET_SERIAL_NUMBER, SELECTED_ASSET_SERIAL_NO);
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
                        uploadInventory(result, APIConstants.M_ASSET_REGISTRATION, "Please wait...\n" + " Mapping is in progress");

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
    public void uploadInventory(final JSONObject regObject, String METHOD_NAME, String progress_message) {
        showProgress(context, progress_message);
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(APIConstants.API_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(APIConstants.API_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(APIConstants.API_TIMEOUT, TimeUnit.SECONDS)
                .build();

        Log.e("REGURL", APIConstants.M_URL + METHOD_NAME);
        Log.e("ASSETPALLETMAPRES", regObject.toString());
        AndroidNetworking.post(APIConstants.M_URL + METHOD_NAME).addJSONObjectBody(regObject)
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
                                Log.e("REGRES", result.toString());
                                String status = result.getString(APIConstants.K_STATUS);
                                String message = result.getString(APIConstants.K_MESSAGE);

                                if (status.equalsIgnoreCase("true")) {
                                    allow_trigger_to_press = false;
                                    setDefault();
                                    AssetUtils.showCommonBottomSheetSuccessDialog(context, "Asset Registered Successfully");

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