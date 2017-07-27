package com.phylogeny.extrabitmanipulation.packet;

import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;

import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import mod.chiselsandbits.api.IBitBrush;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketSetBitStack implements IMessage
{
	private boolean isWire;
	private ItemStack bitStack;
	
	public PacketSetBitStack() {}
	
	public PacketSetBitStack(boolean isCurved, IBitBrush bit)
	{
		this.isWire = isCurved;
		this.bitStack = bit == null ? ItemStack.EMPTY : bit.getItemStack(1);
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeBoolean(isWire);
		ByteBufUtils.writeItemStack(buffer, bitStack);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		isWire = buffer.readBoolean();
		bitStack = ByteBufUtils.readItemStack(buffer);
	}
	
	public static class Handler implements IMessageHandler<PacketSetBitStack, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSetBitStack message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().player;
					try
					{
						BitToolSettingsHelper.setBitStack(player, player.getHeldItemMainhand(), message.isWire,
								ChiselsAndBitsAPIAccess.apiInstance.createBrush(message.bitStack), null);
					}
					catch (InvalidBitItem e) {}
				}
			});
			return null;
		}
		
	}
	
}