package org.nexus.task.woodcutting;

import java.awt.Graphics2D;
import java.util.ArrayList;

import org.nexus.Nex;
import org.nexus.handler.bank.DepositItemHandler;
import org.nexus.handler.bank.WithdrawItemHandler;
import org.nexus.handler.combat.CombatHandler;
import org.nexus.handler.gear.GearHandler;
import org.nexus.handler.gear.GearItem;
import org.nexus.loot.Loot;
import org.nexus.loot.LootHandler;
import org.nexus.node.combat.LootNode;
import org.nexus.node.woodcutting.Cut;
import org.nexus.task.ActionTask;
import org.nexus.task.TaskType;
import org.nexus.utils.item.DepositItem;
import org.nexus.utils.item.RSItem;
import org.nexus.utils.item.WithdrawItem;
import org.osbot.rs07.Bot;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.MethodProvider;

public class WoodcuttingTask extends ActionTask {

	private String treeName;
	private RSItem axe;
	private ArrayList<Integer> itemsToKeep;
	private Cut cutTreeNode;

	public WoodcuttingTask(Area actionArea, Area bankArea, RSItem axe, String treeName) {
		setActionArea(actionArea);
		setBankArea(bankArea);
		this.treeName = treeName;
		this.axe = axe;
		this.skill = Skill.WOODCUTTING;
		Nex.experienceTracker.start(Skill.WOODCUTTING);
		
		// NexusScript.setTracker(getSkill());
	}
	

	@Override
	public MethodProvider exchangeContext(Bot bot) {
		cutTreeNode = new Cut();
		cutTreeNode.exchangeContext(bot);
		return super.exchangeContext(bot);
	}

	@Override
	public int onLoop() throws InterruptedException {
		log("in wc task");
		if (!inventory.contains(axe.getId()) && !equipment.isWieldingWeapon(axe.getId())) {
			log("should not run");
			WithdrawItemHandler.addItem(new WithdrawItem(axe.getId(), 1, axe.getName()));
		} else if (canWieldAxe() && !equipment.isWieldingWeapon(axe.getId())) {
			log("Should gear");
			GearHandler.addItem(new GearItem(EquipmentSlot.WEAPON, axe));
		} else if (inventory.isFull() || playerInBank() && shouldDepositUnnecessaryItems()) {
			itemsToKeep = new ArrayList<Integer>();
			if (!equipment.isWieldingWeapon(axe.getId())) {
				itemsToKeep.add(axe.getId());
			}
			DepositItemHandler.addItem(new DepositItem(DepositItem.DepositType.DEPOSIT_ALL_EXCEPT, itemsToKeep));
		} else if (!playerInActionArea()) {
			return walkToAreaNode.setArea(getActionArea());
		} else if (inventory.getEmptySlotCount() >= 2
				&& LootNode.valueableDropAvailable(this, "Bird nest", getActionArea()) != null) {
			log("lets loot birds nest@");
			return CombatHandler.lootNode.setItemAndArea(
					LootNode.valueableDropAvailable(this, "Bird nest", getActionArea()), getActionArea());
		} else if (!myPlayer().isAnimating()) {
			return cutTreeNode.setTask(this);
		} // else return idle, prepare for next tree
		return 300;
	}

	private boolean shouldDepositUnnecessaryItems() {
		int emptySpots = 27;
		if (canWieldAxe()) {
			emptySpots = 28;
		}
		return inventory.getEmptySlots() != emptySpots;
	}

	private boolean canWieldAxe() {
		switch (axe.getName()) {
		case "Bronze axe":
		case "Iron axe":
			return true;
		case "Mithril axe":
			if (getSkills().getStatic(Skill.ATTACK) >= 20) {
				return true;
			}
		case "Rune axe":
			if (getSkills().getStatic(Skill.ATTACK) >= 40) {
				return true;
			}
		case "Dragon axe":
			if (getSkills().getStatic(Skill.ATTACK) >= 60) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onPaint(Graphics2D g) {
		/*
		 * g.drawString("XP Gained: " +
		 * NexusScript.experienceTracker.getGainedXP(getSkill()), 350, 50);
		 * g.drawString("XP Per Hour: " +
		 * NexusScript.experienceTracker.getGainedXPPerHour(getSkill()), 350, 75);
		 * g.drawString("Logs Per Hour: " +
		 * NexusScript.perHour(LootHandler.loot.size()), 350, 100);
		 * g.drawString("Break after level: " + getWantedLevel(), 350, 125);
		 * g.drawString("Or Break in: " + getTimeLeft() + " minutes", 350, 150);
		 * g.drawString("Money made: " + LootHandler.valueOfLoot, 350, 175);
		 * g.drawString("Money per hour: " +
		 * NexusScript.perHour(LootHandler.valueOfLoot), 350, 200);
		 */
	}

	/*
	 * public Loot getLog() { switch(treeName) { case "Tree": return new Loot(new
	 * RSItem("Logs", 1511), 1); case "Oak": return new Loot(new RSItem("Oak logs",
	 * 1521), 1); case "Willow": return new Loot(new RSItem("Willow logs", 1519),
	 * 1); case "Maple": return new Loot(new RSItem("Maple logs", 1517), 1); case
	 * "Yew": return new Loot(new RSItem("Maple logs", 1517), 1); } return null; }
	 * 
	 */

	public Loot getTreeLog() {
		switch (treeName) {
		case "Tree":
			return new Loot(new RSItem("Logs", 1511), 1);
		case "Oak":
			return new Loot(new RSItem("Oak logs", 1521), 1);
		case "Willow":
			return new Loot(new RSItem("Willow logs", 1519), 1);
		case "Maple":
			return new Loot(new RSItem("Maple logs", 1517), 1);
		case "Yew":
			return new Loot(new RSItem("Maple logs", 1517), 1);
		}
		return null;
	}

	@Override
	public TaskType getTaskType() {
		return TaskType.WOODCUTTING;
	}

	public String getTreeName() {
		return treeName;
	}
	

	@Override
	public String getLog() {
		String respond =  getTaskID();
		if(client.isLoggedIn() && myPlayer().getPosition() != null) {
			respond += ":position;" + myPlayer().getPosition().getX() + ";" + myPlayer().getPosition().getY() + ";" + myPlayer().getPosition().getZ();
		}else {
			respond += ":position;" + 0 + ";" + 0 + ";" + 0;

		}
		
		if(Nex.experienceTracker != null) {
			respond += ":xp;" + Nex.experienceTracker.getGainedXPPerHour(getSkill());
		}else {
			respond += ":xp;" + 0;
		}
		
		if(LootHandler.valueOfLoot > 0) {
			respond += ":loot;" + Nex.perHour(LootHandler.valueOfLoot);
		}else {
			respond += ":loot;" + 0;

		}
		return respond;
	}
}
