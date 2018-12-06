package org.nexus.utils.item;

import org.nexus.utils.inventory.RequiredInventory;
import org.osbot.rs07.api.Bank.BankMode;

public class WithdrawItem extends RequiredItem{
	
	private RequiredInventory inventory;
	private BankMode withdrawMode = BankMode.WITHDRAW_ITEM;
	
	public WithdrawItem(int itemID, int itemAmount, String itemName) {
		setItemID(itemID);
		setAmount(itemAmount);
		setItemName(itemName);
	}
	
	public WithdrawItem(RSItem item, int itemAmount) {
		setItemID(item.getId());
		setAmount(itemAmount);
		setItemName(item.getName());
	}
	
	public WithdrawItem(int itemID, int itemAmount) {
		setItemID(itemID);
		setAmount(itemAmount);
		if(itemID == 995) {
			setItemName("Coins");
		}
	}
	
	public WithdrawItem(int itemID, int amount, BankMode bankMode) {
		setItemID(itemID);
		setAmount(amount);
		setBankMode(bankMode);
	}

	private void setBankMode(BankMode bankMode) {
		this.withdrawMode = bankMode;
		
	}

	public WithdrawItem setInventory(RequiredInventory inventory) {
		this.inventory = inventory;
		return this;
	}
	

	
	public RequiredInventory getInventory() {
		return inventory;
	}

	public BankMode getWithdrawMode() {
		return withdrawMode;
	}
}
