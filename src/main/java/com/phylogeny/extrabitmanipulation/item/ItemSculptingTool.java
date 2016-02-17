package com.phylogeny.extrabitmanipulation.item;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import mod.chiselsandbits.api.APIExceptions.SpaceOccupied;
import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.APIExceptions.CannotBeChiseled;
import mod.chiselsandbits.api.IBitBrush;
import mod.chiselsandbits.api.IBitLocation;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import mod.chiselsandbits.api.ItemType;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.config.ConfigProperty;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;
import com.phylogeny.extrabitmanipulation.reference.Utility;
import com.phylogeny.extrabitmanipulation.shape.AsymmetricalShape;
import com.phylogeny.extrabitmanipulation.shape.Cube;
import com.phylogeny.extrabitmanipulation.shape.Cuboid;
import com.phylogeny.extrabitmanipulation.shape.Ellipsoid;
import com.phylogeny.extrabitmanipulation.shape.Shape;
import com.phylogeny.extrabitmanipulation.shape.Sphere;
import com.phylogeny.extrabitmanipulation.shape.SymmetricalShape;

public class ItemSculptingTool extends ItemBitToolBase
{
	private boolean curved, removeBits;
	
	public ItemSculptingTool(boolean curved, boolean removeBits, String name)
	{
		super(name);
		modeTitles = new String[]{"Local", "Global", "Drawn"};
		this.curved = curved;
		this.removeBits = removeBits;
	}
	
	public boolean isCurved()
	{
		return curved;
	}
	
