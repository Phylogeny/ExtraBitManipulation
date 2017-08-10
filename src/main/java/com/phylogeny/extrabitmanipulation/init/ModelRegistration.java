package com.phylogeny.extrabitmanipulation.init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.phylogeny.extrabitmanipulation.armor.ChiseledArmorStackHandeler.ChiseledArmorBakedModel;
import com.phylogeny.extrabitmanipulation.armor.ModelChiseledArmor;
import com.phylogeny.extrabitmanipulation.armor.ModelChiseledArmorLeggings;
import com.phylogeny.extrabitmanipulation.block.BlockExtraBitManipulationBase;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ArmorMovingPart;
import com.phylogeny.extrabitmanipulation.item.ItemExtraBitManipulationBase;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;
import com.phylogeny.extrabitmanipulation.reference.Reference;

public class ModelRegistration
{
	private static final String ARMOR_TEXTURE_PATH = Reference.MOD_ID + ":textures/armor/chiseled_armor.png";
	private static ModelChiseledArmor armorModel;
	private static ModelChiseledArmorLeggings armorModelLeggings;
	private static ModelBiped armorModelEmpty;
	
	public static void registerItemModels()
	{
		ArmorMovingPart.initIconModelLocations();
		registerChiseledArmorItemModel(ItemsExtraBitManipulation.chiseledHelmet);
		registerChiseledArmorItemModel(ItemsExtraBitManipulation.chiseledChestplate);
		registerChiseledArmorItemModel(ItemsExtraBitManipulation.chiseledLeggings);
		registerChiseledArmorItemModel(ItemsExtraBitManipulation.chiseledBoots);
		registerItemModel(ItemsExtraBitManipulation.diamondNugget);
		registerItemModel(ItemsExtraBitManipulation.bitWrench);
		registerItemModel(ItemsExtraBitManipulation.sculptingLoop);
		registerItemModel(ItemsExtraBitManipulation.sculptingSquare);
		registerItemModel(ItemsExtraBitManipulation.sculptingSpadeCurved);
		registerItemModel(ItemsExtraBitManipulation.sculptingSpadeSquared);
		registerItemModel(ItemsExtraBitManipulation.modelingTool);
		registerItemModel(ItemsExtraBitManipulation.modelingToolHead);
		registerItemModel(ItemsExtraBitManipulation.bitWrenchHead);
		registerItemModel(ItemsExtraBitManipulation.sculptingLoopHead);
		registerItemModel(ItemsExtraBitManipulation.sculptingSquareHead);
		registerItemModel(ItemsExtraBitManipulation.sculptingSpadeCurvedHead);
		registerItemModel(ItemsExtraBitManipulation.sculptingSpadeSquaredHead);
		registerItemBlockModel(BlocksExtraBitManipulation.bodyPartTemplate);
		armorModel = new ModelChiseledArmor();
		armorModelLeggings = new ModelChiseledArmorLeggings();
		armorModelEmpty = new ModelBiped();
		armorModelEmpty.bipedHead.cubeList.clear();
		armorModelEmpty.bipedBody.cubeList.clear();
		armorModelEmpty.bipedRightArm.cubeList.clear();
		armorModelEmpty.bipedLeftArm.cubeList.clear();
		armorModelEmpty.bipedRightLeg.cubeList.clear();
		armorModelEmpty.bipedLeftLeg.cubeList.clear();
		List<ResourceLocation> modelLocations = new ArrayList<ResourceLocation>();
		for (ArmorMovingPart movingPart : ArmorMovingPart.values())
		{
			for (ModelResourceLocation modelLocation : movingPart.getIconModelLocations())
				modelLocations.add(modelLocation);
		}
		registerIsolatedModel(ItemsExtraBitManipulation.chiseledHelmet, modelLocations.toArray(new ResourceLocation[modelLocations.size()]));
	}
	
	@SubscribeEvent
	public void onModelBakeEvent(ModelBakeEvent event)
	{
		registerBakedItemModel(event, ItemsExtraBitManipulation.chiseledHelmet);
		registerBakedItemModel(event, ItemsExtraBitManipulation.chiseledChestplate);
		registerBakedItemModel(event, ItemsExtraBitManipulation.chiseledLeggings);
		registerBakedItemModel(event, ItemsExtraBitManipulation.chiseledBoots);
	}
	
	private static void registerBakedItemModel(ModelBakeEvent event, Item item)
	{
		event.getModelRegistry().putObject(new ModelResourceLocation(item.getRegistryName(), "inventory"), new ChiseledArmorBakedModel());
	}
	
	private static void registerItemBlockModel(Block block)
	{
		Item item = Item.getItemFromBlock(block);
		if (item != null)
			registerItemModel(item, ((BlockExtraBitManipulationBase) block).getName());
	}
	
	private static void registerItemModel(Item item)
	{
		registerItemModel(item, ((ItemExtraBitManipulationBase) item).getName());
	}
	
	private static void registerChiseledArmorItemModel(Item item)
	{
		ItemChiseledArmor armorPiece = ((ItemChiseledArmor) item);
		ResourceLocation name = armorPiece.getRegistryName();
		if (name == null)
			return;
		
		registerItemModel(armorPiece, name.getResourcePath());
		registerIsolatedModel(armorPiece, armorPiece.initItemModelLocation());
	}
	
	private static void registerItemModel(Item item, String name)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, name), "inventory"));
	}
	
	private static void registerIsolatedModel(Item item, ResourceLocation... resourceLocations)
	{
		ModelLoader.registerItemVariants(item, resourceLocations);
	}
	
	public static ModelBiped getArmorModel(ItemStack stack, EntityEquipmentSlot slot)
	{
		return shouldRenderEmptymodel(stack) ? armorModelEmpty : (slot == EntityEquipmentSlot.LEGS ? armorModelLeggings : armorModel);
	}
	
	public static String getArmorTexture(ItemStack stack)
	{
		return shouldRenderEmptymodel(stack) ? null : ARMOR_TEXTURE_PATH;
	}
	
	private static boolean shouldRenderEmptymodel(ItemStack stack)
	{
		return Configs.armorModelRenderMode == ArmorModelRenderMode.NEVER || (Configs.armorModelRenderMode == ArmorModelRenderMode.IF_EMPTY
				&& ItemStackHelper.getNBTOrNew(stack).getCompoundTag(NBTKeys.ARMOR_DATA).getBoolean(NBTKeys.ARMOR_NOT_EMPTY));
	}
	
	public static enum ArmorModelRenderMode
	{
		IF_EMPTY, NEVER, ALWAYS;
	}
	
}