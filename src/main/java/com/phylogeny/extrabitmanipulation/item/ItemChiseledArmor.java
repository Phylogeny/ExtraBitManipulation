package com.phylogeny.extrabitmanipulation.item;

import java.util.List;

import javax.annotation.Nullable;

import mod.chiselsandbits.api.APIExceptions.CannotBeChiseled;
import mod.chiselsandbits.api.APIExceptions.InvalidBitItem;
import mod.chiselsandbits.api.APIExceptions.SpaceOccupied;
import mod.chiselsandbits.api.IBitAccess;
import mod.chiselsandbits.api.IBitBrush;
import mod.chiselsandbits.api.IBitLocation;
import mod.chiselsandbits.api.IChiselAndBitsAPI;
import mod.chiselsandbits.api.ItemType;
import mod.chiselsandbits.api.KeyBindingContext;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.api.ChiselsAndBitsAPIAccess;
import com.phylogeny.extrabitmanipulation.armor.ArmorItem;
import com.phylogeny.extrabitmanipulation.armor.DataChiseledArmorPiece;
import com.phylogeny.extrabitmanipulation.armor.GlOperation;
import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.client.CreativeTabExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.helper.BitAreaHelper;
import com.phylogeny.extrabitmanipulation.helper.BitInventoryHelper;
import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;
import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper.ArmorBodyPartTemplateData;
import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper.ArmorCollectionData;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.init.BlocksExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.init.KeyBindingsExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.init.ModelRegistration;
import com.phylogeny.extrabitmanipulation.packet.PacketCreateBodyPartTemplate;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.NBTKeys;
import com.phylogeny.extrabitmanipulation.reference.Reference;
import com.phylogeny.extrabitmanipulation.reference.Utility;

@KeyBindingContext("menuitem")
public class ItemChiseledArmor extends ItemArmor
{
	public static final String[] MODE_TITLES = new String[]{"Template Creation", "Block Collection"};
	public static final String[] SCALE_TITLES = new String[]{"1:1", "1:2", "1:4"};
	public final ArmorMovingPart[] MOVING_PARTS;
	public final String[] MOVING_PART_TITLES;
	public final ArmorType armorType;
	@SideOnly(Side.CLIENT)
	private ModelResourceLocation itemModelLocation;
	
	@SuppressWarnings("null")
	public ItemChiseledArmor(String name, EntityEquipmentSlot equipmentSlot, ArmorType armorType, ArmorMovingPart... movingParts)
	{
		super(ArmorMaterial.DIAMOND, 0, equipmentSlot);
		setRegistryName(name);
		setUnlocalizedName(getRegistryName().toString());
		setCreativeTab(CreativeTabExtraBitManipulation.CREATIVE_TAB);
		this.armorType = armorType;
		MOVING_PARTS = movingParts;
		MOVING_PART_TITLES = new String[MOVING_PARTS.length];
		for (int i = 0; i < MOVING_PARTS.length; i++)
			MOVING_PART_TITLES[i] = MOVING_PARTS[i].getName();
	}
	
	@SuppressWarnings("null")
	@SideOnly(Side.CLIENT)
	public ResourceLocation initItemModelLocation()
	{
		ResourceLocation loc = new ResourceLocation(getRegistryName().getResourceDomain(),
				getRegistryName().getResourcePath() + "_default");
		itemModelLocation = new ModelResourceLocation(loc, "inventory");
		return loc;
	}
	
	@SideOnly(Side.CLIENT)
	public ModelResourceLocation getItemModelLocation()
	{
		return itemModelLocation;
	}
	
