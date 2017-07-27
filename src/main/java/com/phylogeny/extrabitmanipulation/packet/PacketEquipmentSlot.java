package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;

import javax.annotation.Nullable;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketEquipmentSlot implements IMessage
{
	protected EntityEquipmentSlot equipmentSlot;
	
	public PacketEquipmentSlot() {}
	
	public PacketEquipmentSlot(@Nullable EntityEquipmentSlot equipmentSlot)
	{
		this.equipmentSlot = equipmentSlot;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		boolean notNull = equipmentSlot != null;
		buffer.writeBoolean(notNull);
		if (notNull)
			buffer.writeInt(equipmentSlot.ordinal());
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		if (buffer.readBoolean())
			equipmentSlot = EntityEquipmentSlot.values()[buffer.readInt()];
	}
	
}