package ci.ui.TextField.PopupWindow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.ui.TextField.Adapter.CIMenusAdapter;
import ci.ui.define.ViewScaleDef;

/**
 * Created by kevincheng on 2016/2/17.
 */
public class CISelectPopupWindow extends PopupWindow implements AdapterView.OnItemClickListener{

    private Context              m_context  = null;
    private ListView             m_listView = null;
    private AdapterView.OnItemClickListener m_listener = null;
    private ArrayList<String>    m_alItems  = null;
    private CIMenusAdapter       m_adapter  = null;

    public CISelectPopupWindow(){}

    public CISelectPopupWindow(Context context,
                               int width,
                               int height,
                               ArrayList<String> alItems,
                               AdapterView.OnItemClickListener listener){
        m_context  = context;
        m_listener = listener;
        m_alItems  = alItems;
        LayoutInflater layoutInflater = LayoutInflater.from(m_context);
        View view = layoutInflater.inflate(R.layout.popupwindow_menu, null);
        initialLayoutComponent(view);

        setTextSizeAndLayoutParams(view, ViewScaleDef.getInstance(m_context));
        setContentView(view);
        setWidth(width);
        setHeight(height);
        setOutsideTouchable(true);
        setTouchable(true);
        //SDK 19 需設定這個，ListView的item被點擊才能接收到onItemClick事件
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
    }

    private void initialLayoutComponent(View view) {
        m_listView = (ListView)view.findViewById(R.id.popupwindow_menu_listview);
        m_adapter = new CIMenusAdapter(m_context, m_alItems,R.layout.list_item_textfeild_pulldown_menu);
        m_listView.setAdapter(m_adapter);
        m_listView.setOnItemClickListener(this);
    }

    private void setTextSizeAndLayoutParams(View view, ViewScaleDef scaleDef) {
        scaleDef.setViewSize(m_listView, 320, ViewGroup.LayoutParams.WRAP_CONTENT);
        m_listView.setDividerHeight(scaleDef.getLayoutHeight(0.7));
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        m_listener.onItemClick(adapterView,view,i,l);
        dismiss();
    }
}
