package com.rhetorical.cod.inventories;

import com.rhetorical.cod.Main;
import com.rhetorical.cod.assignments.Assignment;
import com.rhetorical.cod.assignments.AssignmentType;
import com.rhetorical.cod.game.GameManager;
import com.rhetorical.cod.lang.Lang;
import com.rhetorical.cod.loadouts.Loadout;
import com.rhetorical.cod.perks.CodPerk;
import com.rhetorical.cod.perks.Perk;
import com.rhetorical.cod.perks.PerkSlot;
import com.rhetorical.cod.progression.CreditManager;
import com.rhetorical.cod.progression.StatHandler;
import com.rhetorical.cod.streaks.KillStreak;
import com.rhetorical.cod.weapons.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static com.rhetorical.cod.Main.lastLoc;
import static com.rhetorical.cod.Main.lobbyLoc;

public class InventoryManager implements Listener {

	public ItemStack closeInv = new ItemStack(Material.BARRIER);
	public ItemStack backInv = new ItemStack(Material.REDSTONE);

	public Inventory mainInventory;
	public Inventory mainShopInventory;
	private Inventory leaderboardInventory;
	public HashMap<Player, Inventory> createClassInventory = new HashMap<>();
	private HashMap<Player, Inventory> selectClassInventory = new HashMap<>();
	private HashMap<Player, Inventory> personalStatistics = new HashMap<>();
	private HashMap<Player, Inventory> assignmentsInventory = new HashMap<>();
	private HashMap<Player, Inventory> mainKillStreakInventory = new HashMap<>();
	private HashMap<Player, Inventory> killStreakInventory1 = new HashMap<>();
	private HashMap<Player, Inventory> killStreakInventory2 = new HashMap<>();
	private HashMap<Player, Inventory> killStreakInventory3 = new HashMap<>();


	private ItemStack joinGame = new ItemStack(Material.EMERALD);
	private ItemStack createClass = new ItemStack(Material.CHEST);
	private ItemStack scoreStreaks = new ItemStack(Material.DIAMOND);
	private ItemStack combatRecord = new ItemStack(Material.PAPER);
	private ItemStack prestigeItem = new ItemStack(Material.ANVIL);
	private ItemStack assignmentsItem = new ItemStack(Material.GOLD_INGOT);
	private ItemStack leaderboard = new ItemStack(Material.PAPER);

	private ItemStack shopItem = new ItemStack(Material.EMERALD);
	private ItemStack gunShopItem = new ItemStack(Material.CHEST);
	private ItemStack grenadeShopItem = new ItemStack(Material.CHEST);
	private ItemStack perkShopItem = new ItemStack(Material.CHEST);

	public ItemStack codItem = new ItemStack(Material.ENDER_PEARL);
	public ItemStack leaveItem = new ItemStack(Material.BARRIER);

	public ItemStack voteItemA = new ItemStack(Material.PAPER);
	public ItemStack voteItemB = new ItemStack(Material.PAPER);

	public boolean shouldCancelClick(Inventory i, Player p) {
		if (i.equals(mainInventory)) {
			return true;
		}
		if (i.equals(p.getInventory()) && GameManager.isInMatch(p)) {
			return true;
		}
		if (i.equals(createClassInventory.get(p))) {
			return true;
		}

		for (Loadout loadout : Main.loadManager.getLoadouts(p)) {
			if (loadout.getPrimaryInventory().equals(i) || loadout.getSecondaryInventory().equals(i) || loadout.getLethalInventory().equals(i) || loadout.getTacticalInventory().equals(i) || loadout.getPerk1Inventory().equals(i) || loadout.getPerk2Inventory().equals(i) || loadout.getPerk3Inventory().equals(i)) {
				return true;
			}
		}

		return i.equals(this.selectClassInventory.get(p)) || i.equals(leaderboardInventory) || i.equals(personalStatistics.get(p)) || i.equals(mainShopInventory) || i.equals(Main.shopManager.gunShop.get(p)) || i.equals(Main.shopManager.weaponShop.get(p)) || i.equals(Main.shopManager.perkShop.get(p))
				|| i.equals(this.mainKillStreakInventory.get(p))
				|| i.equals(this.assignmentsInventory.get(p))
				|| i.equals(this.killStreakInventory1.get(p)) || i.equals(this.killStreakInventory2.get(p)) || i.equals(this.killStreakInventory3.get(p));

	}

	public InventoryManager() {

		mainInventory = Bukkit.createInventory(null, 27, Lang.INVENTORY_MAIN_MENU_TITLE.getMessage());
		mainShopInventory = Bukkit.createInventory(null, 9, Lang.INVENTORY_SHOP_MENU_TITLE.getMessage());
		leaderboardInventory = Bukkit.createInventory(null, 36, Lang.INVENTORY_LEADERBOARD_MENU_TITLE.getMessage());

		setupStaticItems();
		setupMainInventories();

		Bukkit.getServer().getPluginManager().registerEvents(this, Main.getPlugin());
	}

	private void setupStaticItems() {
		ItemMeta closeInvMeta = closeInv.getItemMeta();
		closeInvMeta.setDisplayName(Lang.INVENTORY_CLOSE_BUTTON_NAME.getMessage());
		closeInv.setItemMeta(closeInvMeta);

		ItemMeta leaveMeta = leaveItem.getItemMeta();
		leaveMeta.setDisplayName(Lang.INVENTORY_LEAVE_GAME_NAME.getMessage());
		List<String> leaveLore = new ArrayList<>();
		leaveLore.add(Lang.INVENTORY_LEAVE_GAME_LORE.getMessage());
		leaveMeta.setLore(leaveLore);
		leaveItem.setItemMeta(leaveMeta);

		ItemMeta codMeta = codItem.getItemMeta();
		codMeta.setDisplayName(Lang.INVENTORY_OPEN_MENU_NAME.getMessage());
		List<String> codLore = new ArrayList<>();
		codLore.add(Lang.INVENTORY_OPEN_MENU_LORE.getMessage());
		codMeta.setLore(codLore);
		codItem.setItemMeta(codMeta);

		ItemMeta backInvMeta = backInv.getItemMeta();
		backInvMeta.setDisplayName(Lang.INVENTORY_BACK_BUTTON_NAME.getMessage());
		backInv.setItemMeta(backInvMeta);

		{
			ItemMeta voteMeta = voteItemA.getItemMeta();
			voteMeta.setDisplayName(Lang.INVENTORY_VOTE_MAP_ONE_NAME.getMessage());
			List<String> voteLore = new ArrayList<>();
			codLore.add(Lang.INVENTORY_VOTE_MAP_ONE_LORE.getMessage());
			voteMeta.setLore(voteLore);
			voteItemA.setItemMeta(voteMeta);
		}

		{
			ItemMeta voteMeta = voteItemB.getItemMeta();
			voteMeta.setDisplayName(Lang.INVENTORY_VOTE_MAP_TWO_NAME.getMessage());
			List<String> voteLore = new ArrayList<>();
			codLore.add(Lang.INVENTORY_VOTE_MAP_TWO_LORE.getMessage());
			voteMeta.setLore(voteLore);
			voteItemB.setItemMeta(voteMeta);
		}
	}

