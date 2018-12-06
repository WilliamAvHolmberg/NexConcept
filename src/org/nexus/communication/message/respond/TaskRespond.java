package org.nexus.communication.message.respond;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;
import java.util.function.BooleanSupplier;

import org.nexus.communication.message.NexMessage;
import org.nexus.task.ActionTask;
import org.nexus.task.Task;
import org.nexus.utils.inventory.InventoryItem;
import org.nexus.utils.inventory.RequiredInventory;
import org.nexus.utils.item.RSItem;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.MethodProvider;

public abstract class TaskRespond extends NexMessage {

	protected Task newTask;
	protected long currentTime;
	
	public TaskRespond(MethodProvider methodProvider, Stack<NexMessage> messageQueue, String respond) {
		super(methodProvider, messageQueue, respond);
	}

	public RequiredInventory getInventory(String parsedInventory) {
		RequiredInventory inv = new RequiredInventory();
		for(String parsedInvItem : parsedInventory.split(";")) {
			if(parsedInvItem.length() > 2) {
				String[] moreParsed = parsedInvItem.split(",");
				String itemName = moreParsed[0];
				int itemId = Integer.parseInt(moreParsed[1]);
				int itemAmount = Integer.parseInt(moreParsed[2]);
				int buyAmount = Integer.parseInt(moreParsed[3]);
				methodProvider.log("Buy amount for item: " + itemName + ":::" + buyAmount);
				InventoryItem newItem = new InventoryItem(itemAmount, new RSItem(itemName, itemId), buyAmount);
				inv.addItem(newItem);
			}
		}
		return inv;
	}

	public void setBreakConditions(Task newTask, String parsedBreakCondition, String breakAfter,
			String parsedlevelGoal) {
		if(parsedBreakCondition.toLowerCase().contains("time_or_level")) {
			newTask.setBreakAfterTime(5 + (int)Double.parseDouble(breakAfter));
			((ActionTask)newTask).setWantedLevel((int)Double.parseDouble(parsedlevelGoal));
		}else if(parsedBreakCondition.toLowerCase().contains("time")) {
			newTask.setBreakAfterTime(5 + (int)Double.parseDouble(breakAfter));
		}else if(parsedBreakCondition.toLowerCase().contains("level")) {
			((ActionTask)newTask).setWantedLevel((int)Double.parseDouble(parsedlevelGoal));
		}
		
	}
}
