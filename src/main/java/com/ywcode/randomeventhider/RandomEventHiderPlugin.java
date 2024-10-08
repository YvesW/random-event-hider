package com.ywcode.randomeventhider;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.GraphicsObject;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.Player;
import net.runelite.api.Renderable;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AreaSoundEffectPlayed;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.SoundEffectPlayed;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@PluginDescriptor(
		name = "Random Event Hider",
		description = "Adds the ability to hide specific random events that interact with you or with other players.",
		tags = {"random event,hider,random event hider,ra hider,messenger,strange plant,poof,smoke,star mining,shooting stars,forestry,count check,wgs,while guthix sleeps,guardian of armadyl"}
)

public class RandomEventHiderPlugin extends Plugin {

	private static final Set<Integer> RANDOM_EVENT_NPCS = ImmutableSet.of(
			NpcID.BEE_KEEPER_6747,
			NpcID.CAPT_ARNAV,
			NpcID.COUNT_CHECK_12551, NpcID.COUNT_CHECK_12552,
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
			//Regicide
			NpcID.KINGS_MESSENGER,
			//The Frozen Door
			NpcID.MESSENGER,
			//4x Into the Tombs/Varlamore
			NpcID.MESSENGER_11814, NpcID.MESSENGER_11815, NpcID.MESSENGER_11816, NpcID.MESSENGER_11817,
			//WGS Guardian of Armadyl messenger
			NpcID.GUARDIAN_OF_ARMADYL_13509
	);

