package ci.function.About.Adapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.About.CIGlobalServiceActivity.GroupItem;
import ci.function.About.CIServiceAreaActivity;
import ci.ws.Models.entities.CIGlobalServiceEntity;
import ci.ws.Models.entities.CIGlobalServiceList;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.object.CIDialPhoneNumberManager;
import ci.ws.cores.object.GsonTool;

/**
 * 全球服務頁面服務據點列表Adapter
 */
public class CIGlobalServiceListAdpater extends BaseExpandableListAdapter{
    /**
     * 顯示清單模式：
     * NORMAL（一般）, SEARCH（搜尋）, NEAREST_OFFICE（最近辦事處）
     * */
    public enum EMode {
        NORMAL, SEARCH, NEAREST_OFFICE
    }
    public static class GroupHolder {

        TextView        bigCategory,        //大分類
                        countyCateGory;     //國家分類
        RelativeLayout  rlcountyCategory,   //國家分類layout
                        rlBigCategory;      //大分類layout
    }


    public static class ChildHolder {
        RelativeLayout  rlPhoneClick,   //電話按鈕
                        rlMapClick;     //地圖按鈕
        ImageView       ivPhone,        //電話圖示
                        ivlistArrow,    //列表圖示
                        ivLocate;       //座標圖示
        TextView        tvAirLines,     //城市文字
                        tvAddress;      //城市地址
    }

    private ViewScaleDef         m_vScaleDef = null;
    private CIGlobalServiceList  m_datas     = null;
    private Context              m_context   = null;
    private ArrayList<GroupItem> m_items     = null;
    private CIAlertDialog        m_dialog    = null;
    private EMode                m_mode      = EMode.NORMAL;
    private CIDialPhoneNumberManager m_dialPhoneNumberManager   = null;
    public CIGlobalServiceListAdpater(Context context,
                                      ArrayList<GroupItem> items,
                                      CIGlobalServiceList datas,
                                      EMode mode) {
        this.m_context = context;
        this.m_items = items;
        this.m_datas = datas;
        this.m_mode = mode;
        m_vScaleDef = ViewScaleDef.getInstance(context);
        m_dialPhoneNumberManager = new CIDialPhoneNumberManager();
    }

    public void setItem(ArrayList<GroupItem> items){
        this.m_items = items;
    }

    public void setDatas(CIGlobalServiceList datas){
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
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        GroupHolder groupHolder = null;
        if ( null == convertView ){
            groupHolder = new GroupHolder();
            convertView = LayoutInflater.from(m_context).inflate( R.layout.layout_global_service_group_item, null);
            groupHolder.bigCategory     = (TextView)convertView.findViewById(R.id.tv_big_category);
            groupHolder.countyCateGory  = (TextView)convertView.findViewById(R.id.tv_county_category);
            groupHolder.rlcountyCategory = (RelativeLayout)convertView.findViewById(R.id.rl_county_category);
            groupHolder.rlBigCategory = (RelativeLayout)convertView.findViewById(R.id.rl_big_category);
            //自適應
            m_vScaleDef.selfAdjustAllView(groupHolder.rlcountyCategory);
            m_vScaleDef.selfAdjustAllView(groupHolder.rlBigCategory);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder)convertView.getTag();
        }

