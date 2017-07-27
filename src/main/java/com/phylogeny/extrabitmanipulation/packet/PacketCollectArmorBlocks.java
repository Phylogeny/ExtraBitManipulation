package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper.ArmorCollectionData;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor;

public class PacketCollectArmorBlocks implements IMessage
{
	private ArmorCollectionData collectionData = new ArmorCollectionData();
	
	public PacketCollectArmorBlocks() {}
	
	public PacketCollectArmorBlocks(ArmorCollectionData collectionData)
	{
		this.collectionData = collectionData;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		collectionData.toBytes(buffer);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		collectionData.fromBytes(buffer);
	}
	
	public static class Handler implements IMessageHandler<PacketCollectArmorBlocks, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketCollectArmorBlocks message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.world;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					ItemStack stack = player.getHeldItemMainhand();
					if (ItemStackHelper.isChiseledArmorStack(stack))
						ItemChiseledArmor.collectArmorBlocks(player, message.collectionData);
				}
			});
			return null;
		}
		
	}
	
}