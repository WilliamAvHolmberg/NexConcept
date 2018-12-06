 package org.nexus.handler.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.nexus.handler.Handler;
import org.nexus.handler.bank.BankHandler;
import org.nexus.handler.bank.DepositItemHandler;
import org.nexus.handler.bank.WithdrawItemHandler;
import org.nexus.handler.gear.Gear;
import org.nexus.handler.gear.GearHandler;
import org.nexus.handler.gear.GearItem;
import org.nexus.node.Node;
import org.nexus.node.combat.Eat;
import org.nexus.node.combat.Fight;
import org.nexus.node.combat.LootNode;
import org.nexus.task.CombatTask;
import org.nexus.utils.WebBank;
import org.nexus.utils.inventory.InventoryItem;
import org.nexus.utils.inventory.RequiredInventory;
import org.nexus.utils.item.DepositItem;
import org.nexus.utils.item.WithdrawItem;
import org.nexus.utils.item.DepositItem.DepositType;
import org.osbot.rs07.api.Inventory;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.api.ui.Skill;

public class CombatHandler extends Handler {

	public static Fight fightNode = new Fight();
	public static Eat eatNode = new Eat();
	public static LootNode lootNode = new LootNode();
	List<String> itemsToWithdraw = new ArrayList<String>();
	List<Integer> itemsToKeep = new ArrayList<Integer>();

	public CombatHandler() {
		fightNode = new Fight();
		eatNode = new Eat();
		lootNode = new LootNode();
	}

	public int onLoop() {
		// TODO, WALK TO BANK: FIX INVENTORY:: ETC :: NOW LETS PLAY A FIFA GAME
		log("lets get combat node");
		CombatTask combatTask = (CombatTask) getCurrentTask();
		GearItem itemToEquip = getGearItemToEquip(combatTask.getPreferredGear());
		if (itemToEquip != null && inventory.isFull()) {
			log("inventory");
			DepositItemHandler.addItem(new DepositItem(DepositType.DEPOSIT_ALL_EXCEPT, itemToEquip.getItem().getId()));
		}else if(itemToEquip != null) {
			log("equip");
			GearHandler.addItem(itemToEquip);
			return 0;
		}else if (!playerInActionArea()) {
			return handleOutOfActionArea(combatTask);
		}else if (inventory.isFull() && !inventory.contains(combatTask.getFood().getId())) { //TODO, maybe add method, getRequiredInventory at bank
			log("eat");
			DepositItemHandler.addItem(new DepositItem(DepositItem.DepositType.DEPOSIT_ALL_EXCEPT, combatTask.getRequiredInventory().getItemIds()));
		}else if (Eat.shallEat(this)) {
			return handleShallEat(combatTask);
		} else if (LootNode.valueableDropAvailable(this, combatTask.getLootThreshold(), combatTask.getActionArea()) != null){
			log("loot");
			return handleLoot(combatTask);
			
		} else if (!myPlayer().isAnimating()) {
			log("lets send combat task");
			return fightNode.setCombatTask(combatTask);
		}
		return 0;
	}

	private int handleLoot(CombatTask combatTask) {
		GroundItem item = LootNode.valueableDropAvailable(this, combatTask.getLootThreshold(),combatTask.getActionArea());
		if(inventory.isFull() && inventory.contains(combatTask.getFood().getId())) {
			return eatNode.setFood(combatTask.getFood());
		}else if (!inventory.isFull() || (item.getDefinition().getNotedId() == -1 && inventory.contains(item.getId()))) {
			return lootNode.setItemAndArea(item, combatTask.getActionArea());
		} else {

		}
		return 0;
	}

	private int handleOutOfActionArea(CombatTask combatTask) {
		InventoryItem itemToWithdraw = getItemToWithdraw(combatTask.getRequiredInventory());
		 if (shouldDepositUnnecessaryItems(combatTask)) {
			itemsToKeep = new ArrayList<Integer>();
			for (InventoryItem item : combatTask.getRequiredInventory().getItems()) {
				itemsToKeep.add(item.getItem().getId());
			}
			DepositItemHandler.addItem(new DepositItem(DepositItem.DepositType.DEPOSIT_ALL_EXCEPT, itemsToKeep));
			return 0;
		}else if (Eat.getCurrentHealth(this) != skills.getStatic(Skill.HITPOINTS) && inventory.contains(combatTask.getFood().getId())) {
			return handleShallEat(combatTask);
		}  else if (itemToWithdraw != null) {
			int amountToWithdraw = (int) (itemToWithdraw.getAmount());
			WithdrawItemHandler.addItem(new WithdrawItem(itemToWithdraw.getItem().getId(), amountToWithdraw,
					itemToWithdraw.getItem().getName()).setInventory(combatTask.getRequiredInventory()));
			return 0;
		}else {
			log("helloo");
			return walkToAreaNode.setArea(combatTask.getActionArea());
		}
	}

	private InventoryItem getItemToWithdraw(RequiredInventory inventory) {
		for (InventoryItem item : inventory.getItems()) {
			if (getInventory().getAmount(item.getItem().getId()) < item.getAmount()) {
				return item;
			}
		}
		return null;
	}

	private int handleShallEat(CombatTask combatTask) {
		if (combatTask.getFood() == null) {
			log("we got no food. lets walk to bank :) ");
			return walkToAreaNode.setArea(WebBank.getNearest(this).getArea());
		} else if (!inventory.contains(combatTask.getFood().getId())) {
			WithdrawItemHandler.addItem(new WithdrawItem(combatTask.getFood(), 1));
			return 0;
		} else {
			return eatNode.setFood(combatTask.getFood());
		}
	}

	private GearItem getGearItemToEquip(Gear gear) {
		for (Entry<EquipmentSlot, GearItem> entry : gear.getGear().entrySet()) {
			EquipmentSlot key = entry.getKey();
			GearItem value = entry.getValue();
			if (key != null && value != null && value.getItem() != null
					&& !equipment.isWearingItem(key, value.getItem().getId())) {
				log("not wearing:" + value.getItem().getName());
				return value;
			}
		}
		return null;
	}

	private boolean shouldDepositUnnecessaryItems(CombatTask combatTask) {
		for(Item item : inventory.getItems()) {
			if (item != null && !combatTask.getRequiredInventory().getItemIds().contains(item.getId())){
				return true;
			}
		}
		return false;
	}



}
