package ci.function.PersonalDetail;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.ui.TextField.CIMilesProgressDropDownMenuFragment;
import ci.ui.define.ViewScaleDef;
import ci.ws.Models.entities.CIMilesProgressEntity;
import ci.ws.define.CICardType;

/**
 * Created by kevincheng on 2016/3/16.
 * 達成標準
 * 參考 會員級別資格
 * https://www.china-airlines.com/us/zh/member/membership-benefits/member-tiers
 *
 */
public class CIPersonalMileFragment extends BaseFragment
        implements AdapterView.OnItemClickListener{

    private CIMilesProgressDropDownMenuFragment m_fragment                          = null;
    private ArrayList<String>                   m_alString                          = null;
    private EMileProgressType                   m_eType                             = EMileProgressType.MILES_COMPLETED;
    private TextView                            m_tvRenewalOrUpgradeCompleteContent = null;
    private LinearLayout                        m_llRenewal                         = null;
    private LinearLayout                        m_llUpgrade                         = null;
    private View                                m_view                              = null;
    private View                                m_vMiddimMargin                     = null;
    private TextView                            m_tvRenewalPercent                  = null;
    private View                                m_vRenewalProgress                  = null;
    private TextView                            m_tvRenewalDate                     = null;
    private TextView                            m_tvUpgradePercent                  = null;
    private View                                m_vUpgradeProgress                  = null;
    private TextView                            m_tvUpgradeDate                     = null;
    private item                                m_datas                             = null;
    private String                              m_strCardType                       = null;
    private int                                 m_iIsShowRenewalBarChartForCardType = View.GONE;
    private int                                 m_iIsShowUpgradeBarChartForCardType = View.GONE;
    private int                                 m_iIsShowRenewalBarChartForMileType = View.GONE;
    private int                                 m_iIsShowUpgradeBarChartForMileType = View.GONE;

    enum EMileProgressType {

        /** 累積哩程 */
        MILES_COMPLETED,

        /** 商務旅次 */
        FIRST_CLASS_FLIGHT_COMPLETED,

        /** 加權旅次 */
        QUALIFED_WEIGHTED_TRIPS_COMPLETED,

        /** 華航 / 華信自營航班加權旅次 */
        WEIGHTED_TRIP_COUNTS_OF_CI_AE
    }

    enum ECaleUnit {

        /** 哩程 */
        MILES,

        /** 旅次 */
        FLIGHTS
    }



    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_personal_mile;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        m_view = view;
        String selectMilesSelect[];
        m_alString = new ArrayList<>();
        selectMilesSelect = getResources().getStringArray(R.array.miles_progress_pull_down);
        Collections.addAll(m_alString, selectMilesSelect);

        //完成晉升或是續卡標準內容
        m_tvRenewalOrUpgradeCompleteContent = (TextView) view.findViewById(R.id.tv_renewal_or_upgrade_complete_content);

        //續卡區塊
        m_llRenewal         = (LinearLayout) view.findViewById(R.id.ll_renewal);

        //晉升區塊
        m_llUpgrade         = (LinearLayout) view.findViewById(R.id.ll_upgrade);

        //中間間隔
        m_vMiddimMargin     = view.findViewById(R.id.v_middim_margin);

        //續卡區塊內容
        m_tvRenewalPercent  = (TextView)view.findViewById(R.id.tv_renewal_percent);
        m_vRenewalProgress  = view.findViewById(R.id.v_renewal_progress);
        m_tvRenewalDate     = (TextView)view.findViewById(R.id.tv_renewal_date);

        //晉升區塊內容
        m_tvUpgradePercent  = (TextView)view.findViewById(R.id.tv_upgrade_percent);
        m_vUpgradeProgress  = view.findViewById(R.id.v_upgrade_progress);
        m_tvUpgradeDate     = (TextView)view.findViewById(R.id.tv_upgrade_date);

        //初始化資料
        m_strCardType = CIApplication.getLoginInfo().GetCardType();
        initialMilesData();
        initViewVisibleByMemberCardType(m_strCardType);
    }

    private void initialMilesData() {
        if( null == m_datas ) {
            m_datas = new item();
        }

        m_datas.fMilesCompletedRenewal = 0;//50000;
        m_datas.fMilesCompletedUpgrade = 0;//30000;
        m_datas.fFirstClassFlightCompletedRenewal = 0;//8;
        m_datas.fFirstClassFlightCompletedUpgrade = 0;//30;
        m_datas.fQualifedWeightedRenewal = 0;//36;
        m_datas.fQualifedWeightedUpgrade = 0;//40;
        m_datas.strMilesCompletedRenewalDate = "";//"11 Oct, 2018";
        m_datas.strMilesCompletedUpgradeDate = "";//"12 Oct, 2018";
        m_datas.strFirstClassFlightCompletedRenewalDate = "";//"13 Oct, 2018";
        m_datas.strFirstClassFlightCompletedUpgradeDate = "";//"14 Oct, 2018";
        m_datas.strQualifedWeightedRenewalDate = "";//"15 Oct, 2018";
        m_datas.strQualifedWeightedUpgradeDate = "";//"16 Oct, 2018";
        m_datas.strWeightedTripCountsOfCiAeRenewalDate = "";//"15 Oct, 2018";
        m_datas.strWeightedTripCountsOfCiAeUpgradeDate = "";//"16 Oct, 2018";

        m_eType = EMileProgressType.MILES_COMPLETED;
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(view.findViewById(R.id.root));
        changeDataTypeByMileProgressType(m_eType);
    }

    @Override
    protected void setOnParameterAndListener(View view) {

        //測試用假資料
        //new CIInquiryMilesProgressModel(null).InquiryMilesProgressFromWS("WD9750947");
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        m_fragment = CIMilesProgressDropDownMenuFragment.newInstance(R.array.miles_progress_pull_down);
        m_fragment.setOnItemClickListener(this);
        //2016-06-22 for 修正閃退問題
        //transaction.replace(R.id.fl_select_type,m_fragment,m_fragment.getClass().getSimpleName()).commit();
        transaction.replace(R.id.fl_select_type,m_fragment,m_fragment.getClass().getSimpleName()).commitAllowingStateLoss();
        m_view.findViewById(R.id.root).post(new Runnable() {
            @Override
            public void run() {
                m_fragment.setText(m_alString.get(m_eType.ordinal()));
            }
        });
    }

    @Override
    protected void onFragmentResume() {
        super.onFragmentResume();
    }

    @Override
    protected void onFragmentPause() {
        super.onFragmentPause();
    }

    @Override
    public void onFragmentHide() {
        super.onFragmentHide();
    }

    @Override
    public void onFragmentShow() {
        super.onFragmentShow();

        //更新畫面文字title
        updateMileViewText();

        getView().setVisibility(View.GONE);

        //Reset 資料以及畫面
        initialMilesData();

        //初始化資料
        m_strCardType = CIApplication.getLoginInfo().GetCardType();
        initViewVisibleByMemberCardType(m_strCardType);
    }

    //更新畫面文字title
    private void updateMileViewText() {

        ((TextView)getView().findViewById(R.id.tv_title)).setText(R.string.miles_progress);
        ((TextView)getView().findViewById(R.id.tv_miles_progress_notice)).setText(R.string.miles_progress_notice_msg);
        ((TextView)getView().findViewById(R.id.tv_renewal)).setText(R.string.renewal);
        ((TextView)getView().findViewById(R.id.tv_personal_detail_due)).setText(R.string.personal_detail_due);
        ((TextView)getView().findViewById(R.id.tv_upgrade)).setText(R.string.upgrade);
        ((TextView)getView().findViewById(R.id.tv_upgrade_date)).setText(R.string.upgrade_tag);

        String selectMilesSelect[];
        if( null == m_alString ) {
            m_alString = new ArrayList<>();
        } else {
            m_alString.clear();
        }
        selectMilesSelect = getResources().getStringArray(R.array.miles_progress_pull_down);
        Collections.addAll(m_alString, selectMilesSelect);

        registerFragment(getChildFragmentManager());
    }

    public void setPersonalMileInfo(CIMilesProgressEntity data) {

        getView().setVisibility(View.VISIBLE);

        if( m_datas == null ) {
            m_datas = new item();
        }


        switch (m_strCardType){
            /**華夏卡*/
            case CICardType.DYNA:
                m_datas.fStandartMilesCompletedRenewal = 0;
                m_datas.fStandartMilesCompletedUpgrade = data.miles.crtmil_standard01;
                m_datas.fMilesCompletedRenewal = 0;
                m_datas.fMilesCompletedUpgrade = data.miles.crtmil_accumulation01;

                m_datas.fStandartFirstClassFlightCompletedRenewal = 0;
                m_datas.fStandartFirstClassFlightCompletedUpgrade = data.first_class_flights.crtftrp_standard01;
                m_datas.fFirstClassFlightCompletedRenewal = 0;
                m_datas.fFirstClassFlightCompletedUpgrade = data.first_class_flights.crtftrp_accumulation01;

                m_datas.fStandartQualifedWeightedRenewal = 0;
                m_datas.fStandartQualifedWeightedUpgrade = data.qualified_weighted_trips.wgttrip_standard01;
                m_datas.fQualifedWeightedRenewal = 0;
                m_datas.fQualifedWeightedUpgrade = data.qualified_weighted_trips.wgttrip_accumulation01;

                m_datas.fStandartWeightedTripCountsOfCiAeRenewal = 0;
                m_datas.fStandartWeightedTripCountsOfCiAeUpgrade = data.ci_trips.wgtcitrip_standard01;
                m_datas.fWeightedTripCountsOfCiAeRenewal = 0;
                m_datas.fWeightedTripCountsOfCiAeUpgrade = data.ci_trips.wgtcitrip_accumulation01;

                break;

            /**金卡、翡翠卡*/
            case CICardType.GOLD:
            case CICardType.EMER:
                //2016-11-17 Modify by Ryan, 調整Tag對應
                m_datas.fStandartMilesCompletedRenewal = data.miles.crtmil_standard02;
                m_datas.fStandartMilesCompletedUpgrade = data.miles.crtmil_standard01;
                m_datas.fMilesCompletedRenewal = data.miles.crtmil_accumulation02;
                m_datas.fMilesCompletedUpgrade = data.miles.crtmil_accumulation01;

                m_datas.fStandartFirstClassFlightCompletedRenewal = data.first_class_flights.crtftrp_standard02;
                m_datas.fStandartFirstClassFlightCompletedUpgrade = data.first_class_flights.crtftrp_standard01;
                m_datas.fFirstClassFlightCompletedRenewal = data.first_class_flights.crtftrp_accumulation02;
                m_datas.fFirstClassFlightCompletedUpgrade = data.first_class_flights.crtftrp_accumulation01;

                m_datas.fStandartQualifedWeightedRenewal = data.qualified_weighted_trips.wgttrip_standard02;
                m_datas.fStandartQualifedWeightedUpgrade = data.qualified_weighted_trips.wgttrip_standard01;
                m_datas.fQualifedWeightedRenewal = data.qualified_weighted_trips.wgttrip_accumulation02;
                m_datas.fQualifedWeightedUpgrade = data.qualified_weighted_trips.wgttrip_accumulation01;

                m_datas.fStandartWeightedTripCountsOfCiAeRenewal = data.ci_trips.wgtcitrip_standard02;
                m_datas.fStandartWeightedTripCountsOfCiAeUpgrade = data.ci_trips.wgtcitrip_standard01;
                m_datas.fWeightedTripCountsOfCiAeRenewal = data.ci_trips.wgtcitrip_accumulation02;
                m_datas.fWeightedTripCountsOfCiAeUpgrade = data.ci_trips.wgtcitrip_accumulation01;
                break;

            /**晶鑽卡*/
            case CICardType.PARA:
                m_datas.fStandartMilesCompletedRenewal = data.miles.crtmil_standard01;
                m_datas.fStandartMilesCompletedUpgrade = 0;
                m_datas.fMilesCompletedRenewal = data.miles.crtmil_accumulation01;
                m_datas.fMilesCompletedUpgrade = 0;

                m_datas.fStandartFirstClassFlightCompletedRenewal = data.first_class_flights.crtftrp_standard01;
                m_datas.fStandartFirstClassFlightCompletedUpgrade = 0;
                m_datas.fFirstClassFlightCompletedRenewal = data.first_class_flights.crtftrp_accumulation01;
                m_datas.fFirstClassFlightCompletedUpgrade = 0;

                m_datas.fStandartQualifedWeightedRenewal = data.qualified_weighted_trips.wgttrip_standard01;
                m_datas.fStandartQualifedWeightedUpgrade = 0;
                m_datas.fQualifedWeightedRenewal = data.qualified_weighted_trips.wgttrip_accumulation01;
                m_datas.fQualifedWeightedUpgrade = 0;

                m_datas.fStandartWeightedTripCountsOfCiAeRenewal = data.ci_trips.wgtcitrip_standard01;
                m_datas.fStandartWeightedTripCountsOfCiAeUpgrade = 0;
                m_datas.fWeightedTripCountsOfCiAeRenewal = data.ci_trips.wgtcitrip_accumulation01;
                m_datas.fWeightedTripCountsOfCiAeUpgrade = 0;
                break;
        }

        //2016-07-22 modify by ryan for 調整顯示的 format
        //String expire_date = AppInfo.getInstance(CIApplication.getContext()).ConvertDateFormatToyyyyMMddEEE(miles.expire_date);

        //2016-11-16 Modify by ryan for 續卡效期一律調整為抓取會員卡效期
        String expire_date = CIApplication.getLoginInfo().GetCardTypeExp();
        //String expire_date = miles.expire_date;
        m_datas.strMilesCompletedRenewalDate = expire_date;
        m_datas.strMilesCompletedUpgradeDate = expire_date;
        m_datas.strFirstClassFlightCompletedRenewalDate = expire_date;
        m_datas.strFirstClassFlightCompletedUpgradeDate = expire_date;
        m_datas.strQualifedWeightedRenewalDate = expire_date;
        m_datas.strQualifedWeightedUpgradeDate = expire_date;
        m_datas.strWeightedTripCountsOfCiAeRenewalDate = expire_date;
        m_datas.strWeightedTripCountsOfCiAeUpgradeDate = expire_date;

        changeDataTypeByMileProgressType(m_eType);
    }

    /**
     * 初始化會員級別顯示的項目
     * @param cardType
     */
    private void initViewVisibleByMemberCardType(String cardType){
        switch (cardType){
            /**華夏卡*/
            case CICardType.DYNA:
                //需顯示的項目
                m_iIsShowRenewalBarChartForCardType = View.GONE;
                m_iIsShowUpgradeBarChartForCardType = View.VISIBLE;
                break;

            /**金卡、翡翠卡*/
            case CICardType.GOLD:
            case CICardType.EMER:
                //需顯示的項目
                m_iIsShowRenewalBarChartForCardType = View.VISIBLE;
                m_iIsShowUpgradeBarChartForCardType = View.VISIBLE;
                break;

            /**晶鑽卡*/
            case CICardType.PARA:
                //需顯示的項目
                m_iIsShowRenewalBarChartForCardType = View.VISIBLE;
                m_iIsShowUpgradeBarChartForCardType = View.GONE;
                break;
        }
    }

    private void initBarChart(){
        @SuppressLint("WrongConstant")
        int iMiddimMarginVisibility =
                m_iIsShowRenewalBarChartForCardType | m_iIsShowUpgradeBarChartForCardType
                | m_iIsShowRenewalBarChartForMileType | m_iIsShowUpgradeBarChartForMileType;

        m_vMiddimMargin.setVisibility(iMiddimMarginVisibility);

        @SuppressLint("WrongConstant")
        int iRenewalVisibility = m_iIsShowRenewalBarChartForCardType | m_iIsShowRenewalBarChartForMileType;

        m_llRenewal.setVisibility(iRenewalVisibility);

        @SuppressLint("WrongConstant")
        int iUpgradeVisibility = m_iIsShowUpgradeBarChartForCardType | m_iIsShowUpgradeBarChartForMileType;

        m_llUpgrade.setVisibility(iUpgradeVisibility);
    }

    /**
     * 改變里程概況累計表
     * @param type EMileProgressType
     */
    private void changeDataTypeByMileProgressType(EMileProgressType type){


        m_iIsShowRenewalBarChartForMileType = View.VISIBLE;
        m_iIsShowUpgradeBarChartForMileType = View.VISIBLE;

        switch (type){
            /**累積哩程*/
            case MILES_COMPLETED:
                setRenewalContent(m_datas.fMilesCompletedRenewal,
                                    m_datas.fStandartMilesCompletedRenewal,
                                    m_datas.strMilesCompletedRenewalDate,
                                    ECaleUnit.MILES);
                setUpgradeContent(m_datas.fMilesCompletedUpgrade,
                                    m_datas.fStandartMilesCompletedUpgrade,
                                    m_datas.strMilesCompletedUpgradeDate,
                                    ECaleUnit.MILES
                                    );
                setUpgradeTag(false);
                break;
            /**商務旅次*/
            case FIRST_CLASS_FLIGHT_COMPLETED:
                setRenewalContent(m_datas.fFirstClassFlightCompletedRenewal,
                        m_datas.fStandartFirstClassFlightCompletedRenewal,
                        m_datas.strFirstClassFlightCompletedRenewalDate,
                        ECaleUnit.FLIGHTS);
                setUpgradeContent(m_datas.fFirstClassFlightCompletedUpgrade,
                        m_datas.fStandartFirstClassFlightCompletedUpgrade,
                        m_datas.strFirstClassFlightCompletedUpgradeDate,
                        ECaleUnit.FLIGHTS
                );
                //CR 2018-01-15 只有商務旅次才顯示文字
                setUpgradeTag(true);
                break;
            /**加權旅次*/
            case QUALIFED_WEIGHTED_TRIPS_COMPLETED:
                setRenewalContent(m_datas.fQualifedWeightedRenewal,
                        m_datas.fStandartQualifedWeightedRenewal,
                        m_datas.strQualifedWeightedRenewalDate,
                        ECaleUnit.FLIGHTS);
                setUpgradeContent(m_datas.fQualifedWeightedUpgrade,
                        m_datas.fStandartQualifedWeightedUpgrade,
                        m_datas.strQualifedWeightedUpgradeDate,
                        ECaleUnit.FLIGHTS
                );
                setUpgradeTag(false);
                break;
            /**華航 / 華信自營航班加權旅次*/
            case WEIGHTED_TRIP_COUNTS_OF_CI_AE:
                setRenewalContent(m_datas.fWeightedTripCountsOfCiAeRenewal,
                                  m_datas.fStandartWeightedTripCountsOfCiAeRenewal,
                                  m_datas.strWeightedTripCountsOfCiAeRenewalDate,
                                  ECaleUnit.FLIGHTS);
                setUpgradeContent(m_datas.fWeightedTripCountsOfCiAeUpgrade,
                                  m_datas.fStandartWeightedTripCountsOfCiAeUpgrade,
                                  m_datas.strWeightedTripCountsOfCiAeUpgradeDate,
                                  ECaleUnit.FLIGHTS
                );
                setUpgradeTag(false);
                //此項永不顯示續卡長條圖
                //m_iIsShowRenewalBarChartForMileType = View.GONE;
                break;
        }
        initBarChart();
    }

    /**
     * 設定續卡區塊內容
     * @param value     實際值
     * @param standart  標準值
     * @param date      有效日期
     * @param unit      累計表單位
     */
    private void setRenewalContent(float value, float standart, String date, ECaleUnit unit) {

        if(0 >= standart){
            return;
        }
        ViewScaleDef scaleDef = ViewScaleDef.getInstance(getActivity());
        if(unit == ECaleUnit.MILES){
            m_tvRenewalPercent.setText(getStyleText(formatMilesPercentText(value, standart), scaleDef));
        } else {
            m_tvRenewalPercent.setText(getStyleText(formatFlightsPercentText(value, standart), scaleDef));
        }

        double renewalPercent = (double)value / standart;

        m_vRenewalProgress.getLayoutParams().width = scaleDef.getLayoutWidth(300 * renewalPercent);
        m_vRenewalProgress.requestLayout();
        m_tvRenewalDate.setText(date);
    }

    /**
     * 設定晉升區塊內容
     * @param value     實際值
     * @param standart  標準值
     * @param date      有效日期
     * @param unit      累計表單位
     */
    private void setUpgradeContent(float value,float standart, String date, ECaleUnit unit) {

        if(0 >= standart){
            return;
        }

        ViewScaleDef scaleDef = ViewScaleDef.getInstance(getActivity());
        if(unit == ECaleUnit.MILES){
            m_tvUpgradePercent.setText(getStyleText(formatMilesPercentText(value, standart), scaleDef));
        } else {
            m_tvUpgradePercent.setText(getStyleText(formatFlightsPercentText(value, standart), scaleDef));
        }

        double renewalPercent = (double) value / standart;

        m_vUpgradeProgress.getLayoutParams().width = scaleDef.getLayoutWidth(300 * renewalPercent);
        m_vUpgradeProgress.requestLayout();
    }

    /**
     * 設定是否顯示晉升長條圖下方文字
     * @param isShow
     */
    private void setUpgradeTag(boolean isShow){
        if(isShow) {
            m_tvUpgradeDate.setVisibility(View.VISIBLE);
        } else {
            m_tvUpgradeDate.setVisibility(View.GONE);
        }

    }

    /**
     * 設定完成續卡或晉升標準後，上方文字是否顯示
     * @param isRenewal 續卡是否達到標準
     * @param isUpgrade 晉升是否達到標準
     */
    private void setRenewalOrUpgradeCompleteContent(boolean isRenewal,
                                                    boolean isUpgrade){
        if(true == isUpgrade || true == isRenewal){
            m_tvRenewalOrUpgradeCompleteContent.setVisibility(View.VISIBLE);
            m_tvRenewalOrUpgradeCompleteContent.setText(getString(R.string.congratulations_eligible_to_renew_gold_membership));
            if(true == isUpgrade){
                m_tvRenewalOrUpgradeCompleteContent.setText( getString(R.string.congratulations_eligible_to_upgrade_to_emerald_membership));
            }
        } else {
            m_tvRenewalOrUpgradeCompleteContent.setVisibility(View.GONE);
        }
    }

    private String valueFormat( float value ){

        DecimalFormat df = new DecimalFormat("#,###,##0");
        String strformat = "";
        if ( value <= 0.0 ){
            strformat = "0";
        } else {
            strformat= df.format(value);
        }

        return strformat;
    }

    /**
     * 格式化里程百分比字串
     * @param value     目前值
     * @param standart  標準值
     * @return
     */
    private String formatMilesPercentText(float value, float standart){

        //2016-11-10 Ryan, 加入里程格式化
        String raw = getString(R.string.miles_percent);
        return String.format(raw, valueFormat(value), valueFormat(standart));
        //return String.format(raw,(int)value,(int)standart);
    }

    public static String fmt(double d)
    {
        if(d == (long) d)
            return String.format("%d",(long)d);
        else
            return String.format("%s",d);
    }

    /**
     * 格式化旅次百分比字串
     * @param value
     * @param standart
     * @return  格式化後的字串
     */
    private String formatFlightsPercentText(float value, float standart){

        //2016-11-10 Ryan, 加入里程格式化
        String raw = getString(R.string.flights_percent);
        //return String.format(raw, valueFormat(value), valueFormat(standart));
        return String.format(raw, fmt(value),fmt(standart));
    }

    private SpannableString getStyleText(String content, ViewScaleDef viewScaleDef){
        SpannableString spannableString = new SpannableString(content);
        int color;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            color = getResources().getColor(R.color.black_one,null);
        } else {
            color = getResources().getColor(R.color.black_one);
        }
        int startIndex = spannableString.toString().indexOf("/");
        int endIndex = spannableString.toString().length();
        spannableString.setSpan(new TextAppearanceSpan(null, 0,viewScaleDef.getTextSize(13) , null, null),
                                startIndex+1,
                                endIndex,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(color),
                                startIndex,
                                endIndex,
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    public void onLanguageChangeUpdateUI() {}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                m_eType = EMileProgressType.MILES_COMPLETED;
                break;
            case 1:
                m_eType = EMileProgressType.FIRST_CLASS_FLIGHT_COMPLETED;
                break;
            case 2:
                m_eType = EMileProgressType.QUALIFED_WEIGHTED_TRIPS_COMPLETED;
                break;
            case 3:
                m_eType = EMileProgressType.WEIGHTED_TRIP_COUNTS_OF_CI_AE;
                break;
        }
        changeDataTypeByMileProgressType(m_eType);
    }

    private class item{
        float  fMilesCompletedRenewal;
        float  fMilesCompletedUpgrade;
        float  fFirstClassFlightCompletedRenewal;
        float  fFirstClassFlightCompletedUpgrade;
        float  fWeightedTripCountsOfCiAeRenewal;
        float  fWeightedTripCountsOfCiAeUpgrade;
        float  fQualifedWeightedRenewal;
        float  fQualifedWeightedUpgrade;
        float  fStandartMilesCompletedRenewal;
        float  fStandartMilesCompletedUpgrade;
        float  fStandartFirstClassFlightCompletedRenewal;
        float  fStandartFirstClassFlightCompletedUpgrade;
        float  fStandartQualifedWeightedRenewal;
        float  fStandartQualifedWeightedUpgrade;
        float  fStandartWeightedTripCountsOfCiAeRenewal;
        float  fStandartWeightedTripCountsOfCiAeUpgrade;
        String strMilesCompletedRenewalDate;
        String strMilesCompletedUpgradeDate;
        String strFirstClassFlightCompletedRenewalDate;
        String strFirstClassFlightCompletedUpgradeDate;
        String strQualifedWeightedRenewalDate;
        String strQualifedWeightedUpgradeDate;
        String strWeightedTripCountsOfCiAeRenewalDate;
        String strWeightedTripCountsOfCiAeUpgradeDate;
    }

//    //2016/07/22 ryan for 調整時間 format
//    private String getDateFormat( String strDepartrue ){
//
//        String strDate = "";
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            Date date = sdf.parse(strDepartrue);
//
//            SimpleDateFormat sdfDate = new SimpleDateFormat("MMM dd, yyyy");
//            strDate = sdfDate.format(date);
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        return strDate;
//    }
}