	// Main Inventory

	private void setupMainInventories() {
		joinGame = new ItemStack(Material.EMERALD);
		ItemMeta joinGameMeta = joinGame.getItemMeta();
		joinGameMeta.setDisplayName(Lang.INVENTORY_JOIN_GAME_NAME.getMessage());
		ArrayList<String> joinGameLore = new ArrayList<>();
		joinGameLore.add(Lang.INVENTORY_JOIN_GAME_LORE.getMessage());
		joinGameMeta.setLore(joinGameLore);
		joinGame.setItemMeta(joinGameMeta);

		mainInventory.setItem(13, joinGame);

		createClass = new ItemStack(Material.CHEST);
		ItemMeta createClassMeta = createClass.getItemMeta();
		createClassMeta.setDisplayName(Lang.INVENTORY_CREATE_A_CLASS_NAME.getMessage());
		ArrayList<String> createClassLore = new ArrayList<>();
		createClassLore.add(Lang.INVENTORY_CREATE_A_CLASS_LORE.getMessage());
		createClassMeta.setLore(createClassLore);
		createClass.setItemMeta(createClassMeta);

		mainInventory.setItem(1, createClass);

		scoreStreaks = new ItemStack(Material.DIAMOND);
		ItemMeta scoreStreakMeta = scoreStreaks.getItemMeta();
		scoreStreakMeta.setDisplayName(Lang.INVENTORY_SCORESTREAKS_NAME.getMessage());
		ArrayList<String> scoreStreakLore = new ArrayList<>();
		scoreStreakLore.add(Lang.INVENTORY_SCORESTREAKS_LORE.getMessage());
		scoreStreakMeta.setLore(scoreStreakLore);
		scoreStreaks.setItemMeta(scoreStreakMeta);

		mainInventory.setItem(11, scoreStreaks);

		prestigeItem = new ItemStack(Material.ANVIL);
		ItemMeta prestigeMeta = prestigeItem.getItemMeta();
		prestigeMeta.setDisplayName(Lang.INVENTORY_PRESTIGE_NAME.getMessage());
		ArrayList<String> prestigeLore = new ArrayList<>();
		prestigeLore.add(Lang.INVENTORY_PRESTIGE_LORE.getMessage());
		prestigeMeta.setLore(prestigeLore);
		prestigeItem.setItemMeta(prestigeMeta);

		mainInventory.setItem(18, prestigeItem);

		assignmentsItem = new ItemStack(Material.GOLD_INGOT);
		ItemMeta assignmentMeta = assignmentsItem.getItemMeta();
		assignmentMeta.setDisplayName(Lang.INVENTORY_ASSIGNMENTS_NAME.getMessage());
		ArrayList<String> assignmentLore = new ArrayList<>();
		assignmentLore.add(Lang.INVENTORY_ASSIGNMENTS_LORE.getMessage());
		assignmentMeta.setLore(assignmentLore);
		assignmentsItem.setItemMeta(assignmentMeta);

		mainInventory.setItem(7, assignmentsItem);

		combatRecord = new ItemStack(Material.PAPER);
		ItemMeta combatRecordMeta = combatRecord.getItemMeta();
		combatRecordMeta.setDisplayName(Lang.INVENTORY_RECORD_NAME.getMessage());
		ArrayList<String> combatRecordLore = new ArrayList<>();
		combatRecordLore.add(Lang.INVENTORY_RECORD_LORE.getMessage());
		combatRecordMeta.setLore(combatRecordLore);
		combatRecord.setItemMeta(combatRecordMeta);

		mainInventory.setItem(5, combatRecord);

		leaderboard = new ItemStack(Material.PAPER);
		ItemMeta leaderboardMeta = leaderboard.getItemMeta();
		leaderboardMeta.setDisplayName(Lang.INVENTORY_BOARDS_NAME.getMessage());
		ArrayList<String> leaderboardLore = new ArrayList<>();
		leaderboardLore.add(Lang.INVENTORY_BOARDS_LORE.getMessage());
		leaderboardMeta.setLore(leaderboardLore);
		leaderboard.setItemMeta(leaderboardMeta);

		mainInventory.setItem(15, leaderboard);

		mainInventory.setItem(26, closeInv);

		ItemStack shop = shopItem;
		ItemMeta shopMeta = shop.getItemMeta();
		shopMeta.setDisplayName(Lang.INVENTORY_SHOP_NAME.getMessage());
		ArrayList<String> shopLore = new ArrayList<>();
		shopLore.add(Lang.INVENTORY_SHOP_LORE.getMessage());
		shopMeta.setLore(shopLore);
		shop.setItemMeta(shopMeta);

		shopItem = shop;

		mainInventory.setItem(3, shopItem);

		ItemStack gunItem;
		try {
			gunItem = new ItemStack(Material.valueOf("WOODEN_HOE"));
		} catch(Exception ignored) {
			gunItem = new ItemStack(Material.valueOf("WOOD_HOE"));
		}
		ItemMeta gunMeta = gunItem.getItemMeta();
		gunMeta.setDisplayName(Lang.INVENTORY_GUN_SHOP_NAME.getMessage());
		ArrayList<String> gunLore = new ArrayList<>();
		gunLore.add(Lang.INVENTORY_GUN_SHOP_LORE.getMessage());
		gunMeta.setLore(gunLore);
		gunItem.setItemMeta(gunMeta);

		gunShopItem = gunItem;

		ItemStack grenadeItem = new ItemStack(Material.SLIME_BALL);
		ItemMeta grenadeMeta = grenadeItem.getItemMeta();
		grenadeMeta.setDisplayName(Lang.INVENTORY_GRENADE_SHOP_NAME.getMessage());
		ArrayList<String> grenadeLore = new ArrayList<>();
		grenadeLore.add(Lang.INVENTORY_GRENADE_SHOP_LORE.getMessage());
		gunMeta.setLore(grenadeLore);
		grenadeItem.setItemMeta(grenadeMeta);

		grenadeShopItem = grenadeItem;

		ItemStack perkItem = new ItemStack(Material.APPLE);
		ItemMeta perkMeta = perkItem.getItemMeta();
		perkMeta.setDisplayName(Lang.INVENTORY_PERK_SHOP_NAME.getMessage());
		ArrayList<String> perkLore = new ArrayList<>();
		perkLore.add(Lang.INVENTORY_PERK_SHOP_LORE.getMessage());
		perkMeta.setLore(perkLore);
		perkItem.setItemMeta(perkMeta);

		perkShopItem = perkItem;

		mainShopInventory.setItem(0, gunShopItem);
		mainShopInventory.setItem(1, grenadeShopItem);
		mainShopInventory.setItem(2, perkShopItem);
		mainShopInventory.setItem(8, backInv);
	}

