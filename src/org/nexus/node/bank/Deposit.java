package org.nexus.node.bank;

import java.util.ArrayList;
import java.util.List;

import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.script.MethodProvider;
import org.nexus.node.Node;
import org.nexus.utils.item.DepositItem;

public class Deposit extends Node {
	
	private DepositItem depositItem;
	private List<Integer> itemsToKeep;
	private int[] stockArr;



	@Override
	public int onLoop() {
		itemsToKeep = depositItem.getItems();
		try {
			if (grandExchange.close() && bank.open()) {
				switch (depositItem.getType()) {
				case DEPOSIT_ALL:
					itemsToKeep = null;
					bank.depositAll();
					break;
				case DEPOSIT_ALL_EXCEPT:
					depositAllExcept(itemsToKeep);
				default:
					break;

				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 200;
	}

	private void depositAllExcept(List<Integer> items) {
		if (items != null && !items.isEmpty()) {
			stockArr = items.stream().mapToInt(i->i).toArray();
			bank.depositAllExcept(stockArr);
		} else {
			bank.depositAll();
		}

	}

	

	@Override
	public String toString() {
		return "Deposit to Bank";
	}

	public int setItem(DepositItem depositItem) {
		this.depositItem = depositItem;
		return onLoop();
	}

}
