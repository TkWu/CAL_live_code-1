package ci.function.TimeTable;

import android.content.Context;
import ci.function.Core.SLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.List;

import ci.function.TimeTable.CISelectDepartureAirpotActivity;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ws.Models.entities.CIFlightStationEntity;

/**
 * 全球服務頁面服務據點列表Adapter
 */
public class CISelectDepartureListAdpater extends BaseExpandableListAdapter{
    /**
     * 顯示清單模式：
     * NORMAL（一般）, SEARCH（搜尋）, NEAREST_OFFICE（最近辦事處）
     * */
    public enum EMode {
        NORMAL, RECENT, SEARCH, NEAREST_OFFICE
    }
    public static class GroupHolder {

        TextView        bigCategory,        //大分類
                        countyCateGory;     //國家分類
        RelativeLayout  rlcountyCategory,   //國家分類layout
                        rlBigCategory;      //大分類layout
    }


    public class ChildHolder {
        ImageView ivLocate; //位置圖示

        TextView tvAirLines,   //機場
                tvCity, //國家
                tvAbbreviation; //城市縮寫

    }

    private ViewScaleDef m_vScaleDef = null;
    private List<CIFlightStationEntity>  m_datas     = null;
    private Context              m_context   = null;
    private ArrayList<CISelectDepartureAirpotActivity.GroupItem> m_items     = null;
    private CIAlertDialog m_dialog    = null;
    private EMode                m_mode      = EMode.NORMAL;

    public CISelectDepartureListAdpater(Context context,
                                        ArrayList<CISelectDepartureAirpotActivity.GroupItem> items,
                                        List<CIFlightStationEntity> datas,
                                        EMode mode) {
        this.m_context = context;
        this.m_items = items;
        this.m_datas = datas;
        this.m_mode = mode;
        m_vScaleDef = ViewScaleDef.getInstance(context);
    }

    public void setItem(ArrayList<CISelectDepartureAirpotActivity.GroupItem> items){
        this.m_items = items;
    }

    public ArrayList<CISelectDepartureAirpotActivity.GroupItem> getItem(){
        return this.m_items;
    }

