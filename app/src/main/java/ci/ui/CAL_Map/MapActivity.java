package ci.ui.CAL_Map;
/*
 * 1. fragment replace: replace the view of map
 * 2. QWebservice: get route data
 * 3. change_btn enlarge/shrink map view
 * 4. timer: re-get data every 300 secs
 * */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.chinaairlines.mobile30.R;


public class MapActivity extends FragmentActivity implements
		FlightLocationManager.Callback{

	private RelativeLayout map_layout;
	private boolean isfull = false;
	private String returnS = "";
	private Button change_btn;
	private FlightLocationManager m_manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cal_map);
		map_layout = (RelativeLayout) findViewById(R.id.map_layout);
		change_btn = (Button) findViewById(R.id.change_btn);
		m_manager = new FlightLocationManager(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		m_manager.executeTask(null);
	}

	@Override
	protected void onPause() {
		super.onPause();
		m_manager.cancleTask();
	}

	public void changeFragment(Fragment f) {
		DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
		int width = displayMetrics.widthPixels;
		int height = displayMetrics.heightPixels;
		
		if (isfull) {
			map_layout.getLayoutParams().height = height;
			map_layout.getLayoutParams().width = width;
			map_layout.requestLayout();
		} else {
			map_layout.getLayoutParams().height = height / 2;
			map_layout.getLayoutParams().width = width;
			map_layout.requestLayout();

		}
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.map_layout, f);
		transaction.commitAllowingStateLoss();
	}

	public void mapButton(View v) {
		isfull = !isfull;
		Fragment fr = MapFragment.newInstance(returnS);
		FragmentChangeListener fc = (FragmentChangeListener) this;
		fc.changeFragment(fr);

		if (!isfull) {
			change_btn.setBackgroundResource(getResources().getIdentifier(
					"@drawable/fullscreen", "drawable", "MapActivity"));
		} else {
			change_btn.setBackgroundResource(getResources().getIdentifier(
					"@drawable/fullscreen_exit", "drawable", "MapActivity"));
		}
	}

	@Override
	public void onDataBinded(String result) {
		changeFragment(MapFragment.newInstance(result));
	}
}
