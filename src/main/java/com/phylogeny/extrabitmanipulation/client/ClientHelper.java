package com.phylogeny.extrabitmanipulation.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ClientHelper
{
	
	private static Minecraft getMinecraft()
	{
		return Minecraft.getMinecraft();
	}
	
	public static IThreadListener getThreadListener()
	{
		return getMinecraft();
	}
	
	public static World getWorld()
	{
		return getMinecraft().theWorld;
	}
	
	public static EntityPlayer getPlayer()
	{
		return getMinecraft().thePlayer;
	}
	
	public static ItemStack getHeldItemMainhand()
	{
		return getPlayer().getHeldItemMainhand();
	}
	
	public static RayTraceResult getObjectMouseOver()
	{
		return getMinecraft().objectMouseOver;
	}
	
	public static void spawnParticle(World worldIn, Vec3d particlePos, IParticleFactory particleFactory)
	{
		getMinecraft().effectRenderer.addEffect(particleFactory.getEntityFX(0, worldIn,
				particlePos.xCoord, particlePos.yCoord, particlePos.zCoord, 0, 0, 0));
	}
	
	public static void printChatMessageWithDeletion(String text)
	{
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(text), 627250);
	}
	
	public static void bindTexture(ResourceLocation resource)
	{
		getMinecraft().getTextureManager().bindTexture(resource);
	}
	
	public static RenderItem getRenderItem()
	{
		return getMinecraft().getRenderItem();
	}
	
	public static RenderManager getRenderManager()
	{
		return getMinecraft().getRenderManager();
	}
	
	public static BlockModelShapes getBlockModelShapes()
	{
		return getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
	}
	
	public static ItemColors getItemColors()
	{
		return getMinecraft().getItemColors();
	}
	
}