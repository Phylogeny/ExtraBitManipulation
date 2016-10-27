package com.phylogeny.extrabitmanipulation.packet;

import com.phylogeny.extrabitmanipulation.config.ConfigReplacementBits;
import com.phylogeny.extrabitmanipulation.item.ItemModelingTool;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCreateModel extends PacketBlockInteraction implements IMessage
{
	private ConfigReplacementBits replacementBitsUnchiselable = new ConfigReplacementBits();
	private ConfigReplacementBits replacementBitsInsufficient = new ConfigReplacementBits();
	
	public PacketCreateModel() {}
	
	public PacketCreateModel(BlockPos pos, EnumFacing side, ConfigReplacementBits replacementBitsUnchiselable,
			ConfigReplacementBits replacementBitsInsufficient)
	{
		super(pos, side, new Vec3d(0, 0, 0));
		this.replacementBitsUnchiselable = replacementBitsUnchiselable;
		this.replacementBitsInsufficient = replacementBitsInsufficient;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		replacementBitsUnchiselable.toBytes(buffer);
		replacementBitsInsufficient.toBytes(buffer);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		replacementBitsUnchiselable.fromBytes(buffer);
		replacementBitsInsufficient.fromBytes(buffer);
	}
	
	public static class Handler implements IMessageHandler<PacketCreateModel, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketCreateModel message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					ItemStack stack = player.getHeldItemMainhand();
					if (stack != null && stack.getItem() instanceof ItemModelingTool)
						((ItemModelingTool) stack.getItem()).createModel(stack, player, player.worldObj, message.getPos(),
								message.getSide(), message.replacementBitsUnchiselable, message.replacementBitsInsufficient);
				}
			});
			return null;
		}
		
	}
	
}