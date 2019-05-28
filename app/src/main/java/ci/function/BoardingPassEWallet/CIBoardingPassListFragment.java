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
import ci.function.BoardingPassEWallet.Adapter.CIBoardingPassRecyclerViewAdapter;
import ci.ui.define.ViewScaleDef;
import ci.ws.Models.entities.CIBoardPassResp_Itinerary;

/**
 * Created by kevincheng on 2016/3/25.
 */
public class CIBoardingPassListFragment extends BaseFragment {

    public interface CIBoardingPassListInterface{
        void ResetList();
    }

    public interface CIBoardingPassListParameter{
        ArrayList<CIBoardPassResp_Itinerary> GetListData();
    }

    public interface CIBoardingPassListListener{
        void loadData();
    }

    private CIBoardingPassListInterface     m_Interface = new CIBoardingPassListInterface() {
        @Override
        public void ResetList() {

            if ( null != m_Parameter ){
                m_alBoardPass_Itinerary = m_Parameter.GetListData();
            }

            m_adapter = new CIBoardingPassRecyclerViewAdapter(
                    getActivity(),
                    m_alBoardPass_Itinerary );
            m_recyclerView.setAdapter(m_adapter);
        }
    };

    private CIBoardingPassListParameter     m_Parameter         = null;
    private CIBoardingPassListListener      m_Listener          = null;

    private RecyclerView                    m_recyclerView      = null;
    private CIBoardingPassRecyclerViewAdapter m_adapter         = null;
    private ArrayList<CIBoardPassResp_Itinerary> m_alBoardPass_Itinerary = new ArrayList<>();

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_boarding_pass_ewallet_all_tab_content;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        if ( null != m_Parameter ){
            m_alBoardPass_Itinerary = m_Parameter.GetListData();
        }

        m_recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        m_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        m_adapter = new CIBoardingPassRecyclerViewAdapter(
                getActivity(),
                m_alBoardPass_Itinerary);
        m_recyclerView.setAdapter(m_adapter);

        m_FragmentHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if ( null != m_Listener)
                    m_Listener.loadData();
            }
        }, 500);
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

    public CIBoardingPassListInterface uiSetParameterListener(
            CIBoardingPassListParameter onParameter,
            CIBoardingPassListListener onListener ) {
        m_Parameter = onParameter;
        m_Listener  = onListener;

        return m_Interface;
    }
}
