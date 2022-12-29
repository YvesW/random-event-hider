package com.ywcode.randomeventhider;

import com.google.common.annotations.*;
import com.google.common.collect.*;
import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.callback.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.*;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.*;

@Slf4j
@PluginDescriptor(
		name = "Random Event Hider",
		description = "Adds the ability to hide specific random events that interact with you or with other players.",
		tags = {"random event,hider,random event hider,ra hider"}
)

//TODO: remove tests IN ALL FILES INCLUDING CONFIG (ctrl+f TEST:)

public class RandomEventHiderPlugin extends Plugin {
	private static final Set<Integer> EVENT_NPCS = ImmutableSet.of(
			NpcID.BEE_KEEPER_6747,
			NpcID.CAPT_ARNAV,
			NpcID.DR_JEKYLL, NpcID.DR_JEKYLL_314,
			NpcID.DRUNKEN_DWARF,
			NpcID.DUNCE_6749,
			NpcID.EVIL_BOB, NpcID.EVIL_BOB_6754,
			NpcID.FLIPPA_6744,
			NpcID.FREAKY_FORESTER_6748,
			NpcID.FROG_5429, NpcID.FROG_5430, NpcID.FROG_5431, NpcID.FROG_5432, NpcID.FROG_5833, NpcID.FROG,
			NpcID.GENIE, NpcID.GENIE_327,
			NpcID.GILES, NpcID.GILES_5441,
			NpcID.LEO_6746,
			NpcID.MILES, NpcID.MILES_5440,
			NpcID.MYSTERIOUS_OLD_MAN_6750, NpcID.MYSTERIOUS_OLD_MAN_6751,
			NpcID.MYSTERIOUS_OLD_MAN_6752, NpcID.MYSTERIOUS_OLD_MAN_6753,
			NpcID.NILES, NpcID.NILES_5439,
			NpcID.PILLORY_GUARD,
			NpcID.POSTIE_PETE_6738,
			NpcID.QUIZ_MASTER_6755,
			NpcID.RICK_TURPENTINE, NpcID.RICK_TURPENTINE_376,
			NpcID.SANDWICH_LADY,
			NpcID.SERGEANT_DAMIEN_6743,
			NpcID.STRANGE_PLANT,//PM potentially changes into a game object instead of an NPC after a while? Not sure tbh, just a speculation. Haven't been able to test it yet, but seems unlikely.
			5216 //TEST: benny test varrock square
	);

	private static final Set<Integer> FROGS_NPCS = ImmutableSet.of(
			NpcID.FROG_5429, NpcID.FROG_5430, NpcID.FROG_5431, NpcID.FROG_5432, NpcID.FROG_5833, NpcID.FROG
	);

