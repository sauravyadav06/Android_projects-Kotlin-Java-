package com.psl.inventorydemo.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static final String SHARED_PREF_NAME = "SEUIC_INVENTORY_EXPORT_SHARED_PREF";

    private static final String IS_HOST_CONFIG = "IS_HOST_CONFIG";
    private static final String HOST_URL = "HOST_URL";
    private static final String ASSET_MASTER_LAST_SYNC_DATE = "ASSET_MASTER_LAST_SYNC_DATE";


    private static final String IS_LOGIN_SAVED = "IS_LOGIN_SAVED";
    private static final String LOGIN_USER = "LOGIN_USER";
    private static final String LOGIN_USER_ID = "LOGIN_USER_ID";
    private static final String LOGIN_CUSTOMER_ID = "LOGIN_CUSTOMER_ID";
    private static final String LOGIN_PASSWORD = "LOGIN_PASSWORD";
    private static final String COMPANY_CODE = "COMPANY_CODE";
    private static final String COMPANY_ID = "COMPANY_ID";
    private static final String CURRENT_ACCESS_PASSWORD = "CURRENT_ACCESS_PASSWORD";
    private static final String DEVICE_ID = "DEVICE_ID";
    private static final String SAVED_POWER = "POWER";

    private SharedPreferencesManager() {}

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static boolean getIsLoginSaved(Context context) {
        return getSharedPreferences(context).getBoolean(IS_LOGIN_SAVED, false);
    }


    public static int getPower(Context context) {
        return getSharedPreferences(context).getInt(SAVED_POWER, 30);
    }

    public static void setPower(Context context, int newValue) {

        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(SAVED_POWER, newValue);
        editor.commit();
    }

    public static void setHostUrl(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(HOST_URL, newValue);
        editor.commit();
    }

    public static String getHostUrl(Context context) {
        return getSharedPreferences(context).getString(HOST_URL, "");
    }

    public static boolean getIsHostConfig(Context context) {
        return getSharedPreferences(context).getBoolean(IS_HOST_CONFIG, false);
    }

    public static void setIsHostConfig(Context context, boolean newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(IS_HOST_CONFIG, newValue);
        editor.commit();
    }
    public static void setIsLoginSaved(Context context, boolean newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(IS_LOGIN_SAVED, newValue);
        editor.commit();
    }

    public static String getCurrentAccessPassword(Context context) {
        return getSharedPreferences(context).getString(CURRENT_ACCESS_PASSWORD, "00000000");
    }

    public static void setCurrentAccessPassword(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(CURRENT_ACCESS_PASSWORD, newValue);
        editor.commit();
    }

    public static String getDeviceId(Context context) {
        return getSharedPreferences(context).getString(DEVICE_ID, "");
    }

    public static void setDeviceId(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(DEVICE_ID, newValue);
        editor.commit();
    }

    public static String getSavedUser(Context context) {
        return getSharedPreferences(context).getString(LOGIN_USER, "");
    }

    public static void setSavedUser(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(LOGIN_USER, newValue);
        editor.commit();
    }

    public static void setSavedUserId(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(LOGIN_USER_ID, newValue);
        editor.commit();
    }

    public static String getSavedUserId(Context context) {
        return getSharedPreferences(context).getString(LOGIN_USER_ID, "");
    }


    public static String getSavedPassword(Context context) {
        return getSharedPreferences(context).getString(LOGIN_PASSWORD, "");
    }

    public static void setSavedPassword(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(LOGIN_PASSWORD, newValue);
        editor.commit();
    }


    public static String getCompanyCode(Context context) {
        return getSharedPreferences(context).getString(COMPANY_CODE, "15");
    }

    public static void setCompanyCode(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(COMPANY_CODE, newValue);
        editor.commit();
    }


    public static String getCompanyId(Context context) {
        return getSharedPreferences(context).getString(COMPANY_ID, "");
    }

    public static void setCompanyId(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(COMPANY_ID, newValue);
        editor.commit();
    }

    public static String getCustomerId(Context context) {
        return getSharedPreferences(context).getString(LOGIN_CUSTOMER_ID, "");
    }

    public static void setCustomerId(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(LOGIN_CUSTOMER_ID, newValue);
        editor.commit();
    }
    public static String getAssetMasterLastSyncDate(Context context) {
        return getSharedPreferences(context).getString(ASSET_MASTER_LAST_SYNC_DATE, "01-01-1970");
    }

    public static void setAssetMasterLastSyncDate(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(ASSET_MASTER_LAST_SYNC_DATE, newValue);
        editor.commit();
    }

}
