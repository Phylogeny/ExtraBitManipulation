package com.phylogeny.extrabitmanipulation.api.jei.armor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.IGuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import com.phylogeny.extrabitmanipulation.api.jei.InfoRecipeBase;
import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.client.GuiHelper;
import com.phylogeny.extrabitmanipulation.init.KeyBindingsExtraBitManipulation;

public class ChiseledArmorInfoRecipe extends InfoRecipeBase
{
	public static final String[] GRAPHIC_NAMES = new String[]{"chiseled_helmet", "creation", "collection", "chiseled_armor_gui", "chiseled_armor_slots_gui"};
	private int imageIndex;
	
	public static List<ChiseledArmorInfoRecipe> create(IGuiHelper guiHelper, List<ItemStack> sculptingStacks)
	{
		List<ChiseledArmorInfoRecipe> recipes = new ArrayList<ChiseledArmorInfoRecipe>();
		for (int i = 0; i < GRAPHIC_NAMES.length; i++)
		{
			recipes.add(new ChiseledArmorInfoRecipe(guiHelper, sculptingStacks, ChiseledArmorInfoRecipeCategory.NAME,
					GRAPHIC_NAMES[i], i == 0 ? 89 : (i == 3 ? 76 : (i == 4 ? 75 : 78)), i == 0 ? 21 : (i == 3 ? 32 : (i == 4 ? 41 : 19)),
							i == 0 ? 187 : (i == 3 ? 186 : (i == 4 ? 183 : 186)), i == 0 ? 127 : (i == 3 ? 108 : (i == 4 ? 103 : 125)), i));
		}
		return recipes;
	}
	
	public ChiseledArmorInfoRecipe(IGuiHelper guiHelper, List<ItemStack> sculptingStacks, String catagoryName,
			String imageName, int imageLeft, int imageTop, int imageRight, int imageBottom, int imageIndex)
	{
		super(guiHelper, sculptingStacks, 0, 0, imageName, imageName.toLowerCase().replace(" ", "_"), ChiseledArmorInfoRecipeCategory.NAME + "." + imageName,
				imageLeft, imageTop, imageRight, imageBottom, catagoryName);
		this.imageIndex = imageIndex;
		for (int i = 0; i < tooltipLines.size(); i++)
			tooltipLines.set(i, tooltipLines.get(i).replace("@",
					KeyBindingsExtraBitManipulation.OPEN_CHISELED_ARMOR_SLOTS_GUI.getKeyBinding().getDisplayName()));
	}
	
	protected String translateName(String name)
	{
		return translateName(ChiseledArmorInfoRecipeCategory.NAME, name);
	}
	
	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		int xPos = 60;
		int yPos = 0;
		slotDrawable.draw(minecraft, xPos, yPos);
		ClientHelper.bindTexture(image);
		GuiHelper.drawTexturedRect(imageBox.getMinX(), imageBox.getMinY(), imageBox.getMaxX(), imageBox.getMaxY());
		xPos = 82;
		yPos = slotDrawable.getHeight() / 2 - minecraft.fontRenderer.FONT_HEIGHT / 2;
		minecraft.fontRenderer.drawString(name, xPos, yPos, Color.black.getRGB());
		String text = this.text;
		int wrapWidth = 60;
		if (imageIndex == 0)
		{
			xPos = 53;
			yPos = 44;
			wrapWidth = 84;
		}
		else if (imageIndex == 1)
		{
			xPos = 37;
			yPos = 30;
		}
		else
		{
			xPos = 37;
			yPos = 23;
			if (imageIndex > 2)
			{
				if (imageIndex == 3)
				{
					text = text.replace("@", KeyBindingsExtraBitManipulation.OPEN_CHISELED_ARMOR_GUI.getKeyBinding().getDisplayName());
					wrapWidth = 73;
					yPos += 3;
				}
				yPos += 3;
			}
		}
		for (String s : minecraft.fontRenderer.listFormattedStringToWidth(text, wrapWidth))
		{
			minecraft.fontRenderer.drawString(s, xPos - minecraft.fontRenderer.getStringWidth(s) / 2, yPos, Color.black.getRGB());
			yPos += minecraft.fontRenderer.FONT_HEIGHT;
		}
	}
	
}