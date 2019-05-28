package ci.function.MyTrips.Detail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.function.Core.CIApplication;
import ci.function.Main.item.CIQuestionItem;
import ci.ui.define.ViewScaleDef;

import static ci.function.Main.item.CIProgressBarStyleHandler.setSeekBarStyle;

/**
 * Created by kevincheng on 2017/5/9.
 */

public class CIQuestionnaireResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private CIQuestionItem m_data;
    private Context        m_context;
    public CIQuestionnaireResultAdapter(CIQuestionItem data) {
        m_data = data;
        m_context = CIApplication.getContext();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.layout_questionnaire_result_view, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
        ItemHolder itemHolder= (ItemHolder)holder;
        CIQuestionItem.Question data = m_data.questions.get(position);
        setViewVisiable(itemHolder, position);
        setViewContent(itemHolder,  data);
        setViewParams(itemHolder);

    }

    private void setViewVisiable(ItemHolder itemHolder,int position){
        if(0 >= position){
            itemHolder.m_vLineTop.setVisibility(ViewGroup.VISIBLE);
        } else {
            itemHolder.m_vLineTop.setVisibility(ViewGroup.GONE);
        }

    }

    private void setViewContent(ItemHolder itemHolder, CIQuestionItem.Question data){
        itemHolder.m_progressBar.setTag(data.code);
        itemHolder.m_question.setText(data.name);
        itemHolder.m_progressBar.setProgress(data.progress);
        setSeekBarStyle(data.progress,
                        itemHolder.m_progressBar,
                        null,
                        true);
    }

    private void setViewParams(ItemHolder itemHolder){
        ViewScaleDef def = ViewScaleDef.getInstance(m_context);

        def.setViewSize(itemHolder.m_question, 180, ViewGroup.LayoutParams.WRAP_CONTENT);
        def.setTextSize(18, itemHolder.m_question);

        def.setViewSize(itemHolder.m_linearLayout, 300, ViewGroup.LayoutParams.WRAP_CONTENT);
        def.setPadding(itemHolder.m_linearLayout, 0, 20.3, 0, 20.3);

        def.setMargins(itemHolder.m_relativeLayout, 11, 0, 0, 0);

        def.setViewSize(itemHolder.m_progressBar, 100, ViewGroup.LayoutParams.WRAP_CONTENT);

        def.setViewSize(itemHolder.m_vLineTop, 300, 1);
        def.setViewSize(itemHolder.m_vLineBottom, 300, 1);

    }


    @Override
    public int getItemCount() {
        return m_data.questions.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder{
        public TextView     m_question;
        public ProgressBar  m_progressBar;
        public View         m_vLineTop;
        public View         m_vLineBottom;
        public LinearLayout m_linearLayout;
        public RelativeLayout m_relativeLayout;

        public ItemHolder(View itemView) {
            super(itemView);
            m_question          = (TextView) itemView.findViewById(R.id.tv_question);
            m_progressBar       = (ProgressBar)  itemView.findViewById(R.id.progressBar);
            m_linearLayout      = (LinearLayout)  itemView.findViewById(R.id.ll_progress);
            m_relativeLayout    = (RelativeLayout)  itemView.findViewById(R.id.rl_progressBar);
            m_vLineTop          = itemView.findViewById(R.id.v_line_top);
            m_vLineBottom       = itemView.findViewById(R.id.v_line_bottom);
        }
    }


}
