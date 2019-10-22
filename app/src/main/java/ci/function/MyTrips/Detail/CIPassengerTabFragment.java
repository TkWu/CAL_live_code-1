package ci.function.MyTrips.Detail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import ci.function.BaggageTrack.CIBaggageInfoContentActivity;
import ci.function.Base.BaseFragment;
import ci.function.BoardingPassEWallet.Adapter.CIExtraServiceRecyclerViewAdapter;
import ci.function.BoardingPassEWallet.BoardingPass.CIBoardingPassCardActivity;
import ci.function.BoardingPassEWallet.ExtraServices.CIExtraServicesCardActivity;
import ci.function.BoardingPassEWallet.item.CIExtraServiceItem;
import ci.function.Checkin.CICancelCheckInActivity;
import ci.function.Checkin.CICheckInActivity;
import ci.function.Core.CIApplication;
import ci.function.HomePage.CIMainCheckInFragment;
import ci.function.MealSelection.CISelectPassengerActivity;
import ci.function.MyTrips.Adapter.CIPassengerRecyclerViewAdapter;
import ci.function.MyTrips.CIAddPassengerFragment;
import ci.function.MyTrips.Detail.AddBaggage.CIAddExcessBaggageActivity;
import ci.function.SeatSelection.CISelectSeatMapActivity;
import ci.ui.WebView.CIWithoutInternetActivity;
import ci.ui.define.HomePage_Status;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.toast.CIToastView;
import ci.ui.view.ImageHandle;
import ci.ws.Models.entities.BaggageEntity;
import ci.ws.Models.entities.CIAddMemberCardReq;
import ci.ws.Models.entities.CIBaggageInfoContentResp;
import ci.ws.Models.entities.CIBaggageInfoNumEntity;
import ci.ws.Models.entities.CIBaggageInfoResp;
import ci.ws.Models.entities.CIBoardPassResp;
import ci.ws.Models.entities.CIBoardPassResp_Itinerary;
import ci.ws.Models.entities.CIBoardPassResp_PaxInfo;
import ci.ws.Models.entities.CIBoardPassResp_PnrInfo;
import ci.ws.Models.entities.CICheckFlightMealOpenReq;
import ci.ws.Models.entities.CICheckInPax_InfoEntity;
import ci.ws.Models.entities.CICheckInAllPaxResp;
import ci.ws.Models.entities.CIDeleteOrderMealReq;
import ci.ws.Models.entities.CIEWallet_ExtraService_Info;
import ci.ws.Models.entities.CIPassengerListResp;
import ci.ws.Models.entities.CIPassengerListResp_PaxInfo;
import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.Presenter.CIAddMemberCardPresenter;
import ci.ws.Presenter.CICheckFlightMealOpenPresenter;
import ci.ws.Presenter.CIHandleOrderMealPresenter;
import ci.ws.Presenter.CIInquiryBaggageInfoPresenter;
import ci.ws.Presenter.CIInquiryBoardPassPresenter;
import ci.ws.Presenter.CIInquiryCheckInPresenter;
import ci.ws.Presenter.CIManageTicketPresenter;
import ci.ws.Presenter.Listener.CIAddMemberListener;
import ci.ws.Presenter.Listener.CIBaggageInfoListener;
import ci.ws.Presenter.Listener.CICheckFlightMealOpenListener;
import ci.ws.Presenter.Listener.CIHandleOrderMealListener;
import ci.ws.Presenter.Listener.CIInquiryBoardPassListener;
import ci.ws.Presenter.Listener.CIInquiryCheckInListener;
import ci.ws.define.CIWSHomeStatus_Code;
import ci.ws.define.CIWSResultCode;

import static ci.function.BoardingPassEWallet.CIBoardingWithQRCodeActivity.BoardingPassType;
import static ci.function.MyTrips.Adapter.CIPassengerRecyclerViewAdapter.onPassengerRecyclerViewAdapterListener;

/** 乘客卡
 * Created by jlchen on 2016/3/2.
 *
 * 2016/07/14 目前乘客頁面邏輯如下:
 * 請搭配參考
 * 乘客資料: CIMyTripsDetialActivity.class(透過WS取得)
 * 乘客卡UI: CIPassengerRecyclerViewAdapter.class
 *
 * 1. 判斷是否可線上check-in: 透過InquiryFlightStation取得出發地機場tag:"is_vcheckin"是否為"Y"
 * 2. 取得此航段狀態: 透過InquiryPNRItineraryStatus取得狀態tag:"iStatus_Code"
 * 3. 取得乘客資料: 透過InquiryAllPassengerByPNR取得
 * 4. 若航段狀態為2或3時, 且可進行線上check-in, 需進一步取得CPR乘客資料: 透過InquiryCheckInAllPaxByPNR取得.
 *    之後需依照旅客姓名塞入對應的Uci與Did給CIPassengerListResp,
 *    假如此乘客可以進行報到,已經完成報到且不是黑名單時, 寫入乘客狀態為2(顯示登機證) ; 否則為3(顯示預辦登機).
 * 5. 如果不可線上check-in, 就不顯示預辦登機或登機證
 * 6. pnr_status = 3時, 乘客狀態也要為3才顯示預辦登機, 否則就隱藏;
 *    pnr_status = 2時, 乘客狀態為3顯示預辦登機, 乘客狀態為2顯示登機證, 否則就都隱藏.
 * 7. 座位欄: 必須顯示,不可隱藏.
 *    當 is_change_seat = Y & is_do = Y & 身份不為嬰兒 & 狀態為(3.4.5) ->右側才可以顯示選擇或編輯按鈕;
 *    如果乘客狀態為2之後, 右側顯示關閉字串; 當乘客身份為嬰兒時, 右側不顯示任何按鈕及字串; 其他情況右側均顯示未開放字串.
 * 8. 餐點欄: 依照狀態顯示或隱藏.
 *    當 is_change_meal = Y 時顯示; is_change_meal = N 隱藏.
 *    當 is_change_meal = Y & is_do = Y & 乘客tkt_confirm_code不為null & 狀態為(4) ->右側才可以顯示選擇或編輯;
 *    只要不能選餐, 右側均顯示未開放字串.
 * 9. 行李欄: 必須顯示,不可隱藏.
 *    當 is_add_bag= Y & bagg_all = null或空字串 & 狀態為(4.5.6) ->右側才可以顯示編輯按鈕;
 *    其他情況右側均不顯示任何按鈕及字串.
 */
