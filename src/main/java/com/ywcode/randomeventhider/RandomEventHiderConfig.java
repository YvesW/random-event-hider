package com.ywcode.randomeventhider;

import net.runelite.client.config.*;

@ConfigGroup("RandomEventHider")
public interface RandomEventHiderConfig extends Config
{
	@ConfigSection(
			name = "Others' random events",
			description = "Hide the random events of other players",
			position = 0,
			closedByDefault = true
	)
	String othersRandomsCategory = "othersRandomsCategory";
	@ConfigSection(
			name = "Own random events",
			description = "Hide your own random events",
			position = 1,
			closedByDefault = true
	)
	String ownRandomsCategory = "ownRandomsCategory";
	@ConfigSection(
			name = "Miscellaneous",
			description = "Miscellaneous settings",
			position = 2,
			closedByDefault = true
	)
	String miscCategory = "miscCategory";

	@ConfigItem(
			keyName = "hideOtherBeekeeper",
			name = "Hide others' Beekeeper",
			description = "Hide the Beekeeper random event of other players",
			position = 0,
			section = othersRandomsCategory
	)
	default boolean hideOtherBeekeeper() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOtherCaptArnav",
			name = "Hide others' Capt' Arnav",
			description = "Hide the Capt' Arnav random event of other players",
			position = 1,
			section = othersRandomsCategory
	)
	default boolean hideOtherCaptArnav() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOtherNiles",
			name = "Hide others' Certers (Niles)",
			description = "Hide the Certers (Niles, Miles, Giles) random event of other players",
			position = 2,
			section = othersRandomsCategory
	)
	default boolean hideOtherNiles() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOtherDrillDemon",
			name = "Hide others' Drill Demon",
			description = "Hide the Drill Demon/Sergeant Damien random event of other players",
			position = 3,
			section = othersRandomsCategory
	)
	default boolean hideOtherDrillDemon() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOtherDrunkenDwarf",
			name = "Hide others' Drunken Dwarf",
			description = "Hide the Drunken Dwarf random event of other players",
			position = 4,
			section = othersRandomsCategory
	)
	default boolean hideOtherDrunkenDwarf() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOtherEvilBob",
			name = "Hide others' Evil Bob",
			description = "Hide the Evil Bob random event of other players",
			position = 5,
			section = othersRandomsCategory
	)
	default boolean hideOtherEvilBob() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOtherEvilTwin",
			name = "Hide others' Evil Twin",
			description = "Hide the Evil Twin/Postie Pete/Molly random event of other players",
			position = 6,
			section = othersRandomsCategory
	)
	default boolean hideOtherEvilTwin() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOtherFreakyForester",
			name = "Hide others' Freaky Forester",
			description = "Hide the Freaky Forester random event of other players",
			position = 7,
			section = othersRandomsCategory
	)
	default boolean hideOtherFreakyForester() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOtherGenie",
			name = "Hide others' Genie",
			description = "Hide the Genie random event of other players",
			position = 8,
			section = othersRandomsCategory
	)
	default boolean hideOtherGenie() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOtherGravedigger",
			name = "Hide others' Gravedigger",
			description = "Hide the Gravedigger/Leo random event of other players",
			position = 9,
			section = othersRandomsCategory
	)
	default boolean hideOtherGravedigger() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOtherJekyllHyde",
			name = "Hide others' Jekyll and Hyde",
			description = "Hide the Jekyll and Hyde random event of other players",
			position = 10,
			section = othersRandomsCategory
	)
	default boolean hideOtherJekyllHyde() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOtherKissTheFrog",
			name = "Hide others' Kiss the Frog",
			description = "Hide the Kiss the Frog random event of other players",
			position = 11,
			section = othersRandomsCategory
	)
	default boolean hideOtherKissTheFrog() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOtherMaze",
			name = "Hide others' Maze",
			description = "Hide the Maze random event of other players",
			position = 12,
			section = othersRandomsCategory
	)
	default boolean hideOtherMaze() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOtherMime",
			name = "Hide others' Mime",
			description = "Hide the Mime random event of other players",
			position = 13,
			section = othersRandomsCategory
	)
	default boolean hideOtherMime() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOtherMysteriousOldMan",
			name = "Hide others' Mysterious Old Man",
			description = "Hide the Mysterious Old Man random event of other players",
			position = 14,
			section = othersRandomsCategory
	)
	default boolean hideOtherMysteriousOldMan() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOtherPilloryGuard",
			name = "Hide others' Pillory Guard",
			description = "Hide the Pillory Guard random event of other players",
			position = 15,
			section = othersRandomsCategory
	)
	default boolean hideOtherPilloryGuard() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOtherPinball",
			name = "Hide others' Pinball",
			description = "Hide the Pinball/Flippa/Tilt random event of other players",
			position = 16,
			section = othersRandomsCategory
	)
	default boolean hideOtherPinball() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOtherPrisonPete",
			name = "Hide others' Prison Pete",
			description = "Hide the Prison Pete random event of other players",
			position = 17,
			section = othersRandomsCategory
	)
	default boolean hideOtherPrisonPete() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOtherQuizMaster",
			name = "Hide others' Quiz Master",
			description = "Hide the Quiz Master random event of other players",
			position = 18,
			section = othersRandomsCategory
	)
	default boolean hideOtherQuizMaster() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOtherRickTurpentine",
			name = "Hide others' Rick Turpentine",
			description = "Hide the Rick Turpentine random event of other players",
			position = 19,
			section = othersRandomsCategory
	)
	default boolean hideOtherRickTurpentine() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOtherSandwichLady",
			name = "Hide others' Sandwich Lady",
			description = "Hide the Sandwich Lady random event of other players",
			position = 20,
			section = othersRandomsCategory
	)
	default boolean hideOtherSandwichLady() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOtherSurpriseExam",
			name = "Hide others' Surprise Exam",
			description = "Hide the Surprise Exam/Dunce random event of other players",
			position = 21,
			section = othersRandomsCategory
	)
	default boolean hideOtherSurpriseExam() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOwnBeekeeper",
			name = "Hide your own Beekeeper",
			description = "Hide your own Beekeeper random event",
			position = 0,
			section = ownRandomsCategory
	)
	default boolean hideOwnBeekeeper() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOwnCaptArnav",
			name = "Hide your own Capt' Arnav",
			description = "Hide your own Capt' Arnav random event",
			position = 1,
			section = ownRandomsCategory
	)
	default boolean hideOwnCaptArnav() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOwnNiles",
			name = "Hide your own Certers (Niles)",
			description = "Hide your own Certers (Niles, Miles, Giles) random event",
			position = 2,
			section = ownRandomsCategory
	)
	default boolean hideOwnNiles() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOwnDrillDemon",
			name = "Hide your own Drill Demon",
			description = "Hide your own Drill Demon/Sergeant Damien random event",
			position = 3,
			section = ownRandomsCategory
	)
	default boolean hideOwnDrillDemon() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOwnDrunkenDwarf",
			name = "Hide your own Drunken Dwarf",
			description = "Hide your own Drunken Dwarf random event",
			position = 4,
			section = ownRandomsCategory
	)
	default boolean hideOwnDrunkenDwarf() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOwnEvilBob",
			name = "Hide your own Evil Bob",
			description = "Hide your own Evil Bob random event",
			position = 5,
			section = ownRandomsCategory
	)
	default boolean hideOwnEvilBob() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOwnEvilTwin",
			name = "Hide your own Evil Twin",
			description = "Hide your own Evil Twin/Postie Pete/Molly random event",
			position = 6,
			section = ownRandomsCategory
	)
	default boolean hideOwnEvilTwin() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOwnFreakyForester",
			name = "Hide your own Freaky Forester",
			description = "Hide your own Freaky Forester random event",
			position = 7,
			section = ownRandomsCategory
	)
	default boolean hideOwnFreakyForester() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOwnGenie",
			name = "Hide your own Genie",
			description = "Hide your own Genie random event",
			position = 8,
			section = ownRandomsCategory
	)
	default boolean hideOwnGenie() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOwnGravedigger",
			name = "Hide your own Gravedigger",
			description = "Hide your own Gravedigger/Leo random event",
			position = 9,
			section = ownRandomsCategory
	)
	default boolean hideOwnGravedigger() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOwnJekyllHyde",
			name = "Hide your own Jekyll and Hyde",
			description = "Hide your own Jekyll and Hyde random event",
			position = 10,
			section = ownRandomsCategory
	)
	default boolean hideOwnJekyllHyde() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOwnKissTheFrog",
			name = "Hide your own Kiss the Frog",
			description = "Hide your own Kiss the Frog random event",
			position = 11,
			section = ownRandomsCategory
	)
	default boolean hideOwnKissTheFrog() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOwnMaze",
			name = "Hide your own Maze",
			description = "Hide your own Maze random event",
			position = 12,
			section = ownRandomsCategory
	)
	default boolean hideOwnMaze() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOwnMime",
			name = "Hide your own Mime",
			description = "Hide your own Mime random event",
			position = 13,
			section = ownRandomsCategory
	)
	default boolean hideOwnMime() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOwnMysteriousOldMan",
			name = "Hide your own Mysterious Old Man",
			description = "Hide your own Mysterious Old Man random event",
			position = 14,
			section = ownRandomsCategory
	)
	default boolean hideOwnMysteriousOldMan() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOwnPilloryGuard",
			name = "Hide your own Pillory Guard",
			description = "Hide your own Pillory Guard random event",
			position = 15,
			section = ownRandomsCategory
	)
	default boolean hideOwnPilloryGuard() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOwnPinball",
			name = "Hide your own Pinball",
			description = "Hide your own Pinball/Flippa/Tilt random event",
			position = 16,
			section = ownRandomsCategory
	)
	default boolean hideOwnPinball() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOwnPrisonPete",
			name = "Hide your own Prison Pete",
			description = "Hide your own Prison Pete random event",
			position = 17,
			section = ownRandomsCategory
	)
	default boolean hideOwnPrisonPete() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOwnQuizMaster",
			name = "Hide your own Quiz Master",
			description = "Hide your own Quiz Master random event",
			position = 18,
			section = ownRandomsCategory
	)
	default boolean hideOwnQuizMaster() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOwnRickTurpentine",
			name = "Hide your own Rick Turpentine",
			description = "Hide your own Rick Turpentine random event",
			position = 19,
			section = ownRandomsCategory
	)
	default boolean hideOwnRickTurpentine() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOwnSandwichLady",
			name = "Hide your own Sandwich Lady",
			description = "Hide your own Sandwich Lady random event",
			position = 20,
			section = ownRandomsCategory
	)
	default boolean hideOwnSandwichLady() {
		return false;
	}

	@ConfigItem(
			keyName = "hideOwnSurpriseExam",
			name = "Hide your own Surprise Exam",
			description = "Hide your own Surprise Exam/Dunce random event",
			position = 21,
			section = ownRandomsCategory
	)
	default boolean hideOwnSurpriseExam() {
		return false;
	}

	//Strange plant does not interact with any person, so we'll hide them all if hideAllStrangePlant is enabled.
	@ConfigItem(
			keyName = "hideAllStrangePlant",
			name = "Hide ALL Strange Plants",
			description = "Hide ALL Strange Plant random events, both your own events and the events of other players",
			position = 0,
			section = miscCategory
	)
	default boolean hideAllStrangePlant() {
		return false;
	}

	@ConfigItem(
			keyName = "muteDwarf",
			name = "Mute Drunken Dwarf",
			description = "Mutes the Drunken Dwarf",
			position = 1,
			section = miscCategory
	)
	default boolean muteDwarf() {
		return false;
	}

	@ConfigItem(
			keyName = "muteBob",
			name = "Mute Evil Bob",
			description = "Mutes Evil Bob's meow",
			position = 2,
			section = miscCategory
	)
	default boolean muteBob() {
		return false;
	}

	@ConfigItem(
			keyName = "muteFrog",
			name = "Mute the Frogs",
			description = "Mutes the splashes by the Frogs",
			position = 3,
			section = miscCategory
	)
	default boolean muteFrog() {
		return false;
	}

	@ConfigItem(
			keyName = "mutePoof",
			name = "Mute the Poof",
			description = "Mutes the poof (smoke) sound",
			position = 4,
			section = miscCategory
	)
	default boolean mutePoof() {
		return true;
	}

	@ConfigItem(
			keyName = "muteOtherRandomSounds",
			name = "Mute other sound effects",
			description = "Mutes other sound effects (i.e. sound effects not listed above) made by random events",
			position = 5,
			section = miscCategory
	)
	default boolean muteOtherRandomSounds() {
		return false;
	}

	@ConfigItem(
			keyName = "hidePoof",
			name = "Hide the Poof animation",
			description = "Hides random events' poof (smoke) animation",
			position = 6,
			section = miscCategory
	)
	default boolean hidePoof() {
		return true;
	}

}