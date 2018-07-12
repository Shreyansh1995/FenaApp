package com.androidstuff.fenaapp.ClassUtills;

/**
 * Created by Flash_Netcomm on 6/18/2018.
 */

public class ConfigInfo {

    public static final String BaseUrl="http://182.18.171.238:8093/Fena.svc/";


   // public static final String ConsumerDataForm="http://192.168.0.20/fena_app/api/save_retailer";


    public static final String SignIn=BaseUrl+"CheckRSCode";
    public static final String ConFENARegister=BaseUrl+"FENARegister ";
    public static final String ConsumerDataForm=BaseUrl+"FENAConsumerAdd";
        public static final String GetList=BaseUrl+"FENAGetFormDetails";
        public static final String DeleteList=BaseUrl+"FENADeleteConsumerRetailerData";
        public static final String RetailerAdd=BaseUrl+"FENARetailerAdd";
        public static final String RetGetEditData=BaseUrl+"FENAConsumerGetSingle";
        public static final String updatedate=BaseUrl+"FENAUpdateDetails";
        public static final String count=BaseUrl+"FENACountInquery";
        public static final String alert=BaseUrl+"FENALogOutAlertMSG";
}
