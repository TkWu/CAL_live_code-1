package ci.ui.TextField.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.ui.TextField.Base.SViewHolder;
import ci.ui.define.ViewScaleDef;

/**
 * Created by kevincheng on 2016/2/18.
 */
public class CIMenusAdapter extends BaseAdapter implements Cloneable{

    private Context           m_context         = null;
    private ArrayList<String> m_alAllData       = null;
    private int               m_item_resource   = 0;

    public CIMenusAdapter(Context context,
                          ArrayList<String> alAllData,
                          int itemResource) {
        this.m_item_resource = itemResource;
        this.m_alAllData = alAllData;
        this.m_context = context;
    }

    public void setData(ArrayList<String> alAllData) {
        this.m_alAllData = alAllData;
    }

    @Override
    public int getCount() {
        if (m_alAllData != null) {
            return m_alAllData.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (m_alAllData != null) {
            return m_alAllData.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(m_context).inflate(m_item_resource, parent, false);
            holder = new SViewHolder(convertView);
            ViewScaleDef mBkUIDefine = ViewScaleDef.getInstance(m_context);
            mBkUIDefine.selfAdjustAllView(convertView);

        } else {
            holder = (SViewHolder) convertView.getTag();
        }
        String strText = m_alAllData.get(position);

        if (strText != null && strText.length() > 0) {
            ((TextView) holder.getView(R.id.tvContent)).setText(strText);
        }

        if(null != holder.getView(R.id.v_line)){
            if((m_alAllData.size() - 1) <= position){
                holder.getView(R.id.v_line).setVisibility(View.GONE);
            } else {
                holder.getView(R.id.v_line).setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }
    @Override
    public Object clone()  {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
