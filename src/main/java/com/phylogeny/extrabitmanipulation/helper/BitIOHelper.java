package com.phylogeny.extrabitmanipulation.helper;

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import mod.chiselsandbits.api.IBitBrush;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import mod.chiselsandbits.api.IMultiStateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class BitIOHelper
{
	
	public static void writeStateToBitMapToNBT(ItemStack bitStack, String key, HashMap<IBlockState, IBitBrush> stateToBitMap, boolean saveStatesById)
	{
		if (!bitStack.hasTagCompound())
			return;
		
		int counter = 0;
		int n = stateToBitMap.size();
		NBTTagCompound nbt = bitStack.getTagCompound();
		if (saveStatesById)
		{
			int[] mapArray = new int[n * 2];
			for (Entry<IBlockState, IBitBrush> entry : stateToBitMap.entrySet())
			{
				mapArray[counter++] = Block.getStateId(entry.getKey());
				mapArray[counter++] = entry.getValue().getStateID();
			}
			writeObjectToNBT(nbt, key + 0, mapArray);
			nbt.removeTag(key + 1);
			nbt.removeTag(key + 2);
			nbt.removeTag(key + 3);
		}
		else
		{
			boolean isBlockMap = key.equals(NBTKeys.BLOCK_TO_BIT_MAP_PERMANENT);
			String[] domainArray = new String[n * 2];
			String[] pathArray = new String[n * 2];
			byte[] metaArray = isBlockMap ? null : new byte[n * 2];
			for (Entry<IBlockState, IBitBrush> entry : stateToBitMap.entrySet())
			{
				saveStateToMapArrays(domainArray, pathArray, metaArray, counter++, entry.getKey());
				saveStateToMapArrays(domainArray, pathArray, metaArray, counter++, Block.getStateById(entry.getValue().getStateID()));
			}
			nbt.removeTag(key + 0);
			writeObjectToNBT(nbt, key + 1, domainArray);
			writeObjectToNBT(nbt, key + 2, pathArray);
			if (!isBlockMap)
				writeObjectToNBT(nbt, key + 3, metaArray);
		}
	}
	
	private static void saveStateToMapArrays(String[] domainArray, String[] pathArray, byte[] metaArray, int index, IBlockState state)
	{
		ResourceLocation regName = state.getBlock().getRegistryName();
		domainArray[index] = regName.getResourceDomain();
		pathArray[index] = regName.getResourcePath();
		if (metaArray != null)
			metaArray[index] = (byte) state.getBlock().getMetaFromState(state);
	}
	
	public static HashMap<IBlockState, IBitBrush> readStateToBitMapFromNBT(IChiselAndBitsAPI api, ItemStack bitStack, String key)
	{
		HashMap<IBlockState, IBitBrush> stateToBitMap = new HashMap<IBlockState, IBitBrush>();
		if (!bitStack.hasTagCompound())
			return stateToBitMap;
		
		NBTTagCompound nbt = bitStack.getTagCompound();
		boolean saveStatesById = !nbt.hasKey(key + 2);
		boolean isBlockMap = key.equals(NBTKeys.BLOCK_TO_BIT_MAP_PERMANENT);
		if (saveStatesById ? !nbt.hasKey(key + 0) : !nbt.hasKey(key + 1) || (!isBlockMap && !nbt.hasKey(key + 3)))
			return stateToBitMap;
		
		if (saveStatesById)
		{
			int[] mapArray = (int[]) readArrayFromNBT(nbt, key + 0);
			if (mapArray == null)
				return stateToBitMap;
			
			for (int i = 0; i < mapArray.length; i += 2)
			{
				IBlockState state = Block.getStateById(mapArray[i]);
				if (!isAir(state))
				{
					try
					{
						stateToBitMap.put(state, api.createBrushFromState(Block.getStateById(mapArray[i + 1])));
					}
					catch (InvalidBitItem e) {}
				}
			}
		}
		else
		{
			String[] domainArray = (String[]) readArrayFromNBT(nbt, key + 1);
			String[] pathArray = (String[]) readArrayFromNBT(nbt, key + 2);
			byte[] metaArray = isBlockMap ? null : (byte[]) readArrayFromNBT(nbt, key + 3);
			if (domainArray == null || pathArray == null || (!isBlockMap && metaArray == null))
				return stateToBitMap;
			
			for (int i = 0; i < domainArray.length; i += 2)
			{
				IBlockState state = readStateFromMapArrays(domainArray, pathArray, metaArray, i);
				if (!isAir(state))
				{
					try
					{
						stateToBitMap.put(state, api.createBrushFromState(readStateFromMapArrays(domainArray, pathArray, metaArray, i + 1)));
					}
					catch (InvalidBitItem e) {}
				}
			}
		}
		return stateToBitMap;
	}
	
	private static IBlockState readStateFromMapArrays(String[] domainArray, String[] pathArray, byte[] metaArray, int index)
	{
		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(domainArray[index], pathArray[index]));
		return block == null ? Blocks.AIR.getDefaultState() : (metaArray != null ? block.getStateFromMeta(metaArray[index]) : block.getDefaultState());
	}
	
	private static byte[] compressObject(Object object) throws IOException
	{
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutputStream objectStream = new ObjectOutputStream(new DeflaterOutputStream(byteStream));
		objectStream.writeObject(object);
		objectStream.close();
		return byteStream.toByteArray();
	}
	
	private static Object decompressObject(byte[] bytes) throws IOException, ClassNotFoundException
	{
		return (new ObjectInputStream(new InflaterInputStream(new ByteArrayInputStream(bytes)))).readObject();
	}
	
	private static void writeObjectToNBT(NBTTagCompound nbt, String key, Object object)
	{
		try
		{
			nbt.setByteArray(key, compressObject(object));
		}
		catch (IOException e) {}
	}
	
	private static Object readArrayFromNBT(NBTTagCompound nbt, String key)
	{
		try
		{
			return decompressObject(nbt.getByteArray(key));
		}
		catch (ClassNotFoundException e) {}
		catch (IOException e) {}
		return null;
	}
	
	public static void readStatesFromNBT(NBTTagCompound nbt, HashMap<IBlockState, Integer> stateMap, IBlockState[][][] stateArray)
	{
		String key = NBTKeys.SAVED_STATES;
		int[] stateIDs = (int[]) readArrayFromNBT(nbt, key);
		if (stateIDs == null)
			stateIDs = new int[4096];
		
		for (int n = 0; n < stateIDs.length; n++)
		{
			int i = n / 256;
			int n2 = n % 256;
			int j = n2 / 16;
			int k = n2 % 16;
			IBlockState state = Block.getStateById(stateIDs[n]);
			stateArray[i][j][k] = state;
			if (!isAir(state))
				stateMap.put(state, 1 + (stateMap.containsKey(state) ? stateMap.get(state) : 0));
		}
		if (stateIDs.length == 0)
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
	
	public static void saveBlockStates(IChiselAndBitsAPI api, EntityPlayer player, World world, AxisAlignedBB box, NBTTagCompound nbt)
	{
		if (world.isRemote)
			return;
		
		int[] stateIDs = new int[4096];
		int index = 0;
		int diffX = 16 - (int) (box.maxX - box.minX);
		int diffZ = 16 - (int) (box.maxZ - box.minZ);
		int halfDiffX = diffX / 2;
		int halfDiffZ = diffZ / 2;
		int minX = (int) (box.minX - halfDiffX);
		int maxX = (int) (box.maxX + (diffX - halfDiffX));
		int minZ = (int) (box.minZ - halfDiffZ);
		int maxZ = (int) (box.maxZ + (diffZ - halfDiffZ));
		IBlockState airState = Blocks.AIR.getDefaultState();
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
						if (api.isBlockChiseled(world, pos) && state.getBlock() instanceof IMultiStateBlock)
							state = ((IMultiStateBlock) state.getBlock()).getPrimaryState(world, pos);
					}
					else
					{
						state = airState;
					}
					stateIDs[index++] = Block.getStateId(state);
				}
			}
		}
		String key = NBTKeys.SAVED_STATES;
		writeObjectToNBT(nbt, key, stateIDs);
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
	
	public static void stateToBytes(ByteBuf buffer, IBlockState state)
	{
		buffer.writeInt(Block.getStateId(state));
	}
	
	public static IBlockState stateFromBytes(ByteBuf buffer)
	{
		return Block.getStateById(buffer.readInt());
	}
	
}