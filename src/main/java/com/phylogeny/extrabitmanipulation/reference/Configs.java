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
		public static boolean DISPLAY_NAME_DIAMETER;
		public static boolean DISPLAY_NAME_USE_METER_UNITS;
		public static float SEMI_DIAMETER_PADDING;
		public static boolean PLACE_BITS_IN_INVENTORY;
		public static boolean DROP_BITS_IN_BLOCKSPACE;
		public static float BIT_SPAWN_BOX_CONTRACTION;
		public static boolean DROP_BITS_PER_BLOCK;
		public static boolean DROP_BITS_AS_FULL_CHISELED_BLOCKS;
	
	//ITEM PROPERTIES
		public static Map<Item, Config> itemPropertyMap = new HashMap<Item, Config>();
	
	//ITEM RECIPES
		public static Map<Item, Config> itemRecipeMap = new HashMap<Item, Config>();
	
	//RENDER WRENCH OVERLAYS
		public static boolean DISABLE_OVERLAYS;
		public static double ROTATION_PERIOD;
		public static double MIRROR_PERIOD;
		public static double MIRROR_AMPLITUDE;
		public static double TRANSLATION_SCALE_PERIOD;
		public static double TRANSLATION_DISTANCE;
		public static double TRANSLATION_OFFSET_DISTANCE;
		public static double TRANSLATION_FADE_DISTANCE;
		public static double TRANSLATION_MOVEMENT_PERIOD;
	
	//RENDER SCULPTING TOOL SHAPES
		public static Map<Item, ConfigShapeRenderPair> itemShapeMap = new HashMap<Item, ConfigShapeRenderPair>();
		public static ConfigShapeRender[] itemShapes = new ConfigShapeRender[]{
					new ConfigShapeRender("Bit Removal Bounding Box", true, true, 115, 28, 0, 0, 0, 2.0F),
					new ConfigShapeRender("Bit Addition Bounding Box", true, false, 115, 28, 0, 0, 0, 2.0F),
					new ConfigShapeRender("Bit Removal Enveloped Shape", false, true, 38, 115, 0, 0, 255, 2.0F),
					new ConfigShapeRender("Bit Addition Enveloped Shape", true, false, 38, 115, 0, 0, 255, 2.0F)
				};
}