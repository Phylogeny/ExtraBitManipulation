package com.phylogeny.extrabitmanipulation.entity;

import io.netty.buffer.ByteBuf;

import java.util.List;
import javax.annotation.Nullable;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.packet.PacketBitParticles;
import com.phylogeny.extrabitmanipulation.packet.PacketPlaceEntityBit;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;
import com.phylogeny.extrabitmanipulation.reference.Utility;

import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.IBitLocation;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import mod.chiselsandbits.api.APIExceptions.CannotBeChiseled;
import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import mod.chiselsandbits.api.APIExceptions.SpaceOccupied;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityBit extends Entity implements IProjectile, IEntityAdditionalSpawnData
{
	private ItemStack bitStack = ItemStack.EMPTY;
	private int xTile;
	private int yTile;
	private int zTile;
	private Block inTile;
	private int inData;
	protected boolean inGround;
	public Entity shootingEntity;
	private int ticksInAir;
	
	public EntityBit(World worldIn)
	{
		super(worldIn);
		xTile = -1;
		yTile = -1;
		zTile = -1;
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
		double d0 = getEntityBoundingBox().getAverageEdgeLength() * 10.0D;
		if (Double.isNaN(d0))
			d0 = 4.0D;
		
		d0 *= 64.0D;
		return distance < d0 * d0;
	}
	
	public void setAim(Entity shooter, float pitch, float yaw, float velocity, float inaccuracy)
	{
		float f = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
		float f1 = -MathHelper.sin(pitch * 0.017453292F);
		float f2 = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
		setThrowableHeading(f, f1, f2, velocity, inaccuracy);
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
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
	{
		setPosition(x, y, z);
		setRotation(yaw, pitch);
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
			float f = MathHelper.sqrt(x * x + z * z);
			rotationPitch = (float)(MathHelper.atan2(y, f) * (180D / Math.PI));
			rotationYaw = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
			prevRotationPitch = rotationPitch;
			prevRotationYaw = rotationYaw;
			setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
		}
	}
	
	@SuppressWarnings("null")
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F)
		{
			float f = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
			rotationYaw = (float)(MathHelper.atan2(motionX, motionZ) * (180D / Math.PI));
			rotationPitch = (float)(MathHelper.atan2(motionY, f) * (180D / Math.PI));
			prevRotationYaw = rotationYaw;
			prevRotationPitch = rotationPitch;
		}
		BlockPos blockpos = new BlockPos(xTile, yTile, zTile);
		IBlockState iblockstate = world.getBlockState(blockpos);
		Block block = iblockstate.getBlock();
		if (iblockstate.getMaterial() != Material.AIR)
		{
			AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(world, blockpos);

			if (axisalignedbb != Block.NULL_AABB && axisalignedbb.offset(blockpos).isVecInside(new Vec3d(posX, posY, posZ)))
				inGround = true;
		}
		if (inGround)
		{
			int j = block.getMetaFromState(iblockstate);
			if ((block != inTile || j != inData) && !world.collidesWithAnyBlock(getEntityBoundingBox().expandXyz(0.05D)))
			{
				inGround = false;
				motionX *= rand.nextFloat() * 0.2F;
				motionY *= rand.nextFloat() * 0.2F;
				motionZ *= rand.nextFloat() * 0.2F;
				ticksInAir = 0;
			}
			else
			{
				setDead();
			}
		}
		else
		{
			++ticksInAir;
			Vec3d vec3d1 = new Vec3d(posX, posY, posZ);
			Vec3d vec3d = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
			RayTraceResult raytraceresult = world.rayTraceBlocks(vec3d1, vec3d, false, true, false);
			vec3d1 = new Vec3d(posX, posY, posZ);
			vec3d = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
			if (raytraceresult != null)
				vec3d = new Vec3d(raytraceresult.hitVec.xCoord, raytraceresult.hitVec.yCoord, raytraceresult.hitVec.zCoord);
			
			Entity entity = findEntityOnPath(vec3d1, vec3d);
			if (entity != null)
				raytraceresult = new RayTraceResult(entity);
			
			if (raytraceresult != null && raytraceresult.entityHit instanceof EntityPlayer)
			{
				EntityPlayer entityplayer = (EntityPlayer)raytraceresult.entityHit;
				if (shootingEntity instanceof EntityPlayer && !((EntityPlayer)shootingEntity).canAttackPlayer(entityplayer))
					raytraceresult = null;
			}
			if (raytraceresult != null)
				onHit(raytraceresult);
			
			posX += motionX;
			posY += motionY;
			posZ += motionZ;
			float f4 = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
			rotationYaw = (float)(MathHelper.atan2(motionX, motionZ) * (180D / Math.PI));
			for (rotationPitch = (float)(MathHelper.atan2(motionY, f4) * (180D / Math.PI)); rotationPitch - prevRotationPitch < -180.0F; prevRotationPitch -= 360.0F)
			{
			}
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
			float f1 = 0.99F;
			if (isInWater())
			{
				for (int i = 0; i < 4; ++i)
				{
					world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, posX - motionX * 0.25D, posY - motionY * 0.25D, posZ - motionZ * 0.25D, motionX, motionY, motionZ, new int[0]);
				}
				f1 = 0.6F;
			}
			if (isWet())
				extinguish();
			
			motionX *= f1;
			motionY *= f1;
			motionZ *= f1;
			if (!hasNoGravity())
				motionY -= 0.05000000074505806D;
			
			setPosition(posX, posY, posZ);
			doBlockCollisions();
		}
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
					ExtraBitManipulation.packetNetwork.sendToAllAround(new PacketBitParticles(flag,
							this, entity),
						new TargetPoint(world.provider.getDimension(), posX, posY, posZ, 100));
				}
			}
		}
		else
		{
			BlockPos pos = result.getBlockPos();
			xTile = pos.getX();
			yTile = pos.getY();
			zTile = pos.getZ();
			IBlockState iblockstate = world.getBlockState(pos);
			inTile = iblockstate.getBlock();
			inData = inTile.getMetaFromState(iblockstate);
			motionX = ((float)(result.hitVec.xCoord - posX));
			motionY = ((float)(result.hitVec.yCoord - posY));
			motionZ = ((float)(result.hitVec.zCoord - posZ));
			float f2 = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
			posX -= motionX / f2 * 0.05000000074505806D;
			posY -= motionY / f2 * 0.05000000074505806D;
			posZ -= motionZ / f2 * 0.05000000074505806D;
			inGround = true;
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
				ExtraBitManipulation.packetNetwork.sendToAllAround(new PacketPlaceEntityBit(bitStack, pos, result),
						new TargetPoint(world.provider.getDimension(), posX, posY, posZ, 100));
		}
		if (!world.isRemote)
		{
			if (drop)
				entityDropItem(bitStack, 0);
			
			setDead();
		}
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
	
	@Override
	public void move(MoverType moverType, double x, double y, double z)
	{
		super.move(moverType, x, y, z);
		if (inGround)
		{
			xTile = MathHelper.floor(posX);
			yTile = MathHelper.floor(posY);
			zTile = MathHelper.floor(posZ);
		}
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
			if (!entity1.canBeCollidedWith() || (entity1 == shootingEntity && ticksInAir < 5))
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
		compound.setInteger("xTile", xTile);
		compound.setInteger("yTile", yTile);
		compound.setInteger("zTile", zTile);
		ResourceLocation resourcelocation = Block.REGISTRY.getNameForObject(inTile);
		compound.setString("inTile", resourcelocation == null ? "" : resourcelocation.toString());
		compound.setByte("inData", (byte)inData);
		compound.setByte("inGround", (byte)(inGround ? 1 : 0));
		NBTTagCompound nbt = new NBTTagCompound();
		bitStack.writeToNBT(nbt);
		compound.setTag(NBTKeys.ENTITY_BIT_STACK, nbt);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		xTile = compound.getInteger("xTile");
		yTile = compound.getInteger("yTile");
		zTile = compound.getInteger("zTile");
		if (compound.hasKey("inTile", 8))
		{
			inTile = Block.getBlockFromName(compound.getString("inTile"));
		}
		else
		{
			inTile = Block.getBlockById(compound.getByte("inTile") & 255);
		}
		inData = compound.getByte("inData") & 255;
		inGround = compound.getByte("inGround") == 1;
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
	
}