package com.phylogeny.extrabitmanipulation.entity;

import io.netty.buffer.ByteBuf;

import java.util.List;

import javax.annotation.Nullable;

import mod.chiselsandbits.api.APIExceptions.CannotBeChiseled;
import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import mod.chiselsandbits.api.APIExceptions.SpaceOccupied;
import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.IBitLocation;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.packet.PacketBitParticles;
import com.phylogeny.extrabitmanipulation.packet.PacketPlaceEntityBit;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;
import com.phylogeny.extrabitmanipulation.reference.Utility;

public class EntityBit extends Entity implements IProjectile, IEntityAdditionalSpawnData
{
	private ItemStack bitStack = ItemStack.EMPTY;
	protected boolean inGround;
	public Entity shootingEntity;
	
	public EntityBit(World worldIn)
	{
		super(worldIn);
		setSize(Utility.PIXEL_F, Utility.PIXEL_F);
	}
	
	public EntityBit(World worldIn, double x, double y, double z, ItemStack bitStack)
	{
		this(worldIn);
		setPosition(x, y, z);
		this.bitStack = bitStack.copy();
		this.bitStack.setCount(1);
	}
	
	public EntityBit(World worldIn, EntityLivingBase shooter, ItemStack bitStack)
	{
		this(worldIn, shooter.posX, shooter.posY + shooter.getEyeHeight() - 0.10000000149011612D, shooter.posZ, bitStack);
		shootingEntity = shooter;
	}
	
	@Override
	protected void entityInit() {}
	
	public ItemStack getBitStack()
	{
		return bitStack;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance)
	{
		double range = getEntityBoundingBox().getAverageEdgeLength() * 10.0D;
		if (Double.isNaN(range))
			range = 4.0D;
		
		range *= 64.0D;
		return distance < range * range;
	}
	
	public void setAim(Entity shooter, float pitch, float yaw, float velocity, float inaccuracy)
	{
		pitch = (float) Math.toRadians(pitch);
		yaw = (float) Math.toRadians(yaw);
		float x = -MathHelper.sin(yaw) * MathHelper.cos(pitch);
		float y = -MathHelper.sin(pitch);
		float z = MathHelper.cos(yaw) * MathHelper.cos(pitch);
		setThrowableHeading(x, y, z, velocity, inaccuracy);
		motionX += shooter.motionX;
		motionZ += shooter.motionZ;
		if (!shooter.onGround)
			motionY += shooter.motionY;
	}
	
	@Override
	public void setThrowableHeading(double x, double y, double z, float velocity, float inaccuracy)
	{
		float f = MathHelper.sqrt(x * x + y * y + z * z);
		x /= f;
		y /= f;
		z /= f;
		x += rand.nextGaussian() * 0.007499999832361937D * inaccuracy;
		y += rand.nextGaussian() * 0.007499999832361937D * inaccuracy;
		z += rand.nextGaussian() * 0.007499999832361937D * inaccuracy;
		x *= velocity;
		y *= velocity;
		z *= velocity;
		motionX = x;
		motionY = y;
		motionZ = z;
		float f1 = MathHelper.sqrt(x * x + z * z);
		rotationYaw = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
		rotationPitch = (float)(MathHelper.atan2(y, f1) * (180D / Math.PI));
		prevRotationYaw = rotationYaw;
		prevRotationPitch = rotationPitch;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void setVelocity(double x, double y, double z)
	{
		motionX = x;
		motionY = y;
		motionZ = z;
		if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F)
		{
			setRotation(x, y, z);
			setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
		}
	}
	
	private void setRotation(double x, double y, double z)
	{
		float f = MathHelper.sqrt(x * x + z * z);
		rotationPitch = (float)(MathHelper.atan2(y, f) * (180D / Math.PI));
		rotationYaw = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
		prevRotationPitch = rotationPitch;
		prevRotationYaw = rotationYaw;
	}
	
	@Override
	public void onUpdate()
	{
		if (inGround)
			return;
		
		super.onUpdate();
		if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F)
			setRotation(motionX, motionY, motionZ);
		
		Vec3d start = new Vec3d(posX, posY, posZ);
		Vec3d end = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
		RayTraceResult result = world.rayTraceBlocks(start, end, false, true, false);
		start = new Vec3d(posX, posY, posZ);
		end = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
		if (result != null)
			end = new Vec3d(result.hitVec.xCoord, result.hitVec.yCoord, result.hitVec.zCoord);
		
		Entity entity = findEntityOnPath(start, end);
		if (entity != null)
			result = new RayTraceResult(entity);
		
		if (result != null)
			onHit(result);
		
