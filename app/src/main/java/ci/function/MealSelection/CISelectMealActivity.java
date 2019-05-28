package ci.function.MealSelection;

import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.Main.BaseActivity;
import ci.function.MealSelection.item.CIMealResultEntity;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CIDeleteOrderMealReq;
import ci.ws.Models.entities.CIInquirtMealInfoResp;
import ci.ws.Models.entities.CIInquiryMealByPassangerResp;
import ci.ws.Models.entities.CIInsertOrderMealReq;
import ci.ws.Models.entities.CIMealDetailEntity;
import ci.ws.Models.entities.CIMealInfoEntity;
import ci.ws.Models.entities.CIMealPassenagerEntity;
import ci.ws.Presenter.CIHandleOrderMealPresenter;
import ci.ws.Presenter.Listener.CIHandleOrderMealListener;

/**
 * 選擇餐點
 * Created by flowmahuang on 2016/3/8.
 * * Modifly by Ryan at 2016-09-29
 */
public class CISelectMealActivity extends BaseActivity implements View.OnClickListener {

    public final static String BUNDLE_MEAL_LIST         = "BUNDLE_MEAL_LIST";
    public final static String BUNDLE_PASSENGER_LIST    = "BUNDLE_PASSENGER_LIST";
    public final static String BUNDLE_DEPARTURE_STATION = "BUNDLE_DEPARTURE_STATION";
    public final static String BUNDLE_ARRIVAL_STATION   = "BUNDLE_ARRIVAL_STATION";

    private static final int MEAL_UNSELECT = -1;

    private NavigationBar   m_Navigationbar = null;
    private Button          m_btnNext       = null;
    private Button          m_btnPrevious   = null;

    private TextView        m_tvPassenagerName  = null;
    private LinearLayout    m_llayoutContent    = null;

    class HolderMealHint {

        public View     vCell;
        /**餐點類型描述*/
        public TextView tvDesc;
        /**餐點名稱*/
        public TextView tvName;
        /**分隔線*/
        public View     vDiv;
    }
    /**選擇的餐點皆視，每一個MealHint都是一個餐*/
    private ArrayList<HolderMealHint> m_arMealHintList = new ArrayList<>();

    private TextView        m_tvMealSelectText      = null;
    private LinearLayout    m_llayout_NormalMeal    = null;
    private LinearLayout    m_llayout_VegeMeal      = null;

    private ImageView[] m_arImgbtnMeal = null;

    private ScrollView  m_ScrollView = null;
    private View        m_ShadowView = null;

    private TextView m_tvNormalTitle= null;
    private TextView m_tvVegeTitle  = null;
    private TextView m_tvVegeDesc   = null;

    //Y軸可滾動的範圍大小,
    private int m_iScrollHeight = 0;

    private int m_iNowSelect    = MEAL_UNSELECT;
    private int m_iLastSelect   = MEAL_UNSELECT;

    private String m_strDepatureAirport;
    private String m_strArrivalAirport;

    private ViewScaleDef                        m_vScaleDef = null;
    private ArrayList<CIMealPassenagerEntity>   m_MealPassenagerList;
    private CIInquirtMealInfoResp               m_MealInfo;
    private CIHandleOrderMealPresenter          m_OrderMealPresenter = null;


    public final static String INTENT_PASSENGER_MAX = "passenger_max";

    /** 訂位代號*/
    public String   m_Pnr_id;
    /** 行程序號*/
    public int      m_Itinerary_seq;
    /**乘客搭乘倉別*/
    public String   m_Pax_seat_class;

    /**乘客選餐順位用來表示目前進行到第幾位乘客
     * 由第0位開始選，*/
    public int      m_iPassenagerSeqnum = 0;

    /**選餐順序，用來表示目前乘客正在選第幾餐*/
    public int      m_iSelectMealSeqNum = 0;

