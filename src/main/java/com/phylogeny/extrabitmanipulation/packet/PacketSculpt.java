package com.phylogeny.extrabitmanipulation.packet;

import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper.SculptingData;
import com.phylogeny.extrabitmanipulation.item.ItemSculptingTool;

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

public class PacketSculpt extends PacketBlockInteraction implements IMessage
{
	private Vec3d drawnStartPoint;
	private SculptingData sculptingData = new SculptingData();
	
	public PacketSculpt() {}
	
	public PacketSculpt(BlockPos pos, EnumFacing side, Vec3d hit, Vec3d drawnStartPoint, SculptingData sculptingData)
	{
		super(pos, side, hit);
		this.drawnStartPoint = drawnStartPoint;
		this.sculptingData = sculptingData;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		boolean notNull = drawnStartPoint != null;
		buffer.writeBoolean(notNull);
		if (notNull)
		{
			buffer.writeDouble(drawnStartPoint.xCoord);
			buffer.writeDouble(drawnStartPoint.yCoord);
			buffer.writeDouble(drawnStartPoint.zCoord);
		}
		sculptingData.toBytes(buffer);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		if (buffer.readBoolean())
			drawnStartPoint = new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
		
		sculptingData.fromBytes(buffer);
	}
	
	public static class Handler implements IMessageHandler<PacketSculpt, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSculpt message, final MessageContext ctx)
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
					if (ItemStackHelper.isSculptingToolStack(stack) && (!player.isSneaking() || message.drawnStartPoint != null))
						((ItemSculptingTool) stack.getItem()).sculptBlocks(stack, player, player.worldObj, message.getPos(),
								message.getSide(), message.getHit(), message.drawnStartPoint, message.sculptingData);
				}
			});
			return null;
		}
		
	}
	
}