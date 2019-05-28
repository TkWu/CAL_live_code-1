package ci.function.BaggageTrack.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.Core.CIApplication;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;
import ci.ws.Models.entities.CIBaggageInfoNumEntity;
import ci.ws.Models.entities.CIBaggageInfoResp;
import ci.ws.Models.entities.CIBaggageInfoResp_Pax;


public class CIBaggageTrackingRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public interface onRecyclerViewAdapterListener{
        void onClick_GotoAdd();

        void onBaggageInfoClick( String strDeparture_Date, String strDeparture_Station, String strArrival_Station, String strBaggage_Number, String strBaggage_ShowNumber );
    }

    private onRecyclerViewAdapterListener m_Listener;

    enum EType{
        TYPE_ITEM,TYPE_FOOTER
    }


    private Context m_context = null;
    private ViewScaleDef m_vScaleDef;
    private ArrayList<CIBaggageInfoResp> m_arItemList = new ArrayList<>();

    public CIBaggageTrackingRecyclerViewAdapter(Context context,
                                                ArrayList<CIBaggageInfoResp> arDataList,
                                                onRecyclerViewAdapterListener listener) {
        this.m_context = context;
        this.m_vScaleDef = ViewScaleDef.getInstance(m_context);
        this.m_Listener = listener;
        this.m_arItemList = arDataList;
    }

    @Override
    public int getItemCount() {

        return m_arItemList == null ? 1 : m_arItemList.size() + 1;
    }

    public void setDataList( ArrayList<CIBaggageInfoResp> arDataList ){
        this.m_arItemList = arDataList;
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
                                      .inflate(R.layout.layout_baggage_tracking_list_view, parent, false);

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

        //--- head自適應 ---//
        RelativeLayout.LayoutParams rlParams = (RelativeLayout.LayoutParams)holder.rlayout_head.getLayoutParams();
        rlParams.width = m_vScaleDef.getLayoutWidth(340);
        rlParams.topMargin = m_vScaleDef.getLayoutHeight(20);
        rlParams.bottomMargin = m_vScaleDef.getLayoutHeight(10);

        m_vScaleDef.setViewSize(holder.tvDate, 160, 19.3);
        m_vScaleDef.setTextSize(16, holder.tvDate);
        m_vScaleDef.setViewSize(holder.tvGoto, 160, 19.3);
        m_vScaleDef.setTextSize(16, holder.tvGoto);

        rlParams = (RelativeLayout.LayoutParams)holder.line.getLayoutParams();
        rlParams.height = m_vScaleDef.getLayoutHeight(30);
        //--- head自適應 ---//


        String strFromTo = String.format(m_context.getString(R.string.my_trips_goto),
                m_arItemList.get(position).Departure_Station,
                m_arItemList.get(position).Arrival_Station);


        holder.tvDate.setText(m_arItemList.get(position).Departure_Date);
        holder.tvGoto.setText(strFromTo);


        prepareInfo(holder, position);
    }

    private void prepareInfo(final ItemHolder holder, final int position){
        holder.llayout_body.removeAllViews();

        LayoutInflater layoutInflater =
                (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if ( null == m_arItemList.get(position).Pax )
            return;

        final ArrayList<CIBaggageInfoResp_Pax> arPaxList = m_arItemList.get(position).Pax;

        for ( int iIdx = 0; iIdx < arPaxList.size(); iIdx++ ){

            CIBaggageInfoResp_Pax pax_Info = arPaxList.get(iIdx);
            for ( final CIBaggageInfoNumEntity baggageInfo : pax_Info.Baggage_Number ){

                //中間 航班代號和飛行時間等資訊
                View vCardView = layoutInflater.inflate(R.layout.fragment_boarding_pass_background_card_receipt_item, null);
                final ViewHolder viewHolder = new ViewHolder(vCardView);

                //填入資訊
                String strName = "";
                if ( null != pax_Info.First_Name )
                    strName = pax_Info.First_Name;
                if ( null != pax_Info.Last_Name )
                    strName = strName + " " + pax_Info.Last_Name;

                viewHolder.tvName.setText(strName);
                viewHolder.tvNum.setText(baggageInfo.Baggage_ShowNumber);
                viewHolder.vLine.setBackgroundColor(ContextCompat.getColor(m_context, R.color.french_blue));

                //要跳轉的按鈕
                final String strShowNum = baggageInfo.Baggage_ShowNumber;
                vCardView.setTag(baggageInfo.Baggage_BarcodeNumber);
                vCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String strBaggage_Number    = (String)v.getTag();

                        if ( null != m_Listener ){
                            m_Listener.onBaggageInfoClick(  m_arItemList.get(position).Departure_Date,
                                                            m_arItemList.get(position).Departure_Station,
                                                            m_arItemList.get(position).Arrival_Station,
                                                            strBaggage_Number,
                                                            strShowNum);
                        }
                    }
                });

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(340, 113);
                layoutParams.topMargin = 10;
                layoutParams.leftMargin = 10;
                layoutParams.rightMargin = 10;

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)viewHolder.tvName.getLayoutParams();
                params.leftMargin = 20;

                params = (RelativeLayout.LayoutParams)viewHolder.tvNum.getLayoutParams();
                params.leftMargin = 20;

                params = (RelativeLayout.LayoutParams)viewHolder.iv_barcode.getLayoutParams();
                params.leftMargin = 6;

                        //加入資訊
                holder.llayout_body.addView(vCardView, layoutParams);

                m_vScaleDef.selfAdjustAllView(vCardView.findViewById(R.id.root));
                HandlerThread handlerThread = new HandlerThread("workThread");
                handlerThread.start();

                Handler workHandler = new Handler(handlerThread.getLooper());
                workHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        if(TextUtils.isEmpty(baggageInfo.Baggage_BarcodeNumber)) {
                            return;
                        }

                        final Bitmap bitmap = ImageHandle.encodeToBarcode(baggageInfo.Baggage_BarcodeNumber,
                                m_vScaleDef.getLayoutWidth(236.7),
                                m_vScaleDef.getLayoutHeight(32));
                        holder.llayout_body.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                viewHolder.iv_barcode.setBackground(new BitmapDrawable(null , bitmap));
                                viewHolder.iv_barcode.startAnimation(AnimationUtils.loadAnimation(CIApplication.getContext(), R.anim.anim_alpha_in));
                            }
                        },500);
                    }
                });
            }
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
    private class ViewHolder extends RecyclerView.ViewHolder{

        TextView       tvName;
        TextView       tvNum;
        ImageView      iv_barcode;
        View           vLine;
        ImageView      imgArrow;

        private ViewHolder(View view) {
            super(view);

            tvName = (TextView) view.findViewById(R.id.tv_name);
            tvNum = (TextView) view.findViewById(R.id.tv_bag_num);
            iv_barcode = (ImageView) view.findViewById(R.id.iv_barcode);
            vLine = view.findViewById(R.id.v_line);
            imgArrow = (ImageView)view.findViewById(R.id.iv_arrow);
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
            if ( null != m_Listener ){
                m_Listener.onClick_GotoAdd();
            }
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
        vh.m_tvNotFind.setText(R.string.baggage_tracking_can_not_find_you_baggage);

        lp = (RelativeLayout.LayoutParams)vh.m_btnAdd.getLayoutParams();
        lp.width = m_vScaleDef.getLayoutWidth(130);
        lp.height = m_vScaleDef.getLayoutHeight(40);
        lp.leftMargin = m_vScaleDef.getLayoutWidth(20);
        m_vScaleDef.setTextSize(m_vScaleDef.DEF_TEXT_SIZE_16, vh.m_btnAdd);
        vh.m_btnAdd.setText(R.string.baggage_tracking_can_not_find_button_find);
        //--- add view自適應---//
    }

}
