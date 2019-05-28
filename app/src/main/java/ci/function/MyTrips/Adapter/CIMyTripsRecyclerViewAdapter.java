package ci.function.MyTrips.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.MyTrips.CIMyTripsFragment;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;
import ci.ui.view.DashedLine;
import ci.ws.Models.entities.CIFlightStationEntity;
import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.Presenter.CIInquiryFlightStationPresenter;

/**
 * Created by jlchen on 2016/3/8.
 */
public class CIMyTripsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public interface onMyTripsRecyclerViewAdapterParameter{
        // 傳入mytrips mode值
        // 近期行程的margin會依照mytrips mode不同而有所不同
        int GetMyTripsMode();
        // 顯示ws錯誤
        String GetErrorMsg();
        // 是否顯示ProgressBar
        boolean IsShowProgressBar();
    }

    public interface onMyTripsRecyclerViewAdapterListener{
        void AddTripsOnClick();

        void onItineraryClick( int ipostion);
    }

    private onMyTripsRecyclerViewAdapterParameter   m_Parameter;
    private onMyTripsRecyclerViewAdapterListener    m_Listener;

    public static final int             TYPE_ITEM       = 2000;
    public static final int             TYPE_FOOTER     = 2001;

    private Context                     m_context 		= null;
    private ViewScaleDef                m_vScaleDef;
    private ArrayList<CITripListResp_Itinerary> m_arItemList	= new ArrayList<CITripListResp_Itinerary>();

    //判斷item是否要發光(剛新增的item要發光)
    private boolean                     m_bShadow       = false;

    public CIMyTripsRecyclerViewAdapter(Context context,
                            ArrayList<CITripListResp_Itinerary> arDataList,
                            boolean bShadow,
                            onMyTripsRecyclerViewAdapterParameter onParameter,
                            onMyTripsRecyclerViewAdapterListener onListener){
        this.m_context      = context;
        this.m_vScaleDef    = ViewScaleDef.getInstance(m_context);

        this.m_arItemList   = arDataList;
        this.m_bShadow      = bShadow;
        this.m_Parameter    = onParameter;
        this.m_Listener     = onListener;
    }

    @Override
    public int getItemCount() {
        return m_arItemList == null ? 1 : m_arItemList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if ( null == m_arItemList )
            return TYPE_FOOTER;
//      check what type our position is, based on the assumption that the order is items > footers
        if(position >= m_arItemList.size()){
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//      if our position is one of our items (this comes from getItemViewType(int position) below)
        if(viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_my_trip_info_view, parent, false);

            return new ItemHolder(view);

//      else we have a header/footer
        }else{
            View viewAdd = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_my_trip_add_view, parent, false);

            return new FooterViewHolder(viewAdd);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof FooterViewHolder){
            //add oru view to a footer view and display it
            prepareFooter((FooterViewHolder) holder);
        }else {
            //it's one of our items, display as required
            prepareItem((ItemHolder) holder, position);
        }
    }

    private void prepareItem(ItemHolder holder, int position){

        //乘客數
//        holder.iPassengerCount = m_arItemList.get(position).getPassengerCount();
        //check in狀態
//        holder.checkInType = m_arItemList.get(position).getCheckType();

        //--- head自適應 ---//
        holder.llayout_body.getLayoutParams().width = m_vScaleDef.getLayoutWidth(340);

        RelativeLayout.LayoutParams rParams =
                (RelativeLayout.LayoutParams)holder.rlayout_head.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutHeight(19.7);

        int iMyTripsMode = CIMyTripsFragment.MyTripsMode.CI_MEMBER.ordinal();

        if ( null != m_Parameter )
            iMyTripsMode = m_Parameter.GetMyTripsMode();

        int iMargins;
        if ( iMyTripsMode == CIMyTripsFragment.MyTripsMode.BASE.ordinal() ){
            //因為選擇按鈕隱藏 listview不留上間距 所以rlayout_head的TopMargin要設為30
            iMargins = 30;
        }else {
            iMargins = 10;
        }

        m_vScaleDef.setMargins(holder.rlayout_head, 10, iMargins, 10, 0);

        if ( 0 != position){
            rParams.topMargin = m_vScaleDef.getLayoutHeight(20);
        }
        //2016-11-15 Modify by Ryan for 調整PNR時間邏輯, 不再抓取CPR的時間來更新, 調整為抓取DISPLAY TAG
        //日期
//        if ( null == m_arItemList.get(position).getDisplayDepartureDate() )
//            m_arItemList.get(position).Departure_Date = "";
        holder.tvDate.setText(m_arItemList.get(position).getDisplayDepartureDate());
        m_vScaleDef.setTextSize(m_vScaleDef.DEF_TEXT_SIZE_16, holder.tvDate);

        //2016-11-15 Modify by Ryan for 調整PNR時間邏輯, 不再抓取CPR的時間來更新, 調整為抓取DISPLAY TAG
        //起訖站
//        if ( null == m_arItemList.get(position).Departure_Date )
//            m_arItemList.get(position).Departure_Date = "";
        if ( null == m_arItemList.get(position).Arrival_Station )
            m_arItemList.get(position).Arrival_Station = "";
        holder.tvGoto.setText(
                String.format(
                        m_context.getString(R.string.my_trips_goto),
                        m_arItemList.get(position).Departure_Station,
                        m_arItemList.get(position).Arrival_Station));
        m_vScaleDef.setTextSize(m_vScaleDef.DEF_TEXT_SIZE_16, holder.tvGoto);

//        holder.shadowLayout.invalidateShadow(
//                ContextCompat.getColor(m_context, R.color.transparent),
//                ViewScaleDef.getInstance(m_context).getLayoutHeight(10),
//                m_context.getResources().getDimension(R.dimen.default_corner_radius),
//                0, 0);

        //新增的行程要發光
//        if ( position == m_arItemList.size()-1 && true == m_bShadow ){
//            holder.shadowLayout.invalidateShadow(
//                    ContextCompat.getColor(m_context, R.color.cyan),
//                    ViewScaleDef.getInstance(m_context).getLayoutHeight(10),
//                    m_context.getResources().getDimension(R.dimen.default_corner_radius),
//                    0, 0);
//        }else {
//            holder.shadowLayout.invalidateShadow(
//                    ContextCompat.getColor(m_context, R.color.transparent),
//                    ViewScaleDef.getInstance(m_context).getLayoutHeight(10),
//                    m_context.getResources().getDimension(R.dimen.default_corner_radius),
//                    0, 0);
//        }
        //--- head自適應 ---//

        prepareFlight(holder, position);

    }

    private void prepareFlight(final ItemHolder holder, final int position){
        holder.llayout_body.removeAllViews();

        LayoutInflater layoutInflater =
                (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //中間 航班代號和飛行時間等資訊
        View vFlight = layoutInflater.inflate(R.layout.layout_my_trip_info_card_view, null);
        final FlightHolder flightHolder = new FlightHolder(vFlight);

        final CITripListResp_Itinerary itineraryInfo = m_arItemList.get(position);

        String strDisplayDepartureDate = itineraryInfo.getDisplayDepartureDate();
        String strDisplayArrivalDate = itineraryInfo.getDisplayArrivalDate();
        AppInfo appInfo = AppInfo.getInstance(m_context);
        String day = appInfo.getShowTomorrowDay(strDisplayDepartureDate, strDisplayArrivalDate);
        flightHolder.tvTomorrowTag.setText(day);

        //--- body自適應 ---//
        LinearLayout.LayoutParams layoutparams =
                (LinearLayout.LayoutParams) flightHolder.rl_trip_card.getLayoutParams();
        layoutparams.width = m_vScaleDef.getLayoutWidth(307);
        m_vScaleDef.setPadding(flightHolder.rl_trip_card, 20, 0, 0, 0);

        m_vScaleDef.setTextSize(14, flightHolder.tvAirliner);
        RelativeLayout.LayoutParams rParams =
                (RelativeLayout.LayoutParams)flightHolder.tvAirliner.getLayoutParams();
        rParams.rightMargin = m_vScaleDef.getLayoutWidth(7.7);
        rParams.topMargin = m_vScaleDef.getLayoutHeight(17.3);
        rParams.height = m_vScaleDef.getLayoutHeight(17);

        if ( null == itineraryInfo.Airlines_Name )
            itineraryInfo.Airlines_Name = "";
        if ( null == itineraryInfo.Airlines )
            itineraryInfo.Airlines = "";
        if ( null == itineraryInfo.Flight_Number )
            itineraryInfo.Flight_Number = "";
        flightHolder.tvAirliner.setText(
                itineraryInfo.Airlines_Name +" - "
                        +itineraryInfo.Airlines +" "
                        +itineraryInfo.Flight_Number);

        rParams = (RelativeLayout.LayoutParams)flightHolder.rl_flight_from_to.getLayoutParams();
        rParams.rightMargin = m_vScaleDef.getLayoutWidth(7.7);
        rParams.topMargin = m_vScaleDef.getLayoutHeight(9);
        rParams.height = m_vScaleDef.getLayoutHeight(27);

        m_vScaleDef.setTextSize(22, flightHolder.tvFromLocation);
        if ( null == itineraryInfo.Departure_Station_Name )
            itineraryInfo.Departure_Station_Name = "";
        //2016-09-30 modifly 場站名稱調整為抓取TimeTable內的資料
        String strStationName = getDescNameByStationCode(itineraryInfo.Departure_Station);
        if ( TextUtils.isEmpty(strStationName) ){
            strStationName = itineraryInfo.Departure_Station_Name;
        }
        flightHolder.tvFromLocation.setText(strStationName);

        rParams = (RelativeLayout.LayoutParams)flightHolder.ivFlight.getLayoutParams();
        rParams.rightMargin = m_vScaleDef.getLayoutWidth(7);
        rParams.leftMargin = m_vScaleDef.getLayoutWidth(7);
        rParams.height = m_vScaleDef.getLayoutMinUnit(21.3);
        rParams.width = m_vScaleDef.getLayoutMinUnit(21.3);

        m_vScaleDef.setTextSize(22, flightHolder.tvToLocation);
        if ( null == itineraryInfo.Arrival_Station_Name )
            itineraryInfo.Arrival_Station_Name = "";
        //2016-09-30 modifly 場站名稱調整為抓取TimeTable內的資料
        String strArrivalStationName = getDescNameByStationCode(itineraryInfo.Arrival_Station);
        if ( TextUtils.isEmpty(strArrivalStationName) ){
            strArrivalStationName = itineraryInfo.Arrival_Station_Name;
        }
        flightHolder.tvToLocation.setText(strArrivalStationName);

        rParams = (RelativeLayout.LayoutParams)flightHolder.rl_flight_from_to_time.getLayoutParams();
        rParams.topMargin = m_vScaleDef.getLayoutHeight(2.7);
        rParams.rightMargin = m_vScaleDef.getLayoutWidth(7.7);
        rParams.bottomMargin = m_vScaleDef.getLayoutHeight(17.3);
        rParams.height = m_vScaleDef.getLayoutHeight(21.7);

        rParams = (RelativeLayout.LayoutParams)flightHolder.ivFromTime.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutMinUnit(18);
        rParams.width = m_vScaleDef.getLayoutMinUnit(18);

        m_vScaleDef.setTextSize(18, flightHolder.tvFromTime);
        m_vScaleDef.setMargins(flightHolder.tvFromTime, 4, 0, 0, 0);
        //2016-11-15 Modify by Ryan for 調整PNR時間邏輯, 不再抓取CPR的時間來更新, 調整為抓取DISPLAY TAG
//        if ( null == itineraryInfo.Departure_Time )
//            itineraryInfo.Departure_Time = "";
        flightHolder.tvFromTime.setText(itineraryInfo.getDisplayDepartureTime());
        //flightHolder.tvFromTime.setText(itineraryInfo.Departure_Time);

        rParams = (RelativeLayout.LayoutParams)flightHolder.vFlight.getLayoutParams();
        rParams.rightMargin = m_vScaleDef.getLayoutWidth(7);
        rParams.leftMargin = m_vScaleDef.getLayoutWidth(7);
        rParams.height = m_vScaleDef.getLayoutHeight(21.3);
        rParams.width = m_vScaleDef.getLayoutHeight(21.3);

        rParams = (RelativeLayout.LayoutParams)flightHolder.ivToTime.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutMinUnit(18);
        rParams.width = m_vScaleDef.getLayoutMinUnit(18);

        m_vScaleDef.setTextSize(18, flightHolder.tvToTime);
        m_vScaleDef.setMargins(flightHolder.tvToTime, 4, 0, 0, 0);

        m_vScaleDef.setTextSize(14, flightHolder.tvTomorrowTag);
        rParams = (RelativeLayout.LayoutParams)flightHolder.tvTomorrowTag.getLayoutParams();
        rParams.leftMargin = m_vScaleDef.getLayoutWidth(3);
        //2016-11-15 Modify by Ryan for 調整PNR時間邏輯, 不再抓取CPR的時間來更新, 調整為抓取DISPLAY TAG
//        if ( null == itineraryInfo.Arrival_Time )
//            itineraryInfo.Arrival_Time = "";
        flightHolder.tvToTime.setText(itineraryInfo.getDisplayArrivalTime());
        //flightHolder.tvToTime.setText(itineraryInfo.Arrival_Time);

        rParams = (RelativeLayout.LayoutParams) flightHolder.vLine.getLayoutParams();
        rParams.width = m_vScaleDef.getLayoutWidth(1);
        rParams.height = m_vScaleDef.getLayoutHeight(112);

        rParams = (RelativeLayout.LayoutParams)flightHolder.ivArrow.getLayoutParams();
        rParams.height = m_vScaleDef.getLayoutMinUnit(32);
        rParams.width = m_vScaleDef.getLayoutMinUnit(32);
        //--- body自適應 ---//

        holder.llayout_body.addView(vFlight);

        //要跳轉至MyTripsDetail的按鈕
        flightHolder.ll_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 2016-10-07 調整邏輯,修正為每次點擊行程都跟Server取得最新的形成資訊,已確保行程時間是最新的
                if ( null != m_Listener ){
                    m_Listener.onItineraryClick(position);
                }
            }
        });
    }


    //head欄 顯示行程日期 & 要從卡個機場飛去哪個機場
    public static class ItemHolder extends RecyclerView.ViewHolder {

        RelativeLayout  rlayout_bg;
        RelativeLayout  rlayout_head;
        LinearLayout    llayout_body;
        TextView        tvDate;
        TextView        tvGoto;
//        ShadowLayout    shadowLayout;
        FrameLayout     shadowLayout;

        int             iPassengerCount;

        public ItemHolder(View itemLayoutView) {
            super(itemLayoutView);

            rlayout_bg = (RelativeLayout) itemLayoutView.findViewById(R.id.rlayout_bg);
            rlayout_head = (RelativeLayout) itemLayoutView.findViewById(R.id.rlayout_head);
            tvDate       = (TextView) itemLayoutView.findViewById(R.id.tv_Date);
            tvGoto       = (TextView) itemLayoutView.findViewById(R.id.tv_goto);
            llayout_body = (LinearLayout) itemLayoutView.findViewById(R.id.llayout_body);
//            shadowLayout = (ShadowLayout) itemLayoutView.findViewById(R.id.shadowlayout);
            shadowLayout = (FrameLayout) itemLayoutView.findViewById(R.id.shadowlayout);
        }
    }

    //body欄 顯示每一航班名稱&編號, 起始地&目的地, 飛行&降落的時間...等
    private static class FlightHolder extends RecyclerView.ViewHolder {
        LinearLayout    ll_card;
        RelativeLayout  rl_trip_card;
        TextView        tvAirliner;
        RelativeLayout  rl_flight_from_to;
        TextView        tvFromLocation;
        ImageView       ivFlight;
        TextView        tvToLocation;
        RelativeLayout  rl_flight_from_to_time;
        ImageView       ivFromTime;
        TextView        tvFromTime;
        View            vFlight;
        ImageView       ivToTime;
        TextView        tvToTime;
        TextView        tvTomorrowTag;
        View            vLine;
        ImageView       ivArrow;

        private FlightHolder(View flightLayoutView) {
            super(flightLayoutView);

            ll_card = (LinearLayout)flightLayoutView.findViewById(R.id.ll_card);
            rl_trip_card = (RelativeLayout)flightLayoutView.findViewById(R.id.rl_trip_card);
            tvAirliner = (TextView)flightLayoutView.findViewById(R.id.tv_airliner);

            rl_flight_from_to =
                    (RelativeLayout)flightLayoutView.findViewById(R.id.rl_flight_from_to);
            tvFromLocation = (TextView)flightLayoutView.findViewById(R.id.tv_from);
            ivFlight = (ImageView)flightLayoutView.findViewById(R.id.iv_flight);
            tvToLocation = (TextView)flightLayoutView.findViewById(R.id.tv_to);

            rl_flight_from_to_time =
                    (RelativeLayout) flightLayoutView.findViewById(R.id.rl_flight_from_to_time);
            ivFromTime = (ImageView)flightLayoutView.findViewById(R.id.iv_from_time);
            tvFromTime = (TextView)flightLayoutView.findViewById(R.id.tv_from_time);
            vFlight = (View)flightLayoutView.findViewById(R.id.v_flight);
            ivToTime = (ImageView)flightLayoutView.findViewById(R.id.iv_to_time);
            tvToTime = (TextView)flightLayoutView.findViewById(R.id.tv_to_time);
            tvTomorrowTag = (TextView)flightLayoutView.findViewById(R.id.tv_tomorrow_tag);

            vLine = (View)flightLayoutView.findViewById(R.id.vline);
            ivArrow = (ImageView)flightLayoutView.findViewById(R.id.iv_arrow);
        }
    }

    //轉機欄 顯示與轉機時間相關的文字
    private static class LayoverHolder extends RecyclerView.ViewHolder{
        LinearLayout    ll_layover;
        TextView        tv_layover;
        DashedLine      dl_top;
        DashedLine      dl_bottom;

        private LayoverHolder(View layoverHolder){
            super(layoverHolder);

            ll_layover = (LinearLayout)layoverHolder.findViewById(R.id.ll_layover);
            tv_layover = (TextView)layoverHolder.findViewById(R.id.tv_layover);
            dl_top = (DashedLine)layoverHolder.findViewById(R.id.dl_top);
            dl_bottom = (DashedLine)layoverHolder.findViewById(R.id.dl_bottom);
        }
    }

    //新增行程按鈕
    //our footer RecyclerView.ViewHolder is just a FrameLayout
    public class FooterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        RelativeLayout  m_rlMsg;
        ProgressBar     m_ProgressBar;
        TextView        m_tvNotFind;
        RelativeLayout  m_rlLine;
        View            m_vLine1;
        View            m_vLine2;
        LinearLayout    m_llAdd;
        TextView        m_tvAdd;
        Button          m_btnAdd;

        public FooterViewHolder(View footerView) {
            super(footerView);
            m_rlMsg = (RelativeLayout) footerView.findViewById(R.id.rl_msg);
            m_ProgressBar = (ProgressBar) footerView.findViewById(R.id.progressBar);
            m_tvNotFind = (TextView) footerView.findViewById(R.id.tv_not_find);

            m_rlLine = (RelativeLayout) footerView.findViewById(R.id.rl_line);
            m_vLine1 = (View) footerView.findViewById(R.id.vline1);
            m_vLine2 = (View) footerView.findViewById(R.id.vline2);
            m_llAdd = (LinearLayout) footerView.findViewById(R.id.ll_add);
            m_tvAdd = (TextView) footerView.findViewById(R.id.tv_add);
            m_btnAdd = (Button) footerView.findViewById(R.id.btn_add);
            m_btnAdd.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            m_Listener.AddTripsOnClick();
        }
    }

    private void prepareFooter(FooterViewHolder vh){

        if ( null != m_Parameter ){
            if ( true == m_Parameter.IsShowProgressBar() ){
                vh.m_ProgressBar.setVisibility(View.VISIBLE);
            }else {
                vh.m_ProgressBar.setVisibility(View.GONE);
            }
        }

        vh.m_rlMsg.getLayoutParams().height = m_vScaleDef.getLayoutHeight(50);
        if ( 1 == getItemCount() ){
            m_vScaleDef.setTextSize(20, vh.m_tvNotFind);

        }else {
            vh.m_tvNotFind.setVisibility(View.GONE);
        }

        //--- add view自適應---//
        m_vScaleDef.setMargins(vh.m_rlLine, 0, 0, 0, 10);

        int iline = 1;
        if ( 0 < m_vScaleDef.getLayoutHeight(0.3) ){
            iline = m_vScaleDef.getLayoutHeight(0.3);
        }
        RelativeLayout.LayoutParams rp =
                (RelativeLayout.LayoutParams) vh.m_vLine1.getLayoutParams();
        rp.height = iline;

        rp = (RelativeLayout.LayoutParams) vh.m_vLine2.getLayoutParams();
        rp.height = iline;

        m_vScaleDef.setPadding(vh.m_llAdd, 20, 16, 20, 16);

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)vh.m_tvAdd.getLayoutParams();
        lp.width = m_vScaleDef.getLayoutWidth(170);
        m_vScaleDef.setTextSize(m_vScaleDef.DEF_TEXT_SIZE_16, vh.m_tvAdd);

        lp = (LinearLayout.LayoutParams)vh.m_btnAdd.getLayoutParams();
        lp.width = m_vScaleDef.getLayoutWidth(130);
        lp.height = m_vScaleDef.getLayoutHeight(40);
        lp.leftMargin = m_vScaleDef.getLayoutWidth(20);
        m_vScaleDef.setTextSize(m_vScaleDef.DEF_TEXT_SIZE_16, vh.m_btnAdd);
        //--- add view自適應---//
    }

    private String getDescNameByStationCode( String strCode ){

        CIFlightStationEntity station = CIInquiryFlightStationPresenter.getInstance(null, CIInquiryFlightStationPresenter.ESource.TimeTable).getStationInfoByIATA(strCode);
        if ( null != station && !TextUtils.isEmpty(station.localization_name) ){
            return station.localization_name;
        } else {
            return "";
        }
    }
}
