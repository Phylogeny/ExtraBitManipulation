package com.phylogeny.extrabitmanipulation.entity;

import io.netty.buffer.ByteBuf;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.packet.PacketBitParticles;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;
import com.phylogeny.extrabitmanipulation.reference.Utility;

import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import mod.chiselsandbits.api.APIExceptions.SpaceOccupied;
import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.IBitLocation;
import mod.chiselsandbits.api.APIExceptions.CannotBeChiseled;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityBit extends EntityThrowableFixed implements IEntityAdditionalSpawnData
{
	private ItemStack bitStack = ItemStack.EMPTY;
	
	public EntityBit(World worldIn)
	{
		super(worldIn);
	}
	
	public EntityBit(World worldIn, double x, double y, double z, ItemStack bitStack)
	{
		super(worldIn, x, y, z);
		init(bitStack);
	}
	
	public EntityBit(World worldIn, EntityLivingBase throwerIn, ItemStack bitStack)
	{
		super(worldIn, throwerIn);
		init(bitStack);
	}
	
	private void init(ItemStack bitStack)
	{
		this.bitStack = bitStack.copy();
		this.bitStack.setCount(1);
		setSize(Utility.PIXEL_F, Utility.PIXEL_F);
	}
	
	public ItemStack getBitStack()
	{
		return bitStack;
	}
	
	@Override
	protected void onImpact(RayTraceResult result)
	{
		if (bitStack.isEmpty())
			return;
		
		IChiselAndBitsAPI api = ChiselsAndBitsAPIAccess.apiInstance;
		boolean drop = true;
		boolean isLava = false;
		try
		{
			IBlockState state = api.createBrush(bitStack).getState();
			if (state != null)
			{
				isLava = state.getMaterial() != Material.WATER;
				drop = isLava && state.getMaterial() != Material.LAVA;
			}
		}
		catch (InvalidBitItem e) {}
		Entity entity = result.entityHit;
		if (entity != null)
		{
			if (!world.isRemote)
			{
				if ((isLava ? Configs.disableIgniteEntities : Configs.disableExtinguishEntities) || drop)
				{
					if (!Configs.thrownBitDamageDisable)
						entity.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), Configs.thrownBitDamage);
					
					drop = true;
				}
				else 
				{
					if (isLava)
					{
						playSound(SoundEvents.BLOCK_FIRE_AMBIENT, 1.0F, 3.6F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
					}
					else
					{
						playSound(getSwimSound(), 0.2F, 1.6F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
					}
					int flag = isLava ? 0 : 1;
					if (entity.isBurning() != isLava)
					{
						if (isLava)
						{
							entity.setFire(Configs.thrownLavaBitBurnTime);
						}
						else
						{
							entity.extinguish();
							playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
							flag = 2;
						}
					}
					if (!isLava && entity instanceof EntityBlaze)
					{
						if (!Configs.thrownWaterBitBlazeDamageDisable)
							entity.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), Configs.thrownWaterBitBlazeDamage);
						
						flag = 2;
					}
					ExtraBitManipulation.packetNetwork.sendToAllAround(new PacketBitParticles(flag,
							this, entity),
						new TargetPoint(world.provider.getDimension(), posX, posY, posZ, 100));
				}
			}
		}
		else if (result.typeOfHit == RayTraceResult.Type.BLOCK)
		{
			BlockPos pos = result.getBlockPos();
			if (!(isLava ? Configs.disableIgniteBlocks : Configs.disableExtinguishBlocks) && !drop)
			{
				if (!world.isRemote)
				{
					pos = pos.offset(result.sideHit);
					if (isLava)
					{
						if (world.isAirBlock(pos))
						{
							playSound(SoundEvents.BLOCK_FIRE_AMBIENT, 1.0F, 3.6F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
							world.setBlockState(pos, Blocks.FIRE.getDefaultState(), 11);
						}
					}
					else
					{
						playSound(getSwimSound(), 0.2F, 1.6F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
						int flag = 3;
						if (world.getBlockState(pos).getBlock() == Blocks.FIRE)
						{
							playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
							world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
							flag = 4;
						}
						Vec3d hit = result.hitVec.addVector(Utility.PIXEL_D * result.sideHit.getFrontOffsetY() * 2,
								Utility.PIXEL_D * result.sideHit.getFrontOffsetX() * 2,
								Utility.PIXEL_D * result.sideHit.getFrontOffsetZ() * 2);
						ExtraBitManipulation.packetNetwork.sendToAllAround(new PacketBitParticles(flag, hit, pos),
							new TargetPoint(world.provider.getDimension(), posX, posY, posZ, 100));
					}
					setDead();
				}
				return;
			}
			drop = true;
			if (!world.isRemote)
			{
				float volume = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ) * 0.2F;
				if (volume > 1.0F)
					volume = 1.0F;
				
				SoundEvent sound = SoundEvents.BLOCK_METAL_HIT;
				IBlockState state = world.getBlockState(pos);
				if (state != null)
				{
					SoundType soundType = state.getBlock().getSoundType(state, world, pos, this);
					if (soundType != null)
						sound = soundType.getFallSound();
				}
				playSound(sound, volume, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
			}
			try
			{
				IBitLocation bitLoc = api.getBitPos((float) result.hitVec.xCoord - pos.getX(),
						(float) result.hitVec.yCoord - pos.getY(), (float) result.hitVec.zCoord - pos.getZ(), result.sideHit, pos, false);
				Vec3d center = new Vec3d(bitLoc.getBitX() * Utility.PIXEL_D + pos.getX() + Utility.PIXEL_D * result.sideHit.getFrontOffsetX(),
						bitLoc.getBitY() * Utility.PIXEL_D + pos.getY() + Utility.PIXEL_D * result.sideHit.getFrontOffsetY(),
						bitLoc.getBitZ() * Utility.PIXEL_D + pos.getZ() + Utility.PIXEL_D * result.sideHit.getFrontOffsetZ());
				pos = new BlockPos(center);
				IBitAccess bitAccess = api.getBitAccess(world, pos);
				if (api.canBeChiseled(world, pos))
				{
					int x = (int) (Math.ceil((int) ((center.xCoord - pos.getX()) / Utility.PIXEL_D)));
					int y = (int) (Math.ceil((int) ((center.yCoord - pos.getY()) / Utility.PIXEL_D)));
					int z = (int) (Math.ceil((int) ((center.zCoord - pos.getZ()) / Utility.PIXEL_D)));
					if (bitAccess.getBitAt(x, y, z).isAir())
					{
						bitAccess.setBitAt(x, y, z, api.createBrush(bitStack));
						bitAccess.commitChanges(true);
						drop = false;
					}
				}
			}
			catch (CannotBeChiseled e) {}
			catch (SpaceOccupied e) {}
			catch (InvalidBitItem e) {}
		}
		if (world.isRemote)
			return;
		
		if (drop)
			entityDropItem(bitStack, 0);
		
		setDead();
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);
		NBTTagCompound nbt = new NBTTagCompound();
		bitStack.writeToNBT(nbt);
		compound.setTag(NBTKeys.ENTITY_BIT_STACK, nbt);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);
		bitStack = new ItemStack((NBTTagCompound) compound.getTag(NBTKeys.ENTITY_BIT_STACK));
	}
	
	@Override
	public void writeSpawnData(ByteBuf buffer)
	{
		ByteBufUtils.writeItemStack(buffer, bitStack);
	}
	
	@Override
	public void readSpawnData(ByteBuf buffer)
	{
		bitStack = ByteBufUtils.readItemStack(buffer);
	}
	
}