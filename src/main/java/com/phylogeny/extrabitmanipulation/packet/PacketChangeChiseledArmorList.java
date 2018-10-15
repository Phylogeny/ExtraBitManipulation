package com.phylogeny.extrabitmanipulation.packet;

import javax.annotation.Nullable;

import com.phylogeny.extrabitmanipulation.armor.DataChiseledArmorPiece;
import com.phylogeny.extrabitmanipulation.armor.capability.ChiseledArmorSlotsHandler;
import com.phylogeny.extrabitmanipulation.armor.capability.IChiseledArmorSlotsHandler;
import com.phylogeny.extrabitmanipulation.client.GuiHelper;
import com.phylogeny.extrabitmanipulation.client.gui.armor.GuiChiseledArmor;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.init.RenderLayersExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ArmorType;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public abstract class PacketChangeChiseledArmorList extends PacketArmorSlotInt
{
	protected int armorItemIndex, selectedEntry;
	protected boolean refreshLists;
	protected NBTTagCompound nbt = new NBTTagCompound();
	
	public PacketChangeChiseledArmorList() {}
	
	public PacketChangeChiseledArmorList(NBTTagCompound nbt, ArmorType armorType, int indexArmorSet,
			int partIndex, int armorItemIndex, int selectedEntry, boolean refreshLists, @Nullable EntityPlayer player)
	{
		super(armorType, indexArmorSet, partIndex);
		this.nbt = nbt;
		this.armorItemIndex = armorItemIndex;
		this.selectedEntry = selectedEntry;
		this.refreshLists = refreshLists;
		if (indexArmorSet > 0 && player instanceof EntityPlayerMP)
		{
			IChiseledArmorSlotsHandler cap = ChiseledArmorSlotsHandler.getCapability(player);
			if (cap != null)
				cap.markSlotDirty(armorType.getSlotIndex(indexArmorSet));
		}
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		super.toBytes(buffer);
		ByteBufUtils.writeTag(buffer, nbt);
		buffer.writeInt(armorItemIndex);
		buffer.writeInt(selectedEntry);
		buffer.writeBoolean(refreshLists);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		super.fromBytes(buffer);
		nbt = ByteBufUtils.readTag(buffer);
		armorItemIndex = buffer.readInt();
		selectedEntry = buffer.readInt();
		refreshLists = buffer.readBoolean();
	}
	
	protected NBTTagCompound getData(NBTTagCompound nbt, boolean serverSide)
	{
		NBTTagCompound data = ItemStackHelper.getArmorData(nbt);
		if (!serverSide)
			RenderLayersExtraBitManipulation.removeFromRenderMaps(data);
		
		return data;
	}
	
	protected void initData(final PacketChangeChiseledArmorList message, ItemStack stack)
	{
		NBTTagCompound nbt = ItemStackHelper.getNBTOrNew(stack);
		if (nbt.hasKey(NBTKeys.ARMOR_DATA))
			return;
		
		new DataChiseledArmorPiece(message.armorType).saveToNBT(nbt);
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