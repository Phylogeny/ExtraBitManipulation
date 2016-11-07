package com.phylogeny.extrabitmanipulation.packet;

import java.util.Map;

import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.helper.BitIOHelper;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;

import io.netty.buffer.ByteBuf;
import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import mod.chiselsandbits.api.IBitBrush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketAddBitMapping extends PacketBitMapIO
{
	private IBlockState state;
	private IBitBrush bit;
	
	public PacketAddBitMapping() {}
	
	public PacketAddBitMapping(String nbtKey, IBlockState state, IBitBrush bit, boolean saveStatesById)
	{
		super(nbtKey, saveStatesById);
		this.state = state;
		this.bit = bit;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		BitIOHelper.stateToBytes(buffer, state);
		boolean removeMapping = bit == null;
		buffer.writeBoolean(removeMapping);
		if (!removeMapping)
			ItemStackHelper.stackToBytes(buffer, bit.getItemStack(1));
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		state = BitIOHelper.stateFromBytes(buffer);
		if (buffer.readBoolean())
		{
			bit = null;
			return;
		}
		try
		{
			bit = ChiselsAndBitsAPIAccess.apiInstance.createBrush(ItemStackHelper.stackFromBytes(buffer));
		}
		catch (InvalidBitItem e)
		{
			bit = null;
		}
	}
	
	public static class Handler implements IMessageHandler<PacketAddBitMapping, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketAddBitMapping message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					ItemStack stack = player.getHeldItemMainhand();
					if (ItemStackHelper.isModelingToolStack(stack))
					{
						Map<IBlockState, IBitBrush> bitMapPermanent = BitIOHelper.readStateToBitMapFromNBT(ChiselsAndBitsAPIAccess.apiInstance,
								stack, message.nbtKey);
						if (message.bit != null)
						{
							bitMapPermanent.put(message.state, message.bit);
						}
						else
						{
							bitMapPermanent.remove(message.state);
						}
						BitIOHelper.writeStateToBitMapToNBT(stack, message.nbtKey, bitMapPermanent, message.saveStatesById);
						player.inventoryContainer.detectAndSendChanges();
					}
				}
			});
			return null;
		}
		
	}
	
}