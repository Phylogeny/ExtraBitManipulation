package com.phylogeny.extrabitmanipulation.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

import com.phylogeny.extrabitmanipulation.client.eventhandler.ClientEventHandler;
import com.phylogeny.extrabitmanipulation.init.ItemsExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.item.ItemExtraBitManipulationBase;
import com.phylogeny.extrabitmanipulation.reference.Reference;

public class ProxyClient extends ProxyCommon
{
	
	@Override
	public void init()
	{
		super.init();
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
	}
	
	private void register(Item item)
	{
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		renderItem.getItemModelMesher().register(item, 0, new ModelResourceLocation(Reference.MOD_ID + ":"
				+ (((ItemExtraBitManipulationBase) item).getName()), "inventory"));
	}
	
}