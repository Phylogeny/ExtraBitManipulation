package com.phylogeny.extrabitmanipulation.packet;

import com.phylogeny.extrabitmanipulation.item.ItemBitWrench;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCycleWrench implements IMessage
{
	
	@Override
	public void fromBytes(ByteBuf buffer) {}

	@Override
	public void toBytes(ByteBuf buffer) {}
	
	public static class Handler implements IMessageHandler<PacketCycleWrench, IMessage>
	{
		@Override
		public IMessage onMessage(PacketCycleWrench message, MessageContext ctx)
		{
			EntityPlayer player = ctx.getServerHandler().playerEntity;
			ItemStack stack = player.getCurrentEquippedItem();
			String name = stack.getDisplayName();
			boolean setTag = !stack.hasTagCompound();
			if (setTag)
			{
				stack.setTagCompound(new NBTTagCompound());
			}
			NBTTagCompound nbt = stack.getTagCompound();
			int mode = (nbt.getInteger("mode") + 1) % 3;
			nbt.setInteger("mode", mode);
			if (setTag)
			{
				ItemBitWrench.setWrenchDisplayName(stack, 1);
			}
			else
			{
				ItemBitWrench.setWrenchDisplayName(stack, name.substring(0, name.indexOf(" - ")), mode);
			}
			return null;
		}
		
	}
	
}