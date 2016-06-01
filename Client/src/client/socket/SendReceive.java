package client.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SendReceive {
	
	//Nhận chuỗi từ server
	public String receive(SocketChannel server) throws IOException{
		ByteBuffer crunchifyBuffer = ByteBuffer.allocate(256);
		server.read(crunchifyBuffer);
		String result = new String(crunchifyBuffer.array()).trim();
		return result; 
	}
	
	//Đóng gói và gửi chuỗi qua server
	public void send(SocketChannel client, String msg){
		byte[] message = new String(msg).getBytes();
		ByteBuffer buffer = ByteBuffer.wrap(message);
		try {
			client.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
