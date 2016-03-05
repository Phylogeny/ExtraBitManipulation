package com.phylogeny.extrabitmanipulation.extendedproperties;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.packet.PacketSculpt;
import com.phylogeny.extrabitmanipulation.packet.PacketSyncAllSculptingData;
import com.phylogeny.extrabitmanipulation.packet.PacketSyncRotation;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SculptSettingsPlayerProperties implements IExtendedEntityProperties
{
	private static final String ID = "SculptSettingsPlayerProperties";
	private int rotation;
	
	public int getRotation()
	{
//		rotation = 5;//TODO
		return rotation;
	}
	
	public void setRotation(int rotation, boolean syncServer)
	{
		this.rotation = rotation;
		if (syncServer) syncData(new PacketSyncRotation(rotation));
	}

	private void syncData(IMessage packet)
	{
		ExtraBitManipulation.packetNetwork.sendToServer(packet);
	}
	
	public void syncAllData(EntityPlayerMP player)
	{
		ExtraBitManipulation.packetNetwork.sendTo(new PacketSyncAllSculptingData(rotation), player);
	}
	
	@Override
	public void saveNBTData(NBTTagCompound compound)
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("rotation", rotation);
		compound.setTag(ID, nbt);
	}
	
	@Override
	public void loadNBTData(NBTTagCompound compound)
	{
		NBTTagCompound nbt = (NBTTagCompound) compound.getTag(ID);
		rotation = nbt.getInteger("rotation");
	}
	
	@Override
	public void init(Entity entity, World world)
	{
		rotation = 1;
	}
	
	public static SculptSettingsPlayerProperties get(Entity entity)
	{
		return (SculptSettingsPlayerProperties) entity.getExtendedProperties(ID);
	}
	
	public static void register(Entity entity)
	{
		entity.registerExtendedProperties(ID, new SculptSettingsPlayerProperties());
	}
	
}