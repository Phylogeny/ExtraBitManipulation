package com.phylogeny.extrabitmanipulation.proxy;

import mod.chiselsandbits.core.ChiselsAndBits;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.armor.capability.ChiseledArmorSlotsEventHandler;
import com.phylogeny.extrabitmanipulation.armor.capability.ChiseledArmorSlotsHandler;
import com.phylogeny.extrabitmanipulation.armor.capability.ChiseledArmorSlotsStorage;
import com.phylogeny.extrabitmanipulation.armor.capability.IChiseledArmorSlotsHandler;
import com.phylogeny.extrabitmanipulation.client.gui.GuiBitMapping;
import com.phylogeny.extrabitmanipulation.client.gui.armor.GuiChiseledArmor;
import com.phylogeny.extrabitmanipulation.client.gui.armor.GuiInventoryArmorSlots;
import com.phylogeny.extrabitmanipulation.config.ConfigHandlerExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.container.ContainerHeldItem;
import com.phylogeny.extrabitmanipulation.container.ContainerPlayerArmorSlots;
import com.phylogeny.extrabitmanipulation.container.ContainerPlayerInventory;
import com.phylogeny.extrabitmanipulation.entity.EntityBit;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.init.BlocksExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.init.ItemsExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.init.PacketRegistration;
import com.phylogeny.extrabitmanipulation.init.RecipesExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.init.ReflectionExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.reference.BaublesReferences;
import com.phylogeny.extrabitmanipulation.reference.CustomNPCsReferences;
import com.phylogeny.extrabitmanipulation.reference.GuiIDs;
import com.phylogeny.extrabitmanipulation.reference.JeiReferences;
import com.phylogeny.extrabitmanipulation.reference.MorePlayerModelsReference;
import com.phylogeny.extrabitmanipulation.reference.Reference;

public class ProxyCommon implements IGuiHandler
{
	
	public void preinit(FMLPreInitializationEvent event)
	{
		BlocksExtraBitManipulation.blocksInit();
		ItemsExtraBitManipulation.itemsInit();
		BaublesReferences.isLoaded = Loader.isModLoaded(BaublesReferences.MOD_ID);
		JeiReferences.isLoaded = Loader.isModLoaded(JeiReferences.MOD_ID);
		MorePlayerModelsReference.isLoaded = Loader.isModLoaded(MorePlayerModelsReference.MOD_ID);
		CustomNPCsReferences.isLoaded = Loader.isModLoaded(CustomNPCsReferences.MOD_ID);
		ConfigHandlerExtraBitManipulation.setUpConfigs(event.getModConfigurationDirectory());
		MinecraftForge.EVENT_BUS.register(new ConfigHandlerExtraBitManipulation());
		MinecraftForge.EVENT_BUS.register(new ChiseledArmorSlotsEventHandler());
		CapabilityManager.INSTANCE.register(IChiseledArmorSlotsHandler.class, new ChiseledArmorSlotsStorage(), ChiseledArmorSlotsHandler.class);
		PacketRegistration.registerPackets();
		ResourceLocation name = new ResourceLocation(Reference.MOD_ID, "entity_bit");
		EntityRegistry.registerModEntity(name, EntityBit.class, name.toString(), 0, ExtraBitManipulation.instance, 64, 3, false);
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ChiselsAndBits.getItems().itemBlockBit, new BehaviorProjectileDispense()
		{
			@Override
			protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn)
			{
				return new EntityBit(worldIn, position.getX(), position.getY(), position.getZ(), stackIn);
			}
		});
		ReflectionExtraBitManipulation.initReflectionFieldsCommon();
		ChiseledArmorSlotsEventHandler.addCommandTabCompletions();
	}
	
	public void init()
	{
		RecipesExtraBitManipulation.recipeInit();
		NetworkRegistry.INSTANCE.registerGuiHandler(ExtraBitManipulation.instance, new ProxyCommon());
	}
	
	public void postinit() {}
	
	public static ContainerHeldItem createBitMappingContainer(EntityPlayer player)
	{
		return new ContainerHeldItem(player, 60, 137);
	}
	
	public static ContainerPlayerInventory createArmorContainer(EntityPlayer player)
	{
		return new ContainerPlayerInventory(player, 38, 148);
	}
	
	public static ContainerPlayerArmorSlots createArmorSlotsContainer(EntityPlayer player)
	{
		return new ContainerPlayerArmorSlots(player.inventory, !player.world.isRemote, player);
	}
	
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int unused0, int unused1, int unused2)
	{
		if (openBitMappingGui(id, player.getHeldItemMainhand()))
			return createBitMappingContainer(player);
		
		if (id == GuiIDs.CHISELED_ARMOR.getID())
			return createArmorContainer(player);
		
		if (id == GuiIDs.CHISELED_ARMOR_SLOTS.getID())
			return createArmorSlotsContainer(player);
		
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int unused0, int unused1, int unused2)
	{
		if (openBitMappingGui(id, player.getHeldItemMainhand()))
			return new GuiBitMapping(player, ItemStackHelper.isDesignStack(player.getHeldItemMainhand()));
		
		if (id == GuiIDs.CHISELED_ARMOR.getID())
			return new GuiChiseledArmor(player);
		
		if (id == GuiIDs.CHISELED_ARMOR_SLOTS.getID())
			return new GuiInventoryArmorSlots(createArmorSlotsContainer(player));
		
		return null;
	}
	
	private boolean openBitMappingGui(int id, ItemStack stack)
	{
		return id == GuiIDs.BIT_MAPPING.getID() && (ItemStackHelper.isModelingToolStack(stack) || ItemStackHelper.isDesignStack(stack));
	}
	
}