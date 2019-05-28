package ci.function.BaggageTrack;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ci.function.Core.CIApplication;
import ci.function.Core.EncryptValueManager;
import ci.function.Core.Encryption;
import ci.ui.define.UiMessageDef;
import ci.ui.object.AppInfo;
import ci.ws.Models.entities.CIBaggageInfoNumEntity;
import ci.ws.Models.entities.CIBaggageInfoReq;
import ci.ws.Models.entities.CIBaggageInfoResp;
import ci.ws.Models.entities.CIBaggageInfoResp_Pax;
import ci.ws.Models.entities.CITripListResp;
import ci.ws.Models.entities.CITripListResp_Itinerary;

public class CIBaggageInfoManager {

    private final String TAG = "BaggageINfo";

    private Context m_context = null;

    private SharedPreferences m_spBaggageInfo = null;

    //SharedPreferences 檔名定義
    /**
     * 行李列表資訊
     */
    private static final String KEY_BAGGAGE_LIST   = "KEY_BAGGAGE";

    //SharedPreferences 欄位預設值

    public CIBaggageInfoManager(Context context) {

        this.m_context = context;
        m_spBaggageInfo = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }

    /**依照會員帳號來儲存資料，未登入者只有一份*/
    private String getBaggageDataKey(){

        final String CardNo     = CIApplication.getLoginInfo().GetUserMemberCardNo();
        final String LoginType  = CIApplication.getLoginInfo().GetLoginType();
        final boolean isLogin   = CIApplication.getLoginInfo().GetLoginStatus();
        Encryption encryption = Encryption.getInstance();
        if( isLogin && !TextUtils.isEmpty(CardNo)
                && UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER.equals(LoginType)){
            return encryption.MD5(KEY_BAGGAGE_LIST + "_" + CardNo);
        } else {
            return encryption.MD5(KEY_BAGGAGE_LIST);
        }

    }

    /**
     * 儲存行李列表資料
     *
     * @param arDataList
     */
    public void setBaggageList( ArrayList<CIBaggageInfoResp> arDataList ) {

        if (null == m_spBaggageInfo ) {
            return;
        }

        String json = new Gson().toJson(arDataList);

        EncryptValueManager.setValue(m_spBaggageInfo, getBaggageDataKey(), json);
    }

    /**
     * @return 儲存的行李列表資料
     */
    public ArrayList<CIBaggageInfoResp> getBaggageList() {

        ArrayList<CIBaggageInfoResp> arBaggageInfoList = new ArrayList<>();
        if (null == m_spBaggageInfo ) {
            return arBaggageInfoList;
        }

        String strBaggageList = EncryptValueManager.getString(m_spBaggageInfo, getBaggageDataKey(), "");
        if ( !TextUtils.isEmpty(strBaggageList) ){
            try {

                Type listType = new TypeToken<List<CIBaggageInfoResp>>(){}.getType();
                Gson gson = new Gson();
                arBaggageInfoList = gson.fromJson( strBaggageList , listType);

            } catch (Exception e ){
                e.printStackTrace();
            }
        }
        if (null == arBaggageInfoList ) {
            return new ArrayList<CIBaggageInfoResp>();
        }

        return arBaggageInfoList;
    }

    /**檢核行李條碼格式*/
    public boolean checkBaggageTagFormat( String strBarcodeResult ){

        if ( TextUtils.isEmpty(strBarcodeResult) ){
            return false;
        }

        int iLength = strBarcodeResult.length();
        //10碼數字
        if ( 10 == iLength ){
            return strBarcodeResult.matches("[0-9]+");
        } else if ( 8 == iLength ){
            //兩碼英文、後六碼數字
            return strBarcodeResult.matches("[a-zA-Z]{2}[0-9]{6}");

        } else {
            return false;
        }
    }

    /**檢查行李資訊是否過期*/
    public ArrayList<CIBaggageInfoReq> checkBaggageInfo( ArrayList<CIBaggageInfoResp> arBaggageList ){

        ArrayList<CIBaggageInfoReq> arNewBagTagList = new ArrayList<>();

        for ( CIBaggageInfoResp resp : arBaggageList ){
            if ( checkData(resp.Departure_Date, resp.Arrival_Date) ){
                for ( CIBaggageInfoResp_Pax pax : resp.Pax ){
                    for ( CIBaggageInfoNumEntity info : pax.Baggage_Number ){
                        CIBaggageInfoReq req = new CIBaggageInfoReq();
                        req.First_Name = pax.First_Name;
                        req.Last_Name = pax.Last_Name;
                        req.Baggage_Number  = info.Baggage_BarcodeNumber;
                        arNewBagTagList.add(req);
                    }
                }
            }
        }

        return arNewBagTagList;
    }

    /**檢查PNR的日期是否過期，過期的無法查詢行李*/
    public ArrayList<String> checkPNRforBaggage( ArrayList<CITripListResp> arPnrList ){

        ArrayList<String> arPNRList = new ArrayList<>();

        for ( CITripListResp TripInfo : arPnrList ){
            boolean bValid = false;
            for ( CITripListResp_Itinerary itinerary : TripInfo.Itinerary_Info ){
                if ( checkData(itinerary.Departure_Date, itinerary.Arrival_Date) ){
                    bValid = true;
                    break;
                }
            }
            if ( bValid ){
                arPNRList.add(TripInfo.PNR_Id);
            }
        }

        return arPNRList;
    }


    /**判斷今天日期是否在可查詢的時間區間*/
    private boolean checkData( String strDeparture, String strArrival ){

        if ( TextUtils.isEmpty(strDeparture) || TextUtils.isEmpty(strArrival) ){
            return false;
        }

        final String pattern = "yyyy-MM-dd";
        Date dDeparture = AppInfo.getDate(strDeparture, pattern);
        Date dArrival   = AppInfo.getDate(strArrival, pattern);

        Calendar calDeparture = Calendar.getInstance();
        calDeparture.setTime(dDeparture);
        calDeparture.add(Calendar.DAY_OF_MONTH, -1);

        Calendar calArrival = Calendar.getInstance();
        calArrival.setTime(dArrival);
        calArrival.add(Calendar.DAY_OF_MONTH, +3);

        Calendar calToday = Calendar.getInstance();
        Date dToday = calToday.getTime();
        Date dStart = calDeparture.getTime();
        Date dEnd   = calArrival.getTime();

        if ( dStart.getTime() <= dToday.getTime() &&
                dToday.getTime() <= dEnd.getTime()  ){
            return true;
        }

        return false;
    }

}
