package org.nexus;

import java.awt.Graphics2D;

import org.nexus.communication.NexHelper;
import org.nexus.communication.message.NexMessage;
import org.nexus.event.login.LoginEvent;
import org.nexus.handler.TaskHandler;
import org.nexus.handler.bank.BankHandler;
import org.nexus.handler.bank.DepositItemHandler;
import org.nexus.handler.bank.WithdrawItemHandler;
import org.nexus.handler.gear.GearHandler;
import org.nexus.handler.grandexchange.BuyItemHandler;
import org.nexus.handler.grandexchange.SellItemHandler;
import org.nexus.loot.LootHandler;
import org.nexus.node.mule.CheckIfWeShallSellItems;
import org.nexus.task.Task;
import org.nexus.task.TaskType;
import org.nexus.task.mule.Mule;
import org.nexus.task.quests.CooksAssistantQuest;
import org.nexus.task.quests.GobinDiplomacyQuest;
import org.nexus.task.quests.RomeoAndJulietQuest;
import org.nexus.task.quests.tutorial.TutorialIsland;
import org.nexus.task.woodcutting.WoodcuttingTask;
import org.nexus.utils.Sleep;
import org.osbot.rs07.api.Client;
import org.osbot.rs07.api.Quests.Quest;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.api.ui.Message.MessageType;
import org.osbot.rs07.api.util.ExperienceTracker;
import org.osbot.rs07.constants.ResponseCode;
import org.osbot.rs07.listener.LoginResponseCodeListener;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

@ScriptManifest(author = "", info = "", logo = "", name = "NEX", version = 0)
public class Nex extends Script implements LoginResponseCodeListener{

	public static boolean SHOULD_RUN = true;
	public static Task CURRENT_TASK;
	// threshold for which items from bank that should be sold
	public static int item_theshold = 500;
	// threshold for when player should mule. If bank value exceeds the threshold.
	public static int mule_threshold = 30000;
	public static long SLEEP_UNTIL = 0;
	GearHandler gearHandler;
	DepositItemHandler depositItemHandler;
	WithdrawItemHandler withdrawItemHandler;
	SellItemHandler sellItemHandler;
	BuyItemHandler buyItemHandler;
	public static ExperienceTracker experienceTracker;
	NexHelper helper;
	public static String ingameUsername = "";

	@Override
	public void onStart() throws InterruptedException {
		Sleep.sleep(10000);
		super.onStart();
		if (client.getUsername() == null) {
			log("no username, lets quit");
			stop();
		}

		helper = new NexHelper(bot.getUsername(), "ugot00wned2", CURRENT_TASK);
		helper.exchangeContext(bot);
		gearHandler = new GearHandler();
		gearHandler.exchangeContext(bot);
		depositItemHandler = new DepositItemHandler();
		depositItemHandler.exchangeContext(bot);
		withdrawItemHandler = new WithdrawItemHandler();
		withdrawItemHandler.exchangeContext(bot);
		sellItemHandler = new SellItemHandler();
		sellItemHandler.exchangeContext(bot);
		buyItemHandler = new BuyItemHandler();
		buyItemHandler.exchangeContext(bot);
		Thread thread = new Thread(helper);
		thread.start();

		this.experienceTracker = getExperienceTracker();
		experienceTracker.startAll();

	}

	@Override
	public int onLoop() throws InterruptedException {
		if (helper.secondsSinceLastLog() > 420) {
			log("we have to break because we are not connected to serv.");
			System.exit(1);
		}
		if (!isloggedIn()) {
			login();
		} else if (CURRENT_TASK == null) {
			getNewTask();
		} else if (CURRENT_TASK.isFinished()) {
			log("Task completed");
			CURRENT_TASK.removeTask();
			LootHandler.reset();
		} else if (!DepositItemHandler.itemsToDeposit.isEmpty()) { // if should bank
			return depositItemHandler.onLoop();
		} else if (CURRENT_TASK.getTaskType() == TaskType.WITHDRAW_ITEM_FROM_MULE) {
			log("here");
			return CURRENT_TASK.onLoop();
		} else if (!SellItemHandler.items.isEmpty()) { // if should bank
			return sellItemHandler.onLoop();
		} else if (!BuyItemHandler.items.isEmpty()) {
			return buyItemHandler.onLoop();
		} else if (!WithdrawItemHandler.itemsToWithdraw.isEmpty()) { // if should bank
			return withdrawItemHandler.onLoop();
		} else if (!GearHandler.itemsToEquip.isEmpty()) { // if should equip
			return gearHandler.onLoop();
		} else {

			return CURRENT_TASK.onLoop();
		}
		return 200;
	}

