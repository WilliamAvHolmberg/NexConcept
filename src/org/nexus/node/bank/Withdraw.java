package org.nexus.node.bank;

import org.nexus.communication.NexHelper;
import org.nexus.communication.message.request.MuleRequest;
import org.nexus.handler.grandexchange.BuyItemHandler;
import org.nexus.node.Node;
import org.nexus.task.ActionTask;
import org.nexus.task.TaskType;
import org.nexus.utils.Sleep;
import org.nexus.utils.item.GEItem;
import org.nexus.utils.item.WithdrawItem;
import org.osbot.rs07.script.MethodProvider;

public class Withdraw extends Node {

	private WithdrawItem item;
	int amountRequired;
	int bankAmount;
	int amountRequiredFromGE;
	int invAmountPreWithdraw;

	@Override
	public int onLoop() {
		try {
			if (bank.open()) {
				if (bank.getWithdrawMode() == item.getWithdrawMode()) {
					invAmountPreWithdraw = (int) inventory.getAmount(item.getItemID());
					amountRequired = item.getAmount() - invAmountPreWithdraw;
					bankAmount = (int) bank.getAmount(item.getItemID());
					log("Amount of items required:" + amountRequired);
					log("Amount of items in Bank:" + bankAmount);
					log("Amount of items in inventory pre withdraw:" + invAmountPreWithdraw);

					if (bankAmount < amountRequired) {
						amountRequiredFromGE = amountRequired - bankAmount;
						handleBankDoesNotContainItem(item, amountRequiredFromGE);
					} else {
						bank.withdraw(item.getItemID(), amountRequired);
						Sleep.sleepUntil(
								() -> inventory.getAmount(item.getItemID()) - invAmountPreWithdraw == amountRequired,
								3000);
					}
				} else {
					bank.enableMode(item.getWithdrawMode());
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 200;
	}

	private void handleBankDoesNotContainItem(WithdrawItem item, int amountRequiredFromGE) {
		if (item.getItemID() == 995) {
			if(amountRequiredFromGE < 20000) {
				amountRequiredFromGE = 20000;
			}
			NexHelper.pushMessage(new MuleRequest(this,NexHelper.getQueue(), "MULE_WITHDRAW:995:" + amountRequiredFromGE));
			log("sleep until we got new task");
			Sleep.sleepUntil(() -> getCurrentTask().getTaskType() == TaskType.WITHDRAW_ITEM_FROM_MULE, 15000);
		} else {
			if (getCurrentTask().getRequiredInventory() != null
					&& getCurrentTask().getRequiredInventory().find(item.getItemID()) != null) {
				log("Item that we are buying are inventory item. lets change");
				amountRequiredFromGE = getCurrentTask().getRequiredInventory().find(item.getItemID()).getBuyAmount();
			}
			BuyItemHandler.addItem(new GEItem(item.getItemID(), amountRequiredFromGE, item.getItemName()));
			log("Bank does not contain the required amount of our item. Buy:" + amountRequiredFromGE);
		}

	}

	@Override
	public String toString() {
		return "Withdraw From Bank";
	}

	public int setItem(WithdrawItem withdrawItem) {
		this.item = withdrawItem;
		return onLoop();
	}

}