	private static final Set<Integer> EVENT_NPCS; // Combine sets because everything that happens for RANDOM_EVENT_NPCS should also happen for MESSENGER_NPCS and they are technically different types of NPCs.
	static {
		final Set<Integer> combinedSets = new HashSet<>();
		combinedSets.addAll(RANDOM_EVENT_NPCS);
		combinedSets.addAll(MESSENGER_NPCS);
		EVENT_NPCS = ImmutableSet.copyOf(combinedSets);
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
	// Vars are quite heavily cached so could probably just config.configKey(). However, the best practice behavior in plugins is to have a bunch of variables to store the results of the config methods, and check it in startUp/onConfigChanged. It feels redundant, but it's better than hitting the reflective calls every frame. --LlemonDuck
	private static boolean hideOtherBeekeeper;
	private static boolean hideOtherCaptArnav;
	private static boolean hideOtherNiles;
	private static boolean hideOtherCountCheck;
	private static boolean hideOtherDrillDemon;
	private static boolean hideOtherDrunkenDwarf;
	private static boolean hideOtherEvilBob;
	private static boolean hideOtherEvilTwin;
	private static boolean hideOtherFreakyForester;
	private static boolean hideOtherGenie;
	private static boolean hideOtherGravedigger;
	private static boolean hideOtherJekyllHyde;
	private static boolean hideOtherKissTheFrog;
	private static boolean hideOtherMaze;
	private static boolean hideOtherMime;
	private static boolean hideOtherMysteriousOldMan;
	private static boolean hideOtherPilloryGuard;
	private static boolean hideOtherPinball;
	private static boolean hideOtherPrisonPete;
	private static boolean hideOtherQuizMaster;
	private static boolean hideOtherRickTurpentine;
	private static boolean hideOtherSandwichLady;
	private static boolean hideOtherSurpriseExam;
	private static boolean hideOwnBeekeeper;
	private static boolean hideOwnCaptArnav;
	private static boolean hideOwnCountCheck;
	private static boolean hideOwnNiles;
	private static boolean hideOwnDrillDemon;
	private static boolean hideOwnDrunkenDwarf;
	private static boolean hideOwnEvilBob;
	private static boolean hideOwnEvilTwin;
	private static boolean hideOwnFreakyForester;
	private static boolean hideOwnGenie;
	private static boolean hideOwnGravedigger;
	private static boolean hideOwnJekyllHyde;
	private static boolean hideOwnKissTheFrog;
	private static boolean hideOwnMaze;
	private static boolean hideOwnMime;
	private static boolean hideOwnMysteriousOldMan;
	private static boolean hideOwnPilloryGuard;
	private static boolean hideOwnPinball;
	private static boolean hideOwnPrisonPete;
	private static boolean hideOwnQuizMaster;
	private static boolean hideOwnRickTurpentine;
	private static boolean hideOwnSandwichLady;
	private static boolean hideOwnSurpriseExam;
	private static boolean hideAllStrangePlant;
	private static boolean muteDwarf;
	private static boolean muteBob;
	private static boolean muteFrogs;
	private static boolean mutePoof;
	private static boolean muteOtherRandomSounds;
	private static boolean hidePoof;
	private static boolean hideOtherMessengers;
	// ------------- End of wall of config vars -------------

	private static final Map<Integer, Integer> ownRandomsMap = new LinkedHashMap<>();
	private static final Map<Integer, Integer> otherRandomsMap = new LinkedHashMap<>();
	private static final Map<WorldPoint, Integer> spawnedDespawnedNpcLocations = new LinkedHashMap<>();
	//Not sure why I used LinkedHashMaps here instead of regular HashMaps, since the order probably does not matter. Considering it was my first plugin, I might not have thought about it. There should not be a significant performance difference between HashMap and LinkedHashMap anyway, so I'm not going to touch it as testing this plugin ingame is awful.
	//Should maybe use a custom class RandomEvent with stuff such as npcIndex, npcId, interactingWith, npcSpawnedLocation, tickCountSpawned, npcDespawnedLocation, tickCountDespawned

	private static int currentRegionID; //0 by default

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
		hideOtherCountCheck = config.hideOtherCountCheck();
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
		hideOwnCountCheck = config.hideOwnCountCheck();
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
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned) {
		int npcSpawnedId = npcSpawned.getNpc().getId();
		int npcSpawnedIndex = npcSpawned.getNpc().getIndex();
		Actor npcSpawnedActor = npcSpawned.getActor();
		addPoofLocationToMap(npcSpawnedId, npcSpawnedIndex, npcSpawnedActor);
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned) {
		int npcDespawnedId = npcDespawned.getNpc().getId();
		int npcDespawnedIndex = npcDespawned.getNpc().getIndex();
		Actor npcDespawnedActor = npcDespawned.getActor();
		removePoofLocationFromMap(npcDespawnedId, npcDespawnedIndex, npcDespawnedActor);

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
		int currentTickCount = client.getTickCount();
		spawnedDespawnedNpcLocations.values().removeIf(value -> currentTickCount - value > 5);
		//The set is backed by the map, so changes to the map are reflected in the set, and vice-versa as can be read in the entrySet javadoc or https://stackoverflow.com/questions/1884889/iterating-over-and-removing-from-a-map/29187813#29187813
		//The same applies to values() as can be read here: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Map.html#values() (removeIf traverses all elements of the collection using its iterator() and each matching element is removed using Iterator.remove())
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

	private void addPoofLocationToMap(int npcId, int npcIndex, Actor npcActor) {
		addRemovePoofLocationToMap(npcId, npcIndex, npcActor, true);
	}

	private void addRemovePoofLocationToMap(int npcId, int npcIndex, Actor npcActor, boolean NpcSpawned) {
		//If an Npc is hidden via the plugin, the poof should happen on the NpcSpawned location (will always happen due to code to prevent Npc flashing)
		//If an Npc is not hidden via the plugin, the poof should happen briefly on the NpcSpawned location (unless both own and other are not hidden!) and also on the NpcDespawned location
		//Tldr: Poof always happens on the spawn location (except when both own and other are NOT hidden), but only on the despawn location if the Npc is not hidden.
		//Edit: turns out via research that NpcDespawned always creates a poof
		if (EVENT_NPCS.contains(npcId) || npcId == NpcID.STRANGE_PLANT) {
			WorldPoint npcWorldPoint = WorldPoint.fromLocalInstance(client, npcActor.getLocalLocation());
			if (NpcSpawned && shouldHideBasedOnMaps(npcIndex, npcId)) {
				spawnedDespawnedNpcLocations.put(npcWorldPoint, client.getTickCount());
			}
			if (!NpcSpawned /*&& !shouldHideBasedOnMaps(npcIndex, npcId)*/) {
				spawnedDespawnedNpcLocations.put(npcWorldPoint, client.getTickCount());
			}
		}
	}

	private void removePoofLocationFromMap(int npcId, int npcIndex, Actor npcActor) {
		addRemovePoofLocationToMap(npcId, npcIndex, npcActor, false);
	}

	private boolean mapContainsFrogId(Map<Integer, Integer> Map) {
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

		//Strange plant does not interact with any person, so we'll hide them all if hideAllStrangePlant is enabled.
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
				case NpcID.CAPT_ARNAV:
					return hideOwnCaptArnav;
				case NpcID.GILES:
				case NpcID.GILES_5441:
				case NpcID.MILES:
				case NpcID.MILES_5440:
				case NpcID.NILES:
				case NpcID.NILES_5439:
					return hideOwnNiles;
				case NpcID.COUNT_CHECK_12551:
				case NpcID.COUNT_CHECK_12552:
					return hideOwnCountCheck;
				case NpcID.SERGEANT_DAMIEN_6743:
					return hideOwnDrillDemon;
				case NpcID.DRUNKEN_DWARF:
					return hideOwnDrunkenDwarf;
				case NpcID.EVIL_BOB: //Evil Bob random, see https://discord.com/channels/177206626514632704/269673599554551808/1057450627774562394
					return hideOwnEvilBob;
				case NpcID.POSTIE_PETE_6738:
					return hideOwnEvilTwin;
				case NpcID.FREAKY_FORESTER_6748:
					return hideOwnFreakyForester;
				case NpcID.GENIE:
				case NpcID.GENIE_327:
					return hideOwnGenie;
				case NpcID.LEO_6746:
					return hideOwnGravedigger;
				case NpcID.DR_JEKYLL:
				case NpcID.DR_JEKYLL_314:
					return hideOwnJekyllHyde;
				case NpcID.FROG_5429:
				case NpcID.FROG_5430:
				case NpcID.FROG_5431:
				case NpcID.FROG_5432:
				case NpcID.FROG_PRINCE:
				case NpcID.FROG_PRINCESS:
				case NpcID.FROG:
					return hideOwnKissTheFrog;
				case NpcID.MYSTERIOUS_OLD_MAN_6752:
					return hideOwnMaze; //6752 is maze https://discord.com/channels/177206626514632704/269673599554551808/1059302464622448650
				case NpcID.MYSTERIOUS_OLD_MAN_6753:
					return hideOwnMime; //6753 is mime https://discord.com/channels/177206626514632704/269673599554551808/1059302464622448650
				case NpcID.MYSTERIOUS_OLD_MAN_6750: //Mysterious Old Man (Rick Turpentine style), see https://discord.com/channels/177206626514632704/269673599554551808/1057448583881826374
				case NpcID.MYSTERIOUS_OLD_MAN_6751:
					return hideOwnMysteriousOldMan;
				case NpcID.PILLORY_GUARD:
					return hideOwnPilloryGuard;
				case NpcID.FLIPPA_6744:
					return hideOwnPinball;
				case NpcID.EVIL_BOB_6754: //Prison Pete, see https://discord.com/channels/177206626514632704/269673599554551808/1057450627774562394
					return hideOwnPrisonPete;
				case NpcID.QUIZ_MASTER_6755:
					return hideOwnQuizMaster;
				case NpcID.RICK_TURPENTINE:
				case NpcID.RICK_TURPENTINE_376:
					return hideOwnRickTurpentine;
				case NpcID.SANDWICH_LADY:
					return hideOwnSandwichLady;
				case NpcID.DUNCE_6749:
					return hideOwnSurpriseExam;
			}
		} else { //if (!OwnEvent)
			switch (id) {
				case NpcID.BEE_KEEPER_6747:
					return hideOtherBeekeeper;
				case NpcID.CAPT_ARNAV:
					return hideOtherCaptArnav;
				case NpcID.GILES:
				case NpcID.GILES_5441:
				case NpcID.MILES:
				case NpcID.MILES_5440:
				case NpcID.NILES:
				case NpcID.NILES_5439:
					return hideOtherNiles;
				case NpcID.COUNT_CHECK_12551:
				case NpcID.COUNT_CHECK_12552:
					return hideOtherCountCheck;
				case NpcID.SERGEANT_DAMIEN_6743:
					return hideOtherDrillDemon;
				case NpcID.DRUNKEN_DWARF:
					return hideOtherDrunkenDwarf;
				case NpcID.EVIL_BOB: //Evil Bob random, see https://discord.com/channels/177206626514632704/269673599554551808/1057450627774562394
					return hideOtherEvilBob;
				case NpcID.POSTIE_PETE_6738:
					return hideOtherEvilTwin;
				case NpcID.FREAKY_FORESTER_6748:
					return hideOtherFreakyForester;
				case NpcID.GENIE:
				case NpcID.GENIE_327:
					return hideOtherGenie;
				case NpcID.LEO_6746:
					return hideOtherGravedigger;
				case NpcID.DR_JEKYLL:
				case NpcID.DR_JEKYLL_314:
					return hideOtherJekyllHyde;
				case NpcID.FROG_5429:
				case NpcID.FROG_5430:
				case NpcID.FROG_5431:
				case NpcID.FROG_5432:
				case NpcID.FROG_PRINCE:
				case NpcID.FROG_PRINCESS:
				case NpcID.FROG:
					return hideOtherKissTheFrog;
				case NpcID.MYSTERIOUS_OLD_MAN_6752:
					return hideOtherMaze; //6752 is maze https://discord.com/channels/177206626514632704/269673599554551808/1059302464622448650
				case NpcID.MYSTERIOUS_OLD_MAN_6753:
					return hideOtherMime; //6753 is mime https://discord.com/channels/177206626514632704/269673599554551808/1059302464622448650
				case NpcID.MYSTERIOUS_OLD_MAN_6750: //Mysterious Old Man (Rick Turpentine style), see https://discord.com/channels/177206626514632704/269673599554551808/1057448583881826374
				case NpcID.MYSTERIOUS_OLD_MAN_6751:
					return hideOtherMysteriousOldMan;
				case NpcID.PILLORY_GUARD:
					return hideOtherPilloryGuard;
				case NpcID.FLIPPA_6744:
					return hideOtherPinball;
				case NpcID.EVIL_BOB_6754: //Prison Pete, see https://discord.com/channels/177206626514632704/269673599554551808/1057450627774562394
					return hideOtherPrisonPete;
				case NpcID.QUIZ_MASTER_6755:
					return hideOtherQuizMaster;
				case NpcID.RICK_TURPENTINE:
				case NpcID.RICK_TURPENTINE_376:
					return hideOtherRickTurpentine;
				case NpcID.SANDWICH_LADY:
					return hideOtherSandwichLady;
				case NpcID.DUNCE_6749:
					return hideOtherSurpriseExam;
				//The messengers are the only NpcIDs that are other-specific, i.e. not also listed above as potential 'own' events
				case NpcID.KINGS_MESSENGER:
				case NpcID.MESSENGER:
				case NpcID.MESSENGER_11814:
				case NpcID.MESSENGER_11815:
				case NpcID.MESSENGER_11816:
				case NpcID.MESSENGER_11817:
				case NpcID.GUARDIAN_OF_ARMADYL_13509:
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