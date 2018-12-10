package org.nexus.communication.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

import org.nexus.task.Task;
import org.osbot.rs07.script.MethodProvider;

public class CustomLog extends NexMessage {


	public CustomLog(MethodProvider methodProvider, Stack<NexMessage> messageQueue, String respond) {
		super(methodProvider, messageQueue, respond);
	}

	@Override
	public void execute(PrintWriter out, BufferedReader in) throws IOException {
		out.println(respond);
		in.readLine(); //will always return ok. Therefor nothing shall be done.
		
	}
}
