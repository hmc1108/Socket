package client.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class RemotePC {
	
	//Thực thi lệnh tắt máy trong 10s
	public void shutdown() throws IOException{
		Runtime runtime = Runtime.getRuntime();
		Process proc1 = runtime.exec("shutdown -s -t 10");
	}
	
	//Đóng gói và gửi danh sách các dịch vụ đang chạy
	public void sendServices(SocketChannel socket){
		String os = System.getProperty("os.name").toLowerCase();
		SendReceive s = new SendReceive();
		if(os.startsWith("windows")){
			try{
				Process p = Runtime.getRuntime().exec("sc query");
				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String text;
				in.readLine();
				in.readLine();
				while ((text=in.readLine())!=null){
						if(text.contains("SERVICE_NAME:")){
							s.send(socket, "gets, " + text.substring(14));
							Thread.sleep(100);	//Các gói tin cách nhau 1/10 giây
						}
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}

	}
	
	//Dừng dịch vụ đang chạy
	public boolean stopServices(String name){
		String os = System.getProperty("os.name").toLowerCase();
		if(os.startsWith("windows")){
			try{
				Process p = Runtime.getRuntime().exec("sc stop " + name);
				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String text;
				in.readLine();
				in.readLine();
				while ((text=in.readLine())!=null){
						//Kiểm tra xem đã dừng đc chưa
						if (text.contains("STOP_PENDING"))
							return true;
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return false;
	}
	
	//Chạy 1 dịch vụ
	public boolean startServices(String name){
		String os = System.getProperty("os.name").toLowerCase();
		if(os.startsWith("windows")){
			try{
				Process p = Runtime.getRuntime().exec("sc start " + name);
				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String text;
				in.readLine();
				in.readLine();
				while ((text=in.readLine())!=null){
					//Kiểm tra xem đã chạy được chưa
						if (text.contains("START_PENDING"))
							return true;
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return false;
	}
	
	//Dừng 1 ứng dụng
	public boolean stopApp(String id){
		String os = System.getProperty("os.name").toLowerCase();
		if(os.startsWith("windows")){
			String s;
			//Cấu trúc lệnh thực thi khác nhau với PID hay name
			if (isInteger(id))
				s = "taskkill /f /pid " + id;
			else
				s = "taskkill /f /im "+id;
			try{
				Runtime.getRuntime().exec(s);
				
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return !checkApp(id);	//Kiểm tra dừng đc chưa
	}
	
	//Chạy 1 ứng dụng
	public boolean startApp(String name){
		String os = System.getProperty("os.name").toLowerCase();
		if(os.startsWith("windows")){
			try{
				Runtime.getRuntime().exec(name);
				
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return checkApp(name);	//Kiểm tra chạy đc chưa
	}
	
	//Kiểm tra ứng dụng có đang chạy ko
	public boolean checkApp(String name){
		if (!isInteger(name) && !name.contains(".exe"))
			name = name + ".exe";
		String os = System.getProperty("os.name").toLowerCase();
		if(os.startsWith("windows")){
			try{
				Thread.sleep(1000);	//Dừng để đợi ứng dụng được bật và có trong tasklist
				Process p = Runtime.getRuntime().exec("tasklist");
				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String text;
				in.readLine();
				in.readLine();
				System.out.println(name);
				while ((text=in.readLine())!=null){
						if (text.contains(name)){
							System.out.println(text);
							return true;
						}
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return false;
	}
	
	//Kiểm tra 1 chuỗi có phải là số nguyên ko
	public static boolean isInteger(String s) {
		try
		{
			Integer.parseInt(s);
			// s is a valid integer
			return true;
		}
		catch (NumberFormatException ex)
		{
			return false;
		}
	}
}
