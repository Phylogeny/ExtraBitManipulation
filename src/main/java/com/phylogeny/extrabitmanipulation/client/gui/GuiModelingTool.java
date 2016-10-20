package com.phylogeny.extrabitmanipulation.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.container.ContainerModelingTool;
import com.phylogeny.extrabitmanipulation.helper.BitIOHelper;
import com.phylogeny.extrabitmanipulation.helper.BitInventoryHelper;
import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;
import com.phylogeny.extrabitmanipulation.item.ItemModelingTool;
import com.phylogeny.extrabitmanipulation.item.ItemModelingTool.BitCount;
import com.phylogeny.extrabitmanipulation.packet.PacketModelingTool;
import com.phylogeny.extrabitmanipulation.packet.PacketSetTabAndStateBlockButton;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;
import com.phylogeny.extrabitmanipulation.reference.Reference;

import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.IBitBrush;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import mod.chiselsandbits.api.ItemType;
import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import mod.chiselsandbits.api.APIExceptions.SpaceOccupied;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;

public class GuiModelingTool extends GuiContainer
{
	public static final ResourceLocation GUI_TEXTURE = new ResourceLocation(Reference.GROUP_ID, "textures/guis/modeling_tool.png");
	private IChiselAndBitsAPI api;
	private GuiListBitMapping bitMappingList;
	private ItemStack modelingToolStack, previewStack, previewResultStack;
	private IBlockState[][][] stateArray;
	private HashMap<IBlockState, Integer> stateMap;
	private HashMap<IBlockState, ArrayList<BitCount>> stateToBitCountArray;
	private HashMap<IBlockState, IBitBrush> stateToBitMapPermanent, stateToBitMapManual, blockToBitMapPermanent, blockToBitMapManual, blockToBitMapAllBlocks;
	private GuiButtonSelect buttonStates, buttonBlocks;
	private GuiButtonTab[] tabButtons = new GuiButtonTab[4];
	private static final String[] tabButtonHoverText = new String[]{"Current Model", "All Saved Mappings", "All Minecraft Blocks", "Model Result"};
	private int savedTab;
	private static boolean stateMauallySelected, savedBlockButton;
	private GuiTextField searchField;
	
	public GuiModelingTool(InventoryPlayer playerInventory, ItemStack modelingToolStack)
	{
		super(new ContainerModelingTool(playerInventory));
		api = ChiselsAndBitsAPIAccess.apiInstance;
		this.modelingToolStack = modelingToolStack;
		xSize = 254;
		ySize = 219;
		NBTTagCompound nbt = modelingToolStack.hasTagCompound() ? modelingToolStack.getTagCompound() : new NBTTagCompound();
		stateMauallySelected = nbt.getBoolean(NBTKeys.BUTTON_STATE_BLOCK_SETTING);
		savedTab = nbt.getInteger(NBTKeys.TAB_SETTING);
		stateToBitMapPermanent = BitIOHelper.readStateToBitMapFromNBT(api, modelingToolStack, NBTKeys.STATE_TO_BIT_MAP_PERMANENT);
		blockToBitMapPermanent = BitIOHelper.readStateToBitMapFromNBT(api, modelingToolStack, NBTKeys.BLOCK_TO_BIT_MAP_PERMANENT);
		stateMap = new HashMap<IBlockState, Integer>();
		stateArray = new IBlockState[16][16][16];
		BitIOHelper.readStatesFromNBT(nbt, stateMap, stateArray);
	}

	private void constructManualMaps()
	{
		stateToBitMapManual = new HashMap<IBlockState, IBitBrush>();
		blockToBitMapManual = new HashMap<IBlockState, IBitBrush>();
		blockToBitMapAllBlocks = new HashMap<IBlockState, IBitBrush>();
		if (stateMap.isEmpty())
			return;
		
		if (tabButtons[2].selected)
		{
			for (Block block : Block.REGISTRY)
			{
				ResourceLocation regName = block.getRegistryName();
				if (regName == null)
					continue;
				
				if (regName.getResourceDomain().equals("chiselsandbits"))
				{
					Item item = Item.getItemFromBlock(block);
					if (item != null)
					{
						ItemType itemType = ChiselsAndBitsAPIAccess.apiInstance.getItemType(new ItemStack(item));
						if (itemType != null && itemType == ItemType.CHISLED_BLOCK)
							continue;
					}
				}
				if (BitIOHelper.isAir(block))
					continue;
				
				IBlockState state = block.getDefaultState();
				addBitToManualMap(state.getBlock().getDefaultState(), blockToBitMapPermanent, blockToBitMapAllBlocks);
			}
		}
		for (IBlockState state : stateMap.keySet())
		{
			addBitToManualMap(state, stateToBitMapPermanent, stateToBitMapManual);
			addBitToManualMap(state.getBlock().getDefaultState(), blockToBitMapPermanent, blockToBitMapManual);
		}
	}
	
