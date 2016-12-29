package com.phylogeny.extrabitmanipulation.reference;

public class Reference
{
	public static final String MOD_ID = "ExtraBitManipulation";
	public static final String GROUP_ID = "extrabitmanipulation";
	public static final String MOD_NAME = "Extra Bit Manipulation";
	public static final String MOD_PATH = "com.phylogeny." + GROUP_ID;
	public static final String VERSION = "@VERSION@";
	public static final String UPDATE_JSON = "@UPDATE@";
	public static final String DEPENDENCIES = "required-after:" + ChiselsAndBitsReferences.MOD_ID + "@[8.0,)";
	public static final String CLIENT_CLASSPATH = MOD_PATH + ".proxy.ProxyClient";
	public static final String COMMON_CLASSPATH = MOD_PATH + ".proxy.ProxyCommon";
	public static final String GUI_FACTORY_CLASSPATH = MOD_PATH + ".client.config.GuiFactoryExtraBitManipulation";
	
}