package ci.function.MyTrips.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.Core.CIApplication;
import ci.function.Core.SLog;
import ci.function.MyTrips.Detail.CIQuestionnaireActivity;
import ci.ui.WebView.CIWebviewReadmeActivity;
import ci.ui.define.HomePage_Status;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.CILoginInfo;
import ci.ui.view.DashedLine;
import ci.ui.view.ImageHandle;
import ci.ws.Models.CIInquiryAllPassengerByPNRModel;
import ci.ws.Models.entities.BaggageEntity;
import ci.ws.Models.entities.CIBaggageInfoNumEntity;
import ci.ws.Models.entities.CICheckInPax_InfoEntity;
import ci.ws.Models.entities.CIMealInfoEntity;
import ci.ws.Models.entities.CIPassengerListResp_Meal;
import ci.ws.Models.entities.CIPassengerListResp_PaxInfo;
import ci.ws.Models.entities.CIPullQuestionnaireReq;
import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.cores.object.GsonTool;
import ci.ws.define.CICardType;
import ci.ws.define.CIWSHomeStatus_Code;

/**
 * 這是trip detail-乘客卡的Adapter~
 * 乘客卡狀態 請見CIPassengerTabFragment的註解
 * 方法setUiVisibility()是依乘客卡狀態設定各個view是否要顯示/隱藏
 * Created by jlchen on 2016/3/4.
 */
public class CIPassengerRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

//    protected android.os.Handler m_BaseHandler = new android.os.Handler();

    public interface onPassengerRecyclerViewAdapterListener {
        /**
         * 加入會員按鈕
         */
        void JoinMemberOnClick();

        /**
         * 當開放CheckIn時且尚未CheckIn, 可按下CheckIn按鈕
         */
        void CheckInOnClick(CIPassengerListResp_PaxInfo paxInfo);

        /**
         * 當已完成CheckIn時, 可按下登機證按鈕
         */
        void BoardingPassOnClick(CIPassengerListResp_PaxInfo paxInfo);

        /**
         * 當已完成CheckIn時, 可按下取消CheckIn按鈕
         */
        void CancelCheckInOnClick(CIPassengerListResp_PaxInfo paxInfo);

        /**
         * 未選位, 按下選位按鈕
         */
        void SelectSeatOnClick(CIPassengerListResp_PaxInfo paxInfo);

        /**
         * 未選餐, 按下選餐按鈕
         */
        void SelectMealOnClick(CIPassengerListResp_PaxInfo paxInfo);

        /**
         * 已選位, 要變更座位資料
         */
        void EditSeatOnClick(CIPassengerListResp_PaxInfo paxInfo);

        /**
         * 已選餐, 要變更餐點資料
         */
        void EditMealOnClick(CIPassengerListResp_PaxInfo paxInfo);

        /**
         * 已選餐, 要刪除餐點資料
         */
        void DeleteMealOnClick(CIPassengerListResp_PaxInfo paxInfo);

        int NONE = 0;
        int ADD_BAGGAGE = 1;
        int ADD_EXCESS_BAGGAGE = 2;
        int EWALLET_DBBaggage = 3;
        int EWALLET_ExcessBaggage = 4;

        /**
         * 編輯額外行李資料
         * NONE：無
         * ADD_BAGGAGE：樂go
         * ADD_EXCESS_BAGGAGE：加購
         * EWALLET_DBBaggage：樂go電子票卷
         * EWALLET_ExcessBaggage：加購電子票卷
         */
        void EditExtraBagOnClick(int bagType, CIPassengerListResp_PaxInfo passengerItem);

        /**
         * 顯示加購行李電子票卷
         */
        void extraBagEwallet(int bagType, CIPassengerListResp_PaxInfo passengerItem);

        /**
         * 點選的行李追蹤號碼
         * */
        void BaggageTrackingInfo( CIBaggageInfoNumEntity InfoEntity );

        /**
         * 檢視額外服務-wifi
         */
        void ViewWifiOnClick();

        /**
         * 檢視額外服務-高鐵
         */
        void ViewHSROnClick();

        /**
         * 最底下新增乘客的按鈕
         */
        void AddPassengerOnClick();

        /**
         * 補入會員卡號(會員卡號在req物件中會帶入)
         */
        void AddMemberCard(String pnr_id,
                           String pnr_seq,
                           String pax_number);

        /**
         * 改票
         */
        void ManageTicket(CIPassengerListResp_PaxInfo passengerItem);
    }

    private onPassengerRecyclerViewAdapterListener m_Listener;

    public static final int TYPE_ITEM = 2000;
    public static final int TYPE_FOOTER = 2001;

    private ViewScaleDef m_vScaleDef = null;

    private Context m_context = null;
    //    private Fragment            m_fragment = null;
    private ArrayList<CIPassengerListResp_PaxInfo> m_alPassenger = new ArrayList<CIPassengerListResp_PaxInfo>();

    //是否要顯示新增乘客(只有可以線上CheckIn且還未開放CheckIn時,才可以新增乘客)
    private boolean m_bIsShowAddView = false;
    private CITripListResp_Itinerary m_tripData = null;
    private boolean m_bIsOnlineCheckIn = false;
    private int m_iPNRStatus = HomePage_Status.TYPE_UNKNOW;

    //2016-08-10 add for 需要判斷該航班是否可以線上列印電子登機證
    private boolean m_bIsvPass = false;

    //2016-08-10 ryan add for 新增是否可以ancel Check-in 的邏輯
    private ArrayList<CICheckInPax_InfoEntity> m_CPRPaxInfoList = null;

    /**Inquiry 時輸入的FirstName LastName 用來當作是未登入時的本人姓名*/
    private String m_strInquiryFirstName = "";
    private String m_strInquiryLastName  = "";

    public CIPassengerRecyclerViewAdapter(//CIPassengerTabFragment fragment,
                                          Context context,
                                          CITripListResp_Itinerary tripData,
                                          ArrayList<CIPassengerListResp_PaxInfo> arDataList,
                                          onPassengerRecyclerViewAdapterListener listener,
                                          boolean bIsOnlineCheckIn,
                                          boolean bIsShowAddView,
                                          int iPNRStatus,
                                          boolean bIsvPass,
                                          String strFirstName,
                                          String strLastName) {
//        this.m_fragment = fragment;
        this.m_context = context;
        this.m_vScaleDef = ViewScaleDef.getInstance(m_context);

        this.m_tripData = tripData;
        this.m_alPassenger = arDataList;
        this.m_Listener = listener;
        this.m_bIsOnlineCheckIn = bIsOnlineCheckIn;
        this.m_bIsShowAddView = bIsShowAddView;
        this.m_iPNRStatus = iPNRStatus;
        this.m_bIsvPass = bIsvPass;

        this.m_strInquiryFirstName  = strFirstName;
        this.m_strInquiryLastName   = strLastName;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return m_alPassenger == null ? 1 : m_alPassenger.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
//      check what type our position is, based on the assumption that the order is items > footers
        if (null == m_alPassenger)
            return TYPE_FOOTER;

        if (position >= m_alPassenger.size())
            return TYPE_FOOTER;

        return TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//      if our position is one of our items (this comes from getItemViewType(int position) below)
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_view_passenger_card, parent, false);

            return new ItemHolder(view);

