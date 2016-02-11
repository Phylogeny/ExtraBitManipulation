package com.phylogeny.extrabitmanipulation.packet;

import com.phylogeny.extrabitmanipulation.config.ConfigProperty;
import com.phylogeny.extrabitmanipulation.item.ItemBitToolBase;
import com.phylogeny.extrabitmanipulation.item.ItemBitWrench;
import com.phylogeny.extrabitmanipulation.item.ItemSculptingTool;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCycleData implements IMessage
{
	private boolean forward;
	
	public PacketCycleData() {}
	
	public PacketCycleData(boolean forward)
	{
		this.forward = forward;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeBoolean(forward);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		forward = buffer.readBoolean();
	}
	
	public static class Handler implements IMessageHandler<PacketCycleData, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketCycleData message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					ItemStack stack = player.getCurrentEquippedItem();
					if (stack != null && stack.getItem() instanceof ItemBitToolBase)
					{
						ItemBitToolBase itemTool = (ItemBitToolBase) stack.getItem();
						if (stack.getItem() instanceof ItemBitWrench)
						{
							itemTool.cycleModes(stack, message.forward);
						}
						else
						{
							ConfigProperty config = (ConfigProperty) Configs.itemPropertyMap.get(itemTool);
							itemTool.cycleData(stack, NBTKeys.SCULPT_SEMI_DIAMETER, message.forward, config.maxDamage);
						}
					}
				}
			});
			return null;
		}
		
	}
	
}