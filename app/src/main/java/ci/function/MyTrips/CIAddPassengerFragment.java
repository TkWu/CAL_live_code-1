package ci.function.MyTrips;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import ci.function.Core.SLog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ci.function.Base.BaseFragment;
import ci.ui.TextField.Base.CITextFieldFragment.TypeMode;
import ci.ui.TextField.CICustomTextFieldFragment;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.function.Checkin.PassengersItem;
import ci.ui.toast.CIToastView;
import ci.ui.view.ShadowBar.ShadowBarScrollview;

/**
 * Created by kevin on 2016/2/28.
 */
public class CIAddPassengerFragment extends BaseFragment
    implements  View.OnClickListener,
                View.OnTouchListener,
                CIAddPassengerInputFragment.onFragmentDeletedListener {

    public final static String              NAME                            = "NAME";
    public final static String              ITEM                            = "ITEM";
    public final static int                 DEF_RESET_NO                    = 2;
    private             int                 m_iFragmentCount                = 0;
    private             LinearLayout        m_contentView                   = null;
    private             EMode               m_mode                          = EMode.SINGLE;
    private             int                 m_iMax                          = 0;
    private             RelativeLayout      m_rlFinish                      = null;
    private             LinearLayout        m_llAdd                         = null;
    private             ShadowBarScrollview m_scrollView                    = null;
    private CICustomTextFieldFragment       m_fullNameFragment              = null;
    private             ArrayList<Integer>  m_alViewId                      = null;
    private             ArrayList<CIAddPassengerInputFragment> m_alFragment = null;

    /**
         * SINGLE   只能加入一個同伴(預設)
         * MORE     能加入最多九個同伴
         */
        public enum EMode {
            SINGLE, MORE
        }

        /**
         * 代入模式參數建構一個實例
         * @param mode 模式
         * @return CIAddPassengerFragment
         */
        public static CIAddPassengerFragment newInstance(EMode mode) {
            Bundle bundle = new Bundle();
            bundle.putString(UiMessageDef.BUNDLE_FRAGMENT_MODE, mode.name());
            CIAddPassengerFragment fragment = new CIAddPassengerFragment();
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle bundle = getArguments();
            if (null != bundle) {
                m_mode = EMode.valueOf(bundle.getString(UiMessageDef.BUNDLE_FRAGMENT_MODE));
            }
            /**最多可新增同伴的數量*/
            switch (m_mode) {
                case SINGLE:
                    m_iMax = 1;
                    break;
                case MORE:
                    m_iMax = 9;
                    break;
            }
        }

        /**
         * BaseFragment在
         * {@link BaseFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)
         * onCreateView()}時 設定{@link LayoutInflater#inflate(int, ViewGroup, boolean)
         * inflate()}用
         *
         * @return 此畫面的 Layout Resource Id
         */
        @Override
        protected int getLayoutResourceId() {
            return R.layout.fragment_add_passenger;
        }

        /**
         * 元件初始化，靜態取得元件實體、動態建製元件實體…等
         *
         * @param inflater
         * @param view
         */
        @Override
        protected void initialLayoutComponent(LayoutInflater inflater, View view) {
            m_alViewId = new ArrayList<>();
            m_alFragment = new ArrayList<>();
            m_scrollView = (ShadowBarScrollview)view.findViewById(R.id.scrollview);
            inflater.inflate(R.layout.layout_add_passenger_contain, m_scrollView.getContentView());
            m_rlFinish = (RelativeLayout) view.findViewById(R.id.rl_finish);
            String head = String.format(getString(R.string.add_passenager_passenager_x), getCount());
            m_contentView = ((LinearLayout) view.findViewById(R.id.ll_content));
            m_llAdd = (LinearLayout) view.findViewById(R.id.ll_add);
            ((TextView) view.findViewById(R.id.tv_add_head)).setText(head);
        }

        /**
         * 設定字型大小及版面大小
         *
         * @param view
     * @param vScaleDef 請參閱{@link ViewScaleDef}
     */
    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(view.findViewById(R.id.root));
        vScaleDef.selfAdjustSameScaleView(view.findViewById(R.id.img_add), 24, 24);
        m_scrollView.setShadowBarHeight(vScaleDef.getLayoutHeight(16));
    }

    /**
     * 通知父BaseActivity對子BaseFragment設定客製的「OnParameter(參數讀取)」跟「OnListener(
     * 動線通知)」介面
     *
     * @param view
     */
    @Override
    protected void setOnParameterAndListener(View view) {
        m_llAdd.setOnClickListener(this);
        view.findViewById(R.id.btn_finish).setOnClickListener(this);
        view.findViewById(R.id.root).setOnTouchListener(this);

    }

    /**
     * 依需求調用以下函式
     *
     * @param fragmentManager
     * @see FragmentTransaction FragmentTransaction相關操作
     */
    @Override
    protected void registerFragment(FragmentManager fragmentManager) {
        m_fullNameFragment = CICustomTextFieldFragment.newInstance("",TypeMode.ONLY_DISPLAY);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment1, m_fullNameFragment, NAME + m_iFragmentCount);
        transaction.commitAllowingStateLoss();
        addPassengerTextFeild();
    }

    @Override
    public void onResume() {
        super.onResume();
        m_fullNameFragment.setText("YuRong Cheng");
    }

    /**
     * 若收到Handle Message且BaseActivity不認得時，
     * 視為子class自訂Message，可經由此Function接收通知。
     *
     * @param msg
     * @return true：代表此Message已處理<p> false：代表此Message連子class也不認得<p>
     */
    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    /**
     * 若子class有自訂Message，請經由此Function清空Message。
     */
    @Override
    protected void removeOtherHandleMessage() {

    }

    /**
     * 當App語言變更後, 會呼叫此介面，藉此更新畫面UI,需要重新呼叫setText
     */
    @Override
    public void onLanguageChangeUpdateUI() {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.ll_add:
                addPassengerTextFeild();
                break;
            case R.id.btn_finish:
                if(false == isFillComplete()){
                    CIToastView.makeText(getContext(),getString(R.string.please_fill_all_text_field_that_must_to_fill)).show();
                    return;
                }
                List<PassengersItem> items = getData();
                Intent data = new Intent();
                if(EMode.SINGLE == m_mode){
                        data.putExtra(NAME,items.get(0).strName);
                        getActivity().setResult(getActivity().RESULT_OK, data);
                        getActivity().finish();

                } else {
                    getActivity().setResult(getActivity().RESULT_OK);
                    getActivity().finish();
                }
                break;
        }

    }

    private int getCount(){
        m_iFragmentCount++;
        return m_iFragmentCount;
    }

    private void addPassengerTextFeild(){
        m_iFragmentCount =  getCount();
        int viewID = getViewId();
        //初始化frameLayout
        final FrameLayout layout = new FrameLayout(getActivity());
//        final ViewScaleDef viewScaleDef = ViewScaleDef.getInstance(getActivity());
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);
        layout.setId(viewID);
        m_contentView.addView(layout);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        final CIAddPassengerInputFragment fragment = new CIAddPassengerInputFragment();
        fragment.setOnFragmentDeletedListener(this);
        m_alFragment.add(fragment);
        transaction.replace(viewID, fragment, String.valueOf(viewID)).commit();


        m_contentView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(false == isVisible()){
                        return;
                    }
                    m_scrollView.getScrollView().fullScroll(ScrollView.FOCUS_DOWN);
                    setFragmentConfig(m_iFragmentCount, fragment);
                    setTextFeildActionDone();
                }
            },200);


        //達到最大值就隱藏增加按鈕
        if(m_iMax <= m_iFragmentCount){
            m_llAdd.setVisibility(View.GONE);
        }
    }

        /**
         * 對View Id 做資源管理，避免使用到整數的極限值造成異常
         * @return view id
         */
        private int getViewId(){
            int iViewId = 11;
            while(true){
                if(!m_alViewId.contains(iViewId)){
                    break;
                }
                iViewId++;
            }
            m_alViewId.add(iViewId);
           SLog.d("viewId", iViewId + "");
            return iViewId;
        }

        /**
         * 釋放加入管理的view id，可再次被利用
         * @return view id
         */
        private void releaseViewId(Integer viewId){
            m_alViewId.remove(viewId);
        }

        private void setFragmentConfig(int count, CIAddPassengerInputFragment fragment){
            fragment.setNumber(count);
            if(1 < count){
                fragment.setGarbageIconVisible(View.VISIBLE);
            } else {
                fragment.setGarbageIconVisible(View.GONE);
            }

        }

        @Override
        public void onFragmentDeleted(Fragment deletedfragment) {
            getChildFragmentManager().beginTransaction().remove(deletedfragment).commit();
            releaseViewId(deletedfragment.getId());
            m_contentView.removeView(m_contentView.findViewById(deletedfragment.getId()));
            m_alFragment.remove(deletedfragment);
            int count = DEF_RESET_NO;
            for (CIAddPassengerInputFragment fragment : m_alFragment) {
                fragment.setNumber(count);
                count++;
            }
            setTextFeildActionDone();
            //減少一個Fragment數量
            m_iFragmentCount--;
            //判斷是否顯示新增按鈕文字
            if(m_iMax > m_iFragmentCount){
                m_llAdd.setVisibility(View.VISIBLE);
            }
        }

    private void setTextFeildActionDone(){
        int size = m_alFragment.size();
        int count = 1;
        for (CIAddPassengerInputFragment fragment : m_alFragment) {
            if (size == count) {
                fragment.setImeOption(EditorInfo.IME_ACTION_DONE);
            } else {
                fragment.setImeOption(EditorInfo.IME_ACTION_NEXT);
            }
            count++;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        HidekeyBoard();
        return false;
    }

    /**
     * 轉換Activity
     * @param clazz 目標activity名稱
     * @param key   extra key
     * @param extra extra value
     */
    private void changeActivity(Class clazz,String key,String extra){
        Intent intent = new Intent();
        intent.putExtra(key, extra);
        intent.setClass(getActivity(), clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    private List<PassengersItem> getData(){
        List<PassengersItem> items = new LinkedList<>();
        for(CIAddPassengerInputFragment fragment:m_alFragment){
            PassengersItem item = new PassengersItem();
            item.strName = fragment.getFirstName() + fragment.getLastName();
            items.add(item);
        }
        return items;
    }

    private boolean isFillComplete(){
        for(CIAddPassengerInputFragment fragment : m_alFragment){
            if(true == TextUtils.isEmpty(fragment.getFirstName())
                || true == TextUtils.isEmpty(fragment.getLastName())){
                return false;
            }
        }
        return true;
    }

}
