package com.phylogeny.extrabitmanipulation.client;

import java.util.concurrent.TimeUnit;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.Disk;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Quadric;
import org.lwjgl.util.glu.Sphere;

import com.google.common.base.Stopwatch;
import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.client.gui.GuiBitToolSettingsMenu;
import com.phylogeny.extrabitmanipulation.config.ConfigShapeRender;
import com.phylogeny.extrabitmanipulation.config.ConfigShapeRenderPair;
import com.phylogeny.extrabitmanipulation.helper.BitAreaHelper;
import com.phylogeny.extrabitmanipulation.helper.BitAreaHelper.ModelingBoxSet;
import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;
import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper.ModelReadData;
import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper.SculptingData;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.init.KeyBindingsExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.item.ItemModelingTool;
import com.phylogeny.extrabitmanipulation.item.ItemSculptingTool;
import com.phylogeny.extrabitmanipulation.packet.PacketCycleBitWrenchMode;
import com.phylogeny.extrabitmanipulation.packet.PacketOpenBitMappingGui;
import com.phylogeny.extrabitmanipulation.packet.PacketReadBlockStates;
import com.phylogeny.extrabitmanipulation.packet.PacketSculpt;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.GuiIDs;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;
import com.phylogeny.extrabitmanipulation.reference.Reference;
import com.phylogeny.extrabitmanipulation.reference.Utility;

