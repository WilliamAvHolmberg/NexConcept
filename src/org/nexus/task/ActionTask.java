package org.nexus.task;
import java.util.ArrayList;

import org.nexus.Nex;
import org.nexus.handler.gear.Gear;
import org.nexus.handler.gear.GearItem;
import org.nexus.node.walking.WalkToArea;
import org.nexus.utils.WebBank;
import org.nexus.utils.inventory.RequiredInventory;
import org.osbot.rs07.Bot;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.MethodProvider;

public abstract class ActionTask extends Task{

	protected Area actionArea;
	protected Area bankArea;
	protected Skill skill;
	protected WalkToArea walkToAreaNode;
	protected int levelGoal;

	
	@Override
	public MethodProvider exchangeContext(Bot bot) {
		walkToAreaNode = new WalkToArea();
		walkToAreaNode.exchangeContext(bot);
		return super.exchangeContext(bot);
	}

	public int getLevelGoal() {
		return levelGoal;
	}
	public void setLevelGoal(int levelGoal) {
		this.levelGoal = levelGoal;
	}

	
	public Area getActionArea() {
		return actionArea;
	}
	public void setActionArea(Area actionArea) {
		this.actionArea = actionArea;
	}
	public Area getBankArea() {
		if(bankArea != null) {
			return bankArea;
		}
		return WebBank.getNearest(this).getArea();
	}
	public void setBankArea(Area bankArea) {
		this.bankArea = bankArea;
	}
	public Skill getSkill() {
		return skill;
	}
	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	public void setWantedLevel(int levelGoal) {
		this.levelGoal = levelGoal;
	}
	
	public boolean isFinished() {
		if (breakAfterTime > 0 && timeStartedMilli > 0 && getTimeLeft() <= 0) {
			log("time");
			return true;
		} else if (getSkill() != null && getLevelGoal() <= getSkills().getStatic(getSkill())) {
			log("skill: "+  getSkill());
			log("Wanted: " + getLevelGoal());
			return true;
		}
		return false;
	}
	
	public boolean playerInBank() {
		return getBankArea().contains(myPlayer());
	}
	
	public boolean playerInActionArea() {
		return actionArea.contains(myPlayer());
	}
	
	
	@Override
	public void removeTask() {
		// TODO send TASK_COMPLETED to server
		Nex.CURRENT_TASK = null;
	}

}