//      else we have a header/footer
        } else {
            View viewAdd = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_view_passenger_add, parent, false);

            return new FooterViewHolder(viewAdd);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof FooterViewHolder) {
            //add oru view to a footer view and display it
            prepareFooter((FooterViewHolder) holder);

        } else {
            //it's one of our items, display as required
            prepareItem((ItemHolder) holder, position);

            setUiVisibility((ItemHolder) holder, position);
        }
    }

    public void setCPRPassenagerData(ArrayList<CICheckInPax_InfoEntity> CPRPaxInfoList) {
        this.m_CPRPaxInfoList = CPRPaxInfoList;
    }

    //每個item依照各狀態顯示應該顯示的view
    private void setUiVisibility(ItemHolder itemHolder, final int position) {

        final CIPassengerListResp_PaxInfo passengerItem = m_alPassenger.get(position);
        /** --- 問卷調查 --- */
        final CILoginInfo info = new CILoginInfo(CIApplication.getContext());

        itemHolder.m_btnQues.setEnabled(true);
        itemHolder.m_btnQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToQuestionnaire(passengerItem.Card_Id);
            }
        });

        itemHolder.m_ivQuesInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(m_context, CIWebviewReadmeActivity.class);
                Bitmap bitmap = ImageHandle.getScreenShot((Activity) m_context);
                Bitmap blur = ImageHandle.BlurBuilder(m_context, bitmap, 13.5f, 0.15f);

                Bundle bundle = new Bundle();
                bundle.putParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG, blur);
                bundle.putString(UiMessageDef.BUNDLE_WEBVIEW_URL_TAG, m_context.getString(R.string.rate_this_trip_info_url));
                intent.putExtras(bundle);
                m_context.startActivity(intent);
            }
        });


        /** --- 問卷調查 --- */

        /** --- 補入會員卡號 --- */
        if (TextUtils.isEmpty(passengerItem.Card_Id) && info.isDynastyFlyerMember()) {
            itemHolder.m_rlAddMemberCard.setVisibility(View.VISIBLE);
            itemHolder.m_rlAddMemberCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m_Listener.AddMemberCard(m_tripData.Pnr_Id,
                            m_tripData.Pnr_Seq,
                            passengerItem.Pax_Number);
                }
            });

        } else {
            itemHolder.m_rlAddMemberCard.setVisibility(View.GONE);
        }
        /** --- 補入會員卡號 --- */


        /** --- 乘客姓名 --- */
        String strName = "";
        if (null != passengerItem.First_Name)
            strName = passengerItem.First_Name;
        if (null != passengerItem.Last_Name)
            strName = strName + " " + passengerItem.Last_Name;
        itemHolder.m_tvPassengerName.setText(strName);
        /** --- 乘客姓名 --- */

        /** --- 乘客卡號/卡別 --- */
        if (null == passengerItem.Card_Id)
            passengerItem.Card_Id = "";
        if (null == passengerItem.Card_Type)
            passengerItem.Card_Type = "";

        if (0 < passengerItem.Card_Id.length()) {
            //乘客擁有華航卡號
            itemHolder.m_rlCardAndMiles.setVisibility(View.VISIBLE);
            itemHolder.m_vCardNumGap.setVisibility(View.VISIBLE);
            itemHolder.m_vNameGap.setVisibility(View.VISIBLE);
            //itemHolder.m_vGapCard.setVisibility(View.VISIBLE);
            itemHolder.m_tvCardNumber.setText(passengerItem.Card_Id);

            //依卡別顯示不同的卡圖
            switch (passengerItem.Card_Type) {

                case CICardType.DYNA:
                    itemHolder.m_ivCard.setImageResource(R.drawable.img_gray_card);
                    break;
                case CICardType.EMER:
                    itemHolder.m_ivCard.setImageResource(R.drawable.img_green_card);
                    break;
                case CICardType.GOLD:
                    itemHolder.m_ivCard.setImageResource(R.drawable.img_gold_card);
                    break;
                case CICardType.PARA:
                    itemHolder.m_ivCard.setImageResource(R.drawable.img_blue_card);
                    break;
                //撈不到卡別 就不顯示圖片
                default:
                    itemHolder.m_ivCard.setVisibility(View.INVISIBLE);
                    break;
            }

            //PNR會員卡號與登入會員卡號相同, 顯示里程數
            if (CIApplication.getLoginInfo().GetUserMemberCardNo().equals(passengerItem.Card_Id)) {
                itemHolder.m_tvMiles.setVisibility(View.VISIBLE);
                itemHolder.m_tvMiles.setText(
                        String.format(
                                m_context.getString(R.string.menu_miles),
                                CIApplication.getLoginInfo().GetMiles()));
            } else {
                itemHolder.m_tvMiles.setVisibility(View.GONE);
            }

            //已是華航會員 就不用顯示加入會員按鈕
            //itemHolder.m_rlJoinMember.setVisibility(View.GONE);
            //itemHolder.m_vGapJoin.setVisibility(View.GONE);
        } else {
            //沒有卡號
            itemHolder.m_rlCardAndMiles.setVisibility(View.GONE);
            itemHolder.m_vCardNumGap.setVisibility(View.GONE);
            itemHolder.m_vNameGap.setVisibility(View.GONE);

            //2016.05.30 加入會員欄位改為隱藏不show
            //itemHolder.m_rlJoinMember.setVisibility(View.GONE);
            //itemHolder.m_vGapJoin.setVisibility(View.GONE);
//            itemHolder.m_btnJoinMember.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {}
//            });
//            itemHolder.m_ibtnJoinMember.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {}
//            });
        }
        /** --- 乘客卡號/卡別 --- */
        itemHolder.m_vCardNumGap.setVisibility(View.VISIBLE);
        //
        Boolean bNormalType = true;
        /** --- 是否可以線上check-in --- */
        if (m_bIsOnlineCheckIn) {
            //( m_iPNRStatus == CIWSHomeStatus_Code.TYPE_D_E_CHECKIN || m_iPNRStatus == CIWSHomeStatus_Code.TYPE_D_E_GATE_INFO ) ) {
            //可以線上checkin，隱藏不能online Check-in的訊息
            itemHolder.m_rlNoOnlineCheckIn.setVisibility(View.GONE);

            itemHolder.m_rlQues.setBackgroundResource(
                    R.drawable.bg_main_flight_info_body_radius_gray);

            //調整判斷邏輯, 改使用CPR的資料來判斷
            if (passengerItem.bHaveCPR) {
                //2016-11-22 Mdify by Ryan for 調整邏輯, 登機門時間有可能已經關櫃, 所以要移除是否可以Check-in的判斷, 避免已經Check-in卻又因為關櫃的Flag被誤判
                if ((passengerItem.CPR_Is_Do_Check_In && !passengerItem.CPR_Is_Black && m_iPNRStatus == CIWSHomeStatus_Code.TYPE_D_E_CHECKIN) ||
                        (!passengerItem.CPR_Is_Black && m_iPNRStatus == CIWSHomeStatus_Code.TYPE_D_E_GATE_INFO)) {
                    bNormalType = false;
                    //已經完成Check-in
                    if (passengerItem.CPR_Is_Check_In) {
                        //已經完成Check-in，隱藏Check-in按鈕
                        itemHolder.m_rlCheckIn.setVisibility(View.GONE);
                        //判斷登機證是否該顯示
                        if (m_bIsvPass && !TextUtils.isEmpty(passengerItem.CPR_Boarding_Pass)) {
                            //boarding gate顯示登機證
                            itemHolder.m_rlBoardingPass.setVisibility(View.VISIBLE);
                            itemHolder.m_btnBoardingPass.setVisibility(View.VISIBLE);
                            itemHolder.m_btnBoardingPass.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (null != m_Listener)
                                        m_Listener.BoardingPassOnClick(passengerItem);
                                }
                            });
                        } else {
                            itemHolder.m_btnBoardingPass.setVisibility(View.GONE);
                        }
                        //Cancel Check-in 單獨判斷CPR_Is_Do_Cancel_Check_In, 來決定是否可以取消
                        if (passengerItem.CPR_Is_Do_Cancel_Check_In) {
                            itemHolder.m_llCancelCheckIn.setVisibility(View.VISIBLE);
                            itemHolder.m_btnCancelCheckIn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (null != m_Listener)
                                        m_Listener.CancelCheckInOnClick(passengerItem);
                                }
                            });
                            itemHolder.m_ibtnCancelCheckIn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (null != m_Listener)
                                        m_Listener.CancelCheckInOnClick(passengerItem);
                                }
                            });
                            //如果登機證的按鈕被隱藏，又需要顯示Cancel Check-in 則隱藏Gap避免間隔過大
                            if (itemHolder.m_btnBoardingPass.getVisibility() == View.GONE) {
                                itemHolder.m_vCardNumGap.setVisibility(View.GONE);
                            } else {
                                itemHolder.m_vCardNumGap.setVisibility(View.VISIBLE);
                            }
                        } else {
                            itemHolder.m_llCancelCheckIn.setVisibility(View.INVISIBLE);
                        }
                        //如果登機證的按鈕被隱藏，又需要顯示Cancel Check-in 則隱藏Gap避免間隔過大
                        if (itemHolder.m_btnBoardingPass.getVisibility() == View.GONE && passengerItem.CPR_Is_Do_Cancel_Check_In) {
                            itemHolder.m_vCardNumGap.setVisibility(View.GONE);
                        } else {
                            itemHolder.m_vCardNumGap.setVisibility(View.VISIBLE);
                        }
                    } else {
                        // 2016-11-22 Modify by Ryan
                        // 沒有Check-in的邏輯判斷, 需判斷已經關櫃
                        // 還沒關櫃才可以顯示預辦登機按鈕
                        if (passengerItem.CPR_Is_Do_Check_In) {
                            //check-in顯示預辦登機
                            itemHolder.m_rlCheckIn.setVisibility(View.VISIBLE);
                            itemHolder.m_btnCheckIn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (null != m_Listener)
                                        m_Listener.CheckInOnClick(passengerItem);
                                }
                            });
                            itemHolder.m_rlBoardingPass.setVisibility(View.GONE);
                            itemHolder.m_vCardNumGap.setVisibility(View.VISIBLE);
                        } else {
                            //已經關櫃, 不能Check-in 則顯示一般模式
                            bNormalType = true;
                        }
                    }
                } else {
                    //沒有CPR資料不可顯示預辦登機.登機證畫面
                    bNormalType = true;
                }

            } else {
                bNormalType = true;
                //沒有CPR資料不可顯示預辦登機.登機證畫面
            }

            if (bNormalType) {
                //沒有CPR資料不可顯示預辦登機.登機證畫面
                itemHolder.m_rlBoardingPass.setVisibility(View.GONE);
                itemHolder.m_rlCheckIn.setVisibility(View.GONE);
            }
        } else {

//            if ( false == m_bIsOnlineCheckIn ){
            //不行線上check-in, 顯示提示訊息
            itemHolder.m_rlNoOnlineCheckIn.setVisibility(View.VISIBLE);
//            } else {
//                itemHolder.m_rlNoOnlineCheckIn.setVisibility(View.GONE);
//            }

            //票號欄背景要取消圓角
            itemHolder.m_rlQues.setBackgroundColor(
                    ContextCompat.getColor(m_context, R.color.white_five));

            //不可顯示預辦登機.登機證畫面
            itemHolder.m_rlBoardingPass.setVisibility(View.GONE);
            itemHolder.m_rlCheckIn.setVisibility(View.GONE);
        }

        /** --- 是否可以線上check-in --- */

        /** --- 依狀態號判斷是否可以選位/選餐/加購行李 --- */
        boolean bIsSeat = false;
        boolean bIsSeatClose = false;
        boolean bIsMeal = false;
        boolean bIsMealClose = false;
        boolean bIsBag = false;

        //乘客狀態
        SLog.i("Pax Status_Code",
                passengerItem.First_Name + "" + passengerItem.Last_Name + "乘客狀態:" + passengerItem.Status_Code);

        switch (passengerItem.Status_Code) {
            case CIWSHomeStatus_Code.TYPE_B_HAVE_TICKET:
                //可加購行李
                bIsBag = true;
                break;
            case CIWSHomeStatus_Code.TYPE_C_SEAT_180D_14D:
                //可選位
                bIsSeat = true;
                //可加購行李
                bIsBag = true;
                break;
            case CIWSHomeStatus_Code.TYPE_C_SEAT_MEAL_14D_24H:
                //可選位
                bIsSeat = true;
                //可選餐
                bIsMeal = true;
                //可加購行李
                bIsBag = true;
                break;
            case CIWSHomeStatus_Code.TYPE_D_E_CHECKIN:
                //可選餐
                bIsMeal = true;
                break;
            case CIWSHomeStatus_Code.TYPE_D_E_GATE_INFO:
                //乘客狀態如果已經報到，且是在報到時期(PNR status 3)，就可以選位，此為特殊狀況，報到時的選位
                if (m_iPNRStatus == CIWSHomeStatus_Code.TYPE_D_E_CHECKIN) {
                    //如果已經完成報到, 則要檢查CPR資料確認是否可以換位
                    if (false == passengerItem.bHaveCPR) {
                        bIsSeat = true;
                    } else if (passengerItem.CPR_Is_Change_Seat) {
                        bIsSeat = true;
                    } else {
                        bIsSeat = false;
                    }
                } else {
                    //關閉選位
                    bIsSeatClose = true;
                }
                //可選餐
                bIsMeal = true;
                break;
            case CIWSHomeStatus_Code.TYPE_F_IN_FLIGHT:
            case CIWSHomeStatus_Code.TYPE_G_TRANSITION:
            case CIWSHomeStatus_Code.TYPE_H_ARRIVAL:
                //關閉選位
                bIsSeatClose = true;
                //關閉選餐
                bIsMealClose = true;
                break;
            default:
                break;
        }
        /** --- 依狀態號判斷是否可以選位/選餐/加購行李 --- */

        /** --- 座位欄 --- */
        if (null == m_tripData.Is_Do)
            m_tripData.Is_Do = "";
        if (null == passengerItem.Is_Change_Seat)
            passengerItem.Is_Change_Seat = "";
        if (null == passengerItem.Pax_Type)
            passengerItem.Pax_Type = "";
        if (null == passengerItem.Seat_Number)
            passengerItem.Seat_Number = "";

        if (0 < passengerItem.Seat_Number.length()) {
            //已有座位
            itemHolder.m_rlSeatSelect.setVisibility(View.GONE);
            itemHolder.m_rlSeatData.setVisibility(View.VISIBLE);

            //顯示艙等及座位號碼
            itemHolder.m_tvSeatDataTitle.setText(passengerItem.Class_of_Service_Name);
            itemHolder.m_tvSeatData.setText(passengerItem.Seat_Number);

        } else {
            //空字串 沒有座位
            itemHolder.m_rlSeatSelect.setVisibility(View.VISIBLE);
            itemHolder.m_rlSeatData.setVisibility(View.GONE);
        }

        //當Is_Do=Y & Is_Change_Seat = Y & Pax_Type != IFN & Status_Code為3.4.5時 可以選擇或編輯座位
        if (m_tripData.Is_Do.equals("Y")
                && passengerItem.Is_Change_Seat.equals("Y")
                && !passengerItem.Pax_Type.equals(CIPassengerListResp_PaxInfo.PASSENGER_INFANT)
                && bIsSeat) {
            if (0 < passengerItem.Seat_Number.length()) {
                //顯示編輯按鈕
                setSeatBtnVisibility(itemHolder, itemHolder.m_ibtnSeatEdit);
                itemHolder.m_ibtnSeatEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != m_Listener)
                            m_Listener.EditSeatOnClick(passengerItem);
                    }
                });
            } else {
                //顯示選擇按鈕
                setSeatBtnVisibility(itemHolder, itemHolder.m_btnSeatSelect);
                itemHolder.m_btnSeatSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != m_Listener)
                            m_Listener.SelectSeatOnClick(passengerItem);
                    }
                });
            }
        } else {
//            if ( true == bIsSeatClose ){
//                //pnr狀態為board_gat之後 顯示關閉字串
//                setSeatBtnVisibility(itemHolder, itemHolder.m_tvSeatClose);
//            }else {
            //其他情況顯示無效字串
            setSeatBtnVisibility(itemHolder, itemHolder.m_tvSeatInvalid);
//            }

            //身份為嬰兒 不顯示任何按鈕或文字
            if (passengerItem.Pax_Type.equals(CIPassengerListResp_PaxInfo.PASSENGER_INFANT)) {
                setSeatBtnVisibility(itemHolder, null);
            }
        }
        /** --- 座位欄 --- */

        /** --- 餐點欄 --- */
        if (null == passengerItem.Is_Change_Meal)
            passengerItem.Is_Change_Meal = "";

        //如果Is_Change_Meal為Y時顯示餐點欄 否則要隱藏
        if ("Y".equals(passengerItem.Is_Change_Meal)) {
            itemHolder.m_rlMeal.setVisibility(View.VISIBLE);
            itemHolder.m_rlMealTextLayout.setVisibility(View.VISIBLE);

            //2016-09-27 調整餐點顯示邏輯，調整餐點結構
            //顯示餐點資料
            //ArrayList<CIPassengerListResp_MealDetail> alMeal = new ArrayList<>();
            if (null != passengerItem.Meal && null != passengerItem.Meal.meal_info) {
                itemHolder.m_llMealDataText.removeAllViews();
                if (null == passengerItem.Meal.tkt_confirm_code)
                    passengerItem.Meal.tkt_confirm_code = "";
            } else {
                passengerItem.Meal = new CIPassengerListResp_Meal();
                passengerItem.Meal.tkt_confirm_code = "";
            }

            //2016-09-29 調整餐點欄顯示邏輯
            int iMealInfoSize = passengerItem.Meal.meal_info.size();
            if (iMealInfoSize > 0) {
                //有餐點資訊, 則需顯示出餐點資訊
                itemHolder.m_tvMeal.setVisibility(View.GONE);
                itemHolder.m_llMealDataText.setVisibility(View.VISIBLE);
                //itemHolder.m_rlMealEditLayout.setVisibility(View.VISIBLE);

                LayoutInflater layoutInflater =
                        (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                for (int i = 0; i < iMealInfoSize; i++) {

                    View vMeal = layoutInflater.inflate(R.layout.layout_view_passenger_card_text_item, null);
                    MealHolder mealViewHolder = new MealHolder(vMeal);

                    prepareMeal(mealViewHolder, passengerItem.Meal.meal_info.get(i), i);

                    itemHolder.m_llMealDataText.addView(vMeal);
                }
            } else {
                //未無餐點資訊
                itemHolder.m_tvMeal.setVisibility(View.VISIBLE);
                itemHolder.m_llMealDataText.setVisibility(View.GONE);
            }

            //當Is_Do=Y & Is_Change_Meal=Y & tkt_confirm_code != 空字串 & Status_Code為4時 可以選擇或編輯餐點
            //2016-09-27 增加邏輯，當meal_type = 一般餐點才能進行選餐, 特殊餐不能選
            if (m_tripData.Is_Do.equals("Y")
                    && 0 < passengerItem.Meal.tkt_confirm_code.length()
                    && bIsMeal
                    && TextUtils.equals(passengerItem.Meal.meal_type, CIPassengerListResp_Meal.MEAL_TYPE_NORMAL)) {

                //如果有餐點資訊, 而且有訂單編號, 代表已經有選餐點, 則要顯示編輯按鈕
                if (iMealInfoSize > 0 && !TextUtils.isEmpty(passengerItem.Meal.pono_number)) {
                    //顯示編輯區
                    itemHolder.m_rlMealEditLayout.setVisibility(View.VISIBLE);
                    //顯示編輯按鈕
                    itemHolder.m_ibtnMealEdit.setVisibility(View.VISIBLE);
                    itemHolder.m_ibtnMealEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != m_Listener)
                                m_Listener.EditMealOnClick(passengerItem);
                        }
                    });

                    //顯示刪除按鈕
                    itemHolder.m_ibtnMealGarbage.setVisibility(View.VISIBLE);
                    itemHolder.m_ibtnMealGarbage.setTag(position);
                    itemHolder.m_ibtnMealGarbage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != m_Listener)
                                m_Listener.DeleteMealOnClick(passengerItem);
                        }
                    });
                    //隱藏選擇按鈕. 隱藏未開放標示
                    itemHolder.m_btnMealSelect.setVisibility(View.GONE);
                    itemHolder.m_tvMealInvalid.setVisibility(View.GONE);
                } else {
                    //顯示選餐按鈕
                    setMealBtnVisibility(itemHolder, itemHolder.m_btnMealSelect);
                    itemHolder.m_btnMealSelect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != m_Listener)
                                m_Listener.SelectMealOnClick(passengerItem);
                        }
                    });
                    //隱藏編輯區按鈕
                    itemHolder.m_rlMealEditLayout.setVisibility(View.GONE);
                    itemHolder.m_tvMealInvalid.setVisibility(View.GONE);
                }
            } else {
                itemHolder.m_rlMealEditLayout.setVisibility(View.GONE);
                //2016-07-12 與iOS統一邏輯: 只要是不能選餐, 一律顯示未開放
                setMealBtnVisibility(itemHolder, itemHolder.m_tvMealInvalid);
            }
        } else {
            //隱藏餐點欄
            itemHolder.m_rlMeal.setVisibility(View.GONE);
        }
        /** --- 餐點欄 --- */

        /** --- 行李欄 --- */
        /**
         * 不能[樂go] & 不能[加購]
         * 左邊顯示 免費行李，右邊不出button
         * 能[樂go] & 不能[加購]
         * 左邊顯示 免費行李 右邊出 樂go button 點擊開網頁
         * 能[加購] & 不能[樂go]
         * 左邊顯示 免費行李 右邊出 [加購] button 點擊進 eb 加購流程
         * 若哪天系統異常，能[樂go] 也能[加購]，以加購為第一優先判斷 出[加購] 的button

         * 已完成[樂購]
         * 左邊顯示 免費行李跟樂go行李  右邊顯示電子票券，點擊顯示 在乘客頁回傳的樂go 資訊
         * 已完成[加購]
         * 左邊顯示 免費行李跟加購行李  右邊顯示電子票券，點擊顯示 在乘客頁回傳的加購 資訊
         * 同上哪天系統異常 有[樂go] 也有[加購] 以[加購]為第一優先判斷 出[加購] 的電子票券
         */
        if (null == passengerItem.Is_Add_Baggage)
            passengerItem.Is_Add_Baggage = "";
        if (null == passengerItem.Baggage_Allowence)
            passengerItem.Baggage_Allowence = "";
        if (null == passengerItem.Is_Add_ExcessBaggage)
            passengerItem.Is_Add_ExcessBaggage = "";


        //免費行李
        itemHolder.m_tvFreeBagTitle.setText(m_context.getString(R.string.trip_detail_passenger_free_bag_title));
        itemHolder.m_tvFreeBagValue.setText(passengerItem.Baggage_Allowence);


        final String YES = "Y";
        boolean addBag = false;

        //初始化
        setBagBtn(itemHolder, onPassengerRecyclerViewAdapterListener.NONE, null);


        if (null != passengerItem.ExcessBaggage) {
            //加購行李數量
            itemHolder.m_llBuyBag.setVisibility(View.VISIBLE);
            itemHolder.m_tvBuybagTitle.setText(m_context.getString(R.string.trip_detail_passenger_add_excess_bag_title));
            itemHolder.m_tvBuybagValue.setText(getEbReadableInfo(passengerItem.ExcessBaggage));
            setBagBtn(itemHolder, onPassengerRecyclerViewAdapterListener.EWALLET_ExcessBaggage, passengerItem);
        } else if (null != passengerItem.DBBaggage) {
            //樂go行李數量
            itemHolder.m_llBuyBag.setVisibility(View.VISIBLE);
            itemHolder.m_tvBuybagTitle.setText(m_context.getString(R.string.extra_service_title_extra_baggage));
            itemHolder.m_tvBuybagValue.setText(getEbReadableInfo(passengerItem.DBBaggage));
            setBagBtn(itemHolder, onPassengerRecyclerViewAdapterListener.EWALLET_DBBaggage, passengerItem);
        } else {
            itemHolder.m_llBuyBag.setVisibility(View.GONE);
            addBag = true;
        }


        //addBag == true 就是沒有加購或樂go行李數量，bIsBag == true 就是 Status_Code為4.5.6時 可以加購行李
        if (bIsBag && addBag) {
            //加購行李
            if (passengerItem.Is_Add_ExcessBaggage.toUpperCase().equals(YES)) {
                //加購行李
                // TODO: 2017/11/15 暫時隱藏加購功能
//                setBagBtn(itemHolder, onPassengerRecyclerViewAdapterListener.ADD_EXCESS_BAGGAGE, passengerItem);
            } else if (passengerItem.Is_Add_Baggage.toUpperCase().equals(YES)) {
                //樂go行李
                setBagBtn(itemHolder, onPassengerRecyclerViewAdapterListener.ADD_BAGGAGE, null);
            }
        }
        /** --- 行李欄 --- */

        /** --- 行李追蹤 --- */
        /**
         * 會員登入
         * 非會員本人的牌卡不顯示行李追蹤牌卡
         * 非會員登入
         * FindMyBooking時查詢的Name與牌卡Name不相同時，是為非本人，則不顯示行李追蹤牌卡
         * */
        boolean bIsMySelf = CheckIsMyPassengerCard(passengerItem.First_Name, passengerItem.Last_Name);
        //
