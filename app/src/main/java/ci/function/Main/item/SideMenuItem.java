package ci.function.Main.item;

/**
 */
public class SideMenuItem {

    public int		iId;
    public int	    iNameResId;
    public int		iDrawableId;
    public int		iDrawableId_n;
    public Class<?>	_class;
    public Boolean	bShowNum;
    public int      iNum;
    public boolean  bSelect;

    /**
     * @param iId -畫面ID
     * @param iNameResId -顯示名稱的Resourse id
     * @param iDrawableId 被點擊時顯示的icon
     * @param iDrawableId_n 未被點擊後顯示的icon
     * @param _class 對應的class，有可能使用Fragment或Activity
     * @param bShowNum 是否顯示數字於name後方
     * @param iNum 數字*/
    public SideMenuItem(int iId, int iNameResId, int iDrawableId, int iDrawableId_n, Class<?> _class, Boolean bShowNum, int iNum)
    {
        this.iId			= iId;
        this.iNameResId		= iNameResId;
        this.iDrawableId	= iDrawableId;
        this.iDrawableId_n	= iDrawableId_n;
        this._class			= _class;
        this.bShowNum       = bShowNum;
        this.iNum           = iNum;
        this.bSelect        = false;
    }
}
