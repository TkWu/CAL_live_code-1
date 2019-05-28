package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

/**
 * Created by Ryan on 16/5/5.
 */
public class CIMilesProgressEntity {

    /**會籍哩程完成狀況*/
    @Expose
    public milesInfo miles;

    /**加權旅次,晉升與續卡標準與累積資訊。*/
    @Expose
    public trips qualified_weighted_trips;

    /**商務旅次,晉升與續卡標準與累積資訊。*/
    @Expose
    public flights first_class_flights;

    /**華航 / 華信自營航班加權旅次,晉升與續卡標準與累積資訊。*/
    @Expose
    public ciTrips ci_trips;

    /**6個月內到期的哩程有效期限，格式（YYYY-MM-DD）*/
    @Expose
    public String expire_date;

    /**飛行旅次*/
    public float flight;


    public class milesInfo{

        /**普卡、EMER與GOLD卡的晉升標準哩程數。
         * Paragon 卡的續卡標準哩程數。*/
        @Expose
        public float crtmil_standard01;

        /**普卡、EMER與GOLD卡晉升已累積哩程數。
         * Paragon 卡的續卡已累積哩程數。*/
        @Expose
        public float crtmil_accumulation01;

        /**EMER與GOLD卡的續卡標準哩程數。*/
        @Expose
        public float crtmil_standard02;

        /**EMER與GOLD續卡已累積哩程數。*/
        @Expose
        public float crtmil_accumulation02;
    }

    public class trips{

        /**普卡、EMER與GOLD卡的晉升標準哩程數。
         * Paragon 卡的續卡標準哩程數。*/
        @Expose
        public float wgttrip_standard01;

        /**普卡、EMER與GOLD卡晉升已累積哩程數。
         * Paragon 卡的續卡已累積哩程數。*/
        @Expose
        public float wgttrip_accumulation01;

        /**EMER與GOLD卡的續卡標準哩程數。*/
        @Expose
        public float wgttrip_standard02;

        /**EMER與GOLD續卡已累積哩程數。*/
        @Expose
        public float wgttrip_accumulation02;
    }

    public class flights{

        /**普卡、EMER與GOLD卡的晉升標準哩程數。
         * Paragon 卡的續卡標準哩程數。*/
        @Expose
        public float crtftrp_standard01;

        /**普卡、EMER與GOLD卡晉升已累積哩程數。
         * Paragon 卡的續卡已累積哩程數。*/
        @Expose
        public float crtftrp_accumulation01;

        /**EMER與GOLD卡的續卡標準哩程數。*/
        @Expose
        public float crtftrp_standard02;

        /**EMER與GOLD續卡已累積哩程數。*/
        @Expose
        public float crtftrp_accumulation02;
    }

    public class ciTrips{

        /**普卡、EMER與GOLD卡的晉升標準哩程數。
         * Paragon 卡的續卡標準哩程數。*/
        @Expose
        public float wgtcitrip_standard01;

        /**普卡、EMER與GOLD卡晉升已累積哩程數。
         * Paragon 卡的續卡已累積哩程數。*/
        @Expose
        public float wgtcitrip_accumulation01;

        /**EMER與GOLD卡的續卡標準哩程數。*/
        @Expose
        public float wgtcitrip_standard02;

        /**EMER與GOLD續卡已累積哩程數。*/
        @Expose
        public float wgtcitrip_accumulation02;
    }
}
