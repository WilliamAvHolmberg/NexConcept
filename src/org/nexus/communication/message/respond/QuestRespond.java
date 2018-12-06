package org.nexus.communication.message.respond;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;
import java.util.function.BooleanSupplier;

import org.nexus.Nex;
import org.nexus.communication.message.NexMessage;
import org.nexus.task.quests.CooksAssistantQuest;
import org.nexus.task.quests.GobinDiplomacyQuest;
import org.nexus.task.quests.RomeoAndJulietQuest;
import org.nexus.task.woodcutting.WoodcuttingTask;
import org.nexus.utils.WebBank;
import org.nexus.utils.item.RSItem;
import org.osbot.rs07.api.Quests;
import org.osbot.rs07.api.Quests.Quest;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.MethodProvider;

public class QuestRespond extends TaskRespond {

	public QuestRespond(MethodProvider methodProvider, Stack<NexMessage> messageQueue, String respond) {
		super(methodProvider, messageQueue, respond);
		methodProvider.log("wc res is created. is res null?: " + respond == null);

	}

	@Override
	public void execute(PrintWriter out, BufferedReader in) throws IOException {
		if (respond != null) {
			methodProvider.log(respond);
		} else {
			methodProvider.log("respond is null");
		}
		String[] parsed = respond.split(":");
		String currentTaskID = parsed[3];
		String questName = parsed[4];
		if (questName.contains(Quest.GOBLIN_DIPLOMACY.toString())) {
			newTask = new GobinDiplomacyQuest();
		} else if (questName.contains(Quest.ROMEO_JULIET.toString())) {
			newTask = new RomeoAndJulietQuest();
		} else if (questName.contains(Quest.COOKS_ASSISTANT.toString())) {
			newTask = new CooksAssistantQuest();
		} else {
			methodProvider.log("quest:" + questName + "not found");
		}
		if (newTask != null) {
			newTask.setTaskID(currentTaskID);

			Nex.CURRENT_TASK = (newTask);
		}
	}
}
