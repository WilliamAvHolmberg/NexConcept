package org.nexus.node.mule;

import java.util.ArrayList;
import java.util.Arrays;

import org.nexus.Nex;
import org.nexus.handler.TaskHandler;
import org.nexus.handler.grandexchange.SellItemHandler;
import org.nexus.node.Node;
import org.nexus.task.mule.PrepareForMuleDepositTask;
import org.nexus.utils.Exchange;
import org.nexus.utils.item.GESellItem;
import org.osbot.rs07.api.model.Item;


public class CheckIfWeShallSellItems extends Node{

	private ArrayList<String> untradeableItems = new ArrayList<String>(Arrays.asList("Oak logs", "Shrimps", "Mind rune", "Air rune", "Fire rune", "Water rune", "Earth rune"));
	//used to check when last time we checked items was
	public static long last_check = 0;

	@Override
	public int onLoop() {
		try {
			if(bank.open()) {
				ArrayList<GESellItem> itemsToSell = new ArrayList<GESellItem>();
				int totalValue = (int) bank.getAmount(995);
				log(totalValue);
				for(Item item : bank.getItems()) {
					if(item != null && item.getId() != 995) {
					int value = item.getAmount() * Exchange.getPrice(item.getId(), this);
					
					if((!untradeableItems.contains(item.getName()) || worlds.isMembersWorld() || getQuests().getQuestPoints() >= 7) &&value > Nex.item_theshold && (getCurrentTask() == null || !getCurrentTask().getRequiredItems().contains(item.getId()))) {
						itemsToSell.add(new GESellItem(item.getId(), item.getName(), item.getAmount(), (int) (value * 0.6)));
						totalValue += value;
					}
					}
				
				}
				if(totalValue > Nex.mule_threshold) {
					log("lets sell!");
					itemsToSell.forEach(item -> {
						SellItemHandler.addItem(item);
					});
					TaskHandler.addTaskAndResetStack(new PrepareForMuleDepositTask());
				}else {
					log("lets not sell. Remove deposit to mule");
				}
				log("total value: " + totalValue);
				last_check = System.currentTimeMillis();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 200;
		
	}

	
	public static long getNextCheckInMilli() {
		return ((last_check + 1800 * 1000) );

	}
	
	public static long getTimeTilNextCheckInMinutes() {
		return (getNextCheckInMilli() - System.currentTimeMillis())/60000;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
