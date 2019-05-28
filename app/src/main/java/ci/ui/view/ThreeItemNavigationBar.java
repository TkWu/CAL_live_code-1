package ci.ui.view;

import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Base.BaseFragment;
import ci.ui.define.ViewScaleDef;

/**
 * Created by kevin on 2016/2/25.
 */
public class ThreeItemNavigationBar extends BaseFragment
    implements View.OnClickListener{

    private TextView m_tvLeftItem               = null,
                     m_tvRightItem              = null,
                     m_tvMiddleItem             = null;
    private RelativeLayout m_rlLeftBg           = null,
                           m_rlRightBg          = null,
                           m_rlMiddleBg         = null;
    private String         m_strLeftText        = null,
                           m_strRightText       = null,
                           m_strMiddleText      = null;
    private EInitItem m_initItem           = null;
    private static final String   LEFT_ITEM      = "LEFT_ITEM",
                                  RIGHT_ITEM     = "RIGHT_ITEM",
                                  MIDDLE_ITEM    = "MIDDLE_ITEM",
                                  INIT_ITEM      = "INIT_ITEM";
    private ItemClickListener     m_listener     = null;


    public enum EInitItem {
        LEFT,MIDDLE,RIGHT
    }
    /**
     * 使用方法：
     * v.getId()判斷item被點擊
     * R.id.rl_left_bg  點擊左邊
     * R.id.rl_right_bg 點擊右邊
     * R.id.rl_middle_bg 點擊右邊
     * */
    public interface ItemClickListener{
        void onItemClick(View v);
    }


    /**
     * 創建新的雙項導覽欄物件
     * @param left   左邊項目文字
     * @param right  右邊項目文字
     * @param middle 中間項目文字
     * @param init   初始化item style
     * @return       TwoItemNavigationBar物件
     */
    public static ThreeItemNavigationBar newInstance(String left,String middle,String right,EInitItem init) {
        Bundle bundle = new Bundle();
        bundle.putString(LEFT_ITEM, left);
        bundle.putString(RIGHT_ITEM, right);
        bundle.putString(MIDDLE_ITEM, middle);
        bundle.putString(INIT_ITEM, init.name());
        ThreeItemNavigationBar fragment = new ThreeItemNavigationBar();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (null != bundle) {
            m_strLeftText   = bundle.getString(LEFT_ITEM, "");
            m_strMiddleText   = bundle.getString(MIDDLE_ITEM, "");
            m_strRightText  = bundle.getString(RIGHT_ITEM, "");
            m_initItem      = EInitItem.valueOf(bundle.getString(INIT_ITEM, EInitItem.LEFT.name()));
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_three_item_navigate_bar;
    }

    /**
     * 元件初始化，靜態取得元件實體、動態建製元件實體…等
     *
     * @param inflater
     * @param view
     */
    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        m_tvLeftItem    = (TextView)view.findViewById(R.id.tv_left_item);
        m_tvMiddleItem  = (TextView)view.findViewById(R.id.tv_middle_item);
        m_tvRightItem   = (TextView)view.findViewById(R.id.tv_right_item);
        m_rlLeftBg      = (RelativeLayout)view.findViewById(R.id.rl_left_bg);
        m_rlMiddleBg    = (RelativeLayout)view.findViewById(R.id.rl_middle_bg);
        m_rlRightBg     = (RelativeLayout)view.findViewById(R.id.rl_right_bg);
        if(null != m_strLeftText){
            m_tvLeftItem.setText(m_strLeftText);
        }
        if(null != m_strRightText){
            m_tvRightItem.setText(m_strRightText);
        }
        if(null != m_strMiddleText){
            m_tvMiddleItem.setText(m_strMiddleText);
        }
        //init
        switch(m_initItem){
            case LEFT:
                switchItemStyle(m_rlLeftBg);
                break;
            case MIDDLE:
                switchItemStyle(m_rlMiddleBg);
                break;
            case RIGHT:
                switchItemStyle(m_rlRightBg);
                break;
        }

    }

    /**
     * 設定字型大小及版面大小
     *
     * @param view
     * @param vScaleDef 請參閱{@link ViewScaleDef}
     */
    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(view);
    }

    /**
     * 通知父BaseActivity對子BaseFragment設定客製的「OnParameter(參數讀取)」跟「OnListener(
     * 動線通知)」介面
     *
     * @param view
     */
    @Override
    protected void setOnParameterAndListener(View view) {
        m_rlLeftBg.setOnClickListener(this);
        m_rlMiddleBg.setOnClickListener(this);
        m_rlRightBg.setOnClickListener(this);
    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

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

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switchItemStyle(v);
        if(null != m_listener){
            m_listener.onItemClick(v);
        }
    }

    /**
     * 設置左右及中間 item style
     * @param v
     */
    private void switchItemStyle(View v){
        switch (v.getId()){
            case R.id.rl_left_bg:
                setStyle(R.color.french_blue,
                         R.color.white_four,
                         R.color.white_four,
                         R.drawable.bg_btn_left_radius_white,
                         R.color.french_blue_40,
                         R.drawable.bg_btn_right_radius_french_blue_40);
                //寫入當下狀態, 讓外界取用 2016/03/18 ryan
                m_initItem = EInitItem.LEFT;
                break;
            case R.id.rl_middle_bg:
                setStyle(R.color.white_four,
                        R.color.french_blue,
                        R.color.white_four,
                        R.drawable.bg_btn_left_radius_french_blue_40,
                        R.color.white_four,
                        R.drawable.bg_btn_right_radius_french_blue_40);
                //寫入當下狀態, 讓外界取用 2016/03/18 ryan
                m_initItem = EInitItem.MIDDLE;
                break;
            case R.id.rl_right_bg:
                setStyle(R.color.white_four,
                         R.color.white_four,
                         R.color.french_blue,
                         R.drawable.bg_btn_left_radius_french_blue_40,
                         R.color.french_blue_40,
                         R.drawable.bg_btn_right_radius_white);
                //寫入當下狀態, 讓外界取用 2016/03/18 ryan
                m_initItem = EInitItem.RIGHT;
                break;
        }
    }

    /**
     * 設置背景及文字style
     * @param leftTextColor     左邊文字顏色
     * @param middleTextColor   中間文字顏色
     * @param rightTextColor    右邊文字顏色
     * @param leftBg            左邊背景顏色
     * @param middleBg          中間背景顏色
     * @param rightBg           右邊背景顏色
     */
    private void setStyle(int leftTextColor,
                          int middleTextColor,
                          int rightTextColor,
                          int leftBg,
                          int middleBg,
                          int rightBg){
        m_rlRightBg.setBackgroundResource(rightBg);
        m_rlMiddleBg.setBackgroundResource(middleBg);
        m_rlLeftBg.setBackgroundResource(leftBg);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            m_tvLeftItem.setTextColor(getResources().getColor(leftTextColor,null));
            m_tvMiddleItem.setTextColor(getResources().getColor(middleTextColor, null));
            m_tvRightItem.setTextColor(getResources().getColor(rightTextColor, null));
        } else {
            m_tvLeftItem.setTextColor(getResources().getColor(leftTextColor));
            m_tvMiddleItem.setTextColor(getResources().getColor(middleTextColor));
            m_tvRightItem.setTextColor(getResources().getColor(rightTextColor));
        }

    }

    /**
     * 設置被點擊時的監聽事件
     * 增加取得當下狀態, 讓外界取用 2016/03/18 ryan
     * @return EInitItem 選擇的狀態
     */
    public EInitItem getSelectType(){
        return m_initItem;
    }

    /**
     * 設置被點擊時的監聽事件
     * @param listener ItemClickListener 監聽者
     */
    public void setListener(ItemClickListener listener){
        m_listener = listener;
    }
}
