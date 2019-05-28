package ci.function.SeatSelection;

import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import ci.function.Core.SLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Main.BaseActivity;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.NavigationBar;

/**
 * Created by flowmahuang on 2016/4/7.
 */
public class CISelectSeatResultActivity extends BaseActivity implements View.OnClickListener {
    private NavigationBar m_Navigationbar = null;
    private Button m_ConfirmButton = null;
    private ListView m_ListView = null;
    private Intent getIntent = null;
    private View shadow = null;

    private ViewScaleDef m_vScaleDef = null;
    private LayoutInflater inflater;
    private BaseAdapter baseAdapter;

    public int itemCount = 0;
    public boolean returnOrNot;
    private String[] PassengerSeat = null;
    private String[] PassengerSeatReturn = null;
    private String[] PassengerName = null;
    private String DepartureStation = null;
    private String ArrvialStation = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_select_seat_result;
    }

    @Override
    protected void initialLayoutComponent() {
        m_vScaleDef = ViewScaleDef.getInstance(m_Context);

        inflater = (LayoutInflater) m_Context.getSystemService(m_Context.LAYOUT_INFLATER_SERVICE);
        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_ConfirmButton = (Button) findViewById(R.id.btn_confirm);
        m_ListView = (ListView) findViewById(R.id.lv_select_seat_card);
        shadow = findViewById(R.id.vGradient);

        getIntent = this.getIntent();
        itemCount = getIntent.getIntExtra("PassengerNum", 0);
        returnOrNot = getIntent.getBooleanExtra("ReturnOrNot", false);
        PassengerName = getIntent.getStringArrayExtra("PassengerName");
        PassengerSeat = getIntent.getStringArrayExtra("Seat");
        DepartureStation = getIntent.getStringExtra("Departure");
        ArrvialStation = getIntent.getStringExtra("Arrvial");

//        if (returnOrNot)
//            PassengerSeatReturn = getIntent.getStringArrayExtra("SeatReturn");

        setResult(RESULT_OK, getIntent);

        m_ListView.setVerticalScrollBarEnabled(false);
    }

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_Context.getString(R.string.seat_selection_result);
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
        CISelectSeatResultActivity.this.finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
        m_ListView.setAdapter(baseAdapter = baseAdapter());
        m_ListView.setOnScrollListener(scrollListener());
        m_ConfirmButton.setOnClickListener(this);

        ViewTreeObserver vto = m_ListView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                m_ListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int iCnt = m_ListView.getChildCount();

                if (iCnt > 0) {
                    int totalHeight = 0;

                    for (int i = 0, len = baseAdapter.getCount(); i < len; i++) {
                        View listItem = baseAdapter.getView(i, null, m_ListView);
                        //避免 listItem 為 null 時閃退 by Kevin 2016/12
                        try{
                            listItem.measure(0, 0); // 计算子项View 的宽高
                            int list_child_item_height = listItem.getMeasuredHeight()+m_ListView.getDividerHeight();
                           SLog.w("ListView Child Height", "child height="+list_child_item_height);
                            totalHeight += list_child_item_height; // 统计所有子项的总高度
                        }catch (Exception e){}
                    }
                   SLog.w("ListView", "height="+m_ListView.getHeight());
                   SLog.w("ListView", "item total height="+totalHeight);

                    //內容高度 <= ScrollView高度, 沒有可滾動區域, 無法監聽onScrollChanged(), 需隱藏陰影
                    if ( totalHeight <= m_ListView.getHeight() ) {
//                        m_vShadowTop.setAlpha(0);
//                        m_vShadowBottom.setAlpha(0);
                        shadow.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {}

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    protected void onLanguageChangeUpdateUI() {}

    @Override
    public void onClick(View v) {
        if (v.getId() == m_ConfirmButton.getId()) {
            finish();
            //測試切換activity滑入滑出動畫
            overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
        }
    }

    public BaseAdapter baseAdapter() {
        return new BaseAdapter() {
            @Override
            public int getCount() {
                if (returnOrNot)
                    return itemCount * 2 + 3;
                else
                    return itemCount + 2;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                GroupHolder groupHolder;
                GroupHolder2 groupHolder2;

                if (position == 0) {

                    groupHolder2 = new GroupHolder2();

                    convertView = inflater.inflate(R.layout.layout_select_meal_card_list_top_item, null);

                    groupHolder2.icon = (ImageView) convertView.findViewById(R.id.img_done);
                    groupHolder2.completeTitle = (TextView) convertView.findViewById(R.id.tv_select_done);
                    groupHolder2.completeTitle.setText(getString(R.string.your_seat_selection_is_completed));

                    m_vScaleDef.setMargins(groupHolder2.icon, 0, 20, 0, 0);
                    m_vScaleDef.selfAdjustSameScaleView(groupHolder2.icon, 48, 48);

                    m_vScaleDef.setTextSize(20, groupHolder2.completeTitle);
                    m_vScaleDef.setMargins(groupHolder2.completeTitle, 0, 30, 0, 30);

                } else if (returnOrNot) {
                    if (position == 1 || position == 2 + itemCount) {

                        groupHolder = new GroupHolder();

                        convertView = inflater.inflate(R.layout.layout_select_seat_result_card_item, null);

                        groupHolder.SelectSeatBody = (RelativeLayout) convertView.findViewById(R.id.rlayout_select_flight_body);
                        groupHolder.SelectSeatBody.setVisibility(View.GONE);

                        groupHolder.SelectSeatHead = (RelativeLayout) convertView.findViewById(R.id.rlayout_select_flight_head);
                        groupHolder.selectFlightFrom = (TextView) convertView.findViewById(R.id.tv_airport_from);
                        groupHolder.selectFlightTo = (TextView) convertView.findViewById(R.id.tv_airport_to);
                        groupHolder.selectFlightIcon = (ImageView) convertView.findViewById(R.id.title_flight_image);
                        groupHolder.lineView = convertView.findViewById(R.id.vline);

                        groupHolder.selectFlightFrom.setText("TPE");
                        groupHolder.selectFlightTo.setText("FUK");

                        groupHolder.lineView.setVisibility(View.GONE);

                        m_vScaleDef.selfAdjustAllView(groupHolder.SelectSeatHead);
                        m_vScaleDef.selfAdjustSameScaleView(groupHolder.selectFlightIcon, 21.3, 21.3);

                        if (position == 2 + itemCount) {
                            m_vScaleDef.setMargins(groupHolder.SelectSeatHead, 0, 10, 0, 0);
                        }
                    } else {
                        groupHolder = new GroupHolder();

                        convertView = inflater.inflate(R.layout.layout_select_seat_result_card_item, null);

                        groupHolder.SelectSeatHead = (RelativeLayout) convertView.findViewById(R.id.rlayout_select_flight_head);
                        groupHolder.SelectSeatHead.setVisibility(View.GONE);

                        groupHolder.whoSelectFlight = (TextView) convertView.findViewById(R.id.tv_select_flight_name);
                        groupHolder.selectFlightSeatNumber = (TextView) convertView.findViewById(R.id.tv_select_seat_number);
                        groupHolder.SelectSeatBody = (RelativeLayout) convertView.findViewById(R.id.rlayout_select_flight_body);
                        groupHolder.lineView = convertView.findViewById(R.id.vline);

                        if (itemCount + 2 > position && position > 1) {
                            groupHolder.whoSelectFlight.setText(PassengerName[position - 2]);
                            groupHolder.selectFlightSeatNumber.setText(PassengerSeat[position - 2]);
                        }
                        if (position > itemCount + 2) {
                            groupHolder.whoSelectFlight.setText(PassengerName[position - itemCount - 3]);
                            groupHolder.selectFlightSeatNumber.setText(PassengerSeatReturn[position - itemCount - 3]);
                        }

                        groupHolder.lineView.setVisibility(View.VISIBLE);

                        if (position == itemCount + 1 || position == itemCount * 2 + 2) {
                            if (Build.VERSION.SDK_INT > 21)
                                groupHolder.SelectSeatBody.setBackground(getResources().getDrawable(R.drawable.bg_boader_white_radius_body, null));
                            else
                                groupHolder.SelectSeatBody.setBackground(getResources().getDrawable(R.drawable.bg_boader_white_radius_body));
                        }

                        m_vScaleDef.selfAdjustAllView(groupHolder.SelectSeatBody);
                    }
                } else if (!returnOrNot) {
                    if (position == 1) {

                        groupHolder = new GroupHolder();

                        convertView = inflater.inflate(R.layout.layout_select_seat_result_card_item, null);

                        groupHolder.SelectSeatBody = (RelativeLayout) convertView.findViewById(R.id.rlayout_select_flight_body);
                        groupHolder.SelectSeatBody.setVisibility(View.GONE);

                        groupHolder.SelectSeatHead = (RelativeLayout) convertView.findViewById(R.id.rlayout_select_flight_head);
                        groupHolder.selectFlightFrom = (TextView) convertView.findViewById(R.id.tv_airport_from);
                        groupHolder.selectFlightTo = (TextView) convertView.findViewById(R.id.tv_airport_to);
                        groupHolder.selectFlightIcon = (ImageView) convertView.findViewById(R.id.title_flight_image);
                        groupHolder.lineView = convertView.findViewById(R.id.vline);

                        groupHolder.selectFlightFrom.setText(DepartureStation);
                        groupHolder.selectFlightTo.setText(ArrvialStation);

                        groupHolder.lineView.setVisibility(View.GONE);

                        m_vScaleDef.selfAdjustAllView(groupHolder.SelectSeatHead);
                        m_vScaleDef.selfAdjustSameScaleView(groupHolder.selectFlightIcon, 21.3, 21.3);
                    } else {
                        groupHolder = new GroupHolder();

                        convertView = inflater.inflate(R.layout.layout_select_seat_result_card_item, null);

                        groupHolder.SelectSeatHead = (RelativeLayout) convertView.findViewById(R.id.rlayout_select_flight_head);
                        groupHolder.SelectSeatHead.setVisibility(View.GONE);

                        groupHolder.whoSelectFlight = (TextView) convertView.findViewById(R.id.tv_select_flight_name);
                        groupHolder.selectFlightSeatNumber = (TextView) convertView.findViewById(R.id.tv_select_seat_number);
                        groupHolder.SelectSeatBody = (RelativeLayout) convertView.findViewById(R.id.rlayout_select_flight_body);
                        groupHolder.lineView = convertView.findViewById(R.id.vline);


                        if (itemCount + 2 > position && position > 1) {
                            groupHolder.whoSelectFlight.setText(PassengerName[position - 2]);
                            if (PassengerSeat[position - 2].equals(""))
                                groupHolder.selectFlightSeatNumber.setText("-");
                            else
                                groupHolder.selectFlightSeatNumber.setText(PassengerSeat[position - 2]);
                        }
                        groupHolder.lineView.setVisibility(View.VISIBLE);

                        if (position == itemCount + 1) {
                            groupHolder.lineView.setVisibility(View.GONE);
                            if (Build.VERSION.SDK_INT > 21) {
                                groupHolder.SelectSeatBody.setBackground(
                                        getResources().getDrawable(R.drawable.bg_boader_white_radius_body, null));
                            } else {
                                groupHolder.SelectSeatBody.setBackground(
                                        getResources().getDrawable(R.drawable.bg_boader_white_radius_body));
                            }
                        }

                        m_vScaleDef.selfAdjustAllView(groupHolder.SelectSeatBody);
                    }
                }
                return convertView;
            }
        };
    }

    public static class GroupHolder {
        TextView selectFlightFrom,
                selectFlightTo,
                whoSelectFlight,
                selectFlightSeatNumber;
        ImageView selectFlightIcon;
        View lineView;
        RelativeLayout SelectSeatHead,
                SelectSeatBody;
    }

    public static class GroupHolder2 {
        ImageView icon;
        TextView completeTitle;
    }

    private AbsListView.OnScrollListener scrollListener() {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastItem = firstVisibleItem + visibleItemCount;
//                if (!returnOrNot){
//                    shadow.setVisibility(View.GONE);
//                }
//                else

                if (itemCount != 0) {
                    if (totalItemCount == visibleItemCount) {
                        if (m_ListView.getChildAt((m_ListView.getChildCount() - 1)).getBottom() <= m_ListView.getHeight()) {
                            shadow.setVisibility(View.GONE);
                        } else {
                            shadow.setVisibility(View.VISIBLE);
                        }

                    } else if (lastItem == totalItemCount) {
                        if (m_ListView.getChildAt((m_ListView.getChildCount() - 1)).getBottom() <= m_ListView.getHeight()) {
                            shadow.setVisibility(View.GONE);
                        } else{
                            shadow.setVisibility(View.VISIBLE);
                        }
                    } else{
                        shadow.setVisibility(View.VISIBLE);
                    }
                }
            }
        };
    }
}
