package org.nexus.task.quests;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

import org.nexus.Nex;
import org.nexus.handler.bank.WithdrawItemHandler;
import org.nexus.task.QuestTask;
import org.nexus.task.Task;
import org.nexus.task.TaskType;
import org.nexus.task.quests.tutorial.sections.BankSection;
import org.nexus.task.quests.tutorial.sections.CookingSection;
import org.nexus.task.quests.tutorial.sections.FightingSection;
import org.nexus.task.quests.tutorial.sections.MiningSection;
import org.nexus.task.quests.tutorial.sections.PriestSection;
import org.nexus.task.quests.tutorial.sections.QuestSection;
import org.nexus.task.quests.tutorial.sections.RuneScapeGuideSection;
import org.nexus.task.quests.tutorial.sections.SurvivalSection;
import org.nexus.task.quests.tutorial.sections.TutorialSection;
import org.nexus.task.quests.tutorial.sections.WizardSection;
import org.nexus.utils.Sleep;
import org.nexus.utils.inventory.InventoryItem;
import org.nexus.utils.inventory.RequiredInventory;
import org.nexus.utils.item.RSItem;
import org.osbot.rs07.Bot;
import org.osbot.rs07.api.Quests.Quest;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.MethodProvider;

public class CooksAssistantQuest extends QuestTask {

	private final Area cookArea = new Area(3205, 3212, 3212, 3217);
	private final InventoryItem egg = new InventoryItem(1, new RSItem("Egg", 1944), 1);
	private final InventoryItem potOfFlour = new InventoryItem(1, new RSItem("Pot of Flour", 1933), 1);
	private final InventoryItem bucketOfMilk = new InventoryItem(1, new RSItem("Bucket of Milk", 1927), 1);
	public RequiredInventory requiredInventory = new RequiredInventory()
			.setItems(new ArrayList<InventoryItem>(Arrays.asList(egg, potOfFlour, bucketOfMilk)));

	@Override
	public MethodProvider exchangeContext(Bot bot) {
		return super.exchangeContext(bot);
	}

	@Override
	public final int onLoop() throws InterruptedException {
		
		if (getCurrentSection() == 0 && getItemToWithdraw(requiredInventory) != null) {
			WithdrawItemHandler.addItem(getItemToWithdraw(requiredInventory));
		} else if (!cookArea.contains(myPlayer())) {
			walking.webWalk(cookArea);
		} else if (pendingOption()) {
			selectOption("What's wrong?", "I'm always happy to help a cook in distress");
		} else if (pendingContinue()) {
			selectContinue();
		} else {
			talkToNpc("Cook");
		}


		return 200;
	}

	@Override
	public String getLog() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onPaint(Graphics2D g) {
		g.drawString("DOING COOKS ASS. :" + getCurrentSection(), 10, 75);
	}

	@Override
	public TaskType getTaskType() {
		return TaskType.QUEST;
	}

	@Override
	public void removeTask() {
		// TODO queue tutorial island is done. Add RunTime
		TaskHandler.getCurrentTask() = null;

	}

	@Override
	public RequiredInventory getRequiredInventory() {
		return this.requiredInventory;
	}
	
	
	public int getQuestConfig() {
		return 29;
	}


	public Quest getQuest() {
		return Quest.COOKS_ASSISTANT;
	}

	public static Quest getThisQuest() {
		return Quest.COOKS_ASSISTANT;
	}
	



}
