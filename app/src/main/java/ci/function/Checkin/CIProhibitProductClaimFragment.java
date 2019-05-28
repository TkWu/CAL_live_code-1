package ci.function.Checkin;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.LruCache;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.Locale;

import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.ui.define.ViewScaleDef;

/**
 * Created by Ryan on 16/3/07.
 */
public class CIProhibitProductClaimFragment extends BaseFragment {

    private static int PROHIBIT_PRODUCT_CLAIM_COUNT = 4;

    public interface onCIProhibitProductClaimListener {

        void onCheckBoxClick ( boolean bClick );
    }

    private onCIProhibitProductClaimListener m_onListener = null;


    private RecyclerView        m_RecyclerView      = null;
    private View                m_vGradient         = null;

    private ImageView       m_imgView       = null;
    private RelativeLayout  m_rlayout       = null;
    private TextView        m_tvNotice      = null;
    private ImageButton     m_imgbtnCheckBox= null;

    private LruCache<Integer, Bitmap> m_lruCache = null;

    private ProhibitProductClaimRecyclerAdapter adapter = null;

    private ViewScaleDef m_vScaleDef = null;

    PreCachingLayoutManager m_CachingLayoutManager = null;
    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_checkin_prohibit_product_claim;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        m_RecyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        m_RecyclerView.setDrawingCacheEnabled(true);

        m_vGradient     = view.findViewById(R.id.vGradient);

        m_CachingLayoutManager = new PreCachingLayoutManager(getActivity());
        m_RecyclerView.setLayoutManager(m_CachingLayoutManager);

