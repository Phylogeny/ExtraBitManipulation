package com.phylogeny.extrabitmanipulation.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.phylogeny.extrabitmanipulation.client.eventhandler.ClientEventHandler;
import com.phylogeny.extrabitmanipulation.init.ItemsExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.item.ItemExtraBitManipulationBase;
import com.phylogeny.extrabitmanipulation.reference.Reference;

public class ProxyClient extends ProxyCommon
{
	
	@Override
	public void preinit(FMLPreInitializationEvent event)
	{
		super.preinit(event);
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
		register(ItemsExtraBitManipulation.DiamondNugget);
		register(ItemsExtraBitManipulation.BitWrench);
		register(ItemsExtraBitManipulation.SculptingLoop);
		register(ItemsExtraBitManipulation.SculptingSquare);
		register(ItemsExtraBitManipulation.SculptingSpadeCurved);
		register(ItemsExtraBitManipulation.SculptingSpadeSquared);
		register(ItemsExtraBitManipulation.ModelingTool);
		register(ItemsExtraBitManipulation.ModelingToolHead);
		register(ItemsExtraBitManipulation.BitWrenchHead);
		register(ItemsExtraBitManipulation.SculptingLoopHead);
		register(ItemsExtraBitManipulation.SculptingSquareHead);
		register(ItemsExtraBitManipulation.SculptingSpadeCurvedHead);
		register(ItemsExtraBitManipulation.SculptingSpadeSquaredHead);
	}
	
	private void register(Item item)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(
				new ResourceLocation(Reference.MOD_ID, (((ItemExtraBitManipulationBase) item).getName())), "inventory"));
	}
	
}