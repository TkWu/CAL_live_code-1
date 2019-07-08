package ci.function.Checkin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.List;

import ci.function.Core.SLog;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ws.Models.entities.CIApisDocmuntTypeEntity;
import ci.ws.Models.entities.CIApisQryRespEntity;

public class CIAPISCheckInDocmuntTypeListAdapter extends BaseExpandableListAdapter {

    private int               m_item_resource   = 0;

    public static class GroupHolder {
        TextView        bigCategory,        //大分類
                        countyCateGory;     //國家分類
        RelativeLayout  rlcountyCategory,   //國家分類layout
                        rlBigCategory;      //大分類layout
    }

    public class ChildHolder {
        TextView tvDoctypes;   //APIS 類型
    }

    private ViewScaleDef m_vScaleDef = null;
    //private List<CIApisDocmuntTypeEntity> m_datas     = null;
    //private List<CIApisQryRespEntity.ApisRespDocObj> m_datas     = null;
    private Context m_context   = null;
    private ArrayList<CIAPISCheckInDocmuntTypeSelectMenuActivity.GroupItem> m_items     = null;
    private CIAlertDialog m_dialog    = null;
//    private EDoctypeMode  m_mode      = EDoctypeMode.OTHER;

    public CIAPISCheckInDocmuntTypeListAdapter(Context context,
                                        ArrayList<CIAPISCheckInDocmuntTypeSelectMenuActivity.GroupItem> items, int itemResource)
    {
        this.m_context = context;
        this.m_items = items;
        this.m_item_resource = itemResource;
        m_vScaleDef = ViewScaleDef.getInstance(context);
    }


    public void setItem(ArrayList<CIAPISCheckInDocmuntTypeSelectMenuActivity.GroupItem> items){
        this.m_items = items;
    }

    public ArrayList<CIAPISCheckInDocmuntTypeSelectMenuActivity.GroupItem> getItem(){
        return this.m_items;
    }

    public void setDatas(List<CIApisDocmuntTypeEntity> datas){
        //this.m_datas = datas;
    }

//    public void setMode(EDoctypeMode mode){
//        this.m_mode = mode;
//    }

    @Override
    public int getGroupCount() {

        if (null == m_items) {
            return 0;
        } else {
            return m_items.size();
        }
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        if (null == m_items) {
            return 0;
        } else if (null == m_items.get(groupPosition)) {
            return 0;
        } else {
            return m_items.get(groupPosition).docsObject.size();
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return m_items.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return m_items.get(groupPosition).docsObject.get(childPosition);
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
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder groupHolder = null;

        groupHolder = new GroupHolder();
        convertView = LayoutInflater.from(m_context).inflate(R.layout.layout_global_service_group_item, null);
        groupHolder.bigCategory     = (TextView)convertView.findViewById(R.id.tv_big_category);
        groupHolder.countyCateGory  = (TextView)convertView.findViewById(R.id.tv_county_category);
        groupHolder.rlcountyCategory = (RelativeLayout)convertView.findViewById(R.id.rl_county_category);
        groupHolder.rlBigCategory = (RelativeLayout)convertView.findViewById(R.id.rl_big_category);
        //自適應
        m_vScaleDef.selfAdjustAllView(groupHolder.rlBigCategory);

        groupHolder.bigCategory.setText(m_items.get(groupPosition).apis_group_name);

        groupHolder.bigCategory.setVisibility(View.VISIBLE);
        groupHolder.rlBigCategory.setVisibility(ViewGroup.VISIBLE);
        groupHolder.rlcountyCategory.setVisibility(ViewGroup.GONE);

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition,final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        CIAPISCheckInDocmuntTypeListAdapter.ChildHolder childHolder;

        if (convertView == null) {
            childHolder = new CIAPISCheckInDocmuntTypeListAdapter.ChildHolder();
            if (m_items.get(groupPosition).apis_group_type.equals(CIAPISCheckInDocmuntTypeSelectMenuActivity.CICheckInAPISGroupType.ALL.name())) {
                convertView = LayoutInflater.from(m_context).inflate(R.layout.list_item_textfeild_fullpage_menu, parent, false);
                childHolder.tvDoctypes = (TextView) convertView.findViewById(R.id.tvContent);
                //自適應
                m_vScaleDef.selfAdjustAllView(childHolder.tvDoctypes);
                childHolder.tvDoctypes.setText(m_items.get(groupPosition).docsObject.get(childPosition).documentName);

            }else{
                convertView = LayoutInflater.from(m_context).inflate(R.layout.list_item_textfeild_fullpage_menu, parent, false);
                childHolder.tvDoctypes = (TextView) convertView.findViewById(R.id.tvContent);
                //自適應
                m_vScaleDef.selfAdjustAllView(childHolder.tvDoctypes);
                childHolder.tvDoctypes.setText(m_items.get(groupPosition).docsObject.get(childPosition).documentName);
            }

        } else {
            childHolder = (CIAPISCheckInDocmuntTypeListAdapter.ChildHolder) convertView.getTag();
        }


        //childHolder.tvDoctypes.setText(m_datas.get(m_items.get(groupPosition).childItems.get(childPosition).index).code_1A);

        SLog.d(m_items.get(groupPosition).docsObject.get(childPosition).documentName);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
