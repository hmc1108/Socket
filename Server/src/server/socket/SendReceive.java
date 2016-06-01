package server.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SendReceive {
	
	final String tg_shutdown ="shutdown";
	final String tg_getservices = "gets";
	final String tg_stopservices = "stops, ";
	final String tg_startservices = "starts, ";
	final String tg_stopapp = "stopa, ";
	final String tg_startapp = "starta, ";
	final String tg_hook = "hook";
	final String tg_unhook = "unhook";
	final String tg_print = "print";
	
	public String receive(SocketChannel client) throws IOException{
		ByteBuffer crunchifyBuffer = ByteBuffer.allocate(256);
		client.read(crunchifyBuffer);
		String result = new String(crunchifyBuffer.array()).trim();
		return result;
	}
	
	//Gửi thông tin yêu cầu tắt máy
	public void sendShutdown(SocketChannel client){
		byte[] message = new String(tg_shutdown).getBytes();
		ByteBuffer buffer = ByteBuffer.wrap(message);
		try {
			client.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//Gửi thông tin yêu cầu ngắt kết nối
	public void sendClose(SocketChannel client){
		byte[] message = new String("fail").getBytes();
		ByteBuffer buffer = ByteBuffer.wrap(message);
		try {
			client.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//Gửi thông tin yêu cầu lấy dịch vụ
	public void sendGetService(SocketChannel client){
		byte[] message = new String(tg_getservices).getBytes();
		ByteBuffer buffer = ByteBuffer.wrap(message);
		try {
			client.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//Gửi thông tin yêu cầu dừng dịch vụ
	public void sendStopService(SocketChannel client, String servicename){
		byte[] message = new String(tg_stopservices+ servicename).getBytes();
		ByteBuffer buffer = ByteBuffer.wrap(message);
		try {
			client.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//Gửi thông tin yêu cầu chạy dịch vụ
	public void sendStartService(SocketChannel client, String servicename){
		byte[] message = new String(tg_startservices+ servicename).getBytes();
		ByteBuffer buffer = ByteBuffer.wrap(message);
		try {
			client.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//Gửi thông tin yêu cầu bắt đầu ứng dụng
	public void sendStartApp(SocketChannel client, String appname){
		byte[] message = new String(tg_startapp+ appname).getBytes();
		ByteBuffer buffer = ByteBuffer.wrap(message);
		try {
			client.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//Gửi thông tin yêu cầu dừng ứng dụng
	public void sendStopApp(SocketChannel client, String appid){
		byte[] message = new String(tg_stopapp+ appid).getBytes();
		ByteBuffer buffer = ByteBuffer.wrap(message);
		try {
			client.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Gửi thông tin yêu cầu hook
	public void sendHook(SocketChannel client){
		byte[] message = new String(tg_hook).getBytes();
		ByteBuffer buffer = ByteBuffer.wrap(message);
		try {
			client.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Gửi thông tin yêu cầu unhook
	public void sendUnhook(SocketChannel client){
		byte[] message = new String(tg_unhook).getBytes();
		ByteBuffer buffer = ByteBuffer.wrap(message);
		try {
			client.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Gửi thông tin yêu cầu lấy các phím ghi đc
	public void sendGetKeys(SocketChannel client){
		byte[] message = new String(tg_print).getBytes();
		ByteBuffer buffer = ByteBuffer.wrap(message);
		try {
			client.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