public class CIPassengerTabFragment extends BaseFragment{

    public interface OnPassengerTabFragmentListener {
        void showProDlg();

        void hideProDlg();

        void ResetPassengerCount(int iCount);

        void ReLoadPassenagerInfo();
    }

    public interface OnPassengerTabFragmentParameter {
        //該筆pnr的狀態
        int getPNRStatusCode();

        //機場是否可以線上check in
        boolean getIsOnlineCheckInByStatuCode();

        //該機場是否可以列印線上電子登機證
        boolean getIsvBoardingPassByStatuCode();

        //行程資料
        CITripListResp_Itinerary getTripData();

        //CPR資料
        ArrayList<CICheckInPax_InfoEntity> getCPRData();
    }

    public interface OnPassengerTabFragmentInterface {
        void SetPassengerList(CIPassengerListResp passengerListResp);

        void setUserName( String strFirstName, String strLastName );
    }

    private OnPassengerTabFragmentListener  m_Listener;
    private OnPassengerTabFragmentParameter m_Parameter;

    private OnPassengerTabFragmentInterface m_Interface = new OnPassengerTabFragmentInterface() {
        @Override
        public void SetPassengerList(CIPassengerListResp passengerListResp) {
            if ( null != passengerListResp ){
                m_PassengerData     = passengerListResp;
                m_alPassengerItem   = (ArrayList)passengerListResp.Pax_Info;
            }else {
                m_alPassengerItem.clear();
            }

            if ( null != m_Parameter ){
                m_iPNRStatus        = m_Parameter.getPNRStatusCode();
                m_bIsOnlineCheckIn  = m_Parameter.getIsOnlineCheckInByStatuCode();
                m_CheckInResp = m_Parameter.getCPRData();

                m_bIsvPass          = m_Parameter.getIsvBoardingPassByStatuCode();
            }

            if ( null != m_rv ){
                m_adapter = new CIPassengerRecyclerViewAdapter(
                        //CIPassengerTabFragment.this,
                        getActivity(),
                        m_tripData,
                        m_alPassengerItem,
                        m_onPassengerRecyclerViewAdapterListener,
                        m_bIsOnlineCheckIn,
                        m_bShowAddView,
                        m_iPNRStatus,
                        m_bIsvPass,
                        m_strInquiryFirstName,
                        m_strInquiryLastName);

                //塞入CPR 資料
                m_adapter.setCPRPassenagerData(m_CheckInResp);

                m_rv.setAdapter(m_adapter);
            }

            if ( null != m_Listener ){
                m_Listener.ResetPassengerCount(m_alPassengerItem.size());
            }
        }

        @Override
        public void setUserName(String strFirstName, String strLastName) {
            m_strInquiryFirstName   = strFirstName;
            m_strInquiryLastName    = strLastName;
        }
    };

