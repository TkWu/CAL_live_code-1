package ci.function.BaggageTrack;

/**
 * Created by user on 2016/4/11.
 */

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.Base.BaseNoToolbarActivity;
import ci.function.Core.CIApplication;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;
import ci.ui.view.ShadowBar.ShadowBarRecycleView;
import ci.ws.Models.entities.CIBaggageInfoContentResp;

public class CIBaggageInfoContentActivity extends BaseNoToolbarActivity {


    private RelativeLayout          m_rlBackground  = null;
    private ImageButton             m_ivbtn_close   = null;

    private Bitmap                  m_bitmapBlur    = null;

    private ShadowBarRecycleView    m_ShadowView = null;
    private RecyclerView            m_RecyclerView = null;
    private CIBaggageInfoAdapter    m_RecyclerAdpter = null;

    private String                  m_strBaggageNum = "";
    private String                  m_strDepartureDate = "";
    private String                  m_strDepartureStation = "";
    private String                  m_strArrivalStation = "";

    private ArrayList<CIBaggageInfoContentResp> m_alBaggageData = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_baggageinfo_content;
    }

    @Override
    protected void initialLayoutComponent() {
        Bundle bundle   = getIntent().getExtras();
        //背景
        m_bitmapBlur    = bundle.getParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG);
        m_strBaggageNum = bundle.getString(UiMessageDef.BUNDLE_BAGGAGE_CONTENT_NUMBER, "");
        m_strDepartureDate = bundle.getString(UiMessageDef.BUNDLE_BAGGAGE_CONTENT_DEPARTUREDATE, "");
        m_strDepartureStation = bundle.getString(UiMessageDef.BUNDLE_BAGGAGE_CONTENT_DEPARTURESTATION, "");
        m_strArrivalStation = bundle.getString(UiMessageDef.BUNDLE_BAGGAGE_CONTENT_ARRIVALSTATION, "");
        m_alBaggageData = (ArrayList<CIBaggageInfoContentResp>) bundle.getSerializable(UiMessageDef.BUNDLE_BAGGAGE_CONTENT_RESP);
        if ( null == m_alBaggageData ){
            m_alBaggageData = new ArrayList<>();
        }

        m_rlBackground  = (RelativeLayout)findViewById(R.id.rl_bg);
        m_ivbtn_close   = (ImageButton) findViewById(R.id.ivbtn_close);
        m_ivbtn_close.setOnClickListener(m_onClick);

        //設定模糊背景
        BitmapDrawable drawable = new BitmapDrawable(m_Context.getResources(), m_bitmapBlur);
        m_rlBackground.setBackground(drawable);

        m_ShadowView = (ShadowBarRecycleView)findViewById(R.id.sv_Recycleview);
        m_RecyclerView = m_ShadowView.getRecyclerView();
        m_RecyclerView.setHasFixedSize(true);

        m_RecyclerAdpter = new CIBaggageInfoAdapter();
        m_RecyclerView.setAdapter(m_RecyclerAdpter);

        m_RecyclerAdpter.notifyDataSetChanged();
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        //自適應
        ViewScaleDef vScale = ViewScaleDef.getInstance(m_Context);
        vScale.selfAdjustAllView(findViewById(R.id.root));
        vScale.selfAdjustSameScaleView(findViewById(R.id.iv_flight_from_to), 30, 30);
        vScale.selfAdjustSameScaleView(m_ivbtn_close, 40, 40);

        m_ShadowView.setShadowBarHeight(vScaleDef.getLayoutHeight(16));

        //因繼承 FragmentActivity 所以語系要另外更新一次
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.locale = CIApplication.getLanguageInfo().getLanguage_Locale();
        resources.updateConfiguration(config, dm);
        //

        TextView tvBaggageNum = (TextView)findViewById(R.id.tv_baggage_num);
        tvBaggageNum.setText(getString(R.string.baggage_info_content_baggage_tag) + m_strBaggageNum );
        TextView tvDate = (TextView)findViewById(R.id.tv_date);
        tvDate.setText(m_strDepartureDate);

        TextView tvDeparture = (TextView)findViewById(R.id.tv_departure_station);
        tvDeparture.setText(m_strDepartureStation);
        TextView tvArrival = (TextView)findViewById(R.id.tv_arrival_station);
        tvArrival.setText(m_strArrivalStation);
    }

    View.OnClickListener m_onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CIBaggageInfoContentActivity.this.finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ImageHandle.recycleBitmap(m_bitmapBlur);
        System.gc();
    }



    class CIBaggageInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        ViewScaleDef m_vScaleDef;

        public static final int TYPE_HEAD = 3000;
        public static final int TYPE_ITEM = 3001;

        private int iHeaderCnt = 1;

        CIBaggageInfoAdapter(){
            this.m_vScaleDef    = ViewScaleDef.getInstance(m_Context);
        }

        @Override
        public int getItemViewType(int position) {
            //if ( !TextUtils.isEmpty(m_alBaggageData.get(position).strTitle) ){
            if ( 0 == position ){
                return TYPE_HEAD;
            } else {
                return TYPE_ITEM;
            }
        }

        @Override
        public int getItemCount() {
            return m_alBaggageData == null ? iHeaderCnt : m_alBaggageData.size() + iHeaderCnt ;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == TYPE_ITEM) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_baggageinfo_content_view, parent, false);

                return new ItemHolder(view);
            }else{
                View viewAdd = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_baggageinfo_content_header_view, parent, false);

                return new HeadHolder(viewAdd);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if(holder instanceof HeadHolder){
                setTextAndViewSizeHead((HeadHolder) holder, position);
            }else {
                setTextAndViewSizeItem((ItemHolder) holder, position);
                setItemData((ItemHolder) holder, position);
            }
        }

        private void setTextAndViewSizeHead(final HeadHolder holder, final int iIndex) {

            RelativeLayout.LayoutParams rParams =
                    (RelativeLayout.LayoutParams) holder.m_tvTitle.getLayoutParams();

            rParams.height  = m_vScaleDef.getLayoutWidth(23);
            rParams.leftMargin = m_vScaleDef.getLayoutWidth(20);
            rParams.rightMargin = m_vScaleDef.getLayoutWidth(20);
            rParams.bottomMargin = m_vScaleDef.getLayoutHeight(18);
            rParams.topMargin =  m_vScaleDef.getLayoutHeight(18);
            m_vScaleDef.setTextSize(m_vScaleDef.DEF_TEXT_SIZE_16, holder.m_tvTitle);
            //holder.m_tvTitle.setText(m_alBaggageData.get(iIndex).strTitle);
        }

        private void setTextAndViewSizeItem(final ItemHolder viewholder, final int iIndex) {

            m_vScaleDef.setPadding(viewholder.rlayout_bg, 20, 0, 20, 0);

            RelativeLayout.LayoutParams rlParams = (RelativeLayout.LayoutParams)viewholder.tvDeparture.getLayoutParams();

            rlParams.height = m_vScaleDef.getLayoutHeight(25);
            rlParams.width = m_vScaleDef.getLayoutWidth(45);
            m_vScaleDef.setTextSize(m_vScaleDef.DEF_TEXT_SIZE_16, viewholder.tvDeparture);

            rlParams = (RelativeLayout.LayoutParams)viewholder.rlayout_img.getLayoutParams();
            rlParams.leftMargin = m_vScaleDef.getLayoutWidth(6);
            rlParams.height = m_vScaleDef.getLayoutHeight(84);
            rlParams.width = m_vScaleDef.getLayoutMinUnit(24);

            rlParams = (RelativeLayout.LayoutParams)viewholder.img_baggage.getLayoutParams();
            rlParams.height = m_vScaleDef.getLayoutMinUnit(24);
            rlParams.width = m_vScaleDef.getLayoutMinUnit(24);

            rlParams = (RelativeLayout.LayoutParams)viewholder.img_arrow.getLayoutParams();
            rlParams.height = m_vScaleDef.getLayoutMinUnit(45.7);
            rlParams.width = m_vScaleDef.getLayoutMinUnit(8);
            rlParams.topMargin = m_vScaleDef.getLayoutHeight(6);

            rlParams = (RelativeLayout.LayoutParams)viewholder.tvBaggageStatus.getLayoutParams();
            rlParams.height = m_vScaleDef.getLayoutHeight(25);
            rlParams.leftMargin = m_vScaleDef.getLayoutWidth(18);
            m_vScaleDef.setTextSize(m_vScaleDef.DEF_TEXT_SIZE_16, viewholder.tvBaggageStatus);

            rlParams = (RelativeLayout.LayoutParams)viewholder.tvExecuteTime.getLayoutParams();
            rlParams.height = m_vScaleDef.getLayoutHeight(21);
            m_vScaleDef.setTextSize( 13 , viewholder.tvExecuteTime);
        }

        private void setItemData( final ItemHolder viewholder, final int iIndex ){

            int iPosition = iIndex - 1;

            CIBaggageInfoContentResp item = m_alBaggageData.get(iPosition);
            viewholder.tvDeparture.setText(item.Flight_Departure_Station);
            viewholder.tvExecuteTime.setText(String.format("%s %s", item.Execute_Time , item.Flight_Number));
            viewholder.tvBaggageStatus.setText(item.Bag_Status);

            //扣掉header
            if ( iPosition == 0 ){

                if ( Build.VERSION.SDK_INT > 22 ){
                    viewholder.tvDeparture.setTextColor(getResources().getColor(R.color.french_blue, null));
                    viewholder.tvBaggageStatus.setTextColor(getResources().getColor(R.color.french_blue, null));
                } else {
                    viewholder.tvDeparture.setTextColor(getResources().getColor(R.color.french_blue));
                    viewholder.tvBaggageStatus.setTextColor(getResources().getColor(R.color.french_blue));
                }

                if ( m_alBaggageData.size() == 1  ){
                    //只有一筆狀態
                    viewholder.img_baggage.setImageResource(R.drawable.ic_status_cycle_blue);
                    viewholder.img_arrow.setVisibility(View.INVISIBLE);
                } else {
                    //當前狀態
                    viewholder.img_baggage.setImageResource(R.drawable.ic_baggage_b);
                    viewholder.img_arrow.setVisibility(View.VISIBLE);
                }

            } else {

                if ( Build.VERSION.SDK_INT > 22 ){
                    viewholder.tvDeparture.setTextColor(getResources().getColor(R.color.brownish_grey, null));
                    viewholder.tvBaggageStatus.setTextColor(getResources().getColor(R.color.brownish_grey, null));
                } else {
                    viewholder.tvDeparture.setTextColor(getResources().getColor(R.color.brownish_grey));
                    viewholder.tvBaggageStatus.setTextColor(getResources().getColor(R.color.brownish_grey));
                }
                viewholder.img_baggage.setImageResource(R.drawable.ic_status_cycle_blue);

                if ( iPosition == m_alBaggageData.size() - 1 ){
                    viewholder.img_arrow.setVisibility(View.INVISIBLE);
                } else {
                    viewholder.img_arrow.setVisibility(View.VISIBLE);
                }
            }

        }

        public class HeadHolder extends RecyclerView.ViewHolder {

            TextView        m_tvTitle     = null;

            public HeadHolder(View view) {
                super(view);
                m_tvTitle     = (TextView) view.findViewById(R.id.tv_title);
            }
        }

        public class ItemHolder extends RecyclerView.ViewHolder {
            RelativeLayout  rlayout_bg;
            TextView        tvDeparture;
            RelativeLayout  rlayout_img;
            ImageView       img_baggage;
            ImageView       img_arrow;
            //View            vDiv;

            TextView        tvBaggageStatus;
            TextView        tvExecuteTime;


            public ItemHolder(View view) {
                super(view);

                rlayout_bg       = (RelativeLayout) view.findViewById(R.id.rlayout_root);
                tvDeparture      = (TextView)view.findViewById(R.id.tv_departure_station);
                rlayout_img      = (RelativeLayout) view.findViewById(R.id.rlayout_img);
                img_baggage      = (ImageView)view.findViewById(R.id.img_baggage);
                img_arrow        = (ImageView)view.findViewById(R.id.img_arrow);

                tvBaggageStatus  = (TextView)view.findViewById(R.id.tv_bag_status);
                tvExecuteTime    = (TextView)view.findViewById(R.id.tv_Execute_Time);

            }
        }
    }
}