//        passengerItem.BaggageInfoNum = new ArrayList<>();
//        CIBaggageInfoNumEntity BagggageEntity = new CIBaggageInfoNumEntity();
//        BagggageEntity.Baggage_showNumber = "CI 123456";
//        BagggageEntity.Baggage_BarcodeNumber = "";
//        passengerItem.BaggageInfoNum.add(BagggageEntity);
//        CIBaggageInfoNumEntity BagggageEntity1 = new CIBaggageInfoNumEntity();
//        BagggageEntity1.Baggage_showNumber = "CI xxxxxx";
//        BagggageEntity1.Baggage_BarcodeNumber = "";
//        passengerItem.BaggageInfoNum.add(BagggageEntity1);
        //
        if (null == passengerItem.BaggageInfoNum || passengerItem.BaggageInfoNum.size() <= 0 || !bIsMySelf ) {
            itemHolder.m_rlBagTracking.setVisibility(View.GONE);
        } else {
            itemHolder.m_rlBagTracking.setVisibility(View.VISIBLE);

            LayoutInflater layoutInflater =
                    (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            boolean bHasBaggage = false;
            for (CIBaggageInfoNumEntity entity : passengerItem.BaggageInfoNum) {
                if (!TextUtils.isEmpty(entity.Baggage_ShowNumber)) {
                    bHasBaggage = true;

                    View vBaggage = layoutInflater.inflate(R.layout.layout_view_passenger_card_baggage_tracking_item, null);

                    BaggageTrackHolder bagTackerHolder = new BaggageTrackHolder(vBaggage);
                    prepareBaggTrack(bagTackerHolder, entity);

                    itemHolder.m_llBagTracking.addView(vBaggage, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, m_vScaleDef.getLayoutHeight(60)));
                }
            }
            if (!bHasBaggage) {
                itemHolder.m_rlBagTracking.setVisibility(View.GONE);
            } else {
                itemHolder.m_rlBagTracking.setVisibility(View.VISIBLE);
            }
        }
        /** --- 行李追蹤 --- */

        /** --- 貴賓室欄 --- */
        if (null == passengerItem.Vip_Lounge)
            passengerItem.Vip_Lounge = "";
        //是否擁有貴賓休息室
        if (passengerItem.Vip_Lounge.equals("Y")) {
            itemHolder.m_rlVip.setVisibility(View.VISIBLE);
        } else {
            itemHolder.m_rlVip.setVisibility(View.GONE);
        }
        /** --- 貴賓室欄 --- */

        //是否有額外服務-預設先隱藏
        itemHolder.m_rlExtraService.setVisibility(View.GONE);

        //顯示機票號碼
        if (null == passengerItem.Ticket)
            passengerItem.Ticket = "";
        itemHolder.m_tvTicketNumberData.setText(passengerItem.Ticket);
        //新增改票的按鈕
        itemHolder.m_btnModiflyTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                if (null != m_Listener) {
                    m_Listener.ManageTicket(passengerItem);
                }
            }
        });
    }

    private String getEbReadableInfo(BaggageEntity data) {
        String unit;
        unit = CIInquiryAllPassengerByPNRModel.getBagUnit(data.ssrtype);
        String ssramount = data.ssramount;
        ssramount = CIInquiryAllPassengerByPNRModel.getBagAmount(ssramount);
        return ssramount + unit;
    }

    private void changeToQuestionnaire(String cardid) {
        Intent intent = new Intent(m_context, CIQuestionnaireActivity.class);
        CIPullQuestionnaireReq req = new CIPullQuestionnaireReq();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        /**起飛地 TPE*/
        req.departure = m_tripData.Departure_Station;
        /**起飛日期 2017-05-19*/
        req.departure_date = m_tripData.Departure_Date;
        /**抵達地 LAX*/
        req.arrival = m_tripData.Arrival_Station;
        /**飛機編號 CI0008*/
        req.fltnumber = m_tripData.Airlines + m_tripData.Flight_Number;
        /**ABC123*/
        req.PNR = m_tripData.Pnr_Id;
        /**會員編號 WE0401308*/
        req.cardid = cardid;

        String strJson = GsonTool.toJson(req);
        Bitmap bitmap = ImageHandle.getScreenShot((Activity) m_context);
        Bitmap blur = ImageHandle.BlurBuilder(m_context, bitmap, 13.5f, 0.15f);
        Bundle bundle = new Bundle();
        bundle.putString(UiMessageDef.BUNDLE_PULL_QUES_REQ_DATA, strJson);
        bundle.putParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG, blur);
        intent.putExtras(bundle);
        m_context.startActivity(intent);
    }

    //是否顯示座位按鈕
    private void setSeatBtnVisibility(ItemHolder itemHolder, View v) {
        itemHolder.m_btnSeatSelect.setVisibility(View.GONE);
        itemHolder.m_ibtnSeatEdit.setVisibility(View.GONE);
        itemHolder.m_tvSeatInvalid.setVisibility(View.GONE);
        itemHolder.m_tvSeatClose.setVisibility(View.GONE);

        if (null != v)
            v.setVisibility(View.VISIBLE);
    }

    //是否顯示餐點按鈕
    private void setMealBtnVisibility(ItemHolder itemHolder, View v) {
        itemHolder.m_btnMealSelect.setVisibility(View.GONE);
        itemHolder.m_ibtnMealEdit.setVisibility(View.GONE);
        itemHolder.m_ibtnMealGarbage.setVisibility(View.GONE);
        itemHolder.m_tvMealInvalid.setVisibility(View.GONE);
//        itemHolder.m_tvMealClose.setVisibility(View.GONE);

        if (null != v)
            v.setVisibility(View.VISIBLE);
    }

    //是否顯示行李按鈕
    private void setBagBtn(ItemHolder itemHolder,
                           final int bagType,
                           final CIPassengerListResp_PaxInfo passengerItem) {

        //TODO 以下兩個view已經不使用，保持GONE，暫時保留 by Kevin
        itemHolder.m_tvBagInvalid.setVisibility(View.GONE);
        itemHolder.m_tvBagClose.setVisibility(View.GONE);

        if (bagType == onPassengerRecyclerViewAdapterListener.ADD_BAGGAGE) {
            setAddBagBtnVisibleAndText(itemHolder, View.VISIBLE, m_context.getString(R.string.trip_detail_passenger_add_bag_btn));
            setBagEwalletBtn(itemHolder, View.GONE);
        } else if (bagType == onPassengerRecyclerViewAdapterListener.ADD_EXCESS_BAGGAGE) {
            setAddBagBtnVisibleAndText(itemHolder, View.VISIBLE, m_context.getString(R.string.trip_detail_passenger_add_excess_bag_btn));
            setBagEwalletBtn(itemHolder, View.GONE);
        } else if (bagType == onPassengerRecyclerViewAdapterListener.EWALLET_DBBaggage
                || bagType == onPassengerRecyclerViewAdapterListener.EWALLET_ExcessBaggage) {
            setAddBagBtnVisibleAndText(itemHolder, View.GONE, "");
            setBagEwalletBtn(itemHolder, View.VISIBLE);
        } else {
            setAddBagBtnVisibleAndText(itemHolder, View.GONE, "");
            setBagEwalletBtn(itemHolder, View.GONE);
        }

        itemHolder.m_btnBagEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != m_Listener)
                    m_Listener.EditExtraBagOnClick(bagType, passengerItem);
            }
        });

        itemHolder.m_btnBagEwallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != m_Listener)
                    m_Listener.extraBagEwallet(bagType, passengerItem);
            }
        });
    }

    private void setAddBagBtnVisibleAndText(ItemHolder itemHolder, int visible, String text) {
        itemHolder.m_btnBagEdit.setVisibility(visible);
        itemHolder.m_btnBagEdit.setText(text);
    }

    private void setBagEwalletBtn(ItemHolder itemHolder, int visible) {
        itemHolder.m_btnBagEwallet.setVisibility(visible);
    }

    private static class ItemHolder extends RecyclerView.ViewHolder {
        //        RelativeLayout m_rlPassengerName = null,
        RelativeLayout m_rlJoinMember = null,
                m_rlCardAndMiles = null,
                m_rlCheckIn = null,
                m_rlBoardingPass = null,
                m_rlSeat = null,
                m_rlSeatSelect = null,
                m_rlSeatData = null,
                m_rlMeal = null,
                m_rlMealTextLayout = null,
                m_rlMealEditLayout = null,
                m_rlBagTracking = null,
                m_rlVip = null,
                m_rlExtraService = null,
                m_rlTicketNumber = null,
                m_rlNoOnlineCheckIn = null,
                m_rlAddMemberCard = null,
                m_rlQues = null;

        LinearLayout m_llCancelCheckIn = null,
                m_llPassengerInfo = null,
        //                m_llSeatText = null,
        m_llSeatData = null,
        //                m_llMealText = null,
        m_llMealDataText = null,
                m_llExtraService = null,
                m_llBag = null,
                m_llBagTracking = null,
                m_llBuyBag = null,
                m_llPassengerName = null;

        TextView m_tvPassengerName = null,
                m_tvCardNumber = null,
                m_tvMiles = null,
                m_tvSeat = null,
                m_tvSeatDataTitle = null,
                m_tvSeatData = null,
                m_tvSeatClose = null,
                m_tvSeatInvalid = null,
                m_tvMeal = null,
        //                m_tvMealClose = null,
        m_tvMealInvalid = null,
                m_tvBuybagValue = null,
                m_tvBuybagTitle = null,
                m_tvFreeBagValue = null,
                m_tvFreeBagTitle = null,
                m_tvBagClose = null,
                m_tvBagInvalid = null,
                m_tvVip = null,
                m_tvExtraService = null,
                m_tvHSR = null,
                m_tvWifi = null,
                m_tvTicketNumTitle = null,
                m_tvTicketNumberData = null,
                m_tvNoOnlineCheckIn = null,
                m_tvAddMemberCard = null,
                m_tvQues = null;

        Button m_btnJoinMember = null,
                m_btnCheckIn = null,
                m_btnBoardingPass = null,
                m_btnCancelCheckIn = null,
                m_btnSeatSelect = null,
                m_btnMealSelect = null,
                m_btnHSR = null,
                m_btnQues = null,
                m_btnWifi = null,
                m_btnBagEdit = null,
                m_btnBagEwallet = null,
                m_btnModiflyTicket = null;

        ImageButton m_ibtnJoinMember = null,
                m_ibtnCancelCheckIn = null,
                m_ibtnSeatEdit = null,
                m_ibtnMealEdit = null,
                m_ibtnMealGarbage = null;

        ImageView m_ivCard = null,
                m_ivSeat = null,
                m_ivMeal = null,
                m_ivBag = null,
                m_ivBagTracking = null,
                m_ivVip = null,
                m_ivAddMemberCard = null,
                m_ivExtraService = null,
                m_ivQues = null,
                m_ivQuesInfo = null;

        View m_vNameGap = null,
                m_vCardNumGap = null,
                m_vCheckInMargin = null,
                m_vMealLine = null,
                m_vBagLine = null,
                m_vBagTrackingLine = null,
                m_vVipLine = null,
                m_vExtraServiceLine = null,
                m_vTicketNumberLine = null,
                m_vLine = null,
                m_vQues = null;

        DashedLine m_dlHSR = null,
                m_dlWifi = null;

        public ItemHolder(View convertView) {
            super(convertView);

            m_llPassengerName = (LinearLayout) convertView.findViewById(R.id.ll_passenger_name);
            m_rlJoinMember = (RelativeLayout) convertView.findViewById(R.id.rl_join_member);
            m_rlCardAndMiles = (RelativeLayout) convertView.findViewById(R.id.rl_card_and_miles);
            m_rlCheckIn = (RelativeLayout) convertView.findViewById(R.id.rl_check_in);
            m_rlBoardingPass = (RelativeLayout) convertView.findViewById(R.id.rl_boarding_pass);
            m_rlSeat = (RelativeLayout) convertView.findViewById(R.id.rl_seat);
            m_rlSeatSelect = (RelativeLayout) convertView.findViewById(R.id.rl_select_seat);
            m_rlSeatData = (RelativeLayout) convertView.findViewById(R.id.rl_seat_data);
            m_rlQues = (RelativeLayout) convertView.findViewById(R.id.rl_ques);
            //2016-09-29 調整餐點資訊結構
            m_rlMeal = (RelativeLayout) convertView.findViewById(R.id.rl_meal);
            m_rlMealTextLayout = (RelativeLayout) convertView.findViewById(R.id.rl_meal_text);
            m_tvMeal = (TextView) convertView.findViewById(R.id.tv_meal);
            m_llMealDataText = (LinearLayout) convertView.findViewById(R.id.ll_meal_data_text);
            m_rlBagTracking = (RelativeLayout) convertView.findViewById(R.id.rl_bag_tracking);

            m_btnMealSelect = (Button) convertView.findViewById(R.id.btn_meal_select);
            m_rlMealEditLayout = (RelativeLayout) convertView.findViewById(R.id.rl_meal_edit);
            m_ibtnMealEdit = (ImageButton) convertView.findViewById(R.id.ibtn_meal_edit);
            m_ibtnMealGarbage = (ImageButton) convertView.findViewById(R.id.ibtn_meal_garbage);
            m_tvMealInvalid = (TextView) convertView.findViewById(R.id.tv_meal_invalid);

            m_rlVip = (RelativeLayout) convertView.findViewById(R.id.rl_vip);
            m_rlExtraService = (RelativeLayout) convertView.findViewById(R.id.rl_extra_service);
            m_rlTicketNumber = (RelativeLayout) convertView.findViewById(R.id.rl_ticket_number);
            m_rlNoOnlineCheckIn = (RelativeLayout) convertView.findViewById(R.id.rl_no_online_check_in);
            m_rlAddMemberCard = (RelativeLayout) convertView.findViewById(R.id.rl_add_member_card);

            m_llCancelCheckIn = (LinearLayout) convertView.findViewById(R.id.ll_cancel_check_in);
            m_llPassengerInfo = (LinearLayout) convertView.findViewById(R.id.ll_passenger_info);
            m_llSeatData = (LinearLayout) convertView.findViewById(R.id.ll_seat_data);

            m_llExtraService = (LinearLayout) convertView.findViewById(R.id.ll_extra_service);
            m_llBag = (LinearLayout) convertView.findViewById(R.id.ll_bag);
            m_llBuyBag = (LinearLayout) convertView.findViewById(R.id.ll_buybag);
            m_llBagTracking = (LinearLayout) convertView.findViewById(R.id.ll_bag_tracking);

            m_tvPassengerName = (TextView) convertView.findViewById(R.id.tv_passenger_name);
            m_tvCardNumber = (TextView) convertView.findViewById(R.id.tv_card_number);
            m_tvMiles = (TextView) convertView.findViewById(R.id.tv_miles);
            m_tvSeat = (TextView) convertView.findViewById(R.id.tv_seat);
            m_tvSeatDataTitle = (TextView) convertView.findViewById(R.id.tv_seat_data_title);
            m_tvSeatData = (TextView) convertView.findViewById(R.id.tv_seat_data);
            m_tvSeatClose = (TextView) convertView.findViewById(R.id.tv_seat_close);
            m_tvSeatInvalid = (TextView) convertView.findViewById(R.id.tv_seat_invalid);

            m_tvFreeBagValue = (TextView) convertView.findViewById(R.id.tv_freebag_value);
            m_tvFreeBagTitle = (TextView) convertView.findViewById(R.id.tv_freebag_title);
            m_tvBuybagValue = (TextView) convertView.findViewById(R.id.tv_buybag_value);
            m_tvBuybagTitle = (TextView) convertView.findViewById(R.id.tv_buybag_title);
            m_tvBagClose = (TextView) convertView.findViewById(R.id.tv_bag_close);
            m_tvBagInvalid = (TextView) convertView.findViewById(R.id.tv_bag_invalid);
            m_tvVip = (TextView) convertView.findViewById(R.id.tv_vip);
            m_tvExtraService = (TextView) convertView.findViewById(R.id.tv_extra_service);
            m_tvHSR = (TextView) convertView.findViewById(R.id.tv_hsr);
            m_tvWifi = (TextView) convertView.findViewById(R.id.tv_wifi);
            m_tvTicketNumTitle = (TextView) convertView.findViewById(R.id.tv_ticket_text);
            m_tvTicketNumberData = (TextView) convertView.findViewById(R.id.tv_ticket_number_data);
            m_tvNoOnlineCheckIn = (TextView) convertView.findViewById(R.id.tv_no_online_check_in);
            m_tvQues = (TextView) convertView.findViewById(R.id.tv_ques);
            m_tvAddMemberCard = (TextView) convertView.findViewById(R.id.tv_add_member_card);

            m_btnJoinMember = (Button) convertView.findViewById(R.id.btn_join_member);
            m_btnCheckIn = (Button) convertView.findViewById(R.id.btn_check_in);
            m_btnBoardingPass = (Button) convertView.findViewById(R.id.btn_boarding_pass);
            m_btnCancelCheckIn = (Button) convertView.findViewById(R.id.btn_cancel_check_in);
            m_btnSeatSelect = (Button) convertView.findViewById(R.id.btn_seat_select);
            m_btnHSR = (Button) convertView.findViewById(R.id.btn_hsr);
            m_btnWifi = (Button) convertView.findViewById(R.id.btn_wifi);
            m_btnQues = (Button) convertView.findViewById(R.id.btn_ques);
            m_btnBagEdit = (Button) convertView.findViewById(R.id.btn_bag_edit);
            m_btnBagEwallet = (Button) convertView.findViewById(R.id.btn_bag_ewallet);
            m_btnModiflyTicket = (Button) convertView.findViewById(R.id.btn_ticket_modifly);

            m_ibtnJoinMember = (ImageButton) convertView.findViewById(R.id.ibtn_join_member);
            m_ibtnCancelCheckIn = (ImageButton) convertView.findViewById(R.id.ibtn_cancel_check_in);
            m_ibtnSeatEdit = (ImageButton) convertView.findViewById(R.id.ibtn_seat_edit);

            m_ivCard = (ImageView) convertView.findViewById(R.id.iv_card);
            m_ivSeat = (ImageView) convertView.findViewById(R.id.iv_seat);
            m_ivMeal = (ImageView) convertView.findViewById(R.id.iv_meal);
            m_ivBag = (ImageView) convertView.findViewById(R.id.iv_bag);
            m_ivBagTracking = (ImageView) convertView.findViewById(R.id.iv_bag_tracking);
            m_ivVip = (ImageView) convertView.findViewById(R.id.iv_vip);
            m_ivExtraService = (ImageView) convertView.findViewById(R.id.iv_extra_service);
            m_ivQues = (ImageView) convertView.findViewById(R.id.iv_ques);
            m_ivQuesInfo = (ImageView) convertView.findViewById(R.id.iv_ques_info);
            m_ivAddMemberCard = (ImageView) convertView.findViewById(R.id.iv_add_member_card);

            m_vNameGap = (View) convertView.findViewById(R.id.v_gap_name_card);
            m_vCardNumGap = (View) convertView.findViewById(R.id.v_gap_card_number);
            m_vCheckInMargin = (View) convertView.findViewById(R.id.v_check_in_margin);
            m_vMealLine = (View) convertView.findViewById(R.id.vline_meal);
            m_vBagLine = (View) convertView.findViewById(R.id.vline_bag);
            m_vBagTrackingLine = (View) convertView.findViewById(R.id.vline_bag_tracking);
            m_vVipLine = (View) convertView.findViewById(R.id.vline_vip);
            m_vExtraServiceLine = (View) convertView.findViewById(R.id.vline_extra_service);
            m_vTicketNumberLine = (View) convertView.findViewById(R.id.vline_ticket_number);
            m_vLine = (View) convertView.findViewById(R.id.vDiv);
            m_vQues = (View) convertView.findViewById(R.id.vline_ques);

            m_dlHSR = (DashedLine) convertView.findViewById(R.id.dl_hsr);
            m_dlWifi = (DashedLine) convertView.findViewById(R.id.dl_wifi);
        }
    }

    //item自適應
    private void prepareItem(ItemHolder itemHolder, int position) {

        //分隔線高
        int iDivHeight = m_vScaleDef.getLayoutHeight(1);
        if (0 >= iDivHeight) {
            iDivHeight = 1;
        }

        //乘客姓名
        m_vScaleDef.setPadding(itemHolder.m_llPassengerName, 20, 20, 20, 0);

        m_vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_20, itemHolder.m_tvPassengerName);
        LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) itemHolder.m_tvPassengerName.getLayoutParams();
        lParams.width = m_vScaleDef.getLayoutWidth(300);
        lParams.height = m_vScaleDef.getLayoutHeight(24);

