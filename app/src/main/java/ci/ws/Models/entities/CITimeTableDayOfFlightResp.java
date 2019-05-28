package ci.ws.Models.entities;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * 2017-02-15 CR 增加航班的飛行日期(8天)
 *
 * Created by jlchen on 2017/2/15.
 */
public class CITimeTableDayOfFlightResp {

    /**需要顯示 飛機圖*/
    public static final String IS_FLIGHT_Y = "Y";
    /**不要顯示 飛機圖*/
    public static final String IS_FLIGHT_N = "N";

    @Expose
    public String date;

    @Expose
    public String is_flight;
}
