package com.phylogeny.extrabitmanipulation.init;

import com.phylogeny.extrabitmanipulation.reference.Reference;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public enum KeyBindingsExtraBitManipulation
{
	OPEN_MODEING_TOOL_GUI("modeling.gui")
	{
		@Override
		public boolean isKeyDown()
		{
			return getKeyBinding().isKeyDown();
		}
	},
	
	SHIFT("Shift")
	{
		@Override
		public boolean isKeyDown()
		{
			return isKeyDown(GuiScreen.isShiftKeyDown());
		}
	},
	
	CONTROL("Control")
	{
		@Override
		public boolean isKeyDown()
		{
			return isKeyDown(GuiScreen.isCtrlKeyDown());
		}
	},
	
	ALT("Alt")
	{
		@Override
		public boolean isKeyDown()
		{
			return isKeyDown(GuiScreen.isAltKeyDown());
		}
	};
	
	private KeyBinding keyBinding;
	private String description;
	
	private KeyBindingsExtraBitManipulation(String description)
	{
		this.description = description;
	}
	
	public boolean isKeyDown()
	{
		return false;
	}
	
	protected boolean isKeyDown(boolean defaultCheck)
	{
		return isSetToDefaultValue() ? defaultCheck : getKeyBinding().isKeyDown();
	}
	
	public boolean isSetToDefaultValue()
	{
		return keyBinding.getKeyCode() == keyBinding.getKeyCodeDefault();
	}
	
	public void register()
	{
		keyBinding = new KeyBinding("keybinding." + Reference.GROUP_ID + "." + description.toLowerCase(), 0, "itemGroup." + Reference.MOD_ID);
		ClientRegistry.registerKeyBinding(keyBinding);
	}
	
	public String getText()
	{
		return isSetToDefaultValue() ? description.toUpperCase() : ("[" + GameSettings.getKeyDisplayString(keyBinding.getKeyCode()) + "]");
	}
	
	public KeyBinding getKeyBinding()
	{
		return keyBinding;
	}
	
}