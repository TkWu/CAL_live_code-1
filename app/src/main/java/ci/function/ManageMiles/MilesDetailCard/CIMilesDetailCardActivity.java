package ci.function.ManageMiles.MilesDetailCard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseNoToolbarActivity;
import ci.function.ManageMiles.item.CIAccumulationItem;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;
import ci.ws.Models.entities.CIRedeemRecord_Info;
import ci.ws.cores.object.CIWSBookingClass;

/**
 * Created by jlchen on 2016/3/10.
 */
public class CIMilesDetailCardActivity extends BaseNoToolbarActivity {

    public enum MilesDetailCardType{
        FLIGHT, EVENT, REDEEM, AWARDS_TRANSFER, AWARDS_RECEIVED;
    }

    private LayoutInflater  m_layoutInflater;

    private RelativeLayout  m_rlBackground  = null;
    private RelativeLayout  m_rlCard        = null;
    private LinearLayout    m_llCardBody    = null;
    private LinearLayout    m_llNameData    = null;
    private LinearLayout    m_llBodyData    = null;
    private TextView        m_tvTitle       = null;
    private TextView        m_tvDate        = null;
    private TextView        m_tvMile        = null;
    private TextView        m_tvTransfer    = null;
    private TextView        m_tvNotInUse    = null;
    private TextView        m_tvIssued      = null;
    private ImageButton     m_ibtnClose     = null;
    private RelativeLayout  m_rlNotInUse    = null;
    private RelativeLayout  m_rlTicketIssued= null;
    private TextView        m_tvDueDate     = null;

    private Bitmap          m_bitmapBlur    = null;