import mod.chiselsandbits.api.APIExceptions.CannotBeChiseled;
import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.IBitBrush;
import mod.chiselsandbits.api.IBitLocation;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import mod.chiselsandbits.api.ModKeyBinding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class ClientEventHandler
{
	private float millisecondsElapsed;
	private static Stopwatch timer;
	private Vec3d drawnStartPoint = null;
	private Vec3i drawnStartPointModelingTool = null;
	private static final ResourceLocation ARROW_HEAD = new ResourceLocation(Reference.GROUP_ID, "textures/overlays/ArrowHead.png");
	private static final ResourceLocation ARROW_BIDIRECTIONAL = new ResourceLocation(Reference.GROUP_ID, "textures/overlays/ArrowBidirectional.png");
	private static final ResourceLocation ARROW_CYCLICAL = new ResourceLocation(Reference.GROUP_ID, "textures/overlays/ArrowCyclical.png");
	private static final ResourceLocation CIRCLE = new ResourceLocation(Reference.GROUP_ID, "textures/overlays/Circle.png");
	private static final ResourceLocation INVERSION = new ResourceLocation(Reference.GROUP_ID, "textures/overlays/Inversion.png");
	private static final int[] DIRECTION_FORWARD = new int[]{2, 0, 5, 4, 1, 3};
	private static final int[] DIRECTION_BACKWARD = new int[]{1, 4, 0, 5, 3, 2};
	private static final int[] AXIS_FORWARD = new int[]{2, 3, 4, 5, 0, 1};
	private static final int[] AXIS_BACKWARD = new int[]{4, 5, 0, 1, 2, 3};
	private static final int[] SHAPE_CURVED = new int[]{1, 2, 0, 0, 0, 0, 0};
//	private static final int[] SHAPE_FLAT = new int[]{3, 3, 3, 4, 5, 6, 3}; TODO
	private static final int[] SHAPE_FLAT = new int[]{3, 3, 3, 6, 3, 3, 3};
	
	@SubscribeEvent
	public void registerTextures(@SuppressWarnings("unused") TextureStitchEvent.Pre event)
	{
		registerTexture(ARROW_HEAD);
		registerTexture(ARROW_BIDIRECTIONAL);
		registerTexture(ARROW_CYCLICAL);
		registerTexture(CIRCLE);
		registerTexture(INVERSION);
	}
	
	private void registerTexture(ResourceLocation resourceLocation)
	{
		SimpleTexture texture = new SimpleTexture(resourceLocation);
		Minecraft.getMinecraft().renderEngine.loadTexture(resourceLocation, texture);
	}
	
	@SubscribeEvent
	public void onKeyInput(@SuppressWarnings("unused") InputEvent.KeyInputEvent event)
	{
		if (ChiselsAndBitsAPIAccess.apiInstance.getKeyBinding(ModKeyBinding.MODE_MENU).isKeyDown()
				|| KeyBindingsExtraBitManipulation.OPEN_BIT_MAPPING_GUI.isKeyDown())
		{
			if (KeyBindingsExtraBitManipulation.OPEN_BIT_MAPPING_GUI.isKeyDown())
			{
				ItemStack stack = ClientHelper.getHeldItemMainhand();
				if ((ItemStackHelper.isModelingToolStack(stack) && ItemStackHelper.hasKey(stack, NBTKeys.SAVED_STATES))
						|| (stack != null && stack.hasTagCompound() && ItemStackHelper.isDesignStack(stack)))
					openBitMappingGui();
			}
			else
			{
				if (ItemStackHelper.isBitToolStack(ClientHelper.getHeldItemMainhand()))
						Minecraft.getMinecraft().displayGuiScreen(new GuiBitToolSettingsMenu());
			}
		}
	}
	
	private void openBitMappingGui()
	{
		EntityPlayer player = ClientHelper.getPlayer();
		player.openGui(ExtraBitManipulation.instance, GuiIDs.BIT_MAPPING_GUI.getID(), player.worldObj, 0, 0, 0);
		ExtraBitManipulation.packetNetwork.sendToServer(new PacketOpenBitMappingGui());
	}
	
	@SuppressWarnings("null")
	@SubscribeEvent
	public void interceptMouseInput(MouseEvent event)
	{
		EntityPlayer player = ClientHelper.getPlayer();
		if (event.getDwheel() != 0)
		{
			ItemStack stack = player.getHeldItemMainhand();
			if (ItemStackHelper.isBitToolStack(stack))
			{
				boolean forward = event.getDwheel() < 0;
				if (KeyBindingsExtraBitManipulation.SHIFT.isKeyDown())
				{
					if (ItemStackHelper.isBitWrenchItem(stack.getItem()))
					{
						ExtraBitManipulation.packetNetwork.sendToServer(new PacketCycleBitWrenchMode(forward));
					}
					else
					{
						cycleSemiDiameter(player, stack, forward);
					}
					event.setCanceled(true);
				}
				else if (ItemStackHelper.isSculptingToolItem(stack.getItem())
						&& (KeyBindingsExtraBitManipulation.CONTROL.isKeyDown()
								|| KeyBindingsExtraBitManipulation.ALT.isKeyDown()))
				{
					if (KeyBindingsExtraBitManipulation.CONTROL.isKeyDown())
					{
						cycleDirection(player, stack, forward);
					}
					else
					{
						cycleWallThickness(player, stack, forward);
					}
					event.setCanceled(true);
				}
			}
			else
			{
				drawnStartPoint = null;
			}
		}
		else if ((KeyBindingsExtraBitManipulation.CONTROL.isKeyDown() || KeyBindingsExtraBitManipulation.ALT.isKeyDown()) && event.isButtonstate())
		{
			ItemStack stack = player.getHeldItemMainhand();
			if (stack != null)
			{
				Item item = stack.getItem();
				if (ItemStackHelper.isSculptingToolItem(item))
				{
					if (KeyBindingsExtraBitManipulation.CONTROL.isKeyDown())
					{
						if (event.getButton() == 1)
							cycleShapeType(player, stack, item);
						
						if (event.getButton() == 0)
							toggleBitGridTargeted(player, stack);
					}
					else
					{
						if (event.getButton() == 1)
							toggleHollowShape(player, stack, item);
						
						if (event.getButton() == 0)
							toggleOpenEnds(player, stack);
					}
					event.setCanceled(true);
				}
			}
		}
		else if (event.getButton() == 0)
		{
			if (!player.capabilities.allowEdit)
				return;
			
			ItemStack stack = player.getHeldItemMainhand();
			if (stack != null)
			{
				Item item = stack.getItem();
				if (event.isButtonstate() && ItemStackHelper.isBitWrenchItem(item))
				{
					event.setCanceled(true);
				}
				else if (ItemStackHelper.isSculptingToolItem(item))
				{
					boolean drawnMode = BitToolSettingsHelper.getSculptMode(stack.getTagCompound()) == 2;
					if (!drawnMode)
						drawnStartPoint = null;
					
					if (event.isButtonstate() || (drawnMode && drawnStartPoint != null))
					{
						ItemSculptingTool toolItem = (ItemSculptingTool) item;
						boolean removeBits = toolItem.removeBits();
						RayTraceResult target = Minecraft.getMinecraft().objectMouseOver;
						boolean shiftDown = KeyBindingsExtraBitManipulation.SHIFT.isKeyDown();
						if (target != null && target.typeOfHit != RayTraceResult.Type.MISS)
						{
							if (target.typeOfHit == RayTraceResult.Type.BLOCK)
							{
								BlockPos pos = target.getBlockPos();
								EnumFacing side = target.sideHit;
								Vec3d hit = target.hitVec;
								boolean swingTool = true;
								if (drawnMode && event.isButtonstate() && drawnStartPoint != null)
								{
									event.setCanceled(true);
									return;
								}
								if (!shiftDown && drawnMode && event.isButtonstate())
								{
									IBitLocation bitLoc = ChiselsAndBitsAPIAccess.apiInstance.getBitPos((float) hit.xCoord - pos.getX(),
											(float) hit.yCoord - pos.getY(), (float) hit.zCoord - pos.getZ(), side, pos, false);
									if (bitLoc != null)
									{
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
										drawnStartPoint = new Vec3d(x2, y2, z2);
									}
									else
									{
										drawnStartPoint = null;
										swingTool = false;
									}
								}
								else
								{
									if (shiftDown)
									{
										IChiselAndBitsAPI api = ChiselsAndBitsAPIAccess.apiInstance;
										IBitLocation bitLoc = api.getBitPos((float) hit.xCoord - pos.getX(), (float) hit.yCoord - pos.getY(),
												(float) hit.zCoord - pos.getZ(), side, pos, false);
										if (bitLoc != null)
										{
											try
											{
												IBitAccess bitAccess = api.getBitAccess(player.worldObj, pos);
												IBitBrush bit = bitAccess.getBitAt(bitLoc.getBitX(), bitLoc.getBitY(), bitLoc.getBitZ());
												BitToolSettingsHelper.setBitStack(player, stack, removeBits, bit,
														removeBits ? Configs.sculptSetBitWire : Configs.sculptSetBitSpade);
												if ((removeBits ? Configs.sculptSetBitWire : Configs.sculptSetBitSpade).shouldDisplayInChat())
												{
													printChatMessageWithDeletion((removeBits ? "Removing only " : "Sculpting with ")
															+ BitToolSettingsHelper.getBitName(bit.getItemStack(1)));
												}
											}
											catch (CannotBeChiseled e)
											{
												event.setCanceled(true);
												return;
											}
										}
									}
									else if (!shiftDown || removeBits || drawnMode)
									{
										SculptingData sculptingData = new SculptingData(stack.getTagCompound(), toolItem);
										swingTool = toolItem.sculptBlocks(stack, player, player.worldObj, pos, side, hit, drawnStartPoint, sculptingData);
										ExtraBitManipulation.packetNetwork.sendToServer(new PacketSculpt(pos, side, hit, drawnStartPoint, sculptingData));
									}
									if (drawnMode && !event.isButtonstate())
										drawnStartPoint = null;
								}
								if (swingTool)
									player.swingArm(EnumHand.MAIN_HAND);
								
								event.setCanceled(true);
							}
						}
						else if (shiftDown && event.isButtonstate() && removeBits)
						{
							BitToolSettingsHelper.setBitStack(player, stack, true, null, Configs.sculptSetBitWire);
							if ((removeBits ? Configs.sculptSetBitWire : Configs.sculptSetBitSpade).shouldDisplayInChat())
								printChatMessageWithDeletion("Removing any/all bits");
						}
						else if (drawnMode)
						{
							drawnStartPoint = null;
						}
					}
				}
			}
		}
		else if (event.getButton() == 1 && event.isButtonstate())
		{
			ItemStack stack = player.getHeldItemMainhand();
			if (ItemStackHelper.isModelingToolStack(stack) && ItemStackHelper.hasKey(stack, NBTKeys.SAVED_STATES)
					&& KeyBindingsExtraBitManipulation.SHIFT.isKeyDown())
			{
				openBitMappingGui();
				event.setCanceled(true);
			}
		}
		if (!event.isCanceled() && event.getButton() == 1 && event.isButtonstate())
		{
			ItemStack stack = player.getHeldItemMainhand();
			if (ItemStackHelper.isSculptingToolStack(stack))
				cycleSculptMode(player, stack, !KeyBindingsExtraBitManipulation.SHIFT.isKeyDown());	
		}
		if (event.getDwheel() != 0)
		{
			ItemStack stack = player.getHeldItemMainhand();
			if (ItemStackHelper.isModelingToolStack(stack))
			{
				boolean forward = event.getDwheel() < 0;
				if (KeyBindingsExtraBitManipulation.CONTROL.isKeyDown() || KeyBindingsExtraBitManipulation.SHIFT.isKeyDown())
				{
					if (KeyBindingsExtraBitManipulation.SHIFT.isKeyDown())
					{
						cycleModelAreaMode(player, stack, forward);
					}
					else
					{
						cycleModelSnapMode(player, stack, forward);
					}
					event.setCanceled(true);
				}
			}
			else
			{
				drawnStartPointModelingTool = null;
			}
		}
		else if (KeyBindingsExtraBitManipulation.CONTROL.isKeyDown() && event.isButtonstate())
		{
			ItemStack stack = player.getHeldItemMainhand();
			if (ItemStackHelper.isModelingToolStack(stack))
			{
				if (event.getButton() == 1)
					toggleModelGuiOpen(player, stack);
				
				event.setCanceled(true);
			}
		}
		else if (event.getButton() == 0)
		{
			ItemStack stack = player.getHeldItemMainhand();
			if (ItemStackHelper.isModelingToolStack(stack))
			{
				Item item = stack.getItem();
				if (item != null)
				{
					boolean drawnMode = BitToolSettingsHelper.getModelAreaMode(stack.getTagCompound()) == 2;
					if (!drawnMode)
						drawnStartPointModelingTool = null;
					
					if (event.isButtonstate() || (drawnMode && drawnStartPointModelingTool != null))
					{
						RayTraceResult target = Minecraft.getMinecraft().objectMouseOver;
						if (target != null && target.typeOfHit != RayTraceResult.Type.MISS)
						{
							if (target.typeOfHit == RayTraceResult.Type.BLOCK)
							{
								BlockPos pos = target.getBlockPos();
								Vec3d hit = target.hitVec;
								boolean swingTool = true;
								if (drawnMode && event.isButtonstate() && drawnStartPointModelingTool != null)
								{
									event.setCanceled(true);
									return;
								}
								if (!KeyBindingsExtraBitManipulation.SHIFT.isKeyDown() && drawnMode && event.isButtonstate())
								{
									drawnStartPointModelingTool = new Vec3i(pos.getX(), pos.getY(), pos.getZ());
								}
								else
								{
									if (!KeyBindingsExtraBitManipulation.SHIFT.isKeyDown() || drawnMode)
									{
										ModelReadData modelingData = new ModelReadData(stack.getTagCompound());
										swingTool = BitAreaHelper.readBlockStates(stack, player, player.worldObj, pos, hit,
												drawnStartPointModelingTool, modelingData);
										ExtraBitManipulation.packetNetwork.sendToServer(new PacketReadBlockStates(pos, hit,
												drawnStartPointModelingTool, modelingData));
									}
									if (drawnMode && !event.isButtonstate())
										drawnStartPointModelingTool = null;
								}
								if (swingTool)
									player.swingArm(EnumHand.MAIN_HAND);
								
								event.setCanceled(true);
							}
						}
						else if (drawnMode)
						{
							drawnStartPointModelingTool = null;
						}
					}
				}
			}
		}
	}
	
	private void cycleModelAreaMode(EntityPlayer player, ItemStack stack, boolean forward)
	{
		int mode = BitToolSettingsHelper.cycleData(BitToolSettingsHelper.getModelAreaMode(stack.getTagCompound()),
				forward, ItemModelingTool.AREA_MODE_TITLES.length);
		BitToolSettingsHelper.setModelAreaMode(player, stack, mode, Configs.modelAreaMode);
		if (Configs.modelAreaMode.shouldDisplayInChat())
			printChatMessageWithDeletion(BitToolSettingsHelper.getModelAreaModeText(mode));
	}
	
	private void cycleModelSnapMode(EntityPlayer player, ItemStack stack, boolean forward)
	{
		int mode = BitToolSettingsHelper.cycleData(BitToolSettingsHelper.getModelSnapMode(stack.getTagCompound()),
				forward, ItemModelingTool.SNAP_MODE_TITLES.length);
		BitToolSettingsHelper.setModelSnapMode(player, stack, mode, Configs.modelSnapMode);
		if (Configs.modelSnapMode.shouldDisplayInChat())
			printChatMessageWithDeletion(BitToolSettingsHelper.getModelSnapModeText(mode));
	}
	
	private void toggleModelGuiOpen(EntityPlayer player, ItemStack stack)
	{
		boolean modelGuiOpen = !BitToolSettingsHelper.getModelGuiOpen(stack.getTagCompound());
		BitToolSettingsHelper.setModelGuiOpen(player, stack, modelGuiOpen, Configs.modelGuiOpen);
		if (Configs.modelGuiOpen.shouldDisplayInChat())
			printChatMessageWithDeletion(BitToolSettingsHelper.getModelGuiOpenText(modelGuiOpen));
	}
	
	private void cycleSculptMode(EntityPlayer player, ItemStack stack, boolean forward)
	{
		int mode = BitToolSettingsHelper.cycleData(BitToolSettingsHelper.getSculptMode(stack.getTagCompound()), forward, ItemSculptingTool.MODE_TITLES.length);
		BitToolSettingsHelper.setSculptMode(player, stack, mode, Configs.sculptMode);
		if (Configs.sculptMode.shouldDisplayInChat())
			printChatMessageWithDeletion(BitToolSettingsHelper.getSculptModeText(mode));
	}
	
	private void cycleDirection(EntityPlayer player, ItemStack stack, boolean forward)
	{
		NBTTagCompound nbt = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
		int direction = BitToolSettingsHelper.getDirection(nbt);
		int shapeType = BitToolSettingsHelper.getShapeType(nbt, ((ItemSculptingTool) stack.getItem()).isCurved());
		int rotation = direction / 6;
		direction %= 6;
		if (!(shapeType == 4 && (forward ? rotation != 1 : rotation != 0)) && !(shapeType == 5 && (forward ? rotation != 3 : rotation != 0)))
		{
			direction = shapeType == 2 || shapeType > 3 ? (forward ? DIRECTION_FORWARD[direction] : DIRECTION_BACKWARD[direction])
					: (forward ? AXIS_FORWARD[direction] : AXIS_BACKWARD[direction]);
			rotation = forward ? 0 : (shapeType == 4 ? 1 : 3);
		}
		else
		{
			rotation = shapeType == 4 ? (rotation == 0 ? 1 : 0) : BitToolSettingsHelper.cycleData(rotation, forward, 4);
		}
		direction += 6 * rotation;
		BitToolSettingsHelper.setDirection(player, stack, direction, Configs.sculptDirection);
		if (Configs.sculptDirection.shouldDisplayInChat())
			printChatMessageWithDeletion(BitToolSettingsHelper.getDirectionText(direction, shapeType == 4 || shapeType == 5));
	}
	
	private void cycleShapeType(EntityPlayer player, ItemStack stack, Item item)
	{
		boolean isCurved = ((ItemSculptingTool) item).isCurved();
		NBTTagCompound nbt = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
		int shapeType = BitToolSettingsHelper.getShapeType(nbt, isCurved);
		shapeType = isCurved ? SHAPE_CURVED[shapeType] : SHAPE_FLAT[shapeType];
		BitToolSettingsHelper.setShapeType(player, stack, isCurved, shapeType, isCurved ? Configs.sculptShapeTypeCurved : Configs.sculptShapeTypeFlat);
		if ((isCurved ? Configs.sculptShapeTypeCurved : Configs.sculptShapeTypeFlat).shouldDisplayInChat())
			printChatMessageWithDeletion(BitToolSettingsHelper.getShapeTypeText(shapeType));
	}
	
	private void toggleBitGridTargeted(EntityPlayer player, ItemStack stack)
	{
		boolean targetBitGrid = !BitToolSettingsHelper.isBitGridTargeted(stack.getTagCompound());
		BitToolSettingsHelper.setBitGridTargeted(player, stack, targetBitGrid, Configs.sculptTargetBitGridVertexes);
		if (Configs.sculptTargetBitGridVertexes.shouldDisplayInChat())
			printChatMessageWithDeletion(BitToolSettingsHelper.getBitGridTargetedText(targetBitGrid));
	}
	
	private void cycleSemiDiameter(EntityPlayer player, ItemStack stack, boolean forward)
	{
		int semiDiameter = BitToolSettingsHelper.cycleData(BitToolSettingsHelper.getSemiDiameter(stack.getTagCompound()),
				forward, Configs.maxSemiDiameter);
		BitToolSettingsHelper.setSemiDiameter(player, stack, semiDiameter, Configs.sculptSemiDiameter);
		if (Configs.sculptSemiDiameter.shouldDisplayInChat())
			printChatMessageWithDeletion(BitToolSettingsHelper.getSemiDiameterText(stack.getTagCompound(), semiDiameter));
	}
	
	private void toggleHollowShape(EntityPlayer player, ItemStack stack, Item item)
	{
		boolean isWire = ((ItemSculptingTool) item).removeBits();
		boolean isHollowShape = !BitToolSettingsHelper.isHollowShape(stack.getTagCompound(), isWire);
		BitToolSettingsHelper.setHollowShape(player, stack, isWire, isHollowShape, isWire ? Configs.sculptHollowShapeWire : Configs.sculptHollowShapeSpade);
		if ((isWire ? Configs.sculptHollowShapeWire : Configs.sculptHollowShapeSpade).shouldDisplayInChat())
			printChatMessageWithDeletion(BitToolSettingsHelper.getHollowShapeText(isHollowShape));
	}
	
	private void toggleOpenEnds(EntityPlayer player, ItemStack stack)
	{
		boolean areEndsOpen = !BitToolSettingsHelper.areEndsOpen(stack.getTagCompound());
		BitToolSettingsHelper.setEndsOpen(player, stack, areEndsOpen, Configs.sculptOpenEnds);
		if (Configs.sculptOpenEnds.shouldDisplayInChat())
			printChatMessageWithDeletion(BitToolSettingsHelper.getOpenEndsText(areEndsOpen));
	}
	
	private void cycleWallThickness(EntityPlayer player, ItemStack stack, boolean forward)
	{
		int wallThickness = BitToolSettingsHelper.cycleData(BitToolSettingsHelper.getWallThickness(stack.getTagCompound()),
				forward, Configs.maxWallThickness);
		BitToolSettingsHelper.setWallThickness(player, stack, wallThickness, Configs.sculptWallThickness);
		if (Configs.sculptWallThickness.shouldDisplayInChat())
			printChatMessageWithDeletion(BitToolSettingsHelper.getWallThicknessText(wallThickness));
	}
	
	private void printChatMessageWithDeletion(String text)
	{
		GuiNewChat chatGUI = Minecraft.getMinecraft().ingameGUI.getChatGUI();
		chatGUI.printChatMessageWithOptionalDeletion(new TextComponentString(text), 627250);
	}
	
	@SuppressWarnings("null")
	@SubscribeEvent
	public void cancelBoundingBoxDraw(DrawBlockHighlightEvent event)
	{
		ItemStack stack = event.getPlayer().getHeldItemMainhand();
		if (ItemStackHelper.isSculptingToolStack(stack) && BitToolSettingsHelper.getSculptMode(stack.getTagCompound()) == 1)
			event.setCanceled(true);
	}
	
	@SubscribeEvent
	public void renderBoxesSpheresAndOverlays(RenderWorldLastEvent event)
	{
		if (!Configs.disableOverlays)
		{
			EntityPlayer player = ClientHelper.getPlayer();
			World world = player.worldObj;
			ItemStack stack = player.getHeldItemMainhand();
			if (stack != null)
			{
				RayTraceResult target = Minecraft.getMinecraft().objectMouseOver;
				if (target != null && target.typeOfHit.equals(RayTraceResult.Type.BLOCK) && ItemStackHelper.isBitToolItem(stack.getItem()))
				{
					IChiselAndBitsAPI api = ChiselsAndBitsAPIAccess.apiInstance;
					float ticks = event.getPartialTicks();
					double playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * ticks;
					double playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * ticks;
					double playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * ticks;
					EnumFacing dir = target.sideHit;
					Tessellator t = Tessellator.getInstance();
					VertexBuffer vb = t.getBuffer();
					BlockPos pos = target.getBlockPos();
					int x = pos.getX();
					int y = pos.getY();
					int z = pos.getZ();
					double diffX = playerX - x;
					double diffY = playerY - y;
					double diffZ = playerZ - z;
					Vec3d hit = target.hitVec;
					if (ItemStackHelper.isBitWrenchItem(stack.getItem()) && api.isBlockChiseled(world, target.getBlockPos()))
					{
						int mode = ItemStackHelper.getNBTOrNew(stack).getInteger(NBTKeys.WRENCH_MODE);
						if (timer == null)
							timer = Stopwatch.createStarted();
						
						millisecondsElapsed = timer.elapsed(TimeUnit.MILLISECONDS);
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
						
						boolean invertDirection = KeyBindingsExtraBitManipulation.SHIFT.isKeyDown();
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
							if (side % 2 == 1)
								angle += 180;
							
							if (side >= 4)
								angle -= 90;
						}
						else
						{
							if (mode == 0)
							{
								if (side % 2 == (invertDirection ? 0 : 1))
									angle *= -1;
							}
							else
							{
								if (side < 2 || side > 3)
									angle *= -1;
							}
							if (eastWest)
								angle += 90;
							
							if (side == (mode == 1 ? 1 : 0) || side == 3 || side == 4)
								angle += 180;
						}
						double offsetX2 = 0.5 * invOffsetX;
						double offsetY2 = 0.5 * invOffsetY;
						double offsetZ2 = 0.5 * invOffsetZ;
						
						double mirTravel = mode == 1 ? Configs.mirrorAmplitude * Math.cos(Math.PI * 2 * millisecondsElapsed / Configs.mirrorPeriod) : 0;
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
						
						Minecraft.getMinecraft().renderEngine.bindTexture(mode == 0 ? ARROW_CYCLICAL
								: (mode == 1 ? ARROW_BIDIRECTIONAL : (mode == 2 ? CIRCLE : INVERSION)));
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
							box = contractBoxOrRenderArrows(true, t, vb, side, northSouth, dir2, box, invOffsetX,
									invOffsetY, invOffsetZ, invertDirection, minU, maxU, minV, maxV);
						}
						
						renderTexturedSide(t, vb, side, northSouth, box, minU, maxU, minV, maxV, 1);
						GlStateManager.popMatrix();
						
						AxisAlignedBB box3 = world.getBlockState(pos).getSelectedBoundingBox(world, pos);
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
											Minecraft.getMinecraft().renderEngine.bindTexture(ARROW_HEAD);
											angle = 90;
											if (side % 2 == 0)
												angle += 180;
											
											if (invertDirection)
												angle += 180;
											
											mode2 = 2;
										}
										else
										{
											Minecraft.getMinecraft().renderEngine.bindTexture(ARROW_CYCLICAL);
											mode2 = 0;
										}
									}
									else if (mode == 2)
									{
										if (!oppRotation)
										{
											Minecraft.getMinecraft().renderEngine.bindTexture(ARROW_HEAD);
											if (side == 0 ? s == 2 || s == 5 : (side == 1 ? s == 3 || s == 4 : (side == 2 ? s == 1 || s == 5
													: (side == 3 ? s == 0 || s == 4 : (side == 4 ? s == 1 || s == 2 : s == 0 || s == 3)))))
												angle += 180;
											
											if (invertDirection)
												angle += 180;
										}
										else
										{
											Minecraft.getMinecraft().renderEngine.bindTexture(CIRCLE);
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
									if (s % 2 == 1)
										angle += 180;
									
									if (s >= 4)
										angle -= 90;
								}
								else
								{
									if (mode2 == 0)
									{
										if (s % 2 == (invertDirection ? 0 : 1))
											angle *= -1;
										
										if (oppRotation)
											angle *= -1;
									}
									else
									{
										if (s < 2 || s > 3)
											angle *= -1;
									}
									if (eastWest)
										angle -= 90;
									
									if (s == (mode2 == 1 ? 1 : 0) || s == 3 || s == 5)
										angle += 180;
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
									box = contractBoxOrRenderArrows(oppRotation, t, vb, side, northSouth, dir2, box, invOffsetX,
											invOffsetY, invOffsetZ, invertDirection, minU, maxU, minV, maxV);
								}
								if (mode2 != 2 || oppRotation)
									renderTexturedSide(t, vb, s, northSouth, box, minU, maxU, minV, maxV, 1);
								
								GlStateManager.popMatrix();
							}
						}
						
						GlStateManager.enableLighting();
						GlStateManager.disableBlend();
						GlStateManager.enableTexture2D();
						GlStateManager.popMatrix();
					}
					else if (ItemStackHelper.isSculptingToolItem(stack.getItem()))
					{
						ItemSculptingTool toolItem = (ItemSculptingTool) stack.getItem();
						boolean removeBits = toolItem.removeBits();
						int mode = BitToolSettingsHelper.getSculptMode(stack.getTagCompound());
						if (!removeBits || mode > 0 || api.canBeChiseled(world, target.getBlockPos()))
						{
							float hitX = (float) hit.xCoord - pos.getX();
							float hitY = (float) hit.yCoord - pos.getY();
							float hitZ = (float) hit.zCoord - pos.getZ();
							IBitLocation bitLoc = api.getBitPos(hitX, hitY, hitZ, dir, pos, false);
							if (bitLoc != null)
							{
								NBTTagCompound nbt = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
								int x2 = bitLoc.getBitX();
								int y2 = bitLoc.getBitY();
								int z2 = bitLoc.getBitZ();
								if (!toolItem.removeBits())
								{
									x2 += dir.getFrontOffsetX();
									y2 += dir.getFrontOffsetY();
									z2 += dir.getFrontOffsetZ();
								}
								boolean isDrawn = drawnStartPoint != null;
								boolean drawnBox = mode == 2 && isDrawn;
								int shapeType = BitToolSettingsHelper.getShapeType(nbt, toolItem.isCurved());
								boolean fixedNotSym = !drawnBox && shapeType == 2 || shapeType > 4;
								glStart();
								double r = BitToolSettingsHelper.getSemiDiameter(nbt) * Utility.PIXEL_D;
								ConfigShapeRenderPair configPair = Configs.itemShapeMap.get(toolItem);
								ConfigShapeRender configBox = configPair.boundingBox;
								AxisAlignedBB box = null, shapeBox = null;
								double x3 = x + x2 * Utility.PIXEL_D;
								double y3 = y + y2 * Utility.PIXEL_D;
								double z3 = z + z2 * Utility.PIXEL_D;
								if (configBox.renderInnerShape || configBox.renderOuterShape)
								{
									GlStateManager.pushMatrix();
									GL11.glLineWidth(configBox.lineWidth);
									boolean inside = ItemSculptingTool.wasInsideClicked(dir, hit, pos);
									if (drawnBox)
									{
										double x4 = drawnStartPoint.xCoord;
										double y4 = drawnStartPoint.yCoord;
										double z4 = drawnStartPoint.zCoord;
										if (Math.max(x3, x4) == x3)
										{
											x3 += Utility.PIXEL_D;
										}
										else
										{
											x4 += Utility.PIXEL_D;
										}
										if (Math.max(y3, y4) == y3)
										{
											y3 += Utility.PIXEL_D;
										}
										else
										{
											y4 += Utility.PIXEL_D;
										}
										if (Math.max(z3, z4) == z3)
										{
											z3 += Utility.PIXEL_D;
										}
										else
										{
											z4 += Utility.PIXEL_D;
										}
										box = new AxisAlignedBB(x4, y4, z4, x3, y3, z3);
									}
									else
									{
										double f = 0;
										Vec3d vecOffset = new Vec3d(0, 0, 0);
										boolean targetBitGrid = BitToolSettingsHelper.isBitGridTargeted(nbt);
										if (mode == 2)
										{
											r = 0;
										}
										else if (targetBitGrid)
										{
											f = Utility.PIXEL_D * 0.5;
											vecOffset = BitAreaHelper.getBitGridOffset(dir, inside, hitX, hitY, hitZ, removeBits);
											r -= f;
										}
										box = new AxisAlignedBB(x - r, y - r, z - r, x + r + Utility.PIXEL_D, y + r + Utility.PIXEL_D, z + r + Utility.PIXEL_D)
													.offset(x2 * Utility.PIXEL_D + f * vecOffset.xCoord,
															y2 * Utility.PIXEL_D + f * vecOffset.yCoord,
															z2 * Utility.PIXEL_D + f * vecOffset.zCoord);
										boolean placementOffset = BitToolSettingsHelper.isShapeOffset(nbt) && !removeBits && mode != 2;
										double r2 = r + (targetBitGrid ? Utility.PIXEL_D * 0.5 : 0);
										if (placementOffset)
											box = box.offset(dir.getFrontOffsetX() * r2, dir.getFrontOffsetY() * r2, dir.getFrontOffsetZ() * r2);
										
										if (targetBitGrid && mode != 2)
										{
											x3 = (box.maxX + box.minX) * 0.5 - f;
											y3 = (box.maxY + box.minY) * 0.5 - f;
											z3 = (box.maxZ + box.minZ) * 0.5 - f;
										}
										if (!targetBitGrid && placementOffset)
										{
											x3 += dir.getFrontOffsetX() * r2;
											y3 += dir.getFrontOffsetY() * r2;
											z3 += dir.getFrontOffsetZ() * r2;
										}
									}
									if (fixedNotSym)
										shapeBox = box.expandXyz(0);
									
									if (mode == 0)
									{
										BlockPos pos2 = !removeBits && !inside ? pos.offset(dir) : pos;
										AxisAlignedBB box2 = !removeBits ? new AxisAlignedBB(pos2) :
											world.getBlockState(pos2).getSelectedBoundingBox(world, pos2);
										if ((int) Math.round(box2.minX) != pos2.getX() || (int) Math.round(box2.minY) != pos2.getY()
												|| (int) Math.round(box2.minZ) != pos2.getZ())
											box2 = box2.offset(pos2);
										
										box = limitBox(box, box2);
									}
									double f = 0.0020000000949949026;
									if (configBox.renderOuterShape)
										RenderGlobal.func_189697_a(box.expandXyz(f).offset(-playerX, -playerY, -playerZ),
												configBox.red, configBox.green, configBox.blue, configBox.outerShapeAlpha);
									
									if (configBox.renderInnerShape)
									{
										GlStateManager.depthFunc(GL11.GL_GREATER);
										RenderGlobal.func_189697_a(box.expandXyz(f).offset(-playerX, -playerY, -playerZ),
												configBox.red, configBox.green, configBox.blue, configBox.innerShapeAlpha);
										GlStateManager.depthFunc(GL11.GL_LEQUAL);
									}
									GlStateManager.popMatrix();
								}
								if (!fixedNotSym && box != null)
									shapeBox = box.expandXyz(0);
								
								boolean isHollow = BitToolSettingsHelper.isHollowShape(nbt, removeBits);
								boolean isOpen = isHollow && BitToolSettingsHelper.areEndsOpen(nbt);
								renderEnvelopedShapes(shapeType, nbt, playerX, playerY, playerZ, isDrawn,
										drawnBox, r, configPair, shapeBox, x3, y3, z3, 0, isOpen);
								float wallThickness = BitToolSettingsHelper.getWallThickness(nbt) * Utility.PIXEL_F;
								if (wallThickness > 0 && isHollow && !(mode == 2 && !drawnBox))
									renderEnvelopedShapes(shapeType, nbt, playerX, playerY, playerZ, isDrawn, drawnBox, r, configPair, shapeBox,
											x3, y3, z3, wallThickness, isOpen);
								
								glEnd();
							}
						}
					}
					else if (ItemStackHelper.isModelingToolItem(stack.getItem()))
					{
						glStart();
						ModelingBoxSet boxSet = BitAreaHelper.getModelingToolBoxSet(player, x, y, z, hit,
								drawnStartPointModelingTool, true, BitToolSettingsHelper.getModelAreaMode(stack.getTagCompound()),
								BitToolSettingsHelper.getModelSnapMode(stack.getTagCompound()));
						if (!boxSet.isEmpty())
						{
							renderModelingToolBoundingBox(boxSet.getBoundingBox().offset(-playerX, -playerY, -playerZ), 115);
							if (boxSet.hasPoint())
								renderModelingToolBoundingBox(boxSet.getPoint().offset(-playerX, -playerY, -playerZ), 155);
						}
						glEnd();
					}
				}
			}
		}
	}
	
	private void glStart()
	{
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.disableTexture2D();
		GlStateManager.depthMask(false);
	}
	
	private void glEnd()
	{
		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}
	
	private void renderModelingToolBoundingBox(AxisAlignedBB boxBounding, int outerAlpha)
	{
		RenderGlobal.func_189697_a(boxBounding, 1, 1, 1, outerAlpha / 255.0F);
		GlStateManager.depthFunc(GL11.GL_GREATER);
		RenderGlobal.func_189697_a(boxBounding, 1, 1, 1, 28 / 255.0F);
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
	}
	
	private void renderEnvelopedShapes(int shapeType, NBTTagCompound nbt, double playerX,
			double playerY, double playerZ, boolean isDrawn, boolean drawnBox, double r, ConfigShapeRenderPair configPair,
			AxisAlignedBB box, double x, double y, double z, double contraction, boolean isOpen)
	{
		ConfigShapeRender configShape = configPair.envelopedShape;
		if (configShape.renderInnerShape || configShape.renderOuterShape)
		{
			double a = 0, b = 0, c = 0;
			/* 0 = sphere
			 * 1 = cylinder
			 * 2 = cone
			 * 3 = cube
			 * 4 = triangular prism
			 * 5 = triangular pyramid
			 * 6 = square pyramid
			 */
			int dir = BitToolSettingsHelper.getDirection(nbt);
//			int rotation = dir / 6;
			dir %= 6;
			boolean notFullSym = shapeType != 0 && shapeType != 3;
			boolean notSym = shapeType == 2 || shapeType > 4;
			double ri = r + Utility.PIXEL_D * 0.5;
			r = Math.max(ri - contraction, 0);
			boolean drawnNotSym = notSym && drawnBox;
			double base = 0;
			double v;
			if (drawnBox || notSym)
			{
				double f = 0.5;
				double minX = box.minX * f;
				double minY = box.minY * f;
				double minZ = box.minZ * f;
				double maxX = box.maxX * f;
				double maxY = box.maxY * f;
				double maxZ = box.maxZ * f;
				double x2 = maxX - minX;
				double y2 = maxY - minY;
				double z2 = maxZ - minZ;
				if (drawnNotSym)
				{
					if (dir == 2 || dir == 3)
					{
						v = y2;
						y2 = z2;
						z2 = v;
					}
					else if (dir > 3)
					{
						v = y2;
						y2 = x2;
						x2 = v;
					}
				}
				if (notSym && contraction > 0)
				{
					if (!isOpen)
						base = contraction;
					
					y2 *= 2;
					double y2sq = y2 * y2;
					double aInset = (Math.sqrt(x2 * x2 + y2sq) * contraction) / x2 + base;
					double cInset = (Math.sqrt(z2 * z2 + y2sq) * contraction) / z2 + base;
					a = Math.max((y2 - aInset) * (x2 / y2), 0);
					c = Math.max((y2 - cInset) * (z2 / y2), 0);
					contraction = Math.min(aInset - base, cInset - base);
					b = Math.max(y2 * 0.5 - contraction * 0.5 - base * 0.5, 0);
				}
				else
				{
					a = Math.max(x2 - (!isOpen || !notFullSym || dir < 4 ? contraction : 0), 0);
					c = Math.max(z2 - (!isOpen || !notFullSym || dir != 2 && dir != 3 ? contraction : 0), 0);
					b = Math.max(y2 - (!isOpen || !notFullSym || dir > 1 ? contraction : 0), 0);
				}
				r = Math.max(Math.max(a, b), c);
				x = maxX + minX;
				y = maxY + minY;
				z = maxZ + minZ;
				if (drawnBox)
				{
					if (notSym || !notFullSym)
					{
						if (dir < 2 || dir > 3 || !notFullSym)
						{
							v = b;
							b = c;
							c = v;
						}
					}
					else
					{
						if (dir < 2)
						{
							v = b;
							b = c;
							c = v;
						}
						else if (dir > 3)
						{
							v = a;
							a = c;
							c = v;
						}
						else
						{
							v = b;
							b = a;
							a = v;
						}
					}
				}
			}
			else
			{
				a = b = c = r;
				if (b > 0 && notFullSym && isOpen)
					b += contraction * (isDrawn ? 0 : 1);
			}
			Quadric shape = shapeType > 2 ? new Prism(shapeType > 4, shapeType == 4 || shapeType == 5) : (notFullSym ? new Cylinder() : new Sphere());
			shape.setDrawStyle(GLU.GLU_LINE);
			Quadric lid = new Disk();
			lid.setDrawStyle(GLU.GLU_LINE);
			GlStateManager.pushMatrix();
			GL11.glLineWidth(configShape.lineWidth);
			double x2 = x - playerX;
			double y2 = y - playerY;
			double z2 = z - playerZ;
			if (!notSym && !isDrawn)
			{
				double hp = Utility.PIXEL_D * 0.5;
				x2 += hp;
				y2 += hp;
				z2 += hp;
			}
			if (notFullSym)
			{
				if (isOpen && contraction > 0 && !notSym)
				{
					double offset = contraction * (notSym ? 0.5 : (drawnBox ? 0 : -1));
					if (dir != 3)
						y2 += dir == 0 ? offset : -offset;
					
					if (dir > 2)
						x2 += dir == 5 ? -offset : offset;
					
					if (dir == 2 || dir == 3)
						z2 += dir == 2 ? offset : -offset;
				}
			}
			GlStateManager.translate(x2, y2, z2);
			int rot2 = dir;
			if (!(drawnNotSym && dir == 2))
			{
				if (notFullSym && rot2 != 1)
				{
					int angle = 90;
					if (rot2 == 3)
					{
						rot2 = 0;
						angle = 180;
						if (!(drawnNotSym && dir == 3))
							GlStateManager.rotate(90, 0, 0, 1);
					}
					else if (rot2 > 1)
					{
						rot2 %= 4;
					}
					else
					{
						rot2 = rot2 ^ 1 + 4;
					}
					Vec3i vec = EnumFacing.getFront(rot2).getOpposite().getDirectionVec();
					GlStateManager.rotate(angle, vec.getX(), vec.getY(), vec.getZ());
				}
				else
				{
					GlStateManager.rotate(90, 1, 0, 0);
				}
			}
			boolean openSym = notFullSym && !notSym && isOpen && !isDrawn;
			if (notFullSym)
			{
				double offset1 = 0;
				double offset2 = 0;
				double r2 = r;
				if (notSym)
				{
					r2 -= contraction * 0.5 - base * 0.5;
				}
				else if (openSym)
				{
					double m = -contraction;
					if (dir == 0)
						m *= 2;
					
					if (dir != 1)
						r -= m;
					
					if (dir > 1)
					{
						if (dir < 3)
						{
							offset1 = m;
						}
						else
						{
							offset2 = m;
						}
					}
				}
				GlStateManager.translate(offset1, offset2, -r2);
			}
			if (openSym)
			{
				v = b;
				b = c;
				c = v;
			}
			if (drawnNotSym)
			{
				if (dir == 2 || dir == 3)
				{
					v = b;
					b = c;
					c = v;
				}
				else if (dir > 3)
				{
					v = b;
					b = a;
					a = v;
				}
			}
			if (notFullSym && drawnBox)
			{
				if (b > c && b > a)
				{
					GlStateManager.translate(0, 0, b - c);
				}
				else if (a > c && a >= b)
				{
					GlStateManager.translate(0, 0, a - c);
				}
			}
			GlStateManager.scale(a / ri, b / ri, c / ri);
			if (configShape.renderOuterShape)
				drawEnvelopedShapes(ri, configShape, shapeType, shape, lid, true, notSym, isOpen);
			
			if (configShape.renderInnerShape)
			{
				GlStateManager.depthFunc(GL11.GL_GREATER);
				drawEnvelopedShapes(ri, configShape, shapeType, shape, lid, false, notSym, isOpen);
				GlStateManager.depthFunc(GL11.GL_LEQUAL);
			}
			GlStateManager.popMatrix();
		}
	}
	
	private void drawEnvelopedShapes(double r, ConfigShapeRender configShape, int shapeType, Quadric shape,
			Quadric lid, boolean isOuter, boolean isCylinder, boolean isOpen)
	{
		GlStateManager.pushMatrix();
		drawEnvelopedShape(shape, r, isOuter, configShape, isCylinder, isOpen);
		if (shapeType > 0 && shapeType < 3 && !isOpen)
		{
			if (shapeType == 1)
				drawEnvelopedShape(lid, r, isOuter, configShape, isCylinder, isOpen);
			
			GlStateManager.translate(0, 0, r * 2);
			drawEnvelopedShape(lid, r, isOuter, configShape, isCylinder, isOpen);
		}
		GlStateManager.popMatrix();
	}
	
	private void drawEnvelopedShape(Quadric shape, double radius, boolean isOuter,
			ConfigShapeRender configShape, boolean isCone, boolean isOpen)
	{
		GlStateManager.pushMatrix();
		GlStateManager.color(configShape.red, configShape.green,
				configShape.blue, isOuter ? configShape.outerShapeAlpha : configShape.innerShapeAlpha);
		float r = (float) radius;
		if (shape instanceof Prism)
		{
			((Prism) shape).draw(r, isOpen);
		}
		else if (shape instanceof Sphere)
		{
			((Sphere) shape).draw(r, 32, 32);
		}
		else if (shape instanceof Cylinder)
		{
			((Cylinder) shape).draw(isCone ? 0 : r, r, r * 2, 32, 32);
		}
		else if (shape instanceof Disk)
		{
			((Disk) shape).draw(0, r, 32, 32);
		}
		GlStateManager.popMatrix();
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
		return mode == 0 ? (millisecondsElapsed * (360.0 / Configs.rotationPeriod)) % 360 : 0;
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
	
	private AxisAlignedBB contractBoxOrRenderArrows(boolean contractBox, Tessellator t, VertexBuffer vb, int side, boolean northSouth, EnumFacing dir,
			AxisAlignedBB box, double invOffsetX, double invOffsetY, double invOffsetZ, boolean invertDirection, float minU, float maxU, float minV, float maxV)
	{
		if (contractBox)
		{
			double amount = (millisecondsElapsed % Configs.translationScalePeriod) / Configs.translationScalePeriod;
			amount /= invertDirection ? -2 : 2;
			if (invertDirection && Configs.translationScalePeriod > 1)
				amount += 0.5;
			
			box = box.expand(-amount * invOffsetX, -amount * invOffsetY, -amount * invOffsetZ);
		}
		else if (Configs.translationDistance > 0)
		{
			double distance = Configs.translationDistance;
			double fadeDistance = Configs.translationFadeDistance;
			double period = Configs.translationMovementPeriod;
			double offsetDistance = Configs.translationOffsetDistance;
			int timeOffset = offsetDistance > 0 ? (int) (period / (distance / offsetDistance)) : 0;
			if (timeOffset > period / 3.0)
				timeOffset = (int) (period / 3.0);
			
			if (fadeDistance > distance / 2.0)
				fadeDistance = distance / 2.0;
			
			int n = offsetDistance == 0 || period == 1 ? 1 : 3;
			for (int i = 0; i < n; i++)
			{
				double amount = ((millisecondsElapsed + timeOffset * i) % period) / (period / (distance * 100.0) * 100.0);
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
				renderTexturedSide(t, vb, side, northSouth, box2, minU, maxU, minV, maxV, alpha);
			}
		}
		else
		{
			renderTexturedSide(t, vb, side, northSouth, box, minU, maxU, minV, maxV, 1);
		}
		return box;
	}
	
	private void renderTexturedSide(Tessellator t, VertexBuffer vb, int side, boolean northSouth,
			AxisAlignedBB box, float minU, float maxU, float minV, float maxV, double alpha)
	{
		GL11.glColor4d(1, 1, 1, alpha);
		if (side == 1 || side == 3 || side == 4)
		{
			vb.begin(7, DefaultVertexFormats.POSITION_TEX);
			vb.pos(box.minX, box.minY, box.maxZ).tex(maxU, minV).endVertex();
			vb.pos(box.maxX, northSouth ? box.minY : box.maxY, box.maxZ).tex(minU, minV).endVertex();
			vb.pos(box.maxX, box.maxY, box.minZ).tex(minU, maxV).endVertex();
			vb.pos(box.minX, northSouth ? box.maxY : box.minY, box.minZ).tex(maxU, maxV).endVertex();
			t.draw();
			vb.begin(7, DefaultVertexFormats.POSITION_TEX);
			vb.pos(box.maxX, northSouth ? box.minY : box.maxY, box.maxZ).tex(minU, minV).endVertex();
			vb.pos(box.minX, box.minY, box.maxZ).tex(maxU, minV).endVertex();
			vb.pos(box.minX, northSouth ? box.maxY : box.minY, box.minZ).tex(maxU, maxV).endVertex();
			vb.pos(box.maxX, box.maxY, box.minZ).tex(minU, maxV).endVertex();
			t.draw();
		}
		else
		{
			vb.begin(7, DefaultVertexFormats.POSITION_TEX);
			vb.pos(box.minX, northSouth ? box.maxY : box.minY, box.minZ).tex(maxU, minV).endVertex();
			vb.pos(box.maxX, box.maxY, box.minZ).tex(minU, minV).endVertex();
			vb.pos(box.maxX, northSouth ? box.minY : box.maxY, box.maxZ).tex(minU, maxV).endVertex();
			vb.pos(box.minX, box.minY, box.maxZ).tex(maxU, maxV).endVertex();
			t.draw();
			vb.begin(7, DefaultVertexFormats.POSITION_TEX);
			vb.pos(box.maxX, box.maxY, box.minZ).tex(minU, minV).endVertex();
			vb.pos(box.minX, northSouth ? box.maxY : box.minY, box.minZ).tex(maxU, minV).endVertex();
			vb.pos(box.minX, box.minY, box.maxZ).tex(maxU, maxV).endVertex();
			vb.pos(box.maxX, northSouth ? box.minY : box.maxY, box.maxZ).tex(minU, maxV).endVertex();
			t.draw();
		}
	}
	
}