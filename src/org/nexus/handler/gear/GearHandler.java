package org.nexus.handler.gear;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.nexus.handler.Handler;
import org.nexus.node.Node;
import org.nexus.node.equip.EquipItem;
import org.osbot.rs07.Bot;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.script.MethodProvider;

public class GearHandler extends Handler {

	public static Stack<GearItem> itemsToEquip = new Stack<GearItem>();
	private EquipItem equipItemNode = new EquipItem();
	private Gear preferredGear;

	@Override
	public int onLoop() {
		if (itemsToEquip.isEmpty()) {
			preferredGear = getCurrentTask().getPreferredGear();

			for (HashMap.Entry<EquipmentSlot, GearItem> entry : preferredGear.getGear().entrySet()) {
				EquipmentSlot slot = entry.getKey();
				GearItem item = entry.getValue();
				if (item != null && !equipment.isWearingItem(slot, item.getItem().getName())) {
					itemsToEquip.push(item);
					log("should see this once");
				}
			}

		} else {
			if (isWearing(itemsToEquip.peek())) {
				itemsToEquip.pop();
			} else {
				return equipItemNode.setGearItem(itemsToEquip.peek());
			}
		}
		return 200;
	}

	private boolean isWearing(GearItem peek) {
		return equipment.isWearingItem(peek.getSlot(), peek.getItem().getId());
	}

	/*
	 * public GearItem getPreferredGearInSlot(EquipmentSlot slot) { for(GearItem
	 * item: gear) { if(item.getSlot() == slot) { return item; } } return null; }
	 */

	public static GearItem getWithdrawItem() {
		if (itemsToEquip.isEmpty()) {
			return null;
		}
		return itemsToEquip.peek();
	}

	public static void addItem(GearItem item) {
		itemsToEquip.add(item);
	}

	public static void removeItem(GearItem item) {
		itemsToEquip.remove(item);
	}

	@Override
	public MethodProvider exchangeContext(Bot bot) {
		this.equipItemNode.exchangeContext(bot);
		return super.exchangeContext(bot);
	}


}
