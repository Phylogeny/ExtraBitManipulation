package com.phylogeny.extrabitmanipulation.helper;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;

import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

import mod.chiselsandbits.api.IBitBrush;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import mod.chiselsandbits.api.IMultiStateBlock;
import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

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
					nbtState = new NBTTagCompound();
					writeStateToNBT(nbtState, state);
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
	
	private static void writeStateToNBT(NBTTagCompound nbtState, IBlockState state)
	{
		writeBlockToNBT(nbtState, state);
		nbtState.setInteger(NBTKeys.STATE_META, state.getBlock().getMetaFromState(state));
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
	
}