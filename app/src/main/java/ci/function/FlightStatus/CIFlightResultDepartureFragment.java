package ci.function.FlightStatus;

import android.content.Intent;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ci.function.Base.BaseFragment;
import ci.ui.TimeTable.CIATtimeTableList;
import ci.ui.TimeTable.CITimeTableFlightStatusDetailList;
import ci.ui.define.ViewScaleDef;
import ci.ws.Models.entities.CIFlightStationEntity;
import ci.ws.Models.entities.CIFlightStatus_infoEntity;
import ci.ws.Models.entities.CITimeTable_InfoEntity;
import ci.ws.Presenter.CIInquiryFlightStationPresenter;

/**
 * Created by flowmahuang on 2016/3/16.
 */
public class CIFlightResultDepartureFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    /**
     * BUNDLE_FROM_CODE  出發機場代號
     * BUNDLE_TO_CODE 抵達機場代號
     * BUNDLE_DATA  帶入的資料
     */
    public static final String BUNDLE_FROM_CODE = "From";
    public static final String BUNDLE_TO_CODE = "to";
    public static final String BUNDLE_DATA = "data";

    private static final int VIEW_TYPE_FROM_TIME_TABLE      = CITimeTableFlightStatusDetailList.DEF_VIEW_TYPE_TIME_TABLE;
    private static final int VIEW_TYPE_FROM_FLIGHT_STATUS   = CITimeTableFlightStatusDetailList.DEF_VIEW_TYPE_FIGHT_STATUS;

    private TextView m_CountryFrom = null;
    private TextView m_CountryTo = null;
    private TextView m_CountryFromDetail = null;
    private TextView m_CountryToDetail = null;

    public CIATtimeTableList    m_CardList      = null;
    private View                m_vShadowView   = null;
    private ImageView           m_AirplaneImage = null;

    private int m_iViewType;

    private String textContent = "";
    private String m_strData;

    private static final String NO_DATA = "[]";
    private static final String NULL = "null";

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_flight_result;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
//        m_dialog = CIProgressDialog.createDialog(getActivity());
        m_iViewType = CIFlightResultActivity.getWhatClass();

        m_CountryFrom = (TextView) view.findViewById(R.id.tv_country_from);
        m_CountryTo = (TextView) view.findViewById(R.id.tv_country_to);
        m_CountryFromDetail = (TextView) view.findViewById(R.id.tv_country_from_detail);
        m_CountryToDetail = (TextView) view.findViewById(R.id.tv_country_to_detail);
        m_CardList = (CIATtimeTableList) view.findViewById(R.id.ls_timetable_result);
        m_CardList.setFromClass(m_iViewType);
