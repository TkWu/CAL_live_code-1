package ci.function.MealSelection;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.Main.BaseActivity;
import ci.function.MealSelection.item.CIMealResultEntity;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CIMealInfoEntity;

/**
 * Created by flowmahuang on 2016/3/8.
 * Modifly by Ryan at 2016-09-29
 */
public class CISelectMealResultActivity extends BaseActivity implements View.OnClickListener {

    public final static String BUNDLE_DEPARTURE_STATION = "BUNDLE_DEPARTURE_STATION";
    public final static String BUNDLE_ARRIVAL_STATION   = "BUNDLE_ARRIVAL_STATION";
    public final static String BUNDLE_MEAL_RESULT       = "BUNDLE_MEAL_RESULT";

    public int              m_iItemCount    = 0;
    private ViewScaleDef    m_vScaleDef     = null;
    private NavigationBar   m_Navigationbar = null;
    private Button          m_btnBackHome   = null;
    private LayoutInflater  m_inflater;
    private ListView        m_listview  = null;
    private Intent          m_Intent    = null;
    private View            m_vShadow   = null;

    private String m_strDepatureAirport;
    private String m_strArrivalAirport;

    private ArrayList<CIMealResultEntity> m_arPassenagerMealResultList = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_select_meal_result;
    }

    @Override
    protected void initialLayoutComponent() {
        m_inflater = (LayoutInflater) m_Context.getSystemService(m_Context.LAYOUT_INFLATER_SERVICE);
        m_vScaleDef = ViewScaleDef.getInstance(m_Context);

        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_btnBackHome = (Button) findViewById(R.id.btn_back_home);
        m_listview = (ListView) findViewById(R.id.lv_meal_card);
        m_vShadow = findViewById(R.id.vGradient);

        m_Intent = this.getIntent();
        //
        m_strDepatureAirport= m_Intent.getStringExtra(BUNDLE_DEPARTURE_STATION);
        m_strArrivalAirport = m_Intent.getStringExtra(BUNDLE_ARRIVAL_STATION);
        m_arPassenagerMealResultList = (ArrayList<CIMealResultEntity>) m_Intent.getSerializableExtra(CISelectMealResultActivity.BUNDLE_MEAL_RESULT);
        //
        m_listview.setDividerHeight(m_vScaleDef.getLayoutHeight(8));
        m_listview.setVerticalScrollBarEnabled(false);

        if ( null == m_arPassenagerMealResultList || m_arPassenagerMealResultList.size() <= 0 ) {
            m_vShadow.setVisibility(View.GONE);
        } else {
            m_iItemCount = m_arPassenagerMealResultList.size();
        }
    }

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_Context.getString(R.string.select_meal_flight, m_strDepatureAirport, m_strArrivalAirport);
        }
    };

    private NavigationBar.onNavigationbarListener m_onNavigationbarListener = new NavigationBar.onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {}
        @Override
        public void onLeftMenuClick() {}
        @Override
        public void onBackClick() {
            onBackPressed();
        }
        @Override
        public void onDeleteClick() {}
        @Override
        public void onDemoModeClick() {}
    };

    public void onBackPressed() {
        //CISelectMealResultActivity.this.finish();
        //overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
        Finish();
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
        m_btnBackHome.setOnClickListener(this);
        m_listview.setAdapter(baseAdapter());
        m_listview.setOnScrollListener(scrollListener());

        View view = LayoutInflater.from(this).inflate(R.layout.layout_select_meal_card_list_top_item, null);

        ImageView icon = (ImageView) view.findViewById(R.id.img_done);
        TextView tvMealCardName = (TextView) view.findViewById(R.id.tv_select_done);
        m_vScaleDef.setMargins(icon, 0, 20, 0, 0);
        icon.getLayoutParams().height = m_vScaleDef.getLayoutHeight(48);
        icon.getLayoutParams().width = m_vScaleDef.getLayoutHeight(48);
        m_vScaleDef.setTextSize(20, tvMealCardName);
        m_vScaleDef.setMargins(tvMealCardName, 0, 20, 0, 30);

        m_listview.addHeaderView(view);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {

    }

    @Override
    protected void onLanguageChangeUpdateUI() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == m_btnBackHome.getId()) {
            Finish();
        }
    }

    public void Finish() {

        this.setResult(Activity.RESULT_OK);
        this.finish();

        //測試切換activity滑入滑出動畫
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    public BaseAdapter baseAdapter() {
        return new BaseAdapter() {
            @Override
            public int getCount() {
                //return m_iItemCount + 1;
                if ( null == m_arPassenagerMealResultList ){
                    return 0;
                }
                return m_arPassenagerMealResultList.size();
            }

            @Override
            public Object getItem(int position) {

                if ( null == m_arPassenagerMealResultList ){
                    return null;
                }
                return m_arPassenagerMealResultList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                CIMealResultEntity Passenager = m_arPassenagerMealResultList.get(position);
                int iMealCnt = (null == Passenager.arMealInfoList)? 0:Passenager.arMealInfoList.size();
                GroupHolder groupHolder = null;
                if ( null == convertView ){
                    groupHolder = new GroupHolder();

                    convertView = m_inflater.inflate(R.layout.layout_select_meal_selection_card_item, null);

                    groupHolder.llayout_meal_card_head = (LinearLayout) convertView.findViewById(R.id.llayout_meal_card_head);
                    groupHolder.mealCardName = (TextView) convertView.findViewById(R.id.tv_meal_card_name);
                    groupHolder.llayout_body = (LinearLayout) convertView.findViewById(R.id.llayout_meal_card_body);

                    m_vScaleDef.selfAdjustAllView(groupHolder.llayout_meal_card_head);


                    for ( int iIdx = 0; iIdx < iMealCnt; iIdx++ ){

                        View vBodyView = LayoutInflater.from(m_Context).inflate(R.layout.layout_select_meal_result_detail_info_item, null);

                        Holderitem holderitem = new Holderitem();

                        LinearLayout layout = (LinearLayout)vBodyView.findViewById(R.id.llayout_meal_card_layout);
                        holderitem.tvMealDesc = (TextView) vBodyView.findViewById(R.id.tv_meal_card_desc);
                        holderitem.tvMealName = (TextView) vBodyView.findViewById(R.id.tv_meal_card_name);
                        holderitem.vDiv = vBodyView.findViewById(R.id.vline);

                        layout.getLayoutParams().height = m_vScaleDef.getLayoutHeight(60);
                        m_vScaleDef.setPadding(layout, 20,12,20,12);

                        holderitem.tvMealDesc.getLayoutParams().height = m_vScaleDef.getLayoutHeight(15.7);
                        m_vScaleDef.setTextSize( 13, holderitem.tvMealDesc);

                        holderitem.tvMealName.getLayoutParams().height = m_vScaleDef.getLayoutHeight(19.3);
                        ((LinearLayout.LayoutParams)holderitem.tvMealName.getLayoutParams()).topMargin = m_vScaleDef.getLayoutHeight(1);
                        m_vScaleDef.setTextSize( 16, holderitem.tvMealName );

                        holderitem.vDiv.getLayoutParams().height = m_vScaleDef.getLayoutHeight(1);
                        ((LinearLayout.LayoutParams)holderitem.vDiv.getLayoutParams()).leftMargin = m_vScaleDef.getLayoutHeight(20);
                        ((LinearLayout.LayoutParams)holderitem.vDiv.getLayoutParams()).rightMargin = m_vScaleDef.getLayoutHeight(20);

                        if ( iIdx == iMealCnt-1 ){
                            holderitem.vDiv.setVisibility(View.GONE);
                        } else {
                            holderitem.vDiv.setVisibility(View.VISIBLE);
                        }

                        groupHolder.llayout_body.addView(vBodyView);
                        groupHolder.arbody.add(holderitem);
                    }
                    convertView.setTag(groupHolder);
                } else {
                    groupHolder =  (GroupHolder)convertView.getTag();
                }

                groupHolder.mealCardName.setText(Passenager.strName);

                for ( int iIdx = 0; iIdx < iMealCnt; iIdx++ ){

                    Holderitem holderitem = groupHolder.arbody.get(iIdx);
                    CIMealInfoEntity mealInfoEntity = Passenager.arMealInfoList.get(iIdx);

                    holderitem.tvMealDesc.setText( mealInfoEntity.mealtype_desc );
                    holderitem.tvMealName.setText( mealInfoEntity.meal_name );

                    if ( TextUtils.equals(getString(R.string.unselected), mealInfoEntity.meal_name ) ){
                        if (Build.VERSION.SDK_INT > 22)
                            holderitem.tvMealName.setTextColor(getResources().getColor(R.color.grey_four, null));
                        else
                            holderitem.tvMealName.setTextColor(getResources().getColor(R.color.grey_four));
                    } else {
                        if (Build.VERSION.SDK_INT > 22)
                            holderitem.tvMealName.setTextColor(getResources().getColor(R.color.black_one, null));
                        else
                            holderitem.tvMealName.setTextColor(getResources().getColor(R.color.black_one));
                    }
                }

                return convertView;
            }
        };
    }

    public class GroupHolder {
        LinearLayout    llayout_meal_card_head;
        TextView        mealCardName;
        LinearLayout    llayout_body;

        ArrayList<Holderitem> arbody;

        public GroupHolder (){
            arbody = new ArrayList<>();
        }
    }

    public class Holderitem {
        TextView    tvMealName;
        TextView    tvMealDesc;
        View        vDiv;
    }

    private AbsListView.OnScrollListener scrollListener() {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int lastItem = firstVisibleItem + visibleItemCount;
                if (totalItemCount == visibleItemCount) {
                    m_vShadow.setVisibility(View.GONE);
                } else if (lastItem == totalItemCount) {
                    if (m_listview.getChildAt((m_listview.getChildCount() - 1)).getBottom() == m_listview.getHeight()) {
                        m_vShadow.setVisibility(View.GONE);
                    } else {
                        m_vShadow.setVisibility(View.VISIBLE);
                    }
                } else {
                    m_vShadow.setVisibility(View.VISIBLE);
                }
            }
        };
    }
}
