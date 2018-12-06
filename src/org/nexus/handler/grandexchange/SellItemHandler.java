package org.nexus.handler.grandexchange;

import java.util.Stack;

import org.nexus.handler.Handler;
import org.nexus.handler.bank.BankHandler;
import org.nexus.handler.bank.DepositItemHandler;
import org.nexus.node.Node;
import org.nexus.node.ge.BuyItem;
import org.nexus.node.ge.HandleCoins;
import org.nexus.node.ge.SellItem;
import org.nexus.utils.WebBank;
import org.nexus.utils.item.DepositItem;
import org.nexus.utils.item.GEItem;
import org.nexus.utils.item.GESellItem;
import org.nexus.utils.item.WithdrawItem;
import org.osbot.rs07.Bot;
import org.osbot.rs07.api.Bank.BankMode;
import org.osbot.rs07.script.MethodProvider;


public class SellItemHandler extends Handler {
	public static Stack<GESellItem> items = new Stack<GESellItem>();
	private GESellItem geItem;
	private WithdrawItem withdrawItem;
	public static HandleCoins handleCoins = new HandleCoins();
	public static BuyItem buyItemNode = new BuyItem();
	public static SellItem sellItemNode = new SellItem();
	@Override
	public MethodProvider exchangeContext(Bot bot) {
		handleCoins.exchangeContext(bot);
		buyItemNode.exchangeContext(bot);
		sellItemNode.exchangeContext(bot);
		return super.exchangeContext(bot);
	}
	public SellItemHandler() {
	}

	public int onLoop() {
		log("in ge handler");
			log("items not null");
			geItem = items.peek();
			log("we are gonna sell " + geItem.getItemName());
			//withdrawItem = BankHandler.getWithdrawItem();
			if (!playerAtGrandExchange()) {
				return walkToAreaNode.setArea(WebBank.GRAND_EXCHANGE.getArea());
			}else if(shouldDepositUnnecessaryItems()){
				log("lets deposit uneccessary");
				DepositItemHandler.addItem(new DepositItem(DepositItem.DepositType.DEPOSIT_ALL, null)); // + 1 because of noted
			}else if(!geItem.hasBeenWithdrawnFromBank()){
				log("item has not been withdrawn");
				return withdrawRequiredItem(geItem);
			}else if(!geItem.hasBeenSold()) {
				if(!inventory.contains(geItem.getItemName())) {
					geItem.setHasBeenSold(true);
					log("item has been sold");
					return 200;
				}else {
					log("lets sell item");
					return sellItemNode.setItem(geItem);
				}
			}
		
		return 200;
	}
	
	private boolean shouldDepositUnnecessaryItems() {
		return inventory.getEmptySlotCount() < 26;
	}

	private int withdrawRequiredItem(GESellItem geItem) {
		try {
			if (bank.open()) {
				if (!bank.contains(geItem.getItemID())) {
					geItem.setHasBeenWithdrawn(true);
					return onLoop();
				} else {
					log("lets return withdraw!");
					return withdraw.setItem(new WithdrawItem(geItem.getItemID(), (int) bank.getAmount(geItem.getItemID()), BankMode.WITHDRAW_NOTE));
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return withdrawRequiredItem(geItem);
	}

	public static void addItem(GESellItem item) {
		items.add(item);
	}

	public static void removeItem(GEItem item) {
		items.remove(item);
	}

	public static GESellItem getItem() {
		if (items.isEmpty()) {
			return null;
		}
		return items.peek();
	}

	private boolean playerAtGrandExchange() {
		return WebBank.GRAND_EXCHANGE.getArea().contains(myPosition());
	}





}
