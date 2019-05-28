package ci.function.Main;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import ci.function.Core.SLog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.chinaairlines.mobile30.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import ci.function.Base.BaseFragment;
import ci.function.Core.CIApplication;
import ci.function.HomePage.CIHomePageBgManager;
import ci.function.HomePage.item.CIHomeBgItem;
import ci.function.Main.item.SideMenuItem;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewIdDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;
import ci.ui.view.ImageHandle;
import ci.ws.define.CICardType;


public class LeftDrawerFragment extends BaseFragment
        implements View.OnClickListener{

    public interface OnLeftDrawerListener {

        void onLeftMenuClick( SideMenuItem sideMenuItem );

        void onLogoutClick();

        void onLoginClick();

        void onSignUpClick();

        void onBeMemberClick();
    }

    public interface OnLeftDrawerInterface {

        /**帶入被點擊的View Id, 當View id 相同 Menu 會有被選取的背景色*/
        void onSelectMenu( int iViewId );

        /** 變更會員小卡*/
        void changeView( int iViewFlipper, String strUserName, String strUserPhotoUrl);
    }

    private OnLeftDrawerListener m_Listener;

    private ExpandableListView  m_expandableListView = null;
    private SideMenuAdpater     m_Adpater   = null;
    private ViewFlipper         m_viewFlipper = null;
    private Button              m_btnLogout = null,
                                m_btnLogin  = null,
                                m_btnSignUp = null,
                                m_btnBecomeAMember = null;
    private TextView            m_tvHello  = null,
                                m_tvWelcome  = null,
                                m_tvUserName  = null,
                                m_tvUserNameDynasty = null,
                                m_tvCardNumber = null,
                                m_tvMiles = null;
    private ImageView           m_ivPhoto   = null,
                                m_ivPhotoDynasty = null,
                                m_ivCard = null;

    private static final int    REQUEST_CODE_SELECT_PHOTO   = 1714;
    private static final String IMAGE_TYPE                  = "image/*";
    private static final String FILENAME_EXTENSION          = ".png";
    private static final String FOLDER_NAME                 = "/Profile";

    private ArrayList<ArrayList<SideMenuItem>> m_MenuItemList = new ArrayList<ArrayList<SideMenuItem>>();

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_left_drawer;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        RelativeLayout bglayout = (RelativeLayout)view.findViewById(R.id.rlayout_frame);
        CIHomeBgItem Homebg = CIHomePageBgManager.getInstance().getBackground();
        bglayout.setBackgroundResource(Homebg.getLeftImageId());

        m_expandableListView = (ExpandableListView)view.findViewById(R.id.expandableListView);

        m_MenuItemList = ViewIdDef.getInstance(getActivity()).getLeftMenuList();

        m_Adpater = new SideMenuAdpater( getActivity(), m_MenuItemList);

        m_expandableListView.setAdapter(m_Adpater);
        m_expandableListView.setOnGroupClickListener(m_onGroupClicklistener);
        m_expandableListView.setOnChildClickListener(m_OnChildClickListener);

        m_viewFlipper = (ViewFlipper) view.findViewById(R.id.viewFlipper);

        View vNotLogin  = View.inflate(getActivity(), R.layout.layout_sidemenu_user_not_login, null);
        View vTemporary = View.inflate(getActivity(), R.layout.layout_sidemenu_user_temporary, null);
        View vDynasty   = View.inflate(getActivity(), R.layout.layout_sidemenu_user_dynasty, null);
        m_viewFlipper.addView(vNotLogin, ViewIdDef.PERSONAL_TYPE_NOT_LOGIN);
        m_viewFlipper.addView(vTemporary, ViewIdDef.PERSONAL_TYPE_TEMPORARY);
        m_viewFlipper.addView(vDynasty, ViewIdDef.PERSONAL_TYPE_DYNASTY_FLYER);

        m_viewFlipper.setDisplayedChild(ViewIdDef.PERSONAL_TYPE_NOT_LOGIN);

//        m_viewFlipper.setInAnimation(getActivity(), R.anim.anim_left_in);
//        m_viewFlipper.setOutAnimation(getActivity(), R.anim.anim_right_out);
//        m_viewFlipper.setFlipInterval(3000);
//        m_viewFlipper.startFlipping();

        /** ---------- PERSONAL VIEW BY NOT LOGIN ---------- */
        m_tvHello   = (TextView)m_viewFlipper.getChildAt(ViewIdDef.PERSONAL_TYPE_NOT_LOGIN).findViewById(R.id.tv_hello);
        m_tvWelcome   = (TextView)m_viewFlipper.getChildAt(ViewIdDef.PERSONAL_TYPE_NOT_LOGIN).findViewById(R.id.tv_welcome);
        m_btnLogin  = (Button)m_viewFlipper.getChildAt(ViewIdDef.PERSONAL_TYPE_NOT_LOGIN).findViewById(R.id.btn_login);
        m_btnLogin.setOnClickListener(this);
        m_btnSignUp = (Button)m_viewFlipper.getChildAt(ViewIdDef.PERSONAL_TYPE_NOT_LOGIN).findViewById(R.id.btn_signup);
        m_btnSignUp.setOnClickListener(this);

        /** ---------- PERSONAL VIEW BY TEMPORARY ---------- */
        m_tvUserName = (TextView)m_viewFlipper.getChildAt(ViewIdDef.PERSONAL_TYPE_TEMPORARY).findViewById(R.id.tv_username);
        m_ivPhoto   = (ImageView)m_viewFlipper.getChildAt(ViewIdDef.PERSONAL_TYPE_TEMPORARY).findViewById(R.id.iv_userphoto);
        m_btnBecomeAMember = (Button)m_viewFlipper.getChildAt(ViewIdDef.PERSONAL_TYPE_TEMPORARY).findViewById(R.id.btn_become_a_member);
        m_btnBecomeAMember.setOnClickListener(this);

        /** ---------- PERSONAL VIEW BY DYNASTY FLYER ---------- */
        m_ivPhotoDynasty   = (ImageView)m_viewFlipper.getChildAt(ViewIdDef.PERSONAL_TYPE_DYNASTY_FLYER).findViewById(R.id.iv_userphoto_dynasty);
        //TODO: 2017.1.4 CR - CI會員點選頭像可更換大頭貼
        m_ivPhotoDynasty.setOnClickListener(this);
        m_tvUserNameDynasty = (TextView)m_viewFlipper.getChildAt(ViewIdDef.PERSONAL_TYPE_DYNASTY_FLYER).findViewById(R.id.tv_username_dynasty);
        m_ivCard  = (ImageView)m_viewFlipper.getChildAt(ViewIdDef.PERSONAL_TYPE_DYNASTY_FLYER).findViewById(R.id.iv_card);
        m_tvCardNumber = (TextView)m_viewFlipper.getChildAt(ViewIdDef.PERSONAL_TYPE_DYNASTY_FLYER).findViewById(R.id.tv_card_number);
        //m_tvMiles = (TextView)m_viewFlipper.getChildAt(ViewIdDef.PERSONAL_TYPE_DYNASTY_FLYER).findViewById(R.id.tv_miles);

        /** ------------------------------------------------------------------- */

        m_btnLogout = (Button)view.findViewById(R.id.btn_logout);
        m_btnLogout.setOnClickListener(this);

        if (m_viewFlipper.getDisplayedChild() == ViewIdDef.PERSONAL_TYPE_NOT_LOGIN){
            m_btnLogout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_logout:
                m_Listener.onLogoutClick();
                break;
            case R.id.btn_login:
                m_Listener.onLoginClick();
                break;
            case R.id.btn_signup:
                m_Listener.onSignUpClick();
                break;
            case R.id.btn_become_a_member:
                m_Listener.onBeMemberClick();
                break;
            case R.id.iv_userphoto_dynasty:

                //TODO: 2017.1.4 CR - CI會員點選頭像可更換大頭貼
                //開啟相簿選擇照片
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(IMAGE_TYPE);

                if (intent.resolveActivity(getActivity().getPackageManager()) == null) {
                   SLog.e("[LeftDrawer]", "error: Cannot launch photo library");
                    return;
                }

                try {
                    startActivityForResult(intent, REQUEST_CODE_SELECT_PHOTO);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                   SLog.e("[LeftDrawer]", "error: Cannot launch photo library");
                }
        }
    }

    private void changeActivity(Class clazz){
        Intent intent = new Intent();
        intent.setClass(getActivity(), clazz);
        startActivity(intent);

        //測試切換activity滑入滑出動畫
        getActivity().overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {

        //175 扣掉menu group間隔16 以及分隔線1.4 = 157.6
        RelativeLayout.LayoutParams rParams = (RelativeLayout.LayoutParams)m_viewFlipper.getLayoutParams();
        rParams.height = vScaleDef.getLayoutHeight( 157.6 );

        /** ---------- PERSONAL VIEW BY NOT LOGIN ---------- */
        LinearLayout llText = (LinearLayout) m_viewFlipper.getChildAt(ViewIdDef.PERSONAL_TYPE_NOT_LOGIN).findViewById(R.id.ll_text);
        rParams = (RelativeLayout.LayoutParams) llText.getLayoutParams();
        rParams.topMargin = vScaleDef.getLayoutHeight(32);
        rParams.height = vScaleDef.getLayoutHeight(64);
        rParams.width = vScaleDef.getLayoutWidth(290);

        LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) m_tvHello.getLayoutParams();
        lParams.height = vScaleDef.getLayoutHeight(32);
        vScaleDef.setTextSize( 20, m_tvHello);

        lParams = (LinearLayout.LayoutParams) m_tvWelcome.getLayoutParams();
        lParams.height = vScaleDef.getLayoutHeight(32);
        vScaleDef.setTextSize( 20, m_tvWelcome);

        LinearLayout llButton = (LinearLayout) m_viewFlipper.getChildAt(ViewIdDef.PERSONAL_TYPE_NOT_LOGIN).findViewById(R.id.ll_button);
        rParams = (RelativeLayout.LayoutParams) llButton.getLayoutParams();
        rParams.topMargin = vScaleDef.getLayoutHeight(123);
        //rParams.bottomMargin = vScaleDef.getLayoutHeight(18);

        lParams = (LinearLayout.LayoutParams) m_btnLogin.getLayoutParams();
        lParams.height = vScaleDef.getLayoutHeight(34);
        lParams.width = vScaleDef.getLayoutWidth(100);
        vScaleDef.setTextSize( 16, m_btnLogin);

        lParams = (LinearLayout.LayoutParams) m_btnSignUp.getLayoutParams();
        lParams.leftMargin = vScaleDef.getLayoutWidth(10);
        lParams.height = vScaleDef.getLayoutHeight(34);
        lParams.width = vScaleDef.getLayoutWidth(100);
        vScaleDef.setTextSize( 16, m_btnSignUp);

        /** ---------- PERSONAL VIEW BY TEMPORARY ---------- */
        RelativeLayout rlImg = (RelativeLayout) m_viewFlipper.getChildAt(ViewIdDef.PERSONAL_TYPE_TEMPORARY).findViewById(R.id.rl_photo);
        rParams = (RelativeLayout.LayoutParams) rlImg.getLayoutParams();
        rParams.topMargin = vScaleDef.getLayoutHeight(18);
        rParams.width = vScaleDef.getLayoutMinUnit(64);
        rParams.height = vScaleDef.getLayoutMinUnit(64);

        ImageView ivDefault = (ImageView) m_viewFlipper.getChildAt(ViewIdDef.PERSONAL_TYPE_TEMPORARY).findViewById(R.id.iv_default);
        rParams = (RelativeLayout.LayoutParams) ivDefault.getLayoutParams();
        rParams.width = vScaleDef.getLayoutMinUnit(64);
        rParams.height = vScaleDef.getLayoutMinUnit(64);

        rParams = (RelativeLayout.LayoutParams) m_ivPhoto.getLayoutParams();
        rParams.width = vScaleDef.getLayoutMinUnit(60);
        rParams.height = vScaleDef.getLayoutMinUnit(60);

        rParams = (RelativeLayout.LayoutParams) m_tvUserName.getLayoutParams();
        rParams.topMargin = vScaleDef.getLayoutHeight(90);
        rParams.height = vScaleDef.getLayoutHeight(25.3);
        rParams.width = vScaleDef.getLayoutWidth(260);
        vScaleDef.setTextSize( 20, m_tvUserName);

        rParams = (RelativeLayout.LayoutParams) m_btnBecomeAMember.getLayoutParams();
        rParams.topMargin = vScaleDef.getLayoutHeight(123);
        //rParams.bottomMargin = vScaleDef.getLayoutHeight(18);
        rParams.height = vScaleDef.getLayoutHeight(34);
        rParams.width = vScaleDef.getLayoutWidth(220);
        vScaleDef.setTextSize( 16, m_btnBecomeAMember);

        /** ---------- PERSONAL VIEW BY DYNASTY FLYER ---------- */

        RelativeLayout rlImgDynasty = (RelativeLayout) m_viewFlipper.getChildAt(ViewIdDef.PERSONAL_TYPE_DYNASTY_FLYER).findViewById(R.id.rl_photo_dynasty);
        rParams = (RelativeLayout.LayoutParams) rlImgDynasty.getLayoutParams();
        rParams.topMargin = vScaleDef.getLayoutHeight(18);
        rParams.width = vScaleDef.getLayoutMinUnit(64);
        rParams.height = vScaleDef.getLayoutMinUnit(64);

        ImageView ivDefaultDynasty = (ImageView) m_viewFlipper.getChildAt(ViewIdDef.PERSONAL_TYPE_DYNASTY_FLYER).findViewById(R.id.iv_default_dynasty);
        rParams = (RelativeLayout.LayoutParams) ivDefaultDynasty.getLayoutParams();
        rParams.width = vScaleDef.getLayoutMinUnit(64);
        rParams.height = vScaleDef.getLayoutMinUnit(64);

        rParams = (RelativeLayout.LayoutParams) m_ivPhotoDynasty.getLayoutParams();
        rParams.width = vScaleDef.getLayoutMinUnit(60);
        rParams.height = vScaleDef.getLayoutMinUnit(60);

        rParams = (RelativeLayout.LayoutParams) m_tvUserNameDynasty.getLayoutParams();
        rParams.topMargin = vScaleDef.getLayoutHeight(90);
        rParams.height = vScaleDef.getLayoutHeight(25.3);
        rParams.width = vScaleDef.getLayoutWidth(260);
        vScaleDef.setTextSize( 20, m_tvUserNameDynasty);

        RelativeLayout rlDetail = (RelativeLayout) m_viewFlipper.getChildAt(ViewIdDef.PERSONAL_TYPE_DYNASTY_FLYER).findViewById(R.id.rl_detail);
        rParams = (RelativeLayout.LayoutParams) rlDetail.getLayoutParams();
        rParams.topMargin = vScaleDef.getLayoutHeight(133);
        rParams.width = vScaleDef.getLayoutWidth(260);
        //rParams.bottomMargin = vScaleDef.getLayoutHeight(24);

        lParams = (LinearLayout.LayoutParams) m_ivCard.getLayoutParams();
        //rParams.rightMargin = vScaleDef.getLayoutWidth(6);
        lParams.width = vScaleDef.getLayoutMinUnit(26.7);
        lParams.height = vScaleDef.getLayoutMinUnit(18);

        //rParams = (RelativeLayout.LayoutParams) m_tvCardNumber.getLayoutParams();
        //rParams.rightMargin = vScaleDef.getLayoutWidth(16.2);
        vScaleDef.setTextSize( 13, m_tvCardNumber);

//        View v = (View) m_viewFlipper.getChildAt(ViewIdDef.PERSONAL_TYPE_DYNASTY_FLYER).findViewById(R.id.view);
//        rParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
//        rParams.height  = vScaleDef.getLayoutHeight(16);
//        rParams.width = vScaleDef.getLayoutWidth(1);

//        rParams = (RelativeLayout.LayoutParams) m_tvMiles.getLayoutParams();
//        rParams.leftMargin = vScaleDef.getLayoutWidth(16.2);
//        vScaleDef.setTextSize( 13, m_tvMiles);

        /** ------------------------------------------------------------------- */

        rParams = (RelativeLayout.LayoutParams) m_btnLogout.getLayoutParams();
        rParams.bottomMargin = vScaleDef.getLayoutHeight(29.7);
        rParams.height = vScaleDef.getLayoutHeight(34);
        rParams.width = vScaleDef.getLayoutWidth(140);
        vScaleDef.setTextSize( 16, m_btnLogout);

        m_Adpater.setGroupGap( vScaleDef.getLayoutHeight(33.4) );

        int iSize = m_Adpater.getGroupCount();
        for ( int iIdx = 0; iIdx < iSize; iIdx++ ){
            m_expandableListView.expandGroup(iIdx);
        }
        m_Adpater.notifyDataSetChanged();
    }

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

    public OnLeftDrawerInterface SetOnLeftDrawerListenerAndParameter( OnLeftDrawerListener listener){

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


    OnLeftDrawerInterface m_onInterface = new OnLeftDrawerInterface(){

        @Override
        public void onSelectMenu( int iViewId ) {

            onCheckMenuIsSelect(iViewId);
        }

        @Override
        public void changeView(int iViewFlipper, String strUserName, String strUserPhotoUrl) {

            m_viewFlipper.setDisplayedChild(iViewFlipper);

            switch (iViewFlipper){
                case ViewIdDef.PERSONAL_TYPE_NOT_LOGIN:
//                    m_btnLogin  = (Button)m_viewFlipper.getCurrentView().findViewById(R.id.btn_login);
//                    m_btnLogin.setOnClickListener(LeftDrawerFragment.this);
//                    m_btnSignUp = (Button)m_viewFlipper.getCurrentView().findViewById(R.id.btn_signup);
//                    m_btnSignUp.setOnClickListener(LeftDrawerFragment.this);

                    m_btnLogout.setVisibility(View.GONE);
                    break;
                case ViewIdDef.PERSONAL_TYPE_TEMPORARY:
                    m_tvUserName.setText(strUserName);

                    if (strUserPhotoUrl.length() > 0){
                        new DownloadImageTask(m_ivPhoto).execute(strUserPhotoUrl);
                    }else {
                        m_ivPhotoDynasty.setImageBitmap(null);
                    }

                    m_btnLogout.setVisibility(View.VISIBLE);
                    break;
                case ViewIdDef.PERSONAL_TYPE_DYNASTY_FLYER:

                    //卡片需設圓角
                    String strCardType = CIApplication.getLoginInfo().GetCardType();

                    int iResourceId = -1;

                    switch (strCardType) {
                        case CICardType.DYNA:
                            iResourceId = R.drawable.img_gray_card;
                            break;
                        case CICardType.EMER:
                            iResourceId = R.drawable.img_green_card;
                            break;
                        case CICardType.GOLD:
                            iResourceId = R.drawable.img_gold_card;
                            break;
                        case CICardType.PARA:
                            iResourceId = R.drawable.img_blue_card;
                            break;
                    }

                    if ( -1 != iResourceId ){
                        Bitmap bitmap = ImageHandle.getLocalBitmap(getActivity(), iResourceId ,1);

                        int iRadius = ViewScaleDef.getInstance(getActivity()).getLayoutMinUnit(1);
                        if ( 1 > iRadius ){
                            iRadius = 1;
                        }
                        Bitmap bmRadius = ImageHandle.getRoundedCornerBitmap( bitmap, iRadius );
                        m_ivCard.setImageBitmap(bmRadius);

                        bitmap.recycle();
//                        System.gc();
                    }

                    m_tvUserNameDynasty.setText(strUserName);

                    //確認sd card是否有儲存該會員的圖像, 有的話就顯示
                    checkMemberPhoto(strUserPhotoUrl);

                    m_tvCardNumber.setText(CIApplication.getLoginInfo().GetUserMemberCardNo());
                    //m_tvMiles.setText(String.format(getString(R.string.menu_miles), CIApplication.getLoginInfo().GetMiles()));

                    m_btnLogout.setVisibility(View.VISIBLE);
                    break;
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

            if ( null != m_Listener && null != sideMenuItem ){
                onCheckMenuIsSelect(sideMenuItem.iId );
                m_Listener.onLeftMenuClick(sideMenuItem);
            }

            return true;
        }
    };

    @Override
    public void onLanguageChangeUpdateUI() {

        if ( null != m_Adpater ){
            m_Adpater.notifyDataSetChanged();
        }

        m_btnLogout.setText(getString(R.string.menu_log_out));

        m_tvHello.setText(getString(R.string.input_detail_hello));
        m_tvWelcome.setText(getString(R.string.menu_welcome_tag));
        m_btnLogin.setText(getString(R.string.menu_log_in));
        m_btnSignUp.setText(getString(R.string.menu_sign_up));

        m_btnBecomeAMember.setText(getString(R.string.menu_become_a_dynasty_flyer));

        //m_tvMiles.setText(String.format(getString(R.string.menu_miles), CIApplication.getLoginInfo().GetMiles()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //TODO: 2017.1.4 CR - CI會員點選頭像可更換大頭貼
        if ( requestCode == REQUEST_CODE_SELECT_PHOTO && resultCode == Activity.RESULT_OK ){
            Uri selectedimg = data.getData();

            Bitmap bitmap = null;
            if(selectedimg == null){  //部分手機拍照後不會自動儲存，所以會拿不到uri
                bitmap = (Bitmap) (data.getExtras() == null ? null : data.getExtras().get("data"));
            }else {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedimg);
                } catch (IOException e) {
                    e.printStackTrace();
                   SLog.e("[LeftDrawer]", "onActivityResult getBitmap error");
                }
            }

            if ( null != bitmap ){
                try {
                    setImgRounded(m_ivPhotoDynasty, bitmap);
                }catch (Exception e){
                    e.printStackTrace();
                   SLog.e("[LeftDrawer]", "onActivityResult setImgRounded error");
                }
            }else {
               SLog.e("[LeftDrawer]", "onActivityResult bitmap null");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //大頭貼圓框處理
    private void setImgRounded(ImageView imageView, Bitmap bitmap){
        //圖片須裁切成正方形
        Bitmap bmCrop = ImageHandle.ImageCrop( bitmap, true );

        //縮小至 64 x 64
        Bitmap bmResized = Bitmap.createScaledBitmap(bmCrop,
                ViewScaleDef.getInstance(getActivity()).getLayoutHeight(64),
                ViewScaleDef.getInstance(getActivity()).getLayoutHeight(64), true);

        //圓角處理
        Bitmap bmResult = ImageHandle.getRoundedCornerBitmap(bmResized,
                ViewScaleDef.getInstance(getActivity()).getLayoutMinUnit(100));

        imageView.setImageBitmap(bmResult);

        ImageHandle.recycleBitmap(bitmap);
        ImageHandle.recycleBitmap(bmCrop);
        ImageHandle.recycleBitmap(bmResized);

        //如果為會員, 需儲存會員頭像
        if( true == CIApplication.getLoginInfo().GetLoginStatus()
                && CIApplication.getLoginInfo().GetLoginType().equals(UiMessageDef.BUNDLE_LOGIN_TYPE_DYNASTY_FLYER)){
            saveMemberPhoto(bmResult);
        }
    }

    //儲存會員圖像至sd card
    private void saveMemberPhoto(Bitmap bitmap){

        String fname = CIApplication.getLoginInfo().GetUserMemberCardNo() + FILENAME_EXTENSION;
        String root = AppInfo.getInstance(getActivity()).getFilePath() + FOLDER_NAME;

        File myDir = new File(root);
        if  (!myDir.exists() && !myDir.isDirectory()) {
            //資料夾不存在 建立資料夾
            myDir.mkdir();
        }

        File file = new File (myDir, fname);
        if (file.exists()) {
            //已有同名檔案, 刪除舊檔
            file.delete();
        }

        try {
            file.createNewFile();

            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** 取用Profile邏輯：
     1.判斷資料夾路徑sdcard/packagename/file/Profile/是否存在, 存在則繼續2. 不存在則跳至3.
     2.判斷該會員圖像是否存在, 存在就直接使用.顯示;
     3.如果資料夾路徑或圖像檔不存在, 就看是否有社群登入照片(可能該會員是用社群登入), 有就直接使用並存Profile, 否則就不設圖像
    */
    private void checkMemberPhoto(String strUserPhotoUrl){

        String fname = CIApplication.getLoginInfo().GetUserMemberCardNo() + FILENAME_EXTENSION;
        String root = AppInfo.getInstance(getActivity()).getFilePath() + FOLDER_NAME;

        File myDir = new File(root);
        //確認資料夾是否存在
        if  (!myDir.exists() && !myDir.isDirectory()) {
            //不存在
        } else {
            //存在

            //確認是否有該會員的照片檔
            File file = new File (myDir, fname);
            if (file.exists()) {
                //顯示會員圖片
                m_ivPhotoDynasty.setImageBitmap(
                        BitmapFactory.decodeFile(root+"/"+fname));

                return;
            }else {
                //刪除Profile 文件夾
                deleteAllFile(myDir);
            }
        }

        //如果社群有照片就直接用
        if (strUserPhotoUrl.length() > 0) {
            new DownloadImageTask(m_ivPhotoDynasty).execute(strUserPhotoUrl);
        }else {
            m_ivPhotoDynasty.setImageBitmap(null);
        }
    }

    //刪除資料夾及裡面所有檔案
    private void deleteAllFile(File path) {
        if (!path.exists()) {
            return;
        }
        if (path.isFile()) {
            path.delete();
            return;
        }
        File[] files = path.listFiles();
        for (int i = 0; i < files.length; i++) {
            deleteAllFile(files[i]);
        }
        path.delete();
    }

    //個人照片下載並進行處理(社群照片)
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {

            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            if ( true ==  AppInfo.getInstance(getActivity()).bIsNetworkAvailable() ){

                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                   SLog.e("[LeftDrawer]", "error:"+ e.getMessage());
                    e.printStackTrace();
                }
                return mIcon11;
            }else {
                return null;
            }

        }

        protected void onPostExecute(Bitmap result) {
            if (null == result){
                m_ivPhotoDynasty.setImageBitmap(null);
                return;
            }

            setImgRounded(bmImage, result);
        }
    }
}
