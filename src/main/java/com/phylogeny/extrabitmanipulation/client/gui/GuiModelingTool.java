package com.phylogeny.extrabitmanipulation.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.config.ConfigHandlerExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.container.ContainerModelingTool;
import com.phylogeny.extrabitmanipulation.helper.BitIOHelper;
import com.phylogeny.extrabitmanipulation.helper.BitInventoryHelper;
import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.init.SoundsExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.item.ItemModelingTool;
import com.phylogeny.extrabitmanipulation.item.ItemModelingTool.BitCount;
import com.phylogeny.extrabitmanipulation.packet.PacketBitMappingsPerTool;
import com.phylogeny.extrabitmanipulation.packet.PacketClearStackBitMappings;
import com.phylogeny.extrabitmanipulation.packet.PacketModelingTool;
import com.phylogeny.extrabitmanipulation.packet.PacketOverwriteStackBitMappings;
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
	public static final ResourceLocation BOX_CHECKED = new ResourceLocation(Reference.GROUP_ID, "textures/guis/box_checked.png");
	public static final ResourceLocation BOX_UNCHECKED = new ResourceLocation(Reference.GROUP_ID, "textures/guis/box_unchecked.png");
	public static final ResourceLocation SETTINGS_MAIN = new ResourceLocation(Reference.GROUP_ID, "textures/guis/settings_main.png");
	public static final ResourceLocation SETTINGS_BACK = new ResourceLocation(Reference.GROUP_ID, "textures/guis/settings_back.png");
	private IChiselAndBitsAPI api;
	private GuiListBitMapping bitMappingList;
	private ItemStack previewStack, previewResultStack;
	private IBlockState[][][] stateArray;
	private Map<IBlockState, Integer> stateMap;
	private Map<IBlockState, ArrayList<BitCount>> stateToBitCountArray;
	private Map<IBlockState, IBitBrush> stateToBitMapPermanent, stateToBitMapManual, blockToBitMapPermanent, blockToBitMapManual, blockToBitMapAllBlocks;
	private GuiButtonSelect buttonStates, buttonBlocks;
	private GuiButtonTextured buttonSettings, buttonBitMapPerTool;
	private GuiButtonGradient buttonOverwriteStackMapsWithConfig, buttonOverwriteConfigMapsWithStack, buttonRestoreConfigMaps, buttonClearStackMaps;
	private GuiButtonTab[] tabButtons = new GuiButtonTab[4];
	private static final String[] tabButtonHoverText = new String[]{"Current Model", "All Saved Mappings", "All Minecraft Blocks", "Model Result"};
	private int savedTab;
	private boolean stateMauallySelected, showSettings, bitMapPerTool;
	private GuiTextField searchField;
	
	public GuiModelingTool(InventoryPlayer playerInventory)
	{
		super(new ContainerModelingTool(playerInventory));
		api = ChiselsAndBitsAPIAccess.apiInstance;
		xSize = 254;
		ySize = 219;
		NBTTagCompound nbt = ItemStackHelper.getNBTOrNew(playerInventory.getCurrentItem());
		stateMauallySelected = nbt.getBoolean(NBTKeys.BUTTON_STATE_BLOCK_SETTING);
		savedTab = nbt.getInteger(NBTKeys.TAB_SETTING);
		bitMapPerTool = nbt.getBoolean(NBTKeys.BIT_MAPS_PER_TOOL);
	}

	private void constructManualMaps()
	{
		stateToBitMapManual = new LinkedHashMap<IBlockState, IBitBrush>();
		blockToBitMapManual = new LinkedHashMap<IBlockState, IBitBrush>();
		blockToBitMapAllBlocks = new LinkedHashMap<IBlockState, IBitBrush>();
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
	
	private void addBitToManualMap(IBlockState state, Map<IBlockState, IBitBrush> bitMapPermanent, Map<IBlockState, IBitBrush> bitMapManual)
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
		Map<IBlockState, IBitBrush> bitMapPermanent = getBitMapPermanent();
		if (bit != null)
		{
			bitMapPermanent.put(state, bit);
			Map<IBlockState, IBitBrush> blockToBitMap = getBitMapManual();
			if (blockToBitMap.containsKey(state))
				blockToBitMap.put(state, bit);
		}
		else
		{
			bitMapPermanent.remove(state);
			constructManualMaps();
		}
		if (bitMapPerTool)
		{
			String nbtKey = buttonStates.selected ? NBTKeys.STATE_TO_BIT_MAP_PERMANENT : NBTKeys.BLOCK_TO_BIT_MAP_PERMANENT;
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketModelingTool(nbtKey, state, bit, Configs.saveStatesById));
		}
		else
		{
			Map<IBlockState, IBitBrush> bitMap = buttonStates.selected ? Configs.modelStateToBitMap : Configs.modelBlockToBitMap;
			if (bit != null)
			{
				bitMap.put(state, bit);
			}
			else
			{
				bitMap.remove(state);
			}
			String[] entryStrings = BitIOHelper.getEntryStringsFromModelBitMap(bitMap);
			if (buttonStates.selected)
			{
				Configs.modelStateToBitMapEntryStrings = entryStrings;
			}
			else
			{
				Configs.modelBlockToBitMapEntryStrings = entryStrings;
			}
			BitToolSettingsHelper.setBitMapProperty(buttonStates.selected, entryStrings);
		}
		refreshList();
	}
	
	private void refreshList()
	{
		constructStateToBitCountArray();
		Map<IBlockState, IBitBrush> bitMapPermanent = getBitMapPermanent();
		bitMappingList.refreshList(tabButtons[0].selected || tabButtons[2].selected ? getBitMapManual()
				: (tabButtons[3].selected ? null : bitMapPermanent), bitMapPermanent,
				tabButtons[3].selected ? stateToBitCountArray : null, searchField.getText(), buttonStates.selected);
		setPreviewStack();
		tabButtons[0].setIconStack(previewStack);
	}

	private void constructStateToBitCountArray()
	{
		stateToBitCountArray = new LinkedHashMap<IBlockState, ArrayList<BitCount>>();
		Map<IBitBrush, Integer> bitMap = new HashMap<IBitBrush, Integer>();
		EntityPlayer player = mc.thePlayer;
		ItemModelingTool itemModelingTool = (ItemModelingTool) getModelingToolStack().getItem();
		if (itemModelingTool.mapBitsToStates(api, Configs.replacementBitsUnchiselable, Configs.replacementBitsInsufficient,
				BitInventoryHelper.getInventoryBitCounts(api, player), stateMap, stateToBitCountArray,
				stateToBitMapPermanent, blockToBitMapPermanent, bitMap, player.capabilities.isCreativeMode).isEmpty())
		{
			IBitAccess bitAccess = api.createBitItem(null);
			Map<IBlockState, ArrayList<BitCount>> stateToBitCountArrayCopy = new HashMap<IBlockState, ArrayList<BitCount>>();
			for (Entry<IBlockState, ArrayList<BitCount>> entry : stateToBitCountArray.entrySet())
			{
				ArrayList<BitCount> bitCountArray = new ArrayList<BitCount>();
				for (BitCount bitCount : entry.getValue())
				{
					bitCountArray.add(new BitCount(bitCount.getBit(), bitCount.getCount()));
				}
				stateToBitCountArrayCopy.put(entry.getKey(), bitCountArray);
			}
			previewResultStack = itemModelingTool.createModel(null, null, getModelingToolStack(), stateArray, stateToBitCountArrayCopy, bitAccess)
					? bitAccess.getBitsAsItem(null, ItemType.CHISLED_BLOCK, false) : null;
		}
		else
		{
			previewResultStack = null;
		}
	}

	private Map<IBlockState, IBitBrush> getBitMapManual()
	{
		return tabButtons[2].selected ? blockToBitMapAllBlocks : (buttonStates.selected ? stateToBitMapManual : blockToBitMapManual);
	}
	
	private Map<IBlockState, IBitBrush> getBitMapPermanent()
	{
		return buttonStates.selected ? stateToBitMapPermanent : blockToBitMapPermanent;
	}
	
	public void setPreviewStack()
	{
		IBitAccess bitAccess = api.createBitItem(null);
		IBitBrush defaultBit = null;
		try
		{
			defaultBit = api.createBrushFromState((Configs.replacementBitsUnchiselable.getDefaultReplacementBit().getDefaultState()));
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
		return mc.thePlayer.getHeldItemMainhand();
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
		buttonSettings = new GuiButtonTextured(7, guiLeft + 237, guiTop + 6, 12, 12,
				"Bit Mapping Settings", "Back To Preview", SETTINGS_BACK, SETTINGS_MAIN, null, null);
		buttonBitMapPerTool = new GuiButtonTextured(8, guiLeft + 143, guiTop + 26, 12, 12, "Save/access mappings per tool or per client config",
				BOX_CHECKED, BOX_UNCHECKED, SoundsExtraBitManipulation.boxCheck, SoundsExtraBitManipulation.boxUncheck);
		if (showSettings)
			buttonSettings.selected = true;
		
		int y = guiTop + 44;
		int offsetY = 19;
		String hovertext = "Overwrite mappings saved in 1 with the mappings saved in 2";
		String stackText = "this Modeling Tool's NBT";
		String configText = "the client config file";
		buttonOverwriteStackMapsWithConfig = new GuiButtonGradient(9, guiLeft + 130, y, 102, 14,
				"Write Config->Stack", hovertext.replace("1", stackText).replace("2", configText));
		buttonOverwriteConfigMapsWithStack = new GuiButtonGradient(10, guiLeft + 130, y + offsetY, 102, 14,
				"Write Stack->Config", hovertext.replace("2", stackText).replace("1", configText));
		buttonRestoreConfigMaps = new GuiButtonGradient(11, guiLeft + 130, y + offsetY * 2, 102, 14,
				"Reset Config Maps", "Reset " + configText + " mapping data to their default values");
		buttonClearStackMaps = new GuiButtonGradient(12, guiLeft + 130, y + offsetY * 3, 102, 14,
				"Clear Stack Data", "Delete all saved mappping data from " + stackText);
		updateButtons();
		if (bitMapPerTool)
		{
			buttonBitMapPerTool.selected = true;
			stateToBitMapPermanent = BitIOHelper.readStateToBitMapFromNBT(api, getModelingToolStack(), NBTKeys.STATE_TO_BIT_MAP_PERMANENT);
			blockToBitMapPermanent = BitIOHelper.readStateToBitMapFromNBT(api, getModelingToolStack(), NBTKeys.BLOCK_TO_BIT_MAP_PERMANENT);
		}
		else
		{
			stateToBitMapPermanent = Configs.modelStateToBitMap;
			blockToBitMapPermanent = Configs.modelBlockToBitMap;
		}
		stateToBitMapPermanent = getSortedLinkedBitMap(stateToBitMapPermanent);
		blockToBitMapPermanent = getSortedLinkedBitMap(blockToBitMapPermanent);
		buttonList.add(buttonSettings);
		buttonList.add(buttonBitMapPerTool);
		buttonList.add(buttonOverwriteStackMapsWithConfig);
		buttonList.add(buttonOverwriteConfigMapsWithStack);
		buttonList.add(buttonRestoreConfigMaps);
		buttonList.add(buttonClearStackMaps);
		stateMap = new HashMap<IBlockState, Integer>();
		stateArray = new IBlockState[16][16][16];
		BitIOHelper.readStatesFromNBT(ItemStackHelper.getNBTOrNew(getModelingToolStack()), stateMap, stateArray);
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
		y = guiTop + 122;
		int colorSelected = -16726016;
		int colorDeselected = -8882056;
		buttonStates = new GuiButtonSelect(4, x, y, 37, 12, "States", "Map bits to individual block states", colorSelected, colorDeselected);
		buttonBlocks = new GuiButtonSelect(5, x + 37, y, 36, 12, "Blocks", "Map bits to all posible states of a given block", colorSelected, colorDeselected);
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
	
	private LinkedHashMap getSortedLinkedBitMap(Map<IBlockState, IBitBrush> bitMap)
	{
		return BitInventoryHelper.getSortedLinkedHashMap(bitMap, new Comparator<Object>() {
			@Override
			public int compare(Object object1, Object object2)
			{
				return getName(object1).compareTo(getName(object2));
			}
			
			@SuppressWarnings("unchecked")
			private String getName(Object object)
			{
				IBlockState state = ((Map.Entry<IBlockState, IBitBrush>) (object)).getKey();
				Block block = state.getBlock();
				ResourceLocation regName = block.getRegistryName();
				return regName == null ? "" : (regName.getResourceDomain() + regName.getResourcePath() + block.getMetaFromState(state));
			}
		});
	}
	
	private void updateButtons()
	{
		buttonBitMapPerTool.visible = buttonOverwriteStackMapsWithConfig.visible = buttonOverwriteConfigMapsWithStack.visible
				= buttonOverwriteConfigMapsWithStack.visible = buttonRestoreConfigMaps.visible = buttonClearStackMaps.visible = showSettings;
		if (!showSettings)
			return;
		
		LinkedHashMap stateToBitMapSorted = getSortedLinkedBitMap(Configs.modelStateToBitMap);
		LinkedHashMap blockToBitMapSorted = getSortedLinkedBitMap(Configs.modelBlockToBitMap);
		buttonOverwriteStackMapsWithConfig.enabled = buttonOverwriteConfigMapsWithStack.enabled
				= !BitIOHelper.areSortedBitMapsIdentical(stateToBitMapSorted,
					getSortedLinkedBitMap(BitIOHelper.readStateToBitMapFromNBT(api, getModelingToolStack(), NBTKeys.STATE_TO_BIT_MAP_PERMANENT)))
				|| !BitIOHelper.areSortedBitMapsIdentical(blockToBitMapSorted,
						getSortedLinkedBitMap(BitIOHelper.readStateToBitMapFromNBT(api, getModelingToolStack(), NBTKeys.BLOCK_TO_BIT_MAP_PERMANENT)));
		buttonRestoreConfigMaps.enabled = !BitIOHelper.areSortedBitMapsIdentical(stateToBitMapSorted,
				getSortedLinkedBitMap(BitIOHelper.getModelBitMapFromEntryStrings(ConfigHandlerExtraBitManipulation.STATE_TO_BIT_MAP_DEFAULT_VALUES)))
			|| !BitIOHelper.areSortedBitMapsIdentical(blockToBitMapSorted,
					getSortedLinkedBitMap(BitIOHelper.getModelBitMapFromEntryStrings(ConfigHandlerExtraBitManipulation.BLOCK_TO_BIT_MAP_DEFAULT_VALUES)));
		buttonClearStackMaps.enabled = BitIOHelper.hasBitMapsInNbt(getModelingToolStack());
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
		if (showSettings)
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0.5, 0);
			fontRendererObj.drawString("Map Per Tool", getGuiLeft() + 133, guiTop + 29, 4210752);
			GlStateManager.popMatrix();
		}
		else
		{
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
		for (GuiButton button : buttonList)
		{
			if (!(button instanceof GuiButtonBase))
				continue;
			
			GuiButtonBase buttonBase = (GuiButtonBase) button;
			if (button.isMouseOver() && button.visible)
				drawHoveringText(Arrays.<String>asList(new String[] {button instanceof GuiButtonTextured && buttonBase.selected
						? ((GuiButtonTextured) button).getSelectedHoverText() : buttonBase.getHoverText()}), mouseX, mouseY, mc.fontRendererObj);
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
		else if (id == 7)
		{
			showSettings ^= true;
			buttonSettings.selected = showSettings;
			updateButtons();
		}
		else if (id == 8)
		{
			bitMapPerTool ^= true;
			buttonBitMapPerTool.selected = bitMapPerTool;
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketBitMappingsPerTool(bitMapPerTool));
		}
		else if (id == 9)
		{
			ItemStack stack = getModelingToolStack();
			overwriteStackBitMappings(stack, Configs.modelBlockToBitMap, NBTKeys.BLOCK_TO_BIT_MAP_PERMANENT);
			overwriteStackBitMappings(stack, Configs.modelStateToBitMap, NBTKeys.STATE_TO_BIT_MAP_PERMANENT);
		}
		else if (id == 10 || id == 11)
		{
			Configs.modelBlockToBitMapEntryStrings = id == 10 ? overwriteConfigMapWithStackMap(Configs.modelBlockToBitMap, NBTKeys.BLOCK_TO_BIT_MAP_PERMANENT)
					: ConfigHandlerExtraBitManipulation.BLOCK_TO_BIT_MAP_DEFAULT_VALUES;
			Configs.modelStateToBitMapEntryStrings = id == 10 ? overwriteConfigMapWithStackMap(Configs.modelStateToBitMap, NBTKeys.STATE_TO_BIT_MAP_PERMANENT)
					: ConfigHandlerExtraBitManipulation.STATE_TO_BIT_MAP_DEFAULT_VALUES;
			if (id == 11)
			{
				BitToolSettingsHelper.setBitMapProperty(true, Configs.modelStateToBitMapEntryStrings);
				BitToolSettingsHelper.setBitMapProperty(false, Configs.modelBlockToBitMapEntryStrings);
			}
			Configs.initModelingBitMaps();
		}
		else if (id == 12)
		{
			BitIOHelper.clearAllBitMapsFromNbt(getModelingToolStack());
			ExtraBitManipulation.packetNetwork.sendToServer(new PacketClearStackBitMappings());
		}
		else
		{
			super.actionPerformed(button);
			return;
		}
		if (id >= 8)
			setWorldAndResolution(mc, width, height);
	}
	
	private void overwriteStackBitMappings(ItemStack stack, Map<IBlockState, IBitBrush> bitMap, String key)
	{
		BitIOHelper.writeStateToBitMapToNBT(stack, key, bitMap, Configs.saveStatesById);
		ExtraBitManipulation.packetNetwork.sendToServer(new PacketOverwriteStackBitMappings(bitMap, key, Configs.saveStatesById));
	}
	
	private String[] overwriteConfigMapWithStackMap(Map<IBlockState, IBitBrush> bitMap, String key)
	{
		String[] entryStrings = BitIOHelper.getEntryStringsFromModelBitMap(BitIOHelper.readStateToBitMapFromNBT(api, getModelingToolStack(), key));
		BitToolSettingsHelper.setBitMapProperty(bitMap.equals(Configs.modelStateToBitMap), entryStrings);
		return entryStrings;
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
		searchField.mouseClicked(mouseX, mouseY, mouseButton);
		bitMappingList.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state)
	{
		super.mouseReleased(mouseX, mouseY, state);
		bitMappingList.mouseReleased(mouseX, mouseY, state);
		updateButtons();
	}
	
}