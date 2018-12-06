package org.nexus.utils.inventory;

import java.util.ArrayList;
import java.util.HashMap;

import org.nexus.utils.item.RSItem;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.script.MethodProvider;



public class RequiredInventory {
	
	private ArrayList<InventoryItem> items;
	
	public RequiredInventory() {
		items = new ArrayList<InventoryItem>();
	}
	
	public RequiredInventory addItem(InventoryItem item) {	
		items.add(item);
		return this;
	}
	
	public void addItem(RSItem item, int amount) {	
		items.add(new InventoryItem(amount, item, 1));
	}
	
	public ArrayList<InventoryItem> getItems(){
		return items;
	}
	public ArrayList<Integer> getItemIds(){
		ArrayList<Integer> itemIds = new ArrayList<Integer>();
		items.forEach(item -> {
			itemIds.add(item.getItem().getId());
		});
		return itemIds;
	}
	
	public ArrayList<String> getItemNames(){
		ArrayList<String> itemNames = new ArrayList<String>();
		items.forEach(item -> {
			itemNames.add(item.getItem().getName());
		});
		return itemNames;
	}
	
	
	public static boolean inventoryOnlyContainsRequiredItems(MethodProvider methodprovider, RequiredInventory inventory) {
		for(Item item : methodprovider.inventory.getItems()) {
			if(item != null && !inventory.getItemIds().contains(item.getId())) {
				return false;
			}
		}
		return true;
	}

	public InventoryItem find(int itemID) {
		for(InventoryItem item : items) {
			if(item.getItem().getId() == itemID) {
				return item;
			}
		}
		
		return null;
	}

	public RequiredInventory setItems(ArrayList<InventoryItem> arrayList) {
		items = arrayList;
		return this;
	}
}
