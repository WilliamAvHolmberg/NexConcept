package org.nexus.node.mule;

import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.listener.MessageListener;
import org.osbot.rs07.script.MethodProvider;

public class WithdrawItemFromPlayer extends Trade {



	@Override
	public int onLoop() {
		initValues();
		if(!getTrade().isCurrentlyTrading()) {
			initiateTrade(playerToTrade);
		}else if(firstTradeScreenIsOkay(itemID, itemAmount)) {
			acceptFirstScreen();
		}else if(trade.isSecondInterfaceOpen()) {
			acceptFirstScreen();
		}else {
			log("Waiting for mule to do move.");
		}
		return 200;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Trade with Slave";
	}

}
