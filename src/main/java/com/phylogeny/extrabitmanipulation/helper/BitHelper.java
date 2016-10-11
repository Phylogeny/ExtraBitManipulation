package com.phylogeny.extrabitmanipulation.helper;

import io.netty.buffer.ByteBuf;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;
import com.phylogeny.extrabitmanipulation.shape.Shape;

import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.IBitBag;
import mod.chiselsandbits.api.IBitBrush;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import mod.chiselsandbits.api.IMultiStateBlock;
import mod.chiselsandbits.api.ItemType;
import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import mod.chiselsandbits.api.APIExceptions.SpaceOccupied;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class BitHelper
{
	
	public static LinkedHashMap<Integer, Integer> getInventoryBitCounts(IChiselAndBitsAPI api, EntityPlayer player)
	{
		HashMap<Integer, Integer> bitCounts = new HashMap<Integer, Integer>();
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
		List<Map.Entry<Integer, Integer>> bitCountsList = new LinkedList(bitCounts.entrySet());
		Collections.sort(bitCountsList, new Comparator<Object>() {
			@Override
			@SuppressWarnings("unchecked")
			public int compare(Object object1, Object object2) {
				return ((Comparable<Integer>) ((Map.Entry<Integer, Integer>) (object2)).getValue()).compareTo(((Map.Entry<Integer, Integer>) (object1)).getValue());
			}
		});
		LinkedHashMap<Integer, Integer> bitCountsSorted = new LinkedHashMap<Integer, Integer>();
		for (Map.Entry<Integer, Integer> entry : bitCountsList)
		{
			bitCountsSorted.put(entry.getKey(), entry.getValue());
		}
		return bitCountsSorted;
	}
	
	public static int countInventoryBits(IChiselAndBitsAPI api, EntityPlayer player, ItemStack setBitStack)
	{
		int count = 0;
		for (int i = 0; i < player.inventory.getSizeInventory(); i++)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);
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
		return isBitStack(api, putativeBitStack) && ItemStack.areItemStackTagsEqual(putativeBitStack, bitStack);
	}

	public static boolean isBitStack(IChiselAndBitsAPI api, ItemStack putativeBitStack)
	{
		return putativeBitStack != null && api.getItemType(putativeBitStack) == ItemType.CHISLED_BIT;
	}
	
	public static void removeOrAddInventoryBits(IChiselAndBitsAPI api, EntityPlayer player, ItemStack setBitStack, int quota, boolean addBits)
	{
		if (quota <= 0)
			return;
		
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
					quota = addBits ? addBitsToBag(quota, bitBag, j, setBitStack)
							: removeBitsFromStack(api, setBitStack, quota, null, bitBag, j, bagStack);
					if (quota <= 0)
						break;
				}
			}
			if (quota <= 0) break;
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
	
	public static HashMap<IBlockState, IBitBrush> writeStateToBitMapToNBT(ItemStack bitStack, String key, HashMap<IBlockState, IBitBrush> stateToBitMap)
	{
		if (bitStack.hasTagCompound())
		{
			NBTTagCompound nbtEntry;
			NBTTagList nbtList = new NBTTagList();
			for (IBlockState state : stateToBitMap.keySet())
			{
				nbtEntry = new NBTTagCompound();
				writeBlockToNBT(nbtEntry, state);
				nbtEntry.setInteger(NBTKeys.STATE_META, state.getBlock().getMetaFromState(state));
				ItemStackHelper.saveStackToNBT(nbtEntry, stateToBitMap.get(state).getItemStack(1), NBTKeys.BIT_STACK);
				nbtList.appendTag(nbtEntry);
			}
			bitStack.getTagCompound().setTag(key, nbtList);
		}
		return stateToBitMap;
	}
	
	public static HashMap<IBlockState, IBitBrush> readStateToBitMapFromNBT(IChiselAndBitsAPI api, ItemStack bitStack, String key)
	{
		HashMap<IBlockState, IBitBrush> stateToBitMap = new HashMap<IBlockState, IBitBrush>();
		if (bitStack.hasTagCompound() && bitStack.getTagCompound().hasKey(key))
		{
			NBTTagList stateNbtList = bitStack.getTagCompound().getTagList(key, Constants.NBT.TAG_COMPOUND);
			for (int n = 0; n < stateNbtList.tagCount(); n++)
			{
				NBTTagCompound entryNbt = stateNbtList.getCompoundTagAt(n);
				IBlockState state = readStateFromNBT(entryNbt);
				if (!isAir(state))
				{
					try
					{
						stateToBitMap.put(state, api.createBrush(ItemStackHelper.loadStackFromNBT(entryNbt, NBTKeys.BIT_STACK)));
					}
					catch (InvalidBitItem e) {}
				}
			}
		}
		return stateToBitMap;
	}
	
	public static void readStatesFromNBT(NBTTagCompound nbt, HashMap<IBlockState, Integer> stateMap, IBlockState[][][] stateArray)
	{
		NBTTagList stateNbtList = nbt.getTagList(NBTKeys.SAVED_STATES, Constants.NBT.TAG_COMPOUND);
		for (int n = 0; n < stateNbtList.tagCount(); n++)
		{
			NBTTagCompound stateNbt = stateNbtList.getCompoundTagAt(n);
			int i = n / 256;
			int n2 = n % 256;
			int j = n2 / 16;
			int k = n2 % 16;
			IBlockState state = readStateFromNBT(stateNbt);
			stateArray[i][j][k] = state;
			if (!isAir(state))
				stateMap.put(state, 1 + (stateMap.containsKey(state) ? stateMap.get(state) : 0));
		}
		if (stateNbtList.hasNoTags())
		{
			IBlockState air = Blocks.AIR.getDefaultState();
			for (int i = 0; i < 16; i++)
			{
				for (int j = 0; j < 16; j++)
				{
					for (int k = 0; k < 16; k++)
					{
						stateArray[i][j][k] = air;
					}
				}
			}
		}
	}
	
	public static void saveBlockStates(IChiselAndBitsAPI api, EntityPlayer player, World world, BlockPos pos, NBTTagCompound nbt)
	{
		if (world.isRemote)
			return;
		
		NBTTagCompound nbtState;
		NBTTagList nbtList = new NBTTagList();
		for (int i = 0; i < 16; i++)
		{
			for (int j = 0; j < 16; j++)
			{
				for (int k = 0; k < 16; k++)
				{
					BlockPos pos2 = new BlockPos(pos.getX() - 8 + i, pos.getY() - 8 + j, pos.getZ() - 8 + k);
					IBlockState state = world.getBlockState(pos2);
					if (api.isBlockChiseled(world, pos2) && state.getBlock() instanceof IMultiStateBlock)
						state = ((IMultiStateBlock) state.getBlock()).getPrimaryState(world, pos2);
					
					nbtState = new NBTTagCompound();
					writeBlockToNBT(nbtState, state);
					nbtState.setInteger(NBTKeys.STATE_META, state.getBlock().getMetaFromState(state));
					nbtList.appendTag(nbtState);
				}
			}
		}
		nbt.setTag(NBTKeys.SAVED_STATES, nbtList);
		player.inventoryContainer.detectAndSendChanges();
	}
	
	public static boolean isAir(IBlockState state)
	{
		return state.equals(Blocks.AIR.getDefaultState());
	}
	
	public static boolean isAir(Block block)
	{
		return block.equals(Blocks.AIR);
	}
	
	public static IBlockState readStateFromNBT(NBTTagCompound nbt)
	{
		Block block = readBlockFromNBT(nbt);
		if (block == null)
			return Blocks.AIR.getDefaultState();
		
		return block.getStateFromMeta(nbt.getInteger(NBTKeys.STATE_META));
	}
	
	private static void writeBlockToNBT(NBTTagCompound nbtState, IBlockState state)
	{
		ResourceLocation regName = state.getBlock().getRegistryName();
		nbtState.setString(NBTKeys.STATE_DOMAIN, regName.getResourceDomain());
		nbtState.setString(NBTKeys.STATE_PATH, regName.getResourcePath());
	}
	
	public static Block readBlockFromNBT(NBTTagCompound nbt)
	{
		return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString(NBTKeys.STATE_DOMAIN), nbt.getString(NBTKeys.STATE_PATH)));
	}
	
	public static void stateToBytes(ByteBuf buffer, IBlockState state)
	{
		Block block = state.getBlock();
		blockToBytes(buffer, block);
		buffer.writeInt(block.getMetaFromState(state));
	}
	
	public static IBlockState stateFromBytes(ByteBuf buffer)
	{
		Block block = blockFromBytes(buffer);
		int meta = buffer.readInt();
		return block != null ? block.getStateFromMeta(meta) : null;
	}
	
	public static void blockToBytes(ByteBuf buffer, Block block)
	{
		ResourceLocation regName = block.getRegistryName();
		ByteBufUtils.writeUTF8String(buffer, regName.getResourceDomain());
		ByteBufUtils.writeUTF8String(buffer, regName.getResourcePath());
	}
	
	public static Block blockFromBytes(ByteBuf buffer)
	{
		return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ByteBufUtils.readUTF8String(buffer), ByteBufUtils.readUTF8String(buffer)));
	}

	public static String getBitName(ItemStack bitStack)
	{
		return bitStack.getDisplayName().replace("Chiseled Bit - ", "");
	}
	
}