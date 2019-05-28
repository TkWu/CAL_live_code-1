package com.chinaairlines.mobile30;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by kevincheng on 2016/5/24.
 */
public class Config {

    public static final String  WS_RESULT_CODE_SUCCESS  = "000";
    public static final String  WS_RESULT_CODE_ERROR    = "-999";
    public static final String  WS_RESULT_ERROR         = "error";
    public static final String  CARD_NO             = "WD9751050";
    public static final String  PASSWORD            = "123456";
    public static final String  STATION             = "TPE";
    public static final String  LATITUDE            = "25.08028";
    public static final String  LONGITUDE           = "121.23222";
    public static final String  DEPARTURE_STATION   = "TPE";
    public static final String  ARRVAL_STATION      = "NRT";
    public static final String  PNR                 = "2QU4RH";
    public static final String  PNR_SEQ             = "20";
    public static final String  TICKET_NO           = "6779034982829";
    public static final String  FIRST_NAME          = "YENCHEN";
    public static final String  LAST_NAME           = "WANG";
    public static final String[] PNR_LIST           = {"2QU4RH"};

    public static Set<String> getPnrList(){

        Set<String> list = new LinkedHashSet<>();

        for (int i = 0; i < Config.PNR_LIST.length; i ++){
            list.add(Config.PNR_LIST[i]);
        }

        return list;
    }
}
