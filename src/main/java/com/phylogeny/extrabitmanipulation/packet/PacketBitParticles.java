package com.phylogeny.extrabitmanipulation.packet;

import javax.annotation.Nullable;

import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.client.ParticleSplashBit;
import com.phylogeny.extrabitmanipulation.client.ParticleSplashBit.Factory;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketBitParticles implements IMessage
{
	private int flag;
	private Vec3d locBit, locEntity;
	private double width, height;
	
	public PacketBitParticles() {}
	
	public PacketBitParticles(int flag, @Nullable Entity entityBit, @Nullable Entity entity)
	{
		this.flag = flag;
		if (entityBit == null || entity == null)
		{
			locBit = new Vec3d(0, 0, 0);
			locEntity = new Vec3d(0, 0, 0);
			return;
		}
		double x = entityBit.posX;
		double y = entityBit.posY;
		double z = entityBit.posZ;
		Vec3d start = new Vec3d(x, y, z);
		Vec3d end = new Vec3d(x + entityBit.motionX, y + entityBit.motionY, z + entityBit.motionZ);
		RayTraceResult result = entity.getEntityBoundingBox().expandXyz(0.30000001192092896D).calculateIntercept(start, end);
		locBit = result != null ? result.hitVec : start;
		locEntity = new Vec3d(entity.posX, entity.posY, entity.posZ);
		width = (flag == 0 ? entityBit.width : entity.width) + 0.2;
		height = (flag == 0 ? entityBit.height : entity.height) + 0.2;
	}
	
	public PacketBitParticles(int flag, Vec3d locBit, BlockPos pos)
	{
		this(flag, null, (Entity) null);
		this.locBit = locBit;
		locEntity = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
	}
	
	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(flag);
		buffer.writeDouble(locBit.xCoord);
		buffer.writeDouble(locBit.yCoord);
		buffer.writeDouble(locBit.zCoord);
		buffer.writeDouble(locEntity.xCoord);
		buffer.writeDouble(locEntity.yCoord);
		buffer.writeDouble(locEntity.zCoord);
		buffer.writeDouble(width);
		buffer.writeDouble(height);
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		flag = buffer.readInt();
		locBit = new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
		locEntity = new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
		width = buffer.readDouble();
		height = buffer.readDouble();
	}
	
	public static class Handler implements IMessageHandler<PacketBitParticles, IMessage>
	{
		@Override
		public IMessage onMessage(final PacketBitParticles message, final MessageContext ctx)
		{
			ClientHelper.getThreadListener().addScheduledTask(new Runnable()
			{
				@Override
				public void run()
				{
					World world = ClientHelper.getWorld();
					double width = message.width;
					double height = message.height;
					double x = message.locBit.xCoord;
					double y = message.locBit.yCoord;
					double z = message.locBit.zCoord;
					double x2, y2, z2;
					if (message.flag == 0)
					{
						for (int i = 0; i < 3; i++)
						{
							x2 = x - width * 0.5 + width * world.rand.nextDouble();
							y2 = y - height * 0.5 + height * world.rand.nextDouble();
							z2 = z - width * 0.5 + width * world.rand.nextDouble();
							world.spawnParticle(EnumParticleTypes.FLAME, x2, y2, z2, 0, 0, 0, new int[0]);
						}
					}
					else if (message.flag == 3 || message.flag == 4)
					{
						Factory particleFactory = new ParticleSplashBit.Factory();
						for (int i = 0; i < 8; i++)
						{
							ClientHelper.spawnParticle(world, message.locBit, particleFactory);
							if (message.flag == 4)
								world.spawnParticle(EnumParticleTypes.CLOUD, message.locEntity.xCoord + Math.random(),
									message.locEntity.yCoord + Math.random(), message.locEntity.zCoord + Math.random(), 0, 0, 0, new int[0]);
						}
					}
					else
					{
						Factory particleFactory = new ParticleSplashBit.Factory();
						for (int i = 0; i < 8; i++)
						{
							ClientHelper.spawnParticle(world, message.locBit, particleFactory);
						}
						if (message.flag != 2)
							return;
						
						int count = MathHelper.clamp((int) (width * width * height * 6.25), 1, 50);
						for (int i = 0; i < count; i++)
						{
							ClientHelper.spawnParticle(world, message.locBit, particleFactory);
							if (message.flag == 2)
							{
								x2 = message.locEntity.xCoord - width * 0.5 + width * world.rand.nextDouble();
								y2 = message.locEntity.yCoord + height * world.rand.nextDouble();
								z2 = message.locEntity.zCoord - width * 0.5 + width * world.rand.nextDouble();
								world.spawnParticle(EnumParticleTypes.CLOUD, x2, y2, z2, 0, 0, 0, new int[0]);
							}
						}
					}
				}
			});
			return null;
		}
		
	}
	
}