package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;
import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.IBitLocation;
import mod.chiselsandbits.api.APIExceptions.CannotBeChiseled;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.item.ItemSculptingLoop;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

public class PacketSculpt implements IMessage
{
	private BlockPos pos;
	private EnumFacing side;
	private Vec3 hit;
	
	public PacketSculpt() {}
	
	public PacketSculpt(BlockPos pos, EnumFacing side, Vec3 hit)
	{
		this.pos = pos;
		this.side = side;
		this.hit = hit;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(pos.getX());
		buffer.writeInt(pos.getY());
		buffer.writeInt(pos.getZ());
		buffer.writeInt(side.ordinal());
		buffer.writeDouble(hit.xCoord);
		buffer.writeDouble(hit.yCoord);
		buffer.writeDouble(hit.zCoord);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		pos = new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
		side = EnumFacing.getFront(buffer.readInt());
		hit = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
	}
	
	public static class Handler implements IMessageHandler<PacketSculpt, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketSculpt message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					ItemStack stack = player.getCurrentEquippedItem();
					if (stack != null && stack.getItem() instanceof ItemSculptingLoop)
					{
						ItemSculptingLoop toolItem = (ItemSculptingLoop) stack.getItem();
						if (!player.isSneaking() || toolItem.removeBits())
						{
							toolItem.sculptBlocks(stack, player, player.worldObj, message.pos, message.side, message.hit);
						}
						else
						{
							IChiselAndBitsAPI api = ChiselsAndBitsAPIAccess.apiInstance;
							IBitLocation bitLoc = api.getBitPos((float) message.hit.xCoord - message.pos.getX(), (float) message.hit.yCoord - message.pos.getY(),
									(float) message.hit.zCoord - message.pos.getZ(), message.side, message.pos, false);
							if (bitLoc != null)
							{
								try
								{
									IBitAccess bitAccess = api.getBitAccess(player.worldObj, message.pos);
									toolItem.initialize(stack);
									ItemStack bitStack = bitAccess.getBitAt(bitLoc.getBitX(), bitLoc.getBitY(), bitLoc.getBitZ()).getItemStack(1);
									NBTTagCompound nbt = new NBTTagCompound();
									bitStack.writeToNBT(nbt);
									stack.getTagCompound().setTag(NBTKeys.PAINT_BIT, nbt);
									player.inventoryContainer.detectAndSendChanges();
								}
								catch (CannotBeChiseled e) {}
							}
						}
					}
				}
			});
			return null;
		}
		
	}
	
}