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
	public static final String[] GRAPHIC_NAMES = new String[]{"chiseled_helmet", "creation", "collection", "chiseled_armor_gui"};
	private int imageIndex;
	
	public static List<ChiseledArmorInfoRecipe> create(IGuiHelper guiHelper, List<ItemStack> sculptingStacks)
	{
		List<ChiseledArmorInfoRecipe> recipes = new ArrayList<ChiseledArmorInfoRecipe>();
		for (int i = 0; i < GRAPHIC_NAMES.length; i++)
		{
			recipes.add(new ChiseledArmorInfoRecipe(guiHelper, sculptingStacks, ChiseledArmorInfoRecipeCategory.NAME,
					GRAPHIC_NAMES[i], i == 0 ? 76 : (i == 3 ? 61 : 65), i == 0 ? 22 : (i == 3 ? 33 : 20),
							i == 0 ? 174 : (i == 3 ? 171 : 173), i == 0 ? 128 : (i == 3 ? 109 : 126), i));
		}
		return recipes;
	}
	
	public ChiseledArmorInfoRecipe(IGuiHelper guiHelper, List<ItemStack> sculptingStacks, String catagoryName,
			String imageName, int imageLeft, int imageTop, int imageRight, int imageBottom, int imageIndex)
	{
		super(guiHelper, sculptingStacks, 0, 0, imageName, imageName.toLowerCase().replace(" ", "_"), ChiseledArmorInfoRecipeCategory.NAME + "." + imageName,
				imageLeft, imageTop, imageRight, imageBottom, catagoryName);
		this.imageIndex = imageIndex;
	}
	
	protected String translateName(String name)
	{
		return translateName(ChiseledArmorInfoRecipeCategory.NAME, name);
	}
	
	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		int xPos = 47;
		int yPos = 0;
		slotDrawable.draw(minecraft, xPos, yPos);
		ClientHelper.bindTexture(image);
		GuiHelper.drawTexturedRect(imageBox.getMinX(), imageBox.getMinY(), imageBox.getMaxX(), imageBox.getMaxY());
		xPos = 69;
		yPos = slotDrawable.getHeight() / 2 - minecraft.fontRendererObj.FONT_HEIGHT / 2;
		minecraft.fontRendererObj.drawString(name, xPos, yPos, Color.black.getRGB());
		String text;
		int wrapWidth = 60;
		if (imageIndex == 0)
		{
			text = "Render items and blocks as moving parts of armor pieces.";
			xPos = 40;
			yPos = 44;
			wrapWidth = 84;
		}
		else if (imageIndex == 1)
		{
			text = "To import copies of blocks from the world, start by setting a boypart template area.";
			xPos = 20;
			yPos = 30;
		}
		else
		{
			text = imageIndex == 2 ? "Once your model is created, draw a box around it in collection mode to copy and import the intersecting blocks."
				: "Items (and rendering operations to apply to them) of worn armor pieces can be manually added and modified by pressing "
					+ KeyBindingsExtraBitManipulation.OPEN_CHISELED_ARMOR_GUI.getKeyBinding().getDisplayName() + ".";
			xPos = 24;
			yPos = 23;
		}
		for (String s : minecraft.fontRendererObj.listFormattedStringToWidth(text, wrapWidth))
		{
			minecraft.fontRendererObj.drawString(s, xPos - minecraft.fontRendererObj.getStringWidth(s) / 2, yPos, Color.black.getRGB());
			yPos += minecraft.fontRendererObj.FONT_HEIGHT;
		}
	}
	
}