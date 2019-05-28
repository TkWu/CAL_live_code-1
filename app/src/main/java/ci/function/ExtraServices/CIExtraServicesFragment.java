package ci.function.ExtraServices;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
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

import ci.function.Base.BaseFragment;
import ci.ui.WebView.CIWithoutInternetActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;

/**
 * 其他服務頁面
 * zeplin: 22.1-2
 * wireframe: p.133
 * Created by jlchen on 2016/3/30.
 */
public class CIExtraServicesFragment extends BaseFragment {

    private LinearLayout                    m_llBg      = null;
    private RecyclerView                    m_rv        = null;
    private ArrayList<CIExtraServicesItem>  m_alData    = null;
    private CIExtraServicesAdapter          m_adapter   = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_extra_services;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        m_llBg  = (LinearLayout) view.findViewById(R.id.ll_bg);
        m_rv    = (RecyclerView)view.findViewById(R.id.rv);
        m_rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        initialDataList();
        m_adapter = new CIExtraServicesAdapter();
        m_rv.setAdapter(m_adapter);
    }

    private void initialDataList(){
        m_alData = new ArrayList<>();
        m_alData.add(new CIExtraServicesItem(getActivity(),
                getString(R.string.extra_service_title_extra_baggage),
                getString(R.string.extra_service_content_extra_baggage),
                getString(R.string.extra_services_extra_baggage_link),
                R.drawable.img_services_extra_baggage));
        //2018-06-21 第二階段CR-移除親子臥艙的牌卡
//        m_alData.add(new CIExtraServicesItem(getActivity(),
//                getString(R.string.extra_service_title_family_couch),
//                getString(R.string.extra_service_content_family_couch),
//                getString(R.string.extra_services_family_couch_link),
//                R.drawable.img_services_family_couch));
        m_alData.add(new CIExtraServicesItem(getActivity(),
                getString(R.string.extra_service_title_wifi),
                getString(R.string.extra_service_content_wifi),
                getString(R.string.extra_services_wifi_link),
                R.drawable.img_services_wifi_onboard));
        m_alData.add(new CIExtraServicesItem(getActivity(),
                getString(R.string.extra_service_title_hsr),
                getString(R.string.extra_service_content_hsr),
                getString(R.string.extra_services_hsr_link),
                R.drawable.img_services_thsr));

        /* ECO_Travel*/
        m_alData.add(new CIExtraServicesItem(getActivity(),
                getString(R.string.extra_service_title_eco_travel),
                getString(R.string.extra_service_content_eco_travel),
                getString(R.string.extra_services_eco_travel_link),
                R.drawable.img_services_eco_travel));
    }
    private void resetDataList(){

        if ( null == m_alData || 0 ==m_alData.size() ){
            initialDataList();
            return;
        }

        m_alData.get(0).resetText(
                getString(R.string.extra_service_title_extra_baggage),
                getString(R.string.extra_service_content_extra_baggage),
                getString(R.string.extra_services_extra_baggage_link));
        //2018-06-21 第二階段CR-移除親子臥艙的牌卡
//        m_alData.get(1).resetText(
//                getString(R.string.extra_service_title_family_couch),
//                getString(R.string.extra_service_content_family_couch),
//                getString(R.string.extra_services_family_couch_link));
        m_alData.get(1).resetText(
                getString(R.string.extra_service_title_wifi),
                getString(R.string.extra_service_content_wifi),
                getString(R.string.extra_services_wifi_link));
        m_alData.get(2).resetText(
                getString(R.string.extra_service_title_hsr),
                getString(R.string.extra_service_content_hsr),
                getString(R.string.extra_services_hsr_link));
        m_alData.get(3).resetText(
                getString(R.string.extra_service_title_eco_travel),
                getString(R.string.extra_service_content_eco_travel),
                getString(R.string.extra_services_eco_travel_link));
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {}

    @Override
    protected void setOnParameterAndListener(View view) {}

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {}

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    public void onLanguageChangeUpdateUI() {}

    @Override
    public void onFragmentShow() {
        super.onFragmentShow();

        if ( null == m_alData
                || 0 == m_alData.size()
                || m_alData.get(0).getTitle() != getString(R.string.extra_service_title_extra_baggage) ){
            resetDataList();
            m_adapter.notifyDataSetChanged();
        }

        m_rv.getLayoutManager().scrollToPosition(0);

//        if ( null == m_adapter){
//            m_adapter = new CIExtraServicesAdapter();
//            m_rv.setAdapter(m_adapter);
//        }
//
//        m_FragmentHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                initialDataList();
//                m_adapter.notifyDataSetChanged();
//            }
//        }, 500);
    }

    @Override
    public void onFragmentHide() {
        super.onFragmentHide();

//        System.gc();
    }

    @Override
    public void onResume() {
        super.onResume();
        if ( null != m_llBg ){
            m_llBg.setBackgroundResource(R.drawable.bg_login);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if ( null != m_llBg ){
            if( m_llBg.getBackground() instanceof BitmapDrawable){
                m_llBg.setBackground(null);
            }
        }
    }

    @Override
    public void onDestroy() {
        if ( null != m_alData ){
            for ( int i = 0; i < m_alData.size(); i ++ ){
                ImageHandle.recycleBitmap(m_alData.get(i).getBitmap());
            }
            m_alData = null;
        }
        if (null != m_llBg) {
            m_llBg.setBackground(null);
        }
//        System.gc();
        super.onDestroy();
    }

    class CIExtraServicesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        ViewScaleDef m_vScaleDef;

        public static final int TYPE_HEAD = 3000;
        public static final int TYPE_ITEM = 3001;

        CIExtraServicesAdapter(){
            this.m_vScaleDef    = ViewScaleDef.getInstance(getActivity());
        }

        @Override
        public int getItemViewType(int position) {
            if( 0 == position){
                return TYPE_HEAD;
            }
            return TYPE_ITEM;
        }

        @Override
        public int getItemCount() {
            return m_alData == null ? 1 : m_alData.size()+1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == TYPE_ITEM) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_view_extra_services_item, parent, false);

                return new ItemHolder(view);
            }else{
                View viewAdd = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_view_extra_services_text, parent, false);

                return new HeadHolder(viewAdd);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if(holder instanceof HeadHolder){
                setTextAndViewSizeHead((HeadHolder) holder);
            }else {
                setTextAndViewSizeItem((ItemHolder) holder, position);
            }
        }

        private void setTextAndViewSizeHead(final HeadHolder holder) {

            holder.m_llText.setPadding(
                    m_vScaleDef.getLayoutWidth(10), 0,
                    m_vScaleDef.getLayoutWidth(10), m_vScaleDef.getLayoutHeight(20));
            LinearLayout.LayoutParams lParams =
                    (LinearLayout.LayoutParams) holder.m_tvMsg.getLayoutParams();
            //lParams.height = m_vScaleDef.getLayoutHeight(113.3);
            lParams.rightMargin = m_vScaleDef.getLayoutWidth(20);
            lParams.leftMargin = m_vScaleDef.getLayoutWidth(20);
            lParams.topMargin = m_vScaleDef.getLayoutHeight(20);
            lParams.bottomMargin = m_vScaleDef.getLayoutHeight(10);
            m_vScaleDef.setTextSize(m_vScaleDef.DEF_TEXT_SIZE_16, holder.m_tvMsg);
            holder.m_tvMsg.setText(getString(R.string.extra_services_msg));

            //2016/04-21 需求變更, 不顯示連結官網的字樣, by ryan
//            SpannableString spannableString =
//                    new SpannableString(holder.m_tvMsg.getText().toString());
//
//            String strWS = m_Context.getString(R.string.extra_services_msg_website);
//            int iHeadIndex = spannableString.toString().indexOf(strWS, 0);
//            int iTailIndex = iHeadIndex + strWS.length();
//
//            SpannableStringBuilder stringBuilder =
//                    new SpannableStringBuilder(spannableString.toString());
//            ClickableSpan clickableSpan = new ClickableSpan() {
//                @Override
//                public void onClick(View widget) {
//                    Intent intent = new Intent();
//                    intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_TITLE_TEXT_TAG, "華航官網");
//                    intent.setClass(m_Context, CIWebViewActivity.class);
//                    m_Context.startActivity(intent);
//
//                    ((Activity)m_Context).overridePendingTransition(
//                            R.anim.anim_right_in,
//                            R.anim.anim_left_out);
//                }
//            };
//            stringBuilder.setSpan(
//                    clickableSpan,
//                    iHeadIndex,
//                    iTailIndex,
//                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//            stringBuilder.setSpan(
//                    new ForegroundColorSpan(Color.WHITE),
//                    iHeadIndex,
//                    iTailIndex,
//                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//            holder.m_tvMsg.setText(stringBuilder);
//            holder.m_tvMsg.setMovementMethod(LinkMovementMethod.getInstance());
        }

        private void setTextAndViewSizeItem(final ItemHolder viewholder, final int iIndex) {

            //extra service不顯示獎項兌換資格.里程數. webonly
            m_vScaleDef.setMargins(viewholder.rlayout_bg, 10, 10, 10, 0);

            viewholder.imageView.getLayoutParams().height = m_vScaleDef.getLayoutWidth(124);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)viewholder.tvTitle.getLayoutParams();
            viewholder.tvTitle.setMinHeight(m_vScaleDef.getLayoutHeight(22));
