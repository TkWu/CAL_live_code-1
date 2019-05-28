package ci.function.BaggageTrack;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.chinaairlines.mobile30.R;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;

import ci.function.Core.CIApplication;
import ci.function.Main.BaseActivity;
import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;
import ci.ui.toast.CIToastView;
import ci.ui.view.NavigationBar;

/**掃描行李追蹤條碼
 * 格式 Code128、ITF */
public class CIScannerBaggageTagActivity extends BaseActivity implements BarcodeCallback {


    private NavigationBar.onNavigationbarParameter m_onNavigationParameter = new NavigationBar.onNavigationbarParameter() {

        @Override
        public Boolean GetToolbarType() {
            return false;
        }

        @Override
        public String GetTitle() {
            return m_Context.getString(R.string.menu_title_baggage_tracking);
        }
    };

    private NavigationBar.onNavigationbarListener m_onNavigationbarListener = new NavigationBar.onNavigationbarListener() {

        @Override
        public void onRightMenuClick() {}

        @Override
        public void onLeftMenuClick() {}

        @Override
        public void onBackClick() {
            onBackPressed();
        }

        @Override
        public void onDeleteClick() {}

        @Override
        public void onDemoModeClick() {}
    };


    public static final String BUNDLE_KEY_TAG = "Tag";

    private NavigationBar   m_Navigationbar = null;

    private DecoratedBarcodeView m_barcodeScannerView;

    private int m_iTag;

    private String m_strBarcodeResult;

    private long m_lShowTime = 0;


    private NavigationBar.onNavigationbarInterface m_onNavigationbarInterface = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_scanner_baggage_tag;
    }

    @Override
    protected void initialLayoutComponent() {

        m_Navigationbar = (NavigationBar) findViewById(R.id.toolbar);

        m_barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
        m_barcodeScannerView.decodeContinuous(this);

    }

    @Override
    protected void setTextSizeAndLayoutParams(ViewScaleDef vScaleDef) {
        vScaleDef.selfAdjustAllView(findViewById(R.id.root));
    }

    @Override
    protected void setOnParameterAndListener() {
        m_onNavigationbarInterface = m_Navigationbar.uiSetParameterListener(
                m_onNavigationParameter,
                m_onNavigationbarListener);
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

    @Override
    protected void onLanguageChangeUpdateUI() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onResume() {
        super.onResume();
        m_barcodeScannerView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        m_barcodeScannerView.pause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return m_barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }


    @Override
    public void barcodeResult(BarcodeResult result) {
        if(result.getText() == null) {
            // Prevent duplicate scans
            return;
        }

        m_strBarcodeResult = result.getText();

        if ( CIApplication.getBaggageInfo().checkBaggageTagFormat(m_strBarcodeResult) ){

            Intent intent = new Intent();
            intent.putExtra(UiMessageDef.BUNDLE_BAGGAGE_CONTENT_NUMBER, m_strBarcodeResult);
            setResult(RESULT_OK, intent);
            onBackPressed();
        } else {
            if ( System.currentTimeMillis() - m_lShowTime > 3000 ){
                m_lShowTime = System.currentTimeMillis();
                CIToastView.makeText(m_Context, getString(R.string.baggage_tag_ref_hint) + getString(R.string.member_login_input_correvt_format_msg)).show();
            }
        }
    }

    @Override
    public void possibleResultPoints(List<ResultPoint> resultPoints) {

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_left_in, R.anim.anim_right_out);
    }

}