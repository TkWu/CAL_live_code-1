package ci.function.MyTrips.Detail.AddBaggage;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ci.function.Base.BaseFragment;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ShadowBar.ShadowBarScrollview;
import ci.ws.Models.CIInquiryAllPassengerByPNRModel;
import ci.ws.Models.entities.CIInquiryEBBasicInfoResp;
import ci.ws.Models.entities.CIInquiryExcessBaggageInfoReq;

/**
 * Created by kevincheng on 2017/9/18.
 */

public class CIAddExcessBaggageFragment extends BaseFragment{
    private ShadowBarScrollview      m_shadowScrollView = null;
    private LinearLayout             m_llContent        = null;
    private CIInquiryEBBasicInfoResp m_eBBasicInfo      = null;
    private ArrayList<View>          m_arViews          = null;
    private ICallBack                m_callback         = null;
    public interface ICallBack {
        void onClickAddOrDecreaseButton(boolean isAddBag);
    }

    public static CIAddExcessBaggageFragment newInstance(CIInquiryEBBasicInfoResp datas) {

        Bundle args = new Bundle();
        args.putSerializable(UiMessageDef.BUNDLE_INQUIRY_EBBASIC_INFO_RESP, datas);
        CIAddExcessBaggageFragment fragment = new CIAddExcessBaggageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_add_excess_baggage_card;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        Bundle bundle = getArguments();
        if(null != bundle) {
            m_eBBasicInfo = (CIInquiryEBBasicInfoResp)bundle.getSerializable(UiMessageDef.BUNDLE_INQUIRY_EBBASIC_INFO_RESP);
        }
        m_shadowScrollView  = (ShadowBarScrollview) view.findViewById(R.id.shadowlayout);
        m_llContent         = m_shadowScrollView.getContentView();

        int iPaxInfo = 0;

        if(null != m_eBBasicInfo && null != m_eBBasicInfo.Pax_Info) {
            iPaxInfo = m_eBBasicInfo.Pax_Info.size();
            m_arViews = new ArrayList<>();
        } else {
            return;
        }
       
        for(int loop = 0; loop < iPaxInfo; loop ++) {
            View           ViewContent = m_layoutInflater.inflate( R.layout.layout_view_passenger_add_excess_baggage_card, null);

            final TextView      name        = (TextView) ViewContent.findViewById(R.id.name);
            final TextView      value      = (TextView)ViewContent.findViewById(R.id.tv_value);
            final TextView      unit       = (TextView)ViewContent.findViewById(R.id.tv_unit);
            final ImageButton   add         = (ImageButton)ViewContent.findViewById(R.id.ib_add);
            final ImageButton   decrease    = (ImageButton)ViewContent.findViewById(R.id.ib_decrease);

            final Map<String, Integer>  mapEbOptionIndex = new HashMap<>();
            final CIInquiryEBBasicInfoResp.PaxInfo             paxInfo        = m_eBBasicInfo.Pax_Info.get(loop);
            final ArrayList<CIInquiryEBBasicInfoResp.EbOption> ebOption       = paxInfo.eb_option;

            //收集購買資料
            Item item       = new Item();
            item.paxNum     = paxInfo.pax_num;
            item.firstName  = paxInfo.first_name;
            item.lastName   = paxInfo.last_name;
            ViewContent.setTag(item);
            m_arViews.add(ViewContent);


            mapEbOptionIndex.put(paxInfo.pax_num, 0);

            name.setText(paxInfo.first_name + " " + paxInfo.last_name);
            unit.setText(CIInquiryAllPassengerByPNRModel.getBagUnit(m_eBBasicInfo.ssrType));

            //判斷單一乘客是否能夠加購行李
            if(checkCanAddBag(ViewContent, paxInfo, ebOption)) {
                //初始化數量/件的值
                value.setText(ebOption.get(0).kgAmt);
                int maxIndex = ebOption.size() - 1;
                setAmountBtnEnable(add,
                                   decrease,
                                   0,
                                   maxIndex);

                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        amountLogicControl(mapEbOptionIndex,
                                           paxInfo,
                                           ebOption,
                                           value,
                                           add,
                                           decrease,
                                           v);
                        if(null != m_callback) {
                            m_callback.onClickAddOrDecreaseButton(checkAddBag(getAddBagInfo()));
                        }
                    }
                };

                add.setOnClickListener(onClickListener);
                decrease.setOnClickListener(onClickListener);
            }

