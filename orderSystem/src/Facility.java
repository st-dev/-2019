import java.util.Calendar;

public class Facility
{
	int no;
	String location;
	boolean a;
	boolean b;
	boolean c;
	boolean d;
	int time; //预计结束时间
	boolean mor_aft; //0是上午1是下午
	int num; //预约人数
	Calendar cal; //日期
	
	Facility(int no, String location, boolean a, boolean b, boolean c, boolean d, int t, boolean m, int n, 
			int cal_y, int cal_m, int cal_d)
	{
		this.no = no;
		this.location = location;
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		time = t;
		mor_aft = m;
		num = n;
		cal = Calendar.getInstance();
		cal.set(cal_y, cal_m, cal_d);
	}
}
