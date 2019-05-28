package ci.function.FlightStatus;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.ui.define.ViewScaleDef;

/**
 * FlightStatus頁面下方選取RadioButton與日期
 * Created by flowmahuang on 2016/3/28.
 */
public class CISelectDateEightDayFragment extends BaseFragment implements View.OnClickListener {
    private TextView m_TitleMonth = null;
    private ImageView m_DepartureRadio = null;
    private ImageView m_ArrivalRadio = null;
    private RelativeLayout m_DepartureButton = null;
    private RelativeLayout m_ArrivalButton = null;
    private GridView m_EightGridView = null;

    private boolean radioCheck = true;
    private int dateCheck;
    private CISelectDateEightDayAdapter baseAdapter;

    private String[] monthTitle;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_select_date_eight_day;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        m_TitleMonth = (TextView) view.findViewById(R.id.tv_month_title);
        m_DepartureRadio = (ImageView) view.findViewById(R.id.radio_departure_date);
        m_ArrivalRadio = (ImageView) view.findViewById(R.id.radio_arrival_date);
        m_DepartureButton = (RelativeLayout) view.findViewById(R.id.rlayout_departure_date);
        m_ArrivalButton = (RelativeLayout) view.findViewById(R.id.rlayout_arrival_date);
        m_EightGridView = (GridView) view.findViewById(R.id.grid_eight_day);

        dateCheck = 2;
        initDateAndTitle();
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(view.findViewById(R.id.root_eight_day));
        vScaleDef.setTextSize(16, m_TitleMonth);
        vScaleDef.selfAdjustSameScaleView(m_DepartureRadio, 24, 24);
        vScaleDef.selfAdjustSameScaleView(m_ArrivalRadio, 24, 24);
        m_EightGridView.setHorizontalSpacing(vScaleDef.getLayoutWidth(15.3));
        m_EightGridView.setVerticalSpacing(vScaleDef.getLayoutHeight(12.6));
        m_EightGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    protected void setOnParameterAndListener(View view) {
        m_DepartureButton.setOnClickListener(this);
        m_ArrivalButton.setOnClickListener(this);
        baseAdapter = new CISelectDateEightDayAdapter(getContext());
        m_EightGridView.setAdapter(baseAdapter);
        m_EightGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 7) {
                    baseAdapter.setSeclection(position);
                    baseAdapter.notifyDataSetChanged();
                    m_TitleMonth.setText(monthTitle[position]);
                    dateCheck = position + 1;
                }
            }
        });
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlayout_departure_date:
                if (radioCheck) return;
                if (Build.VERSION.SDK_INT > 21) {
                    m_DepartureRadio.setBackground(getResources().getDrawable(R.drawable.btn_radio_on, null));
                    m_ArrivalRadio.setBackground(getResources().getDrawable(R.drawable.btn_radio_off, null));
                } else {
                    m_DepartureRadio.setBackground(getResources().getDrawable(R.drawable.btn_radio_on));
                    m_ArrivalRadio.setBackground(getResources().getDrawable(R.drawable.btn_radio_off));
                }
                radioCheck = true;
                break;
            case R.id.rlayout_arrival_date:
                if (!radioCheck) return;
                if (Build.VERSION.SDK_INT > 21) {
                    m_ArrivalRadio.setBackground(getResources().getDrawable(R.drawable.btn_radio_on, null));
                    m_DepartureRadio.setBackground(getResources().getDrawable(R.drawable.btn_radio_off, null));
                } else {
                    m_ArrivalRadio.setBackground(getResources().getDrawable(R.drawable.btn_radio_on));
                    m_DepartureRadio.setBackground(getResources().getDrawable(R.drawable.btn_radio_off));
                }
                radioCheck = false;
                break;
        }
    }

    private void initDateAndTitle() {
        final Calendar c = Calendar.getInstance();
        //2016-11-09 Ryan, 修正月份顯示的格式, 避免簡中出現 十一月
        SimpleDateFormat month = new SimpleDateFormat("MMM", CIApplication.getLanguageInfo().getLanguage_Locale());
        //SimpleDateFormat month = new SimpleDateFormat("MMMM");
        c.add(Calendar.DAY_OF_MONTH, -1);
        monthTitle = new String[8];

        for (int i = 0; i < 8; i++) {
            monthTitle[i] = month.format(c.getTime());
            if (i == 1) {
                m_TitleMonth.setText(month.format(c.getTime()));
            }
            c.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    public int getSelectDateCheck() {
        return dateCheck;
    }

    public boolean getRadioCheck() {
        return radioCheck;
    }
}
