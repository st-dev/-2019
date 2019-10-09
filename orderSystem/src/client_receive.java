import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.awt.Color;

public class client_receive {
	public client_receive(DatagramSocket soc) {
		JFrame frame2 = new JFrame();
		frame2.getContentPane().setBackground(Color.WHITE);
		frame2.setTitle("预约结果");
		frame2.setSize(500, 300);
		frame2.getContentPane().setLayout(null);		
		
		JTextArea text = new JTextArea();
		text.setEditable(false);
		text.setBounds(30, 50, 500, 180);
		frame2.getContentPane().add(text);
		
		text.setText("请等待...\n");
		byte[] buf = new byte[1000];
		DatagramPacket rdp = null;
		try {
			rdp = new DatagramPacket(buf, buf.length);
			soc.receive(rdp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String ret = new String(rdp.getData(),rdp.getOffset(),rdp.getLength());
		text.setText(ret);		

		frame2.setVisible(true);
		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
