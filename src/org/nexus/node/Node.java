package org.nexus.node;

import org.nexus.Nex;
import org.nexus.task.Task;
import org.osbot.rs07.script.MethodProvider;

public abstract class Node extends MethodProvider {
	public abstract int onLoop();
	
	public abstract String toString(); //Node name - eg: CutTree
	
	public Task getCurrentTask() {
		return TaskHandler.getCurrentTask();
	}
	

}
