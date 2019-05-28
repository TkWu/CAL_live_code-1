package ci.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.Base.BaseView;
import ci.ui.define.ViewScaleDef;

/**
 * Created by Ryan on 16/3/5.
 */
public class StepHorizontalView extends BaseView{

    private static final int DEF_STEP = 1;
    class  ViewHolder{
        int         iIndex;
        TextView    tvText;
        ImageView   imgStep;
        ImageView   imgDone;
    }

    private LinearLayout m_llayout_Content = null;

    private int m_iCurrStep = DEF_STEP;    //  起始步驟
    private int m_iTotalCnt = 0;
    private ArrayList<ViewHolder> m_arrStepsList = new ArrayList<>();

    public StepHorizontalView(Context context) {
        super(context);
        initial();
    }

    public StepHorizontalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initial();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_steps_horizontal_view;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater) {

        m_llayout_Content = (LinearLayout)findViewById(R.id.llayout_bg);
    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {

        int ipadding = vScaleDef.getLayoutWidth(17);
        m_llayout_Content.setPadding(ipadding, 0, ipadding,0 );
    }

    public void initialView( int iStepCount ){

        if ( iStepCount <= 0 ){
            return;
        }

        m_iTotalCnt = iStepCount;
        ViewScaleDef vScaleDef = ViewScaleDef.getInstance(m_Context);
        for ( int iIdx = DEF_STEP; iIdx <= m_iTotalCnt; iIdx++ ){

            ViewHolder viewholder = new ViewHolder();
            View view = m_layoutInflater.inflate(R.layout.layout_step_view , null);

            viewholder.imgStep  = (ImageView)view.findViewById(R.id.img_Step);
            viewholder.tvText   = (TextView)view.findViewById(R.id.tv_StepText);
            viewholder.imgDone  = (ImageView)view.findViewById(R.id.img_done);
            viewholder.iIndex   = iIdx;
            viewholder.tvText.setText(String.valueOf(iIdx));
            viewholder.imgDone.setVisibility(View.INVISIBLE);
            if ( iIdx == m_iCurrStep ){
                viewholder.imgStep.setImageResource(R.drawable.img_steps_1);
                viewholder.tvText.setAlpha(1);
            } else {
                viewholder.imgStep.setImageResource(R.drawable.img_steps_2);
                viewholder.tvText.setAlpha((float)0.4);
            }

            viewholder.imgStep.getLayoutParams().height = vScaleDef.getLayoutMinUnit(8);
            ((RelativeLayout.LayoutParams) viewholder.imgStep.getLayoutParams()).topMargin = vScaleDef.getLayoutHeight(3);

            vScaleDef.setTextSize(16, viewholder.tvText);

            int iWidth  = vScaleDef.getLayoutWidth(56.7);
            //int iHeight = vScaleDef.getLayoutHeight(39);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(iWidth, LayoutParams.MATCH_PARENT);
            if ( iIdx != DEF_STEP ){
                params.leftMargin = vScaleDef.getLayoutWidth(10.7);
            }
            m_llayout_Content.addView( view, params);

            m_arrStepsList.add(viewholder);
        }
    }

    public int setNextSteps(){

        m_iCurrStep++;

        for ( ViewHolder view : m_arrStepsList ){

            if ( view.iIndex < m_iCurrStep ){
                view.imgDone.setVisibility(View.VISIBLE);
                view.imgStep.setImageResource(R.drawable.img_steps_1);
                view.tvText.setVisibility(View.INVISIBLE);
            } else if ( view.iIndex == m_iCurrStep ){
                view.imgDone.setVisibility(View.INVISIBLE);
                view.imgStep.setImageResource(R.drawable.img_steps_1);
                view.tvText.setVisibility(View.VISIBLE);
                view.tvText.setAlpha(1);
            } else {
                view.imgDone.setVisibility(View.INVISIBLE);
                view.imgStep.setImageResource(R.drawable.img_steps_2);
                view.tvText.setVisibility(View.VISIBLE);
                view.tvText.setAlpha((float)0.4);
            }

        }
        return m_iCurrStep;
    }

    public void setCurrentStep( int iCurrStep ){

        m_iCurrStep = iCurrStep;
        for ( ViewHolder view : m_arrStepsList ){

            if ( view.iIndex < m_iCurrStep ){
                view.imgDone.setVisibility(View.VISIBLE);
                view.imgStep.setImageResource(R.drawable.img_steps_1);
                view.tvText.setVisibility(View.INVISIBLE);
            } else if ( view.iIndex == m_iCurrStep ){
                view.imgDone.setVisibility(View.INVISIBLE);
                view.imgStep.setImageResource(R.drawable.img_steps_1);
                view.tvText.setVisibility(View.VISIBLE);
                view.tvText.setAlpha(1);
            } else {
                view.imgDone.setVisibility(View.INVISIBLE);
                view.imgStep.setImageResource(R.drawable.img_steps_2);
                view.tvText.setVisibility(View.VISIBLE);
                view.tvText.setAlpha((float)0.4);
            }

        }
    }
}
