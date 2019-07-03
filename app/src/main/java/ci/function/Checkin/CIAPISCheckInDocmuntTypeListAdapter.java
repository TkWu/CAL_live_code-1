package ci.function.Checkin;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ci.function.TimeTable.CISelectDepartureListAdpater;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ws.Models.entities.CIApisDocmuntTypeEntity;

public class CIAPISCheckInDocmuntTypeListAdapter extends BaseExpandableListAdapter {
    public enum EMode {
        SAVED, ALL
    }
    public static class GroupHolder {
        TextView bigCategory;        //大分類
        RelativeLayout rlBigCategory;      //大分類layout
    }


    public class ChildHolder {
        TextView tvDoctypes;   //APIS 類型
    }

    private ViewScaleDef m_vScaleDef = null;
    private List<CIApisDocmuntTypeEntity> m_datas     = null;
    private Context m_context   = null;
    private ArrayList<CIAPISCheckInDocmuntTypeSelectMenuActivity.GroupItem> m_items     = null;
    private CIAlertDialog m_dialog    = null;
    private EMode                m_mode      = EMode.ALL;

    public CIAPISCheckInDocmuntTypeListAdapter(Context context,
                                        ArrayList<CIAPISCheckInDocmuntTypeSelectMenuActivity.GroupItem> items,
                                        List<CIApisDocmuntTypeEntity> datas,
                                        EMode mode) {
        this.m_context = context;
        this.m_items = items;
        this.m_datas = datas;
        this.m_mode = mode;
        m_vScaleDef = ViewScaleDef.getInstance(context);
    }


    public void setItem(ArrayList<CIAPISCheckInDocmuntTypeSelectMenuActivity.GroupItem> items){
        this.m_items = items;
    }

    public ArrayList<CIAPISCheckInDocmuntTypeSelectMenuActivity.GroupItem> getItem(){
        return this.m_items;
    }

    public void setDatas(List<CIApisDocmuntTypeEntity> datas){
        this.m_datas = datas;
    }

    public void setMode(EMode mode){
        this.m_mode = mode;
    }

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
            return m_items.get(groupPosition).childItems.size();
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return m_items.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return m_items.get(groupPosition).childItems.get(childPosition);
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
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition,final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        CIAPISCheckInDocmuntTypeListAdapter.ChildHolder childHolder = null;

        childHolder = new CIAPISCheckInDocmuntTypeListAdapter.ChildHolder();

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
