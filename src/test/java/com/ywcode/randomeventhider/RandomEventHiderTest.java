package com.ywcode.randomeventhider;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class RandomEventHiderTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(RandomEventHiderPlugin.class);
		RuneLite.main(args);
	}
}