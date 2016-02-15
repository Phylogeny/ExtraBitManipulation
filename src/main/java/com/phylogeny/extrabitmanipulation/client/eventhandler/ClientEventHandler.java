package com.phylogeny.extrabitmanipulation.client.eventhandler;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.config.ConfigProperty;
import com.phylogeny.extrabitmanipulation.config.ConfigShapeRender;
import com.phylogeny.extrabitmanipulation.config.ConfigShapeRenderPair;
import com.phylogeny.extrabitmanipulation.item.ItemBitWrench;
import com.phylogeny.extrabitmanipulation.item.ItemBitToolBase;
import com.phylogeny.extrabitmanipulation.item.ItemSculptingTool;
import com.phylogeny.extrabitmanipulation.packet.PacketCycleData;
import com.phylogeny.extrabitmanipulation.packet.PacketSculpt;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;
import com.phylogeny.extrabitmanipulation.reference.Reference;
import com.phylogeny.extrabitmanipulation.reference.Utility;

import mod.chiselsandbits.api.APIExceptions.CannotBeChiseled;
import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.IBitLocation;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEventHandler
{
	private int frameCounter;
	private Vec3 drawnStartPoint = null;
	private static final ResourceLocation arrowHead = new ResourceLocation(Reference.GROUP_ID, "textures/overlays/ArrowHead.png");
	private static final ResourceLocation arrowBidirectional = new ResourceLocation(Reference.GROUP_ID, "textures/overlays/ArrowBidirectional.png");
	private static final ResourceLocation arrowCyclical = new ResourceLocation(Reference.GROUP_ID, "textures/overlays/ArrowCyclical.png");
	private static final ResourceLocation circle = new ResourceLocation(Reference.GROUP_ID, "textures/overlays/Circle.png");
	private static final ResourceLocation inversion = new ResourceLocation(Reference.GROUP_ID, "textures/overlays/Inversion.png");
	
	@SubscribeEvent
	public void interceptMouseInput(MouseEvent event)
	{
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (event.dwheel != 0 && player.isSneaking())
		{
			ItemStack stack = player.getCurrentEquippedItem();
			if (stack != null && stack.getItem() instanceof ItemBitToolBase)
			{
				event.setCanceled(true);
				ExtraBitManipulation.packetNetwork.sendToServer(new PacketCycleData(event.dwheel < 0));
			}
		}
		else if (event.button == 0)
		{
			if (!player.capabilities.allowEdit) return;
			ItemStack itemStack = player.inventory.getCurrentItem();
			if (itemStack != null)
			{
				Item item = itemStack.getItem();
				if (event.buttonstate && item instanceof ItemBitWrench)
				{
					event.setCanceled(true);
				}
				else if (item != null && item instanceof ItemSculptingTool)
				{
					boolean drawnMode = (itemStack.hasTagCompound() && itemStack.getTagCompound().getInteger(NBTKeys.MODE) == 2);
					if (!drawnMode)
					{
						drawnStartPoint = null;
					}
					if (event.buttonstate || drawnMode)
					{
						MovingObjectPosition target = Minecraft.getMinecraft().objectMouseOver;
						if (target != null && target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
						{
							BlockPos pos = target.getBlockPos();
							EnumFacing side = target.sideHit;
							Vec3 hit = target.hitVec;
							ItemSculptingTool toolItem = (ItemSculptingTool) item;
							boolean readBit = player.isSneaking() && !toolItem.removeBits();
							boolean swingTool = true;
							if (!readBit && drawnMode && event.buttonstate)
							{
								IBitLocation bitLoc = ChiselsAndBitsAPIAccess.apiInstance.getBitPos((float) hit.xCoord - pos.getX(),
										(float) hit.yCoord - pos.getY(), (float) hit.zCoord - pos.getZ(), side, pos, false);
								if (bitLoc != null)
								{
									int x = pos.getX();
									int y = pos.getY();
									int z = pos.getZ();
									float x2 = x + bitLoc.getBitX() * Utility.pixelF;
									float y2 = y + bitLoc.getBitY() * Utility.pixelF;
									float z2 = z + bitLoc.getBitZ() * Utility.pixelF;
									if (!toolItem.removeBits())
									{
										x2 += side.getFrontOffsetX() * Utility.pixelF;
										y2 += side.getFrontOffsetY() * Utility.pixelF;
										z2 += side.getFrontOffsetZ() * Utility.pixelF;
									}
									drawnStartPoint = new Vec3(x2, y2, z2);
								}
								else
								{
									drawnStartPoint = null;
									swingTool = false;
								}
							}
							else
							{
								if (readBit)
								{
									IChiselAndBitsAPI api = ChiselsAndBitsAPIAccess.apiInstance;
									IBitLocation bitLoc = api.getBitPos((float) hit.xCoord - pos.getX(), (float) hit.yCoord - pos.getY(),
											(float) hit.zCoord - pos.getZ(), side, pos, false);
									if (bitLoc != null)
									{
										try
										{
											IBitAccess bitAccess = api.getBitAccess(player.worldObj, pos);
										}
										catch (CannotBeChiseled e)
										{
											event.setCanceled(true);
											return;
										}
									}
								}
								else if (!player.isSneaking() || toolItem.removeBits() || drawnMode)
								{
									swingTool = toolItem.sculptBlocks(itemStack, player, player.worldObj, pos, side, hit, drawnStartPoint);
									if (drawnMode) swingTool = true;
								}
								ExtraBitManipulation.packetNetwork.sendToServer(new PacketSculpt(pos, side, hit, drawnStartPoint));
								if (drawnMode && !event.buttonstate)
								{
									drawnStartPoint = null;
								}
							}
							if (swingTool) player.swingItem();
							event.setCanceled(true);
						}
						else if (drawnMode)
						{
							drawnStartPoint = null;
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void cancelBoundingBoxDraw(DrawBlockHighlightEvent event)
	{
		ItemStack itemStack = event.player.inventory.getCurrentItem();
		if (itemStack != null)
		{
			Item item = itemStack.getItem();
			if (item != null && item instanceof ItemSculptingTool && itemStack.hasTagCompound()
					&& itemStack.getTagCompound().getInteger(NBTKeys.MODE) == 1)
			{
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public void renderBoxesSpheresAndOverlays(RenderWorldLastEvent event)
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
						&& stack.getItem() instanceof ItemBitToolBase)
				{
					IChiselAndBitsAPI api = ChiselsAndBitsAPIAccess.apiInstance;
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
	                double diffX = playerX - x;
					double diffY = playerY - y;
					double diffZ = playerZ - z;
					Vec3 hit = target.hitVec;
					if (stack.getItem() instanceof ItemBitWrench && api.isBlockChiseled(world, target.getBlockPos()))
					{
						int mode = !stack.hasTagCompound() ? 0 : stack.getTagCompound().getInteger(NBTKeys.MODE);
						frameCounter++;
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
		                GlStateManager.pushMatrix();
		                GlStateManager.disableLighting();
		                GlStateManager.enableAlpha();
						GlStateManager.enableBlend();
						GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
						GlStateManager.enableTexture2D();
						GlStateManager.pushMatrix();
						double angle = getInitialAngle(mode);
						if (mode == 3)
	    				{
	    					if (side % 2 == 1) angle += 180;
	    					if (side >= 4) angle -= 90;
	    				}
						else
						{
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
						}
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
						
						Minecraft.getMinecraft().renderEngine.bindTexture(mode == 0 ? arrowCyclical : (mode == 1 ? arrowBidirectional : (mode == 2 ? circle : inversion)));
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
						GlStateManager.popMatrix();
		        		
		        		AxisAlignedBB box3 = world.getBlockState(pos).getBlock().getSelectedBoundingBox(world, pos);
						for (int s = 0; s < 6; s++)
		        		{
							if (s != side)
							{
								GlStateManager.pushMatrix();
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
								if (mode != 3)
			    				{
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
			    				}
			    				mirTravel1 = mirTravel;
			    				mirTravel2 = 0;
			    				if (mode != 3 && (((side <= 1 && mirrorInversion ? side > 1 : side <= 1) && s > 1)
			    						|| ((mirrorInversion ? (oppRotation ? player.getHorizontalFacing().ordinal() > 3 : side > 3)
			    								: (side == 2 || side == 3)) && s <= 1)))
			    				{
			    					angle += 90;
			    					mirTravel1 = 0;
				    				mirTravel2 = mirTravel;
			    				}
			    				if (mode == 3)
			    				{
			    					if (s % 2 == 1) angle += 180;
			    					if (s >= 4) angle -= 90;
			    				}
			    				else
			    				{
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
			    				}
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
			            		GlStateManager.popMatrix();
							}
		        		}
						
						GlStateManager.enableLighting();
						GlStateManager.disableBlend();
						GlStateManager.enableTexture2D();
						GlStateManager.popMatrix();
					}
					else if (stack.getItem() instanceof ItemSculptingTool)
					{
						ItemSculptingTool toolItem = (ItemSculptingTool) stack.getItem();
						boolean removeBits = toolItem.removeBits();
						if (!removeBits || (stack.hasTagCompound() && stack.getTagCompound().getInteger(NBTKeys.MODE) == 1) || api.canBeChiseled(world, target.getBlockPos()))
						{
							IBitLocation bitLoc = api.getBitPos((float) hit.xCoord - pos.getX(), (float) hit.yCoord - pos.getY(),
									(float) hit.zCoord - pos.getZ(), dir, pos, false);
							if (bitLoc != null)
							{
								GlStateManager.enableBlend();
								GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
								GlStateManager.disableTexture2D();
								GlStateManager.depthMask(false);
								double r = (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBTKeys.SCULPT_SEMI_DIAMETER)
										? stack.getTagCompound().getInteger(NBTKeys.SCULPT_SEMI_DIAMETER)
												: ((ConfigProperty) Configs.itemPropertyMap.get(toolItem)).defaultRemovalSemiDiameter) * Utility.pixelD;
								ConfigShapeRenderPair configPair = Configs.itemShapeMap.get(toolItem);
								ConfigShapeRender configBox = configPair.boundingBox;
								if (configBox.renderInnerShape || configBox.renderOuterShape)
								{
									GlStateManager.pushMatrix();
									GL11.glLineWidth(configBox.lineWidth);
									AxisAlignedBB box = new AxisAlignedBB(x - r, y - r, z - r, x + r + Utility.pixelD, y + r + Utility.pixelD, z + r + Utility.pixelD)
											.offset(bitLoc.getBitX() * Utility.pixelD, bitLoc.getBitY() * Utility.pixelD, bitLoc.getBitZ() * Utility.pixelD);
									if (!stack.hasTagCompound() || stack.getTagCompound().getInteger(NBTKeys.MODE) == 0)
									{
										BlockPos pos2 = !removeBits && !ItemSculptingTool.wasInsideClicked(dir, hit, pos) ? pos.offset(dir) : pos;
										Block block = world.getBlockState(pos2).getBlock();
										AxisAlignedBB box2 = !removeBits ? new AxisAlignedBB(pos2.getX(), pos2.getY(), pos2.getZ(),
												pos2.getX() + 1, pos2.getY() + 1, pos2.getZ() + 1) :
											block.getSelectedBoundingBox(world, pos2);
										box = limitBox(box, box2);
									}
									double f = 0.0020000000949949026;
									if (configBox.renderOuterShape)
									{
										GlStateManager.color(configBox.red, configBox.green,
												configBox.blue, configBox.outerShapeAlpha);
										RenderGlobal.drawSelectionBoundingBox(box.expand(f, f, f).offset(-playerX, -playerY, -playerZ));
									}
									if (configBox.renderInnerShape)
									{
										GlStateManager.color(configBox.red, configBox.green,
												configBox.blue, configBox.innerShapeAlpha);
										GlStateManager.depthFunc(GL11.GL_GREATER);
										RenderGlobal.drawSelectionBoundingBox(box.expand(f, f, f).offset(-playerX, -playerY, -playerZ));
										GlStateManager.depthFunc(GL11.GL_LEQUAL);
									}
									GlStateManager.popMatrix();
								}
								if (configPair.hasEnvelopedShape())
								{
									ConfigShapeRender configShape = configPair.envelopedShape;
									if (configShape.renderInnerShape || configShape.renderOuterShape)
									{
										r += Utility.pixelD * 0.5;
										Sphere sphere = new Sphere();
										sphere.setDrawStyle(GLU.GLU_LINE);
										int outerSphereID = 0, innerSphereID = 0;
										if (configShape.renderOuterShape)
										{
											outerSphereID = GL11.glGenLists(1);
											GL11.glNewList(outerSphereID, GL11.GL_COMPILE);
											GlStateManager.color(configShape.red, configShape.green,
													configShape.blue, configShape.outerShapeAlpha);
											sphere.draw((float) r, 32, 32);
											GL11.glEndList();
										}
										if (configShape.renderInnerShape)
										{
											innerSphereID = GL11.glGenLists(1);
											GL11.glNewList(innerSphereID, GL11.GL_COMPILE);
											GlStateManager.color(configShape.red, configShape.green,
													configShape.blue, configShape.innerShapeAlpha);
											sphere.draw((float) r, 32, 32);
											GL11.glEndList();
										}
										GlStateManager.pushMatrix();
										GL11.glLineWidth(configShape.lineWidth);
										GlStateManager.translate(bitLoc.getBitX() * Utility.pixelD + x - playerX + (Utility.pixelD * 0.5),
												bitLoc.getBitY() * Utility.pixelD + y - playerY + (Utility.pixelD * 0.5),
												bitLoc.getBitZ() * Utility.pixelD + z - playerZ + (Utility.pixelD * 0.5));
										GlStateManager.rotate(90, 1, 0, 0);
										if (configShape.renderOuterShape) GL11.glCallList(outerSphereID);
										if (configShape.renderInnerShape)
										{
											GlStateManager.depthFunc(GL11.GL_GREATER);
											GL11.glCallList(innerSphereID);
											GlStateManager.depthFunc(GL11.GL_LEQUAL);
										}
										GlStateManager.popMatrix();
									}
								}
								GlStateManager.depthMask(true);
								GlStateManager.enableTexture2D();
								GlStateManager.disableBlend();
							}
						}
					}
				}
			}
		}
	}
	
	private AxisAlignedBB limitBox(AxisAlignedBB box, AxisAlignedBB mask)
	{
		double d0 = Math.max(box.minX, mask.minX);
        double d1 = Math.max(box.minY, mask.minY);
        double d2 = Math.max(box.minZ, mask.minZ);
        double d3 = Math.min(box.maxX, mask.maxX);
        double d4 = Math.min(box.maxY, mask.maxY);
        double d5 = Math.min(box.maxZ, mask.maxZ);
        return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
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