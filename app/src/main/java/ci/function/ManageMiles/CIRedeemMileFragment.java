package ci.function.ManageMiles;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ClipDrawable;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.Base.BaseFragment;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;

/**
 * 目前使用在里程管理, 用來哩程兌換服務, 可根據Type來決定要顯示的版型
 * Created by Kevin on 2016/3/10.
 * extra services頁面也需要用到,使用前須先接好Parameter. Ling - 2016/3/30
 */
public class CIRedeemMileFragment extends BaseFragment
    implements View.OnClickListener{

    public static final int PROMOTION_TRIPS = 0;
    public static final int PROMOTION_MILES = 1;

    public interface onPromotionParameter {

        int getPromotionType();

        /**取得title字串*/
        String getTitleText();

        /**取得內文字串*/
        String getSummaryText();
    }

    private static class ViewHolder {

        RelativeLayout rlayout_bg;
        ImageView      imageView;
        ImageView      imageViewBlur;
        RelativeLayout rlayout_Summary;
        RelativeLayout rlayout_WebOnly;
        TextView       tvTitle;
        TextView       tvSummary;
        TextView       tvMiles;
        TextView       tvWebOnly;
        RelativeLayout rlayout_eligble_to_redeem;
        TextView       tvEligbleToRedeem;
        ImageView      ivArrow;
        RelativeLayout rlayout_miles;
        TextView       tvNeedMoreMiles;
        ImageView      img_top_view;
    }

    private LinearLayout          m_llayout_content = null;
    private onPromotionParameter  m_onParameter     = null;
    private ArrayList<ViewHolder> m_arrViewList     = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_redeem_mile_view;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        m_llayout_content = (LinearLayout) view.findViewById(R.id.llayout_bg);


        ViewScaleDef vScaleDef = ViewScaleDef.getInstance(getActivity());

        m_arrViewList = new ArrayList<>();
        m_arrViewList.clear();

        Bitmap bitmap = ImageHandle.getLocalBitmap(getActivity(), R.drawable.sample_redeem_mile, 1);

        if (null != m_onParameter){//extra service

            ViewHolder viewholder = new ViewHolder();
            View vExtraServices = inflater.inflate(R.layout.layout_redeem_mile_view, null);

            viewholder.rlayout_bg       = (RelativeLayout) vExtraServices.findViewById(R.id.rlayout_bg);
            viewholder.imageView        = (ImageView)vExtraServices.findViewById(R.id.imageView);
            viewholder.imageViewBlur    = (ImageView)vExtraServices.findViewById(R.id.imageView_blur);
            viewholder.rlayout_Summary  = (RelativeLayout)vExtraServices.findViewById(R.id.rlayout_summary);
            viewholder.tvSummary        = (TextView)vExtraServices.findViewById(R.id.tv_summary);
            viewholder.tvTitle          = (TextView)vExtraServices.findViewById(R.id.tv_title);

            viewholder.rlayout_eligble_to_redeem = (RelativeLayout)vExtraServices.findViewById(R.id.rlayout_eligble_to_redeem);
            viewholder.tvEligbleToRedeem = (TextView)vExtraServices.findViewById(R.id.tv_eligble_to_redeem);
            viewholder.ivArrow          = (ImageView)vExtraServices.findViewById(R.id.iv_ic_list_arrow);

            viewholder.rlayout_miles    = (RelativeLayout)vExtraServices.findViewById(R.id.rlayout_miles);
            viewholder.tvNeedMoreMiles  = (TextView)vExtraServices.findViewById(R.id.tv_need_more_miles);
            viewholder.img_top_view     = (ImageView)vExtraServices.findViewById(R.id.img_top_view);
            viewholder.tvMiles          = (TextView)vExtraServices.findViewById(R.id.tv_miles);

            viewholder.rlayout_WebOnly  = (RelativeLayout)vExtraServices.findViewById(R.id.rlayout_web_only);
            viewholder.tvWebOnly        = (TextView)vExtraServices.findViewById(R.id.tv_web_only);

            //extra service不顯示獎項兌換資格.里程數. webonly
            viewholder.rlayout_eligble_to_redeem.setVisibility(View.GONE);
            viewholder.imageViewBlur.setVisibility(View.GONE);
            viewholder.rlayout_miles.setVisibility(View.GONE);
            viewholder.rlayout_WebOnly.setVisibility(View.GONE);
            viewholder.tvMiles.setVisibility(View.GONE);

            viewholder.imageView.getLayoutParams().height = vScaleDef.getLayoutHeight(140);
            viewholder.imageViewBlur.getLayoutParams().height = vScaleDef.getLayoutHeight(140);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)viewholder.tvTitle.getLayoutParams();
            layoutParams.height = vScaleDef.getLayoutHeight(24);
            layoutParams = (RelativeLayout.LayoutParams)viewholder.tvSummary.getLayoutParams();
            layoutParams.topMargin = vScaleDef.getLayoutHeight(8);
            viewholder.tvSummary.setMinHeight(vScaleDef.getLayoutHeight(37.3));

            viewholder.rlayout_Summary.getLayoutParams().height = vScaleDef.getLayoutHeight(90);
            int iwidth = vScaleDef.getLayoutWidth(20);
            int itop = vScaleDef.getLayoutHeight(10.7);
            int ibottom = vScaleDef.getLayoutHeight(10);
            viewholder.rlayout_Summary.setPadding(iwidth, itop, iwidth, ibottom);
            vScaleDef.setTextSize(20, viewholder.tvTitle);
            viewholder.tvTitle.setText(m_onParameter.getTitleText());
            vScaleDef.setTextSize(13, viewholder.tvSummary);
            viewholder.tvSummary.setText(m_onParameter.getSummaryText());
            vScaleDef.setTextSize(20, viewholder.tvMiles);

            viewholder.rlayout_eligble_to_redeem.getLayoutParams().height   = vScaleDef.getLayoutHeight(30);
            viewholder.rlayout_eligble_to_redeem.getLayoutParams().width    = ViewGroup.LayoutParams.MATCH_PARENT;
            vScaleDef.setTextSize(13, viewholder.tvEligbleToRedeem);
            vScaleDef.selfAdjustSameScaleView(viewholder.ivArrow, 24, 24);
            vScaleDef.setMargins(viewholder.ivArrow, 0, 0, 20.3, 0);

            viewholder.rlayout_miles.getLayoutParams().height   = vScaleDef.getLayoutHeight(30);
            viewholder.rlayout_miles.getLayoutParams().width    = ViewGroup.LayoutParams.MATCH_PARENT;
            vScaleDef.setTextSize(13, viewholder.tvNeedMoreMiles);
            ((RelativeLayout.LayoutParams)viewholder.tvNeedMoreMiles.getLayoutParams()).rightMargin = vScaleDef.getLayoutWidth(44);
            viewholder.img_top_view.getLayoutParams().height = vScaleDef.getLayoutMinUnit(30);
            viewholder.img_top_view.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;

            vScaleDef.setViewSize(viewholder.rlayout_WebOnly, 100, 24);
            vScaleDef.setMargins(viewholder.rlayout_WebOnly, 0, 10, 10, 0);
            vScaleDef.setTextSize(13, viewholder.tvWebOnly);

            //影像圓角處理
            Bitmap roundedBitmap = ImageHandle.getRoundedCornerBitmap(bitmap,
                    vScaleDef.getLayoutMinUnit(3),
                    true, true, false, false);
            viewholder.imageView.setImageBitmap(roundedBitmap);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, vScaleDef.getLayoutHeight(230));
            params.topMargin = vScaleDef.getLayoutHeight(10);
            m_llayout_content.addView( vExtraServices, params );

            m_arrViewList.add(viewholder);
        }else {//redeem mile

            int iSize = 3;

            for (int iIdx = 0; iIdx < iSize; iIdx++) {

                ViewHolder viewholder = new ViewHolder();
                View vRedeem = inflater.inflate(R.layout.layout_redeem_mile_view, null);

                if(0 == iIdx){
                    vRedeem.setOnClickListener(this);
                }

                viewholder.rlayout_bg       = (RelativeLayout) vRedeem.findViewById(R.id.rlayout_bg);
                viewholder.imageView        = (ImageView)vRedeem.findViewById(R.id.imageView);
                viewholder.imageViewBlur    = (ImageView)vRedeem.findViewById(R.id.imageView_blur);
                viewholder.rlayout_Summary  = (RelativeLayout)vRedeem.findViewById(R.id.rlayout_summary);
                viewholder.tvSummary        = (TextView)vRedeem.findViewById(R.id.tv_summary);
                viewholder.tvTitle          = (TextView)vRedeem.findViewById(R.id.tv_title);

                viewholder.rlayout_eligble_to_redeem = (RelativeLayout)vRedeem.findViewById(R.id.rlayout_eligble_to_redeem);
                viewholder.tvEligbleToRedeem = (TextView)vRedeem.findViewById(R.id.tv_eligble_to_redeem);
                viewholder.ivArrow          = (ImageView)vRedeem.findViewById(R.id.iv_ic_list_arrow);

                viewholder.rlayout_miles    = (RelativeLayout)vRedeem.findViewById(R.id.rlayout_miles);
                viewholder.tvNeedMoreMiles  = (TextView)vRedeem.findViewById(R.id.tv_need_more_miles);
                viewholder.img_top_view     = (ImageView)vRedeem.findViewById(R.id.img_top_view);
                viewholder.tvMiles          = (TextView)vRedeem.findViewById(R.id.tv_miles);

                viewholder.rlayout_WebOnly  = (RelativeLayout)vRedeem.findViewById(R.id.rlayout_web_only);
                viewholder.tvWebOnly        = (TextView)vRedeem.findViewById(R.id.tv_web_only);
                if ( iIdx == 0 ){
                    //eligble to redeem
                    viewholder.rlayout_eligble_to_redeem.setVisibility(View.VISIBLE);
                    viewholder.rlayout_miles.setVisibility(View.INVISIBLE);
                    viewholder.rlayout_WebOnly.setVisibility(View.GONE);
                } else if(iIdx == 1){
                    //need more miles
                    viewholder.rlayout_eligble_to_redeem.setVisibility(View.INVISIBLE);
                    viewholder.rlayout_miles.setVisibility(View.VISIBLE);
                    viewholder.rlayout_WebOnly.setVisibility(View.GONE);
                } else {
                    //web only
                    viewholder.rlayout_eligble_to_redeem.setVisibility(View.INVISIBLE);
                    viewholder.rlayout_miles.setVisibility(View.VISIBLE);
                    viewholder.rlayout_WebOnly.setVisibility(View.VISIBLE);
                }

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)viewholder.tvTitle.getLayoutParams();
                layoutParams.height = vScaleDef.getLayoutHeight(24);
                layoutParams = (RelativeLayout.LayoutParams)viewholder.tvSummary.getLayoutParams();
                layoutParams.topMargin = vScaleDef.getLayoutHeight(8);
                viewholder.tvSummary.setMinHeight(vScaleDef.getLayoutHeight(37.3));

                viewholder.imageView.getLayoutParams().height = vScaleDef.getLayoutHeight(140);
                viewholder.imageViewBlur.getLayoutParams().height = vScaleDef.getLayoutHeight(140);
                viewholder.rlayout_Summary.getLayoutParams().height = vScaleDef.getLayoutHeight(90);
                int iwidth = vScaleDef.getLayoutWidth(20);
                int itop = vScaleDef.getLayoutHeight(10.7);
                int ibottom = vScaleDef.getLayoutHeight(10);
                viewholder.rlayout_Summary.setPadding(iwidth, itop, iwidth, ibottom);
                vScaleDef.setTextSize(20, viewholder.tvTitle);
                vScaleDef.setTextSize(13, viewholder.tvSummary);
                vScaleDef.setTextSize(20, viewholder.tvMiles);

                viewholder.rlayout_eligble_to_redeem.getLayoutParams().height   = vScaleDef.getLayoutHeight(30);
                viewholder.rlayout_eligble_to_redeem.getLayoutParams().width    = ViewGroup.LayoutParams.MATCH_PARENT;
                vScaleDef.setTextSize(13, viewholder.tvEligbleToRedeem);
                vScaleDef.selfAdjustSameScaleView(viewholder.ivArrow, 24, 24);
                vScaleDef.setMargins(viewholder.ivArrow, 0, 0, 20.3, 0);

                viewholder.rlayout_miles.getLayoutParams().height   = vScaleDef.getLayoutHeight(30);
                viewholder.rlayout_miles.getLayoutParams().width    = ViewGroup.LayoutParams.MATCH_PARENT;
                vScaleDef.setTextSize(13, viewholder.tvNeedMoreMiles);
                ((RelativeLayout.LayoutParams)viewholder.tvNeedMoreMiles.getLayoutParams()).rightMargin = vScaleDef.getLayoutWidth(44);
                viewholder.img_top_view.getLayoutParams().height = vScaleDef.getLayoutMinUnit(30);
                viewholder.img_top_view.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;

                vScaleDef.setViewSize(viewholder.rlayout_WebOnly, 100, 24);
                vScaleDef.setMargins(viewholder.rlayout_WebOnly, 0, 10, 10, 0);
                vScaleDef.setTextSize(13,viewholder.tvWebOnly);

                //設定尚需哩程文字
                String msg = String.format(getString(R.string.need_xxx_miles),5000);
                viewholder.tvNeedMoreMiles.setText(msg);
                //設定需多少哩可兌換的文字
                viewholder.tvMiles.setText("17,777" + getString(R.string.miles));
                //影像模糊處理
                Bitmap blurBitmap = ImageHandle.BlurBuilder(getActivity(), bitmap, 13, 0.15f);
                viewholder.imageViewBlur.setImageBitmap(blurBitmap);
                ClipDrawable clipDrawable = new ClipDrawable(viewholder.imageViewBlur.getDrawable(), Gravity.BOTTOM,ClipDrawable.VERTICAL);
                viewholder.imageViewBlur.setImageDrawable(clipDrawable);
                viewholder.imageViewBlur.getDrawable().setLevel(2142);

                //影像圓角處理
                Bitmap roundedBitmap = ImageHandle.getRoundedCornerBitmap(bitmap , vScaleDef.getLayoutMinUnit(3));
                viewholder.imageView.setImageBitmap(roundedBitmap);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, vScaleDef.getLayoutHeight(230));
                params.topMargin = vScaleDef.getLayoutHeight(10);
                m_llayout_content.addView( vRedeem, params);

                m_arrViewList.add(viewholder);
            }
        }
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {

    }


    @Override
    protected void setOnParameterAndListener(View view) {

    }

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {

    }

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    public void onLanguageChangeUpdateUI() {}

    public void uiSetParameterListener(onPromotionParameter onParameter) {
        m_onParameter = onParameter;
    }

    @Override
    public void onClick(View v) {
        changeActivityForResult(CIRedeemMilesWebViewActivity.class);
    }

    /**
     * 轉換Activity
     * @param clazz     目標activity名稱
     */
    private void changeActivityForResult(Class clazz){
        Intent intent = new Intent();
        intent.setClass(getActivity(), clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, UiMessageDef.REQUEST_CODE_LOGIN);
        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }
}
