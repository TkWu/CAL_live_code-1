package ci.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.chinaairlines.mobile30.R;

/**
 * Created by jlchen on 2016/6/29.
 *
 * view陰影效果
 *
 * values/styles.xml 提供以下參數
 * sl_shadowColor   陰影顏色
 * sl_shadowSize    陰影大小
 * sl_cornerRadius  陰影圓框弧度
 * sl_dx            陰影偏移位置(左右)(x軸)
 * sl_dy            陰影偏移位置(上下)(y軸)
 *
 * 使用方法：將要使用陰影的view外面包一層ShadowLayout
 * 並依需求填入對應的參數即可
 *
 * 範例可參考layout/layout_popupwindow_shortcutadd.xml
 * 或layout/layout_fragment_home.xml
 */
public class ShadowLayout extends FrameLayout {

    //需要程式控制shadowLayout外觀時 呼叫此方法即可
    public void invalidateShadow(int iShadowColor,
                                 float fShadowSize, float fCornerRadius,
                                 float fDx, float fDy) {
        mForceInvalidateShadow = true;

        mShadowColor = iShadowColor;
        mShadowSize = fShadowSize;
        mCornerRadius = fCornerRadius;
        mDx = fDx;
        mDy = fDy;

        requestLayout();
        invalidate();
    }

    private Context m_Context;

    private int mfillColor; //ShadowLayout裡面的背景色; 畫筆顏色
    private int mShadowColor; //陰影顏色
    private float mShadowSize; //陰影大小
    private float mCornerRadius; //陰影圓框弧度
    private float mDx; //陰影偏移位置(左右)
    private float mDy; //陰影偏移位置(上下)

    private boolean mInvalidateShadowOnSizeChanged = true;
    private boolean mForceInvalidateShadow = false;

    public ShadowLayout(Context context) {
        super(context);
        this.m_Context = context;
        initView(context, null);
    }

    public ShadowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.m_Context = context;
        //attrs：用來檢索res(資源檔)的參數
        initView(context, attrs);
    }

    public ShadowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.m_Context = context;
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        initAttributes(context, attrs);

        int xPadding = (int) (mShadowSize + Math.abs(mDx));
        int yPadding = (int) (mShadowSize + Math.abs(mDy));
        setPadding(xPadding, yPadding, xPadding, yPadding);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        //檢索此style擁有的參數
        TypedArray attr = getTypedArray(context, attrs, R.styleable.ShadowLayout);
        if (attr == null) {
            return;
        }

        try {
            mCornerRadius = attr.getDimension(R.styleable.ShadowLayout_sl_cornerRadius, getResources().getDimension(R.dimen.default_corner_radius));
            mShadowSize = attr.getDimension(R.styleable.ShadowLayout_sl_shadowSize, getResources().getDimension(R.dimen.default_shadow_radius));
            mDx = attr.getDimension(R.styleable.ShadowLayout_sl_dx, 0);
            mDy = attr.getDimension(R.styleable.ShadowLayout_sl_dy, 0);
            mShadowColor = attr.getColor(R.styleable.ShadowLayout_sl_shadowColor, ContextCompat.getColor(m_Context, R.color.cyan));
            //mfillColor: ShadowLayout裡面的背景色 & 畫筆顏色 ;不能設為透明 因為會整個layout畫不出來
            mfillColor = Color.WHITE;
        } finally {
            attr.recycle();
        }
    }

    private TypedArray getTypedArray(Context context, AttributeSet attributeSet, int[] attr) {
        return context.obtainStyledAttributes(attributeSet, attr, 0, 0);
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return 0;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return 0;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(w > 0 && h > 0 && (getBackground() == null || mInvalidateShadowOnSizeChanged || mForceInvalidateShadow)) {
            mForceInvalidateShadow = false;
            setBackgroundCompat(w, h);
        }
    }

    @SuppressWarnings("deprecation")
    private void setBackgroundCompat(int w, int h) {

        Bitmap bitmap = createShadowBitmap(w, h,
                mCornerRadius, mShadowSize,
                mDx, mDy,
                mShadowColor,  mfillColor);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(drawable);
        } else {
            setBackground(drawable);
        }
    }

    private Bitmap createShadowBitmap(int shadowWidth, int shadowHeight, float cornerRadius, float shadowRadius,
                                      float dx, float dy, int shadowColor, int fillColor) {

        Bitmap output = Bitmap.createBitmap(shadowWidth, shadowHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        RectF shadowRect = new RectF(
                shadowRadius,
                shadowRadius,
                shadowWidth - shadowRadius,
                shadowHeight - shadowRadius);

        if (dy > 0) {
            shadowRect.top += dy;
            shadowRect.bottom -= dy;
        } else if (dy < 0) {
            shadowRect.top += Math.abs(dy);
            shadowRect.bottom -= Math.abs(dy);
        }

        if (dx > 0) {
            shadowRect.left += dx;
            shadowRect.right -= dx;
        } else if (dx < 0) {
            shadowRect.left += Math.abs(dx);
            shadowRect.right -= Math.abs(dx);
        }

        Paint shadowPaint = new Paint();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE, shadowPaint);
        }

        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor( fillColor );
        shadowPaint.setStyle(Paint.Style.FILL);

        if (!isInEditMode()) {
            shadowPaint.setShadowLayer(shadowRadius, dx, dy, shadowColor);
        }

        canvas.drawRoundRect(shadowRect, cornerRadius, cornerRadius, shadowPaint);

        return output;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mForceInvalidateShadow) {
            mForceInvalidateShadow = false;
            setBackgroundCompat(right - left, bottom - top);
        }
    }

    public void setInvalidateShadowOnSizeChanged(boolean invalidateShadowOnSizeChanged) {
        mInvalidateShadowOnSizeChanged = invalidateShadowOnSizeChanged;
    }

}
