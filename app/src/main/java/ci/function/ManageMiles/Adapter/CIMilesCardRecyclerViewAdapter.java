package ci.function.ManageMiles.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.ManageMiles.item.CIAccumulationGroupItem;
import ci.function.ManageMiles.item.CIAccumulationItem;
import ci.ui.define.ViewScaleDef;

/**
 * Created by jlchen on 2016/3/9.
 */
public class CIMilesCardRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public interface onCIMilesCardAdapterListener{
        void onCardClick( CIAccumulationItem item );
    }

    private onCIMilesCardAdapterListener m_Listener;

    public enum MilesCardType {
        ACCUMULATION, REDEEM, AWARD_TRANSFER;
    }

    private MilesCardType m_type = null;

    private Context m_context = null;
    private ViewScaleDef m_vScaleDef = null;

    private ArrayList<CIAccumulationGroupItem> m_arList = new ArrayList<>();

    public CIMilesCardRecyclerViewAdapter(Context context,
                              ArrayList<CIAccumulationGroupItem> arDataList,
                              MilesCardType type,
                              onCIMilesCardAdapterListener listener){
        this.m_context      = context;
        this.m_vScaleDef    = ViewScaleDef.getInstance(m_context);
        this.m_arList       = arDataList;
        this.m_type         = type;
        this.m_Listener     = listener;
    }


    @Override
    public int getItemCount() {
        return m_arList == null ? 0 : m_arList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_view_miles_activity_card, parent, false);

        return new HeadHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        setTextAndViewSize_Head((HeadHolder) holder, position);
    }

    private void setTextAndViewSize_Head(HeadHolder holder, int position){

        holder.tvYear.setText(m_arList.get(position).GetYear());

        switch ( m_type ){
            case ACCUMULATION:
                holder.tvHeadData.setText(m_context.getString(R.string.description_accumulation_date));
                holder.tvHeadMile.setVisibility(View.VISIBLE);
                break;
            case REDEEM:
                holder.tvHeadData.setText(m_context.getString(R.string.awards_redeem_date));
                holder.tvHeadMile.setVisibility(View.VISIBLE);
                break;
            case AWARD_TRANSFER:
                holder.tvHeadData.setText(m_context.getString(R.string.awards_transfer_date));
                holder.tvHeadMile.setVisibility(View.GONE);
                break;
        }

        holder.llData.removeAllViews();

        ArrayList<CIAccumulationItem> arrayList = m_arList.get(position).GetAccumulationList();

        for (int i = 0; i < arrayList.size(); i++){

            View vItem = LayoutInflater.from(m_context).inflate(
                    R.layout.layout_view_miles_activity_item, null);

            ItemHolder itemHolder = new ItemHolder(vItem);

            if ( i == 0 ){
                itemHolder.vTop.setVisibility(View.VISIBLE);
            }else {
                itemHolder.vTop.setVisibility(View.GONE);
            }

            setTextAndViewSizeItem(itemHolder, arrayList.get(i));

            holder.llData.addView(vItem);
        }

//        m_vScaleDef.selfAdjustAllView(holder.llData);

        RelativeLayout.LayoutParams rp = (RelativeLayout.LayoutParams) holder.llYear.getLayoutParams();
        rp.height = m_vScaleDef.getLayoutHeight(45.7);
        m_vScaleDef.setPadding(holder.llYear, 20, 12, 20, 12);

        m_vScaleDef.setTextSize(18, holder.tvYear);
        holder.tvYear.setMinHeight(m_vScaleDef.getLayoutHeight(21.7));

        m_vScaleDef.setPadding(holder.rlListHead, 20, 0, 20, 0);
        holder.rlListHead.getLayoutParams().height = m_vScaleDef.getLayoutHeight(24);

        rp = (RelativeLayout.LayoutParams) holder.tvHeadData.getLayoutParams();
        rp.width = m_vScaleDef.getLayoutWidth(230);
        m_vScaleDef.setTextSize(13, holder.tvHeadData);

        rp = (RelativeLayout.LayoutParams) holder.tvHeadMile.getLayoutParams();
        rp.width = m_vScaleDef.getLayoutWidth(50);
        rp.leftMargin = m_vScaleDef.getLayoutWidth(20);
        m_vScaleDef.setTextSize(13, holder.tvHeadMile);

        holder.llBottom.getLayoutParams().height = m_vScaleDef.getLayoutHeight(20);

        holder.vDiv.getLayoutParams().height = m_vScaleDef.getLayoutHeight(10);
    }

    private void setTextAndViewSizeItem(final ItemHolder iHolder, final CIAccumulationItem item) {

        iHolder.rlBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_Listener.onCardClick(item);

            }
        });

        iHolder.tvDescription.setText(item.m_strDescription);
        //統一調整時間顯示格式 by ryan 2016-08-16
        iHolder.tvDate.setText(item.m_strDate);
        //iHolder.tvDate.setText(AppInfo.getInstance(m_context).ConvertDateFormatToyyyyMMddEEE(item.m_strDate));
        iHolder.tvMile.setText(item.m_strMiles);

        switch ( m_type ){
            case ACCUMULATION:
                iHolder.tvMile.setTextColor( ContextCompat.getColor(m_context, R.color.french_blue));
                break;
            case REDEEM:
                iHolder.tvMile.setTextColor(ContextCompat.getColor(m_context, R.color.pinkish_red));

                if( true == item.m_bTransfer ){
                    iHolder.tvTransfer.setVisibility(View.VISIBLE);
                }else {
                    iHolder.tvTransfer.setVisibility(View.GONE);
                }

                break;
            case AWARD_TRANSFER:
                iHolder.tvMile.setVisibility(View.GONE);

                if( true == item.m_bTransfer ){
                    iHolder.tvTransfer.setVisibility(View.VISIBLE);
                }else {
                    iHolder.tvTransfer.setVisibility(View.GONE);
                }

                break;
        }

        //自適應
        iHolder.vTop.getLayoutParams().height = m_vScaleDef.getLayoutHeight(10);

        RelativeLayout.LayoutParams rp = (RelativeLayout.LayoutParams)iHolder.rlItem.getLayoutParams();
        rp.height = m_vScaleDef.getLayoutHeight(70);
        rp.leftMargin = m_vScaleDef.getLayoutWidth(20);
        rp.rightMargin = m_vScaleDef.getLayoutWidth(20);

        iHolder.rlLeft.getLayoutParams().width = m_vScaleDef.getLayoutWidth(198);

        m_vScaleDef.setTextSize(18, iHolder.tvDescription);

        rp = (RelativeLayout.LayoutParams)iHolder.llLeft .getLayoutParams();
        rp.topMargin = m_vScaleDef.getLayoutHeight(4);

        m_vScaleDef.setTextSize(13, iHolder.tvDate);

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) iHolder.tvTransfer.getLayoutParams();
        lp.width = m_vScaleDef.getLayoutWidth(70);
        lp.height = m_vScaleDef.getLayoutHeight(16);
        lp.leftMargin = m_vScaleDef.getLayoutWidth(5);
        m_vScaleDef.setTextSize(13, iHolder.tvTransfer);

        rp = (RelativeLayout.LayoutParams)iHolder.tvMile .getLayoutParams();
        rp.width = m_vScaleDef.getLayoutWidth(92);
        rp.leftMargin = m_vScaleDef.getLayoutWidth(10);
        m_vScaleDef.setTextSize(24, iHolder.tvMile);

        int i = 1;
        if ( 0 < m_vScaleDef.getLayoutHeight(1) )
            i = m_vScaleDef.getLayoutHeight(1);
        rp = (RelativeLayout.LayoutParams)iHolder.vLine.getLayoutParams();
        rp.leftMargin = m_vScaleDef.getLayoutWidth(20);
        rp.rightMargin = m_vScaleDef.getLayoutWidth(20);
        rp.height = i;
    }

    //head
    public class HeadHolder extends RecyclerView.ViewHolder {

        RelativeLayout  rlRoot;
        LinearLayout    llYear;
        TextView        tvYear;
        TextView        tvHeadData;
        TextView        tvHeadMile;
        LinearLayout    llList;
        RelativeLayout  rlListHead;
        LinearLayout    llData;
        LinearLayout    llBottom;
        View            vDiv;

        public HeadHolder(View hLayoutView) {
            super(hLayoutView);

            rlRoot      = (RelativeLayout)hLayoutView.findViewById(R.id.rl_list_root);
            llYear      = (LinearLayout)hLayoutView.findViewById(R.id.ll_year);
            tvYear      = (TextView)hLayoutView.findViewById(R.id.tv_year);
            tvHeadData  = (TextView)hLayoutView.findViewById(R.id.tv_head_data);
            tvHeadMile  = (TextView)hLayoutView.findViewById(R.id.tv_miles_head);
            llList      = (LinearLayout)hLayoutView.findViewById(R.id.ll_list);
            rlListHead  = (RelativeLayout)hLayoutView.findViewById(R.id.rl_list_head);
            llData      = (LinearLayout)hLayoutView.findViewById(R.id.ll_list_data);
            llBottom    = (LinearLayout)hLayoutView.findViewById(R.id.ll_bottom);
            vDiv        = (View)hLayoutView.findViewById(R.id.vDiv);
        }
    }

    //body
    public class ItemHolder extends RecyclerView.ViewHolder {

        RelativeLayout rlBg;
        View        vTop;
        RelativeLayout rlItem;
        RelativeLayout rlLeft;
        LinearLayout llLeft;
        TextView    tvDescription;
        TextView    tvDate;
        TextView    tvMile;
        TextView    tvTransfer;
        View        vLine;

        public ItemHolder(View itemLayoutView) {
            super(itemLayoutView);

            rlBg = (RelativeLayout)itemLayoutView.findViewById(R.id.list_root);
            vTop = (View)itemLayoutView.findViewById(R.id.v_top);
            rlItem = (RelativeLayout)itemLayoutView.findViewById(R.id.rlayout);
            rlLeft = (RelativeLayout)itemLayoutView.findViewById(R.id.rl_left);
            llLeft = (LinearLayout)itemLayoutView.findViewById(R.id.ll_left_bottom);
            tvDescription = (TextView)itemLayoutView.findViewById(R.id.tv_description);
            tvDescription = (TextView)itemLayoutView.findViewById(R.id.tv_description);
            tvDate = (TextView)itemLayoutView.findViewById(R.id.tv_date_data);
            tvMile = (TextView)itemLayoutView.findViewById(R.id.tv_miles_data);
            tvTransfer = (TextView)itemLayoutView.findViewById(R.id.tv_transfer);
            vLine = (View)itemLayoutView.findViewById(R.id.v_line);
        }
    }
}
