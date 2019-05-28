package ci.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.chinaairlines.mobile30.R;

/**
 * 自定義虛線控件 , 可直接於layout.xml使用此view
 * (範例可見layout_my_trip_info_layover_view.xml)
 *
 * values/styles.xml 提供以下參數
 * dl_color             虛線顏色
 * dl_shadowSize        陰影大小
 * dl_dy                陰影偏移位置(上下)(y軸)
 *
 * Created by jlchen on 2016/2/26.
 */
public class DashedLine extends View {

    private static int ilineHeight;
    private static int ilineWidth;

    private int m_iColor = 0;
    private int m_iShadowColor = 0;
    private float m_fShadowSize;  //陰影大小

    private Context m_Context;

    public DashedLine(Context context) {
        super(context);
        m_Context = context;
    }

    public DashedLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        m_Context = context;
        initAttributes(context, attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        //檢索此style擁有的參數
        TypedArray attr = getTypedArray(context, attrs, R.styleable.DashedLine);
        if (attr == null) {
            return;
        }

        try {
            m_iColor = attr.getColor(R.styleable.DashedLine_dl_color, ContextCompat.getColor(m_Context, R.color.warm_grey_50));

            m_iShadowColor = attr.getColor(R.styleable.DashedLine_dl_shadowColor, ContextCompat.getColor(m_Context, R.color.transparent));
            m_fShadowSize = attr.getDimension(R.styleable.DashedLine_dl_shadowSize, 0);
        } finally {
            attr.recycle();
        }
    }

    private TypedArray getTypedArray(Context context, AttributeSet attributeSet, int[] attr) {
        return context.obtainStyledAttributes(attributeSet, attr, 0, 0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setBackgroundCompat(w, h);
    }

    @SuppressWarnings("deprecation")
    private void setBackgroundCompat(int w, int h) {

        Bitmap bitmap = createShadowBitmap(m_fShadowSize,
                m_iShadowColor,  m_iColor);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(drawable);
        } else {
            setBackground(drawable);
        }
    }

    private Bitmap createShadowBitmap(float shadowSize, int shadowColor, int fillColor) {

        ilineWidth = this.getWidth();
        ilineHeight = this.getHeight();

        if(ilineWidth <= 0) {
            ilineWidth = 1;
        }

        if(ilineHeight <= 0) {
            ilineHeight = 1;
        }

        Bitmap output = Bitmap.createBitmap(ilineWidth, ilineHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        Paint paintShadow = new Paint();

        if ( 0 != shadowSize ){
            ilineHeight  = ilineHeight / 2;

            if ( 0 < ilineHeight ){
                paint.setStrokeWidth(ilineHeight);
                paintShadow.setStrokeWidth(ilineHeight);
                shadowSize = ilineHeight;
            }else {
                paint.setStrokeWidth(2);
                paintShadow.setStrokeWidth(2);
                ilineHeight = 2;
                shadowSize = 2;
            }

            paintShadow.setStyle(Paint.Style.STROKE);
            paintShadow.setColor(shadowColor);
        }else {
            if ( 0 < ilineHeight ){
                paint.setStrokeWidth(ilineHeight);
            }else {
                paint.setStrokeWidth(2);
            }
        }
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(fillColor);

        int iGap = 10; //ViewScaleDef.getInstance(m_Context).getLayoutWidth(10);
        int iDash = 8; //ViewScaleDef.getInstance(m_Context).getLayoutWidth(8);

        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(ilineWidth, 0);

        PathEffect effects = new DashPathEffect(new float[]{iGap, iDash, iGap, iDash}, 1);
        paint.setPathEffect(effects);

        Path pathShadow = new Path();
        if ( 0 != shadowSize ){
            pathShadow.moveTo(1, 1 + ilineHeight);
            pathShadow.lineTo(ilineWidth, 1 + ilineHeight);

            paintShadow.setPathEffect(effects);
            canvas.drawPath(pathShadow, paintShadow);
        }

        canvas.drawPath(path, paint);

        return output;
    }

//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        ilineHeight  = this.getHeight();
//        ilineWidth = this.getWidth();
//
//        int iGap = ViewScaleDef.getInstance(m_Context).getLayoutWidth(10);
//        int iDash = ViewScaleDef.getInstance(m_Context).getLayoutWidth(8);
//
//        if ( 0 < ilineHeight ){
//            paint.setStrokeWidth(ilineHeight);
//        }else {
//            paint.setStrokeWidth(2);
//        }
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setColor(m_iColor);
//
//        Path path = new Path();
//        path.moveTo(1, 1);
//        path.lineTo(ilineWidth, 1);
//
//        PathEffect effects = new DashPathEffect(new float[]{iGap, iDash, iGap, iDash}, 1);
//        paint.setPathEffect(effects);
//        canvas.drawPath(path, paint);
//    }
}
