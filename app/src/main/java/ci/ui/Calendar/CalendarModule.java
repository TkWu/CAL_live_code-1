package ci.ui.Calendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarModule {

	public final static int MIN_DATE = 0, MAX_DATE = 1;
	public Calendar calendar;
	public CalendarModule() {
		calendar = Calendar.getInstance();
	}
	
	//取得指定年月的最大天數
	public int getMaxDay(int year, int month) {
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		int maxDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		return maxDate;
	}

	//取得指定年月的第一天星期
	public int getFirstDayWeek(int year, int month) {
		return getDayWeek(0, year, month);
	}

	//取得指定年月的最後一天星期
	public int getLastDayWeek(int year, int month) {
		return getDayWeek(1, year, month);
	}

	//取得指定年月第一天或最後一天並轉換成日期(get=0取得第一天，get=1取得最後一天)
	private int getDayWeek(int get, int year, int month) {
		calendar.set(Calendar.YEAR, year);

		if (get == 0) {
			calendar.set(Calendar.MONTH, month - 1);
			calendar.set(Calendar.DAY_OF_MONTH,
			calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		} else {
			calendar.set(Calendar.MONTH, month - 1);
			calendar.set(Calendar.DAY_OF_MONTH,
			calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		}
		calendar.get(Calendar.DAY_OF_WEEK);
		return calendar.get(Calendar.DAY_OF_WEEK);// 星期日 = 1
	}

	//取得當天年份
	public int getYear() {
		calendar.clear();
		calendar.setTime(new Date());
		return calendar.get(Calendar.YEAR);
	}

	//取得當天月份
	public int getMonth() {
		calendar.clear();
		calendar.setTime(new Date());
		return calendar.get(Calendar.MONTH) + 1;
	}

	//取得當天日期
	public int getDay() {
		calendar.clear();
		calendar.setTime(new Date());
		return calendar.get(Calendar.DATE);
	}

	//設定月分格式，並顯示當天年月日
	public String setDateFormat(String format) {
		calendar.clear();
		DateFormat FM = new SimpleDateFormat(format);
		calendar.set(Calendar.YEAR, getYear());
		calendar.set(Calendar.MONTH, getMonth() - 1);
		calendar.set(Calendar.DATE, getDay());
		return FM.format(calendar.getTime());
	}
}