	public void setupCreateClassInventory(Player p) {

		Main.loadManager.load(p);

		Main.shopManager.loadPurchaseData(p);

		Inventory customClassInventory = Bukkit.createInventory(p, 9 * Main.loadManager.getAllowedClasses(p), Lang.INVENTORY_CREATE_A_CLASS_NAME.getMessage());

		int line;

		for (int i = 0; i < Main.loadManager.getAllowedClasses(p); i++) {
			line = i * 9;

			Loadout loadout = Main.loadManager.getLoadouts(p).get(i);

			ItemStack header;
			try {
				header = new ItemStack(Material.valueOf("CRAFTING_TABLE"));
			} catch(Exception e) {
				header = new ItemStack(Material.valueOf("WORKBENCH"));
			}
			ItemMeta headerMeta = header.getItemMeta();
			headerMeta.setDisplayName(loadout.getName());
			ArrayList<String> headerLore = new ArrayList<>();
			headerLore.add(Lang.INVENTORY_LOADOUT_HEADER_LORE.getMessage());
			headerMeta.setLore(headerLore);
			header.setItemMeta(headerMeta);

			ItemStack primary = loadout.getPrimary().getGun();
			ItemMeta primaryMeta = primary.getItemMeta();
			primaryMeta.setDisplayName(Lang.INVENTORY_PRIMARY_WEAPON_NAME.getMessage().replace("{gun}", loadout.getPrimary().getName()));
			ArrayList<String> primaryLore = new ArrayList<>();
			primaryLore.add(Lang.INVENTORY_PRIMARY_WEAPON_LORE.getMessage());
			primaryMeta.setLore(primaryLore);
			primary.setItemMeta(primaryMeta);

			ItemStack secondary = loadout.getSecondary().getGun();
			ItemMeta secondaryMeta = secondary.getItemMeta();
			secondaryMeta.setDisplayName(Lang.INVENTORY_SECONDARY_WEAPON_NAME.getMessage().replace("{gun}", loadout.getSecondary().getName()));
			ArrayList<String> secondaryLore = new ArrayList<>();
			secondaryLore.add(Lang.INVENTORY_SECONDARY_WEAPON_LORE.getMessage());
			secondaryMeta.setLore(secondaryLore);
			secondary.setItemMeta(secondaryMeta);

			ItemStack lethal = loadout.getLethal().getWeapon();
			ItemMeta lethalMeta = lethal.getItemMeta();
			lethalMeta.setDisplayName(Lang.INVENTORY_LETHAL_WEAPON_NAME.getMessage().replace("{gun}", loadout.getLethal().getName()));
			ArrayList<String> lethalLore = new ArrayList<>();
			lethalLore.add(Lang.INVENTORY_LETHAL_WEAPON_LORE.getMessage());
			lethalMeta.setLore(lethalLore);
			lethal.setItemMeta(lethalMeta);

			ItemStack tactical = loadout.getTactical().getWeapon();
			ItemMeta tacticalMeta = tactical.getItemMeta();
			tacticalMeta.setDisplayName(Lang.INVENTORY_TACTICAL_WEAPON_NAME.getMessage().replace("{gun}", loadout.getTactical().getName()));
			ArrayList<String> tacticalLore = new ArrayList<>();
			tacticalLore.add(Lang.INVENTORY_TACTICAL_WEAPON_LORE.getMessage());
			tacticalMeta.setLore(tacticalLore);
			tactical.setItemMeta(tacticalMeta);

			ItemStack perkOne = loadout.getPerk1().getItem();
			ItemMeta perkOneMeta = perkOne.getItemMeta();
			perkOneMeta.setDisplayName(Lang.INVENTORY_PERK_NAME.getMessage().replace("{i}", "1").replace("{perk}", loadout.getPerk1().getPerk().getName()));
			perkOneMeta.setLore(loadout.getPerk1().getLore());
			perkOne.setItemMeta(perkOneMeta);

			ItemStack perkTwo = loadout.getPerk2().getItem();
			ItemMeta perkTwoMeta = perkTwo.getItemMeta();
			perkTwoMeta.setDisplayName(Lang.INVENTORY_PERK_NAME.getMessage().replace("{i}", "2").replace("{perk}", loadout.getPerk2().getPerk().getName()));
			perkTwoMeta.setLore(loadout.getPerk2().getLore());
			perkTwo.setItemMeta(perkTwoMeta);

			ItemStack perkThree = loadout.getPerk3().getItem();
			ItemMeta perkThreeMeta = perkThree.getItemMeta();
			perkThreeMeta.setDisplayName(Lang.INVENTORY_PERK_NAME.getMessage().replace("{i}", "3").replace("{perk}", loadout.getPerk3().getPerk().getName()));
			perkThreeMeta.setLore(loadout.getPerk3().getLore());
			perkThree.setItemMeta(perkThreeMeta);

			customClassInventory.setItem(line, header);
			customClassInventory.setItem(line + 1, primary);
			customClassInventory.setItem(line + 2, secondary);
			customClassInventory.setItem(line + 3, lethal);
			customClassInventory.setItem(line + 4, tactical);
			customClassInventory.setItem(line + 5, perkOne);
			customClassInventory.setItem(line + 6, perkTwo);
			customClassInventory.setItem(line + 7, perkThree);
			customClassInventory.setItem(line + 8, backInv);

			createClassInventory.put(p, customClassInventory);

		}

	}

	public void setupPlayerSelectionInventories(Player p) {
		for (Loadout loadout : Main.loadManager.getLoadouts(p)) {

			Main.shopManager.loadPurchaseData(p);

			Inventory primary = Bukkit.createInventory(p, 36, "Primary Weapons");
			Inventory secondary = Bukkit.createInventory(p, 36, "Secondary Weapons");
			Inventory lethal = Bukkit.createInventory(p, 27, "Lethal Grenades");
			Inventory tactical = Bukkit.createInventory(p, 27, "Tactical Grenades");
			Inventory perk1 = Bukkit.createInventory(p, 27, "Perk One");
			Inventory perk2 = Bukkit.createInventory(p, 27, "Perk Two");
			Inventory perk3 = Bukkit.createInventory(p, 27, "Perk Three");

			// primary.addItem(Main.loadManager.getDefaultPrimary().getGun());
			// secondary.addItem(Main.loadManager.getDefaultSecondary().getGun());
			// lethal.addItem(Main.loadManager.getDefaultLethal().getWeapon());
			// tactical.addItem(Main.loadManager.getDefaultTactical().getWeapon());

			for (CodGun gun : Main.shopManager.getPurchasedGuns().get(p)) {
				if (gun.getGunType() == GunType.Primary) {
					primary.addItem(gun.getGun());
				} else {
					secondary.addItem(gun.getGun());
				}
			}

			for (CodWeapon weapon : Main.shopManager.getPurchasedWeapons().get(p)) {
				if (weapon.getWeaponType() == WeaponType.LETHAL) {
					lethal.addItem(weapon.getWeapon());
				} else {
					tactical.addItem(weapon.getWeapon());
				}
			}

			for (CodPerk perk : Main.shopManager.getPerks(p)) {
				if (perk.getSlot().equals(PerkSlot.ONE)) {
					perk1.addItem(perk.getItem());
				} else if (perk.getSlot().equals(PerkSlot.TWO)) {
					perk2.addItem(perk.getItem());
				} else if (perk.getSlot().equals(PerkSlot.THREE)) {
					perk3.addItem(perk.getItem());
				}
			}

			primary.setItem(35, backInv);
			secondary.setItem(35, backInv);
			lethal.setItem(26, backInv);
			tactical.setItem(26, backInv);
			perk1.setItem(26, backInv);
			perk2.setItem(26, backInv);
			perk3.setItem(26, backInv);

			loadout.setPrimaryInventory(primary);
			loadout.setSecondaryInventory(secondary);
			loadout.setLethalInventory(lethal);
			loadout.setTacticalInventory(tactical);
			loadout.setPerkInventory(0, perk1);
			loadout.setPerkInventory(1, perk2);
			loadout.setPerkInventory(2, perk3);
		}
	}

