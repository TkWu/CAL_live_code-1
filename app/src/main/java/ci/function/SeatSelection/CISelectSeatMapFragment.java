package ci.function.SeatSelection;

import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.HashMap;

import ci.function.Base.BaseFragment;
import ci.ui.define.ViewScaleDef;
import ci.ws.Models.CISeatFloor;

/** 上or下層座位圖
 * Created by jlchen on 2016/7/4.
 */
public class CISelectSeatMapFragment extends BaseFragment{

    public interface OnSeatFloorFragmentListener{
        //通知activity已經畫好座位圖
        void SetSeatMapComplete();
        //通知activity要變更乘客選位資料
        void onSelectSeatClickGetView(ArrayList<CIPassengerSeatItem> passengerSeatItems);
        void onSelectSeatClick(View v, String strSeatNum);
    }

    public interface OnSeatFloorFragmentParameter{
        //取得該層座位圖資料
        ArrayList<CISeatFloor>          getSeatFloorList();
        //取得乘客座位資料
        ArrayList<CIPassengerSeatItem>  getPassengerSeatData();
        //座位資料與選位序號的對應表
        HashMap<Integer, String>        getSeatHmData();
    }

    public interface OnSeatFloorFragmentInterface{
        //更換上下層
        void ChangeFloor(int iIndex, ArrayList<CIPassengerSeatItem> passengerSeatItems);
        //更換乘客
        void ChangePassenger(int iIndex);
        //更新當前座位資料
        void SetNowSelectSeat(int iIndex, ArrayList<CIPassengerSeatItem> passengerSeatItems);
    }

    private OnSeatFloorFragmentInterface m_Interface = new OnSeatFloorFragmentInterface() {
        @Override
        public void ChangeFloor(int iIndex, ArrayList<CIPassengerSeatItem> passengerSeatItems) {
            m_iIndex = iIndex;
            m_alPaxSeatData = passengerSeatItems;

            //依照index及各乘客對應的view 設定正確的背景
            for (int i = 0; i < m_alPaxSeatData.size(); i++ ){
                if ( null != m_alPaxSeatData.get(i).m_vItem ){
                    if ( i != m_iIndex ){
                        ((TextView) m_alPaxSeatData.get(i).m_vItem
                                .findViewById(R.id.tv_num))
                                .setBackgroundResource(R.drawable.bg_select_seat_item_partner);
                    }else {
                        ((TextView) m_alPaxSeatData.get(i).m_vItem
                                .findViewById(R.id.tv_num))
                                .setBackgroundResource(R.drawable.bg_select_seat_item_your);
                    }
                }
            }
        }

        @Override
        public void ChangePassenger(int iIndex) {
            if ( null != m_alPaxSeatData.get(m_iIndex).m_vItem ){
                //原當前乘客更換背景為同伴
                ((TextView) m_alPaxSeatData.get(m_iIndex).m_vItem
                        .findViewById(R.id.tv_num))
                        .setBackgroundResource(R.drawable.bg_select_seat_item_partner);
            }

            m_iIndex = iIndex;

            if ( null != m_alPaxSeatData.get(m_iIndex).m_vItem ){
                //原同伴更換背景為當前乘客
                ((TextView) m_alPaxSeatData.get(m_iIndex).m_vItem
                        .findViewById(R.id.tv_num))
                        .setBackgroundResource(R.drawable.bg_select_seat_item_your);
            }
        }

        @Override
        public void SetNowSelectSeat(int iIndex, ArrayList<CIPassengerSeatItem> passengerSeatItems) {

            if ( null == m_SeatInfoView )
                return;

            m_iIndex = iIndex;
            m_alPaxSeatData = passengerSeatItems;

            m_FragmentHandler.post(new Runnable() {
                @Override
                public void run() {

                    //選擇的位置與當前已選的位置不一樣, 要做更新
                    for (int i = 0; i < m_SeatInfoView.length; i++) {
                        m_SeatInfoView[i].notifyAdapter(m_iIndex, m_alPaxSeatData);
                    }
                }
            });
//            if ( null != m_Adapter ){
//                m_Adapter.notifyDataSetChanged();
//            }
        }
    };