//        //加入會員
//        rParams = (RelativeLayout.LayoutParams) itemHolder.m_rlJoinMember.getLayoutParams();
//        rParams.topMargin = m_vScaleDef.getLayoutHeight(20.7);
//
//        rParams = (RelativeLayout.LayoutParams) itemHolder.m_btnJoinMember.getLayoutParams();
//        rParams.height = m_vScaleDef.getLayoutHeight(16);
//        m_vScaleDef.setTextSize(13, itemHolder.m_btnJoinMember);
//
//        rParams = (RelativeLayout.LayoutParams) itemHolder.m_ibtnJoinMember.getLayoutParams();
//        rParams.height = m_vScaleDef.getLayoutMinUnit(16);
//        rParams.width = m_vScaleDef.getLayoutMinUnit(16);
//        rParams.leftMargin = m_vScaleDef.getLayoutWidth(3);

        lParams = (LinearLayout.LayoutParams) itemHolder.m_vNameGap.getLayoutParams();
        lParams.height = m_vScaleDef.getLayoutHeight(20);

        //m_vScaleDef.setMargins(itemHolder.m_rlCardAndMiles, 0, 20, 0, 0);
        //會員卡號&里程數
        RelativeLayout.LayoutParams rParams = (RelativeLayout.LayoutParams) itemHolder.m_ivCard.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutMinUnit(18);
        rParams.width = m_vScaleDef.getLayoutMinUnit(26.7);

        m_vScaleDef.setTextSize(13, itemHolder.m_tvCardNumber);
        rParams = (RelativeLayout.LayoutParams) itemHolder.m_tvCardNumber.getLayoutParams();
        rParams.width = m_vScaleDef.getLayoutWidth(90);
        rParams.height = m_vScaleDef.getLayoutHeight(16);
        rParams.leftMargin = m_vScaleDef.getLayoutWidth(6.7);
        rParams.rightMargin = m_vScaleDef.getLayoutWidth(16.7);

        m_vScaleDef.setTextSize(13, itemHolder.m_tvMiles);
        rParams = (RelativeLayout.LayoutParams) itemHolder.m_tvMiles.getLayoutParams();
        rParams.width = m_vScaleDef.getLayoutWidth(160);
        rParams.height = m_vScaleDef.getLayoutHeight(16);

        lParams = (LinearLayout.LayoutParams) itemHolder.m_vCardNumGap.getLayoutParams();
        lParams.height = m_vScaleDef.getLayoutHeight(20);

        //預辦登機
        m_vScaleDef.setMargins(itemHolder.m_rlCheckIn, 0, 0, 0, 0);

        m_vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_16, itemHolder.m_btnCheckIn);
        rParams = (RelativeLayout.LayoutParams) itemHolder.m_btnCheckIn.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutHeight(40);

        rParams = (RelativeLayout.LayoutParams) itemHolder.m_vCheckInMargin.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutHeight(36);

        //登機證
        m_vScaleDef.setMargins(itemHolder.m_rlBoardingPass, 0, 0, 0, 0);

        m_vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_16, itemHolder.m_btnBoardingPass);
        rParams = (RelativeLayout.LayoutParams) itemHolder.m_btnBoardingPass.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutHeight(40);


        lParams = (LinearLayout.LayoutParams) itemHolder.m_rlAddMemberCard.getLayoutParams();
        lParams.height = m_vScaleDef.getLayoutHeight(40);
        lParams.bottomMargin = m_vScaleDef.getLayoutHeight(20);

        m_vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_16, itemHolder.m_tvAddMemberCard);
        m_vScaleDef.selfAdjustSameScaleView(itemHolder.m_ivAddMemberCard, 16, 16);

        //取消登機
        rParams = (RelativeLayout.LayoutParams) itemHolder.m_llCancelCheckIn.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutHeight(36);

        m_vScaleDef.setTextSize(14, itemHolder.m_btnCancelCheckIn);
        lParams = (LinearLayout.LayoutParams) itemHolder.m_btnCancelCheckIn.getLayoutParams();
        lParams.height = m_vScaleDef.getLayoutHeight(17);

        lParams = (LinearLayout.LayoutParams) itemHolder.m_ibtnCancelCheckIn.getLayoutParams();
        lParams.leftMargin = m_vScaleDef.getLayoutWidth(6);
        lParams.height = m_vScaleDef.getLayoutMinUnit(16);
        lParams.width = m_vScaleDef.getLayoutMinUnit(16);

        //乘客搭機資訊
        m_vScaleDef.setPadding(itemHolder.m_llPassengerInfo, 20, 0, 20, 0);

        //座位
        lParams = (LinearLayout.LayoutParams) itemHolder.m_rlSeat.getLayoutParams();
        lParams.height = m_vScaleDef.getLayoutHeight(60.7);

        rParams = (RelativeLayout.LayoutParams) itemHolder.m_ivSeat.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutMinUnit(24);
        rParams.width = m_vScaleDef.getLayoutMinUnit(24);

        //移除多餘ayout
