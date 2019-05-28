package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

/**
 * Created by KevinCheng on 16/8/29.
 * Doc. : CI_APP_API_CheckVersion.docx
 * 功能說明：版本更新通知與重要公告。
 */
public class CICheckVersionAndAnnouncementEntity implements Cloneable{

    /**
     * 提示類型
     * A：公告
     * V：版本更新
     */
    @Expose
    public String TYPE;

    /**
     * 更新/公告內容
     */
    @Expose
    public String CONTENT;

    /**
     * 更新/公告網址
     */
    @Expose
    public String URL;

    /**
     * 是否強制更新 Y/N
     */
    @Expose
    public String IS_FORCED_UPDATE;

    @Override
    protected Object clone(){
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
