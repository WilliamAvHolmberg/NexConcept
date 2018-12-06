package org.nexus.handler;

import org.nexus.Nex;
import org.nexus.node.bank.Deposit;
import org.nexus.node.bank.Withdraw;
import org.nexus.node.equip.EquipItem;
import org.nexus.node.walking.WalkToArea;
import org.nexus.task.ActionTask;
import org.nexus.task.Task;
import org.nexus.utils.WebBank;
import org.osbot.rs07.Bot;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.script.MethodProvider;

public abstract class Handler extends MethodProvider {

	public static WalkToArea walkToAreaNode = new WalkToArea();
	public static Withdraw withdraw = new Withdraw();
	public static Deposit deposit = new Deposit();

	@Override
	public MethodProvider exchangeContext(Bot bot) {
		this.walkToAreaNode.exchangeContext(bot);
		this.withdraw.exchangeContext(bot);
		this.deposit.exchangeContext(bot);
		return super.exchangeContext(bot);
	}
	public abstract int onLoop();

	public ActionTask getCurrentTask() {
		if (Nex.CURRENT_TASK instanceof ActionTask) {
			return (ActionTask) Nex.CURRENT_TASK;
		}
		return null;
	}

	public Area getBankArea() {
		if (getCurrentTask() == null || getCurrentTask().getBankArea() == null) {
			return WebBank.getNearest(this).getArea();
		}
		return getCurrentTask().getBankArea();
	}

	public boolean playerInArea(Area area) {
		return area.contains(myPlayer());
	}

	public boolean playerInBank() {
		return getBankArea().contains(myPlayer());
	}

	public boolean playerInActionArea() {
		if (getCurrentTask() != null && getCurrentTask().getActionArea() != null) {
			return playerInArea(getCurrentTask().getActionArea());
		}
		return false;
	}
	


}