    private CISeatInfoView.OnSeatInfoViewListener m_SeatInfoViewListener = new CISeatInfoView.OnSeatInfoViewListener() {

        @Override
        public void onSeatItemClick(final View v, final String strSeatNum) {

            //選擇的位置與當前已選的位置一樣, 不做任何事
            if ( strSeatNum.equals(m_alPaxSeatData.get(m_iIndex).m_strPassengerSeat) ) {
//                for (int i = 0; i < m_SeatInfoView.length; i++) {
//                    m_SeatInfoView[i].delPartnerSelection(m_strSeat);
//                    m_SeatInfoView[i].setNowSelection("");
//                    m_SeatInfoView[i].notifyAdapter();
//                }
            } else {
                //選到同伴位置就不做任何事
                for (int i = 0; i < m_alPaxSeatData.size(); i++ ){
                    if ( i != m_iIndex && strSeatNum.equals(m_alPaxSeatData.get(i).m_strPassengerSeat) )
                        return;
                }

                //有取到上一個當前位置view
                if ( null != m_alPaxSeatData.get(m_iIndex).m_vItem ){

                    //新點擊的view 直接更換背景為橘色 並設定選位序號
                    ((TextView) v.findViewById(R.id.tv_num))
                            .setBackgroundResource(R.drawable.bg_select_seat_item_your);
                    ((TextView) v.findViewById(R.id.tv_num)).setText(""+(m_alPaxSeatData.get(m_iIndex).m_iPassengerSeq+1));

                    //舊的view 更換背景為空位
                    ((TextView) m_alPaxSeatData.get(m_iIndex).m_vItem.findViewById(R.id.tv_num))
                            .setBackgroundResource(R.drawable.bg_select_seat_item_available);
                    ((TextView) m_alPaxSeatData.get(m_iIndex).m_vItem.findViewById(R.id.tv_num)).setText("");

                    m_alPaxSeatData.get(m_iIndex).m_vItem = v;

                    m_alPaxSeatData.get(m_iIndex).m_strPassengerSeat = strSeatNum;

                    //通知activitiy更新乘客資訊
                    if ( null != m_Listener )
                        m_Listener.onSelectSeatClickGetView(m_alPaxSeatData);
                }else {
                    if ( null == m_alPaxSeatData.get(m_iIndex).m_strPassengerSeat ||
                            m_alPaxSeatData.get(m_iIndex).m_strPassengerSeat.equals("") ){

                        if ( m_alPaxSeatData.get(m_iIndex).m_iPassengerSeq == -1 ){
                            m_alPaxSeatData.get(m_iIndex).m_iPassengerSeq = searchSeqMax()+1;
                        }

                        //原本就是空位 直接將被選取的item更換背景
                        ((TextView) v.findViewById(R.id.tv_num))
                                .setBackgroundResource(R.drawable.bg_select_seat_item_your);
                        ((TextView) v.findViewById(R.id.tv_num)).setText(""+(m_alPaxSeatData.get(m_iIndex).m_iPassengerSeq+1));

                        m_alPaxSeatData.get(m_iIndex).m_vItem = v;

                        m_alPaxSeatData.get(m_iIndex).m_strPassengerSeat = strSeatNum;

                        //通知activitiy更新乘客資訊
                        if ( null != m_Listener )
                            m_Listener.onSelectSeatClickGetView(m_alPaxSeatData);
                    }else {
                        //直接將被選取的item更換背景
                        ((TextView) v.findViewById(R.id.tv_num))
                                .setBackgroundResource(R.drawable.bg_select_seat_item_your);
                        ((TextView) v.findViewById(R.id.tv_num)).setText(""+(m_alPaxSeatData.get(m_iIndex).m_iPassengerSeq+1));

                        m_alPaxSeatData.get(m_iIndex).m_vItem = v;

                        m_alPaxSeatData.get(m_iIndex).m_strPassengerSeat = strSeatNum;

                        m_FragmentHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //沒有取到上一個當前位置view 就等activity先更新完乘客資料,再做adapter.notifyDataSetChanged()
                                if ( null != m_Listener )
                                    m_Listener.onSelectSeatClick(v, strSeatNum);
                            }
                        });
                    }
                }
            }
        }

        @Override
        public void getSelectSeatItem(View v, int iSeq, String strSeatNum) {
            m_alPaxSeatData.get(m_iIndex).m_vItem = v;
        }

        @Override
        public void getPartnerSelectSeatItem(View v, int iSeq, String strSeatNum) {
            for (int i = 0; i < m_alPaxSeatData.size(); i++ ){
                if (m_alPaxSeatData.get(i).m_iPassengerSeq == iSeq){
                    m_alPaxSeatData.get(i).m_vItem = v;
                }
            }
        }
    };

    ViewTreeObserver.OnScrollChangedListener m_onScroll = new ViewTreeObserver.OnScrollChangedListener() {
        @Override
        public void onScrollChanged() {

            int iScrollHeight = 0;
            int iCnt = m_svGridView.getChildCount();
            if (iCnt > 0) {
                iScrollHeight = m_svGridView.getChildAt(0).getHeight() - m_svGridView.getHeight();

                if ( m_svGridView.getChildAt(0).getHeight() <= m_svGridView.getHeight() )
                    return;
            }

            //利用百分比決定 blurr 效果
            //減少分母, 提高Blur的效果
            float fAlpha = (m_svGridView.getScrollY() / (float) iScrollHeight);

            //如果滑動至頂部或底部 隱藏上方或下方陰影
            if (fAlpha >= 1) {
                m_vShadowTop.setVisibility(View.VISIBLE);
                m_vShadowBottom.setVisibility(View.GONE);
            } else if (fAlpha <= 0) {
                m_vShadowTop.setVisibility(View.GONE);
                m_vShadowBottom.setVisibility(View.VISIBLE);
            } else {
                m_vShadowTop.setVisibility(View.VISIBLE);
                m_vShadowBottom.setVisibility(View.VISIBLE);
            }
//            m_vShadowTop.setAlpha(fAlpha);
//
//            fAlpha = 1 - fAlpha;
//            m_vShadowBottom.setAlpha(fAlpha);
        }
    };

