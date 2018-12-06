package org.nexus.loot;

import org.nexus.utils.item.RSItem;

public class Loot {

	private RSItem item;
	private int amount;
	
	public Loot(RSItem item, int amount) {
		this.item = item;
		this.amount = amount;
	}
	
	public RSItem getItem() {
		return item;
	}
	
	public int getAmount() {
		return amount;
	}
	

}
