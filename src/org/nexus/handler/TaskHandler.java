package org.nexus.handler;

import java.util.Stack;

import org.nexus.Nex;
import org.nexus.task.Task;


public class TaskHandler {
	public static Stack<Task> available_tasks = new Stack<Task>();
	
	public static void addTask(Task task) {
		available_tasks.add(task);
	}
	
	/*
	 * Adds a task to the top of the list. If currenttask is not null, remove currenttask and add it to the list
	 */
	public static void addPrioritizedTask(Task task) {
		Task currentTask;
		if(Nex.CURRENT_TASK != null) {
			currentTask = Nex.CURRENT_TASK;
			
			available_tasks.push(currentTask);
			Nex.CURRENT_TASK = null;
		}
		available_tasks.push(task);
	}
	
	/*
	 * Adds a task to the top of the list. If currenttask is not null, remove currenttask and add it to the list
	 */
	public static void addTaskAndResetStack(Task task) {
		Nex.CURRENT_TASK = null;
		available_tasks.removeAllElements();
		available_tasks.add(task);
	}
	
	public static Task popTask() {
		if(available_tasks.isEmpty()) {
			return null;
		}
		return available_tasks.pop();
	}
	
	public static String getTaskList() {
		String tempString = "";
		if(available_tasks.isEmpty()) {
			return "No Tasks in TaskList.";
		}else {
			for(Task task : available_tasks) {
				tempString += task;
			}
		}
		return tempString;
	}
	
}
