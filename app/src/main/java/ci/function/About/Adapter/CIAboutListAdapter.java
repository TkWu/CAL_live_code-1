package ci.function.About.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.About.item.CIAboutChildItem;
import ci.function.About.item.CIAboutGroupItem;
import ci.ui.define.ViewScaleDef;

/** 關於華航adapter
 * Created by jlchen on 2016/4/7.
 */
public class CIAboutListAdapter extends BaseExpandableListAdapter{

    public static class GroupHolder {
        TextView        tvText;         //項目名稱
    }

    public static class ChildHolder {
        RelativeLayout  rlCall,         //撥打電話
                        rlNoaml;        //其他一般子項目
        ImageView       ivPhone,
                        ivIcon,         //圖示
                        ivlistArrow;    //箭頭
        TextView        tvCall,         //電話
                        tvService,      //客服中心名稱
                        tvText;         //一般子項目名稱
        View            vLine;
    }


    public interface OnAboutListAdapterListener{
        void OnChildItemClick(CIAboutChildItem childItem);
    }

    private OnAboutListAdapterListener  m_Listener  = null;
    private Context                     m_Context   = null;
    private ViewScaleDef                m_vScaleDef = null;
    private ArrayList<CIAboutGroupItem> m_alData    = null;

    public CIAboutListAdapter(Context context,
                              ArrayList<CIAboutGroupItem> alData,
                              OnAboutListAdapterListener listener){
        this.m_Context  = context;
        this.m_vScaleDef= ViewScaleDef.getInstance(m_Context);
        this.m_alData   = alData;
        this.m_Listener = listener;
    }

    @Override
    public int getGroupCount() {
        if (null == m_alData) {
            return 0;
        } else {
            return m_alData.size();
        }
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (null == m_alData) {
            return 0;
        } else if (null == m_alData.get(groupPosition)) {
            return 0;
        } else {
            return m_alData.get(groupPosition).getChildList().size();
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return m_alData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return m_alData.get(groupPosition).getChildList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        GroupHolder groupHolder = null;
        if ( null == convertView ){
            groupHolder = new GroupHolder();
            convertView = LayoutInflater.from(m_Context).inflate(
                    R.layout.layout_view_about_group_item, null);
            groupHolder.tvText     = (TextView)convertView.findViewById(R.id.tv);
            //自適應
            m_vScaleDef.selfAdjustAllView(groupHolder.tvText);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder)convertView.getTag();
        }

        groupHolder.tvText.setText(m_alData.get(groupPosition).getTitle());

        return convertView;
    }

    public void setServiceInfo(String office,String number){
        if(null != m_alData && 0 != m_alData.size() ){
            CIAboutChildItem childData = m_alData.get(0).getChildList().get(0);
            childData.setText(office);
            childData.setCallNumber(number);
        }
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ChildHolder childHolder = null;
        if ( null == convertView ){
            childHolder = new ChildHolder();
            convertView = LayoutInflater.from(m_Context).inflate(
                    R.layout.layout_view_about_child_item, null);
            childHolder.rlCall          = (RelativeLayout) convertView.findViewById(R.id.rl_call);
            childHolder.rlNoaml         = (RelativeLayout) convertView.findViewById(R.id.rl_nomal);
            childHolder.ivPhone         = (ImageView) convertView.findViewById(R.id.iv_call);
            childHolder.ivIcon          = (ImageView) convertView.findViewById(R.id.iv_icon);
            childHolder.ivlistArrow     = (ImageView) convertView.findViewById(R.id.iv_arrow);
            childHolder.tvCall          = (TextView) convertView.findViewById(R.id.tv_call_number);
            childHolder.tvService       = (TextView) convertView.findViewById(R.id.tv_call_service);
            childHolder.tvText          = (TextView) convertView.findViewById(R.id.tv);
            childHolder.vLine           = (View) convertView.findViewById(R.id.v_line);

            //自適應
            m_vScaleDef.selfAdjustAllView(childHolder.rlCall);
            m_vScaleDef.selfAdjustAllView(childHolder.rlNoaml);
            m_vScaleDef.selfAdjustSameScaleView(childHolder.ivPhone, 28, 28);
            m_vScaleDef.selfAdjustSameScaleView(childHolder.ivIcon, 28, 28);
            m_vScaleDef.selfAdjustSameScaleView(childHolder.ivlistArrow,24,24);

            //設定到tag
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder)convertView.getTag();
        }

        final CIAboutChildItem childData = m_alData.get(groupPosition).getChildList().get(childPosition);
        if ( null != childData.getCallNumber() ){
            childHolder.rlCall.setVisibility(View.VISIBLE);
            childHolder.rlNoaml.setVisibility(View.GONE);

            //取得當地的客服電話
            childHolder.tvCall.setText(childData.getCallNumber());
            childHolder.tvService.setText(childData.getText());

            childHolder.rlCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m_Listener.OnChildItemClick(childData);
                }
            });
        }else {
            childHolder.rlCall.setVisibility(View.GONE);
            childHolder.rlNoaml.setVisibility(View.VISIBLE);

            childHolder.rlNoaml.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    m_Listener.OnChildItemClick(childData);
                }
            });

            if ( 0 != childData.getIconId() ){
                childHolder.ivIcon.setVisibility(View.VISIBLE);
                childHolder.ivIcon.setImageResource(childData.getIconId());
            }else {
                childHolder.ivIcon.setVisibility(View.GONE);
            }

            childHolder.tvText.setText(childData.getText());
        }

        //最後一列不顯示分隔線
        if (true == isLastChild){
            childHolder.vLine.setVisibility(View.GONE);
        }else {
            childHolder.vLine.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
