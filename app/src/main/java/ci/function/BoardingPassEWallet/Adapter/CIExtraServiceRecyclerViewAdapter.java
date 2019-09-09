package ci.function.BoardingPassEWallet.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.Locale;

import ci.function.BoardingPassEWallet.ExtraServices.CIExtraServicesCardActivity;
import ci.function.BoardingPassEWallet.item.CIExtraServiceItem;
import ci.function.Core.CIApplication;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;
import ci.ws.Models.entities.CIEWallet_ExtraService_Info;

/**
 * Created by kevincheng on 2016/3/25.
 */
public class CIExtraServiceRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public interface onRecyclerViewAdapterListener{
        void OnGotoExtraServicesFragmentClick();
    }

    private onRecyclerViewAdapterListener m_Listener;

    enum EType{
        TYPE_ITEM,TYPE_FOOTER
    }

    /**
     *  THSR:高鐵,
     *  VIP:VIP,
     *  WIFI:Wifi,
     *  FamilyCouch: 親子臥艙加購,
     *  ExtraBaggage:超重行李加購(樂購)
     *  EB:超重行李加購
     *  */
    public enum EServiceType{
        WIFI, ExtraBaggage, VIP, THSR, FamilyCouch, EB, EVENT
    }


    private Context m_context = null;
    private ViewScaleDef m_vScaleDef;
    private ArrayList<CIExtraServiceItem> m_arItemList = new ArrayList<>();