//        if (textContent != null) {
//            m_CardList.setTextContent(textContent);
//        }
        m_vShadowView = (View) view.findViewById(R.id.vGradient);
        m_AirplaneImage = (ImageView) view.findViewById(R.id.airplane_image);
        CIInquiryFlightStationPresenter flightStationPresenter = CIInquiryFlightStationPresenter.getInstance(null, CIInquiryFlightStationPresenter.ESource.TimeTable);
        if (m_iViewType == VIEW_TYPE_FROM_FLIGHT_STATUS) {

            //2017-03-28 modifly by ryan for 航班動態需要顯示飛行日
            if (textContent != null) {
                m_CardList.setTextContent(textContent);
            }
            //
            m_strData = getArguments().getString(BUNDLE_DATA);
            //判斷資料
            if (!m_strData.equals(NO_DATA)) {
                Type dataType = new TypeToken<ArrayList<CIFlightStatus_infoEntity>>() {
                }.getType();
                Gson gson = new Gson();
                ArrayList<CIFlightStatus_infoEntity> respData = gson.fromJson(m_strData, dataType);
//                設定Flight  Status 資料
                m_CardList.setFlightStatusData(respData);
                CIFlightStationEntity fromData = flightStationPresenter.getStationInfoByIATA(respData.get(0).depature_station_code);
                CIFlightStationEntity toData = flightStationPresenter.getStationInfoByIATA(respData.get(respData.size() - 1).arrival_station_code);

                m_CountryFrom.setText(respData.get(0).depature_station_code);
                //抓最後一筆資料的抵達代號
                m_CountryTo.setText(respData.get(respData.size() - 1).arrival_station_code);
                //判斷代號是否有對應的機場名
                if (fromData != null) {
                    m_CountryFromDetail.setText(fromData.airport_name);
                }
                if (toData != null) {
                    m_CountryToDetail.setText(toData.airport_name);
                }
            }
        } else if (m_iViewType == VIEW_TYPE_FROM_TIME_TABLE) {

            //2017-03-28 modifly by ryan for TimeTable不需要顯示飛行日
            if (textContent != null) {
                m_CardList.setTextContent("");
            }
            //

            if (getArguments().getString(BUNDLE_FROM_CODE) != null) {
                m_CountryFrom.setText(getArguments().getString(BUNDLE_FROM_CODE));
            }
            if (getArguments().getString(BUNDLE_TO_CODE) != null) {
                m_CountryTo.setText(getArguments().getString(BUNDLE_TO_CODE));
            }
            if (getArguments().getString(BUNDLE_DATA) != null) {
                m_strData = getArguments().getString(BUNDLE_DATA);
            }
//            m_AirplaneImage.setVisibility(View.GONE);
            //判斷資料
            if (!m_strData.equals(NO_DATA)) {
                Type dataType = new TypeToken<ArrayList<CITimeTable_InfoEntity>>() {
                }.getType();
                Gson gson = new Gson();
                ArrayList<CITimeTable_InfoEntity> respData = gson.fromJson(m_strData, dataType);
//                設定TimeTable 資料
                m_CardList.setTimeTableData(respData);


                CIFlightStationEntity fromData = flightStationPresenter.getStationInfoByIATA(getArguments().getString(BUNDLE_FROM_CODE));
                CIFlightStationEntity toData = flightStationPresenter.getStationInfoByIATA(getArguments().getString(BUNDLE_TO_CODE));
                //判斷代號是否有對應的幾場名
                if (fromData != null) {
                    m_CountryFromDetail.setText(fromData.airport_name);
                }
                if (toData != null) {
                    m_CountryToDetail.setText(toData.airport_name);
                }
            }

        }
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
//        vScaleDef.setMargins(m_CountryFrom, 20, 20, 0, 0);
//        vScaleDef.setMargins(m_CountryTo, 0, 20, 20, 0);
//        vScaleDef.setMargins(m_CountryFromDetail, 20, 0, 0, 0);
//        vScaleDef.setMargins(m_CountryToDetail, 0, 0, 20, 0);
//        vScaleDef.setMargins(m_AirplaneImage, 0, 28, 0, 0);
//        vScaleDef.setTextSize(46.7, m_CountryFrom);
//        vScaleDef.setTextSize(46.7, m_CountryTo);
//        vScaleDef.setTextSize(16, m_CountryFromDetail);
//        vScaleDef.setTextSize(16, m_CountryToDetail);
        vScaleDef.selfAdjustSameScaleView(m_AirplaneImage, 40, 40);
//        m_CardList.setDividerHeight(vScaleDef.getLayoutHeight(10));
    }

    @Override
    protected void setOnParameterAndListener(View view) {
        m_CardList.setOnItemClickListener(this);
//        m_CardList.setOnScrollListener(scrollListener());
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
    public void onLanguageChangeUpdateUI() {

    }

    private void changeActivity(Class clazz, int position) {
        Intent intent = new Intent();
        intent.putExtra(CIFlightResultDetialActivity.BUNDLE_PARA_VIEW_TYPE, m_iViewType);
        intent.putExtra(CIFlightResultDetialActivity.BUNDLE_PARA_DATA_POSITION, position - 1);
        intent.putExtra(CIFlightResultDetialActivity.BUNDLE_PARA_ITINERARY_DATA, m_strData);
        intent.setClass(getContext(), clazz);
        startActivity(intent);

        //測試切換activity滑入滑出動畫
        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        changeActivity(CIFlightResultDetialActivity.class, position);
    }

//    private AbsListView.OnScrollListener scrollListener() {
//        return new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                int lastItem = firstVisibleItem + visibleItemCount;
//                if (totalItemCount == visibleItemCount) {
//                    if (m_CardList.getChildAt((m_CardList.getChildCount() - 1)).getBottom() == m_CardList.getHeight()) {
//                        if (m_CardList.getChildAt((m_CardList.getChildCount() - 1)) != null) {
//                            m_vShadowView.setVisibility(View.GONE);
//                        } else {
//                            m_vShadowView.setVisibility(View.VISIBLE);
//                        }
//                    }
//                } else if (lastItem == totalItemCount) {
//                    if (m_CardList.getChildAt((m_CardList.getChildCount() - 1)).getBottom() == m_CardList.getHeight()) {
//                        m_vShadowView.setVisibility(View.GONE);
//                    } else
//                        m_vShadowView.setVisibility(View.VISIBLE);
//                } else
//                    m_vShadowView.setVisibility(View.VISIBLE);
//
//            }
//        };
//    }

    //從哪個進入點進入   timetable  ,  flight status
    public void setFromClass(int status) {
        this.m_iViewType = status;
    }

    //設定depart  or  return 字串
    public void setContentt(String string) {
        this.textContent = string;
    }

}

