package org.nexus.node.equip;


import org.nexus.handler.bank.BankHandler;
import org.nexus.handler.bank.WithdrawItemHandler;
import org.nexus.handler.gear.GearItem;
import org.nexus.node.Node;
import org.nexus.utils.Sleep;
import org.nexus.utils.item.WithdrawItem;
import org.osbot.rs07.script.MethodProvider;



public class EquipItem extends Node{

	GearItem item;
	



	@Override
	public int onLoop() {
		if(widgets.closeOpenInterface() && inventory.contains(item.getItem().getId())) {
			equipment.equip(item.getSlot(), item.getItem().getId());
			Sleep.sleepUntil(() -> equipment.isWearingItem(item.getSlot(), item.getItem().getId()), 3000);
		}else {
			WithdrawItemHandler.addItem(new WithdrawItem(item.getItem().getId(), 1 , item.getItem().getName()));
		}
		return 200;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Equip item";
	}
	
	public int setGearItem(GearItem item) {
		this.item = item;
		return onLoop();
	}

}
