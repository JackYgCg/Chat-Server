﻿package cn.chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * 聊天室服务端
 * 
 * @author soft01
 */
public class Client extends JFrame {
	/*
	 * java.net.Socket 套接字 Socket封装了TCP通讯协议,使用它可以基于 TCP协议与远程计算机通讯
	 */
	private Socket socket;

	private static final long serialVersionUID = 1L;
	private JPanel panelTool;
	private JButton btnSend;
	private JTextField txtInput;
	private JTextArea txtaShow;
	// 滚动条
	private JScrollPane srlp;

	/*
	 * 客户端的构造方法用来初始化客户端
	 */
	public Client() throws Exception {
		super("聊天窗口");
		try {
			/*
			 * 实例化Socket时,构造方法要求传入 两个参数: 1:String,指定服务器端的IP地址 2:int,指定服务端打开的服务端端口
			 * 
			 * 通过IP地址可以找到服务器端所在计算机 通过端口号可以找到服务器上运行的服务端应用程序
			 * 
			 */
			System.out.println("正在连接服务端...");
			socket = new Socket("localhost", 8088);
			// socket = new Socket("176.135.1.135",8088);
			System.out.println("与服务端建立连接..");

			txtaShow = new JTextArea();
			srlp = new JScrollPane(txtaShow);
			this.add(srlp, BorderLayout.CENTER);
			panelTool = new JPanel();
			txtInput = new JTextField(10);
			panelTool.add(txtInput);
			btnSend = new JButton("发送");
			panelTool.add(btnSend);
			this.add(panelTool, BorderLayout.SOUTH);
			this.setVisible(true);
			setLocation(350, 150);
			setBackground(Color.CYAN);
			setSize(300, 500);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} catch (Exception e) {
			// 记录日志
			throw e;
		}
	}

	/*
	 * 启动客户端的方法
	 */
	public void start() {
		try {
			/*
			 * Socket提供了方法 OutputStream getOutputStream() 该方法可以获取一个输出流,通过该 输出流写出的数据会发送给远端,这里
			 * 远端就是服务端
			 */
			OutputStream os = socket.getOutputStream();
			OutputStreamWriter ow = new OutputStreamWriter(os);
			PrintWriter pw = new PrintWriter(ow, true);

			/*
			 * 启动用于接收服务端发送过来消息的线程
			 */
			SeverHandler sh = new SeverHandler();
			Thread b = new Thread(sh);
			b.start();

			String a = null;
			final long time = System.currentTimeMillis() - 1000;
		
			// 按钮事件触发后添加到文本
			btnSend.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (System.currentTimeMillis() - time < 1000) {
						System.out.println("说话太快");
					}else {
						System.out.println("发送了一条消息");
						pw.println(txtInput.getText());
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("输出失败");
		}
	}

	public static void main(String[] args) {
		try {
			// 实例化客户端
			Client client = new Client();
			// 启动客户端
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("客户端启动失败");
		}

	}

	/**
	 * 该线程用于拿取服务端的输入流(拿取服务端的数据)
	 */
	private class SeverHandler implements Runnable {

		private BufferedReader br = null;
		private InputStreamReader isr = null;
		private InputStream in = null;

		public void run() {
			try {
				in = socket.getInputStream();
				isr = new InputStreamReader(in, "utf-8");
				br = new BufferedReader(isr);
				String fet = null;
				while ((fet = br.readLine()) != null) {
					// 按钮事件触发后添加到文本
					txtaShow.append(fet + "\r\n");
				}
			} catch (Exception e) {
			} finally {
				try {
					br.close();
					isr.close();
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
