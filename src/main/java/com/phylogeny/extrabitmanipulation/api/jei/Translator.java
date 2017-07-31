package com.phylogeny.extrabitmanipulation.api.jei;

import java.util.IllegalFormatException;

import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.FMLLog;

import org.apache.logging.log4j.Level;

import com.phylogeny.extrabitmanipulation.reference.Reference;

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
			String errorMessage = "Format error: " + s;
			FMLLog.log(Reference.MOD_NAME, Level.ERROR, errorMessage, e);
			return errorMessage;
		}
	}
	
}