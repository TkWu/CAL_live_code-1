package ci.function.PersonalDetail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.function.Core.SLog;
import ci.function.Main.BaseActivity;
import ci.function.PersonalDetail.APIS.CIAddSaveAPISActivity;
import ci.function.PersonalDetail.APIS.CIPersonalAPISDetialActivity;
import ci.function.PersonalDetail.APIS.CIPersonalAddAPISActivity;
import ci.function.PersonalDetail.APIS.CIPersonalCompanionsAPISListActivity;
import ci.function.PersonalDetail.QRCode.CIPersonalQRCodeActivity;
import ci.function.PersonalDetail.SocialNetwork.CISocialNetworkDetailActivity;
import ci.function.Signup.CISignUpActivity;
import ci.ui.ApisCard.CIApisCardView;
import ci.ui.SocialNetworkCard.CIPersonalSocialNetworkView;
import ci.ui.SocialNetworkCard.CIPersonalSocialNetworkView.OnPersonalSocialNetworkViewListener;
import ci.ui.TextField.CIApisDocmuntTextFieldFragment;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;
import ci.ui.object.CILoginInfo;
import ci.ui.toast.CIToastView;
import ci.ui.view.ImageHandle;
import ci.ws.Models.entities.CIApisEntity;
import ci.ws.Models.entities.CIApisResp;
import ci.ws.Models.entities.CICompanionApisNameEntity;
import ci.ws.Models.entities.CIExpiringMileageResp;
import ci.ws.Models.entities.CIInquiryAwardRecordRespList;
import ci.ws.Models.entities.CIMileageRecordResp;
import ci.ws.Models.entities.CIMileageResp;
import ci.ws.Models.entities.CIMilesProgressEntity;
import ci.ws.Models.entities.CIProfileEntity;
import ci.ws.Models.entities.CIRedeemRecordResp;
import ci.ws.Presenter.CIAPISPresenter;
import ci.ws.Presenter.CIInquiryMileagePresenter;
import ci.ws.Presenter.CIInquiryMilesProgressPresenter;
import ci.ws.Presenter.CIProfilePresenter;
import ci.ws.Presenter.Listener.CIInquiryApisListListener;
import ci.ws.Presenter.Listener.CIInquiryMileageListener;
import ci.ws.Presenter.Listener.CIInquiryMilesProgressListener;
import ci.ws.Presenter.Listener.CIInquiryProfileListener;
import ci.ws.define.CICardType;

/**
 * 個人資訊
 * Created by jlchen on 2016/3/16.
 */
