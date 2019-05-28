package ci.function.MealSelection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;

import com.chinaairlines.mobile30.R;

import ci.function.Main.BaseActivity;
import ci.ui.MealCard.CIAListView;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.dialog.CIAlertDialog;
import ci.ui.object.CIProgressDialog;
import ci.ui.view.NavigationBar;
import ci.ws.Models.entities.CIInquirtMealInfoResp;
import ci.ws.Models.entities.CIInquiryMealByPassangerResp;
import ci.ws.Models.entities.CIPassengerListResp_PaxInfo;
import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.Presenter.CIInquiryMealTermsPresenter;
import ci.ws.Presenter.CISelectMealPresenter;
import ci.ws.Presenter.Listener.CIInquiryMealTermsListener;
import ci.ws.Presenter.Listener.CISelectMealListener;

/**
 * 選擇乘客以進入點餐畫面
 * Created by flowmahuang on 2016/3/7.
 * * Modifly by Ryan at 2016-09-29
 */
public class CISelectPassengerActivity extends BaseActivity implements View.OnClickListener {

    private NavigationBar               m_Navigationbar = null;
    private Button                      m_SelectPassengerNextButton = null;
    private CIAListView                 m_List = null;
    private View                        m_vShadowView = null;
    //private CIInquiryMealTermsPresenter m_MealTermsPresenter = null;
    //private CISelectMealPresenter       m_SelectMealPresenter = null;

    //private CIPassengerListResp         m_PassengerData         = null;
    //單一位乘客資訊
    private CIPassengerListResp_PaxInfo m_SinglePassenagerInfo  = null;
    private CITripListResp_Itinerary    m_tripData              = null;
    private String RespObj;

    private CIInquiryMealByPassangerResp m_MealbyPassanager = null;

    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_Context.getString(R.string.select_passenager);
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

    @Override
    public void onBackPressed() {
        CISelectPassengerActivity.this.finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_select_passenger;
    }

