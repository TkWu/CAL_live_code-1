package ci.ws.Models.entities;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ryan on 16/5/18.
 */
public class CIInquiryMealByPassangerResp implements Serializable {

    /** 訂位代號*/
    public String pnr_id;

    /** 行程序號*/
    public int itinerary_seq;

    /**航班日期*/
    public String flight_date;

    /**航班編號*/
    public String flight_num;

    /**航班航段*/
    public String flight_sector;

    /**乘客搭乘倉別*/
    public String pax_seat_class;

    /**可選餐的乘客*/
    public ArrayList<CIMealPassenagerEntity> passangers;

    public CIInquiryMealByPassangerResp(){

        passangers = new ArrayList<CIMealPassenagerEntity>();
    }
}
