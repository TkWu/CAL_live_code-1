package ci.function.MyTrips.Detail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Core.CIApplication;
import ci.function.Main.item.CIProgressBarStyleHandler;
import ci.function.Main.item.CIQuestionItem;
import ci.ui.define.ViewScaleDef;

import static ci.function.Main.item.CIProgressBarStyleHandler.setProgressByRange;

/**
 * Created by kevincheng on 2017/5/9.
 */

public class CIQuestionnaireAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private CIQuestionItem m_data;
    private Context        m_context;
    public CIQuestionnaireAdapter(CIQuestionItem data) {
        m_data = data;
        m_context = CIApplication.getContext();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.layout_questionnaire_view, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
        ItemHolder itemHolder= (ItemHolder)holder;
        CIQuestionItem.Question data = m_data.questions.get(position);
        setViewVisiable(itemHolder, position);
        setViewContent(itemHolder,  data);
        setViewParams(itemHolder);
        setListener(itemHolder,  data);

    }

    private void setViewVisiable(ItemHolder itemHolder,int position){
        if(0 >= position){
            itemHolder.m_vLineTop.setVisibility(ViewGroup.VISIBLE);
        } else {
            itemHolder.m_vLineTop.setVisibility(ViewGroup.GONE);
        }

    }

    private void setViewContent(ItemHolder itemHolder, CIQuestionItem.Question data){

        itemHolder.m_seekBar.setTag(data.code);
        itemHolder.m_tvQuestion.setText(data.name);
        itemHolder.m_seekBar.setProgress(data.progress);
        CIProgressBarStyleHandler.setSeekBarStyle(data.progress,
                                                  itemHolder.m_seekBar,
                                                  itemHolder.m_tvScore,
                                                  true);
    }

    private void setViewParams(ItemHolder itemHolder){
        ViewScaleDef def = ViewScaleDef.getInstance(m_context);


        def.setViewSize(itemHolder.m_rlQuestion, 300, ViewGroup.LayoutParams.WRAP_CONTENT);
        def.setMargins(itemHolder.m_rlQuestion, 0, 10, 0, 0);

        def.setViewSize(itemHolder.m_tvQuestion, 270, ViewGroup.LayoutParams.WRAP_CONTENT);
        def.setTextSize(18, itemHolder.m_tvQuestion);

        def.setTextSize(18, itemHolder.m_tvScore);

        def.setViewSize(itemHolder.m_seekBar, 318, ViewGroup.LayoutParams.WRAP_CONTENT);
        def.setMargins(itemHolder.m_seekBar, 0, 21.3, 0, 21.3);
        int padding = def.getLayoutMinUnit(9);
        itemHolder.m_seekBar.setPadding(padding, 0, padding, 0);

        def.setViewSize(itemHolder.m_vLineTop, 300, 1);
        def.setViewSize(itemHolder.m_vLineBottom, 300, 1);

    }

    private void setListener(final ItemHolder itemHolder,final CIQuestionItem.Question data){
        itemHolder.m_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                CIProgressBarStyleHandler.setSeekBarStyle(progress,
                                                          seekBar,
                                                          itemHolder.m_tvScore,
                                                          false);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setProgressByRange(seekBar.getProgress(), seekBar);
                data.progress = seekBar.getProgress();
            }
        });
    }


    @Override
    public int getItemCount() {
        return m_data.questions.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder{
        public TextView m_tvQuestion;
        public TextView m_tvScore;
        public SeekBar  m_seekBar;
        public View     m_vLineTop;
        public View     m_vLineBottom;
        public RelativeLayout m_rlQuestion;

        public ItemHolder(View itemView) {
            super(itemView);
            m_tvQuestion    = (TextView) itemView.findViewById(R.id.tv_question);
            m_tvScore       = (TextView) itemView.findViewById(R.id.tv_score);
            m_rlQuestion    = (RelativeLayout) itemView.findViewById(R.id.rl_question);
            m_seekBar       = (SeekBar)  itemView.findViewById(R.id.seekBar);
            m_vLineTop      = itemView.findViewById(R.id.v_line_top);
            m_vLineBottom   = itemView.findViewById(R.id.v_line_bottom);
        }
    }


    public CIQuestionItem getData(){
        return m_data;
    }
}
