package ci.ws.Models.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Ewallet - Coupon 回傳資料結構
 * Created by jlchen on 2016/6/16.
 */
@SuppressWarnings("serial")
public class CIInquiryCouponResp implements Serializable {
    public List<CIInquiryCoupon_Info> CouponInfo = new ArrayList<>();
}
