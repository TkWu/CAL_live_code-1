package ci.ws.define;

/**
 * Created by Ryan on 16/5/19.
 */
public class CIWSHomeStatus_Code {

    /**No Ticket*/
    public static final int	TYPE_A_NO_TICKET                            = 999;
    /**have ticket*/
    public static final int	TYPE_B_HAVE_TICKET                          = 6;
    /**have ticket, can select seat meal
     * 14Day~24hr*/
    public static final int	TYPE_C_SEAT_MEAL_14D_24H                    = 4;
    /**have ticket, can select seat
     * 180Day~14Day*/
    public static final int	TYPE_C_SEAT_180D_14D                        = 5;
    /**顯示Ceck-in or 臨櫃報到*/
    public static final int TYPE_D_E_CHECKIN                            = 3;
    /**顯示登機門*/
    public static final int TYPE_D_E_GATE_INFO                          = 2;
    /**In Flight*/
    public static final int	TYPE_F_IN_FLIGHT                            = 11;
    /**Transition*/
    public static final int	TYPE_G_TRANSITION                           = 12;
    /**Arrival*/
    public static final int	TYPE_H_ARRIVAL                              = 0;

}
