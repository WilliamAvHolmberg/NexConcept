package org.nexus.task;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import org.nexus.handler.bank.WithdrawItemHandler;
import org.nexus.utils.Sleep;
import org.nexus.utils.inventory.InventoryItem;
import org.nexus.utils.inventory.RequiredInventory;
import org.osbot.rs07.api.Inventory;
import org.osbot.rs07.api.Quests.Quest;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.RS2Widget;

public abstract class QuestTask extends Task{



	public abstract Quest getQuest();
	public abstract int getQuestConfig();
	
	protected void removeItem(InventoryItem item) {
		if(requiredInventory.getItems().contains(item)) {
			requiredInventory.getItems().remove(item);
		}	
	}
	
	protected boolean walkTo(Area area) {
		return walking.webWalk(area);
	}
	
	protected boolean walkTo(Position position) {
		return walking.webWalk(position);
	}
	
	protected boolean isOnCutscene(){
        return getConfigs().get(1021) == 192 && getMap().isMinimapLocked();
	}
	

	protected void talkToNpc(String name) {
		if (getNpc(name) != null && getNpc(name).interact("Talk-to")) {
			Sleep.sleepUntil(this::pendingContinue, 5000, 600);
		}
	}
	
	public void walkAndTalkTo(Position position,String name) {
		if(pendingContinue()) {
			selectContinue();
		}else if(getNpc(name) == null) {
			walkTo(position);
		}else {
			talkToNpc(name);
		}
		
	}
	
	public void walkAndTalkTo(Position position,String name, String... options) {
		if(pendingContinue()) {
			selectContinue();
		}else if(getNpc(name) == null) {
			walkTo(position);
		}else if(pendingOption()) {
			log("pending op");
			selectOption(options);
		}else {
			talkToNpc(name);
		}
		
	}
	
	public void walkAndTalkTo(Position position,String name, RequiredInventory requiredInventory, String... options) {
		if(getItemToWithdraw(requiredInventory) != null) {
			WithdrawItemHandler.addItem(getItemToWithdraw(requiredInventory));
		}else if(getNpc(name) == null) {
			walkTo(position);
		}else if(pendingContinue()) {
			selectContinue();
		}else if(pendingOption()) {
			log("pending op");
			selectOption(options);
		}else {
			talkToNpc(name);
		}
		
	}
	
	protected boolean createItem(InventoryItem finalItem, InventoryItem subItem1, InventoryItem subItem2) {
		RequiredInventory reqInv = new RequiredInventory().addItem(subItem1).addItem(subItem2);
		if(inventory.contains(finalItem.getItem().getName())) {
			return true;
		}
		if(getItemToWithdraw(reqInv) != null) {
			WithdrawItemHandler.addItem(getItemToWithdraw(reqInv));
			return false;
		}
		if(inventory.contains(subItem1.getItem().getName()) && inventory.contains(subItem2.getItem().getName())) {
			if(inventory.isItemSelected()) {
				inventory.interact("Use", subItem1.getItem().getName());
			}else {
				inventory.interact("Use", subItem2.getItem().getName());
			}
		}
		Sleep.sleep(1000);
		return false;
	}
	
	protected boolean selectOption(String... options) {
		return 	getDialogues().selectOption(options);
	}

	protected NPC getNpc(String name) {
		if(getNpcs().closest(name) != null && !getMap().canReach(getNpcs().closest(name))) {
			return null;
		}
		return getNpcs().closest(name);
	}

	protected boolean pendingContinue() {
		RS2Widget continueWidget = getContinueWidget();
		return continueWidget != null && continueWidget.isVisible();
	}
	
	protected RS2Widget getContinueWidget() {
		return getWidgets().singleFilter(getWidgets().getAll(),
				widget -> widget.isVisible() && (widget.getMessage().contains("Click here to continue")
						|| widget.getMessage().contains("Click to continue")));
	}
	
	protected boolean pendingOption() {
		return getDialogues().isPendingOption();
	}
	
	protected boolean inArea(Area area) {
		return area.contains(myPlayer());
	}
	
	protected RS2Widget getOptionWidget(String... options) {
		return getWidgets().singleFilter(getWidgets().getAll(),
				widget -> widget.isVisible() && Arrays.asList(options).contains(widget.getMessage()));
	}

	protected boolean selectContinue() {
		RS2Widget continueWidget = getContinueWidget();
		if (continueWidget == null) {
			return false;
		}
		if (continueWidget.getMessage().contains("Click here to continue")) {
			getKeyboard().pressKey(KeyEvent.VK_SPACE);
			Sleep.sleepUntil(() -> !continueWidget.isVisible(), 1000, 600);
			return true;
		} else if (continueWidget.interact()) {
			Sleep.sleepUntil(() -> !continueWidget.isVisible(), 1000, 600);
			return true;
		}
		return false;
	}


	
	protected InventoryItem getItemToWithdraw(RequiredInventory inventory) {
		for (InventoryItem item : inventory.getItems()) {
			if (getInventory().getAmount(item.getItem().getId()) < item.getAmount()) {
				return item;
			}
		}
		return null;
	}


	protected int getCurrentSection() {
		return getConfigs().get(getQuestConfig());
	}
	public boolean isFinished() {
		return getQuests().isComplete(getQuest());
	}
	@Override
	public TaskType getTaskType() {
		return TaskType.QUEST;
	}
	
	

}
