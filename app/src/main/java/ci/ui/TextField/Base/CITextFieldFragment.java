package ci.ui.TextField.Base;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.lang.reflect.Field;
import java.util.ArrayList;

import ci.ui.TextField.InputFilter.CIRegexInputFilter;
import ci.ui.define.ViewScaleDef;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by kevincheng on 2016/2/3.
 */
public class CITextFieldFragment extends CIBaseTextFieldFragment
    implements View.OnClickListener,
               View.OnFocusChangeListener{
    public  static final String             TEXT_HINT                = "TEXT_HINT",
                                            TYPE_MODE                = "TYPE_MODE",
                                            FILTER_MODE              = "FILTER_MODE",
                                            REGEX                    = "REGEX",
                                            ITEM_ARRAY               = "ITEM_ARRAY",
                                            TEXT_MAX_LENGHT          = "TEXT_MAX_LENGHT";
    protected              String           m_strHint                = null;
    protected              TextInputLayout  m_textInputLayout        = null;
    private                TextView         m_tvFloatTitle           = null;
    protected              EditText         m_editText               = null;
    private                ImageView        m_ivDelete               = null,
                                            m_ivDropDown             = null,
                                            m_ivItem                 = null;
    private                TextView         m_tvError                = null;
    protected              boolean          m_bIsShowError           = false;
    protected              boolean          m_bIsLock                = false;
    protected              String           m_strErrorMsg            = null;
    protected              int              m_iResItemArray          = 0;
    protected              double           m_dWidth                 = 320 + 7;//7px 微調使底線寬可達320px
    protected              dropDownListener m_dropDownListener       = null;
    protected              onItemClickListener m_ItemClickListener   = null;
    protected              TypeMode         m_mode                   = TypeMode.NORMAL;
    protected              int              m_FragmentId             = 0;
    protected              int              m_iUnderLineYoffset      = 0;
    protected              EFilterMode      m_eFilterMode            = EFilterMode.NONE;
    private                ArrayList<InputFilter> m_alInputFilter    = null;
    /**TextFeild模式
     * NORMAL          一般
     * MENU_PULL_DOWN  視窗選單
     * MENU_FULL_PAGE  整頁選單
     * ONLY_DISPLAY    僅供顯示
     * EDITTEXT_BUTTON 一般編輯模式＋按鈕
     */
    public enum TypeMode {
        NORMAL, MENU_PULL_DOWN, MENU_FULL_PAGE, ONLY_DISPLAY, EDITTEXT_BUTTON
    }

    /**
     * 過濾文字模式
     * NONE     不過濾文字
     * REGEX    依賴正規表達式
     */
    public enum EFilterMode{
        NONE, REGEX
    }

    /**下拉事件監聽者介面*/
    public interface dropDownListener {
        void onDropDown(TypeMode mode, View v, String tag);
    }

    /**按鈕事件監聽者介面
     * 搭配 EDITTEXT_BUTTON 狀態使用 */
    public interface onItemClickListener {
        void onItemClick(TypeMode mode, View v, String tag);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        String strRegex = "";
        int    iTextMaxLenght = 0;
        if (null != bundle) {
            m_strHint       = bundle.getString(TEXT_HINT, null);
            m_mode          = TypeMode.valueOf(bundle.getString(TYPE_MODE, "NORMAL"));
            m_iResItemArray = bundle.getInt(ITEM_ARRAY, 0);
            m_eFilterMode   = EFilterMode.valueOf(bundle.getString(FILTER_MODE, EFilterMode.NONE.name()));
            strRegex        = bundle.getString(REGEX, "");
            iTextMaxLenght  = bundle.getInt(TEXT_MAX_LENGHT, 0);
        }

        m_alInputFilter = new ArrayList<>();

        if(false == TextUtils.isEmpty(strRegex) && EFilterMode.REGEX == m_eFilterMode){
            m_alInputFilter.add(new CIRegexInputFilter(strRegex));
        }

        if(0 < iTextMaxLenght && Integer.MAX_VALUE >= iTextMaxLenght){
            setMaxLenght(iTextMaxLenght);
        }

    }
    /**設定佈局ID*/
    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_ci_text_feild;
    }

    /**設定自適應*/
    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {

        //輸入欄位文字
        //vScaleDef.setViewSize(m_editText, m_dWidth, WRAP_CONTENT);
        vScaleDef.setViewSize(m_textInputLayout, m_dWidth, WRAP_CONTENT);
        vScaleDef.setTextSize(18, m_editText);
        int iTextInputLayout_r = m_textInputLayout.getEditText().getPaddingRight();
//        int iTextInputLayout_t = m_textInputLayout.getEditText().getPaddingTop();
//        int iTextInputLayout_b = m_textInputLayout.getEditText().getPaddingBottom();
        m_textInputLayout.getEditText().setPadding(vScaleDef.getLayoutWidth(13),
                                                   vScaleDef.getLayoutHeight(11),
                                                   iTextInputLayout_r + vScaleDef.getLayoutMinUnit(13),
                                                   vScaleDef.getLayoutHeight(13));

        int iEditText_b = m_editText.getBottom() + m_editText.getCompoundPaddingBottom();
        int iEditText_r = m_editText.getRight() ;
        int iEditText_l = m_editText.getLeft() + m_editText.getCompoundPaddingLeft();
        vScaleDef.setMargins(m_textInputLayout, 0, 0, 0, 0);
        //清除文字按鈕
        vScaleDef.selfAdjustSameScaleView(m_ivDelete, 24, 24);
        int marginBottom = Math.max(iEditText_b,vScaleDef.getLayoutHeight(10));
        ((ViewGroup.MarginLayoutParams)m_ivDelete.getLayoutParams()).setMargins(0, 0, iEditText_r + vScaleDef.getLayoutMinUnit(12), marginBottom);

        //彈出menu按鈕
        vScaleDef.selfAdjustSameScaleView(m_ivDropDown, 24, 24);
        ((ViewGroup.MarginLayoutParams)m_ivDropDown.getLayoutParams()).setMargins(0, 0, iEditText_r + vScaleDef.getLayoutMinUnit(3), marginBottom);

        //客製化按鈕
        vScaleDef.selfAdjustSameScaleView(m_ivItem, 24, 24);
        ((ViewGroup.MarginLayoutParams)m_ivItem.getLayoutParams()).setMargins(0, 0, iEditText_r + vScaleDef.getLayoutMinUnit(12), marginBottom);

        //設定浮動標籤
        m_tvFloatTitle.setPadding(iEditText_l, 0, 0, 0);
        vScaleDef.setTextSize(13, m_tvFloatTitle);
        setFloatLableTextSize(vScaleDef.getTextSize(13), vScaleDef.getTextSize(18), m_textInputLayout);

        //下方紅底白字錯誤
        vScaleDef.setViewSize(m_tvError, WRAP_CONTENT, 16);
        vScaleDef.setTextSize(13, m_tvError);
        vScaleDef.setPadding(m_tvError, 10, 0, 10, 0);
        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        m_textInputLayout.measure(w, h);
        int height = m_textInputLayout.getMeasuredHeight();
        m_iUnderLineYoffset = vScaleDef.getLayoutHeight(8);
        ((ViewGroup.MarginLayoutParams)m_tvError.getLayoutParams())
                .setMargins(0
                        , (height - m_iUnderLineYoffset)
                        , iTextInputLayout_r
                        , 0);
        m_tvError.setMaxWidth(vScaleDef.getLayoutWidth(310));

        if(0 < m_alInputFilter.size()){
            setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_FILTER);
            if(null != m_editText){
                InputFilter[] inputFilters;
                inputFilters = m_alInputFilter.toArray(new InputFilter[0]);
                m_editText.setFilters(inputFilters);
            }
        }
    }
    /**設定View元件*/
    @Override
    protected void setupViewComponents(View rootview) {

        m_tvFloatTitle    = (TextView) rootview.findViewById(R.id.float_label);
        m_textInputLayout = (TextInputLayout) rootview.findViewById(R.id.text_input_layout);
        m_editText        = (EditText) rootview.findViewById(R.id.edit_text);
        m_ivDelete        = (ImageView) rootview.findViewById(R.id.iv_btn_delete_n);
        m_ivDropDown      = (ImageView) rootview.findViewById(R.id.iv_ic_drop_down);
        m_ivItem          = (ImageView) rootview.findViewById(R.id.iv_ic_item);
        m_tvError         = (TextView) rootview.findViewById(R.id.tv_error);
        m_editText  .addTextChangedListener(this);
        m_ivDelete  .setOnClickListener(this);
        m_ivItem.setOnClickListener(this);
        m_ivDropDown.setClickable(false);
//        m_ivDropDown.setOnClickListener(this);
        if(null != m_strHint && 0 < m_strHint.length()){
            m_textInputLayout.setHint(m_strHint);
            m_textInputLayout.setHintAnimationEnabled(true);
        } else {
            m_textInputLayout.setHintAnimationEnabled(false);
        }


        m_editText.setOnClickListener(this);
        m_editText.setOnFocusChangeListener(this);
        handleMode(m_mode);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (false == hasFocus && true == m_bIsShowError) {
            showError();
        } else {
            hideError();
        }
    }

    /**輸入文字後的事件*/
    @Override
    public void afterTextChanged(Editable editable) {
        super.afterTextChanged(editable);
        if(0 < editable.length() &&
                TypeMode.NORMAL == m_mode &&
                false == m_bIsLock){
            m_ivDelete.setVisibility(View.VISIBLE);
        } else {
            m_ivDelete.setVisibility(View.GONE);
        }
    }

    /**
     * 調整輸入框寬度
     * @param width
     */
    public void setWidth(double width){
        m_dWidth = width;
    }

    /**
     * 設定文字
     */
    public void setText(String text){
        if(null != m_textInputLayout){
            m_textInputLayout.getEditText().setText(text);
        }
    }

    /**取得輸入欄位字元*/
    public String getText(){
        if(null != m_editText) {
            return m_editText.getText().toString().trim();
        }
        return "";
    }

    /**主要設定提示字移動至浮動標籤時的字樣*/
    public void changeHint(CharSequence hint){
        if(null != m_textInputLayout) {
            m_textInputLayout.setHint(null);
            m_tvFloatTitle.setVisibility(View.VISIBLE);
            m_tvFloatTitle.setText(hint);
        }
    }

    /**設定初始提示字*/
    public void setHint(CharSequence hint){
        if(null != m_textInputLayout) {
            m_strHint = hint.toString();
            m_textInputLayout.setHint(hint);
            m_tvFloatTitle.setVisibility(View.GONE);
            m_tvFloatTitle.setText(hint);
        }
    }

    /**取得Hint字串*/
    public String getHint(){
        if(null != m_strHint && 0 < m_strHint.length())
            return m_strHint;
        return m_tvFloatTitle.getText().toString();
    }

    /**設定錯誤格式的訊息*/
    public void setErrorMsg(String errorMsg){
        m_strErrorMsg = errorMsg;
    }

    /**取得錯誤格式的訊息*/
    public String getErrorMsg(){
        return m_strErrorMsg;
    }

    /**設定輸入文字格式的正確性*/
    public void setIsFormatCorrect(boolean isFormatCorrect){
        m_bIsShowError = !isFormatCorrect;
    }

    /**取得目前輸入文字格式的正確性*/
    public boolean getIsFormatCorrect(){
        return !m_bIsShowError;
    }

    /**顯示下方紅底白字錯誤訊息框*/
    public void showError() {
        if(null != m_tvError){
            m_tvError.setVisibility(View.VISIBLE);
            m_tvError.setText(getErrorMsg());
        }
    }

    /**隱藏下方紅底白字錯誤訊息框*/
    public void hideError() {
        if(null != m_tvError){
            m_tvError.setVisibility(View.INVISIBLE);
            m_textInputLayout.setErrorEnabled(false);
        }
    }

    /**取得TypeMode*/
    public TypeMode getMode(){
        return m_mode;
    }


    /**設定View的監聽事件*/
    @Override
    public void onClick(View v) {
        if(R.id.iv_btn_delete_n == v.getId()){
            m_editText.setText(null);
            hideError();
        }

        if( R.id.iv_ic_item == v.getId() ){
            if ( null != m_ItemClickListener ){
                m_ItemClickListener.onItemClick(m_mode, v, getTag());
            }
        } else if(TypeMode.NORMAL != m_mode && R.id.edit_text == v.getId()){
            if(null != m_dropDownListener){
                m_dropDownListener.onDropDown(m_mode, v,getTag());
            }
        }
    }

    /**設定浮動標籤的文字大小*/
    private void setFloatLableTextSize(float CollapsedTextSize,float ExpandedTextSize,TextInputLayout layout){
        try {
            Field field = layout.getClass().getDeclaredField("mCollapsingTextHelper");
            field.setAccessible(true);
            Object mCollapsingTextHelper = field.get(layout);
            Field field2 = mCollapsingTextHelper.getClass().getDeclaredField("mCollapsedTextSize");
            Field field3 = mCollapsingTextHelper.getClass().getDeclaredField("mExpandedTextSize");
            field2.setAccessible(true);
            field3.setAccessible(true);
            field2.setFloat(mCollapsingTextHelper, CollapsedTextSize);
            field3.setFloat(mCollapsingTextHelper, ExpandedTextSize);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**設定EditText的一些功能*/
    protected final void setLockEditText(EditText editText, boolean lock) {
        //也許可以用inputType?
        editText.setLongClickable(!lock);
        editText.setFocusable(!lock);
        editText.setFocusableInTouchMode(!lock);
        /**因為需要監聽EditText onClick event 所以不能false clickable*/
//        editText.setClickable(!lock);
    }

    /**
     * 設定TextFeild Lock
     * @param isLock if true 鎖定
     */
    public void setLock(boolean isLock){
        if(null == m_editText){
            return;
        }

        //在設定或解除鎖定時，判斷一次是否該顯示格式錯誤
        onFocusChange(m_editText, isFocused());

        m_bIsLock = isLock;
        if(true == isLock) {
            m_editText.setOnClickListener(null);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                m_editText.setTextColor(getResources().getColor(R.color.white_50,null));
            } else {
                m_editText.setTextColor(getResources().getColor(R.color.white_50));
            }
            m_ivDelete  .setOnClickListener(null);
            m_ivDelete  .setVisibility(View.GONE);
            m_ivDropDown.setOnClickListener(null);
            m_ivDropDown.setVisibility(View.GONE);
            m_ivItem.setOnClickListener(null);
            m_ivItem.setVisibility(View.GONE);
        } else {
            handleMode(m_mode);
            if(false == TextUtils.isEmpty(m_editText.getText())
                    && !( TypeMode.MENU_FULL_PAGE == m_mode
                            || TypeMode.MENU_PULL_DOWN == m_mode
                            || TypeMode.ONLY_DISPLAY == m_mode
                            || TypeMode.EDITTEXT_BUTTON == m_mode) ){
                m_ivDelete.setVisibility(View.VISIBLE);
            }
            m_editText.setOnClickListener(this);
            m_ivDelete  .setOnClickListener(this);
            m_ivDropDown.setOnClickListener(this);
            m_ivItem.setOnClickListener(this);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                m_editText.setTextColor(getResources().getColor(R.color.white,null));
            } else {
                m_editText.setTextColor(getResources().getColor(R.color.white));
            }
        }
        m_editText.setEnabled(!isLock);
    }

    /**處理View在每個Mode的應有的狀態*/
    private void handleMode(TypeMode mode){
        if (TypeMode.NORMAL == mode){
            m_ivDropDown.setVisibility(View.GONE);
            m_ivItem.setVisibility(View.GONE);
            setLockEditText(m_editText,false);
        } else if (TypeMode.MENU_FULL_PAGE == mode ||
                   TypeMode.MENU_PULL_DOWN == mode){
            m_ivDelete.setVisibility(View.GONE);
            m_ivDropDown.setVisibility(View.VISIBLE);
            m_ivItem.setVisibility(View.GONE);
            setLockEditText(m_editText,true);
        } else if (TypeMode.ONLY_DISPLAY == mode){
            m_ivDelete.setVisibility(View.GONE);
            m_ivDropDown.setVisibility(View.GONE);
            m_ivItem.setVisibility(View.GONE);
            setLockEditText(m_editText, true);
        } else if (TypeMode.EDITTEXT_BUTTON == mode){
           m_ivDelete.setVisibility(View.GONE);
           m_ivDropDown.setVisibility(View.GONE);
           m_ivItem.setVisibility(View.VISIBLE);
        }
    }

    /**設定下拉事件監聽者*/
    public void setDropDownListener(dropDownListener listener){
        this.m_dropDownListener = listener;
    }

    /**設定按鈕事件監聽者*/
    public void setItemClickListener(onItemClickListener listener){
        this.m_ItemClickListener = listener;
    }

    /**設定輸入格式*/
    public void setInputType(int inputType){
        if(null != m_editText) {
            m_editText.setInputType(inputType);
        }
    }

    /**
     * 輸入欄是否被Focus
     * @return true if focused
     */
    public boolean isFocused(){
        if(null != m_editText) {
            return m_editText.isFocused();
        }
        return false;
    }

    /**
     * 設定最大字數
     * @param lenght 字數
     */
    public void setMaxLenght(int lenght){
        if(0 < lenght && Integer.MAX_VALUE >= lenght){

            if(null == m_alInputFilter){
                m_alInputFilter = new ArrayList<>();
            }

            if(0 < m_alInputFilter.size()){
                for(InputFilter inputFilter: m_alInputFilter){
                    if(inputFilter instanceof InputFilter.LengthFilter){
                        m_alInputFilter.remove(inputFilter);
                    }
                }
            }

            m_alInputFilter.add(new InputFilter.LengthFilter(lenght));
            InputFilter[] inputFilters;
            inputFilters = m_alInputFilter.toArray(new InputFilter[0]);
            if(null != m_editText){
                m_editText.setFilters(inputFilters);
            }
        }
    }

    /**
     * 設定Fragment Id
     * @param id
     */
    public void setFragmentId(int id){
        m_FragmentId = id;
    }

    /**
     * 設定imeOption
     * @param imeOption
     */
    public void setImeOptions(int imeOption){
        if(null != m_editText) {
            m_editText.setImeOptions(imeOption);
        }
    }
}
