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
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.config.ConfigProperty;
import com.phylogeny.extrabitmanipulation.config.ConfigSculptSettingBase;
import com.phylogeny.extrabitmanipulation.helper.SculptSettingsHelper;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;
import com.phylogeny.extrabitmanipulation.reference.Utility;
import com.phylogeny.extrabitmanipulation.shape.AsymmetricalShape;
import com.phylogeny.extrabitmanipulation.shape.Cone;
import com.phylogeny.extrabitmanipulation.shape.ConeElliptic;
import com.phylogeny.extrabitmanipulation.shape.Cube;
import com.phylogeny.extrabitmanipulation.shape.Cuboid;
import com.phylogeny.extrabitmanipulation.shape.Cylinder;
import com.phylogeny.extrabitmanipulation.shape.CylinderElliptic;
import com.phylogeny.extrabitmanipulation.shape.Ellipsoid;
import com.phylogeny.extrabitmanipulation.shape.PyramidRectangular;
import com.phylogeny.extrabitmanipulation.shape.Shape;
import com.phylogeny.extrabitmanipulation.shape.Sphere;
import com.phylogeny.extrabitmanipulation.shape.PyramidSquare;
import com.phylogeny.extrabitmanipulation.shape.SymmetricalShape;

public class ItemSculptingTool extends ItemBitToolBase
{
	public static final String[] MODE_TITLES = new String[]{"Local", "Global", "Drawn"};
	private boolean curved, removeBits;
	
