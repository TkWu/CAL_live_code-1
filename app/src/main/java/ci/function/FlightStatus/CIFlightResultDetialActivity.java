package ci.function.FlightStatus;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.chinaairlines.mobile30.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ci.function.Core.CIApplication;
import ci.function.Main.BaseActivity;
import ci.ui.CAL_Map.FlightLocationManager;
import ci.ui.CAL_Map.MapFragment;
import ci.ui.TimeTable.CITimeTableFlightStatusDetailList;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;
import ci.ui.object.CIFlightTrackInfo;
import ci.ui.view.ImageHandle;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CIFlightStatus_infoEntity;
import ci.ws.Models.entities.CITimeTable_InfoEntity;
import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.cores.object.CIDisplayDateTimeInfo;
import ci.ws.cores.object.CIWSCommon;

import static ci.ui.object.CIFlightTrackInfo.SHAREPRE_TAG_TRACKDATA;
import static ci.ui.object.CIFlightTrackInfo.SHAREPRE_TAG_TRACKDATA_0;
import static ci.ui.object.CIFlightTrackInfo.SHAREPRE_TAG_TRACKDATA_1;
import static ci.ui.object.CIFlightTrackInfo.SHAREPRE_TAG_TRACKDATA_2;

/**
 * Created by flowmahuang on 2016/3/15.
 */
//備註：FlightStatus & TimeTable 共用航班搜尋結果頁面
public class CIFlightResultDetialActivity extends BaseActivity {

    /**
     * BUNDLE_PARA_VIEW_TYPE    進入點  TimeTable 還是 Flight status
     * BUNDLE_PARA_ITINERARY_DATA  帶入的資料
     * BUNDLE_PARA_DATA_POSITION  選擇第幾個
     */
    public static final String BUNDLE_PARA_VIEW_TYPE        = "view_type";
    public static final String BUNDLE_PARA_ITINERARY_DATA   = "data";
    public static final String BUNDLE_PARA_DATA_POSITION    = "position";

    public static final int VIEW_FROM_TIME_TABLE            = CITimeTableFlightStatusDetailList.DEF_VIEW_TYPE_TIME_TABLE;
    public static final int VIEW_FROM_FLIGHT_STATUS         = CITimeTableFlightStatusDetailList.DEF_VIEW_TYPE_FIGHT_STATUS;

    private NavigationBar                       m_Navigationbar = null;
    private Intent                              m_getIntent     = null;
    private FrameLayout                         m_content       = null;
    private CITimeTableFlightStatusDetailList   m_DetailListView= null;
    private View                                m_vShadowView   = null;
    private Button                              m_btnTrack      = null;

    private int                                 m_iViewType;
    private FlightLocationManager               m_manager = null;

    //----接入華航提供map所需要的物件 by kevin 2016/5/30
    private CITripListResp_Itinerary m_dataForMap = null;

    private int m_iMapW;
    private int m_iMapH;
    
