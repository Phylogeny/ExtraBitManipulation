package com.phylogeny.extrabitmanipulation.reference;

import java.util.HashMap;
import java.util.Map;

import com.phylogeny.extrabitmanipulation.config.Config;
import com.phylogeny.extrabitmanipulation.config.ConfigShapeRender;
import com.phylogeny.extrabitmanipulation.config.ConfigShapeRenderPair;

import net.minecraft.item.Item;

public class Configs
{
	//SCULPTING SETTINGS
		public static boolean bitTypeInChat;
		public static boolean displayNameDiameter;
		public static boolean displayNameUseMeterUnits;
		public static float semiDiameterPadding;
		public static boolean placeBitsInInventory;
		public static boolean dropBitsInBlockspace;
		public static float bitSpawnBoxContraction;
		public static boolean dropBitsPerBlock;
		public static boolean dropBitsAsFullChiseledBlocks;
	
	//ITEM PROPERTIES
		public static Map<Item, Config> itemPropertyMap = new HashMap<Item, Config>();
	
	//ITEM RECIPES
		public static Map<Item, Config> itemRecipeMap = new HashMap<Item, Config>();
	
	//RENDER WRENCH OVERLAYS
		public static boolean disableOverlays;
		public static double rotationPeriod;
		public static double mirrorPeriod;
		public static double mirrorAmplitude;
		public static double translationScalePeriod;
		public static double translationDistance;
		public static double translationOffsetDistance;
		public static double translationFadeDistance;
		public static double translationMovementPeriod;
	
	//RENDER SCULPTING TOOL SHAPES
		public static Map<Item, ConfigShapeRenderPair> itemShapeMap = new HashMap<Item, ConfigShapeRenderPair>();
		public static ConfigShapeRender[] itemShapes = new ConfigShapeRender[]{
					new ConfigShapeRender("Bit Removal Bounding Box", true, true, 115, 28, 0, 0, 0, 2.0F),
					new ConfigShapeRender("Bit Addition Bounding Box", true, false, 115, 28, 0, 0, 0, 2.0F),
					new ConfigShapeRender("Bit Removal Enveloped Shape", false, true, 38, 115, 0, 0, 255, 2.0F),
					new ConfigShapeRender("Bit Addition Enveloped Shape", true, false, 38, 115, 0, 0, 255, 2.0F)
				};
}