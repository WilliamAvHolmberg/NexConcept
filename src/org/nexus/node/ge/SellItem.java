package org.nexus.node.ge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.nexus.handler.grandexchange.SellItemHandler;
import org.nexus.node.Node;
import org.nexus.utils.Sleep;
import org.nexus.utils.item.GESellItem;
import org.osbot.rs07.api.GrandExchange;
import org.osbot.rs07.api.GrandExchange.Box;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.MethodProvider;


public class SellItem extends Node {

	private GESellItem item;
	private int itemID;
	private int itemAmount;
	private String itemName;
	private int itemPrice;
	
	private List<Box> relevantBoxes;
	private List<Box> availableBoxes;
	private HashMap<Box, Integer[]> hash = new HashMap<Box, Integer[]>();
	private Box boxToUse;




	@Override
	public int onLoop(){
		hash.put(Box.BOX_1, new Integer[] { 465, 7 });
		hash.put(Box.BOX_2, new Integer[] { 465, 8 });
		hash.put(Box.BOX_3, new Integer[] { 465, 9 });
		hash.put(Box.BOX_4, new Integer[] { 465, 10 });
		hash.put(Box.BOX_5, new Integer[] { 465, 11 });
		hash.put(Box.BOX_6, new Integer[] { 465, 12 });
		itemID = item.getItemID();
		if(inventory.contains(itemID + 1)) {
			itemID = itemID + 1;
		}
		itemName = item.getItemName();
		itemAmount = (int) inventory.getAmount(itemName);
		itemPrice = item.getItemPrice();
		log("lets sell item:" + itemID);
		if (openGE()) {
			log("ge is open");
			relevantBoxes = getRelevantBoxes(itemID);
			if (relevantBoxes.isEmpty()) {
				createNewOffer(itemID, itemAmount, itemName, itemPrice);
			} else {
				handleBoxes(relevantBoxes, itemID);
			}
		}
		return 200;
	}

	private void createNewOffer(int itemID, int itemAmount, String itemName, int itemPrice) {
		log("lets create new buy offer: " + itemPrice);
		grandExchange.sellItem(itemID, 1, itemAmount);
		Sleep.sleepUntil(() -> !relevantBoxes.isEmpty(), 5000);
		claimItem();
		SellItemHandler.items.remove(item);
	}

	private void handleBoxes(List<Box> relevantBoxes, int itemID) {
		for (Box box : relevantBoxes) {
			log(grandExchange.getStatus(box));
			log("Item ID:" + grandExchange.getItemId(box));
			switch (grandExchange.getStatus(box)) {
			case FINISHED_SALE:
				claimItem();
				break;
			case PENDING_BUY:
				if (grandExchange.getItemId(box) == itemID) {
					abortUnfinishedOffer(box);
				}
				break;
			default:
				break;
			}
		}

	}

	private void abortUnfinishedOffer(Box box) {
		log("Trying to buy item, lets abandon: " + box);
		RS2Widget widget = widgets.get(hash.get(box)[0], hash.get(box)[1]);
		if (widget != null) {
			widget.interact("Abort offer");
		}
		// TODO sleep until offer is aborted
	}

	private void claimItem() {
		grandExchange.collect();
	}

	/*
	 * Shall return boxes that are either empty or contain our itemID
	 */
	private List<Box> getRelevantBoxes(int itemID) {
		relevantBoxes = new ArrayList<Box>();
		for (Box box : GrandExchange.Box.values()) {
			if (grandExchange.getStatus(box) != GrandExchange.Status.EMPTY
					&& (grandExchange.getStatus(box) == GrandExchange.Status.FINISHED_BUY
							|| grandExchange.getItemId(box) == itemID)) {
				relevantBoxes.add(box);
			}
		}
		return relevantBoxes;
	}

	/*
	 * Shall return boxes that are empty
	 */
	private List<Box> getAvailableBoxes() {
		relevantBoxes = new ArrayList<Box>();
		for (Box box : GrandExchange.Box.values()) {
			if (grandExchange.getStatus(box) == GrandExchange.Status.EMPTY) {
				relevantBoxes.add(box);
			}
		}
		return relevantBoxes;
	}

	private boolean openGE() {
		if (!grandExchange.isOpen()) {
			NPC grandExchangeClerk = getNpcs().closest("Grand Exchange Clerk");
			if (grandExchangeClerk != null) {
				boolean didInteraction = grandExchangeClerk.interact("Exchange");
				Sleep.sleepUntil(() -> grandExchange.isOpen(), 3000);
				return openGE();
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "Buy Item";
	}
	
	public int setItem(GESellItem geItem) {
		this.item = geItem;
		return onLoop();
	}

}
