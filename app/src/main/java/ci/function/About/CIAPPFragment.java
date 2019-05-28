package ci.function.About;

import android.graphics.Bitmap;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.About.Adapter.CIAPPAdapter;
import ci.function.About.item.CIAPPItem;
import ci.function.Base.BaseFragment;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;

/**
 * Created by kevincheng on 2016/4/11.
 */
public class CIAPPFragment extends BaseFragment {
    private RecyclerView m_recyclerView = null;
    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_china_airlines_app;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        m_recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        m_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        CIAPPAdapter adapter = new CIAPPAdapter(getActivity(), getDatas());
        m_recyclerView.setMotionEventSplittingEnabled(false);
        m_recyclerView.setAdapter(adapter);
    }

    private ArrayList<CIAPPItem> getDatas(){
        ArrayList<CIAPPItem> datas = new ArrayList<>();
        Bitmap bmpEshopping = ImageHandle.getLocalBitmap(getActivity(),R.drawable.ic_eshopping_app_icon,1);
        Bitmap bmpBookCity     = ImageHandle.getLocalBitmap(getActivity(),R.drawable.ci_book_city,1);
        datas.add(new CIAPPItem(getString(R.string.app_eshopping_name),
                getString(R.string.eshopping_packagename),
                getString(R.string.app_eshopping_desc),
                ImageHandle.BitmapToByteArray(bmpEshopping)));
        datas.add(new CIAPPItem(getString(R.string.app_exquisitetravel_name),
                getString(R.string.exquisitetravel_packagename),
                getString(R.string.app_exquisitetravel_desc),
                ImageHandle.BitmapToByteArray(bmpBookCity)));
        return datas;
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {

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
