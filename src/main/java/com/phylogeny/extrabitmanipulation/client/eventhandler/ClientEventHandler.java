package com.phylogeny.extrabitmanipulation.client.eventhandler;

import org.lwjgl.opengl.GL11;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.item.ItemBitWrench;
import com.phylogeny.extrabitmanipulation.packet.PacketCycleWrench;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEventHandler
{
	private int frameCounter;
	private static final ResourceLocation arrowHead = new ResourceLocation(Reference.GROUP_ID, "textures/overlays/ArrowHead.png");
	private static final ResourceLocation arrowBidirectional = new ResourceLocation(Reference.GROUP_ID, "textures/overlays/ArrowBidirectional.png");
	private static final ResourceLocation arrowCyclical = new ResourceLocation(Reference.GROUP_ID, "textures/overlays/ArrowCyclical.png");
	private static final ResourceLocation circle = new ResourceLocation(Reference.GROUP_ID, "textures/overlays/Circle.png");
	
	@SubscribeEvent
	public void interceptMouseInput(MouseEvent event)
	{
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (event.dwheel != 0 && player.isSneaking())
		{
			ItemStack stack = player.getCurrentEquippedItem();
			if (stack != null && stack.getItem() instanceof ItemBitWrench && event.isCancelable())
			{
				event.setCanceled(true);
				ExtraBitManipulation.packetNetwork.sendToServer(new PacketCycleWrench());
			}
		}
	}
	
	@SubscribeEvent
	public void renderBoxesBlocksAndOverlays(RenderWorldLastEvent event)
	{
		if (!Configs.DISABLE_OVERLAYS)
		{
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			World world = player.worldObj;
			ItemStack stack = player.getCurrentEquippedItem();
			if (stack != null)
			{
				MovingObjectPosition target = Minecraft.getMinecraft().objectMouseOver;
				if (target != null && target.typeOfHit.equals(MovingObjectType.BLOCK)
						&& ChiselsAndBitsAPIAccess.apiInstance.isBlockChiseled(world, target.getBlockPos())
						&& stack.getItem() instanceof ItemBitWrench)
				{
					int mode = !stack.hasTagCompound() ? 0 : stack.getTagCompound().getInteger("mode");
					frameCounter++;
					float ticks = event.partialTicks;
	        		double playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * ticks;
	        		double playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * ticks;
	        		double playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * ticks;
	        		EnumFacing dir = target.sideHit;
	                Tessellator t = Tessellator.getInstance();
	                WorldRenderer wr = t.getWorldRenderer();
	                BlockPos pos = target.getBlockPos();
	                int x = pos.getX();
	                int y = pos.getY();
	                int z = pos.getZ();
	                Vec3 hit = target.hitVec;
	                int side = dir.ordinal();
	                boolean upDown = side <= 1;
	                boolean eastWest = side >= 4;
	                boolean northSouth = !upDown && !eastWest;
	                AxisAlignedBB box = new AxisAlignedBB(eastWest ? hit.xCoord : x, upDown ? hit.yCoord : y, northSouth ? hit.zCoord : z,
	                		eastWest ? hit.xCoord : x + 1, upDown ? hit.yCoord : y + 1, northSouth ? hit.zCoord : z + 1);
	                
	                int offsetX = Math.abs(dir.getFrontOffsetX());
	                int offsetY = Math.abs(dir.getFrontOffsetY());
	                int offsetZ = Math.abs(dir.getFrontOffsetZ());
	                double invOffsetX = offsetX ^ 1;
	                double invOffsetY = offsetY ^ 1;
	                double invOffsetZ = offsetZ ^ 1;
	                
	                boolean invertDirection = player.isSneaking();
	                GL11.glPushMatrix();
	                GL11.glDisable(GL11.GL_LIGHTING);
					GL11.glEnable(GL11.GL_ALPHA_TEST);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					GL11.glPushMatrix();
					double angle = getInitialAngle(mode);
					if (mode == 0)
					{
						if (side % 2 == (invertDirection ? 0 : 1)) angle *= -1;
					}
					else
					{
						if (side < 2 || side > 3) angle *= -1;
					}
					if (eastWest) angle += 90;
					if (side == (mode == 1 ? 1 : 0) || side == 3 || side == 4) angle += 180;
					double diffX = playerX - x;
					double diffY = playerY - y;
					double diffZ = playerZ - z;
					double offsetX2 = 0.5 * invOffsetX;
					double offsetY2 = 0.5 * invOffsetY;
					double offsetZ2 = 0.5 * invOffsetZ;
					
					double mirTravel = mode == 1 ? Configs.MIRROR_AMPLITUDE * Math.cos(Math.PI * 2 * frameCounter / Configs.MIRROR_PERIOD) : 0;
					double mirTravel1 = mirTravel;
					double mirTravel2 = 0;
					boolean mirrorInversion = invertDirection && mode == 1;
					if (mirrorInversion && side <= 1 && player.getHorizontalFacing().ordinal() > 3)
					{
						angle += 90;
						mirTravel1 = 0;
	    				mirTravel2 = mirTravel;
					}
					translateAndRotateTexture(playerX, playerY, playerZ, dir, upDown, eastWest, offsetX, offsetY,
							offsetZ, angle, diffX, diffY, diffZ, offsetX2, offsetY2, offsetZ2, mirTravel1, mirTravel2);
					
					Minecraft.getMinecraft().renderEngine.bindTexture(mode == 0 ? arrowCyclical : (mode == 1 ? arrowBidirectional : circle));
					float minU = 0;
	        		float maxU = 1;
	        		float minV = 0;
	        		float maxV = 1;
	        		if (mode == 0)
					{
	        			if (invertDirection)
	        			{
	        				float minU2 = minU;
	        				minU = maxU;
	        				maxU = minU2;
	        			}
					}
	        		else if (mode == 2)
	        		{
	        			EnumFacing dir2 = side <= 1 ? EnumFacing.WEST : (side <= 3 ? EnumFacing.WEST : EnumFacing.DOWN);
	        			box = contractBoxOrRenderArrows(true, t, wr, side, northSouth, dir2, box, invOffsetX,
	        					invOffsetY, invOffsetZ, invertDirection, minU, maxU, minV, maxV);
	        		}
	        		
					renderTexturedSide(t, wr, side, northSouth, box, minU, maxU, minV, maxV, 1);
	        		GL11.glPopMatrix();
	        		
	        		AxisAlignedBB box3 = world.getBlockState(pos).getBlock().getSelectedBoundingBox(world, pos);
					for (int s = 0; s < 6; s++)
	        		{
						if (s != side)
						{
							GL11.glPushMatrix();
							upDown = s <= 1;
			                eastWest = s >= 4;
			                northSouth = !upDown && !eastWest;
							dir = EnumFacing.getFront(s);
							box = new AxisAlignedBB(eastWest ? (s == 5 ? box3.maxX : box3.minX) : x,
																upDown ? (s == 1 ? box3.maxY : box3.minY) : y,
																northSouth ? (s == 3 ? box3.maxZ : box3.minZ) : z,
																eastWest ? (s == 4 ? box3.minX : box3.maxX) : x + 1,
																upDown ? (s == 0 ? box3.minY : box3.maxY) : y + 1,
																northSouth ? (s == 2 ? box3.minZ : box3.maxZ) : z + 1);
							angle = getInitialAngle(mode);
		    				
							boolean oppRotation = false;
							int mode2 = mode;
							oppRotation = dir == EnumFacing.getFront(side).getOpposite();
							if (mode == 0)
		    				{
		    					if (!oppRotation)
		    					{
		    						Minecraft.getMinecraft().renderEngine.bindTexture(arrowHead);
		    						angle = 90;
		    						if (side % 2 == 0) angle += 180;
		    						if (invertDirection) angle += 180;
		    						mode2 = 2;
		    					}
		    					else
		    					{
		    						Minecraft.getMinecraft().renderEngine.bindTexture(arrowCyclical);
		    						mode2 = 0;
		    					}
		    				}
		    				else if (mode == 2)
		    				{
		    					if (!oppRotation)
		    					{
		    						Minecraft.getMinecraft().renderEngine.bindTexture(arrowHead);
		    						if (side == 0 ? s == 2 || s == 5 : (side == 1 ? s == 3 || s == 4 : (side == 2 ? s == 1 || s == 5 : (side == 3 ? s == 0 || s == 4
		    								: (side == 4 ? s == 1 || s == 2 : s == 0 || s == 3))))) angle += 180;
		    						if (invertDirection) angle += 180;
		    					}
		    					else
		    					{
		    						Minecraft.getMinecraft().renderEngine.bindTexture(circle);
		    					}
		    				}
		    				mirTravel1 = mirTravel;
		    				mirTravel2 = 0;
		    				if (((side <= 1 && mirrorInversion ? side > 1 : side <= 1) && s > 1)
		    						|| ((mirrorInversion ? (oppRotation ? player.getHorizontalFacing().ordinal() > 3 : side > 3) : (side == 2 || side == 3)) && s <= 1))
		    				{
		    					angle += 90;
		    					mirTravel1 = 0;
			    				mirTravel2 = mirTravel;
		    				}
		    				
		    				if (mode2 == 0)
		    				{
		    					if (s % 2 == (invertDirection ? 0 : 1)) angle *= -1;
		    					if (oppRotation) angle *= -1;
		    				}
		    				else
		    				{
		    					if (s < 2 || s > 3) angle *= -1;
		    				}
		    				if (eastWest) angle -= 90;
		    				if (s == (mode2 == 1 ? 1 : 0) || s == 3 || s == 5) angle += 180;
		    				offsetX = Math.abs(dir.getFrontOffsetX());
			                offsetY = Math.abs(dir.getFrontOffsetY());
			                offsetZ = Math.abs(dir.getFrontOffsetZ());
			                invOffsetX = offsetX ^ 1;
			                invOffsetY = offsetY ^ 1;
			                invOffsetZ = offsetZ ^ 1;
		    				offsetX2 = 0.5 * invOffsetX;
		    				offsetY2 = 0.5 * invOffsetY;
		    				offsetZ2 = 0.5 * invOffsetZ;
		    				translateAndRotateTexture(playerX, playerY, playerZ, dir, upDown, eastWest, offsetX, offsetY,
		    						offsetZ, angle, diffX, diffY, diffZ, offsetX2, offsetY2, offsetZ2, mirTravel1, mirTravel2);
		    				minU = 0;
		            		maxU = 1;
		            		minV = 0;
		            		maxV = 1;
		            		if (mode2 == 0)
		    				{
		            			if (oppRotation)
			    				{
		            				minU = 1;
				            		maxU = 0;
			    				}
		            			if (invertDirection)
		            			{
		            				float minU2 = minU;
		            				minU = maxU;
		            				maxU = minU2;
		            			}
		    				}
		            		else if (mode2 == 2)
		            		{
		            			EnumFacing dir2 = side <= 1 ? (s == 2 || s == 3 ? EnumFacing.WEST : EnumFacing.DOWN)
	            						: (side >= 4 ? EnumFacing.WEST : (s <= 1 ? EnumFacing.WEST : EnumFacing.DOWN));
		            			box = contractBoxOrRenderArrows(oppRotation, t, wr, side, northSouth, dir2, box, invOffsetX,
		            					invOffsetY, invOffsetZ, invertDirection, minU, maxU, minV, maxV);
		            		}
		            		if (mode2 != 2 || oppRotation) renderTexturedSide(t, wr, s, northSouth, box, minU, maxU, minV, maxV, 1);
		            		GL11.glPopMatrix();
						}
	        		}
					
					GL11.glEnable(GL11.GL_LIGHTING);
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					GL11.glPopMatrix();
				}
			}
		}
	}
	
	private double getInitialAngle(int mode)
	{
		return mode == 0 ? (frameCounter * (360 / Configs.ROTATION_PERIOD)) % 360 : 0;
	}
	
	private void translateAndRotateTexture(double playerX, double playerY, double playerZ, EnumFacing dir, boolean upDown,
			boolean eastWest, int offsetX, int offsetY, int offsetZ, double angle, double diffX, double diffY, double diffZ,
			double offsetX2, double offsetY2, double offsetZ2, double mirTravel1, double mirTravel2)
	{
		double cos = Math.cos(Math.toRadians(angle));
		double sin = Math.sin(Math.toRadians(angle));
		if (upDown)
		{
			GL11.glTranslated(diffX * cos + diffZ * sin - diffX + mirTravel1, 0, -diffX * sin + diffZ * cos - diffZ + mirTravel2);
		}
		else if (eastWest)
		{
			GL11.glTranslated(0, diffY * cos - diffZ * sin - diffY + mirTravel2, diffY * sin + diffZ * cos - diffZ + mirTravel1);
		}
		else
		{
			GL11.glTranslated(diffX * cos - diffY * sin - diffX + mirTravel1, diffX * sin + diffY * cos - diffY + mirTravel2, 0);
		}
		GL11.glTranslated(offsetX2, offsetY2, offsetZ2);
		GL11.glRotated(angle, offsetX, offsetY, offsetZ);
		GL11.glTranslated(-offsetX2, -offsetY2, -offsetZ2);
		GL11.glTranslated(-playerX + 0.002 * dir.getFrontOffsetX(), -playerY + 0.002 * dir.getFrontOffsetY(), -playerZ + 0.002 * dir.getFrontOffsetZ());
	}
	
	private AxisAlignedBB contractBoxOrRenderArrows(boolean contractBox, Tessellator t, WorldRenderer wr, int side, boolean northSouth, EnumFacing dir, AxisAlignedBB box,
			double invOffsetX, double invOffsetY, double invOffsetZ, boolean invertDirection, float minU, float maxU, float minV, float maxV)
	{
		if (contractBox)
		{
			double amount = (frameCounter % Configs.TRANSLATION_SCALE_PERIOD) / Configs.TRANSLATION_SCALE_PERIOD;
			amount /= invertDirection ? -2 : 2;
			if (invertDirection && Configs.TRANSLATION_SCALE_PERIOD > 1) amount += 0.5;
			box = box.contract(amount * invOffsetX, amount * invOffsetY, amount * invOffsetZ);
		}
		else if (Configs.TRANSLATION_DISTANCE > 0)
		{
			double distance = Configs.TRANSLATION_DISTANCE;
			double fadeDistance = Configs.TRANSLATION_FADE_DISTANCE;
			double period = Configs.TRANSLATION_MOVEMENT_PERIOD;
			double offsetDistance = Configs.TRANSLATION_OFFSET_DISTANCE;
			int timeOffset = offsetDistance > 0 ? (int) (period / (distance / offsetDistance)) : 0;
			if (timeOffset > period / 3.0) timeOffset = (int) (period / 3.0);
			if (fadeDistance > distance / 2.0) fadeDistance = distance / 2.0;
			int n = offsetDistance == 0 || period == 1 ? 1 : 3;
			for (int i = 0; i < n; i++)
			{
				double amount = ((frameCounter + timeOffset * i) % period) / (period / (distance * 100.0) * 100.0);
				double alpha = 1;
				if (period > 1)
				{
					if (amount < fadeDistance)
					{
						alpha = amount / fadeDistance;
					}
					else if (amount > distance - fadeDistance)
					{
						alpha = (distance - amount) / fadeDistance;
					}
					amount -= distance / 2.0;
				}
				AxisAlignedBB box2 = new AxisAlignedBB(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)
					.offset(amount * dir.getFrontOffsetX(), amount * dir.getFrontOffsetY(), amount * dir.getFrontOffsetZ());
				renderTexturedSide(t, wr, side, northSouth, box2, minU, maxU, minV, maxV, alpha);
			}
		}
		else
		{
			renderTexturedSide(t, wr, side, northSouth, box, minU, maxU, minV, maxV, 1);
		}
		return box;
	}
	
	private void renderTexturedSide(Tessellator t, WorldRenderer wr, int side, boolean northSouth,
			AxisAlignedBB box, float minU, float maxU, float minV, float maxV, double alpha)
	{
		GL11.glColor4d(1, 1, 1, alpha);
		if (side == 1 || side == 3 || side == 4)
		{
			wr.begin(7, DefaultVertexFormats.POSITION_TEX);
			wr.pos(box.minX, box.minY, box.maxZ).tex(maxU, minV).endVertex();
			wr.pos(box.maxX, northSouth ? box.minY : box.maxY, box.maxZ).tex(minU, minV).endVertex();
			wr.pos(box.maxX, box.maxY, box.minZ).tex(minU, maxV).endVertex();
			wr.pos(box.minX, northSouth ? box.maxY : box.minY, box.minZ).tex(maxU, maxV).endVertex();
			t.draw();
			wr.begin(7, DefaultVertexFormats.POSITION_TEX);
			wr.pos(box.maxX, northSouth ? box.minY : box.maxY, box.maxZ).tex(minU, minV).endVertex();
			wr.pos(box.minX, box.minY, box.maxZ).tex(maxU, minV).endVertex();
			wr.pos(box.minX, northSouth ? box.maxY : box.minY, box.minZ).tex(maxU, maxV).endVertex();
			wr.pos(box.maxX, box.maxY, box.minZ).tex(minU, maxV).endVertex();
			t.draw();
		}
		else
		{
			wr.begin(7, DefaultVertexFormats.POSITION_TEX);
			wr.pos(box.minX, northSouth ? box.maxY : box.minY, box.minZ).tex(maxU, minV).endVertex();
			wr.pos(box.maxX, box.maxY, box.minZ).tex(minU, minV).endVertex();
			wr.pos(box.maxX, northSouth ? box.minY : box.maxY, box.maxZ).tex(minU, maxV).endVertex();
			wr.pos(box.minX, box.minY, box.maxZ).tex(maxU, maxV).endVertex();
			t.draw();
			wr.begin(7, DefaultVertexFormats.POSITION_TEX);
			wr.pos(box.maxX, box.maxY, box.minZ).tex(minU, minV).endVertex();
			wr.pos(box.minX, northSouth ? box.maxY : box.minY, box.minZ).tex(maxU, minV).endVertex();
			wr.pos(box.minX, box.minY, box.maxZ).tex(maxU, maxV).endVertex();
			wr.pos(box.maxX, northSouth ? box.minY : box.maxY, box.maxZ).tex(minU, maxV).endVertex();
			t.draw();
		}
	}
	
}