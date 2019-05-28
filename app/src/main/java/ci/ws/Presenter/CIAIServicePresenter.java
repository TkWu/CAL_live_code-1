package ci.ws.Presenter;


import android.os.Handler;
import android.os.Looper;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import ci.function.Core.CIApplication;
import ci.function.Core.Encryption;
import ci.ui.object.CILoginInfo;
import ci.ws.Models.CIAIServiceModel;
import ci.ws.Models.CIBookTicketModel;
import ci.ws.Models.CIInquiryPromoteCodeTokenModel;
import ci.ws.Models.entities.CIInquiryPromoteCodeTokenResp;
import ci.ws.cores.object.CIResponse;

/**
 * Created by ryan on 2018/12/10.
 */
public class CIAIServicePresenter {

    private CallBack                       m_view                         = null;
    private Handler                        m_hdUIThreadhandler            = null;

    public CIAIServicePresenter(CallBack view) {
        m_view = view;
        startUIThreadHandler();
    }

    public void fetchAIServiceWebData() {
        m_view.showProgress();
        CIAIServiceModel.findData(callBack, getPostData());
    }

    private String getPostData(){
        /**
         * 文件：參照 子科的 mail 2018/12/04 12:07
         * 版本：2018/12/10
         * postData = "lang=zh-TW&ID=CT0000000&ffLname=WANG&ffFname=SHOUMING&vtoken=%7c%c2%83%5e%c2%a6%c2%afq%c2%a8%c2%aac&deviceID=914033CB5DBF42A18864A0103A2DE8C6";
         * */

        Map<String, String> mapParams = new LinkedHashMap<>();

        Locale locale = CIApplication.getLanguageInfo().getLanguage_Locale();
        //mapParams.put("lang", locale.toString());
        switch (locale.toString()){
            case "zh_TW":
                mapParams.put("lang","zh-TW");
                break;
            case "zh_CN":
                mapParams.put("lang","zh-CN");
                break;
        }

        String strId = "", strFName = "", strLName = "", strVToken = "";
        if (  CIApplication.getLoginInfo().isDynastyFlyerMember() ){
            strId = CIApplication.getLoginInfo().GetUserMemberCardNo();
            strFName = CIApplication.getLoginInfo().GetUserFirstName();
            strLName = CIApplication.getLoginInfo().GetUserLastName();
            strVToken = CIApplication.getLoginInfo().GetMemberToken();
        }
        mapParams.put("ffID" ,      strId);
        mapParams.put("ffLname" ,   strLName);
        mapParams.put("ffFname" ,   strFName);
        mapParams.put("vtoken" ,    strVToken);
        mapParams.put("deviceID" , CIApplication.getDeviceInfo().getAndroidId());

        String postData ="";
        Iterator<Map.Entry<String, String>> iter = mapParams.entrySet().iterator();
        int count = 0;
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            String key = entry.getKey();
            String val = entry.getValue();
            if(0 == count){
                postData+= key+"="+val;
            } else {
                postData+= "&"+key+"="+val;
            }
            count++;
        }
        return postData;
    }

    private void startUIThreadHandler() {
        m_hdUIThreadhandler = new Handler(Looper.getMainLooper());
    }

    CIAIServiceModel.CallBack callBack = new CIAIServiceModel.CallBack() {
        @Override
        public void onSuccess(final String webData) {
            m_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if(null != m_view){
                        m_view.hideProgress();
                        m_view.onDataBinded(webData);
                    }
                }
            });
        }

        @Override
        public void onError(CIResponse response, int code,final Exception exception) {
            m_hdUIThreadhandler.post(new Runnable() {
                @Override
                public void run() {
                    if(null != m_view) {
                        m_view.hideProgress();
                        m_view.onDataFetchFeild(exception.getMessage());
                    }
                }
            });
        }
    };

    //request and response interrupt on pause
    public void interrupt(){
        m_view.hideProgress();
        CIAIServiceModel.cancel();
    }

    public interface CallBack{
        void showProgress();
        void hideProgress();
        void onDataBinded(String webData);
        void onDataFetchFeild(String msg);
    }
}