    @Override
    protected void initialLayoutComponent() {

        Bundle bundle = getIntent().getExtras();
        if ( null != bundle ){
            m_tripData      = (CITripListResp_Itinerary) bundle.getSerializable(UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST);
            //m_PassengerData = (CIPassengerListResp) bundle.getSerializable(UiMessageDef.BUNDLE_PASSENGER_INFO);
            m_SinglePassenagerInfo = (CIPassengerListResp_PaxInfo)bundle.getSerializable(UiMessageDef.BUNDLE_PASSENGER_INFO_SINGLE);
        }
        callSelectMealApi();

        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);
        m_SelectPassengerNextButton = (Button) findViewById(R.id.btn_to_select_meal);
        m_List = (CIAListView) findViewById(R.id.ls_passenger);
        m_vShadowView = findViewById(R.id.vGradient);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
    }

    @Override
    protected void setOnParameterAndListener() {
        m_SelectPassengerNextButton.setOnClickListener(this);
        m_Navigationbar.uiSetParameterListener(m_onNavigationParameter, m_onNavigationbarListener);
        m_List.setOnScrollListener(scrollListener());
        m_List.setFooterClickLisenerListener(setFooterListener());
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {}

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
    public void onClick(View view) {
        if (view.getId() == m_SelectPassengerNextButton.getId()) {
            if (m_List.getNum() == 0) {
                CIAlertDialog dialog = new CIAlertDialog(m_Context, new CIAlertDialog.OnAlertMsgDialogListener() {
                    @Override
                    public void onAlertMsgDialog_Confirm() {
                    }

                    @Override
                    public void onAlertMsgDialogg_Cancel() {
                    }
                });
                dialog.uiSetTitleText(getString(R.string.warning));
                dialog.uiSetConfirmText(getResources().getString(R.string.confirm));
                dialog.uiSetContentText(getString(R.string.SelectMeal_NotSelectedPassenger));
                dialog.show();
            } else {
                String flight_secor = m_tripData.Departure_Station + m_tripData.Arrival_Station;
//                m_DoWhat = SELECT_MEAL;
                CISelectMealPresenter.getInstance(m_SelectMealListener).InquiryMealInfoFromWS(m_tripData.Departure_Date, m_tripData.Flight_Number, flight_secor, m_tripData.Booking_Class);

            }
        }
    }

    private AbsListView.OnScrollListener scrollListener() {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastItem = firstVisibleItem + visibleItemCount;
                if (totalItemCount == visibleItemCount) {
                    m_vShadowView.setVisibility(View.GONE);
                } else if (lastItem == totalItemCount) {
                    if (m_List.getChildAt((m_List.getChildCount() - 1)).getBottom() == m_List.getHeight()) {
                        m_vShadowView.setVisibility(View.GONE);
                    } else
                        m_vShadowView.setVisibility(View.VISIBLE);
                } else
                    m_vShadowView.setVisibility(View.VISIBLE);

            }
        };
    }

    private CIAListView.footerClickLisener setFooterListener() {
        return new CIAListView.footerClickLisener() {
            @Override
            public void onFooterClick() {
//                m_DoWhat = SELECT_NOTICE;
                CIInquiryMealTermsPresenter.getInstance(m_InquiryMealTermsListener).InquiryMealTermsFromWS();

            }
        };
    }

    CIInquiryMealTermsListener m_InquiryMealTermsListener = new CIInquiryMealTermsListener() {
        @Override
        public void onInquiryMealTermsSuccess(String rt_code, String rt_msg, String strMealteams) {
            Intent intent = new Intent(CISelectPassengerActivity.this, CIMealSelectionNoticeActivity.class);
            intent.putExtra(CIMealSelectionNoticeActivity.MEALTERM, strMealteams);
            startActivity(intent);


            overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
        }

        @Override
        public void onInquiryMealTermsError(String rt_code, String rt_msg) {
            CIAlertDialog dialog = new CIAlertDialog(CISelectPassengerActivity.this, new CIAlertDialog.OnAlertMsgDialogListener() {
                @Override
                public void onAlertMsgDialog_Confirm() {

                }

                @Override
                public void onAlertMsgDialogg_Cancel() {

                }
            });
            dialog.uiSetTitleText(getString(R.string.warning));
            dialog.uiSetContentText(rt_msg);
            dialog.uiSetConfirmText(getResources().getString(R.string.confirm));
            dialog.show();
        }

        @Override
        public void showProgress() {
            showProgressDialog(new CIProgressDialog.CIProgressDlgListener() {
                @Override
                public void onBackPressed() {
                    CIInquiryMealTermsPresenter.getInstance(m_InquiryMealTermsListener).interruptWS();
                }
            });
        }

        @Override
        public void hideProgress() {
            hideProgressDialog();
        }
    };

    private void callSelectMealApi() {
//        m_DoWhat = SELECT_PASSENGER;

        String flight_secor = m_tripData.Departure_Station + m_tripData.Arrival_Station;
        CISelectMealPresenter.getInstance(m_SelectMealListener).InquiryMealByPassangerFromWS(m_tripData.Pnr_Id, m_SinglePassenagerInfo.First_Name
                , m_SinglePassenagerInfo.Last_Name, flight_secor, m_tripData.Departure_Date);
    }

    CISelectMealListener m_SelectMealListener = new CISelectMealListener() {
        @Override
        public void onInquiryMealPassenagerSuccess(String rt_code, String rt_msg, CIInquiryMealByPassangerResp mealByPassangerResp) {
            m_List.setSelectMealPresenter(mealByPassangerResp.passangers);

            m_MealbyPassanager = mealByPassangerResp;
        }

        @Override
        public void onInquiryMealPassenagerError(String rt_code, String rt_msg) {
            CIAlertDialog dialog = new CIAlertDialog(CISelectPassengerActivity.this, new CIAlertDialog.OnAlertMsgDialogListener() {
                @Override
                public void onAlertMsgDialog_Confirm() {
                    onBackPressed();
                }

                @Override
                public void onAlertMsgDialogg_Cancel() {

                }
            });
            dialog.uiSetTitleText(getString(R.string.warning));
            dialog.uiSetContentText(rt_msg);
            dialog.uiSetConfirmText(getResources().getString(R.string.confirm));
            dialog.show();
        }

        @Override
        public void onInquiryMealInfoSuccess(String rt_code, String rt_msg, CIInquirtMealInfoResp
        mealInfoResp) {
            Intent intent = new Intent();
            intent.putExtra(CISelectMealActivity.INTENT_PASSENGER_MAX, m_List.getNum());
            //
            m_MealbyPassanager.passangers = m_List.getSelectPassenagerList();
            intent.putExtra(CISelectMealActivity.BUNDLE_PASSENGER_LIST,     m_MealbyPassanager);
            intent.putExtra(CISelectMealActivity.BUNDLE_MEAL_LIST,          mealInfoResp);
            intent.putExtra(CISelectMealActivity.BUNDLE_DEPARTURE_STATION,  m_tripData.Departure_Station);
            intent.putExtra(CISelectMealActivity.BUNDLE_ARRIVAL_STATION,    m_tripData.Arrival_Station);
            //
            intent.setClass(m_Context, CISelectMealActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            startActivity(intent);
            finish();
            //測試切換activity滑入滑出動畫
            overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
        }

        @Override
        public void onInquiryMealInfoError(String rt_code, String rt_msg) {

            showDialog(getResources().getString(R.string.warning), rt_msg, getResources().getString(R.string.confirm), "", new CIAlertDialog.OnAlertMsgDialogListener() {

                @Override
                public void onAlertMsgDialog_Confirm() {
                    onBackPressed();
                }
                @Override
                public void onAlertMsgDialogg_Cancel() {}
            });
        }

        @Override
        public void showProgress() {
            showProgressDialog(new CIProgressDialog.CIProgressDlgListener() {
                @Override
                public void onBackPressed() {

                    CISelectMealPresenter.getInstance(m_SelectMealListener).InterruptInquiryMealInfoFromWS();
                    CISelectMealPresenter.getInstance(m_SelectMealListener).InterruptInquiryMealByPassangerFromWS();
                    finish();

                    overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
                }
            });
        }

        @Override
        public void hideProgress() {
            hideProgressDialog();
        }
    };

    @Override
    public void onPause() {
        super.onPause();

        CISelectMealPresenter.getInstance(null).InterruptInquiryMealByPassangerFromWS();
        CISelectMealPresenter.getInstance(null).InterruptInquiryMealInfoFromWS();
        CISelectMealPresenter.getInstance(null).setCallbackListener(null);

        CIInquiryMealTermsPresenter.getInstance(m_InquiryMealTermsListener).interruptWS();
    }
}
