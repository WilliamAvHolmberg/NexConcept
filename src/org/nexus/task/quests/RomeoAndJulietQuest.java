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
import org.osbot.rs07.api.Inventory;
import org.osbot.rs07.api.Quests.Quest;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.MethodProvider;

public class RomeoAndJulietQuest extends QuestTask {

	private final Position julietPosition = new Position(3158,3425,1);
	private final Position romeoPosition = new Position(3213,3423,0);

	private InventoryItem cadavaBerries = new InventoryItem(1, new RSItem("Cadava berries", 753), 1);
	private InventoryItem cadavaPotion = new InventoryItem(1, new RSItem("Cadava potion", 756), 1);

	public RequiredInventory requiredInventory = new RequiredInventory()
			.setItems(new ArrayList<InventoryItem>(Arrays.asList(cadavaBerries)));

	@Override
	public MethodProvider exchangeContext(Bot bot) {
		return super.exchangeContext(bot);
	}

	@Override
	public final int onLoop() throws InterruptedException {

		switch (getCurrentSection()) {
		case 0:
			walkAndTalkTo(julietPosition, "Juliet", new RequiredInventory().addItem(cadavaBerries),
					"I guess I could look for him for you.");
			break;
		case 20:
			walkAndTalkTo(romeoPosition, "Romeo", "I guess I could look for him for you.");
			break;
		case 30:
			if (getCamera().getYawAngle() == 191) {
				Sleep.sleepUntil(()-> getCamera().getYawAngle() != 191, 30000);
				Sleep.sleep(1000);
			}
			
			walkAndTalkTo(new Position(3255, 3481, 0), "Father Lawrence");
			break;
		case 40:
			walkAndTalkTo(new Position(3195, 3404, 0), "Apothecary", "Talk about something else.", "Talk about Romeo & Juliet.", "Ok, thanks.");
			break;
		case 50:
			if (!inventory.contains("Cadava potion")) {
				walkAndTalkTo(new Position(3195, 3404, 0), "Apothecary", new RequiredInventory().addItem(cadavaBerries), "Talk about something else.", "Talk about Romeo & Juliet.","Ok, thanks.");
			}else {
				walkAndTalkTo(julietPosition, "Juliet", new RequiredInventory().addItem(cadavaPotion));
				return 2500;
			}
			break;
		case 60:
			if(myPosition().getX() > 8000) { //cutscene
				if(pendingContinue()) {
					selectContinue();
				}
			}else {
			walkAndTalkTo(romeoPosition, "Romeo");
			}
		}

		return 1000;
	}

	public int getQuestConfig() {
		return 144;
	}

	@Override
	public String getLog() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onPaint(Graphics2D g) {
		g.drawString("DOING QUEST: " + getQuest() + ":" + getCurrentSection(), 10, 75);
	}

	@Override
	public TaskType getTaskType() {
		return TaskType.QUEST;
	}

	@Override
	public void removeTask() {
		// TODO queue tutorial island is done. Add RunTime
		Nex.CURRENT_TASK = null;

	}

	@Override
	public RequiredInventory getRequiredInventory() {
		return this.requiredInventory;
	}

	public Quest getQuest() {
		return Quest.ROMEO_JULIET;
	}

	public static Quest getThisQuest() {
		return Quest.ROMEO_JULIET;
	}

}
