package com.phylogeny.extrabitmanipulation.api;

import java.util.ArrayList;
import java.util.List;

import com.phylogeny.extrabitmanipulation.init.ItemsExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.reference.Reference;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class JustEnoughItemsPlugin extends BlankModPlugin
{
	
	@Override
	public void register(IModRegistry registry)
	{
		addDescription(registry, "bit_wrench", ItemsExtraBitManipulation.bitWrench);
		addDescription(registry, "modeling_tool", ItemsExtraBitManipulation.modelingTool);
		addDescription(registry, "sculpting", ItemsExtraBitManipulation.sculptingLoop, ItemsExtraBitManipulation.sculptingSquare,
				ItemsExtraBitManipulation.sculptingSpadeCurved, ItemsExtraBitManipulation.sculptingSpadeSquared);
	}
	
	private void addDescription(IModRegistry registry, String langKeySuffix, Item... items)
	{
		List<ItemStack> stacks = new ArrayList<ItemStack>();
		for (Item item : items)
		{
			stacks.add(new ItemStack(item));
		}
		registry.addDescription(stacks, "item.description." + Reference.GROUP_ID + ":" + langKeySuffix);
	}
	
}