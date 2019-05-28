package ci.function.ExtraServices;

import android.content.Context;
import android.graphics.Bitmap;

import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;

/**
 * Created by jlchen on 2016/4/14.
 */
public class CIExtraServicesItem {
    public String   m_strTitle;
    public String   m_strContent;
    public String   m_strUrl;
    public int      m_iImgRes;
    public Bitmap   m_bitmap;

    public CIExtraServicesItem(Context context, String strTitle, String strContent, String strUrl, int iRes){
        this.m_strTitle     = strTitle;
        this.m_strContent   = strContent;
        this.m_strUrl       = strUrl;
        this.m_iImgRes      = iRes;

        //Res轉bitmap
        Bitmap resBitmap = ImageHandle.getLocalBitmap(
                context, iRes, 1);

        //縮放處理
        Bitmap zoomBitmap = ImageHandle.zoomImage(
                resBitmap,
                ViewScaleDef.getInstance(context).getLayoutWidth(300),
                ViewScaleDef.getInstance(context).getLayoutWidth(124));

        //圓角處理
        this.m_bitmap = ImageHandle.getRoundedCornerBitmap(
                zoomBitmap,
                ViewScaleDef.getInstance(context).getLayoutMinUnit(3),
                true, true, false, false);

        //回收已不需要的bitmap
        ImageHandle.recycleBitmap(resBitmap);
        ImageHandle.recycleBitmap(zoomBitmap);
    }

    public void resetText(String strTitle, String strContent, String strUrl){
        this.m_strTitle     = strTitle;
        this.m_strContent   = strContent;
        this.m_strUrl       = strUrl;
    }

    public String getTitle(){
        return m_strTitle;
    }

    public String getContent(){
        return m_strContent;
    }

    public String getUrl(){
        return m_strUrl;
    }

    public int getImgRes(){
        return m_iImgRes;
    }

    public Bitmap getBitmap(){
        return m_bitmap;
    }
}