//    private ArrayList<CIExtraServiceItem> m_arItemList = new ArrayList<>();

    public CIExtraServiceRecyclerViewAdapter(Context context,
                                             ArrayList<CIExtraServiceItem> arDataList,
                                             onRecyclerViewAdapterListener listener) {
        this.m_context = context;
        this.m_vScaleDef = ViewScaleDef.getInstance(m_context);
        this.m_arItemList = arDataList;
        this.m_Listener = listener;
    }

    @Override
    public int getItemCount() {

        return m_arItemList == null ? 1 : m_arItemList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if ( null == m_arItemList ){
            return EType.TYPE_FOOTER.ordinal();
        }
        if ( position >= m_arItemList.size() ){
            return EType.TYPE_FOOTER.ordinal();
        }
        return EType.TYPE_ITEM.ordinal();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == EType.TYPE_ITEM.ordinal()) {
            View view = LayoutInflater.from(parent.getContext())
                                      .inflate(R.layout.layout_boarding_pass_info_and_extra_service_view, parent, false);

            return new ItemHolder(view);
        } else {
            View viewAdd = LayoutInflater.from(parent.getContext())
                                         .inflate(R.layout.layout_lastitem_add_view, parent, false);

            return new FooterViewHolder(viewAdd);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterViewHolder) {
            prepareFooter((FooterViewHolder) holder);
        } else {
            prepareItem((ItemHolder) holder, position);
        }
    }

    private void prepareItem(ItemHolder holder, int position){

//        String strDate = "";
//        String strFromTo = "";
//        if ( null != m_arItemList ){
//
//            if ( null != m_arItemList.get(position).getDate() ){
//                strDate = m_arItemList.get(position).getDate();
//            }
//
//            if ( null != m_arItemList.get(position).getFromTo() ){
////                    && null != m_PnrData.Itinerary_Info.get(position).Arrival_Station ){
//                strFromTo = m_arItemList.get(position).getFromTo();
////                        String.format(
////                        m_context.getString(R.string.my_trips_goto),
////                        m_PnrData.Itinerary_Info.get(position).Departure_Station,
////                        m_PnrData.Itinerary_Info.get(position).Arrival_Station);
//            }
//        }
//        //填入資料
//        holder.tvDate.setText(strDate);
//        holder.tvGoto.setText(strFromTo);
//        holder.tvDate.setText(m_arItemList.get(position).getDate());
//        holder.tvGoto.setText(m_arItemList.get(position).getFromTo());

        //--- head自適應 ---//
        holder.tvDate.setVisibility(View.GONE);
        holder.tvGoto.setVisibility(View.GONE);
//        m_vScaleDef.setTextSize(16,holder.tvDate);
//        m_vScaleDef.setTextSize(16, holder.tvGoto);
//        m_vScaleDef.setMargins(holder.tvGoto, 20, 0, 0, 0);
        m_vScaleDef.setViewSize(holder.rlayout_head, 340, 9.5);
        m_vScaleDef.setViewSize(holder.line, ViewGroup.LayoutParams.MATCH_PARENT, 30);
        //--- head自適應 ---//

        prepareInfo(holder, position);

    }

    private void prepareInfo(final ItemHolder holder, final int position){
        holder.llayout_body.removeAllViews();

        LayoutInflater layoutInflater =
                (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if ( null == m_arItemList.get(position).m_arExtraServiceDataList )
            return;

        final ArrayList<CIEWallet_ExtraService_Info> arrayList = m_arItemList.get(position).m_arExtraServiceDataList;

        for ( int i = 0; i < arrayList.size(); i++){

            final int finalI = i;

            //中間 航班代號和飛行時間等資訊
            View vBoardingPass = layoutInflater.inflate(R.layout.layout_extra_service_info_card_view, null);
            final ExtraServiceHolder extraServiceHolder = new ExtraServiceHolder(vBoardingPass);

            //填入資訊
            String strName = "";
            if ( null != arrayList.get(i).FIRSTNAME )
                strName = arrayList.get(i).FIRSTNAME;
            if ( null != arrayList.get(i).LASTNAME )
                strName = strName + " " + arrayList.get(i).LASTNAME;

            extraServiceHolder.tvName.setText(strName);

            if ( null != arrayList.get(i).SERVICETYPE )
            {
                if ( "EVENT".equals(arrayList.get(i).SERVICETYPE) )
                    extraServiceHolder.tvServiceNum.setText(arrayList.get(i).CARD_NO);
                else
                    extraServiceHolder.tvServiceNum.setText(arrayList.get(i).TICKETNO);
            }




//            extraServiceHolder.tvName.setText(alBoardingPass.get(i).getName());
//            extraServiceHolder.tvServiceNum.setText(alBoardingPass.get(i).getServiceNum());

            //--- body自適應 ---//
            m_vScaleDef.selfAdjustAllView(vBoardingPass.findViewById(R.id.root));
            m_vScaleDef.selfAdjustSameScaleView(extraServiceHolder.ivServiceType, 32, 32);
            m_vScaleDef.selfAdjustSameScaleView(extraServiceHolder.ivUsed, 80.7, 68.7);

            //加入登機資訊
            holder.llayout_body.addView(vBoardingPass);

            int iWifiRes = R.drawable.ic_wifi_b_services;
            int iVipRes = R.drawable.ic_vip_b_services;
            int iBagRes = R.drawable.ic_baggage_b_services;
            int iHsrRes = R.drawable.ic_vip_b_high_speed_rail;
            int iFamilyRes = R.drawable.ic_family_couch_b;

            boolean bStatue = false;
//            if(true == m_arItemList.get(position).getIsExpired()){
            if( "Y".equals(arrayList.get(i).STATUS) ){
                bStatue = true;

                iWifiRes = R.drawable.ic_wifi_services;
                iVipRes = R.drawable.ic_vip_services;
                iBagRes = R.drawable.ic_baggage_services;
                iHsrRes = R.drawable.ic_vip_b_high_speed_rail_w;
                iFamilyRes = R.drawable.ic_family_couch_w;

                extraServiceHolder.ivUsed.setVisibility(View.VISIBLE);
                Locale locale = CIApplication.getLanguageInfo().getLanguage_Locale();
                switch (locale.toString()){
                    case "zh_TW":
                        extraServiceHolder.ivUsed.setImageResource(R.drawable.img_used_zh_tw);
                        break;
                    case "zh_CN":
                        extraServiceHolder.ivUsed.setImageResource(R.drawable.img_used_zh_cn);
                        break;
                    case "en":
                        extraServiceHolder.ivUsed.setImageResource(R.drawable.img_used_en);
                        break;
                    case "ja_JP":
                        extraServiceHolder.ivUsed.setImageResource(R.drawable.img_used_ja);
                        break;
                    default:
                        extraServiceHolder.ivUsed.setImageResource(R.drawable.img_used_zh_tw);
                        break;
                }

                extraServiceHolder.tvName.setTextColor(ContextCompat.getColor(m_context, R.color.white_four));
                extraServiceHolder.tvServiceNum.setTextColor(ContextCompat.getColor(m_context,R.color.powder_blue));
                extraServiceHolder.tvServiceType.setTextColor(ContextCompat.getColor(m_context,R.color.white_four));

                extraServiceHolder.vExtraServiceBg.setBackgroundResource(R.drawable.bg_btn_radius_3dp_dark_periwinkle);
                extraServiceHolder.vRight.setBackgroundResource(R.drawable.bg_btn_radius_3dp_white_four_french_blue_two_50);

            } else {
                bStatue = false;

                extraServiceHolder.ivUsed.setVisibility(View.GONE);
                extraServiceHolder.tvName.setTextColor(ContextCompat.getColor(m_context, R.color.black_one));
                extraServiceHolder.tvServiceNum.setTextColor(ContextCompat.getColor(m_context,R.color.grey_four));
                extraServiceHolder.tvServiceType.setTextColor(ContextCompat.getColor(m_context,R.color.french_blue));

                extraServiceHolder.vExtraServiceBg.setBackgroundResource(R.drawable.bg_btn_radius_3dp_white_four_white_six);
                extraServiceHolder.vRight.setBackgroundResource(R.drawable.bg_btn_radius_3dp_white_four_powder_blue_30);
            }

            final boolean finalBStatue = bStatue;

            String strType = "";
            if ( null != arrayList.get(i).SERVICETYPE)
                strType = arrayList.get(i).SERVICETYPE;

//            final EServiceType serviceType = EServiceType.valueOf(extraServiceHolder.tvServiceNum.getText().toString()); //alBoardingPass.get(i).getServiceType();
//            switch (serviceType){
            switch (strType){
                case "WIFI":
//                case WIFI:
                    extraServiceHolder.ivServiceType.setBackgroundResource(iWifiRes);
                    extraServiceHolder.tvServiceType.setText(m_context.getString(R.string.wifi));
                    break;
                case "VIP":
//                case VIP_LOUNGE:
                    extraServiceHolder.ivServiceType.setBackgroundResource(iVipRes);
                    extraServiceHolder.tvServiceType.setText(m_context.getString(R.string.vip_lounge));
                    break;
                case "EB":
                    extraServiceHolder.ivServiceType.setBackgroundResource(iBagRes);
                    extraServiceHolder.tvServiceType.setText(m_context.getString(R.string.eb_extra_baggage));
                    break;
                case "ExtraBaggage":
//                case EXTRA_BAGGAGE:
                    extraServiceHolder.ivServiceType.setBackgroundResource(iBagRes);
                    extraServiceHolder.tvServiceType.setText(m_context.getString(R.string.extra_baggage));
                    break;
                case "THSR":
//                case HSR:
                    extraServiceHolder.ivServiceType.setBackgroundResource(iHsrRes);
                    extraServiceHolder.tvServiceType.setText(m_context.getString(R.string.trips_detail_hsr));
                    break;
                case "FamilyCouch":
//                case FAMILY:
                    extraServiceHolder.ivServiceType.setBackgroundResource(iFamilyRes);
                    extraServiceHolder.tvServiceType.setText(m_context.getString(R.string.family_couch));
                    break;
                case "EVENT":
//                case FAMILY:
                    extraServiceHolder.ivServiceType.setBackgroundResource(iVipRes);
                    extraServiceHolder.tvServiceType.setText(m_context.getString(R.string.event_vip));
                    break;
            }

            //要跳轉至CIBoardingPassActivity的按鈕
            extraServiceHolder.rl_extra_service_click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Activity activity = (Activity) m_context;
                    Bitmap bitmap = ImageHandle.getScreenShot(activity);
                    Bitmap blur   = ImageHandle.BlurBuilder(m_context, bitmap, 13.5f, 0.15f);

                    Bundle bundle = new Bundle();
                    bundle.putString(UiMessageDef.BUNDLE_ACTIVITY_MODE,
                            arrayList.get(finalI).SERVICETYPE);
                    bundle.putBoolean(CIExtraServiceItem.DEF_IS_EXPIRED_TAG,
                            finalBStatue);
                            //m_arItemList.get(position).getIsExpired());
                    bundle.putParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG, blur);
                    bundle.putSerializable(UiMessageDef.BUNDLE_EWALLET_EXTRA_SERVICES_DATA,
                            arrayList.get(finalI));
                    Intent intent = new Intent();
                    intent.putExtras(bundle);

                    intent.setClass(m_context, CIExtraServicesCardActivity.class);
                    activity.startActivity(intent);

                    activity.overridePendingTransition(R.anim.anim_alpha_in, 0);

                    bitmap.recycle();
//                    System.gc();

                }
            });
        }
    }

    //head欄 顯示行程日期 & 要從卡個機場飛去哪個機場
    public class ItemHolder extends RecyclerView.ViewHolder {
        RelativeLayout  rl_root;
        RelativeLayout  rlayout_head;
        LinearLayout    llayout_body;
        TextView        tvDate;
        TextView        tvGoto;
        View            line;

        public ItemHolder(View view) {
            super(view);
            rl_root      = (RelativeLayout) view.findViewById(R.id.root);
            rlayout_head = (RelativeLayout) view.findViewById(R.id.rlayout_head);
            tvDate       = (TextView) view.findViewById(R.id.tv_Date);
            tvGoto       = (TextView) view.findViewById(R.id.tv_goto);
            llayout_body = (LinearLayout) view.findViewById(R.id.llayout_body);
            line         = view.findViewById(R.id.v_line);
        }
    }

    //body欄 顯示每一服務 乘客姓名、服務編號、服務類別...
    private class ExtraServiceHolder extends RecyclerView.ViewHolder{
        RelativeLayout rl_extra_service_click;
        TextView       tvName;
        TextView       tvServiceNum;
        TextView       tvServiceType;
        ImageView      ivServiceType;
        View           vLineTop;
        View           vExtraServiceBg;
        View           vRight;
        ImageView      ivUsed;

        private ExtraServiceHolder(View view) {
            super(view);
            rl_extra_service_click = (RelativeLayout) view.findViewById(R.id.ll_extra_service_click);
            tvName = (TextView) view.findViewById(R.id.tv_name_value);
            tvServiceNum = (TextView) view.findViewById(R.id.tv_service_num_value);
            tvServiceType = (TextView) view.findViewById(R.id.tv_service);
            ivServiceType = (ImageView) view.findViewById(R.id.iv_service);
            vLineTop = view.findViewById(R.id.v_line1);
            vExtraServiceBg = view.findViewById(R.id.v_extra_service_bg);
            vRight = view.findViewById(R.id.vRight);
            ivUsed = (ImageView)view.findViewById(R.id.iv_used);
        }
    }

    //新增服務按鈕
