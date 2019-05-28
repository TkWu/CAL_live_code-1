package ci.ui.view.ShadowBar;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * {@link LastItemRecyclerOnScrollListener }
 * 使用此 Listener 可以偵測畫面RecyclerView是否已經滑到底,
 * 可以用來製作 LoadMore, 或者是 陰影的效果偵測,
 * 要參考陰影效果使用實例請看{@link ShadowBarRecycleView}
 * Created by Ryan on 16/3/17.
 */
public abstract class LastItemRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = LastItemRecyclerOnScrollListener.class.getSimpleName();

    int firstVisibleItem, visibleItemCount, totalItemCount;

    private LinearLayoutManager mLinearLayoutManager;

    public LastItemRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        int iLastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
        int iLastComp = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();


        int top =0, bottom = 0, h =0;
//        View view = recyclerView.getChildAt(firstVisibleItem);
//        if ( null != view ){
//            bottom = view.getBottom();
//            top = view.getTop();
//            if ( null != view.getRootView() ){
//                h = view.getRootView().getHeight();
//            }
//        }
//
//        String tag = String.format("VCnt=%d, TCnt=%d, First=%d, Top=%d, bottom=%d,ParentH=%d", visibleItemCount, totalItemCount, firstVisibleItem, top, bottom, h);
//        Log.d("[CAL]", tag);

        if ( (visibleItemCount + firstVisibleItem) == totalItemCount && iLastComp == (totalItemCount-1) ){
            onLastItemShow(true);

//            int test1[] = new int[2];
//
//            int LastItemPos = totalItemCount-1;
//            View view = recyclerView.getChildAt(iLastComp);
//            if ( null != view ){
//                top = view.getTop();
//                bottom = view.getBottom();
//
//                view.getLocationOnScreen(test1);
//            }
//
//            String tag = String.format("LastItem Show Pos=%d, Last=%d, iLastComp=%d", LastItemPos, iLastVisibleItem, iLastComp);
//            Log.d("[CAL]", tag);

        } else {
            onLastItemShow(false);
        }
    }

    public abstract void onLastItemShow(Boolean bShow );
}
