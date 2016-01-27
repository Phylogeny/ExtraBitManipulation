package com.phylogeny.extrabitmanipulation.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

import com.phylogeny.extrabitmanipulation.client.eventhandler.ClientEventHandler;
import com.phylogeny.extrabitmanipulation.init.ItemsExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.item.ItemBitWrench;
import com.phylogeny.extrabitmanipulation.item.ItemSculptingLoop;
import com.phylogeny.extrabitmanipulation.reference.Reference;

public class ProxyClient extends ProxyCommon
{
	
	public void registerRenderInformation()
	{
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		register(renderItem, ItemsExtraBitManipulation.BitWrench, ((ItemBitWrench) ItemsExtraBitManipulation.BitWrench).getName());
		register(renderItem, ItemsExtraBitManipulation.SculptingLoop, ((ItemSculptingLoop) ItemsExtraBitManipulation.SculptingLoop).getName());
	}
	
	private void register(RenderItem renderItem, Item item, String name)
	{
		renderItem.getItemModelMesher().register(item, 0, new ModelResourceLocation(Reference.MOD_ID + ":" + name, "inventory"));
	}
	
}