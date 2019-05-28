package ci.function.MyTrips.Detail;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;
import ci.ui.view.ImageHandle;
import ci.ws.Models.entities.CITripListResp_Itinerary;

/**
 * Created by kevincheng on 2016/3/4.
 */
public class CIFlightAirCraftFragment extends BaseFragment {
    private ImageView    m_ivAirCraft = null;
    private CITripListResp_Itinerary m_data   = null;
    private final String PREFIX       = "img_flight_photo_";

    /**
     * 代碼對應飛機型號
     * 77W = B777-300
     * 744 = B747-400
     * 738 = B737-800
     * 343 = A340-300
     * 333 = A330-300
     * 359 = A350-900   //2016-11-10 Ryan, 350更正為359
     *
     * 如果server給的資料對應不到目前的飛機圖
     * ，則顯示預設圖檔R.drawable.img_flight_photo
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        m_data = (CITripListResp_Itinerary)getArguments().getSerializable(UiMessageDef.BUNDLE_MY_TRIPS_DATA_LIST);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_flight_tab_flight_air_craft;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        m_ivAirCraft = (ImageView) view.findViewById(R.id.iv_aircraft_img);

//        if(null != m_data){
//            int  resId = AppInfo.getInstance(getContext()).GetIconResourceId(
//                    PREFIX, m_data.Equipment.toLowerCase());
//            if(0 == resId){
//                m_ivAirCraft.setImageResource(R.drawable.img_flight_photo);
//            } else {
//                m_ivAirCraft.setImageResource(resId);
//            }
//
//        }

        //2016-11-10 Ryan, 調整飛機圖邏輯, 加入其他航空公司判斷
        if ( null == m_data ){
            return;
        }

        if ( TextUtils.equals( "CI", m_data.Airlines ) || TextUtils.equals( "AE", m_data.Airlines ) ){
            int  resId = AppInfo.getInstance(getContext()).GetIconResourceId(
                    PREFIX, m_data.Equipment.toLowerCase());
            if(0 == resId){
                m_ivAirCraft.setImageResource(R.drawable.img_flight_photo);
            } else {
                m_ivAirCraft.setImageResource(resId);
            }
        } else {
            // 未定義的航空公司都顯示預設圖檔
            m_ivAirCraft.setImageResource(R.drawable.img_flight_photo);
        }
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(view.findViewById(R.id.root));
        BitmapDrawable drawable = (BitmapDrawable)m_ivAirCraft.getDrawable();
        if(null != drawable){
            Bitmap bitmap = drawable.getBitmap();
            m_ivAirCraft.setImageBitmap(ImageHandle.getRoundedCornerBitmap(
                    bitmap,
                    vScaleDef.getLayoutMinUnit(3)));
        } else {
            m_ivAirCraft.setImageResource(R.drawable.bg_btn_radius_fill_white);
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
