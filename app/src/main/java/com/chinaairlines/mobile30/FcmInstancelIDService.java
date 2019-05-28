package com.chinaairlines.mobile30;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import ci.function.Core.CIApplication;
import ci.function.Core.SLog;
import ci.ui.object.CIFCMManager;
import ci.ui.object.CIPNRStatusManager;
import ci.ui.object.item.CIHomeStatusEntity;


public class FcmInstancelIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();

        SLog.d( "FcmInstancelIDService","[FCM] onTokenRefresh = " + token);

        //收到Token, 重送資料回Server //因應首頁發出要求去重刷首頁，避免設定Listener為null時有機會讓首頁無法刷新
        CIHomeStatusEntity homeStatusEntity = CIPNRStatusManager.getInstanceWithoutSetListener().getHomeStatusFromMemory();
        if ( null == homeStatusEntity ) {
            CIApplication.getFCMManager().UpdateDeviceToCIServer(null);
        } else {
            CIApplication.getFCMManager().UpdateDeviceToCIServer(homeStatusEntity.AllItineraryInfo);
        }
    }
}