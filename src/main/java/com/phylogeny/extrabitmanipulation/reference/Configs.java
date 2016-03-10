package com.phylogeny.extrabitmanipulation.reference;

import java.util.HashMap;
import java.util.Map;

import com.phylogeny.extrabitmanipulation.config.ConfigNamed;
import com.phylogeny.extrabitmanipulation.config.ConfigSculptSettingBoolean;
import com.phylogeny.extrabitmanipulation.config.ConfigSculptSettingInt;
import com.phylogeny.extrabitmanipulation.config.ConfigSculptSettingBitStack;
import com.phylogeny.extrabitmanipulation.config.ConfigShapeRender;
import com.phylogeny.extrabitmanipulation.config.ConfigShapeRenderPair;

import net.minecraft.item.Item;

public class Configs
{
	//SCULPTING SETTINGS
		public static boolean displayNameDiameter;
		public static boolean displayNameUseMeterUnits;
		public static float semiDiameterPadding;
		public static boolean placeBitsInInventory;
		public static boolean dropBitsInBlockspace;
		public static float bitSpawnBoxContraction;
		public static boolean dropBitsPerBlock;
		public static boolean dropBitsAsFullChiseledBlocks;
		public static int maxSemiDiameter;
		public static int maxWallThickness;
		public static ConfigSculptSettingInt sculptRotation;
		public static ConfigSculptSettingInt sculptShapeTypeCurved;
		public static ConfigSculptSettingInt sculptShapeTypeFlat;
		public static ConfigSculptSettingBoolean sculptTargetBitGridVertexes;
		public static ConfigSculptSettingInt sculptSemiDiameter;
		public static ConfigSculptSettingBoolean sculptHollowShape;
		public static ConfigSculptSettingBoolean sculptOpenEnds;
		public static ConfigSculptSettingInt sculptWallThickness;
		public static ConfigSculptSettingBitStack sculptSetBitWire;
		public static ConfigSculptSettingBitStack sculptSetBitSpade;
		
	//ITEM PROPERTIES
		public static Map<Item, ConfigNamed> itemPropertyMap = new HashMap<Item, ConfigNamed>();
	
	//ITEM RECIPES
		public static Map<Item, ConfigNamed> itemRecipeMap = new HashMap<Item, ConfigNamed>();
	
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