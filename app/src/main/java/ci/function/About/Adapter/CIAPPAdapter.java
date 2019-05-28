package ci.function.About.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.function.About.item.CIAPPItem;
import ci.function.Login.api.GooglePlusLoginApi;
import ci.ui.define.ViewScaleDef;
import ci.ui.view.ImageHandle;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by kevincheng on 2016/3/25.
 */
public class CIAPPAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context m_context = null;
    private ViewScaleDef m_vScaleDef;
    private ArrayList<CIAPPItem> m_arItemList = new ArrayList<>();

    public CIAPPAdapter(Context context,
                        ArrayList<CIAPPItem> arDataList) {
        this.m_context = context;
        this.m_vScaleDef = ViewScaleDef.getInstance(m_context);
        this.m_arItemList = arDataList;
    }

    @Override
    public int getItemCount() {
        return m_arItemList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_china_airlines_app_view, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        prepareItem((ItemHolder) holder, position);
    }

    private void prepareItem(ItemHolder holder, final int position) {

        //填入資料
        holder.tvTitle.setText(m_arItemList.get(position).m_strAppName);
        holder.tvSummary.setText(m_arItemList.get(position).m_strSummary);
        Bitmap bitmap = ImageHandle.ByteArrayToBitmap(m_arItemList.get(position).m_byteArrayImg);
        holder.iv_app_icon.setImageBitmap(bitmap);

        //--- head自適應 ---//
        m_vScaleDef.setViewSize(holder.rl_root, MATCH_PARENT, 93.3);
        m_vScaleDef.setViewSize(holder.rl_tv, 218, WRAP_CONTENT);
        m_vScaleDef.setMargins(holder.rl_tv, 16, 0, 0, 0);

        //設定字型大小
        m_vScaleDef.setTextSize(16, holder.tvTitle);
        m_vScaleDef.setTextSize(13, holder.tvSummary);
        m_vScaleDef.setMargins(holder.tvSummary, 0, 4, 0, 0);

        //設定圖形
        m_vScaleDef.selfAdjustSameScaleView(holder.iv_app_icon, 56, 56);
        m_vScaleDef.selfAdjustSameScaleView(holder.iv_array, 24, 24);
        m_vScaleDef.setMargins(holder.iv_app_icon, 16, 0, 0, 0);
        m_vScaleDef.setMargins(holder.iv_array, 10, 0, 0, 0);
        m_vScaleDef.setViewSize(holder.line, MATCH_PARENT, 1);
        //--- head自適應 ---//

        holder.rl_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String packageName = m_arItemList.get(position).m_strAppPackageName;
                Intent intent = m_context.getPackageManager().getLaunchIntentForPackage(packageName);
                if(null != intent){
                    m_context.startActivity(intent);
                } else {
                    if (true == GooglePlusLoginApi.checkPlayServicesShowDialog(m_context)) {
                        Uri uri = Uri.parse("market://details?id=" + packageName);
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        m_context.startActivity(intent);
                    }
                }
            }
        });
    }


    //APP 資料
    public static class ItemHolder extends RecyclerView.ViewHolder {
        LinearLayout rl_root;
        LinearLayout rl_tv;
        ImageView    iv_app_icon;
        ImageView    iv_array;
        TextView     tvTitle;
        TextView     tvSummary;
        View         line;

        public ItemHolder(View view) {
            super(view);
            rl_root = (LinearLayout) view.findViewById(R.id.rl_root);
            rl_tv = (LinearLayout) view.findViewById(R.id.ll_tv);
            iv_app_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
            iv_array = (ImageView) view.findViewById(R.id.iv_array);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            tvSummary = (TextView) view.findViewById(R.id.tv_summary);
            line = view.findViewById(R.id.v_line);
        }
    }

}
