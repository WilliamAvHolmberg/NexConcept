package org.nexus.task.mule;

import java.awt.Graphics2D;

import org.nexus.handler.bank.WithdrawItemHandler;
import org.nexus.node.mule.DepositItemToPlayer;
import org.nexus.node.mule.WithdrawItemFromPlayer;
import org.nexus.task.TaskType;
import org.nexus.utils.Sleep;
import org.nexus.utils.WebBank;
import org.nexus.utils.item.WithdrawItem;
import org.osbot.rs07.Bot;
import org.osbot.rs07.script.MethodProvider;

public class DepositToPlayerTask extends Mule {
	protected DepositItemToPlayer tradeWithMule;

	public DepositToPlayerTask(int world, int itemID, int itemAmount, int startAmount, String tradeName) {
		super(world, itemID, itemAmount, startAmount, tradeName);
	}

	@Override
	public MethodProvider exchangeContext(Bot bot) {
		tradeWithMule = new DepositItemToPlayer();
		tradeWithMule.exchangeContext(bot);
		return super.exchangeContext(bot);
	}

	@Override
	public void onPaint(Graphics2D g) {
		g.drawString("Trade to mule: " + tradeName, 350, 150);
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
		else if (!WebBank.GRAND_EXCHANGE.getArea().contains(myPlayer())) {
			walking.webWalk(WebBank.GRAND_EXCHANGE.getArea());
		} else if (!trade.isCurrentlyTrading() && inventory.getAmount(995) < getItemAmount()) {
			WithdrawItemHandler.addItem(new WithdrawItem(995, getItemAmount()));
		} else if (getMule(getTradeName()) != null) {
			log("Mule is available within a distance of:" + getMule(getTradeName()).getPosition().distance(myPlayer()));
			return tradeWithMule.onLoop();
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
		return TaskType.DEPOSIT_ITEM_TO_PLAYER;
	}
}
