package server.socket;

import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class Service{

	JDialog service;
	MainFrame parent;
	final String title = "Service...";
	List li;
	
	public Service(MainFrame owner) {
		if (owner == null){
			JFrame emptyWin = new JFrame();
			service = new JDialog(emptyWin, title, true);
		}
		else{
			service = new JDialog(owner,title, true);
			parent=owner;
		}
		
		service.addWindowListener( 
	              new java.awt.event.WindowAdapter() {
	                public void windowClosing( java.awt.event.WindowEvent e ) {
	                  service.setVisible(false);
	                }
	                }
	            );
		
		//Tạo giao diện
		service.setLayout(null);
		JButton getButton = new JButton("Get");
		getButton.setBounds(30, 20, 80, 30);
		JButton clearButton = new JButton("Clear");
		clearButton.setBounds(120, 20, 80, 30);
		JButton startButton = new JButton("Start");
		startButton.setBounds(210, 20, 80, 30);
		JButton stopButton = new JButton("Stop");
		stopButton.setBounds(300, 20, 80, 30);
		JLabel servicesName = new JLabel("Name of services:");
		servicesName.setBounds(30, 70, 220, 30);
		li = new List();
		li.setBounds(30, 110, 350, 220);
		
		
	    service.add(getButton);
	    service.add(clearButton);
	    service.add(startButton);
	    service.add(stopButton);
	    service.add(servicesName);
	    service.add(li);
	    service.pack();
		service.setSize(430, 400);
		service.setLocation(150, 100);
		SendReceive srvc = new SendReceive();
		
		//Gửi thông điệp y/c d/s dịch vụ đang chạy
		getButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent evt){
						//Gửi tín hiệu
						li.removeAll();
						srvc.sendGetService(parent.client);
					}
				});
		
		//Xóa danh sách tạm thời
		clearButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent evt){
						li.removeAll();
						service.add(li);
					}
				});
		
		//Chạy 1 dịch vụ
		startButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent evt){
						String name = JOptionPane.showInputDialog(null, "Name of service: ");
						if (name!=null)
							srvc.sendStartService(parent.client, name);
					}
				});
		
		//Dừng 1 dịch vụ
		stopButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent evt){
						String a = li.getSelectedItem();
						if (a!= null) 
							srvc.sendStopService(parent.client, a);
					}
				});
		
	}
	
	public void show(){
		service.setVisible(true);
	}
	
	//Thiết lập danh sách các dịch vụ
	public void setService(String s){
		//Tách chuỗi
		String[]arr = s.split(",");
		
		for(int i =0; i<arr.length; i++){
			li.add(arr[i].replaceAll("(^ )|( $)", ""));
		}
		service.add(li);
	}
}