        //
        m_RecyclerView.getViewTreeObserver().addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener(){

            @Override
            public void onGlobalLayout() {
                m_RecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int visibleItemCount = m_RecyclerView.getChildCount();
                int totalItemCount = m_CachingLayoutManager.getItemCount();
                int firstVisibleItem = m_CachingLayoutManager.findFirstVisibleItemPosition();
                int iLastComp = m_CachingLayoutManager.findLastCompletelyVisibleItemPosition();

                if ( (visibleItemCount + firstVisibleItem) == totalItemCount && iLastComp == (totalItemCount-1) ){
                    //onLastItemShow(true);
                    m_vGradient.setVisibility(View.GONE);
                } else {
                    //onLastItemShow(false);
                    m_vGradient.setVisibility(View.VISIBLE);
                }

            }
        });

        m_RecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int visibleItemCount = m_RecyclerView.getChildCount();
                int totalItemCount = m_CachingLayoutManager.getItemCount();
                int firstVisibleItem = m_CachingLayoutManager.findFirstVisibleItemPosition();
                int iLastComp = m_CachingLayoutManager.findLastCompletelyVisibleItemPosition();

                if ( (visibleItemCount + firstVisibleItem) == totalItemCount && iLastComp == (totalItemCount-1) ){
                    //onLastItemShow(true);
                    m_vGradient.setVisibility(View.GONE);
                } else {
                    //onLastItemShow(false);
                    m_vGradient.setVisibility(View.VISIBLE);
                }
            }
        });
        //

        new ViewAsyncTask().execute();
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {

        m_vScaleDef = vScaleDef;

        adapter = new ProhibitProductClaimRecyclerAdapter();

        if ( null != m_onListener ){
            m_onListener.onCheckBoxClick(IsCheckBoxClick());
        }
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

    public void onSetListener( onCIProhibitProductClaimListener listener ){
        m_onListener = listener;
    }

    public Boolean IsCheckBoxClick(){
        if ( null != m_imgbtnCheckBox ){
            return m_imgbtnCheckBox.isSelected();
        }
        return false;
    }

    View.OnClickListener m_onClick = new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            if ( v.getId() == m_imgbtnCheckBox.getId() || v.getId() == m_rlayout.getId() ){
                onCheckBoxClick();
            }
        }
    };

    public void onCheckBoxClick(){

        if ( m_imgbtnCheckBox.isSelected() == false ){
            m_imgbtnCheckBox.setSelected(true);
            m_imgbtnCheckBox.setImageResource(R.drawable.btn_checkbox_on);
        } else {
            m_imgbtnCheckBox.setSelected(false);
            m_imgbtnCheckBox.setImageResource(R.drawable.btn_checkbox_off);
        }

        if ( null != m_onListener ){
            m_onListener.onCheckBoxClick(IsCheckBoxClick());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if( null != m_imgView ) {
            Drawable drawable = m_imgView.getDrawable();

            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

            if( null != bitmap && !bitmap.isRecycled() ){
                bitmap.recycle();
                bitmap = null;

                System.gc();
            }
        }

        clearImageCache();

    }

    private String getLanguage() {

        //判斷語系
        String strLanguage = CIApplication.getLanguageInfo().LoadLanguage();
        if ( Locale.CHINA.toString().equals(strLanguage) ){
            return "sc";
        } else if ( Locale.ENGLISH.toString().equals(strLanguage) ){
            return "en";
        } else if ( Locale.JAPAN.toString().equals(strLanguage) ){
            return "jpn";
        } else {
            return "tc";
        }
//        if ( CILanguageInfo.LANGUAGE[CILanguageInfo.eLANGUAGE.CHINA.ordinal()].toString().equals(strLanguage) ){
//            return "sc";
//        } else if ( CILanguageInfo.LANGUAGE[CILanguageInfo.eLANGUAGE.ENGLISH.ordinal()].toString().equals(strLanguage) ){
//            return "en";
//        } else if ( CILanguageInfo.LANGUAGE[CILanguageInfo.eLANGUAGE.JAPAN.ordinal()].toString().equals(strLanguage) ){
//            return "jpn";
//        } else {
//            return "tc";
//        }
    }

    private class ProhibitProductClaimRecyclerAdapter extends RecyclerView.Adapter {

        public ProhibitProductClaimRecyclerAdapter(){
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.layout_ui_baggage_info, null);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if( 4 == position ) {

                ((ViewHolder)holder).m_ivImage.setVisibility(View.GONE);
                ((ViewHolder)holder).m_rlCheckBox.setVisibility(View.VISIBLE);

                if(null == m_imgbtnCheckBox || null == m_rlayout || null == m_tvNotice) {

                    m_tvNotice = ((ViewHolder) holder).m_tvNotice;
                    m_imgbtnCheckBox = ((ViewHolder) holder).m_imgBtn;
                    m_rlayout = ((ViewHolder)holder).m_rlCheckBox;
                    m_rlayout.setOnClickListener(m_onClick);
                    m_tvNotice.setText(getString(R.string.prohibit_product_claim_notice));
//                    m_imgbtnCheckBox.setOnClickListener(m_onClick);

                    ((LinearLayout.LayoutParams)m_rlayout.getLayoutParams()).topMargin = m_vScaleDef.getLayoutWidth(14);
                    ((LinearLayout.LayoutParams)m_rlayout.getLayoutParams()).bottomMargin = m_vScaleDef.getLayoutWidth(50);

                    (m_imgbtnCheckBox.getLayoutParams()).height = m_vScaleDef.getLayoutMinUnit(24);
                    (m_imgbtnCheckBox.getLayoutParams()).width = m_vScaleDef.getLayoutMinUnit(24);

                    ((RelativeLayout.LayoutParams)m_tvNotice.getLayoutParams()).rightMargin = m_vScaleDef.getLayoutWidth(30);
                    ((RelativeLayout.LayoutParams)m_tvNotice.getLayoutParams()).leftMargin = m_vScaleDef.getLayoutWidth(62);
                    ((RelativeLayout.LayoutParams)m_imgbtnCheckBox.getLayoutParams()).leftMargin = m_vScaleDef.getLayoutWidth(30);
                    m_vScaleDef.setTextSize(16, m_tvNotice);


                    m_tvNotice.setText(getString(R.string.prohibit_product_claim_notice));
                    m_imgbtnCheckBox.setSelected(false);

                }
            } else {

                ((ViewHolder)holder).m_ivImage.setVisibility(View.VISIBLE);
                ((ViewHolder)holder).m_rlCheckBox.setVisibility(View.GONE);

                ImageView ivImage = ((ViewHolder)holder).m_ivImage;
                ((LinearLayout.LayoutParams)ivImage.getLayoutParams()).rightMargin  = m_vScaleDef.getLayoutWidth(10);
                ((LinearLayout.LayoutParams)ivImage.getLayoutParams()).leftMargin   = m_vScaleDef.getLayoutWidth(10);

                ((ViewHolder)holder).m_ivImage.setImageBitmap( getBitmapFromCache(position));

            }

        }

        @Override
        public int getItemCount() {

            //危險物品圖片 + 須知同意選項
            return (PROHIBIT_PRODUCT_CLAIM_COUNT+1);
        }

    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        TextView    m_tvNotice;
        ImageView   m_ivImage;
        ImageButton m_imgBtn;
        RelativeLayout m_rlCheckBox;

        public ViewHolder(View convertView) {
            super(convertView);
            m_tvNotice = (TextView)convertView.findViewById(R.id.tv_notice);
            m_ivImage = (ImageView)convertView.findViewById(R.id.iv_content);
            m_imgBtn  = (ImageButton)convertView.findViewById(R.id.imgbtn_checkbox);
            m_rlCheckBox = (RelativeLayout)convertView.findViewById(R.id.rlayout_checkbox);
        }
    }


    public class PreCachingLayoutManager extends LinearLayoutManager {
        private static final int DEFAULT_EXTRA_LAYOUT_SPACE = 600;
        private int extraLayoutSpace = -1;
        private Context context;

        public PreCachingLayoutManager(Context context) {
            super(context);
            this.context = context;
        }

        public PreCachingLayoutManager(Context context, int extraLayoutSpace) {
            super(context);
            this.context = context;
            this.extraLayoutSpace = extraLayoutSpace;
        }

        public PreCachingLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
            this.context = context;
        }

        public void setExtraLayoutSpace(int extraLayoutSpace) {
            this.extraLayoutSpace = extraLayoutSpace;
        }

        @Override
        protected int getExtraLayoutSpace(RecyclerView.State state) {
            if (extraLayoutSpace > 0) {
                return extraLayoutSpace;
            }
            return DEFAULT_EXTRA_LAYOUT_SPACE;
        }
    }

    private void initUI() {
        m_RecyclerView.setAdapter( adapter);
    }


    private class ViewAsyncTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideProgressDialog();
            initUI();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            for( int iIndex = 0 ; iIndex < PROHIBIT_PRODUCT_CLAIM_COUNT ; iIndex++ ) {
                Context context = CIApplication.getContext();
                int resourceId = context.getResources().
                        getIdentifier("img_dangerous_"+getLanguage()+"_"+ (iIndex+1), "drawable", context.getPackageName());

                Bitmap bitmap = createScaledBitmap( BitmapFactory.decodeResource(context.getResources(), resourceId) );
                addBitmapToCache(iIndex, bitmap);
            }

            return null;
        }
    }

    private Bitmap createScaledBitmap(Bitmap bitmap) {

        if( null == bitmap ) {
            return null;
        }

        int width = m_vScaleDef.getDisplayMetrics().widthPixels - ( m_vScaleDef.getLayoutWidth(10) * 2 );
        float fScale =  (float)width / bitmap.getWidth() ;
        int height = (int)(bitmap.getHeight() * fScale);

        Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, width, height,false);

        bitmap.recycle();

        return scaleBitmap;
    }

    private synchronized void addBitmapToCache(int iKey, Bitmap bitmap) {
        if( null == m_lruCache ) {
            m_lruCache = new LruCache<>(PROHIBIT_PRODUCT_CLAIM_COUNT);
        }

        if( null == getBitmapFromCache(iKey) ) {
            m_lruCache.put(iKey, bitmap);
        }
    }

    private synchronized Bitmap getBitmapFromCache(int iKey) {

        if( null == m_lruCache ) {
            return null;
        }

        return m_lruCache.get(iKey);
    }

    private synchronized void clearImageCache() {
        if( null != m_lruCache ) {
            for( int iIndex = 0 ; iIndex < PROHIBIT_PRODUCT_CLAIM_COUNT ; iIndex++ ) {
                Bitmap bitmap = getBitmapFromCache(iIndex);

                if( null != bitmap && !bitmap.isRecycled() ) {
                    bitmap.recycle();
                    bitmap = null;
                }
            }

            m_lruCache.evictAll();
        }
    }

}