//    private RecyclerView.OnScrollListener scrollListener() {
//        return new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if (!recyclerView.canScrollVertically(1) && !recyclerView.canScrollVertically(-1)) {
//                    m_vShadowBottom.setVisibility(View.GONE);
//                    m_vShadowTop.setVisibility(View.GONE);
//                } else if (!recyclerView.canScrollVertically(1)) {
//                    m_vShadowBottom.setVisibility(View.GONE);
//                    m_vShadowTop.setVisibility(View.VISIBLE);
//                } else if (!recyclerView.canScrollVertically(-1)) {
//                    m_vShadowTop.setVisibility(View.GONE);
//                    m_vShadowBottom.setVisibility(View.VISIBLE);
//                } else {
//                    m_vShadowBottom.setVisibility(View.VISIBLE);
//                    m_vShadowTop.setVisibility(View.VISIBLE);
//                }
//            }
//        };
//    }

    private OnSeatFloorFragmentListener m_Listener = null;
    private OnSeatFloorFragmentParameter m_Parameter = null;

//    private RecyclerView m_rv = null;
//    private LinearLayoutManager m_LayoutManager = null;
//    private MyAdapter m_Adapter = null;
    private ScrollView m_svGridView = null;
    private LinearLayout m_llContent = null;
    private CISeatInfoView[] m_SeatInfoView;
    private View m_vShadowBottom = null;
    private View m_vShadowTop = null;

    private int m_iIndex = 0;
    private int m_iY = 0;

    private HashMap<Integer, String> m_hmSeatData = new HashMap<>();
    private ArrayList<CISeatFloor> m_alSeatData = new ArrayList<>();
    private ArrayList<CIPassengerSeatItem> m_alPaxSeatData = new ArrayList<>();

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_seat_floor;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        //取得初始化資料
        if ( null != m_Parameter ){
            m_alSeatData    = m_Parameter.getSeatFloorList();
            m_alPaxSeatData = m_Parameter.getPassengerSeatData();
            m_hmSeatData    = m_Parameter.getSeatHmData();

            if ( null == m_alSeatData ){
                m_alSeatData = new ArrayList<>();
            }
            if ( null == m_alPaxSeatData ){
                m_alPaxSeatData = new ArrayList<>();
            }
            if ( null == m_hmSeatData ){
                m_hmSeatData = new HashMap<>();
            }
        }

        //座位位置圖
