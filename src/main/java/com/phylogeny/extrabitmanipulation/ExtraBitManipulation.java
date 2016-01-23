package com.phylogeny.extrabitmanipulation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import com.phylogeny.extrabitmanipulation.client.config.ConfigHandlerExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.client.eventhandler.ClientEventHandler;
import com.phylogeny.extrabitmanipulation.init.ItemsExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.init.RecipesExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.item.ItemBitWrench;
import com.phylogeny.extrabitmanipulation.packet.PacketCycleWrench;
import com.phylogeny.extrabitmanipulation.reference.Reference;

@Mod(modid = Reference.MOD_ID, version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY_CLASSPATH, dependencies = "required-after:chiselsandbits")
public class ExtraBitManipulation
{
	public static SimpleNetworkWrapper packetNetwork = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);
	
	@EventHandler
	public void preinit(FMLPreInitializationEvent event)
	{
    	ItemsExtraBitManipulation.itemsInit();
		ConfigHandlerExtraBitManipulation.setUpConfigs(event.getSuggestedConfigurationFile());
		MinecraftForge.EVENT_BUS.register(new ConfigHandlerExtraBitManipulation());
		packetNetwork.registerMessage(PacketCycleWrench.Handler.class, PacketCycleWrench.class, 0, Side.SERVER);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		RecipesExtraBitManipulation.recipeInit();
		if(event.getSide() == Side.CLIENT)
		{
			MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
			RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
			renderItem.getItemModelMesher().register(ItemsExtraBitManipulation.BitWrench, 0, new ModelResourceLocation(Reference.MOD_ID + ":" + ((ItemBitWrench) ItemsExtraBitManipulation.BitWrench).getName(), "inventory"));
		}
	}
    
}