package com.phylogeny.extrabitmanipulation.reference;

import java.util.HashMap;
import java.util.Map;

import mod.chiselsandbits.api.IBitBrush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

import com.phylogeny.extrabitmanipulation.config.ConfigBitStack;
import com.phylogeny.extrabitmanipulation.config.ConfigBitToolSettingBoolean;
import com.phylogeny.extrabitmanipulation.config.ConfigBitToolSettingInt;
import com.phylogeny.extrabitmanipulation.config.ConfigNamed;
import com.phylogeny.extrabitmanipulation.config.ConfigReplacementBits;
import com.phylogeny.extrabitmanipulation.config.ConfigShapeRender;
import com.phylogeny.extrabitmanipulation.config.ConfigShapeRenderPair;
import com.phylogeny.extrabitmanipulation.helper.BitIOHelper;
import com.phylogeny.extrabitmanipulation.init.ModelRegistration.ArmorModelRenderMode;
import com.phylogeny.extrabitmanipulation.packet.PacketChangeArmorItemList.ArmorSlotRemovalMode;

public class Configs
{
	//MODELING TOOL SETTINGS
		public static boolean saveStatesById;
		public static ConfigReplacementBits replacementBitsUnchiselable;
		public static ConfigReplacementBits replacementBitsInsufficient;
		
	//BIT TOOL SETTINGS
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
		public static boolean oneBitTypeInversionRequirement;
		public static ArmorSlotRemovalMode armorSlotRemovalMode;
		public static ArmorModelRenderMode armorModelRenderMode;
		public static float armorZFightingBufferScale;
		public static float armorZFightingBufferScaleRightFoot;
		public static ConfigBitToolSettingInt sculptMode;
		public static ConfigBitToolSettingInt sculptDirection;
		public static ConfigBitToolSettingInt sculptShapeTypeCurved;
		public static ConfigBitToolSettingInt sculptShapeTypeFlat;
		public static ConfigBitToolSettingBoolean sculptTargetBitGridVertexes;
		public static ConfigBitToolSettingInt sculptSemiDiameter;
		public static ConfigBitToolSettingBoolean sculptHollowShapeWire;
		public static ConfigBitToolSettingBoolean sculptHollowShapeSpade;
		public static ConfigBitToolSettingBoolean sculptOpenEnds;
		public static ConfigBitToolSettingInt sculptWallThickness;
		public static ConfigBitStack sculptSetBitWire;
		public static ConfigBitStack sculptSetBitSpade;
		public static ConfigBitToolSettingBoolean sculptOffsetShape;
		public static ConfigBitToolSettingInt armorMode;
		public static ConfigBitToolSettingInt armorScale;
		public static ConfigBitToolSettingInt armorMovingPartHelmet;
		public static ConfigBitToolSettingInt armorMovingPartChestplate;
		public static ConfigBitToolSettingInt armorMovingPartLeggings;
		public static ConfigBitToolSettingInt armorMovingPartBoots;
		public static ConfigBitToolSettingInt armorTabIndex;
		public static ConfigBitToolSettingBoolean armorTargetBits;
		public static ConfigBitToolSettingBoolean armorPixelTranslation;
		public static ConfigBitToolSettingBoolean armorFullIllumination;
		public static ConfigBitToolSettingBoolean armorLookAtCursor;
		public static ConfigBitToolSettingInt modelAreaMode;
		public static ConfigBitToolSettingInt modelSnapMode;
		public static ConfigBitToolSettingBoolean modelGuiOpen;
		public static String[] modelBlockToBitMapEntryStrings;
		public static String[] modelStateToBitMapEntryStrings;
		public static Map<IBlockState, IBitBrush> modelBlockToBitMap;
		public static Map<IBlockState, IBitBrush> modelStateToBitMap;
		
	//ITEM PROPERTIES
		public static Map<Item, ConfigNamed> itemPropertyMap = new HashMap<Item, ConfigNamed>();
		
	//ITEM RECIPES
		public static Map<Item, ConfigNamed> itemRecipeMap = new HashMap<Item, ConfigNamed>();
		public static boolean disableDiamondNuggetOreDict;
		public static boolean disableDiamondToNuggets;
		public static boolean disableNuggetsToDiamond;
		
	//THROWN BITS
		public static boolean disableIgniteEntities;
		public static boolean disableIgniteBlocks;
		public static boolean disableExtinguishEntities;
		public static boolean disableExtinguishBlocks;
		public static float thrownBitVelocity;
		public static float thrownBitInaccuracy;
		public static float thrownBitDamage;
		public static float thrownWaterBitBlazeDamage;
		public static boolean thrownBitDamageDisable;
		public static boolean thrownWaterBitBlazeDamageDisable;
		public static int thrownLavaBitBurnTime;
		
	//RENDER WRENCH OVERLAYS
		public static boolean disableOverlays;
		public static int rotationPeriod;
		public static int mirrorPeriod;
		public static double mirrorAmplitude;
		public static int translationScalePeriod;
		public static double translationDistance;
		public static double translationOffsetDistance;
		public static double translationFadeDistance;
		public static int translationMovementPeriod;
		
	//RENDER SCULPTING TOOL SHAPES
		public static Map<Item, ConfigShapeRenderPair> itemShapeMap = new HashMap<Item, ConfigShapeRenderPair>();
		public static ConfigShapeRender[] itemShapes = new ConfigShapeRender[]{
					new ConfigShapeRender("Bit Removal Bounding Box", true, true, 115, 28, 0, 0, 0, 2.0F),
					new ConfigShapeRender("Bit Addition Bounding Box", true, false, 115, 28, 0, 0, 0, 2.0F),
					new ConfigShapeRender("Bit Removal Enveloped Shape", false, true, 38, 115, 0, 0, 255, 2.0F),
					new ConfigShapeRender("Bit Addition Enveloped Shape", true, false, 38, 115, 0, 0, 255, 2.0F)
				};
	
	public static void initModelingBitMaps()
	{
		modelBlockToBitMap = BitIOHelper.getModelBitMapFromEntryStrings(modelBlockToBitMapEntryStrings);
		modelStateToBitMap = BitIOHelper.getModelBitMapFromEntryStrings(modelStateToBitMapEntryStrings);
	}
	
}