    public void setDatas(List<CIFlightStationEntity> datas){
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

        convertView = LayoutInflater.from(m_context).inflate(R.layout.layout_global_service_group_item, null);
        groupHolder.bigCategory     = (TextView)convertView.findViewById(R.id.tv_big_category);
        groupHolder.countyCateGory  = (TextView)convertView.findViewById(R.id.tv_county_category);
        groupHolder.rlcountyCategory = (RelativeLayout)convertView.findViewById(R.id.rl_county_category);
        groupHolder.rlBigCategory = (RelativeLayout)convertView.findViewById(R.id.rl_big_category);
        //自適應
        m_vScaleDef.selfAdjustAllView(groupHolder.rlcountyCategory);
        m_vScaleDef.selfAdjustAllView(groupHolder.rlBigCategory);

        if(EMode.NORMAL == m_mode){/**一般模式*/
            if(0 == groupPosition){
                groupHolder.bigCategory.setText(R.string.select_airport_section_all_airports);
                groupHolder.bigCategory.setVisibility(View.VISIBLE);
                groupHolder.rlBigCategory.setVisibility(ViewGroup.VISIBLE);
                groupHolder.rlcountyCategory.setVisibility(ViewGroup.VISIBLE);
            } else {
                groupHolder.bigCategory.setVisibility(View.GONE);
                groupHolder.rlBigCategory.setVisibility(ViewGroup.GONE);
                groupHolder.rlcountyCategory.setVisibility(ViewGroup.VISIBLE);
            }

            groupHolder.countyCateGory.setText(m_items.get(groupPosition).country);

        } else if(EMode.RECENT == m_mode) {/**Recent*/
            if(0 == groupPosition){
                groupHolder.bigCategory.setText(R.string.select_airport_section_recent);
                groupHolder.bigCategory.setVisibility(View.VISIBLE);
                groupHolder.rlBigCategory.setVisibility(ViewGroup.VISIBLE);
                groupHolder.rlcountyCategory.setVisibility(ViewGroup.GONE);
            }else if(1 == groupPosition){
                groupHolder.bigCategory.setText(R.string.select_airport_section_all_airports);
                groupHolder.bigCategory.setVisibility(View.VISIBLE);
                groupHolder.rlBigCategory.setVisibility(ViewGroup.VISIBLE);
                groupHolder.rlcountyCategory.setVisibility(ViewGroup.VISIBLE);
            } else {
                groupHolder.bigCategory.setVisibility(View.GONE);
                groupHolder.rlBigCategory.setVisibility(ViewGroup.GONE);
                groupHolder.rlcountyCategory.setVisibility(ViewGroup.VISIBLE);
            }

            groupHolder.countyCateGory.setText(m_items.get(groupPosition).country);

        } else if(EMode.SEARCH == m_mode){/**搜尋模式*/
            groupHolder.bigCategory.setText("");
            groupHolder.rlBigCategory.setVisibility(ViewGroup.GONE);
            groupHolder.rlcountyCategory.setVisibility(ViewGroup.GONE);

        } else if(EMode.NEAREST_OFFICE == m_mode) {/**最近辦事處模式*/

            String spilt[] = m_datas.get(m_items.get(1).childItems.get(0).index).country.split(",");

            if (spilt.length > 1) {

//                if (spilt[1].equals("recent")){
                    if (0 == groupPosition) {
                        groupHolder.bigCategory.setText(R.string.select_airport_section_airports_nearby);
                        groupHolder.bigCategory.setVisibility(View.VISIBLE);
                        groupHolder.rlBigCategory.setVisibility(ViewGroup.VISIBLE);
                        groupHolder.rlcountyCategory.setVisibility(ViewGroup.GONE);
                    } else if (1 == groupPosition) {
                        groupHolder.bigCategory.setText(R.string.select_airport_section_recent);
                        groupHolder.bigCategory.setVisibility(View.VISIBLE);
                        groupHolder.rlBigCategory.setVisibility(ViewGroup.VISIBLE);
                        groupHolder.rlcountyCategory.setVisibility(ViewGroup.GONE);
                    } else if (2 == groupPosition) {
                        groupHolder.bigCategory.setText(R.string.select_airport_section_all_airports);
                        groupHolder.bigCategory.setVisibility(View.VISIBLE);
                        groupHolder.rlBigCategory.setVisibility(ViewGroup.VISIBLE);
                        groupHolder.rlcountyCategory.setVisibility(ViewGroup.VISIBLE);
                    } else {
                        groupHolder.bigCategory.setVisibility(View.GONE);
                        groupHolder.rlBigCategory.setVisibility(ViewGroup.GONE);
                        groupHolder.rlcountyCategory.setVisibility(ViewGroup.VISIBLE);
                    }
                    groupHolder.countyCateGory.setText(m_items.get(groupPosition).country);
//                }


            }else{

                if (0 == groupPosition) {
                    groupHolder.bigCategory.setText(R.string.select_airport_section_airports_nearby);
                    groupHolder.bigCategory.setVisibility(View.VISIBLE);
                    groupHolder.rlBigCategory.setVisibility(ViewGroup.VISIBLE);
                    groupHolder.rlcountyCategory.setVisibility(ViewGroup.GONE);
                } else if (1 == groupPosition) {
                    groupHolder.bigCategory.setText(R.string.select_airport_section_all_airports);
                    groupHolder.bigCategory.setVisibility(View.VISIBLE);
                    groupHolder.rlBigCategory.setVisibility(ViewGroup.VISIBLE);
                    groupHolder.rlcountyCategory.setVisibility(ViewGroup.VISIBLE);
                } else {
                    groupHolder.bigCategory.setVisibility(View.GONE);
                    groupHolder.rlBigCategory.setVisibility(ViewGroup.GONE);
                    groupHolder.rlcountyCategory.setVisibility(ViewGroup.VISIBLE);
                }
                groupHolder.countyCateGory.setText(m_items.get(groupPosition).country);
            }



        }


        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition,final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ChildHolder childHolder = null;

        childHolder = new ChildHolder();

        convertView = LayoutInflater.from(m_context).inflate(R.layout.layout_select_departure_airport_item, null);
        childHolder.ivLocate = (ImageView) convertView.findViewById(R.id.iv_locate);
        childHolder.tvCity = (TextView) convertView.findViewById(R.id.tv_city_name);
        childHolder.tvAirLines = (TextView) convertView.findViewById(R.id.tv_airport);
        childHolder.tvAbbreviation = (TextView) convertView.findViewById(R.id.tv_city_abbreviation);

        //自適應

        m_vScaleDef.setPadding(convertView, 0, 0, 0, 14.3);
        m_vScaleDef.selfAdjustAllView(childHolder.ivLocate);
        m_vScaleDef.selfAdjustAllView(childHolder.tvAirLines);
        m_vScaleDef.selfAdjustAllView(childHolder.tvAbbreviation);
        m_vScaleDef.selfAdjustAllView(childHolder.tvCity);
        m_vScaleDef.selfAdjustSameScaleView(childHolder.ivLocate, 24, 24);

        childHolder.tvAbbreviation.setText(m_datas.get(m_items.get(groupPosition).childItems.get(childPosition).index).IATA);

        //決定是否要畫item間隔線條
        if (childPosition >= 1  && childPosition < getChildrenCount(groupPosition)) {

            View line = new View(m_context);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, m_vScaleDef.getLayoutHeight(1));

            line.setBackgroundResource(R.color.white_30);

            line.setLayoutParams(layoutParams);

            ((RelativeLayout) convertView).addView(line);

        }else if(EMode.SEARCH == m_mode){/**搜尋模式*/

            View line = new View(m_context);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, m_vScaleDef.getLayoutHeight(1));

