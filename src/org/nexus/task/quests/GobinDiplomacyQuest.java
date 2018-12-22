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

public class GobinDiplomacyQuest extends QuestTask {

	private final Position testPos = new Position(2957, 3511, 0);

	private InventoryItem goblinMail = new InventoryItem(3, new RSItem("Goblin mail", 288), 3);
	private InventoryItem blueDye = new InventoryItem(1, new RSItem("Blue dye", 1767), 1);
	private InventoryItem orangeDye = new InventoryItem(1, new RSItem("Orange dye", 1769), 1);

	private InventoryItem orangeGoblinMail = new InventoryItem(1, new RSItem("Orange goblin mail", 286), 1);
	private InventoryItem blueGoblinMail = new InventoryItem(1, new RSItem("Blue goblin mail", 287), 1);

	@Override
	public MethodProvider exchangeContext(Bot bot) {
		return super.exchangeContext(bot);
	}

	@Override
	public final int onLoop() throws InterruptedException {

		switch (getCurrentSection()) {
		case 0:
			walkAndTalkTo(testPos, "General bentnoze",
					new RequiredInventory().addItem(goblinMail).addItem(blueDye).addItem(orangeDye),
					"Do you want me to pick an armour colour for you?", "What about a different colour?",
					"No, he doesn't look fat");
			break;
		case 3: // orange
			if (createItem(orangeGoblinMail, orangeDye, goblinMail.setAmount(3))) {
				walkAndTalkTo(testPos, "General bentnoze", "I have some orange armour here", "No, he doesn't look fat");
			}
			break;
		case 4: // blue
			if (createItem(blueGoblinMail, blueDye, goblinMail.setAmount(2))) {
				walkAndTalkTo(testPos, "General bentnoze", "I have some blue armour here", "No, he doesn't look fat");
			}
			return 600;
		case 5:
			if (myPosition().getX() > 8000) { // cutscene
				if (pendingContinue()) {
					selectContinue();
				}
			} else if (inventory.contains("Goblin mail")) {
				walkAndTalkTo(testPos, "General bentnoze", "I have some brown armour here", "No, he doesn't look fat");
			}
			break;
		}

		return 600;
	}

	public int getQuestConfig() {
		return 62;
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
		TaskHandler.getCurrentTask() = null;

	}

	@Override
	public RequiredInventory getRequiredInventory() {
		return this.requiredInventory;
	}

	public Quest getQuest() {
		return Quest.GOBLIN_DIPLOMACY;
	}

	public static Quest getThisQuest() {
		return Quest.GOBLIN_DIPLOMACY;
	}

}
