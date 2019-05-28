package ci.ui.view.ShadowBar;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by kevincheng on 2017/5/11.
 */

public class ShadowBarLogic {

    public static void switchViewGradientByLastItemOnRecyclerView(RecyclerView recyclerView,
                                                            View vGradient,
                                                            LinearLayoutManager linearLayoutManager){
        if (isLastItemForRecyclerView(recyclerView, linearLayoutManager)){
            vGradient.setVisibility(View.GONE);
        } else {
            vGradient.setVisibility(View.VISIBLE);
        }
    }

    private static boolean isLastItemForRecyclerView(RecyclerView recyclerView ,LinearLayoutManager linearLayoutManager){
        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = linearLayoutManager.getItemCount();
        int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
        int iLastComp = linearLayoutManager.findLastCompletelyVisibleItemPosition();
        return (visibleItemCount + firstVisibleItem) == totalItemCount && iLastComp == (totalItemCount-1);
    }
}
