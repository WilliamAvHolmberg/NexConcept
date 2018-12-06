package org.nexus.node.bank;

import org.nexus.node.Node;
import org.nexus.utils.Sleep;

public class OpenBank extends Node{


	@Override
	public int onLoop() {
		try {
			bank.open();
			Sleep.sleepUntil(() -> bank.isOpen(), 2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return 200;
	}

	@Override
	public String toString() {
		return "Open Bank";
	}

}
