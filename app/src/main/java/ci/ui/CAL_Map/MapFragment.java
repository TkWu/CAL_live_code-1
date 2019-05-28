package ci.ui.CAL_Map;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chinaairlines.mobile30.R;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ci.function.Core.SLog;

public class MapFragment extends Fragment implements OnMapReadyCallback {
	private View v;
	private List<LatLng> routeArray = new ArrayList<LatLng>();
	private GoogleMap mMap;
	private static String resultString = "";
	private static final String RESULT_STRING= "RESULT_STRING";
//	private static Boolean indexFull = false;
	LatLngBounds bounds = null;

	public static MapFragment newInstance(String _inResult) {
		Bundle bundle = new Bundle();
		bundle.putString(RESULT_STRING, _inResult);
		MapFragment f = new MapFragment();
		f.setArguments(bundle);
//		indexFull = _indexFull;
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(null != getArguments()) {
			resultString = getArguments().getString(RESULT_STRING,"");
		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		v = inflater.inflate(R.layout.fragment_cal_map, container, false);

		SupportMapFragment mapFragment = (SupportMapFragment) this
				.getChildFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		//Todd added
		return v;
	}

	// Obtain the SupportMapFragment and get notified when the map is ready
	// to be used.

	public void drawLine(List<LatLng> points) {
		if (points == null) {
		SLog.d("Draw Line", "got null as parameters");
			return;
		}
		Polyline line = mMap.addPolyline(new PolylineOptions().width(3).color(
				Color.BLUE));
		line.setPoints(points);
	}

	// ���ʦa�Ϩ�Ѽƫ��w����m
	private void moveMap(LatLng place) {
		// CameraPosition cameraPosition = new CameraPosition.Builder()
		// .target(place).zoom(1).build();

		int padding = 200; // offset from edges of the map in pixels
		try {
			final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
			//排在Thread最後才執行縮放畫面 by kevin
			new Handler().post(new Runnable() {
				@Override
				public void run() {
					try {
						mMap.animateCamera(cu);
					} catch (RuntimeException e) {
						Crashlytics.logException(e);
					}
				}
			});
		SLog.d("moveMap","moveMap");
		} catch (Exception ex) {
		SLog.e("moveMap",ex.toString());
		}

		//
		// if (path.count > 0) {
		// GMSCoordinateBounds *bounds = [[GMSCoordinateBounds alloc]
		// initWithPath:path];
		// GMSCameraUpdate *update = [GMSCameraUpdate fitBounds:bounds
		// withPadding:50.0f];
		// [_mapView animateWithCameraUpdate:update];
		// }
		// GMSVisibleRegion visibleRegion = _mapView.projection.visibleRegion;
		// GMSCoordinateBounds *bounds = [[GMSCoordinateBounds alloc]
		// initWithRegion:visibleRegion];
		// BOOL contains = [bounds containsCoordinate:_markerPlant.position];
		// if (contains == NO) {
		// GMSCameraUpdate *camP = [GMSCameraUpdate
		// setTarget:_markerPlant.position];
		// [_mapView animateWithCameraUpdate:camP];
		// }
	}

	private void setUpMap() {
		JSONObject DEP_object = null;
		JSONObject ARR_object = null;
		JSONObject CURRENT_LOCATION = null;

		try {
			JSONObject Return_object = new JSONObject(resultString);

			if (Return_object.getInt("Result") == 1) {
				JSONArray Flights = Return_object.getJSONArray("Flights");
				JSONObject tmp_1 = Flights.getJSONObject(0);
				DEP_object = tmp_1.getJSONObject("DepartureAirport");
				ARR_object = tmp_1.getJSONObject("ArrivalAirport");
				CURRENT_LOCATION = tmp_1.getJSONObject("CurrentPosition");
				JSONArray RoutePath = tmp_1.getJSONArray("RoutePath");

				for (int i = 0; i < RoutePath.length(); i++) {
					JSONObject json = RoutePath.getJSONObject(i);
					try {

						final Double lat = json.getDouble("LatitudeDegree");
						final Double lng = json.getDouble("LongitudeDegree");
						LatLng latLng = new LatLng(lat, lng);
						if (!routeArray.contains(latLng)) {
							routeArray.add(latLng);
						}
					} catch (Exception e) {
					SLog.e("Exception", e.toString());
						e.printStackTrace();
						return;
					}
				}

				drawLine(routeArray);
				LatLng dep_location = new LatLng(
						DEP_object.getDouble("LatitudeDegree"),
						DEP_object.getDouble("LongitudeDegree"));
				LatLng arr_location = new LatLng(
						ARR_object.getDouble("LatitudeDegree"),
						ARR_object.getDouble("LongitudeDegree"));

				LatLngBounds.Builder builder = new LatLngBounds.Builder();
				builder.include(dep_location);
				builder.include(arr_location);
				bounds = builder.build();

				LatLng CURRENT = new LatLng(
						CURRENT_LOCATION.getDouble("LatitudeDegree"),
						CURRENT_LOCATION.getDouble("LongitudeDegree"));

				float RotateDegree = Float.parseFloat(CURRENT_LOCATION
						.getString("RotateDegree"));

				TextView textDEP = new TextView(getActivity());
				textDEP.setText(DEP_object.getString("AirportIataCode"));
				textDEP.setTextColor(getResources().getColor(R.color.alert_dialog_background));
				textDEP.setBackgroundColor(getResources().getColor(R.color.d90f17));
				IconGenerator generatorDEP = new IconGenerator(getActivity());
				generatorDEP.setContentView(textDEP);
				Bitmap iconDEP = generatorDEP.makeIcon();
				MarkerOptions MarkerOptionDEP = new MarkerOptions().position(dep_location).icon(BitmapDescriptorFactory.fromBitmap(iconDEP));
				mMap.addMarker(MarkerOptionDEP);


				TextView textARR = new TextView(getActivity());
				textARR.setText(ARR_object.getString("AirportIataCode"));
				textARR.setTextColor(getResources().getColor(R.color.alert_dialog_background));
				textARR.setBackgroundColor(getResources().getColor(R.color.d90f17));
				IconGenerator generatorARR = new IconGenerator(getActivity());
				generatorARR.setContentView(textARR);
				Bitmap iconARR = generatorARR.makeIcon();
				MarkerOptions MarkerOptionARR = new MarkerOptions().position(arr_location).icon(BitmapDescriptorFactory.fromBitmap(iconARR));
				mMap.addMarker(MarkerOptionARR);

				addPlaneMarker(CURRENT, RotateDegree);
				routeArray.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Manipulates the map once available. This callback is triggered when the
	 * map is ready to be used. This is where we can add markers or lines, add
	 * listeners or move the camera. In this case, we just add a marker near
	 * Sydney, Australia. If Google Play services is not installed on the
	 * device, the user will be prompted to install it inside the
	 * SupportMapFragment. This method will only be triggered once the user has
	 * installed Google Play services and returned to the app.
	 */
	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		LatLng TYairport = new LatLng(25.079943, 121.234185);
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(TYairport, 1));
		setUpMap();
	}

	/**2017-02-17 Modify by tz-ke.wu mail 提供src code */
	private void addPlaneMarker(LatLng place, float RotateDegree) {
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x/15;
		int height = width;

		//調整寫法, 2017-02-17 Modify by Ryan
		Resources res = getResources();
		Bitmap b = BitmapFactory.decodeResource(res, R.drawable.icon_plane);
//		BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.icon_plane);
//		Bitmap b=bitmapdraw.getBitmap();
		Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
		BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(smallMarker);
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(place).icon(icon).rotation(RotateDegree);//.anchor(0.5f,0.5f);
		mMap.addMarker(markerOptions);
		moveMap(place);
	}

//	private void addPlaneMarker(LatLng place, float RotateDegree) {
//		BitmapDescriptor icon = BitmapDescriptorFactory
//				.fromResource(R.drawable.icon_plane);
//
//		MarkerOptions markerOptions = new MarkerOptions();
//		markerOptions.position(place).icon(icon).rotation(RotateDegree);
//		mMap.addMarker(markerOptions);
//		moveMap(place);
//	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if(null != mMap){
			mMap.clear();
		}
		routeArray.clear();
		mMap = null;
		routeArray = null;
		System.gc();
	}
}