	private boolean hideOtherBeekeeper;
	private boolean hideOtherCaptArnav;
	private boolean hideOtherNiles;
	private boolean hideOtherDrillDemon;
	private boolean hideOtherDrunkenDwarf;
	private boolean hideOtherEvilBob;
	private boolean hideOtherEvilTwin;
	private boolean hideOtherFreakyForester;
	private boolean hideOtherGenie;
	private boolean hideOtherGravedigger;
	private boolean hideOtherJekyllHyde;
	private boolean hideOtherKissTheFrog;
	private boolean hideOtherMaze;
	private boolean hideOtherMime;
	private boolean hideOtherMysteriousOldMan;
	private boolean hideOtherPilloryGuard;
	private boolean hideOtherPinball;
	private boolean hideOtherPrisonPete;
	private boolean hideOtherQuizMaster;
	private boolean hideOtherRickTurpentine;
	private boolean hideOtherSandwichLady;
	private boolean hideOtherStrangePlant;
	private boolean hideOtherSurpriseExam;
	private boolean hideOwnBeekeeper;
	private boolean hideOwnCaptArnav;
	private boolean hideOwnNiles;
	private boolean hideOwnDrillDemon;
	private boolean hideOwnDrunkenDwarf;
	private boolean hideOwnEvilBob;
	private boolean hideOwnEvilTwin;
	private boolean hideOwnFreakyForester;
	private boolean hideOwnGenie;
	private boolean hideOwnGravedigger;
	private boolean hideOwnJekyllHyde;
	private boolean hideOwnKissTheFrog;
	private boolean hideOwnMaze;
	private boolean hideOwnMime;
	private boolean hideOwnMysteriousOldMan;
	private boolean hideOwnPilloryGuard;
	private boolean hideOwnPinball;
	private boolean hideOwnPrisonPete;
	private boolean hideOwnQuizMaster;
	private boolean hideOwnRickTurpentine;
	private boolean hideOwnSandwichLady;
	private boolean hideOwnStrangePlant;
	private boolean hideOwnSurpriseExam;
	private boolean hideOwnBenny; //TEST: remove test
	private boolean hideOtherBenny; //TEST: remove test
	/* Originally wrote this thinking I'd separate 2D elements and model, but later opted not to do it.
	private boolean hideOtherBeekeeper2D;
	private boolean hideOtherCaptArnav2D;
	private boolean hideOtherNiles2D;
	private boolean hideOtherDrillDemon2D;
	private boolean hideOtherDrunkenDwarf2D;
	private boolean hideOtherEvilBob2D;
	private boolean hideOtherEvilTwin2D;
	private boolean hideOtherFreakyForester2D;
	private boolean hideOtherGenie2D;
	private boolean hideOtherGravedigger2D;
	private boolean hideOtherJekyllHyde2D;
	private boolean hideOtherKissTheFrog2D;
	private boolean hideOtherMaze2D;
	private boolean hideOtherMime2D;
	private boolean hideOtherMysteriousOldMan2D;
	private boolean hideOtherPilloryGuard2D;
	private boolean hideOtherPinball2D;
	private boolean hideOtherPrisonPete2D;
	private boolean hideOtherQuizMaster2D;
	private boolean hideOtherRickTurpentine2D;
	private boolean hideOtherSandwichLady2D;
	private boolean hideOtherStrangePlant2D;
	private boolean hideOtherSurpriseExam2D;
	private boolean hideOwnBeekeeper2D;
	private boolean hideOwnCaptArnav2D;
	private boolean hideOwnNiles2D;
	private boolean hideOwnDrillDemon2D;
	private boolean hideOwnDrunkenDwarf2D;
	private boolean hideOwnEvilBob2D;
	private boolean hideOwnEvilTwin2D;
	private boolean hideOwnFreakyForester2D;
	private boolean hideOwnGenie2D;
	private boolean hideOwnGravedigger2D;
	private boolean hideOwnJekyllHyde2D;
	private boolean hideOwnKissTheFrog2D;
	private boolean hideOwnMaze2D;
	private boolean hideOwnMime2D;
	private boolean hideOwnMysteriousOldMan2D;
	private boolean hideOwnPilloryGuard2D;
	private boolean hideOwnPinball2D;
	private boolean hideOwnPrisonPete2D;
	private boolean hideOwnQuizMaster2D;
	private boolean hideOwnRickTurpentine2D;
	private boolean hideOwnSandwichLady2D;
	private boolean hideOwnStrangePlant2D;
	private boolean hideOwnSurpriseExam2D;
	 */

	private LinkedHashMap<Integer, Integer> ownRandomsMap = new LinkedHashMap<Integer, Integer>();
	private LinkedHashMap<Integer, Integer> otherRandomsMap = new LinkedHashMap<Integer, Integer>();

	private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

	@Inject
	private Client client;

	@Inject
	private RandomEventHiderConfig config;

	@Inject
	private Hooks hooks;

	@Override
	protected void startUp() {
		hooks.registerRenderableDrawListener(drawListener);
		updateConfig();
	}

	@Override
	protected void shutDown() {
		hooks.unregisterRenderableDrawListener(drawListener);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged e) {
		if (e.getGroup().equals("RandomEventHider")) {
			updateConfig();
		}
	}