    private onPassengerRecyclerViewAdapterListener m_onPassengerRecyclerViewAdapterListener
            = new onPassengerRecyclerViewAdapterListener() {

        @Override
        public void JoinMemberOnClick() {

        }

        @Override
        public void CheckInOnClick(CIPassengerListResp_PaxInfo paxInfo) {

            m_PaxInfo = paxInfo;

            //舊站url
            m_strOldCheckInUrl = CIMainCheckInFragment.getWebOnlineCheckInUrl(m_tripData.Pnr_Id, paxInfo.First_Name, paxInfo.Last_Name);

            //須先確認機場為新/舊站
            m_InquiryCheckInPresenter.InquiryCheckInByPNRFromWS(m_tripData.Pnr_Id, paxInfo.First_Name, paxInfo.Last_Name);
        }

        @Override
        public void BoardingPassOnClick(CIPassengerListResp_PaxInfo paxInfo) {

            m_PaxInfo = paxInfo;

            Set<String> pnrList = new LinkedHashSet<>();
            pnrList.add(m_tripData.Pnr_Id);
            m_strFirstName  = paxInfo.First_Name;
            m_strLastName   = paxInfo.Last_Name;
            //2018-06-29 第二階段CR 新增FirstName LastName 避免PNRId重複使用導致看到別人的資料
            CIInquiryBoardPassPresenter.getInstance(m_BoardPassWSListener).
                    InquiryBoardPassFromWSByPNRListAndCardNo("", m_strFirstName, m_strLastName, pnrList);
        }

        @Override
        public void CancelCheckInOnClick(final CIPassengerListResp_PaxInfo paxInfo) {

            m_PaxInfo = paxInfo;

//            showDialog(
//                    getString(R.string.warning),
//                    getString(R.string.cancel_check_in_alert_msg),
//                    getString(R.string.confirm),
//                    getString(R.string.cancel),
//                    new CIAlertDialog.OnAlertMsgDialogListener() {
//                        @Override
//                        public void onAlertMsgDialog_Confirm() {
//                            //舊站url
//                            m_strOldCheckInUrl = CIMainCheckInFragment.getWebOnlineCheckInUrl(m_tripData.Pnr_Id, paxInfo.First_Name, paxInfo.Last_Name);
//
//                            //須先確認機場為新/舊站
//                            CIInquiryCheckInPresenter.getInstance(m_CheckInWSListener)
//                                    .InquiryCheckInByPNRFromWS(m_tripData.Pnr_Id, paxInfo.First_Name, paxInfo.Last_Name);

                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(
                                    UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST, m_tripData);
                            bundle.putSerializable(
                                    UiMessageDef.BUNDLE_PASSENGER_INFO, m_PassengerData);
                            bundle.putSerializable(
                                    UiMessageDef.BUNDLE_CANCEL_CHECK_IN_DATA, m_CheckInResp);
                            intent.putExtras(bundle);
                            ChangeActivity(
                                    intent,
                                    CICancelCheckInActivity.class,
                                    UiMessageDef.REQUEST_CODE_TRIP_DETAIL_PASSENGER_CANCEL_CHECK_IN);
//                        }
//
//                        @Override
//                        public void onAlertMsgDialogg_Cancel() {}
//                    });
        }

        @Override
        public void SelectSeatOnClick(CIPassengerListResp_PaxInfo paxInfo) {
            m_PaxInfo = paxInfo;
            //643924-2019
            //GoSelectSeatActivity();
            GoSelectSeatActivity_Web();
        }

        @Override
        public void SelectMealOnClick(CIPassengerListResp_PaxInfo paxInfo) {
            m_PaxInfo = paxInfo;
            GoSelectMealActivity();
        }

        @Override
        public void EditSeatOnClick(CIPassengerListResp_PaxInfo paxInfo) {
            m_PaxInfo = paxInfo;
            //643924-2019
            //GoSelectSeatActivity();
            GoSelectSeatActivity_Web();
        }

        @Override
        public void EditMealOnClick(CIPassengerListResp_PaxInfo paxInfo) {
            m_PaxInfo = paxInfo;
            GoSelectMealActivity();
        }

        @Override
        public void DeleteMealOnClick( CIPassengerListResp_PaxInfo paxInfo ){
            m_PaxInfo = paxInfo;
            showDialog(getString(R.string.warning),
                    getString(R.string.SelectMeal_delete_meal),
                    getString(R.string.confirm),
                    getString(R.string.cancel),
                    new CIAlertDialog.OnAlertMsgDialogListener(){

                        @Override
                        public void onAlertMsgDialog_Confirm() {
                            DelectMealInfo();
                        }

                        @Override
                        public void onAlertMsgDialogg_Cancel() {}
                    });
        }

        @Override
        public void EditExtraBagOnClick(int bagType, CIPassengerListResp_PaxInfo passengerItem) {
            Intent intent = new Intent();
            if(bagType == onPassengerRecyclerViewAdapterListener.ADD_BAGGAGE){
                //樂go行李
                //2016.05.30 按下編輯額外行李, show ExtraService by Bag 的webview

                intent.putExtra(
                        UiMessageDef.BUNDLE_WEBVIEW_TITLE_TEXT_TAG,
                        getString(R.string.extra_service_title_extra_baggage));
                intent.putExtra(
                        UiMessageDef.BUNDLE_WEBVIEW_URL_TAG,
                        getString(R.string.trip_detail_passenger_bag_url));

                ChangeActivity(intent, CIWithoutInternetActivity.class);
            } else if (bagType == onPassengerRecyclerViewAdapterListener.ADD_EXCESS_BAGGAGE){
                //加購行李
                Bundle bundle = new Bundle();
                bundle.putSerializable(UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST, m_tripData);
                bundle.putSerializable(UiMessageDef.BUNDLE_PASSENGER_INFO_SINGLE, passengerItem );
                intent.putExtras(bundle);
                ChangeActivity(intent, CIAddExcessBaggageActivity.class, UiMessageDef.REQUEST_CODE_ADD_EXCESS_BAG);
            }
        }

        @Override
        public void extraBagEwallet(int bagType, CIPassengerListResp_PaxInfo passengerItem) {
            displayEwallet(handleBaggageData(bagType , passengerItem));
        }

        @Override
        public void BaggageTrackingInfo(CIBaggageInfoNumEntity InfoEntity) {

            m_BaggageEntity = InfoEntity;
            CIInquiryBaggageInfoPresenter.getInstance(m_BaggageInfoListener).InquiryBaggageInfoByBGNumFromWS( InfoEntity.Baggage_BarcodeNumber, m_tripData.Departure_Station, m_tripData.Departure_Date);
        }


        CIBaggageInfoListener m_BaggageInfoListener = new CIBaggageInfoListener() {

            @Override
            public void onBaggageInfoByPNRAndBGNumSuccess(String rt_code, String rt_msg, ArrayList<CIBaggageInfoResp> arBaggageInfoListResp ) {}

            @Override
            public void onBaggageInfoByPNRAndBGNumError(String rt_code, String rt_msg) {}

            @Override
            public void onBaggageInfoByBGNumSuccess(String rt_code, String rt_msg, ArrayList<CIBaggageInfoContentResp> arDataList ) {

                Bitmap   bitmap   = ImageHandle.getScreenShot(getActivity());
                Bitmap   blur     = ImageHandle.BlurBuilder(getActivity(), bitmap, 13.5f, 0.15f);

                Bundle bundle = new Bundle();
                bundle.putParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG, blur);
                bundle.putSerializable(UiMessageDef.BUNDLE_BAGGAGE_CONTENT_RESP, arDataList);
                bundle.putString(UiMessageDef.BUNDLE_BAGGAGE_CONTENT_NUMBER, m_BaggageEntity.Baggage_ShowNumber);
                bundle.putString(UiMessageDef.BUNDLE_BAGGAGE_CONTENT_DEPARTUREDATE, m_tripData.Departure_Date);
                bundle.putString(UiMessageDef.BUNDLE_BAGGAGE_CONTENT_DEPARTURESTATION, m_tripData.Departure_Station);
                bundle.putString(UiMessageDef.BUNDLE_BAGGAGE_CONTENT_ARRIVALSTATION, m_tripData.Arrival_Station);

                Intent intent = new Intent();
                intent.putExtras(bundle);

                ChangeActivity( intent, CIBaggageInfoContentActivity.class);
            }

            @Override
            public void onBaggageInfoByBGNumError(String rt_code, String rt_msg) {
                showDialog(getString(R.string.warning),
                        rt_msg,
                        getString(R.string.confirm));
            }

            @Override
            public void showProgress() {
                if ( null != m_Listener )
                    m_Listener.showProDlg();
            }

            @Override
            public void hideProgress() {
                if ( null != m_Listener )
                    m_Listener.hideProDlg();
            }
        };


        private CIEWallet_ExtraService_Info handleBaggageData(int bagType, CIPassengerListResp_PaxInfo passengerItem ){
            BaggageEntity bagData = null;
            CIEWallet_ExtraService_Info data = new CIEWallet_ExtraService_Info();
            if(bagType == onPassengerRecyclerViewAdapterListener.EWALLET_DBBaggage) {
                bagData = passengerItem.DBBaggage;
                data.SERVICETYPE = CIExtraServiceRecyclerViewAdapter.EServiceType.ExtraBaggage.name();

            } else if(bagType == onPassengerRecyclerViewAdapterListener.EWALLET_ExcessBaggage) {
                bagData = passengerItem.ExcessBaggage;
                data.SERVICETYPE = CIExtraServiceRecyclerViewAdapter.EServiceType.EB.name();
            }

            if(bagData == null) {
                return null;
            }
            data.TICKETNO = bagData.ticketno;
            data.EBAMOUNT = bagData.ebamount;
            data.EBCURRENCY = bagData.ebcurrency;
            data.FIRSTNAME = bagData.firstname;
            data.LASTNAME = bagData.lastname;
            data.SSRAMOUNT = bagData.ssramount;
            data.SSRTYPE = bagData.ssrtype;
            data.EMDNO = bagData.emdno;
            data.Flight_Info = bagData.Flight_Info;
            data.STATUS = bagData.status;
            data.PURCHASE_DATE = bagData.purchase_date;

            return data;
        }

