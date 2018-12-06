package org.nexus.node.mule;

import org.nexus.node.Node;
import org.nexus.task.mule.Mule;
import org.nexus.utils.Sleep;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.script.MethodProvider;


public abstract class Trade extends Node{

	protected String playerToTrade;
	protected int itemID;
	protected int itemAmount;



	public void initValues() {
		playerToTrade = ((Mule) getCurrentTask()).getTradeName();
		itemID = ((Mule) getCurrentTask()).getItemID();
		itemAmount = ((Mule) getCurrentTask()).getItemAmount();
	}
	
	

	public void initiateTrade(String name) {
		Player target = getPlayer(name);
		if(target != null) {
			target.interact("Trade with");
			Sleep.sleepUntil(() -> trade.isCurrentlyTrading(), 25000);
		}
		
	}
	
	public boolean firstTradeScreenIsOkay(int itemID, int itemAmount) {
		if(getTrade().getTheirOffers().getAmount(itemID) >= itemAmount || getTrade().getOurOffers().getAmount(itemID) >= itemAmount ) {
			return true;
		}
		return false;
	}
	
	public void acceptFirstScreen() {
			trade.acceptTrade();
			Sleep.sleepUntil(() -> !trade.isFirstInterfaceOpen(), 7500);
	}
	
	public void addItemToTrade(int itemID, int itemAmount) {
		if(trade.isFirstInterfaceOpen()) {
			getTrade().offer(itemID, itemAmount);
			Sleep.sleepUntil(() -> getTrade().getOurOffers().getAmount(995) >= itemAmount, 7500);
		}
	}
	
	
	public Player getPlayer(String name) {
		for(Player player : players.getAll()) {
			if(player.getName().toLowerCase().equals(name.toLowerCase())) {
				return player;
			}
		}
		return null;
	}




}