	private void updateConfig() {
		hideOtherBeekeeper = config.hideOtherBeekeeper();
		hideOtherCaptArnav = config.hideOtherCaptArnav();
		hideOtherNiles = config.hideOtherNiles();
		hideOtherDrillDemon = config.hideOtherDrillDemon();
		hideOtherDrunkenDwarf = config.hideOtherDrunkenDwarf();
		hideOtherEvilBob = config.hideOtherEvilBob();
		hideOtherEvilTwin = config.hideOtherEvilTwin();
		hideOtherFreakyForester = config.hideOtherFreakyForester();
		hideOtherGenie = config.hideOtherGenie();
		hideOtherGravedigger = config.hideOtherGravedigger();
		hideOtherJekyllHyde = config.hideOtherJekyllHyde();
		hideOtherKissTheFrog = config.hideOtherKissTheFrog();
		hideOtherMaze = config.hideOtherMaze();
		hideOtherMime = config.hideOtherMime();
		hideOtherMysteriousOldMan = config.hideOtherMysteriousOldMan();
		hideOtherPilloryGuard = config.hideOtherPilloryGuard();
		hideOtherPinball = config.hideOtherPinball();
		hideOtherPrisonPete = config.hideOtherPrisonPete();
		hideOtherQuizMaster = config.hideOtherQuizMaster();
		hideOtherRickTurpentine = config.hideOtherRickTurpentine();
		hideOtherSandwichLady = config.hideOtherSandwichLady();
		hideOtherStrangePlant = config.hideOtherStrangePlant();
		hideOtherSurpriseExam = config.hideOtherSurpriseExam();
		hideOwnBeekeeper = config.hideOwnBeekeeper();
		hideOwnCaptArnav = config.hideOwnCaptArnav();
		hideOwnNiles = config.hideOwnNiles();
		hideOwnDrillDemon = config.hideOwnDrillDemon();
		hideOwnDrunkenDwarf = config.hideOwnDrunkenDwarf();
		hideOwnEvilBob = config.hideOwnEvilBob();
		hideOwnEvilTwin = config.hideOwnEvilTwin();
		hideOwnFreakyForester = config.hideOwnFreakyForester();
		hideOwnGenie = config.hideOwnGenie();
		hideOwnGravedigger = config.hideOwnGravedigger();
		hideOwnJekyllHyde = config.hideOwnJekyllHyde();
		hideOwnKissTheFrog = config.hideOwnKissTheFrog();
		hideOwnMaze = config.hideOwnMaze();
		hideOwnMime = config.hideOwnMime();
		hideOwnMysteriousOldMan = config.hideOwnMysteriousOldMan();
		hideOwnPilloryGuard = config.hideOwnPilloryGuard();
		hideOwnPinball = config.hideOwnPinball();
		hideOwnPrisonPete = config.hideOwnPrisonPete();
		hideOwnQuizMaster = config.hideOwnQuizMaster();
		hideOwnRickTurpentine = config.hideOwnRickTurpentine();
		hideOwnSandwichLady = config.hideOwnSandwichLady();
		hideOwnStrangePlant = config.hideOwnStrangePlant();
		hideOwnSurpriseExam = config.hideOwnSurpriseExam();
		hideOwnBenny = config.hideOwnBenny(); //TEST: remove test
		hideOtherBenny = config.hideOtherBenny(); //TEST: remove test
		/* Originally wrote this thinking I'd separate 2D elements and model, but later opted not to do it.
		hideOtherBeekeeper2D = config.hideOtherBeekeeper2D();
		hideOtherCaptArnav2D = config.hideOtherCaptArnav2D();
		hideOtherNiles2D = config.hideOtherNiles2D();
		hideOtherDrillDemon2D = config.hideOtherDrillDemon2D();
		hideOtherDrunkenDwarf2D = config.hideOtherDrunkenDwarf2D();
		hideOtherEvilBob2D = config.hideOtherEvilBob2D();
		hideOtherEvilTwin2D = config.hideOtherEvilTwin2D();
		hideOtherFreakyForester2D = config.hideOtherFreakyForester2D();
		hideOtherGenie2D = config.hideOtherGenie2D();
		hideOtherGravedigger2D = config.hideOtherGravedigger2D();
		hideOtherJekyllHyde2D = config.hideOtherJekyllHyde2D();
		hideOtherKissTheFrog2D = config.hideOtherKissTheFrog2D();
		hideOtherMaze2D = config.hideOtherMaze2D();
		hideOtherMime2D = config.hideOtherMime2D();
		hideOtherMysteriousOldMan2D = config.hideOtherMysteriousOldMan2D();
		hideOtherPilloryGuard2D = config.hideOtherPilloryGuard2D();
		hideOtherPinball2D = config.hideOtherPinball2D();
		hideOtherPrisonPete2D = config.hideOtherPrisonPete2D();
		hideOtherQuizMaster2D = config.hideOtherQuizMaster2D();
		hideOtherRickTurpentine2D = config.hideOtherRickTurpentine2D();
		hideOtherSandwichLady2D = config.hideOtherSandwichLady2D();
		hideOtherStrangePlant2D = config.hideOtherStrangePlant2D();
		hideOtherSurpriseExam2D = config.hideOtherSurpriseExam2D();
		hideOwnBeekeeper2D = config.hideOwnBeekeeper2D();
		hideOwnCaptArnav2D = config.hideOwnCaptArnav2D();
		hideOwnNiles2D = config.hideOwnNiles2D();
		hideOwnDrillDemon2D = config.hideOwnDrillDemon2D();
		hideOwnDrunkenDwarf2D = config.hideOwnDrunkenDwarf2D();
		hideOwnEvilBob2D = config.hideOwnEvilBob2D();
		hideOwnEvilTwin2D = config.hideOwnEvilTwin2D();
		hideOwnFreakyForester2D = config.hideOwnFreakyForester2D();
		hideOwnGenie2D = config.hideOwnGenie2D();
		hideOwnGravedigger2D = config.hideOwnGravedigger2D();
		hideOwnJekyllHyde2D = config.hideOwnJekyllHyde2D();
		hideOwnKissTheFrog2D = config.hideOwnKissTheFrog2D();
		hideOwnMaze2D = config.hideOwnMaze2D();
		hideOwnMime2D = config.hideOwnMime2D();
		hideOwnMysteriousOldMan2D = config.hideOwnMysteriousOldMan2D();
		hideOwnPilloryGuard2D = config.hideOwnPilloryGuard2D();
		hideOwnPinball2D = config.hideOwnPinball2D();
		hideOwnPrisonPete2D = config.hideOwnPrisonPete2D();
		hideOwnQuizMaster2D = config.hideOwnQuizMaster2D();
		hideOwnRickTurpentine2D = config.hideOwnRickTurpentine2D();
		hideOwnSandwichLady2D = config.hideOwnSandwichLady2D();
		hideOwnStrangePlant2D = config.hideOwnStrangePlant2D();
		hideOwnSurpriseExam2D = config.hideOwnSurpriseExam2D();
		hideOwnBenny2D = config.hideOwnBenny2D();
		hideOtherBenny2D = config.hideOtherBenny2D();
		 */
	}