//        m_rv = (RecyclerView) view.findViewById(R.id.lv_select_seat);
        m_svGridView = (ScrollView) view.findViewById(R.id.sv_select_seat);
        m_llContent = (LinearLayout) view.findViewById(R.id.ll_content);

        //位置圖捲軸陰影
        m_vShadowBottom = view.findViewById(R.id.vGradient);
        m_vShadowTop = view.findViewById(R.id.vGradient2);

        m_svGridView.getViewTreeObserver().addOnScrollChangedListener(m_onScroll);

        ViewTreeObserver vto = m_svGridView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                m_svGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int iCnt = m_svGridView.getChildCount();
                if (iCnt > 0) {
                    //內容高度 <= ScrollView高度, 沒有可滾動區域, 無法監聽onScrollChanged(), 需隱藏陰影
                    if ( m_svGridView.getChildAt(0).getHeight() <= m_svGridView.getHeight() ) {
//                        m_vShadowTop.setAlpha(0);
//                        m_vShadowBottom.setAlpha(0);
                        m_vShadowTop.setVisibility(View.GONE);
                        m_vShadowBottom.setVisibility(View.GONE);
                    }
                }
            }
        });

        setSeatMapContent();

//        m_LayoutManager = new LinearLayoutManager(getActivity());
//        m_rv.setLayoutManager(m_LayoutManager);
//        m_rv.addOnScrollListener(scrollListener());
//
//        m_Adapter = new MyAdapter(getActivity());
//        m_rv.setAdapter(m_Adapter);
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        m_svGridView.setPadding(vScaleDef.getLayoutWidth(8), 0, vScaleDef.getLayoutWidth(8), 0);
//        m_rv.setPadding(vScaleDef.getLayoutWidth(8), 0, vScaleDef.getLayoutWidth(8), 0);

        m_vShadowTop.getLayoutParams().height       = vScaleDef.getLayoutHeight(16);
        m_vShadowBottom.getLayoutParams().height    = vScaleDef.getLayoutHeight(16);
    }

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

        if ( null != m_svGridView){
            m_FragmentHandler.post(new Runnable() {
                @Override
                public void run() {
                    m_svGridView.scrollTo(0, m_iY);
                }
            });
        }
    }

    @Override
    public void onFragmentHide() {
        super.onFragmentHide();

        if ( null != m_svGridView ){
            m_iY = m_svGridView.getScrollY();
        }
//            m_FragmentHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    m_svGridView.fullScroll(ScrollView.FOCUS_UP);
//                }
//            });
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        m_llContent.removeAllViews();
    }

    public OnSeatFloorFragmentInterface uiSetParameterListener(
            OnSeatFloorFragmentParameter onParameter,
            OnSeatFloorFragmentListener onListener) {
        m_Parameter = onParameter;
        m_Listener = onListener;

        return m_Interface;
    }

    //設定該層座位資料
    private void setSeatMapContent() {
        m_llContent.removeAllViews();

        //下層段數大於0, 依照段數產生多個gridview
        if (m_alSeatData.size() > 0) {

            m_SeatInfoView = new CISeatInfoView[m_alSeatData.size()];

            for (int i = 0; i < m_alSeatData.size(); i++) {
                final CISeatFloor seatFloor = m_alSeatData.get(i);

                m_SeatInfoView[i] = new CISeatInfoView(getActivity());
                m_SeatInfoView[i].uiSetParameterListener(new CISeatInfoView.OnSeatInfoViewParameter() {

                    @Override
                    public CISeatFloor GetSeatInfo() {
                        //按順序傳入各段座位表資料
                        return seatFloor;
                    }

                    @Override
                    public ArrayList<CIPassengerSeatItem> GetPassengerSeatData() {
                        return m_alPaxSeatData;
                    }
                }, m_SeatInfoViewListener);

//                //傳入原始乘客座位資料給各段座位表
//                for (int j = 0; j < m_alPaxSeatData.size(); j++) {
//                    if (!m_alPaxSeatData.get(j).m_strPassengerSeat.equals("")) {
//                        //設定所有乘客的選位序號
//                        m_SeatInfoView[i].setSelectSeq(m_alPaxSeatData.get(j).m_iPassengerSeq);
//                        //設定該序號下的對應座位
//                        m_SeatInfoView[i].setPartnerSelection(m_alPaxSeatData.get(j).m_strPassengerSeat);
//                    }
//                }

                //設定已有人的座位
                for (int j = 0; j < m_hmSeatData.size(); j++) {
                    m_SeatInfoView[i].setAlreadySelect(m_hmSeatData.get(j));
                }

//                if (!m_alPaxSeatData.get(0).m_strPassengerSeat.equals("")) {
//                    m_iSeq = m_alPaxSeatData.get(0).m_iPassengerSeq;
//                    m_SeatInfoView[i].setSelectSeq(m_alPaxSeatData.get(0).m_iPassengerSeq);
//                } else {
//                    m_SeatInfoView[i].setSelectSeq(m_iSeq);
//                }
//                m_SeatInfoView[i].setNowSelection(m_alPaxSeatData.get(0).m_strPassengerSeat);

                m_SeatInfoView[i].setGridView();
//                m_SeatInfoView[i].notifyAdapter(m_iIndex, m_alPaxSeatData);

                m_llContent.addView(m_SeatInfoView[i]);
            }
        }

        m_FragmentHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if ( null != m_Listener ){
                    m_Listener.SetSeatMapComplete();
                }
            }
        }, 500);
    }

    //遍尋最大的已儲存的seq值
    private int searchSeqMax() {
        int re = -1;
        for (int i = 0; i < m_alPaxSeatData.size(); i++) {
            if (m_alPaxSeatData.get(i).m_iPassengerSeq > re)
                re = m_alPaxSeatData.get(i).m_iPassengerSeq;
        }
        return re;
    }

