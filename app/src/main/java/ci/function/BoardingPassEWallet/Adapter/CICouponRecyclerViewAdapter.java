package ci.function.BoardingPassEWallet.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;
import ci.ws.Models.entities.CIInquiryCoupon_Info;

/**
 * Created by jlchen on 16/6/16.
 */
public class CICouponRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public interface onCouponRecyclerViewListener{
        void onItemClick(CIInquiryCoupon_Info item);
    }

    private onCouponRecyclerViewListener    m_Listener  = null;

    private Context                         m_context   = null;
    private ViewScaleDef                    m_vScaleDef;
    private ArrayList<CIInquiryCoupon_Info> m_arItemList= new ArrayList<>();

    public CICouponRecyclerViewAdapter(Context context,
                                       ArrayList<CIInquiryCoupon_Info> arDataList,
                                       onCouponRecyclerViewListener listener) {
        this.m_context      = context;
        this.m_vScaleDef    = ViewScaleDef.getInstance(m_context);
        this.m_arItemList   = arDataList;
        this.m_Listener     = listener;
    }

    @Override
    public int getItemCount() {
        return m_arItemList == null ? 0 : m_arItemList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_coupon_view, parent, false);

        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        ItemHolder itemHolder = (ItemHolder)holder;

        m_vScaleDef.selfAdjustAllView(itemHolder.m_rlRoot);
//        m_vScaleDef.selfAdjustSameScaleView(itemHolder.m_rlDisc, 186, 33);

        byte[] bInformationImage = Base64.decode(m_arItemList.get(position).InformationImage, Base64.DEFAULT);
        //圖片圓角處理
        Bitmap bitmap = BitmapFactory.decodeByteArray(
                bInformationImage,
                0,
                bInformationImage.length);
        Bitmap roundedBitmap = ImageHandle.getRoundedCornerBitmap(bitmap,
                m_vScaleDef.getLayoutMinUnit(3),
                true, true, false, false);
        itemHolder.m_ivCoupon.setImageBitmap(roundedBitmap);
        bitmap.recycle();
        System.gc();

        if ( null == m_arItemList.get(position).Title )
            m_arItemList.get(position).Title = "";
        itemHolder.m_tvTitle.setText(m_arItemList.get(position).Title);

        if ( null == m_arItemList.get(position).ExpiryDate )
            m_arItemList.get(position).ExpiryDate = "";
        itemHolder.m_tvDate.setText(m_arItemList.get(position).ExpiryDate);

        if ( null == m_arItemList.get(position).Discont )
            m_arItemList.get(position).Discont = "";
        itemHolder.m_tvNumber.setText(m_arItemList.get(position).Discont);

        if ( null == m_arItemList.get(position).DiscontUnit )
            m_arItemList.get(position).DiscontUnit = "";
        itemHolder.m_tvOff.setText(m_arItemList.get(position).DiscontUnit);

        if ( position == m_arItemList.size()-1 ){
            itemHolder.m_vDiv.getLayoutParams().height = m_vScaleDef.getLayoutHeight(20);
        }else {
            itemHolder.m_vDiv.getLayoutParams().height = m_vScaleDef.getLayoutHeight(10);
        }

        itemHolder.m_rlRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( null != m_Listener )
                    m_Listener.onItemClick(m_arItemList.get(position));
            }
        });
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        RelativeLayout m_rlRoot = null;
        RelativeLayout m_rlDisc = null;
        ImageView   m_ivCoupon  = null;
        TextView    m_tvNumber  = null;
        TextView    m_tvOff     = null;
        TextView    m_tvTitle   = null;
        TextView    m_tvSub     = null;
        TextView    m_tvDate    = null;
        View        m_vDiv      = null;

        public ItemHolder(View view) {
            super(view);
            m_rlRoot    = (RelativeLayout) view.findViewById(R.id.rl_coupon);
            m_rlDisc    = (RelativeLayout) view.findViewById(R.id.rl_discount);
            m_ivCoupon  = (ImageView) view.findViewById(R.id.iv_coupon);
            m_tvNumber  = (TextView) view.findViewById(R.id.tv_number);
            m_tvOff     = (TextView) view.findViewById(R.id.tv_off);
            m_tvTitle   = (TextView) view.findViewById(R.id.tv_title);
            m_tvSub     = (TextView) view.findViewById(R.id.tv_sub_title);
            m_tvDate    = (TextView) view.findViewById(R.id.tv_date);
            m_vDiv      = (View) view.findViewById(R.id.v_div);
        }
    }
}
