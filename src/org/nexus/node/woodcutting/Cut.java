package org.nexus.node.woodcutting;

import java.util.Arrays;
import java.util.List;

import org.nexus.node.Node;
import org.nexus.task.woodcutting.WoodcuttingTask;
import org.nexus.utils.Sleep;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.script.MethodProvider;


public class Cut extends Node {

	RS2Object object;
	private WoodcuttingTask wcTask;

	
	@Override
	public int onLoop() {
		object = getClosestAvailableTree();
		if (object != null && object.interact("Chop down")) {
			log("Successfully interacted with object");
			Sleep.sleepUntil(() -> !object.exists() || myPlayer().isAnimating(), 2500);
		} else {
			log("We failed to interact with object");
		}
		return 200;

	}

	/*
	 * Filter that sorts out objects that are available
	 */

	Filter<RS2Object> appropriateObjectFilter = new Filter<RS2Object>() {
		@Override
		public boolean match(RS2Object o) {
			return wcTask.getActionArea().contains(o) && o.getName().equals(wcTask.getTreeName()) && o.hasAction("Chop down");
		}
	};


	public RS2Object getClosestAvailableTree() {
		return objects.closest(appropriateObjectFilter);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Cut";
	}

	public int setTask(WoodcuttingTask task) {
		this.wcTask = task;
		return onLoop();
	}

}
