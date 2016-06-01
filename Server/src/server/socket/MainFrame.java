package server.socket;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class MainFrame extends JFrame implements ActionListener {
	JButton shutdown;
	JButton service;
	JButton app;
	JButton keystroke;
	JButton exit;
	JLabel statusBar;
	final int port = 31249;
	
	static Service serv;
	static Application appli;
	static KeyStroke ks;

	ServerSocketChannel serverSocket;
	InetSocketAddress serverAddr;
	public SocketChannel client;

	public MainFrame(String title){
		super(title);
		addWindowListener( 
	              new java.awt.event.WindowAdapter() {
	                public void windowClosing( java.awt.event.WindowEvent e ) {
	                	if (!statusBar.getText().contains("Wait")){
	            			SendReceive send = new SendReceive();
	                		send.sendClose(client);
	                    	}
	                  dispose() ;
	                  System.exit( 0 );
	                }
	                }
	            );
		
		//XÂY DỰNG GIAO DIỆN
		this.setLayout(null);
		
		//awt: Các nút khác
		shutdown = new JButton("Shutdown");
		service = new JButton("Service...");
		app = new JButton("Application...");
		keystroke = new JButton("Keystroke");
		exit = new JButton("Exit");
		statusBar = new JLabel("Wait for connection...");
		
		shutdown.setBounds(20,30,120,50);
		service.setBounds(150, 30, 120, 50);
		app.setBounds(280, 30, 120, 50);
		keystroke.setBounds(410,30,120,50);
		exit.setBounds(195, 100,140,50);
		statusBar.setBounds(10,160,300,25);
		
		this.add(service);
		this.add(app);
		this.add(shutdown);
		this.add(keystroke);
		this.add(exit);
		this.add(statusBar);
		this.pack();
		this.setSize(570, 220);
		this.setLocation(100, 100);
		this.setVisible(true);
		
		exit.addActionListener(this);
		shutdown.addActionListener(this);
		service.addActionListener(this);
		app.addActionListener(this);
		keystroke.addActionListener(this);
	}
	
	public static void main(String args[]) throws IOException, InterruptedException{
		MainFrame server = new MainFrame("Server");
		serv = new Service(server);
		appli = new Application(server);
		ks = new KeyStroke(server);
		server.new ServerConnection();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		SendReceive send = new SendReceive();
		
		//Xử lý sự kiện thoát
		if (arg0.getSource()==exit){
			if (!statusBar.getText().contains("Wait")){
    			send.sendClose(client);
            	}
			System.exit(0);
		}
		//Xử lý khi chưa kết nối
		else if (statusBar.getText().contains("Wait")){
			JOptionPane.showMessageDialog(this, "No connection");
		}
		//Xử lý khi chọn shutdown
		else if (arg0.getSource()==shutdown){
			send.sendShutdown(client);
		}
		//Xử lý khi chọn services...
		else if (arg0.getSource()==service){
			serv.show();
		}
		//Xử lý khi chọn Application...
		else if (arg0.getSource()==app){
			appli.show();
		}
		//Xử lý khi chọn Keystroke
		else if(arg0.getSource()==keystroke){
			ks.show();
		}
	}
	
	//Lắng nghe kết nối từ client
	public class ServerConnection{
		public ServerConnection() throws IOException, InterruptedException{
			Selector selector = Selector.open();
			serverSocket = ServerSocketChannel.open();
			serverAddr = new InetSocketAddress(port);
			
			//Non blocking socket
			serverSocket.bind(serverAddr);
			serverSocket.configureBlocking(false);
			
			int ops = serverSocket.validOps();
			SelectionKey selectKy = serverSocket.register(selector, ops, null);
			
			SendReceive r = new SendReceive();
			while (true){
				// Lấy danh sách các kênh kết nối tới
				selector.select();
				
				Set<SelectionKey> crunchifyKeys = selector.selectedKeys();
				Iterator<SelectionKey> crunchifyIterator = crunchifyKeys.iterator();
 
				while (crunchifyIterator.hasNext()) {
					SelectionKey myKey = crunchifyIterator.next();
					
					//Kênh sẵn sàng kết nối
					if (myKey.isAcceptable()) {
						client = serverSocket.accept();
						// Thiết lập kênh non blocking
						client.configureBlocking(false);
 
						// Thiết lập sẵn sàng đọc
						client.register(selector, SelectionKey.OP_READ);
						
						// kênh có sẵn sàng để đọc chưa
						statusBar.setText("Connection established: " +
		                        client.socket().getRemoteSocketAddress());							
						
					} else if (myKey.isReadable()) {
						client = (SocketChannel) myKey.channel();
						String result = r.receive(client);
						System.out.println(result);
						if(result.equals("fail")){
							client.close();
							statusBar.setText("Wait for connection...");
						}
						else forReceive(result);
					}
					
					crunchifyIterator.remove();
				}
			}
		}
	}
	
	//Xử lý khi nhận tin
	public void forReceive(String result){
		SendReceive r = new SendReceive();
		//Chạy dịch vụ thành công
		if (result.equals("startsok")){
			JOptionPane.showMessageDialog(this, "Start service successfully");
		}
		//Chạy dịch vụ thất bại
		else if (result.equals("startsfail")){
			JOptionPane.showMessageDialog(this, "Start service failed");
		}
		//Dừng dịch vụ thành công
		else if (result.equals("stopsok")){
			JOptionPane.showMessageDialog(this, "Stop service successfully");
		}
		//Dừng dịch vụ thất bại
		else if (result.equals("stopsfail")){
			JOptionPane.showMessageDialog(this, "Stop service failed");
		}
		//Chạy ứng dụng thành công
		else if (result.equals("startaok")){
			JOptionPane.showMessageDialog(this, "Start application successfully");
		}
		//Chạy ứng dụng thất bại
		else if (result.equals("startafail")){
			JOptionPane.showMessageDialog(this, "Start application failed");
		}
		//Dừng ứng dụng thành công
		else if (result.equals("stopaok")){
			JOptionPane.showMessageDialog(this, "Stop application successfully");
		}
		//Dừng ứng dụng thất bại
		else if (result.equals("stopafail")){
			JOptionPane.showMessageDialog(this, "Stop application failed");
		}
		//Gói danh sách các dịch vụ đang chạy
		else if (result.length() > r.tg_getservices.length() && result.substring(0,r.tg_getservices.length()).equals(r.tg_getservices)){
			serv.setService(result.substring(r.tg_getservices.length()+2));		
		}
		//Gói chuỗi bàn phím nhận được
		else if (result.length() > r.tg_print.length() && result.substring(0,r.tg_print.length()).equals(r.tg_print)){
			ks.setKeys(result.substring(r.tg_hook.length()+2));
		}

	}
}
