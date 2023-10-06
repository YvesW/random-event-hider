package com.ywcode.randomeventhider;

import com.google.common.annotations.*;
import com.google.common.collect.*;
import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.*;
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
		tags = {"random event,hider,random event hider,ra hider,messenger,strange plant,poof,smoke,star mining,shooting stars,forestry"}
)

public class RandomEventHiderPlugin extends Plugin {

	private static final Set<Integer> RANDOM_EVENT_NPCS = ImmutableSet.of(
			NpcID.BEE_KEEPER_6747,
			NpcID.CAPT_ARNAV,
			NpcID.DR_JEKYLL, NpcID.DR_JEKYLL_314,
			NpcID.DRUNKEN_DWARF,
			NpcID.DUNCE_6749,
			NpcID.EVIL_BOB, NpcID.EVIL_BOB_6754,
			NpcID.FLIPPA_6744,
			NpcID.FREAKY_FORESTER_6748,
			NpcID.FROG_5429, NpcID.FROG_5430, NpcID.FROG_5431, NpcID.FROG_5432, NpcID.FROG, NpcID.FROG_PRINCE, NpcID.FROG_PRINCESS,
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
			NpcID.SERGEANT_DAMIEN_6743
	);

	private static final Set<Integer> MESSENGER_NPCS = ImmutableSet.of(
			//Regicide, The Frozen Door, and 4x Into the Tombs respectively
			NpcID.KINGS_MESSENGER, NpcID.MESSENGER, NpcID.MESSENGER_11814, NpcID.MESSENGER_11815, NpcID.MESSENGER_11816, NpcID.MESSENGER_11817
	);

	private static final Set<Integer> EVENT_NPCS; // Combine sets because everything that happens for RANDOM_EVENT_NPCS should also happen for MESSENGER_NPCS and they are technically different types of NPCs.
	static {
		Set<Integer> combinedSets = new HashSet<>();
		combinedSets.addAll(RANDOM_EVENT_NPCS);
		combinedSets.addAll(MESSENGER_NPCS);
		EVENT_NPCS = Collections.unmodifiableSet(combinedSets);
	}

	private static final Set<Integer> FROGS_NPCS = ImmutableSet.of(
			NpcID.FROG_5429, NpcID.FROG_5430, NpcID.FROG_5431, NpcID.FROG_5432, NpcID.FROG, NpcID.FROG_PRINCE, NpcID.FROG_PRINCESS
	);

	private static final int POOF_SOUND = 1930;
	private static final int DRUNKEN_DWARF_SOUND = 2297;
	private static final int EVIL_BOB_MEOW = 333; //Apparently also cat hiss
	private static final int FROG_SPLASH = 838; //Thanks veknow
	private static final int POOF_GRAPHICSOBJECT_ID = 86; //Apparently called GREY_BUBBLE_TELEPORT in GraphicID.java
	private static final int FROG_REALM_REGIONID = 9802;

	// ------------- Wall of config vars -------------
	// Vars are quite heavily cached so could also just config.configKey tbh
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
	private boolean hideOwnSurpriseExam;
	private boolean hideAllStrangePlant;
	private boolean muteDwarf;
	private boolean muteBob;
	private boolean muteFrogs;
	private boolean mutePoof;
	private boolean muteOtherRandomSounds;
	private boolean hidePoof;
	private boolean hideOtherMessengers;
	// ------------- End of wall of config vars -------------

	private final LinkedHashMap<Integer, Integer> ownRandomsMap = new LinkedHashMap<Integer, Integer>();
	private final LinkedHashMap<Integer, Integer> otherRandomsMap = new LinkedHashMap<Integer, Integer>();
	private final LinkedHashMap<WorldPoint, Integer> spawnedDespawnedNpcLocations = new LinkedHashMap<WorldPoint, Integer>();
	private final LinkedHashMap<WorldPoint, Integer> spawnedDespawnedNpcLocationsDeletion = new LinkedHashMap<WorldPoint, Integer>();
	//Should maybe use a custom class RandomEvent with stuff such as npcIndex, npcId, interactingWith, npcSpawnedLocation, gameCycleSpawned, npcDespawnedLocation, gameCycleDespawned