    /**Send Request順序，用來表示目前發送到哪一位乘客的選餐資訊*/
    public int      m_iSendRequestSeqNum = 0;

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_Context.getString(R.string.select_meal_flight, m_strDepatureAirport, m_strArrivalAirport);
        }
    };

    private NavigationBar.onNavigationbarListener m_onNavigationbarListener = new NavigationBar.onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {}
        @Override
        public void onLeftMenuClick() {}
        @Override
        public void onBackClick() { onBackPressed(); }
        @Override
        public void onDeleteClick() {}
        @Override
        public void onDemoModeClick() {}
    };


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_select_meal;
    }

    @Override
    protected void initialLayoutComponent() {

        m_vScaleDef = ViewScaleDef.getInstance(m_Context);

        m_Navigationbar     = (NavigationBar) findViewById(R.id.toolbar);
        m_tvPassenagerName  = (TextView) findViewById(R.id.tvPassenagerName);
        m_llayoutContent    = (LinearLayout) findViewById(R.id.llayout_content);

        m_tvMealSelectText      = (TextView) findViewById(R.id.tv_please_select_meal);
//        m_SelectMealSelection   = (LinearLayout) findViewById(R.id.llayout_select_meal_selection);
        m_llayout_NormalMeal    = (LinearLayout) findViewById(R.id.llayout_normal_meal_place);
        m_llayout_VegeMeal      = (LinearLayout) findViewById(R.id.llayout_vegetarian_meal_place);

        m_btnNext       = (Button) findViewById(R.id.btn_to_select_complete);
        m_btnPrevious   = (Button) findViewById(R.id.btn_previous);
        m_btnNext.setOnClickListener(this);
        m_btnPrevious.setOnClickListener(this);
        //
        m_ScrollView    = (ScrollView)findViewById(R.id.select_meal_scroll);
        m_ScrollView.setVerticalScrollBarEnabled(false);
        m_ShadowView    = findViewById(R.id.vGradient);

        //
        m_tvNormalTitle = (TextView)findViewById(R.id.tvNormalTitle);
        m_tvVegeTitle   = (TextView)findViewById(R.id.tvVegeTitle);
        m_tvVegeDesc    = (TextView)findViewById(R.id.tvVegeDesc);
        //
        m_ScrollView.getViewTreeObserver().addOnScrollChangedListener(m_onScroll);
        //
    }

    private void initDataFromIntent() {
        Intent getIntent = getIntent();

//        PassengerNumMax = getIntent.getIntExtra(INTENT_PASSENGER_MAX, 0) - 1;
        //
        m_MealInfo = (CIInquirtMealInfoResp)getIntent.getSerializableExtra(BUNDLE_MEAL_LIST);
        m_strDepatureAirport= getIntent.getStringExtra(BUNDLE_DEPARTURE_STATION);
        m_strArrivalAirport = getIntent.getStringExtra(BUNDLE_ARRIVAL_STATION);
        //將可選餐人員的資料分拆開來
        CIInquiryMealByPassangerResp MealPassenagerData = (CIInquiryMealByPassangerResp)getIntent.getSerializableExtra(BUNDLE_PASSENGER_LIST);
        m_Pnr_id        = MealPassenagerData.pnr_id;
        m_Itinerary_seq = MealPassenagerData.itinerary_seq;
        m_Pax_seat_class= MealPassenagerData.pax_seat_class;
        m_MealPassenagerList = MealPassenagerData.passangers;
        //
        //先進行選餐資料檢查

        int iSize = m_MealPassenagerList.size();
        for ( int iIdx = 0; iIdx < iSize; iIdx++ ){
            CIMealPassenagerEntity passenager = m_MealPassenagerList.get(iIdx);

            int iCnt = passenager.meal_info.size();
            for ( int iIx = iCnt-1; iIx >= 0; iIx-- ){
                CIMealInfoEntity mealinfo = passenager.meal_info.get(iIx);

                CIMealDetailEntity mealInfoDetail = m_MealInfo.mDetailMap.get(mealinfo.meal_seq);
                if ( null == mealInfoDetail || false == mealInfoDetail.bIsHaveMealInfo ){
                    passenager.meal_info.remove(iIx);
                }
            }
        }
        //
        PrepareMealType();
        PrepareMealItem();
    }

    /**
     * 根據MeallPassenager帶過來的資料決定選餐類型的順序
     * */
    private void PrepareMealType(){

        //先檢查乘客資訊是否有資料
        //由於每位乘客選擇的餐點類型都會相同故抓取第0位乘客餐點資訊
        int iSize = m_MealPassenagerList.size();
        if ( m_iPassenagerSeqnum >= iSize ){
            return;
        }
        CIMealPassenagerEntity mealPassenager = m_MealPassenagerList.get(m_iPassenagerSeqnum);
        //設定乘客姓名
        m_tvPassenagerName.setText( mealPassenager.pax_first_name + " " + mealPassenager.pax_last_name );
        //設定餐點類型
        int iCnt = mealPassenager.meal_info.size();
        int iViewCnt = m_llayoutContent.getChildCount();
        //當畫面上顯示的餐點類型與實際不符，重新產生View
        if ( iCnt != m_arMealHintList.size() ) {
            m_llayoutContent.removeAllViews();
            for (int iIdx = 0; iIdx < iCnt; iIdx++) {

                CIMealInfoEntity mealInfoEntity = mealPassenager.meal_info.get(iIdx);
                //
//                CIInquirtMealInfoResp.MealDetail Infodetail = m_MealInfo.mDetailMap.get(mealPASSdetail.strMealtype_code);
//                if (false == Infodetail.bIsHaveMealInfo) {
//                    continue;
//                }

                View vCell = LayoutInflater.from(m_Context).inflate(R.layout.layout_activity_select_meal_hint_item, null);

                HolderMealHint hint = new HolderMealHint();

                hint.tvDesc = (TextView) vCell.findViewById(R.id.tv_meal_desc);
                hint.tvName = (TextView) vCell.findViewById(R.id.tv_meal_name);
                hint.vDiv = vCell.findViewById(R.id.vline);

                hint.tvDesc.getLayoutParams().height = m_vScaleDef.getLayoutHeight(15.7);
                if ( iIdx != 0 ){
                    ((LinearLayout.LayoutParams) hint.tvDesc.getLayoutParams()).topMargin = m_vScaleDef.getLayoutHeight(12);
                }
                m_vScaleDef.setTextSize(13, hint.tvDesc);

                hint.tvName.getLayoutParams().height = m_vScaleDef.getLayoutHeight(19.3);
                ((LinearLayout.LayoutParams) hint.tvName.getLayoutParams()).topMargin = m_vScaleDef.getLayoutHeight(2);
                m_vScaleDef.setTextSize(16, hint.tvName);

                hint.vDiv.getLayoutParams().height = m_vScaleDef.getLayoutHeight(0.7);
                ((LinearLayout.LayoutParams) hint.vDiv.getLayoutParams()).topMargin = m_vScaleDef.getLayoutHeight(12);

                hint.tvDesc.setText(mealInfoEntity.mealtype_desc);
                //確認是否有選過餐，有選過將餐點資訊顯示在上面
                //meal_code == "", 代表沒選過餐點
                if ( TextUtils.isEmpty(mealInfoEntity.meal_code) ) {
                    hint.tvName.setText("");
                } else {
                    hint.tvName.setText( mealInfoEntity.meal_name );
                }

                hint.vCell = vCell;
                m_arMealHintList.add(hint);
                m_llayoutContent.addView(vCell);
            }

            m_iSelectMealSeqNum = 0;

        } else {
            //順序相同
            for (int iIdx = 0; iIdx < iCnt; iIdx++) {

                CIMealInfoEntity mealInfoEntity = mealPassenager.meal_info.get(iIdx);
                //
                HolderMealHint hint = m_arMealHintList.get(iIdx);
                //確認是否有選過餐，有選過將餐點資訊顯示在上面
                //meal_code == "", 代表沒選過餐點
                if ( TextUtils.isEmpty(mealInfoEntity.meal_code) ) {
                    hint.tvName.setText("");
                } else {
                    hint.tvName.setText( mealInfoEntity.meal_name );
                }
            }
        }
    }

    /**
     * 設定餐點類型標題*/
    private void setMealTypeTitle( String strMealType ) {

        if ( TextUtils.equals(CIMealInfoEntity.MEALTYPE_BREAKFAST, strMealType) ){
            m_tvMealSelectText.setText(getString(R.string.SelectMeal_Label_PleaseSelectBreakfast));

        } else if ( TextUtils.equals(CIMealInfoEntity.MEALTYPE_LUNCH, strMealType) ){
            m_tvMealSelectText.setText(getString(R.string.SelectMeal_Label_PleaseSelectLunch));

        } else if ( TextUtils.equals(CIMealInfoEntity.MEALTYPE_DINNER, strMealType) ){
            m_tvMealSelectText.setText(getString(R.string.SelectMeal_Label_PleaseSelectDinner));
        } else if ( TextUtils.equals(CIMealInfoEntity.MEALTYPE_R, strMealType) ){
            m_tvMealSelectText.setText(getString(R.string.SelectMeal_Label_PleaseSelectR));
        }
    }

    /**
     * 根據選餐順序，決定當下畫面要選擇的餐點資訊*/
    private void PrepareMealItem() {

        //根據乘客順序來決定目前要抓取哪位乘客的餐點資訊
        if ( m_iPassenagerSeqnum >= m_MealPassenagerList.size() ){
            return;
        }

        CIMealPassenagerEntity mealPassenager = m_MealPassenagerList.get(m_iPassenagerSeqnum);
        //根據乘客順序來決定目前要抓取哪位乘客的餐點資訊
        if ( m_iSelectMealSeqNum >= mealPassenager.meal_info.size() ){
            return;
        }

        //先取的目前要選第幾到餐，再透過餐點類型取的該餐點的詳細資訊
        CIMealInfoEntity PASSmealInfoEntity = mealPassenager.meal_info.get(m_iSelectMealSeqNum);
        CIMealDetailEntity mealDetailEntity = m_MealInfo.mDetailMap.get(PASSmealInfoEntity.meal_seq);

        setMealTypeTitle(PASSmealInfoEntity.mealtype_code);

        m_llayout_NormalMeal.removeAllViewsInLayout();
        m_llayout_VegeMeal.removeAllViewsInLayout();

//        if ( null == mealDetail ){
//            return;
//        }

        //記錄乘客的餐點, 已經選擇的要顯示選取的狀態
        String strMealCode = "";
        if ( false == TextUtils.isEmpty(PASSmealInfoEntity.meal_code)){
            strMealCode = PASSmealInfoEntity.meal_code;
        }

        int iNormalNum  = 0;
        int iVegeNum    = 0;
        if ( null != mealDetailEntity.arCommonList ){
            iNormalNum = mealDetailEntity.arCommonList.size();
        }
        if ( iNormalNum <= 0 ){
            m_tvNormalTitle.setVisibility(View.GONE);
            m_llayout_NormalMeal.setVisibility(View.GONE);
        } else {
            m_tvNormalTitle.setVisibility(View.VISIBLE);
            m_llayout_NormalMeal.setVisibility(View.VISIBLE);
        }
        if ( null != mealDetailEntity.arMenuOnlyList ){
            iVegeNum = mealDetailEntity.arMenuOnlyList.size();
        }
        if ( iVegeNum <= 0 ){
            m_tvVegeTitle.setVisibility(View.GONE);
            m_tvVegeDesc.setVisibility(View.GONE);
            m_llayout_VegeMeal.setVisibility(View.GONE);
        } else {
            m_tvVegeTitle.setVisibility(View.VISIBLE);
            m_tvVegeDesc.setVisibility(View.VISIBLE);
            m_llayout_VegeMeal.setVisibility(View.VISIBLE);
        }

        //暫時產生一個容器存放Button, 來展示UI點擊的效果變化
        m_arImgbtnMeal = new ImageView[iNormalNum + iVegeNum];

        LinearLayout.LayoutParams Nromalparams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                m_vScaleDef.getLayoutHeight(70));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                m_vScaleDef.getLayoutHeight(60));
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                m_vScaleDef.getLayoutHeight(0.7));
        LinearLayout.LayoutParams circleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        for (int iIdx = 0; iIdx < iNormalNum; iIdx++) {
            //
            CIMealInfoEntity MealInfo = mealDetailEntity.arCommonList.get(iIdx);
            //
            LinearLayout    llayoutNormal   = new LinearLayout(m_Context);
            ImageView       imgBtnSelect    = new ImageView(m_Context);
            TextView        tvNormal        = new TextView(m_Context);
            tvNormal.setMaxLines(2);
            tvNormal.setEllipsize(TextUtils.TruncateAt.END);

            imgBtnSelect.setLayoutParams(circleParams);
            //已經選餐的要顯示選取的圖示
            if ( TextUtils.equals(strMealCode, MealInfo.meal_code)){
                imgBtnSelect.setBackgroundResource(R.drawable.btn_radio_on);
                m_iNowSelect = iIdx;
            } else {
                imgBtnSelect.setBackgroundResource(R.drawable.btn_radio_off);
            }
            m_vScaleDef.selfAdjustSameScaleView(imgBtnSelect, 24, 24);

            tvNormal.setLayoutParams(textParams);
            tvNormal.setText(MealInfo.meal_name);
            if (Build.VERSION.SDK_INT > 22)
                tvNormal.setTextColor(getColor(R.color.white_four));
            else
                tvNormal.setTextColor(getResources().getColor(R.color.white_four));
            m_vScaleDef.setMargins(tvNormal, 10, 0, 0, 0);
            m_vScaleDef.setTextSize(16, tvNormal);

            llayoutNormal.setLayoutParams(Nromalparams);
            llayoutNormal.setGravity(Gravity.CENTER_VERTICAL);

            m_arImgbtnMeal[iIdx] = imgBtnSelect;
            //
            llayoutNormal.addView(imgBtnSelect);
            llayoutNormal.addView(tvNormal);
            llayoutNormal.setOnClickListener(CircalClickListener(iIdx));
            llayoutNormal.setTag(MealInfo);
            m_llayout_NormalMeal.addView(llayoutNormal);

            if ( iIdx != iNormalNum - 1 ) {
                View vLine = new View(m_Context);
                if (Build.VERSION.SDK_INT > 22)
                    vLine.setBackgroundColor(getColor(R.color.white_four));
                else
                    vLine.setBackgroundColor(getResources().getColor(R.color.white_four));
                vLine.setLayoutParams(lineParams);
                vLine.setAlpha(0.4f);
                m_llayout_NormalMeal.addView(vLine);
            }
        }

        for (int iJx = 0; iJx < iVegeNum; iJx++) {
            CIMealInfoEntity MealInfo = mealDetailEntity.arMenuOnlyList.get(iJx);

            //由於一般餐點與特殊餐只能選一種，故為了畫面呈現，因此依序計算ndex
            int iIdx = iNormalNum + iJx;
            //
            LinearLayout llayoutVegeMeal = new LinearLayout(m_Context);
            ImageView imgBtnSelect = new ImageView(m_Context);
            TextView tvVege = new TextView(m_Context);
            tvVege.setMaxLines(2);
            tvVege.setEllipsize(TextUtils.TruncateAt.END);

            imgBtnSelect.setLayoutParams(circleParams);
            if ( TextUtils.equals(strMealCode, MealInfo.meal_code)){
                imgBtnSelect.setBackgroundResource(R.drawable.btn_radio_on);
                m_iNowSelect = iIdx;
            } else {
                imgBtnSelect.setBackgroundResource(R.drawable.btn_radio_off);
            }
            m_vScaleDef.selfAdjustSameScaleView(imgBtnSelect, 24, 24);

            tvVege.setLayoutParams(textParams);
            tvVege.setText(MealInfo.meal_name);
            if (Build.VERSION.SDK_INT > 22)
                tvVege.setTextColor(getColor(R.color.white_four));
            else
                tvVege.setTextColor(getResources().getColor(R.color.white_four));
            m_vScaleDef.setMargins(tvVege, 10, 0, 0, 0);
            m_vScaleDef.setTextSize(16, tvVege);

            llayoutVegeMeal.setLayoutParams(params);
            llayoutVegeMeal.setGravity(Gravity.CENTER_VERTICAL);

            m_arImgbtnMeal[iIdx] = imgBtnSelect;
            llayoutVegeMeal.addView(imgBtnSelect);
            llayoutVegeMeal.addView(tvVege);
            llayoutVegeMeal.setOnClickListener(CircalClickListener(iIdx));
            llayoutVegeMeal.setTag(MealInfo);
            m_llayout_VegeMeal.addView(llayoutVegeMeal);
            //
            //放置分割線
            View vLine = new View(m_Context);
            if (Build.VERSION.SDK_INT > 22)
                vLine.setBackgroundColor(getColor(R.color.white_four));
            else
                vLine.setBackgroundColor(getResources().getColor(R.color.white_four));
            vLine.setLayoutParams(lineParams);
            vLine.setAlpha(0.4f);
            m_llayout_VegeMeal.addView(vLine);
        }
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {

        vScaleDef.selfAdjustAllView(findViewById(R.id.root));

        initDataFromIntent();

        CheckPreviousVisible();
        ChangeButtonEnable();
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
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
    protected void onLanguageChangeUpdateUI() {}

    @Override
    public void onClick(View v) {

        if ( v.getId() == m_btnNext.getId() ){

            CIMealPassenagerEntity passenage = m_MealPassenagerList.get(m_iPassenagerSeqnum);

            //點下一步的時候，先確認是否還有下一餐
            if ( m_iSelectMealSeqNum == passenage.meal_info.size()-1 ){
                //已經沒有下一餐可以選擇，則檢查是否還有下一位乘客
                if ( m_iPassenagerSeqnum == m_MealPassenagerList.size()-1 ){
                    //沒有下一位乘客，則送出餐點資訊
                    //PreparRequest();
                    //
                    m_iSendRequestSeqNum = m_MealPassenagerList.size();
                    if ( false == CheckAndSendDeleteMealRequest() ){
                        //沒有人需要先刪除餐點則直接送出選餐
                        m_iSendRequestSeqNum = m_MealPassenagerList.size();
                        SendOrderMealRequest();
                    }
                    return;
                } else {
                    m_iSelectMealSeqNum = 0;
                    m_iPassenagerSeqnum++;
                    //開始選下一位乘客
                }
            } else {
                m_iSelectMealSeqNum++;
                //開始選下一餐
            }

        } else if ( v.getId() == m_btnPrevious.getId() ){

            //如果user 不是停留在第一餐，則顯示上一個給user選擇
            if ( m_iSelectMealSeqNum != 0 ){
                m_iSelectMealSeqNum--;
                //顯示上一個餐點選擇模式


                //如果user 已經停留在第一餐, 則退回上一位乘客
            } else {

                m_iPassenagerSeqnum--;
                m_iSelectMealSeqNum = m_MealPassenagerList.get(m_iPassenagerSeqnum).meal_info.size() -1;

            }
        }
        m_ScrollView.smoothScrollTo(0,0);
        clearSelect();
        PrepareMealType();
        PrepareMealItem();
        CheckPreviousVisible();
        ChangeButtonEnable();

    }


    private View.OnClickListener CircalClickListener(final int i) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_iLastSelect = m_iNowSelect;
                m_iNowSelect = i;
                //上一次點擊的位置與當下點擊同一個，不動作
                if ( m_iLastSelect == m_iNowSelect){
                    return;
                }
                setCircleDisplay();
                //setSelectMealDisplay();

                //將點選的餐點資訊更新到畫面上以及乘客資訊內
                CIMealInfoEntity mealInfo = (CIMealInfoEntity)v.getTag();
                if ( null != mealInfo ){
                    m_arMealHintList.get(m_iSelectMealSeqNum).tvName.setText(mealInfo.meal_name);
                    //更新該位乘客的餐點資訊
                    m_MealPassenagerList.get(m_iPassenagerSeqnum).meal_info.set(m_iSelectMealSeqNum, mealInfo);

                }

                //變更Button狀態
                ChangeButtonEnable();
            }
        };
    }

    ViewTreeObserver.OnScrollChangedListener m_onScroll = new ViewTreeObserver.OnScrollChangedListener() {
        @Override
        public void onScrollChanged() {

            int iCnt = m_ScrollView.getChildCount();
            if ( iCnt > 0 ){
                m_iScrollHeight = m_ScrollView.getChildAt(0).getHeight() - m_ScrollView.getHeight();
            }

            if ( m_iScrollHeight <= 0 ){

                m_ShadowView.setAlpha(0);
                return;
            }

            //利用百分比決定 blurr 效果
            //減少分母, 提高Blur的效果
            float fAlpha = (m_ScrollView.getScrollY() / (float)m_iScrollHeight);

            // Apply a ceil
            if (fAlpha > 1) {
                fAlpha = 1;
            } else if ( fAlpha < 0 ){
                fAlpha = 0;
            }

            fAlpha = 1 - fAlpha;

            m_ShadowView.setAlpha(fAlpha);
        }
    };


    public void onBackPressed() {
        CISelectMealActivity.this.finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    public void ChangeButtonEnable(){
        if ( m_iNowSelect == -1 ){
            m_btnNext.setAlpha((float) 0.5);
            m_btnNext.setEnabled(false);
        } else {
            m_btnNext.setAlpha(1);
            m_btnNext.setEnabled(true);
        }
    }

    //確認要不要顯示上一步按鈕
    public void CheckPreviousVisible() {

        if ( m_iPassenagerSeqnum == 0 && m_iSelectMealSeqNum == 0 ) {
            m_btnPrevious.setVisibility(View.GONE);
            m_vScaleDef.setMargins(m_btnNext, 0, 0, 0, 0);
        } else {
            m_btnPrevious.setVisibility(View.VISIBLE);
            m_vScaleDef.setMargins(m_btnPrevious, 0, 0, 10, 0);
            m_vScaleDef.setMargins(m_btnNext, 10, 0, 0, 0);
        }
    }

    private void setCircleDisplay() {
        if (m_iNowSelect != MEAL_UNSELECT)
            m_arImgbtnMeal[m_iNowSelect].setBackgroundResource(R.drawable.btn_radio_on);
        if (m_iLastSelect != MEAL_UNSELECT)
            m_arImgbtnMeal[m_iLastSelect].setBackgroundResource(R.drawable.btn_radio_off);
    }

    private void clearSelect() {
        m_iNowSelect = MEAL_UNSELECT;
        m_iLastSelect = MEAL_UNSELECT;
        for (int i = 0; i < m_arImgbtnMeal.length; i++) {
            m_arImgbtnMeal[i].setBackgroundResource(R.drawable.btn_radio_off);
        }
        m_tvPassenagerName.setText("");
        m_tvMealSelectText.setText("");
    }

//    private void PreparRequest(){
//
//        for ( CIMealPassenagerEntity passenagerEntity : m_MealPassenagerList ){
//
//            //預設三餐都是 N,N,N;
//            String [] MealTag = { "N,N,N;", "N,N,N;", "N,N,N;" };
//
//            int iSize = passenagerEntity.meal_info.size();
//            for ( int iIdx = 0; iIdx < iSize; iIdx++ ){
//                CIMealInfoEntity mealInfoEntity = passenagerEntity.meal_info.get(iIdx);
//
//                if ( TextUtils.equals("1", mealInfoEntity.meal_seq) ){
//                    MealTag[0] =    mealInfoEntity.meal_content_seq + "," +
//                                    mealInfoEntity.meal_code + "," +
//                                    mealInfoEntity.mealtype_code + ";";
//
//                } else if ( TextUtils.equals("2", mealInfoEntity.meal_seq) ){
//                    MealTag[1] =    mealInfoEntity.meal_content_seq + "," +
//                                    mealInfoEntity.meal_code + "," +
//                                    mealInfoEntity.mealtype_code + ";";
//
//                } else if ( TextUtils.equals("3", mealInfoEntity.meal_seq) ){
//                    MealTag[2] =    mealInfoEntity.meal_content_seq + "," +
//                                    mealInfoEntity.meal_code + "," +
//                                    mealInfoEntity.mealtype_code + ";";
//                }
//            }
//
//            passenagerEntity.strErrorMsg = MealTag[0] + MealTag[1] + MealTag[2];
//        }
//        m_iSendRequestSeqNum = m_MealPassenagerList.size();
//    }

    private void SendOrderMealRequest(){

        if ( null == m_OrderMealPresenter ){
            m_OrderMealPresenter = CIHandleOrderMealPresenter.getInstance(m_InsertOrderMealListener);
        }

        int iSize = m_MealPassenagerList.size();
        while ( m_iSendRequestSeqNum > 0 ){
            //從第一個人開始送封包
            CIMealPassenagerEntity passenager = m_MealPassenagerList.get( iSize - m_iSendRequestSeqNum );

            //檢查沒有錯誤訊息，可直接送出選餐
            if ( TextUtils.isEmpty(passenager.strErrorMsg) ){
                //組封包
                //預設三餐都是 N,N,N;
                String [] MealTag = { "N,N,N;", "N,N,N;", "N,N,N;" };

                int iMealSize = passenager.meal_info.size();
                for ( int iIdx = 0; iIdx < iMealSize; iIdx++ ){
                    CIMealInfoEntity mealInfoEntity = passenager.meal_info.get(iIdx);

                    if ( TextUtils.equals("1", mealInfoEntity.meal_seq) ){
                        MealTag[0] =    mealInfoEntity.meal_content_seq + "," +
                                mealInfoEntity.meal_code + "," +
                                mealInfoEntity.mealtype_code + ";";

                    } else if ( TextUtils.equals("2", mealInfoEntity.meal_seq) ){
                        MealTag[1] =    mealInfoEntity.meal_content_seq + "," +
                                mealInfoEntity.meal_code + "," +
                                mealInfoEntity.mealtype_code + ";";

                    } else if ( TextUtils.equals("3", mealInfoEntity.meal_seq) ){
                        MealTag[2] =    mealInfoEntity.meal_content_seq + "," +
                                mealInfoEntity.meal_code + "," +
                                mealInfoEntity.mealtype_code + ";";
                    }
                }
                //
                CIInsertOrderMealReq MealReq = new CIInsertOrderMealReq();
                MealReq.pax_seq         = Integer.toString(passenager.pax_seq);
                MealReq.pax_subseq      = Integer.toString(passenager.pax_subseq);
                MealReq.itinerary_seq   = Integer.toString(m_Itinerary_seq);
                MealReq.pax_seat_class  = m_Pax_seat_class;
                MealReq.pnr_Id          = m_Pnr_id;
                MealReq.meal_detail     = MealTag[0] + MealTag[1] + MealTag[2];

                m_OrderMealPresenter.InsertOrderMealFromWS(MealReq);
                break;
            } else {
                m_iSendRequestSeqNum--;
            }
        }
    }

    private boolean CheckAndSendDeleteMealRequest(){

        Boolean bIsNeedDelete = false;

        if ( null == m_OrderMealPresenter ){
            m_OrderMealPresenter = CIHandleOrderMealPresenter.getInstance(m_InsertOrderMealListener);
        }

        //ArrayList<CIInsertOrderMealReq> arMealRequestList = new ArrayList<>();
        int iSize = m_MealPassenagerList.size();
        while ( m_iSendRequestSeqNum > 0 ) {
            CIMealPassenagerEntity passenager = m_MealPassenagerList.get( iSize - m_iSendRequestSeqNum );
            //再進行選餐之前要先檢查是否已經有選過餐，如果有選過會有訂單編號
            //要先取消上一次的餐點
            if ( false == TextUtils.isEmpty( passenager.pono_number ) ){

                CIDeleteOrderMealReq MealReq = new CIDeleteOrderMealReq();
                MealReq.pnr_Id          = m_Pnr_id;
                MealReq.itinerary_seq   = Integer.toString(m_Itinerary_seq);
                MealReq.ssr_seq         = Integer.toString(passenager.ssr_seq);
                MealReq.pono_num        = passenager.pono_number;

                m_OrderMealPresenter.DeleteOrderMealFromWS(MealReq);

                bIsNeedDelete = true;
                return bIsNeedDelete;
            } else {
                m_iSendRequestSeqNum--;
            }
        }
        return bIsNeedDelete;
    }

    private void CheckSendRequestIsSuccess(){

        ArrayList<CIMealResultEntity> arPassenagerMealResultList = new ArrayList<>();
        //ArrayList<String> arErrorPassenagerList = new ArrayList<>();
        String strErrorMessage = "";

        //選餐的總人數, 以人 -> 餐點把資料組起來
        int iPassenagerCnt = m_MealPassenagerList.size();
        for ( int ipIdx = 0; ipIdx < iPassenagerCnt; ipIdx++ ){
            CIMealPassenagerEntity passenagerEntity = m_MealPassenagerList.get(ipIdx);
            //
            CIMealResultEntity passenager = new CIMealResultEntity();
            passenager.strName = passenagerEntity.pax_first_name + " " + passenagerEntity.pax_last_name;

            //檢查是否傳送成功，只加成功的乘客資訊待到下一頁
            if ( false == TextUtils.isEmpty( passenagerEntity.strErrorMsg ) ){

                strErrorMessage += String.format( passenager.strName + "\n" + passenagerEntity.strErrorMsg + "\n\n" );
                continue;
            }

            //將餐點資訊整合起來
            int iMealCnt = m_MealPassenagerList.get(ipIdx).meal_info.size();
            for ( int iIdx = 0; iIdx < iMealCnt; iIdx++ ){
                passenager.arMealInfoList.add( passenagerEntity.meal_info.get(iIdx) );
            }

            arPassenagerMealResultList.add(passenager);
        }

        if ( false == TextUtils.isEmpty(strErrorMessage) ){

            ShowAlertMessage(arPassenagerMealResultList, strErrorMessage );
        } else {

            GoToNextActivity(arPassenagerMealResultList);
        }
    }

    private void ShowAlertMessage( final ArrayList<CIMealResultEntity> arPassenagerMealResultList, String strErrorMessage ){


        CIAlertDialog dialog = new CIAlertDialog(m_Context, new CIAlertDialog.OnAlertMsgDialogListener() {
            @Override
            public void onAlertMsgDialog_Confirm() {
                if ( arPassenagerMealResultList.size() > 0 ){
                    GoToNextActivity(arPassenagerMealResultList);
                }
            }
            @Override
            public void onAlertMsgDialogg_Cancel() {}
        });

        dialog.uiSetConfirmText(getResources().getString(R.string.yes));
        dialog.uiSetContentText(strErrorMessage);
        dialog.show();
    }

    private void GoToNextActivity( ArrayList<CIMealResultEntity> arPassenagerMealResultList ) {

        Intent intent = new Intent();
        intent.setClass(m_Context, CISelectMealResultActivity.class);
        intent.putExtra(CISelectMealResultActivity.BUNDLE_DEPARTURE_STATION,m_strDepatureAirport);
        intent.putExtra(CISelectMealResultActivity.BUNDLE_ARRIVAL_STATION, m_strArrivalAirport);
        intent.putExtra(CISelectMealResultActivity.BUNDLE_MEAL_RESULT, arPassenagerMealResultList);
        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    CIHandleOrderMealListener m_InsertOrderMealListener = new CIHandleOrderMealListener() {
        @Override
        public void onOrderSuccess(String rt_code, String rt_msg) {

            /**發送成功之後將Request封包清空，方便方便哪些乘客已經發送成功*/
            int iSize = m_MealPassenagerList.size();
            if ( m_iSendRequestSeqNum > 0 ){
                //傳送成功以後將該員的Request 封包清掉
                CIMealPassenagerEntity passenager = m_MealPassenagerList.get( iSize - m_iSendRequestSeqNum );
                passenager.strErrorMsg = "";
            }

            m_iSendRequestSeqNum--;
            if ( m_iSendRequestSeqNum > 0 ){
                SendOrderMealRequest();
            } else {

                hideProgressDialog();
                CheckSendRequestIsSuccess();
            }
        }

        @Override
        public void onOrderError(String rt_code, String rt_msg) {
            //20160912 測試用
//                if ( !TextUtils.isEmpty(rt_code) ){
//                    GoToNextActivity();
//                    return;
//                }
//                //
            //發送失敗，將錯誤訊息塞入RequestTag, 方便統一發送完畢後顯示用
            int iSize = m_MealPassenagerList.size();
            if ( m_iSendRequestSeqNum > 0 ){
                //傳送成功以後將該員的Request 封包清掉
                CIMealPassenagerEntity passenager = m_MealPassenagerList.get( iSize - m_iSendRequestSeqNum );
                passenager.strErrorMsg = rt_msg;
            }
            //
            m_iSendRequestSeqNum--;
            if ( m_iSendRequestSeqNum > 0 ){
                SendOrderMealRequest();
            } else {
                hideProgressDialog();
                CheckSendRequestIsSuccess();
            }
        }

        @Override
        public void onDeleteOrderSuccess(String rt_code, String rt_msg) {

            /**發送成功之後將Request封包清空，方便方便哪些乘客已經發送成功*/
            int iSize = m_MealPassenagerList.size();
            if ( m_iSendRequestSeqNum > 0 ){
                //傳送成功以後將該員的訂單編號清掉，代表該園目前沒有餐點訂單
                CIMealPassenagerEntity passenager = m_MealPassenagerList.get( iSize - m_iSendRequestSeqNum );
                passenager.pono_number = "";
            }

            m_iSendRequestSeqNum--;
            //檢查是否有乘客已經有訂單編號, 則需要刪除再進行選餐
            if ( false == CheckAndSendDeleteMealRequest() ){
                //要開始送出選餐封包, 要先將參數歸0,
                m_iSendRequestSeqNum = iSize;
                //沒有人需要先刪除餐點則直接送出選餐
                SendOrderMealRequest();
            }
        }

        @Override
        public void onDeleteOrderError(String rt_code, String rt_msg) {

            //發送失敗，將錯誤訊息塞入RequestTag, 方便統一發送完畢後顯示用
            int iSize = m_MealPassenagerList.size();
            if ( m_iSendRequestSeqNum > 0 ){
                //傳送成功以後將該員的Request 封包清掉
                CIMealPassenagerEntity passenager = m_MealPassenagerList.get( iSize - m_iSendRequestSeqNum );
                passenager.strErrorMsg = rt_msg;
            }
            //
            m_iSendRequestSeqNum--;
            //檢查是否有乘客已經有訂單編號, 則需要刪除再進行選餐
            if ( false == CheckAndSendDeleteMealRequest() ){
                //要開始送出選餐封包, 要先將參數歸0,
                m_iSendRequestSeqNum = iSize;
                //沒有人需要先刪除餐點則直接送出選餐
                SendOrderMealRequest();
            }
        }

        @Override
        public void showProgress() {
            showProgressDialog();
        }

        @Override
        public void hideProgress() {}
    };
}
