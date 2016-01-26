package com.phylogeny.extrabitmanipulation.packet;

import com.phylogeny.extrabitmanipulation.item.ItemBitWrench;
import com.phylogeny.extrabitmanipulation.item.ItemSculptingLoop;
import com.phylogeny.extrabitmanipulation.reference.Configs;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCycleData implements IMessage
{
	private boolean forward;
	
	public PacketCycleData() {}
	
	public PacketCycleData(boolean forward)
	{
		this.forward = forward;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeBoolean(forward);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		forward = buffer.readBoolean();
	}
	
	public static class Handler implements IMessageHandler<PacketCycleData, IMessage>
	{
		@Override
		public IMessage onMessage(PacketCycleData message, MessageContext ctx)
		{
			EntityPlayer player = ctx.getServerHandler().playerEntity;
			ItemStack stack = player.getCurrentEquippedItem();
			boolean isWrench = stack.getItem() instanceof ItemBitWrench;
			if (stack != null && (isWrench || stack.getItem() instanceof ItemSculptingLoop))
			{
				if (isWrench)
				{
					((ItemBitWrench) stack.getItem()).cycleModes(stack, message.forward);
				}
				else
				{
					NBTTagCompound nbt = stack.getTagCompound();
					if (!nbt.hasKey("sculptRadius"))
					{
						nbt.setInteger("sculptRadius", Configs.DEFAULT_REMOVAL_RADIUS);
					}
					((ItemSculptingLoop) stack.getItem()).cycleData(stack, "sculptRadius", message.forward, Configs.MAX_REMOVAL_RADIUS);
				}
			}
			return null;
		}
		
	}
	
}