//        rParams = (RelativeLayout.LayoutParams) itemHolder.m_llSeatText.getLayoutParams();
//        rParams.width = m_vScaleDef.getLayoutWidth(158);
//        rParams.leftMargin = m_vScaleDef.getLayoutWidth(18);
        //未選位
        m_vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_16, itemHolder.m_tvSeat);
        rParams = (RelativeLayout.LayoutParams) itemHolder.m_tvSeat.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutHeight(60);
        rParams.width = m_vScaleDef.getLayoutWidth(158);
        rParams.leftMargin = m_vScaleDef.getLayoutWidth(18);

        m_vScaleDef.setTextSize(13, itemHolder.m_btnSeatSelect);
        rParams = (RelativeLayout.LayoutParams) itemHolder.m_btnSeatSelect.getLayoutParams();
        rParams.width = m_vScaleDef.getLayoutWidth(80);
        rParams.height = m_vScaleDef.getLayoutHeight(32);

        //已選位
        rParams = (RelativeLayout.LayoutParams) itemHolder.m_llSeatData.getLayoutParams();
        rParams.width = m_vScaleDef.getLayoutWidth(158);
        rParams.leftMargin = m_vScaleDef.getLayoutWidth(18);

        m_vScaleDef.setTextSize(13, itemHolder.m_tvSeatDataTitle);
        lParams = (LinearLayout.LayoutParams) itemHolder.m_tvSeatDataTitle.getLayoutParams();
        lParams.height = m_vScaleDef.getLayoutHeight(15.7);

        m_vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_16, itemHolder.m_tvSeatData);
        lParams = (LinearLayout.LayoutParams) itemHolder.m_tvSeatData.getLayoutParams();
        lParams.height = m_vScaleDef.getLayoutHeight(19.3);
        lParams.topMargin = m_vScaleDef.getLayoutHeight(1);

        rParams = (RelativeLayout.LayoutParams) itemHolder.m_ibtnSeatEdit.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutMinUnit(24);
        rParams.width = m_vScaleDef.getLayoutMinUnit(24);

        m_vScaleDef.setTextSize(13, itemHolder.m_tvSeatClose);

        m_vScaleDef.setTextSize(13, itemHolder.m_tvSeatInvalid);

        //餐點
        itemHolder.m_rlMeal.setMinimumHeight(m_vScaleDef.getLayoutHeight(60));

        m_vScaleDef.setViewSize(itemHolder.m_rlQues, ViewGroup.LayoutParams.MATCH_PARENT, 50);

        m_vScaleDef.setViewSize(itemHolder.m_btnQues, 80, 32);
        m_vScaleDef.setTextSize(13, itemHolder.m_btnQues);
        rParams = (RelativeLayout.LayoutParams) itemHolder.m_btnQues.getLayoutParams();
        rParams.rightMargin = m_vScaleDef.getLayoutWidth(20);

        itemHolder.m_vMealLine.getLayoutParams().height = iDivHeight;

        rParams = (RelativeLayout.LayoutParams) itemHolder.m_ivMeal.getLayoutParams();
        rParams.topMargin = m_vScaleDef.getLayoutHeight(18);
        rParams.height = m_vScaleDef.getLayoutMinUnit(24);
        rParams.width = m_vScaleDef.getLayoutMinUnit(24);

        //移除多餘ayout 2016-09-08
