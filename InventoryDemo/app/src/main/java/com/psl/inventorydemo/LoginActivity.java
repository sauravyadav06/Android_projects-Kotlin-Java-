package com.psl.inventorydemo;

import static com.psl.inventorydemo.helper.AssetUtils.hideProgressDialog;
import static com.psl.inventorydemo.helper.AssetUtils.showProgress;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.provider.Settings;
import android.util.Log;

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
import com.psl.inventorydemo.databinding.ActivityLoginBinding;
import com.psl.inventorydemo.helper.APIConstants;
import com.psl.inventorydemo.helper.AssetUtils;
import com.psl.inventorydemo.helper.ConnectionDetector;
import com.psl.inventorydemo.helper.SharedPreferencesManager;
import com.psl.inventorydemo.model.AssetTypeMaster;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class LoginActivity extends AppCompatActivity {
    private Context context = this;
    private ActivityLoginBinding binding;
    ConnectionDetector cd;
    DatabaseHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        db = new DatabaseHandler(context);
        cd = new ConnectionDetector(context);
        String androidID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        androidID = androidID.toUpperCase();
        SharedPreferencesManager.setDeviceId(context, androidID);

        if (SharedPreferencesManager.getIsLoginSaved(context)) {
            binding.chkRemember.setChecked(true);
            binding.edtUserName.setText(SharedPreferencesManager.getSavedUser(context));
            binding.edtPassword.setText(SharedPreferencesManager.getSavedPassword(context));
        } else {
            binding.chkRemember.setChecked(false);
            binding.edtUserName.setText("");
            binding.edtPassword.setText("");
        }
        binding.btnLogin.setOnClickListener(view -> {
//           Intent loginIntent = new Intent(LoginActivity.this, DashboardActivity.class);
//            startActivity(loginIntent);

                String user = binding.edtUserName.getText().toString().trim();
                String password = binding.edtPassword.getText().toString().trim();
                if (user.equalsIgnoreCase("") || password.equalsIgnoreCase("")) {
                    AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.login_data_validation));
                } else {
                    if (cd.isConnectingToInternet()) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put(APIConstants.K_USER, user);
                            jsonObject.put(APIConstants.K_PASSWORD, password);
                            jsonObject.put(APIConstants.K_DEVICE_ID, SharedPreferencesManager.getDeviceId(context));
                            userLogin(jsonObject, APIConstants.M_USER_LOGIN, "Please wait...\n" + "User login is in progress");

                        } catch (JSONException e) {

                        }
                    }else {
                        AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.internet_error));
                    }

                }

        });
        binding.btnClear.setOnClickListener(view -> {
            binding.chkRemember.setChecked(false);
            binding.edtUserName.setText("");
            binding.edtPassword.setText("");
            SharedPreferencesManager.setIsLoginSaved(context, false);
            SharedPreferencesManager.setSavedUser(context, "");
            SharedPreferencesManager.setSavedPassword(context, "");
            binding.chkRemember.setChecked(false);
        });
        binding.textDeviceId.setText("Device ID: " + SharedPreferencesManager.getDeviceId(context));
    }
    public void userLogin(final JSONObject loginRequestObject, String METHOD_NAME, String progress_message) {
        showProgress(context, progress_message);
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(APIConstants.API_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(APIConstants.API_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(APIConstants.API_TIMEOUT, TimeUnit.SECONDS)
                .build();
Log.e("LoginURL", APIConstants.M_URL + METHOD_NAME);
        AndroidNetworking.post(APIConstants.M_URL + METHOD_NAME).addJSONObjectBody(loginRequestObject)
                .setTag("test")
                .setPriority(Priority.LOW)
                .setOkHttpClient(okHttpClient) // passing a custom okHttpClient
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject result) {
                        hideProgressDialog();
                        if (result != null) {
                            try {
                                Log.e("LOGINRESULT", result.toString());
                                String status = result.getString(APIConstants.K_STATUS).trim();
                                String message = result.getString(APIConstants.K_MESSAGE).trim();

                                if (status.equalsIgnoreCase("true")) {
                                    SharedPreferencesManager.setSavedUser(context, loginRequestObject.getString(APIConstants.K_USER));
                                    SharedPreferencesManager.setSavedPassword(context, loginRequestObject.getString(APIConstants.K_PASSWORD));
                                    SharedPreferencesManager.setSavedPassword(context, binding.edtPassword.getText().toString().trim());
                                    JSONArray dataArray = null;
                                    if(result.has(APIConstants.K_DATA)){
                                        dataArray = result.getJSONArray(APIConstants.K_DATA);
                                        if (dataArray.length()>0) {
                                            List<AssetTypeMaster> assetTypeMasterList = new ArrayList<>();
                                            for (int i = 0; i < dataArray.length(); i++) {
                                                JSONObject assetObj = dataArray.getJSONObject(i);
                                                AssetTypeMaster assetTypeMaster = new AssetTypeMaster();

                                                // Extract AssetName and ATypeID
                                                if (assetObj.has(APIConstants.K_ASSET_TYPE_ID)) {
                                                    String assettypeid = assetObj.getString(APIConstants.K_ASSET_TYPE_ID).trim();
                                                    assettypeid = AssetUtils.get2DigitAssetTypeId(assettypeid);
                                                    assetTypeMaster.setAssetTypeID(assettypeid);
                                                    Log.e("assetTypeID", assettypeid);
                                                    Log.e("assetTypeID1", assetTypeMaster.getAssetTypeID());
                                                }
                                                if (assetObj.has("AssetName")) {
                                                    assetTypeMaster.setAssetName(assetObj.getString("AssetName").trim());
                                                    Log.e("assetName", assetObj.getString("AssetName").trim());
                                                    Log.e("assetName1", assetTypeMaster.getAssetName());
                                                }

                                                // Add the object to the list
                                                assetTypeMasterList.add(assetTypeMaster);
                                            }
                                            if (assetTypeMasterList.size() > 0) {
                                                db.deleteAssetTypeMaster();
                                                db.storeAssetTypeMaster(assetTypeMasterList);
                                            }
                                            if (binding.chkRemember.isChecked()) {
                                                SharedPreferencesManager.setIsLoginSaved(context, true);
                                            } else {
                                                SharedPreferencesManager.setIsLoginSaved(context, false);
                                            }
                                            Intent loginIntent = new Intent(LoginActivity.this, DashboardActivity.class);
                                            startActivity(loginIntent);
                                        }
                                    }
                                } else {
                                    AssetUtils.showCommonBottomSheetErrorDialog(context, message);
                                }
                            } catch (JSONException e) {
                                hideProgressDialog();
                                AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.something_went_wrong_error));
                            }
                        } else {
                            hideProgressDialog();
                            AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.communication_error));
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        hideProgressDialog();
                        Log.e("ERROR", anError.getErrorDetail());
//                        if (BuildConfig.DEBUG) {
//                            // do something for a debug build
//                            try {
//                                parseJson(new JSONObject(AssetUtils.getJsonFromAssets(context,"loginres.json")),new JSONObject(AssetUtils.getJsonFromAssets(context,"loginreq.json")));
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }else{
                        if (anError.getErrorDetail().equalsIgnoreCase("responseFromServerError")) {
                            AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.communication_error));
                        } else if (anError.getErrorDetail().equalsIgnoreCase("connectionError")) {
                            AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.internet_error));
                        } else {
                            AssetUtils.showCommonBottomSheetErrorDialog(context, getResources().getString(R.string.internet_error));
                        }
                        //}

                    }
                });
    }


}