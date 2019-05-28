package ci.function.SeatSelection;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.HashMap;

import ci.function.Base.BaseView;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.CIGridView;
import ci.ws.Models.CISeatFloor;
import ci.ws.Models.entities.CISeatInfo;

/**
 * 座位依機型 各層可能有多段座位 各段座位的列數不盡相同
 * 此class表示一段
 * Created by jlchen on 2016/6/28.
 */
public class CISeatInfoView extends BaseView{

    public interface OnSeatInfoViewParameter {
        //取得該段座位內容
        CISeatFloor                     GetSeatInfo();
        //取得乘客座位資料
        ArrayList<CIPassengerSeatItem>  GetPassengerSeatData();
    }

    public interface OnSeatInfoViewListener {
        //按下某一座位
        void onSeatItemClick(View v, String strSeatNum);
        //取得當前選位的view
        void getSelectSeatItem(View v, int iSeq, String strSeatNum);
        //取得同伴座位的view
        void getPartnerSelectSeatItem(View v, int iSeq, String strSeatNum);
    }

    private OnSeatInfoViewParameter m_Parameter = null;
    private OnSeatInfoViewListener  m_Listener  = null;

    private Context                 m_Context   = null;

    private CIGridView              m_gvSeat    = null;
    private CISeatInfoAdapter       m_Adapter   = null;
    private LinearLayout            m_llColName = null;
    private TextView[]              m_tvColName = null;
    private View                    m_vDiv      = null;

    private int                     m_iWidth    = 0;
    private int                     m_iIndex    = 0;

    private CISeatFloor             m_DataList  = null;
    private ArrayList<CIPassengerSeatItem> m_alPaxSeatData = null;

    public CISeatInfoView(Context context) {
        super(context);
        m_Context = context;
    }

