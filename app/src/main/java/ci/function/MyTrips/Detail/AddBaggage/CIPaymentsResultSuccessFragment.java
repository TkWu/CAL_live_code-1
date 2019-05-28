package ci.function.MyTrips.Detail.AddBaggage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.Base.BaseFragment;
import ci.function.BoardingPassEWallet.ExtraServices.CIExtraServicesCardActivity;
import ci.function.BoardingPassEWallet.item.CIExtraServiceItem;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;
import ci.ui.view.ImageHandle;
import ci.ui.view.ShadowBar.ShadowBarScrollview;
import ci.ws.Models.CIInquiryAllPassengerByPNRModel;
import ci.ws.Models.entities.CIEBPaymentResp;
import ci.ws.Models.entities.CIEWallet_ExtraService_Info;
import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.Models.entities.BaggageEntity.FlightInfo;
import ci.ws.Models.entities.FlightInfoList;

/**
 * Created by kevincheng on 2017/9/22.
 * 付款結果成功頁
 */

public class CIPaymentsResultSuccessFragment extends BaseFragment{
    private ShadowBarScrollview      m_shadowScrollView = null;
    private LinearLayout             m_llContent        = null;
    private CIEBPaymentResp          m_ebpResp          = null;
    private CITripListResp_Itinerary m_tliResp          = null;
    private ImageView                m_ivTitle          = null;
    private ImageView                m_ivFlight         = null;
    private ArrayList<View>          m_arView           = null;

