package com.psl.inventorydemo.helper;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.psl.inventorydemo.R;
import com.psl.inventorydemo.rfid.SeuicGlobalRfidHandler;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AssetUtils {



    public static boolean isStringContainsOnlyNumbers(String str) {
        // Regex to check string
        // contains only digits
        String regex = "[0-9]+";
        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the string is empty
        // return false
        if (str == null) {
            return false;
        }

        // Find match between given string
        // and regular expression
        // using Pattern.matcher()
        Matcher m = p.matcher(str);

        // Return if the string
        // matched the ReGex
        return m.matches();
    }
    public static String get2DigitNumber(String number){
        String newnumber = number.trim();
        if(number.length() == 1){
            newnumber = "0"+number;
        }
        return newnumber;
    }

    static ProgressDialog progressDialog;

    /**
     * method to show Progress Dialog
     */
    public static void showProgress(Context context, String progress_message) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(progress_message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        //progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    /**
     * method to hide Progress Dialog
     */
    public static void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public static int getPercentage(int value) {
        int a = 0;
        switch (value) {

            case 39:
                a = 100;
                break;
            case 40:
                a = 98;
                break;
            case 41:
                a = 97;
                break;
            case 42:
                a = 96;
                break;
            case 43:
                a = 94;
                break;
            case 44:
                a = 92;
                break;
            case 45:
                a = 90;
                break;
            case 46:
                a = 89;
                break;
            case 47:
                a = 87;
                break;
            case 48:
                a = 85;
                break;
            case 49:
                a = 84;
                break;
            case 50:
                a = 82;
                break;
            case 51:
                a = 79;
                break;
            case 52:
                a = 75;
                break;
            case 53:
                a = 72;
                break;
            case 54:
                a = 70;
                break;
            case 55:
                a = 67;
                break;
            case 56:
                a = 65;
                break;
            case 57:
                a = 62;
                break;
            case 58:
                a = 60;
                break;
            case 59:
                a = 57;
                break;
            case 60:
                a = 54;
                break;
            case 61:
                a = 51;
                break;
            case 62:
                a = 48;
                break;
            case 63:
                a = 43;
                break;
            case 64:
                a = 40;
                break;
            case 65:
                a = 36;
                break;
            case 66:
                a = 33;
                break;
            case 67:
                a = 31;
                break;
            case 68:
                a = 29;
                break;
            case 69:
                a = 27;
                break;
            case 70:
                a = 25;
                break;
            case 71:
                a = 23;
                break;
            case 72:
                a = 21;
                break;
            case 73:
                a = 19;
                break;
            case 74:
                a = 17;
                break;
            case 75:
                a = 15;
                break;
            case 76:
                a = 13;
                break;
            case 77:
                a = 11;
                break;
            case 78:
                a = 10;
                break;
            case 79:
                a = 8;
                break;
            case 80:
                a = 7;
                break;
            case 81:
                a = 6;
                break;
            case 82:
                a = 5;
                break;
            case 83:
                a = 4;
                break;
            case 84:
                a = 3;
                break;
            case 85:
                a = 2;
                break;
            case 86:
                a = 1;
                break;
        }
        return a;
    }
    public static int getRangePercentage(int value) {
        int a = 0;
        if (value < 30) {
            a = 100;
        } else if (value >= 30 && value < 32) {
            a = 99;
        } else if (value >= 32 && value < 34) {
            a = 98;
        } else if (value >= 34 && value < 36) {
            a = 97;
        } else if (value >= 36 && value < 38) {
            a = 96;
        } else if (value >= 38 && value < 40) {
            a = 95;
        } else if (value >= 40 && value < 42) {
            a = 94;
        } else if (value >= 42 && value < 44) {
            a = 93;
        } else if (value >= 44 && value < 46) {
            a = 92;
        } else if (value >= 46 && value < 48) {
            a = 91;
        } else if (value >= 48 && value < 50) {
            a = 90;
        } else if (value >= 50 && value < 52) {
            a = 89;
        } else if (value >= 52 && value < 54) {
            a = 88;
        } else if (value >= 54 && value < 56) {
            a = 87;
        } else if (value >= 56 && value < 58) {
            a = 86;
        } else if (value >= 58 && value < 60) {
            a = 85;
        } else if (value >= 60 && value < 62) {
            a = 84;
        } else if (value >= 62 && value < 64) {
            a = 83;
        } else if (value >= 64 && value < 66) {
            a = 82;
        } else if (value >= 66 && value < 68) {
            a = 81;
        } else if (value >= 68 && value < 70) {
            a = 80;
        } else if (value >= 70 && value < 72) {
            a = 79;
        } else if (value >= 72 && value < 74) {
            a = 78;
        } else if (value >= 74 && value < 76) {
            a = 77;
        } else if (value >= 76 && value < 78) {
            a = 76;
        } else if (value >= 78 && value < 80) {
            a = 75;
        } else if (value >= 80 && value < 82) {
            a = 74;
        } else if (value >= 82 && value < 84) {
            a = 73;
        } else if (value >= 84 && value < 86) {
            a = 72;
        } else if (value >= 86 && value < 88) {
            a = 71;
        } else if (value >= 88 && value < 90) {
            a = 70;
        } else if (value >= 90 && value < 92) {
            a = 69;
        } else if (value >= 92 && value < 94) {
            a = 68;
        } else if (value >= 94 && value < 96) {
            a = 67;
        } else if (value >= 96 && value < 98) {
            a = 66;
        } else if (value >= 98 && value < 100) {
            a = 65;
        } else if (value >= 100 && value < 102) {
            a = 64;
        } else if (value >= 102 && value < 104) {
            a = 63;
        } else if (value >= 104 && value < 106) {
            a = 62;
        } else if (value >= 106 && value < 108) {
            a = 61;
        } else if (value >= 108 && value < 110) {
            a = 60;
        } else if (value >= 110 && value < 112) {
            a = 59;
        } else if (value >= 112 && value < 114) {
            a = 58;
        } else if (value >= 114 && value < 116) {
            a = 57;
        } else if (value >= 116 && value < 118) {
            a = 56;
        } else if (value >= 118 && value < 120) {
            a = 55;
        } else if (value >= 120 && value < 122) {
            a = 54;
        } else if (value >=  122 && value < 124) {
            a = 53;
        } else if (value >= 124 && value < 126) {
            a = 52;
        } else if (value >= 126 && value < 128) {
            a = 51;
        } else if (value >= 128 && value < 130) {
            a = 50;
        } else if (value >= 130 && value < 132) {
            a = 49;
        } else if (value >= 132 && value < 134) {
            a = 48;
        } else if (value >= 134 && value < 136) {
            a = 47;
        } else if (value >= 136 && value < 138) {
            a = 46;
        } else if (value >= 138 && value < 140) {
            a = 45;
        } else if (value >= 140 && value < 142) {
            a = 44;
        } else if (value >= 142 && value < 144) {
            a = 43;
        } else if (value >= 144 && value < 146) {
            a = 42;
        } else if (value >= 146 && value < 148) {
            a = 41;
        } else if (value >= 148 && value < 150) {
            a = 40;
        } else if (value >= 150 && value < 152) {
            a = 39;
        } else if (value >= 152 && value < 154) {
            a = 38;
        } else if (value >= 154 && value < 156) {
            a = 37;
        } else if (value >= 156 && value < 158) {
            a = 36;
        } else if (value >= 158 && value < 160) {
            a = 35;
        } else if (value >= 160 && value < 162) {
            a = 34;
        } else if (value >= 162 && value < 164) {
            a = 33;
        } else if (value >= 164 && value < 166) {
            a = 32;
        } else if (value >= 166 && value < 168) {
            a = 31;
        } else if (value >= 168 && value < 170) {
            a = 30;
        } else if (value >= 170 && value < 172) {
            a = 29;
        } else if (value >= 172 && value < 174) {
            a = 28;
        } else if (value >= 174 && value < 176) {
            a = 27;
        } else if (value >= 176 && value < 178) {
            a = 26;
        } else if (value >= 178 && value < 180) {
            a = 25;
        } else if (value >= 180 && value < 182) {
            a = 24;
        } else if (value >= 182 && value < 184) {
            a = 23;
        } else if (value >= 184 && value < 186) {
            a = 22;
        } else if (value >= 186 && value < 188) {
            a = 21;
        } else if (value >= 188 && value < 190) {
            a = 20;
        } else if (value >= 190 && value < 192) {
            a = 19;
        } else if (value >= 192 && value < 194) {
            a = 18;
        } else if (value >= 194 && value < 196) {
            a = 17;
        } else if (value >= 196 && value < 198) {
            a = 16;
        } else if (value >= 198 && value < 200) {
            a = 15;
        } else if (value >= 200 && value < 202) {
            a = 14;
        } else if (value >= 202 && value < 204) {
            a = 13;
        } else if (value >= 204 && value < 206) {
            a = 12;
        } else if (value >= 206 && value < 208) {
            a = 11;
        } else if (value >= 208 && value < 210) {
            a = 10;
        } else if (value >= 210 && value < 212) {
            a = 9;
        } else if (value >= 212 && value < 214) {
            a = 8;
        } else if (value >= 214 && value < 216) {
            a = 7;
        } else if (value >= 216 && value < 218) {
            a = 6;
        } else if (value >= 218 && value < 220) {
            a = 5;
        } else if (value >= 220 && value < 222) {
            a = 4;
        } else if (value >= 222 && value < 224) {
            a = 3;
        } else if (value >= 224 && value < 226) {
            a = 2;
        } else if (value >= 226 && value < 228) {
            a = 1;
        } else {
            a = 0; // For value >= 228
        }
        return a;
    }


    static BottomSheetDialog bottomSheetDialog;

    public static void showCommonBottomSheetErrorDialog(Context context, String message) {
        try {
            if (bottomSheetDialog != null) {
                bottomSheetDialog.dismiss();
            }
            bottomSheetDialog = new BottomSheetDialog(context);
            bottomSheetDialog.setContentView(R.layout.custom_bottom_dialog_layout);
            TextView textmessage = bottomSheetDialog.findViewById(R.id.textMessage);
            textmessage.setText(message);
            textmessage.setBackgroundColor(context.getResources().getColor(R.color.red));
            bottomSheetDialog.show();
            new CountDownTimer(2500, 500) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onFinish() {
                    // TODO Auto-generated method stub
                    bottomSheetDialog.dismiss();
                }
            }.start();
        }catch (Exception e){}
    }


    public static void showCommonBottomSheetSuccessDialog(Context context, String message) {
        try {
            if (bottomSheetDialog != null) {
                bottomSheetDialog.dismiss();
            }
            bottomSheetDialog = new BottomSheetDialog(context);
            bottomSheetDialog.setContentView(R.layout.custom_bottom_dialog_layout);
            TextView textmessage = bottomSheetDialog.findViewById(R.id.textMessage);
            textmessage.setText(message);
            textmessage.setBackgroundColor(context.getResources().getColor(R.color.green));
            bottomSheetDialog.show();
            new CountDownTimer(2000, 500) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onFinish() {
                    // TODO Auto-generated method stub
                    bottomSheetDialog.dismiss();
                }
            }.start();
        }catch (Exception e){}
    }
    public static String getSystemDateTimeInFormatt() {
        try {
            int year, monthformat, dateformat, sec;
            String da, mont, hor, min, yr, systemDate, secs;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            year = calendar.get(Calendar.YEAR);
            monthformat = calendar.get(Calendar.MONTH) + 1;
            dateformat = calendar.get(Calendar.DATE);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);
            sec = calendar.get(Calendar.SECOND);
            da = Integer.toString(dateformat);
            mont = Integer.toString(monthformat);
            hor = Integer.toString(hours);
            min = Integer.toString(minutes);
            secs = Integer.toString(sec);
            if (da.trim().length() == 1) {
                da = "0" + da;
            }
            if(mont.trim().equals("1")){
                mont = "01";
            }
            if(mont.trim().equals("2")){
                mont = "02";
            }
            if(mont.trim().equals("3")){
                mont = "03";
            }
            if(mont.trim().equals("4")){
                mont = "04";
            }
            if(mont.trim().equals("5")){
                mont = "05";
            }
            if(mont.trim().equals("6")){
                mont = "06";
            }
            if(mont.trim().equals("7")){
                mont = "07";
            }
            if(mont.trim().equals("8")){
                mont = "08";
            }
            if(mont.trim().equals("9")){
                mont = "09";
            }
            if(mont.trim().equals("10")){
                mont = "10";
            }
            if(mont.trim().equals("11")){
                mont = "11";
            }
            if(mont.trim().equals("12")){
                mont = "12";
            }
           /* if (mont.trim().equals("1")) {
                mont = "Jan";
            }
            if (mont.trim().equals("2")) {
                mont = "Feb";
            }
            if (mont.trim().equals("3")) {
                mont = "Mar";
            }
            if (mont.trim().equals("4")) {
                mont = "Apr";
            }
            if (mont.trim().equals("5")) {
                mont = "May";
            }
            if (mont.trim().equals("6")) {
                mont = "Jun";
            }
            if (mont.trim().equals("7")) {
                mont = "Jul";
            }
            if (mont.trim().equals("8")) {
                mont = "Aug";
            }
            if (mont.trim().equals("9")) {
                mont = "Sep";
            }
            if (mont.trim().equals("10")) {
                mont = "Oct";
            }
            if (mont.trim().equals("11")) {
                mont = "Nov";
            }
            if (mont.trim().equals("12")) {
                mont = "Dec";
            }*/
            if (hor.trim().length() == 1) {
                hor = "0" + hor;
            }
            if (min.trim().length() == 1) {
                min = "0" + min;
            }
            if (secs.trim().length() == 1) {
                secs = "0" + secs;
            }
            yr = Integer.toString(year);
            // systemDate = (da + mont + yr + hor + min + secs);
            systemDate = (yr + "-" + mont + "-" + da + " " + hor + ":" + min + ":" + secs);
            return systemDate;
        } catch (Exception e) {
            // return "01011970000000";
            // return "1970-01-01 00:00:00";
            return "1970-01-01 00:00:00";
        }
    }

    public static String getUTCSystemDateTimeInFormatt() {
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            f.setTimeZone(TimeZone.getTimeZone("UTC"));
            Log.e("UTCDATETIME1",f.format(new Date()));
           // f.setTimeZone(TimeZone.getTimeZone("GMT"));
            System.out.println(f.format(new Date()));
            Log.e("UTCDATETIME2",f.format(new Date()));
           String utcdatetime = f.format(new Date());
           return utcdatetime;
        } catch (Exception e) {
            // return "01011970000000";
            // return "1970-01-01 00:00:00";
            return "1970-01-01 00:00:00";
        }
    }
    public static String getCurrentSystemDate() {
        try {
            int year, monthformat, dateformat, sec;
            String da, mont, hor, min, yr, systemDate, secs;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            year = calendar.get(Calendar.YEAR);
            monthformat = calendar.get(Calendar.MONTH) + 1;
            dateformat = calendar.get(Calendar.DATE);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);
            sec = calendar.get(Calendar.SECOND);
            da = Integer.toString(dateformat);
            mont = Integer.toString(monthformat);
            hor = Integer.toString(hours);
            min = Integer.toString(minutes);
            secs = Integer.toString(sec);
            if (da.trim().length() == 1) {
                da = "0" + da;
            }
            if (mont.trim().equals("1")) {
                mont = "01";
            }
            if (mont.trim().equals("2")) {
                mont = "02";
            }
            if (mont.trim().equals("3")) {
                mont = "03";
            }
            if (mont.trim().equals("4")) {
                mont = "04";
            }
            if (mont.trim().equals("5")) {
                mont = "05";
            }
            if (mont.trim().equals("6")) {
                mont = "06";
            }
            if (mont.trim().equals("7")) {
                mont = "07";
            }
            if (mont.trim().equals("8")) {
                mont = "08";
            }
            if (mont.trim().equals("9")) {
                mont = "09";
            }
            if (mont.trim().equals("10")) {
                mont = "10";
            }
            if (mont.trim().equals("11")) {
                mont = "11";
            }
            if (mont.trim().equals("12")) {
                mont = "12";
            }
            if (hor.trim().length() == 1) {
                hor = "0" + hor;
            }
            if (min.trim().length() == 1) {
                min = "0" + min;
            }
            if (secs.trim().length() == 1) {
                secs = "0" + secs;
            }
            yr = Integer.toString(year);
            systemDate = (da + "-" + mont + "-" + yr);
            return systemDate;
        } catch (Exception e) {
            return "01-01-1970";
        }
    }
    public static String getInventoryType(int type){
        String inventirytype = "INV";
        if(type==0){
            inventirytype =  "INV";
        }
        if(type==1){
            inventirytype =  "IN";
        }
        if(type==2){
            inventirytype =  "OUT";
        }

        return inventirytype;
    }

    public static String get2DigitAssetTypeId(String id){
        String assetid = id;
        if(id != null){
            if(id.length()==1){
                assetid = "0"+id;
            }
        } else{
            assetid = "00";
        }
        return assetid;
    }

    public static String get8DigitAssetSerialNumber(String number){
        String serial = number;
        if(serial.length()==1){
            serial = "0000000"+number;
        }
        if(serial.length()==2){
            serial = "000000"+number;
        }
        if(serial.length()==3){
            serial = "00000"+number;
        }
        if(serial.length()==4){
            serial = "0000"+number;
        }
        if(serial.length()==5){
            serial = "000"+number;
        }
        if(serial.length()==6){
            serial = "00"+number;
        }
        if(serial.length()==7){
            serial = "0"+number;
        }
        return serial;
    }

    public static String numberToHex(String number){
        int decimal = Integer.parseInt(number);
        int rem;
        String hex="";
        char hexchars[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        while(decimal>0)
        {
            rem=decimal%16;
            hex=hexchars[rem]+hex;
            decimal=decimal/16;
        }
        if(hex.length()==1){
            hex = "0"+hex;
        }
        return hex;
    }

    public static String hexToNumber(String hex){
        //int num = Integer.parseInt(hex,16);
        //return String.valueOf(num);
        String digits = "0123456789ABCDEF";
        hex = hex.toUpperCase();
        long val = 0;
        for (int i = 0; i < hex.length(); i++)
        {
            char c = hex.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }
        return String.valueOf(val);
    }

    public static boolean isHexNumber(String str) {
        boolean flag = false;
        for (int i = 0; i < str.length(); i++) {
            char cc = str.charAt(i);
            if (cc == '0' || cc == '1' || cc == '2' || cc == '3' || cc == '4'
                    || cc == '5' || cc == '6' || cc == '7' || cc == '8'
                    || cc == '9' || cc == 'A' || cc == 'B' || cc == 'C'
                    || cc == 'D' || cc == 'E' || cc == 'F' || cc == 'a'
                    || cc == 'b' || cc == 'c' || cc == 'c' || cc == 'd'
                    || cc == 'e' || cc == 'f') {
                flag = true;
            }
        }
        return flag;
    }
    static Dialog errordialog, successdialog;

    public static void showCustomErrorDialog(Context context, String msg) {
        if (errordialog != null) {
            errordialog.dismiss();
        }
        if (successdialog != null) {
            successdialog.dismiss();
        }
        errordialog = new Dialog(context);
        errordialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        errordialog.setCancelable(false);
        errordialog.setContentView(R.layout.custom_alert_dialog_layout);
        TextView text = (TextView) errordialog.findViewById(R.id.text_dialog);
        text.setText(msg);
        Button dialogButton = (Button) errordialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errordialog.dismiss();
            }
        });
       // errordialog.getWindow().getAttributes().windowAnimations = R.style.FadeInOutAnimation;
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (!activity.isFinishing()) {
                errordialog.show();
            }
        }
    }

    static Dialog powersettingDialog;
    public static void openPowerSettingDialog(Context context, SeuicGlobalRfidHandler rfidHandler) {
        if (powersettingDialog != null) {
            powersettingDialog.dismiss();
        }

        powersettingDialog = new Dialog(context);
        powersettingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        powersettingDialog.setCancelable(false);
        powersettingDialog.setContentView(R.layout.custom_setting_dialog_layout);
        ImageView img = (ImageView) powersettingDialog.findViewById(R.id.img);
        TextView image_dialog = (TextView) powersettingDialog.findViewById(R.id.image_dialog);

        image_dialog.setText("Set Reader Power");

        TextView tip = (TextView) powersettingDialog.findViewById(R.id.tip);

        TextView textClose = (TextView) powersettingDialog.findViewById(R.id.textClose);


        textClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powersettingDialog.dismiss();
            }
        });

        if (SharedPreferencesManager.getPower(context)==30) {
            tip.setTextColor(context.getResources().getColor(R.color.green));
            tip.setText("Current  RF Power : HIGH");
            img.setImageDrawable(context.getResources().getDrawable(R.drawable.success));
        } else if (SharedPreferencesManager.getPower(context)==20){
            tip.setTextColor(context.getResources().getColor(R.color.boh));
            tip.setText("Current  RF Power : MEDIUM");
            img.setImageDrawable(context.getResources().getDrawable(R.drawable.success));
        }
        else if (SharedPreferencesManager.getPower(context)==10){
            tip.setTextColor(context.getResources().getColor(R.color.red));
            tip.setText("Current  RF Power : LOW");
            img.setImageDrawable(context.getResources().getDrawable(R.drawable.success));
        }else{
            tip.setTextColor(context.getResources().getColor(R.color.green));
            tip.setText("Current  RF Power : HIGH");
            img.setImageDrawable(context.getResources().getDrawable(R.drawable.success));
        }

        Button btnHigh = (Button) powersettingDialog.findViewById(R.id.btnHigh);
        Button btnMedium = (Button) powersettingDialog.findViewById(R.id.btnMedium);
        Button btnLow = (Button) powersettingDialog.findViewById(R.id.btnLow);
        btnLow.setVisibility(View.VISIBLE);

        btnHigh.setText("HIGH");
        btnMedium.setText("MEDIUM");
        btnLow.setText("LOW");
        btnHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powersettingDialog.dismiss();
                SharedPreferencesManager.setPower(context, 30);
                setAntennaPower(context,String.valueOf(30),rfidHandler);
            }
        });
        btnMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powersettingDialog.dismiss();
                SharedPreferencesManager.setPower(context, 20);
                setAntennaPower(context,String.valueOf(20),rfidHandler);
            }
        });
        btnLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                powersettingDialog.dismiss();
                SharedPreferencesManager.setPower(context, 10);
                setAntennaPower(context,String.valueOf(10),rfidHandler);
            }
        });
        // successdialog.getWindow().getAttributes().windowAnimations = R.style.FadeInOutAnimation;
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (!activity.isFinishing()) {
                powersettingDialog.show();
                // startCountDownTimer();
            }
        }
    }

    private static void setAntennaPower(Context context, String power, SeuicGlobalRfidHandler rfidHandler) {
        if (TextUtils.isEmpty(power)) {
            Toast.makeText(context, "Power cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        int p = Integer.parseInt(power);
        int m = 30;
        if ((p < 0) || (p > m)) {
            Toast.makeText(context, context.getResources().getString(R.string.power_range), Toast.LENGTH_SHORT).show();
        } else {
            try {
                // mDevice.setParameters(UHFService.PARAMETER_CLEAR_EPCLIST, 1);
                boolean rv =  rfidHandler.mDevice.setPower(p);
                if (!rv) {
                    Toast.makeText(context, context.getResources().getString(R.string.set_power_fail), Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(context, context.getResources().getString(R.string.set_power_ok), Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){

            }

        }
    }

    private static final int KEY_LENGTH = 256;
    private static final int ITERATION_COUNT = 1000;


    private static final String PBKDF2_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int PKCS5_SALT_LENGTH = 32;
    private static final String DELIMITER = "]";
    private static final SecureRandom random = new SecureRandom();

    public static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCyrO7FXRoYOddgWzJopsNNW8PoywyYAlKaE80EWCWGYl/vN5Z84eftQmvsxvZsMBJu0KD4XMrbhv15EzWTgrlGGh/mbL9t5ztT2RZw0KDUWXcKbbSbosX3YknhJkAB+rARQWROjhFaIB8VPD7KipdbK1jnO9Rjz92taQl266cgqQIDAQAB";
    public static final String GET_PRODUCT_MASTER = "getProductMaster";
    //  public static final String GET_PRODUCT_MASTER_ENCR = "3EdrGr1/6I/nBchjvbktnCQz8A99Z0swmJvynLTBHFI=]BI1vqq2RWR5gMxTZenGdwQ==]JRuZfgQHpi2KstchW/kTxZPptwleWXjlRn3GwhtEuGk=";
    //  public static final String GET_PRODUCT_MASTER = decrypt(GET_PRODUCT_MASTER_ENCR,publicKey);


    private  String hash256(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data.getBytes());
        return bytesToHex(md.digest());
    }

    private String bytesToHex(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        for (byte byt : bytes) {
            result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }


    public static String encrypt(String plaintext, String password) {
        byte[] salt  = generateSalt();
        SecretKey key = deriveKey(password, salt);

        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            byte[] iv = generateIv(cipher.getBlockSize());
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
            byte[] cipherText = cipher.doFinal(plaintext.getBytes("UTF-8"));

            if(salt != null) {
                return String.format("%s%s%s%s%s",
                        toBase64(salt),
                        DELIMITER,
                        toBase64(iv),
                        DELIMITER,
                        toBase64(cipherText));
            }

            return String.format("%s%s%s",
                    toBase64(iv),
                    DELIMITER,
                    toBase64(cipherText));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String ciphertext, String password) {
        String[] fields = ciphertext.split(DELIMITER);
        if(fields.length != 3) {
            throw new IllegalArgumentException("Invalid encypted text format");
        }
        byte[] salt        = fromBase64(fields[0]);
        byte[] iv          = fromBase64(fields[1]);
        byte[] cipherBytes = fromBase64(fields[2]);
        SecretKey key = deriveKey(password, salt);

        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec ivParams = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivParams);
            byte[] plaintext = cipher.doFinal(cipherBytes);
            return new String(plaintext, "UTF-8");
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] generateSalt() {
        byte[] b = new byte[PKCS5_SALT_LENGTH];
        random.nextBytes(b);
        return b;
    }

    private static byte[] generateIv(int length) {
        byte[] b = new byte[length];
        random.nextBytes(b);
        return b;
    }

    private static SecretKey deriveKey(String password, byte[] salt) {
        try {
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBKDF2_DERIVATION_ALGORITHM);
            byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
            return new SecretKeySpec(keyBytes, "AES");
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static String toBase64(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    private static byte[] fromBase64(String base64) {
        return Base64.decode(base64, Base64.NO_WRAP);
    }

    public static String getSystemDateTimeForExport() {
        try {
            int year, monthformat, dateformat, sec;
            String da, mont, hor, min, yr, systemDate, secs;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            year = calendar.get(Calendar.YEAR);
            monthformat = calendar.get(Calendar.MONTH) + 1;
            dateformat = calendar.get(Calendar.DATE);
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);
            sec = calendar.get(Calendar.SECOND);
            da = Integer.toString(dateformat);
            mont = Integer.toString(monthformat);
            hor = Integer.toString(hours);
            min = Integer.toString(minutes);
            secs = Integer.toString(sec);
            if (da.trim().length() == 1) {
                da = "0" + da;
            }
            if (mont.trim().equals("1")) {
                mont = "Jan";
            }
            if (mont.trim().equals("2")) {
                mont = "Feb";
            }
            if (mont.trim().equals("3")) {
                mont = "Mar";
            }
            if (mont.trim().equals("4")) {
                mont = "Apr";
            }
            if (mont.trim().equals("5")) {
                mont = "May";
            }
            if (mont.trim().equals("6")) {
                mont = "Jun";
            }
            if (mont.trim().equals("7")) {
                mont = "Jul";
            }
            if (mont.trim().equals("8")) {
                mont = "Aug";
            }
            if (mont.trim().equals("9")) {
                mont = "Sep";
            }
            if (mont.trim().equals("10")) {
                mont = "Oct";
            }
            if (mont.trim().equals("11")) {
                mont = "Nov";
            }
            if (mont.trim().equals("12")) {
                mont = "Dec";
            }
            if (hor.trim().length() == 1) {
                hor = "0" + hor;
            }
            if (min.trim().length() == 1) {
                min = "0" + min;
            }
            if (secs.trim().length() == 1) {
                secs = "0" + secs;
            }
            yr = Integer.toString(year);
            systemDate = (da + mont + yr +"T"+ hor + min + secs);
            return systemDate;
        } catch (Exception e) {
            return "01Jan1970T000000";
        }
    }

    public static void showCustomSuccessDialog(Context context, String msg) {

        if (successdialog != null) {
            successdialog.dismiss();
        }
        successdialog = new Dialog(context);
        successdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        successdialog.setCancelable(false);
        successdialog.setContentView(R.layout.custom_alert_success_dialog_layout);
        TextView text = (TextView) successdialog.findViewById(R.id.text_dialog);
        text.setText(msg);
        Button dialogButton = (Button) successdialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                successdialog.dismiss();
            }
        });
        // successdialog.getWindow().getAttributes().windowAnimations = R.style.FadeInOutAnimation;
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (!activity.isFinishing()) {
                successdialog.show();
            }
        }

    }
    public static String convertStringToHex (String serialNo){
        String hexaNumber = "";
        try{
            int decimal = Integer.parseInt(serialNo);
            String hexString = Integer.toHexString(decimal).toUpperCase(); // Convert to hex and change to uppercase
            while (hexString.length() < 4) {
                hexString = "0" + hexString;

            }
            hexaNumber = hexString;
        } catch (NumberFormatException e){

        }
       return  hexaNumber;
    }

}
