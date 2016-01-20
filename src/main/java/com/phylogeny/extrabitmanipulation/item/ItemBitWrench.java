package com.phylogeny.extrabitmanipulation.item;

import java.util.List;

import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.client.creativetab.CreativeTabExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.reference.Configs;

import mod.chiselsandbits.api.APIExceptions.CannotBeChiseled;
import mod.chiselsandbits.api.APIExceptions.SpaceOccupied;
import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.IBitBrush;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemBitWrench extends Item
{
	private static final String name = "BitWrench";
	public static final String[] modeTitles = new String[]{"Rotation", "Mirroring", "Translation"};
	public static final String[] modeText = new String[]{"rotate", "mirror", "translate"};
	
	public ItemBitWrench()
	{
		setUnlocalizedName(name);
		setCreativeTab(CreativeTabExtraBitManipulation.bitWrench);
		if (Configs.TAKES_DAMAGE_BIT_WRENCH)
		{
			setMaxDamage(Configs.MAX_DAMAGE_BIT_WRENCH);
		}
		maxStackSize = 1;
	}
	
	public String getName()
	{
		return name;
	}
	
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		if (ChiselsAndBitsAPIAccess.apiInstance.isBlockChiseled(world, pos))
		{
			IBitAccess bitAccess;
			try
			{
				bitAccess = ChiselsAndBitsAPIAccess.apiInstance.getBitAccess(world, pos);
			}
			catch (CannotBeChiseled e)
			{
				e.printStackTrace();
				return false;
			}
			IBitBrush[][][] bitArray = new IBitBrush[16][16][16];
			int mode = !stack.hasTagCompound() ? 0 : stack.getTagCompound().getInteger("mode");
			int increment = 1; //currently fixed
			boolean invertDirection = player.isSneaking();
			int s = (player.isSneaking() ? (mode == 1 ? (side.rotateAround((side.getAxis().isHorizontal()
					? EnumFacing.UP : player.getHorizontalFacing()).getAxis())) : side.getOpposite()) : side).ordinal();
			boolean canTranslate = true;
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
					}
				}
			}
			int increment2 = invertDirection ? -increment : increment;
			if (mode != 2 || canTranslate)
			{
				for (int i = 0; i < 16; i++)
				{
					for (int j = 0; j < 16; j++)
					{
						for (int k = 0; k < 16; k++)
						{
							try
							{
								IBitBrush bit = bitArray[i][j][k];
								switch (mode)
								{
									case 0:	switch (s)
											{
												case 0: bitAccess.setBitAt(k, j, 16 - 1 - i, bit); break;
												case 1: bitAccess.setBitAt(16 - 1 - k, j, i, bit); break;
												case 2: bitAccess.setBitAt(16 - 1 - j, i, k, bit); break;
												case 3: bitAccess.setBitAt(j, 16 - 1 - i, k, bit); break;
												case 4: bitAccess.setBitAt(i, 16 - 1 - k, j, bit); break;
												case 5: bitAccess.setBitAt(i, k, 16 - 1 - j, bit);
											}
											break;
									case 1: if (s <= 1)
											{
												bitAccess.setBitAt(i, 16 - 1 - j, k, bit);
											}
											else if (s <= 3)
											{
												bitAccess.setBitAt(i, j, 16 - 1 - k, bit);
											}
											else
											{
												bitAccess.setBitAt(16 - 1 - i, j, k, bit);
											}
											break;
									case 2: if (canTranslate)
											{
												int i2 = i + side.getFrontOffsetX() * increment2;
												int j2 = j + side.getFrontOffsetY() * increment2;
												int k2 = k + side.getFrontOffsetZ() * increment2;
												if (!(i2 < 0 || j2 < 0 || k2 < 0
														|| i2 >= 16 || j2 >= 16 || k2 >= 16))
												{
													bitAccess.setBitAt(i, j, k, bitArray[i2][j2][k2]);
												}
												if ((s == 4 && i < removalLayer + increment)
														|| (s == 5 && i > removalLayer - increment)
														|| (s == 0 && j < removalLayer + increment)
														|| (s == 1 && j > removalLayer - increment)
														|| (s == 2 && k < removalLayer + increment)
														|| (s == 3 && k > removalLayer - increment))
												{
													bitAccess.setBitAt(i, j, k, null);
												}
											}
										break;
								}
							}
							catch (SpaceOccupied e) {}
						}
					}
				}
				bitAccess.commitChanges();
				if (Configs.TAKES_DAMAGE_BIT_WRENCH) stack.damageItem(1, player);
				return true;
			}
		}
        return false;
    }
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		int mode = stack.hasTagCompound() ? stack.getTagCompound().getInteger("mode") : 0;
		if (!stack.hasTagCompound())
		{
			setWrenchDisplayName(stack, mode);
		}
		String text = modeText[mode];
		list.add("Right click blocks to " + text + (mode == 0 ? " CW." : (mode == 1 ? " front-to-back." : " away from you.")));
		list.add("Do so while sneaking to " + text + (mode == 0 ? " CCW." : (mode == 1 ? " left-to-right." : " towards you.")));
		list.add("Mouse wheel while sneaking to cycle mode.");
	}
	
	
	public void onCreated(ItemStack stack, World world, EntityPlayer player)
    {
		stack.setTagCompound(new NBTTagCompound());
		setWrenchDisplayName(stack, 0);
    }
	
	public static void setWrenchDisplayName(ItemStack stack, int mode)
	{
		setWrenchDisplayName(stack, stack.getDisplayName(), mode);
	}
	
	public static void setWrenchDisplayName(ItemStack stack, String name, int mode)
	{
		stack.setStackDisplayName(EnumChatFormatting.RESET + name + " - " + modeTitles[mode]);
	}
	
}