package ci.function.Main;

import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.function.Core.SLog;
import ci.function.HomePage.CIHomePageBgManager;
import ci.function.HomePage.item.CIHomeBgItem;
import ci.ui.define.ViewIdDef;
import ci.ui.define.ViewScaleDef;
import ci.function.Main.item.SideMenuItem;


public class RightDrawerFragment extends BaseFragment {


    public interface OnRightDrawerListener {

        void onRightMenuClick( SideMenuItem sideMenuItem );

        /**當首頁Menu 被點擊告知其他畫面*/
        void onGoHomePage();
    }

    public interface OnRightDrawerInterface {

        /**帶入被點擊的View Id, 當View id 相同 Menu 會有被選取的背景色*/
        void onSelectMenu( int iViewId );
    }

    private View m_vHeader = null;

    private ExpandableListView  m_expandableListView    = null;
    private SideMenuAdpater     m_Adpater               = null;

    private SideMenuAdpater.ChildHolder cHeaderHolder   = null;

    private OnRightDrawerListener m_Listener;
    private ArrayList<ArrayList<SideMenuItem>> m_MenuItemList = new ArrayList<ArrayList<SideMenuItem>>();

    //20190227 高層要的小圖示
    private Map<String, Integer> media_icon_res_ids = new HashMap<String, Integer>()
    {
        {
            put("fb", R.drawable.ic_media_fb);
            put("wechat", R.drawable.ic_media_wechat);
            put("weibo", R.drawable.ic_media_weibo);
            put("youtube", R.drawable.ic_media_youtube);
            put("ig", R.drawable.ic_media_ig);
            put("twitter", R.drawable.ic_media_twitter);
        }
    };
    ArrayList<Integer> icon_array =new ArrayList<Integer>();
    private View m_vFooter = null;
    private SideMenuAdpater.FooterHolder mFooterHolder   = null;
    //end 20190227 高層要的小圖示

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_right_drawer;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {


        RelativeLayout bglayout = (RelativeLayout)view.findViewById(R.id.rlayout_frame);
        CIHomeBgItem Homebg = CIHomePageBgManager.getInstance().getBackground();
        bglayout.setBackgroundResource(Homebg.getRightImageId());

        //m_vHeader = view.findViewById(R.id.vHeader);
        m_vHeader = LayoutInflater.from(getContext()).inflate(R.layout.layout_sidemenu_child_view, null);

        cHeaderHolder = new SideMenuAdpater.ChildHolder();
        cHeaderHolder.rlayout_bg= (RelativeLayout)m_vHeader.findViewById(R.id.rlayout_bg);
        cHeaderHolder.imgIcon   = (ImageView)m_vHeader.findViewById(R.id.img_icon);
        cHeaderHolder.tvText    = (TextView)m_vHeader.findViewById(R.id.tvText);
        cHeaderHolder.tvNum     = (TextView)m_vHeader.findViewById(R.id.tvNum);
        cHeaderHolder.rlayout_bg.setOnClickListener(m_onClick);

        //20190227 高層要的小圖示
        m_vFooter = LayoutInflater.from(getContext()).inflate(R.layout.layout_sidemenu_child_media_footer_view, null);
        mFooterHolder = new SideMenuAdpater.FooterHolder();

        mFooterHolder.vLine = (View)m_vFooter.findViewById(R.id.vline);
        mFooterHolder.rlayout_icon_table = (TableLayout) m_vFooter.findViewById(R.id.rlayout_icon_table);

        m_expandableListView = (ExpandableListView)view.findViewById(R.id.expandableListView);
        m_expandableListView.setFooterDividersEnabled(true);
        m_expandableListView.addHeaderView(m_vHeader);
        m_expandableListView.addFooterView(m_vFooter);

        m_MenuItemList = ViewIdDef.getInstance(getActivity()).getRightMenuList();
        m_Adpater = new SideMenuAdpater( getActivity(), m_MenuItemList );

        m_expandableListView.setAdapter(m_Adpater);
        m_expandableListView.setOnGroupClickListener(m_onGroupClicklistener);
        m_expandableListView.setOnChildClickListener(m_OnChildClickListener);
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {

        cHeaderHolder.tvText.setText(R.string.menu_title_homepage);

        cHeaderHolder.imgIcon.setImageResource(R.drawable.ic_home);
        cHeaderHolder.tvNum.setVisibility(View.INVISIBLE);
        cHeaderHolder.rlayout_bg.setBackgroundResource(R.drawable.bg_transparent_press_black20);

//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)m_vHeader.getLayoutParams();
//        params.topMargin = vScaleDef.getLayoutHeight(6);

        cHeaderHolder.rlayout_bg.getLayoutParams().height = vScaleDef.getLayoutHeight(50);

        RelativeLayout.LayoutParams rparams = (RelativeLayout.LayoutParams)cHeaderHolder.imgIcon.getLayoutParams();
        rparams.height = vScaleDef.getLayoutHeight(22);
        rparams.width  = vScaleDef.getLayoutHeight(22);
        rparams.leftMargin = vScaleDef.getLayoutWidth(24);

        rparams = (RelativeLayout.LayoutParams)cHeaderHolder.tvText.getLayoutParams();
        rparams.leftMargin = vScaleDef.getLayoutWidth(22);
        vScaleDef.setTextSize(16, cHeaderHolder.tvText);

        int iHeight = vScaleDef.getLayoutWidth(20);
        rparams = (RelativeLayout.LayoutParams)cHeaderHolder.tvNum.getLayoutParams();
        rparams.leftMargin = vScaleDef.getLayoutWidth(8.3);
        rparams.height = iHeight;
        vScaleDef.setTextSize(14, cHeaderHolder.tvNum);
        int iPadding = vScaleDef.getLayoutMinUnit(3.7);
//        cHeaderHolder.tvNum.setMinHeight(iHeight);
        cHeaderHolder.tvNum.setMinWidth(iHeight);
        cHeaderHolder.tvNum.setPadding(iPadding, iPadding, iPadding, iPadding);

        setFooterIconbyLanguage(mFooterHolder,vScaleDef);

        m_Adpater.setGroupGap(vScaleDef.getLayoutHeight(17.4));
        m_Adpater.setLeftMargin(vScaleDef.getLayoutWidth(22), vScaleDef.getLayoutWidth(24));
        int iSize = m_Adpater.getGroupCount();
        for ( int iIdx = 0; iIdx < iSize; iIdx++ ){
            m_expandableListView.expandGroup(iIdx);
        }

        m_Adpater.notifyDataSetChanged();
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
    protected void removeOtherHandleMessage() {

    }

    public OnRightDrawerInterface SetOnRightDrawerListener( OnRightDrawerListener listener ){

        this.m_Listener = listener;

        return m_onInterface;
    }

    /**@param iViewID 被點選的View Id*/
    public void onCheckMenuIsSelect( int iViewID ){

        int igSize = m_MenuItemList.size();
        for ( int igIdx = 0; igIdx < igSize; igIdx++ ){
            int iSize = m_MenuItemList.get(igIdx).size();
            for ( int iIdx = 0; iIdx < iSize; iIdx++ ){
                if ( iViewID == m_MenuItemList.get(igIdx).get(iIdx).iId ){
                    m_MenuItemList.get(igIdx).get(iIdx).bSelect = true;
                } else {
                    m_MenuItemList.get(igIdx).get(iIdx).bSelect = false;
                }
            }
        }

        if ( null != m_Adpater ){
            m_Adpater.notifyDataSetChanged();
        }
    }


    private void ChangeHeaderSelectMode( boolean bSelect ){
        cHeaderHolder.rlayout_bg.setSelected(bSelect);
        if ( bSelect ){
            cHeaderHolder.imgIcon.setImageResource(R.drawable.ic_home);
        } else {
            cHeaderHolder.imgIcon.setImageResource(R.drawable.ic_home_n);
        }
    }


    OnRightDrawerInterface m_onInterface = new OnRightDrawerInterface(){

        @Override
        public void onSelectMenu( int iViewId ){

            if ( ViewIdDef.VIEW_ID_HOME == iViewId ){
                ChangeHeaderSelectMode(true);
            } else {
                ChangeHeaderSelectMode(false);
            }
            onCheckMenuIsSelect(iViewId);
        }
    };


    View.OnClickListener m_onClick = new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            if ( v.getId() == cHeaderHolder.rlayout_bg.getId() ){
                ChangeHeaderSelectMode(true);
                //onCheckMenuIsSelect(ViewIdDef.VIEW_ID_HOME);
                if ( null != m_Listener ){
                    m_Listener.onGoHomePage();
                }
            }
        }
    };

