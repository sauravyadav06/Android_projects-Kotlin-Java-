package com.psl.inventorydemo;

import static com.psl.inventorydemo.helper.AssetUtils.hideProgressDialog;
import static com.psl.inventorydemo.helper.AssetUtils.showProgress;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.psl.inventorydemo.adapter.CustomRecyclerViewDashboardAdapter;
import com.psl.inventorydemo.database.DatabaseHandler;
import com.psl.inventorydemo.databinding.ActivityDashboardBinding;
import com.psl.inventorydemo.helper.APIConstants;
import com.psl.inventorydemo.helper.AssetUtils;
import com.psl.inventorydemo.helper.ConnectionDetector;
import com.psl.inventorydemo.helper.SharedPreferencesManager;
import com.psl.inventorydemo.model.AssetMaster;
import com.psl.inventorydemo.model.DashboardModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class DashboardActivity extends AppCompatActivity {
    private Context context = this;
    private ActivityDashboardBinding binding;
    CustomRecyclerViewDashboardAdapter customAdapter;
    List<DashboardModel> dashboardModelList;
    DatabaseHandler db;
    ConnectionDetector cd;
    private Handler assetHandler = new Handler();
    private Runnable assetPollingRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_dashboard);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_dashboard);
        db = new DatabaseHandler(context);
        cd = new ConnectionDetector(context);
        dashboardModelList = new ArrayList<>();
        // Create and add instances of DashboardModel
        DashboardModel model1 = new DashboardModel();
        model1.setMenuID(1);
        model1.setTextTitle("Asset Registration");
        dashboardModelList.add(model1);

        DashboardModel model2 = new DashboardModel();
        model2.setMenuID(2);
        model2.setTextTitle("Check IN/ Check OUT");
        dashboardModelList.add(model2);

        DashboardModel model3 = new DashboardModel();
        model3.setMenuID(3);
        model3.setTextTitle("Inventory");
        dashboardModelList.add(model3);

        DashboardModel model4 = new DashboardModel();
        model4.setMenuID(4);
        model4.setTextTitle("Search");
        dashboardModelList.add(model4);

        DashboardModel model5 = new DashboardModel();
        model5.setMenuID(5);
        model5.setTextTitle("Pick");
        dashboardModelList.add(model5);

        DashboardModel model6 = new DashboardModel();
        model6.setMenuID(6);
        model6.setTextTitle("Put");
        dashboardModelList.add(model6);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2); // you can change grid columns to 3 or more
        binding.recycleview.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        customAdapter = new CustomRecyclerViewDashboardAdapter(DashboardActivity.this, dashboardModelList);
        binding.recycleview.setAdapter(customAdapter); // set the Adapter to RecyclerView
    }
    private void startAssetsApiHandler() {
        if (assetHandler != null) {
            // Remove any existing callbacks
            assetHandler.removeCallbacks(assetPollingRunnable);
        }
        assetPollingRunnable = new Runnable() {
            @Override
            public void run() {
                if (cd.isConnectingToInternet()) {
                    getAssetsMaster(APIConstants.M_GET_ASSET_MASTER+SharedPreferencesManager.getDeviceId(context), "GETTING ASSETS");
                    assetHandler.postDelayed(this, 5000);
                }
                else {
                    AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.internet_error));
                }
            }
        };
        assetHandler.postDelayed(assetPollingRunnable, 2000);
    }

    private void stopAssetsApiHandler() {
        // Remove any pending callbacks and messages
        assetHandler.removeCallbacks(assetPollingRunnable);
    }
    public void gridClicked(int position, int menu_id,String textTitle) {
            switch (menu_id) {
                case 1:
                    Intent regIntent = new Intent(DashboardActivity.this, AssetRegistrationActivity.class);
                    startActivity(regIntent);

                    break;
                case 2:
                    Intent checkinoutintent = new Intent(DashboardActivity.this, CheckinoutActivity.class);
                    startActivity(checkinoutintent);
                    break;
                case 3:
                   Intent inventIntent = new Intent(DashboardActivity.this, InventoryActivity.class);
                   startActivity(inventIntent);
                    break;
                case 4:
                    Intent searchIntent = new Intent(DashboardActivity.this, AssetSearchActivity.class);
                    startActivity(searchIntent);
                    break;
                case 5:
                    Intent pickIntent = new Intent(DashboardActivity.this, AssetPickActivity.class);
                    startActivity(pickIntent);
                    break;
                case 6:
                    Intent putIntent = new Intent(DashboardActivity.this, AssetPutActivity.class);
                    startActivity(putIntent);
                    break;

                default:
                    AssetUtils.showCommonBottomSheetErrorDialog(context, "No Content");
                    break;

        }
    }
    public void getAssetsMaster(String METHOD_NAME, String progress_message) {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(APIConstants.API_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(APIConstants.API_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(APIConstants.API_TIMEOUT, TimeUnit.SECONDS)
                .build();
        //showProgress(context, progress_message);
        Log.e("ASSETMASTERURL", SharedPreferencesManager.getHostUrl(context) + METHOD_NAME );
        AndroidNetworking.get(APIConstants.M_URL + METHOD_NAME)
                .setTag("test")
                .setPriority(Priority.LOW)
                .setOkHttpClient(okHttpClient) // passing a custom okHttpClient
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject result) {

                        if (result != null) {
                            try {
                                hideProgressDialog();
                                Log.e("ASSETMASTERRESULT", result.toString());
                                if (result.has(APIConstants.K_STATUS)) {
                                    if (result.getString(APIConstants.K_STATUS).equalsIgnoreCase("true")) {
                                        JSONArray dataArray;
                                        if (result.has(APIConstants.K_DATA)) {
                                            dataArray = result.getJSONArray(APIConstants.K_DATA);
                                            if (dataArray != null) {
                                                if (dataArray.length() > 0) {
                                                    parseMasterFetchAndDoAction(dataArray);
                                                }
                                            }
                                        }

                                    } else {
                                        String message = result.getString(APIConstants.K_MESSAGE);
                                        AssetUtils.showCommonBottomSheetErrorDialog(context, message);
                                    }
                                }
                            } catch (JSONException e) {
                                AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.something_went_wrong_error));
                            }
                        } else {
                            hideProgressDialog();
                            // Toast.makeText(context,"Communication Error",Toast.LENGTH_SHORT).show();
                            AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.communication_error));
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        hideProgressDialog();
                        Log.e("ERROR", anError.getErrorDetail());
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
    private void parseMasterFetchAndDoAction(JSONArray result) {

        List<AssetMaster> list = new ArrayList<>();
        if (result.length() > 0) {
            try {
                for (int i = 0; i < result.length(); i++) {
                    AssetMaster assetMaster = new AssetMaster();
                    JSONObject jsonObject = result.getJSONObject(i);

                    if (jsonObject.has(APIConstants.K_ASSET_ID)) {
                        String assetid = jsonObject.getString(APIConstants.K_ASSET_ID).trim();
                        assetMaster.setAssetID(assetid);
                    }

                    if (jsonObject.has(APIConstants.K_ASSET_NAME)) {
                        String assetname = jsonObject.getString(APIConstants.K_ASSET_NAME).trim();
                        assetMaster.setAssetName(assetname);
                    }

                    if (jsonObject.has(APIConstants.K_ASSET_TYPE)) {
                        String assetType = jsonObject.getString(APIConstants.K_ASSET_TYPE).trim();
                        assetMaster.setAssetType(assetType);
                    }
                    if (jsonObject.has(APIConstants.K_ASSET_TYPE_ID)) {
                        String assetTypeID = jsonObject.getString(APIConstants.K_ASSET_TYPE_ID).trim();
                        assetMaster.setAssetTypeID(assetTypeID);
                    }
                    if (jsonObject.has(APIConstants.K_ASSET_TAG_ID)) {
                        String assetTagID = jsonObject.getString(APIConstants.K_ASSET_TAG_ID).trim();
                        assetMaster.setAssetTagID(assetTagID);
                    }
                    if (jsonObject.has(APIConstants.K_ASSET_SERIAL_NUMBER)) {
                        String assetSerialNo = jsonObject.getString(APIConstants.K_ASSET_SERIAL_NUMBER).trim();
                        assetMaster.setAssetSerialNo(assetSerialNo);
                    }
                    list.add(assetMaster);
                }
                if (list.size() > 0) {
                    db.deleteAssetMaster();
                    db.storeAssetMaster(list);
                }
            } catch (JSONException e) {
                AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.something_went_wrong_error));
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        startAssetsApiHandler();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAssetsApiHandler();
    }

    @Override
    protected void onDestroy() {
        stopAssetsApiHandler();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        stopAssetsApiHandler();
        super.onBackPressed();
    }
}