    public CISeatInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        m_Context = context;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_view_seat_info;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater) {
        m_llColName = (LinearLayout) findViewById(R.id.ll_col_name);
        m_gvSeat    = (CIGridView) findViewById(R.id.grid_view);
        m_vDiv      = findViewById(R.id.v_div);

        if ( null != m_Parameter ){
            m_DataList      = m_Parameter.GetSeatInfo();
            m_alPaxSeatData = m_Parameter.GetPassengerSeatData();
        }

        m_Adapter = new CISeatInfoAdapter(
                m_Context,
                m_DataList,
                m_Listener);

        setSeatEnglish();
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {

        m_llColName.getLayoutParams().height = vScaleDef.getLayoutHeight(25);
        vScaleDef.setMargins(m_llColName, 0, 3, 0, 0);

        for (int i = 0; i < m_DataList.SeatCol; i++) {
            vScaleDef.setTextSize(13, m_tvColName[i]);
            m_iWidth = vScaleDef.getLayoutWidth(344) / m_DataList.SeatCol;
            m_tvColName[i].getLayoutParams().width = m_iWidth;
        }

        m_vDiv.getLayoutParams().height = vScaleDef.getLayoutHeight(8);
    }

    private void setSeatEnglish() {

        m_llColName.removeAllViews();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
//        params.weight = 1;
        m_tvColName = new TextView[m_DataList.SeatCol];
        for (int i = 0; i < m_DataList.SeatCol; i++) {
            m_tvColName[i] = new TextView(m_Context);
            m_tvColName[i].setGravity(Gravity.CENTER);
            m_tvColName[i].setLayoutParams(params);
            m_tvColName[i].setTextColor(Color.parseColor("#b2b2b2"));
            if (m_DataList.arColTextList.get(i).ColType == CISeatInfo.CISeatType.Aisle)
                m_tvColName[i].setText("");
            else
                m_tvColName[i].setText(m_DataList.arColTextList.get(i).ColName);
            m_llColName.addView(m_tvColName[i]);
        }
    }

    public void setGridView(){
        m_Adapter.setSelectSeat(m_iIndex, m_alPaxSeatData);

        m_gvSeat.setHaveScrollbar(false);
        m_gvSeat.setNumColumns(m_DataList.SeatCol);
        m_gvSeat.setAdapter(m_Adapter);

        setItemWidth( m_iWidth );
    }

    public void notifyAdapter(int iIndex, ArrayList<CIPassengerSeatItem> passengerSeatItems){
        m_alPaxSeatData = passengerSeatItems;
        m_iIndex = iIndex;

        m_Adapter.setSelectSeat(m_iIndex, m_alPaxSeatData);
        m_Adapter.notifyDataSetChanged();
        m_gvSeat.setAdapter(m_Adapter);
    }

    public void setItemWidth(int i) {
        m_Adapter.setItemWidth(i);
    }

    public void setAlreadySelect(String seat){
        m_Adapter.setAlreadySelect(seat);
    }

    public void uiSetParameterListener(OnSeatInfoViewParameter onParameter,
                                       OnSeatInfoViewListener onListener){
        m_Parameter = onParameter;
        m_Listener  = onListener;

        initial();
    }

    /**
     * 各段座位gridView的adapter
     * Created by jlchen on 2016/7/4.
     */
    public class CISeatInfoAdapter extends BaseAdapter {

        private class Holder {
            FrameLayout flSeat  = null;
            TextView    tvNum   = null;
        }

        private OnSeatInfoViewListener m_Listener = null;

        private Context m_Context = null;
        //當前座位
        private String m_strNowSelect = "";
        //每個方格的寬高
        private int m_iItemWidth = 0;
        //最初的座位表
        private HashMap<String, Integer> m_hmAlreadySelectSeat = null;
        //最初的座位表
        private HashMap<String, Integer> m_hmSelectSeatAndSeq = new HashMap<>();
        //該段座位圖資料
        private CISeatFloor m_DataList = null;

        public CISeatInfoAdapter(Context context,
                                 CISeatFloor data,
                                 OnSeatInfoViewListener listener) {
            this.m_Listener = listener;
            this.m_Context = context;
            this.m_DataList = data;
            this.m_hmAlreadySelectSeat = new HashMap<>();
        }

        @Override
        public int getCount() {
            if (null == m_DataList)
                return 0;

            return m_DataList.SeatCol * m_DataList.SeatRow;
        }

        @Override
        public Object getItem(int position) {
            if (null == m_DataList)
                return null;

            return m_DataList.arSeatList.get(position);
        }

        @Override
        public long getItemId(int position) {
            if (null == m_DataList)
                return 0;

            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            Holder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(m_Context).inflate(
                        R.layout.layout_view_seat_item, parent, false);

                holder = new Holder();
                holder.flSeat   = (FrameLayout) convertView.findViewById(R.id.fl_seat_item);
                holder.tvNum    = (TextView) convertView.findViewById(R.id.tv_num);

                ViewScaleDef vScaleDef = ViewScaleDef.getInstance(m_Context);
                holder.flSeat.getLayoutParams().height = m_iItemWidth;
                holder.flSeat.getLayoutParams().width = m_iItemWidth;
                holder.flSeat.setPadding(
                        vScaleDef.getLayoutWidth(3),
                        vScaleDef.getLayoutWidth(3),
                        vScaleDef.getLayoutWidth(3),
                        vScaleDef.getLayoutWidth(3));

                //取對應座位資訊
                final CISeatInfo seatInfo = m_DataList.arSeatList.get(position);
                //座位名
                final String strSeat = seatInfo.Row_Number + seatInfo.Col_Name;

                //依照座位類型呈現對應的ui
                switch (seatInfo.SeatType) {
                    case Seat:
                        //空位
                        if (seatInfo.SeatStatus == CISeatInfo.CISeatStatus.Available) {
                            if (strSeat.equals(m_strNowSelect)) {
                                //橘色 當前選擇
                                holder.tvNum.setBackgroundResource(R.drawable.bg_select_seat_item_your);
                                holder.tvNum.setText(Integer.toString(m_hmSelectSeatAndSeq.get(strSeat) + 1));
                                vScaleDef.setTextSize(16, holder.tvNum);

                                if (m_Listener != null) {
                                    m_Listener.getSelectSeatItem(convertView, m_hmSelectSeatAndSeq.get(strSeat), strSeat);
                                }

                                final View ConvertView = convertView;
                                convertView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (m_Listener != null) {
                                            m_Listener.onSeatItemClick(ConvertView, strSeat);
                                        }
                                    }
                                });
                                break;
                            } else {
                                if (true == m_hmSelectSeatAndSeq.containsKey(strSeat)) {
                                    //同行乘客位置
                                    holder.tvNum.setBackgroundResource(R.drawable.bg_select_seat_item_partner);
                                    holder.tvNum.setText(Integer.toString(m_hmSelectSeatAndSeq.get(strSeat) + 1));
                                    vScaleDef.setTextSize(16, holder.tvNum);

                                    if (m_Listener != null) {
                                        m_Listener.getPartnerSelectSeatItem(convertView, m_hmSelectSeatAndSeq.get(strSeat), strSeat);
                                    }

                                    final View ConvertView = convertView;
                                    convertView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (m_Listener != null) {
                                                m_Listener.onSeatItemClick(ConvertView, strSeat);
                                            }
                                        }
                                    });
                                    break;
                                } else {
                                    //空位
                                    holder.tvNum.setBackgroundResource(R.drawable.bg_select_seat_item_available);
                                    holder.tvNum.setText("");
                                    vScaleDef.setTextSize(16, holder.tvNum);

                                    final View ConvertView = convertView;
                                    convertView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (m_Listener != null) {
                                                m_Listener.onSeatItemClick(ConvertView, strSeat);
                                            }
                                        }
                                    });
                                    break;
                                }
                            }

                            //有人的座位
                        } else if (seatInfo.SeatStatus == CISeatInfo.CISeatStatus.Occupied) {
                            if (m_hmAlreadySelectSeat.get(strSeat) != null) {
                                if (strSeat.equals(m_strNowSelect)) {
                                    //橘色 當前選擇
                                    holder.tvNum.setBackgroundResource(R.drawable.bg_select_seat_item_your);
                                    holder.tvNum.setText(Integer.toString(m_hmSelectSeatAndSeq.get(strSeat) + 1));
                                    vScaleDef.setTextSize(16, holder.tvNum);

                                    if (m_Listener != null) {
                                        m_Listener.getSelectSeatItem(convertView, m_hmSelectSeatAndSeq.get(strSeat), strSeat);
                                    }

                                    final View ConvertView = convertView;
                                    convertView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (m_Listener != null) {
                                                m_Listener.onSeatItemClick(ConvertView, strSeat);
                                            }
                                        }
                                    });
                                    break;
                                } else {
                                    if (true == m_hmSelectSeatAndSeq.containsKey(strSeat)) {
                                        //同行乘客位置
                                        holder.tvNum.setBackgroundResource(R.drawable.bg_select_seat_item_partner);
                                        holder.tvNum.setText(Integer.toString(m_hmSelectSeatAndSeq.get(strSeat) + 1));
                                        vScaleDef.setTextSize(16, holder.tvNum);

                                        if (m_Listener != null) {
                                            m_Listener.getPartnerSelectSeatItem(convertView, m_hmSelectSeatAndSeq.get(strSeat), strSeat);
                                        }

                                        final View ConvertView = convertView;
                                        convertView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (m_Listener != null) {
                                                    m_Listener.onSeatItemClick(ConvertView, strSeat);
                                                }
                                            }
                                        });
                                        break;
                                    } else {
                                        //空位
                                        holder.tvNum.setBackgroundResource(R.drawable.bg_select_seat_item_available);
                                        holder.tvNum.setText("");
                                        vScaleDef.setTextSize(16, holder.tvNum);

                                        final View ConvertView = convertView;
                                        convertView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (m_Listener != null) {
                                                    m_Listener.onSeatItemClick(ConvertView, strSeat);
                                                }
                                            }
                                        });
                                        break;
                                    }
                                }
                            } else {
                                //其他人
                                holder.tvNum.setBackgroundResource(R.drawable.bg_select_seat_item_occupied);
                                holder.tvNum.setText("");
                                vScaleDef.setTextSize(13, holder.tvNum);
                                break;
                            }
                        }
                        break;
                    case Aisle:
                        //走道 顯示行數
                        holder.tvNum.setBackgroundColor(ContextCompat.getColor(m_Context, R.color.transparent));
                        holder.tvNum.setText(""+seatInfo.Row_Number);
                        vScaleDef.setTextSize(13, holder.tvNum);
                        break;
                    case Empty:
                        //空格
                        holder.tvNum.setBackgroundColor(ContextCompat.getColor(m_Context, R.color.transparent));
                        holder.tvNum.setText("");
                        vScaleDef.setTextSize(13, holder.tvNum);
                        break;
                    case Another:
                        //其他
                        holder.tvNum.setBackgroundColor(ContextCompat.getColor(m_Context, R.color.transparent));
                        holder.tvNum.setText("");
                        vScaleDef.setTextSize(13, holder.tvNum);
                        break;
                }

            } else {
                holder = (Holder) convertView.getTag();
            }
            return convertView;
        }

        public void setItemWidth(int iWidth) {
            m_iItemWidth = iWidth;
        }

        public void setSelectSeat(int iIndex, ArrayList<CIPassengerSeatItem> alPassenger){

            m_hmSelectSeatAndSeq.clear();

            for (int i = 0; i < alPassenger.size(); i++) {
                CIPassengerSeatItem paxItem = alPassenger.get(i);

//                    if (false == paxItem.m_bReturn) {
                //去程
                if ( i == iIndex ) {
                    m_strNowSelect = paxItem.m_strPassengerSeat;
                }
                if (!TextUtils.isEmpty(paxItem.m_strPassengerSeat)) {
                    m_hmSelectSeatAndSeq.put(paxItem.m_strPassengerSeat, paxItem.m_iPassengerSeq);
                }
//                    } else {
//                        //回程
//                        if (true == m_alPaxSeatData.get(i).m_bNowSelect) {
//                            m_strNowSelect = m_alPaxSeatData.get(i).m_strPassengerSeatReturn;
//                        }
//                        if (!TextUtils.isEmpty(paxItem.m_strPassengerSeatReturn)) {
//                            m_hmSelectSeatAndSeq.put(paxItem.m_strPassengerSeatReturn, paxItem.m_iPassengerSeqReturn);
//                        }
//                    }
            }
        }

        public void setAlreadySelect(String strSeat) {
            m_hmAlreadySelectSeat.put(strSeat, 1);
        }
    }
}
