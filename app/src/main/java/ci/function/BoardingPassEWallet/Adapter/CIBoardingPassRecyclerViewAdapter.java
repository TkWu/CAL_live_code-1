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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.Locale;

import ci.function.BoardingPassEWallet.BoardingPass.CIBoardingPassCardActivity;
import ci.function.Core.CIApplication;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;
import ci.ui.view.ImageHandle;
import ci.ws.Models.entities.CIBoardPassResp_Itinerary;
import ci.ws.Models.entities.CIBoardPassResp_PaxInfo;

/**
 * Created by kevincheng on 2016/3/25.
 */
public class CIBoardingPassRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context                     m_context 		= null;
    private ViewScaleDef                m_vScaleDef;
    private ArrayList<CIBoardPassResp_Itinerary> m_arItemList = new ArrayList<>();


    public CIBoardingPassRecyclerViewAdapter(Context context,
                                             ArrayList<CIBoardPassResp_Itinerary> arDataList){
        this.m_context      = context;
        this.m_vScaleDef    = ViewScaleDef.getInstance(m_context);
        this.m_arItemList   = arDataList;
    }

    @Override
    public int getItemCount() {
        return m_arItemList == null ? 1 : m_arItemList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_boarding_pass_info_and_extra_service_view, parent, false);

        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        prepareItem((ItemHolder) holder, position);

    }

    private void prepareItem(ItemHolder holder, int position){

        if ( null == m_arItemList.get(position).Departure_Date )
            m_arItemList.get(position).Departure_Date = "";

        //統一時間顯示邏輯 by ryan  2016-08-16
        String strDate = m_arItemList.get(position).Departure_Date;
//        String strDate = AppInfo.getInstance(m_context).ConvertDateFormatToyyyyMMddEEE(
//                m_arItemList.get(position).Departure_Date);

// 2016-12-20 Modify by Ryan for 移除過期的邏輯判斷！
//        TODO: 暫時判斷是否已使用
//        final boolean bIsExpired =
//                AppInfo.getInstance(m_context).bIsExpired(
//                        m_arItemList.get(position).Departure_Date);
//        if ( true == bIsExpired && 0 < strDate.length() )
//            strDate = strDate +" "+ m_context.getString(R.string.boarding_pass_expired);

        if ( null == m_arItemList.get(position).Departure_Station )
            m_arItemList.get(position).Departure_Station = "";
        if ( null == m_arItemList.get(position).Arrival_Station )
            m_arItemList.get(position).Arrival_Station = "";

        String strFromTo = String.format(m_context.getString(R.string.my_trips_goto),
                m_arItemList.get(position).Departure_Station,
                m_arItemList.get(position).Arrival_Station);

        //填入資料
        holder.tvDate.setText(strDate);
        holder.tvGoto.setText(strFromTo);

        //--- head自適應 ---//
        m_vScaleDef.setTextSize(16,holder.tvDate);
        m_vScaleDef.setTextSize(16,holder.tvGoto);
        m_vScaleDef.setMargins(holder.tvGoto, 20, 0, 0, 0);
        m_vScaleDef.setViewSize(holder.rlayout_head, 340, 19.3);
        m_vScaleDef.setViewSize(holder.line, ViewGroup.LayoutParams.MATCH_PARENT, 30);
        //--- head自適應 ---//

        prepareInfo(holder, position);

    }

    private void prepareInfo(final ItemHolder holder, final int position){
        holder.llayout_body.removeAllViews();

        LayoutInflater layoutInflater =
                (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final CIBoardPassResp_Itinerary itinerary = m_arItemList.get(position);
        ArrayList<CIBoardPassResp_PaxInfo> alBoardingPass = (ArrayList)itinerary.Pax_Info;

        for ( int i = 0; i < alBoardingPass.size(); i++){
            //中間 航班代號和飛行時間等資訊
            View vBoardingPass = layoutInflater.inflate(R.layout.layout_boarding_pass_info_card_view, null);
            final BoardingPassHolder boardingPassHolder = new BoardingPassHolder(vBoardingPass);

            if ( null == alBoardingPass.get(i).First_Name )
                alBoardingPass.get(i).First_Name = "";
            if ( null == alBoardingPass.get(i).Last_Name )
                alBoardingPass.get(i).Last_Name = "";
            String strName = alBoardingPass.get(i).First_Name +" "+ alBoardingPass.get(i).Last_Name;

            if ( null == alBoardingPass.get(i).Seat_Number)
                alBoardingPass.get(i).Seat_Number = "";

            //填入資訊
            boardingPassHolder.tvName.setText(strName);
            boardingPassHolder.tvSeat.setText(alBoardingPass.get(i).Seat_Number);

            //--- body自適應 ---//
            m_vScaleDef.selfAdjustAllView(vBoardingPass.findViewById(R.id.root));
            m_vScaleDef.selfAdjustSameScaleView(boardingPassHolder.ivUsed, 80.7, 68.7);

            //加入登機資訊
            holder.llayout_body.addView(vBoardingPass);

            //2016-12-20 by Ryan, 不顯示已過期的圖示
            final boolean bIsExpired =false;
//            final boolean bIsExpired =
//                    AppInfo.getInstance(m_context).bIsExpired(
//                            m_arItemList.get(position).Departure_Date);
//            if ( true == bIsExpired ){
//                boardingPassHolder.ivUsed.setVisibility(View.VISIBLE);
//                Locale locale = CIApplication.getLanguageInfo().getLanguage_Locale();
//                switch (locale.toString()){
//                    case "zh_TW":
//                        boardingPassHolder.ivUsed.setImageResource(R.drawable.img_used_zh_tw);
//                        break;
//                    case "zh_CN":
//                        boardingPassHolder.ivUsed.setImageResource(R.drawable.img_used_zh_cn);
//                        break;
//                    case "en":
//                        boardingPassHolder.ivUsed.setImageResource(R.drawable.img_used_en);
//                        break;
//                    case "ja_JP":
//                        boardingPassHolder.ivUsed.setImageResource(R.drawable.img_used_ja);
//                        break;
//                    default:
//                        boardingPassHolder.ivUsed.setImageResource(R.drawable.img_used_zh_tw);
//                        break;
//                }
//
//                boardingPassHolder.tvName.setTextColor(ContextCompat.getColor(m_context,R.color.white_four));
//                boardingPassHolder.tvSeatTitle.setTextColor(ContextCompat.getColor(m_context,R.color.powder_blue));
//                boardingPassHolder.tvSeat.setTextColor(ContextCompat.getColor(m_context,R.color.white_four));
//                boardingPassHolder.vBoardingPassBg.setBackgroundResource(R.drawable.bg_btn_radius_3dp_dark_periwinkle);
//            } else {
                boardingPassHolder.ivUsed.setVisibility(View.GONE);
                boardingPassHolder.tvName.setTextColor(ContextCompat.getColor(m_context,R.color.black_one));
                boardingPassHolder.tvSeatTitle.setTextColor(ContextCompat.getColor(m_context,R.color.grey_four));
                boardingPassHolder.tvSeat.setTextColor(ContextCompat.getColor(m_context,R.color.french_blue));
                boardingPassHolder.vBoardingPassBg.setBackgroundResource(R.drawable.bg_btn_radius_3dp_white_four_white_six);
//            }

            //乘客index
            final int finalIndex = i;
            //要跳轉至CIBoardingPassActivity的按鈕
            boardingPassHolder.rl_boarding_pass_click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Activity activity = (Activity) m_context;

                    Bitmap bitmap = ImageHandle.getScreenShot(activity);
                    Bitmap blur = ImageHandle.BlurBuilder(m_context, bitmap, 13.5f, 0.15f);

                    Bundle bundle = new Bundle();
                    //傳入是否已使用登機證的tag
                    bundle.putBoolean(UiMessageDef.BUNDLE_BOARDING_PASS_IS_EXPIRED_TAG, bIsExpired);
                    //當前點擊的是第幾個乘客
                    bundle.putInt(UiMessageDef.BUNDLE_BOARDING_PASS_INDEX, finalIndex);
                    bundle.putSerializable(UiMessageDef.BUNDLE_BOARDING_PASS_DATA, itinerary);
                    bundle.putParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG, blur);

                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(m_context, CIBoardingPassCardActivity.class);
                    activity.startActivity(intent);

                    activity.overridePendingTransition(R.anim.anim_alpha_in, 0);

                    bitmap.recycle();
                    System.gc();
                }
            });

        }
    }

    //head欄 顯示行程日期 & 要從卡個機場飛去哪個機場
    public static class ItemHolder extends RecyclerView.ViewHolder {

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

    //body欄 顯示每一登機證 乘客姓名&編號,
    private static class BoardingPassHolder extends RecyclerView.ViewHolder{
        RelativeLayout rl_boarding_pass_click;
        TextView       tvName;
        TextView       tvSeatTitle;
        TextView       tvSeat;
        View           vLineTop;
        View           vBoardingPassBg;
        ImageView      ivUsed;

        private BoardingPassHolder(View view) {
            super(view);
            rl_boarding_pass_click = (RelativeLayout) view.findViewById(R.id.ll_boarding_pass_click);
            tvName = (TextView) view.findViewById(R.id.tv_name_value);
            tvSeatTitle = (TextView) view.findViewById(R.id.tv_seat);
            tvSeat = (TextView) view.findViewById(R.id.tv_seat_value);
            vLineTop = view.findViewById(R.id.v_line1);
            vBoardingPassBg = view.findViewById(R.id.v_boarding_pass_bg);
            ivUsed = (ImageView)view.findViewById(R.id.iv_used);
        }
    }
}