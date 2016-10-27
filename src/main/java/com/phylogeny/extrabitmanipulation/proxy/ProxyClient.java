package com.phylogeny.extrabitmanipulation.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.phylogeny.extrabitmanipulation.client.eventhandler.ClientEventHandler;
import com.phylogeny.extrabitmanipulation.helper.BitIOHelper;
import com.phylogeny.extrabitmanipulation.init.ItemsExtraBitManipulation;
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
	}
	
	@Override
	public void init()
	{
		super.init();
		Configs.sculptSetBitWire.init();
		Configs.sculptSetBitSpade.init();
		Configs.replacementBitsUnchiselable.initDefaultReplacementBit();
		Configs.replacementBitsInsufficient.initDefaultReplacementBit();
		Configs.modelBlockToBitMap = BitIOHelper.getModelBitMapFromEntryStrings(Configs.modelBlockToBitMapEntryStrings);
		Configs.modelStateToBitMap = BitIOHelper.getModelBitMapFromEntryStrings(Configs.modelStateToBitMapEntryStrings);
	}
	
	private void register(Item item)
	{
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		renderItem.getItemModelMesher().register(item, 0, new ModelResourceLocation(Reference.MOD_ID + ":"
				+ (((ItemExtraBitManipulationBase) item).getName()), "inventory"));
	}
	
}