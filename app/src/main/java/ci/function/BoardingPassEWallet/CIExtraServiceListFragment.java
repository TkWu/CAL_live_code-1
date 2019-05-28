package ci.function.BoardingPassEWallet;

import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.Base.BaseFragment;
import ci.function.BoardingPassEWallet.Adapter.CIExtraServiceRecyclerViewAdapter;
import ci.function.BoardingPassEWallet.item.CIExtraServiceItem;
import ci.function.Main.CIMainActivity;
import ci.function.Main.item.SideMenuItem;
import ci.ui.define.ViewIdDef;
import ci.ui.define.ViewScaleDef;

import static ci.function.BoardingPassEWallet.Adapter.CIExtraServiceRecyclerViewAdapter.onRecyclerViewAdapterListener;

/**
 * Created by kevincheng on 2016/3/25.
 */
public class CIExtraServiceListFragment extends BaseFragment
    implements onRecyclerViewAdapterListener{

    public interface CIExtraServiceListInterface{
        void ResetList();
    }

    public interface CIExtraServiceListParameter{
        ArrayList<CIExtraServiceItem> GetListData();
    }

    private CIExtraServiceListInterface m_Interface = new CIExtraServiceListInterface(){

        @Override
        public void ResetList() {
            if ( null != m_Parameter ){
                m_arExtraServiceData = m_Parameter.GetListData();
            }

            if ( null == m_recyclerView ){
                return;
            }

            m_adapter = new CIExtraServiceRecyclerViewAdapter(
                    getActivity(),
                    m_arExtraServiceData,
                    CIExtraServiceListFragment.this);
            m_recyclerView.setAdapter(m_adapter);
        }
    };

    private CIExtraServiceListParameter         m_Parameter         = null;

    private RecyclerView                        m_recyclerView      = null;
    private CIExtraServiceRecyclerViewAdapter   m_adapter           = null;
//    private ArrayList<CIEWallet_ExtraService_Info> m_arExtraServiceData = new ArrayList<>();
    private ArrayList<CIExtraServiceItem>       m_arExtraServiceData = new ArrayList<>();

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_boarding_pass_ewallet_all_tab_content;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        m_recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        m_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        m_adapter = new CIExtraServiceRecyclerViewAdapter(
                getActivity(),
                m_arExtraServiceData,
                this);

        m_recyclerView.setAdapter(m_adapter);
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(view.findViewById(R.id.root));
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

    //跳轉至其他服務fragment
    @Override
    public void OnGotoExtraServicesFragmentClick() {
        ((CIMainActivity)getActivity()).m_bShowAnim = true;

        SideMenuItem sideMenuItem = ViewIdDef.getInstance(getActivity()).getRightMenuList().get(1).get(0);
        ((CIMainActivity)getActivity()).SelectSideMenu(sideMenuItem);
    }

    public CIExtraServiceListInterface uiSetParameterListener(
            CIExtraServiceListParameter onParameter) {

        m_Parameter = onParameter;

        return m_Interface;
    }
}
