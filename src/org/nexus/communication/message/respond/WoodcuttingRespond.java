package org.nexus.communication.message.respond;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;
import java.util.function.BooleanSupplier;

import org.nexus.Nex;
import org.nexus.communication.message.NexMessage;
import org.nexus.task.woodcutting.WoodcuttingTask;
import org.nexus.utils.WebBank;
import org.nexus.utils.item.RSItem;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.MethodProvider;

public class WoodcuttingRespond extends TaskRespond {

	public WoodcuttingRespond(MethodProvider methodProvider, Stack<NexMessage> messageQueue, String respond) {
		super(methodProvider, messageQueue, respond);
		methodProvider.log("wc res is created. is res null?: " + respond == null);
		
	}

	@Override
	public void execute(PrintWriter out, BufferedReader in) throws IOException {
		if(respond != null) {
		methodProvider.log(respond);
		}else {
			methodProvider.log("respond is null");
		}
		String[] parsed = respond.split(":");
		String currentTaskID = parsed[3];
		String parsedBankArea = parsed[4];
		String parsedActionArea = parsed[5];
		String parsedAxeID = parsed[6];
		String axeName = parsed[7];
		String treeName = parsed[8];
		String parsedBreakCondition = parsed[9];
		String breakAfter = parsed[10];
		String parsedLevelGoal = parsed[11];
		
		Area actionArea = WebBank.parseCoordinates(parsedActionArea);
		Area bankArea = null;
		if (!parsedBankArea.equals("none")) {
			bankArea = WebBank.parseCoordinates(parsedBankArea);
		}
		
		int axeID = Integer.parseInt(parsedAxeID);
		RSItem axe = new RSItem(axeName, axeID);
		
		newTask = new WoodcuttingTask(actionArea, bankArea, axe, treeName);
		newTask.setTaskID(currentTaskID);
		setBreakConditions(newTask, parsedBreakCondition, breakAfter, parsedLevelGoal);
		TaskHandler.getCurrentTask() = (newTask);
	}
}
