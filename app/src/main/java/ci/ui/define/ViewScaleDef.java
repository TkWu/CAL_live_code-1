package ci.ui.define;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import ci.ui.view.StepHorizontalView;

public class ViewScaleDef
{

	private static ViewScaleDef m_instance            = null;


	public static final double	DEF_TEXT_SIZE_20		= 20;
	public static final double	DEF_TEXT_SIZE_16		= 16;

	private int		m_iMaxLayoutHeightWeight            = 0;	//長度單位最大值
	private int		m_iMaxLayoutWidthWeight             = 0;	//寬度單位最大值
	private float	m_fHeightUnit                       = 0;	//長度單位
	private float	m_fWidthUnit                        = 0;	//寬度單位
	private float	m_fFontUnit                         = 0;	//字型單位

	private float	m_fScreenScaleDensity               = 0;    //螢幕密度
	private int		m_iStatusBarHeight                  = 0;	//狀態列高度

	private DisplayMetrics m_Dm                         = null; //取的螢幕解析度

	public static ViewScaleDef getInstance(Context context)
	{
		if (null == m_instance) {
			m_instance = new ViewScaleDef();
			m_instance.initial(context);
		}

		return m_instance;
	}

	@SuppressLint("NewApi")
	private void initial(Context context)
	{
		//設定「長度、寬度單位」最大值
		m_iMaxLayoutHeightWeight = context.getResources().getInteger(R.integer.activity_weight_sum_vertical);
		m_iMaxLayoutWidthWeight = context.getResources().getInteger(R.integer.activity_weight_sum_horizontal);

		//設定「長度、寬度、字型單位」
		m_Dm = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		windowManager.getDefaultDisplay().getMetrics(m_Dm);

		m_fHeightUnit = (float) m_Dm.heightPixels / m_iMaxLayoutHeightWeight;
		m_fWidthUnit = (float) m_Dm.widthPixels / m_iMaxLayoutWidthWeight;
		m_fFontUnit = Math.min(m_fHeightUnit, m_fWidthUnit);

		m_fScreenScaleDensity = m_Dm.scaledDensity;

		//m_iStatusBarHeight = iGetStatusBarHeight(context);
	}

	/**
	 * @param dHeight 高度比例，參數範圍為「1 ~ 640」
	 * @return 取出經長寬比運算出的高度
	 */
	public int getLayoutHeight(double dHeight) {
		return (int) (dHeight * m_fHeightUnit); //m_fFontUnit 高的UNIT一般較小(因按鍵列跟系統列的關係)，所以同U數要設正方形要用同一個getLayout，不然會變扁
	}

	/**
	 * @param dWeight 寬度比例，參數範圍為「1 ~ 360」
	 * @return 取出經長寬比運算出的寬度
	 */
	public int getLayoutWidth(double dWeight) {
		return (int) (dWeight * m_fWidthUnit); //m_fWidthUnit); // 高的UNIT一般較小(因按鍵列跟系統列的關係)，所以同U數要設正方形要用同一個getLayout，不然會變扁
	}

	/**
	 * @param dWeight 單位比例，抓取寬高比最小的值，請小心使用
	 * @return 取出經長寬比運算出的寬度
	 */
	public int getLayoutMinUnit(double dWeight) {
		return (int) (dWeight * m_fFontUnit); // 高的UNIT一般較小(因按鍵列跟系統列的關係)，所以同U數要設正方形要用同一個getLayout，不然會變扁
	}

	/**
	 * @param iTextLength 總行數
	 * @return 取出經長寬比運算出的總行高度
	 */
	public int getLayoutHeightByTextSize(double dTextSizeDefine, int iTextLength)
	{
		if (iTextLength <= 0) {
			return 0;
		}

		int iLayoutHeight = getTextSize(dTextSizeDefine) * iTextLength;
		int iMaxLayoutHeight = getLayoutHeight(m_iMaxLayoutHeightWeight);
		if (iMaxLayoutHeight <= iLayoutHeight) {
			return iMaxLayoutHeight;
		}

		return iLayoutHeight;
	}

