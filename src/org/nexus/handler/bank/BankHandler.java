package org.nexus.handler.bank;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.nexus.handler.Handler;
import org.nexus.node.Node;
import org.nexus.node.bank.Deposit;
import org.nexus.node.bank.OpenBank;
import org.nexus.node.bank.Withdraw;
import org.nexus.node.mule.CheckIfWeShallSellItems;
import org.nexus.utils.inventory.RequiredInventory;
import org.nexus.utils.item.DepositItem;
import org.nexus.utils.item.WithdrawItem;
import org.nexus.utils.item.DepositItem.DepositType;
import org.osbot.rs07.Bot;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.script.MethodProvider;

public abstract class BankHandler extends Handler {
	public OpenBank openBankNode = new OpenBank();
	public Withdraw withdrawNode = new Withdraw();
	public Deposit depositNode = new Deposit();
	public CheckIfWeShallSellItems checkIfSellItemNode = new CheckIfWeShallSellItems();

	private int[] stockArr;
	@Override
	public MethodProvider exchangeContext(Bot bot) {
		this.openBankNode.exchangeContext(bot);
		this.withdrawNode.exchangeContext(bot);
		this.depositNode.exchangeContext(bot);
		this.checkIfSellItemNode.exchangeContext(bot);
		return super.exchangeContext(bot);
	}




	protected boolean bankIsOpen() {
		return bank.isOpen();
	}

	public static boolean bankIsOpen(MethodProvider methodProvider) {
		return methodProvider.bank.isOpen();
	}




}