	@Subscribe
	public void onInteractingChanged(InteractingChanged event) {
		Actor source = event.getSource();
		Actor target = event.getTarget();
		Player player = client.getLocalPlayer();

		//This is the player's own random event, assuming the random immediately interacts with the player when spawned.
		//Won't get marked as someone else's NPC later on (even if other people interact with it), since it's already on this map.
		//Assuming random event NPCs for other players immediately interact with another user when they spawn, it'll get on the "other" map, even if the player talks to someone else's random event later on.
		//Also write down the index in case there are multiple random events with the same NPC id on screen (index likely differs?)
		if (player != null && (source instanceof NPC) && (target instanceof Player) && target == player && EVENT_NPCS.contains(((NPC) source).getId())) {
			if (!ownRandomsMap.containsKey(((NPC) source).getIndex()) && !otherRandomsMap.containsKey(((NPC) source).getIndex())) { //A potential Id check is probably redundant, since all NPCs most likely have a unique Index. Additionally, doesn't error out if lists are empty AFAIK, so no isEmpty() check first.
				ownRandomsMap.put(((NPC) source).getIndex(), ((NPC) source).getId()); //Id is probably useful for e.g. the Frog random
			}
		}

		//This is someone else's random event, assuming the random immediately interacts with the target when spawned.
		//Won't get marked as your own NPC later on (even if the player interacts with it), since it's already on this map.
		//Also write down the index in case there are multiple random events with the same NPC id on screen (index likely differs?)
		if (player != null && (source instanceof NPC) && (target instanceof Player) && target != player && EVENT_NPCS.contains(((NPC) source).getId())) {
			if (!ownRandomsMap.containsKey(((NPC) source).getIndex()) && !otherRandomsMap.containsKey(((NPC) source).getIndex())) {//A potential Id check is probably redundant, since all NPCs most likely have a unique Index. Additionally, doesn't error out if lists are empty AFAIK, so no isEmpty() check first.

				//Frogs are the only event that spawn multiple Npcs. Not sure if they all interact with the player (most likely not; haven't been able to test yet though).
				//Don't add them to otherRandomMap if there's already a frog targeting the player to not hide the other frog Npcs if "Hide own kiss the frog" is enabled.
				//Will also hide other's frogs if both you and another player have the 'kiss the frog' event at the exact same time, and you only got your own hidden; or it will not hide theirs if you only got 'hide other kiss the frog' enabled. However, we accept that.
				if (!((FROGS_NPCS.contains(((NPC) source).getId())) &&
						(ownRandomsMap.containsValue(NpcID.FROG_5429) ||
								ownRandomsMap.containsValue(NpcID.FROG_5430) ||
								ownRandomsMap.containsValue(NpcID.FROG_5431) ||
								ownRandomsMap.containsValue(NpcID.FROG_5432) ||
								ownRandomsMap.containsValue(NpcID.FROG_5833) ||
								ownRandomsMap.containsValue(NpcID.FROG)))) {
					otherRandomsMap.put(((NPC) source).getIndex(), ((NPC) source).getId()); //Id is probs useful for e.g. the Frog random
				}
			}
		}
	}

