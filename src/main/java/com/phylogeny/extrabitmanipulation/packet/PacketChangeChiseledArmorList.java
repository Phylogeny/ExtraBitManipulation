package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.phylogeny.extrabitmanipulation.armor.DataChiseledArmorPiece;
import com.phylogeny.extrabitmanipulation.client.GuiHelper;
import com.phylogeny.extrabitmanipulation.client.gui.armor.GuiChiseledArmor;
import com.phylogeny.extrabitmanipulation.init.RenderLayersExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ArmorType;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

public class PacketChangeChiseledArmorList extends PacketEquipmentSlot
{
	protected int partIndex, armorItemIndex, selectedEntry;
	protected boolean refreshLists;
	
	public PacketChangeChiseledArmorList() {}
	
	public PacketChangeChiseledArmorList(EntityEquipmentSlot equipmentSlot, int partIndex, int armorItemIndex, int selectedEntry, boolean refreshLists)
	{
		super(equipmentSlot);
		this.partIndex = partIndex;
		this.armorItemIndex = armorItemIndex;
		this.selectedEntry = selectedEntry;
		this.refreshLists = refreshLists;
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		buffer.writeInt(partIndex);
		buffer.writeInt(armorItemIndex);
		buffer.writeInt(selectedEntry);
		buffer.writeBoolean(refreshLists);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
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
	
	protected void initData(final PacketChangeChiseledArmorList message, ItemStack stack)
	{
		if (stack.hasTagCompound())
			return;
		
		NBTTagCompound nbt = new NBTTagCompound();
		new DataChiseledArmorPiece(ArmorType.values()[5 - message.equipmentSlot.ordinal()]).saveToNBT(nbt);
		stack.setTagCompound(nbt);
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