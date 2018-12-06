package org.nexus.utils.inventory;

import org.nexus.utils.item.RSItem;
import org.osbot.rs07.api.ui.EquipmentSlot;



public class InventoryItem {
	
	private int amount;
	private RSItem item;
	private int buyAmount;
	
	public InventoryItem(int amount, RSItem item, int buyAmount) {
		this.amount = amount;
		this.item = item;
		this.buyAmount = buyAmount;
	}
	public int getAmount() {
		return amount;
	}

	public RSItem getItem() {
		return item;
	}
	
	public int getBuyAmount() {
		return buyAmount;
	}
	
	public InventoryItem setAmount(int amount) {
		this.amount = amount;
		return this;
	}

}
