package ci.function.Base;


import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chinaairlines.mobile30.R;

import java.util.ArrayList;

import ci.ui.define.HomePage_Status;
import ci.ui.define.ViewScaleDef;
import ci.ui.object.AppInfo;
import ci.ws.Models.CIInquiryCheckInModel;
import ci.ws.Models.CIInquiryTripModel;

/**
 * for Demo
 */
public class CIDemoFragment extends BaseFragment {

    private ListView    m_listview  = null;
    private DemoAdapter m_Adpater   = null;

    private ArrayList<Item> m_dataList = new ArrayList<>();

    class Item{

        String  strName;
        int     iStatus;
    }

    public enum EMode{
        A_NO_TICKET,
        B_HAVE_TICKET,
        C_SEAT,
        C_SEAT_MEAL,
        D_ONLINE_CHECKIN,
        D_WEB_ONLINE_CHECKIN,
        D_ONLINE_CHECKIN_FINISH,
        D_ONLINE_CHECKIN_FINISH_AT_AIRPORT_1H5,
        E_CHECKIN_IN_DESK,
        E_CHECKIN_IN_DESK_FINISH_AT_AIRPORT_1H5,
        F_IN_FLIGHT,
        G_TRANSITION,
        H_ARRIVAL,
        CANCEL

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_demo;
    }