public class CIPersonalFragment extends BaseFragment implements
        View.OnClickListener {

    CIInquiryMileageListener m_MileageListener = new CIInquiryMileageListener() {
        @Override
        public void onInquiryMileageSuccess(String rt_code, String rt_msg, CIMileageResp MileageResp) {
            //總里程數有更動時才更新畫面並將資料存入記憶體
            if (!m_strMile.equals(MileageResp.mileage)){

                CIApplication.getLoginInfo().SetMiles(MileageResp.mileage);
                m_strMile = MileageResp.mileage;

                m_tvMiles.setText(m_strMile);
            }
        }

        @Override
        public void onInquiryMileageError(String rt_code, String rt_msg) {}

        @Override
        public void onInquiryExpiringMileageSuccess(String rt_code, String rt_msg, CIExpiringMileageResp expiringMileageResp) {}

        @Override
        public void onInquiryExpiringMileageError(String rt_code, String rt_msg) {}

        @Override
        public void onInquiryMileageRecordSuccess(String rt_code, String rt_msg, CIMileageRecordResp mileageRecordResp) {}

        @Override
        public void onInquiryMileageRecordError(String rt_code, String rt_msg) {}

        @Override
        public void onInquiryRedeemRecordSuccess(String rt_code, String rt_msg, CIRedeemRecordResp redeemRecordResp) {}

        @Override
        public void onInquiryRedeemRecordError(String rt_code, String rt_msg) {}

        @Override
        public void onInquiryAwardRecordSuccess(String rt_code, String rt_msg, CIInquiryAwardRecordRespList awardRecordResps) {}

        @Override
        public void onInquiryAwardRecordError(String rt_code, String rt_msg) {}

        @Override
        public void showProgress() {}

        @Override
        public void hideProgress() {}

        @Override
        public void onAuthorizationFailedError(String rt_code, String rt_msg) {
            isProcessWSErrCode(rt_code, rt_msg);
        }
    };

    private OnPersonalSocialNetworkViewListener m_OnPersonalSocialNetworkViewListener = new OnPersonalSocialNetworkViewListener() {
        @Override
        public void OnEditClick() {
            Intent intent = new Intent();
            intent.setClass(getActivity(), CISocialNetworkDetailActivity.class);
            startActivityForResult(intent, UiMessageDef.REQUEST_CODE_SOCIAL_NETWORK_TAG);

            getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
        }

        @Override
        public void OnFacebookConnectClick() {}

        @Override
        public void OnFacebookDisconnectClick() {}

        @Override
        public void OnGoogleConnectClick() {}

        @Override
        public void OnGoogleDisconnectClick() {}
    };

    private CIPersonalProfileFragment.onPersonalProfileCardViewListener m_OnPersonalProfileCardViewListener = new CIPersonalProfileFragment.onPersonalProfileCardViewListener() {
        @Override
        public void onEditPersonalProfileClick() {
            Intent intent = new Intent();
            intent.putExtra(UiMessageDef.BUNDLE_ACTIVITY_MODE, CISignUpActivity.EMode.EDIT.name());
            intent.putExtra(UiMessageDef.BUNDLE_ACTIVITY_DATA, m_profile);
            intent.setClass(getActivity(), CISignUpActivity.class);
            startActivityForResult(intent, UiMessageDef.REQUEST_CODE_PERSONAL_PROFILE_EDIT);
            getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
        }
    };

    private CIApisCardView.OnPersonalApisCardViewListener m_OnPersonalApisCardViewListener = new CIApisCardView.OnPersonalApisCardViewListener() {
        @Override
        public void OnAddPersonalAPISClick() {
//            Intent intent = new Intent();
//            intent.putExtra(
//                    UiMessageDef.BUNDLE_ACTIVITY_MODE,
//                    CIPersonalAddAPISActivity.CIPersonalAddAPISType.ADD_MY_APIS.name());
//            Bundle bundle = new Bundle();
//            bundle.putSerializable(CIApisDocmuntTextFieldFragment.APIS_TYPE, CIApisDocmuntTextFieldFragment.EType.Personal);
//            intent.putExtras(bundle);
//            intent.setClass(getActivity(), CIPersonalAddAPISActivity.class);
//            startActivityForResult(intent, UiMessageDef.REQUEST_CODE_PERSONAL_ADD_APIS_TAG);
//            getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);


            Intent intent = new Intent();
            intent.putExtra(
                    UiMessageDef.BUNDLE_ACTIVITY_MODE,
                    CIPersonalAddAPISActivity.CIPersonalAddAPISType.ADD_MY_APIS.name());
            Bundle bundle = new Bundle();
            bundle.putSerializable(CIApisDocmuntTextFieldFragment.APIS_TYPE, CIApisDocmuntTextFieldFragment.EType.Personal);
            intent.putExtras(bundle);
            intent.setClass(getActivity(), CIAddSaveAPISActivity.class);
            startActivityForResult(intent, UiMessageDef.REQUEST_CODE_PERSONAL_ADD_APIS_TAG);
            getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
        }

        @Override
        public void OnAddCompanionsAPISClick() {
            Intent intent = new Intent();
            intent.putExtra(
                    UiMessageDef.BUNDLE_ACTIVITY_MODE,
                    CIPersonalAddAPISActivity.CIPersonalAddAPISType.ADD_COMPANAIONS_APIS.name());
            intent.setClass(getActivity(), CIPersonalAddAPISActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(CIApisDocmuntTextFieldFragment.APIS_TYPE, CIApisDocmuntTextFieldFragment.EType.Personal);
            intent.putExtras(bundle);
            startActivityForResult(intent, UiMessageDef.REQUEST_CODE_PERSONAL_ADD_COMPANIONS_APIS_TAG);

            getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
        }

        @Override
        public void OnPersonalAPISDetailClick(String strAPISName) {
            Intent intent = new Intent();
            intent.putExtra(
                    UiMessageDef.BUNDLE_ACTIVITY_MODE,
                    CIPersonalAddAPISActivity.CIPersonalAddAPISType.EDIT_MY_APIS.name());
            intent.putExtra(
                    UiMessageDef.BUNDLE_PERSONAL_EDIT_APIS_USER_NAME_TAG,
                    CIApplication.getLoginInfo().GetUserName());
            intent.putExtra(
                    UiMessageDef.BUNDLE_PERSONAL_EDIT_APIS_TAG, strAPISName);
            intent.setClass(getActivity(), CIPersonalAPISDetialActivity.class);
            startActivityForResult(intent, UiMessageDef.REQUEST_CODE_PERSONAL_EDIT_APIS_TAG);

            getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
        }

        @Override
        public void OnCompanionsAPISDetailClick(String strCompanionName, String strAPISNameS) {
            Intent intent = new Intent();
            intent.putExtra(
                    UiMessageDef.BUNDLE_PERSONAL_EDIT_APIS_USER_NAME_TAG, strCompanionName);
            intent.putExtra(
                    UiMessageDef.BUNDLE_PERSONAL_EDIT_APIS_TAG, strAPISNameS);
            intent.setClass(getActivity(), CIPersonalCompanionsAPISListActivity.class);
            startActivityForResult(intent, UiMessageDef.REQUEST_CODE_PERSONAL_EDIT_COMPANIONS_APIS_TAG);

            getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
        }
    };

    private CIInquiryMilesProgressListener m_InquiryMilesProgressListener = new CIInquiryMilesProgressListener() {
        @Override
        public void onStationSuccess(String rt_code, String rt_msg, CIMilesProgressEntity miles) {

            //2016-09-12 調整顯示的日期
            m_tvDate.setText(CIApplication.getLoginInfo().GetCardTypeExp());
            //2016/07/22 ryan for 調整時間 format
            //m_tvDate.setText(miles.expire_date);
            //m_tvDate.setText(AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormatToyyyyMMddEEE(miles.expire_date));
            m_tvMiles.setText(CIApplication.getLoginInfo().GetMiles());
            m_tvFlights.setText(Integer.toString((int)miles.flight));
            //m_tvFlights.setText(String.format("%f",miles.flight));

            CIPersonalMileFragment fragment = (CIPersonalMileFragment)getChildFragmentManager().findFragmentByTag(CIPersonalMileFragment.class.getSimpleName());
            fragment.setPersonalMileInfo(miles);

        }

//        //2016/07/22 ryan for 調整時間 format
//        private String getDateFormat( String strDepartrue ){
//
//            String strDate = "";
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            try {
//                Date date = sdf.parse(strDepartrue);
//
//                SimpleDateFormat sdfDate = new SimpleDateFormat("MMM dd, yyyy");
//                strDate = sdfDate.format(date);
//
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            return strDate;
//        }

        @Override
        public void onStationError(String rt_code, String rt_msg) {
           SLog.e("onStationError", "rt_code: " + rt_code + "   rt_msg: " + rt_msg);

            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm));
        }

        @Override
        public void showProgress() {

        }

        @Override
        public void hideProgress() {

        }

        @Override
        public void onAuthorizationFailedError(String rt_code, String rt_msg) {
            isProcessWSErrCode(rt_code, rt_msg);
        }
    };

    private CIInquiryProfileListener m_InquiryProfileListener = new CIInquiryProfileListener() {
        @Override
        public void onInquiryProfileSuccess(String rt_code, String rt_msg, CIProfileEntity profile) {

            CILoginInfo loginInfo = CIApplication.getLoginInfo();

            if( null != profile ) {
                //更新 Profile 資料

                m_profile = profile;

                loginInfo.SetBirthday(profile.birth_date);
                loginInfo.SetCellCity(profile.cell_city);
                loginInfo.SetCellNum(profile.cell_num);
                loginInfo.SetRcvEmail(profile.rcv_email);
                loginInfo.SetRcvSMS(profile.rcv_sms);
                loginInfo.SetChinName(profile.chin_name);
                loginInfo.SetSurName(profile.surname);
                loginInfo.SetIDNum(profile.id_num);
                loginInfo.SetNationCode(profile.nation_code);
                loginInfo.SetPassport(profile.Passport);
                loginInfo.SetGuardCardNo(profile.guard_card_no);
                loginInfo.SetGuardFirstName(profile.guard_first_name);
                loginInfo.SetGuardLastName(profile.guard_last_name);
                loginInfo.SetGuardBirthDate(profile.guard_birth_date);
                loginInfo.SetMealType(profile.Meal_Type);
                loginInfo.SetSeatCode(profile.Seat_Code);
                loginInfo.SetUserEmail(profile.email);
                loginInfo.SetUserProfileFirstName(profile.first_name);
                loginInfo.SetUserProfileLastName(profile.last_name);
                loginInfo.SetCardType(profile.CardType);
                loginInfo.SetCardTypeExp(profile.CardTypeExp);
                loginInfo.SetVipCardType(profile.VipType);
                loginInfo.SetVipCardEffDate(profile.VipEffdt);
                loginInfo.SetVipCardExpDate(profile.VipExprdt);

                //有綁定Google帳號
                if( CIProfileEntity.SOCIAL_COMBINE_YES.equals(profile.con_google) ) {
                    loginInfo.SetGoogleCombineStatus(true);
                    loginInfo.SetGoogleEmail(profile.google_mail);
                    loginInfo.SetGoogleLoginId(profile.google_uid);
                } else {
                    loginInfo.SetGoogleCombineStatus(false);
                    loginInfo.SetGoogleEmail("");
                    loginInfo.SetGoogleLoginId("");
                }

                //有綁定Facebook帳號
                if( CIProfileEntity.SOCIAL_COMBINE_YES.equals(profile.con_facebook) ) {
                    loginInfo.SetFbCombineStatus(true);
                    loginInfo.SetFbEmail(profile.facebook_mail);
                    loginInfo.SetFbLoginId(profile.facebook_uid);
                } else {
                    loginInfo.SetFbCombineStatus(false);
                    loginInfo.SetFbEmail("");
                    loginInfo.SetFbLoginId("");
                }
            }

            if( null != m_profileFragment ) {
                m_profileFragment.updateProfileInfo(profile);
            }

            if( null != m_ivCard) {
                m_ivCard.setImageResource(getCarResourceId());
            }

            if( null != m_tvDate) {
                m_tvDate.setText(loginInfo.GetCardTypeExp());
            }

            refreshCardData();
            setSocialNetworkView();
        }

        @Override
        public void onInquiryProfileError(String rt_code, String rt_msg) {
            //更新 Profile 資料
            CIApplication.getLoginInfo().SetBirthday("");
            CIApplication.getLoginInfo().SetCellCity("");
            CIApplication.getLoginInfo().SetCellNum("");
            CIApplication.getLoginInfo().SetRcvEmail("");
            CIApplication.getLoginInfo().SetRcvSMS("");
            CIApplication.getLoginInfo().SetChinName("");
            CIApplication.getLoginInfo().SetSurName("");
            CIApplication.getLoginInfo().SetIDNum("");
            CIApplication.getLoginInfo().SetNationCode("");
            CIApplication.getLoginInfo().SetPassport("");
            CIApplication.getLoginInfo().SetGuardCardNo("");
            CIApplication.getLoginInfo().SetGuardFirstName("");
            CIApplication.getLoginInfo().SetGuardLastName("");
            CIApplication.getLoginInfo().SetGuardBirthDate("");
            CIApplication.getLoginInfo().SetMealType("");
            CIApplication.getLoginInfo().SetSeatCode("");
            CIApplication.getLoginInfo().SetUserEmail("");
            CIApplication.getLoginInfo().SetUserProfileFirstName("");
            CIApplication.getLoginInfo().SetUserProfileLastName("");

            CIApplication.getLoginInfo().SetGoogleCombineStatus(false);
            CIApplication.getLoginInfo().SetGoogleEmail("");
            CIApplication.getLoginInfo().SetGoogleLoginId("");

            CIApplication.getLoginInfo().SetFbCombineStatus(false);
            CIApplication.getLoginInfo().SetFbEmail("");
            CIApplication.getLoginInfo().SetFbLoginId("");

            if( null != m_profileFragment ) {
                m_profileFragment.updateProfileInfo(null);
            }

            setSocialNetworkView();

            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm));
        }

        @Override
        public void onUpdateProfileSuccess(String rt_code, String rt_msg) {

        }

        @Override
        public void onUpdateProfileError(String rt_code, String rt_msg) {

        }

        @Override
        public void showProgress() {

        }

        @Override
        public void hideProgress() {

        }

        @Override
        public void onAuthorizationFailedError(String rt_code, String rt_msg) {
            isProcessWSErrCode(rt_code, rt_msg);
        }
    };



