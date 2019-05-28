package ci.function.ManageMiles.TabFragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ci.function.Base.BaseFragment;
import ci.function.ManageMiles.Adapter.CIMilesCardRecyclerViewAdapter;
import ci.function.ManageMiles.MilesDetailCard.CIMilesDetailCardActivity;
import ci.function.ManageMiles.item.CIAccumulationGroupItem;
import ci.function.ManageMiles.item.CIAccumulationItem;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;
import ci.ws.Models.entities.CIRedeemRecord_Info;

import static ci.function.ManageMiles.MilesDetailCard.CIMilesDetailCardActivity.MilesDetailCardType.AWARDS_TRANSFER;
import static ci.function.ManageMiles.MilesDetailCard.CIMilesDetailCardActivity.MilesDetailCardType.REDEEM;

/**  里程兌換紀錄
 * Created by jlchen on 2016/3/9.
 */
public class CIRedeemTabFragment extends BaseFragment{

    private CIMilesCardRecyclerViewAdapter.onCIMilesCardAdapterListener m_adapterListener = new CIMilesCardRecyclerViewAdapter.onCIMilesCardAdapterListener() {

        @Override
        public void onCardClick(CIAccumulationItem item) {
            //里程類型
            //1:座艙升等
            //2.酬賓機票
            //3.酬賓獎項轉讓

            //獎項類型
            //UP:升等（Upgrade Award)
            //TK:免票（Upgrage Ticket)
            //EB:超重行李（Excess Baggage）
            //VL:VIP（VIP Lounge Useage）
            //OTHER:其他（種類名稱，請參照award.desc）


            //將目前的畫面截圖下來, 當作背景
            Bitmap bitmap = ImageHandle.getScreenShot(getActivity());
            Bitmap blur = ImageHandle.BlurBuilder(getActivity(), bitmap, 13.5f, 0.15f);

            Bundle bundle = new Bundle();
            bundle.putParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG, blur);
            if ( item.m_strFlightItem.equals("3") ||
                    (!item.m_strFlightItem.equals("1") &&
                    !item.m_strFlightItem.equals("2"))){
                bundle.putString(UiMessageDef.BUNDLE_MILES_DETAIL_TYPE, AWARDS_TRANSFER.name());
            } else {
                bundle.putString(UiMessageDef.BUNDLE_MILES_DETAIL_TYPE, REDEEM.name());
            }
            bundle.putSerializable(UiMessageDef.BUNDLE_MILES_DETAIL_DATA, item);
            Intent intent = new Intent();
            intent.putExtras(bundle);
            intent.setClass(getActivity(), CIMilesDetailCardActivity.class);
            startActivity(intent);

            getActivity().overridePendingTransition(R.anim.anim_alpha_in, 0);

            bitmap.recycle();

        }

    };

    private RecyclerView m_rv = null;
    private CIMilesCardRecyclerViewAdapter m_adapter = null;
    private ArrayList<CIAccumulationGroupItem> m_alData = new ArrayList<>();

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_recycler_view;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        m_rv = (RecyclerView)view.findViewById(R.id.rv);
        m_rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        m_adapter = new CIMilesCardRecyclerViewAdapter(
                getActivity(),
                m_alData,
                CIMilesCardRecyclerViewAdapter.MilesCardType.REDEEM,
                m_adapterListener);

        m_rv.setAdapter(m_adapter);
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.setMargins(m_rv, 10, 0, 10, 0);
    }

    @Override
    protected void setOnParameterAndListener(View view) {}

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {}

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    public void onLanguageChangeUpdateUI() {}

//    @Override
//    public boolean uiOnBackPressed() {
//        if (m_PopupWindow != null){
//            if (m_PopupWindow.isShowing()){
//                m_PopupWindow.dismiss();
//                return true;
//            }else {
//                return super.uiOnBackPressed();
//            }
//        }else {
//            return super.uiOnBackPressed();
//        }
//    }

    public void ResetDataList(ArrayList<CIRedeemRecord_Info> arrayList){
        m_alData.clear();

        Set<String> setYear = new HashSet<>();

        if ( null == arrayList ){
            return;
        }

        for (int i = 0; i < arrayList.size(); i ++){

            //依照flight_date年份分組
            if ( null == arrayList.get(i).flight_date )
                arrayList.get(i).flight_date = "";

            String[] split = arrayList.get(i).flight_date.split("-");

            if ( false == setYear.contains(split[0]) ){
                m_alData.add(new CIAccumulationGroupItem(split[0]));
                setYear.add(split[0]);
            }

            //將resp傳回的值塞入自定義的item裡 以便adapter及popupwindow共用
            CIAccumulationItem item = new CIAccumulationItem();

            if ( null == arrayList.get(i).flight_item )
                arrayList.get(i).flight_item = "";
            item.m_strType = arrayList.get(i).flight_item;

            if ( null == arrayList.get(i).flight_desc )
                arrayList.get(i).flight_desc = "";
            item.m_strDescription = arrayList.get(i).flight_desc;

            if ( null == arrayList.get(i).flight_duedate )
                arrayList.get(i).flight_duedate = "";
            item.m_strExDate = arrayList.get(i).flight_duedate;

            if ( null == arrayList.get(i).flight_awdno )
                arrayList.get(i).flight_awdno = "";
            item.m_strAwardNo = arrayList.get(i).flight_awdno;

            item.m_strDate = arrayList.get(i).flight_date;

            if ( null == arrayList.get(i).flight_mileage )
                arrayList.get(i).flight_mileage = "";
            if ( 0 < arrayList.get(i).flight_mileage.length() )
                item.m_strMiles = "-"+arrayList.get(i).flight_mileage;

            if ( null == arrayList.get(i).flight_remark )
                arrayList.get(i).flight_remark = "";
            item.m_strRemark = arrayList.get(i).flight_remark;

            if ( null == arrayList.get(i).ticket )
                arrayList.get(i).ticket = "";
            item.m_strTicketNo = arrayList.get(i).ticket;

            if ( null == arrayList.get(i).nominee )
                arrayList.get(i).nominee = "";
            item.m_strNominee = arrayList.get(i).nominee;

            if ( null == arrayList.get(i).card_no )
                arrayList.get(i).card_no = "";
            item.m_strCardNo = arrayList.get(i).card_no;

            if ( null == arrayList.get(i).bnsusg )
                arrayList.get(i).bnsusg = "";
            item.m_strBnsusg = arrayList.get(i).bnsusg;

            //flight_item等於 3 時就顯示轉讓
            if ( arrayList.get(i).flight_item.equals("3")){
                item.m_bTransfer = true;
            }else {
                item.m_bTransfer = false;
            }

            item.m_strFlightItem = arrayList.get(i).flight_item;

            //年份一樣的放同一個arraylist
            for (int j = 0; j < m_alData.size(); j ++){
                if (split[0].equals(m_alData.get(j).GetYear())){
                    m_alData.get(j).addInfo(item);
                }
            }
        }
        if (null != m_adapter) {
            m_adapter.notifyDataSetChanged();
        }
    }
}
