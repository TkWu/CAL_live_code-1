package ci.ui.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import ci.ui.define.ViewScaleDef;

/**
 * 影像處理
 * 1.圖片圓角處理
 * 2.裁圖
 * 3.按照正方形裁切圖片
 * 4.調整影像模糊
 * 5.ByteArray 及 Bitmap 互相轉換
 * 6.ResourceId 轉換 Bitmap
 * 7.回收圖片在記憶體中佔用的資源
 * 8.文字轉換QRCode
 *
 * Edit by Kevin , Ling , Ryan .
 */
public class ImageHandle {

    //圓角轉換函式，帶入resource圖片及圓角數值則回傳圓角圖，回傳Bitmap再置入ImageView
    public static Bitmap getRoundedCornerBitmap(Context context,int resource ,float roundPx) {
        Bitmap bitmap = getLocalBitmap(context, resource,1);
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas      canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                                   bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    //圓角轉換函式，帶入Bitmap圖片及圓角數值則回傳圓角圖，回傳Bitmap再置入ImageView
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx)
    {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                                            bitmap.getHeight(),
                                            Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                                   bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /** 圓角轉換函式(如果某些角不需要作圓角處理 該角需帶入false)
     * @param bRoundTL 左上角
     * @param bRoundTR 右上角
     * @param bRoundBL 左下角
     * @param bRoundBR 右下角
     * */
    public static Bitmap getRoundedCornerBitmap(Context context,int resource ,float roundPx,
                                                boolean bRoundTL, boolean bRoundTR,
                                                boolean bRoundBL, boolean bRoundBR ) {
        Bitmap bitmap = getLocalBitmap(context, resource, 1);

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas      canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        int iRectW = w/2;
        if ( iRectW > roundPx ){
            iRectW = (int)roundPx;
        }

        int iRectH = h/2;
        if ( iRectH > roundPx ){
            iRectH = (int)roundPx;
        }


        //左上
        if ( false == bRoundTL ){
            canvas.drawRect(0, 0, iRectW, iRectH, paint);
        }

        //右上
        if ( false == bRoundTR ){
            canvas.drawRect(w-iRectW, 0, w, iRectH, paint);
        }

        //左下
        if ( false == bRoundBL ){
            canvas.drawRect(0, h-iRectH, iRectW, h, paint);
        }

        //右下
        if ( false == bRoundBR ){
            canvas.drawRect(w-iRectW, h-iRectH, w, h, paint);
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0,0, paint);

        return output;
    }

    /** 圓角轉換函式(如果某些角不需要作圓角處理 該角需帶入false)
     * @param bRoundTL 左上角
     * @param bRoundTR 右上角
     * @param bRoundBL 左下角
     * @param bRoundBR 右下角
     * */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx,
                                                boolean bRoundTL, boolean bRoundTR,
                                                boolean bRoundBL, boolean bRoundBR ) {

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Bitmap output = Bitmap.createBitmap(w, h,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        int iRectW = w/2;
        if ( iRectW > roundPx ){
            iRectW = (int)roundPx;
        }

        int iRectH = h/2;
        if ( iRectH > roundPx ){
            iRectH = (int)roundPx;
        }

        //左上
        if ( false == bRoundTL ){
            canvas.drawRect(0, 0, iRectW, iRectH, paint);
        }

        //右上
        if ( false == bRoundTR ){
            canvas.drawRect(w-iRectW, 0, w, iRectH, paint);
        }

        //左下
        if ( false == bRoundBL ){
            canvas.drawRect(0, h-iRectH, iRectW, h, paint);
        }

        //右下
        if ( false == bRoundBR ){
            canvas.drawRect(w-iRectW, h-iRectH, w, h, paint);
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0,0, paint);

        return output;
    }

    /***
     * 圖片的缩放方法
     * @param bm 原圖
     * @param newWidth 縮放後寬度
     * @param newHeight 縮放後高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bm,
                                   double newWidth,
                                   double newHeight) {
        // 獲取原圖寬高
        float width = bm.getWidth();
        float height = bm.getHeight();

        // 創建操作圖片用的matrix對象
        Matrix matrix = new Matrix();
        // 計算縮放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 進行縮放
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bm, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    private static Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }

    public static Bitmap BlurBuilder(Context context, Bitmap image,float radius){
        return BlurBuilder(context, image,radius, 0.4f);
    }

    //2016/03/10 移除Class BlurBuilder, 調整為ImageHandle的子類別, 方便外面使用

    public static Bitmap BlurBuilder(Context context, Bitmap image,float radius, float scale) {

        if(context == null){
            return null;
        }

        try {
            int width = Math.round(image.getWidth() * scale);
            int height = Math.round(image.getHeight() * scale);

            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            RenderScript rs = RenderScript.create(context);
            ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);

            theIntrinsic.setRadius(radius);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);

            return outputBitmap;
        } catch(Exception e) {
            e.printStackTrace();
            //如果發生異常，就返回原圖，不做模糊處理
            return image;
        }


    }

    //將全螢幕畫面轉換成Bitmap
    public static Bitmap getScreenShot(Activity context) {

        if(context == null){
            return null;
        }

        //藉由View來Cache全螢幕畫面後放入Bitmap
        View view = context.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap fullBitmap = view.getDrawingCache();

        //取得系統狀態列高度
        int mStatusBarHeight = ViewScaleDef.getInstance(context).getStatusBarHeight();

        //取得手機螢幕長寬尺寸
        int mPhoneWidth = ViewScaleDef.getInstance(context).getDisplayMetrics().widthPixels;
        int mPhoneHeight = ViewScaleDef.getInstance(context).getDisplayMetrics().heightPixels;

        //將狀態列的部分移除並建立新的Bitmap
        Bitmap bitmap = Bitmap.createBitmap(fullBitmap, 0,
                mStatusBarHeight, mPhoneWidth, mPhoneHeight - mStatusBarHeight);
        //將Cache的畫面清除
        view.destroyDrawingCache();

        return bitmap;
    }


    /**
     * 按正方形裁切圖片
     */
    public static Bitmap ImageCrop(Bitmap bitmap, boolean isRecycled)
    {

        if (bitmap == null)
        {
            return null;
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int wh = w > h ? h : w; // 裁切後所取的正方形區域邊長

        int retX = w > h ? (w - h) / 2 : 0; //從原圖取正方形左上角x坐标
        int retY = w > h ? 0 : (h - w) / 2;

        Bitmap bmp = Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);
        if (isRecycled && bitmap != null && !bitmap.equals(bmp)
                && !bitmap.isRecycled())
        {
            bitmap.recycle();
            bitmap = null;
        }

        return bmp;
    }

    public static byte[] BitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public static Bitmap ByteArrayToBitmap(byte[] image){
        return BitmapFactory.decodeByteArray(image,0,image.length);
    }

    public static Bitmap getLocalBitmap(Context con, int resourceId, int scale){
        InputStream inputStream = con.getResources().openRawResource(resourceId);
        return BitmapFactory.decodeStream(inputStream, null, getBitmapOptions(scale));
    }

    public static BitmapFactory.Options getBitmapOptions(int scale){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = scale;
        return options;
    }

    /**回收圖片在記憶體中佔用的資源*/
    public static void recycleImageViewBitMap(ImageView imageView) {
        if (imageView != null) {
            if ( imageView.getDrawable() instanceof BitmapDrawable ){
                BitmapDrawable bd = (BitmapDrawable) imageView.getDrawable();
                recycleBitmapDrawable(bd);
            }
        }
    }

    public static void recycleBitmapDrawable(BitmapDrawable bd) {
        if (bd != null) {
            Bitmap bitmap = bd.getBitmap();
            recycleBitmap(bitmap);
        }
        bd = null;
    }

    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

/** 回收圖片資源在onDestoryView寫法範例代碼
 *  @Override
    public void onDestroyView() {
        recycleImageViewBitMap(m_imageView_Tendenvy);
        recycleImageViewBitMap(m_imageView_Timeformat);
        if(m_bitmap_Tendency!=null)
        {
            m_bitmap_Tendency.recycle();
            m_bitmap_Tendency = null;
        }
        if(m_bitmap_TimeFormat!=null)
        {
            m_bitmap_TimeFormat.recycle();
            m_bitmap_TimeFormat = null;
        }
        System.gc();
        super.onDestroyView();
    }
*/

    /**
     * 轉換文字為QRcode
     * @param strData 文字資訊
     * @param iWidth  圖片寬度
     * @return Bitmap QRCode
     */
    public static Bitmap encodeToQRCode(final String strData , final int iWidth) {
        if(TextUtils.isEmpty(strData) || TextUtils.equals(strData, "null") || iWidth <= 0){
            return null;
        }
        final int BLACK = Color.BLACK;
        final int WHITE = Color.WHITE;
        BitMatrix result;
        Bitmap bitmap = null;
        try {
            String strUtf8Data = new String(strData.getBytes("UTF-8"),"ISO-8859-1");;
            //result = new MultiFormatWriter().encode(strUtf8Data, BarcodeFormat.QR_CODE,iWidth,iWidth);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.encodeBitmap(strUtf8Data, BarcodeFormat.QR_CODE, iWidth, iWidth);

        } catch (WriterException e) {
            // Unsupported format
            return null;
        } catch ( UnsupportedEncodingException e) {
            return null;
        }
//        int w = result.getWidth();
//        int h = result.getHeight();
//        int[] pixels = new int[w * h];
//        for (int y = 0; y < h; y++) {
//            int offset = y * w;
//            for (int x = 0; x < w; x++) {
//                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
//            }
//        }
//        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
//        bitmap.setPixels(pixels, 0, iWidth, 0, 0, w, h);
//        return bitmap;

        return bitmap;
    }

    public static Bitmap encodeToBarcode(final String strData , final int iWidth, final int iHeight) {
        if(TextUtils.isEmpty(strData) || TextUtils.equals(strData, "null") || iWidth <= 0){
            return null;
        }
        final int BLACK = Color.BLACK;
        final int WHITE = Color.WHITE;
        BitMatrix result;
        Bitmap bitmap = null;
        try {
            String strUtf8Data = new String(strData.getBytes("UTF-8"),"ISO-8859-1");;
            //result = new MultiFormatWriter().encode(strUtf8Data, BarcodeFormat.CODE_128,iWidth,iHeight);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.encodeBitmap(strUtf8Data, BarcodeFormat.CODE_128,iWidth,iHeight);

        } catch (WriterException e) {
            // Unsupported format
            return null;
        } catch ( UnsupportedEncodingException e) {
            return null;
        }
//        int w = result.getWidth();
//        int h = result.getHeight();
//        int[] pixels = new int[w * h];
//        for (int y = 0; y < h; y++) {
//            int offset = y * w;
//            for (int x = 0; x < w; x++) {
//                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
//            }
//        }
//        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
//        bitmap.setPixels(pixels, 0, iWidth, 0, 0, w, h);
        return bitmap;
    }
}