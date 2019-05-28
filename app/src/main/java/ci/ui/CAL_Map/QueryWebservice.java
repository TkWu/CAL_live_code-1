package ci.ui.CAL_Map;



import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ci.ws.Models.entities.CITripListResp_Itinerary;
import ci.ws.define.BaseWSConfig;

public class QueryWebservice {
	private String WSDL_TARGET_NAMESPACE = "http://tempuri.org/";
	private String ACTION_FLIGHTROUTE = "FlightRoute";
	private String ACTION_FLIGHTCURRENTLOCATION = "IService/FlightCurrentLocation";

	private String SOAP_ACTION = "";
	private String OPERATION_NAME = "";

	//2016-12-14 Modify by Ryan
	//參照 mail 變更3.0 App 所使用的URL連結
	//yichien.chen@china-airlines.com
	//public String SOAP_ADDRESS = "http://mweb.china-airlines.com/mobile30/fltstatus/Service.svc";
	public String SOAP_ADDRESS = "https://" + BaseWSConfig.DEF_MOBILE30_BASE_WS_SITE + "/mobile30/fltstatus/Service.svc";

	private CITripListResp_Itinerary m_data = null;
	public QueryWebservice(int caseNum,CITripListResp_Itinerary data) {
		m_data = data;
		switch (caseNum) {
		case 1: // ACTION_FLIGHTROUTE
			SOAP_ACTION = WSDL_TARGET_NAMESPACE + "IService/"
					+ ACTION_FLIGHTROUTE;
			OPERATION_NAME = ACTION_FLIGHTROUTE;
			break;
		case 2: // ACTION_FLIGHTCURRENTLOCATION
			SOAP_ACTION = WSDL_TARGET_NAMESPACE + "IService/"
					+ ACTION_FLIGHTCURRENTLOCATION;
			OPERATION_NAME = ACTION_FLIGHTCURRENTLOCATION;
			break;

		}
	}

	public String QueryMapLatLong() {

		if(null == m_data){
			return null;
		}

		String date = m_data.Departure_Date.replace("-","") + " " + m_data.Departure_Time;

		try {
			URL               url               = new URL(SOAP_ADDRESS);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
			httpURLConnection.setRequestProperty("SOAPAction", SOAP_ACTION);
			String xml= "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">"+
					"<soapenv:Header/>"+
					"<soapenv:Body>"+
					"<tem:FlightRoute>"+
					"<tem:airlineCode>"+m_data.Airlines+"</tem:airlineCode>"+
					"<tem:flightNum>"+m_data.Flight_Number+"</tem:flightNum>"+
					"<tem:departureDate>"+date+"</tem:departureDate>"+
					"<tem:departureAirport>"+m_data.Departure_Station+"</tem:departureAirport>"+
					"<tem:arrivalAirport>"+m_data.Arrival_Station+"</tem:arrivalAirport>"+
					"</tem:FlightRoute>"+
					"</soapenv:Body>"+
					"</soapenv:Envelope>";

			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			OutputStream         outputStream   = httpURLConnection.getOutputStream();
			BufferedOutputStream bufferedWriter = new BufferedOutputStream(outputStream);
			bufferedWriter.write(xml.getBytes());

			bufferedWriter.flush();
			bufferedWriter.close();
			outputStream.close();

			int status=httpURLConnection.getResponseCode();
			if(status == 200)
			{   InputStream    inputStream    = httpURLConnection.getInputStream();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
				String         result         = "";
				String         line;
				while ((line = bufferedReader.readLine()) != null) {
					result += line;
				}
				bufferedReader.close();
				inputStream.close();
				int start_idx = result.indexOf("{\"Flights\"");
				int end_idx = result.indexOf("</FlightRouteResult>");
				result = result.substring(start_idx, end_idx);
				return result;
			}
			else
				return null;

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


}
