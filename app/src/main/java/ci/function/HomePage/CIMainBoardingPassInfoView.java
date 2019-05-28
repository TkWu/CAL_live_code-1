package ci.function.HomePage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.LinkedHashSet;
import java.util.Set;

import ci.function.Base.BaseView;
import ci.function.BoardingPassEWallet.BoardingPass.CIBoardingPassCardActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.CIModelInfo;
import ci.ui.object.CIProgressDialog;
import ci.ui.object.item.CIHomeStatusEntity;
import ci.ui.view.ImageHandle;
import ci.ws.Models.entities.CIBoardPassResp;
import ci.ws.Models.entities.CIBoardPassRespItineraryList;
import ci.ws.Models.entities.CIBoardPassResp_PnrInfo;
import ci.ws.Presenter.CIInquiryBoardPassPresenter;
import ci.ws.Presenter.Listener.CIInquiryBoardPassListener;
import ci.ws.cores.object.GsonTool;

/**
 * Created by Ryan on 16/2/26.
 */
public class CIMainBoardingPassInfoView extends BaseView {

    public interface listener {
        CIHomeStatusEntity onInquiryBoardingPassData();
    }

    private ImageView   m_imgIcon     = null;
    private TextView    m_tvTitle     = null;
    private TextView    m_tvText      = null;
    private CIHomeStatusEntity m_data = null;
    private CIInquiryBoardPassPresenter m_presenter = null;
    private listener m_listener = null;
    public CIMainBoardingPassInfoView(Context context) {
        super(context);
        initial();
    }

    public CIMainBoardingPassInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initial();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_main_info_boarding_pass_view;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater) {

        m_tvTitle   = (TextView)findViewById(R.id.tvTitle);
        m_imgIcon   = (ImageView)findViewById(R.id.imgIcon);
        m_tvText    = (TextView)findViewById(R.id.tvText);
    }

    @Override
    protected void setTextSizeAndLayoutParams( ViewScaleDef vScaleDef) {

        //
        vScaleDef.setTextSize( 16, m_tvTitle);
        vScaleDef.setTextSize( 20, m_tvText);
        ((LayoutParams)m_imgIcon.getLayoutParams()).topMargin = vScaleDef.getLayoutHeight(8.7);
        ((LayoutParams)m_tvTitle.getLayoutParams()).topMargin = vScaleDef.getLayoutHeight(13);
        ((LayoutParams)m_tvText.getLayoutParams()).topMargin = vScaleDef.getLayoutHeight(20);

        m_imgIcon.getLayoutParams().height  = vScaleDef.getLayoutMinUnit(156.7);
        m_imgIcon.getLayoutParams().width   = vScaleDef.getLayoutMinUnit(156.7);
    }

    public void setTitleText( String strText ){
        if ( null != strText && strText.length() > 0 ){
            m_tvTitle.setText(strText);
        }
    }

    public void setContentText( String strText ){
        if ( null != strText && strText.length() > 0 ){
            m_tvText.setText(strText);
        }
    }

    public void setIcon( Bitmap bitmap ){
        if ( bitmap != null ){
            m_imgIcon.setBackground(new BitmapDrawable(null ,bitmap));
        } else {
            m_imgIcon.setBackgroundResource(R.color.white);
        }
        //點擊QR code圖要有透明灰的效果-Ling
        m_imgIcon.setImageResource(R.drawable.bg_transparent_press_black20);
    }

    public void setListener(listener listener){
        m_listener = listener;
    }

    //點選qr code: 顯示登機卡頁面
    public void SetIconOnClick(){
        m_imgIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CIModelInfo info = new CIModelInfo(m_Context);
                String strData = info.GetHomepageBoardingPassData();
                if(TextUtils.isEmpty(strData)){
                    if(null != m_listener){
                        m_data = m_listener.onInquiryBoardingPassData();
                    }
                    inquiryBoardingPassDataByPNRNo();
                } else {
                    CIBoardPassRespItineraryList datas
                            = GsonTool.toObject(strData, CIBoardPassRespItineraryList.class);
                    showBoardingPassCard(datas);
                }
            }
        });
    }


    private void inquiryBoardingPassDataByPNRNo(){
        m_presenter = CIInquiryBoardPassPresenter.getInstance(new CIInquiryBoardPassListener() {
            @Override
            public void onSuccess(String rt_code, String rt_msg, CIBoardPassResp datas) {
                CIBoardPassRespItineraryList arrData = sortBoardingPassData(datas);
                if(null == arrData){
                    return;
                } else {
                    CIModelInfo info = new CIModelInfo(m_Context);
                    String strData = GsonTool.toJson(arrData);
                    info.setHomepageBoardingPassData(strData);
                }

                showBoardingPassCard(arrData);
            }

            @Override
            public void onError(String rt_code, String rt_msg) {
                showDialog(m_Context.getString(R.string.warning),
                        rt_msg);
            }

            @Override
            public void showProgress() {
                showProgressDialog(new CIProgressDialog.CIProgressDlgListener() {
                    @Override
                    public void onBackPressed() {
                        m_presenter.interrupt();
                    }
                });
            }

            @Override
            public void hideProgress() {
                hideProgressDialog();
            }
        });
        Set<String> datas = new LinkedHashSet<>();
        if(null != m_data){
            datas.add(m_data.PNR_Id);
            m_presenter.InquiryBoardPassFromWSByPNRListAndCardNo(null, "", "", datas);
        }
    }


    private CIBoardPassRespItineraryList sortBoardingPassData(CIBoardPassResp datas){
        if ( null == datas || 0 >= datas.Pnr_Info.size()){
            return null;
        }

        CIBoardPassResp_PnrInfo pnrInfo = datas.Pnr_Info.get(0);

        //rt_code不為000時, 該pnr資料已失效
        if ( !pnrInfo.rt_code.equals("000") ){
            return null;
        }

        if ( null == pnrInfo.Itinerary
                || 0 >= pnrInfo.Itinerary.size() ){
            return null;
        }
        CIBoardPassRespItineraryList arrDatas = new CIBoardPassRespItineraryList();
        for ( int i = 0; i < pnrInfo.Itinerary.size(); i ++ ){
            arrDatas.add(pnrInfo.Itinerary.get(i)) ;
        }

        return arrDatas;

    }



    private void showBoardingPassCard(CIBoardPassRespItineraryList datas) {

        Bitmap bitmap = ImageHandle.getScreenShot((Activity)m_Context);
        Bitmap blur = ImageHandle.BlurBuilder(m_Context, bitmap, 13.5f, 0.15f);

        Bundle bundle = new Bundle();
        //傳入是否已使用登機證的tag
        bundle.putBoolean(UiMessageDef.BUNDLE_BOARDING_PASS_IS_EXPIRED_TAG, false);
        bundle.putSerializable(UiMessageDef.BUNDLE_BOARDING_PASS_DATAS, datas);
        bundle.putParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG, blur);

        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(m_Context, CIBoardingPassCardActivity.class);
        m_Context.startActivity(intent);

        ((Activity) m_Context).overridePendingTransition(R.anim.anim_alpha_in, 0);

        bitmap.recycle();
        System.gc();
    }
}
