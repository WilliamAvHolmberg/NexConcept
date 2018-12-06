package org.nexus.node.walking;

import java.util.List;

import org.nexus.node.Node;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.event.webwalk.PathPreferenceProfile;
import org.osbot.rs07.script.MethodProvider;

public class WalkToArea extends Node {

	private Area area;



	@Override
	public int onLoop() {
		if (widgets.closeOpenInterface()) {
			log("lets walk to webbank");
			WebWalkEvent event = new WebWalkEvent(area);

			event.setPathPreferenceProfile(PathPreferenceProfile.DEFAULT.setAllowTeleports(true));
			execute(event);
		}
		return 200;
	}

	@Override
	public String toString() {
		return "Walk To Location";
	}

	public int setArea(Area area) {
		this.area = area;
		return onLoop();
	}
}
