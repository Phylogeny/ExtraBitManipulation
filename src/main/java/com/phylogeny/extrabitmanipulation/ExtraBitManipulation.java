package com.phylogeny.extrabitmanipulation;

import com.phylogeny.extrabitmanipulation.proxy.ProxyCommon;
import com.phylogeny.extrabitmanipulation.reference.Reference;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = Reference.MOD_ID, version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY_CLASSPATH, dependencies = "required-after:chiselsandbits@[10.0,)")
public class ExtraBitManipulation
{
	@Mod.Instance(Reference.MOD_ID)
	public static ExtraBitManipulation instance;
	
	@SidedProxy(clientSide = Reference.CLIENT_CLASSPATH, serverSide = Reference.COMMON_CLASSPATH)
	public static ProxyCommon proxy;
	
	public static SimpleNetworkWrapper packetNetwork = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);
	
	@EventHandler
	public void preinit(FMLPreInitializationEvent event)
	{
		proxy.preinit(event);
	}
	
	@EventHandler
	public void init(@SuppressWarnings("unused") FMLInitializationEvent event)
	{
		proxy.init();
	}
	
	@EventHandler
	public void postinit(@SuppressWarnings("unused") FMLPostInitializationEvent event)
	{
		proxy.postinit();
	}
	
}