        private void displayEwallet(CIEWallet_ExtraService_Info data){

            if(null == data) {
                return;
            }
            Bitmap   bitmap   = ImageHandle.getScreenShot(getActivity());
            Bitmap   blur     = ImageHandle.BlurBuilder(getActivity(), bitmap, 13.5f, 0.15f);
            boolean  finalBStatue;
            if( "Y".equals(data.STATUS) ){
                finalBStatue = true;
            } else {
                finalBStatue = false;
            }
            Bundle bundle = new Bundle();
            bundle.putString(UiMessageDef.BUNDLE_ACTIVITY_MODE,
                             data.SERVICETYPE);
            bundle.putBoolean(CIExtraServiceItem.DEF_IS_EXPIRED_TAG,
                              finalBStatue);
            bundle.putParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG, blur);
            bundle.putSerializable(UiMessageDef.BUNDLE_EWALLET_EXTRA_SERVICES_DATA,
                                   data);
            Intent intent = new Intent();
            intent.putExtras(bundle);

            intent.setClass(getContext(), CIExtraServicesCardActivity.class);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.anim_alpha_in, 0);
        }

        @Override
        public void ViewWifiOnClick() {

        }

        @Override
        public void ViewHSROnClick() {

        }

        @Override
        public void AddPassengerOnClick() {
//            //2016.05.30先暫時拿掉新增旅客的進入點
//            //重設Tab乘客數
//            if ( null != m_Listener ) {
//                m_Listener.ResetPassengerCount(m_alPassengerItem.size());
//            }
//
//            Intent intent = new Intent();
//            intent.setClass(getActivity(), CIAddPassengerActivity.class);
//            intent.putExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE,
//                    CIAddPassengerActivity.EMode.BASE_SINGLE.name());
//            startActivityForResult(intent, REQUEST_CODE);
//            getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
        }

        @Override
        public void AddMemberCard(String pnr_id, String pnr_seq, String pax_number) {
            CIAddMemberCardReq req = new CIAddMemberCardReq();
            req.Pnr_id = pnr_id;
            req.Pnr_Seq = pnr_seq;
            req.Pax_Number = pax_number;
            CIAddMemberCardPresenter.getInstance(m_AddMemberListener).addMemberCard(req);
        }

        @Override
        public void ManageTicket(CIPassengerListResp_PaxInfo passengerItem) {

            if ( null != m_ManagerPresenter ){
                m_ManagerPresenter.fetchBookTicketWebData(getManageTicketPostData(passengerItem));
            }
        }
    };

    CIAddMemberListener m_AddMemberListener = new CIAddMemberListener() {
        @Override
        public void onSuccess(String rt_code, String rt_msg) {
            showDialog(getString(R.string.warning),
                       rt_msg,
                       getString(R.string.confirm));
            m_Listener.ReLoadPassenagerInfo();
        }

        @Override
        public void onError(String rt_code, String rt_msg) {
            showDialog(getString(R.string.warning),
                       rt_msg,
                       getString(R.string.confirm));
        }

        @Override
        public void showProgress() {
            if ( null != m_Listener )
                m_Listener.showProDlg();
        }

        @Override
        public void hideProgress() {
            if ( null != m_Listener )
                m_Listener.hideProDlg();
        }
    };

    CICheckFlightMealOpenListener m_CheckFlightMealOpenWSListener = new CICheckFlightMealOpenListener() {
        @Override
        public void onSuccess(String rt_code, String rt_msg) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            //m_tripData 為該行程所有的相關資料
            //m_PassengerData 是該行程所有的乘客資料
            if ( null != m_tripData )
                bundle.putSerializable(UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST, m_tripData);
            if ( null != m_PaxInfo )
                bundle.putSerializable(UiMessageDef.BUNDLE_PASSENGER_INFO_SINGLE, m_PaxInfo );

            intent.putExtras(bundle);
            ChangeActivity(intent, CISelectPassengerActivity.class,
                    UiMessageDef.REQUEST_CODE_TRIP_DETAIL_PASSENGER_SELECT_MEAL);
        }

        @Override
        public void onError(String rt_code, String rt_msg) {

            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm));
        }

        @Override
        public void showProgress() {
            if ( null != m_Listener )
                m_Listener.showProDlg();
        }

        @Override
        public void hideProgress() {
            if ( null != m_Listener )
                m_Listener.hideProDlg();
        }
    };

    CIHandleOrderMealListener m_DeleteMealListener = new CIHandleOrderMealListener() {

        @Override
        public void onOrderSuccess(String rt_code, String rt_msg) {}

        @Override
        public void onOrderError(String rt_code, String rt_msg) {}

        @Override
        public void onDeleteOrderSuccess(String rt_code, String rt_msg) {
            if ( null != m_Listener ){
                m_Listener.ReLoadPassenagerInfo();
            }
        }

        @Override
        public void onDeleteOrderError(String rt_code, String rt_msg) {
            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm));
        }

        @Override
        public void showProgress() {
            if ( null != m_Listener )
                m_Listener.showProDlg();
        }

        @Override
        public void hideProgress() {
            if ( null != m_Listener )
                m_Listener.hideProDlg();
        }
    };

    CIInquiryCheckInListener m_CheckInWSListener = new CIInquiryCheckInListener() {
        @Override
        public void onInquiryCheckInSuccess(String rt_code, String rt_msg, CICheckInAllPaxResp CheckInList) {

            ArrayList<CICheckInPax_InfoEntity> arrayList = CheckInList;

            //新站
            Intent intent = new Intent();
            intent.putExtra(CICheckInActivity.BUNDLE_ENTRY,
                    CICheckInActivity.BUNDLE_PARA_ENTRY_TRIP_DETAIL);
            intent.putExtra(UiMessageDef.BUNDLE_CHECK_IN_FLIGHT_LIST, arrayList);

            ChangeActivity(intent, CICheckInActivity.class,
                    UiMessageDef.REQUEST_CODE_TRIP_DETAIL_PASSENGER_CHECK_IN);
        }

        @Override
        public void onInquiryCheckInError(String rt_code, String rt_msg) {
            //-998表示為舊站
            if(rt_code.equals(CIWSResultCode.NO_RESULTS)) {

                Intent intent = new Intent();
                intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_TITLE_TEXT_TAG, getString(R.string.check_in));
                intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_URL_TAG, m_strOldCheckInUrl);

                ChangeActivity(intent, CIWithoutInternetActivity.class,
                        UiMessageDef.REQUEST_CODE_TRIP_DETAIL_PASSENGER_CHECK_IN_WEB);

            } else {
                showDialog(getString(R.string.warning),
                        rt_msg,
                        getString(R.string.confirm));
            }
        }

        @Override
        public void onInquiryCheckInAllPaxSuccess(String rt_code, String rt_msg, CICheckInAllPaxResp CheckInList) {

        }

        @Override
        public void onInquiryCheckInAllPaxError(String rt_code, String rt_msg) {

        }

        @Override
        public void showProgress() {
            if ( null != m_Listener )
                m_Listener.showProDlg();
        }

        @Override
        public void hideProgress() {
            if ( null != m_Listener )
                m_Listener.hideProDlg();
        }
    };

    CIInquiryBoardPassListener m_BoardPassWSListener = new CIInquiryBoardPassListener() {
        @Override
        public void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas) {

            //比對pnrId一致
            CIBoardPassResp_PnrInfo pnrInfo = null;
            if ( 1 != datas.Pnr_Info.size() ){
                for ( int i = 0; i < datas.Pnr_Info.size(); i ++ ){
                    if ( datas.Pnr_Info.get(i).Pnr_Id.equals( m_tripData.Pnr_Id ) ){
                        pnrInfo = datas.Pnr_Info.get(i);
                        break;
                    }
                }
            }else {
                pnrInfo = datas.Pnr_Info.get(0);
            }

            if ( null == pnrInfo
                    || !"000".equals(pnrInfo.rt_code)
                    || null == pnrInfo.Itinerary
                    || 0 >= pnrInfo.Itinerary.size() ){
                showDialog(getString(R.string.warning),
                        getString(R.string.no_match_data),
                        getString(R.string.confirm));
                return;
            }

            //無論加到什麼資料皆顯示，不需比對時間及人名 by Kevin
            CIBoardPassResp_Itinerary itinerary      = new CIBoardPassResp_Itinerary();
            //ArrayList<CIBoardPassResp_PaxInfo> alPax = new ArrayList<>();
            for ( int i = 0; i < pnrInfo.Itinerary.size(); i ++ ){
                //2016-08-24 by Ryan
                //調整邏輯，當遇到多航段同時Check-in的時候會拿到多航段的BoardingPass, 此時只需要顯示user 點擊的航段就可以
                //故檢查BoardingPass 的航段是否屬於ser 點擊的航段
                //2018-10-28 新增判斷條件調整為 Departure_Station、Arrival_Station、Departure_Date、Airlines + Flight_Number  (航空公司 + 航班編號)
                if ( TextUtils.equals(pnrInfo.Itinerary.get(i).Departure_Station,   m_tripData.Departure_Station)   &&
                        TextUtils.equals(pnrInfo.Itinerary.get(i).Arrival_Station,  m_tripData.Arrival_Station)     &&
                        TextUtils.equals(pnrInfo.Itinerary.get(i).Departure_Date,   m_tripData.Departure_Date)      &&
                        TextUtils.equals(pnrInfo.Itinerary.get(i).Airlines,         m_tripData.Airlines)            &&
                        TextUtils.equals(pnrInfo.Itinerary.get(i).Flight_Number,    m_tripData.Flight_Number)       ){

                    itinerary = pnrInfo.Itinerary.get(i);
                }
            }

            if ( null == itinerary || null == itinerary.Pax_Info || 0 >= itinerary.Pax_Info.size() ){

                showDialog(getString(R.string.warning),
                        getString(R.string.no_match_data),
                        getString(R.string.confirm));
                return;
            }


            //依照點擊的乘客名稱，登機證ViewPager跳轉至同名的頁面 by Kevin
            boolean isFindedPassenger   = false;
            int     paxIndex            = 0;
            //2016-08-24 ryan for 直接使用要顯示的航段來判斷
            //for(CIBoardPassResp_PaxInfo data: alPax){
            for( CIBoardPassResp_PaxInfo data : itinerary.Pax_Info ){
                if(TextUtils.equals(data.First_Name, m_strFirstName)
                        && TextUtils.equals(data.Last_Name, m_strLastName)){
                    isFindedPassenger = true;
                    break;
                }
                paxIndex++;
            }

            Bitmap bitmap = ImageHandle.getScreenShot(getActivity());
            Bitmap blur = ImageHandle.BlurBuilder(getActivity(), bitmap, 13.5f, 0.15f);

            Bundle bundle = new Bundle();
//            //傳入是否已使用登機證的tag
//            bundle.putBoolean(UiMessageDef.BUNDLE_BOARDING_PASS_IS_EXPIRED_TAG, bIsExpired);

//            //當前點擊的是第幾個乘客 by Kevin
            if(true == isFindedPassenger){
                bundle.putInt(UiMessageDef.BUNDLE_BOARDING_PASS_INDEX, paxIndex);
            }
            bundle.putSerializable(
                    UiMessageDef.BUNDLE_BOARDING_PASS_DATA, itinerary);
            bundle.putParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG, blur);

            Intent intent = new Intent();
            intent.putExtras(bundle);
            intent.setClass(getActivity(), CIBoardingPassCardActivity.class);
            getActivity().startActivity(intent);

            getActivity().overridePendingTransition(R.anim.anim_alpha_in, 0);

            bitmap.recycle();
            System.gc();
        }

        @Override
        public void onError(String rt_code, String rt_msg) {
            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm));
        }

        @Override
        public void showProgress() {
            if ( null != m_Listener )
                m_Listener.showProDlg();
        }

        @Override
        public void hideProgress() {
            if ( null != m_Listener )
                m_Listener.hideProDlg();
        }
    };

    CIManageTicketPresenter.CallBack m_onManagerCallback = new CIManageTicketPresenter.CallBack() {
        @Override
        public void showProgress() {
            if ( null != m_Listener )
                m_Listener.showProDlg();
        }

        @Override
        public void hideProgress() {
            if ( null != m_Listener )
                m_Listener.hideProDlg();
        }

        @Override
        public void onDataBinded(String webData) {
            Intent data = new Intent();
            data.putExtra(UiMessageDef.BUNDLE_WEBVIEW_TITLE_TEXT_TAG, getString(R.string.ticket_modifly));
            data.putExtra(UiMessageDef.BUNDLE_WEBVIEW_WEB_DATA_TAG, webData);
            data.putExtra(UiMessageDef.BUNDLE_WEBVIEW_WEB_IS_SHOW_CLOSE_BTN_TAG, true);
            ChangeActivity( data, CIWithoutInternetActivity.class );
        }

        @Override
        public void onDataFetchFeild(String msg) {
            showDialog(getString(R.string.warning),
                    msg,
                    getString(R.string.confirm));
        }
    };

    CIManageTicketPresenter.CallBack m_onManagerCallback_Services = new CIManageTicketPresenter.CallBack() {
        @Override
        public void showProgress() {
            if ( null != m_Listener )
                m_Listener.showProDlg();
        }

        @Override
        public void hideProgress() {
            if ( null != m_Listener )
                m_Listener.hideProDlg();
        }

        @Override
        public void onDataBinded(String webData) {
            Intent data = new Intent();
            data.putExtra(UiMessageDef.BUNDLE_WEBVIEW_TITLE_TEXT_TAG, getString(R.string.select_seat));
            data.putExtra(UiMessageDef.BUNDLE_WEBVIEW_WEB_DATA_TAG, webData);
            data.putExtra(UiMessageDef.BUNDLE_WEBVIEW_WEB_IS_SHOW_CLOSE_BTN_TAG, true);
            ChangeActivity( data, CIWithoutInternetActivity.class );
        }

        @Override
        public void onDataFetchFeild(String msg) {
            showDialog(getString(R.string.warning),
                    msg,
                    getString(R.string.confirm));
        }
    };

    private final int                   REQUEST_CODE       = 3916; //(cip 字母順序)

    private BoardingPassType            m_type             = BoardingPassType.AVAILABLE;
    private String                      m_addPassengerName = null;

    private RecyclerView                    m_rv            = null;
    private CIPassengerRecyclerViewAdapter  m_adapter       = null;

    private ArrayList<CIPassengerListResp_PaxInfo>       m_alPassengerItem   = new ArrayList<>();
    private boolean                     m_bShowAddView      = false;

    private ArrayList<CICheckInPax_InfoEntity> m_CheckInResp = null;
    private CIPassengerListResp         m_PassengerData     = null;
    //被點擊的該位乘客
    private CIPassengerListResp_PaxInfo m_PaxInfo           = null;
    private CITripListResp_Itinerary    m_tripData          = null;
    private boolean                     m_bIsOnlineCheckIn  = false;
    private int                         m_iPNRStatus        = HomePage_Status.TYPE_UNKNOW;
    private String                      m_strOldCheckInUrl  = null;

    //2016-08-10 add for 需要判斷該航班是否可以線上列印電子登機證
    private boolean                     m_bIsvPass          = false;

    //取到登機證時 要比對姓名
    private String                      m_strFirstName      = "";
    private String                      m_strLastName       = "";
    //當前乘客index
