package com.phylogeny.extrabitmanipulation.packet;

import javax.annotation.Nullable;

import com.phylogeny.extrabitmanipulation.helper.BitIOHelper;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ArmorType;

import io.netty.buffer.ByteBuf;

public abstract class PacketArmorSlot extends PacketArmorSet
{
	protected ArmorType armorType;
	
	public PacketArmorSlot() {}
	
	public PacketArmorSlot(@Nullable ArmorType armorType, int indexArmorSet)
	{
		super(indexArmorSet);
		this.armorType = armorType;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		if (BitIOHelper.notNullToBuffer(buffer, armorType))
			buffer.writeInt(armorType.ordinal());
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		if (buffer.readBoolean())
			armorType = ArmorType.values()[buffer.readInt()];
	}
	
}