	private void addBitToManualMap(IBlockState state, HashMap<IBlockState, IBitBrush> bitMapPermanent, HashMap<IBlockState, IBitBrush> bitMapManual)
	{
		IBitBrush bit = null;
		if (bitMapPermanent.containsKey(state))
		{
			bit = bitMapPermanent.get(state);
		}
		else
		{
			try
			{
				bit = api.createBrushFromState(state);
			}
			catch (InvalidBitItem e) {}
		}
		bitMapManual.put(state, bit);
	}
	
	public void addPermanentMapping(IBlockState state, IBitBrush bit)
	{
		HashMap<IBlockState, IBitBrush> bitMapPermanent = getBitMapPermanent();
		if (bit != null)
		{
			bitMapPermanent.put(state, bit);
			HashMap<IBlockState, IBitBrush> blockToBitMap = getBitMapManual();
			if (blockToBitMap.containsKey(state))
				blockToBitMap.put(state, bit);
		}
		else
		{
			bitMapPermanent.remove(state);
			constructManualMaps();
		}
		String nbtKey = buttonStates.selected ? NBTKeys.STATE_TO_BIT_MAP_PERMANENT : NBTKeys.BLOCK_TO_BIT_MAP_PERMANENT;
		ExtraBitManipulation.packetNetwork.sendToServer(new PacketModelingTool(nbtKey, state, bit, Configs.saveStatesById));
		refreshList();
	}
	
	private void refreshList()
	{
		constructStateToBitCountArray();
		HashMap<IBlockState, IBitBrush> bitMapPermanent = getBitMapPermanent();
		bitMappingList.refreshList(tabButtons[0].selected || tabButtons[2].selected ? getBitMapManual()
				: (tabButtons[3].selected ? null : bitMapPermanent), bitMapPermanent,
				tabButtons[3].selected ? stateToBitCountArray : null, searchField.getText(), buttonStates.selected);
		setPreviewStack();
		tabButtons[0].setIconStack(previewStack);
	}

	private void constructStateToBitCountArray()
	{
		stateToBitCountArray = new HashMap<IBlockState, ArrayList<BitCount>>();
		HashMap<IBitBrush, Integer> bitMap = new HashMap<IBitBrush, Integer>();
		EntityPlayer player = mc.thePlayer;
		ItemModelingTool itemModelingTool = (ItemModelingTool) modelingToolStack.getItem();
		if (itemModelingTool.mapBitsToStates(api, BitInventoryHelper.getInventoryBitCounts(api, player), stateMap,
				stateToBitCountArray, stateToBitMapPermanent, blockToBitMapPermanent, bitMap, player.capabilities.isCreativeMode).isEmpty())
		{
			IBitAccess bitAccess = api.createBitItem(null);
			HashMap<IBlockState, ArrayList<BitCount>> stateToBitCountArrayCopy = new HashMap<IBlockState, ArrayList<BitCount>>();
			for (Entry<IBlockState, ArrayList<BitCount>> entry : stateToBitCountArray.entrySet())
			{
				ArrayList<BitCount> bitCountArray = new ArrayList<BitCount>();
				for (BitCount bitCount : entry.getValue())
				{
					bitCountArray.add(new BitCount(bitCount.getBit(), bitCount.getCount()));
				}
				stateToBitCountArrayCopy.put(entry.getKey(), bitCountArray);
			}
			previewResultStack = itemModelingTool.createModel(null, null, modelingToolStack, stateArray, stateToBitCountArrayCopy, bitAccess)
					? bitAccess.getBitsAsItem(null, ItemType.CHISLED_BLOCK, false) : null;
		}
		else
		{
			previewResultStack = null;
		}
	}