//    private int                         m_iPaxIndex = 0;

    /**Inquiry 時輸入的FirstName LastName 用來當作是未登入時的本人姓名*/
    private String m_strInquiryFirstName = "";
    private String m_strInquiryLastName  = "";


    //2016-07-15 ryan 調整Check-in Presenter 使用方式
    private CIInquiryCheckInPresenter m_InquiryCheckInPresenter = null;

    //2018-06-26 ryan 新增改票功能
    private CIManageTicketPresenter m_ManagerPresenter = null;

    //643924-2019
    private CIManageTicketPresenter m_ManagerPresenter_Services = null;

    //2018-10-31 新增行李追蹤，被點擊的行李牌卡
    private CIBaggageInfoNumEntity m_BaggageEntity = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_recycler_view;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        m_rv = (RecyclerView)view.findViewById(R.id.rv);
        m_rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        m_adapter = new CIPassengerRecyclerViewAdapter(
//                this,
                getActivity(),
                m_tripData,
                m_alPassengerItem,
                m_onPassengerRecyclerViewAdapterListener,
                m_bIsOnlineCheckIn,
                m_bShowAddView,
                m_iPNRStatus,
                m_bIsvPass,
                m_strInquiryFirstName,
                m_strInquiryLastName);

        m_rv.setAdapter(m_adapter);

        if ( null != m_Parameter ){
            m_tripData = m_Parameter.getTripData();
        }
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.setMargins(m_rv, 10, 0, 10, 0);
    }

    @Override
    protected void setOnParameterAndListener(View view) {

        m_InquiryCheckInPresenter = new CIInquiryCheckInPresenter(m_CheckInWSListener);

        //2018-06-26 新增改票
        m_ManagerPresenter = new CIManageTicketPresenter(m_onManagerCallback);
        m_ManagerPresenter_Services = new CIManageTicketPresenter(m_onManagerCallback_Services);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {}

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    public void onLanguageChangeUpdateUI() {}

    private void GoSelectSeatActivity(){

        if ( null == m_tripData ){
            showDialog(getString(R.string.warning), getString(R.string.no_match_data));
            return;
        }
        if ( null == m_tripData.Is_Do )
            m_tripData.Is_Do = "";

        CIPassengerListResp passengerList = new CIPassengerListResp();
        if ( null != m_PassengerData){

            for ( int i = 0; i < m_PassengerData.Pax_Info.size(); i ++ ){

                if ( null == m_PassengerData.Pax_Info.get(i).Is_Change_Seat )
                    m_PassengerData.Pax_Info.get(i).Is_Change_Seat = "";
                if ( null == m_PassengerData.Pax_Info.get(i).Pax_Type )
                    m_PassengerData.Pax_Info.get(i).Pax_Type = "";

                //當Is_Do=Y & Is_Change_Seat = Y & Pax_Type != IFN & Status_Code為3.4.5時 可以選擇或編輯座位
                if ( m_tripData.Is_Do.equals("Y")
                        && m_PassengerData.Pax_Info.get(i).Is_Change_Seat.equals("Y")
                        && !m_PassengerData.Pax_Info.get(i).Pax_Type.equals(CIPassengerListResp_PaxInfo.PASSENGER_INFANT)
                        && true == IsPaxCanSelectSeat(m_PassengerData.Pax_Info.get(i).Status_Code) ){
                    passengerList.Pax_Info.add(m_PassengerData.Pax_Info.get(i));
                }
            }
        }

//        //只能傳入可選位的乘客資料
//        ArrayList<CICheckInPax_InfoEntity> alPassenger = new ArrayList<>();
//        if ( null != m_CheckInResp){
//            for ( int i = 0; i < m_CheckInResp.size(); i ++ ){
//                for ( int j = 0; j < passengerList.Pax_Info.size(); j ++ ){
//                    if ( m_CheckInResp.get(i).First_Name.equals(passengerList.Pax_Info.get(j).First_Name)
//                    && m_CheckInResp.get(i).Last_Name.equals(passengerList.Pax_Info.get(j).Last_Name) ){
//                        alPassenger.add(m_CheckInResp.get(i));
//                    }
//                }
//            }
//        }

        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        //check-in選位 要帶Y, N
        if(CIWSHomeStatus_Code.TYPE_D_E_CHECKIN == m_iPNRStatus){
            bundle.putString(UiMessageDef.BUNDLE_IS_CHECK_IN_SELECT_SEAT, "Y");
//            if ( null != m_CheckInResp){
//                //cpr 行程與乘客資料
//                bundle.putSerializable(UiMessageDef.BUNDLE_CHECK_IN_SELECT_SEAT_DATA, alPassenger);
//            }else {
//                showDialog(getString(R.string.warning), getString(R.string.no_match_data));
//                return;
//            }
        } else {
            bundle.putString(UiMessageDef.BUNDLE_IS_CHECK_IN_SELECT_SEAT, "N");
        }
        //m_tripData 為該行程所有的相關資料
        bundle.putSerializable(UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST, m_tripData);
        //m_PassengerData 是該行程所有的乘客資料
        bundle.putSerializable(UiMessageDef.BUNDLE_PASSENGER_INFO, passengerList);

        intent.putExtras(bundle);

        ChangeActivity(intent, CISelectSeatMapActivity.class,
//        ChangeActivity(intent, CISelectSeatActivity.class,
                UiMessageDef.REQUEST_CODE_TRIP_DETAIL_PASSENGER_SELECT_SEAT);
    }

    private void GoSelectSeatActivity_Web(){

        if ( null != m_PassengerData){
            if(m_tripData.Is_Do.equals("Y")
                    && true == IsPaxCanSelectSeat(m_iPNRStatus)){
                if(CIWSHomeStatus_Code.TYPE_C_SEAT_180D_14D == m_iPNRStatus
                        ||CIWSHomeStatus_Code.TYPE_C_SEAT_MEAL_14D_24H == m_iPNRStatus){
                    if ( null != m_ManagerPresenter_Services ){
                        for ( int i = 0; i < m_PassengerData.Pax_Info.size(); i ++ ){
                            if (m_PassengerData.Pax_Info.get(i).Is_Change_Seat.equals("Y")
                                    && !m_PassengerData.Pax_Info.get(i).Pax_Type.equals(CIPassengerListResp_PaxInfo.PASSENGER_INFANT)){
                                m_ManagerPresenter_Services.fetchBookTicketWebData(getManageTicketPostData_Services(m_PassengerData.Pax_Info.get(i)));
                            }
                        }
                    }
                } else if(CIWSHomeStatus_Code.TYPE_D_E_CHECKIN == m_iPNRStatus
                        || CIWSHomeStatus_Code.TYPE_D_E_GATE_INFO == m_iPNRStatus){
                    Intent intent = new Intent();
                    intent.putExtra(
                            UiMessageDef.BUNDLE_WEBVIEW_TITLE_TEXT_TAG,
                            getString(R.string.select_seat));
                    for ( int i = 0; i < m_PassengerData.Pax_Info.size(); i ++ ){
                        if (m_PassengerData.Pax_Info.get(i).Is_Change_Seat.equals("Y")
                                && !m_PassengerData.Pax_Info.get(i).Pax_Type.equals(CIPassengerListResp_PaxInfo.PASSENGER_INFANT)){
                            intent.putExtra(
                                    UiMessageDef.BUNDLE_WEBVIEW_URL_TAG,
                                    getString(R.string.trip_detail_checkin_url)+"&IIdentification="+m_tripData.Pnr_Id+"&ISurname="+m_PassengerData.Pax_Info.get(i).Last_Name);
                        }
                    }
                    intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_WEB_IS_SHOW_CLOSE_BTN_TAG, true);
                    ChangeActivity(intent, CIWithoutInternetActivity.class);
                }
            }
        }
    }

    private boolean IsPaxCanSelectSeat(int iStatus){

        switch ( iStatus ){
            case CIWSHomeStatus_Code.TYPE_B_HAVE_TICKET:
                return false;
            case CIWSHomeStatus_Code.TYPE_C_SEAT_180D_14D:
                return true;
            case CIWSHomeStatus_Code.TYPE_C_SEAT_MEAL_14D_24H:
                return true;
            case CIWSHomeStatus_Code.TYPE_D_E_CHECKIN:
                //643924-2019
                //return false;
                return true;
            case CIWSHomeStatus_Code.TYPE_D_E_GATE_INFO:
                if ( m_iPNRStatus == CIWSHomeStatus_Code.TYPE_D_E_CHECKIN ){
                    return true;
                }else {
                    return false;
                }
            case CIWSHomeStatus_Code.TYPE_F_IN_FLIGHT:
            case CIWSHomeStatus_Code.TYPE_G_TRANSITION:
            case CIWSHomeStatus_Code.TYPE_H_ARRIVAL:
            default:
                return false;
        }
    }

    private void GoSelectMealActivity(){
        CICheckFlightMealOpenReq req = new CICheckFlightMealOpenReq();

        if ( null != m_tripData ){

            if ( null == m_tripData.Airlines )
                m_tripData.Airlines = "";
            if ( null == m_tripData.Flight_Number )
                m_tripData.Flight_Number = "";
            if ( null == m_tripData.Departure_Station )
                m_tripData.Departure_Station = "";
            if ( null == m_tripData.Arrival_Station )
                m_tripData.Arrival_Station = "";
            if ( null == m_tripData.Booking_Class_Name_Tag )
                m_tripData.Booking_Class_Name_Tag = "";

            //CI or AE, 抓tripData
            req.flight_company  = m_tripData.Airlines;

            //2016-06-21 modifly by ryan for 統一由 WS Model層解析為4碼, ui 不用特別處理
            req.flight_num      = m_tripData.Flight_Number;

            //出發+目的地的機場代碼, 抓tripData
            req.flight_sector   = m_tripData.Departure_Station + m_tripData.Arrival_Station;
            //飛行日, 抓tripData出發日
            req.flight_date     = m_tripData.Departure_Date;
            //艙等
            req.seat_class      = m_tripData.Booking_Class_Name_Tag;
        }

        if ( null != m_PaxInfo ){
            if ( null == m_PaxInfo.Meal.tkt_confirm_code )
                m_PaxInfo.Meal.tkt_confirm_code = "";

            //PNR狀態
            req.pnr_status      = m_PaxInfo.Meal.tkt_confirm_code;
        }

        CICheckFlightMealOpenPresenter.getInstance(m_CheckFlightMealOpenWSListener).CheckFlightMealOpenFromWS(req);
    }

    private String getManageTicketPostData( CIPassengerListResp_PaxInfo passengerItem ){

        /**
         * 文件：CA_I-004_reservation inquiry interface_v2.1.docx
         * 版本：2018/-6/26
         * postData = LANG=GB&REC_LOC=NZ3T6A&LASTNAME=OKARR&FIRSTNAME=TEST&SERVICE_TYPE=RetrievePNR
         * */
        Map<String, String> mapParams = new LinkedHashMap<>();

        Locale locale = CIApplication.getLanguageInfo().getLanguage_Locale();
        switch (locale.toString()){
            case "zh_TW":
                mapParams.put("LANG","TW");
                break;
            case "zh_CN":
                mapParams.put("LANG","CN");
                break;
            case "en":
                mapParams.put("LANG","GB");
                break;
            case "ja_JP":
                mapParams.put("LANG","JP");
                break;
        }

        mapParams.put("REC_LOC" ,       m_tripData.Pnr_Id);
        mapParams.put("LASTNAME" ,      passengerItem.Last_Name);
        mapParams.put("FIRSTNAME" ,     passengerItem.First_Name);
        mapParams.put("SERVICE_TYPE" ,  "RetrievePNR");

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

    //643924-2019-Start
    private String getManageTicketPostData_Services( CIPassengerListResp_PaxInfo passengerItem ){

        /**
         * 文件：CA_I-004_reservation inquiry interface_v2.1.docx
         * 版本：2018/-6/26
         * postData = LANG=GB&REC_LOC=NZ3T6A&LASTNAME=OKARR&FIRSTNAME=TEST&SERVICE_TYPE=RetrievePNRServices
         * */
        Map<String, String> mapParams = new LinkedHashMap<>();

        Locale locale = CIApplication.getLanguageInfo().getLanguage_Locale();
        switch (locale.toString()){
            case "zh_TW":
                mapParams.put("LANG","TW");
                break;
            case "zh_CN":
                mapParams.put("LANG","CN");
                break;
            case "en":
                mapParams.put("LANG","GB");
                break;
            case "ja_JP":
                mapParams.put("LANG","JP");
                break;
        }

        mapParams.put("REC_LOC" ,       m_tripData.Pnr_Id);
        mapParams.put("LASTNAME" ,      passengerItem.Last_Name);
        mapParams.put("FIRSTNAME" ,     passengerItem.First_Name);
        mapParams.put("SERVICE_TYPE" ,  "RetrievePNRServices");

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
    //643924-2019-End

    private void DelectMealInfo(){

        CIDeleteOrderMealReq deleteOrderMealReq = new CIDeleteOrderMealReq();

        if ( null != m_tripData ){
            deleteOrderMealReq.pnr_Id           = m_tripData.Pnr_Id;
        }
        if ( null != m_PaxInfo ){
            deleteOrderMealReq.itinerary_seq    = m_PaxInfo.Meal.itinerary_seq;
            deleteOrderMealReq.pono_num         = m_PaxInfo.Meal.pono_number;
            deleteOrderMealReq.ssr_seq          = String.valueOf( m_PaxInfo.Meal.ssr_seq );
        }

        CIHandleOrderMealPresenter.getInstance(m_DeleteMealListener).DeleteOrderMealFromWS(deleteOrderMealReq);
    }

    private void ChangeActivity(Intent intent, Class clazz){
        intent.setClass(getActivity(), clazz);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    private void ChangeActivity(Intent intent, Class clazz, int iRequestCode){
        intent.setClass(getActivity(), clazz);
        getActivity().startActivityForResult(intent, iRequestCode);
        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    public OnPassengerTabFragmentInterface uiSetParameterListener(
            OnPassengerTabFragmentParameter onParameter,
            OnPassengerTabFragmentListener onListener) {
        m_Parameter     = onParameter;
        m_Listener      = onListener;
        return m_Interface;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == getActivity().RESULT_OK && requestCode == REQUEST_CODE && null != data){
            m_addPassengerName = data.getStringExtra(CIAddPassengerFragment.NAME);
            CIToastView.makeText(getActivity(), m_addPassengerName).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        m_InquiryCheckInPresenter.InquiryCheckInCancel();
        CIInquiryBoardPassPresenter.getInstance(m_BoardPassWSListener).interrupt();
        CICheckFlightMealOpenPresenter.getInstance(m_CheckFlightMealOpenWSListener).CheckFlightMealOpenCancel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CIInquiryBoardPassPresenter.getInstance(null);
        //CIInquiryCheckInPresenter.getInstance(null);
        m_InquiryCheckInPresenter.setCallBack(null);
        CICheckFlightMealOpenPresenter.getInstance(null);
    }
}
