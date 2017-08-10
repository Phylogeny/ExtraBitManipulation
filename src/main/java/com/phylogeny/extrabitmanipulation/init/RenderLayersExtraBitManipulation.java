package com.phylogeny.extrabitmanipulation.init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;

import com.phylogeny.extrabitmanipulation.armor.ChiseledArmorStackHandeler;
import com.phylogeny.extrabitmanipulation.armor.LayerChiseledArmor;
import com.phylogeny.extrabitmanipulation.client.ClientHelper;

public class RenderLayersExtraBitManipulation
{
	private static List<LayerChiseledArmor> armorLayers = new ArrayList<LayerChiseledArmor>();
	
	public static void init()
	{
		addLayerChiseledArmorToEntityRender(EntityArmorStand.class);
		addLayerChiseledArmorToEntityRender(EntityVillager.class);
		addLayerChiseledArmorToEntityRender(EntityZombie.class);
		addLayerChiseledArmorToEntityRender(EntityGiantZombie.class);
		addLayerChiseledArmorToEntityRender(EntityPigZombie.class);
		addLayerChiseledArmorToEntityRender(EntitySkeleton.class);
		for (RenderPlayer renderPlayer : ClientHelper.getRenderManager().getSkinMap().values())
		{
			LayerChiseledArmor layer = new LayerChiseledArmor(renderPlayer);
			renderPlayer.addLayer(layer);
			armorLayers.add(layer);
		}
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