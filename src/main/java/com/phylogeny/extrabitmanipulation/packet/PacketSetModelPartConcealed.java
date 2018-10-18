package com.phylogeny.extrabitmanipulation.packet;

import javax.annotation.Nullable;

import com.phylogeny.extrabitmanipulation.armor.ModelPartConcealer;
import com.phylogeny.extrabitmanipulation.armor.capability.ChiseledArmorSlotsHandler;
import com.phylogeny.extrabitmanipulation.armor.capability.IChiseledArmorSlotsHandler;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ArmorType;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ModelMovingPart;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSetModelPartConcealed extends PacketArmorSlotInt
{
	private boolean isOverlay, remove;
	
	public PacketSetModelPartConcealed() {}
	
	public PacketSetModelPartConcealed(@Nullable ArmorType armorType, int indexArmorSet, ModelMovingPart part, boolean isOverlay, boolean remove)
	{
		super(armorType, indexArmorSet, part.ordinal());
		this.isOverlay = isOverlay;
		this.remove = remove;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		buffer.writeBoolean(isOverlay);
		buffer.writeBoolean(remove);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		isOverlay = buffer.readBoolean();
		remove = buffer.readBoolean();
	}
	
	public static class Handler implements IMessageHandler<PacketSetModelPartConcealed, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSetModelPartConcealed message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().player;
					ItemStack stack = getArmorStack(player, message);
					if (stack.isEmpty())
						return;
					
					NBTTagCompound nbt = stack.getTagCompound();
					if (nbt == null)
						return;
					
					ModelPartConcealer modelPartConcealer = ModelPartConcealer.loadFromNBT(nbt);
					if (modelPartConcealer == null)
						modelPartConcealer = new ModelPartConcealer();
					
					modelPartConcealer.addOrRemove(message.value, message.isOverlay, message.remove);
					modelPartConcealer.saveToNBT(nbt);
					player.inventoryContainer.detectAndSendChanges();
					IChiseledArmorSlotsHandler cap = ChiseledArmorSlotsHandler.getCapability(player);
					if (cap != null)
						cap.onContentsChanged(message.armorType.getSlotIndex(message.indexArmorSet));
				}
			});
			return null;
		}
		
	}
	
}