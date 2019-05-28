package ci.function.SeatSelection;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.chinaairlines.mobile30.R;

import java.util.HashMap;

import ci.ws.Models.CISeatFloor;
import ci.ws.Models.entities.CISeatInfo;

/**
 * Created by flowmahuang on 2016/4/18.
 */
@Deprecated
public class CISelectSeatRecyclerViewAdapter extends RecyclerView.Adapter {
    private Context m_Context;
    private CISeatFloor m_DataList;

    private String nowClick = "";
    private int seq = 0;
    private int itemWidth;
    private HashMap<String, Integer> seatSeq;
    private HashMap<String,Integer> alreadySelectSeat;

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position, String display);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public CISelectSeatRecyclerViewAdapter(Context context, CISeatFloor seqList) {
        this.m_Context = context;
        seatSeq = new HashMap<>();
        alreadySelectSeat = new HashMap<>();
        m_DataList = seqList;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CISelectSeatRecyclerViewItemView view = new CISelectSeatRecyclerViewItemView(m_Context);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final CISelectSeatRecyclerViewItemView convertView = (CISelectSeatRecyclerViewItemView) holder.itemView;
        if (position != m_DataList.SeatCol * m_DataList.SeatRow) {
            final CISeatInfo info = m_DataList.arSeatList.get(position);
            String colName = info.Col_Name;
            String rowNumber = info.Row_Number;
            final String NumEng = rowNumber + colName;
            switch (info.SeatType) {
                case Seat:
                    if (info.SeatStatus == CISeatInfo.CISeatStatus.Available) {
                        if (NumEng.equals(nowClick)) {
                            convertView.setImageViewImage(R.drawable.bg_select_seat_item_your,
                                    Integer.toString(seq + 1), itemWidth);
                            convertView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mOnItemClickListener != null) {
                                        mOnItemClickListener.onItemClick(v, position, NumEng);
                                    }
                                }
                            });
                            break;
                        } else if (seatSeq.get(NumEng) != null) {
                            convertView.setImageViewImage(R.drawable.bg_select_seat_item_partner,
                                    Integer.toString(seatSeq.get(NumEng) + 1), itemWidth);
                        } else {
                            convertView.setImageViewImage(R.drawable.bg_select_seat_item_available, "", itemWidth);
                            convertView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mOnItemClickListener != null) {
                                        mOnItemClickListener.onItemClick(v, position, NumEng);
                                    }
                                }
                            });
                            break;
                        }
                    } else if (info.SeatStatus == CISeatInfo.CISeatStatus.Occupied) {
                        if (alreadySelectSeat.get(NumEng) != null){
                            if (NumEng.equals(nowClick)) {
                                convertView.setImageViewImage(R.drawable.bg_select_seat_item_your,
                                        Integer.toString(seq + 1), itemWidth);
                                convertView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (mOnItemClickListener != null) {
                                            mOnItemClickListener.onItemClick(v, position, NumEng);
                                        }
                                    }
                                });
                                break;
                            } else if (seatSeq.get(NumEng) != null) {
                                convertView.setImageViewImage(R.drawable.bg_select_seat_item_partner,
                                        Integer.toString(seatSeq.get(NumEng) + 1), itemWidth);
                            } else {
                                convertView.setImageViewImage(R.drawable.bg_select_seat_item_available, "", itemWidth);
                                convertView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (mOnItemClickListener != null) {
                                            mOnItemClickListener.onItemClick(v, position, NumEng);
                                        }
                                    }
                                });
                                break;
                            }
                        } else {
                            convertView.setImageViewImage(R.drawable.bg_select_seat_item_occupied, "", itemWidth);
                        }
                    }
                    break;
                case Aisle:
                    convertView.setTextViewText(rowNumber);
                    break;
                case Empty:
                    convertView.setTextViewText("");
                    break;
                case Another:
                    convertView.setTextViewText("");
                    break;
            }
        } else {
            convertView.setFinalRow();
        }

    }

    @Override
    public int getItemCount() {
        return m_DataList.SeatCol * m_DataList.SeatRow + 1;
    }

    public void setNowSelection(String display) {
        nowClick = display;
    }

    public void setSelectSeq(int s) {
        seq = s;
    }

    public void setPartnerSelection(String display) {
        seatSeq.put(display, seq);
    }

    public void delPartnerSelection(String display) {
        seatSeq.remove(display);
    }

    public void clearPartnerSelection() {
        seatSeq.clear();
    }

    public void setItemWidth(int i) {
        itemWidth = i;
    }

    public void setAlreadySelect(String seat){alreadySelectSeat.put(seat,1);}
}
