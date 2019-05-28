package ci.function.SpecialCondition.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.ui.define.ViewScaleDef;

/**
 * Created by jlchen on 2016/3/7.
 */
public class CITravelAlertAdapter extends BaseAdapter{

    private static class ItemHolder{

        RelativeLayout  m_rlayout = null;

        TextView        m_tvDate = null,
                        m_tvTitle = null,
                        m_tvMsg = null;
    }

    private Context             m_context 		= null;

    private ArrayList<String>  m_alAlertData    = new ArrayList<String>();
    private ViewScaleDef       m_vScaleDef;

    public CITravelAlertAdapter(Context context,
                              ArrayList<String> arDataList){
        this.m_context      = context;
        this.m_alAlertData  = arDataList;

        this.m_vScaleDef    = ViewScaleDef.getInstance(m_context);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ItemHolder itemHolder;

        if (null == convertView)
        {
            itemHolder = new ItemHolder();
            convertView = LayoutInflater.from(m_context).inflate(
                    R.layout.layout_view_travel_alert,
                    parent,
                    false);

            itemHolder.m_rlayout = (RelativeLayout)convertView.findViewById(R.id.rl_travel_alert);
            itemHolder.m_tvDate = (TextView)convertView.findViewById(R.id.tv_date_time);
            itemHolder.m_tvTitle = (TextView)convertView.findViewById(R.id.tv_title);
            itemHolder.m_tvMsg = (TextView)convertView.findViewById(R.id.tv_msg);

            convertView.setTag(itemHolder);
        }else {
            itemHolder = (ItemHolder) convertView.getTag();
        }

        setTextSizeAndLayoutParams(itemHolder, position);

        return convertView;
    }

    private void setTextSizeAndLayoutParams(ItemHolder itemHolder, int position) {
        m_vScaleDef.setPadding(itemHolder.m_rlayout, 30, 10, 30, 40);

        if ( 0 == position){

            m_vScaleDef.setMargins(itemHolder.m_rlayout, 0, 10, 0, 0);
        }
        else if ( position == m_alAlertData.size()-1 ){

            m_vScaleDef.setMargins(itemHolder.m_rlayout, 0, 0, 0, 10);
        }

        m_vScaleDef.setTextSize(13, itemHolder.m_tvDate);
        itemHolder.m_tvDate.setMinHeight(m_vScaleDef.getLayoutHeight(16));

        m_vScaleDef.setTextSize(20, itemHolder.m_tvTitle);
        m_vScaleDef.setMargins(itemHolder.m_tvTitle, 0, 14, 0, 0);
        itemHolder.m_tvTitle.setMinHeight(m_vScaleDef.getLayoutHeight(24));

        m_vScaleDef.setTextSize(ViewScaleDef.DEF_TEXT_SIZE_16, itemHolder.m_tvMsg);
        m_vScaleDef.setMargins(itemHolder.m_tvMsg, 0, 16, 0, 0);
        itemHolder.m_tvMsg.setLineSpacing(m_vScaleDef.getLayoutHeight(4.7), 1.0f);
    }
}