	/**
	 * @param iTextLength 總字數
	 * @return 取出經長寬比運算出的總字寬度
	 */
	public int getLayoutWidthByTextSize(double dTextSizeDefine, int iTextLength)
	{
		if (iTextLength <= 0) {
			return 0;
		}

		int iLayoutWidth = getTextSize(dTextSizeDefine) * iTextLength;
		int iMaxLayoutWidth = getLayoutWidth(m_iMaxLayoutWidthWeight);
		if (iMaxLayoutWidth <= iLayoutWidth) {
			return iMaxLayoutWidth;
		}

		return iLayoutWidth;
	}


	/**
	 * @return 取出字型大小
	 */
	public int getTextSize(double dTextSizeDefine) {
		return (int) (dTextSizeDefine * m_fFontUnit);
	}

	/**
	 * 設定字型大小-TextView
	 * @param textView
	 */
	public void setTextSize(double dTextSizeDefine, TextView textView) {
		textView.setIncludeFontPadding(false);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize(dTextSizeDefine));
	}

	/**
	 * 設定字型大小-Button
	 * @param button
	 */
	public void setTextSize(double dTextSizeDefine, Button button) {
		button.setIncludeFontPadding(false);
		button.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize(dTextSizeDefine));
	}

	/**
	 * 設定字型大小-EditText
	 * @param editText
	 */
	public void setTextSize(double dTextSizeDefine, EditText editText) {
		editText.setIncludeFontPadding(false);
		editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize(dTextSizeDefine));
	}

	/**
	 * 取得
	 * @return 取出經長寬比運算出的高度
	 */
	public DisplayMetrics getDisplayMetrics() {
		return m_Dm;
	}

	/**@return 螢幕密度
	public float getScaleDensity()
	{
		return m_fScreenScaleDensity;
	}

	/**
	 * 取得StatusBar的高度
	 *
	 * @param context
	 * @return height
	 */
