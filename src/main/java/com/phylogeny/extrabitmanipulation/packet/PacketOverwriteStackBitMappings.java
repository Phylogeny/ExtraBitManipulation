package com.phylogeny.extrabitmanipulation.packet;

import java.util.Map;

import com.phylogeny.extrabitmanipulation.helper.BitIOHelper;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

import io.netty.buffer.ByteBuf;
import mod.chiselsandbits.api.IBitBrush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOverwriteStackBitMappings implements IMessage
{
	private Map<IBlockState, IBitBrush> stateToBitMap, blockToBitMap;
	private boolean saveStatesById;
	
	public PacketOverwriteStackBitMappings() {}
	
	public PacketOverwriteStackBitMappings(Map<IBlockState, IBitBrush> stateToBitMap, Map<IBlockState, IBitBrush> blockToBitMap, boolean saveStatesById)
	{
		this.stateToBitMap = stateToBitMap;
		this.blockToBitMap = blockToBitMap;
		this.saveStatesById = saveStatesById;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		BitIOHelper.stateToBitMapToBytes(buffer, stateToBitMap);
		BitIOHelper.stateToBitMapToBytes(buffer, blockToBitMap);
		buffer.writeBoolean(saveStatesById);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		stateToBitMap = BitIOHelper.stateToBitMapFromBytes(buffer);
		blockToBitMap = BitIOHelper.stateToBitMapFromBytes(buffer);
		saveStatesById = buffer.readBoolean();
	}
	
	public static class Handler implements IMessageHandler<PacketOverwriteStackBitMappings, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketOverwriteStackBitMappings message, final MessageContext ctx)
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
					{
						BitIOHelper.writeStateToBitMapToNBT(stack, NBTKeys.STATE_TO_BIT_MAP_PERMANENT, message.stateToBitMap, message.saveStatesById);
						BitIOHelper.writeStateToBitMapToNBT(stack, NBTKeys.BLOCK_TO_BIT_MAP_PERMANENT, message.blockToBitMap, message.saveStatesById);
						player.inventoryContainer.detectAndSendChanges();
					}
				}
			});
			return null;
		}
		
	}
	
}