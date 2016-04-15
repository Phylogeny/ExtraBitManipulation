package com.phylogeny.extrabitmanipulation.helper;

import java.util.HashMap;
import java.util.Set;

import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.shape.Shape;

import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.IBitBag;
import mod.chiselsandbits.api.IBitBrush;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import mod.chiselsandbits.api.ItemType;
import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import mod.chiselsandbits.api.APIExceptions.SpaceOccupied;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BitStackHelper
{
	
	public static int countInventoryBits(IChiselAndBitsAPI api, EntityPlayer player, ItemStack setBitStack)
	{
		int count = 0;
		InventoryPlayer inventoy = player.inventory;
		for (int i = 0; i < inventoy.getSizeInventory(); i++)
		{
			ItemStack stack = inventoy.getStackInSlot(i);
			if (stack != null)
			{
				count += getBitCountFromStack(api, setBitStack, stack);
				if (api.getItemType(stack) == ItemType.BIT_BAG)
				{
					IBitBag bitBag = api.getBitbag(stack);
					for (int j = 0; j < bitBag.getSlots(); j++)
					{
						ItemStack bagStack = bitBag.getStackInSlot(j);
						count += getBitCountFromStack(api, setBitStack, bagStack);
					}
				}
			}
		}
		return count;
	}

	private static int getBitCountFromStack(IChiselAndBitsAPI api, ItemStack setBitStack, ItemStack stack)
	{
		return areBitStacksEqual(api, setBitStack, stack) ? stack.stackSize : 0;
	}

	private static boolean areBitStacksEqual(IChiselAndBitsAPI api, ItemStack bitStack, ItemStack putativeBitStack)
	{
		return putativeBitStack != null && api.getItemType(putativeBitStack) == ItemType.CHISLED_BIT
				&& ItemStack.areItemStackTagsEqual(putativeBitStack, bitStack);
	}
	
	public static void removeOrAddInventoryBits(IChiselAndBitsAPI api, EntityPlayer player, ItemStack setBitStack, int quota, boolean addBits)
	{
		if (quota > 0)
		{
			InventoryPlayer inventoy = player.inventory;
			for (int i = 0; i < inventoy.getSizeInventory(); i++)
			{
				ItemStack stack = inventoy.getStackInSlot(i);
				if (!addBits) quota = removeBitsFromStack(api, setBitStack, quota, inventoy, null, i, stack);
				if (api.getItemType(stack) == ItemType.BIT_BAG)
				{
					IBitBag bitBag = api.getBitbag(stack);
					for (int j = 0; j < bitBag.getSlots(); j++)
					{
						ItemStack bagStack = bitBag.getStackInSlot(j);
						quota = addBits ? addBitsToBag(quota, bitBag, j, setBitStack)
								: removeBitsFromStack(api, setBitStack, quota, null, bitBag, j, bagStack);
						if (quota <= 0) break;
					}
				}
				if (quota <= 0) break;
			}
		}
	}
	
	private static int addBitsToBag(int quota, IBitBag bitBag, int index, ItemStack stack)
	{
		if (stack != null)
		{
			int size = stack.stackSize;
			ItemStack remainingStack = bitBag.insertItem(index, stack, false);
			int reduction = size - (remainingStack != null ? remainingStack.stackSize : 0);
			quota -= reduction;
			stack.stackSize -= reduction;
		}
		return quota;
	}

	private static int removeBitsFromStack(IChiselAndBitsAPI api, ItemStack setBitStack,
			int quota, InventoryPlayer inventoy, IBitBag bitBag, int index, ItemStack stack)
	{
		if (areBitStacksEqual(api, setBitStack, stack))
		{
			int size = stack.stackSize;
			if (size > quota)
			{
				if (bitBag != null)
				{
					bitBag.extractItem(index, quota, false);
				}
				else
				{
					stack.stackSize = size - quota;
				}
				quota = 0;
			}
			else
			{
				if (bitBag != null)
				{
					bitBag.extractItem(index, size, false);
				}
				else if (inventoy != null)
				{
					inventoy.setInventorySlotContents(index, null);
				}
				quota -= size;
			}
		}
		return quota;
	}
	
	public static void giveOrDropStacks(EntityPlayer player, World world, BlockPos pos, Shape shape,
			IChiselAndBitsAPI api, HashMap<IBlockState, Integer> bitTypes)
	{
		if (bitTypes != null)
		{
			Set<IBlockState> keySet = bitTypes.keySet();
			for (IBlockState state : keySet)
			{
				ItemStack bitStack;
				try
				{
					bitStack = api.getBitItem(state);
				}
				catch (InvalidBitItem e)
				{
					continue;
				}
				if (bitStack.getItem() != null)
				{
					IBitBrush bit;
					try
					{
						bit = api.createBrush(bitStack);
					}
					catch (InvalidBitItem e)
					{
						continue;
					}
					int totalBits = bitTypes.get(state);
					if (Configs.dropBitsAsFullChiseledBlocks && totalBits >= 4096)
					{
						IBitAccess bitAccess = api.createBitItem(null);
						setAllBits(bitAccess, bit);
						int blockCount = totalBits / 4096;
						totalBits -= blockCount * 4096;
						while (blockCount > 0)
						{
							int stackSize = blockCount > 64 ? 64 : blockCount;
							ItemStack stack2 = bitAccess.getBitsAsItem(null, ItemType.CHISLED_BLOCK, false);
							if (stack2 != null)
							{
								stack2.stackSize = stackSize;
								givePlayerStackOrDropOnGround(player, world, api, pos, shape, stack2);
							}
							blockCount -= stackSize;
						}
					}
					int quota;
					while (totalBits > 0)
					{
						quota = totalBits > 64 ? 64 : totalBits;
						ItemStack bitStack2 = bit.getItemStack(quota);
						givePlayerStackOrDropOnGround(player, world, api, pos, shape, bitStack2);
						totalBits -= quota;
					}
				}
			}
			bitTypes.clear();
			if (Configs.placeBitsInInventory) player.inventoryContainer.detectAndSendChanges();
		}
	}

	private static void givePlayerStackOrDropOnGround(EntityPlayer player, World world, IChiselAndBitsAPI api, BlockPos pos, Shape shape, ItemStack stack)
	{
		if (Configs.placeBitsInInventory)
		{
			removeOrAddInventoryBits(api, player, stack, stack.stackSize, true);
			if (stack.stackSize > 0)
			{
				player.inventory.addItemStackToInventory(stack);
			}
		}
		if (stack.stackSize > 0)
		{
			if (Configs.dropBitsInBlockspace)
			{
				spawnStacksInShape(world, pos, shape, stack);
			}
			else
			{
				player.dropItem(stack, false, false);
			}
		}
	}
	
	private static void spawnStacksInShape(World world, BlockPos pos, Shape shape, ItemStack stack)
	{
		if (!world.isRemote && world.getGameRules().getBoolean("doTileDrops") && !world.restoringBlockSnapshots)
        {
			Vec3 spawnPoint = shape.getRandomInternalPoint(world, pos);
			EntityItem entityitem = new EntityItem(world, spawnPoint.xCoord, spawnPoint.yCoord - 0.25, spawnPoint.zCoord, stack);
            entityitem.setDefaultPickupDelay();
            world.spawnEntityInWorld(entityitem);
        }
	}
	
	private static void setAllBits(IBitAccess bitAccess, IBitBrush bit)
	{
		for (int i = 0; i < 16; i++)
		{
			for (int j = 0; j < 16; j++)
			{
				for (int k = 0; k < 16; k++)
				{
					try
					{
						bitAccess.setBitAt(i, j, k, bit);
					}
					catch (SpaceOccupied e) {}
				}
			}
		}
	}
	
}