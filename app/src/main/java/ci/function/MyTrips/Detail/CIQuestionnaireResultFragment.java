package ci.function.MyTrips.Detail;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.function.Main.item.CIQuestionItem;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ShadowBar.ShadowBarLogic;
import ci.ws.cores.object.GsonTool;

/**
 * 問卷調查輸入結果頁面
 * zeplin: China Airlines_2 10.7-4
 * Created by kevincheng on 2017/5/9.
 */

public class CIQuestionnaireResultFragment extends BaseFragment{

    private TextView                        m_tvTitle                = null;
    private CIQuestionnaireResultAdapter    m_adapter                = null;
    private RecyclerView                    m_recyclerView           = null;
    private View                            m_vGradient              = null;
    private Context                         m_applicationContext     = CIApplication.getContext();
    private LinearLayoutManager             m_linearLayoutManager    = null;

    public static CIQuestionnaireResultFragment getInstance(String strJson){
        Bundle bundle = new Bundle();
        bundle.putString(UiMessageDef.BUNDLE_QUESTIONNAIRE_DATA, strJson);
        CIQuestionnaireResultFragment fragment = new CIQuestionnaireResultFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_questionnaire_result_card;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        m_tvTitle       = (TextView) view.findViewById(R.id.question_title);
        m_recyclerView  = (RecyclerView)view.findViewById(R.id.recycler_view);
        m_vGradient     = view.findViewById(R.id.vGradient);

        Bundle bundle = getArguments();
        if(null == bundle){
            return;
        }

        String strJosn = bundle.getString(UiMessageDef.BUNDLE_QUESTIONNAIRE_DATA,"");
        CIQuestionItem item = GsonTool.getGson().fromJson(strJosn, CIQuestionItem.class);

        m_tvTitle.setText(getString(R.string.trips_detail_anwser_title));
        m_adapter       = new CIQuestionnaireResultAdapter(item);
        m_linearLayoutManager = new LinearLayoutManager(m_applicationContext);
        m_recyclerView.setLayoutManager(m_linearLayoutManager);
        m_recyclerView.setAdapter(m_adapter);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ShadowBarLogic.switchViewGradientByLastItemOnRecyclerView(m_recyclerView,
                                                                          m_vGradient,
                                                                          m_linearLayoutManager);
            }
        });
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(view.findViewById(R.id.root));
    }

    @Override
    protected void setOnParameterAndListener(View view) {

        m_recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                ShadowBarLogic.switchViewGradientByLastItemOnRecyclerView(recyclerView,
                                                                          m_vGradient,
                                                                          m_linearLayoutManager);
            }
        });

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
