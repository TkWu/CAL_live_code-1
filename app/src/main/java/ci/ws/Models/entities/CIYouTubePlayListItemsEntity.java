package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

/**
 * Created by kevincheng on 2016/4/14.
 */
public class CIYouTubePlayListItemsEntity {

    /**
     * The snippet object contains basic details about the playlist item, such as its title and
     * position in the playlist.
     * The value may be {@code null}.
     */
    @Expose
    public PlaylistItemSnippet snippet;
    
    public static class PlaylistItemSnippet{

        /**
         * The item's title.
         * The value may be {@code null}.
         */
        @Expose
        public java.lang.String title;

        /**
         * The item's description.
         * The value may be {@code null}.
         */
        @Expose
        public java.lang.String description;


        /**
         * The id object contains information that can be used to uniquely identify the resource that is
         * included in the playlist as the playlist item.
         * The value may be {@code null}.
         */
        @Expose
        public ResourceId resourceId;

        /**
         * A map of thumbnail images associated with the playlist item. For each object in the map, the
         * key is the name of the thumbnail image, and the value is an object that contains other
         * information about the thumbnail.
         * The value may be {@code null}.
         */
        @Expose
        public ThumbnailDetails thumbnails;

        /**
         * The ID that YouTube uses to uniquely identify the referred resource, if that resource is a
         * playlist. This property is only present if the resourceId.kind value is youtube#playlist.
         * The value may be {@code null}.
         */
        @Expose
        public java.lang.String playlistId;

    }

    public static class ThumbnailDetails{
        /**
         * The default image for this resource.
         * The value may be {@code null}.
         */
        @Expose
        public Thumbnail default__;

        /**
         * The high quality image for this resource.
         * The value may be {@code null}.
         */
        @Expose
        public Thumbnail high;

        /**
         * The maximum resolution quality image for this resource.
         * The value may be {@code null}.
         */
        @Expose
        public Thumbnail maxres;

        /**
         * The medium quality image for this resource.
         * The value may be {@code null}.
         */
        @Expose
        public Thumbnail medium;

        /**
         * The standard quality image for this resource.
         * The value may be {@code null}.
         */
        @Expose
        public Thumbnail standard;
    }

    public static class Thumbnail{
        /**
         * (Optional) Height of the thumbnail image.
         * The value may be {@code null}.
         */
        @Expose
        public java.lang.Long height;

        /**
         * The thumbnail image's URL.
         * The value may be {@code null}.
         */
        @Expose
        public java.lang.String url;

        /**
         * (Optional) Width of the thumbnail image.
         * The value may be {@code null}.
         */
        @Expose
        public java.lang.Long width;
    }

    public static class ResourceId{

        /**
         * The type of the API resource.
         * The value may be {@code null}.
         */
        @Expose
        public java.lang.String kind;

        /**
         * The ID that YouTube uses to uniquely identify the referred resource, if that resource is a
         * video. This property is only present if the resourceId.kind value is youtube#video.
         * The value may be {@code null}.
         */
        @Expose
        public java.lang.String videoId;
    }

}
