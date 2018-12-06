package org.nexus.node.combat;


import org.nexus.node.Node;
import org.nexus.utils.Exchange;
import org.nexus.utils.Sleep;
import org.nexus.utils.item.RSItem;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.script.MethodProvider;



public class LootNode extends Node {

	private GroundItem item;
	private Area area;



	@Override
	public int onLoop() {
		loot(item);
		return 200;

	}

	public void loot(GroundItem item) {
		int amountBeforeLoot = (int) getInventory().getAmount(item.getId());
		if (item != null) {
			item.interact("Take");
			Sleep.sleepUntil(() -> inventory.getAmount(item.getId()) > amountBeforeLoot, 300, 4000);
			int amountAfterLoot = (int) getInventory().getAmount(item.getId());

			// if successful loot
			if (amountAfterLoot > amountBeforeLoot) {
				
				log("Successfull loot");
				if(item.getName().equals("Mark of grace")) {
					//((AgilityTask) NexusScript.currentTask).updateMarkOfGraces();
					//LootHandler.valueOfLoot += 13780;
					log("adding mark of grace loot");
				}

				// calculate the quantity
				int lootAmount = amountAfterLoot - amountBeforeLoot;
				// get iaoxItem
				RSItem rsItem = new RSItem(item.getName(), item.getId());
				// nullcheck if we found the item
				if (rsItem != null) {
					log("item price: " + Exchange.getPrice(item.getId(), this));
					//LootHandler.addLoot(new Loot(rsItem, lootAmount));
					log("item price: " + Exchange.getPrice(rsItem.getId(), this));

				} else {
					log("could not find item: " + item.getName());
				}
			}
		}
	}

	public static GroundItem valueableDropAvailable(MethodProvider methodProvider, int threshold, Area area) {
		for (GroundItem item : methodProvider.groundItems.getAll()) {
			int price = Exchange.getPrice(item.getId(), methodProvider);
			if (area.contains(item) && price * item.getAmount() > threshold) {
				return item;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Eat";
	}

	public int setItemAndArea(GroundItem item, Area area) {
		this.item = item;
		this.area = area;
		return onLoop();
	}

	public static GroundItem valueableDropAvailable(MethodProvider methodProvider, String name, Area area) {
		for (GroundItem item : methodProvider.groundItems.getAll()) {
			int price = Exchange.getPrice(item.getId(), methodProvider);
			if (area.contains(item) && item.getName().equals(name)) {
				return item;
			}
		}
		return null;
	}

}
