package org.nexus.node.combat;

import org.nexus.node.Node;
import org.nexus.utils.Sleep;
import org.nexus.utils.item.RSItem;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.MethodProvider;

public class Eat extends Node {

	private MethodProvider methodProvider;
	private RSItem food;

	@Override
	public int onLoop() {
		if (widgets.closeOpenInterface()) {
			int healthBeforeEat = skills.getDynamic(Skill.HITPOINTS);
			log("lets eat");
			inventory.interact("Eat", food.getName());
			Sleep.sleepUntil(() -> skills.getDynamic(Skill.HITPOINTS) >= healthBeforeEat + 1, 2500);
		}
		return 200;
	}

	public static int getCurrentHealth(MethodProvider methodProvider) {
		return methodProvider.skills.getDynamic(Skill.HITPOINTS);
	}

	public int getCurrentHealth() {
		return skills.getDynamic(Skill.HITPOINTS);
	}

	public static boolean shallEat(MethodProvider methodProvider) {
		return getCurrentHealth(methodProvider) < getStaticHealth(methodProvider) / 2;
	}

	private static int getStaticHealth(MethodProvider methodProvider) {
		return methodProvider.skills.getStatic(Skill.HITPOINTS);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Eat";
	}

	public int setFood(RSItem food) {
		this.food = food;
		return onLoop();
	}

}