//            layoutParams.height = m_vScaleDef.getLayoutHeight(24);
            layoutParams = (RelativeLayout.LayoutParams)viewholder.tvSummary.getLayoutParams();
            layoutParams.topMargin = m_vScaleDef.getLayoutHeight(8);
            viewholder.tvSummary.setMinHeight(m_vScaleDef.getLayoutHeight(37.3));

//            viewholder.rlayout_Summary.setMinHeight(m_vScaleDef.getLayoutHeight(90));
            int iwidth = m_vScaleDef.getLayoutWidth(20);
            int itop = m_vScaleDef.getLayoutHeight(10.7);
            int ibottom = m_vScaleDef.getLayoutHeight(10);
            viewholder.rlayout_Summary.setPadding(iwidth, itop, iwidth, ibottom);
            m_vScaleDef.setTextSize(20, viewholder.tvTitle);
            viewholder.tvTitle.setText( m_alData.get(iIndex-1).getTitle() );
            m_vScaleDef.setTextSize(13, viewholder.tvSummary);
            viewholder.tvSummary.setText( m_alData.get(iIndex-1).getContent() );

//            viewholder.imageView.setImageResource(m_alData.get(iIndex-1).getImgRes());

//            //Res轉bitmap
//            Bitmap resBitmap = ImageHandle.getLocalBitmap(
//                    getActivity(),
//                    m_alData.get(iIndex-1).getImgRes(),
//                    1);
//
//            //縮放處理
//            Bitmap zoomBitmap = ImageHandle.zoomImage(
//                    resBitmap,
//                    m_vScaleDef.getLayoutWidth(300),
//                    m_vScaleDef.getLayoutWidth(124));
//
//            //圓角處理
//            Bitmap roundedBitmap = ImageHandle.getRoundedCornerBitmap(
//                    zoomBitmap,
//                    m_vScaleDef.getLayoutMinUnit(3),
//                    true, true, false, false);

            viewholder.imageView.setImageBitmap(m_alData.get(iIndex-1).getBitmap());

            viewholder.rlayout_bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent();
                    intent.putExtra(
                            UiMessageDef.BUNDLE_WEBVIEW_TITLE_TEXT_TAG,
                            m_alData.get(iIndex-1).getTitle());
                    intent.putExtra(
                            UiMessageDef.BUNDLE_WEBVIEW_URL_TAG,
                            m_alData.get(iIndex-1).getUrl());
                    intent.setClass(getActivity(), CIWithoutInternetActivity.class);
                    startActivity(intent);

                    getActivity().overridePendingTransition(
                            R.anim.anim_right_in,
                            R.anim.anim_left_out);
                }
            });

            if ( iIndex == m_alData.size() ){
                viewholder.vDiv.getLayoutParams().height = m_vScaleDef.getLayoutHeight(20);
                viewholder.vDiv.setVisibility(View.VISIBLE);
//                m_vScaleDef.setMargins(viewholder.rlayout_Summary, 0, 0, 0, 20);
            }else {
                viewholder.vDiv.setVisibility(View.GONE);
//                m_vScaleDef.setMargins(viewholder.rlayout_Summary, 0, 0, 0, 0);
            }