//    private CIPersonalQRCodeActivity m_PopupWindow = null;
    private ScrollView      m_sv                    = null;
    private RelativeLayout  m_rlCard                = null;
    private TextView        m_tvCardData_cardno     = null;
    private TextView        m_tvCardData_cardname   = null;
    private TextView        m_tvCardData_cardvalid  = null;



    private ImageView       m_ivCard                = null;
    private TextView        m_tvDate                = null;
    private TextView        m_tvMiles               = null;
    private TextView        m_tvFlights             = null;
    private LinearLayout    m_llQrcode              = null;
    private FrameLayout     m_flSocialNetworkView   = null;
    private FrameLayout     m_flMyApisView          = null;
    private FrameLayout     m_flCompanionsApisView  = null;

    private CIPersonalProfileFragment   m_profileFragment   = null;
    private CIPersonalMileFragment      m_mileFragmetn      = null;

    private String          m_strMile               = "0";
    private boolean         m_bWSError              = true;

    private CIProfileEntity m_profile = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_personal;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        m_sv                    = (ScrollView) view.findViewById(R.id.root);
        m_rlCard                = (RelativeLayout) view.findViewById(R.id.rl_dynasty_flyer_card);
        m_tvCardData_cardno     = (TextView) view.findViewById(R.id.tv_card_no) ;
        m_tvCardData_cardname   = (TextView) view.findViewById(R.id.tv_card_name) ;
        m_tvCardData_cardvalid  = (TextView) view.findViewById(R.id.tv_card_valid) ;
        m_ivCard                = (ImageView) view.findViewById(R.id.iv_card);

        refreshCardData();

