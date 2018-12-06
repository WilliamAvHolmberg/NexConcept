package org.nexus.node.ge;


import org.nexus.communication.NexHelper;
import org.nexus.communication.message.request.MuleRequest;
import org.nexus.handler.grandexchange.BuyItemHandler;
import org.nexus.node.Node;
import org.nexus.task.TaskType;
import org.nexus.utils.Sleep;

public class HandleCoins extends Node {


	@Override
	public int onLoop(){
		if (bank.isOpen()) {
			if(!inventory.isEmptyExcept(995)) {
			log("lets deposit all except coins");
			bank.depositAllExcept(995);
			Sleep.sleepUntil(() -> inventory.onlyContains(995), 2000);
			}else if(!inventory.contains(995) && bank.getAmount(995) >= 1.5*BuyItemHandler.getItem().getAmount() * BuyItemHandler.getItem().getItemPrice()) {
				log("lets withdraw coins");
				bank.withdrawAll(995);
				Sleep.sleepUntil(() -> inventory.onlyContains(995), 2000);
			}else {
				NexHelper.pushMessage(new MuleRequest(this, NexHelper.getQueue(), "MULE_WITHDRAW:995:" + (int) 1.5 *BuyItemHandler.getItem().getAmount() * BuyItemHandler.getItem().getItemPrice()));
				log("sleep until we got new task");
				Sleep.sleepUntil(() -> getCurrentTask().getTaskType() == TaskType.WITHDRAW_ITEM_FROM_MULE, 15000);
			}
		}else {
			try {
				bank.open();
				Sleep.sleepUntil(() -> bank.isOpen(), 2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return 200;
	}

	@Override
	public String toString() {
		return "Deposit All But Coins";
	}

	

}