//            //回收已不需要的bitmap
//            resBitmap.recycle();
//            zoomBitmap.recycle();
//            System.gc();
        }

        //上方文字
        public class HeadHolder extends RecyclerView.ViewHolder {
            LinearLayout    m_llText    = null;
            TextView        m_tvMsg     = null;

            public HeadHolder(View view) {
                super(view);

                m_llText    = (LinearLayout)view.findViewById(R.id.ll_text);
                m_tvMsg     = (TextView)    view.findViewById(R.id.tv_msg);
            }
        }

        //牌卡
        public class ItemHolder extends RecyclerView.ViewHolder {
            RelativeLayout  rlayout_bg;
            ImageView       imageView;
            RelativeLayout  rlayout_Summary;
            TextView        tvTitle;
            TextView        tvSummary;
            View            vDiv;

            public ItemHolder(View view) {
                super(view);

                rlayout_bg       = (RelativeLayout) view.findViewById(R.id.rlayout_bg);
                imageView        = (ImageView)view.findViewById(R.id.imageView);
                rlayout_Summary  = (RelativeLayout)view.findViewById(R.id.rlayout_summary);
                tvSummary        = (TextView)view.findViewById(R.id.tv_summary);
                tvTitle          = (TextView)view.findViewById(R.id.tv_title);
                vDiv             = (View)view.findViewById(R.id.v_div);
            }
        }
    }
}
