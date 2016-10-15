package com.phylogeny.extrabitmanipulation.proxy;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.capability.IBitToolSettingsHandler;
import com.phylogeny.extrabitmanipulation.capability.BitToolSettingsEventHandler;
import com.phylogeny.extrabitmanipulation.capability.BitToolSettingsHandler;
import com.phylogeny.extrabitmanipulation.capability.Storage;
import com.phylogeny.extrabitmanipulation.client.gui.GuiModelingTool;
import com.phylogeny.extrabitmanipulation.config.ConfigHandlerExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.container.ContainerModelingTool;
import com.phylogeny.extrabitmanipulation.init.ItemsExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.init.PacketRegistration;
import com.phylogeny.extrabitmanipulation.init.RecipesExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.item.ItemModelingTool;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.GuiIDs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class ProxyCommon implements IGuiHandler
{
	
	public void preinit(FMLPreInitializationEvent event)
	{
		ItemsExtraBitManipulation.itemsInit();
		ConfigHandlerExtraBitManipulation.setUpConfigs(event.getSuggestedConfigurationFile());
		MinecraftForge.EVENT_BUS.register(new ConfigHandlerExtraBitManipulation());
		MinecraftForge.EVENT_BUS.register(new BitToolSettingsEventHandler());
		CapabilityManager.INSTANCE.register(IBitToolSettingsHandler.class, new Storage(), BitToolSettingsHandler.class);
		PacketRegistration.registerPackets();
	}
	
	public void init()
	{
		RecipesExtraBitManipulation.recipeInit();
		Configs.sculptSetBitWire.init();
		Configs.sculptSetBitSpade.init();
		Configs.replacementBitsUnchiselable.defaultReplacementBit.init();
		Configs.replacementBitsInsufficient.defaultReplacementBit.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(ExtraBitManipulation.instance, new ProxyCommon());
	}
	
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		return id == GuiIDs.MODELING_TOOL_BIT_MAPPING && getModelingToolStack(player) != null ? new ContainerModelingTool(player.inventory) : null;
	}
	
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		if (id == GuiIDs.MODELING_TOOL_BIT_MAPPING)
		{
			ItemStack modelingToolStack = getModelingToolStack(player);
			if (modelingToolStack != null)
				return new GuiModelingTool(player.inventory, modelingToolStack);
		}
		return null;
	}
	
	private ItemStack getModelingToolStack(EntityPlayer player)
	{
		ItemStack itemStack = player.getHeldItemMainhand();
		return itemStack != null && itemStack.getItem() instanceof ItemModelingTool ? itemStack : null;
	}
	
}