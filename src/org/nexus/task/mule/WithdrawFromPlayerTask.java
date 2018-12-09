package org.nexus.task.mule;

import java.awt.Graphics2D;
import java.util.function.BooleanSupplier;

import org.nexus.node.mule.WithdrawItemFromPlayer;
import org.nexus.task.TaskType;
import org.nexus.utils.Sleep;
import org.osbot.rs07.Bot;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.script.MethodProvider;

public class WithdrawFromPlayerTask extends Mule {

	protected WithdrawItemFromPlayer tradeWithSlave;
	public WithdrawFromPlayerTask(int world, int itemID, int itemAmount, int startAmount, String tradeName) {
		super(world, itemID, itemAmount, startAmount, tradeName);
	}
	
	@Override
	public MethodProvider exchangeContext(Bot bot) {
		tradeWithSlave = new WithdrawItemFromPlayer();
		tradeWithSlave.exchangeContext(bot);
		return super.exchangeContext(bot);
	}

	@Override
	public void onPaint(Graphics2D g) {
		g.drawString("Started: " + getTimeStartedInMilli(), 250, 250);
		g.drawString("Time til new request " + getTimeLeft() , 250, 270);
		super.onPaint(g);
	}

	@Override
	public int onLoop() throws InterruptedException {
		if(world > 0 && getWorlds().getCurrentWorld() != world) {
			log("lets hop");
			getWorlds().hop(world);
			Sleep.sleepUntil(() ->getWorlds().getCurrentWorld() == world, 15000);
			Sleep.sleep(10000);
		}
		else if (getMule(getTradeName()) != null) {
			log("Mule is available within a distance of:"
					+ getMule(getTradeName()).getPosition().distance(myPlayer()));
			return withdrawItemFromPlayer.onLoop();
		}else {
			log("mule name is not avialable");
		}
		return 200;
	}

	@Override
	public String getLog() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TaskType getTaskType() {
		return TaskType.WITHDRAW_ITEM_FROM_MULE;
	}
}
