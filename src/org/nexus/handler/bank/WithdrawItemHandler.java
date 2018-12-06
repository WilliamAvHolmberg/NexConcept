package org.nexus.handler.bank;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.nexus.handler.Handler;
import org.nexus.node.Node;
import org.nexus.node.bank.Deposit;
import org.nexus.node.bank.OpenBank;
import org.nexus.node.bank.Withdraw;
import org.nexus.node.mule.CheckIfWeShallSellItems;
import org.nexus.utils.inventory.InventoryItem;
import org.nexus.utils.inventory.RequiredInventory;
import org.nexus.utils.item.DepositItem;
import org.nexus.utils.item.WithdrawItem;
import org.nexus.utils.item.DepositItem.DepositType;
import org.osbot.rs07.Bot;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.script.MethodProvider;

public class WithdrawItemHandler extends BankHandler {
	public static Stack<WithdrawItem> itemsToWithdraw = new Stack<WithdrawItem>();

	private int[] stockArr;

	@Override
	public int onLoop() {
		return getWithdrawNode(itemsToWithdraw.peek());
	}



	private int getWithdrawNode(WithdrawItem withdrawItem) {
		if (withdrawItem.getInventory() != null
				&& !RequiredInventory.inventoryOnlyContainsRequiredItems(this, withdrawItem.getInventory())) {
			DepositItemHandler.addItem(
					new DepositItem(DepositType.DEPOSIT_ALL_EXCEPT, withdrawItem.getInventory().getItemIds()));
			return 200;
		} else if (inventory.getAmount(withdrawItem.getItemID()) == withdrawItem.getAmount()) {
			WithdrawItemHandler.removeItem(withdrawItem);
			log("lets remove item");
			return 300;
		} else if (!playerInBank()) {
			return walkToAreaNode.setArea(getBankArea());
		} else if (!bankIsOpen()) {
			return openBankNode.onLoop();
		} else if (CheckIfWeShallSellItems.getTimeTilNextCheckInMinutes() <= 0) {
			log("lets check items for some stupid reason.");
			log(System.currentTimeMillis());
			log(CheckIfWeShallSellItems.getNextCheckInMilli());
			return checkIfSellItemNode.onLoop();
		} else {
			return withdrawNode.setItem(withdrawItem);
		}
	}


	

	

	public static void addItem(WithdrawItem item) {
		itemsToWithdraw.add(item);
	}
	
	public static void addItem(InventoryItem item) {
		itemsToWithdraw.add(new WithdrawItem(item.getItem(), item.getAmount()));
	}

	public static void removeItem(WithdrawItem item) {
		itemsToWithdraw.remove(item);
	}

	public static WithdrawItem getWithdrawItem() {
		if (itemsToWithdraw.isEmpty()) {
			return null;
		}
		return itemsToWithdraw.peek();
	}


}
