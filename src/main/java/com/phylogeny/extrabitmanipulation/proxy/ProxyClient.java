package com.phylogeny.extrabitmanipulation.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.phylogeny.extrabitmanipulation.client.eventhandler.ClientEventHandler;
import com.phylogeny.extrabitmanipulation.init.ItemsExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.init.KeyBindingsExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.init.SoundsExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.item.ItemExtraBitManipulationBase;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.Reference;

public class ProxyClient extends ProxyCommon
{
	
	@Override
	public void preinit(FMLPreInitializationEvent event)
	{
		super.preinit(event);
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
		register(ItemsExtraBitManipulation.diamondNugget);
		register(ItemsExtraBitManipulation.bitWrench);
		register(ItemsExtraBitManipulation.sculptingLoop);
		register(ItemsExtraBitManipulation.sculptingSquare);
		register(ItemsExtraBitManipulation.sculptingSpadeCurved);
		register(ItemsExtraBitManipulation.sculptingSpadeSquared);
		register(ItemsExtraBitManipulation.modelingTool);
		register(ItemsExtraBitManipulation.modelingToolHead);
		register(ItemsExtraBitManipulation.bitWrenchHead);
		register(ItemsExtraBitManipulation.sculptingLoopHead);
		register(ItemsExtraBitManipulation.sculptingSquareHead);
		register(ItemsExtraBitManipulation.sculptingSpadeCurvedHead);
		register(ItemsExtraBitManipulation.sculptingSpadeSquaredHead);
		SoundsExtraBitManipulation.registerSounds();
		Framebuffer frameBuffer = Minecraft.getMinecraft().getFramebuffer();
		if (!frameBuffer.isStencilEnabled())
			frameBuffer.enableStencil();
	}
	
	@Override
	public void init()
	{
		super.init();
		KeyBindingsExtraBitManipulation.init();
		FMLInterModComms.sendMessage("chiselsandbits", "initkeybindingannotations", "");
	}
	
	@Override
	public void postinit()
	{
		Configs.sculptSetBitWire.init();
		Configs.sculptSetBitSpade.init();
		Configs.replacementBitsUnchiselable.initDefaultReplacementBit();
		Configs.replacementBitsInsufficient.initDefaultReplacementBit();
		Configs.initModelingBitMaps();
	}
	
	private void register(Item item)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(
				new ResourceLocation(Reference.MOD_ID, (((ItemExtraBitManipulationBase) item).getName())), "inventory"));
	}
	
}