	private static boolean shouldCleanMap = false;
	private static int currentRegionID = 0;

	private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

	@Inject
	private Client client;

	@Inject
	private RandomEventHiderConfig config;

	@Inject
	private Hooks hooks;

	@Override
	public void startUp() {
		hooks.registerRenderableDrawListener(drawListener);
		updateConfig();
	}

	@Override
	public void shutDown() {
		hooks.unregisterRenderableDrawListener(drawListener);
		ownRandomsMap.clear();
		otherRandomsMap.clear();
		spawnedDespawnedNpcLocations.clear();
		spawnedDespawnedNpcLocationsDeletion.clear();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged) {
		if (configChanged.getGroup().equals("RandomEventHider")) {
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
		hideOwnSurpriseExam = config.hideOwnSurpriseExam();
		hideAllStrangePlant = config.hideAllStrangePlant();
		muteDwarf = config.muteDwarf();
		muteBob = config.muteBob();
		muteFrogs = config.muteFrogs();
		mutePoof = config.mutePoof();
		muteOtherRandomSounds = config.muteOtherRandomSounds();
		hidePoof = config.hidePoof();
		hideOtherMessengers = config.hideOtherMessengers();
	}

	@Subscribe
	public void onInteractingChanged(InteractingChanged interactingChanged) {
		Actor source = interactingChanged.getSource();
		Actor target = interactingChanged.getTarget();
		Player player = client.getLocalPlayer();

		if (player != null && (source instanceof NPC) && (target instanceof Player) && EVENT_NPCS.contains(((NPC) source).getId())) {
			int sourceIndex = ((NPC) source).getIndex();
			int sourceId = ((NPC) source).getId();

			//This is the player's own random event, assuming the random immediately interacts with the player when spawned.
			//Won't get marked as someone else's NPC later on (even if other people interact with it), since it's already on this map.
			//Assuming random event NPCs for other players immediately interact with another user when they spawn, it'll get on the "other" map, even if the player talks to someone else's random event later on.
			//Also write down the index in case there are multiple random events with the same NPC id on screen
			if (target == player && !ownRandomsMap.containsKey(sourceIndex) && !otherRandomsMap.containsKey(sourceIndex)) { //A potential Id check is redundant, since all NPCs have a unique Index. Additionally, doesn't error out if maps are empty AFAIK, so no isEmpty() check first.
				ownRandomsMap.put(sourceIndex, sourceId); //Id is probably useful for e.g. the Frog random
			}

			//This is someone else's random event, assuming the random immediately interacts with the target when spawned.
			//Won't get marked as your own NPC later on (even if the player interacts with it), since it's already on this map.
			//Also write down the index in case there are multiple random events with the same NPC id on screen
			if (target != player && !ownRandomsMap.containsKey(sourceIndex) && !otherRandomsMap.containsKey(sourceIndex)) { //A potential Id check is redundant, since all NPCs have a unique Index. Additionally, doesn't error out if maps are empty AFAIK, so no isEmpty() check first.
				//Frogs are the only event that spawn multiple Npcs. Not sure if they all interact with the player (very likely not; haven't been able to 100% properly research this yet though).
				//Don't add them to otherRandomMap if there's already a frog targeting the player to not hide the other frog Npcs if "Hide own kiss the frog" is enabled.
				//Will also hide other's frogs if both you and another player have the 'kiss the frog' event at the exact same time, and you only got your own hidden; or it will not hide theirs if you only got 'hide other kiss the frog' enabled. However, we accept that.
				if (! (FROGS_NPCS.contains(sourceId) &&	mapContainsFrogId(ownRandomsMap)) ) {
					otherRandomsMap.put(sourceIndex, sourceId); //sourceId is probs useful for e.g. the Frog random
				}
			}
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged) {
		if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN || gameStateChanged.getGameState() == GameState.HOPPING) {
			ownRandomsMap.clear();
			otherRandomsMap.clear();
			spawnedDespawnedNpcLocations.clear();
			spawnedDespawnedNpcLocationsDeletion.clear();
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned) {
		int npcSpawnedId = npcSpawned.getNpc().getId();
		int npcSpawnedIndex = npcSpawned.getNpc().getIndex();
		Actor npcSpawnedActor = npcSpawned.getActor();
		addPoofLocationToList(npcSpawnedId, npcSpawnedIndex, npcSpawnedActor, true);
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned) {
		int npcDespawnedId = npcDespawned.getNpc().getId();
		int npcDespawnedIndex = npcDespawned.getNpc().getIndex();
		Actor npcDespawnedActor = npcDespawned.getActor();
		addPoofLocationToList(npcDespawnedId, npcDespawnedIndex, npcDespawnedActor, false);

		if (EVENT_NPCS.contains(npcDespawnedId)) {
			if (ownRandomsMap.containsKey(npcDespawnedIndex)) {
				ownRandomsMap.remove(npcDespawnedIndex, npcDespawnedId);
			}
			if (otherRandomsMap.containsKey(npcDespawnedIndex)) {
				otherRandomsMap.remove(npcDespawnedIndex, npcDespawnedId);
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick gameTick) {
		//Iterate through Map and remove entries that are >5 gameticks (150 GameCycles/ClientTicks) old
		//Alternative is to e.g. add the GraphicsObjects to a list and iterate through them until getPrevious == null conform conform https://discord.com/channels/301497432909414422/419891709883973642/740262232432050247 but that did not seem to work that well.
		//However, would also have to remove frogs etc. still this way (they spawn multiple Npcs, but only one GraphicsObject Poof)!
		currentRegionID = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID(); //Somewhat caching this here instead of putting it into ShouldHideBasedOnMaps because then it can get called multiple times per gameCycle around e.g. prif stars.
		if (!spawnedDespawnedNpcLocations.isEmpty()) {
			int currentGameCycle = client.getGameCycle();
			for (Map.Entry<WorldPoint, Integer> entry : spawnedDespawnedNpcLocations.entrySet()) {
				int npcGameCycle = entry.getValue();
				if (currentGameCycle - npcGameCycle > 150) {
					spawnedDespawnedNpcLocationsDeletion.put(entry.getKey(), entry.getValue());
					shouldCleanMap = true;
				}
			}
			cleanupMap(); //Alternative is to use an iterator
		}
	}

	@Subscribe
	public void onSoundEffectPlayed(SoundEffectPlayed soundEffectPlayed) {
		int soundId = soundEffectPlayed.getSoundId();
		if (soundEffectPlayed.getSource() != null && soundEffectPlayed.getSource() instanceof NPC) {
			int sourceNpcId = ((NPC) soundEffectPlayed.getSource()).getId();
			if ((EVENT_NPCS.contains(sourceNpcId) || (sourceNpcId == NpcID.STRANGE_PLANT)) && shouldMute(soundId)) {
				soundEffectPlayed.consume();
			}
		}
		//POOF_SOUND source is null apparently (found through ingame experimentation)
		if ((soundId == POOF_SOUND || soundId == EVIL_BOB_MEOW || soundId == DRUNKEN_DWARF_SOUND || soundId == FROG_SPLASH) && shouldMute(soundId)) {
			soundEffectPlayed.consume();
		}
	}

	@Subscribe
	public void onAreaSoundEffectPlayed(AreaSoundEffectPlayed areaSoundEffectPlayed) {
		int soundId = areaSoundEffectPlayed.getSoundId();
		if (areaSoundEffectPlayed.getSource() != null && areaSoundEffectPlayed.getSource() instanceof NPC) {
			int sourceNpcId = ((NPC) areaSoundEffectPlayed.getSource()).getId();
			if ((EVENT_NPCS.contains(sourceNpcId) || (sourceNpcId == NpcID.STRANGE_PLANT)) && shouldMute(soundId)) {
				areaSoundEffectPlayed.consume();
			}
		}
		//POOF_SOUND source is null apparently (found through ingame experimentation)
		if ((soundId == POOF_SOUND || soundId == EVIL_BOB_MEOW || soundId == DRUNKEN_DWARF_SOUND || soundId == FROG_SPLASH) && shouldMute(soundId)) {
			areaSoundEffectPlayed.consume();
		}
	}

	@VisibleForTesting
	boolean shouldDraw(Renderable renderable, boolean drawingUI) {
		if (renderable instanceof NPC) {
			NPC npc = (NPC) renderable;
			int npcId = npc.getId();
			if (EVENT_NPCS.contains(npcId) || (npcId == NpcID.STRANGE_PLANT)) { // Instance check removed because PoH can still have random events and the events have different overworld NPC ids anyway.
				//Beekeeper uses a different Id, Sergeant Damien uses a different Id, Evil Bob uses a different Id,
				//the Freaky Forester uses a different Id, Leo uses a different Id, the Frog random does not teleport the played anymore,
				//the Pillory Guard likely uses a different Id and is irrelevant in his event, Flippa uses a different Id,
				//the Prison Pete random only has Bob inviting you (not in the random), the maze random doesn't contain any relevant Npcs,
				//Postie Pete doesn't show up in the Evil Twin random.
				int npcIndex = npc.getIndex();
				return !shouldHideBasedOnMaps(npcIndex, npcId);
			}
		}
		if (renderable instanceof GraphicsObject) {
			GraphicsObject graphicsObject = (GraphicsObject) renderable;
			if (graphicsObject.getId() == POOF_GRAPHICSOBJECT_ID) {
				//This code is written with the assumption that POOF_GRAPHICSOBJECT_ID is used for multiple npcs, e.g. imps, double agents etc.
				//Otherwise a simple Id check would have been enough. If the current implementation turns out to be too crappy, I'll just swap to that.
				WorldPoint graphicsObjectWorldPoint = WorldPoint.fromLocalInstance(client, graphicsObject.getLocation());
				if (spawnedDespawnedNpcLocations.containsKey(graphicsObjectWorldPoint)) {
					graphicsObject.setFinished(true);
					return !hidePoof;
				}
			}
		}
		return true;
	}

	private void addPoofLocationToList(int npcId, int npcIndex, Actor npcActor, boolean NpcSpawned) {
		//If an Npc is hidden via the plugin, the poof should happen on the NpcSpawned location (will always happen due to code to prevent Npc flashing)
		//If an Npc is not hidden via the plugin, the poof should happen briefly on the NpcSpawned location (unless both own and other are not hidden!) and also on the NpcDespawned location
		//Tldr: Poof always happens on the spawn location (except when both own and other are NOT hidden), but only on the despawn location if the Npc is not hidden.
		//Edit: turns out via research that NpcDespawned always creates a poof
		if (EVENT_NPCS.contains(npcId) || npcId == NpcID.STRANGE_PLANT) {
			WorldPoint npcWorldPoint = npcActor.getWorldLocation(); // TODO: Potentially try 	WorldPoint npcWorldPoint = WorldPoint.fromLocalInstance(client, npcActor.getLocalLocation()); at some point to fix the poof in the POH?
			if (NpcSpawned && shouldHideBasedOnMaps(npcIndex, npcId)) {
				spawnedDespawnedNpcLocations.put(npcWorldPoint, client.getGameCycle());
			}
			if (!NpcSpawned /*&& !shouldHideBasedOnMaps(npcIndex, npcId)*/) {
				spawnedDespawnedNpcLocations.put(npcWorldPoint, client.getGameCycle());
			}
		}
	}

	private void cleanupMap() {
		if (shouldCleanMap) {
			for (Map.Entry<WorldPoint, Integer> entry : spawnedDespawnedNpcLocationsDeletion.entrySet()) {
				spawnedDespawnedNpcLocations.remove(entry.getKey(), entry.getValue());
			}
			spawnedDespawnedNpcLocationsDeletion.clear();
			shouldCleanMap = false;
		}
	}

	private boolean mapContainsFrogId(LinkedHashMap Map) {
		return Map.containsValue(NpcID.FROG_5429) ||
				Map.containsValue(NpcID.FROG_5430) ||
				Map.containsValue(NpcID.FROG_5431) ||
				Map.containsValue(NpcID.FROG_5432) ||
				Map.containsValue(NpcID.FROG) ||
				Map.containsValue(NpcID.FROG_PRINCE) ||
				Map.containsValue(NpcID.FROG_PRINCESS);
	}

	private boolean shouldHideBasedOnMaps(int npcIndex, int npcId) {
		if (currentRegionID == FROG_REALM_REGIONID) {
			//Disable in frog realm so https://github.com/YvesW/random-event-hider/issues/4 doesn't happen
			return false;
		}

		if (ownRandomsMap.containsKey(npcIndex)) {
			return shouldHide(npcId, true);
		}
		if (otherRandomsMap.containsKey(npcIndex)) {
			return shouldHide(npcId, false);
		}

		//Hide other frogs if a frog is on the ownRandomsMap or otherRandomsMap based on settings
		if (FROGS_NPCS.contains(npcId)) {
			if (mapContainsFrogId(ownRandomsMap)) {
				return shouldHide(npcId, true);
			}
			if (mapContainsFrogId(otherRandomsMap)) {
				return shouldHide(npcId, false);
			}  //return (shouldHide(npcId, true) || shouldHide(npcId, false)); 	//At this point there is no frog on any of the maps. Hide to prevent flashing. => However, not needed since this return statement is already listed a couple lines further down.
		}

		//Strange plant does not interact with any person, so we'll hide them all if hideOwnStrangePlant is enabled.
		if (npcId == NpcID.STRANGE_PLANT) {
			return hideAllStrangePlant;
		}

		//Strange plant has already been handled, so it doesn't need to be excluded.
		//Npc is not on any of the maps, so no map.containsKey() check needed here.
		return (shouldHide(npcId, true) || shouldHide(npcId, false)); //Prevent NPCs from flashing when showing up.
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
				case NpcID.FROG_PRINCE:
				case NpcID.FROG_PRINCESS:
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
					return hideOwnMaze; //6752 is maze
				case NpcID.MYSTERIOUS_OLD_MAN_6753:
					return hideOwnMime; //6753 is mime
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
			}
		} else { //if (!OwnEvent)
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
				case NpcID.FROG_PRINCE:
				case NpcID.FROG_PRINCESS:
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
					return hideOtherMaze; //6752 is maze https://discord.com/channels/177206626514632704/269673599554551808/1059302464622448650
				case NpcID.MYSTERIOUS_OLD_MAN_6753:
					return hideOtherMime; //6753 is mime https://discord.com/channels/177206626514632704/269673599554551808/1059302464622448650
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
				case NpcID.KINGS_MESSENGER:
				case NpcID.MESSENGER:
				case NpcID.MESSENGER_11814:
				case NpcID.MESSENGER_11815:
				case NpcID.MESSENGER_11816:
				case NpcID.MESSENGER_11817:
					return hideOtherMessengers;
			}
		}
		return false;
	}

	private boolean shouldMute(int soundId) {
		switch (soundId) {
			case DRUNKEN_DWARF_SOUND:
				return muteDwarf;
			case EVIL_BOB_MEOW:
				return muteBob;
			case FROG_SPLASH:
				return muteFrogs;
			case POOF_SOUND:
				return mutePoof;
		}
		return muteOtherRandomSounds;
	}

	@Provides
	RandomEventHiderConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(RandomEventHiderConfig.class);
	}
}