package ci.function.ManageMiles;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;
import com.google.gson.Gson;

import ci.function.Base.BaseFragment;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/** 首頁上補登完成通知欄
 * Created by kevincheng on 2016/3/14.
 */
public class CIReclaimMilesUpdateCompleteNoticeFragment extends BaseFragment
    implements View.OnClickListener{

    //2016/04/07 從通知跳轉至里程管理頁面時 需顯示切換動畫 此工作統一交由main處理 - by Ling
    public interface OnMilesUpdateNoticeFragmentListener {
        //前往里程管理頁面
        void OnMilesManageClick();
    }

    private OnMilesUpdateNoticeFragmentListener m_Listener;

    private Entity   m_data     = null;

    public static CIReclaimMilesUpdateCompleteNoticeFragment newInstance(Entity data) {

        Bundle args = new Bundle();
        String json = new Gson().toJson(data);
        args.putString(UiMessageDef.BUNDLE_FRAGMENT_DATA,json);
        CIReclaimMilesUpdateCompleteNoticeFragment fragment = new CIReclaimMilesUpdateCompleteNoticeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(null != args){
            String json = args.getString(UiMessageDef.BUNDLE_FRAGMENT_DATA);
            m_data = new Gson().fromJson(json,Entity.class);
        }
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        if(null != m_data){
            String content
                    = String.format(getActivity().getString(R.string.miles_have_been_imported_to_your_account),
                    m_data.name, m_data.first, m_data.second, m_data.miles);
            ((TextView)view.findViewById(R.id.tv_down)).setText(content);
        }
    }

    @Override
    protected void setOnParameterAndListener(View view) {
        view.findViewById(R.id.btn_my_miles).setOnClickListener(this);
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

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_reclaim_miles_update_notice;
    }



    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        //背景
        vScaleDef.setViewSize(view.findViewById(R.id.root), 360, 80);

        //文字區塊
        vScaleDef.setMargins(view.findViewById(R.id.ll_text), 20, 0, 0, 0);
        vScaleDef.setViewSize(view.findViewById(R.id.tv_up), 228, WRAP_CONTENT);
        vScaleDef.setTextSize(16, ((TextView) view.findViewById(R.id.tv_up)));
        vScaleDef.setViewSize(view.findViewById(R.id.tv_up), 228, WRAP_CONTENT);
        vScaleDef.setTextSize(14, ((TextView) view.findViewById(R.id.tv_down)));
        vScaleDef.setMargins(view.findViewById(R.id.tv_down), 0, 6, 0, 0);

        ((TextView) view.findViewById(R.id.tv_down)).setLineSpacing(3.0f,1.0f);

        //右邊按鈕
        vScaleDef.setMargins(view.findViewById(R.id.rl_btn),20,0,0,0);
        vScaleDef.setViewSize(view.findViewById(R.id.btn_my_miles), 72, 32);
        vScaleDef.setTextSize(13, ((Button) view.findViewById(R.id.btn_my_miles)));
    }

    @Override
    public void onClick(View v) {
        if (null != m_Listener){
            m_Listener.OnMilesManageClick();
            getFragmentManager().beginTransaction().remove(this).commit();
        }
    }

    public static class Entity{
        public String name;
        public String first;
        public String second;
        public String miles;
    }

    public void uiSetParameterListener(OnMilesUpdateNoticeFragmentListener onListener) {
        m_Listener = onListener;
    }
}