//    class MyAdapter extends RecyclerView.Adapter {
//
//        private Context m_context;
//
//        MyAdapter(Context context){
//            this.m_context = context;
//        }
//
//        @Override
//        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            CISeatInfoView seatInfoView = new CISeatInfoView(m_context);
//            return new ViewHolder(seatInfoView){};
//        }
//
//        @Override
//        public void onBindViewHolder(ViewHolder holder, final int position) {
//            final CISeatInfoView convertView = (CISeatInfoView) holder.itemView;
//
//            //設定該段座位資料
//            convertView.uiSetParameterListener(new CISeatInfoView.OnSeatInfoViewParameter() {
//                @Override
//                public CISeatFloor GetSeatInfo() {
//                    //按順序傳入各段座位表資料
//                    return m_alSeatData.get(position);
//                }
//
//                @Override
//                public ArrayList<CIPassengerSeatItem> GetPassengerSeatData() {
//                    return m_alPaxSeatData;
//                }
//            }, m_SeatInfoViewListener);
//
//            //設定已有人的座位
//            for (int j = 0; j < m_hmSeatData.size(); j++) {
//                convertView.setAlreadySelect(m_hmSeatData.get(j));
//            }
//
//            convertView.setGridView();
//            convertView.notifyAdapter(m_iIndex, m_alPaxSeatData);
//        }
//
//        @Override
//        public long getItemId(int arg0) {
//            return arg0;
//        }
//
//        @Override
//        public int getItemCount() {
//            if ( null == m_alSeatData )
//                return 0;
//            return m_alSeatData.size();
//        }
//    }
}
