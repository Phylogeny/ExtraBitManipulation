package com.phylogeny.extrabitmanipulation.client.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.client.render.RenderState;

public class GuiButtonTab extends GuiButtonBase
{
	private ItemStack iconStack = ItemStack.EMPTY;
	private float u, v;
	private boolean isLeft;
	private int uWidth, vHeight, uOriginTab, vOriginTab, textureSize, offsetUp;
	private ResourceLocation texture;
	private IBakedModel[] iconModels;
	
	public GuiButtonTab(int buttonId, int x, int y, int width, int height, String hoverText, boolean isLeft,
			int uOriginTab, int vOriginTab, int uSelectedOffset, int textureSize, ResourceLocation texture, IBakedModel... iconModels)
	{
		this(buttonId, x, y, width, height, hoverText, isLeft, 0, 0, 0, 0, uOriginTab, vOriginTab, uSelectedOffset, textureSize, texture, iconModels);
	}
	
	public GuiButtonTab(int buttonId, int x, int y, int width, int height, String hoverText, boolean isLeft,
			ItemStack iconStack, int uOriginTab, int vOriginTab, int uSelectedOffset, int textureSize, ResourceLocation texture)
	{
		this(buttonId, x, y, width, height, hoverText, isLeft, iconStack, 0, 0, 0, 0, uOriginTab, vOriginTab, uSelectedOffset, textureSize, texture);
	}
	
	public GuiButtonTab(int buttonId, int x, int y, int width, int height, String hoverText, boolean isLeft, float u, float v, int uWidth,
			int vHeight, int uOriginTab, int vOriginTab, int uSelectedOffset, int textureSize, ResourceLocation texture, IBakedModel... iconModels)
	{
		this(buttonId, x, y, width, height, hoverText, isLeft, ItemStack.EMPTY, u, v, uWidth, vHeight, uOriginTab, vOriginTab, uSelectedOffset, textureSize, texture);
		this.iconModels = iconModels;
	}
	
	public GuiButtonTab(int buttonId, int x, int y, int width, int height, String hoverText, boolean isLeft, ItemStack iconStack, float u,
			float v, int uWidth, int vHeight, int uOriginTab, int vOriginTab, int offsetUp, int textureSize, ResourceLocation texture)
	{
		super(buttonId, x, y, width, height, "", hoverText);
		this.u = u;
		this.v = v;
		this.uWidth = uWidth;
		this.vHeight = vHeight;
		this.isLeft = isLeft;
		this.iconStack = iconStack;
		this.uOriginTab = uOriginTab;
		this.vOriginTab = vOriginTab;
		this.offsetUp = offsetUp;
		this.textureSize = textureSize;
		this.texture = texture;
		setSilent(true);
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
	{
		if (!visible)
			return;
		
		super.drawButton(mc, mouseX, mouseY, partialTicks);
		ClientHelper.bindTexture(texture);
		float color = enabled ? 1.0F : 0.5F;
		GlStateManager.color(color, color, color);
		int width = 29;
		int height = 26;
		int x = this.x;
		int u = uOriginTab;
		if (selected && enabled)
		{
			u += 25;
			x += isLeft ? -2 : -3;
		}
		else
		{
			width -= 5;
		}
		drawScaledCustomSizeModalRect(x, y - offsetUp, u, vOriginTab, width, height + offsetUp, width, height + offsetUp, textureSize, textureSize);
		if (uWidth > 0 && vHeight > 0)
		{
			ClientHelper.bindTexture(texture);
			drawScaledCustomSizeModalRect(this.x + 4 + getOffsetX(), y + 4, this.u, v, uWidth, vHeight, 19, 18, textureSize, textureSize);
		}
		if (!displayString.isEmpty())
			mc.fontRenderer.drawString(displayString, x + width / 2 - 1 - mc.fontRenderer.getStringWidth(displayString) / 2,
					y + (height - 8) / 2 + 1, enabled ? 4210752 : 13027014, false);
	}

	private int getOffsetX()
	{
		return selected ? -2 : 0;
	}
	
	public ItemStack getIconStack()
	{
		return iconStack;
	}
	
	public void setIconStack(ItemStack iconStack)
	{
		this.iconStack = iconStack;
	}
	
	public void renderIconStack()
	{
		if (iconStack.isEmpty() && iconModels == null)
			return;
		
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5, 0, 0);
		int x = this.x + 5 + getOffsetX();
		int y = this.y + 5;
		float alphaMultiplier;
		if (enabled)
		{
			alphaMultiplier = 1.0F;
		}
		else
		{
			alphaMultiplier = 0.5F;
			GlStateManager.translate(0.5, 0, 0);
		}
		GlStateManager.translate(0, 0, -200);
		GlStateManager.enableDepth();
		if (iconModels == null)
		{
			RenderState.renderStateModelIntoGUI(null, ClientHelper.getRenderItem().getItemModelWithOverrides(iconStack, null, ClientHelper.getPlayer()),
					iconStack, alphaMultiplier, true, false, x - 6, y - 2, 0, 0, 1);
		}
		else
		{
			for (IBakedModel model : iconModels)
				RenderState.renderStateModelIntoGUI(null, model, iconStack, alphaMultiplier, true, false, x - 6, y - 2, 0, 0, 1);
		}
		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
	}
	
}