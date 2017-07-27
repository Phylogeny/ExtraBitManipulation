package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper.ArmorBodyPartTemplateData;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor;

public class PacketCreateBodyPartTemplate extends PacketBlockInteraction implements IMessage
{
	private ArmorBodyPartTemplateData templateData = new ArmorBodyPartTemplateData();
	
	public PacketCreateBodyPartTemplate() {}
	
	public PacketCreateBodyPartTemplate(BlockPos pos, EnumFacing side, Vec3d hit, ArmorBodyPartTemplateData templateData)
	{
		super(pos, side, hit);
		this.templateData = templateData;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		templateData.toBytes(buffer);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		templateData.fromBytes(buffer);
	}
	
	public static class Handler implements IMessageHandler<PacketCreateBodyPartTemplate, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketCreateBodyPartTemplate message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					ItemStack stack = player.getHeldItemMainhand();
					if (ItemStackHelper.isChiseledArmorStack(stack))
						ItemChiseledArmor.createBodyPartTemplate(player, player.worldObj, message.pos, message.side, message.hit, message.templateData);
				}
			});
			return null;
		}
		
	}
	
}