package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

public class PacketChangeGlOperationList extends PacketChangeChiseledArmorList
{
	private String nbtKey;
	
	public PacketChangeGlOperationList() {}
	
	public PacketChangeGlOperationList(NBTTagCompound nbt, String nbtKey, EntityEquipmentSlot equipmentSlot,
			boolean mainArmor, int partIndex, int armorItemIndex, int selectedEntry, boolean refreshLists, EntityPlayer player)
	{
		super(nbt, equipmentSlot, mainArmor, partIndex, armorItemIndex, selectedEntry, refreshLists, player);
		this.nbtKey = nbtKey;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		ByteBufUtils.writeUTF8String(buffer, nbtKey);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		nbtKey = ByteBufUtils.readUTF8String(buffer);
	}
	
	public static class Handler implements IMessageHandler<PacketChangeGlOperationList, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketChangeGlOperationList message, final MessageContext ctx)
		{
			final boolean serverSide = ctx.side == Side.SERVER;
			IThreadListener mainThread = serverSide ? (WorldServer) ctx.getServerHandler().playerEntity.world : ClientHelper.getThreadListener();
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = serverSide ? ctx.getServerHandler().playerEntity : ClientHelper.getPlayer();
					ItemStack stack = ItemStackHelper.getChiseledArmorStack(player, message.equipmentSlot, message.mainArmor);
					if (!ItemStackHelper.isChiseledArmorStack(stack))
						return;
					
					message.initData(message, stack);
					NBTTagCompound nbt = ItemStackHelper.getNBT(stack);
					NBTTagCompound data = message.getData(nbt, serverSide);
					NBTTagList glOperationList = message.nbt.getTagList(message.nbtKey, NBT.TAG_COMPOUND);
					if (message.nbtKey.equals(NBTKeys.ARMOR_GL_OPERATIONS))
					{
						NBTTagList movingParts = data.getTagList(NBTKeys.ARMOR_PART_DATA, NBT.TAG_LIST);
						NBTBase nbtBase = movingParts.get(message.partIndex);
						if (nbtBase.getId() != NBT.TAG_LIST)
							return;
						
						NBTTagList itemList = (NBTTagList) nbtBase;
						NBTTagCompound armorItemNbt = itemList.getCompoundTagAt(message.armorItemIndex);
						armorItemNbt.setTag(message.nbtKey, glOperationList);
						itemList.set(message.armorItemIndex, armorItemNbt);
						data.setTag(NBTKeys.ARMOR_PART_DATA, movingParts);
					}
					else
					{
						data.setTag(message.nbtKey, glOperationList);
					}
					message.finalizeDataChange(message, stack, nbt, data, serverSide, false, false, -1);
					if (serverSide)
					{
						ExtraBitManipulation.packetNetwork.sendTo(new PacketChangeGlOperationList(message.nbt, message.nbtKey,
								message.equipmentSlot, message.mainArmor, message.partIndex, message.armorItemIndex,
								message.selectedEntry, message.refreshLists, player), (EntityPlayerMP) player);
					}
				}
			});
			return null;
		}
		
	}
	
}