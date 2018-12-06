package org.nexus.communication.message.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

import org.nexus.Nex;
import org.nexus.communication.message.DisconnectMessage;
import org.nexus.communication.message.NexMessage;
import org.nexus.handler.TaskHandler;
import org.nexus.node.mule.PrepareForMuleDeposit;
import org.nexus.task.mule.DepositToPlayerTask;
import org.nexus.task.mule.PrepareForMuleDepositTask;
import org.nexus.task.mule.WithdrawFromPlayerTask;
import org.nexus.utils.Sleep;
import org.osbot.rs07.script.MethodProvider;

public class MuleRequest extends NexRequest {

	public MuleRequest(MethodProvider methodProvider, Stack<NexMessage> messageQueue, String respond) {
		super(methodProvider, messageQueue, respond);
	}

	@Override
	public void execute(PrintWriter out, BufferedReader in) throws IOException {
		methodProvider.log("new mule requestelele");
		String[] nextRequest = respond.split(":");
		String muleType = nextRequest[0].toLowerCase();
		String itemID = nextRequest[1];
		String amount = nextRequest[2];
		out.println("mule_request:" + itemID + ":" + amount + ":" + methodProvider.myPlayer().getName() + ":"
				+ methodProvider.worlds.getCurrentWorld() + ":" + muleType);
		String respond = in.readLine();
		methodProvider.log("mule respond: " + respond);
		String[] parsedRespond = respond.split(":");
		if (parsedRespond[0].equals("SUCCESSFUL_MULE_REQUEST")) {
			handleSuccessfullMuleRespond(parsedRespond, Integer.parseInt(itemID), Integer.parseInt(amount));
		} else if (parsedRespond[0].equals("MULE_BUSY")){
			methodProvider.log("mule is busy atm. lets sleep for 15sec and see if available");
			Nex.CURRENT_TASK = new PrepareForMuleDepositTask();
			Nex.CURRENT_TASK.exchangeContext(methodProvider.getBot());
			Sleep.sleep(15000);
		}else {
			methodProvider.log("no mule available");
			messageQueue.add(new DisconnectMessage(methodProvider, messageQueue, null));
		}
		methodProvider.log("helelulu??");
	}

	private void handleSuccessfullMuleRespond(String[] parsedRespond, int itemID, int amount) {
		methodProvider.log("successfull!");
		String muleName = parsedRespond[1];
		String world = parsedRespond[2];
		String muleType = parsedRespond[3].toLowerCase();
		int startAmount;
		long currentTime = System.currentTimeMillis();
		switch (muleType) {
		case "mule_deposit":
			startAmount = (int) methodProvider.inventory.getAmount(itemID);
			newTask = new DepositToPlayerTask(Integer.parseInt(world), itemID, amount, (int) startAmount,
					muleName.toLowerCase());

			newTask.setBreakAfterTime(5);
			break;
		case "mule_withdraw":
			startAmount = (int) methodProvider.inventory.getAmount(itemID);
			newTask = new WithdrawFromPlayerTask(Integer.parseInt(world), itemID, amount, (int) startAmount,
					muleName.toLowerCase());
			newTask.setBreakAfterTime(5);
			break;
		}
		if(newTask != null) {
			TaskHandler.addPrioritizedTask(newTask);
		}

	}

}