//        rParams = (RelativeLayout.LayoutParams) itemHolder.m_llMealText.getLayoutParams();
//        rParams.width = m_vScaleDef.getLayoutWidth(158);
//        rParams.leftMargin = m_vScaleDef.getLayoutWidth(18);
        //未選餐
        m_vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_16, itemHolder.m_tvMeal);
        rParams = (RelativeLayout.LayoutParams) itemHolder.m_tvMeal.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutHeight(60);
        rParams.width = m_vScaleDef.getLayoutWidth(158);
        rParams.leftMargin = m_vScaleDef.getLayoutWidth(18);

        m_vScaleDef.setTextSize(13, itemHolder.m_btnMealSelect);
        rParams = (RelativeLayout.LayoutParams) itemHolder.m_btnMealSelect.getLayoutParams();
        rParams.width = m_vScaleDef.getLayoutWidth(80);
        rParams.height = m_vScaleDef.getLayoutHeight(32);
        rParams.topMargin = m_vScaleDef.getLayoutHeight(13);

        //已選餐
        rParams = (RelativeLayout.LayoutParams) itemHolder.m_llMealDataText.getLayoutParams();
        rParams.width = m_vScaleDef.getLayoutWidth(158);
        rParams.topMargin = m_vScaleDef.getLayoutHeight(20);
        rParams.leftMargin = m_vScaleDef.getLayoutWidth(18);
        rParams.bottomMargin = m_vScaleDef.getLayoutHeight(20);

        rParams = (RelativeLayout.LayoutParams) itemHolder.m_ibtnMealEdit.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutMinUnit(24);
        rParams.width = m_vScaleDef.getLayoutMinUnit(24);
        rParams.topMargin = m_vScaleDef.getLayoutHeight(18);

        rParams = (RelativeLayout.LayoutParams) itemHolder.m_ibtnMealGarbage.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutMinUnit(24);
        rParams.width = m_vScaleDef.getLayoutMinUnit(24);
        rParams.leftMargin = m_vScaleDef.getLayoutWidth(30);
        rParams.topMargin = m_vScaleDef.getLayoutHeight(18);

