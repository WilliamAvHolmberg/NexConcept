package org.nexus.task.mule;

import java.awt.Graphics2D;
import java.util.function.BooleanSupplier;

import org.nexus.Nex;
import org.nexus.node.mule.CheckIfWeShallSellItems;
import org.nexus.node.mule.DepositItemToPlayer;
import org.nexus.node.mule.PrepareForMuleDeposit;
import org.nexus.node.mule.WithdrawItemFromPlayer;
import org.nexus.task.Task;
import org.osbot.rs07.Bot;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.api.ui.Message.MessageType;
import org.osbot.rs07.listener.MessageListener;
import org.osbot.rs07.script.MethodProvider;

public abstract class Mule extends Task{
	
	protected int world;
	protected int itemID;
	protected int itemAmount;
	protected int startAmount;
	protected String tradeName;
	protected boolean banked = false;
	protected boolean soldItems = false;
	public boolean tradeIsCompleted;
	
	protected DepositItemToPlayer depositItemToPlayer;
	protected WithdrawItemFromPlayer withdrawItemFromPlayer;
	protected CheckIfWeShallSellItems sellEveryItemNode = new CheckIfWeShallSellItems();
	
	@Override
	public MethodProvider exchangeContext(Bot arg0) {
		depositItemToPlayer = new DepositItemToPlayer();
		withdrawItemFromPlayer = new WithdrawItemFromPlayer();
		withdrawItemFromPlayer.exchangeContext(arg0);
		return super.exchangeContext(arg0);
	}
	public Mule(int world, int itemID, int itemAmount, int startAmount, String muleName) {
		setWorld(world);
		setItemID(itemID);
		setItemAmount(itemAmount);
		setStartAmount(startAmount);
		setTradeName(muleName);
	}
	
	protected Player getMule(String name) {
		for (Player player : getPlayers().getAll()) {
			if (player.getName().toLowerCase().equals(name.toLowerCase())) {
				return player;
			}
		}
		return null;
	}
	


	public int getWorld() {
		return world;
	}

	public void setWorld(int world) {
		this.world = world;
	}

	public int getItemID() {
		return itemID;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}

	public int getItemAmount() {
		return itemAmount;
	}

	public void setItemAmount(int itemAmount) {
		this.itemAmount = itemAmount;
	}

	public int getStartAmount() {
		return startAmount;
	}

	public void setStartAmount(int startAmount) {
		this.startAmount = startAmount;
	}
	
	public boolean getBanked() {
		return banked;
	}
	
	public void setBanked(boolean bool) {
		this.banked = bool;
	}

	public String getTradeName() {
		return tradeName;
	}
	
	
	@Override
	public boolean isFinished() {
		if(tradeIsCompleted) {
			log("Trade completed from mess");
			return true;
		}	
		if (getTimeLeft() <= 0) {
			log("no time left");
			return true;
		}
		return false;

	}

	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}

	public void setSoldItems(boolean bool) {
		this.soldItems = bool;
	}
	public boolean soldItems() {
		return soldItems;
	}
	
	@Override
	public void removeTask() {
		// TODO send mule done with info
		Nex.CURRENT_TASK = null;
	}
	
	@Override
	public String getLog() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onPaint(Graphics2D g) {
		g.drawString("Trading mule: " + tradeName, 10, 100);
		g.drawString("Item " + itemID, 10, 125);
		g.drawString("Amount of item: " + itemAmount, 10, 150);
		
	}

	



}
