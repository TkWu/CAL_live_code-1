package ci.function.HomePage.item;

/**
 * Created by ryan on 16/5/2.
 */
public class CIHomeBgItem {

    /**左邊SideMenu的背景圖*/
    private int iDrawableId_left;
    /**中間的背景圖*/
    private int  iDrawableId_mid;
    /**右邊SideMenu的背景圖*/
    private int iDrawableId_right;
    /**中間的背景圖,下半部分Blur*/
    private int iDrawableId_mid_blur;

    public CIHomeBgItem( int iDrawableId_left, int iDrawableId_mid, int iDrawableId_right, int iDrawableId_mid_blur){
        this.iDrawableId_left =iDrawableId_left;
        this.iDrawableId_mid = iDrawableId_mid;
        this.iDrawableId_right = iDrawableId_right;
        this.iDrawableId_mid_blur = iDrawableId_mid_blur;
    }

    public int getMidImageId(){
        return iDrawableId_mid;
    }

    public int getMidBlurImageId(){
        return iDrawableId_mid_blur;
    }

    public int getLeftImageId(){
        return iDrawableId_left;
    }

    public int getRightImageId(){
        return iDrawableId_right;
    }
}
