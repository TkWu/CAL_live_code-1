/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chinaairlines.mobile30;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ci.function.Core.SLog;
import ci.ui.object.CIPNRStatusManager;
import ci.ui.object.item.CIHomeStatusEntity;

@Deprecated
public class RegistrationIntentService extends IntentService {
    private static final String FILTER_TAG = "GCM";
    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
//            InstanceID instanceID = InstanceID.getInstance(this);
//            String token = instanceID.getToken(getString(R.string.gcm_SenderId),
//                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]

            // TODO: Implement this method to send any registration to your app's servers.
            //CIApplication.getMrqManager().sendRegistrationToServer(token);

            //收到Token, 重送MRQ  //因應首頁發出要求去重刷首頁，避免設定Listener為null時有機會讓首頁無法刷新 by kevin 2016/12
            CIHomeStatusEntity homeStatusEntity = CIPNRStatusManager.getInstanceWithoutSetListener().getHomeStatusFromMemory();
//            if ( null == homeStatusEntity ){
//                List<MrqEvent> events = new ArrayList<>();
//                CIApplication.getMrqManager().SendDataToMRQServer(CIMrqInfoManager.TAG_LAUNCHED, events);
//            } else {
//                CIApplication.getMrqManager().SendPNRINfotoMRQ(CIMrqInfoManager.TAG_LAUNCHED, homeStatusEntity.AllItineraryInfo);
//            }
            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            ///sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
           SLog.d(TAG, FILTER_TAG + " Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            //sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
//        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
}