		posX += motionX;
		posY += motionY;
		posZ += motionZ;
		float f4 = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
		rotationYaw = (float)(MathHelper.atan2(motionX, motionZ) * (180D / Math.PI));
		for (rotationPitch = (float)(MathHelper.atan2(motionY, f4) * (180D / Math.PI));
				rotationPitch - prevRotationPitch < -180.0F; prevRotationPitch -= 360.0F) {}
		while (rotationPitch - prevRotationPitch >= 180.0F)
		{
			prevRotationPitch += 360.0F;
		}
		while (rotationYaw - prevRotationYaw < -180.0F)
		{
			prevRotationYaw -= 360.0F;
		}
		while (rotationYaw - prevRotationYaw >= 180.0F)
		{
			prevRotationYaw += 360.0F;
		}
		rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2F;
		rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2F;
		float attenuation = 0.99F;
		if (isInWater())
		{
			for (int i = 0; i < 4; ++i)
			{
				world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX - motionX * 0.25D,
						posY - motionY * 0.25D, posZ - motionZ * 0.25D, motionX, motionY, motionZ, new int[0]);
			}
			attenuation = 0.6F;
		}
		if (isWet())
			extinguish();
		
		motionX *= attenuation;
		motionY *= attenuation;
		motionZ *= attenuation;
		if (!hasNoGravity())
			motionY -= 0.05000000074505806D;
		
		setPosition(posX, posY, posZ);
		doBlockCollisions();
	}
	
	protected void onHit(RayTraceResult result)
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
						entity.attackEntityFrom(DamageSource.causeThrownDamage(this, shootingEntity), Configs.thrownBitDamage);
					
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
							entity.attackEntityFrom(DamageSource.causeThrownDamage(this, shootingEntity), Configs.thrownWaterBitBlazeDamage);
						
						flag = 2;
					}
					updateClients(new PacketBitParticles(flag, this, entity));
				}
			}
		}
		else
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
						updateClients(new PacketBitParticles(flag, hit, pos));
					}
					setDead();
				}
				return;
			}
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
			drop = !placeBit(world, bitStack, pos, result.hitVec, result.sideHit, world.isRemote);
			if (!world.isRemote && !drop)
				updateClients(new PacketPlaceEntityBit(bitStack, pos, result));
		}
		if (!world.isRemote)
		{
			if (drop)
				entityDropItem(bitStack, 0);
			
			setDead();
		}
	}
	
	private void updateClients(IMessage message)
	{
		ExtraBitManipulation.packetNetwork.sendToAllAround(message, new TargetPoint(world.provider.getDimension(), posX, posY, posZ, 100));
	}
	
	public static boolean placeBit(World world, ItemStack bitStack, BlockPos pos, Vec3d hitVec, EnumFacing sideHit, boolean simulate)
	{
		try
		{
			IChiselAndBitsAPI api2 = ChiselsAndBitsAPIAccess.apiInstance;
			IBitLocation bitLoc = api2.getBitPos((float) hitVec.xCoord - pos.getX(),
					(float) hitVec.yCoord - pos.getY(), (float) hitVec.zCoord - pos.getZ(), sideHit, pos, false);
			Vec3d center = new Vec3d(bitLoc.getBitX() * Utility.PIXEL_D + pos.getX() + Utility.PIXEL_D * sideHit.getFrontOffsetX(),
					bitLoc.getBitY() * Utility.PIXEL_D + pos.getY() + Utility.PIXEL_D * sideHit.getFrontOffsetY(),
					bitLoc.getBitZ() * Utility.PIXEL_D + pos.getZ() + Utility.PIXEL_D * sideHit.getFrontOffsetZ());
			pos = new BlockPos(center);
			IBitAccess bitAccess = api2.getBitAccess(world, pos);
			if (api2.canBeChiseled(world, pos))
			{
				int x = (int) (Math.ceil((int) ((center.xCoord - pos.getX()) / Utility.PIXEL_D)));
				int y = (int) (Math.ceil((int) ((center.yCoord - pos.getY()) / Utility.PIXEL_D)));
				int z = (int) (Math.ceil((int) ((center.zCoord - pos.getZ()) / Utility.PIXEL_D)));
				if (bitAccess.getBitAt(x, y, z).isAir())
				{
					bitAccess.setBitAt(x, y, z, api2.createBrush(bitStack));
					if (!simulate)
						bitAccess.commitChanges(true);
					
					return true;
				}
			}
		}
		catch (CannotBeChiseled e) {}
		catch (SpaceOccupied e) {}
		catch (InvalidBitItem e) {}
		return false;
	}
	
	@Nullable
	protected Entity findEntityOnPath(Vec3d start, Vec3d end)
	{
		Entity entity = null;
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().addCoord(motionX, motionY, motionZ).expandXyz(1.0D));
		double d0 = 0.0D;
		for (int i = 0; i < list.size(); ++i)
		{
			Entity entity1 = list.get(i);
			if (!entity1.canBeCollidedWith() || (entity1 == shootingEntity && ticksExisted < 5))
				continue;
			
			AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expandXyz(0.30000001192092896D);
			RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(start, end);
			if (raytraceresult != null)
			{
				double d1 = start.squareDistanceTo(raytraceresult.hitVec);
				if (d1 < d0 || d0 == 0.0D)
				{
					entity = entity1;
					d0 = d1;
				}
			}
		}
		return entity;
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		compound.setByte("inGround", (byte)(inGround ? 1 : 0));
		NBTTagCompound nbt = new NBTTagCompound();
		bitStack.writeToNBT(nbt);
		compound.setTag(NBTKeys.ENTITY_BIT_STACK, nbt);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		inGround = compound.getByte("inGround") == 1;
		bitStack = new ItemStack(compound.getCompoundTag(NBTKeys.ENTITY_BIT_STACK));
	}
	
	@Override
	public void writeSpawnData(ByteBuf buffer)
	{
		ByteBufUtils.writeItemStack(buffer, bitStack);
		buffer.writeDouble(motionX);
		buffer.writeDouble(motionY);
		buffer.writeDouble(motionZ);
	}
	
	@Override
	public void readSpawnData(ByteBuf buffer)
	{
		bitStack = ByteBufUtils.readItemStack(buffer);
		motionX = buffer.readDouble();
		motionY = buffer.readDouble();
		motionZ = buffer.readDouble();
	}
	
	@Override
	public boolean canBeAttackedWithItem()
	{
		return false;
	}

	@Override
	public float getEyeHeight()
	{
		return 0.0F;
	}
	
	@Override
	public boolean canPassengerSteer()
	{
		return true;
	}
	
}