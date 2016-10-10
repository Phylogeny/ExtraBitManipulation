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
		register(ItemsExtraBitManipulation.DiamondNugget);
		register(ItemsExtraBitManipulation.BitWrench);
		register(ItemsExtraBitManipulation.SculptingLoop);
		register(ItemsExtraBitManipulation.SculptingSquare);
		register(ItemsExtraBitManipulation.SculptingSpadeCurved);
		register(ItemsExtraBitManipulation.SculptingSpadeSquared);
		register(ItemsExtraBitManipulation.ModelingTool);
		register(ItemsExtraBitManipulation.BitWrenchHead);
		register(ItemsExtraBitManipulation.SculptingLoopHead);
		register(ItemsExtraBitManipulation.SculptingSquareHead);
		register(ItemsExtraBitManipulation.SculptingSpadeCurvedHead);
		register(ItemsExtraBitManipulation.SculptingSpadeSquaredHead);
	}
	
	private void register(Item item)
	{
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		renderItem.getItemModelMesher().register(item, 0, new ModelResourceLocation(Reference.MOD_ID + ":"
				+ (((ItemExtraBitManipulationBase) item).getName()), "inventory"));
	}
	
}