package com.phylogeny.extrabitmanipulation.packet;

import java.util.HashMap;

import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.helper.BitIOHelper;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.item.ItemModelingTool;

import io.netty.buffer.ByteBuf;
import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import mod.chiselsandbits.api.IBitBrush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketModelingTool implements IMessage
{
	private IBlockState state;
	private IBitBrush bit;
	private String nbtKey;
	protected boolean saveStatesById;
	
	public PacketModelingTool() {}
	
	public PacketModelingTool(String nbtKey, IBlockState state, IBitBrush bit, boolean saveStatesById)
	{
		this.nbtKey = nbtKey;
		this.state = state;
		this.bit = bit;
		this.saveStatesById = saveStatesById;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeUTF8String(buffer, nbtKey);
		BitIOHelper.stateToBytes(buffer, state);
		buffer.writeBoolean(saveStatesById);
		boolean removeMapping = bit == null;
		buffer.writeBoolean(removeMapping);
		if (!removeMapping)
			ItemStackHelper.stackToBytes(buffer, bit.getItemStack(1));
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		nbtKey = ByteBufUtils.readUTF8String(buffer);
		state = BitIOHelper.stateFromBytes(buffer);
		saveStatesById = buffer.readBoolean();
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
	
	public static class Handler implements IMessageHandler<PacketModelingTool, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketModelingTool message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					ItemStack itemStack = player.inventory.getCurrentItem();
					if (itemStack != null && itemStack.getItem() != null && itemStack.getItem() instanceof ItemModelingTool)
					{
						HashMap<IBlockState, IBitBrush> bitMapPermanent = BitIOHelper.readStateToBitMapFromNBT(ChiselsAndBitsAPIAccess.apiInstance,
								itemStack, message.nbtKey);
						if (message.bit != null)
						{
							bitMapPermanent.put(message.state, message.bit);
						}
						else
						{
							bitMapPermanent.remove(message.state);
						}
						BitIOHelper.writeStateToBitMapToNBT(itemStack, message.nbtKey, bitMapPermanent, message.saveStatesById);
						player.inventoryContainer.detectAndSendChanges();
					}
				}
			});
			return null;
		}
		
	}
	
}