package ci.ui.object;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

/**
 * Created by kevincheng on 2016/6/3.
 * 主要用來判斷CIInquiryTripModel要求ws所回傳的結果，判斷是否顯示，
 * 若要顯示則會判斷應該設定View元件到什麼對應到相對的圖示及文字及文字顏色
 *
 */
public class CIFlightStatusManager {

    public static void transferIconAndTextByColorCode(ImageView ivFlightStatus,
                                               TextView  tvFlightStatus,
                                               String colorCode,
                                               String flightStatus){

        if(null == ivFlightStatus || null == tvFlightStatus){
            return;
        }

        //飛行狀態依賴Resp資料判斷
        final String    DELAYED     = "#FFC425";
        final String    ARRIVED     = "#A7A9AC";
        final String    CANCELLED   = "#DE0A20";
        final String    ON_TIME     = "#00AA51";
        final String    NULL        = "null";

        switch (colorCode.toUpperCase()){
            case DELAYED:
                ivFlightStatus.setImageResource(R.drawable.ic_delayed);
                break;
            case ARRIVED:
                ivFlightStatus.setImageResource(R.drawable.ic_arrived);
                break;
            case CANCELLED:
                ivFlightStatus.setImageResource(R.drawable.ic_cancelled);
                break;
            case ON_TIME:
                ivFlightStatus.setImageResource(R.drawable.ic_on_time);
                break;
            default://沒有對應的顏色就直接隱藏圖示
                ivFlightStatus.setVisibility(View.GONE);
                break;
        }

        if(!TextUtils.isEmpty(colorCode) && !colorCode.equals(NULL)
                && !TextUtils.isEmpty(flightStatus) && !flightStatus.equals(NULL) ){
            tvFlightStatus.setVisibility(View.VISIBLE);
            tvFlightStatus.setText(flightStatus);
            int color ;
            try {
                color = Color.parseColor(colorCode);
            } catch (Exception e){
                //如果無法正確paser顏色就不要顯示狀態
                tvFlightStatus.setVisibility(View.INVISIBLE);
                ivFlightStatus.setVisibility(View.GONE);
                return ;
            }
            tvFlightStatus.setTextColor(color);
        } else {
            tvFlightStatus.setVisibility(View.INVISIBLE);
            ivFlightStatus.setVisibility(View.GONE);
        }
    }


}
