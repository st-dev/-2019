import java.net.*;
import java.sql.*;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;  

public class server {
	
	static Connection connection = null;  
	static Statement statement = null;   
	static ResultSet resultSet = null;      
	static DatagramSocket rs = null;
    
	public server()
	{
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					rs.close();
					resultSet.close();
					statement.close();
					connection.close();
				} catch (Exception e) {}
				System.out.println("服务器已关闭");
			}
		});
	}
	
    public static void main(String[] args) throws Exception
	{		    
    	System.out.println("服务器已启动");
        try {  
        	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        	String connectionString = "jdbc:sqlserver://localhost;" +
	   				  "integratedSecurity=true;" +
	   				  "databaseName=app;"; 
            connection = DriverManager.getConnection(connectionString);              
            statement = connection.createStatement(); 
            System.out.println("连接数据库成功");
        }  
        catch (Exception e) {  
            e.printStackTrace();  
        }  
        
        Set<String> ids = new HashSet<String>();//存放已经预约过的ID  
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");       
		rs = new DatagramSocket(5678);
		
		while(true) {
			byte[] buf = new byte[200];
			DatagramPacket rdp = new DatagramPacket(buf, buf.length);
			rs.receive(rdp);
			String ret = new String(rdp.getData(), rdp.getOffset(), rdp.getLength());
			ret.trim();
			String[] info = ret.split(" ");
			System.out.println("收到提交："+ret);
			
			Calendar lastTime = Calendar.getInstance();//患者预约的前一项检查日期
			int endtime = 0;//患者预约的前一项检查结束时间
			boolean end_mor_aft = false;//患者预约的前一项检查上下午
			boolean lastTimeInit = false;
			
			String msg;
			if(info[4].equals("0") && info[5].equals("0") && info[6].equals("0") && info[7].equals("0"))
				msg = "没有预约任何部位的检查";
			else if(ids.contains(info[0]))
				msg = "已经预约过";
			else 
			{
				ids.add(info[0]);
				msg = "预约成功\n"+
					  "ID: "+info[0]+"\n"+
					  "姓名: "+info[1]+"\n"+
					  "性别: "+info[2]+"\n"+
					  "年龄: "+info[3]+"\n";
				if(info[4].equals("1"))
				{
					Facility tmp = getFac(1);
					msg += "检查血液日期："+format.format(tmp.cal.getTime());
					tmp.time += 30;//假设A项检查用时30分钟
					tmp.num++;
					msg += output(tmp);
					if(tmp.time>=240 && tmp.mor_aft==false)
					{
						tmp.time = 0;
						tmp.mor_aft = true;
						tmp.num = 0;
					}
					if(tmp.time>=240 && tmp.mor_aft==true)
					{
						tmp.time = 0;
						tmp.mor_aft = false;
						tmp.cal.add(Calendar.DAY_OF_YEAR, 1);
						tmp.num = 0;
					}
					lastTime = tmp.cal;
					lastTimeInit = true;
					end_mor_aft = tmp.mor_aft;
					endtime = tmp.time;
					writeback(tmp);
				}
				
				if(info[5].equals("1"))
				{
					Facility tmp = getFac(2);
					msg += "检查胸腔日期："+format.format(tmp.cal.getTime());
					if(!lastTimeInit)
					{
						lastTime = tmp.cal;
						lastTimeInit = true;
						tmp.time += 50;
					}
					else if(tmp.cal.get(6) == lastTime.get(6))//Calendar.get(6)返回DAY_OF_YEAR
					{
						if(tmp.mor_aft==false && end_mor_aft==true) {
							tmp.num = 0;
							tmp.mor_aft = true;
							tmp.time = endtime + 50;
						}
						else if((tmp.mor_aft==false&&end_mor_aft==false&&tmp.time<endtime)||
								(tmp.mor_aft==true&&end_mor_aft==true&&tmp.time<endtime))
							tmp.time = endtime + 50;
						else
							tmp.time += 50;
					}
					else
						tmp.time += 50;//假设B项检查用时50分钟
					tmp.num++;
					msg += output(tmp);
					if(tmp.time>=240 && tmp.mor_aft==false)
					{
						tmp.time = 0;
						tmp.mor_aft = true;
						tmp.num = 0;
					}
					if(tmp.time>=240 && tmp.mor_aft==true)
					{
						tmp.time = 0;
						tmp.mor_aft = false;
						tmp.cal.add(Calendar.DAY_OF_YEAR, 1);
						tmp.num = 0;
					}
					lastTime = tmp.cal;
					end_mor_aft = tmp.mor_aft;
					endtime = tmp.time;
					writeback(tmp);
				}
				
				if(info[6].equals("1"))
				{
					Facility tmp = getFac(3);
					msg += "检查腹腔日期："+format.format(tmp.cal.getTime());
					if(!lastTimeInit)
					{
						lastTime = tmp.cal;
						lastTimeInit = true;
						tmp.time += 70;
					}
					else if(tmp.cal.get(6) == lastTime.get(6))
					{
						if(tmp.mor_aft==false && end_mor_aft==true) {
							tmp.num = 0;
							tmp.mor_aft = true;
							tmp.time = endtime + 70;
						}
						else if((tmp.mor_aft==false&&end_mor_aft==false&&tmp.time<endtime)||
								(tmp.mor_aft==true&&end_mor_aft==true&&tmp.time<endtime))
							tmp.time = endtime + 70;
						else
							tmp.time += 70;
					}
					else
						tmp.time += 70;//假设C项检查用时70分钟					
					tmp.num++;
					msg += output(tmp);
					if(tmp.time>=240 && tmp.mor_aft==false)
					{
						tmp.time = 0;
						tmp.mor_aft = true;
						tmp.num = 0;
					}
					if(tmp.time>=240 && tmp.mor_aft==true)
					{
						tmp.time = 0;
						tmp.mor_aft = false;
						tmp.cal.add(Calendar.DAY_OF_YEAR, 1);
						tmp.num = 0;
					}
					lastTime = tmp.cal;
					end_mor_aft = tmp.mor_aft;
					endtime = tmp.time;
					writeback(tmp);
				}
				
				if(info[7].equals("1"))
				{
					Facility tmp = getFac(4);
					msg += "检查胃镜日期："+format.format(tmp.cal.getTime());
					if(!lastTimeInit)
					{
						lastTime = tmp.cal;
						lastTimeInit = true;
						tmp.time += 90;
					}
					else if(tmp.cal.get(6) == lastTime.get(6))
					{
						if(tmp.mor_aft==false && end_mor_aft==true) {
							tmp.num = 0;
							tmp.mor_aft = true;
							tmp.time = endtime + 90;}
						else if((tmp.mor_aft==false&&end_mor_aft==false&&tmp.time<endtime)||
								(tmp.mor_aft==true&&end_mor_aft==true&&tmp.time<endtime))
							tmp.time = endtime + 90;
						else
							tmp.time += 90;
					}
					else
						tmp.time += 90;//假设D项检查用时90分钟
					tmp.num++;
					msg += output(tmp);
					if(tmp.time>=240 && tmp.mor_aft==false)
					{
						tmp.time = 0;
						tmp.mor_aft = true;
						tmp.num = 0;
					}
					if(tmp.time>=240 && tmp.mor_aft==true)
					{
						tmp.time = 0;
						tmp.mor_aft = false;
						tmp.cal.add(Calendar.DAY_OF_YEAR, 1);
						tmp.num = 0;
					}
					writeback(tmp);
				}			
			}
			DatagramPacket sdp = new DatagramPacket(msg.getBytes(), msg.getBytes().length, rdp.getAddress(), rdp.getPort());
			rs.send(sdp);
			System.out.println("已返回结果");
		}
	}

	public static String output(Facility tmp) 
	{
		String s = "";
		if(tmp.mor_aft) s += "下午  ";
		else s += "上午  ";
		s += "地点：" + tmp.location + "  ";
		s += "机器编号：" + String.valueOf(tmp.no) + "  ";
		s += "预约号码：" + String.valueOf(tmp.num) + "\n";
		return s;
	}
	
	public static Facility getFac(int choose)
	{
		//检查设备比较器
		Comparator<Facility> comp = new Comparator<Facility>() {
			public int compare(Facility f1, Facility f2) {
				if(f1.cal.get(6) < f2.cal.get(6))
					return -1;
				if(f1.cal.get(6) > f2.cal.get(6))
					return 1;
				else 
				{
					if(f1.mor_aft==false && f2.mor_aft==true)
						return -1;
					if(f1.mor_aft==true && f2.mor_aft==false)
						return 1;
					else return f1.time - f2.time;
				}
			}
		};
				
		Queue<Facility> q = new PriorityQueue<>(comp);
		String sql = "select * from facility"; 
	    try {
			resultSet = statement.executeQuery(sql);
		    Facility f;
		    while (resultSet.next())   
		    {
		    	f = new Facility(resultSet.getInt(1),  
		    			resultSet.getString(2).trim(),
		    			resultSet.getBoolean(3), 
		    			resultSet.getBoolean(4), 
		    			resultSet.getBoolean(5), 
		    			resultSet.getBoolean(6),
		    			resultSet.getInt(7),
		    			resultSet.getBoolean(8),
		    			resultSet.getInt(9),
		    			resultSet.getInt(10),
		    			resultSet.getInt(11),
		    			resultSet.getInt(12));
		    	if(choose==1 && f.a)	q.add(f); 
		    	if(choose==2 && f.b)	q.add(f);
		    	if(choose==3 && f.c)	q.add(f);
		    	if(choose==4 && f.d)	q.add(f);
		    }
	    } catch (SQLException e) {e.printStackTrace();}  
	    return q.poll();
	}
	
	public static void writeback(Facility f)
	{
		String sql = "update facility set time = "+Integer.toString(f.time)+
					 ", mor_aft = '"+Boolean.toString(f.mor_aft)+
					 "', num = "+Integer.toString(f.num)+
					 ", cal_y = "+Integer.toString(f.cal.get(1))+//Calendar.get(1)返回YEAR
					 ", cal_m = "+Integer.toString(f.cal.get(2))+//Calendar.get(2)返回MONTH
					 ", cal_d = "+Integer.toString(f.cal.get(5))+//Calendar.get(5)返回DAY_OF_MONTH
					 " where no = "+String.valueOf(f.no);
		try {
			statement.executeUpdate(sql);
	    } catch (SQLException e) {e.printStackTrace();}  
	}
}