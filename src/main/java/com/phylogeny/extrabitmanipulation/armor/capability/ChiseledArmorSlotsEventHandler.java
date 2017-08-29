package com.phylogeny.extrabitmanipulation.armor.capability;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.input.Keyboard;

import com.phylogeny.extrabitmanipulation.client.ClientHelper;
import com.phylogeny.extrabitmanipulation.client.gui.armor.GuiButtonArmorSlots;
import com.phylogeny.extrabitmanipulation.client.gui.armor.GuiInventoryArmorSlots;
import com.phylogeny.extrabitmanipulation.helper.BitToolSettingsHelper;
import com.phylogeny.extrabitmanipulation.helper.ItemStackHelper;
import com.phylogeny.extrabitmanipulation.init.ReflectionExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.item.ItemChiseledArmor.ArmorType;
import com.phylogeny.extrabitmanipulation.reference.ChiselsAndBitsReferences;
import com.phylogeny.extrabitmanipulation.reference.Configs;
import com.phylogeny.extrabitmanipulation.reference.Reference;

public class ChiseledArmorSlotsEventHandler
{
	
	@SubscribeEvent
	public void onEntityConstruct(AttachCapabilitiesEvent<Entity> event)
	{
		if (event.getObject() instanceof EntityPlayer)
			event.addCapability(new ResourceLocation(Reference.MOD_ID, "chiseled_armor_slots"), new ChiseledArmorSlotsHandler());
	}
	
	@SubscribeEvent
	public void syncDataForNewPlayers(EntityJoinWorldEvent event)
	{
		Entity player = event.getEntity();
		if (!player.world.isRemote && player instanceof EntityPlayerMP)
		{
			IChiseledArmorSlotsHandler cap = ChiseledArmorSlotsHandler.getCapability((EntityPlayer) player);
			if (cap != null)
				cap.syncAllData((EntityPlayerMP) player);
		}
	}
	
	@SubscribeEvent
	public void syncDataForClonedPlayers(PlayerEvent.Clone event)
	{
		if (!event.isWasDeath())
			return;
		
		IChiseledArmorSlotsHandler capOld = ChiseledArmorSlotsHandler.getCapability(event.getOriginal());
		if (capOld != null)
		{
			IChiseledArmorSlotsHandler capNew = ChiseledArmorSlotsHandler.getCapability((EntityPlayer) event.getEntity());
			if (capNew != null)
				((ChiseledArmorSlotsHandler) capNew).deserializeNBT(((ChiseledArmorSlotsHandler) capOld).serializeNBT());
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void dropArmorOnDeath(PlayerDropsEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		IChiseledArmorSlotsHandler cap = ChiseledArmorSlotsHandler.getCapability(player);
		if (cap == null)
			return;
		
		for (int i = 0; i < ArmorType.values().length; i++)
		{
			if (!cap.getStackInSlot(i).isEmpty())
			{
				player.captureDrops = true;
				player.dropItem(cap.getStackInSlot(i).copy(), true, false);
				player.captureDrops = false;
				cap.setStackInSlot(i, ItemStack.EMPTY);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void addArmorButtonToGui(GuiScreenEvent.InitGuiEvent.Post event)
	{
		EntityPlayer player = ClientHelper.getPlayer();
		if (player == null || player.capabilities.isCreativeMode)
			return;
		
		boolean isArmorSlots = event.getGui() instanceof GuiInventoryArmorSlots;
		if (!isArmorSlots && !(event.getGui() instanceof GuiInventory))
			return;
		
		GuiContainer gui = (GuiContainer) event.getGui();
		boolean add = false;
		IChiseledArmorSlotsHandler cap = ChiseledArmorSlotsHandler.getCapability(player);
		if (cap != null)
		{
			for (int i = 0; i < cap.getSlots(); i++)
			{
				if (!cap.getStackInSlot(i).isEmpty())
				{
					add = true;
					break;
				}
			}
		}
		if (!add)
		{
			ArmorButtonVisibiltyMode mode = Configs.armorButtonVisibiltyMode;
			add = isArmorSlots || mode == ArmorButtonVisibiltyMode.ALWAYS;
			if (!isArmorSlots && mode != ArmorButtonVisibiltyMode.NEVER && mode != ArmorButtonVisibiltyMode.ALWAYS)
			{
				for (int i = 0; i < player.inventory.getSizeInventory(); i++)
				{
					ItemStack stack = player.inventory.getStackInSlot(i);
					if (stack.isEmpty())
						continue;
					
					if (mode == ArmorButtonVisibiltyMode.ANY_ITEMS || mode == ArmorButtonVisibiltyMode.CHISELED_ARMOR_ITEMS)
					{
						if (mode == ArmorButtonVisibiltyMode.ANY_ITEMS || ItemStackHelper.isChiseledArmorStack(stack))
						{
							add = true;
							break;
						}
						continue;
					}
					Item item = stack.getItem();
					if (item != null)
					{
						ResourceLocation name = item.getRegistryName();
						if (name != null && (name.getResourceDomain().equals(Reference.MOD_ID)
								|| (mode == ArmorButtonVisibiltyMode.EBM_OR_CNB_ITEMS && name.getResourceDomain().equals(ChiselsAndBitsReferences.MOD_ID))))
						{
							add = true;
							break;
						}
					}
				}
			}
		}
		if (add)
			event.getButtonList().add(new GuiButtonArmorSlots(gui, isArmorSlots ? "Back" : "Chiseled Armor"));
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void resetArmorButtonPosition(GuiScreenEvent.KeyboardInputEvent.Post event)
	{
		if (!Keyboard.isKeyDown(Keyboard.KEY_R) || !GuiButtonArmorSlots.shouldMoveButton() ||
				!(event.getGui() instanceof GuiInventoryArmorSlots) && !(event.getGui() instanceof GuiInventory) ||
				(Configs.armorButtonX.isAtDefaultValue() && Configs.armorButtonY.isAtDefaultValue()))
			return;
		
		List<GuiButton> buttonList = ReflectionExtraBitManipulation.getButtonList(event.getGui());
		for (GuiButton button : buttonList)
		{
			if (button instanceof GuiButtonArmorSlots)
			{
				BitToolSettingsHelper.setArmorButtonPosition(Configs.armorButtonX.getDefaultValue(), Configs.armorButtonY.getDefaultValue());
				((GuiButtonArmorSlots) button).setPosition();
				break;
			}
		}
	}
	
	public static enum ArmorButtonVisibiltyMode
	{
		CHISELED_ARMOR_ITEMS, EBM_ITEMS, EBM_OR_CNB_ITEMS, ANY_ITEMS, ALWAYS, NEVER;
	}
	
}