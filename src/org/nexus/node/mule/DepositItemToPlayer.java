 package org.nexus.node.mule;


import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.listener.MessageListener;
import org.osbot.rs07.script.MethodProvider;

public class DepositItemToPlayer extends Trade{



	@Override
	public int onLoop() {
		initValues();
		if (!getTrade().isCurrentlyTrading()) {
			initiateTrade( playerToTrade);
		} else if (trade.isSecondInterfaceOpen()) {
			acceptFirstScreen();
		} else if (!firstTradeScreenIsOkay( itemID, itemAmount)) {
			log("lets add item");
			log("item id:" + itemID);
			addItemToTrade( itemID, itemAmount);
		} else if (trade.isFirstInterfaceOpen()) {
			acceptFirstScreen();
		}
		return 200;
		
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Trade with Mule";
	}



}
