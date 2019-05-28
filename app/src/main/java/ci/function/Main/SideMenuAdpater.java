package ci.function.Main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.ui.define.ViewScaleDef;
import ci.function.Main.item.SideMenuItem;

/**
 * 首頁SideMenu的Adapter
 */
public class SideMenuAdpater extends BaseExpandableListAdapter{

    public static class GroupHolder {

        RelativeLayout  rlayout_bg;
        View            vLine;
    }


    public static class ChildHolder {

        RelativeLayout  rlayout_bg;
        ImageView       imgIcon;
        TextView        tvText;
        TextView        tvNum;
    }

    //20190227 高層要的小圖示
    public static class FooterHolder {
        View            vLine;
        TableLayout     rlayout_icon_table;
    }

    private ViewScaleDef m_vScaleDef = null;

    private Context m_context = null;
    private ArrayList<ArrayList<SideMenuItem>> m_arMenuList = null;
    private int iGroupGap = -1;
    private int iTextMargin = -1;
    private int iImgMargin = -1;

    SideMenuAdpater( Context context, ArrayList<ArrayList<SideMenuItem>> arMenuList ){
        this.m_context      = context;
        this.m_arMenuList   = arMenuList;
        m_vScaleDef = ViewScaleDef.getInstance(context);
    }

    public void setGroupGap( int iGap ){
        this.iGroupGap = iGap;
    }

    public void setLeftMargin( int iTextMargin , int iImgMargin){
        this.iTextMargin = iTextMargin;
        this.iImgMargin = iImgMargin;
    }

    @Override
    public int getGroupCount() {

        if ( null == m_arMenuList ){
            return 0;
        } else {
            return  m_arMenuList.size();
        }
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        if ( null == m_arMenuList ){
            return 0;
        } else if ( null == m_arMenuList.get(groupPosition) ) {
            return  0;
        } else {
            return m_arMenuList.get(groupPosition).size();
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return m_arMenuList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return m_arMenuList.get(groupPosition).get(childPosition);
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
            convertView = LayoutInflater.from(m_context).inflate( R.layout.layout_sidemenu_group_view, null);
            groupHolder.rlayout_bg  = (RelativeLayout)convertView.findViewById(R.id.rlayout_bg);
            groupHolder.vLine       = (View)convertView.findViewById(R.id.vline);

            if ( iGroupGap != -1 ){
                groupHolder.rlayout_bg.getLayoutParams().height = iGroupGap;
            }

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)groupHolder.vLine.getLayoutParams();
            params.height = m_vScaleDef.getLayoutHeight(1.4);

            convertView.setTag(groupHolder);

        } else {

            groupHolder = (GroupHolder)convertView.getTag();
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        SideMenuItem sideMenuItem = m_arMenuList.get(groupPosition).get(childPosition);
        ChildHolder childHolder = null;
        if ( null == convertView ){
            childHolder = new ChildHolder();
            convertView = LayoutInflater.from(m_context).inflate( R.layout.layout_sidemenu_child_view, null);
            childHolder.rlayout_bg  = (RelativeLayout)convertView.findViewById(R.id.rlayout_bg);
            childHolder.imgIcon     = (ImageView)convertView.findViewById(R.id.img_icon);
            childHolder.tvText      = (TextView)convertView.findViewById(R.id.tvText);
            childHolder.tvNum       = (TextView)convertView.findViewById(R.id.tvNum);

            childHolder.rlayout_bg.getLayoutParams().height = m_vScaleDef.getLayoutHeight(50);

            //icon
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)childHolder.imgIcon.getLayoutParams();
            params.height = m_vScaleDef.getLayoutMinUnit(22);
            params.width  = m_vScaleDef.getLayoutMinUnit(22);
            if ( iImgMargin != -1 ){
                //Right Menu
                params.leftMargin = iImgMargin;
            }else {
                //Left Menu
                params.leftMargin = m_vScaleDef.getLayoutWidth(18);
            }

            //文字
            params = (RelativeLayout.LayoutParams)childHolder.tvText.getLayoutParams();
            if ( iTextMargin != -1 ){
                //Right Menu
                params.leftMargin = iTextMargin;
            }else {
                //Left Menu
                params.leftMargin = m_vScaleDef.getLayoutWidth(17);
            }

            m_vScaleDef.setTextSize(16, childHolder.tvText);

            //通知數字
            int iHeight = m_vScaleDef.getLayoutWidth(20);
            params = (RelativeLayout.LayoutParams)childHolder.tvNum.getLayoutParams();
            params.leftMargin = m_vScaleDef.getLayoutWidth(8.3);
            params.height = iHeight;
            int iPadding = m_vScaleDef.getLayoutMinUnit(3.7);
//            childHolder.tvNum.setMinHeight(iHeight);
            childHolder.tvNum.setMinWidth(iHeight);
            childHolder.tvNum.setPadding(iPadding, 0, iPadding, 0);
            m_vScaleDef.setTextSize(14, childHolder.tvNum);

            childHolder.rlayout_bg.setBackgroundResource(R.drawable.bg_transparent_press_black20);
            //convertView.setBackgroundResource(R.drawable.bg_transparent_press_black20);
            convertView.setTag(childHolder);

        } else {
            childHolder = (ChildHolder)convertView.getTag();
        }

        if ( sideMenuItem.bSelect ){
            //childHolder.rlayout_bg.setBackgroundResource(R.color.black_20);
            //設定icon
            childHolder.imgIcon.setImageResource(sideMenuItem.iDrawableId);
            childHolder.rlayout_bg.setSelected(true);
        } else {
            //childHolder.rlayout_bg.setBackgroundResource( R.color.transparent );
            //設定icon
            childHolder.imgIcon.setImageResource( sideMenuItem.iDrawableId_n );
            childHolder.rlayout_bg.setSelected(false);
        }

        //設定顯示文字
        childHolder.tvText.setText( sideMenuItem.iNameResId );
        //檢核數字的邏輯
        if ( sideMenuItem.bShowNum ){
            childHolder.tvNum.setVisibility(View.VISIBLE);
            if ( sideMenuItem.iNum > 99 ) {
                childHolder.tvNum.setBackgroundResource(R.drawable.ic_number_circle_3);
                childHolder.tvNum.setText(String.valueOf(sideMenuItem.iNum));
            } else if ( sideMenuItem.iNum > 9 ){
                childHolder.tvNum.setBackgroundResource(R.drawable.ic_number_circle_2);
                childHolder.tvNum.setText(String.valueOf(sideMenuItem.iNum));
            } else if ( sideMenuItem.iNum > 0 ){
                childHolder.tvNum.setBackgroundResource(R.drawable.ic_number_circle_1);
                childHolder.tvNum.setText(String.valueOf(sideMenuItem.iNum));
            } else {
                childHolder.tvNum.setVisibility(View.GONE);
            }
        } else {
            childHolder.tvNum.setVisibility(View.GONE);
        }


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
