package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.item.ItemBitWrench;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

public class PacketSetWrechMode implements IMessage
{
	private int mode;
	
	public PacketSetWrechMode() {}
	
	public PacketSetWrechMode(int mode)
	{
		this.mode = mode;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(mode);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		mode = buffer.readInt();
	}
	
	public static class Handler implements IMessageHandler<PacketSetWrechMode, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSetWrechMode message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().player;
					ItemStack stack = player.getHeldItemMainhand();
					if (ItemStackHelper.isBitWrenchStack(stack))
					{
						((ItemBitWrench) stack.getItem()).initialize(stack);
						ItemStackHelper.getNBT(stack).setInteger(NBTKeys.WRENCH_MODE, message.mode);
						player.inventoryContainer.detectAndSendChanges();
					}
				}
			});
			return null;
		}
		
	}
	
}