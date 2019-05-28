package ci.ui.MealCard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.chinaairlines.mobile30.R;

/**
 * Created by user on 2016/3/9.
 */
public class CICheckImageButton extends ImageButton {
    private boolean m_bSelected = false;
    public CICheckImageButton(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public void setSelected( Boolean bSelect ){
        if ( bSelect ) {
            setBackgroundResource(R.drawable.btn_checkbox_on);
            m_bSelected = true;
        } else {
            setBackgroundResource(R.drawable.btn_checkbox_off);
            m_bSelected = false;
        }
    }

    /**傳入原先的Click狀態，進行點擊*/
    public void onClick( Boolean bOrgClick ){
        if ( bOrgClick ) {
            setBackgroundResource(R.drawable.btn_checkbox_off);
        } else {
            setBackgroundResource(R.drawable.btn_checkbox_on);
        }
        m_bSelected = !bOrgClick;
    }

    public boolean getSelected(){return m_bSelected;}
}
