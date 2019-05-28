package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

/**
 * Created by kevincheng on 2016/4/14.
 */
public class CIYouTubeResponceDTO {
    @Expose
    public CIYouTubePlayListItemsList items;

    @Expose
    public String nextPageToken;
}
