package com.psl.inventorydemo.helper;

import java.util.Calendar;
import java.util.Date;

public class APIConstants {
    //http://psltestapi.azurewebsites.net/
    //public static final String M_URL = "https://psladpservice.azurewebsites.net";
    public static final String M_URL = "http://192.168.0.113/ADP";



    public static final String M_TRUCK_DETAILS = "/PDA/GetTruckDetailsForPDA";
    public static final String M_DC_DETAILS = "/PDA/GetDCDetailsForPDA";
    public static final String M_GET_ITEM_DETAILS = "/PDA/GetSTOLineItemsByTruckID";
    public static final String M_UPLOAD_ITEM_DETAILS = "/PDA/InsertTransactionForItems";
    public static final String M_GET_PARTIAL_WORK_ORDERS = "/PDA/GetPartialWorkorderList";
    public static final String M_GET_PARTIAL_WORK_ORDERS_DETAILS = "/PDA/GetPartialWorkorderItemDetails";
    public static final String M_UPLOAD_PARTIAL_WORK_ORDERS_DETAILS = "/PDA/InsertPartialWorkorderDetails";
    public static final String M_GET_BIN_DETAILS = "/PDA/GetBinInfo";
    public static final String M_UPLOAD_ITEM_MOVEMENT = "/PDA/ItemMovementTransaction";
    public static final String M_UPLOAD_ITEM_QR_MANUAL = "/PDA/InsertTransactionForQRManual";

    public static final String M_USER_LOGIN = "/WMS/ADPMobileLogin";
    public static final String M_GET_ASSET_MASTER = "/WMS/ADPGetAllAssetsPDA?clientDeviceID=";
    public static final String M_GET_SERIAL_NO = "/WMS/ADPGetSerialNo?assetTypeID=";
    public static final String M_ASSET_REGISTRATION = "/WMS/ADPRegisterAssetMobile";
    public static final String M_UPLOAD_TRANSACTION = "/WMS/ADPTransactionMobile";


 public static final String M_GET_WORK_ORDER_DETAILS = "/PDA/GetReaderWorkorderList";
 public static final String M_GET_WORK_ORDER_DETAILS_FOR_PDA = "/PDA/GetWorkorderListForPDA";
 public static final String M_POST_CURRENT_PALLET = "/PDA/UploadCurrentPallet";
 public static final String M_GET_SKU_DETAILS = "/PDA/GetSKUDetailsForPDA";
 public static final String M_GET_ALL_SKU_DATA = "/PDA/GetAllSKUsPDA";
 public static final String M_GET_SO_LINE_ITEMS = "/PDA/GetSOLineItemsLoading";




   // public static final String M_UPLOAD_ASSET_PALLET_MAPPING = "/PDA/RegisterAssetPallet";
   // public static final String M_UPLOAD_CONTAINER_PALLET_MAPPING = "/PDA/RegisterContainerPallet";

    public static final int API_TIMEOUT = 60;

    public static final String K_STATUS = "status";
    public static final String K_MESSAGE = "message";
    public static final String K_DATA = "data";

    public static final String K_USER = "UserName";
    public static final String K_PASSWORD = "Password";
    public static final String K_DEVICE_ID = "ClientDeviceID";


    public static final String K_ACTIVITY_TYPE = "ActivityType";
    public static final String K_BIN_TAG_ID = "BinTagID";
    public static final String K_BIN_NAME = "BinName";
    public static final String K_ACTIVITY_ASSET_TYPE = "ActivityAssetType";

    public static final String K_ASSET_TYPE_ID = "ATypeID";
    public static final String K_ASSET_TYPE = "AType";
    public static final String K_ASSET_ID = "AssetID";
    public static final String K_ASSET_NAME = "AName";
    public static final String K_ASSET_SERIAL_NUMBER = "ASerialNo";
    public static final String K_ASSET_TAG_ID = "ATagId";

    public static final String K_INVENTORY_COUNT = "Count";
    public static final String K_INVENTORY_START_DATE_TIME = "StartDate";
    public static final String K_INVENTORY_END_DATE_TIME = "EndDate";
    public static final String K_TRANSACTION_DATE_TIME = "TransactionDateTime";


 public static String getSystemDateTimeForBatchId() {
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
   systemDate = (yr + "-" + mont + "-" + da + "-" + hor + ":" + min + ":" + secs);
   return systemDate;
  } catch (Exception e) {
   // return "01011970000000";
   // return "1970-01-01 00:00:00";
   return "1970-01-01-00:00:00";
  }
 }

}
