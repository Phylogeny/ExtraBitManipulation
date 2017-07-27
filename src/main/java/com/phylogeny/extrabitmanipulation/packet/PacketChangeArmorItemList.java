package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;
import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.IBitBrush;
import mod.chiselsandbits.api.IBitVisitor;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import mod.chiselsandbits.api.ItemType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.armor.ArmorItem;
import com.phylogeny.extrabitmanipulation.armor.DataChiseledArmorPiece;
import com.phylogeny.extrabitmanipulation.armor.GlOperation;
import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.container.ContainerChiseledArmor;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

public class PacketChangeArmorItemList extends PacketChangeChiseledArmorList
{
	private int slotNumber, scale;
	private boolean add;
	
	public PacketChangeArmorItemList() {}
	
	public PacketChangeArmorItemList(EntityEquipmentSlot equipmentSlot, int partIndex, int armorItemIndex,
			int selectedEntry, int slotNumber, boolean add, int scale, boolean refreshLists)
	{
		super(equipmentSlot, partIndex, armorItemIndex, selectedEntry, refreshLists);
		this.slotNumber = slotNumber;
		this.add = add;
		this.scale = scale;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		buffer.writeInt(slotNumber);
		buffer.writeBoolean(add);
		buffer.writeInt(scale);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		slotNumber = buffer.readInt();
		add = buffer.readBoolean();
		scale = buffer.readInt();
	}
	
	public static class Handler implements IMessageHandler<PacketChangeArmorItemList, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketChangeArmorItemList message, final MessageContext ctx)
		{
			final boolean serverSide = ctx.side == Side.SERVER;
			IThreadListener mainThread = serverSide ? (WorldServer) ctx.getServerHandler().playerEntity.world : ClientHelper.getThreadListener();
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = serverSide ? ctx.getServerHandler().playerEntity : ClientHelper.getPlayer();
					ItemStack stack = player.getItemStackFromSlot(message.equipmentSlot);
					if (!stack.hasTagCompound())
						return;
					
					NBTTagCompound nbt = ItemStackHelper.getNBT(stack);
					NBTTagCompound data = message.getData(nbt, serverSide);
					NBTTagList movingParts = data.getTagList(NBTKeys.ARMOR_PART_DATA, NBT.TAG_LIST);
					NBTBase nbtBase = movingParts.get(message.partIndex);
					if (nbtBase.getId() != NBT.TAG_LIST)
						return;
					
					Container container = player.openContainer;
					if (container == null || !(container instanceof ContainerChiseledArmor))
						return;
					
					NBTTagList itemList = (NBTTagList) nbtBase;
					ContainerChiseledArmor containerArmor = (ContainerChiseledArmor) container;
					int stackIndex = 5 - message.equipmentSlot.ordinal();
					int glListRemovalIndex;
					if (message.add)
					{
						containerArmor.addSlot(stackIndex, message.partIndex, message.slotNumber, message.selectedEntry);
						NBTTagCompound armorItemNbt = new NBTTagCompound();
						ArmorItem armorItem = new ArmorItem();
						if (message.scale > 0)
						{
							float scale2 = (float) (1 / Math.pow(2, message.scale));
							armorItem.addGlOperation(GlOperation.createScale(scale2, scale2, scale2));
						}
						armorItem.saveToNBT(armorItemNbt);
						itemList.appendTag(armorItemNbt);
						glListRemovalIndex = -1;
					}
					else
					{
						if (Configs.armorSlotRemovalMode == ArmorSlotRemovalMode.PREVENT_IF_FULL && serverSide
								&& !containerArmor.inventoryItemStacks.get(message.slotNumber).isEmpty())
							return;
						
						ItemStack stackRemoved = containerArmor.removeSlot(stackIndex, message.partIndex, message.slotNumber, message.armorItemIndex);
						if (serverSide && !stackRemoved.isEmpty())
							new StackProvider(player, stackRemoved).giveStackToPlayer();
						
						itemList.removeTag(message.armorItemIndex);
						glListRemovalIndex = message.armorItemIndex;
					}
					movingParts.set(message.partIndex, itemList);
					DataChiseledArmorPiece.setPartData(data, movingParts);
					message.finalizeDataChange(message, stack, nbt, data, serverSide, true, message.add, glListRemovalIndex);
					if (serverSide)
					{
						ExtraBitManipulation.packetNetwork.sendTo(new PacketChangeArmorItemList(message.equipmentSlot, message.partIndex, message.armorItemIndex,
								message.selectedEntry, message.slotNumber, message.add, message.scale, message.refreshLists), (EntityPlayerMP) player);
					}
				}
			});
			return null;
		}
		
	}
	
	public static class StackProvider implements IBitVisitor
	{
		private EntityPlayer player;
		protected ItemStack stack;
		private Vec3d spawnPos;
		protected IChiselAndBitsAPI api;
		
		public StackProvider(EntityPlayer player, ItemStack stack)
		{
			this.player = player;
			this.stack = stack;
			spawnPos = new Vec3d(player.posX, player.posY, player.posZ);
			api = ChiselsAndBitsAPIAccess.apiInstance;
		}
		
		public boolean giveStackToPlayer()
		{
			if (Configs.armorSlotRemovalMode == ArmorSlotRemovalMode.GIVE_AS_BITS && api.getItemType(stack) == ItemType.CHISLED_BLOCK)
			{
				IBitAccess bitAccess = api.createBitItem(stack);
				if (bitAccess != null)
				{
					bitAccess.visitBits(this);
					return true;
				}
			}
			api.giveBitToPlayer(player, stack, spawnPos);
			return true;
		}
		
		@Override
		public IBitBrush visitBit(int x, int y, int z, IBitBrush bit)
		{
			api.giveBitToPlayer(player, bit.getItemStack(1), spawnPos);
			return bit;
		}
		
	}
	
	public static enum ArmorSlotRemovalMode
	{
		GIVE_AS_STACK, GIVE_AS_BITS, PREVENT_IF_FULL;
	}
	
}