    @Override
    protected void initialLayoutComponent(LayoutInflater inflater, View view) {

        m_listview = (ListView)view.findViewById(R.id.listView);

        m_Adpater = new DemoAdapter();
        m_listview.setAdapter(m_Adpater);
        m_listview.setItemsCanFocus(true);
        m_listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        m_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                setDemoFile(EMode.valueOf(m_dataList.get(position).strName));

                //CIToastView.makeText( getContext(), m_dataList.get(position).strName ).show();
//                AppInfo.getInstance(getContext()).SetHoemPageStatus( m_dataList.get(position).iStatus );
                getActivity().onBackPressed();

            }

        });

        m_dataList.clear();

        Item item = new Item();
        item.strName = EMode.A_NO_TICKET.name();
        item.iStatus = HomePage_Status.TYPE_A_NO_TICKET;
        m_dataList.add(item);

        item = new Item();
        item.strName = EMode.B_HAVE_TICKET.name();
        item.iStatus = HomePage_Status.TYPE_B_HAVE_TICKET;
        m_dataList.add(item);

        item = new Item();
        item.strName = EMode.C_SEAT.name();
        item.iStatus = HomePage_Status.TYPE_C_SELECT_SEAT_180D_14D;
        m_dataList.add(item);

        item = new Item();
        item.strName = EMode.C_SEAT_MEAL.name();
        item.iStatus = HomePage_Status.TYPE_C_SEAT_MEAL_14D_24H;
        m_dataList.add(item);

        item = new Item();
        item.strName = EMode.D_ONLINE_CHECKIN.name();
        item.iStatus = HomePage_Status.TYPE_D_ONLINE_CHECKIN;
        m_dataList.add(item);

        item = new Item();
        item.strName = EMode.D_WEB_ONLINE_CHECKIN.name();
        item.iStatus = HomePage_Status.TYPE_D_ONLINE_CHECKIN;
        m_dataList.add(item);

        item = new Item();
        item.strName = EMode.D_ONLINE_CHECKIN_FINISH.name();
        item.iStatus = HomePage_Status.TYPE_D_ONLINE_CHECKIN_FINISH;
        m_dataList.add(item);

        item = new Item();
        item.strName = EMode.D_ONLINE_CHECKIN_FINISH_AT_AIRPORT_1H5.name();
        item.iStatus = HomePage_Status.TYPE_D_ONLINE_CHECKIN_FINISH_AT_AIRPORT_1H5;
        m_dataList.add(item);

        item = new Item();
        item.strName = EMode.E_CHECKIN_IN_DESK.name();
        item.iStatus = HomePage_Status.TYPE_E_DESK_CHECKIN;
        m_dataList.add(item);

        item = new Item();
        item.strName = EMode.E_CHECKIN_IN_DESK_FINISH_AT_AIRPORT_1H5.name();
        item.iStatus = HomePage_Status.TYPE_E_DESK_CHECKIN_FINISH_AT_AIRPORT_1H5;
        m_dataList.add(item);

        item = new Item();
        item.strName = EMode.F_IN_FLIGHT.name();
        item.iStatus = HomePage_Status.TYPE_F_IN_FLIGHT;
        m_dataList.add(item);

        item = new Item();
        item.strName = EMode.G_TRANSITION.name();
        item.iStatus = HomePage_Status.TYPE_G_TRANSITION;
        m_dataList.add(item);

        item = new Item();
        item.strName = EMode.H_ARRIVAL.name();
        item.iStatus = HomePage_Status.TYPE_H_ARRIVAL;
        m_dataList.add(item);

        item = new Item();
        item.strName = EMode.CANCEL.name();
        item.iStatus = 0;
        m_dataList.add(item);
        m_Adpater.notifyDataSetChanged();
    }

    @Override
    protected void setTextSizeAndLayoutParams(View view, ViewScaleDef vScaleDef) {}

    @Override
    protected void setOnParameterAndListener(View view) {}

    @Override
    protected void registerFragment(FragmentManager fragmentManager) {}

    @Override
    protected boolean bOtherHandleMessage(Message msg) {
        return false;
    }

    @Override
    protected void removeOtherHandleMessage() {}

    @Override
    public void onLanguageChangeUpdateUI() {}

    @Override
    public void onResume() {
        super.onResume();

        Toast.makeText( getContext(), "進入Demo模式！", Toast.LENGTH_SHORT ).show();
    }

    class DemoAdapter extends BaseAdapter{


        class ItemHoler{

            TextView tvName;
        }

        @Override
        public int getCount() {
            return m_dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return m_dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ItemHoler holder = new ItemHoler();
            if ( null == convertView ){

                convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_demo_item_view, parent, false);
                holder.tvName = (TextView)convertView.findViewById(R.id.tvName);
                convertView.setTag(holder);

            } else {
                holder = (ItemHoler)convertView.getTag();
            }

            holder.tvName.setText( ((Item)getItem(position)).strName );

            return convertView;
        }

    }

    public static void setDemoFile(EMode mode){
        switch (mode){
            case A_NO_TICKET:
                CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_NoTicket.json");
                CIInquiryTripModel.setIsOnSuccess(true);
                CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_3D.json");
                CIInquiryCheckInModel.setIsOnSuccess(true);
                break;
            case B_HAVE_TICKET:
                CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_6.json");
                CIInquiryTripModel.setIsOnSuccess(true);
                CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_3D.json");
                CIInquiryCheckInModel.setIsOnSuccess(true);
                break;
            case C_SEAT:
                CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_5.json");
                CIInquiryTripModel.setIsOnSuccess(true);
                CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_3D.json");
                CIInquiryCheckInModel.setIsOnSuccess(true);
                break;
            case C_SEAT_MEAL:
                CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_4.json");
                CIInquiryTripModel.setIsOnSuccess(true);
                CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_3D.json");
                CIInquiryCheckInModel.setIsOnSuccess(true);
                break;
            case D_ONLINE_CHECKIN:
                CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_3.json");
                CIInquiryTripModel.setIsOnSuccess(true);
                CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_3D.json");
                CIInquiryCheckInModel.setIsOnSuccess(true);
                break;
            case D_WEB_ONLINE_CHECKIN:
                CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_3.json");
                CIInquiryTripModel.setIsOnSuccess(true);
                CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_3Dw.json");
                CIInquiryCheckInModel.setIsOnSuccess(false);
                break;
            case D_ONLINE_CHECKIN_FINISH:
                CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_3.json");
                CIInquiryTripModel.setIsOnSuccess(true);
                CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_3Df.json");
                CIInquiryCheckInModel.setIsOnSuccess(true);
                break;
            case D_ONLINE_CHECKIN_FINISH_AT_AIRPORT_1H5:
                CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_2.json");
                CIInquiryTripModel.setIsOnSuccess(true);
                CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_2Dg.json");
                CIInquiryCheckInModel.setIsOnSuccess(true);
                break;
            case E_CHECKIN_IN_DESK:
                CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_3.json");
                CIInquiryTripModel.setIsOnSuccess(true);
                CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_3E.json");
                CIInquiryCheckInModel.setIsOnSuccess(true);
                break;
            case E_CHECKIN_IN_DESK_FINISH_AT_AIRPORT_1H5:
                CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_2.json");
                CIInquiryTripModel.setIsOnSuccess(true);
                CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_2Eg.json");
                CIInquiryCheckInModel.setIsOnSuccess(true);
                break;
            case F_IN_FLIGHT:
                CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_11.json");
                CIInquiryTripModel.setIsOnSuccess(true);
                CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_2Dg.json");
                CIInquiryCheckInModel.setIsOnSuccess(true);
                break;
            case G_TRANSITION:
                CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_12.json");
                CIInquiryTripModel.setIsOnSuccess(true);
                CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_2Dg.json");
                CIInquiryCheckInModel.setIsOnSuccess(true);
                break;
            case H_ARRIVAL:
                CIInquiryTripModel.setTestFileName("HomeStatus/MyTripByCardNo_0.json");
                CIInquiryTripModel.setIsOnSuccess(true);
                CIInquiryCheckInModel.setTestFileName("HomeStatus/InquiryCheckInByCard_2Dg.json");
                CIInquiryCheckInModel.setIsOnSuccess(true);
                break;
            case CANCEL:
                CIInquiryTripModel.setTestFileName(null);
                CIInquiryTripModel.setIsOnSuccess(true);
                CIInquiryCheckInModel.setTestFileName(null);
                CIInquiryCheckInModel.setIsOnSuccess(true);
                break;

        }
    }

}