//    our footer RecyclerView.ViewHolder is just a FrameLayout
    public class FooterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        RelativeLayout m_rlLine;
        View           m_vLine1;
        View           m_vLine2;
        RelativeLayout m_rlAdd;
        TextView       m_tvNotFind;
        Button         m_btnAdd;

        public FooterViewHolder(View footerView) {
            super(footerView);
            m_rlLine = (RelativeLayout) footerView.findViewById(R.id.rl_line);
            m_vLine1 = (View) footerView.findViewById(R.id.vline1);
            m_vLine2 = (View) footerView.findViewById(R.id.vline2);
            m_rlAdd = (RelativeLayout) footerView.findViewById(R.id.rl_add);
            m_tvNotFind = (TextView) footerView.findViewById(R.id.tv_not_find);
            m_btnAdd = (Button) footerView.findViewById(R.id.btn_add);
            m_btnAdd.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            m_Listener.OnGotoExtraServicesFragmentClick();
        }
    }

    private void prepareFooter(FooterViewHolder vh) {

        //--- add view自適應---//
        m_vScaleDef.setMargins(vh.m_rlLine, 0, 50, 0, 10);

        int iline = 1;
        if ( 0 < m_vScaleDef.getLayoutHeight(0.3) ){
            iline = m_vScaleDef.getLayoutHeight(0.3);
        }
        RelativeLayout.LayoutParams rp = (RelativeLayout.LayoutParams) vh.m_vLine1.getLayoutParams();
        rp.height = iline;

        rp = (RelativeLayout.LayoutParams) vh.m_vLine2.getLayoutParams();
        rp.height = iline;

        m_vScaleDef.setPadding(vh.m_rlAdd, 20, 16, 20, 16);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)vh.m_tvNotFind.getLayoutParams();
        lp.width = m_vScaleDef.getLayoutWidth(170);
        m_vScaleDef.setTextSize(m_vScaleDef.DEF_TEXT_SIZE_16, vh.m_tvNotFind);

        lp = (RelativeLayout.LayoutParams)vh.m_btnAdd.getLayoutParams();
        lp.width = m_vScaleDef.getLayoutWidth(130);
        lp.height = m_vScaleDef.getLayoutHeight(40);
        lp.leftMargin = m_vScaleDef.getLayoutWidth(20);
        m_vScaleDef.setTextSize(m_vScaleDef.DEF_TEXT_SIZE_16, vh.m_btnAdd);
        //--- add view自適應---//
    }
}
