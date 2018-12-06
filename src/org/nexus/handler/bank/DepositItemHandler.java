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
import org.nexus.utils.inventory.RequiredInventory;
import org.nexus.utils.item.DepositItem;
import org.nexus.utils.item.WithdrawItem;
import org.nexus.utils.item.DepositItem.DepositType;
import org.osbot.rs07.Bot;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.script.MethodProvider;

public class DepositItemHandler extends BankHandler {
	public static Stack<DepositItem> itemsToDeposit = new Stack<DepositItem>();

	private int[] stockArr;

	@Override
	public int onLoop() {
		return getDepositNode(itemsToDeposit.peek());
	}

	public int getDepositNode(DepositItem depositItem) {

		checkIfDepositIsCompleted(depositItem);
		// check if bankHandler still contains depositItem
		if (!DepositItemHandler.itemsToDeposit.contains(depositItem)) {
			return 200;
		} else if (!playerInBank()) {
			return walkToAreaNode.setArea(getBankArea());
		} else if (!bankIsOpen()) {
			log("in bank");
			return openBankNode.onLoop();
		} else if (CheckIfWeShallSellItems.getTimeTilNextCheckInMinutes() <= 0) {
			log("lets check items for some stupid reason.");
			log(System.currentTimeMillis());
			log(CheckIfWeShallSellItems.getNextCheckInMilli());
			return checkIfSellItemNode.onLoop();
		} else {
			log("lets deposit");
			return depositNode.setItem(depositItem);
		}
	}

	
	private void checkIfDepositIsCompleted(DepositItem depositItem) {
		switch (depositItem.getType()) {
		case DEPOSIT_ALL:
			if (inventory.isEmpty()) {
				DepositItemHandler.removeItem(depositItem);
			}
			break;
		case DEPOSIT_ALL_EXCEPT:
			stockArr = depositItem.getItems().stream().mapToInt(i -> i).toArray();
			if (inventory.isEmptyExcept(stockArr)) {
				DepositItemHandler.removeItem(depositItem);
			}
			break;
		default:
			break;
		}
	}

	


	public static void addItem(DepositItem item) {
		itemsToDeposit.add(item);
	}

	public static void removeItem(DepositItem item) {
		itemsToDeposit.remove(item);
	}

	public static DepositItem getDepositItem() {
		if (itemsToDeposit.isEmpty()) {
			return null;
		}
		return itemsToDeposit.peek();
	}

}
