package com.phylogeny.extrabitmanipulation;

import com.phylogeny.extrabitmanipulation.config.ConfigHandlerExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.extendedproperties.SculptSettingsPlayerPropertiesHandler;
import com.phylogeny.extrabitmanipulation.init.ItemsExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.init.PacketRegistration;
import com.phylogeny.extrabitmanipulation.init.RecipesExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.proxy.ProxyCommon;
import com.phylogeny.extrabitmanipulation.reference.Reference;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = Reference.MOD_ID, version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY_CLASSPATH, dependencies = "required-after:chiselsandbits")
public class ExtraBitManipulation
{
	@SidedProxy(clientSide = Reference.CLIENT_CLASSPATH, serverSide = Reference.COMMON_CLASSPATH)
    public static ProxyCommon proxy;
	
	public static SimpleNetworkWrapper packetNetwork = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);
	
	@EventHandler
	public void preinit(FMLPreInitializationEvent event)
	{
		ItemsExtraBitManipulation.itemsInit();
		ConfigHandlerExtraBitManipulation.setUpConfigs(event.getSuggestedConfigurationFile());
		MinecraftForge.EVENT_BUS.register(new ConfigHandlerExtraBitManipulation());
		PacketRegistration.registerPackets();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		RecipesExtraBitManipulation.recipeInit();
		MinecraftForge.EVENT_BUS.register(new SculptSettingsPlayerPropertiesHandler());
		proxy.registerRenderInformation();
	}
    
}