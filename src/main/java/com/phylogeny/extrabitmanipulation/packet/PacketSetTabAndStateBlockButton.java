package com.phylogeny.extrabitmanipulation.packet;

import com.phylogeny.extrabitmanipulation.item.ItemModelMaker;
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

public class PacketSetTabAndStateBlockButton implements IMessage
{
	private int tabSelected;
	private boolean stateButtonSelected;

	public PacketSetTabAndStateBlockButton() {}
	
	public PacketSetTabAndStateBlockButton(int tabSelected, boolean stateButtonSelected)
	{
		this.tabSelected = tabSelected;
		this.stateButtonSelected = stateButtonSelected;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(tabSelected);
		buffer.writeBoolean(stateButtonSelected);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		tabSelected = buffer.readInt();
		stateButtonSelected = buffer.readBoolean();
	}
	
	public static class Handler implements IMessageHandler<PacketSetTabAndStateBlockButton, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSetTabAndStateBlockButton message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					ItemStack itemStack = player.inventory.getCurrentItem();
					if (itemStack != null && itemStack.getItem() != null && itemStack.getItem() instanceof ItemModelMaker)
					{
						if (!itemStack.hasTagCompound())
							itemStack.setTagCompound(new NBTTagCompound());
						
						NBTTagCompound nbt = itemStack.getTagCompound();
						nbt.setInteger(NBTKeys.TAB_SETTING, message.tabSelected);
						nbt.setBoolean(NBTKeys.BUTTON_STATE_BLOCK_SETTING, message.stateButtonSelected);
						player.inventory.markDirty();
					}
				}
			});
			return null;
		}
		
	}
	
}