	private void setupAssignmentsInventory(Player p) {
		List<Assignment> assignments = Main.assignmentManager.getAssignments(p);

		Inventory inventory = Bukkit.createInventory(null, 27, Lang.INVENTORY_ASSIGNMENTS_NAME.getMessage());

		for (int i = 0; i < assignments.size(); i++) {
			Assignment assignment = assignments.get(i);

			Material material;

			if (assignment.getRequirement().getAssignmentType() == AssignmentType.KILLS) {
				material = Material.ARROW;
			} else if (assignment.getRequirement().getAssignmentType() == AssignmentType.PLAY_MODE) {
				try {
					material = Material.valueOf("EXPERIENCE_BOTTLE");
				} catch(Exception e) {
					material = Material.valueOf("EXP_BOTTLE");
				}
			} else {
				material = Material.GOLD_INGOT;
			}
			ItemStack item = new ItemStack(material);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(Lang.ASSIGNMENT_HEADER.getMessage());
			List<String> lore = new ArrayList<>();
			lore.add(Lang.ASSIGNMENT_TYPE.getMessage().replace("{type}", assignment.getRequirement().getAssignmentType().toString()));
			lore.add(Lang.ASSIGNMENT_AMOUNT.getMessage().replace("{amount}", assignment.getRequirement().getRequired () + ""));
			lore.add(Lang.ASSIGNMENT_REWARD.getMessage().replace("{amount}", assignment.getReward() + ""));
			meta.setLore(lore);
			item.setItemMeta(meta);

			if (i == 0) {
				inventory.setItem(11, item);
			} else if (i == 1) {
				inventory.setItem(13, item);
			} else if (i == 2) {
				inventory.setItem(15, item);
			}

			inventory.setItem(26, backInv);
		}

		assignmentsInventory.put(p, inventory);
	}

	public void openAssignmentsInventory(Player p) {
		setupAssignmentsInventory(p);
		p.openInventory(assignmentsInventory.get(p));
	}

	private void setupShopInventories(Player p) {
		Inventory gunShop = Bukkit.createInventory(p, 36, Lang.INVENTORY_GUN_SHOP_NAME.getMessage());
		Inventory weaponShop = Bukkit.createInventory(p, 36, Lang.INVENTORY_GRENADE_SHOP_NAME.getMessage());
		Inventory perkShop = Bukkit.createInventory(p, 36, Lang.INVENTORY_PERK_SHOP_NAME.getMessage());

		ArrayList<CodGun> guns = Main.shopManager.getPrimaryGuns();
		guns.addAll(Main.shopManager.getSecondaryGuns());

		for (CodGun gun : guns) {
			if (gun.getType() == UnlockType.BOTH || gun.getType() == UnlockType.CREDITS) {
				if ((gun.getType() == UnlockType.CREDITS || Main.progressionManager.getLevel(p) >= gun.getLevelUnlock()) && !Main.shopManager.getPurchasedGuns().get(p).contains(gun)) {

					ItemStack item = gun.getGun();

					ItemMeta gunMeta = item.getItemMeta();

					ArrayList<String> lore = new ArrayList<>();

					lore.add(Lang.SHOP_COST + ": " + gun.getCreditUnlock());

					gunMeta.setLore(lore);

					item.setItemMeta(gunMeta);

					gunShop.addItem(item);

				}

			}

		}

		ArrayList<CodWeapon> grenades = Main.shopManager.getLethalWeapons();
		grenades.addAll(Main.shopManager.getTacticalWeapons());

		for (CodWeapon grenade : grenades) {
			if (grenade == null)
				continue;

			if (grenade.getType() == UnlockType.BOTH || grenade.getType() == UnlockType.CREDITS) {
				if ((grenade.getType() == UnlockType.CREDITS || Main.progressionManager.getLevel(p) >= grenade.getLevelUnlock()) && !Main.shopManager.getPurchasedWeapons().get(p).contains(grenade)) {

					ItemStack item = grenade.getWeapon();

					ItemMeta gunMeta = item.getItemMeta();

					ArrayList<String> lore = new ArrayList<>();

					lore.add(Lang.SHOP_COST.getMessage() + ": " + grenade.getCreditUnlock());

					gunMeta.setLore(lore);

					item.setItemMeta(gunMeta);

					if (!weaponShop.contains(item))
						weaponShop.addItem(item);

				}

			}

		}

		ArrayList<CodPerk> perks = Main.perkManager.getAvailablePerks();
		for (CodPerk perk : perks) {

			if (perk.getPerk().getName().equals(Main.perkManager.getDefaultOne().getPerk().getName()) || perk.getPerk().getName().equals(Main.perkManager.getDefaultTwo().getPerk().getName()) || perk.getPerk().getName().equals(Main.perkManager.getDefaultThree().getPerk().getName())) {
				continue;
			}

			Main.shopManager.purchasedPerks.computeIfAbsent(p, k -> new ArrayList<>());

			if (!Main.shopManager.purchasedPerks.get(p).contains(perk) && !perk.equals(Main.perkManager.getDefaultOne()) && !perk.equals(Main.perkManager.getDefaultTwo()) && !perk.equals(Main.perkManager.getDefaultThree())) {
				ItemStack item = perk.getItem();
				ItemMeta perkMeta = item.getItemMeta();
				if (perkMeta.getLore() == null) {
					perkMeta.setLore(new ArrayList<>());
				}
				ArrayList<String> lore = (ArrayList<String>) perkMeta.getLore();
				lore.add(Lang.SHOP_COST.getMessage() + ": " + perk.getCost());
				lore.add(Lang.PERK_SLOT.getMessage() + ": " + perk.getSlot().toString());
				perkMeta.setLore(lore);
				item.setItemMeta(perkMeta);

				perkShop.addItem(item);
			}

		}

		gunShop.setItem(35, backInv);
		weaponShop.setItem(35, backInv);
		perkShop.setItem(35, backInv);

		Main.shopManager.gunShop.put(p, gunShop);
		Main.shopManager.weaponShop.put(p, weaponShop);
		Main.shopManager.perkShop.put(p, perkShop);
	}

