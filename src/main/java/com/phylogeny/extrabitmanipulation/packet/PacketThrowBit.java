package com.phylogeny.extrabitmanipulation.packet;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import mod.chiselsandbits.api.IBitBag;
import mod.chiselsandbits.api.ItemType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.entity.EntityBit;
import com.phylogeny.extrabitmanipulation.reference.Configs;

public class PacketThrowBit implements IMessage
{
	
	public PacketThrowBit() {}
	
	@Override
	public void toBytes(ByteBuf buffer) {}
	
	@Override
	public void fromBytes(ByteBuf buffer) {}
	
	public static class Handler implements IMessageHandler<PacketThrowBit, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketThrowBit message, final MessageContext ctx)
		{
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					ItemStack stack = player.getHeldItemMainhand();
					if (stack == null)
						return;
					
					boolean isBit = ChiselsAndBitsAPIAccess.apiInstance.getItemType(stack) == ItemType.CHISLED_BIT;
					if (!isBit)
					{
						IBitBag bitBag = ChiselsAndBitsAPIAccess.apiInstance.getBitbag(stack);
						if (bitBag == null)
							return;
						
						stack = null;
						int start, end, inc;
						if (Configs.bitBagBitSelectionMode == BitBagBitSelectionMode.END_TO_BEGINNING)
						{
							start = bitBag.getSlots() - 1;
							end = inc = -1;
						}
						else
						{
							start = 0;
							end = bitBag.getSlots();
							inc = 1;
						}
						List<Pair<ItemStack, Integer>> stacks = new ArrayList<Pair<ItemStack, Integer>>();
						boolean isRandom = Configs.bitBagBitSelectionMode == BitBagBitSelectionMode.RANDOM;
						for (int i = start; i != end; i += inc)
						{
							stack = bitBag.extractItem(i, 1, isRandom);
							if (stack != null)
							{
								if (isRandom)
									stacks.add(new ImmutablePair<ItemStack, Integer>(stack, i));
								else
									break;
							}
						}
						if (isRandom && !stacks.isEmpty())
						{
							Pair<ItemStack, Integer> pair = stacks.get(player.worldObj.rand.nextInt(stacks.size()));
							stack = pair.getLeft();
							bitBag.extractItem(pair.getRight(), 1, false);
						}
						if (stack == null)
							return;
					}
					EntityBit entityBit = new EntityBit(player.worldObj, player, stack);
					entityBit.setAim(player, player.rotationPitch, player.rotationYaw, isBit ? Configs.thrownBitVelocity : Configs.thrownBitVelocityBitBag,
							isBit ? Configs.thrownBitInaccuracy : Configs.thrownBitInaccuracyBitBag);
					player.worldObj.spawnEntityInWorld(entityBit);
					player.worldObj.playSound(null, player.posX, player.posY, player.posZ,
							SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (player.worldObj.rand.nextFloat() * 0.4F + 0.8F));
					if (isBit && !player.capabilities.isCreativeMode)
					{
						stack.stackSize--;
						if (stack.stackSize <= 0)
							stack = null;
						
						player.setHeldItem(EnumHand.MAIN_HAND, stack);
					}
				}
			});
			return null;
		}
		
	}
	
	public static enum BitBagBitSelectionMode
	{
		RANDOM, BEGINNING_TO_END, END_TO_BEGINNING
	}
	
}