//        if( m_bitmapCard == null ) {
//            m_bitmapCard = ImageHandle.getLocalBitmap(getActivity(), iResource, 1);
//        }
//        m_ivCard.setImageBitmap(m_bitmapCard);

        m_tvDate                = (TextView) view.findViewById(R.id.tv_date);
        m_tvMiles               = (TextView) view.findViewById(R.id.tv_miles_data);
        m_tvMiles.setText(CIApplication.getLoginInfo().GetMiles());

        m_tvFlights             = (TextView) view.findViewById(R.id.tv_flights_data);

        m_llQrcode              = (LinearLayout) view.findViewById(R.id.ll_qrcode);
        m_llQrcode.setOnClickListener(this);
        m_flSocialNetworkView   = (FrameLayout) view.findViewById(R.id.fl_social_network);
        m_flMyApisView          = (FrameLayout) view.findViewById(R.id.fl_my_apis);
        m_flCompanionsApisView  = (FrameLayout) view.findViewById(R.id.fl_companions_apis);
    }

    private void refreshCardData(){
        CILoginInfo loginInfo = CIApplication.getLoginInfo();
        if( null != m_tvCardData_cardno && null != m_tvCardData_cardname && null != m_tvCardData_cardvalid) {
            m_tvCardData_cardno.setText(String.format("%s", loginInfo.GetUserMemberCardNo()));

            m_tvCardData_cardname.setText(String.format("%s", loginInfo.GetUserName()));

            m_tvCardData_cardvalid.setText(String.format("THRU %s",
                    getDateByFormat(loginInfo.GetCardTypeExp())));
        }
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
//        Log.e("Start", "[selfAdjustAllView]");
//        vScaleDef.selfAdjustAllView(m_sv);
//        Log.e("End", "[selfAdjustAllView]");

        //Log.e("Start", "[setTextSizeAndLayoutParams]*");

        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.rlayout);
        relativeLayout.setPadding(
                vScaleDef.getLayoutWidth(10), vScaleDef.getLayoutHeight(20),
                vScaleDef.getLayoutWidth(10), 0);

        RelativeLayout.LayoutParams rParams = (RelativeLayout.LayoutParams) m_rlCard.getLayoutParams();
        rParams.rightMargin = vScaleDef.getLayoutWidth(10);
        rParams.leftMargin  = vScaleDef.getLayoutWidth(10);

        rParams = (RelativeLayout.LayoutParams) m_tvCardData_cardno.getLayoutParams();
        rParams.topMargin  = vScaleDef.getLayoutHeight(84.7);
        rParams.leftMargin  = vScaleDef.getLayoutWidth(22);
        vScaleDef.setTextSize(18, m_tvCardData_cardno);

        rParams = (RelativeLayout.LayoutParams) m_tvCardData_cardname.getLayoutParams();
        rParams.topMargin  = vScaleDef.getLayoutHeight(110);
        rParams.leftMargin  = vScaleDef.getLayoutWidth(22);
        vScaleDef.setTextSize(13, m_tvCardData_cardname);

        rParams = (RelativeLayout.LayoutParams) m_tvCardData_cardvalid.getLayoutParams();
        rParams.topMargin  = vScaleDef.getLayoutHeight(128.3);
        rParams.leftMargin  = vScaleDef.getLayoutWidth(22);
        vScaleDef.setTextSize(13, m_tvCardData_cardvalid);

        RelativeLayout rlDateAndQRCode = (RelativeLayout) view.findViewById(R.id.rl_date_and_qrcode);
        rlDateAndQRCode.setPadding(
                vScaleDef.getLayoutWidth(10), 0,
                vScaleDef.getLayoutWidth(10), 0);
        rParams = (RelativeLayout.LayoutParams) rlDateAndQRCode.getLayoutParams();
        rParams.topMargin   = vScaleDef.getLayoutHeight(20.9);

        rParams = (RelativeLayout.LayoutParams)view.findViewById(R.id.ll_date).getLayoutParams();
        rParams.width       = vScaleDef.getLayoutWidth(190);

        TextView tvValidDate = (TextView) view.findViewById(R.id.tv_valid_date);
        tvValidDate.setMinHeight(vScaleDef.getLayoutHeight(15.7));
        vScaleDef.setTextSize(13, tvValidDate);

        LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams)m_tvDate.getLayoutParams();
        lParams.topMargin   = vScaleDef.getLayoutHeight(4);
        m_tvDate.setMinHeight(vScaleDef.getLayoutHeight(19.3));
        vScaleDef.setTextSize(vScaleDef.DEF_TEXT_SIZE_16, m_tvDate);

        rParams = (RelativeLayout.LayoutParams)m_llQrcode.getLayoutParams();
        rParams.height      = vScaleDef.getLayoutHeight(40);
        rParams.width       = vScaleDef.getLayoutWidth(120);
        rParams.leftMargin  = vScaleDef.getLayoutWidth(10);
        m_llQrcode.setPadding(vScaleDef.getLayoutWidth(9.7), 0, vScaleDef.getLayoutWidth(10), 0);

        TextView tvQRCode = (TextView) view.findViewById(R.id.tv_qrcode);
        tvQRCode.setMinHeight(vScaleDef.getLayoutHeight(19.3));
        vScaleDef.setTextSize(vScaleDef.DEF_TEXT_SIZE_16, tvQRCode);
        lParams = (LinearLayout.LayoutParams)tvQRCode.getLayoutParams();
        lParams.leftMargin  = vScaleDef.getLayoutWidth(6.3);

        LinearLayout llMileAndFlight = (LinearLayout) view.findViewById(R.id.ll_mile_and_flight);
        llMileAndFlight.setPadding(
                vScaleDef.getLayoutWidth(10), 0,
                vScaleDef.getLayoutWidth(10), 0);
        rParams = (RelativeLayout.LayoutParams)llMileAndFlight.getLayoutParams();
        rParams.topMargin   = vScaleDef.getLayoutHeight(30.3);

        lParams = (LinearLayout.LayoutParams) view.findViewById(R.id.ll_mile).getLayoutParams();
        lParams.width       = vScaleDef.getLayoutWidth(173.3);

        m_tvMiles.setMinHeight(vScaleDef.getLayoutHeight(38.3));
        vScaleDef.setTextSize(32, m_tvMiles);

        TextView tvMile = (TextView) view.findViewById(R.id.tv_miles);
        tvMile.setMinHeight(vScaleDef.getLayoutHeight(15.7));
        vScaleDef.setTextSize(13, tvMile);
        lParams = (LinearLayout.LayoutParams)tvMile.getLayoutParams();
        lParams.topMargin   = vScaleDef.getLayoutHeight(0.3);

        lParams = (LinearLayout.LayoutParams) view.findViewById(R.id.v_line).getLayoutParams();
        lParams.width       = vScaleDef.getLayoutWidth(0.7);
        lParams.height      = vScaleDef.getLayoutHeight(40);
        lParams.rightMargin = vScaleDef.getLayoutWidth(23);
        lParams.leftMargin  = vScaleDef.getLayoutWidth(23);

        lParams = (LinearLayout.LayoutParams) view.findViewById(R.id.ll_flight).getLayoutParams();
        lParams.width       = vScaleDef.getLayoutWidth(100);

        m_tvFlights.setMinHeight(vScaleDef.getLayoutHeight(38.3));
        vScaleDef.setTextSize(32, m_tvFlights);

        TextView tvFlight = (TextView) view.findViewById(R.id.tv_flights);
        tvFlight.setMinHeight(vScaleDef.getLayoutHeight(15.7));
        vScaleDef.setTextSize(13, tvFlight);
        lParams = (LinearLayout.LayoutParams)tvFlight.getLayoutParams();
        lParams.topMargin   = vScaleDef.getLayoutHeight(0.3);

        rParams = (RelativeLayout.LayoutParams) view.findViewById(R.id.fl_miles_progress).getLayoutParams();
        rParams.topMargin   = vScaleDef.getLayoutHeight(40);

        rParams = (RelativeLayout.LayoutParams) view.findViewById(R.id.fl_profile).getLayoutParams();
        rParams.topMargin   = vScaleDef.getLayoutHeight(10);

        rParams = (RelativeLayout.LayoutParams) m_flSocialNetworkView.getLayoutParams();
        rParams.topMargin   = vScaleDef.getLayoutHeight(10);

        rParams = (RelativeLayout.LayoutParams) m_flMyApisView.getLayoutParams();
        rParams.topMargin   = vScaleDef.getLayoutHeight(10);

        rParams = (RelativeLayout.LayoutParams) m_flCompanionsApisView.getLayoutParams();
        rParams.topMargin   = vScaleDef.getLayoutHeight(10);
        rParams.bottomMargin= vScaleDef.getLayoutHeight(20);

        //Log.e("End", "[setTextSizeAndLayoutParams]*");

        vScaleDef.selfAdjustSameScaleView(m_rlCard, 320, 200.8);
        vScaleDef.selfAdjustSameScaleView(m_ivCard, 320, 200.8);
        vScaleDef.selfAdjustSameScaleView(view.findViewById(R.id.iv_qrcode), 24, 24);

        setSocialNetworkView();
        setMyApisView();
        setCompanionsApisView();
    }

    @Override
    protected void setOnParameterAndListener(View view) {
        //CIProfilePresenter.getInstance(null).InquiryProfileFromWS("WD9751230");
        //new CIUpdateProfileModel(null).UpdateProfileFromWS("", null);
    }

    @Override
    protected void registerFragment(final FragmentManager fragmentManager) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {

                m_mileFragmetn = new CIPersonalMileFragment();
                m_profileFragment = new CIPersonalProfileFragment();
                m_profileFragment.setPersonalProfileCardViewListener(m_OnPersonalProfileCardViewListener);

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fl_miles_progress, m_mileFragmetn, m_mileFragmetn.getClass().getSimpleName());
                transaction.replace(R.id.fl_profile, m_profileFragment, m_profileFragment.getClass().getSimpleName());

                transaction.commit();

            }
        });
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();

        if( m_mileFragmetn != null ) {
            m_mileFragmetn.onFragmentResume();
        }

        if( m_profileFragment != null ) {
            m_profileFragment.onFragmentResume();
        }

        final int iResource = getCarResourceId();
        m_ivCard.setImageResource(iResource);
    }

    @Override
    public void onFragmentPause() {
        super.onFragmentPause();

        if( m_mileFragmetn != null ) {
            m_mileFragmetn.onFragmentPause();
        }

        if( m_profileFragment != null ) {
            m_profileFragment.onFragmentPause();
        }

//        recycleBitmap();
    }

    @Override
    public void onFragmentHide() {
        super.onFragmentHide();

        if( m_mileFragmetn != null ) {
            m_mileFragmetn.onFragmentHide();
        }

        if( m_profileFragment != null ) {
            m_profileFragment.onFragmentHide();
        }

        CIProfilePresenter.getInstance(m_InquiryProfileListener).InquiryProfileCancel();

    }

    private void updatePersonalDetailText() {

        ((TextView) getView().findViewById(R.id.tv_valid_date)).setText(R.string.personal_valid_date);

        ((TextView) getView().findViewById(R.id.tv_miles)).setText(R.string.miles);

        ((TextView) getView().findViewById(R.id.tv_flights)).setText(R.string.flights);

        ((TextView) getView().findViewById(R.id.tv_qrcode)).setText(R.string.qrcode);
    }

    @Override
    public void onFragmentShow() {
        super.onFragmentShow();

        //更新PersonalDetail title文字
        updatePersonalDetailText();

        m_bWSError = true;

        //設定畫面資訊
        final int iResource = getCarResourceId();
        m_ivCard.setImageResource(iResource);

        m_tvCardData_cardno.setText(
                        CIApplication.getLoginInfo().GetUserMemberCardNo());

        m_tvCardData_cardname.setText(
                        CIApplication.getLoginInfo().GetUserName());

        m_tvCardData_cardvalid.setText(
                        "THRU " + getDateByFormat(CIApplication.getLoginInfo().GetCardTypeExp()));

        //重置 效期、里程、旅次資訊
        m_tvDate.setText("");
        m_tvMiles.setText( CIApplication.getLoginInfo().GetMiles());
        m_tvFlights.setText( "" );

        //顯示登入時取到的累積里程
        if ( 0 < CIApplication.getLoginInfo().GetMiles().length() ){
            m_strMile = CIApplication.getLoginInfo().GetMiles();
        }else {
            m_strMile = "0";
        }
        m_tvMiles.setText(m_strMile);

        if( m_sv != null ) {
            m_sv.fullScroll(View.FOCUS_UP);
        }

        //取得總里程數
        CIInquiryMileagePresenter.getInstance(m_MileageListener).InquiryMileageFromWS();

        CIInquiryMilesProgressPresenter.getInstance(m_InquiryMilesProgressListener).InquiryMilesProgressFromWS(CIApplication.getLoginInfo().GetUserMemberCardNo());

        //隱藏Social Network view
        removeSocialNetworkView();

        //重建 MyApisView
        setMyApisView();
        //隱藏MyApisView
        updateMyApisView(null);

        //重建CompanionsApisView
        setCompanionsApisView();
        //更新CompanionsApisView
        queryAndUpdateCompanionsApisView();

        //Inquiry Apis list
        CIAPISPresenter.getInstance().InquiryMyApisListFromWS(CIApplication.getLoginInfo().GetUserMemberCardNo(), m_InquiryApisListListener);

        //Inquiry Profile
        CIProfilePresenter.getInstance(m_InquiryProfileListener).InquiryProfileFromWS(CIApplication.getLoginInfo().GetUserMemberCardNo());

        if( m_mileFragmetn != null ) {
            m_mileFragmetn.onFragmentShow();
        }

        if( m_profileFragment != null ) {
            m_profileFragment.onFragmentShow();
        }

    }

    private int getCarResourceId() {
        //設定畫面資訊
        int     iResource   = R.drawable.img_dynasty_flyer_paragon_personal;
        String  strCardType = CIApplication.getLoginInfo().GetCardType();

        switch (strCardType) {
            case CICardType.DYNA:
                m_tvCardData_cardvalid.setVisibility(View.GONE);
                iResource = R.drawable.img_dynasty_flyer_personal;
                break;
            case CICardType.EMER:
                m_tvCardData_cardvalid.setVisibility(View.VISIBLE);
                iResource = R.drawable.img_dynasty_flyer_emerald_personal;
                break;
            case CICardType.GOLD:
                m_tvCardData_cardvalid.setVisibility(View.VISIBLE);
                iResource = R.drawable.img_dynasty_flyer_gold_personal;
                break;
            case CICardType.PARA:
                m_tvCardData_cardvalid.setVisibility(View.VISIBLE);
                iResource = R.drawable.img_dynasty_flyer_paragon_personal;
                break;
        }

        return iResource;
    }

    private String getDateByFormat(String strDate) {

        if( TextUtils.isEmpty(strDate) || !strDate.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$")) {
            return "";
        }

        // 定義時間格式
        SimpleDateFormat sdfA = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfB = new SimpleDateFormat("yyyy-MM");

        try {
            String strNewDate = sdfB.format(sdfA.parse(strDate));

            return strNewDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return strDate;
        }
    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    public void onLanguageChangeUpdateUI() {}

    private void setSocialNetworkView() {
        m_flSocialNetworkView.removeAllViews();

        CIPersonalSocialNetworkView socialNetworkView = new CIPersonalSocialNetworkView(getActivity());

        String strFbEmail = "";
        String strGoogleEmail = "";

        if (true == CIApplication.getLoginInfo().GetFbCombineStatus()) {
            strFbEmail = CIApplication.getLoginInfo().GetFbEmail();
           SLog.i("fb", ":" + strFbEmail);
        }

        if (true == CIApplication.getLoginInfo().GetGoogleCombineStatus()) {
            strGoogleEmail = CIApplication.getLoginInfo().GetGoogleEmail();
           SLog.i("g+", ":" + strGoogleEmail);
        }

        socialNetworkView.setBrowse(strFbEmail, strGoogleEmail);

        socialNetworkView.uiSetOnParameterAndListener(m_OnPersonalSocialNetworkViewListener);
        m_flSocialNetworkView.addView(socialNetworkView);
    }

    private void removeSocialNetworkView() {
        m_flSocialNetworkView.removeAllViews();
    }

    private void setMyApisView() {
        m_flMyApisView.removeAllViews();

        CIApisCardView myApisView = new CIApisCardView(getActivity());
        myApisView.setMyApisView();

//        //TODO: DEMO Have APIS data
//        if (true == CIApplication.getLoginInfo().GetFbCombineStatus()) {
//            myApisView.setHaveData();
//        }
//        else {
//            myApisView.setHaveData(); //TODO:
//
//        }

        myApisView.uiSetOnParameterAndListener(m_OnPersonalApisCardViewListener);
        m_flMyApisView.addView(myApisView);

    }

    private CIInquiryApisListListener m_InquiryApisListListener = new CIInquiryApisListListener() {
        @Override
        public void InquiryApisSuccess(String rt_code, String rt_msg, CIApisResp apis) {

            saveMyApisFromDB(apis.arApisList);

            updateMyApisView(apis.arApisList);
        }

        @Override
        public void InquiryApisError(String rt_code, String rt_msg) {
        updateMyApisView(new ArrayList<CIApisEntity>());
        
            showDialog(getString(R.string.warning),
                    rt_msg,
                    getString(R.string.confirm));
        }

        @Override
        public void InsertApidSuccess(String rt_code, String rt_msg) {

        }

        @Override
        public void InsertApisError(String rt_code, String rt_msg) {

        }

        @Override
        public void UpdateApisSuccess(String rt_code, String rt_msg) {

        }

        @Override
        public void UpdateApisError(String rt_code, String rt_msg) {

        }

        @Override
        public void InsertUpdateApisSuccess(String rt_code, String rt_msg) {

        }

        @Override
        public void InsertUpdateApisError(String rt_code, String rt_msg) {

        }

        @Override
        public void DeleteApisSuccess(String rt_code, String rt_msg) {

        }

        @Override
        public void DeleteApisError(String rt_code, String rt_msg) {

        }

        @Override
        public void showProgress() {

        }

        @Override
        public void hideProgress() {

        }

        @Override
        public void onAuthorizationFailedError(String rt_code, String rt_msg) {
            isProcessWSErrCode(rt_code, rt_msg);


        }
    };

    private void updateMyApisView(ArrayList<CIApisEntity> ar_apisList) {
        if( m_flMyApisView.getChildCount() > 0 ) {

            CIApisCardView myApisView = (CIApisCardView)m_flMyApisView.getChildAt(0);

            if( null == ar_apisList ) {
                myApisView.setVisibility(View.GONE);
            } else {
                myApisView.setVisibility(View.VISIBLE);
                myApisView.notifyMyApisDataUpdate(ar_apisList);
            }
        }

    }

    private void saveMyApisFromDB(ArrayList<CIApisEntity> ar_apisList) {

        for( CIApisEntity entity : ar_apisList ) {
            entity.card_no = CIApplication.getLoginInfo().GetUserMemberCardNo();
            entity.setId( entity.card_no , entity.doc_type );
        }

        CIAPISPresenter.getInstance().saveMyApisList(CIApplication.getLoginInfo().GetUserMemberCardNo(), ar_apisList);
    }

    private void setCompanionsApisView() {
        m_flCompanionsApisView.removeAllViews();

        CIApisCardView companionsApisView = new CIApisCardView(getActivity());
        companionsApisView.setCompanionsApisView();

//        //TODO: DEMO Have APIS data
//        if (true == CIApplication.getLoginInfo().GetFbCombineStatus()) {
//            companionsApisView.setHaveData();
//        }

        companionsApisView.uiSetOnParameterAndListener(m_OnPersonalApisCardViewListener);
        m_flCompanionsApisView.addView(companionsApisView);

    }

    private void queryAndUpdateCompanionsApisView() {


        if( m_flCompanionsApisView.getChildCount() > 0 ) {

            CIApisCardView companionsApisView = (CIApisCardView)m_flCompanionsApisView.getChildAt(0);
            companionsApisView.setVisibility(View.GONE);

            ArrayList<CICompanionApisNameEntity> ar_apisList =
                    CIAPISPresenter.getInstance().getCompanionApisList(CIApplication.getLoginInfo().GetUserMemberCardNo());

            companionsApisView.setVisibility(View.VISIBLE);
            companionsApisView.notifyCompanionApisDataUpdate(ar_apisList);

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       SLog.i("personal result", ":" + requestCode + "," + resultCode);

        if (requestCode == UiMessageDef.REQUEST_CODE_SOCIAL_NETWORK_TAG &&
                resultCode == getActivity().RESULT_OK) {
            //新增社群連結成功
            m_sv.fullScroll(View.FOCUS_UP);
            setSocialNetworkView();
            CIToastView.makeText(getActivity(), getActivity().getString(R.string.change_has_been_saved_successfully)).show();

            //重新 Inquiry Profile
            CIProfilePresenter.getInstance(m_InquiryProfileListener).InquiryProfileFromWS(CIApplication.getLoginInfo().GetUserMemberCardNo());


        } else if (requestCode == UiMessageDef.REQUEST_CODE_SOCIAL_NETWORK_TAG &&
                resultCode == UiMessageDef.RESULT_CODE_SOCIAL_NETWORK_DISCONNECT_OK_TAG) {
            //斷開社群連結成功
            m_sv.fullScroll(View.FOCUS_UP);
            setSocialNetworkView();
            CIToastView.makeText(getActivity(), getActivity().getString(R.string.change_has_been_saved_successfully)).show();

            //重新 Inquiry Profile
            CIProfilePresenter.getInstance(m_InquiryProfileListener).InquiryProfileFromWS(CIApplication.getLoginInfo().GetUserMemberCardNo());

        }else if (requestCode == UiMessageDef.REQUEST_CODE_PERSONAL_ADD_APIS_TAG &&
                resultCode == getActivity().RESULT_OK){
            //新增APIS成功
            m_sv.fullScroll(View.FOCUS_UP);
            CIToastView.makeText(getActivity(), getActivity().getString(R.string.change_has_been_saved_successfully)).show();

            //隱藏MyApisView
            updateMyApisView(null);

            //Inquiry Apis list
            CIAPISPresenter.getInstance().InquiryMyApisListFromWS(CIApplication.getLoginInfo().GetUserMemberCardNo(), m_InquiryApisListListener);

        }else if (requestCode == UiMessageDef.REQUEST_CODE_PERSONAL_EDIT_APIS_TAG &&
                resultCode == getActivity().RESULT_OK){
            //修改/刪除APIS成功
            m_sv.fullScroll(View.FOCUS_UP);
            CIToastView.makeText(getActivity(), getActivity().getString(R.string.change_has_been_saved_successfully)).show();

            //隱藏MyApisView
            updateMyApisView(null);

            //Inquiry Apis list
            CIAPISPresenter.getInstance().InquiryMyApisListFromWS(CIApplication.getLoginInfo().GetUserMemberCardNo(), m_InquiryApisListListener);

        } else if( (requestCode == UiMessageDef.REQUEST_CODE_PERSONAL_ADD_COMPANIONS_APIS_TAG
                || requestCode == UiMessageDef.REQUEST_CODE_PERSONAL_EDIT_COMPANIONS_APIS_TAG ) &&
                resultCode == getActivity().RESULT_OK ) {
            //新增/修改/刪除 同行APIS成功
            m_sv.fullScroll(View.FOCUS_UP);
            CIToastView.makeText(getActivity(), getActivity().getString(R.string.change_has_been_saved_successfully)).show();

            //重建CompanionsApisView
            setCompanionsApisView();
            //更新CompanionsApisView
            queryAndUpdateCompanionsApisView();
        } else if ( requestCode == UiMessageDef.REQUEST_CODE_PERSONAL_PROFILE_EDIT &&
                resultCode == getActivity().RESULT_OK) {

            //編輯 profile成功
            m_sv.fullScroll(View.FOCUS_UP);
            CIToastView.makeText(getActivity(), getActivity().getString(R.string.change_has_been_saved_successfully)).show();


            //隱藏profile 牌卡
            if( m_profileFragment != null ) {
                m_profileFragment.onFragmentShow();
            }

            //移除SocialNetwork 牌卡
            removeSocialNetworkView();

            //重新 Inquiry Profile
            CIProfilePresenter.getInstance(m_InquiryProfileListener).InquiryProfileFromWS(CIApplication.getLoginInfo().GetUserMemberCardNo());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_qrcode:
                if (!AppInfo.getInstance(getActivity()).bIsNetworkAvailable()){

                    ((BaseActivity)getActivity()).showNoWifiDialogAndPopupwindow();
                    return;
                }

                //將目前的畫面截圖下來, 當作背景
                Bitmap bitmap = ImageHandle.getScreenShot(getActivity());
                Bitmap blur = ImageHandle.BlurBuilder(getActivity(), bitmap, 13.5f, 0.15f);

                Bundle bundle = new Bundle();
                bundle.putParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG, blur);
                String strQRCode = getQRCodeUserInfo();
                if ( null != strQRCode )
                    bundle.putString(UiMessageDef.BUNDLE_PERSONAL_QRCODE, strQRCode);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(getActivity(), CIPersonalQRCodeActivity.class);
                startActivity(intent);

                getActivity().overridePendingTransition(R.anim.anim_alpha_in, 0);

                bitmap.recycle();

//                if ( null != m_PopupWindow ) {
//                    if (m_PopupWindow.isShowing()) {
//                        return;
//                    }
//                }

//                Bitmap bitmap = ImageHandle.getScreenShot(getActivity());
//
//                m_PopupWindow = new CIPersonalQRCodeActivity(
//                        getActivity(),
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        bitmap, getQRCodeUserInfo() );
//
//                Rect rect = new Rect();
//                getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
//
//                m_PopupWindow.showAtLocation(
//                        getActivity().getWindow().getDecorView(),
//                        Gravity.CENTER | Gravity.CENTER, 0, rect.top);

                break;
        }
    }

    private String getQRCodeUserInfo() {

        CILoginInfo ciLoginInfo = CIApplication.getLoginInfo();

        StringBuffer sb = new StringBuffer();
        int total_len = 24;
        int firstname_len = ciLoginInfo.GetUserFirstName().length();
        int lastname_len = ciLoginInfo.GetUserLastName().length();
        sb.append(ciLoginInfo.GetUserFirstName() + String.format("%"+(total_len -firstname_len)+"s", " "));
        sb.append(ciLoginInfo.GetUserLastName() + String.format("%"+(total_len -lastname_len)+"s", " "));
        sb.append(String.format("%4s", ciLoginInfo.GetCardType()));
        sb.append(String.format("%9s", ciLoginInfo.GetUserMemberCardNo()));

        return sb.toString();
    }

    //統一用baseFragment的dialog - 2016.05.30 by Ling
