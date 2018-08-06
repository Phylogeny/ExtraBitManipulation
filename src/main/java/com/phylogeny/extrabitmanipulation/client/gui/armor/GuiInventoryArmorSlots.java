package com.phylogeny.extrabitmanipulation.client.gui.armor;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import org.lwjgl.input.Keyboard;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.api.jei.JustEnoughItemsPlugin;
import com.phylogeny.extrabitmanipulation.api.jei.armor.ChiseledArmorInfoRecipeCategory;
import com.phylogeny.extrabitmanipulation.armor.capability.ChiseledArmorSlotsHandler;
import com.phylogeny.extrabitmanipulation.armor.capability.IChiseledArmorSlotsHandler;
import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.client.GuiHelper;
import com.phylogeny.extrabitmanipulation.client.GuiHelper.IHoveringTextRenderer;
import com.phylogeny.extrabitmanipulation.client.gui.button.GuiButtonHelp;
import com.phylogeny.extrabitmanipulation.client.render.RenderState;
import com.phylogeny.extrabitmanipulation.container.ContainerPlayerArmorSlots;
import com.phylogeny.extrabitmanipulation.container.SlotChiseledArmor;
import com.phylogeny.extrabitmanipulation.init.ReflectionExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ArmorType;
import com.phylogeny.extrabitmanipulation.packet.PacketOpenInventoryGui;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.JeiReferences;
import com.phylogeny.extrabitmanipulation.reference.Reference;

public class GuiInventoryArmorSlots extends InventoryEffectRenderer implements IHoveringTextRenderer
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
	
	public boolean isInHelpMode()
	{
		return helpMode;
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
		buttonHelp = new GuiButtonHelp(100, buttonList, guiLeft + xSize / 2 + 10, guiTop + 10, "Show slot/button hover help text", "Exit help mode");
		buttonHelp.selected = helpMode;
		buttonJEI = new GuiButtonExt(100, guiLeft + xSize / 2 - 7, guiTop - 27, 46, 26, "");
		buttonJEI.visible = JeiReferences.isLoaded ? helpMode : false;
		buttonList.add(buttonHelp);
		buttonList.add(buttonJEI);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		if (!helpMode || !JeiReferences.isLoaded)
			return;
		
		int color = buttonJEI.isMouseOver() ? 16777120 : -1;
		int centerX = xSize / 2;
		fontRendererObj.drawString("JEI", centerX + -3, -23, color);
		fontRendererObj.drawString("Info", centerX + -4, -13, color);
		ClientHelper.bindTexture(IMAGE);
		int x = centerX + 17;
		int y = -24;
		GuiHelper.drawTexturedRect(x, y, x + 18.5, y + 20);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		oldMouseX = mouseX;
		oldMouseY = mouseY;
		Slot slot = getSlotUnderMouse();
		for (int i = 46; i < 62 && helpMode; i++)
			((SlotChiseledArmor) inventorySlots.getSlot(i)).setDisabled(true);
		
		super.drawScreen(mouseX, mouseY, partialTicks);
		for (int i = 46; i < 62 && helpMode; i++)
			((SlotChiseledArmor) inventorySlots.getSlot(i)).setDisabled(false);
		
		GuiHelper.drawHoveringTextForButtons(this, buttonList, mouseX, mouseY);
		if (buttonJEI.isMouseOver())
			drawCreativeTabHoveringText("Get more info on Chiseled Armor", mouseX, mouseY);
		
		if (slot != null && helpMode && slot instanceof SlotChiseledArmor)
		{
			drawCreativeTabHoveringText("Only Chiseled Armor with items to render can be put in these slots.\n\nArmor warn here will render " +
					TextFormatting.BLUE + (slot.slotNumber < 50 ? "in place of" : "in addition to") + TextFormatting.WHITE + 
					" any normally worn armor, but will not confer any additional protection.", mouseX, mouseY);
		}
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
		for (int i = 0; i < ChiseledArmorSlotsHandler.COUNT_SETS; i++)
		{
			for (int j = 0; j <ChiseledArmorSlotsHandler.COUNT_TYPES; j++)
			{
				if (cap == null || cap.getStackInSlot(j * ChiseledArmorSlotsHandler.COUNT_TYPES + i).isEmpty())
				{
					RenderState.renderStateModelIntoGUI(null, ArmorType.values()[i].getIconModel(),
							ItemStack.EMPTY, 0.4F, true, false, guiLeft + 71 + 18 * j + (j == 0 ? 0 : 21), guiTop + 6 + i * 18 + ICON_STACK_OFFSETS[i], 0, 0, 1);
				}
			}
		}
		GlStateManager.popMatrix();
		if (helpMode)
		{
			int x, y;
			for (int i = 0; i < ChiseledArmorSlotsHandler.COUNT_SETS; i++)
			{
				for (int j = 0; j < ChiseledArmorSlotsHandler.COUNT_TYPES; j++)
				{
					Slot slot = inventorySlots.inventorySlots.get(46 + i * ChiseledArmorSlotsHandler.COUNT_TYPES + j);
					x = guiLeft + slot.xPos;
					y = guiTop + slot.yPos;
					drawRect(x, y, x + 16, y + 16, GuiChiseledArmor.HELP_TEXT_BACKGROUNG_COLOR);
				}
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
		
		helpMode = buttonHelp.selected;
		if (JeiReferences.isLoaded)
			buttonJEI.visible = helpMode;
	}
	
	
	public void openVanillaInventory(float mouseX, float mouseY)
	{
		EntityPlayer player = ClientHelper.getPlayer();
		player.openContainer.onContainerClosed(player);
		GuiInventory gui = new GuiInventory(player);
		mc.displayGuiScreen(gui);
		ReflectionExtraBitManipulation.setCursorPosition(gui, mouseX, mouseY);
	}
	
	@Override
	public void render(List<String> text, int mouseX, int mouseY)
	{
		drawHoveringText(text, mouseX, mouseY);
	}
	
}