	@SideOnly(Side.CLIENT)
	public IBakedModel getItemModel()
	{
		return ClientHelper.getBlockModelShapes().getModelManager().getModel(itemModelLocation);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	@Nullable
	public ModelBiped getArmorModel(EntityLivingBase entity, ItemStack stack, EntityEquipmentSlot slot, ModelBiped modeldefault)
	{
		return ModelRegistration.getArmorModel(stack, slot);
	}
	
	@Override
	@Nullable
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type)
	{
		return ModelRegistration.getArmorTexture(stack);
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return slotChanged || oldStack.hasTagCompound() != newStack.hasTagCompound() || (oldStack.hasTagCompound() && newStack.hasTagCompound()
				&& !ItemStackHelper.getArmorData(oldStack.getTagCompound()).equals(ItemStackHelper.getArmorData(newStack.getTagCompound())));
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote)
		{
			ArmorBodyPartTemplateData templateData = new ArmorBodyPartTemplateData(ItemStackHelper.getNBTOrNew(stack), this);
			Vec3d hit = new Vec3d(hitX, hitY, hitZ);
			if (createBodyPartTemplate(player, world, pos, facing, hit, templateData) == EnumActionResult.SUCCESS)
				ExtraBitManipulation.packetNetwork.sendToServer(new PacketCreateBodyPartTemplate(pos, facing, hit, templateData));
		}
		return EnumActionResult.SUCCESS;
	}
	
	public static EnumActionResult createBodyPartTemplate(EntityPlayer player, World world, BlockPos pos,
			EnumFacing facing, Vec3d hit, ArmorBodyPartTemplateData templateData)
	{
		NBTTagCompound nbt = ItemStackHelper.getNBTOrNew(player.getHeldItemMainhand());
		if (templateData.getMode() != 0)
			return EnumActionResult.PASS;
		
		IChiselAndBitsAPI api = ChiselsAndBitsAPIAccess.apiInstance;
		IBitBrush bitBodyPartTemplate = null;
		try
		{
			bitBodyPartTemplate = api.createBrushFromState(BlocksExtraBitManipulation.bodyPartTemplate.getDefaultState());
		}
		catch (InvalidBitItem e)
		{
			return EnumActionResult.FAIL;
		}
		ItemStack bitStack = bitBodyPartTemplate.getItemStack(1);
		hit = hit.addVector(pos.getX(), pos.getY(), pos.getZ());
		AxisAlignedBB box = getBodyPartTemplateBox(player, facing, pos, hit, templateData.getScale(), templateData.getMovingPart());
		boolean creativeMode = player.capabilities.isCreativeMode;
		if (!creativeMode)
		{
			int bitsMissing = (int) (Math.round((box.maxX - box.minX) / Utility.PIXEL_D) * Math.round((box.maxY - box.minY) / Utility.PIXEL_D)
					* Math.round((box.maxZ - box.minZ) / Utility.PIXEL_D)) - BitInventoryHelper.countInventoryBits(api, player, bitStack.copy())
					- BitInventoryHelper.countInventoryBlocks(player, BlocksExtraBitManipulation.bodyPartTemplate) * 4096;
			if (bitsMissing > 0)
			{
				if (world.isRemote)
					ClientHelper.printChatMessageWithDeletion("There are insufficient Bodypart Template blocks/bits in your inventory. Obtain " + bitsMissing
							+ " Bodypart Template bits or blocks worth of bits (1 block = 4096 bits).");
				
				return EnumActionResult.FAIL;
			}
		}
		int bitsPlaced = 0;
		AxisAlignedBB boxBlocks = new AxisAlignedBB(Math.floor(box.minX), Math.floor(box.minY), Math.floor(box.minZ),
				Math.ceil(box.maxX), Math.ceil(box.maxY), Math.ceil(box.maxZ));
		try
		{
			api.beginUndoGroup(player);
			for (int i = (int) boxBlocks.minX; i <= boxBlocks.maxX; i++)
			{
				for (int j = (int) boxBlocks.minY; j <= boxBlocks.maxY; j++)
				{
					for (int k = (int) boxBlocks.minZ; k <= boxBlocks.maxZ; k++)
						bitsPlaced = placeBodyPartTemplateBits(world, new BlockPos(i, j, k), api, box, bitBodyPartTemplate, bitsPlaced);
				}
			}
		}
		finally
		{
			api.endUndoGroup(player);
			if (!world.isRemote && !creativeMode)
			{
				bitsPlaced = BitInventoryHelper.removeOrAddInventoryBits(api, player, bitStack.copy(), bitsPlaced, false);
				BitInventoryHelper.removeBitsFromBlocks(api, player, bitStack, BlocksExtraBitManipulation.bodyPartTemplate, bitsPlaced);
				player.inventoryContainer.detectAndSendChanges();
			}
			if (bitsPlaced > 0)
			{
				ItemSculptingTool.playPlacementSound(player, world, pos, 1.0F);
				if (world.isRemote)
					ClientHelper.printChatMessageWithDeletion("Created a " + getPartAndScaleText(templateData.getMovingPart(), templateData.getScale()) +
							" and set collection reference area" );
			}
		}
		if (!world.isRemote)
		{
			writeCollectionBoxToNBT(nbt, player.rotationYaw, player.isSneaking(), player.getHorizontalFacing().getOpposite(), pos, facing, hit);
			player.getHeldItemMainhand().setTagCompound(nbt);
		}
		return bitsPlaced > 0 ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
	}
	
	public static String getPartAndScaleText(ArmorMovingPart part, int scale)
	{
		return SCALE_TITLES[scale] + " scale " + part.getBodyPartTemplate().getName().toLowerCase() + " template";
	}
	
	public static void writeCollectionBoxToNBT(NBTTagCompound nbt, float playerYaw, boolean useBitGrid,
			EnumFacing facingBox, BlockPos pos, EnumFacing facingPlacement, Vec3d hit)
	{
		BitAreaHelper.writeFacingToNBT(facingBox, nbt, NBTKeys.ARMOR_FACING_BOX);
		BitAreaHelper.writeFacingToNBT(facingPlacement, nbt, NBTKeys.ARMOR_FACING_PLACEMENT);
		BitAreaHelper.writeBlockPosToNBT(pos, nbt, NBTKeys.ARMOR_POS);
		BitAreaHelper.writeVecToNBT(hit, nbt, NBTKeys.ARMOR_HIT);
		nbt.setFloat(NBTKeys.ARMOR_YAW_PLAYER, playerYaw);
		nbt.setBoolean(NBTKeys.ARMOR_USE_BIT_GRID, useBitGrid);
	}
	
	private static int placeBodyPartTemplateBits(World world, BlockPos pos, IChiselAndBitsAPI api, AxisAlignedBB box, IBitBrush bitBodyPartTemplate, int bitsPlaced)
	{
		IBitAccess bitAccess;
		try
		{
			bitAccess = api.getBitAccess(world, pos);
		}
		catch (CannotBeChiseled e)
		{
			return bitsPlaced;
		}
		for (int i = 0; i < 16; i++)
		{
			for (int j = 0; j < 16; j++)
			{
				for (int k = 0; k < 16; k++)
				{
					IBitBrush bit = bitAccess.getBitAt(i, j, k);
					if (!bit.isAir())
						continue;
					
					double x = pos.getX() + i * Utility.PIXEL_D + 0.5 * Utility.PIXEL_D;
					double y = pos.getY() + j * Utility.PIXEL_D + 0.5 * Utility.PIXEL_D;
					double z = pos.getZ() + k * Utility.PIXEL_D + 0.5 * Utility.PIXEL_D;
					if (x < box.minX || x > box.maxX || y < box.minY || y > box.maxY || z < box.minZ || z > box.maxZ)
						continue;
					
					try
					{
						bitAccess.setBitAt(i, j, k, bitBodyPartTemplate);
						bitsPlaced++;
					}
					catch (SpaceOccupied e) {}
				}
			}
		}
		bitAccess.commitChanges(true);
		return bitsPlaced;
	}
	
	public static AxisAlignedBB getBodyPartTemplateBox(EntityPlayer player, EnumFacing facingPlacement, BlockPos pos, Vec3d hit, int scale, ArmorMovingPart part)
	{
		return getBodyPartTemplateBox(player.rotationYaw, player.isSneaking(), player.getHorizontalFacing(), facingPlacement, pos, hit, scale, part);
	}
	
	public static AxisAlignedBB getBodyPartTemplateBox(float playerYaw, boolean useBitGrid, EnumFacing facingBox,
			EnumFacing facingPlacement, BlockPos pos, Vec3d hit, int scale, ArmorMovingPart part)
	{
		scale = (int) Math.pow(2, scale);
		BodyPartTemplate bodyPart = part.getBodyPartTemplate();
		boolean isHead = bodyPart == BodyPartTemplate.HEAD;
		double semiDiameterX = (bodyPart == BodyPartTemplate.LIMB ? 2 : 4) * scale * Utility.PIXEL_D;
		double semiDiameterY = (isHead ? 4 : 6) * scale * Utility.PIXEL_D;
		double semiDiameterZ = (isHead ? 4 : 2) * scale * Utility.PIXEL_D;
		if (facingBox.getAxis() == Axis.X)
		{
			double tempX = semiDiameterX;
			semiDiameterX = semiDiameterZ;
			semiDiameterZ = tempX;
		}
		int offsetX = facingPlacement.getFrontOffsetX();
		int offsetY = facingPlacement.getFrontOffsetY();
		int offsetZ = facingPlacement.getFrontOffsetZ();
		double x2, y2, z2;
		AxisAlignedBB box = null;
		if (useBitGrid)
		{
			float hitX = (float) hit.x - pos.getX();
			float hitY = (float) hit.y - pos.getY();
			float hitZ = (float) hit.z - pos.getZ();
			IBitLocation bitLoc = ChiselsAndBitsAPIAccess.apiInstance.getBitPos(hitX, hitY, hitZ, facingPlacement, pos, false);
			if (bitLoc != null)
			{
				x2 = bitLoc.getBitX() * Utility.PIXEL_D;
				y2 = bitLoc.getBitY() * Utility.PIXEL_D;
				z2 = bitLoc.getBitZ() * Utility.PIXEL_D;
				double offset = facingPlacement.getAxisDirection() == AxisDirection.POSITIVE ? Utility.PIXEL_D : 0;
				box = new AxisAlignedBB(x2 - semiDiameterX, y2 - semiDiameterY, z2 - semiDiameterZ, x2 + semiDiameterX, y2 + semiDiameterY,
						z2 + semiDiameterZ).offset((semiDiameterX + offset) * offsetX, (semiDiameterY + offset) * offsetY,
							(semiDiameterZ + offset) * offsetZ).offset(pos);
			}
		}
		else
		{
			x2 = pos.getX() + 0.5;
			y2 = pos.getY() + 0.5;
			z2 = pos.getZ() + 0.5;
			box = new AxisAlignedBB(x2 - semiDiameterX, y2 - semiDiameterY, z2 - semiDiameterZ, x2 + semiDiameterX, y2 + semiDiameterY,
					z2 + semiDiameterZ).offset(0, (semiDiameterY - 0.5) * (offsetY != 0 ? offsetY : 1), 0).offset(offsetX, offsetY,offsetZ);
			if (scale == 4 && bodyPart != BodyPartTemplate.LIMB)
			{
				if (facingBox.getAxis() != Axis.X || isHead)
					box = box.offset((facingPlacement.getAxis() == Axis.X ? (facingPlacement.getAxisDirection() == AxisDirection.POSITIVE)
							: (playerYaw % 360 > (playerYaw > 0 ? 180 : -180))) ? 0.5 : -0.5, 0, 0);
				if (facingBox.getAxis() == Axis.X || isHead)
					box = box.offset(0, 0, (facingPlacement.getAxis() == Axis.Z ? (facingPlacement.getAxisDirection() == AxisDirection.POSITIVE)
							: ((playerYaw - 90) % 360 > (playerYaw > 90 ? 180 : -180))) ? 0.5 : -0.5);
			}																																																			
		}
		return box; 
	}
	
	public static boolean collectArmorBlocks(EntityPlayer player, ArmorCollectionData collectionData)
	{
		ItemStack stack = player.getHeldItemMainhand();
		NBTTagCompound nbt = ItemStackHelper.getNBTOrNew(stack);
		DataChiseledArmorPiece armorPiece = new DataChiseledArmorPiece(nbt, ((ItemChiseledArmor) stack.getItem()).armorType);
		World world = player.world;
		AxisAlignedBB boxCollection = collectionData.getCollectionBox();
		AxisAlignedBB boxBlocks = new AxisAlignedBB(Math.floor(boxCollection.minX), Math.floor(boxCollection.minY), Math.floor(boxCollection.minZ),
				Math.ceil(boxCollection.maxX), Math.ceil(boxCollection.maxY), Math.ceil(boxCollection.maxZ));
		IChiselAndBitsAPI api = ChiselsAndBitsAPIAccess.apiInstance;
		ArmorMovingPart movingPart = collectionData.getMovingPart();
		EnumFacing facingBox = collectionData.getFacing();
		Vec3d orginBox = collectionData.getOriginBodyPart();
		float scale = 1 / (float) Math.pow(2, collectionData.getScale());
		int blocksCollected = 0;
		for (int i = (int) boxBlocks.minX; i <= boxBlocks.maxX; i++)
		{
			for (int j = (int) boxBlocks.minY; j <= boxBlocks.maxY; j++)
			{
				for (int k = (int) boxBlocks.minZ; k <= boxBlocks.maxZ; k++)
				{
					blocksCollected = collectBits(world, new BlockPos(i, j, k), api, boxCollection,
							facingBox, orginBox, scale, armorPiece, movingPart, blocksCollected);
				}
			}
		}
		if (blocksCollected > 0)
		{
			if (world.isRemote)
			{
				ClientHelper.printChatMessageWithDeletion("Imported " + blocksCollected + " block cop" + (blocksCollected > 1 ? "ies" : "y") +
						" at " + SCALE_TITLES[collectionData.getScale()] + " scale into the " + collectionData.getMovingPart().getName().toLowerCase());
			}
			else
			{
				armorPiece.saveToNBT(nbt);
				stack.setTagCompound(nbt);
				player.inventoryContainer.detectAndSendChanges();
			}
		}
		return blocksCollected > 0;
	}
	
	private static int collectBits(World world, BlockPos pos, IChiselAndBitsAPI api, AxisAlignedBB boxCollection, EnumFacing facingBox,
			Vec3d orginBox, float scale, DataChiseledArmorPiece armorPiece, ArmorMovingPart movingPart, int blocksCollected)
	{
		IBitAccess bitAccess;
		try
		{
			bitAccess = api.getBitAccess(world, pos);
		}
		catch (CannotBeChiseled e)
		{
			return blocksCollected;
		}
		IBitAccess bitAccessNew = api.createBitItem(ItemStack.EMPTY);
		if (bitAccessNew == null)
			return blocksCollected;
		
		boolean bitsCollected = false;
		for (int i = 0; i < 16; i++)
		{
			for (int j = 0; j < 16; j++)
			{
				for (int k = 0; k < 16; k++)
				{
					IBitBrush bit = bitAccess.getBitAt(i, j, k);
					if (bit.isAir() || bit.getState() == BlocksExtraBitManipulation.bodyPartTemplate.getDefaultState())
						continue;
					
					double x = pos.getX() + i * Utility.PIXEL_D + 0.5 * Utility.PIXEL_D;
					double y = pos.getY() + j * Utility.PIXEL_D + 0.5 * Utility.PIXEL_D;
					double z = pos.getZ() + k * Utility.PIXEL_D + 0.5 * Utility.PIXEL_D;
					if (x < boxCollection.minX || x > boxCollection.maxX || y < boxCollection.minY
							|| y > boxCollection.maxY || z < boxCollection.minZ || z > boxCollection.maxZ)
						continue;
					
					try
					{
						if (!world.isRemote)
							bitAccessNew.setBitAt(i, j, k, bit);
						
						bitsCollected = true;
					}
					catch (SpaceOccupied e) {}
				}
			}
		}
		if (!world.isRemote && bitsCollected)
		{
			ArmorItem armorItem = new ArmorItem(bitAccessNew.getBitsAsItem(null, ItemType.CHISLED_BLOCK, false));
			if (facingBox != EnumFacing.NORTH)
				armorItem.addGlOperation(GlOperation.createRotation((facingBox.getHorizontalAngle() + 180) % 360, 0, 1, 0));
			
			if (scale != 1)
				armorItem.addGlOperation(GlOperation.createScale(scale, scale, scale));
			
			AxisAlignedBB box = new AxisAlignedBB(pos);
			float x = (float) (box.minX - orginBox.x);
			float y = (float) (box.minY - orginBox.y);
			float z = (float) (box.minZ - orginBox.z);
			if (x != 0 || y != 0 || z != 0)
				armorItem.addGlOperation(GlOperation.createTranslation(x, y, z));
			
			armorPiece.addItemToPart(movingPart.getPartIndex(), armorItem);
		}
		if (bitsCollected)
			blocksCollected++;
		
		return blocksCollected;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
	{
		boolean shiftDown = GuiScreen.isShiftKeyDown();
		boolean ctrlDown = GuiScreen.isCtrlKeyDown();
		ItemBitToolBase.addColorInformation(tooltip, shiftDown);
		NBTTagCompound nbt = stack.getTagCompound();
		int mode = BitToolSettingsHelper.getArmorMode(nbt);
		boolean targetBits = BitToolSettingsHelper.areArmorBitsTargeted(nbt);
		if (shiftDown)
		{
			tooltip.add(ItemBitToolBase.colorSettingText(BitToolSettingsHelper.getArmorModeText(mode), Configs.armorMode));
			tooltip.add(ItemBitToolBase.colorSettingText(BitToolSettingsHelper.getArmorScaleText(nbt), Configs.armorScale));
			tooltip.add(ItemBitToolBase.colorSettingText(BitToolSettingsHelper.getArmorBitsTargetedText(targetBits), Configs.armorTargetBits));
		}
		if (!ctrlDown || shiftDown)
		{
			tooltip.add(ItemBitToolBase.colorSettingText(BitToolSettingsHelper.getArmorMovingPartText(nbt, this),
					BitToolSettingsHelper.getArmorMovingPartConfig(armorType)));
		}
		if (shiftDown)
			return;
		
		if (!ctrlDown)
		{
			ItemBitToolBase.addKeyInformation(tooltip, true);
			return;
		}
		if (mode == 1)
		{
			String target = targetBits ? "bit" : "block";
			tooltip.add("Left click a " + target + ", drag to another ");
			tooltip.add("    " + target + ", then release to import copies");
			if (targetBits)
			{
				tooltip.add("    of all intersecting bits into the");
				tooltip.add("    selected moving part as blocks.");
			}
			else
			{
				tooltip.add("    of all intersecting blocks into the");
				tooltip.add("    selected moving part.");
			}
		}
		else
		{
			tooltip.add("Left click a block to set the collection");
			tooltip.add("    reference area for the bodypart");
			tooltip.add("    template of the selected moving part.");
			tooltip.add("    (sneaking will allow the area to be");
			tooltip.add("    placed outside of the block grid)");
		}
		if (mode == 0)
		{
			tooltip.add("Right click to do so and fill that area");
			tooltip.add("    with bits of bodypart template blocks.");
		}
		tooltip.add("");
		String controlText = ItemBitToolBase.getColoredKeyBindText(KeyBindingsExtraBitManipulation.CONTROL);
		if (KeyBindingsExtraBitManipulation.OPEN_BIT_MAPPING_GUI.getKeyBinding().isSetToDefaultValue())
		{
			tooltip.add(controlText + " right click to toggle mode.");
		}
		else
		{
			tooltip.add(controlText + " right click or press " + KeyBindingsExtraBitManipulation.OPEN_BIT_MAPPING_GUI.getText());
			tooltip.add("    to open mapping/preview GUI.");
		}
		tooltip.add(controlText + " mouse wheel to cycle scale.");
		tooltip.add("");
		String altText = ItemBitToolBase.getColoredKeyBindText(KeyBindingsExtraBitManipulation.ALT);
		tooltip.add(altText + " right click to toggle collection");
		tooltip.add("     target between bits & blocks.");
		tooltip.add(altText + " mouse wheel to cycle moving part.");
		ItemBitToolBase.addKeybindReminders(tooltip, KeyBindingsExtraBitManipulation.SHIFT, KeyBindingsExtraBitManipulation.CONTROL);
	}
	
	public static enum ArmorType
	{
		HELMET("Helmet", 1),
		CHESTPLATE("Chestplate", 3),
		LEGGINGS("Leggings", 3),
		BOOTS("Boots", 2);
		
		private String name;
		private int movingpartCount;
		
		private ArmorType(String name, int movingpartCount)
		{
			this.name = name;
			this.movingpartCount = movingpartCount;
		}
		
		public String getName()
		{
			return name;
		}
		
		public int getMovingpartCount()
		{
			return movingpartCount;
		}
		
	}
	
	public static enum BodyPartTemplate
	{
		HEAD("Head"),
		TORSO("Torso"),
		LIMB("Limb");
		
		private String name;
		
		private BodyPartTemplate(String name)
		{
			this.name = name;
		}
		
		public String getName()
		{
			return name;
		}
		
	}
	
	public static enum ArmorMovingPart
	{
		HEAD(BodyPartTemplate.HEAD, 0, 1, "Head"),
		TORSO(BodyPartTemplate.TORSO, 0, 2, "Torso"),
		PELVIS(BodyPartTemplate.TORSO, 0, 1, "Pelvis"),
		ARM_RIGHT(BodyPartTemplate.LIMB, 1, 1, "Right Arm"),
		ARM_LEFT(BodyPartTemplate.LIMB, 2, 2, "Left Arm"),
		LEG_RIGHT(BodyPartTemplate.LIMB, 1, 3, "Right Leg"),
		LEG_LEFT(BodyPartTemplate.LIMB, 2, 2, "Left Leg"),
		FOOT_RIGHT(BodyPartTemplate.LIMB, 0, 1, "Right Foot"),
		FOOT_LEFT(BodyPartTemplate.LIMB, 1, 2, "Left Foot");
		
		private BodyPartTemplate template;
		private int partIndex, modelCount;
		private String name;
		@SideOnly(Side.CLIENT)
		private ModelResourceLocation[] iconModelLocations;
		
		private ArmorMovingPart(BodyPartTemplate template, int partIndex, int modelCount, String name)
		{
			this.template = template;
			this.partIndex = partIndex;
			this.modelCount = modelCount;
			this.name = name;
		}
		
		public BodyPartTemplate getBodyPartTemplate()
		{
			return template;
		}
		
		public int getPartIndex()
		{
			return partIndex;
		}
		
		public String getName()
		{
			return name;
		}
		
		@SideOnly(Side.CLIENT)
		public ModelResourceLocation[] getIconModelLocations()
		{
			return iconModelLocations;
		}
		
		@SideOnly(Side.CLIENT)
		public IBakedModel[] getIconModels()
		{
			IBakedModel[] models = new IBakedModel[iconModelLocations.length];
			for (int i = 0; i < iconModelLocations.length; i++)
			{
				models[i] = ClientHelper.getBlockModelShapes().getModelManager().getModel(iconModelLocations[i]);
			}
			return models;
		}
		
		@SideOnly(Side.CLIENT)
		public static void initIconModelLocations()
		{
			for (ArmorMovingPart part : ArmorMovingPart.values())
			{
				part.iconModelLocations = new ModelResourceLocation[part.modelCount];
				for (int i = 0; i < part.iconModelLocations.length; i++)
				{
					part.iconModelLocations[i] = new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID,
							"moving_part_" + part.name.toLowerCase().replace(" ", "_") + "_" + i), null);
				}
			}
		}
		
	}
	
}