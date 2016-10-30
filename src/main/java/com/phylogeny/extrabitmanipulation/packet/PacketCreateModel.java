package com.phylogeny.extrabitmanipulation.packet;

import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper.ModelWriteData;
import com.phylogeny.extrabitmanipulation.item.ItemModelingTool;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCreateModel extends PacketBlockInteraction implements IMessage
{
	private ModelWriteData modelingData = new ModelWriteData();
	
	public PacketCreateModel() {}
	
	public PacketCreateModel(BlockPos pos, EnumFacing side, ModelWriteData modelingData)
	{
		super(pos, side, new Vec3(0, 0, 0));
		this.modelingData = modelingData;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		modelingData.toBytes(buffer);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		modelingData.fromBytes(buffer);
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
					ItemStack stack = player.getCurrentEquippedItem();
					if (ItemStackHelper.isModelingToolStack(stack))
						((ItemModelingTool) stack.getItem()).createModel(stack, player, player.worldObj,
								message.getPos(), message.getSide(), message.modelingData);
				}
			});
			return null;
		}
		
	}
	
}