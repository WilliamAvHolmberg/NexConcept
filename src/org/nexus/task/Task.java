package org.nexus.task;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.prefs.PreferencesFactory;

import org.nexus.Nex;
import org.nexus.handler.gear.Gear;
import org.nexus.handler.gear.GearItem;
import org.nexus.utils.inventory.RequiredInventory;
import org.osbot.rs07.script.MethodProvider;

public abstract class Task extends MethodProvider {

	protected long timeStartedMilli = System.currentTimeMillis();
	protected String taskID = "0";
	private Gear EMPTY_GEAR = new Gear();
	protected RequiredInventory requiredInventory;
	protected Gear preferredGear;
	protected long breakAfterTime;

	public abstract int onLoop() throws InterruptedException;

	public abstract boolean isFinished();

	public abstract void removeTask();

	public abstract String getLog();

	public abstract void onPaint(Graphics2D g);

	public long getTimeStartedInMilli() {
		return timeStartedMilli;
	}

	public abstract TaskType getTaskType();

	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}

	public String getTaskID() {
		return this.taskID;
	}

	public ArrayList<Integer> getRequiredItems() {
		ArrayList<Integer> reqItems = new ArrayList<Integer>();
		if (preferredGear != null) {
			for (GearItem item : preferredGear.getGear().values()) {
				if (item != null && item.getItem() != null) {
					reqItems.add(item.getItem().getId());
				}
			}
		}
		if (requiredInventory != null) {
			for (int invItem : requiredInventory.getItemIds()) {
				reqItems.add(invItem);
			}
		}

		return reqItems;
	}

	public void setRequiredInventory(RequiredInventory inventory) {
		this.requiredInventory = inventory;
	}

	public RequiredInventory getRequiredInventory() {
		return this.requiredInventory;
	}

	public Gear getPreferredGear() {
		if (this.preferredGear == null) {
			return EMPTY_GEAR;
		}
		return this.preferredGear;
	}

	public void setPreferredGear(Gear gear) {
		this.preferredGear = gear;
	}
	
	public long getBreakAfterTime() {
		return breakAfterTime;
	}

	public void setBreakAfterTime(long breakAfterTime) {
		this.breakAfterTime = breakAfterTime;
	}

	public long getTimeLeft() {
		return ((timeStartedMilli + (this.breakAfterTime * 60 * 1000)) - System.currentTimeMillis()) / 60000;
	}
	

}