            m_llContent.addView(ViewContent);
        }

    }


    //檢查是否有加購行李(總數量需大於零)
    private boolean checkAddBag(CIInquiryExcessBaggageInfoReq req){
        if(null == req || req.eb.size() <= 0){
            return false;
        }
        int total = 0;
        for(CIInquiryExcessBaggageInfoReq.EB data: req.eb) {
            try {
                total = total + Integer.valueOf(data.ssrAmount);
            } catch (Exception e){}
        }

        if(total > 0) {
            return true;
        } else {
            return false;
        }

    }

    private void amountLogicControl(Map<String, Integer>  mapEbOptionIndex,
                                    CIInquiryEBBasicInfoResp.PaxInfo paxInfo,
                                    ArrayList<CIInquiryEBBasicInfoResp.EbOption> ebOption,
                                    TextView value,
                                    ImageButton add,
                                    ImageButton decrease,
                                    View currentView){

        int index = mapEbOptionIndex.get(paxInfo.pax_num);
        int nextIndex = index + 1;
        boolean bChangeState = false;

        if(index < (ebOption.size() - 1) && currentView.getId() == R.id.ib_add) {
            nextIndex = index + 1;
            bChangeState = true;
        } else if(index > 0 && currentView.getId() == R.id.ib_decrease) {
            nextIndex = index - 1;
            bChangeState = true;
        }

        if(true == bChangeState) {
            mapEbOptionIndex.put(paxInfo.pax_num, nextIndex);
            value.setText(ebOption.get(nextIndex).kgAmt);
            setAmountBtnEnable(add,
                               decrease,
                               nextIndex,
                               ebOption.size() - 1);
        }
    }

    private void setAmountBtnEnable(ImageButton add,
                                    ImageButton decrease,
                                    int index,
                                    int maxIndex){


        if (maxIndex == 0) {
            add.setEnabled(false);
            add.setAlpha(0.5f);
            decrease.setEnabled(false);
            decrease.setAlpha(0.5f);
        } else if (index <= 0) {
             add.setEnabled(true);
             add.setAlpha(1.0f);
             decrease.setEnabled(false);
             decrease.setAlpha(0.5f);
         } else if ( index >= maxIndex) {
             add.setEnabled(false);
             add.setAlpha(0.5f);
             decrease.setEnabled(true);
             decrease.setAlpha(1.0f);
         } else {
             add.setEnabled(true);
             add.setAlpha(1.0f);
             decrease.setEnabled(true);
             decrease.setAlpha(1.0f);
         }
    }


    private boolean checkCanAddBag(View root,
                                   CIInquiryEBBasicInfoResp.PaxInfo paxInfo,
                                   ArrayList<CIInquiryEBBasicInfoResp.EbOption> ebOptions ){

        if(!checkIsAddExcessBaggageTag(paxInfo.is_add_excessBaggage, root)){
            //當is_add_excessBaggage tag 的值已經直接告知不可購買時，不在判斷下面ebOption
            if(null != paxInfo && !TextUtils.isEmpty(paxInfo.msg)) {
                ((TextView)root.findViewById(R.id.tv_msg)).setText(paxInfo.msg);
            }
            return false;
        }
        if(!checkEbOptionData(ebOptions, root)) {
            ((TextView)root.findViewById(R.id.tv_msg)).setText("Data Error");
            return false;
        }
        return true;
    }

    /**
     * 檢查 is_add_excessBaggage tag 是否可加購
     * @param canAddBag
     * @param root
     * @return  不能加購 if false
     */
    private boolean checkIsAddExcessBaggageTag(String canAddBag,
                                            View root){
        if(canAddBag.toUpperCase().equals("Y")) {
            enableAddBagField(root);
            return true;
        } else {
            disableAddBagField(root);
            return false;
        }
    }

    /**
     * EbOption 資料結構檢查
     * @param ebOptions
     * @param root
     * @return 不可加購行李 if false
     */
    private boolean checkEbOptionData(ArrayList<CIInquiryEBBasicInfoResp.EbOption> ebOptions,
                                      View root){
        if(null != ebOptions && ebOptions.size() > 0) {
            //當行李選項只有一個且只能選擇0的數量時，則取消可以選擇的功能
            if(ebOptions.size() == 1) {
                int zeroIndexAmount;
                try {
                    zeroIndexAmount = Integer.parseInt(ebOptions.get(0).kgAmt);
                } catch (Exception e) {
                    disableAddBagField(root);
                    return false;
                }
                if(zeroIndexAmount < 0) {
                    disableAddBagField(root);
                    return false;
                }
            }
        } else {
            disableAddBagField(root);
            return false;
        }
        int lastAmount = -1;
        for(CIInquiryEBBasicInfoResp.EbOption data: ebOptions){
            int currentAmount = 0;
            try{
                currentAmount = Integer.valueOf(data.kgAmt);
            }catch (Exception e){
                disableAddBagField(root);
                return false;
            }
            if(currentAmount <= lastAmount) {
                disableAddBagField(root);
                return false;
            } else {
                lastAmount = currentAmount;
            }
        }
        enableAddBagField(root);
        return true;
    }

    private void enableAddBagField(View root){
        root.findViewById(R.id.rl_add_bag).setAlpha(1f);
        root.findViewById(R.id.ib_add).setEnabled(true);
        root.findViewById(R.id.ib_decrease).setEnabled(true);
        root.findViewById(R.id.rl_msg).setVisibility(View.GONE);
    }

    private void disableAddBagField(View root){
        root.findViewById(R.id.rl_add_bag).setAlpha(0.5f);
        root.findViewById(R.id.ib_add).setEnabled(false);
        root.findViewById(R.id.ib_decrease).setEnabled(false);
        root.findViewById(R.id.rl_msg).setVisibility(View.VISIBLE);
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(m_llContent);
        if(null == m_arViews) {
            return;
        }
        for(int loop = 0;loop < m_arViews.size();loop++) {
            vScaleDef.selfAdjustSameScaleView(m_arViews.get(loop).findViewById(R.id.iv_alerts), 24, 24);
        }
    }

    public CIInquiryExcessBaggageInfoReq getAddBagInfo(){
        if(null != m_arViews && m_arViews.size() > 0) {
            CIInquiryExcessBaggageInfoReq req = new CIInquiryExcessBaggageInfoReq();
            req.eb = new ArrayList<>();
            req.pnr_id = m_eBBasicInfo.pnr_id;
            req.pnr_seq = m_eBBasicInfo.pnr_seq;
            for(int loop = 0;loop < m_arViews.size();loop++) {
                CIInquiryExcessBaggageInfoReq.EB eb = new CIInquiryExcessBaggageInfoReq.EB();
                eb.ssrAmount = ((TextView)m_arViews.get(loop).findViewById(R.id.tv_value)).getText().toString().trim();
                int amount;
                try {
                    amount = Integer.valueOf(eb.ssrAmount);
                } catch (Exception e){
                    continue;
                }
                //加購數量為零，不加入request 資料中
                if(amount > 0) {
                    Item item = (Item)m_arViews.get(loop).getTag();

                    //request 需要的
                    eb.pax_num = item.paxNum;
                    eb.ssrType = m_eBBasicInfo.ssrType;

                    //價格頁顯示名字用
                    eb.first_name = item.firstName;
                    eb.last_name = item.lastName;

                    req.eb.add(eb);
                }
            }
            return req;
        }
        return null;
    }

    public void setListener(ICallBack callBack){
        m_callback  = callBack;
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

    private class Item {
        String paxNum;
        String lastName;
        String firstName;
    }
}
