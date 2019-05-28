package ci.function.BoardingPassEWallet;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import ci.function.Base.BaseFragment;
import ci.function.BoardingPassEWallet.Adapter.CICouponRecyclerViewAdapter;
import ci.function.BoardingPassEWallet.Coupon.CICouponCardActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;
import ci.ws.Models.entities.CIInquiryCoupon_Info;

import static ci.function.BoardingPassEWallet.Adapter.CICouponRecyclerViewAdapter.onCouponRecyclerViewListener;

/**
 * 目前使用在登機證/票券夾, 用來顯示優惠券, 可根據Type來決定要顯示的版型
 * Created by Kevin on 2016/3/10.
 */
public class CICouponListFragment extends BaseFragment {

    public interface CICouponListInterface{
        void ResetList(ArrayList<CIInquiryCoupon_Info> data);
    }

    private CICouponListInterface m_Interface = new CICouponListInterface(){

        @Override
        public void ResetList(ArrayList<CIInquiryCoupon_Info> data) {
            m_alData = data;

            if ( null == m_recyclerView ){
                return;
            }

            m_adapter = new CICouponRecyclerViewAdapter(
                    getActivity(),
                    m_alData,
                    m_AdapterListener);
            m_recyclerView.setAdapter(m_adapter);
        }
    };

    private onCouponRecyclerViewListener m_AdapterListener = new onCouponRecyclerViewListener() {
        @Override
        public void onItemClick(CIInquiryCoupon_Info item) {

            CICouponCardActivity activity = new CICouponCardActivity();
            activity.setCouponInfo(item);

            Bitmap bitmap = ImageHandle.getScreenShot(getActivity());
            Bitmap blur   = ImageHandle.BlurBuilder(getActivity(), bitmap, 13.5f, 0.15f);

            //壓縮blur bitmap 避免佔用過多記憶體
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            blur.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytes = stream.toByteArray();

//            Bundle bundle = new Bundle();
//            bundle.putParcelable(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG, blur);
//            bundle.putSerializable(UiMessageDef.BUNDLE_EWALLET_COUPON_DATA, item);
            Intent intent = new Intent();
            intent.putExtra(UiMessageDef.BUNDLE_BACKGROUND_BITMAP_TAG, bytes);
//            intent.putExtras(bundle);
            intent.setClass(getActivity(), activity.getClass());
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.anim_alpha_in, 0);

            blur.recycle();
            bitmap.recycle();
            System.gc();
        }
    };

    private RelativeLayout              m_rlRoot            = null;
    private RecyclerView                m_recyclerView      = null;
    private CICouponRecyclerViewAdapter m_adapter           = null;

    private ArrayList<CIInquiryCoupon_Info> m_alData        = new ArrayList<>();

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_boarding_pass_ewallet_all_tab_content;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        m_rlRoot = (RelativeLayout) view.findViewById(R.id.root);
        m_recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        m_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        m_adapter = new CICouponRecyclerViewAdapter(
                getActivity(),
                m_alData,
                m_AdapterListener);

        m_recyclerView.setAdapter(m_adapter);
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(view);
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

    public CICouponListInterface uiSetParameterListener() {
        return m_Interface;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if ( null != m_rlRoot ){
            m_rlRoot.removeAllViews();
        }
        m_rlRoot = null;
        m_recyclerView = null;
    }
}
