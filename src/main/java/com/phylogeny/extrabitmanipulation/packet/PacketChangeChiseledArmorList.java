package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import com.phylogeny.extrabitmanipulation.client.GuiHelper;
import com.phylogeny.extrabitmanipulation.client.gui.armor.GuiChiseledArmor;
import com.phylogeny.extrabitmanipulation.init.RenderLayersExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

public class PacketChangeChiseledArmorList extends PacketEquipmentSlot
{
	protected int partIndex, armorItemIndex, selectedEntry;
	protected boolean refreshLists;
	protected NBTTagCompound nbt = new NBTTagCompound();
	
	public PacketChangeChiseledArmorList() {}
	
	public PacketChangeChiseledArmorList(NBTTagCompound nbt, EntityEquipmentSlot equipmentSlot,
			int partIndex, int armorItemIndex, int selectedEntry, boolean refreshLists)
	{
		super(equipmentSlot);
		this.nbt = nbt;
		this.partIndex = partIndex;
		this.armorItemIndex = armorItemIndex;
		this.selectedEntry = selectedEntry;
		this.refreshLists = refreshLists;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		ByteBufUtils.writeTag(buffer, nbt);
		buffer.writeInt(partIndex);
		buffer.writeInt(armorItemIndex);
		buffer.writeInt(selectedEntry);
		buffer.writeBoolean(refreshLists);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		nbt = ByteBufUtils.readTag(buffer);
		partIndex = buffer.readInt();
		armorItemIndex = buffer.readInt();
		selectedEntry = buffer.readInt();
		refreshLists = buffer.readBoolean();
	}
	
	protected NBTTagCompound getData(NBTTagCompound nbt, boolean serverSide)
	{
		NBTTagCompound data = nbt.getCompoundTag(NBTKeys.ARMOR_DATA);
		if (!serverSide)
			RenderLayersExtraBitManipulation.removeFromDisplayListsMaps(data);
		
		return data;
	}
	
	protected void finalizeDataChange(PacketChangeChiseledArmorList message, ItemStack stack, NBTTagCompound nbt,
			NBTTagCompound data, boolean serverSide, boolean isArmorItem, boolean scrollToEnd, int glListRemovalIndex)
	{
		nbt.setTag(NBTKeys.ARMOR_DATA, data);
		stack.setTagCompound(nbt);
		if (serverSide)
			return;
		
		if (message.refreshLists && GuiHelper.getOpenGui() instanceof GuiChiseledArmor)
			((GuiChiseledArmor) GuiHelper.getOpenGui()).refreshListsAndSelectEntry(message.selectedEntry, isArmorItem, scrollToEnd, glListRemovalIndex);
	}
	
}