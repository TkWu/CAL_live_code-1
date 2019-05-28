package ci.ui.define;

import ci.ws.define.CIWSHomeStatus_Code;

/**
 * Created by Ryan on 16/3/2.
 * Code 的定義參照 CIWSHomeStatus_Code
 * 在前面加上 900 來區隔i 與實際Code, 目的為 UI 狀態會有延升,
 * ex check-in, 會有 可以Online Check-in, 臨櫃Check-in, 但WS Code 都是同樣的, 所以有拆成四位數 9030, 9031 區隔,
 * 其他狀態則是 原WS Code = 5, UI Code = 905,
 */
public class HomePage_Status {

    /**未知狀態*/
    public static final int	TYPE_UNKNOW                                 =-1;

    /**No Ticket*/
    public static final int	TYPE_A_NO_TICKET                            = CIWSHomeStatus_Code.TYPE_A_NO_TICKET;
    /**have ticket*/
    public static final int	TYPE_B_HAVE_TICKET                          = 906;

    /**have ticket, can select seat
     * 180Day~14Day*/
    public static final int TYPE_C_SELECT_SEAT_180D_14D                 = 905;
    /**have ticket, can select seat meal
     * 14Day~24H*/
    public static final int	TYPE_C_SEAT_MEAL_14D_24H                    = 904;

    /**Onine Check-In available, not at the airport yet*/
    public static final int	TYPE_D_ONLINE_CHECKIN                       = 9030;
    /**Onine Check-In available, Online Check-in is finished*/
    public static final int	TYPE_D_ONLINE_CHECKIN_FINISH                = 9020;
    /**Onine Check-In available, Online Check-in is finished, BoardingPass not available*/
    public static final int TYPE_D_ONLINE_CHECKIN_FINISH_BOARDING_PASS_NOT_AVAILABLE   = 9010;
    /**Onine Check-In available, Online Check-in is finished, at the airport*/
    public static final int	TYPE_D_ONLINE_CHECKIN_FINISH_AT_AIRPORT_1H5 = 42;
    /**Onine Check-In not available, not at the airport yet*/
    public static final int TYPE_E_DESK_CHECKIN                         = 9031;
    /**Onine Check-In not available, desk check-in is finished*/
    public static final int TYPE_E_DESK_CHECKIN_FINISH_AT_AIRPORT_1H5   = 9021;
    /**In Flight*/
    public static final int	TYPE_F_IN_FLIGHT                            = 911;
    /**Transition*/
    public static final int	TYPE_G_TRANSITION                           = 912;
    /**Arrival*/
    public static final int	TYPE_H_ARRIVAL                              = 900;



    /**Special Condition - 航班狀態*/
    public static final int FLIGHT_STATUS_DELAY            = 100;
    public static final int FLIGHT_STATUS_FLIGHT_CANCEL    = 101;
    public static final int FLIGHT_STATUS_GATE_CHANGE      = 102;
}
