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
import ci.ws.Models.entities.CITimeTable_InfoEntity;
import ci.ws.Presenter.CIInquiryFlightStationPresenter;

/**
 * Created by flowmahuang on 2016/3/16.
 */
public class CIFlightResultReturnFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    /**
     * BUNDLE_FROM  出發機場代號
     * BUNDLE_TO 抵達機場代號
     * BUNDLE_DATA  帶入的資料
     */
    public static final String BUNDLE_FROM  = "From";
    public static final String BUNDLE_TO    = "to";
    public static final String BUNDLE_DATA  = "data";

    public static final int VIEW_FROM_TIME_TABLE    = CITimeTableFlightStatusDetailList.DEF_VIEW_TYPE_TIME_TABLE;
    public static final int VIEW_FROM_FLIGHT_STATUS = CITimeTableFlightStatusDetailList.DEF_VIEW_TYPE_FIGHT_STATUS;

    private TextView m_CountryFrom = null;
    private TextView m_CountryTo = null;
    private TextView m_CountryFromDetail = null;
    private TextView m_CountryToDetail = null;

    private CIATtimeTableList   m_FlightCardList    = null;
    private ImageView           m_AirplaneImage     = null;
    private View                m_vShadowView       = null;

    private String  m_strSubTitleText   = "";
    private String  m_strItineraryData  = "";
    private int     m_iViewType         = 0;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_flight_result;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        m_iViewType = CIFlightResultActivity.getWhatClass();
        m_CountryFrom = (TextView) view.findViewById(R.id.tv_country_from);
        m_CountryTo = (TextView) view.findViewById(R.id.tv_country_to);
        m_CountryFromDetail = (TextView) view.findViewById(R.id.tv_country_from_detail);
        m_CountryToDetail = (TextView) view.findViewById(R.id.tv_country_to_detail);
        m_FlightCardList = (CIATtimeTableList) view.findViewById(R.id.ls_timetable_result);
        m_AirplaneImage = (ImageView) view.findViewById(R.id.airplane_image);
        m_FlightCardList.setFromClass(m_iViewType);
        if (getArguments().getString(BUNDLE_DATA) != null) {
            m_strItineraryData = getArguments().getString(BUNDLE_DATA);
        }
        CIInquiryFlightStationPresenter flightStationPresenter = CIInquiryFlightStationPresenter.getInstance(null, CIInquiryFlightStationPresenter.ESource.TimeTable);
        if (getArguments().getString(BUNDLE_TO) != null) {
            m_CountryFrom.setText(getArguments().getString(BUNDLE_TO));
        }
        if (getArguments().getString(BUNDLE_FROM) != null) {
            m_CountryTo.setText(getArguments().getString(BUNDLE_FROM));
        }

        //2017-03-28 modify by ryan for timetable 不顯示返程日
        m_FlightCardList.setTextContent("");

//        if (m_strSubTitleText != null) {
//            m_FlightCardList.setTextContent(m_strSubTitleText);
//        }
//        m_AirplaneImage.setVisibility(View.GONE);

        if (!m_strItineraryData.equals("[]")) {
            Type dataType = new TypeToken<ArrayList<CITimeTable_InfoEntity>>() {
            }.getType();
            Gson gson = new Gson();
            ArrayList<CITimeTable_InfoEntity> respData = gson.fromJson(m_strItineraryData, dataType);
            m_FlightCardList.setTimeTableData(respData);
            m_vShadowView = (View) view.findViewById(R.id.vGradient);


            CIFlightStationEntity fromData = flightStationPresenter.getStationInfoByIATA(getArguments().getString(BUNDLE_FROM));
            CIFlightStationEntity toData = flightStationPresenter.getStationInfoByIATA(getArguments().getString(BUNDLE_TO));
            //判斷代號是否有對應的機場名
            if (toData != null) {
                m_CountryFromDetail.setText(toData.airport_name);
            }
            if (fromData != null) {
                m_CountryToDetail.setText(fromData.airport_name);
            }
            m_vShadowView = (View) view.findViewById(R.id.vGradient);
        }
    }


    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.setMargins(m_CountryFrom, 20, 20, 0, 0);
        vScaleDef.setMargins(m_CountryTo, 0, 20, 20, 0);
        vScaleDef.setMargins(m_CountryFromDetail, 20, 0, 0, 0);
        vScaleDef.setMargins(m_CountryToDetail, 0, 0, 20, 0);
        vScaleDef.setTextSize(46.7, m_CountryFrom);
        vScaleDef.setTextSize(46.7, m_CountryTo);
        vScaleDef.setTextSize(16, m_CountryFromDetail);
        vScaleDef.setTextSize(16, m_CountryToDetail);
//        m_FlightCardList.setDividerHeight(vScaleDef.getLayoutHeight(10));
    }

    @Override
    protected void setOnParameterAndListener(View view) {
        m_FlightCardList.setOnItemClickListener(this);
//        m_FlightCardList.setOnScrollListener(scrollListener());
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
        intent.putExtra(CIFlightResultDetialActivity.BUNDLE_PARA_VIEW_TYPE, VIEW_FROM_TIME_TABLE);
        intent.putExtra(CIFlightResultDetialActivity.BUNDLE_PARA_ITINERARY_DATA, m_strItineraryData);
        intent.putExtra(CIFlightResultDetialActivity.BUNDLE_PARA_DATA_POSITION, position - 1);
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
//                if (totalItemCount == visibleItemCount)
//                    m_vShadowView.setVisibility(View.GONE);
//                else if (lastItem == totalItemCount) {
//                    if (m_FlightCardList.getChildAt((m_FlightCardList.getChildCount() - 1)).getBottom() == m_FlightCardList.getHeight()) {
//                        m_vShadowView.setVisibility(View.GONE);
//                    } else
//                        m_vShadowView.setVisibility(View.VISIBLE);
//                } else
//                    m_vShadowView.setVisibility(View.VISIBLE);
//
//            }
//        };
//    }

    //設定上方顯示depart on或 return  date的字串
    public void setContentt(String string) {
        this.m_strSubTitleText = string;
    }

}
