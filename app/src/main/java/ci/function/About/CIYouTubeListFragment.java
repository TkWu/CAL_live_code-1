package ci.function.About;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.ws.Models.entities.CIYouTubeDataEntity;
import ci.ws.Models.entities.CIYouTubeDataList;
import ci.function.ManageMiles.CIRedeemMilesWebViewActivity;
import ci.ui.YouTubePlayer.YouTubePlayerActvity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.CIProgressDialog;
import ci.ui.view.ImageHandle;
import ci.ws.Presenter.Listener.IYouTubeListListener;

/**
 * 目前使用在有關華航, 用來播放介紹華航的YouTube影片
 * Created by Kevin on 2016/4/10.
 */
public class CIYouTubeListFragment extends BaseFragment
    implements View.OnClickListener,
        IYouTubeListListener {


    private static class ViewHolder {

        RelativeLayout          rlayout_bg;
        YouTubeThumbnailView    imageView;
        ImageView               play;
        ImageView               gradient;
        RelativeLayout          rlayout_Summary;
        TextView                tvTitle;
        TextView                tvSummary;
    }

    private LinearLayout          m_llayout_content = null;
    private ArrayList<ViewHolder> m_arrViewList     = null;
    private LayoutInflater        m_inflater        = null;
    private ViewScaleDef          m_vScaleDef       = null;
    private View                  m_view            = null;
    private Context               m_context         = null;
    private CIYouTubeListPresenter m_presenter      = null;
    private Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_youtube_view;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {
        m_view = view;
        m_context = CIApplication.getContext();
        m_presenter = new CIYouTubeListPresenter(this);
        thumbnailViewToLoaderMap = new HashMap<>();
        m_inflater = inflater;
        m_presenter.fetchYouTubeData();
    }



    private void initYouTubeList(CIYouTubeDataList list) {
        String key  = m_context.getString(R.string.youtube_api_key);

        m_llayout_content = (LinearLayout) m_view.findViewById(R.id.llayout_bg);
        m_llayout_content.removeAllViews();

        m_vScaleDef = ViewScaleDef.getInstance(getActivity());

        m_arrViewList = new ArrayList<>();
        m_arrViewList.clear();

        for (final CIYouTubeDataEntity data : list) {

            ViewHolder viewholder = new ViewHolder();
            View vExtraServices = m_inflater.inflate(R.layout.layout_youtube_view, null);

            viewholder.rlayout_bg = (RelativeLayout) vExtraServices.findViewById(R.id.rlayout_bg);
            viewholder.imageView = (YouTubeThumbnailView) vExtraServices.findViewById(R.id.imageView);
            viewholder.play = (ImageView) vExtraServices.findViewById(R.id.iv_play);
            viewholder.gradient = (ImageView) vExtraServices.findViewById(R.id.iv_gradient);
            viewholder.rlayout_Summary = (RelativeLayout) vExtraServices.findViewById(R.id.rlayout_summary);
            viewholder.tvSummary = (TextView) vExtraServices.findViewById(R.id.tv_summary);
            viewholder.tvTitle = (TextView) vExtraServices.findViewById(R.id.tv_title);

            viewholder.imageView.setTag(data.videoId);
            viewholder.imageView.initialize(key, new ThumbnailListener());

            viewholder.imageView.getLayoutParams().height = m_vScaleDef.getLayoutHeight(140);

            m_vScaleDef.selfAdjustSameScaleView(viewholder.play, 64, 64);
            m_vScaleDef.setMargins(viewholder.play, 0, 50, 0, 0);

            m_vScaleDef.setViewSize(viewholder.gradient, ViewGroup.LayoutParams.MATCH_PARENT, 140);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)viewholder.tvTitle.getLayoutParams();
            layoutParams.height = m_vScaleDef.getLayoutHeight(24);
            layoutParams = (RelativeLayout.LayoutParams)viewholder.tvSummary.getLayoutParams();
            layoutParams.topMargin = m_vScaleDef.getLayoutHeight(8);
            viewholder.tvSummary.setMinHeight(m_vScaleDef.getLayoutHeight(37.3));

            viewholder.rlayout_Summary.getLayoutParams().height = m_vScaleDef.getLayoutHeight(90);
            int iwidth = m_vScaleDef.getLayoutWidth(20);
            int itop = m_vScaleDef.getLayoutHeight(10.7);
            int ibottom = m_vScaleDef.getLayoutHeight(10);
            viewholder.rlayout_Summary.setPadding(iwidth, itop, iwidth, ibottom);
            m_vScaleDef.setTextSize(20, viewholder.tvTitle);
            //viewholder.tvTitle.setText(data.title.replace("中華航空",""));
            viewholder.tvTitle.setText(data.title);
            m_vScaleDef.setTextSize(13, viewholder.tvSummary);

            String desc = data.description.replace("\n","");
            viewholder.tvSummary.setText(desc);

            viewholder.rlayout_bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra(UiMessageDef.BUNDLE_ACTIVITY_DATA_YOUTUBE_ID, data.videoId);
                    changeActivity(YouTubePlayerActvity.class, intent);
                }
            });


            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, m_vScaleDef.getLayoutHeight(230));
            params.topMargin = m_vScaleDef.getLayoutHeight(10);
            m_llayout_content.addView( vExtraServices, params );

            m_arrViewList.add(viewholder);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        m_presenter.interrupt();
    }

    private void changeActivity(Class clazz, Intent data){
        if(null == data){
            data = new Intent();
        }
        data.setClass(getActivity(),clazz);
        startActivity(data);
        getActivity().overridePendingTransition(R.anim.anim_right_in,R.anim.anim_left_out);
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

    public void releaseLoaders() {
        for (YouTubeThumbnailLoader loader : thumbnailViewToLoaderMap
                .values()) {
            loader.release();
        }
    }

     class ThumbnailListener implements
            YouTubeThumbnailView.OnInitializedListener,
            YouTubeThumbnailLoader.OnThumbnailLoadedListener {

        @Override
        public void onInitializationSuccess(YouTubeThumbnailView view,
                                            YouTubeThumbnailLoader loader) {
            loader.setOnThumbnailLoadedListener(this);
            thumbnailViewToLoaderMap.put(view,loader);
            String videoId = (String) view.getTag();
            loader.setVideo(videoId);
        }

        @Override
        public void onInitializationFailure(YouTubeThumbnailView view,
                                            YouTubeInitializationResult loader) {
        }

        @Override
        public void onThumbnailLoaded(YouTubeThumbnailView view,
                                      String videoId) {
            Bitmap bitmap = ((BitmapDrawable) view.getDrawable()).getBitmap();

            //影像圓角處理
            Bitmap roundedBitmap = ImageHandle.getRoundedCornerBitmap(bitmap,
                                                                      m_vScaleDef.getLayoutMinUnit(3),
                                                                      true, true, false, false);
            view.setImageBitmap(roundedBitmap);
        }

        @Override
        public void onThumbnailError(YouTubeThumbnailView view,
                                     YouTubeThumbnailLoader.ErrorReason errorReason) {
        }
    }

    private CIProgressDialog.CIProgressDlgListener m_progressDlgListener = new CIProgressDialog.CIProgressDlgListener() {
        @Override
        public void onBackPressed() {
            if(null != m_presenter){
                m_presenter.interrupt();
                getActivity().onBackPressed();
            }
        }
    };

    @Override
    public void showProgress() {
        showProgressDialog(m_progressDlgListener);
    }

    @Override
    public void hideProgress() {
        hideProgressDialog();
    }

    @Override
    public void onDataFetchFeild(String msg) {
        //not attached to Activity
        if(null == getActivity()){
            return;
        }
        showDialog(getString(R.string.warning), msg, getString(R.string.confirm));

    }

    @Override
    public void onDataBinded(CIYouTubeDataList list) {
        //not attached to Activity
        if(null == getActivity()){
            return;
        }
        initYouTubeList(list);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseLoaders();
    }
}
