package com.phylogeny.extrabitmanipulation.init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPC64x32;

import com.phylogeny.extrabitmanipulation.armor.ChiseledArmorStackHandeler;
import com.phylogeny.extrabitmanipulation.armor.LayerChiseledArmor;
import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.reference.CustomNPCsReferences;

public class RenderLayersExtraBitManipulation
{
	private static List<LayerChiseledArmor> armorLayers = new ArrayList<LayerChiseledArmor>();
	private static boolean layersInitializedPlayerCNPC;
	
	public static void initLayers()
	{
		addLayerChiseledArmorToEntityRender(EntityArmorStand.class);
		addLayerChiseledArmorToEntityRender(EntityVillager.class);
		addLayerChiseledArmorToEntityRender(EntityZombieVillager.class);
		addLayerChiseledArmorToEntityRender(EntityZombie.class);
		addLayerChiseledArmorToEntityRender(EntityGiantZombie.class);
		addLayerChiseledArmorToEntityRender(EntityPigZombie.class);
		addLayerChiseledArmorToEntityRender(EntitySkeleton.class);
		addLayerChiseledArmorToEntityRender(EntityWitherSkeleton.class);
		addLayerChiseledArmorToEntityRender(EntityHusk.class);
		addLayerChiseledArmorToEntityRender(EntityStray.class);
		addLayerChiseledArmorToEntityRender(EntityVex.class);
		addLayerChiseledArmorToEntityRender(EntityVindicator.class);
		addLayerChiseledArmorToEntityRender(EntityEvoker.class);
		for (RenderPlayer renderPlayer : ClientHelper.getRenderManager().getSkinMap().values())
		{
			LayerChiseledArmor layer = new LayerChiseledArmor(renderPlayer);
			renderPlayer.addLayer(layer);
			armorLayers.add(layer);
		}
		if (Loader.isModLoaded(CustomNPCsReferences.MOD_ID))
			MinecraftForge.EVENT_BUS.register(new RenderLayersExtraBitManipulation());
	}
	
	@SubscribeEvent
	public void initLayersCNPCs(@SuppressWarnings("unused") RenderLivingEvent.Pre<EntityCustomNpc> event)
	{
		if (layersInitializedPlayerCNPC)
			return;
		
		addLayerChiseledArmorToEntityRender(EntityCustomNpc.class);
		addLayerChiseledArmorToEntityRender(EntityNPC64x32.class);
		layersInitializedPlayerCNPC = true;
	}
	
	private static <T extends EntityLivingBase> void addLayerChiseledArmorToEntityRender(Class <? extends Entity > entityClass)
	{
		Render<T> renderer = ClientHelper.getRenderManager().getEntityClassRenderObject(entityClass);
		LayerChiseledArmor layer = new LayerChiseledArmor((RenderLivingBase<T>) renderer);
		((RenderLivingBase<T>) renderer).addLayer(layer);
		armorLayers.add(layer);
	}
	
	public static void clearRenderMaps()
	{
		ChiseledArmorStackHandeler.clearModelMap();
		for (LayerChiseledArmor layer : armorLayers)
			layer.clearDisplayListsMap();
	}
	
	public static void removeFromRenderMaps(NBTTagCompound nbt)
	{
		ChiseledArmorStackHandeler.removeFromModelMap(nbt);
		for (LayerChiseledArmor layer : armorLayers)
			layer.removeFromDisplayListsMap(nbt);
	}
	
}