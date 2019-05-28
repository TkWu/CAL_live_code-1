package ci.ui.MealCard;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ci.ui.define.ViewScaleDef;
import ci.ws.Models.entities.CIMealInfoEntity;
import ci.ws.Models.entities.CIMealPassenagerEntity;

/**
 * Created by user on 2016/3/9.
 * Modifly by ryan on 2016/09/14
 */
public class CIAListView extends ListView {
    public Context          m_context;
    public LayoutInflater   m_inflater;
    private TextView        m_footerView;
    private ViewScaleDef    m_vScaleDef;

    private ArrayList<CIMealPassenagerEntity>   m_arMealPassenagerList = null;
    private footerClickLisener                  m_Listener = null;
    private BaseAdapter                         m_baseAdapter = null;
    //    用來存勾選的狀態
    private boolean[] m_bSelectStatus = null;


    public interface footerClickLisener {
        void onFooterClick();
    }

    public CIAListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.m_context = context;

        m_vScaleDef = ViewScaleDef.getInstance(context);
        m_inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        m_baseAdapter = baseAdapter();
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(Color.TRANSPARENT);
        setSelector(colorDrawable);
        setDivider(ContextCompat.getDrawable(context, R.color.transparent));
        setFooter();
        setAdapter(m_baseAdapter);
        setVerticalScrollBarEnabled(false);

    }

    public class HolderItem {

        public RelativeLayout   rlayout;
        public CICheckImageButton checkBox;
        public TextView         tvName;
        public LinearLayout     llayout_content;
        public View             vDiv;

        public ArrayList<Detail_item> arDetailList;

        public HolderItem(){
            checkBox    = null;
            tvName      = null;
            llayout_content = null;
            arDetailList = new ArrayList<>();
        }
    }

    public class Detail_item {
        View        vBody;
        TextView    tvMealDesc;
        TextView    tvMealdash;
        TextView    tvMealName;
    }

    private BaseAdapter baseAdapter() {
        return new BaseAdapter() {
            @Override
            public int getCount() {
                if ( m_arMealPassenagerList != null) {
                    return m_arMealPassenagerList.size();
                } else {
                    return 0;
                }
            }

            @Override
            public Object getItem(int position) {
                if ( m_arMealPassenagerList != null) {
                    return m_arMealPassenagerList.get(position);
                } else {
                    return null;
                }
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                HolderItem holderItem = null;
                CIMealPassenagerEntity passenager = m_arMealPassenagerList.get(position);
                int iMealCount = passenager.meal_info.size();
                //
                if (convertView == null) {
                    holderItem = new HolderItem();
                    //
                    convertView = m_inflater.inflate(R.layout.layout_meal_passenager_list_item, null);

                    holderItem.rlayout  = (RelativeLayout)convertView.findViewById(R.id.rlayout);
                    holderItem.checkBox = (CICheckImageButton)convertView.findViewById(R.id.image_button);
                    holderItem.tvName   = (TextView) convertView.findViewById(R.id.tvName);
                    holderItem.llayout_content = (LinearLayout)convertView.findViewById(R.id.llayout_content);
                    holderItem.vDiv     = (View)convertView.findViewById(R.id.vline);

                    m_vScaleDef.selfAdjustAllView(holderItem.rlayout);

                    for ( int iIdx =0; iIdx < iMealCount; iIdx++ ){

                        View vBodyView = LayoutInflater.from(m_context).inflate(R.layout.layout_meal_passenager_list_mealdetail_item, null);

                        Detail_item detail_item = new Detail_item();

                        detail_item.vBody       = vBodyView;
                        detail_item.tvMealDesc  = (TextView) vBodyView.findViewById(R.id.tvDesc);
                        detail_item.tvMealdash  = (TextView) vBodyView.findViewById(R.id.tvDash);
                        detail_item.tvMealName  = (TextView) vBodyView.findViewById(R.id.tvMealName);

                        m_vScaleDef.setTextSize(13, detail_item.tvMealDesc);
                        m_vScaleDef.setTextSize(13, detail_item.tvMealdash);
                        m_vScaleDef.setTextSize(13, detail_item.tvMealName);

                        holderItem.arDetailList.add(detail_item);
                        holderItem.llayout_content.addView(vBodyView);
                    }

                    convertView.setTag(holderItem);

                    convertView.setOnClickListener( new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //丟入原來的狀態，模擬checkBox 的click行為
                            HolderItem item = (HolderItem)v.getTag();
                            item.checkBox.onClick(m_bSelectStatus[position]);
                            //回傳Click後的狀態
                            m_bSelectStatus[position] = item.checkBox.getSelected();
                        }
                    });

                } else {
                    holderItem = (HolderItem)convertView.getTag();
                }

                holderItem.tvName.setText(passenager.pax_first_name + " " + passenager.pax_last_name);
                //儲存選擇的狀態
                holderItem.checkBox.setSelected(m_bSelectStatus[position]);
                //
                for ( int iIdx =0; iIdx < iMealCount; iIdx++ ){

                    CIMealInfoEntity mealInfoEntity = passenager.meal_info.get(iIdx);
                    Detail_item detail_item = holderItem.arDetailList.get(iIdx);

                    detail_item.tvMealDesc.setText(mealInfoEntity.mealtype_desc);
                    //
                    if ( TextUtils.isEmpty(mealInfoEntity.meal_code) ){
                        detail_item.vBody.setVisibility(View.VISIBLE);
                        detail_item.tvMealName.setText(m_context.getString(R.string.unselected));
                    } else {
                        detail_item.vBody.setVisibility(View.VISIBLE);
                        detail_item.tvMealName.setText( mealInfoEntity.meal_name );
                    }
                }

                return convertView;
            }
        };
    }

    private OnClickListener setFootOnClick() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_Listener != null) {
                    m_Listener.onFooterClick();
                }
            }
        };
    }

    private void setFooter() {
        View view = m_inflater.inflate(R.layout.layot_meal_footview, null);
        m_footerView = (TextView) view.findViewById(R.id.tv_footer);
        SpannableString content = new SpannableString(getResources().getString(R.string.meal_selection_notice));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        m_footerView.setText(content);
        m_footerView.setOnClickListener(setFootOnClick());
        m_vScaleDef.selfAdjustAllView(m_footerView);
        addFooterView(view);
    }

    /*
    獲得選擇簪點的人數
     */
    public int getNum() {
        int num = 0;
        for (int i = 0; i < m_bSelectStatus.length; i++) {
            if (m_bSelectStatus[i]) {
                num++;
            }
        }
        return num;
    }

    /*
    獲得選擇餐點人的資訊
    */
    public String getSelectInfo() {
        String result = "";
        if (m_arMealPassenagerList != null) {
            ArrayList<CIMealPassenagerEntity> selector = new ArrayList<>();
            Type dataType = new TypeToken<ArrayList<CIMealPassenagerEntity>>() {
            }.getType();
            Gson gson = new Gson();
            for (int i = 0; i < m_bSelectStatus.length; i++) {
                if (m_bSelectStatus[i]) {
                    CIMealPassenagerEntity selectorMap = m_arMealPassenagerList.get(i);
                    selector.add(selectorMap);
                }
            }
            result = gson.toJson(selector, dataType);
        }
        return result;
    }

    public ArrayList<CIMealPassenagerEntity> getSelectPassenagerList(){

        ArrayList<CIMealPassenagerEntity> selector = new ArrayList<>();
        if (m_arMealPassenagerList != null) {
            for (int i = 0; i < m_bSelectStatus.length; i++) {
                if (m_bSelectStatus[i]) {
                    CIMealPassenagerEntity selectorMap = m_arMealPassenagerList.get(i);
                    selector.add(selectorMap);
                }
            }
        }
        return selector;
    }

    //    設定footer 的click事件
    public void setFooterClickLisenerListener(footerClickLisener lisener) {
        if (lisener != null) {
            this.m_Listener = lisener;
        }
    }


    //設定資料來源
    public void setSelectMealPresenter(ArrayList<CIMealPassenagerEntity> inputArrayList) {
        if (inputArrayList != null) {
            this.m_arMealPassenagerList = inputArrayList;
            m_bSelectStatus = new boolean[m_arMealPassenagerList.size()];
            m_baseAdapter.notifyDataSetChanged();
        }
    }

}
