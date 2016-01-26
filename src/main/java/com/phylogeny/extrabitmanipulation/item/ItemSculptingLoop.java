package com.phylogeny.extrabitmanipulation.item;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import mod.chiselsandbits.api.APIExceptions.SpaceOccupied;
import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.APIExceptions.CannotBeChiseled;
import mod.chiselsandbits.api.IBitBrush;
import mod.chiselsandbits.api.IBitLocation;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import mod.chiselsandbits.api.ItemType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.reference.Configs;

public class ItemSculptingLoop extends ItemExtraBitManipulationBase
{
	
	public ItemSculptingLoop()
	{
		super("SculptingLoop", Configs.TAKES_DAMAGE_SCULPTING_LOOP, Configs.MAX_DAMAGE_SCULPTING_LOOP);
		modeTitles = new String[]{"Local", "Global"};
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
		if (!world.isRemote)
		{
			cycleModes(stack, true);
			player.inventoryContainer.detectAndSendChanges();
		}
        return stack;
    }
	
	public static boolean sculptBlock(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
			EnumFacing side, Vec3 hit)
    {
		if (ChiselsAndBitsAPIAccess.apiInstance.canBeChiseled(world, pos))
		{
			IBitAccess bitAccess;
			try
			{
				bitAccess = ChiselsAndBitsAPIAccess.apiInstance.getBitAccess(world, pos);
			}
			catch (CannotBeChiseled e)
			{
				e.printStackTrace();
				return false;
			}
			IChiselAndBitsAPI api = ChiselsAndBitsAPIAccess.apiInstance;
			IBitLocation bitLoc = api.getBitPos((float) hit.xCoord - pos.getX(), (float) hit.yCoord - pos.getY(),
					(float) hit.zCoord - pos.getZ(), side, pos, false);
			if (bitLoc != null)
			{
				HashMap<IBitBrush, Integer> bitTypes = new HashMap<IBitBrush, Integer>();
				int x = bitLoc.getBitX();
				int y = bitLoc.getBitY();
				int z = bitLoc.getBitZ();
				NBTTagCompound nbt = stack.getTagCompound();
				if (!nbt.hasKey("sculptRadius"))
				{
					nbt.setInteger("sculptRadius", Configs.DEFAULT_REMOVAL_RADIUS);
				}
				int r = stack.getTagCompound().getInteger("sculptRadius");
				double r2 = r + 0.2;
				for (int i = x - r; i <= x + r; i++)
				{
					for (int j = y - r; j <= y + r; j++)
					{
						for (int k = z - r; k <= z + r; k++)
						{
							IBitBrush bit1 = bitAccess.getBitAt(i, j, k);
							if (!bit1.isAir())
							{
								float dx = x - i;
							    float dy = y - j;
							    float dz = z - k;
							    if (Math.sqrt(dx * dx + dy * dy + dz * dz) <= r2)
							    {
							    	if (!bitTypes.containsKey(bit1))
									{
										bitTypes.put(bit1, 1);
									}
									else
									{
										bitTypes.put(bit1, bitTypes.get(bit1) + 1);
									}
									try
									{
										bitAccess.setBitAt(i, j, k, null);
									}
									catch (SpaceOccupied e) {}
							    }
							}
						}
					}
				}
				if (!world.isRemote && !player.capabilities.isCreativeMode)
				{
					Set<IBitBrush> keySet = bitTypes.keySet();
					for (IBitBrush bit : keySet)
					{
						ItemStack bitStack = bit.getItemStack(1);
						if (bitStack.getItem() != null)
						{
							InventoryPlayer inv = player.inventory;
							int max = inv.getInventoryStackLimit();
							int total = bitTypes.get(bit);
							int quota;
							while (total > 0)
							{
								boolean added = false;
								if (total > 64)
								{
									quota = 64;
								}
								else
								{
									quota = total;
									for (int i = 0; i < inv.getSizeInventory(); i++)
									{
										ItemStack invStack = inv.getStackInSlot(i);
										if (invStack != null && api.getItemType(invStack) == ItemType.CHISLED_BIT
												&& ItemStack.areItemStackTagsEqual(invStack, bitStack))
										{
											int space = max - invStack.stackSize;
											if (space > 0)
											{
												int addAmount = Math.min(quota, space);
												invStack.stackSize += addAmount;
												quota -= addAmount;
												total -= addAmount;
												if (quota == 0) break;
											}
										}
									}
								}
								if (quota > 0)
								{
									ItemStack stack2 = bit.getItemStack(quota);
									int emptySlot = inv.getFirstEmptyStack();
									if (emptySlot >= 0)
									{
										inv.setInventorySlotContents(emptySlot, stack2);
									}
									else
									{
										player.dropItem(stack2, false, false);
									}
									total -= quota;
								}
							}
						}
					}
					player.inventoryContainer.detectAndSendChanges();
				}
				bitAccess.commitChanges();
				if (Configs.TAKES_DAMAGE_SCULPTING_LOOP) stack.damageItem(1, player);
				return true;
			}
		}
		return false;
    }
	
	public void onCreated(ItemStack stack, World world, EntityPlayer player)
    {
		super.onCreated(stack, world, player);
		stack.getTagCompound().setInteger("sculptRadius", Configs.DEFAULT_REMOVAL_RADIUS);
    }
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		super.addInformation(stack, player, list, par4);
		int mode = stack.getTagCompound().getInteger("mode");
		String text = "Left click to sculpt ";
		if (mode == 0)
		{
			list.add(text + "only the");
			list.add("    block clicked.");
		}
		else
		{
			list.add(text + "all blocks");
			list.add("    intersecting removal area.");
		}
		list.add("Right click to cycle modes.");
		list.add("Mouse wheel while sneaking");
		list.add("    to change removal radius.");
	}
	
}