	private HashMap<IBlockState, IBitBrush> getBitMapManual()
	{
		return tabButtons[2].selected ? blockToBitMapAllBlocks : (buttonStates.selected ? stateToBitMapManual : blockToBitMapManual);
	}
	
	private HashMap<IBlockState, IBitBrush> getBitMapPermanent()
	{
		return buttonStates.selected ? stateToBitMapPermanent : blockToBitMapPermanent;
	}
	
	public void setPreviewStack()
	{
		IBitAccess bitAccess = api.createBitItem(null);
		IBitBrush defaultBit = null;
		try
		{
			defaultBit = api.createBrushFromState((Configs.replacementBitsUnchiselable.defaultReplacementBit.getDefaultState()));
		}
		catch (InvalidBitItem e) {}
		for (int i = 0; i < 16; i++)
		{
			for (int j = 0; j < 16; j++)
			{
				for (int k = 0; k < 16; k++)
				{
					IBlockState state = stateArray[i][j][k];
					IBlockState state2 = state.getBlock().getDefaultState();
					boolean stateFound = stateToBitMapManual.containsKey(state);
					boolean savedStateFound = stateToBitMapPermanent.containsKey(state);
					boolean savedBlockFound = blockToBitMapPermanent.containsKey(state2);
					if (stateFound || savedBlockFound)
					{
						IBitBrush bit = savedBlockFound && !savedStateFound ? blockToBitMapPermanent.get(state2) : stateToBitMapManual.get(state);
						try
						{
							bitAccess.setBitAt(i, j, k, bit != null ? bit : defaultBit);
						}
						catch (SpaceOccupied e) {}
					}
				}
			}
		}
		previewStack = bitAccess.getBitsAsItem(null, ItemType.CHISLED_BLOCK, false);
	}
	
	public ItemStack getModelingToolStack()
	{
		return modelingToolStack;
	}
	
	public int getGuiTop()
	{
		return guiTop;
	}
	
	public int getGuiLeft()
	{
		return guiLeft + 24;
	}
	
	@Override
	public void initGui()
	{
		super.initGui();
		guiLeft -= 12;
		searchField = new GuiTextField(6, fontRendererObj, guiLeft + 44, guiTop + 8, 65, 9);
		searchField.setEnableBackgroundDrawing(false);
		searchField.setTextColor(-1);
		constructStateToBitCountArray();
		int slotHeight = 24;
		for (int i = 0; i < tabButtons.length; i++)
		{
			ItemStack iconStack = i == 0 ? previewStack : (i == 1 ? null : (i == 2 ? new ItemStack(Blocks.GRASS) : null));
			float u = 0;
			float v = 0;
			int uWidth = 0;
			int vHeight = 0;
			if (i == 1 || i == 3)
			{
				u = i == 1 ? 134 : 97;
				v = 219;
				uWidth = 36;
				vHeight = 36;
			}
			GuiButtonTab tab = new GuiButtonTab(i, guiLeft, guiTop + 21 + i * 25, 24, 25, tabButtonHoverText[i], iconStack, u, v, uWidth, vHeight);
			if (i == savedTab)
				tab.selected = true;
			
			tabButtons[i] = tab;
			buttonList.add(tab);
		}
		
		int x = guiLeft + 42;
		int y = guiTop + 122;
		int colorSelected = -16726016;
		int colorDeselected = -8882056;
		buttonStates = new GuiButtonSelect(4, x, y, 37, 12, "States", "Map bits to individual block states.", colorSelected, colorDeselected);
		buttonBlocks = new GuiButtonSelect(5, x + 37, y, 36, 12, "Blocks", "Map bits to all posible states of a given block.", colorSelected, colorDeselected);
		buttonStates.enabled = !tabButtons[2].selected;
		buttonBlocks.enabled = !tabButtons[3].selected;
		int selectedTab = getSelectedTab();
		boolean buttonStatesSlected = selectedTab > 1 ? selectedTab == 3 : stateMauallySelected;
		buttonStates.selected = buttonStatesSlected;
		buttonBlocks.selected = !buttonStatesSlected;
		buttonList.add(buttonStates);
		buttonList.add(buttonBlocks);
		
		bitMappingList = new GuiListBitMapping(this, 150, height, guiTop + 21, guiTop + 121, slotHeight);
		constructManualMaps();
		refreshList();
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException
	{
		if (searchField.textboxKeyTyped(typedChar, keyCode))
		{
			refreshList();
		}
		else
		{
			super.keyTyped(typedChar, keyCode);
		}
	}
	
	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();
		bitMappingList.handleMouseInput();
	}
	
