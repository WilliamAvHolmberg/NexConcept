package org.nexus.node.mule;


import org.nexus.Nex;
import org.nexus.communication.NexHelper;
import org.nexus.communication.message.request.MuleRequest;
import org.nexus.node.Node;
import org.nexus.task.TaskType;
import org.nexus.task.mule.Mule;
import org.nexus.utils.Sleep;
import org.osbot.rs07.script.MethodProvider;



public class PrepareForMuleDeposit extends Node {

	public boolean hasWithdrawnMoney = false;
	@Override
	public int onLoop() {
		if (!hasWithdrawnMoney) {
			withdrawMoney();
		}else {
			hasWithdrawnMoney = false;
			NexHelper.pushMessage(new MuleRequest(this, NexHelper.getQueue(),
					"MULE_DEPOSIT:995:" + inventory.getAmount(995)));
			Sleep.sleepUntil(() -> TaskHandler.getCurrentTask() != null && TaskHandler.getCurrentTask().getTaskType() == TaskType.DEPOSIT_ITEM_TO_PLAYER, 20000);
			
		}
		return 200;
	}

	private void withdrawMoney() {
		try {
			if (bank.open()) {
				if (bank.getAmount(995) == 0) {
					hasWithdrawnMoney = true;
				} else {
					bank.withdrawAll(995);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