//    private void showDialog(String strTitle, String strMsg, String strConfirm,CIAlertDialog.OnAlertMsgDialogListener listner) {
//        if(null == m_dialog) {
//            m_dialog = new CIAlertDialog(getContext(), listner);
//        } else if (true == m_dialog.isShowing()){
//            m_dialog.dismiss();
//            m_dialog = new CIAlertDialog(getContext(), listner);
//        }
//        m_dialog.uiSetTitleText(strTitle);
//        m_dialog.uiSetContentText(strMsg);
//        m_dialog.uiSetConfirmText(strConfirm);
//        m_dialog.show();
//    }

//    @Override
//    public boolean uiOnBackPressed() {
//        if (m_PopupWindow != null) {
//            if (m_PopupWindow.isShowing()) {
//                m_PopupWindow.dismiss();
//                return true;
//            } else {
//                return super.uiOnBackPressed();
//            }
//        } else {
//            return super.uiOnBackPressed();
//        }
//    }

    @Override
    public void onDestroy() {

        recycleBitmap();

        super.onDestroy();
    }

    private void recycleBitmap() {
        if (null != m_sv) {
            m_sv.setBackground(null);
        }

        if (null != m_ivCard) {
            m_ivCard.setImageDrawable(null);
        }

//        System.gc();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(null != m_sv){
            m_sv.setBackgroundResource(R.drawable.bg_login);
        }

        if (null != m_ivCard) {
            final int iResource = getCarResourceId();
            m_ivCard.setImageResource(iResource);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(null != m_sv){
            m_sv.setBackground(null);
        }

        if (null != m_ivCard) {
            m_ivCard.setImageDrawable(null);
        }
    }
}