	private void getNewTask() {
		if (!TaskHandler.available_tasks.isEmpty()) {
			CURRENT_TASK = TaskHandler.available_tasks.pop();
		} else if (!TutorialIsland.isTutorialIslandCompleted(this)) {
			CURRENT_TASK = new TutorialIsland();
		} else {
			helper.getNewTask();
			Sleep.sleepUntil(() -> TaskHandler.getCurrentTask() != null || !TaskHandler.available_tasks.isEmpty(), 30000);
		}
		if (CURRENT_TASK != null) {
			CURRENT_TASK.exchangeContext(bot);
		}

	}

	private void login() {
		LoginEvent logEvent = new LoginEvent(this, bot.getUsername(), "ugot00wned2", this);
		execute(logEvent);
	}

	private boolean isloggedIn() {
		return !getClient().getLoginState().equals(Client.LoginState.LOGGED_OUT);
	}

	@Override
	public void onExit() throws InterruptedException {
		SHOULD_RUN = false;
	}

	@Override
	public void onPaint(Graphics2D g) {
		if (CURRENT_TASK != null) {
			g.drawString(CURRENT_TASK.getClass().getSimpleName(), 10, 10 );
			CURRENT_TASK.onPaint(g);
		}
		if (SLEEP_UNTIL > System.currentTimeMillis()) {
			g.drawString("Sleeping for " + (SLEEP_UNTIL - System.currentTimeMillis()) + " more millis", 10, 35);

		}

		g.drawString("Next Mule check: " + CheckIfWeShallSellItems.getTimeTilNextCheckInMinutes(), 10, 60);
		g.drawString("Last server log: " + helper.secondsSinceLastLog(), 10, 85);
		int x = 250;
		for(NexMessage mess: NexHelper.getQueue()) {
			g.drawString(mess.getClass().getSimpleName(), 10,x);
			x+= 25;
		}
		
		for(Task task: TaskHandler.available_tasks) {
			g.drawString(task.getClass().getSimpleName(), 10,x);
			x+= 25;
		}
	}

	@Override
	public void onMessage(Message message) throws InterruptedException {
		String txt = message.getMessage().toLowerCase();

		if (message.getType() == MessageType.GAME && txt.equals("accepted trade.") && CURRENT_TASK != null
				&& (CURRENT_TASK.getTaskType() == TaskType.DEPOSIT_ITEM_TO_PLAYER
						|| CURRENT_TASK.getTaskType() == TaskType.WITHDRAW_ITEM_FROM_MULE)) {
			((Mule) CURRENT_TASK).tradeIsCompleted = true;
			log("trade is completed from onmessage");
			log("mess:" + message.getMessage());
			log("message type " + message.getType());
		}

		if (message.getMessage().contains("logs.") && CURRENT_TASK.getTaskType() == TaskType.WOODCUTTING) {
			LootHandler.addLoot(((WoodcuttingTask) CURRENT_TASK).getTreeLog());
		}

	}
	
	@Override
	public void onResponseCode(int response) throws InterruptedException {
		log("respone code: " + response);
		if(response == 5) {
			log("already logged in");
			System.exit(1);
		}
		if (response == ResponseCode.ACCOUNT_DISABLED) {
			log("BANNED");
		}

	}

	public static int perHour(int profit) {
		if (CURRENT_TASK != null) {
			long elapsedTimeMs = System.currentTimeMillis() - CURRENT_TASK.getTimeStartedInMilli();
			return (int) (profit * (3600000.0 / elapsedTimeMs));
		} else {
			return 0;
		}
	}

}
