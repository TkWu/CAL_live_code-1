package ci.ui.object;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.ui.define.ViewScaleDef;
import ci.ui.object.festival.IFestival;
import ci.ui.object.festival.SFestival;

/**
 * Created by Ryan on 16/4/20.
 */
public class CIProgressDialog extends Dialog {

    public interface CIProgressDlgListener {

        void onBackPressed();
    }

    //調整做法
    private Context         m_context       = null;
    private ViewScaleDef    m_ViewScaledef  = null;
    private ImageView       m_imgLoading    = null;

    private CIProgressDlgListener m_Listener = null;

    public CIProgressDialog(Context context, CIProgressDlgListener Listener) {
        super(context, R.style.CIProgressDialog);
        setCanceledOnTouchOutside(false);    //點擊外面dialog不會dismiss
        m_context = context;
        m_ViewScaledef = ViewScaleDef.getInstance((Activity) context);
        m_Listener = Listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_ui_progress_dialog);

        m_imgLoading = (ImageView)this.findViewById(R.id.img_loading);
        ImageView imgBackgroud = (ImageView)this.findViewById(R.id.img_background);
        ImageView imgLowestLayer = (ImageView)this.findViewById(R.id.img_lowest_layer);
        //m_imgLoading.setBackgroundResource(R.anim.anim_progress_dlg);
        //m_imgLoading.setImageResource(R.anim.anim_progress_dlg);
        AppInfo appInfo = new AppInfo();

        ArrayList<IFestival> arrayList = SFestival.getAll();
        for(IFestival festival:arrayList) {
            if(true == festival.isBetweenDate()) {
                imgBackgroud.setBackgroundResource(festival.getLoadingResourceId());
                showLimitedIcon(imgBackgroud, imgLowestLayer);
            }
        }

        m_ViewScaledef.selfAdjustSameScaleView(imgLowestLayer, 200, 200);
        m_ViewScaledef.selfAdjustSameScaleView(m_imgLoading, 200, 200);
        m_ViewScaledef.selfAdjustSameScaleView(imgBackgroud, 200, 200);

        TextView tvLoading = (TextView)this.findViewById(R.id.tv_loading);
        m_ViewScaledef.setTextSize(16, tvLoading);
    }

    private void showLimitedIcon(ImageView imgBackgroud, ImageView imgLowestLayer) {
        imgBackgroud.setVisibility(View.VISIBLE);
        imgLowestLayer.setVisibility(View.GONE);
    }

    public void onWindowFocusChanged(boolean hasFocus) {

        //Animation anim = AnimationUtils.loadAnimation(m_context, R.anim.anim_progress_dlg);
        //m_imgLoading.startAnimation(anim);

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(m_imgLoading,"rotation", 0f, 360f);
        objectAnimator.setInterpolator(null);
        objectAnimator.setDuration(1600);
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        objectAnimator.start();

//        AnimationDrawable AniDraw = (AnimationDrawable)m_imgLoading.getDrawable();
//        if ( null != AniDraw ) {
//            AniDraw.start();
//        }
    }

    @Override
    public void onBackPressed() {
        //通知畫面 Dlg 執行了 BackPress
        if ( null != m_Listener ){
            m_Listener.onBackPressed();
        }
        //
//        if(true == m_isBack){
//            super.onBackPressed();
//        } else {
//            //不做任何事，按返回鍵不得被取消
//        }
    }


}
