package com.phylogeny.extrabitmanipulation.client.gui.armor;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import org.lwjgl.input.Keyboard;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.api.jei.JustEnoughItemsPlugin;
import com.phylogeny.extrabitmanipulation.api.jei.armor.ChiseledArmorInfoRecipeCategory;
import com.phylogeny.extrabitmanipulation.capability.armor.ChiseledArmorSlotsHandler;
import com.phylogeny.extrabitmanipulation.capability.armor.IChiseledArmorSlotsHandler;
import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.client.GuiHelper;
import com.phylogeny.extrabitmanipulation.client.gui.button.GuiButtonHelp;
import com.phylogeny.extrabitmanipulation.client.render.RenderState;
import com.phylogeny.extrabitmanipulation.container.ContainerPlayerArmorSlots;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ArmorType;
import com.phylogeny.extrabitmanipulation.packet.PacketOpenInventoryGui;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.Reference;

public class GuiInventoryArmorSlots extends InventoryEffectRenderer
{
	private static final ResourceLocation BACKGROUND = new ResourceLocation(Reference.MOD_ID, "textures/guis/inventory_chiseled_armor_slots.png");
	private static final ResourceLocation IMAGE = new ResourceLocation(Reference.MOD_ID, "textures/jei/images/chiseled_helmet.png");
	private static final int[] ICON_STACK_OFFSETS = new int[]{3, 1, 0, -3};
	private float oldMouseX, oldMouseY;
	private boolean helpMode;
	private GuiButtonHelp buttonHelp;
	private GuiButtonExt buttonJEI;
	
	public GuiInventoryArmorSlots(ContainerPlayerArmorSlots container)
	{
		super(container);
		allowUserInput = true;
		helpMode = false;
	}
	
	private void resetGuiLeft()
	{
		this.guiLeft = (this.width - this.xSize) / 2;
	}
	
	@Override
	public void updateScreen()
	{
		updateActivePotionEffects();
		resetGuiLeft();
	}
	
	@Override
	public void initGui()
	{
		buttonList.clear();
		super.initGui();
		resetGuiLeft();
		buttonHelp = new GuiButtonHelp(100, buttonList, guiLeft + xSize - 17, guiTop + 5, "Show slot/button hover help text", "Exit help mode");
		buttonHelp.selected = helpMode;
		buttonJEI = new GuiButtonExt(100, guiLeft + 119, guiTop + 55, 46, 26, "");
		buttonJEI.visible = JustEnoughItemsPlugin.isJeiInstalled() ? helpMode : false;
		buttonList.add(buttonHelp);
		buttonList.add(buttonJEI);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRenderer.drawString(I18n.format("container.crafting"), 97, 8, 4210752);
		if (!helpMode)
			return;
		
		int color = buttonJEI.isMouseOver() ? 16777120 : -1;
		fontRenderer.drawString("JEI", 123, 59, color);
		fontRenderer.drawString("Info", 122, 69, color);
		ClientHelper.bindTexture(IMAGE);
		int x = 143;
		int y = 58;
		GuiHelper.drawTexturedRect(x, y, x + 18.5, y + 20);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		drawDefaultBackground();
		oldMouseX = mouseX;
		oldMouseY = mouseY;
		super.drawScreen(mouseX, mouseY, partialTicks);
		GuiHelper.drawHoveringTextForButtons(this, buttonList, mouseX, mouseY);
		if (buttonJEI.isMouseOver())
			GuiHelper.drawHoveringText(this, mouseX, mouseY, "Get more info on Chiseled Armor");
		
		boolean cancelStackHoverTextRender = false;
		if (helpMode)
		{
			Slot slot = getSlotUnderMouse();
			if (slot != null && slot.slotNumber > 45)
			{
				GuiHelper.drawHoveringText(this, mouseX, mouseY, "Only Chiseled Armor with items to render can be put in these slots.\n\nArmor warn here " +
						"will render in addition to any normally worn armor, but will not confer any additional protection.");
				cancelStackHoverTextRender = true;
			}
		}
		if (!cancelStackHoverTextRender)
			renderHoveredToolTip(mouseX, mouseY);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(BACKGROUND);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		GuiInventory.drawEntityOnScreen(guiLeft + 51, guiTop + 75, 30, guiLeft + 51 - oldMouseX, guiTop + 75 - 50 - oldMouseY, mc.player);
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, -200);
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.enableDepth();
		IChiseledArmorSlotsHandler cap = ChiseledArmorSlotsHandler.getCapability(mc.player);
		for (int i = 0; i < ArmorType.values().length; i++)
		{
			if (cap == null || cap.getStackInSlot(i).isEmpty())
			{
				RenderState.renderStateModelIntoGUI(null, ArmorType.values()[i].getIconModel(),
						ItemStack.EMPTY, 0.4F, true, false, guiLeft + 71, guiTop + 6 + i * 18 + ICON_STACK_OFFSETS[i], 0, 0, 1);
			}
			if (helpMode)
			{
				Slot slot = inventorySlots.inventorySlots.get(46 + i);
				drawRect(slot.xPos, slot.yPos, slot.xPos + 16, slot.yPos + 16, 1694460416);
			}
		}
		GlStateManager.popMatrix();
		if (helpMode)
		{
			int x, y;
			for (int i = 0; i < ArmorType.values().length; i++)
			{
				Slot slot = inventorySlots.inventorySlots.get(46 + i);
				x = guiLeft + slot.xPos;
				y = guiTop + slot.yPos;
				drawRect(x, y, x + 16, y + 16, GuiChiseledArmor.HELP_TEXT_BACKGROUNG_COLOR);
			}
		}
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		if (Configs.armorSlotsGuiExitToMainInventory && (keyCode == Keyboard.KEY_ESCAPE || mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)))
		{
			openVanillaInventory(oldMouseX, oldMouseY);
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketOpenInventoryGui(true));
			return;
		}
		super.keyTyped(typedChar, keyCode);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if (button == buttonJEI)
			JustEnoughItemsPlugin.openCategory(ChiseledArmorInfoRecipeCategory.UID);
		else
			super.actionPerformed(button);
		
		if (JustEnoughItemsPlugin.isJeiInstalled())
			buttonJEI.visible = helpMode = buttonHelp.selected;
	}
	
	
	public void openVanillaInventory(float mouseX, float mouseY)
	{
		EntityPlayer player = ClientHelper.getPlayer();
		player.openContainer.onContainerClosed(player);
		GuiInventory gui = new GuiInventory(player);
		mc.displayGuiScreen(gui);
		ReflectionHelper.setPrivateValue(GuiInventory.class, gui, mouseX, "oldMouseX", "field_147048_u");
		ReflectionHelper.setPrivateValue(GuiInventory.class, gui, mouseY, "oldMouseY", "field_147047_v");
	}
	
}