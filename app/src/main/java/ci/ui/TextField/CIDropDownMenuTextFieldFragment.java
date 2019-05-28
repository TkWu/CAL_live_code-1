package ci.ui.TextField;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Collections;

import ci.ui.TextField.Base.CITextFieldFragment;
import ci.ui.TextField.PopupWindow.CISelectPopupWindow;
import ci.ui.define.ViewScaleDef;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by kevin on 2016/2/20.
 */
public class CIDropDownMenuTextFieldFragment extends CITextFieldFragment
    implements AdapterView.OnItemClickListener{

    public final static String ITEM_STRING_ARRAY = "ITEM_STRING_ARRAY";

    private ArrayList<String> m_arrayList = null;
    private AdapterView.OnItemClickListener m_listener = null;
    private int m_position = -1;

    public static CIDropDownMenuTextFieldFragment newInstance(Context context, int resString, int itemArray) {
        Bundle bundle = new Bundle();
        String hint   = context.getString(resString);
        bundle.putString(TEXT_HINT, "*" + hint);
        bundle.putInt(ITEM_ARRAY, itemArray);
        bundle.putString(TYPE_MODE, TypeMode.MENU_PULL_DOWN.name());
        CIDropDownMenuTextFieldFragment fragment = new CIDropDownMenuTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static CIDropDownMenuTextFieldFragment newInstance(String strHint, int itemArray) {
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_HINT, strHint);
        bundle.putInt(ITEM_ARRAY, itemArray);
        bundle.putString(TYPE_MODE, TypeMode.MENU_PULL_DOWN.name());
        CIDropDownMenuTextFieldFragment fragment = new CIDropDownMenuTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static CIDropDownMenuTextFieldFragment newInstance(int itemArray) {
        Bundle bundle = new Bundle();
        bundle.putInt(ITEM_ARRAY, itemArray);
        bundle.putString(TYPE_MODE, TypeMode.MENU_PULL_DOWN.name());
        CIDropDownMenuTextFieldFragment fragment = new CIDropDownMenuTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static CIDropDownMenuTextFieldFragment newInstance(String strHint, String[] itemArray) {
        Bundle bundle = new Bundle();
        bundle.putString(TEXT_HINT, strHint);
        bundle.putStringArray(ITEM_STRING_ARRAY, itemArray);
        bundle.putString(TYPE_MODE, TypeMode.MENU_PULL_DOWN.name());
        CIDropDownMenuTextFieldFragment fragment = new CIDropDownMenuTextFieldFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        setDropDownListener(listener);
    }

    dropDownListener listener = new dropDownListener() {
        @Override
        public void onDropDown(TypeMode mode, View v, String tag) {
           pullDownMenu(v);
        }
    };

    private void pullDownMenu(View v){

        String[] array = null;
        if( m_iResItemArray <= 0 ) {
            array = getArguments().getStringArray(ITEM_STRING_ARRAY);
        } else {
            array = getResources().getStringArray(m_iResItemArray);
        }
        m_arrayList = new ArrayList<>();
        Collections.addAll(m_arrayList, array);
        //
        ViewScaleDef viewScaleDef = ViewScaleDef.getInstance(getActivity());
        boolean bIsOutSide = IsOutSide(v);
        int iWidth = bIsOutSide? viewScaleDef.getLayoutHeight(320):WRAP_CONTENT;
        final CISelectPopupWindow popupWindow = new CISelectPopupWindow(getActivity()
                , MATCH_PARENT
                //, WRAP_CONTENT
                ,iWidth
                , m_arrayList
                , this);
        if(bIsOutSide){
            popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        } else {
            popupWindow.showAsDropDown(v, 0, -m_iUnderLineYoffset);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        m_position = position;
        if(null != m_listener){
            m_listener.onItemClick(parent, view, position, id);
        }
        m_editText.setText(m_arrayList.get(position));
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
        m_listener = listener;
    }

    /**
     * 判斷DropDown View是否超出螢幕底部
     * @param v View
     * @return if true 就是超出螢幕底部
     */
    public boolean IsOutSide(View v){
        int itemArray = getArguments().getInt(ITEM_ARRAY);
        int length = 0;

        if( 0 == itemArray ) {
            length = getArguments().getStringArray(ITEM_STRING_ARRAY).length;
        } else {
            length = getResources().getStringArray(itemArray).length;
        }

        ViewScaleDef viewScaleDef = ViewScaleDef.getInstance(getActivity());
        DisplayMetrics dm = viewScaleDef.getDisplayMetrics();
        int screenHeight = dm.heightPixels;
        int iItemHeight = viewScaleDef.getLayoutHeight(51);
        int iTotalHeight = iItemHeight * length;
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        int locationY = location[1];
        int y = locationY + v.getHeight() - m_iUnderLineYoffset + iTotalHeight;
        if(y > screenHeight){
            return true;
        } else {
            return false;
        }
    }

    public int getPosition(){
        return m_position;
    }

    public void setPosition(int position) {
        this.m_position = position;
    }
}
