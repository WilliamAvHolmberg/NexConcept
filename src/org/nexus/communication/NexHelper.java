package org.nexus.communication;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Scanner;
import java.util.Stack;
import java.util.function.BooleanSupplier;

import org.nexus.Nex;
import org.nexus.communication.message.BannedMessage;
import org.nexus.communication.message.DisconnectMessage;
import org.nexus.communication.message.NexMessage;
import org.nexus.communication.message.TaskLog;
import org.nexus.communication.message.request.MuleRequest;
import org.nexus.communication.message.request.RequestTask;
import org.nexus.communication.message.respond.BreakRespond;
import org.nexus.communication.message.respond.MuleRespond;
import org.nexus.communication.message.respond.WoodcuttingRespond;
import org.nexus.task.Task;
import org.osbot.rs07.api.Client;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.MethodProvider;

public class NexHelper extends MethodProvider implements Runnable {
	// private String ip = "192.168.10.127";
	private String ip = "oxnetserver.ddns.net";
	//private String ip = "nexus.no-ip.org";
	private int port = 43594;
	public static long lastLog = System.currentTimeMillis();

	private static Stack<NexMessage> messageQueue;
	private String respond = "none";

	private NexMessage nextRequest;
	private String username;
	private String password;
	private String log;
	private String name;
	public boolean initialized = false;

	public static Stack<NexMessage> getQueue() {
		return messageQueue;
	}
	
	public static void pushMessage(NexMessage message) {
		boolean add = true;
		for(NexMessage mess : messageQueue) {
			if(mess.getClass() == message.getClass()) {
				add = false;
				return;
			}
		}
		if(add) {
			messageQueue.push(message);
		}
		
	}
	public NexHelper(String username, String password, Task currentTask) {
		this.username = username;
		this.password = password;
		this.name = username.split("@")[0];
		messageQueue = new Stack<NexMessage>();
	}

	@Override
	public void run() {
		log("started NexHelper");
		try {
			Socket socket = new Socket(ip, port);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			log("lets init");
			for(int i = 0; i<15; i++) {
				if(isLoggedIn()) {
					break;
				}
				Thread.sleep(1000);
			}
			initializeContactToSocket(out, in);
			initialized = true;
			whileShouldRun(out, in); // main loop, always run while script should be running

		} catch (Exception e) {
			log("Fail");
			log(e);
			messageQueue.add(new DisconnectMessage(this, messageQueue, "Failed to initialize"));
		}

	}
	
	private boolean isLoggedIn() {
		return getClient().getLoginState().equals(Client.LoginState.LOGGED_IN);
	}

	private void whileShouldRun(PrintWriter out, BufferedReader in) throws IOException, InterruptedException {
		while (Nex.SHOULD_RUN) {
			logToServer(out, in);
			checkIfBanned(out, in);
			if (!messageQueue.isEmpty()) {
				log("message queue not empty");
				handleMessageQueue(out, in);
			}
			Thread.sleep(1000);
		}

	}

	private void handleMessageQueue(PrintWriter out, BufferedReader in) throws InterruptedException, IOException {
		nextRequest = messageQueue.pop();
		if (nextRequest != null) {
			log(nextRequest);
			nextRequest.execute(out, in);
		} else {
			logToServer(out, in);
		}

	}

	/*
	 * Initialize contact towards socket if connection fails, stop script
	 */
	private void initializeContactToSocket(PrintWriter out, BufferedReader in) throws IOException {
		log("bla");
		out.println("script:1:" + getIP() + ":" + username + ":" + password + ":"
				+ getName() + ":" + worlds.getCurrentWorld());
		respond = in.readLine();
		if (respond.equals("connected:1")) {
			log("NexHelper has been initialized towards Nexus");
		} else {
			log("Connection Towards Nexus failed");
			messageQueue.push(new DisconnectMessage(this, null, "failed to initialize contact"));
		}
	}
	
	private String getName() {
		if(myPlayer() != null && myPlayer().getName() != null) {
			return myPlayer().getName();
		}
		return name;
	}

	private String getLog() {
		/**
		 * If task is null, return log:0
		 * 
		 * if task is not null and xp per hour > 100 ++ log:1 + xpPerHour if task is not
		 * null and money per hour > 100 ++ moneyPerHour if player is logged in and
		 * position not null, += position coordinates
		 */

		if (Nex.CURRENT_TASK == null || Nex.CURRENT_TASK.getLog() == null) {
			return "log:0";
		}
		String respond = "task_log:1:" + Nex.CURRENT_TASK.getLog();
		return respond;
	}

	/*
	 * Method to take care of every log
	 */
	private void logToServer(PrintWriter out, BufferedReader in) throws InterruptedException, IOException {
		if (System.currentTimeMillis() - lastLog > 5000) { // only log every 5 sec
			log = getLog();
			out.println(log);
			respond = in.readLine();
			log(respond);
			lastLog = System.currentTimeMillis();
		}

	}

	public void getNewTask() {
		pushMessage(new RequestTask(this, messageQueue, null));
	}

	private void checkIfBanned(PrintWriter out, BufferedReader in) throws IOException {
		if (!client.isLoggedIn() && isDisabledMessageVisible()) {
			messageQueue.push(new BannedMessage(this, null, "Player is banned"));
		}
	}

	public boolean isDisabledMessageVisible() {
		return getColorPicker().isColorAt(483, 192, new Color(255, 255, 0));
	}

	public static String getIP() {
		URL url;
		try {
			url = new URL("http://checkip.amazonaws.com/");
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			return br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "not_found";

	}
	
	public long secondsSinceLastLog() {
		return (System.currentTimeMillis() - NexHelper.lastLog)/1000;
	}

}
