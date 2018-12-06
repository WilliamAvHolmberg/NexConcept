package org.nexus.task.mule;

import java.awt.Graphics2D;

import org.nexus.node.mule.PrepareForMuleDeposit;
import org.nexus.node.walking.WalkToArea;
import org.nexus.task.TaskType;
import org.nexus.utils.WebBank;
import org.osbot.rs07.Bot;
import org.osbot.rs07.script.MethodProvider;

public class PrepareForMuleDepositTask extends Mule {
	private PrepareForMuleDeposit prepareForMuleDepositNode = new PrepareForMuleDeposit();
	private WalkToArea walkToAreaNode = new WalkToArea();
	public PrepareForMuleDepositTask() {
		super(0, 0, 0, 0, null);
		this.setBreakAfterTime(10);
	}
	
	@Override
	public MethodProvider exchangeContext(Bot bot) {
		walkToAreaNode.exchangeContext(bot);
		prepareForMuleDepositNode.exchangeContext(bot);
		return super.exchangeContext(bot);
	}


	@Override
	public void onPaint(Graphics2D g) {
		g.drawString("Preparing for mule deposit: ", 350,150);
		super.onPaint(g);
	}



	@Override
	public int onLoop() throws InterruptedException {
		if(!WebBank.GRAND_EXCHANGE.getArea().contains(myPlayer())) {
			walkToAreaNode.setArea(WebBank.GRAND_EXCHANGE.getArea());
		}
		return prepareForMuleDepositNode.onLoop();
	}







	@Override
	public TaskType getTaskType() {
		return TaskType.PREPARE_FOR_MULE_DEPOSIT;
	}
}
