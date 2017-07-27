package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor;

public class PacketSetCollectionBox extends PacketBlockInteraction
{
	private float playerYaw;
	private boolean useBitGrid;
	private EnumFacing facingBox;
	
	public PacketSetCollectionBox() {}
	
	public PacketSetCollectionBox(float playerYaw, boolean useBitGrid, EnumFacing facingBox, BlockPos pos, EnumFacing facingPlacement, Vec3d hit)
	{
		super(pos, facingPlacement, hit);
		this.playerYaw = playerYaw;
		this.useBitGrid = useBitGrid;
		this.facingBox = facingBox;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		buffer.writeFloat(playerYaw);
		buffer.writeBoolean(useBitGrid);
		buffer.writeInt(facingBox.ordinal());
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		playerYaw = buffer.readFloat();
		useBitGrid = buffer.readBoolean();
		facingBox = EnumFacing.getFront(buffer.readInt());
	}
	
	public static class Handler implements IMessageHandler<PacketSetCollectionBox, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSetCollectionBox message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().player;
					ItemStack stack = player.getHeldItemMainhand();
					if (ItemStackHelper.isChiseledArmorStack(stack))
					{
						NBTTagCompound nbt = ItemStackHelper.getNBTOrNew(stack);
						ItemChiseledArmor.writeCollectionBoxToNBT(nbt, message.playerYaw, message.useBitGrid,
								message.facingBox, message.pos, message.side, message.hit);
						stack.setTagCompound(nbt);
						player.inventoryContainer.detectAndSendChanges();
					}
				}
			});
			return null;
		}
		
	}
	
}