	private void setupSelectClassInventory(Player p) {

		Inventory inventory = Bukkit.createInventory(p, 9, Lang.INVENTORY_SELECT_CLASS_TITLE.getMessage());

		for (int i = 0; i < Main.loadManager.getAllowedClasses(p); i++) {

			Loadout loadout = Main.loadManager.getLoadouts(p).get(i);

			ItemStack item = loadout.getPrimary().getGun();

			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(loadout.getName());

			ArrayList<String> lore = new ArrayList<>();
			lore.add(Lang.INVENTORY_SELECT_CLASS_PRIMARY.getMessage() + ": " + loadout.getPrimary().getName());
			lore.add(Lang.INVENTORY_SELECT_CLASS_SECONDARY.getMessage() + ": " + loadout.getSecondary().getName());
			lore.add(Lang.INVENTORY_SELECT_CLASS_LETHAL.getMessage() + ": " + loadout.getLethal().getName());
			lore.add(Lang.INVENTORY_SELECT_CLASS_TACTICAL.getMessage() + ": " + loadout.getTactical().getName());
			lore.add(Lang.INVENTORY_SELECT_CLASS_PERK.getMessage() + " 1: " + loadout.getPerk1().getPerk().toString());
			lore.add(Lang.INVENTORY_SELECT_CLASS_PERK.getMessage() + " 2: " + loadout.getPerk2().getPerk().toString());
			lore.add(Lang.INVENTORY_SELECT_CLASS_PERK.getMessage() + " 3: " + loadout.getPerk3().getPerk().toString());

			meta.setLore(lore);
			item.setItemMeta(meta);

			inventory.setItem(i, item);
		}

		selectClassInventory.put(p, inventory);
	}

	public boolean setupKillStreakInventories(Player p) {

		Inventory one,
				two,
				three;

		one = Bukkit.createInventory(null, 27, Lang.SELECT_STREAK_INVENTORY_NAME.getMessage());
		two = Bukkit.createInventory(null, 27, Lang.SELECT_STREAK_INVENTORY_NAME.getMessage());
		three = Bukkit.createInventory(null, 27, Lang.SELECT_STREAK_INVENTORY_NAME.getMessage());


		for (KillStreak ks : KillStreak.values()) {
			boolean found = false;

			KillStreak[] pStreaks = Main.killstreakManager.getStreaks(p);

			for (int i = 0; i < 3; i++) {
				found = pStreaks[i].equals(ks);
				if (found)
					break;
			}

			if (!found) {
				one.addItem(ks.getKillStreakItem());
				two.addItem(ks.getKillStreakItem());
				three.addItem(ks.getKillStreakItem());
			}
		}


		one.setItem(26, backInv);
		two.setItem(26, backInv);
		three.setItem(26, backInv);

		killStreakInventory1.put(p, one);
		killStreakInventory2.put(p, two);
		killStreakInventory3.put(p, three);
		return true;
	}

	public void openKillStreakInventory(Player p, int i) {

		setupKillStreakInventories(p);

		switch(i) {
			case 1:
				p.openInventory(killStreakInventory1.get(p));
				return;
			case 2:
				p.openInventory(killStreakInventory2.get(p));
				return;
			case 3:
				p.openInventory(killStreakInventory3.get(p));
				return;
			default:
				p.closeInventory();
				break;
		}
	}

	public boolean openSelectClassInventory(Player p) {
		if (!GameManager.isInMatch(p)) {
			Main.sendMessage(p,Main.codPrefix + Lang.ERROR_CAN_NOT_CHANGE_CLASS.getMessage(), Main.lang);
			return false;
		}

		if (this.selectClassInventory.get(p) == null) {
			this.setupSelectClassInventory(p);
		}

		p.openInventory(this.selectClassInventory.get(p));
		return true;
	}

	private void setupLeaderBoard() {
		leaderboardInventory.setItem(35, backInv);
		ArrayList<String> pls = StatHandler.getLeaderboardList();

		TreeMap<Double, String> expMap = new TreeMap<>();

		HashMap<String, ItemStack> leaderboardOrder = new HashMap<>();

		for (String name : pls) {
			ItemStack player;
			try {
				player = new ItemStack(Material.valueOf("SKELETON_SKULL"));
			} catch(Exception e) {
				player = new ItemStack(Material.ANVIL);
			}
			ItemMeta playerMeta = player.getItemMeta();
			playerMeta.setDisplayName(Lang.LEADERBOARD_PLAYER_ENTRY.getMessage().replace("{name}", name));

			double experience = StatHandler.getExperience(name);

			player.setItemMeta(playerMeta);

			leaderboardOrder.put(name, player);

			expMap.put(experience, name);
		}

		for (int i = 0, pos = 1; i < expMap.size(); i++, pos++) {
			String id = expMap.get(expMap.descendingKeySet().toArray()[i]);
			ItemStack item = leaderboardOrder.get(id);
			ItemMeta itemMeta = item.getItemMeta();
			float kills = (float) StatHandler.getKills(id);
			float deaths = (float) StatHandler.getDeaths(id);

			double experience = StatHandler.getExperience(id);

			float kdr = kills / deaths;

			ArrayList<String> lore = new ArrayList<>();

			lore.add(Lang.LEADERBOARD_POSITION.getMessage().replace("{score}", pos + ""));
			lore.add(Lang.LEADERBOARD_SCORE.getMessage().replace("{score}", experience + ""));
			lore.add(Lang.LEADERBOARD_KILLS.getMessage().replace("{score}", (int) kills + ""));
			lore.add(Lang.LEADERBOARD_DEATHS.getMessage().replace("{score}", (int) deaths + ""));
			if (!Float.isNaN(kdr)) {
				lore.add(Lang.LEADERBOARD_KD.getMessage().replace("{score}", kdr + ""));
			} else {
				lore.add(Lang.LEADERBOARD_KD.getMessage().replace("{score}", (int) kills + ""));
			}

			itemMeta.setLore(lore);

			item.setItemMeta(itemMeta);

			leaderboardInventory.setItem(pos - 1, item);

		}

		System.gc();
	}

	private void setupPersonalStatsBoardMenu(Player p) {
		if (!personalStatistics.containsKey(p)) {
			personalStatistics.put(p, Bukkit.createInventory(null, 9, Lang.INVENTORY_RECORD_NAME.getMessage()));
		}

		Inventory inv = personalStatistics.get(p);

		inv.clear();

		int totalKills = StatHandler.getKills(p.getName());
		int totalDeaths = StatHandler.getDeaths(p.getName());

		float kdr = ((float) totalKills) / ((float) totalDeaths);

		ItemStack kills = new ItemStack(Material.ARROW);
		ItemMeta killsMeta = kills.getItemMeta();
		killsMeta.setDisplayName(Lang.LEADERBOARD_KILLS.getMessage().replace("{score}", totalKills + ""));
		kills.setItemMeta(killsMeta);

		ItemStack deaths;
		try {
			deaths = new ItemStack(Material.valueOf("SKELETON_SKULL"));
		} catch(Exception e) {
			deaths = new ItemStack(Material.ANVIL);
		}
		ItemMeta deathsMeta = deaths.getItemMeta();
		deathsMeta.setDisplayName(Lang.LEADERBOARD_DEATHS.getMessage().replace("{score}", totalDeaths + ""));
		deaths.setItemMeta(deathsMeta);

		ItemStack killDeathRatio = new ItemStack(Material.GLASS_BOTTLE);
		ItemMeta killDeathRatioMeta = killDeathRatio.getItemMeta();
		if (!Float.isNaN(kdr)) {
			killDeathRatioMeta.setDisplayName(Lang.LEADERBOARD_KD.getMessage().replace("{score}", kdr + ""));
		} else {
			killDeathRatioMeta.setDisplayName(Lang.LEADERBOARD_KD.getMessage().replace("{score}", totalKills + ""));
		}

		killDeathRatio.setItemMeta(killDeathRatioMeta);

		inv.setItem(0, kills);
		inv.setItem(1, deaths);
		inv.setItem(2, killDeathRatio);
		inv.setItem(8, backInv);

		personalStatistics.put(p, inv);
	}