	public ItemSculptingTool(boolean curved, boolean removeBits, String name)
	{
		super(name);
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
		return stack.hasTagCompound() && stack.getTagCompound().hasKey(NBTKeys.REMAINING_USES)
				&& stack.getTagCompound().getInteger(NBTKeys.REMAINING_USES) < config.maxDamage;
    }
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		ConfigProperty config = (ConfigProperty) Configs.itemPropertyMap.get(this);
		int damage = stack.hasTagCompound() ? stack.getTagCompound().getInteger(NBTKeys.REMAINING_USES) : 0;
		return 1 - damage / ((double) config.maxDamage);
	}
	
	@Override
	public boolean initialize(ItemStack stack)
	{
		super.initialize(stack);
		NBTTagCompound nbt = stack.getTagCompound();
		initInt(nbt, NBTKeys.REMAINING_USES, ((ConfigProperty) Configs.itemPropertyMap.get(this)).maxDamage);
		initInt(nbt, NBTKeys.MODE, Configs.sculptMode.getDefaultValue());
		initInt(nbt, NBTKeys.SCULPT_SEMI_DIAMETER, Configs.sculptSemiDiameter.getDefaultValue());
		initInt(nbt, NBTKeys.ROTATION, Configs.sculptRotation.getDefaultValue());
		initBoolean(nbt, NBTKeys.TARGET_BIT_GRID_VERTEXES, Configs.sculptTargetBitGridVertexes.getDefaultValue());
		initInt(nbt, NBTKeys.SHAPE_TYPE, (curved ? Configs.sculptShapeTypeCurved : Configs.sculptShapeTypeFlat).getDefaultValue());
		initBoolean(nbt, NBTKeys.SCULPT_HOLLOW_SHAPE, Configs.sculptHollowShape.getDefaultValue());
		initBoolean(nbt, NBTKeys.OPEN_ENDS, Configs.sculptOpenEnds.getDefaultValue());
		initInt(nbt, NBTKeys.WALL_THICKNESS, Configs.sculptWallThickness.getDefaultValue());
		
		if (!nbt.hasKey(NBTKeys.SET_BIT))
		{
			ItemStack bitStack = (removeBits ? Configs.sculptSetBitWire : Configs.sculptSetBitSpade).getDefaultValue();
			if (bitStack != null)
			{
				NBTTagCompound nbt2 = new NBTTagCompound();
				bitStack.writeToNBT(nbt2);
				nbt.setTag(NBTKeys.SET_BIT, nbt2);
			}
		}
		return true;
	}
	
	private void initInt(NBTTagCompound nbt, String nbtKey, int initInt)
	{
		if (!nbt.hasKey(nbtKey)) nbt.setInteger(nbtKey, initInt);
	}
	
	private void initBoolean(NBTTagCompound nbt, String nbtKey, boolean initBoolean)
	{
		if (!nbt.hasKey(nbtKey)) nbt.setBoolean(nbtKey, initBoolean);
	}
	
	public boolean sculptBlocks(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
			EnumFacing side, Vec3 hit, Vec3 drawnStartPoint)
    {
		initialize(stack);
		IChiselAndBitsAPI api = ChiselsAndBitsAPIAccess.apiInstance;
		boolean inside = wasInsideClicked(side, hit, pos);
		if (!removeBits && !inside)
		{
			pos = pos.offset(side);
		}
		NBTTagCompound nbt = stack.getTagCompound();
		boolean globalMode = SculptSettingsHelper.getMode(player, nbt) == 1;
		if (drawnStartPoint != null || globalMode || isValidBlock(api, world, pos))
		{
			float hitX = (float) hit.xCoord - pos.getX();
			float hitY = (float) hit.yCoord - pos.getY();
			float hitZ = (float) hit.zCoord - pos.getZ();
			IBitLocation bitLoc = api.getBitPos(hitX, hitY, hitZ, side, pos, false);
			if (bitLoc != null)
			{
				int sculptSemiDiameter =  SculptSettingsHelper.getSemiDiameter(player, nbt);
				int x = pos.getX();
				int y = pos.getY();
				int z = pos.getZ();
				float x2 = x + bitLoc.getBitX() * Utility.PIXEL_F;
				float y2 = y + bitLoc.getBitY() * Utility.PIXEL_F;
				float z2 = z + bitLoc.getBitZ() * Utility.PIXEL_F;
				if (!removeBits)
				{
					x2 += side.getFrontOffsetX() * Utility.PIXEL_F;
					y2 += side.getFrontOffsetY() * Utility.PIXEL_F;
					z2 += side.getFrontOffsetZ() * Utility.PIXEL_F;
				}
				Shape shape;
				AxisAlignedBB box;
				int shapeType = SculptSettingsHelper.getShapeType(player, nbt, curved);
				int rotation = SculptSettingsHelper.getRotation(player, nbt);
				boolean sculptHollowShape = SculptSettingsHelper.isHollowShape(player, nbt);
				float wallThickness = SculptSettingsHelper.getWallThickness(player, nbt) * Utility.PIXEL_F;
				boolean openEnds = SculptSettingsHelper.areEndsOpen(player, nbt);
				if (drawnStartPoint != null)
				{
					shape = curved ? (shapeType == 0 ? new Ellipsoid() : (shapeType == 1 ? new CylinderElliptic() : new ConeElliptic()))
							: (shapeType == 6 ? new PyramidRectangular() : new Cuboid());
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
					((AsymmetricalShape) shape).init(maxX + minX, maxY + minY, maxZ + minZ, maxX - minX, maxY - minY, maxZ - minZ,
							rotation, sculptHollowShape, wallThickness, openEnds);
				}
				else
				{
					shape = curved ? (shapeType == 0 ? new Sphere() : (shapeType == 1 ? new Cylinder() : new Cone()))
							: (shapeType == 6 ? new PyramidSquare() : new Cube());
					int blockSemiDiameter = globalMode ? (int) Math.ceil(sculptSemiDiameter / 16.0) : 0;
					box = new AxisAlignedBB(x - blockSemiDiameter, y - blockSemiDiameter, z - blockSemiDiameter,
							x + blockSemiDiameter, y + blockSemiDiameter, z + blockSemiDiameter);
					float f = 0;
					float x3 = 0, y3 = 0, z3 = 0;
					if (SculptSettingsHelper.isBitGridTargeted(player, nbt))
					{
						f = Utility.PIXEL_F * 0.5F;
						x3 = hitX < (Math.round(hitX/Utility.PIXEL_F) * Utility.PIXEL_F) ? 1 : -1;
						y3 = hitY < (Math.round(hitY/Utility.PIXEL_F) * Utility.PIXEL_F) ? 1 : -1;
						z3 = hitZ < (Math.round(hitZ/Utility.PIXEL_F) * Utility.PIXEL_F) ? 1 : -1;
						int s = side.ordinal();
						double offsetX = Math.abs(side.getFrontOffsetX());
						double offsetY = Math.abs(side.getFrontOffsetY());
						double offsetZ = Math.abs(side.getFrontOffsetZ());
						if (s % 2 == 0)
						{
							if (offsetX > 0) x3 *= -1;
							if (offsetY > 0) y3 *= -1;
							if (offsetZ > 0) z3 *= -1;
						}
						boolean su = s== 1 || s == 3;
						if (removeBits ? (!inside || !su) : (inside && su))
						{
							if (offsetX > 0) x3 *= -1;
							if (offsetY > 0) y3 *= -1;
							if (offsetZ > 0) z3 *= -1;
						}
					}
					((SymmetricalShape) shape).init(x2 + f * x3, y2 + f * y3, z2 + f * z3, addPadding(sculptSemiDiameter) - f,
							rotation, sculptHollowShape, wallThickness, openEnds);
				}
				boolean creativeMode = player.capabilities.isCreativeMode;
				HashMap<IBlockState, Integer> bitTypes = null;
				if (removeBits && !world.isRemote && !creativeMode) bitTypes = new HashMap<IBlockState, Integer>();
				int initialpossibleUses = Integer.MAX_VALUE;
				ItemStack setBitStack = SculptSettingsHelper.getBitStack(player, nbt, removeBits);
				IBitBrush setBit = null;
				try
				{
					setBit = api.createBrush(setBitStack);
					if (!removeBits && !creativeMode)
					{
						initialpossibleUses = countInventoryBits(api, player, setBitStack);
					}
				}
				catch (InvalidBitItem e) {}
				int remainingUses = nbt.getInteger(NBTKeys.REMAINING_USES);
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
										possibleUses, Configs.dropBitsPerBlock, setBit);
							}
						}
					}
				}
				if (!Configs.dropBitsPerBlock)
				{
					giveOrDropStacks(player, world, pos, shape, api, bitTypes);
				}
				int change = initialpossibleUses - possibleUses;
				ConfigProperty config = (ConfigProperty) Configs.itemPropertyMap.get(this);
				int newRemainingUses = remainingUses - (config.takesDamage ? change : 0);
				if (!world.isRemote && !creativeMode)
				{
					nbt.setInteger(NBTKeys.REMAINING_USES, newRemainingUses);
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
		return (value + Configs.semiDiameterPadding) * Utility.PIXEL_F;
	}
	
	private float addPaddingToMin(float value1, float value2)
	{
		return Math.min(value1, value2) - Configs.semiDiameterPadding * Utility.PIXEL_F;
	}
	
	private float addPaddingToMax(float value1, float value2)
	{
		return Math.max(value1, value2) + Configs.semiDiameterPadding * Utility.PIXEL_F;
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
				giveOrDropStacks(player, world, pos, shape, api, bitTypes);
			}
			bitAccess.commitChanges();
		}
		return remainingUses;
    }
	
	private boolean isValidBlock(IChiselAndBitsAPI api, World world, BlockPos pos)
	{
		return api.canBeChiseled(world, pos) && (!removeBits || !world.isAirBlock(pos));
	}

	private void giveOrDropStacks(EntityPlayer player, World world, BlockPos pos, Shape shape,
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
					InventoryPlayer inv = player.inventory;
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
							ItemStack stack2 = bitAccess.getBitsAsItem(null, ItemType.CHISLED_BLOCK);
							if (stack2 != null)
							{
								stack2.stackSize = stackSize;
								givePlayerStackOrDropOnGround(player, world, pos, shape, stack2);
							}
							blockCount -= stackSize;
						}
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
			if (Configs.placeBitsInInventory) player.inventoryContainer.detectAndSendChanges();
		}
	}

	private void givePlayerStackOrDropOnGround(EntityPlayer player, World world, BlockPos pos, Shape shape, ItemStack stack)
	{
		if (Configs.placeBitsInInventory)
		{
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
		initialize(stack);
    }
	
	private String colorSculptSettingText(String text, ConfigSculptSettingBase setting)
	{
		return (setting.isPerTool() ? EnumChatFormatting.GREEN : EnumChatFormatting.BLUE) + text;
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced)
	{
		boolean shiftDown = GuiScreen.isShiftKeyDown();
		boolean ctrlDown = GuiScreen.isCtrlKeyDown();
		if (shiftDown)
		{
			tooltip.add("");
			tooltip.add(EnumChatFormatting.BLUE + "Blue = data stored/accessed per player");
			tooltip.add(EnumChatFormatting.GREEN + "Green = data stored/accessed per tool");
			tooltip.add("");
		}
		NBTTagCompound nbt = stack.getTagCompound();
		int mode = SculptSettingsHelper.getMode(player, nbt);
		if (shiftDown)
		{
			tooltip.add(colorSculptSettingText(SculptSettingsHelper.getModeText(mode), Configs.sculptMode));
		}
		ItemStack setBitStack = SculptSettingsHelper.getBitStack(player, nbt, removeBits);
		if (!ctrlDown || shiftDown)
		{
			String bitType = "Bit Type To " + (removeBits ? "Remove" : "Add") + ": ";
			String unspecifiedBit = removeBits ? "any" : "none";
			if (setBitStack != null)
			{
				String stackName = setBitStack.getDisplayName();
				bitType += (stackName.length() == 12 ? unspecifiedBit : stackName.substring(15));
			}
			else
			{
				bitType += unspecifiedBit;
			}
			if (shiftDown)
			{
				bitType = colorSculptSettingText(bitType, removeBits ? Configs.sculptSetBitWire : Configs.sculptSetBitSpade);
			}
			tooltip.add(bitType);
		}
		if (shiftDown)
		{
			tooltip.add(colorSculptSettingText(SculptSettingsHelper.getRotationText(player, nbt), Configs.sculptRotation));
			tooltip.add(colorSculptSettingText(SculptSettingsHelper.getShapeTypeText(player, nbt, this),
					removeBits ? Configs.sculptShapeTypeCurved : Configs.sculptShapeTypeFlat));
			boolean targetBits = SculptSettingsHelper.isBitGridTargeted(player, nbt);
			tooltip.add(colorSculptSettingText(SculptSettingsHelper.getBitGridTargetedText(targetBits), Configs.sculptTargetBitGridVertexes)
					+ (targetBits ? " (corners)" : " (centers)"));
			tooltip.add(colorSculptSettingText(SculptSettingsHelper.getSemiDiameterText(player, nbt), Configs.sculptSemiDiameter));
			tooltip.add(colorSculptSettingText(SculptSettingsHelper.getHollowShapeText(player, nbt), Configs.sculptHollowShape));
			tooltip.add(colorSculptSettingText(SculptSettingsHelper.getOpenEndsText(player, nbt), Configs.sculptOpenEnds));
			tooltip.add(colorSculptSettingText(SculptSettingsHelper.getWallThicknessText(player, nbt), Configs.sculptWallThickness));
		}
		else
		{
			if (ctrlDown)
			{
				tooltip.add("");
				String removeAddText = removeBits ? "remove" : "add";
				String toFromText = removeBits ? "from" : "to";
				if (!removeBits)
				{
					tooltip.add("Shift left click bit to set bit type.");
				}
				if (mode == 2)
				{
					tooltip.add("Left click point on block, drag");
					tooltip.add("    to another point, then");
					tooltip.add("    release to " + removeAddText + " bits " + toFromText);
					tooltip.add("    all intersecting blocks.");
				}
				else
				{
					String shapeControlText = "Left click block to " + removeAddText + " bits";
					if (mode == 0) shapeControlText += ".";
					tooltip.add(shapeControlText);
					if (mode != 0)
					{
						String areaText = toFromText;
						tooltip.add("    " + areaText + " all intersecting blocks.");
					}
				}
				tooltip.add("Right click to cycle modes.");
				tooltip.add("Shift mouse wheel to change");
				tooltip.add("    " + (removeBits ? "removal" : "addition") + (Configs.displayNameDiameter ? " " : " semi-") + "diameter.");
				tooltip.add("");
				tooltip.add("Control right click to");
				tooltip.add("    change shape.");
				tooltip.add("Control left click to toggle");
				tooltip.add("    target between");
				tooltip.add("    bits & vertecies.");
				tooltip.add("Control mouse wheel to");
				tooltip.add("    change rotation.");
				tooltip.add("");
				tooltip.add("Alt right click to toggle");
				tooltip.add("    shapes solid or hollow.");
				tooltip.add("Alt left click to toggle hollow");
				tooltip.add("    shapes open or closed.");
				tooltip.add("Alt mouse wheel to change hollow");
				tooltip.add("    shape wall thickness.");
			}
			else
			{
				tooltip.add("Hold SHIFT for settings.");
				tooltip.add("Hold CONTROL for controls.");
			}
		}
	}
	
}