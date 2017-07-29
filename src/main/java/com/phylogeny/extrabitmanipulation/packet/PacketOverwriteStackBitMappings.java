package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;

import java.util.Map;

import mod.chiselsandbits.api.IBitBrush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.phylogeny.extrabitmanipulation.helper.BitIOHelper;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;

public class PacketOverwriteStackBitMappings extends PacketBitMapIO
{
	private Map<IBlockState, IBitBrush> bitMap;
	
	public PacketOverwriteStackBitMappings() {}
	
	public PacketOverwriteStackBitMappings(Map<IBlockState, IBitBrush> bitMap, String nbtKey, boolean saveStatesById)
	{
		super(nbtKey, saveStatesById);
		this.bitMap = bitMap;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		BitIOHelper.stateToBitMapToBytes(buffer, bitMap);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		bitMap = BitIOHelper.stateToBitMapFromBytes(buffer);
	}
	
	public static class Handler implements IMessageHandler<PacketOverwriteStackBitMappings, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketOverwriteStackBitMappings message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.world;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					ItemStack stack = player.getHeldItemMainhand();
					if (ItemStackHelper.isModelingToolStack(stack))
					{
						BitIOHelper.writeStateToBitMapToNBT(stack, message.nbtKey, message.bitMap, message.saveStatesById);
						player.inventoryContainer.detectAndSendChanges();
					}
				}
			});
			return null;
		}
		
	}
	
}