	/*
	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned) { //Potentially use NpcSpawned event to instahide and then later on remove the hide, in case the Npc flashes on spawn? Still need further testing with current implementation to determine if it does indeed flash or not, but seems like it doesn't based on initial tests. Alternative: see shoulddraw code.
		}
		*/

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned) {
		if (EVENT_NPCS.contains(npcDespawned.getNpc().getId())) {
			if (ownRandomsMap.containsKey(npcDespawned.getNpc().getIndex())) {
				ownRandomsMap.remove(npcDespawned.getNpc().getIndex(), npcDespawned.getNpc().getId());
			}
			if (otherRandomsMap.containsKey(npcDespawned.getNpc().getIndex())) {
				otherRandomsMap.remove(npcDespawned.getNpc().getIndex(), npcDespawned.getNpc().getId());
			}
		}
	}

	@Provides
	RandomEventHiderConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(RandomEventHiderConfig.class);
	}

	@VisibleForTesting
	boolean shouldDraw(Renderable renderable, boolean drawingUI) {
		if (renderable instanceof NPC && EVENT_NPCS.contains(((NPC) renderable).getId()) && !client.isInInstancedRegion()) { //(Most) NPCs have separate IDs for their overworld counterparts (in contrast to their random event/instanced ones), but just to be sure, let's skip the instances.
			//Randoms might actually not be an instance though... Currently I can only find the maze random event on the map (which doesn't contain any EVENT_NPCS!)...
			//However, it should still be fine: Beekeeper uses a different Id, Sergeant Damien uses a different Id, Evil Bob uses a different Id,
			//the Freaky Forester uses a different Id, Leo uses a different Id, the Frog random does not teleport the played anymore,
			//the Pillory Guard likely uses a different Id and is irrelevant in his event, Flippa uses a different Id,
			//the Prison Pete random only has Bob inviting you (not in the random), the maze random doesn't contain any relevant Npcs,
			//Postie Pete doesn't show up in the Evil Twin random.
			NPC npc = (NPC) renderable;
			if (ownRandomsMap.containsKey(npc.getIndex())) { //Assume this is an own random event
				return !shouldHide(npc.getId(), true);
			}

			if (otherRandomsMap.containsKey(npc.getIndex())) { //Assume this is for other people's random events
				return !shouldHide(npc.getId(), false);
			}

			//TODO: Potentially use the following code to prevent flashing of the Npcs when they spawn in case they don't interact immediately (if this turns out to be a problem). Alternatively: use onNpcSpawned?
			/*
			if (!ownRandomsMap.containsKey(npc.getIndex()) && !otherRandomsMap.containsKey(npc.getIndex())) {
				if (shouldHide(npc.getId(), true) || shouldHide(npc.getId(), false)) {
					return false;
				}
			} */

			//Hide other frogs if a frog is on the ownRandomsMap or otherRandomsMap based on settings
			if (FROGS_NPCS.contains(((NPC) renderable).getId())) {
				if (ownRandomsMap.containsValue(NpcID.FROG_5429) ||
						ownRandomsMap.containsValue(NpcID.FROG_5430) ||
						ownRandomsMap.containsValue(NpcID.FROG_5431) ||
						ownRandomsMap.containsValue(NpcID.FROG_5432) ||
						ownRandomsMap.containsValue(NpcID.FROG_5833) ||
						ownRandomsMap.containsValue(NpcID.FROG)) {
					return !shouldHide(npc.getId(), true);
				}
				if (otherRandomsMap.containsValue(NpcID.FROG_5429) ||
						otherRandomsMap.containsValue(NpcID.FROG_5430) ||
						otherRandomsMap.containsValue(NpcID.FROG_5431) ||
						otherRandomsMap.containsValue(NpcID.FROG_5432) ||
						otherRandomsMap.containsValue(NpcID.FROG_5833) ||
						otherRandomsMap.containsValue(NpcID.FROG)) {
					return !shouldHide(npc.getId(), false);
				}
			}
		}
		return true;
	}

	private boolean shouldHide(int id, boolean OwnEvent) {
		if (OwnEvent) {
			switch (id) {
				case NpcID.BEE_KEEPER_6747:
					return hideOwnBeekeeper;
				case NpcID.SERGEANT_DAMIEN_6743:
					return hideOwnDrillDemon;
				case NpcID.FREAKY_FORESTER_6748:
					return hideOwnFreakyForester;
				case NpcID.FROG_5429:
				case NpcID.FROG_5430:
				case NpcID.FROG_5431:
				case NpcID.FROG_5432:
				case NpcID.FROG_5833:
				case NpcID.FROG:
					return hideOwnKissTheFrog;
				case NpcID.GENIE:
				case NpcID.GENIE_327:
					return hideOwnGenie;
				case NpcID.DR_JEKYLL:
				case NpcID.DR_JEKYLL_314:
					return hideOwnJekyllHyde;
				case NpcID.EVIL_BOB:
					return hideOwnEvilBob;
				case NpcID.EVIL_BOB_6754:
					return hideOwnPrisonPete;
				case NpcID.LEO_6746:
					return hideOwnGravedigger;
				case NpcID.MYSTERIOUS_OLD_MAN_6750:
				case NpcID.MYSTERIOUS_OLD_MAN_6751:
					return hideOwnMysteriousOldMan;
				case NpcID.MYSTERIOUS_OLD_MAN_6752:
					return hideOwnMaze; //TODO: find out if 6752 or 6753 is mime or maze
				case NpcID.MYSTERIOUS_OLD_MAN_6753:
					return hideOwnMime; //TODO: find out if 6752 or 6753 is mime or maze
				case NpcID.QUIZ_MASTER_6755:
					return hideOwnQuizMaster;
				case NpcID.DUNCE_6749:
					return hideOwnSurpriseExam;
				case NpcID.SANDWICH_LADY:
					return hideOwnSandwichLady;
				case NpcID.CAPT_ARNAV:
					return hideOwnCaptArnav;
				case NpcID.DRUNKEN_DWARF:
					return hideOwnDrunkenDwarf;
				case NpcID.FLIPPA_6744:
					return hideOwnPinball;
				case NpcID.GILES:
				case NpcID.GILES_5441:
				case NpcID.MILES:
				case NpcID.MILES_5440:
				case NpcID.NILES:
				case NpcID.NILES_5439:
					return hideOwnNiles;
				case NpcID.PILLORY_GUARD:
					return hideOwnPilloryGuard;
				case NpcID.POSTIE_PETE_6738:
					return hideOwnEvilTwin;
				case NpcID.RICK_TURPENTINE:
				case NpcID.RICK_TURPENTINE_376:
					return hideOwnRickTurpentine;
				case NpcID.STRANGE_PLANT:
					return hideOwnStrangePlant;
				case 5216:
					return hideOwnBenny;//TEST: benny test varrock square
			}
		} else if (!OwnEvent) {
			switch (id) {
				case NpcID.BEE_KEEPER_6747:
					return hideOtherBeekeeper;
				case NpcID.SERGEANT_DAMIEN_6743:
					return hideOtherDrillDemon;
				case NpcID.FREAKY_FORESTER_6748:
					return hideOtherFreakyForester;
				case NpcID.FROG_5429:
				case NpcID.FROG_5430:
				case NpcID.FROG_5431:
				case NpcID.FROG_5432:
				case NpcID.FROG_5833:
				case NpcID.FROG:
					return hideOtherKissTheFrog;
				case NpcID.GENIE:
				case NpcID.GENIE_327:
					return hideOtherGenie;
				case NpcID.DR_JEKYLL:
				case NpcID.DR_JEKYLL_314:
					return hideOtherJekyllHyde;
				case NpcID.EVIL_BOB: //Evil Bob random, see https://discord.com/channels/177206626514632704/269673599554551808/1057450627774562394
					return hideOtherEvilBob;
				case NpcID.EVIL_BOB_6754: //Prison Pete, see https://discord.com/channels/177206626514632704/269673599554551808/1057450627774562394
					return hideOtherPrisonPete;
				case NpcID.LEO_6746:
					return hideOtherGravedigger;
				case NpcID.MYSTERIOUS_OLD_MAN_6750: //Mysterious Old Man (Rick Turpentine style), see https://discord.com/channels/177206626514632704/269673599554551808/1057448583881826374
				case NpcID.MYSTERIOUS_OLD_MAN_6751:
					return hideOtherMysteriousOldMan;
				case NpcID.MYSTERIOUS_OLD_MAN_6752:
					return hideOtherMaze; //TODO: find out if 6752 or 6753 is mime or maze
				case NpcID.MYSTERIOUS_OLD_MAN_6753:
					return hideOtherMime; //TODO: find out if 6752 or 6753 is mime or maze
				case NpcID.QUIZ_MASTER_6755:
					return hideOtherQuizMaster;
				case NpcID.DUNCE_6749:
					return hideOtherSurpriseExam;
				case NpcID.SANDWICH_LADY:
					return hideOtherSandwichLady;
				case NpcID.CAPT_ARNAV:
					return hideOtherCaptArnav;
				case NpcID.DRUNKEN_DWARF:
					return hideOtherDrunkenDwarf;
				case NpcID.FLIPPA_6744:
					return hideOtherPinball;
				case NpcID.GILES:
				case NpcID.GILES_5441:
				case NpcID.MILES:
				case NpcID.MILES_5440:
				case NpcID.NILES:
				case NpcID.NILES_5439:
					return hideOtherNiles;
				case NpcID.PILLORY_GUARD:
					return hideOtherPilloryGuard;
				case NpcID.POSTIE_PETE_6738:
					return hideOtherEvilTwin;
				case NpcID.RICK_TURPENTINE:
				case NpcID.RICK_TURPENTINE_376:
					return hideOtherRickTurpentine;
				case NpcID.STRANGE_PLANT:
					return hideOtherStrangePlant;
				case 5216:
					return hideOtherBenny;//TEST: benny test varrock square
			}
		}
		return false;
	}
}