    private CIAccumulationItem m_Item       = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.popupwindow_miles_detail_card;
    }

    @Override
    protected void initialLayoutComponent() {
        m_layoutInflater = (LayoutInflater) m_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Bundle bundle   = getIntent().getExtras();
        //背景圖
        m_bitmapBlur    = bundle.getParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG);
        //哩程資料
        m_Item          = (CIAccumulationItem)bundle.getSerializable(UiMessageDef.BUNDLE_MILES_DETAIL_DATA);;
        //里程類型
        MilesDetailCardType type = MilesDetailCardType.valueOf(bundle.getString(UiMessageDef.BUNDLE_MILES_DETAIL_TYPE));

        m_rlBackground = (RelativeLayout)findViewById(R.id.rl_bg);
        m_rlCard = (RelativeLayout)findViewById(R.id.rl_card);

        m_tvTitle = (TextView)findViewById(R.id.tv_title);
        m_tvDate = (TextView)findViewById(R.id.tv_date);
        m_tvMile = (TextView)findViewById(R.id.tv_miles);
        m_tvTransfer = (TextView)findViewById(R.id.tv_transfer);
        m_tvDueDate = (TextView)findViewById(R.id.tv_due_date_data);
        m_tvNotInUse = (TextView)findViewById(R.id.tv_not_in_use);
        m_tvIssued = (TextView)findViewById(R.id.tv_ticket_issued);

        m_llCardBody = (LinearLayout)findViewById(R.id.ll_body);
        m_llNameData = (LinearLayout)findViewById(R.id.ll_name_data);
        m_llBodyData = (LinearLayout)findViewById(R.id.ll_other_data);

        m_rlNotInUse = (RelativeLayout)findViewById(R.id.rl_not_in_use);
        m_rlTicketIssued = (RelativeLayout)findViewById(R.id.rl_ticket_issued);

        m_ibtnClose = (ImageButton)findViewById(R.id.ibtn_close);
        m_ibtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        if ( null != m_Item ){
            if ( null != m_Item.m_strDate )
                m_tvDate.setText(m_Item.m_strDate);
                //m_tvDate.setText(AppInfo.getInstance(m_Context).ConvertDateFormatToyyyyMMddEEE(m_Item.m_strDate));
            if ( null != m_Item.m_strDescription )
                m_tvTitle.setText(m_Item.m_strDescription);
            if ( null != m_Item.m_strMiles )
                m_tvMile.setText(m_Item.m_strMiles);
        }

        //設定模糊背景
        BitmapDrawable drawable = new BitmapDrawable(m_Context.getResources(), m_bitmapBlur);
        m_rlBackground.setBackground(drawable);
        //依據里程類型設定牌卡
        setType(type);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(m_rlCard);

        RelativeLayout.LayoutParams rp = (RelativeLayout.LayoutParams)m_ibtnClose.getLayoutParams();
        rp.height = vScaleDef.getLayoutMinUnit(40);
        rp.width = vScaleDef.getLayoutMinUnit(40);
        rp.bottomMargin = vScaleDef.getLayoutMinUnit(16);
    }

    private void setType(MilesDetailCardType type){

        switch (type){
            //狀態A 累積-飛行哩程
            case FLIGHT:
                setFlightCard();
                break;

            //狀態B 累積-推廣或合作哩程
            case EVENT:
                setEventCard();
                break;

            //狀態C 兌換-非酬賓獎項轉讓, 不顯示轉讓tag, 顯示到期日,獎項號碼欄
            case REDEEM:
                setRedeemCard();
                if ( null != m_Item ){
                    //若bnsusg為TK, 需另顯示機票號碼欄
                    if (m_Item.m_strBnsusg.equals(CIRedeemRecord_Info.BNSUSG_UPGRADE_TICKET)){
                        setTicket();
                    }

                    //2016-09-21 調整邏輯，若remark 顯示已使用
                    if ( TextUtils.isEmpty(m_Item.m_strRemark) ){
                        setNotInUse(getString(R.string.not_in_use));
                    }else {
                        setTicketIssued(getString(R.string.ticket_issued));
                    }
                }
                break;

            //狀態D 兌換-酬賓獎項轉讓, 顯示轉讓tag, 顯示轉讓人,到期日,獎項號碼,卡號欄
            case AWARDS_TRANSFER:
                setAwardsTransferCard();
                //是否為轉讓
                if ( null != m_Item && true == m_Item.m_bTransfer ){
                    setTransfer();
                }
                break;

            //狀態E 酬賓獎項
            case AWARDS_RECEIVED:

                setAwardsReceivedCard();
                if ( null != m_Item ){
                    //是否為轉讓
                    if ( true == m_Item.m_bTransfer ){
                        setTransfer();
                    }

                    //2016-09-21 調整邏輯，若remark 顯示已使用
                    if ( TextUtils.isEmpty(m_Item.m_strRemark) ){
                        setNotInUse(getString(R.string.not_in_use));
                    }else {
                        setTicketIssued(getString(R.string.ticket_issued));
                    }
                }
                break;
        }
    }

    private void setFlightCard(){
        setExDate();
        setFlightNumber();
        setCabin();
    }

    private void setEventCard(){
        setExDate();
    }

    private void setRedeemCard(){
        m_tvMile.setTextColor(ContextCompat.getColor(m_Context, R.color.pinkish_red));

        setExDate();
        setAwardNumber();
    }

    private void setAwardsTransferCard(){
        m_tvMile.setTextColor(ContextCompat.getColor(m_Context, R.color.pinkish_red));

        //2016-07-27 modify by ryan for 轉讓or受讓 顯示字串不同，須分開
        setNominator(m_Context.getString(R.string.manage_miles_nominee));
        setExDate();
        setAwardNumber();
//        setCardType();
        setCardNumber();
    }

    private void setTransfer(){
        m_tvTransfer.setVisibility(View.VISIBLE);
    }

    private void setAwardsReceivedCard(){
        m_tvMile.setVisibility(View.GONE);

        //2016-07-27 modify by ryan for 轉讓or受讓 顯示字串不同，須分開
        setNominator(m_Context.getString(R.string.nominator));
        setExDate();
        setAwardNumber();
//        setCardType();
        setCardNumber();
    }

    private void setFlightNumber(){
        View vFlightN = m_layoutInflater.inflate(R.layout.layout_view_miles_detail_card_item, null);
        TextView tvFlight = (TextView)vFlightN.findViewById(R.id.tv_info_title);
        TextView tvFlightData = (TextView)vFlightN.findViewById(R.id.tv_info_data);
        tvFlight.setText(m_Context.getString(R.string.flight_number));
        if ( null != m_Item && null != m_Item.m_strFlightNo )
            tvFlightData.setText(m_Item.m_strFlightNo);
        m_llBodyData.addView(vFlightN);
    }

    private void setCabin(){
        View vCabin = m_layoutInflater.inflate(R.layout.layout_view_miles_detail_card_item, null);
        TextView tvCabin = (TextView)vCabin.findViewById(R.id.tv_info_title);
        TextView tvCabinData = (TextView)vCabin.findViewById(R.id.tv_info_data);
        tvCabin.setText(m_Context.getString(R.string.cabin));
        tvCabinData.setText(CIWSBookingClass.BookingClassCodeToName(m_Item.m_strCabin));
        m_llBodyData.addView(vCabin);
    }

    //2016-07-27 modify by ryan for 轉讓or受讓 顯示字串不同，須分開
    //private void setNominator(){
    private void setNominator( String strNominator ){
        View vName = m_layoutInflater.inflate(R.layout.layout_view_miles_detail_card_item, null);
        TextView tvName = (TextView)vName.findViewById(R.id.tv_info_title);
        TextView tvNameData = (TextView)vName.findViewById(R.id.tv_info_data);
        //tvName.setText(m_Context.getString(R.string.nominator));
        tvName.setText(strNominator);
        if ( null != m_Item && null != m_Item.m_strNominee )
            tvNameData.setText(m_Item.m_strNominee);
        View vlineTop = (View)vName.findViewById(R.id.v_line);
        vlineTop.setVisibility(View.GONE);
        View vlineBottom = (View)vName.findViewById(R.id.v_line_bottom);
        vlineBottom.setVisibility(View.VISIBLE);
        m_llNameData.addView(vName);
    }

    private void setExDate(){
        m_tvDueDate.setText(m_Item.m_strExDate);
        //m_tvDueDate.setText(AppInfo.getInstance(m_Context).ConvertDateFormatToyyyyMMddEEE(m_Item.m_strExDate));
    }

    private void setAwardNumber(){
        View vAwardN = m_layoutInflater.inflate(R.layout.layout_view_miles_detail_card_item, null);
        TextView tvAwardN = (TextView)vAwardN.findViewById(R.id.tv_info_title);
        TextView tvAwardNData = (TextView)vAwardN.findViewById(R.id.tv_info_data);
        tvAwardN.setText(m_Context.getString(R.string.award_no));
        if ( null != m_Item && null != m_Item.m_strAwardNo )
            tvAwardNData.setText(m_Item.m_strAwardNo);
        m_llBodyData.addView(vAwardN);
    }

    private void setCardType(){
        View vCardT = m_layoutInflater.inflate(R.layout.layout_view_miles_detail_card_item, null);
        TextView tvCardT = (TextView)vCardT.findViewById(R.id.tv_info_title);
        TextView tvCardTData = (TextView)vCardT.findViewById(R.id.tv_info_data);
        tvCardT.setText(m_Context.getString(R.string.manage_miles_cardtype));
//        tvCardTData.setText(m_Item.m_strCardType);
        m_llBodyData.addView(vCardT);
    }

    private void setCardNumber(){
        View vCardN = m_layoutInflater.inflate(R.layout.layout_view_miles_detail_card_item, null);
        TextView tvCardN = (TextView)vCardN.findViewById(R.id.tv_info_title);
        TextView tvCardNData = (TextView)vCardN.findViewById(R.id.tv_info_data);
        tvCardN.setText(m_Context.getString(R.string.manage_miles_cardnumber));
        if ( null != m_Item && null != m_Item.m_strCardNo )
            tvCardNData.setText(m_Item.m_strCardNo);
        m_llBodyData.addView(vCardN);
    }

    private void setNotInUse(String str){
        m_rlNotInUse.setVisibility(View.VISIBLE);
        if ( null != str )
            m_tvNotInUse.setText(str);
        m_llCardBody.setBackgroundColor( ContextCompat.getColor(m_Context, R.color.white_five) );
    }

    private void setTicketIssued(String str) {
        m_rlTicketIssued.setVisibility(View.VISIBLE);
        if ( null != str )
            m_tvIssued.setText(str);
        m_llCardBody.setBackgroundColor(ContextCompat.getColor(m_Context, R.color.white_five) );
    }

    private void setTicket(){
        View vTicketN = m_layoutInflater.inflate(R.layout.layout_view_miles_detail_card_item, null);
        TextView tvTicketN = (TextView)vTicketN.findViewById(R.id.tv_info_title);
        TextView tvTicketNData = (TextView)vTicketN.findViewById(R.id.tv_info_data);
        tvTicketN.setText(m_Context.getString(R.string.ticket_number));
        if ( null != m_Item && null != m_Item.m_strTicketNo )
            tvTicketNData.setText(m_Item.m_strTicketNo);
        m_llBodyData.addView(vTicketN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        m_llBodyData.removeAllViews();
        m_llNameData.removeAllViews();

        ImageHandle.recycleBitmap(m_bitmapBlur);
        System.gc();
    }
}
