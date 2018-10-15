package com.phylogeny.extrabitmanipulation.packet;

import javax.annotation.Nullable;

import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ArmorType;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PacketArmorSlotInt extends PacketArmorSlot
{
	protected int value;
	
	public PacketArmorSlotInt() {}
	
	public PacketArmorSlotInt(@Nullable ArmorType armorType, int indexArmorSet, int value)
	{
		super(armorType, indexArmorSet);
		this.value = value;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		buffer.writeInt(value);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		value = buffer.readInt();
	}
	
	protected static ItemStack getArmorStack(EntityPlayer player, PacketArmorSlotInt message)
	{
		ItemStack stack = ItemStackHelper.getChiseledArmorStack(player, message.armorType, message.indexArmorSet);
		return ItemStackHelper.isChiseledArmorStack(stack) ? stack : ItemStack.EMPTY;
	}
}