	private boolean openPersonalStatsMenu(Player p) {

		setupPersonalStatsBoardMenu(p);

		Inventory inv = personalStatistics.get(p);

		p.openInventory(inv);

		return true;
	}

	private void setupKillStreaksInventory(Player p) {

		Inventory inv = Bukkit.createInventory(null, 27, Lang.INVENTORY_SCORESTREAKS_NAME.getMessage());

		ItemStack one,
				two,
				three;

		one = new ItemStack(Main.killstreakManager.getStreaks(p)[0].getKillStreakItem());
		two = new ItemStack(Main.killstreakManager.getStreaks(p)[1].getKillStreakItem());
		three = new ItemStack(Main.killstreakManager.getStreaks(p)[2].getKillStreakItem());

		{
			ItemMeta meta = one.getItemMeta();
			meta.setDisplayName(Lang.KILL_STREAK_NAME.getMessage().replace("{number}", "1"));
			List<String> lore = new ArrayList<>();
			lore.add(Lang.KILL_STREAK_LORE.getMessage());
			meta.setLore(lore);
			one.setItemMeta(meta);
		}

		{
			ItemMeta meta = two.getItemMeta();
			meta.setDisplayName(Lang.KILL_STREAK_NAME.getMessage().replace("{number}", "2"));
			List<String> lore = new ArrayList<>();
			lore.add(Lang.KILL_STREAK_LORE.getMessage());
			meta.setLore(lore);
			two.setItemMeta(meta);
		}

		{
			ItemMeta meta = three.getItemMeta();
			meta.setDisplayName(Lang.KILL_STREAK_NAME.getMessage().replace("{number}", "3"));
			List<String> lore = new ArrayList<>();
			lore.add(Lang.KILL_STREAK_LORE.getMessage());
			meta.setLore(lore);
			three.setItemMeta(meta);
		}

		inv.setItem(11, one);
		inv.setItem(13, two);
		inv.setItem(15, three);

		inv.setItem(26, backInv);

		this.mainKillStreakInventory.put(p, inv);

	}

	private boolean openKillStreaksInventory(Player p) {
		setupKillStreaksInventory(p);
		Inventory inv = mainKillStreakInventory.get(p);
		p.openInventory(inv);
		return true;
	}

