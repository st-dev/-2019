import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class client_send {

	public static void main(String[] args) {	
		JFrame frame = new JFrame();
		frame.setTitle("预约表单");
		frame.setSize(500, 300);
		frame.getContentPane().setLayout(null);
		
		JLabel ID = new JLabel("身份证号");
		ID.setBounds(75, 35, 100, 20);
		frame.getContentPane().add(ID);
		
		JLabel name = new JLabel("姓名");
		name.setBounds(75, 65, 100, 20);
		frame.getContentPane().add(name);
		
		JLabel sex = new JLabel("性别");
		sex.setBounds(75, 95, 100, 20);
		frame.getContentPane().add(sex);
		
		JLabel age = new JLabel("年龄");
		age.setBounds(75, 125, 100, 20);
		frame.getContentPane().add(age);	

		JLabel label = new JLabel("预约项目");
		label.setBounds(75, 155, 100, 20);
		frame.getContentPane().add(label);
				
		JTextField text1 = new JTextField();
		text1.setBounds(200, 35, 200, 20);
		frame.getContentPane().add(text1);
		
		JTextField text2 = new JTextField();
		text2.setBounds(200, 65, 200, 20);
		frame.getContentPane().add(text2);
		
		JTextField text3 = new JTextField();
		text3.setBounds(200, 95, 200, 20);
		frame.getContentPane().add(text3);
		
		JTextField text4 = new JTextField();
		text4.setBounds(200, 125, 200, 20);
		frame.getContentPane().add(text4);
		
		JPanel panel = new JPanel();
		panel.setBounds(180, 145, 240, 30);
		frame.getContentPane().add(panel);
		
		JCheckBox item1 = new JCheckBox("血液");
		panel.add(item1);
		
		JCheckBox item2 = new JCheckBox("胸腔");
		panel.add(item2);
		
		JCheckBox item3 = new JCheckBox("腹腔");
		panel.add(item3);
		
		JCheckBox item4 = new JCheckBox("胃");
		panel.add(item4);		

		JTextPane textPane = new JTextPane();
		textPane.setText("须知：血液项目需空腹检查");
		textPane.setBounds(75, 180, 320, 30);
		textPane.setEditable(false);
		frame.getContentPane().add(textPane);

		JButton submit = new JButton("提交");
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(text1.getText().equals(""))
					JOptionPane.showMessageDialog(null, "身份证号不能为空", "提示", JOptionPane.PLAIN_MESSAGE);
				else
				{
					String str = text1.getText()
						    +" "+text2.getText()
						    +" "+text3.getText()
						    +" "+text4.getText();				
					for(int i=0;i<4;i++)
					{
						if(((JCheckBox)panel.getComponent(i)).isSelected())
							str += " 1";
						else
							str += " 0";
					}
					
					DatagramSocket soc = null;
					try {
						soc = new DatagramSocket();
						DatagramPacket sdp = new DatagramPacket(
							str.getBytes(),
							str.getBytes().length,
							InetAddress.getByName("127.0.0.1"),
							5678);
						soc.send(sdp);
					} catch (Exception e) {
						e.printStackTrace();
					}							
					closeThis();
					new client_receive(soc);
				}			
			}
			private void closeThis() {
				frame.dispose();
			}			
		});	
		submit.setBounds(180, 215, 75, 25);
		frame.getContentPane().add(submit);
		
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
