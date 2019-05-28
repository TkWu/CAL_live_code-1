package ci.function.HomePage;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.Base.BaseFragment;
import ci.ui.WebView.CIWebViewActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;

/**
 * 目前使用在首頁, 用來呈現促銷資訊以及哩程兌換服務, 可根據Type來決定要顯示的版型
 * Created by Ryan on 2016/2/24.
 */
public class CIPromotionFragment extends BaseFragment implements View.OnClickListener{

    public static final int PROMOTION_TRIPS = 0;
    public static final int PROMOTION_MILES = 1;

    public interface onPromotionParameter {

        public int getPromotionType();
    }

    private static class ViewHolder {

        RelativeLayout  rlayout_bg;
        ImageView       imageView;
        RelativeLayout  rlayout_Summary;
        TextView        tvTitle;
        TextView        tvSummary;
        TextView        tvMiles;
        RelativeLayout  rlayout_price;
        TextView        tvPrice;
        TextView        tvUnit;
        RelativeLayout  rlayout_miles;
        TextView        tvTag;
        ImageView       imgArrow;
    }

    private LinearLayout m_llayout_content = null;
    private onPromotionParameter m_onParameter = null;
    private ArrayList<ViewHolder> m_arrViewList= null;


    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_promotion_view;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        m_llayout_content = (LinearLayout)view.findViewById(R.id.llayout_bg);


        ViewScaleDef vScaleDef = ViewScaleDef.getInstance(getActivity());

        m_arrViewList = new ArrayList<>();
        m_arrViewList.clear();
        int iSize = 2;

        Bitmap bitmap = ImageHandle.getLocalBitmap(getActivity(), R.drawable.sample_redeem_mile, 1);

        for ( int iIdx =0; iIdx < iSize; iIdx++ ){

            ViewHolder viewholder = new ViewHolder();
            View vPromotion = inflater.inflate( R.layout.layout_promotion_view_trip ,null );

            viewholder.rlayout_bg   = (RelativeLayout)vPromotion.findViewById(R.id.rlayout_bg);
            viewholder.rlayout_bg.setOnClickListener(this);
            viewholder.imageView    = (ImageView)vPromotion.findViewById(R.id.imageView);
            viewholder.rlayout_Summary = (RelativeLayout)vPromotion.findViewById(R.id.rlayout_summary);
            viewholder.tvSummary    = (TextView)vPromotion.findViewById(R.id.tv_summary);
            viewholder.tvTitle      = (TextView)vPromotion.findViewById(R.id.tv_title);

            viewholder.rlayout_price= (RelativeLayout)vPromotion.findViewById(R.id.rlayout_price);
            viewholder.tvPrice      = (TextView)vPromotion.findViewById(R.id.tv_price);
            viewholder.tvUnit       = (TextView)vPromotion.findViewById(R.id.tv_dollar);

            viewholder.rlayout_miles= (RelativeLayout)vPromotion.findViewById(R.id.rlayout_miles);
            viewholder.tvTag        = (TextView)vPromotion.findViewById(R.id.tv_tag);
            viewholder.imgArrow     = (ImageView)vPromotion.findViewById(R.id.img_arrow);
            viewholder.tvMiles      = (TextView)vPromotion.findViewById(R.id.tv_miles);

            if ( iIdx == 0 ){
                //PROMOTION_TRIPS
                viewholder.rlayout_price.setVisibility(View.VISIBLE);
                viewholder.rlayout_miles.setVisibility(View.INVISIBLE);
                viewholder.tvMiles.setVisibility(View.INVISIBLE);
            } else {
                //PROMOTION_MILES
                viewholder.rlayout_price.setVisibility(View.INVISIBLE);
                viewholder.rlayout_miles.setVisibility(View.VISIBLE);
                viewholder.tvMiles.setVisibility(View.VISIBLE);
            }

            viewholder.imageView.getLayoutParams().height = vScaleDef.getLayoutHeight(140);
//            viewholder.rlayout_Summary.getLayoutParams().height = vScaleDef.getLayoutHeight(90);
            int iwidth = vScaleDef.getLayoutWidth(20);
            int itop = vScaleDef.getLayoutHeight(10.6);
            int ibottom = vScaleDef.getLayoutHeight(10);
            viewholder.rlayout_Summary.setPadding(iwidth, itop, iwidth, ibottom);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)viewholder.tvTitle.getLayoutParams();
            layoutParams.height = vScaleDef.getLayoutHeight(24);
            layoutParams = (RelativeLayout.LayoutParams)viewholder.tvSummary.getLayoutParams();
            layoutParams.topMargin = vScaleDef.getLayoutHeight(8);
            viewholder.tvSummary.setMinHeight(vScaleDef.getLayoutHeight(37.3));

            vScaleDef.setTextSize(20, viewholder.tvTitle);
            vScaleDef.setTextSize(13, viewholder.tvSummary);
            vScaleDef.setTextSize(20, viewholder.tvMiles);

            viewholder.rlayout_price.getLayoutParams().height   = vScaleDef.getLayoutHeight(30);
            viewholder.rlayout_price.getLayoutParams().width    = vScaleDef.getLayoutHeight(156.7);
            viewholder.rlayout_price.setPadding(vScaleDef.getLayoutWidth(20), 0, vScaleDef.getLayoutWidth(20), 0);
            vScaleDef.setTextSize(20, viewholder.tvPrice);
            vScaleDef.setTextSize(12, viewholder.tvUnit);
            viewholder.tvPrice.setMaxWidth(vScaleDef.getLayoutWidth(85));

            viewholder.rlayout_miles.getLayoutParams().height   = vScaleDef.getLayoutHeight(30);
            viewholder.rlayout_miles.getLayoutParams().width    = vScaleDef.getLayoutHeight(160);
            viewholder.rlayout_miles.setPadding(vScaleDef.getLayoutWidth(20), 0, 0, 0);
            vScaleDef.setTextSize(16, viewholder.tvTag);
            ((RelativeLayout.LayoutParams)viewholder.tvTag.getLayoutParams()).rightMargin = vScaleDef.getLayoutWidth(10);
            ((RelativeLayout.LayoutParams)viewholder.imgArrow.getLayoutParams()).rightMargin = vScaleDef.getLayoutWidth(10);
            viewholder.imgArrow.getLayoutParams().height = vScaleDef.getLayoutMinUnit(24);
            viewholder.imgArrow.getLayoutParams().width = vScaleDef.getLayoutMinUnit(24);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, vScaleDef.getLayoutHeight(230));
            params.topMargin = vScaleDef.getLayoutHeight(10);
            m_llayout_content.addView( vPromotion, params);

            Bitmap roundedBitmap = ImageHandle.getRoundedCornerBitmap(
                    bitmap,
                    vScaleDef.getLayoutMinUnit(3),
                    true, true, false, false);
            viewholder.imageView.setImageBitmap(roundedBitmap);

            m_arrViewList.add(viewholder);
        }
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {}

    @Override
    protected void setOnParameterAndListener(View view) {}

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {}

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    public void onLanguageChangeUpdateUI() {}

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.putExtra(UiMessageDef.BUNDLE_WEBVIEW_TITLE_TEXT_TAG, getString(R.string.app_name));
        intent.setClass(getActivity(), CIWebViewActivity.class);
        startActivity(intent);

        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }
}