	@Override
	public void updateScreen()
	{
		super.updateScreen();
		searchField.updateCursorCounter();
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
		GlStateManager.disableLighting();
		searchField.drawTextBox();
		if (!searchField.isFocused() && searchField.getText().isEmpty())
			fontRendererObj.drawString("search", searchField.xPosition, searchField.yPosition, -10197916);
		
		GlStateManager.enableLighting();
		for (int i = 0; i < tabButtons.length; i++)
		{
			GuiButtonTab tab = tabButtons[i];
			tab.renderIconStack();
			if (tab.selected)
				fontRendererObj.drawString(tabButtonHoverText[i], getGuiLeft() + 103, guiTop + 7, 4210752);
		}
		ItemStack previewStack = tabButtons[3].selected ? previewResultStack : this.previewStack;
		if (previewStack != null)
		{
			RenderHelper.enableGUIStandardItemLighting();
			GlStateManager.pushMatrix();
			GlStateManager.translate(getGuiLeft() + 108, guiTop + 21.5, 0);
			GlStateManager.scale(6.2, 6.2, 1);
			mc.getRenderItem().renderItemIntoGUI(previewStack, 0, 0);
			GlStateManager.popMatrix();
			RenderHelper.disableStandardItemLighting();
		}
		else
		{
			fontRendererObj.drawSplitString("No Preview   Available", getGuiLeft() + 131, guiTop + 63, 60, 0);
		}
		int entryCount = bitMappingList.getSize();
		if (entryCount == 0)
			fontRendererObj.drawSplitString("No " + (buttonStates.selected ? "States" : "Blocks")  + "      Found", getGuiLeft() + 31, guiTop + 63, 60, 0);
		
		for (int i = 0; i < entryCount; i++)
		{
			GuiListBitMappingEntry entry = bitMappingList.getListEntry(i);
			entry.drawEntry(i, 0, 0, bitMappingList.width, bitMappingList.slotHeight, mouseX, mouseY, true);
			if (mouseY >= bitMappingList.top && mouseY <= bitMappingList.bottom)
			{
				RenderHelper.enableGUIStandardItemLighting();
				int slotWidth = 19;
				int k = bitMappingList.left + bitMappingList.width / 2 - bitMappingList.width / 2 + 5;
				int l = bitMappingList.top + 4 + i * (bitMappingList.slotHeight) - bitMappingList.getAmountScrolled();
				AxisAlignedBB slot = new AxisAlignedBB(k, l, -1, k + slotWidth, l + bitMappingList.slotHeight - 5, 1);
				Vec3d mousePos = new Vec3d(mouseX, mouseY, 0);
				if (slot.offset(38, 0, 0).isVecInside(mousePos))
				{
					ArrayList<String> hoverTextList = new ArrayList<String>();
					final String unmappedText = "The blockstate is currently mapped to nothing, as it cannot be chiseled.";
					ArrayList<BitCount> bitCountArray = entry.getBitCountArray();
					for (int j = 0; j < bitCountArray.size(); j++)
					{
						BitCount bitCount = bitCountArray.get(j);
						IBitBrush bit = bitCount.getBit();
						ItemStack bitStack = bit != null ? bit.getItemStack(1) : null;
						boolean isAir = bit != null && bit.isAir();
						String text = bitStack != null ? BitToolSettingsHelper.getBitName(bitStack) : (isAir ? "Empty / Air" : unmappedText);
						if (bitStack != null || entry.isAir())
						{
							String text2 = TextFormatting.DARK_RED + (j == 0 ? "Bit:" : "	") + " " + TextFormatting.RESET;
							if (bitCountArray.size() > 1)
								text2 = (j == 0 ? "" : " ") + text2.replace("Bit:", "Bits:");
							
							text = text2 + text;
							if (!entry.isInteractive())
								text += " (" + bitCount.getCount() + ")";
						}
						if (buttonStates.selected ? stateToBitMapPermanent.containsKey(entry.getState()) : blockToBitMapPermanent.containsKey(entry.getState()))
							text += TextFormatting.BLUE + " (manually mapped)";
						
						hoverTextList.add(text);
					}
					if (entry.getBitCountArray().isEmpty())
					{
						hoverTextList.add(unmappedText);
					}
					if (entry.isInteractive())
					{
						hoverTextList.add(!isShiftKeyDown() ? TextFormatting.AQUA + "  Hold SHIFT for usage instructions."
								: TextFormatting.AQUA + "  - Click with bit or block on cursor to add mapping.");
						if (isShiftKeyDown())
						{
							hoverTextList.add(TextFormatting.AQUA + "  - Shift-click with empty cursor to map to air.");
							hoverTextList.add(TextFormatting.AQUA + "  - Control-click with empty cursor to remove mapping.");
						}
					}
					drawHoveringText(hoverTextList, mouseX, mouseY, mc.fontRendererObj);
				}
				else if (slot.isVecInside(mousePos))
				{
					drawHoveringText(Arrays.<String>asList(new String[] {TextFormatting.DARK_RED + (buttonStates.selected ? "State" : "Block")
							+ ": " + TextFormatting.RESET + (buttonStates.selected ? entry.getState().toString()
									: Block.REGISTRY.getNameForObject(entry.getState().getBlock()))}), mouseX, mouseY, mc.fontRendererObj);
				}
				RenderHelper.disableStandardItemLighting();
			}
		}
		if (buttonStates.isMouseOver() || buttonBlocks.isMouseOver())
		{
			List<String> textList = Arrays.<String>asList(new String[] {buttonStates.isMouseOver()
					? buttonStates.getHoverText() : buttonBlocks.getHoverText()});
			drawHoveringText(textList, mouseX, mouseY, mc.fontRendererObj);
		}
		for (int i = 0; i < tabButtons.length; i++)
		{
			if (tabButtons[i].isMouseOver())
				drawHoveringText(Arrays.<String>asList(new String[] {tabButtons[i].getHoverText()}), mouseX, mouseY, mc.fontRendererObj);
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(GUI_TEXTURE);
		int i = (width - xSize) / 2;
		int j = (height - ySize) / 2;
		drawTexturedModalRect(i - 12, j, 0, 0, xSize, ySize);
		bitMappingList.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException
	{
		int id = button.id;
		if (id >= 0 && id <= 5)
		{
			if (id > 3)
			{
				stateMauallySelected = id == 4;
				selectButtonStatesBlocks(stateMauallySelected);
				constructManualMaps();
				refreshList();
			}
			else
			{
				if (getSelectedTab() == id)
					return;
				
				boolean allBlocksPrev = tabButtons[2].selected;
				boolean resultsPrev = tabButtons[3].selected;
				for (GuiButtonTab tab : tabButtons)
				{
					tab.selected = tab.id == id;
				}
				savedTab = id;
				boolean allBlocks = tabButtons[2].selected;
				boolean results = tabButtons[3].selected;
				buttonStates.enabled = !allBlocks;
				buttonBlocks.enabled = !results;
				if (allBlocks)
				{
					selectButtonStatesBlocks(false);
				}
				else if (allBlocksPrev && stateMauallySelected)
				{
					selectButtonStatesBlocks(true);
				}
				if (results)
				{
					selectButtonStatesBlocks(true);
				}
				else if (resultsPrev && !stateMauallySelected)
				{
					selectButtonStatesBlocks(false);
				}
				if (allBlocksPrev != allBlocks)
					constructManualMaps();
				
				refreshList();
			}
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketSetTabAndStateBlockButton(getSelectedTab(), stateMauallySelected));
		}
		else
		{
			super.actionPerformed(button);
		}
	}

	private int getSelectedTab()
	{
		for (GuiButtonTab tab : tabButtons)
		{
			if (tab.selected)
				return tab.id;
		}
		return 0;
	}

	private void selectButtonStatesBlocks(boolean selectStates)
	{
		buttonStates.selected = selectStates;
		buttonBlocks.selected = !selectStates;
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		bitMappingList.mouseClicked(mouseX, mouseY, mouseButton);
		searchField.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state)
	{
		super.mouseReleased(mouseX, mouseY, state);
		bitMappingList.mouseReleased(mouseX, mouseY, state);
	}
	
}