    private CIFlightTrackInfo info;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_flight_result_detail;
    }

    @Override
    protected void onResume() {
        //last edited by kevin 2016/5/30
        if (null != m_dataForMap) {
            m_manager.executeTask(m_dataForMap);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        //last edited by kevin 2016/5/30
        m_manager.cancleTask();
        super.onPause();
    }

    public void changeMapFragment(String result, boolean isfull) {

        //2016-11-03 Ryan, 加上google play service 服務檢查, 針對大陸手機沒有安裝GoogleplayService
        FrameLayout map_layout = (FrameLayout) findViewById(R.id.fl_map);
        //不顯示Mapview
        if ( false == AppInfo.getInstance(CIApplication.getContext()).isGooglePlayServicesAvailable() ){
            map_layout.setVisibility(View.GONE);
            return;
        }
        map_layout.setVisibility(View.VISIBLE);
        //


        //FrameLayout map_layout = (FrameLayout) findViewById(R.id.fl_map);
        if (isfull) {
            map_layout.getLayoutParams().height = m_iMapH;
            map_layout.getLayoutParams().width = m_iMapW;
            map_layout.requestLayout();
        } else {
            map_layout.getLayoutParams().height = m_iMapH / 2;
            map_layout.getLayoutParams().width = m_iMapW;
            map_layout.requestLayout();

        }
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.fl_map, MapFragment.newInstance(result));
        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void initialLayoutComponent() {

        info = new CIFlightTrackInfo(this);
        //初始化華航map物件 by kevin 2016/5/30
        m_dataForMap = new CITripListResp_Itinerary();
        m_manager = new FlightLocationManager(new FlightLocationManager.Callback() {
            @Override
            public void onDataBinded(String result) {
                changeMapFragment(result, false);
            }
        });

        //2017.1.4 CR - flight status／time table 統一使用trip detail 的地圖高度
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        m_iMapW = displayMetrics.widthPixels;
        m_iMapH = displayMetrics.heightPixels;
        //預設地圖img 寬高與map一致
        ImageView imgMap = (ImageView) findViewById(R.id.img_bg_map);
        imgMap.setMinimumHeight(m_iMapH / 2);
        imgMap.setMaxHeight(m_iMapH / 2);
        imgMap.setMinimumWidth(m_iMapW);
        imgMap.setMaxWidth(m_iMapW);

        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_content = (FrameLayout) findViewById(R.id.fl_content);
        m_btnTrack = (Button) findViewById(R.id.track);
        m_vShadowView = findViewById(R.id.vGradient);

        m_getIntent = this.getIntent();
        int position = m_getIntent.getIntExtra(BUNDLE_PARA_DATA_POSITION, 0);
        m_DetailListView = new CITimeTableFlightStatusDetailList(this, null, position);
        m_iViewType = m_getIntent.getIntExtra(BUNDLE_PARA_VIEW_TYPE, VIEW_FROM_FLIGHT_STATUS);
        if (m_iViewType == VIEW_FROM_FLIGHT_STATUS) {
            Gson gson = new Gson();
            Type dataType = new TypeToken<ArrayList<CIFlightStatus_infoEntity>>() {
            }.getType();
            String jsonData = m_getIntent.getStringExtra(BUNDLE_PARA_ITINERARY_DATA);
            ArrayList<CIFlightStatus_infoEntity> respData = gson.fromJson(jsonData, dataType);

            //因應多語系轉換問題, 字串需重新再設定一次 - by Ling 2016.08.02
            //轉換顯示時間
            CIDisplayDateTimeInfo ciDisDeparture = CIWSCommon.ConvertDisplayDateTime(
                    respData.get(position).stdd, respData.get(position).stdt, respData.get(position).etdd,
                    respData.get(position).etdt, respData.get(position).atdd, respData.get(position).atdt);
            respData.get(position).strDisDepartureDate = ciDisDeparture.strDisplayDate;
            respData.get(position).strDisDepartureTime = ciDisDeparture.strDisplayTime;
            respData.get(position).strDisDepartureName = ciDisDeparture.strDisplayTagName;

            CIDisplayDateTimeInfo ciDisArrival = CIWSCommon.ConvertDisplayDateTime(
                    respData.get(position).stad, respData.get(position).stat, respData.get(position).etad,
                    respData.get(position).etat, respData.get(position).atad, respData.get(position).atat);
            respData.get(position).strDisArrivalDate = ciDisArrival.strDisplayDate;
            respData.get(position).strDisArrivalTime = ciDisArrival.strDisplayTime;
            respData.get(position).strDisArrivalName = ciDisArrival.strDisplayTagName;

            m_DetailListView.setFlightStatusData(respData);
            //初始化map資料 by kevin 2016/5/30
            if (null != respData && respData.size() > 0) {
                m_dataForMap.Departure_Station = respData.get(position).depature_station_code;
                m_dataForMap.Arrival_Station = respData.get(position).arrival_station_code;
                m_dataForMap.Departure_Date = respData.get(position).stdd;
                m_dataForMap.Departure_Time = respData.get(position).stdt;
                m_dataForMap.Airlines = respData.get(position).carrier;
                m_dataForMap.Flight_Number = respData.get(position).flight_number;
            }
        } else if (m_iViewType == VIEW_FROM_TIME_TABLE) {
            Gson gson = new Gson();
            Type dataType = new TypeToken<ArrayList<CITimeTable_InfoEntity>>() {
            }.getType();
            String jsonData = m_getIntent.getStringExtra(BUNDLE_PARA_ITINERARY_DATA);
            ArrayList<CITimeTable_InfoEntity> respData = gson.fromJson(jsonData, dataType);
            m_DetailListView.setTimeTableData(respData);
            //初始化map資料 by kevin 2016/5/30
            if (null != respData && respData.size() > 0) {
                m_dataForMap.Departure_Station = respData.get(position).departure_air_port;
                m_dataForMap.Arrival_Station = respData.get(position).arrival_air_port;
                m_dataForMap.Departure_Date = respData.get(position).departure_date;
                m_dataForMap.Departure_Time = respData.get(position).departure_time;
                m_dataForMap.Airlines = respData.get(position).company;
                m_dataForMap.Flight_Number = respData.get(position).flight_number;
            }
        }


        m_DetailListView.setViewType(m_iViewType);
        m_content.addView(m_DetailListView);
        if (m_iViewType == VIEW_FROM_FLIGHT_STATUS) {
            m_btnTrack.setVisibility(View.VISIBLE);
            if (isContain()) {
                m_btnTrack.setBackgroundResource(R.drawable.bg_tracked_btn);
                m_btnTrack.setTextColor(getResources().getColor(R.color.french_blue));
                m_btnTrack.setText(R.string.tracked);
            } else {
                m_btnTrack.setBackgroundResource(R.drawable.bg_track_btn);
                m_btnTrack.setText(R.string.track);
            }
        } else {
            m_btnTrack.setVisibility(View.GONE);
        }

    }

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            String title = "";
            if (m_iViewType == VIEW_FROM_TIME_TABLE)
                title = m_Context.getString(R.string.timetable_title);
            else if (m_iViewType == VIEW_FROM_FLIGHT_STATUS)
                title = m_Context.getString(R.string.flight_status_flight_result);
            return title;
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
        CIFlightResultDetialActivity.this.finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
    }

    @Override
    protected void setOnParameterAndListener() {
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
        m_btnTrack.setOnClickListener(trackClick());
        m_btnTrack.setOnTouchListener(trackTouch());

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

//    private AbsListView.OnScrollListener scrollListener() {
//        return new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {}
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                int lastItem = firstVisibleItem + visibleItemCount;
//                if (totalItemCount == visibleItemCount) {
//                    if (m_DetailListView.getChildAt((m_DetailListView.getChildCount() - 1)) != null) {
//                        if (m_DetailListView.getChildAt((m_DetailListView.getChildCount() - 1)).getBottom() == m_content.getHeight())
//                            m_vShadowView.setVisibility(View.GONE);
//                        else
//                            m_vShadowView.setVisibility(View.VISIBLE);
//                    }
//                } else if (lastItem == totalItemCount) {
//                    if (m_DetailListView.getChildAt((m_DetailListView.getChildCount() - 1)).getBottom() == m_content.getHeight()) {
//                        m_vShadowView.setVisibility(View.GONE);
//                    } else
//                        m_vShadowView.setVisibility(View.VISIBLE);
//                } else
//                    m_vShadowView.setVisibility(View.VISIBLE);
//
//            }
//        };
//    }

    private View.OnClickListener trackClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Type dataType = new TypeToken<ArrayList<CIFlightStatus_infoEntity>>() {
                }.getType();
                ArrayList<CIFlightStatus_infoEntity> dataArray = gson.fromJson(m_DetailListView.getTrackData(), dataType);

                //將目前的畫面截圖下來, 當作背景
                Bitmap bitmap = ImageHandle.getScreenShot((Activity) m_Context);
                Bitmap blur = ImageHandle.BlurBuilder(m_Context, bitmap, 13.5f, 0.15f);

                Bundle bundle = new Bundle();
                bundle.putParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG, blur);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(m_Context, CITrackActivity.class);

                for (int i = 0; i < 3; i++) {
                    ArrayList<CIFlightStatus_infoEntity> sharePreferenceData = gson.fromJson(info.getFlightTrackData(SHAREPRE_TAG_TRACKDATA + i), dataType);
                    if (info.getFlightTrackData(SHAREPRE_TAG_TRACKDATA + i).equals("")) {
//                        沒有重複的資料
                        if (!isContain()) {
                            info.setFlightTrackData(SHAREPRE_TAG_TRACKDATA+ i, m_DetailListView.getTrackData().toString());

                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_alpha_in, 0);

                            break;
                        } else if (info.getFlightTrackData(SHAREPRE_TAG_TRACKDATA + i).equals(m_DetailListView.getTrackData().toString())) {
                            info.removeFlightTrackData(SHAREPRE_TAG_TRACKDATA + i);
                            break;
                        }
                    } else {
//                        儲存的資料和目前的資料一樣，刪除存的資料
                        if (sharePreferenceData.get(0).carrier.equals(dataArray.get(0).carrier) && sharePreferenceData.get(0).flight_number.equals(dataArray.get(0).flight_number)
                                && sharePreferenceData.get(0).depature_station_code.equals(dataArray.get(0).depature_station_code) && sharePreferenceData.get(0).arrival_station_code.equals(dataArray.get(0).arrival_station_code)
                                && sharePreferenceData.get(0).stdd.equals(dataArray.get(0).stdd)&& sharePreferenceData.get(0).stad.equals(dataArray.get(0).stad)) {
                            info.removeFlightTrackData(SHAREPRE_TAG_TRACKDATA + i);
                            break;
                        }
                    }
                }

                bitmap.recycle();
            }
        };
    }

    //touch 改變button顏色
    private View.OnTouchListener trackTouch() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!isFill()) {
                        v.setBackground(getResources().getDrawable(R.drawable.track_click));
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (!isContain() && (!isFill())) {
                        m_btnTrack.setBackgroundResource(R.drawable.bg_tracked_btn);
                        m_btnTrack.setTextColor(getResources().getColor(R.color.french_blue));
                        m_btnTrack.setText(R.string.tracked);
                    } else {
                        m_btnTrack.setBackgroundResource(R.drawable.bg_track_btn);
                        m_btnTrack.setTextColor(getResources().getColor(R.color.white_four));
                        m_btnTrack.setText(R.string.track);
                    }
                }
                return false;
            }
        };
    }

    /*
    判斷 sharedPreferences 內是否有存滿資料
     */
    private boolean isFill() {
        if ((!info.getFlightTrackData(SHAREPRE_TAG_TRACKDATA_0).equals("")
                && !info.getFlightTrackData(SHAREPRE_TAG_TRACKDATA_1).equals("")
                && !info.getFlightTrackData(SHAREPRE_TAG_TRACKDATA_2).equals(""))) {
            return true;
        } else {
            return false;
        }
    }

    /*
    判斷是否有重複的資料
     */
    private boolean isContain() {
        boolean block = false;
        Gson gson = new Gson();
        Type dataType = new TypeToken<ArrayList<CIFlightStatus_infoEntity>>() {
        }.getType();
        ArrayList<CIFlightStatus_infoEntity> dataArray = gson.fromJson(m_DetailListView.getTrackData(), dataType);
        for (int j = 0; j < 3; j++) {
            if (!info.getFlightTrackData(SHAREPRE_TAG_TRACKDATA + j).equals("")) {
                ArrayList<CIFlightStatus_infoEntity> sharePreferenceData = gson.fromJson(info.getFlightTrackData(SHAREPRE_TAG_TRACKDATA + j), dataType);
                if (sharePreferenceData.get(0).carrier.equals(dataArray.get(0).carrier) && sharePreferenceData.get(0).flight_number.equals(dataArray.get(0).flight_number)
                        && sharePreferenceData.get(0).depature_station_code.equals(dataArray.get(0).depature_station_code) && sharePreferenceData.get(0).arrival_station_code.equals(dataArray.get(0).arrival_station_code)
                        && sharePreferenceData.get(0).stdd.equals(dataArray.get(0).stdd)&& sharePreferenceData.get(0).stad.equals(dataArray.get(0).stad)) {

                    block = true;
                    break;
                }
            }
        }
        return block;
    }

}

