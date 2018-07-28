package com.phylogeny.extrabitmanipulation.armor.capability;

import java.util.Collection;
import java.util.HashSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ArmorType;
import com.phylogeny.extrabitmanipulation.packet.PacketSyncArmorSlot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.ItemStackHandler;

public class ChiseledArmorSlotsHandler extends ItemStackHandler implements ICapabilityProvider, IChiseledArmorSlotsHandler
{
	private boolean[] syncedSlots;
	
	@CapabilityInject(IChiseledArmorSlotsHandler.class)
	public static final Capability<IChiseledArmorSlotsHandler> ARMOR_SLOTS_CAP = null;
	
	public ChiseledArmorSlotsHandler()
	{
		super(ArmorType.values().length);
		syncedSlots = new boolean[getSlots()];
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
	{
		 return ARMOR_SLOTS_CAP != null && capability == ARMOR_SLOTS_CAP;
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
	{
		return capability == ARMOR_SLOTS_CAP ? ARMOR_SLOTS_CAP.<T>cast(this) : null;
	}
	
	public static IChiseledArmorSlotsHandler getCapability(EntityPlayer player)
	{
		return player.getCapability(ARMOR_SLOTS_CAP, null);
	}
	
	@Override
	public void syncAllSlots(EntityPlayer player)
	{
		Collection<EntityPlayer> players = null;
		for (int i = 0; i < getSlots(); i++)
		{
			if (syncedSlots[i])
				continue;
			
			if (players == null)
			{
				players = new HashSet<>();
				players.addAll(((WorldServer) player.world).getEntityTracker().getTrackingPlayers(player));
				players.add(player);
			}
			for (EntityPlayer player2 : players)
				ExtraBitManipulation.packetNetwork.sendTo(new PacketSyncArmorSlot(player.getUniqueID(), getStackInSlot(i), i), (EntityPlayerMP) player2);
			
			syncedSlots[i] = true;
		}
	}
	
	@Override
	public void markAllSlotsDirty()
	{
		syncedSlots = new boolean[getSlots()];
	}
	
	@Override
	public void markSlotDirty(int index)
	{
		syncedSlots[index] = false;
	}
	
	@Override
	protected void onContentsChanged(int slot)
	{
		markSlotDirty(slot);
	}
	
	@Override
	public int getSlotLimit(int slot)
	{
		return 1;
	}
	
	@Override
	public void setSize(int size)
	{
		super.setSize(ArmorType.values().length);
	}
	
	public static boolean isItemValid(int slot, ItemStack stack)
	{
		return ItemStackHelper.isChiseledArmorStack(stack) && ((ItemChiseledArmor) stack.getItem()).armorType.ordinal() == slot
				&& ItemStackHelper.isChiseledArmorNotEmpty(stack);
	}
	
	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack)
	{
		if (stack.isEmpty() || isItemValid(slot, stack))
			super.setStackInSlot(slot, stack);
	}
	
	@Override
	@Nonnull
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
	{
		return isItemValid(slot, stack) ? super.insertItem(slot, stack, simulate) : stack;
	}
	
}