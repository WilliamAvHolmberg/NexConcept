package org.nexus.utils.item;

import org.nexus.utils.Exchange;

public class RSItem {
	private String name;
	private int id;

	public RSItem(String name, int id) {
		this.setName(name);
		this.setId(id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getItemPrice() {
		return Exchange.getPrice(id, null);
	}

}
