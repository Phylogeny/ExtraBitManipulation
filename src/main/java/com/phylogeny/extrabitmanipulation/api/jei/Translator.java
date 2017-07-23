package com.phylogeny.extrabitmanipulation.api.jei;

import java.util.IllegalFormatException;

import net.minecraft.util.text.translation.I18n;

import com.phylogeny.extrabitmanipulation.helper.LogHelper;

/**
 * This class is taken by permission from JEI.
 *
 * @author mezz
 */
@SuppressWarnings("deprecation")
public final class Translator
{
	private Translator() {}
	
	public static String translateToLocal(String key)
	{
		if (I18n.canTranslate(key))
			return I18n.translateToLocal(key);
		
		return I18n.translateToFallback(key);
	}
	
	public static String translateToLocalFormatted(String key, Object... format)
	{
		String s = translateToLocal(key);
		try
		{
			return String.format(s, format);
		}
		catch (IllegalFormatException e)
		{
			LogHelper.getLogger().error("Format error: {}", s, e);
			return "Format error: " + s;
		}
	}
	
}