//        m_vScaleDef.setTextSize(13, itemHolder.m_tvMealClose);
//        rParams = (RelativeLayout.LayoutParams) itemHolder.m_tvMealClose.getLayoutParams();
//        rParams.topMargin = m_vScaleDef.getLayoutHeight(21.7);

        m_vScaleDef.setTextSize(13, itemHolder.m_tvMealInvalid);
        rParams = (RelativeLayout.LayoutParams) itemHolder.m_tvMealInvalid.getLayoutParams();
        rParams.topMargin = m_vScaleDef.getLayoutHeight(21.7);

        //行李
        itemHolder.m_vBagLine.getLayoutParams().height = iDivHeight;

        rParams = (RelativeLayout.LayoutParams) itemHolder.m_ivBag.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutMinUnit(24);
        rParams.width = m_vScaleDef.getLayoutMinUnit(24);
        rParams.topMargin = m_vScaleDef.getLayoutHeight(18);

        m_vScaleDef.setTextSize(13, itemHolder.m_tvFreeBagTitle);
        m_vScaleDef.setTextSize(13, itemHolder.m_tvFreeBagValue);
        m_vScaleDef.setTextSize(13, itemHolder.m_tvBuybagTitle);
        m_vScaleDef.setTextSize(13, itemHolder.m_tvBuybagValue);

        rParams = (RelativeLayout.LayoutParams) itemHolder.m_btnBagEdit.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutHeight(32);
        rParams.width = m_vScaleDef.getLayoutWidth(80);
        rParams.topMargin = m_vScaleDef.getLayoutHeight(13);
        m_vScaleDef.setTextSize(13, itemHolder.m_btnBagEdit);

        rParams = (RelativeLayout.LayoutParams) itemHolder.m_btnBagEwallet.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutHeight(32);
        rParams.width = m_vScaleDef.getLayoutWidth(80);
        rParams.topMargin = m_vScaleDef.getLayoutHeight(13);
        m_vScaleDef.setTextSize(13, itemHolder.m_btnBagEwallet);

        m_vScaleDef.setTextSize(13, itemHolder.m_tvBagClose);
        rParams = (RelativeLayout.LayoutParams) itemHolder.m_tvBagClose.getLayoutParams();
        rParams.topMargin = m_vScaleDef.getLayoutHeight(21.7);

        m_vScaleDef.setTextSize(13, itemHolder.m_tvBagInvalid);
        rParams = (RelativeLayout.LayoutParams) itemHolder.m_tvBagInvalid.getLayoutParams();
        rParams.topMargin = m_vScaleDef.getLayoutHeight(21.7);

        //行李追蹤
        itemHolder.m_vBagTrackingLine.getLayoutParams().height = iDivHeight;

        rParams = (RelativeLayout.LayoutParams) itemHolder.m_ivBagTracking.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutMinUnit(24);
        rParams.width = m_vScaleDef.getLayoutMinUnit(24);
        rParams.topMargin = m_vScaleDef.getLayoutHeight(18);

        rParams = (RelativeLayout.LayoutParams) itemHolder.m_llBagTracking.getLayoutParams();
        rParams.topMargin = m_vScaleDef.getLayoutHeight(4);
        rParams.bottomMargin = m_vScaleDef.getLayoutHeight(4);
        rParams.leftMargin = m_vScaleDef.getLayoutWidth(18);

        //貴賓室
        itemHolder.m_vVipLine.getLayoutParams().height = iDivHeight;

        rParams = (RelativeLayout.LayoutParams) itemHolder.m_ivVip.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutMinUnit(24);
        rParams.width = m_vScaleDef.getLayoutMinUnit(24);
        rParams.topMargin = m_vScaleDef.getLayoutHeight(18);

        m_vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_16, itemHolder.m_tvVip);
        rParams = (RelativeLayout.LayoutParams) itemHolder.m_tvVip.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutHeight(60);
        rParams.leftMargin = m_vScaleDef.getLayoutWidth(18);

        //額外服務
        itemHolder.m_vExtraServiceLine.getLayoutParams().height = iDivHeight;

        rParams = (RelativeLayout.LayoutParams) itemHolder.m_ivExtraService.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutMinUnit(24);
        rParams.width = m_vScaleDef.getLayoutMinUnit(24);
        rParams.topMargin = m_vScaleDef.getLayoutHeight(18);

        //問卷圖示
        rParams = (RelativeLayout.LayoutParams) itemHolder.m_ivQues.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutMinUnit(24);
        rParams.width = m_vScaleDef.getLayoutMinUnit(24);
        rParams.leftMargin = m_vScaleDef.getLayoutWidth(20);

        rParams = (RelativeLayout.LayoutParams) itemHolder.m_ivQuesInfo.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutMinUnit(24);
        rParams.width = m_vScaleDef.getLayoutMinUnit(24);
        rParams.rightMargin = m_vScaleDef.getLayoutWidth(121.3);

        rParams = (RelativeLayout.LayoutParams) itemHolder.m_llExtraService.getLayoutParams();
        rParams.leftMargin = m_vScaleDef.getLayoutWidth(18);

        rParams = (RelativeLayout.LayoutParams) itemHolder.m_llBag.getLayoutParams();
        rParams.leftMargin = m_vScaleDef.getLayoutWidth(18);

        rParams.topMargin = m_vScaleDef.getLayoutWidth(14);
        rParams.bottomMargin = m_vScaleDef.getLayoutWidth(14);

        lParams = (LinearLayout.LayoutParams) itemHolder.m_llBuyBag.getLayoutParams();
        lParams.topMargin = m_vScaleDef.getLayoutWidth(7);


        m_vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_16, itemHolder.m_tvExtraService);
        lParams = (LinearLayout.LayoutParams) itemHolder.m_tvExtraService.getLayoutParams();
        lParams.height = m_vScaleDef.getLayoutHeight(60);

        //高鐵
        m_vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_16, itemHolder.m_tvHSR);
        rParams = (RelativeLayout.LayoutParams) itemHolder.m_tvHSR.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutHeight(60);
        rParams.width = m_vScaleDef.getLayoutWidth(158);

        m_vScaleDef.setTextSize(13, itemHolder.m_btnHSR);
        rParams = (RelativeLayout.LayoutParams) itemHolder.m_btnHSR.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutHeight(32);
        rParams.width = m_vScaleDef.getLayoutWidth(80);
        rParams.topMargin = m_vScaleDef.getLayoutHeight(14);

        //wifi
        m_vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_16, itemHolder.m_tvWifi);
        rParams = (RelativeLayout.LayoutParams) itemHolder.m_tvWifi.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutHeight(60);
        rParams.width = m_vScaleDef.getLayoutWidth(158);

        m_vScaleDef.setTextSize(13, itemHolder.m_btnWifi);
        rParams = (RelativeLayout.LayoutParams) itemHolder.m_btnWifi.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutHeight(32);
        rParams.width = m_vScaleDef.getLayoutWidth(80);
        rParams.topMargin = m_vScaleDef.getLayoutHeight(14);

        //問卷調查
        m_vScaleDef.setTextSize(16, itemHolder.m_tvQues);
        rParams = (RelativeLayout.LayoutParams) itemHolder.m_tvQues.getLayoutParams();
        rParams.leftMargin = m_vScaleDef.getLayoutWidth(20);

        //機票號碼
        itemHolder.m_vTicketNumberLine.getLayoutParams().height = iDivHeight;

        m_vScaleDef.setTextSize(13, itemHolder.m_tvTicketNumTitle);
        rParams = (RelativeLayout.LayoutParams) itemHolder.m_tvTicketNumTitle.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutHeight(50);
        rParams.leftMargin = m_vScaleDef.getLayoutWidth(20.3);

        m_vScaleDef.setTextSize(13, itemHolder.m_tvTicketNumberData);
        rParams = (RelativeLayout.LayoutParams) itemHolder.m_tvTicketNumberData.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutHeight(50);
        rParams.leftMargin = m_vScaleDef.getLayoutWidth(4);
        //rParams.rightMargin = m_vScaleDef.getLayoutWidth(19.7);

        m_vScaleDef.setViewSize(itemHolder.m_btnModiflyTicket, 80, 32);
        m_vScaleDef.setTextSize(13, itemHolder.m_btnModiflyTicket);
        rParams = (RelativeLayout.LayoutParams) itemHolder.m_btnModiflyTicket.getLayoutParams();
        rParams.rightMargin = m_vScaleDef.getLayoutWidth(20);


        //無法線上check-in
        m_vScaleDef.setPadding(itemHolder.m_rlNoOnlineCheckIn, 20, 0, 20, 0);

        m_vScaleDef.setTextSize(13, itemHolder.m_tvNoOnlineCheckIn);
        itemHolder.m_tvNoOnlineCheckIn.setMinHeight(m_vScaleDef.getLayoutHeight(40));

        //虛線
        itemHolder.m_dlHSR.getLayoutParams().height = iDivHeight;
        itemHolder.m_dlWifi.getLayoutParams().height = iDivHeight;


        itemHolder.m_vQues.getLayoutParams().height = iDivHeight;

        //各item之間的分隔線
        itemHolder.m_vLine.getLayoutParams().height = m_vScaleDef.getLayoutHeight(10);
        if (!m_bIsShowAddView && position == m_alPassenger.size() - 1) {
            itemHolder.m_vLine.setVisibility(View.GONE);
        } else {
            itemHolder.m_vLine.setVisibility(View.VISIBLE);
        }
    }

    //meal view
    private static class MealHolder extends RecyclerView.ViewHolder {
        TextView tvMeal = null;
        TextView tvMealName = null;

        public MealHolder(View vMeal) {
            super(vMeal);

            tvMeal = (TextView) vMeal.findViewById(R.id.tv_title);
            tvMealName = (TextView) vMeal.findViewById(R.id.tv_data);
        }
    }

    //2016-09-27 更換結構
    //meal自適應
    private void prepareMeal(MealHolder mealViewHolder, CIMealInfoEntity mealItem, int i) {

        if (null != mealItem.mealtype_desc)
            mealViewHolder.tvMeal.setText(mealItem.mealtype_desc);

        if (TextUtils.isEmpty(mealItem.meal_code)) {
            mealViewHolder.tvMealName.setText(m_context.getString(R.string.unselected));
        } else if (null != mealItem.meal_name) {
            mealViewHolder.tvMealName.setText(mealItem.meal_name);
        }

        m_vScaleDef.setTextSize(13, mealViewHolder.tvMeal);
        m_vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_16, mealViewHolder.tvMealName);

        mealViewHolder.tvMeal.setWidth(m_vScaleDef.getLayoutWidth(158));
        mealViewHolder.tvMealName.setWidth(m_vScaleDef.getLayoutWidth(158));
        mealViewHolder.tvMeal.setMinHeight(m_vScaleDef.getLayoutHeight(15.7));
        mealViewHolder.tvMealName.setMinHeight(m_vScaleDef.getLayoutHeight(19.3));

        m_vScaleDef.setMargins(mealViewHolder.tvMealName, 0, 1, 0, 0);

        if (0 != i)
            m_vScaleDef.setMargins(mealViewHolder.tvMeal, 0, 8, 0, 0);

    }

    //新增旅客按鈕
    public class FooterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView m_tvNotFind = null;
        private RelativeLayout m_rlAdd = null;
        private TextView m_tvAdd = null;
        private ImageView m_ivAdd = null;
        private View m_vDiv = null;

        public FooterViewHolder(View footerView) {
            super(footerView);

//            m_BaseHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    //下方的廣告
//                    CIPromotionFragment fragment = new CIPromotionFragment();
//
//                    android.support.v4.app.FragmentManager fragmentManager =
//                            m_fragment.getChildFragmentManager();
//                    android.support.v4.app.FragmentTransaction fragmentTransaction =
//                            fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(
//                            R.id.flayout_promotion, fragment, fragment.toString());
//
//                    fragmentTransaction.commitAllowingStateLoss();
//                }
//            });

            m_tvNotFind = (TextView) footerView.findViewById(R.id.tv_not_find);

            m_rlAdd = (RelativeLayout) footerView.findViewById(R.id.rl_passenger_add);
            m_tvAdd = (TextView) footerView.findViewById(R.id.tv_passenger_add);
            m_ivAdd = (ImageView) footerView.findViewById(R.id.iv_passenger_add);
            m_rlAdd.setOnClickListener(this);

            m_vDiv = (View) footerView.findViewById(R.id.vDiv);
        }

        @Override
        public void onClick(View v) {
            m_Listener.AddPassengerOnClick();
        }
    }

    private void prepareFooter(FooterViewHolder vh) {

        //顯示查無資料訊息
        if (1 == getItemCount()) {
            vh.m_tvNotFind.setVisibility(View.VISIBLE);
        } else {
            vh.m_tvNotFind.setVisibility(View.GONE);
        }
        m_vScaleDef.setTextSize(20, vh.m_tvNotFind);
        vh.m_tvNotFind.getLayoutParams().height = m_vScaleDef.getLayoutHeight(60);

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) vh.m_vDiv.getLayoutParams();
        lp.height = m_vScaleDef.getLayoutHeight(10);

        if (!m_bIsShowAddView) {
            vh.m_rlAdd.setVisibility(View.GONE);
            return;
        }

        //--- add view自適應---//
        m_vScaleDef.setPadding(vh.m_rlAdd, 20, 0, 20, 0);
        lp = (LinearLayout.LayoutParams) vh.m_rlAdd.getLayoutParams();
        lp.height = m_vScaleDef.getLayoutHeight(60);

        m_vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_16, vh.m_tvAdd);
        RelativeLayout.LayoutParams rp = (RelativeLayout.LayoutParams) vh.m_tvAdd.getLayoutParams();
        rp.width = m_vScaleDef.getLayoutWidth(266);
        rp.rightMargin = m_vScaleDef.getLayoutWidth(10);

        rp = (RelativeLayout.LayoutParams) vh.m_ivAdd.getLayoutParams();
        rp.width = m_vScaleDef.getLayoutMinUnit(24);
        rp.height = m_vScaleDef.getLayoutMinUnit(24);
        //--- add view自適應---//
    }

    //meal view
    private static class BaggageTrackHolder extends RecyclerView.ViewHolder {
        RelativeLayout  rlRoot = null;
        LinearLayout    llBaggageTrackingItem = null;
        TextView        tvTitle = null;
        TextView        tvValue = null;
        Button          btnSearch = null;

        public BaggageTrackHolder(View vTracker) {
            super(vTracker);

            rlRoot = (RelativeLayout) vTracker.findViewById(R.id.root);
            llBaggageTrackingItem = (LinearLayout) vTracker.findViewById(R.id.ll_bag_tracking_item);
            tvTitle = (TextView) vTracker.findViewById(R.id.tv_title);
            tvValue = (TextView) vTracker.findViewById(R.id.tv_value);
            btnSearch = (Button) vTracker.findViewById(R.id.btn_bag_search);
        }
    }

    //2016-09-27 更換結構
    //meal自適應
    private void prepareBaggTrack(BaggageTrackHolder bagTrackholder, final CIBaggageInfoNumEntity baggageItem ) {

        m_vScaleDef.selfAdjustAllView(bagTrackholder.rlRoot);

        bagTrackholder.tvValue.setText(baggageItem.Baggage_ShowNumber);

        bagTrackholder.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (null != m_Listener){
                    m_Listener.BaggageTrackingInfo(baggageItem);
                }
            }
        });
    }

    /**檢查該牌卡是否為本人的牌卡
     *
     * 會員登入
     * 比對會員姓名與牌卡姓名是否相同
     * 非會員登入
     * 比對FindMyBooking時的姓名與牌卡姓名是否相同
     * */
    private  boolean CheckIsMyPassengerCard( String strFirstName, String strLastName ){

        boolean bIsMySelf = false;

        if ( CIApplication.getLoginInfo().GetLoginStatus() ){
            //會員登入
            if (    TextUtils.equals(CIApplication.getLoginInfo().GetUserFirstName(), strFirstName ) &&
                    TextUtils.equals(CIApplication.getLoginInfo().GetUserLastName(), strLastName )  ){
                bIsMySelf = true;
            } else {
                bIsMySelf = false;
            }
        } else {
            //未登入
            if (    TextUtils.equals(m_strInquiryFirstName, strFirstName ) &&
                    TextUtils.equals(m_strInquiryLastName, strLastName )  ){
                bIsMySelf = true;
            } else {
                bIsMySelf = false;
            }

        }

        return bIsMySelf;
    }
}
