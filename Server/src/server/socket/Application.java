package server.socket;


import java.awt.Frame;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Application {
	JDialog app;
	MainFrame parent;
	final String title = "Application...";
	
	public Application(MainFrame owner) {
		if (owner == null){
			Frame emptyWin = new Frame();
			app = new JDialog(emptyWin, title, true);
		}
		else{
			app = new JDialog(owner,title, true);
			parent=owner;
		}
		app.addWindowListener( 
	              new java.awt.event.WindowAdapter() {
	                public void windowClosing( java.awt.event.WindowEvent e ) {
	                  app.setVisible(false);
	                }
	                }
	            );
		
		//Tạo giao diện
		app.setLayout(null);
		JButton startButton = new JButton("Start");
		startButton.setBounds(30, 20, 80, 30);
		JButton stopButton = new JButton("Stop");
		stopButton.setBounds(120, 20, 80, 30);		
		
	    app.add(startButton);
	    app.add(stopButton);
	    app.pack();
		app.setSize(250, 100);
		app.setLocation(300, 100);
		
		SendReceive srvc = new SendReceive();
		
		//Yêu cầu nhập tên app và gửi gói tin y/c chạy app
		startButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent evt){
						String name = JOptionPane.showInputDialog(null, "Name of app to start: ");
						if (name!=null)
							srvc.sendStartApp(parent.client, name);
					}
				});
		
		//Yêu cầu nhập tên hoặc PID của app và gửi gói tin đóng ứng dụng
		stopButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent evt){
						String id = JOptionPane.showInputDialog(null, "Name or PID of app to stop: ");
						if (id!=null)
							srvc.sendStopApp(parent.client, id);
					}
				});
		
	}
	
	public void show(){
		app.setVisible(true);
	}
}