        if(EMode.NORMAL == m_mode){/**一般模式*/
            if(0 == groupPosition){
                groupHolder.bigCategory.setText(m_context.getString(R.string.all_area));
                groupHolder.rlBigCategory.setVisibility(ViewGroup.VISIBLE);
            } else {
                groupHolder.rlBigCategory.setVisibility(ViewGroup.GONE);
            }
            groupHolder.countyCateGory.setText(m_items.get(groupPosition).category);
            groupHolder.rlcountyCategory.setVisibility(ViewGroup.VISIBLE);

        } else if(EMode.SEARCH == m_mode){/**搜尋模式*/
            groupHolder.bigCategory.setText("");
            groupHolder.rlBigCategory.setVisibility(ViewGroup.GONE);
            //2016-11-01 Ryan, 搜尋模式一樣顯示Group名稱
            groupHolder.rlcountyCategory.setVisibility(ViewGroup.VISIBLE);
            groupHolder.countyCateGory.setText(m_items.get(groupPosition).category);
            //groupHolder.rlcountyCategory.setVisibility(ViewGroup.GONE);

        } else if(EMode.NEAREST_OFFICE == m_mode){/**最近辦事處模式*/
            if(0 == groupPosition){
                groupHolder.bigCategory.setText(m_context.getString(R.string.nearest_office));
                groupHolder.rlBigCategory.setVisibility(ViewGroup.VISIBLE);
                groupHolder.rlcountyCategory.setVisibility(ViewGroup.GONE);
            } else if(1 == groupPosition){
                groupHolder.bigCategory.setText(m_context.getString(R.string.all_area));
                groupHolder.rlBigCategory.setVisibility(ViewGroup.VISIBLE);
                groupHolder.rlcountyCategory.setVisibility(ViewGroup.VISIBLE);
            } else if(1 < groupPosition) {
                groupHolder.rlBigCategory.setVisibility(ViewGroup.GONE);
                groupHolder.rlcountyCategory.setVisibility(ViewGroup.VISIBLE);
            }
            groupHolder.countyCateGory.setText(m_items.get(groupPosition).category);

        }


        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition,final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ChildHolder childHolder = null;
        if ( null == convertView ){
            childHolder = new ChildHolder();
            convertView = LayoutInflater.from(m_context).inflate(R.layout.layout_global_service_child_item, null);
            childHolder.rlMapClick      = (RelativeLayout) convertView.findViewById(R.id.rl_map_click);
            childHolder.rlPhoneClick    = (RelativeLayout) convertView.findViewById(R.id.rl_phone_click);
            childHolder.ivlistArrow     = (ImageView) convertView.findViewById(R.id.iv_ic_list_arrow);
            childHolder.ivLocate        = (ImageView) convertView.findViewById(R.id.tv_ic_locate_2);
            childHolder.ivPhone         = (ImageView) convertView.findViewById(R.id.iv_ic_phone);
            childHolder.tvAddress       = (TextView) convertView.findViewById(R.id.tv_address);
            childHolder.tvAirLines      = (TextView) convertView.findViewById(R.id.tv_city_name);

            //自適應
            m_vScaleDef.selfAdjustAllView(childHolder.rlMapClick );
            m_vScaleDef.selfAdjustAllView(childHolder.rlPhoneClick );
            m_vScaleDef.selfAdjustAllView(convertView.findViewById(R.id.view_line_h));
            m_vScaleDef.selfAdjustSameScaleView(childHolder.ivlistArrow,24,24);
            m_vScaleDef.selfAdjustSameScaleView(childHolder.ivLocate, 18, 18);
            m_vScaleDef.selfAdjustSameScaleView(childHolder.ivPhone, 28, 28);
            //設定最大寬度
            childHolder.tvAirLines.setMaxWidth(m_vScaleDef.getLayoutWidth(196));
            //設定到tag
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder)convertView.getTag();
        }
            childHolder.tvAirLines.setText(m_datas.get(m_items.get(groupPosition).childItems.get(childPosition).index).branch);
            childHolder.tvAddress.setText(m_datas.get(m_items.get(groupPosition).childItems.get(childPosition).index).address);


        if(EMode.NORMAL == m_mode){/**一般模式*/
            childHolder.ivLocate.setVisibility(ViewGroup.GONE);
        } else if(EMode.SEARCH == m_mode){/**搜尋模式*/
            childHolder.ivLocate.setVisibility(ViewGroup.GONE);
        } else if(EMode.NEAREST_OFFICE == m_mode){/**最近辦事處模式*/
            if(0 == groupPosition && 0 == childPosition){
                childHolder.ivLocate.setVisibility(ViewGroup.VISIBLE);
            } else {
                childHolder.ivLocate.setVisibility(ViewGroup.GONE);
            }
        }

        childHolder.rlPhoneClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phone = m_datas.get(m_items.get(groupPosition).childItems.get(childPosition).index).ticket_op_tel;
                m_dialPhoneNumberManager.showAlertDialogForDialPhoneNumber(m_context, phone);
            }
        });

        childHolder.rlMapClick.setOnClickListener(new View.OnClickListener() {
            final CIGlobalServiceEntity branchData = m_datas.get(m_items.get(groupPosition).childItems.get(childPosition).index);
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra(UiMessageDef.BUNDLE_ACTIVITY_DATA_BRANCH, GsonTool.toJson(branchData));
                data.setClass(m_context, CIServiceAreaActivity.class);
                m_context.startActivity(data);
                ((Activity)m_context).overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
