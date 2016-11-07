package com.phylogeny.extrabitmanipulation.proxy;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.client.gui.GuiBitMapping;
import com.phylogeny.extrabitmanipulation.config.ConfigHandlerExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.container.ContainerBitMapping;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.init.ItemsExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.init.PacketRegistration;
import com.phylogeny.extrabitmanipulation.init.RecipesExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.reference.GuiIDs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class ProxyCommon implements IGuiHandler
{
	
	public void preinit(FMLPreInitializationEvent event)
	{
		ItemsExtraBitManipulation.itemsInit();
		ConfigHandlerExtraBitManipulation.setUpConfigs(event.getModConfigurationDirectory());
		MinecraftForge.EVENT_BUS.register(new ConfigHandlerExtraBitManipulation());
		PacketRegistration.registerPackets();
	}
	
	public void init()
	{
		RecipesExtraBitManipulation.recipeInit();
		NetworkRegistry.INSTANCE.registerGuiHandler(ExtraBitManipulation.instance, new ProxyCommon());
	}
	
	public void postinit() {}
	
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		return openBitMappingGui(id, player) ? new ContainerBitMapping(player.inventory) : null;
	}
	
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		return openBitMappingGui(id, player) ? new GuiBitMapping(player.inventory, ItemStackHelper.isDesignStack(player.getCurrentEquippedItem())) : null;
	}
	
	private boolean openBitMappingGui(int id, EntityPlayer player)
	{
		ItemStack stack = player.getCurrentEquippedItem();
		return (id == GuiIDs.BIT_MAPPING_GUI.getID() && (ItemStackHelper.isModelingToolStack(stack) || ItemStackHelper.isDesignStack(stack)));
	}
	
}