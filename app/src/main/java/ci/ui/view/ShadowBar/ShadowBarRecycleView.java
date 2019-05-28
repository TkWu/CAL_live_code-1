package ci.ui.view.ShadowBar;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseView;
import ci.ui.define.ViewScaleDef;

/**
 * Created by ryan on 16/3/17.
 */
public class ShadowBarRecycleView extends BaseView {

    private RelativeLayout      m_root                  = null;
    private RecyclerView        m_RecyclerView          = null;
    private View                m_vGradient             = null;
    private LinearLayoutManager m_linearLayoutManager   = null;

    public ShadowBarRecycleView(Context context) {
        super(context);
        initial();
    }

    public ShadowBarRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initial();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_lastitem_recycleview;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater) {

        m_root          = (RelativeLayout)findViewById(R.id.root);
        m_RecyclerView  = (RecyclerView)findViewById(R.id.recycler_view);
        m_vGradient     = findViewById(R.id.vGradient);

        m_linearLayoutManager = new LinearLayoutManager(getContext());
        m_RecyclerView.setLayoutManager(m_linearLayoutManager);
//        m_RecyclerView.setOnScrollListener(new LastItemRecyclerOnScrollListener(m_linearLayoutManager){
//
//            @Override
//            public void onLastItemShow(Boolean bShow) {
//                if ( bShow ){
//                    m_vGradient.setVisibility(View.GONE);
//                } else {
//                    m_vGradient.setVisibility(View.VISIBLE);
//                }
//            }
//        });
        m_RecyclerView.getViewTreeObserver().addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener(){

            @Override
            public void onGlobalLayout() {
                m_RecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int visibleItemCount = m_RecyclerView.getChildCount();
                int totalItemCount = m_linearLayoutManager.getItemCount();
                int firstVisibleItem = m_linearLayoutManager.findFirstVisibleItemPosition();
                int iLastComp = m_linearLayoutManager.findLastCompletelyVisibleItemPosition();

                if ( (visibleItemCount + firstVisibleItem) == totalItemCount && iLastComp == (totalItemCount-1) ){
                    //onLastItemShow(true);
                    m_vGradient.setVisibility(View.GONE);
                } else {
                    //onLastItemShow(false);
                    m_vGradient.setVisibility(View.VISIBLE);
                }

            }
        });

        m_RecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int visibleItemCount = recyclerView.getChildCount();
                int totalItemCount = m_linearLayoutManager.getItemCount();
                int firstVisibleItem = m_linearLayoutManager.findFirstVisibleItemPosition();
                int iLastComp = m_linearLayoutManager.findLastCompletelyVisibleItemPosition();

                if ( (visibleItemCount + firstVisibleItem) == totalItemCount && iLastComp == (totalItemCount-1) ){
                    //onLastItemShow(true);
                    m_vGradient.setVisibility(View.GONE);
                } else {
                    //onLastItemShow(false);
                    m_vGradient.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        m_vGradient.getLayoutParams().height = vScaleDef.getLayoutHeight(16);
    }

    public RecyclerView getRecyclerView(){
        return m_RecyclerView;
    }

    public LinearLayoutManager getLinearLayoutManager(){
        return m_linearLayoutManager;
    }

    public void setShadowBarHeight( int iPx ){
        m_vGradient.getLayoutParams().height = iPx;
    }
}
