package com.phylogeny.extrabitmanipulation.packet;

import javax.annotation.Nullable;

import com.phylogeny.extrabitmanipulation.helper.BitIOHelper;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ArmorType;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class PacketArmorSlotInt implements IMessage
{
	protected ArmorType armorType;
	protected int indexArmorSet;
	protected int value;
	
	public PacketArmorSlotInt() {}
	
	public PacketArmorSlotInt(@Nullable ArmorType armorType, int indexArmorSet, int value)
	{
		this.armorType = armorType;
		this.indexArmorSet = indexArmorSet;
		this.value = value;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(value);
		buffer.writeInt(indexArmorSet);
		if (BitIOHelper.notNullToBuffer(buffer, armorType))
			buffer.writeInt(armorType.ordinal());
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		value = buffer.readInt();
		indexArmorSet = buffer.readInt();
		if (buffer.readBoolean())
			armorType = ArmorType.values()[buffer.readInt()];
	}
	
	protected static ItemStack getArmorStack(EntityPlayer player, PacketArmorSlotInt message)
	{
		ItemStack stack = ItemStackHelper.getChiseledArmorStack(player, message.armorType, message.indexArmorSet);
		return ItemStackHelper.isChiseledArmorStack(stack) ? stack : ItemStack.EMPTY;
	}
}
