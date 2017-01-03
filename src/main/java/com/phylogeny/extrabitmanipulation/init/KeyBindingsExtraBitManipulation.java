package com.phylogeny.extrabitmanipulation.init;

import org.lwjgl.input.Keyboard;

import com.phylogeny.extrabitmanipulation.reference.Reference;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public enum KeyBindingsExtraBitManipulation
{
	
	OPEN_BIT_MAPPING_GUI("bitmapping.gui", Keyboard.KEY_R)
	{
		@Override
		public boolean isKeyDown()
		{
			return getKeyBinding().isKeyDown();
		}
	},
	
	SHIFT("Shift", 0) 
	{
		@Override
		public boolean isKeyDown()
		{
			return isKeyDown(GuiScreen.isShiftKeyDown());
		}
	},
	
	CONTROL("Control", 0)
	{
		@Override
		public boolean isKeyDown()
		{
			return isKeyDown(GuiScreen.isCtrlKeyDown());
		}
	},
	
	ALT("Alt", 0)
	{
		@Override
		public boolean isKeyDown()
		{
			return isKeyDown(GuiScreen.isAltKeyDown());
		}
	};
	
	private KeyBinding keyBinding;
	private String description;
	private int defaultKeyCode;
	
	private KeyBindingsExtraBitManipulation(String description, int defaultKeyCode)
	{
		this.description = description;
		this.defaultKeyCode = defaultKeyCode;
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
	
	public static void init()
	{
		for (KeyBindingsExtraBitManipulation keyBinding : values())
		{
			keyBinding.registerKeyBinding();
		}
	}
	
	public void registerKeyBinding()
	{
		keyBinding = new KeyBinding("keybinding." + Reference.GROUP_ID + "." + description.toLowerCase(), defaultKeyCode, "itemGroup." + Reference.MOD_ID);
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