    ExpandableListView.OnGroupClickListener m_onGroupClicklistener = new ExpandableListView.OnGroupClickListener(){

        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            return true;
        }
    };

    ExpandableListView.OnChildClickListener m_OnChildClickListener = new ExpandableListView.OnChildClickListener() {

        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

            SideMenuItem sideMenuItem = null;
            try {
                sideMenuItem = m_MenuItemList.get(groupPosition).get(childPosition);
            } catch ( Exception e ) {
                e.printStackTrace();
                return true;
            }

            ChangeHeaderSelectMode(false);

            if ( null != m_Listener && null != sideMenuItem ){
                onCheckMenuIsSelect( sideMenuItem.iId );
                m_Listener.onRightMenuClick(sideMenuItem);
            }

            return true;
        }
    };

    @Override
    public void onLanguageChangeUpdateUI() {

        if ( null != m_Adpater ){
            m_Adpater.notifyDataSetChanged();
        }

        cHeaderHolder.tvText.setText(R.string.menu_title_homepage);

    }

    //20190227 高層要的小圖示
    private void setFooterIconbyLanguage(SideMenuAdpater.FooterHolder _holder, ViewScaleDef vScaleDef) {
        Locale locale = CIApplication.getLanguageInfo().getLanguage_Locale();

        if (locale.toString().equals("zh_TW")) {
            icon_array.add(media_icon_res_ids.get("fb"));
            icon_array.add(media_icon_res_ids.get("ig"));
            icon_array.add(media_icon_res_ids.get("youtube"));
        }else if (locale.toString().equals("zh_CN")) {
            icon_array.add(media_icon_res_ids.get("weibo"));
            icon_array.add(media_icon_res_ids.get("wechat"));
        }else if (locale.toString().equals("en_US")) {
            icon_array.add(media_icon_res_ids.get("fb"));
            icon_array.add(media_icon_res_ids.get("wechat"));
            icon_array.add(media_icon_res_ids.get("weibo"));
            icon_array.add(media_icon_res_ids.get("youtube"));
        }else if (locale.toString().equals("ja_JP")) {
            icon_array.add(media_icon_res_ids.get("fb"));
            icon_array.add(media_icon_res_ids.get("ig"));
            icon_array.add(media_icon_res_ids.get("weibo"));
            icon_array.add(media_icon_res_ids.get("twitter"));
            icon_array.add(media_icon_res_ids.get("youtube"));
        }else{
            icon_array.add(media_icon_res_ids.get("fb"));
            icon_array.add(media_icon_res_ids.get("ig"));
            icon_array.add(media_icon_res_ids.get("youtube"));
        }

        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = 15;
        params.topMargin= 15;
        params.rightMargin= 44;
        params.height = vScaleDef.getLayoutHeight(22);
        params.width  = vScaleDef.getLayoutHeight(22);

        for (int row_num = 0; row_num <= (icon_array.size()/5); row_num++) {
            TableRow icon_row = new TableRow(getActivity());

            for(Integer res_id: icon_array) {
                ImageView icon_v=new ImageView(getActivity());
                icon_v.setId(res_id);
                icon_v.setImageResource(res_id);
                icon_v.setLayoutParams(params);

                icon_v.setOnClickListener(m_footer_onClick);
                if (icon_array.indexOf(res_id)/5 == row_num) {
                    icon_row.addView(icon_v);
                }else{
                    continue;
                }
            }
            _holder.rlayout_icon_table.addView(icon_row);
        }

    }


    View.OnClickListener m_footer_onClick = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            Locale locale = CIApplication.getLanguageInfo().getLanguage_Locale();

            switch(v.getId()) {
                case R.drawable.ic_media_fb:
                    openApp(getString(R.string.facebook_packagename), locale.toString());
                    break;
                case R.drawable.ic_media_wechat:
                    openApp(getString(R.string.wechat_packagename), locale.toString());
                    break;
                case R.drawable.ic_media_weibo:
                    openApp(getString(R.string.weibo_packagename), locale.toString());
                    break;
                case R.drawable.ic_media_youtube:
                    openApp(getString(R.string.youtube_packagename), locale.toString());
                    break;
                case R.drawable.ic_media_ig:
                    openApp(getString(R.string.instagram_packagename), locale.toString());
                    break;
                case R.drawable.ic_media_twitter:
                    openApp(getString(R.string.twitter_packagename), locale.toString());
                    break;
            }
        }
    };

    private void openApp(String packageName, String appLanguage){
        Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(packageName);
        switch (packageName) {
            case "com.facebook.katana":
                if (null != intent) {
                    try {
                        if (appLanguage.equals("ja_JP")) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.facebook_deeplink_JP))));
                        } else if (appLanguage.equals("en")) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.facebook_deeplink_US))));
                        } else {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.facebook_deeplink_global))));
                        }
                    } catch (Exception e) {
                        //normal web url
                    }
                } else {
                    Uri urif = null;
                    if (appLanguage.equals("ja_JP")) {
                        urif = Uri.parse(getString(R.string.facebook_fanpage_JP));
                    } else if (appLanguage.equals("en")) {
                        urif = Uri.parse(getString(R.string.facebook_fanpage_US));
                    } else {
                        urif = Uri.parse(getString(R.string.facebook_fanpage_global));
                    }
                    Intent intent_webf = new Intent(Intent.ACTION_VIEW, urif);
                    startActivity(intent_webf);
                }
                break;
            case "com.tencent.mm":
                Uri uriw = Uri.parse(getString(R.string.wechat_fanpage_global));
                Intent intent_webws = new Intent(Intent.ACTION_VIEW, uriw);
                startActivity(intent_webws);
                break;
            case "com.sina.weibo":
                if (null != intent) {
                    try {
                        Intent intent_weibo = new Intent(Intent.ACTION_VIEW);
                        intent_weibo.addCategory(Intent.CATEGORY_DEFAULT);
                        intent_weibo.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent_weibo.setData(Uri.parse(getString(R.string.weibo_deeplink_global)));
                        startActivity(intent_weibo);
                    } catch (Exception e) {
                        //normal web url
                    }
                } else {
                    Uri uriwb = Uri.parse(getString(R.string.weibo_fanpage_global));
                    Intent intent_webwb = new Intent(Intent.ACTION_VIEW, uriwb);
                    startActivity(intent_webwb);
                }

                break;
            case "com.google.android.youtube":
                if (null != intent) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.youtube_fanpage_global))));
                } else {
                    Uri uri = Uri.parse(getString(R.string.youtube_fanpage_global));
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                break;
            case "com.twitter.android":
                if (null != intent) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.twitter_deeplink_JP))));
                } else {
                    Uri uritw = Uri.parse(getString(R.string.twitter_fanpage_JP));
                    Intent intent_webtw = new Intent(Intent.ACTION_VIEW, uritw);
                    startActivity(intent_webtw);
                }
                break;
            case "com.instagram.android":
                if (null != intent) {
                    if (appLanguage.equals("ja_JP")) {
                        Intent intent_ig = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.instagram_deeplink_JP)));
                        intent_ig.setPackage(packageName);
                        startActivity(intent_ig);
                    } else if (appLanguage.equals("en")) {
                        Intent intent_ig = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.instagram_deeplink_US)));
                        intent_ig.setPackage(packageName);
                        startActivity(intent_ig);
                    }  else {
                        Intent intent_ig = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.instagram_deeplink_global)));
                        intent_ig.setPackage(packageName);
                        startActivity(intent_ig);
                    }
                }  else {
                    Uri uriig = null;
                    if (appLanguage.equals("ja_JP")) {
                        uriig = Uri.parse(getString(R.string.instagram_fanpage_JP));
                    } else if (appLanguage.equals("en")) {
                        uriig = Uri.parse(getString(R.string.instagram_fanpage_US));
                    } else {
                        uriig = Uri.parse(getString(R.string.instagram_fanpage_global));
                    }
                    Intent intent_igwb = new Intent(Intent.ACTION_VIEW, uriig);
                    startActivity(intent_igwb);
                }
                break;
        }
    }
    //end 20190227 高層要的小圖示
}