	@EventHandler
	public void inventoryClickListener(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player))
			return;

		Player p = (Player) e.getWhoClicked();

		if (e.getInventory() == null)
			return;

		try {
			if (shouldCancelClick(e.getInventory(), p)) {
				e.setCancelled(true);
			} else {
				return;
			}
		} catch(Exception exception) {
			Main.sendMessage(Main.cs, Lang.ERROR_DEFAULT_WEAPONS_GUNS_NOT_SET.getMessage(), Main.lang);
		}

		if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR)
			return;

		if (e.getCurrentItem().equals(closeInv)) {
			p.closeInventory();
			return;
		}

		if (e.getCurrentItem().equals(backInv)) {
			if (!e.getClickedInventory().equals(mainInventory)) {
				p.openInventory(mainInventory);
			} else {
				p.closeInventory();
			}
		}

		if (e.getClickedInventory().equals(mainInventory)) {

			if (e.getCurrentItem().equals(joinGame)) {
				Main.sendMessage(p,Main.codPrefix + Lang.PUT_IN_QUEUE.getMessage(), Main.lang);
				GameManager.findMatch(p);
				p.closeInventory();
			} else if (e.getCurrentItem().equals(createClass)) {
				this.setupCreateClassInventory(p);
				p.openInventory(createClassInventory.get(p));
			} else if (e.getCurrentItem().equals(shopItem)) {
				setupShopInventories(p);
				p.openInventory(mainShopInventory);
			} else if (e.getCurrentItem().equals(combatRecord)) {
				openPersonalStatsMenu(p);
			} else if (e.getCurrentItem().equals(leaderboard)) {
				setupLeaderBoard();
				p.openInventory(leaderboardInventory);
			} else if (e.getCurrentItem().equals(scoreStreaks)) {
				this.openKillStreaksInventory(p);
			} else if (e.getCurrentItem().equals(assignmentsItem)) {
				this.openAssignmentsInventory(p);
			} else if (e.getCurrentItem().equals(prestigeItem)) {
				if (Main.progressionManager.getLevel(p) == Main.progressionManager.maxLevel) {
					Main.progressionManager.addPrestigeLevel(p);
					p.closeInventory();
				} else {
					Main.sendMessage(p, Lang.ERROR_NOT_HIGH_ENOUGH_LEVEL.getMessage(), Main.lang);
				}
			}
		} else if (e.getInventory().equals(mainShopInventory)) {
			if (e.getCurrentItem().equals(gunShopItem)) {
				if (!Main.shopManager.gunShop.containsKey(p)) {
					this.setupShopInventories(p);
				}
				p.openInventory(Main.shopManager.gunShop.get(p));
			} else if (e.getCurrentItem().equals(grenadeShopItem)) {
				if (!Main.shopManager.weaponShop.containsKey(p)) {
					this.setupShopInventories(p);
				}
				p.openInventory(Main.shopManager.weaponShop.get(p));
			} else if (e.getCurrentItem().equals(perkShopItem)) {
				if (!Main.shopManager.weaponShop.containsKey(p)) {
					this.setupShopInventories(p);
				}
				p.openInventory(Main.shopManager.perkShop.get(p));
			} else if (e.getCurrentItem().equals(shopItem)) {
				p.openInventory(mainShopInventory);
			}
		} else if (e.getInventory().equals(mainKillStreakInventory.get(p))) {
			if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)) {
				return;
			}

			if (e.getSlot() == 11) {
				openKillStreakInventory(p, 1);
			} else if (e.getSlot() == 13) {
				openKillStreakInventory(p, 2);
			} else if (e.getSlot() == 15) {
				openKillStreakInventory(p, 3);
			}


		} else if (e.getInventory().equals(createClassInventory.get(p))) {

			int slot = e.getSlot();

			for (int i = 0; i < Main.loadManager.getAllowedClasses(p); i++) {
				if (slot == 1 + (9 * i)) {
					p.openInventory(Main.loadManager.getLoadouts(p).get(i).getPrimaryInventory());
				} else if (slot == 2 + (9 * i)) {
					p.openInventory(Main.loadManager.getLoadouts(p).get(i).getSecondaryInventory());
				} else if (slot == 3 + (9 * i)) {
					p.openInventory(Main.loadManager.getLoadouts(p).get(i).getLethalInventory());
				} else if (slot == 4 + (9 * i)) {
					p.openInventory(Main.loadManager.getLoadouts(p).get(i).getTacticalInventory());
				} else if (slot == 5 + (9 * i)) {
					p.openInventory(Main.loadManager.getLoadouts(p).get(i).getPerk1Inventory());
				} else if (slot == 6 + (9 * i)) {
					p.openInventory(Main.loadManager.getLoadouts(p).get(i).getPerk2Inventory());
				} else if (slot == 7 + (9 * i)) {
					p.openInventory(Main.loadManager.getLoadouts(p).get(i).getPerk3Inventory());
				}
			}

		} else if (Main.shopManager.gunShop.get(p) != null && e.getInventory().equals(Main.shopManager.gunShop.get(p))) {

			if (e.getCurrentItem().equals(backInv)) {
				p.openInventory(mainShopInventory);
				return;
			}

			Main.shopManager.loadPurchaseData(p);

			ArrayList<CodGun> guns = Main.shopManager.getPrimaryGuns();
			guns.addAll(Main.shopManager.getSecondaryGuns());

			for (CodGun gun : guns) {
				if (e.getCurrentItem().getItemMeta().getDisplayName().equals(gun.getGun().getItemMeta().getDisplayName())) {
					int cost = gun.getCreditUnlock();
					if (CreditManager.purchase(p, cost)) {
						ArrayList<CodGun> purchasedGuns = Main.shopManager.purchasedGuns.get(p);
						purchasedGuns.add(gun);
						Main.shopManager.purchasedGuns.put(p, purchasedGuns);
						Main.shopManager.savePurchaseData(p);
						Main.shopManager.loadPurchaseData(p);
						Main.invManager.setupShopInventories(p);
						Main.invManager.setupCreateClassInventory(p);
						Main.invManager.setupPlayerSelectionInventories(p);
						p.openInventory(mainInventory);
						return;
					} else {
						return;
					}
				}
			}

		} else if (e.getInventory().equals(Main.shopManager.weaponShop.get(p))) {

			if (e.getCurrentItem().equals(backInv)) {
				p.openInventory(mainShopInventory);
				return;
			}

			Main.shopManager.loadPurchaseData(p);

			ArrayList<CodWeapon> grenades = Main.shopManager.getLethalWeapons();
			grenades.addAll(Main.shopManager.getTacticalWeapons());

			for (CodWeapon grenade : grenades) {
				if (e.getCurrentItem().getType().equals(grenade.getWeapon().getType())
				&&e.getCurrentItem().getItemMeta().getDisplayName().equals(grenade.getWeapon().getItemMeta().getDisplayName())) {
					int cost = grenade.getCreditUnlock();
					if (CreditManager.purchase(p, cost)) {
						ArrayList<CodWeapon> purchasedGrenades = Main.shopManager.purchasedWeapons.get(p);
						purchasedGrenades.add(grenade);
						Main.shopManager.purchasedWeapons.put(p, purchasedGrenades);
						Main.shopManager.savePurchaseData(p);
						Main.shopManager.loadPurchaseData(p);
						Main.invManager.setupShopInventories(p);
						Main.invManager.setupCreateClassInventory(p);
						Main.invManager.setupPlayerSelectionInventories(p);
						p.openInventory(mainInventory);
						return;
					} else {
						return;
					}
				}
			}
		} else if (e.getInventory().equals(Main.shopManager.perkShop.get(p))) {

			if (e.getCurrentItem().equals(backInv)){
				p.openInventory(mainShopInventory);
				return;
			}

			Main.shopManager.loadPurchaseData(p);

			ArrayList<CodPerk> perks = Main.perkManager.getAvailablePerks();

			for (CodPerk perk : perks) {
				if (e.getCurrentItem().getType().equals(perk.getItem().getType()) && e.getCurrentItem().getItemMeta().getDisplayName().equals(perk.getPerk().getName())) {
					int cost = perk.getCost();
					if (CreditManager.purchase(p, cost)) {
						ArrayList<CodPerk> purchasedPerks = Main.shopManager.purchasedPerks.get(p);
						purchasedPerks.add(perk);
						Main.shopManager.purchasedPerks.put(p, purchasedPerks);
						Main.shopManager.savePurchaseData(p);
						Main.shopManager.loadPurchaseData(p);
						Main.invManager.setupShopInventories(p);
						Main.invManager.setupCreateClassInventory(p);
						Main.invManager.setupPlayerSelectionInventories(p);
						p.openInventory(mainInventory);
						return;
					} else {
						return;
					}
				}
			}
		} else if (e.getInventory().equals(this.selectClassInventory.get(p))) {
			int slot;
			try {
				slot = e.getSlot();
			} catch (NullPointerException exception) {
				Main.sendMessage(Main.cs, Lang.ERROR_SELECTING_CLASS.getMessage(), Main.lang);
				return;
			}

			boolean hasOneManArmy = false;

			if (Main.loadManager.getCurrentLoadout(p).hasPerk(Perk.ONE_MAN_ARMY)) {
				hasOneManArmy = true;
			}

			Loadout current = Main.loadManager.getLoadouts(p).get(slot);
			Main.loadManager.setActiveLoadout(p, current);

			if (!hasOneManArmy) {
				Main.sendMessage(p, Main.codPrefix + Lang.CHANGED_CLASS_MESSAGE.getMessage(), Main.lang);
				Main.perkListener.oneManArmy(p);
			} else {
				Main.sendMessage(p, Main.codPrefix + Lang.CHANGED_CLASS_ONE_MAN_ARMY.getMessage(), Main.lang);
			}

		} else if (e.getInventory().equals(killStreakInventory1.get(p)) || e.getInventory().equals(killStreakInventory2.get(p)) || e.getInventory().equals(killStreakInventory3.get(p))) {

			if (e.getCurrentItem().equals(backInv)) {
				openKillStreaksInventory(p);
				return;
			}

			int inv = -1;

			if (e.getInventory().equals(killStreakInventory1.get(p)))
				inv = 0;
			else if (e.getInventory().equals(killStreakInventory2.get(p)))
				inv = 1;
			else if (e.getInventory().equals(killStreakInventory3.get(p)))
				inv = 2;


			for (KillStreak streak : KillStreak.values()) {
				if (e.getCurrentItem().equals(streak.getKillStreakItem())) {
					Main.killstreakManager.setStreak(p, streak, inv);
					openKillStreaksInventory(p);
					Main.sendMessage(p, Lang.CHANGE_STREAK_SUCCESS.getMessage(), Main.lang);
					return;
				}
			}

		} else {

			ItemStack item = e.getCurrentItem();
			for (Loadout loadout : Main.loadManager.getLoadouts(p)) {

				if (e.getInventory().equals(loadout.getPrimaryInventory())) {

					if (e.getCurrentItem().equals(backInv)) {
						p.openInventory(createClassInventory.get(p));
						return;
					}

					for (CodGun gun : Main.shopManager.getPurchasedGuns().get(p)) {
						if (gun.getGun().equals(item)) {
							loadout.setPrimary(gun);
							Main.invManager.setupCreateClassInventory(p);
							p.openInventory(Main.invManager.createClassInventory.get(p));
							return;
						}
					}

					if (Main.loadManager.getDefaultPrimary().getGun().equals(item)) {
						loadout.setPrimary(Main.loadManager.getDefaultPrimary());
						Main.invManager.setupCreateClassInventory(p);
						p.openInventory(Main.invManager.createClassInventory.get(p));
						return;
					}

					break;

				} else if (e.getInventory().equals(loadout.getSecondaryInventory())) {

					if (e.getCurrentItem().equals(backInv)) {
						p.openInventory(createClassInventory.get(p));
						return;
					}

					for (CodGun gun : Main.shopManager.getPurchasedGuns().get(p)) {
						if (gun.getGun().equals(item)) {
							loadout.setSecondary(gun);
							Main.invManager.setupCreateClassInventory(p);
							p.openInventory(Main.invManager.createClassInventory.get(p));
							return;
						}
					}

					if (Main.loadManager.getDefaultSecondary().getGun().equals(item)) {
						loadout.setSecondary(Main.loadManager.getDefaultSecondary());
						Main.invManager.setupCreateClassInventory(p);
						p.openInventory(Main.invManager.createClassInventory.get(p));
						return;
					}

					break;

				} else if (e.getInventory().equals(loadout.getLethalInventory())) {

					if (e.getCurrentItem().equals(backInv)) {
						p.openInventory(createClassInventory.get(p));
						return;
					}

					for (CodWeapon grenade : Main.shopManager.getPurchasedWeapons().get(p)) {
						if (grenade.getWeapon().equals(item)) {
							loadout.setLethal(grenade);
							Main.invManager.setupCreateClassInventory(p);
							p.openInventory(Main.invManager.createClassInventory.get(p));
							return;
						}
					}

					if (Main.loadManager.getDefaultLethal().getWeapon().equals(item)) {

						loadout.setLethal(Main.loadManager.getDefaultLethal());
						Main.invManager.setupCreateClassInventory(p);
						p.openInventory(Main.invManager.createClassInventory.get(p));
						return;
					}

					break;

				} else if (e.getInventory().equals(loadout.getTacticalInventory())) {

					if (e.getCurrentItem().equals(backInv)) {
						p.openInventory(createClassInventory.get(p));
						return;
					}

					for (CodWeapon grenade : Main.shopManager.getPurchasedWeapons().get(p)) {
						if (grenade.getWeapon().equals(item)) {
							loadout.setTactical(grenade);
							Main.invManager.setupCreateClassInventory(p);
							p.openInventory(Main.invManager.createClassInventory.get(p));
							return;
						}
					}

					if (Main.loadManager.getDefaultTactical().getWeapon().equals(item)) {
						loadout.setTactical(Main.loadManager.getDefaultTactical());
						Main.invManager.setupCreateClassInventory(p);
						p.openInventory(Main.invManager.createClassInventory.get(p));
						return;
					}

					break;

				} else if (e.getInventory().equals(loadout.getPerk1Inventory())) {

					if (e.getCurrentItem().equals(backInv)) {
						p.openInventory(createClassInventory.get(p));
						return;
					}

					for (CodPerk perk : Main.shopManager.getPerks(p)) {
						if (perk.getPerk().getName().equals(item.getItemMeta().getDisplayName())) {
							loadout.setPerk(PerkSlot.ONE, perk);
							Main.invManager.setupCreateClassInventory(p);
							p.openInventory(Main.invManager.createClassInventory.get(p));
							return;
						}
					}

				} else if (e.getInventory().equals(loadout.getPerk2Inventory())) {

					if (e.getCurrentItem().equals(backInv)) {
						p.openInventory(createClassInventory.get(p));
						return;
					}

					for (CodPerk perk : Main.shopManager.getPerks(p)) {
						if (perk.getPerk().getName().equals(item.getItemMeta().getDisplayName())) {
							loadout.setPerk(PerkSlot.TWO, perk);
							Main.invManager.setupCreateClassInventory(p);
							p.openInventory(Main.invManager.createClassInventory.get(p));
							return;
						}
					}

				} else if (e.getInventory().equals(loadout.getPerk3Inventory())) {

					if (e.getCurrentItem().equals(backInv)) {
						p.openInventory(createClassInventory.get(p));
						return;
					}

					for (CodPerk perk : Main.shopManager.getPerks(p)) {
						if (perk.getPerk().getName().equals(item.getItemMeta().getDisplayName())) {
							loadout.setPerk(PerkSlot.THREE, perk);
							Main.invManager.setupCreateClassInventory(p);
							p.openInventory(Main.invManager.createClassInventory.get(p));
							return;
						}
					}

				}
			}
		}

	}

	@EventHandler
	public void itemUseListener(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {

			ItemStack item;
			ItemStack altItem;

			try {
				item = (ItemStack) e.getPlayer().getInventory().getClass().getMethod("getItemInMainHand").invoke(e.getPlayer().getInventory());
				altItem = (ItemStack) e.getPlayer().getInventory().getClass().getMethod("getItemInOffHand").invoke(e.getPlayer().getInventory());
			} catch(NoSuchMethodException er1) {
				item = e.getPlayer().getInventory().getItemInHand();
				altItem = item;
			} catch (Exception er1) {
				item = e.getPlayer().getInventory().getItemInHand();
				altItem = item;
			}

			if (item.equals(leaveItem) || altItem.equals(leaveItem)) {
				if (!GameManager.isInMatch(e.getPlayer())) return;
				GameManager.leaveMatch(e.getPlayer());
				if (Main.lastLoc.containsKey(e.getPlayer())) {
					e.getPlayer().teleport(lastLoc.get(e.getPlayer()));
					lastLoc.remove(e.getPlayer());
				} else {
					if (lobbyLoc != null) {
						e.getPlayer().teleport(lobbyLoc);
					}
				}
				e.setCancelled(true);
				return;
			}
			if (item.equals(codItem) || altItem.equals(codItem)) {
				Main.openMainMenu(e.getPlayer());
				e.setCancelled(true);
//				return;
			}

			if ((item.getItemMeta() != null && item.getItemMeta().getDisplayName().equals(voteItemA.getItemMeta().getDisplayName()))
					|| (altItem.getItemMeta() != null && altItem.getItemMeta().getDisplayName().equals(voteItemA.getItemMeta().getDisplayName()))) {
				try {
					Objects.requireNonNull(GameManager.getMatchWhichContains(e.getPlayer())).addVote(0, e.getPlayer());
				} catch (Exception ignored) {
					return;
				}

				Main.sendMessage(e.getPlayer(), Main.codPrefix + Lang.VOTE_REGISTERED.getMessage(), Main.lang);
			}

			if ((item.getItemMeta() != null && item.getItemMeta().getDisplayName().equals(voteItemB.getItemMeta().getDisplayName()))
					|| (altItem.getItemMeta() != null && altItem.getItemMeta().getDisplayName().equals(voteItemB.getItemMeta().getDisplayName()))) {
				try {
					Objects.requireNonNull(GameManager.getMatchWhichContains(e.getPlayer())).addVote(1, e.getPlayer());
				} catch (Exception ignored) {
					return;
				}

				Main.sendMessage(e.getPlayer(), Main.codPrefix + Lang.VOTE_REGISTERED.getMessage(), Main.lang);
			}
		}
	}

}
