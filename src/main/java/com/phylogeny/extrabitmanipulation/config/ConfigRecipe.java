package com.phylogeny.extrabitmanipulation.config;

public class ConfigRecipe extends ConfigNamed
{
	public boolean isEnabled, isShaped, useOreDictionary;
	public String[] recipe;
	private boolean isEnabledDefault, isShapedDefault, useOreDictionaryDefault;
	private String[] recipeDefault;
	
	public ConfigRecipe(String itemTitle, boolean isEnabledDefault, boolean isShapedDefault,
			boolean useOreDictionaryDefault, String... recipeDefault)
	{
		super(itemTitle);
		this.isEnabledDefault = isEnabledDefault;
		this.isShapedDefault = isShapedDefault;
		this.useOreDictionaryDefault = useOreDictionaryDefault;
		this.recipeDefault = recipeDefault;
	}
	
	public boolean getIsEnabledDefault()
	{
		return isEnabledDefault;
	}
	
	public boolean getIsShapedDefault()
	{
		return isShapedDefault;
	}
	
	public boolean getUseOreDictionaryDefault()
	{
		return useOreDictionaryDefault;
	}
	
	public String[] getRecipeDefault()
	{
		return recipeDefault;
	}
	
}