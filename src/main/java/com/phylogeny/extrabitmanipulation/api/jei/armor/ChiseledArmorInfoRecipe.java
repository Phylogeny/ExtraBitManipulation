package com.phylogeny.extrabitmanipulation.api.jei.armor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

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
	
	public static List<ChiseledArmorInfoRecipe> create(List<ItemStack> sculptingStacks)
	{
		List<ChiseledArmorInfoRecipe> recipes = new ArrayList<ChiseledArmorInfoRecipe>();
		for (int i = 0; i < GRAPHIC_NAMES.length; i++)
		{
			recipes.add(new ChiseledArmorInfoRecipe(sculptingStacks, ChiseledArmorInfoRecipeCategory.NAME,
					GRAPHIC_NAMES[i], i == 0 ? 79 : (i == 3 ? 12 : (i == 4 ? 75 : 78)), i == 0 ? 21 : (i == 3 ? 54 : (i == 4 ? 30 : 24)),
							i == 0 ? 177 : (i == 3 ? 113 : (i == 4 ? 176 : 176)), i == 0 ? 127 : (i == 3 ? 124 : (i == 4 ? 114 : 120)), i));
		}
		return recipes;
	}
	
	public ChiseledArmorInfoRecipe(List<ItemStack> sculptingStacks, String catagoryName,
			String imageName, int imageLeft, int imageTop, int imageRight, int imageBottom, int imageIndex)
	{
		super(sculptingStacks, 0, 0, imageName, imageName.toLowerCase().replace(" ", "_"), ChiseledArmorInfoRecipeCategory.NAME + "." + imageName,
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
		ClientHelper.bindTexture(image);
		GuiHelper.drawTexturedRect(imageBox.getMinX() - 2, imageBox.getMinY(), imageBox.getMaxX() - 2, imageBox.getMaxY());
		xPos = recipeWidth / 2 - minecraft.fontRendererObj.getStringWidth(name) / 2;
		yPos = 4;
		minecraft.fontRendererObj.drawString(name, xPos, yPos, Color.black.getRGB());
		String text = this.text;
		int wrapWidth = 60;
		if (imageIndex == 3)
		{
			xPos = 15;
			yPos = 26;
			text = text.replaceFirst("@", KeyBindingsExtraBitManipulation.OPEN_CHISELED_ARMOR_GUI_VANITY.getKeyBinding().getDisplayName())
					.replace("@", KeyBindingsExtraBitManipulation.OPEN_CHISELED_ARMOR_GUI_MAIN.getKeyBinding().getDisplayName());
			wrapWidth = 153;
			List<String> strings = minecraft.fontRendererObj.listFormattedStringToWidth(text, wrapWidth);
			String lastLine = "";
			for (int i = 0; i < strings.size() && i < 3; i++)
			{
				lastLine = strings.get(i);
				minecraft.fontRendererObj.drawString(lastLine, xPos, yPos, Color.black.getRGB());
				yPos += minecraft.fontRendererObj.FONT_HEIGHT;
			}
			wrapWidth = 62;
			xPos += 99;
			strings = minecraft.fontRendererObj.listFormattedStringToWidth(text.substring(text.indexOf(lastLine)
					+ lastLine.length()).trim(), wrapWidth);
			for (int i = 0; i < strings.size(); i++)
			{
				minecraft.fontRendererObj.drawString(strings.get(i), xPos, yPos, Color.black.getRGB());
				yPos += minecraft.fontRendererObj.FONT_HEIGHT;
			}
			return;
		}
		else if (imageIndex == 0)
		{
			xPos = 53;
			yPos = 44;
			wrapWidth = 84;
		}
		else if (imageIndex == 1)
		{
			xPos = 42;
			yPos = 30;
		}
		else
		{
			xPos = 42;
			yPos = imageIndex > 2 ? 26 : 23;
		}
		for (String s : minecraft.fontRendererObj.listFormattedStringToWidth(text, wrapWidth))
		{
			minecraft.fontRendererObj.drawString(s, xPos - minecraft.fontRendererObj.getStringWidth(s) / 2, yPos, Color.black.getRGB());
			yPos += minecraft.fontRendererObj.FONT_HEIGHT;
		}
	}
	
}