    public static CIPaymentsResultSuccessFragment newInstance(CIEBPaymentResp resp,CITripListResp_Itinerary m_tripData) {

        Bundle args = new Bundle();
        args.putSerializable(UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST, m_tripData);
        args.putSerializable(UiMessageDef.BUNDLE_EBPAYMENTS_RESP, resp);
        CIPaymentsResultSuccessFragment fragment = new CIPaymentsResultSuccessFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_payments_success_result_card;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        final Bundle bundle = getArguments();
        if(null != bundle) {
            m_ebpResp = (CIEBPaymentResp)bundle.getSerializable(UiMessageDef.BUNDLE_EBPAYMENTS_RESP);
            m_tliResp = (CITripListResp_Itinerary)bundle.getSerializable(UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST);
        }

        m_shadowScrollView  = (ShadowBarScrollview) view.findViewById(R.id.shadowlayout);
        m_llContent         = m_shadowScrollView.getContentView();

        //加入抬頭
        View    viewTitle = m_layoutInflater.inflate( R.layout.layout_view_passenger_payments_result_success_title_card, null);
        m_ivTitle = (ImageView) viewTitle.findViewById(R.id.iv_success);
        m_llContent.addView(viewTitle);

        AppInfo appInfo = new AppInfo();

        //加入飛航資訊
        View    viewFlightData = m_layoutInflater.inflate( R.layout.layout_view_passenger_payments_result_success_flight_data_card, null);
        m_ivFlight  = (ImageView) viewFlightData.findViewById(R.id.iv_flight);
        TextView    departure   = (TextView)  viewFlightData.findViewById(R.id.tv_departure);
        TextView    arrival     = (TextView)  viewFlightData.findViewById(R.id.tv_arrival);
        TextView    totol       = (TextView)  viewFlightData.findViewById(R.id.tv_value);

        if(null != m_tliResp) {
            departure.setText(m_tliResp.Departure_Station);
            arrival.setText(m_tliResp.Arrival_Station);
        }

        if(null != m_ebpResp) {
            totol.setText(m_ebpResp.ttl_currency + " " + appInfo.valueFormat(m_ebpResp.ttl_amount));
        }
        LinearLayout.LayoutParams lLayoutParams = new LinearLayout.LayoutParams(340, ViewGroup.LayoutParams.MATCH_PARENT);
        lLayoutParams.topMargin = 30;
        lLayoutParams.gravity = Gravity.CENTER;
        m_llContent.addView(viewFlightData, lLayoutParams);

        //乘客個別購買資訊
        if(null != m_ebpResp && null != m_ebpResp.Pax_Info && m_ebpResp.Pax_Info.size() > 0) {

            m_arView = new ArrayList<>();

            for(int index = 0 ; index < m_ebpResp.Pax_Info.size() ; index++) {
                final CIEBPaymentResp.PaxInfo paxInfo = m_ebpResp.Pax_Info.get(index);

                if(null == paxInfo) {
                    continue;
                }

                View viewBuyData    = m_layoutInflater.inflate( R.layout.layout_view_passenger_payments_result_success_buy_data_card, null);
                ImageView ivEwallet = (ImageView) viewBuyData.findViewById(R.id.iv_ewallet);
                m_arView.add(ivEwallet);

                TextView tvName     = (TextView) viewBuyData.findViewById(R.id.tv_name);
                TextView tvBag      = (TextView) viewBuyData.findViewById(R.id.tv_bag_value);
                TextView tvPrice    = (TextView) viewBuyData.findViewById(R.id.tv_price_value);
                RelativeLayout relativeLayout = (RelativeLayout) viewBuyData.findViewById(R.id.card_root);

                relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Bitmap  bitmap = ImageHandle.getScreenShot(getActivity());
                        Bitmap  blur   = ImageHandle.BlurBuilder(getActivity(), bitmap, 13.5f, 0.15f);

                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();

                        bundle.putString(UiMessageDef.BUNDLE_ACTIVITY_MODE,
                                         "EB");
                        bundle.putBoolean(CIExtraServiceItem.DEF_IS_EXPIRED_TAG,
                                          false);
                        bundle.putParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG, blur);

                        bundle.putSerializable(UiMessageDef.BUNDLE_EWALLET_EXTRA_SERVICES_DATA, convertData(paxInfo, m_ebpResp.purchase_date));
                        intent.putExtras(bundle);
                        intent.setClass(getActivity(), CIExtraServicesCardActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.anim_alpha_in, 0);
                    }
                });

                tvName.setText(paxInfo.first_name + " " + paxInfo.last_name);
                tvBag.setText(paxInfo.ssrAmount + CIInquiryAllPassengerByPNRModel.getBagUnit(paxInfo.ssrType));
                tvPrice.setText(paxInfo.ebCurrency + " " + appInfo.valueFormat(paxInfo.ebAmount));

                lLayoutParams = new LinearLayout.LayoutParams(340, ViewGroup.LayoutParams.MATCH_PARENT);
                lLayoutParams.topMargin = 10;
                lLayoutParams.gravity = Gravity.CENTER;

                m_llContent.addView(viewBuyData, lLayoutParams);
            }
        }






    }

    private CIEWallet_ExtraService_Info convertData(CIEBPaymentResp.PaxInfo data, String purchaseDate){
        CIEWallet_ExtraService_Info convertedData = new CIEWallet_ExtraService_Info();
        convertedData.SERVICETYPE = "EB";
        convertedData.FIRSTNAME = data.first_name;
        convertedData.LASTNAME = data.last_name;
        convertedData.TICKETNO = data.ticket_num;
        convertedData.EMDNO = data.emd_num;
        convertedData.SSRAMOUNT = data.ssrAmount;
        convertedData.SSRTYPE = data.ssrType;
        convertedData.EBAMOUNT = data.ebAmount;
        convertedData.EBCURRENCY = data.ebCurrency;
        convertedData.PURCHASE_DATE = purchaseDate;
        if(null != data.flight_info && data.flight_info.size() > 0) {
            convertedData.Flight_Info = new FlightInfoList();
            for(CIEBPaymentResp.FlightInfo flightInfo:data.flight_info) {
                FlightInfo newFlightInfo = new FlightInfo();
                newFlightInfo.flight_date = flightInfo.flight_date;
                newFlightInfo.flight_num = flightInfo.flight_num;
                convertedData.Flight_Info.add(newFlightInfo);
            }
        }
        return convertedData;
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(view.findViewById(R.id.root));
        vScaleDef.selfAdjustSameScaleView(m_ivTitle, 48, 48);
        vScaleDef.selfAdjustSameScaleView(m_ivFlight, 21.3, 21.3);
        if(null != m_arView) {
            for(View iv : m_arView) {
                vScaleDef.selfAdjustSameScaleView(iv, 24, 24);
            }
        }
    }

    @Override
    protected void setOnParameterAndListener(View view) {
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
}
