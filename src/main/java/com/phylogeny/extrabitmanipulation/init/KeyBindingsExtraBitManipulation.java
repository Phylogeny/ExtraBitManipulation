package com.phylogeny.extrabitmanipulation.init;

import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.reference.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public enum KeyBindingsExtraBitManipulation implements IKeyConflictContext
{
	OPEN_MODEING_TOOL_GUI("modeling.gui", true, false, false)
	{
		@Override
		public boolean isKeyDown()
		{
			return getKeyBinding().isKeyDown();
		}
	},
	
	SHIFT("Shift", false, true, true)
	{
		@Override
		public boolean isKeyDown()
		{
			return isKeyDown(GuiScreen.isShiftKeyDown());
		}
	},
	
	CONTROL("Control", false, false, true)
	{
		@Override
		public boolean isKeyDown()
		{
			return isKeyDown(GuiScreen.isCtrlKeyDown());
		}
	},
	
	ALT("Alt", false, false, false)
	{
		@Override
		public boolean isKeyDown()
		{
			return isKeyDown(GuiScreen.isAltKeyDown());
		}
	};
	
	private KeyBinding keyBinding;
	private String description;
	private boolean checkForOnlyModelingTool, checkForWrench, anyConflicts;
	
	private KeyBindingsExtraBitManipulation(String description, boolean checkForOnlyModelingTool, boolean checkForWrench, boolean anyConflicts)
	{
		this.description = description;
		this.checkForOnlyModelingTool = checkForOnlyModelingTool;
		this.checkForWrench = checkForWrench;
		this.anyConflicts = anyConflicts;
	}
	
	public boolean isKeyDown()
	{
		return false;
	}
	
	protected boolean isKeyDown(boolean defaultCheck)
	{
		return getKeyBinding().isSetToDefaultValue() ? defaultCheck : getKeyBinding().isKeyDown();
	}
	
	public void register()
	{
		keyBinding = new KeyBinding("keybinding." + Reference.GROUP_ID + "." + description.toLowerCase(),
				this, 0, "itemGroup." + Reference.MOD_ID);
		ClientRegistry.registerKeyBinding(keyBinding);
	}
	
	public String getText()
	{
		return keyBinding.isSetToDefaultValue() ? description.toUpperCase() : ("[" + keyBinding.getDisplayName() + "]");
	}
	
	public KeyBinding getKeyBinding()
	{
		return keyBinding;
	}
	
	@Override
	public boolean isActive()
	{
		ItemStack stack = Minecraft.getMinecraft().thePlayer.getHeldItemMainhand();
		return ItemStackHelper.isModelingToolStack(stack) || (!checkForOnlyModelingTool
				&& (ItemStackHelper.isSculptingToolStack(stack) || (checkForWrench && ItemStackHelper.isBitWrenchStack(stack))));
	}
	
	@Override
	public boolean conflicts(IKeyConflictContext other)
	{
		return other == this || other == KeyConflictContext.IN_GAME || other == SHIFT || other == CONTROL
				|| (anyConflicts && (other == ALT || other == OPEN_MODEING_TOOL_GUI));
	}
	
}