	public boolean removeBits()
	{
		return removeBits;
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack)
    {
		ConfigProperty config = (ConfigProperty) Configs.itemPropertyMap.get(this);
		return stack.hasTagCompound() && stack.getTagCompound().hasKey("remainingUses")
				&& stack.getTagCompound().getInteger("remainingUses") < config.maxDamage;
    }
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		ConfigProperty config = (ConfigProperty) Configs.itemPropertyMap.get(this);
		int damage = stack.hasTagCompound() ? stack.getTagCompound().getInteger("remainingUses") : 0;
		return 1 - damage / ((double) config.maxDamage);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
		if (!world.isRemote)
		{
			initialize(stack);
			cycleModes(stack, !player.isSneaking());
			player.inventoryContainer.detectAndSendChanges();
		}
        return stack;
    }
	
	@Override
	public boolean initialize(ItemStack stack)
	{
		super.initialize(stack);
		ConfigProperty config = (ConfigProperty) Configs.itemPropertyMap.get(this);
		NBTTagCompound nbt = stack.getTagCompound();
		if (!nbt.hasKey("remainingUses")) nbt.setInteger("remainingUses", config.maxDamage);
		if (!nbt.hasKey(NBTKeys.SCULPT_SEMI_DIAMETER)) nbt.setInteger(NBTKeys.SCULPT_SEMI_DIAMETER, config.defaultRemovalSemiDiameter);
		if (!nbt.hasKey(NBTKeys.WALL_THICKNESS)) nbt.setInteger(NBTKeys.WALL_THICKNESS, 2);
		if (!nbt.hasKey(NBTKeys.SET_BIT))
		{
			try
			{
				Block block = removeBits ? Blocks.air : Blocks.stone;
				ItemStack bitStack = ChiselsAndBitsAPIAccess.apiInstance.getBitItem(block.getDefaultState());
				NBTTagCompound nbt2 = new NBTTagCompound();
				bitStack.writeToNBT(nbt2);
				nbt.setTag(NBTKeys.SET_BIT, nbt2);
			}
			catch (InvalidBitItem e) {}
		}
		return true;
	}
	
	public boolean sculptBlocks(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
			EnumFacing side, Vec3 hit, Vec3 drawnStartPoint)
    {
		initialize(stack);
		IChiselAndBitsAPI api = ChiselsAndBitsAPIAccess.apiInstance;
		if (!removeBits && !wasInsideClicked(side, hit, pos))
		{
			pos = pos.offset(side);
		}
		NBTTagCompound nbt = stack.getTagCompound();
		boolean globalMode = nbt.getInteger(NBTKeys.MODE) == 1;
		if (drawnStartPoint != null || globalMode || isValidBlock(api, world, pos))
		{
			IBitAccess bitAccess = null;
			try
			{
				bitAccess = api.getBitAccess(world, pos);
			}
			catch (CannotBeChiseled e) {}
			IBitLocation bitLoc = api.getBitPos((float) hit.xCoord - pos.getX(), (float) hit.yCoord - pos.getY(),
					(float) hit.zCoord - pos.getZ(), side, pos, false);
			if (bitLoc != null)
			{
				int sculptSemiDiameter = nbt.getInteger(NBTKeys.SCULPT_SEMI_DIAMETER);
				int x = pos.getX();
				int y = pos.getY();
				int z = pos.getZ();
				float x2 = x + bitLoc.getBitX() * Utility.pixelF;
				float y2 = y + bitLoc.getBitY() * Utility.pixelF;
				float z2 = z + bitLoc.getBitZ() * Utility.pixelF;
				if (!removeBits)
				{
					x2 += side.getFrontOffsetX() * Utility.pixelF;
					y2 += side.getFrontOffsetY() * Utility.pixelF;
					z2 += side.getFrontOffsetZ() * Utility.pixelF;
				}
				Shape shape;
				AxisAlignedBB box;
				//Add controls to set shape solid/hollow and thickness of shape hollow walls
				boolean isSolid = !nbt.getBoolean(NBTKeys.SCULPT_HOLLOW_SHAPE);//TODO
				int wallThickness = nbt.getInteger(NBTKeys.WALL_THICKNESS);//TODO
				if (drawnStartPoint != null)
				{
					shape = curved ? new Ellipsoid() : new Cuboid();
					float x3 = (float) drawnStartPoint.xCoord;
					float y3 = (float) drawnStartPoint.yCoord;
					float z3 = (float) drawnStartPoint.zCoord;
					float minX = addPaddingToMin(x2, x3);
					float minY = addPaddingToMin(y2, y3);
					float minZ = addPaddingToMin(z2, z3);
					float maxX = addPaddingToMax(x2, x3);
					float maxY = addPaddingToMax(y2, y3);
					float maxZ = addPaddingToMax(z2, z3);
					box = new AxisAlignedBB(Math.floor(minX), Math.floor(minY), Math.floor(minZ),
							Math.ceil(maxX), Math.ceil(maxY), Math.ceil(maxZ));
					float f = 0.5F;
					minX *= f;
					minY *= f;
					minZ *= f;
					maxX *= f;
					maxY *= f;
					maxZ *= f;
					((AsymmetricalShape) shape).init(maxX + minX, maxY + minY, maxZ + minZ, maxX - minX, maxY - minY, maxZ - minZ, wallThickness, isSolid);
				}
				else
				{
					shape = curved ? new Sphere() : new Cube();
					int blockSemiDiameter = globalMode ? (int) Math.ceil(sculptSemiDiameter / 16.0) : 0;
					box = new AxisAlignedBB(x - blockSemiDiameter, y - blockSemiDiameter, z - blockSemiDiameter,
							x + blockSemiDiameter, y + blockSemiDiameter, z + blockSemiDiameter);
					((SymmetricalShape) shape).init(x2, y2, z2, addPadding(sculptSemiDiameter), wallThickness, isSolid);
				}
				boolean creativeMode = player.capabilities.isCreativeMode;
				HashMap<IBlockState, Integer> bitTypes = null;
				if (removeBits && !world.isRemote && !creativeMode) bitTypes = new HashMap<IBlockState, Integer>();
				int initialpossibleUses = Integer.MAX_VALUE;
				ItemStack setBitStack = null;
				IBitBrush setBit = null;
				setBitStack = ItemStack.loadItemStackFromNBT((NBTTagCompound) stack.getTagCompound().getTag(NBTKeys.SET_BIT));
				try
				{
					setBit = api.createBrush(setBitStack);
					if (!removeBits && !creativeMode)
					{
						initialpossibleUses = countInventoryBits(api, player, setBitStack);
					}
				}
				catch (InvalidBitItem e) {}
				int remainingUses = nbt.getInteger("remainingUses");
				if (!creativeMode && initialpossibleUses > remainingUses) initialpossibleUses = remainingUses;
				int possibleUses = initialpossibleUses;
				
				for (int i = (int) box.minX; i <= box.maxX; i++)
				{
					for (int j = (int) box.minY; j <= box.maxY; j++)
					{
						for (int k = (int) box.minZ; k <= box.maxZ; k++)
						{
							if (possibleUses > 0)
							{
								possibleUses = sculptBlock(api, stack, player, world, new BlockPos(i, j, k), shape, bitTypes,
										possibleUses, Configs.DROP_BITS_PER_BLOCK, setBit);
							}
						}
					}
				}
				if (!Configs.DROP_BITS_PER_BLOCK)
				{
					giveOrDropStacks(player, world, pos, shape, api, bitAccess, bitTypes);
				}
				int change = initialpossibleUses - possibleUses;
				ConfigProperty config = (ConfigProperty) Configs.itemPropertyMap.get(this);
				int newRemainingUses = remainingUses - (config.takesDamage ? change : 0);
				if (!world.isRemote && !creativeMode)
				{
					nbt.setInteger("remainingUses", newRemainingUses);
					if (!removeBits)
					{
						removeInventoryBits(api, player, setBitStack, change);
					}
					if (newRemainingUses <= 0)
					{
						player.renderBrokenItemStack(stack);
						player.destroyCurrentEquippedItem();
					}
					player.inventoryContainer.detectAndSendChanges();
				}
				if (!creativeMode && newRemainingUses <= 0)
				{
					player.renderBrokenItemStack(stack);
				}
				boolean changed = possibleUses < initialpossibleUses;
				if (changed)
				{
					SoundType sound = Blocks.stone.stepSound;
					world.playSoundEffect((double)((float)pos.getX() + 0.5F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.5F),
							removeBits ? sound.getBreakSound() : sound.getPlaceSound(), (sound.getVolume()) / 8.0F, sound.getFrequency() * 0.8F);
				}
				return changed;
			}
		}
		return false;
    }

	private float addPadding(float value)
	{
		return (value + Configs.SEMI_DIAMETER_PADDING) * Utility.pixelF;
	}
	
	private float addPaddingToMin(float value1, float value2)
	{
		return Math.min(value1, value2) - Configs.SEMI_DIAMETER_PADDING * Utility.pixelF;
	}
	
	private float addPaddingToMax(float value1, float value2)
	{
		return Math.max(value1, value2) + Configs.SEMI_DIAMETER_PADDING * Utility.pixelF;
	}
	
	private int countInventoryBits(IChiselAndBitsAPI api, EntityPlayer player, ItemStack setBitStack)
	{
		int count = 0;
		InventoryPlayer inventoy = player.inventory;
		for (int i = 0; i < inventoy.getSizeInventory(); i++)
		{
			ItemStack stack = inventoy.getStackInSlot(i);
			if (stack != null && api.getItemType(stack) == ItemType.CHISLED_BIT
					&& ItemStack.areItemStackTagsEqual(stack, setBitStack))
			{
				count += stack.stackSize;
			}
		}
		return count;
	}
	
	private void removeInventoryBits(IChiselAndBitsAPI api, EntityPlayer player, ItemStack setBitStack, int quota)
	{
		if (quota > 0)
		{
			InventoryPlayer inventoy = player.inventory;
			for (int i = 0; i < inventoy.getSizeInventory(); i++)
			{
				ItemStack stack = inventoy.getStackInSlot(i);
				if (stack != null && api.getItemType(stack) == ItemType.CHISLED_BIT
						&& ItemStack.areItemStackTagsEqual(stack, setBitStack))
				{
					int size = stack.stackSize;
					if (size > quota)
					{
						stack.stackSize = size - quota;
						quota = 0;
					}
					else
					{
						inventoy.setInventorySlotContents(i, null);
						quota -= size;
					}
					if (quota <= 0) break;
				}
			}
		}
	}

	public static boolean wasInsideClicked(EnumFacing dir, Vec3 hit, BlockPos pos)
	{
		if (hit != null)
		{
			switch (dir.ordinal())
			{
				case 0:	return hit.yCoord > pos.getY();
				case 1:	return hit.yCoord < pos.getY() + 1;
				case 2:	return hit.zCoord > pos.getZ();
				case 3:	return hit.zCoord < pos.getZ() + 1;
				case 4:	return hit.xCoord > pos.getX();
				case 5:	return hit.xCoord < pos.getX() + 1;
			}
		}
		return false;
	}
	
	private int sculptBlock(IChiselAndBitsAPI api, ItemStack stack, EntityPlayer player, World world, BlockPos pos, Shape shape,
			HashMap<IBlockState, Integer> bitTypes, int remainingUses, boolean dropsPerBlock, IBitBrush setBit)
    {
		if (isValidBlock(api, world, pos))
		{
			IBitAccess bitAccess;
			try
			{
				bitAccess = api.getBitAccess(world, pos);
			}
			catch (CannotBeChiseled e)
			{
				e.printStackTrace();
				return remainingUses;
			}
			boolean byPassBitChecks = shape.isBlockInsideShape(pos);
			for (int i = 0; i < 16; i++)
			{
				for (int j = 0; j < 16; j++)
				{
					for (int k = 0; k < 16; k++)
					{
						IBitBrush bit = bitAccess.getBitAt(i, j, k);
						if ((removeBits ? (!bit.isAir() && !(setBit != null && !setBit.isAir() && !setBit.getState().equals(bit.getState()))) : bit.isAir())
								&& (byPassBitChecks || shape.isPointInsideShape(pos, i, j, k)))
						{
							if (bitTypes != null)
					    	{
								IBlockState state = bit.getState();
					    		if (!bitTypes.containsKey(state))
								{
									bitTypes.put(state, 1);
								}
								else
								{
									bitTypes.put(state, bitTypes.get(state) + 1);
								}
					    	}
							try
							{
								bitAccess.setBitAt(i, j, k, removeBits ? null : setBit);
								remainingUses--;
							}
							catch (SpaceOccupied e) {}
							if (remainingUses == 0)
							{
								bitAccess.commitChanges();
								return remainingUses;
							}
						}
					}
				}
			}
			if (dropsPerBlock)
			{
				giveOrDropStacks(player, world, pos, shape, api, bitAccess, bitTypes);
			}
			bitAccess.commitChanges();
		}
		return remainingUses;
    }
	
	private boolean isValidBlock(IChiselAndBitsAPI api, World world, BlockPos pos)
	{
		return api.canBeChiseled(world, pos) && (!removeBits || !world.isAirBlock(pos));
	}

	private void giveOrDropStacks(EntityPlayer player, World world, BlockPos pos, Shape shape, IChiselAndBitsAPI api,
			IBitAccess bitAccess, HashMap<IBlockState, Integer> bitTypes)
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
					InventoryPlayer inv = player.inventory;
					int totalBits = bitTypes.get(state);
					if (Configs.DROP_BITS_AS_FULL_CHISELED_BLOCKS && totalBits >= 4096 && bitAccess != null)
					{
						setAllBits(bitAccess, bit);
						int blockCount = totalBits / 4096;
						totalBits -= blockCount * 4096;
						while (blockCount > 0)
						{
							int stackSize = blockCount > 64 ? 64 : blockCount;
							ItemStack stack2 = bitAccess.getBitsAsItem(null, ItemType.CHISLED_BLOCK);
							if (stack2 != null)
							{
								stack2.stackSize = stackSize;
								givePlayerStackOrDropOnGround(player, world, pos, shape, stack2);
							}
							blockCount -= stackSize;
						}
						setAllBits(bitAccess, null);
					}
					int quota;
					while (totalBits > 0)
					{
						quota = totalBits > 64 ? 64 : totalBits;
						ItemStack bitStack2 = bit.getItemStack(quota);
						givePlayerStackOrDropOnGround(player, world, pos, shape, bitStack2);
						totalBits -= quota;
					}
				}
			}
			bitTypes.clear();
			if (Configs.PLACE_BITS_IN_INVENTORY) player.inventoryContainer.detectAndSendChanges();
		}
	}

	private void givePlayerStackOrDropOnGround(EntityPlayer player, World world, BlockPos pos, Shape shape, ItemStack stack)
	{
		if (Configs.PLACE_BITS_IN_INVENTORY)
		{
			player.inventory.addItemStackToInventory(stack);
		}
		if (stack.stackSize > 0)
		{
			if (Configs.DROP_BITS_IN_BLOCKSPACE)
			{
				spawnStacksInShape(world, pos, shape, stack);
			}
			else
			{
				player.dropItem(stack, false, false);
			}
		}
	}
	
	private void spawnStacksInShape(World world, BlockPos pos, Shape shape, ItemStack stack)
	{
		if (!world.isRemote && world.getGameRules().getBoolean("doTileDrops") && !world.restoringBlockSnapshots)
        {
			Vec3 spawnPoint = shape.getRandomInternalPoint(world, pos);
			EntityItem entityitem = new EntityItem(world, spawnPoint.xCoord, spawnPoint.yCoord - 0.25, spawnPoint.zCoord, stack);
            entityitem.setDefaultPickupDelay();
            world.spawnEntityInWorld(entityitem);
        }
	}

	private void setAllBits(IBitAccess bitAccess, IBitBrush bit)
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
	
	public void onCreated(ItemStack stack, World world, EntityPlayer player)
    {
		super.onCreated(stack, world, player);
		ConfigProperty config = (ConfigProperty) Configs.itemPropertyMap.get(this);
		stack.getTagCompound().setInteger(NBTKeys.SCULPT_SEMI_DIAMETER, config.defaultRemovalSemiDiameter);
    }
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced)
	{
		int mode = stack.hasTagCompound() ? stack.getTagCompound().getInteger(NBTKeys.MODE) : 0;
		ItemStack setBitStack = null;
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBTKeys.SET_BIT))
		{
			setBitStack = ItemStack.loadItemStackFromNBT((NBTTagCompound) stack.getTagCompound().getTag(NBTKeys.SET_BIT));
		}
		else if (!removeBits)
		{
			try
			{
				setBitStack = ChiselsAndBitsAPIAccess.apiInstance.getBitItem(Blocks.stone.getDefaultState());
			}
			catch (InvalidBitItem e) {}
		}
		String bitType = "Bit Type To " + (removeBits ? "Remove" : "Add") + ": ";
		if (setBitStack != null)
		{
			String stackName = setBitStack.getDisplayName();
			bitType += (stackName.length() == 12 ? "Any" : stackName.substring(15));
		}
		else
		{
			bitType += "Any";
		}
		tooltip.add(bitType);
		if (GuiScreen.isShiftKeyDown())
		{
			if (!removeBits)
			{
				tooltip.add("Left click while sneaking to set bit type.");
			}
			if (mode == 2)
			{
				tooltip.add("Left click point on block, drag to");
				tooltip.add("    another point, then release");
				tooltip.add("    to remove " + (curved ? "spherical" : "cubic") + " area of bits");
				tooltip.add("    from all intersecting blocks.");
			}
			else
			{
				tooltip.add("Left click block to remove " + (curved ? "spherical " : "cubic "));
				String text = "area of bits from ";
				if (mode == 0)
				{
					tooltip.add("    " + text + "it.");
				}
				else
				{
					tooltip.add("    " + text + "all intersecting");
					tooltip.add("    blocks.");
				}
			}
			tooltip.add("Right click to cycle modes.");
			tooltip.add("Mouse wheel while sneaking to change");
			tooltip.add("    " + (removeBits ? "removal" : "addition") + " diameter.");
		}
		else
		{
			tooltip.add("Hold SHIFT for info.");
		}
	}
	
}