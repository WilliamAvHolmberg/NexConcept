package org.nexus.handler.grandexchange;

import java.util.Arrays;
import java.util.Stack;

import org.nexus.handler.Handler;
import org.nexus.handler.bank.BankHandler;
import org.nexus.handler.bank.DepositItemHandler;
import org.nexus.handler.bank.WithdrawItemHandler;
import org.nexus.node.Node;
import org.nexus.node.bank.Withdraw;
import org.nexus.node.ge.BuyItem;
import org.nexus.node.ge.HandleCoins;
import org.nexus.task.TaskType;
import org.nexus.utils.WebBank;
import org.nexus.utils.item.DepositItem;
import org.nexus.utils.item.GEItem;
import org.nexus.utils.item.WithdrawItem;
import org.nexus.utils.item.DepositItem.DepositType;
import org.osbot.rs07.Bot;
import org.osbot.rs07.script.MethodProvider;

public class BuyItemHandler extends Handler {
	public static Stack<GEItem> items = new Stack<GEItem>();
	private GEItem geItem;
	private WithdrawItem withdrawItem;
	public HandleCoins handleCoins = new HandleCoins();
	public BuyItem buyItemNode = new BuyItem();

	@Override
	public MethodProvider exchangeContext(Bot bot) {
		handleCoins.exchangeContext(bot);
		buyItemNode.exchangeContext(bot);
		return super.exchangeContext(bot);
	}

	private boolean purchaseAmountIsWrong(GEItem geItem, WithdrawItem withdrawItem) {
		return bank.isOpen() && (withdrawItem.getAmount() - bank.getAmount(geItem.getItemID())) != geItem.getAmount();
	}

	private boolean purchaseIsCompleted(GEItem geItem, WithdrawItem withdrawItem) {
		return bank.isOpen() && bank.getAmount(geItem.getItemID()) >= withdrawItem.getAmount()
				|| inventory.getAmount(geItem.getItemName()) >= withdrawItem.getAmount();
	}

	public static void addItem(GEItem item) {
		items.add(item);
	}

	public static void removeItem(GEItem item) {
		items.remove(item);
	}

	public static GEItem getItem() {
		if (items.isEmpty()) {
			return null;
		}
		return items.peek();
	}

	private boolean playerAtGrandExchange() {
		return WebBank.GRAND_EXCHANGE.getArea().contains(myPosition());
	}

	@Override
	public int onLoop() {
		geItem = items.peek();
		log(geItem.getTotalPrice());
		withdrawItem = WithdrawItemHandler.getWithdrawItem();
		if (purchaseIsCompleted(geItem, withdrawItem)) {
			log("done!");
			DepositItemHandler.addItem(new DepositItem(DepositType.DEPOSIT_ALL, null));
			BuyItemHandler.removeItem(geItem);
		} else if (purchaseAmountIsWrong(geItem, withdrawItem)) {
			log("wrong amount");
			geItem.setAmount((int) (withdrawItem.getAmount() - bank.getAmount(geItem.getItemID())));
		} else if (!playerAtGrandExchange()) {
			return walkToAreaNode.setArea(WebBank.GRAND_EXCHANGE.getArea());
		} else if (!inventory.onlyContains(995)) {
			return deposit.setItem(new DepositItem(DepositType.DEPOSIT_ALL_EXCEPT, 995));
		} else if (inventory.getAmount(995) < geItem.getTotalPrice()) {
			return withdraw.setItem(new WithdrawItem(995, geItem.getTotalPrice()));
		} else {
			return buyItemNode.setItem(geItem);
		}

		return 200;
	}




}
