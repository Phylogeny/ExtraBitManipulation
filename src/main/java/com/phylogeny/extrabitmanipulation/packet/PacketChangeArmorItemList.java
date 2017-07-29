package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
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
import com.phylogeny.extrabitmanipulation.armor.ArmorItem;
import com.phylogeny.extrabitmanipulation.armor.DataChiseledArmorPiece;
import com.phylogeny.extrabitmanipulation.armor.GlOperation;
import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.container.ContainerPlayerInventory;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ArmorType;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

public class PacketChangeArmorItemList extends PacketChangeChiseledArmorList
{
	private int scale;
	private ItemStack stack;
	private ListOperation listOperation;
	
	public PacketChangeArmorItemList() {}
	
	public PacketChangeArmorItemList(EntityEquipmentSlot equipmentSlot, int partIndex, int armorItemIndex,
			int selectedEntry, ListOperation listOperation, ItemStack stack, int scale, boolean refreshLists)
	{
		super(equipmentSlot, partIndex, armorItemIndex, selectedEntry, refreshLists);
		this.listOperation = listOperation;
		this.stack = stack;
		this.scale = scale;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		buffer.writeInt(listOperation.ordinal());
		ByteBufUtils.writeItemStack(buffer, stack);
		buffer.writeInt(scale);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		listOperation = ListOperation.values()[buffer.readInt()];
		stack = ByteBufUtils.readItemStack(buffer);
		scale = buffer.readInt();
	}
	
	public static class Handler implements IMessageHandler<PacketChangeArmorItemList, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketChangeArmorItemList message, final MessageContext ctx)
		{
			final boolean serverSide = ctx.side == Side.SERVER;
			IThreadListener mainThread = serverSide ? (WorldServer) ctx.getServerHandler().player.world : ClientHelper.getThreadListener();
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = serverSide ? ctx.getServerHandler().player : ClientHelper.getPlayer();
					ItemStack stack = player.getItemStackFromSlot(message.equipmentSlot);
					if (!stack.hasTagCompound())
					{
						NBTTagCompound nbt = new NBTTagCompound();
						new DataChiseledArmorPiece(ArmorType.values()[5 - message.equipmentSlot.ordinal()]).saveToNBT(nbt);
						stack.setTagCompound(nbt);
					}
					NBTTagCompound nbt = ItemStackHelper.getNBT(stack);
					NBTTagCompound data = message.getData(nbt, serverSide);
					NBTTagList movingParts = data.getTagList(NBTKeys.ARMOR_PART_DATA, NBT.TAG_LIST);
					NBTBase nbtBase = movingParts.get(message.partIndex);
					if (nbtBase.getId() != NBT.TAG_LIST)
						return;
					
					Container container = player.openContainer;
					if (container == null || !(container instanceof ContainerPlayerInventory))
						return;
					
					NBTTagList itemList = (NBTTagList) nbtBase;
					int glListRemovalIndex = -1;
					boolean add = message.listOperation == ListOperation.ADD;
					if (message.listOperation == ListOperation.MODIFY)
					{
						NBTTagCompound armorItemNbt = itemList.getCompoundTagAt(message.armorItemIndex);
						ItemStackHelper.saveStackToNBT(armorItemNbt, message.stack, NBTKeys.ARMOR_ITEM);
						itemList.set(message.armorItemIndex, armorItemNbt);
					}
					else
					{
						if (add)
						{
							NBTTagCompound armorItemNbt = new NBTTagCompound();
							ArmorItem armorItem = new ArmorItem();
							if (message.scale > 0)
							{
								float scale2 = (float) (1 / Math.pow(2, message.scale));
								armorItem.addGlOperation(GlOperation.createScale(scale2, scale2, scale2));
							}
							armorItem.saveToNBT(armorItemNbt);
							itemList.appendTag(armorItemNbt);
						}
						else
						{
							itemList.removeTag(message.armorItemIndex);
							glListRemovalIndex = message.armorItemIndex;
						}
					}
					movingParts.set(message.partIndex, itemList);
					DataChiseledArmorPiece.setPartData(data, movingParts);
					message.finalizeDataChange(message, stack, nbt, data, serverSide, true, add, glListRemovalIndex);
					if (serverSide)
					{
						ExtraBitManipulation.packetNetwork.sendTo(new PacketChangeArmorItemList(message.equipmentSlot,
								message.partIndex, message.armorItemIndex, message.selectedEntry, message.listOperation,
								message.stack, message.scale, message.refreshLists), (EntityPlayerMP) player);
					}
				}
			});
			return null;
		}
		
	}
	
	public static enum ListOperation
	{
		ADD, REMOVE, MODIFY;
	}
	
}