package com.phylogeny.extrabitmanipulation.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.config.ConfigReplacementBits;
import com.phylogeny.extrabitmanipulation.helper.BitHelper;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.GuiIDs;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;
import com.sun.xml.internal.ws.util.StringUtils;

import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import mod.chiselsandbits.api.APIExceptions.SpaceOccupied;
import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.IBitBrush;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import mod.chiselsandbits.api.APIExceptions.CannotBeChiseled;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class ItemModelMaker extends ItemBitToolBase
{
	public ItemModelMaker(String name)
	{
		super(name);
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return slotChanged;
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
			EnumFacing side, float hitX, float hitY, float hitZ)
	{
		initialize(stack);
		boolean read = player.isSneaking();
		if (!world.getBlockState(pos).getBlock().isReplaceable(world, pos))
		{
			pos = pos.offset(side);
			if (!read && !world.isAirBlock(pos))
				return false;
		}
		else if (!read)
		{
			world.setBlockToAir(pos);
		}
		IChiselAndBitsAPI api = ChiselsAndBitsAPIAccess.apiInstance;
		NBTTagCompound nbt = stack.getTagCompound();
		if (read)
		{
			BitHelper.saveBlockStates(api, player, world, pos, nbt);
			if (read)
				player.openGui(ExtraBitManipulation.instance, GuiIDs.MODEL_MAKER_BIT_MAPPING, player.worldObj, 0, 0, 0);
		}
		else
		{
			if (!nbt.hasKey(NBTKeys.SAVED_STATES))
				return false;
			
			HashMap<IBlockState, Integer> stateMap = new HashMap<IBlockState, Integer>();
			IBlockState[][][] stateArray = new IBlockState[16][16][16];
			BitHelper.readStatesFromNBT(nbt, stateMap, stateArray);
			HashMap<IBlockState, ArrayList<BitCount>> stateToBitCountArray = new HashMap<IBlockState, ArrayList<BitCount>>();
			HashMap<IBitBrush, Integer> bitMap = new HashMap<IBitBrush, Integer>();
			HashMap<IBlockState, Integer> missingBitMap = mapBitsToStates(api, BitHelper.getInventoryBitCounts(api, player), stateMap,
					stateToBitCountArray, BitHelper.readStateToBitMapFromNBT(api, stack, NBTKeys.STATE_TO_BIT_MAP_PERMANENT),
					BitHelper.readStateToBitMapFromNBT(api, stack, NBTKeys.BLOCK_TO_BIT_MAP_PERMANENT), bitMap, player.capabilities.isCreativeMode);
			if (!missingBitMap.isEmpty())
			{
				if (world.isRemote)
				{
					int missingBitCount = 0;
					for (IBlockState state : missingBitMap.keySet())
					{
						missingBitCount += missingBitMap.get(state);
					}
					addChatMessage(player, "Missing " + missingBitCount + " bits to represent the following blocks:");
					for (IBlockState state : missingBitMap.keySet())
					{
						String name = getBlockName(state, new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)));
						addChatMessage(player, "  " + missingBitMap.get(state) + " - " + name);
					}
				}
				return false;
			}
			return createModel(player, world, pos, stack, api, stateArray, stateToBitCountArray, bitMap);
		}
		return true;
	}
	
	private boolean createModel(EntityPlayer player, World world, BlockPos pos, ItemStack stack, IChiselAndBitsAPI api, IBlockState[][][] stateArray,
			HashMap<IBlockState, ArrayList<BitCount>> stateToBitCountArray, HashMap<IBitBrush, Integer> bitMap)
	{
		IBitAccess bitAccess;
		try
		{
			bitAccess = api.getBitAccess(world, pos);
		}
		catch (CannotBeChiseled e)
		{
			e.printStackTrace();
			return false;
		}
		try
		{
			api.beginUndoGroup(player);
			if (!createModel(player, world, stack, stateArray, stateToBitCountArray, bitAccess))
				return false;
			
			bitAccess.commitChanges(true);
		}
		finally
		{
			api.endUndoGroup(player);
		}
		if (!world.isRemote && !player.capabilities.isCreativeMode)
		{
			for (IBitBrush bit : bitMap.keySet())
			{
				BitHelper.removeOrAddInventoryBits(api, player, bit.getItemStack(1), bitMap.get(bit).intValue(), false);
				player.inventoryContainer.detectAndSendChanges();
			}
		}
		return true;
	}
	
	public boolean createModel(EntityPlayer player, World world, ItemStack stack, IBlockState[][][] stateArray,
			HashMap<IBlockState, ArrayList<BitCount>> stateToBitCountArray, IBitAccess bitAccess)
	{
		if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey(NBTKeys.SAVED_STATES))
			return false;
		
		for (int i = 0; i < 16; i++)
		{
			for (int j = 0; j < 16; j++)
			{
				for (int k = 0; k < 16; k++)
				{
					try
					{
						IBitBrush bit = null;
						IBlockState state = stateArray[i][j][k];
						if (!state.equals(Blocks.air.getDefaultState()))
						{
							for (BitCount bitCount : stateToBitCountArray.get(state))
							{
								if (bitCount.count > 0)
								{
									bitCount.count--;
									bit = bitCount.bit;
									break;
								}
							}
						}
						bitAccess.setBitAt(i, j, k, bit);
					}
					catch (SpaceOccupied e)
					{
						if (world != null && world.isRemote)
							addChatMessage(player, "Multipart(s) are in the way.");
						
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public HashMap<IBlockState, Integer> mapBitsToStates(IChiselAndBitsAPI api, LinkedHashMap<Integer, Integer> inventoryBitCounts,
			HashMap<IBlockState, Integer> stateMap, HashMap<IBlockState, ArrayList<BitCount>> stateToBitCountArray,
			HashMap<IBlockState, IBitBrush> manualStateToBitMap, HashMap<IBlockState, IBitBrush> manualBlockToBitMap,
			HashMap<IBitBrush, Integer> bitMap, boolean isCreative)
	{
		HashMap<IBlockState, Integer> missingBitMap = new HashMap<IBlockState, Integer>();
		HashMap<IBlockState, Integer> skippedStatesMap = new HashMap<IBlockState, Integer>();
		HashMap<IBlockState, ArrayList<BitCount>> skippedBitCountArrayMap = new HashMap<IBlockState, ArrayList<BitCount>>();
		for (int pass = 0; pass < 2; pass++)
		{
			for (IBlockState state : stateMap.keySet())
			{
				if (pass == 1 && !skippedStatesMap.containsKey(state))
					continue;
				
				int bitCount = stateMap.get(state);
				ArrayList<BitCount> bitCountArray = pass == 1 ? skippedBitCountArrayMap.get(state) : new ArrayList<BitCount>();
				int remainingBitCount = pass == 1 ? skippedStatesMap.get(state) : 0;
				try
				{
					if (pass == 0)
						remainingBitCount = addBitCountObject(bitCountArray, bitMap, inventoryBitCounts, manualStateToBitMap.containsKey(state)
								? manualStateToBitMap.get(state) : (manualBlockToBitMap.containsKey(state.getBlock().getDefaultState())
												? manualBlockToBitMap.get(state.getBlock().getDefaultState()) : api.createBrushFromState(state)),
												bitCount, isCreative);
					if (remainingBitCount > 0)
					{
						remainingBitCount = getReplacementBit(api, bitMap, inventoryBitCounts, bitCountArray, false, remainingBitCount, isCreative, pass);
						if (remainingBitCount < 0)
						{
							skippedStatesMap.put(state, remainingBitCount * -1);
							skippedBitCountArrayMap.put(state, bitCountArray);
						}
					}
				}
				catch (InvalidBitItem e)
				{
					remainingBitCount = getReplacementBit(api, bitMap, inventoryBitCounts, bitCountArray, true, bitCount, isCreative, pass);
					if (remainingBitCount < 0)
					{
						skippedStatesMap.put(state, remainingBitCount * -1);
						skippedBitCountArrayMap.put(state, bitCountArray);
					}
				}
				stateToBitCountArray.put(state, bitCountArray);
				if (remainingBitCount > 0 && (pass == 1 || !skippedStatesMap.containsKey(state)))
					missingBitMap.put(state, remainingBitCount);
			}
			if (skippedStatesMap.isEmpty())
				break;
		}
		return missingBitMap;
	}
	
	private int getReplacementBit(IChiselAndBitsAPI api, HashMap<IBitBrush, Integer> bitMap, LinkedHashMap<Integer, Integer> inventoryBitCounts,
			ArrayList<BitCount> bitCountArray, boolean unchiselable, int remainingBitCount, boolean isCreative, int pass)
	{
		ConfigReplacementBits replacementBitsConfig = unchiselable ? Configs.replacementBitsUnchiselable : Configs.replacementBitsInsufficient;
		if (pass == 0 && replacementBitsConfig.useDefaultReplacementBit)
		{
			try
			{
				remainingBitCount = addBitCountObject(bitCountArray, bitMap, inventoryBitCounts,
						api.createBrush(replacementBitsConfig.defaultReplacementBit.getDefaultValue()), remainingBitCount, isCreative);
			}
			catch (InvalidBitItem e) {}
		}
		if (remainingBitCount > 0 && replacementBitsConfig.useAnyBitsAsReplacements)
		{
			if (pass == 0)
				return -remainingBitCount;
			
			try
			{
				for (Integer stateID : inventoryBitCounts.keySet())
				{
					remainingBitCount = addBitCountObject(bitCountArray, bitMap, inventoryBitCounts,
							api.createBrushFromState(Block.getStateById(stateID)), remainingBitCount, isCreative);
					if (remainingBitCount == 0)
						break;
				}
			}
			catch (InvalidBitItem e) {}
		}
		if (remainingBitCount > 0 && (replacementBitsConfig.useAirAsReplacement))
		{
			try
			{
				remainingBitCount = addBitCountObject(bitCountArray, bitMap, inventoryBitCounts,
						api.createBrush(null), remainingBitCount, isCreative);
			}
			catch (InvalidBitItem e) {}
		}
		return remainingBitCount;
	}
	
	private int addBitCountObject(ArrayList<BitCount> bitCountArray, HashMap<IBitBrush, Integer> bitMap,
			LinkedHashMap<Integer, Integer> inventoryBitCounts, IBitBrush bit, int bitCount, boolean isCreative)
	{
		if (bit.isAir())
		{
			bitCountArray.add(new BitCount(bit, bitCount));
			return 0;
		}
		boolean hasBitSurvival = inventoryBitCounts.containsKey(bit.getStateID()) && !isCreative;
		int inventoryBitCount = isCreative ? Integer.MAX_VALUE : (hasBitSurvival ? inventoryBitCounts.get(bit.getStateID()) : 0);
		if (inventoryBitCount > 0)
		{
			int bitCount2 = Math.min(inventoryBitCount, bitCount);
			bitCountArray.add(new BitCount(bit, bitCount2));
			bitCount -= bitCount2;
			bitMap.put(bit, bitCount2 + (bitMap.containsKey(bit) ? bitMap.get(bit) : 0));
			if (hasBitSurvival)
				inventoryBitCounts.put(bit.getStateID(), inventoryBitCount - bitCount2);
		}
		return bitCount;
	}
	
	private void addChatMessage(EntityPlayer player, String message)
	{
		player.addChatMessage(new ChatComponentText(message));
	}
	
	private String getBlockName(IBlockState state, ItemStack blockStack)
	{
		String name = state.getBlock().getUnlocalizedName();
		if (blockStack.getItem() != null)
		{
			name = blockStack.getDisplayName();
		}
		else if (state.getBlock().getMaterial().isLiquid())
		{
			Fluid fluid = FluidRegistry.lookupFluidForBlock(state.getBlock());
			if (fluid != null)
				name = StringUtils.capitalize(fluid.getName());
		}
		else
		{
			Item item = Item.getItemFromBlock(state.getBlock());
			if (item != null)
				name = item.toString();
		}
		return name;
	}
	
	public static class BitCount
	{
		private IBitBrush bit;
		private int count;
		
		public BitCount(IBitBrush bit, int count)
		{
			this.bit = bit;
			this.count = count;
		}
		
		public IBitBrush getBit()
		{
			return bit;
		}

		public void setBit(IBitBrush bit)
		{
			this.bit = bit;
		}

		public int getCount()
		{
			return count;
		}
		
	}
	
}