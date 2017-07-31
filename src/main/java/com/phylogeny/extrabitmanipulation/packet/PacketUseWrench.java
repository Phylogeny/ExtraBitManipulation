package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.item.ItemBitWrench;

public class PacketUseWrench extends PacketBlockInteraction implements IMessage
{
	private boolean bitRequirement, invertDirection;
	
	public PacketUseWrench() {}
	
	public PacketUseWrench(BlockPos pos, EnumFacing side, boolean bitRequirement, boolean invertDirection)
	{
		super(pos, side, new Vec3d(0, 0, 0));
		this.bitRequirement = bitRequirement;
		this.invertDirection = invertDirection;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		buffer.writeBoolean(bitRequirement);
		buffer.writeBoolean(invertDirection);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		bitRequirement = buffer.readBoolean();
		invertDirection = buffer.readBoolean();
	}
	
	public static class Handler implements IMessageHandler<PacketUseWrench, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketUseWrench message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable()
			{
				@SuppressWarnings("null")
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					ItemStack stack = player.getHeldItemMainhand();
					if (ItemStackHelper.isBitWrenchStack(stack))
						((ItemBitWrench) stack.getItem()).useWrench(stack, player, player.worldObj, message.pos,
								message.side, message.bitRequirement, message.invertDirection);
				}
			});
			return null;
		}
		
	}
	
}