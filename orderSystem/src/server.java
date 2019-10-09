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
				System.out.println("�������ѹر�");
			}
		});
	}
	
    public static void main(String[] args) throws Exception
	{		    
    	System.out.println("������������");
        try {  
        	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        	String connectionString = "jdbc:sqlserver://localhost;" +
	   				  "integratedSecurity=true;" +
	   				  "databaseName=app;"; 
            connection = DriverManager.getConnection(connectionString);              
            statement = connection.createStatement(); 
            System.out.println("�������ݿ�ɹ�");
        }  
        catch (Exception e) {  
            e.printStackTrace();  
        }  
        
        Set<String> ids = new HashSet<String>();//����Ѿ�ԤԼ����ID  
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");       
		rs = new DatagramSocket(5678);
		
		while(true) {
			byte[] buf = new byte[200];
			DatagramPacket rdp = new DatagramPacket(buf, buf.length);
			rs.receive(rdp);
			String ret = new String(rdp.getData(), rdp.getOffset(), rdp.getLength());
			ret.trim();
			String[] info = ret.split(" ");
			System.out.println("�յ��ύ��"+ret);
			
			Calendar lastTime = Calendar.getInstance();//����ԤԼ��ǰһ��������
			int endtime = 0;//����ԤԼ��ǰһ�������ʱ��
			boolean end_mor_aft = false;//����ԤԼ��ǰһ����������
			boolean lastTimeInit = false;
			
			String msg;
			if(info[4].equals("0") && info[5].equals("0") && info[6].equals("0") && info[7].equals("0"))
				msg = "û��ԤԼ�κβ�λ�ļ��";
			else if(ids.contains(info[0]))
				msg = "�Ѿ�ԤԼ��";
			else 
			{
				ids.add(info[0]);
				msg = "ԤԼ�ɹ�\n"+
					  "ID: "+info[0]+"\n"+
					  "����: "+info[1]+"\n"+
					  "�Ա�: "+info[2]+"\n"+
					  "����: "+info[3]+"\n";
				if(info[4].equals("1"))
				{
					Facility tmp = getFac(1);
					msg += "���ѪҺ���ڣ�"+format.format(tmp.cal.getTime());
					tmp.time += 30;//����A������ʱ30����
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
					msg += "�����ǻ���ڣ�"+format.format(tmp.cal.getTime());
					if(!lastTimeInit)
					{
						lastTime = tmp.cal;
						lastTimeInit = true;
						tmp.time += 50;
					}
					else if(tmp.cal.get(6) == lastTime.get(6))//Calendar.get(6)����DAY_OF_YEAR
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
						tmp.time += 50;//����B������ʱ50����
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
					msg += "��鸹ǻ���ڣ�"+format.format(tmp.cal.getTime());
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
						tmp.time += 70;//����C������ʱ70����					
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
					msg += "���θ�����ڣ�"+format.format(tmp.cal.getTime());
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
						tmp.time += 90;//����D������ʱ90����
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
			System.out.println("�ѷ��ؽ��");
		}
	}

	public static String output(Facility tmp) 
	{
		String s = "";
		if(tmp.mor_aft) s += "����  ";
		else s += "����  ";
		s += "�ص㣺" + tmp.location + "  ";
		s += "������ţ�" + String.valueOf(tmp.no) + "  ";
		s += "ԤԼ���룺" + String.valueOf(tmp.num) + "\n";
		return s;
	}
	
	public static Facility getFac(int choose)
	{
		//����豸�Ƚ���
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
					 ", cal_y = "+Integer.toString(f.cal.get(1))+//Calendar.get(1)����YEAR
					 ", cal_m = "+Integer.toString(f.cal.get(2))+//Calendar.get(2)����MONTH
					 ", cal_d = "+Integer.toString(f.cal.get(5))+//Calendar.get(5)����DAY_OF_MONTH
					 " where no = "+String.valueOf(f.no);
		try {
			statement.executeUpdate(sql);
	    } catch (SQLException e) {e.printStackTrace();}  
	}
}