package com.phylogeny.extrabitmanipulation.helper;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import mod.chiselsandbits.api.APIExceptions.SpaceOccupied;
import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.IBitBag;
import mod.chiselsandbits.api.IBitBrush;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import mod.chiselsandbits.api.ItemType;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.reference.ChiselsAndBitsReferences;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.shape.Shape;

public class BitInventoryHelper
{

	public static Map<Integer, Integer> getInventoryBitCounts(IChiselAndBitsAPI api, EntityPlayer player)
	{
		Map<Integer, Integer> bitCounts = new HashMap<Integer, Integer>();
		for (int i = 0; i < player.inventory.getSizeInventory(); i++)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);
			if (isBitStack(api, stack))
			{
				try
				{
					int bitStateID = api.createBrush(stack).getStateID();
					if (!bitCounts.containsKey(bitStateID))
						bitCounts.put(bitStateID, countInventoryBits(api, player, stack));
				}
				catch (InvalidBitItem e) {}
			}
		}
		return getSortedLinkedHashMap(bitCounts, new Comparator<Object>() {
			@Override
			@SuppressWarnings("unchecked")
			public int compare(Object object1, Object object2)
			{
				return ((Comparable<Integer>) ((Map.Entry<Integer, Integer>) (object2)).getValue())
						.compareTo(((Map.Entry<Integer, Integer>) (object1)).getValue());
			}
		});
	}
	
	public static LinkedHashMap getSortedLinkedHashMap(Map bitCounts, Comparator<Object> comparator)
	{
		List<Map.Entry> bitCountsList = new LinkedList(bitCounts.entrySet());
		Collections.sort(bitCountsList, comparator);
		LinkedHashMap bitCountsSorted = new LinkedHashMap();
		for (Map.Entry entry : bitCountsList)
			bitCountsSorted.put(entry.getKey(), entry.getValue());
		
		return bitCountsSorted;
	}
	
	public static int countInventoryBits(IChiselAndBitsAPI api, EntityPlayer player, ItemStack setBitStack)
	{
		int count = 0;
		for (int i = 0; i < player.inventory.getSizeInventory(); i++)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);
			if (stack == null)
				continue;
			
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
		return count;
	}
	
	public static int countInventoryBlocks(EntityPlayer player, Block block)
	{
		int count = 0;
		for (int i = 0; i < player.inventory.getSizeInventory(); i++)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);
			if (stack != null && stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).block == block)
				count += stack.stackSize;
		}
		return count;
	}
	
	private static int getBitCountFromStack(IChiselAndBitsAPI api, ItemStack setBitStack, ItemStack stack)
	{
		return areBitStacksEqual(api, setBitStack, stack) ? stack.stackSize : 0;
	}
	
	private static boolean areBitStacksEqual(IChiselAndBitsAPI api, ItemStack bitStack, ItemStack putativeBitStack)
	{
		return isBitStack(api, putativeBitStack) && ItemStack.areItemStackTagsEqual(putativeBitStack, bitStack);
	}
	
	public static boolean isBitStack(IChiselAndBitsAPI api, ItemStack putativeBitStack)
	{
		return putativeBitStack != null && api.getItemType(putativeBitStack) == ItemType.CHISLED_BIT;
	}
	
	public static void removeBitsFromBlocks(IChiselAndBitsAPI api, EntityPlayer player, ItemStack bitStack, Block block, int quota)
	{
		if (quota <= 0)
			return;
		
		InventoryPlayer inventoy = player.inventory;
		for (int i = 0; i < inventoy.getSizeInventory(); i++)
		{
			ItemStack stack = inventoy.getStackInSlot(i);
			if (stack == null || !(stack.getItem() instanceof ItemBlock))
				continue;
			
			Block block2 = ((ItemBlock) stack.getItem()).block;
			if (block2 != block)
				continue;
			
			int count = stack.stackSize;
			for (int j = 0; j < count; j++)
			{
				if (quota >= 4096)
				{
					quota -= 4096;
					stack.stackSize -= 1;
				}
				else
				{
					stack.stackSize -= 1;
					break;
				}
				if (quota <= 0) break;
			}
			if (quota > 0 && quota < 4096)
			{
				Vec3d spawnPos = new Vec3d(player.posX, player.posY, player.posZ);
				quota = 4096 - quota;
				int stakCount = (int) Math.ceil(quota / 64.0);
				for (int j = 0; j < stakCount; j++)
				{
					ItemStack stack2 = bitStack.copy();
					stack2.stackSize = Math.min(64, quota);
					quota -= stack2.stackSize;
					api.giveBitToPlayer(player, stack2, spawnPos);
				}
			}
			if (quota <= 0) break;
		}
	}
	
	public static int removeOrAddInventoryBits(IChiselAndBitsAPI api, EntityPlayer player, ItemStack setBitStack, int quota, boolean addBits)
	{
		if (quota <= 0)
			return quota;
		
		InventoryPlayer inventoy = player.inventory;
		for (int i = 0; i < inventoy.getSizeInventory(); i++)
		{
			ItemStack stack = inventoy.getStackInSlot(i);
			if (!addBits)
				quota = removeBitsFromStack(api, setBitStack, quota, inventoy, null, i, stack);
			
			if (api.getItemType(stack) == ItemType.BIT_BAG)
			{
				IBitBag bitBag = api.getBitbag(stack);
				for (int j = 0; j < bitBag.getSlots(); j++)
				{
					ItemStack bagStack = bitBag.getStackInSlot(j);
					quota = addBits ? addBitsToBag(quota, bitBag, j, setBitStack) : removeBitsFromStack(api, setBitStack, quota, null, bitBag, j, bagStack);
					if (quota <= 0) break;
				}
			}
			if (quota <= 0) break;
		}
		return quota;
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
			IChiselAndBitsAPI api, Map<IBlockState, Integer> bitTypes)
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
				player.inventory.addItemStackToInventory(stack);
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
			Vec3d spawnPoint = shape.getRandomInternalPoint(world, pos);
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
	
	public static void setHeldDesignStack(EntityPlayer player, ItemStack stackChiseledBlock)
	{
		ItemStack stack = player.getHeldItemMainhand();
		ItemType itemType = ChiselsAndBitsAPIAccess.apiInstance.getItemType(stack);
		if (itemType == null || !ItemStackHelper.isDesignItemType(itemType))
			return;
		
		IBitAccess bitAccess = ChiselsAndBitsAPIAccess.apiInstance.createBitItem(stackChiseledBlock);
		if (bitAccess != null)
		{
			ItemStack stackDesign = bitAccess.getBitsAsItem(EnumFacing.getFront(ItemStackHelper.getNBTOrNew(stack)
					.getInteger(ChiselsAndBitsReferences.NBT_KEY_DESIGN_SIDE)), itemType, false);
			if (stackDesign == null)
				stackDesign = new ItemStack(Item.getByNameOrId(ChiselsAndBitsReferences.MOD_ID + ":" + (itemType == ItemType.POSITIVE_DESIGN
				? ChiselsAndBitsReferences.ITEM_PATH_DESIGN_POSITIVE : (itemType == ItemType.NEGATIVE_DESIGN
				? ChiselsAndBitsReferences.ITEM_PATH_DESIGN_NEGATIVE : ChiselsAndBitsReferences.ITEM_PATH_DESIGN_MIRROR))));
			
			if (stack != null && stack.hasTagCompound())
			{
				String mode = ItemStackHelper.getNBT(stack).getString(ChiselsAndBitsReferences.NBT_KEY_DESIGN_MODE);
				if (!stackDesign.hasTagCompound())
					stackDesign.setTagCompound(new NBTTagCompound());
				
				ItemStackHelper.getNBT(stackDesign).setString(ChiselsAndBitsReferences.NBT_KEY_DESIGN_MODE, mode);
			}
			player.setHeldItem(EnumHand.MAIN_HAND, stackDesign);
		}
	}
	
}