            line.setBackgroundResource(R.color.white_30);

            line.setLayoutParams(layoutParams);

            ((RelativeLayout) convertView).addView(line);
        }

        if(groupPosition == getGroupCount() - 1 && childPosition == getChildrenCount(groupPosition) - 1){

            m_vScaleDef.setPadding(convertView, 0, 0, 0, 0);

            View line = new View(m_context);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, m_vScaleDef.getLayoutHeight(1));

            layoutParams.addRule(RelativeLayout.BELOW, childHolder.tvCity.getId());

            layoutParams.setMargins(0, m_vScaleDef.getLayoutHeight(14.3), 0, 0);

            line.setBackgroundResource(R.color.white_30);

            line.setLayoutParams(layoutParams);

            ((RelativeLayout) convertView).addView(line);
        }


        //逗號切割區分鄰近與最近查詢
        String spilt[] = m_datas.get(m_items.get(groupPosition).childItems.get(childPosition).index).country.split(",");

        //國家名稱後面near airport
        if (spilt.length > 1 && spilt[1].equals("near airport")) {

            childHolder.ivLocate.setVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)childHolder.tvAirLines.getLayoutParams();
            params.addRule(RelativeLayout.RIGHT_OF, childHolder.ivLocate.getId());
            childHolder.tvAirLines.setLayoutParams(params);

            //2016-08-03 調整顯示位置
            childHolder.tvCity.setText(m_datas.get(m_items.get(groupPosition).childItems.get(childPosition).index).airport_name);
            childHolder.tvAirLines.setText(m_datas.get(m_items.get(groupPosition).childItems.get(childPosition).index).localization_name
                    + ", " + spilt[0]);

            //國家名稱後面recent
        }else if (spilt.length > 1 && spilt[1].equals("recent")) {

            childHolder.ivLocate.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) childHolder.tvAirLines.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_LEFT, childHolder.ivLocate.getId());
            params.setMargins(0, m_vScaleDef.getLayoutHeight(14.3), 0, 0);
            childHolder.tvAirLines.setLayoutParams(params);

            //2016-08-03 調整顯示位置
            childHolder.tvCity.setText(m_datas.get(m_items.get(groupPosition).childItems.get(childPosition).index).airport_name);
            childHolder.tvAirLines.setText(m_datas.get(m_items.get(groupPosition).childItems.get(childPosition).index).localization_name
                    + ", " + spilt[0]);

        } else {

            childHolder.ivLocate.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) childHolder.tvAirLines.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_LEFT, childHolder.ivLocate.getId());
            params.setMargins(0, m_vScaleDef.getLayoutHeight(14.3), 0, 0);
            childHolder.tvAirLines.setLayoutParams(params);

            //2016-08-03 調整顯示位置
            childHolder.tvCity.setText(m_datas.get(m_items.get(groupPosition).childItems.get(childPosition).index).airport_name);
            childHolder.tvAirLines.setText(m_datas.get(m_items.get(groupPosition).childItems.get(childPosition).index).localization_name
                    + ", " + m_datas.get(m_items.get(groupPosition).childItems.get(childPosition).index).country);

        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
