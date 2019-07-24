package com.actolap.wse.test;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;

public class GraphiteLogger {

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String host = "monitor.wsegames.in";
		try (Socket socket = new Socket(host, 2003);
				Writer writer = new OutputStreamWriter(socket.getOutputStream());) {

			Long timestamp = System.currentTimeMillis() / 1000;
			System.out.println(timestamp);

			String sentMessage = "graphite.carbon.local.test.showone 1 " + timestamp;
			System.out.println(sentMessage);
			writer.write(sentMessage);
			writer.flush();

		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}

