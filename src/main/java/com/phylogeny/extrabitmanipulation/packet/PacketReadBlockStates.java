package com.phylogeny.extrabitmanipulation.packet;

import com.phylogeny.extrabitmanipulation.helper.BitAreaHelper;
import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper.ModelingData;
import com.phylogeny.extrabitmanipulation.item.ItemModelingTool;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketReadBlockStates extends PacketBlockInteraction implements IMessage
{
	private Vec3i drawnStartPoint;
	private ModelingData modelingData = new ModelingData();
	
	public PacketReadBlockStates() {}
	
	public PacketReadBlockStates(BlockPos pos, Vec3 hit, Vec3i drawnStartPoint, ModelingData modelingData)
	{
		super(pos, EnumFacing.UP, hit);
		this.drawnStartPoint = drawnStartPoint;
		this.modelingData = modelingData;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		boolean notNull = drawnStartPoint != null;
		buffer.writeBoolean(notNull);
		if (notNull)
		{
			buffer.writeInt(drawnStartPoint.getX());
			buffer.writeInt(drawnStartPoint.getY());
			buffer.writeInt(drawnStartPoint.getZ());
		}
		modelingData.toBytes(buffer);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		if (buffer.readBoolean())
			drawnStartPoint = new Vec3i(buffer.readInt(), buffer.readInt(), buffer.readInt());
		
		modelingData.fromBytes(buffer);
	}
	
	public static class Handler implements IMessageHandler<PacketReadBlockStates, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketReadBlockStates message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					ItemStack stack = player.getCurrentEquippedItem();
					if (stack != null && stack.getItem() instanceof ItemModelingTool && (!player.isSneaking() || message.drawnStartPoint != null))
						BitAreaHelper.readBlockStates(stack, player, player.worldObj, message.getPos(),
								message.getHit(), message.drawnStartPoint, message.modelingData);
				}
			});
			return null;
		}
		
	}
	
}