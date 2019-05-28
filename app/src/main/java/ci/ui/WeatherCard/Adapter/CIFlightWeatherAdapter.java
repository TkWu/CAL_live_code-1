package ci.ui.WeatherCard.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.ui.WeatherCard.resultData.item.CIForecastItem;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;

/**
 * Created by jlchen on 2016/2/15.
 * 飛行狀態-氣象資訊-天氣預報表(GridView)
 */
public class CIFlightWeatherAdapter extends BaseAdapter {

    private Context                     m_context       = null;

    //天氣預報資訊清單
    private ArrayList<CIForecastItem>   m_arMenuList    = null;

    private static final double DEF_TEXT_SIZE           = 13;
    private static final double DEF_TEXTVIEW_HEIGHT     = 15.7;
    private static final double DEF_IMAGE_WIDTH         = 24;
    private static final double DEF_IMAGE_TOP_MAGIN     = 7;
    private static final double DEF_IMAGE_BOTTOM_MAGIN  = 4.3;


    public CIFlightWeatherAdapter(Context context, ArrayList<CIForecastItem> arrayList){
        this.m_context = context;
        this.m_arMenuList = arrayList;
    }

    private class ItemHolder {
        //顯示星期幾
        TextView	tvDay;
        //顯示該日氣象圖片
        ImageView	ivImage;
        //顯示該日最高溫
        TextView	tvDayHigh;
        //顯示該日最低溫
        TextView	tvDayLow;
    }

    @Override
    public int getCount() {
        return m_arMenuList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ItemHolder holder = null;
        CIForecastItem item = m_arMenuList.get(position);

        if (null == convertView) {
            holder      = new ItemHolder();
            convertView =  LayoutInflater.from(m_context).inflate(
                    R.layout.layout_view_weather_girdview_item, parent, false);

            holder.tvDay        = (TextView)convertView.findViewById(R.id.tv_day);
            holder.ivImage      = (ImageView)convertView.findViewById(R.id.iv_day);
            holder.tvDayHigh    = (TextView)convertView.findViewById(R.id.tv_day_high);
            holder.tvDayLow     = (TextView)convertView.findViewById(R.id.tv_day_low);

            //天氣小圖
            int iRes;
            try {
                iRes = AppInfo.getInstance(m_context).GetIconResourceId(
                                m_context.getString(R.string.weather_little), item.m_strCode);
            }catch (Exception e){
                iRes = R.drawable.weather_little_3200;
            }
            //避免 GetIconResourceId 因為找不到資源檔而給0, 塞入預設圖檔
            if ( iRes <= 0 ){
                iRes = R.drawable.weather_little_3200;
            }
            holder.ivImage.setImageResource(iRes);

            //溫度符號
            String strTemp = m_context.getResources().getString(R.string.weather_units);

            //依照yahoo weather api回傳day內容顯示對應的字串 (例如day為Mon, 中文就顯示字串"一")
            String strWeatherDay = "";
            try {
                strWeatherDay = m_context.getString(
                        AppInfo.getInstance(m_context).GetStringResourceId(
                                m_context.getString(R.string.weather_day), item.m_strDay));
            }catch (Exception e){
                strWeatherDay = "";
            }
            holder.tvDay.setText(strWeatherDay);

            //最高溫
            if ( null == item.m_strHigh ){
                item.m_strHigh = "";
            }
            holder.tvDayHigh.setText(item.m_strHigh + strTemp);

            //最低溫
            if ( null == item.m_strLow ){
                item.m_strLow = "";
            }
            holder.tvDayLow.setText(item.m_strLow + strTemp);

            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }

        setTextSizeAndLayoutParams(holder);

        return convertView;
    }


    private void setTextSizeAndLayoutParams(ItemHolder holder)
    {
        ViewScaleDef vScaleDef = ViewScaleDef.getInstance(m_context);

        //字串一律為 高15.7, 字串大小13
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)holder.tvDay.getLayoutParams();
        params.height = vScaleDef.getLayoutHeight(DEF_TEXTVIEW_HEIGHT);
        params.width = vScaleDef.getLayoutWidth(50);
        vScaleDef.setTextSize(DEF_TEXT_SIZE, holder.tvDay);

        params = (LinearLayout.LayoutParams)holder.tvDayHigh.getLayoutParams();
        params.height   = vScaleDef.getLayoutHeight(DEF_TEXTVIEW_HEIGHT);
        vScaleDef.setTextSize(DEF_TEXT_SIZE, holder.tvDayHigh);

        params = (LinearLayout.LayoutParams)holder.tvDayLow.getLayoutParams();
        params.height   = vScaleDef.getLayoutHeight(DEF_TEXTVIEW_HEIGHT);
        vScaleDef.setTextSize(DEF_TEXT_SIZE, holder.tvDayLow);

        //圖片 寬高皆為24, 與上方文字間隔4.3, 與下方文字間隔2.3
        params = (LinearLayout.LayoutParams)holder.ivImage.getLayoutParams();
        params.height       = vScaleDef.getLayoutMinUnit(DEF_IMAGE_WIDTH);
        params.width        = vScaleDef.getLayoutMinUnit(DEF_IMAGE_WIDTH);
        params.topMargin    = vScaleDef.getLayoutHeight(DEF_IMAGE_TOP_MAGIN);
        params.bottomMargin = vScaleDef.getLayoutHeight(DEF_IMAGE_BOTTOM_MAGIN);
    }
}
