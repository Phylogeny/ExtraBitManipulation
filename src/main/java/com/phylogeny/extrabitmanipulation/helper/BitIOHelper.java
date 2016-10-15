package com.phylogeny.extrabitmanipulation.helper;

import io.netty.buffer.ByteBuf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

import mod.chiselsandbits.api.APIExceptions.CannotBeChiseled;
import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.IBitBrush;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.UniqueIdentifier;

public class BitIOHelper
{
	
	public static HashMap<IBlockState, IBitBrush> writeStateToBitMapToNBT(ItemStack bitStack, String key, HashMap<IBlockState, IBitBrush> stateToBitMap)
	{
		if (bitStack.hasTagCompound())
		{
			NBTTagCompound nbtEntry;
			NBTTagList nbtList = new NBTTagList();
			for (IBlockState state : stateToBitMap.keySet())
			{
				nbtEntry = new NBTTagCompound();
				writeStateToNBT(nbtEntry, state);
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
			IBlockState air = Blocks.air.getDefaultState();
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
	
	public static void saveBlockStates(IChiselAndBitsAPI api, EntityPlayer player, World world, AxisAlignedBB box, NBTTagCompound nbt)
	{
		if (world.isRemote)
			return;
		
		NBTTagCompound nbtState;
		NBTTagList nbtList = new NBTTagList();
		int diffX = 16 - (int) (box.maxX - box.minX);
		int diffZ = 16 - (int) (box.maxZ - box.minZ);
		int halfDiffX = diffX / 2;
		int halfDiffZ = diffZ / 2;
		int minX = (int) (box.minX - halfDiffX);
		int maxX = (int) (box.maxX + (diffX - halfDiffX));
		int minZ = (int) (box.minZ - halfDiffZ);
		int maxZ = (int) (box.maxZ + (diffZ - halfDiffZ));
		IBlockState airState = Blocks.air.getDefaultState();
		for (int x = minX; x < maxX; x++)
		{
			for (int y = (int) box.minY; y < box.minY + 16; y++)
			{
				for (int z = minZ; z < maxZ; z++)
				{
					IBlockState state;
					if (y <= box.maxY && x >= box.minX && x <= box.maxX && z >= box.minZ && z <= box.maxZ)
					{
						BlockPos pos = new BlockPos(x, y, z);
						state = world.getBlockState(pos);
						if (api.isBlockChiseled(world, pos))
							state = getPrimaryState(api, world, pos);
					}
					else
					{
						state = airState;
					}
					nbtState = new NBTTagCompound();
					writeStateToNBT(nbtState, state);
					nbtList.appendTag(nbtState);
				}
			}
		}
		nbt.setTag(NBTKeys.SAVED_STATES, nbtList);
		player.inventoryContainer.detectAndSendChanges();
	}
	
	private static IBlockState getPrimaryState(IChiselAndBitsAPI api, World world, BlockPos pos2)
	{
		IBitAccess bitAccess;
		try
		{
			bitAccess = api.getBitAccess(world, pos2);
		}
		catch (CannotBeChiseled e)
		{
			return Blocks.air.getDefaultState();
		}
		HashMap<IBlockState, Integer> stateMap = new HashMap<IBlockState, Integer>();
		for (int i = 0; i < 16; i++)
		{
			for (int j = 0; j < 16; j++)
			{
				for (int k = 0; k < 16; k++)
				{
					IBlockState state = bitAccess.getBitAt(i, j, k).getState();
					if (state == null || state == Blocks.air.getDefaultState())
						continue;
					
					if (!stateMap.containsKey(state))
					{
						stateMap.put(state, 1);
					}
					else
					{
						stateMap.put(state, stateMap.get(state) + 1);
					}
				}
			}
		}
		Integer max = Collections.max(stateMap.values());
		for(Entry<IBlockState, Integer> entry : stateMap.entrySet())
		{
			if (entry.getValue() == max)
				return entry.getKey();
		}
		return Blocks.air.getDefaultState();
	}
	
	public static boolean isAir(IBlockState state)
	{
		return state.equals(Blocks.air.getDefaultState());
	}
	
	public static boolean isAir(Block block)
	{
		return block.equals(Blocks.air);
	}
	
	private static void writeStateToNBT(NBTTagCompound nbtState, IBlockState state)
	{
		writeBlockToNBT(nbtState, state);
		nbtState.setInteger(NBTKeys.STATE_META, state.getBlock().getMetaFromState(state));
	}
	
	public static IBlockState readStateFromNBT(NBTTagCompound nbt)
	{
		Block block = readBlockFromNBT(nbt);
		if (block == null)
			return Blocks.air.getDefaultState();
		
		return block.getStateFromMeta(nbt.getInteger(NBTKeys.STATE_META));
	}
	
	@SuppressWarnings("deprecation")
	public static void writeBlockToNBT(NBTTagCompound nbt, IBlockState state)
	{
		UniqueIdentifier uniqueIdentifier = GameRegistry.findUniqueIdentifierFor(state.getBlock());
		nbt.setString(NBTKeys.BLOCK_MODID, uniqueIdentifier != null ? uniqueIdentifier.modId : "null");
		nbt.setString(NBTKeys.BLOCK_NAME, uniqueIdentifier != null ? uniqueIdentifier.name : "null");
	}
	
	public static Block readBlockFromNBT(NBTTagCompound nbt)
	{
		return GameRegistry.findBlock(nbt.getString(NBTKeys.BLOCK_MODID), nbt.getString(NBTKeys.BLOCK_NAME));
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
	
	@SuppressWarnings("deprecation")
	public static void blockToBytes(ByteBuf buffer, Block block)
	{
		UniqueIdentifier uniqueIdentifier = GameRegistry.findUniqueIdentifierFor(block);
		ByteBufUtils.writeUTF8String(buffer, uniqueIdentifier.modId);
		ByteBufUtils.writeUTF8String(buffer, uniqueIdentifier.name);
	}
	
	public static Block blockFromBytes(ByteBuf buffer)
	{
		return GameRegistry.findBlock(ByteBufUtils.readUTF8String(buffer), ByteBufUtils.readUTF8String(buffer));
	}
	
}