//	private static int iGetStatusBarHeight(Context context)
//	{
//		if (null == context)
//			return 0;
//
//		int iH = 0;
//
//		// 法一
//		// http://blog.csdn.net/devilnov/article/details/9309659
//		Rect frame = new Rect();
//		((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
//		iH = frame.top;
//
//		if (0 == iH)
//		{// 法二
//			// http://stackoverflow.com/questions/3407256/height-of-status-bar-in-android
//			int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
//			if (resourceId > 0)
//			{
//				iH = context.getResources().getDimensionPixelSize(resourceId);
//			}
//		}
//
//		return iH;
//	}

	/**
	 * 取得StatusBar的高度
	 *
	 * @return height px
	 */
	public int getStatusBarHeight(){
		return m_iStatusBarHeight;
	}

	/**
	 * 將Instance設為null，讓前端可以重算版面大小
	 */
	public void setInstanceNull() {
		m_instance = null;
	}



	/**
	 * 設定 View 經長寬比運算出的 Margins
	 */
	public void setMargins(View view, double left, double top, double right, double bottom) {
		if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
			if (left != 0 && top == 0 && right == 0 && bottom == 0) {
				((ViewGroup.MarginLayoutParams) view.getLayoutParams()).setMarginStart(getLayoutWidth(left));
				return;
			}
			if (left == 0 && top == 0 && right != 0 && bottom == 0) {
				((ViewGroup.MarginLayoutParams) view.getLayoutParams()).setMarginEnd(getLayoutWidth(right));
				return;
			}

			((ViewGroup.MarginLayoutParams) view.getLayoutParams()).setMargins(
					getLayoutWidth(left),
					getLayoutHeight(top),
					getLayoutWidth(right),
					getLayoutHeight(bottom));
		} else {
			throw new RuntimeException("This view can not be set margins");
		}
	}

	/**
	 * 設定 View 經長寬比運算出的 Padding
	 */
	public void setPadding(View view, double left, double top, double right, double bottom) {
		view.setPadding(
				getLayoutWidth(left),
				getLayoutHeight(top),
				getLayoutWidth(right),
				getLayoutHeight(bottom));
	}

	/**
	 * 設定 View 經長寬比運算出的長寬
	 */
	public void setViewSize(View view, double width, double height) {
		//某些手機才會發現這個問題...
		if (width >= 0) {
			view.getLayoutParams().width = getLayoutWidth(width);
		} else {
			view.getLayoutParams().width = (int)width;
		}
		if (height >= 0) {
			view.getLayoutParams().height = getLayoutHeight(height);
		} else {
			view.getLayoutParams().height = (int)height;
		}
	}

	public void selfAdjustSameScaleView(View v, double width, double height){
		v.getLayoutParams().height = getLayoutMinUnit(height);
		v.getLayoutParams().width  = getLayoutMinUnit(width);
	}

	public void selfAdjustAllView(View rootView) {
		if (true == isNormalViewGroup(rootView)) {
			for (int i = 0; i < ((ViewGroup) rootView).getChildCount(); i++) {
				selfAdjustAllView(((ViewGroup) rootView).getChildAt(i));
			}
		}

		adjustMargin(rootView);
		adjustViewSize(rootView);
		adjustTextSize(rootView);
		adjustPadding(rootView);
	}

	private void adjustViewSize(View rootView){
		setViewSize(
				rootView,
				rootView.getLayoutParams().width,
				rootView.getLayoutParams().height);
	}

	private void adjustTextSize(View rootView){
		if (rootView instanceof TextView) {
			setTextSize(
					((TextView) rootView).getTextSize(), (TextView) rootView);
		}

	}

	private void adjustMargin(View rootView){
		if (rootView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
			((ViewGroup.MarginLayoutParams) rootView.getLayoutParams()).setMarginStart(getLayoutWidth(((ViewGroup.MarginLayoutParams) rootView.getLayoutParams()).getMarginStart()));
			((ViewGroup.MarginLayoutParams) rootView.getLayoutParams()).setMarginEnd(getLayoutWidth(((ViewGroup.MarginLayoutParams) rootView.getLayoutParams()).getMarginEnd()));
			((ViewGroup.MarginLayoutParams) rootView.getLayoutParams()).setMargins(((ViewGroup.MarginLayoutParams) rootView.getLayoutParams()).getMarginStart(),
					getLayoutHeight(((ViewGroup.MarginLayoutParams) rootView.getLayoutParams()).topMargin),
					((ViewGroup.MarginLayoutParams) rootView.getLayoutParams()).getMarginEnd(),
					getLayoutHeight(((ViewGroup.MarginLayoutParams) rootView.getLayoutParams()).bottomMargin));

		}
	}

	private boolean isNormalViewGroup(View rootView){
		if (rootView instanceof ViewGroup
			&& false == rootView instanceof ListView
			&& false == rootView instanceof Spinner
			&& false == rootView instanceof RecyclerView
			&& false == rootView instanceof StepHorizontalView
			&& false == rootView instanceof TabLayout) {
			return true;
		}
		return false;
	}

	private void adjustPadding(View rootView){
		if (false == rootView instanceof Spinner ){
			if (true == rootView.isPaddingRelative()) {
				rootView.setPaddingRelative(
						getLayoutWidth(rootView.getPaddingStart()),
						getLayoutHeight(rootView.getPaddingTop()),
						getLayoutWidth(rootView.getPaddingEnd()),
						getLayoutHeight(rootView.getPaddingBottom()));
			} else {
				setPadding(
						rootView,
						rootView.getPaddingLeft(),
						rootView.getPaddingTop(),
						rootView.getPaddingRight(),
						rootView.getPaddingBottom());
			}
		}
	}
}
