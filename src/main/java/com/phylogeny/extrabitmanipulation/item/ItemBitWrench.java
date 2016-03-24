package com.phylogeny.extrabitmanipulation.item;

import java.util.List;

import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.config.ConfigProperty;
import com.phylogeny.extrabitmanipulation.helper.SculptSettingsHelper;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;

import mod.chiselsandbits.api.APIExceptions.CannotBeChiseled;
import mod.chiselsandbits.api.APIExceptions.SpaceOccupied;
import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.IBitBrush;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemBitWrench extends ItemBitToolBase
{
	private static final String[] MODE_TITLES = new String[]{"Rotation", "Mirroring", "Translation", "Inversion"};
	private static final String[] MODE_TEXT = new String[]{"rotate", "mirror", "translate", "invert"};
	
	public ItemBitWrench(String name)
	{
		super(name);
	}
	
	public void cycleModes(ItemStack stack, boolean forward)
	{
		initialize(stack);
		NBTTagCompound nbt = stack.getTagCompound();
		int mode = nbt.getInteger(NBTKeys.MODE);
		nbt.setInteger(NBTKeys.MODE, SculptSettingsHelper.cycleData(mode, forward, MODE_TITLES.length));
	}
	
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
			EnumFacing side, float hitX, float hitY, float hitZ)
    {
		initialize(stack);
		IChiselAndBitsAPI api = ChiselsAndBitsAPIAccess.apiInstance;
		int mode = !stack.hasTagCompound() ? 0 : stack.getTagCompound().getInteger(NBTKeys.MODE);
		if ((player.capabilities.isCreativeMode || mode != 3) && api.isBlockChiseled(world, pos))
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
			IBitBrush[][][] bitArray = new IBitBrush[16][16][16];
			int increment = 1; //currently fixed
			boolean invertDirection = player.isSneaking();
			int s = (player.isSneaking() ? (mode == 1 ? (side.rotateAround((side.getAxis().isHorizontal()
					? EnumFacing.UP : player.getHorizontalFacing()).getAxis())) : side.getOpposite()) : side).ordinal();
			boolean canTranslate = true;
			boolean canInvert = false;
			IBitBrush invertbit = null;
			int removalLayer = s % 2 == 1 ? -1 : 16;
			for (int i = 0; i < 16; i++)
			{
				for (int j = 0; j < 16; j++)
				{
					for (int k = 0; k < 16; k++)
					{
						IBitBrush bit = bitAccess.getBitAt(i, j, k);
						bitArray[i][j][k] = bit;
						if (mode == 2)
						{
							if (!bit.isAir() && ((s == 4 && i == 16 - increment)
									|| (s == 0 && j == 16 - increment)
									|| (s == 2 && k == 16 - increment)
									|| (s == 5 && i == increment - 1)
									|| (s == 1 && j == increment - 1)
									|| (s == 3 && k == increment - 1)))
							{
								canTranslate = false;
							}
							if (!bit.isAir())
							{
								if ((s == 4 && i < removalLayer) || (s == 5 && i > removalLayer))	
								{
									removalLayer = i;
								}
								else if ((s == 0 && j < removalLayer) || (s == 1 && j > removalLayer))
								{
									removalLayer = j;
								}
								else if ((s == 2 && k < removalLayer) || (s == 3 && k > removalLayer))
								{
									removalLayer = k;
								}
							}
						}
						else if (mode == 3)
						{
							if (bit.isAir())
							{
								canInvert = true;
							}
							else
							{
								invertbit = bit;
							}
						}
					}
				}
			}
			int increment2 = invertDirection ? -increment : increment;
			if (!(mode == 2 && !canTranslate) && !(mode == 3 && !canInvert))
			{
				int x, y, z;
				for (int i = 0; i < 16; i++)
				{
					for (int j = 0; j < 16; j++)
					{
						for (int k = 0; k < 16; k++)
						{
							IBitBrush bit = bitArray[i][j][k];
							x = i;
							y = j;
							z = k;
							switch (mode)
							{
								case 0:	switch (s)
										{
											case 0: x = k; y = j; z = 16 - 1 - i; break;
											case 1: x = 16 - 1 - k; y =  j; z = i; break;
											case 2: x = 16 - 1 - j; y = i; z = k; break;
											case 3: x = j; y = 16 - 1 - i; z = k; break;
											case 4: x = i; y = 16 - 1 - k; z = j; break;
											case 5: x = i; y = k; z = 16 - 1 - j;
										}
										break;
								case 1: if (s <= 1)
										{
											y = 16 - 1 - j;
										}
										else if (s <= 3)
										{
											z = 16 - 1 - k;
										}
										else
										{
											x = 16 - 1 - i;
										}
										break;
								case 2: int i2 = i + side.getFrontOffsetX() * increment2;
										int j2 = j + side.getFrontOffsetY() * increment2;
										int k2 = k + side.getFrontOffsetZ() * increment2;
										if (!(i2 < 0 || j2 < 0 || k2 < 0
												|| i2 >= 16 || j2 >= 16 || k2 >= 16))
										{
											bit = bitArray[i2][j2][k2];
										}
										if ((s == 4 && i < removalLayer + increment)
												|| (s == 5 && i > removalLayer - increment)
												|| (s == 0 && j < removalLayer + increment)
												|| (s == 1 && j > removalLayer - increment)
												|| (s == 2 && k < removalLayer + increment)
												|| (s == 3 && k > removalLayer - increment))
										{
											bit = null;
										}
										break;
								case 3: bit = bit.isAir() ? invertbit : null;
							}
							try
							{
								bitAccess.setBitAt(x, y, z, bit);
							}
							catch (SpaceOccupied e)
							{
								if (bit != null && !bit.isAir() && bitAccess.getBitAt(x, y, z).isAir())
								{
									return false;
								}
							}
						}
					}
				}
				bitAccess.commitChanges();
				ConfigProperty config = (ConfigProperty) Configs.itemPropertyMap.get(this);
				if (config.takesDamage)
				{
					stack.damageItem(1, player);
					if (stack.getItemDamage() > config.maxDamage)
					{
						player.renderBrokenItemStack(stack);
					}
				}
				return true;
			}
		}
        return false;
    }
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced)
	{
		int mode = stack.hasTagCompound() ? stack.getTagCompound().getInteger(NBTKeys.MODE) : 0;
		String text = MODE_TEXT[mode];
		if (GuiScreen.isShiftKeyDown())
		{
			tooltip.add("Right click blocks to " + text + (mode == 0 ? " CW." : (mode == 1 ? " front-to-back." : (mode == 2 ? " front-to-back." : " their bits."))));
			if (mode != 3)
			{
				tooltip.add("Do so while sneaking to " + text + (mode == 0 ? " CCW." : (mode == 1 ? " left-to-right." : " towards you.")));
			}
			tooltip.add("Mouse wheel while sneaking to cycle modes.");
		}
		else
		{
			tooltip.add("Hold SHIFT for info.");
		}
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack)
    {
		int mode = stack.hasTagCompound() ? stack.getTagCompound().getInteger(NBTKeys.MODE) : 0;
		String displayName = ("" + StatCollector.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name")).trim()
		+ " - " + MODE_TITLES[mode];
		if (mode == 3)
		{
			displayName += " (WIP / Creative Only)";
		}
		return displayName;
    }
	
}