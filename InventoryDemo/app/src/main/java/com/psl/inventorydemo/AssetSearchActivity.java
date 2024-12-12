package com.psl.inventorydemo;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
//import com.psl.inventorydemo.database.DatabaseHandler;
import com.psl.inventorydemo.adapter.SearchableAdapter;
import com.psl.inventorydemo.database.DatabaseHandler;
import com.psl.inventorydemo.databinding.ActivityAssetSearchBinding;
import com.psl.inventorydemo.helper.AssetUtils;
import com.psl.inventorydemo.model.AssetMaster;
import com.psl.inventorydemo.rfid.RFIDInterface;
import com.psl.inventorydemo.rfid.SeuicGlobalRfidHandler;
import com.seuic.uhf.EPC;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class AssetSearchActivity extends AppCompatActivity {

    ActivityAssetSearchBinding binding;
    private Context context = this;
    private SeuicGlobalRfidHandler rfidHandler;
    DatabaseHandler db;
    ArrayList<String> assetTypeList = new ArrayList<>();
    ArrayList<String> assetNameList = new ArrayList<>();

    private Timer beepTimer;
    private int valid_speed = 0;

    private String SELECTED_ASSET_TYPE_ITEM = "";
    private String SELECTED_ASSET_TYPE_NAME = "";
    public String SELECTED_ASSET_TYPE_ID = "";
    private boolean isSearchOn = false;
    SearchableAdapter searchableAssetTypeAdapter, searchableAssetNameAdapter;
    Dialog dialog, dialog1;
    private String default_asset_type = "Select Asset Type";
    private String default_asset_name = "Select Asset Name";
    private boolean isAssetTypeSelected = false;
    private boolean isAssetNameSelected = false;
    private String AssetType = "";
    private String AssetName = "";
    private String CURRENT_EPC = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_asset_search);
        db = new DatabaseHandler(context);
        assetTypeList = db.getAssetType();
        startBeepTimer(2000);
        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAssetTypeSelected) {
                    if(isAssetNameSelected){
                        if (isSearchOn) {
                            isSearchOn = false;
                            rfidHandler.stopInventory();
                            binding.btnSearch.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_red));
                            binding.btnSearch.setText("Stop");
                        } else {
                            isSearchOn = true;
                            rfidHandler.startInventory();
                            binding.btnSearch.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_green));
                            binding.btnSearch.setText("Start");
                        }
                    }
                    else{
                        AssetUtils.showCommonBottomSheetErrorDialog(context, "Please select an asset name");
                    }
                }
                else{
                    AssetUtils.showCommonBottomSheetErrorDialog(context, "Please select an asset type");
                }

            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomConfirmationDialog("Are you sure you want to go back", "Cancel");
                //TODO show confirmation dialog to go back

            }
        });
        binding.searchableAssetType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize dialog
                dialog = new Dialog(AssetSearchActivity.this);

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
                searchableAssetTypeAdapter = new SearchableAdapter(AssetSearchActivity.this, assetTypeList);

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
                        SELECTED_ASSET_TYPE_ID = (String) searchableAssetTypeAdapter.getItem(position);
                        binding.searchableAssetType.setText(SELECTED_ASSET_TYPE_ID);

                        if (SELECTED_ASSET_TYPE_ID.equalsIgnoreCase(default_asset_type) || SELECTED_ASSET_TYPE_ID.equalsIgnoreCase("")) {
                            SELECTED_ASSET_TYPE_ID = "";
                            isAssetTypeSelected = false;

                        } else {
                            isAssetTypeSelected = true;
                            AssetType = SELECTED_ASSET_TYPE_ID;
                            assetNameList = db.getAssetNameByAssetType(AssetType);
                        }

                    }
                });
            }
        });
        binding.searchableAssetName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAssetTypeSelected) {
                    // Initialize dialog
                    dialog = new Dialog(AssetSearchActivity.this);

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
                    searchableAssetNameAdapter = new SearchableAdapter(AssetSearchActivity.this, assetNameList);

                    // set adapter
                    listView.setAdapter(searchableAssetNameAdapter);
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            searchableAssetNameAdapter.getFilter().filter(s);
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
                            SELECTED_ASSET_TYPE_NAME = (String) searchableAssetNameAdapter.getItem(position);
                            binding.searchableAssetName.setText(SELECTED_ASSET_TYPE_NAME);

                            if (SELECTED_ASSET_TYPE_NAME.equalsIgnoreCase(default_asset_name) || SELECTED_ASSET_TYPE_NAME.equalsIgnoreCase("")) {
                                SELECTED_ASSET_TYPE_NAME = "";
                                isAssetNameSelected = false;

                            } else {
                                isAssetNameSelected = true;
                                AssetName = SELECTED_ASSET_TYPE_NAME;
                            }


                        }
                    });
                } else {
                    AssetUtils.showCommonBottomSheetErrorDialog(context, "Please select an Asset Type");
                }
            }

        });
        AssetUtils.showProgress(context, getResources().

                getString(R.string.uhf_initialization));
        rfidHandler = new SeuicGlobalRfidHandler();
        rfidHandler.onCreate(context, new RFIDInterface() {
            @Override
            public void handleTriggerPress(boolean pressed) {
                runOnUiThread(() -> {
                    if (pressed) {
                        binding.btnSearch.performClick();
                    }
                });
            }

            @Override
            public void RFIDInitializationStatus(boolean status) {
                runOnUiThread(() -> {
                    AssetUtils.hideProgressDialog();

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
                            int maxRssi = Integer.MIN_VALUE;
                            String maxRssiEpc = null;

                            for (int i = 0; i < epcList.size(); i++) {
                                String epc = epcList.get(i).getId();
                                int rssi = epcList.get(i).rssi;

                                // Update max RSSI and corresponding EPC
                                if (rssi > maxRssi) {
                                    maxRssi = rssi; // Store the RSSI directly
                                    maxRssiEpc = epc;
                                }
                            }

                            // After finding the maximum RSSI EPC, process it
                            if (maxRssiEpc != null && !maxRssiEpc.isEmpty()) {
                                if (maxRssiEpc.length() >= 24) {
                                    CURRENT_EPC = maxRssiEpc;
                                }
                                doDataValidations(CURRENT_EPC, maxRssi);
                                Log.e("EPC", CURRENT_EPC);
                                Log.e("RSSI", String.valueOf(maxRssi));
                            }
                        }
                    }
                    else {
                        // No EPCs found, reset the UI and internal state
                        binding.textPercentage.setText("0");
                        CURRENT_EPC = "";
                        // Reset any other relevant UI elements or states
                        rfidHandler.stopInventory();
                        new Handler().postDelayed(() -> {
                            isSearchOn = true;
                            rfidHandler.startInventory();
                            binding.btnSearch.setText("Stop");
                            binding.btnSearch.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_red));
                        }, 2000);
                    }
                });
            }
        });

    }

    private void doDataValidations(String epc, int rssi) {
        valid_speed++;
        if (rssi < 0) {
            rssi = (-1) * rssi;
        }
        // Reset the text percentage if no tag is detected
        if (epc == null || epc.isEmpty()) {
            binding.textPercentage.setText("0");
            return; // Exit the method early
        }
        String companyCode = epc.substring(0, 2);
        companyCode = AssetUtils.hexToNumber(companyCode);
        String assetType = epc.substring(2, 4);
        String serialNo = epc.substring(8, 12);
            String selectedSerialNo = db.getAssetSerialNumberByAssetName(AssetName);
            String selectedAssetTypeId = db.getAssetTypeIDByAssetType(AssetType);
            Log.e("epcSer1", selectedSerialNo);
            Log.e("epcAst1", selectedAssetTypeId);
        selectedSerialNo = AssetUtils.convertStringToHex(selectedSerialNo);
            selectedAssetTypeId = AssetUtils.get2DigitAssetTypeId(selectedAssetTypeId);
            Log.e("epcSer", selectedSerialNo);
            Log.e("epcAst", selectedAssetTypeId);
            if (companyCode.equalsIgnoreCase("21") && assetType.equalsIgnoreCase(selectedAssetTypeId) && serialNo.equalsIgnoreCase(selectedSerialNo)) {
                int a = AssetUtils.getRangePercentage(rssi);
                if (a >= 80 && a <= 100) {
                    startBeepTimer(500);
                } else if (a >= 50 && a < 80) {
                    startBeepTimer(1000);
                } else {
                    startBeepTimer(2000);
                }
                binding.textPercentage.setText(String.valueOf(a) + " %");
            }
            else {
                binding.textPercentage.setText("0");
            }
    }

    @Override
    public void onResume() {
        super.onResume();
        setDefault();
        startBeepTimer(2000);
        rfidHandler.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setDefault();
        rfidHandler.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (beepTimer != null) {
            beepTimer.cancel();
        }
        rfidHandler.onPause();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        binding.btnCancel.performClick();

    }

    Dialog customConfirmationDialog;

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
                isSearchOn = false;
                rfidHandler.stopInventory();
                if (action.equalsIgnoreCase("Cancel")) {
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
    private void startBeepTimer(long interval) {
        if (beepTimer != null) {
            beepTimer.cancel(); // Cancel existing timer
        }

        beepTimer = new Timer();
        beepTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Called each time when beepInterval milliseconds
                if (isSearchOn) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.btnSearch.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_red));
                            binding.btnSearch.setText("Stop");
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
                            binding.btnSearch.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_green));
                            binding.btnSearch.setText("Start");
                        }
                    });
                }
            }
        }, 0, interval);
    }
    private void setDefault() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (beepTimer != null) {
                    beepTimer.cancel();
                }
                if (assetNameList != null) {
                    assetNameList.clear();
                }
                CURRENT_EPC = "";
                binding.textPercentage.setText("0");
                binding.searchableAssetType.setText(default_asset_type);
                binding.searchableAssetName.setText(default_asset_name);
                SELECTED_ASSET_TYPE_NAME = "";
                SELECTED_ASSET_TYPE_ID = "";
                isSearchOn = false;
                isAssetTypeSelected = false;
                isAssetNameSelected = false;
                AssetType = "";
                AssetName = "";
            }
        });
    }
}