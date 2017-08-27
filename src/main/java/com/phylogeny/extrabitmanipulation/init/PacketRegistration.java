package com.phylogeny.extrabitmanipulation.init;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.packet.PacketAddBitMapping;
import com.phylogeny.extrabitmanipulation.packet.PacketBitMappingsPerTool;
import com.phylogeny.extrabitmanipulation.packet.PacketBitParticles;
import com.phylogeny.extrabitmanipulation.packet.PacketChangeArmorItemList;
import com.phylogeny.extrabitmanipulation.packet.PacketChangeGlOperationList;
import com.phylogeny.extrabitmanipulation.packet.PacketClearStackBitMappings;
import com.phylogeny.extrabitmanipulation.packet.PacketCollectArmorBlocks;
import com.phylogeny.extrabitmanipulation.packet.PacketCreateBodyPartTemplate;
import com.phylogeny.extrabitmanipulation.packet.PacketCreateModel;
import com.phylogeny.extrabitmanipulation.packet.PacketCursorStack;
import com.phylogeny.extrabitmanipulation.packet.PacketCycleBitWrenchMode;
import com.phylogeny.extrabitmanipulation.packet.PacketOpenBitMappingGui;
import com.phylogeny.extrabitmanipulation.packet.PacketOpenChiseledArmorGui;
import com.phylogeny.extrabitmanipulation.packet.PacketOpenInventoryGui;
import com.phylogeny.extrabitmanipulation.packet.PacketOverwriteStackBitMappings;
import com.phylogeny.extrabitmanipulation.packet.PacketPlaceEntityBit;
import com.phylogeny.extrabitmanipulation.packet.PacketReadBlockStates;
import com.phylogeny.extrabitmanipulation.packet.PacketSculpt;
import com.phylogeny.extrabitmanipulation.packet.PacketSetArmorMode;
import com.phylogeny.extrabitmanipulation.packet.PacketSetArmorMovingPart;
import com.phylogeny.extrabitmanipulation.packet.PacketSetArmorScale;
import com.phylogeny.extrabitmanipulation.packet.PacketSetBitStack;
import com.phylogeny.extrabitmanipulation.packet.PacketSetCollectionBox;
import com.phylogeny.extrabitmanipulation.packet.PacketSetDesign;
import com.phylogeny.extrabitmanipulation.packet.PacketSetDirection;
import com.phylogeny.extrabitmanipulation.packet.PacketSetEndsOpen;
import com.phylogeny.extrabitmanipulation.packet.PacketSetHollowShape;
import com.phylogeny.extrabitmanipulation.packet.PacketSetModelAreaMode;
import com.phylogeny.extrabitmanipulation.packet.PacketSetModelGuiOpen;
import com.phylogeny.extrabitmanipulation.packet.PacketSetModelSnapMode;
import com.phylogeny.extrabitmanipulation.packet.PacketSetSculptMode;
import com.phylogeny.extrabitmanipulation.packet.PacketSetSemiDiameter;
import com.phylogeny.extrabitmanipulation.packet.PacketSetShapeType;
import com.phylogeny.extrabitmanipulation.packet.PacketSetTabAndStateBlockButton;
import com.phylogeny.extrabitmanipulation.packet.PacketSetTargetArmorBits;
import com.phylogeny.extrabitmanipulation.packet.PacketSetTargetBitGridVertexes;
import com.phylogeny.extrabitmanipulation.packet.PacketSetWallThickness;
import com.phylogeny.extrabitmanipulation.packet.PacketSetWrechMode;
import com.phylogeny.extrabitmanipulation.packet.PacketSyncAllArmorSlotData;
import com.phylogeny.extrabitmanipulation.packet.PacketThrowBit;
import com.phylogeny.extrabitmanipulation.packet.PacketUseWrench;

public class PacketRegistration
{
	public static int packetId = 0;
	
	public static enum Side
	{
		CLIENT, SERVER, BOTH;
	}
	
	public static void registerPackets()
	{
		registerPacket(PacketCycleBitWrenchMode.Handler.class, PacketCycleBitWrenchMode.class, Side.SERVER);
		registerPacket(PacketSculpt.Handler.class, PacketSculpt.class, Side.SERVER);
		registerPacket(PacketSetDirection.Handler.class, PacketSetDirection.class, Side.SERVER);
		registerPacket(PacketSetShapeType.Handler.class, PacketSetShapeType.class, Side.SERVER);
		registerPacket(PacketSetTargetBitGridVertexes.Handler.class, PacketSetTargetBitGridVertexes.class, Side.SERVER);
		registerPacket(PacketSetSemiDiameter.Handler.class, PacketSetSemiDiameter.class, Side.SERVER);
		registerPacket(PacketSetHollowShape.Handler.class, PacketSetHollowShape.class, Side.SERVER);
		registerPacket(PacketSetEndsOpen.Handler.class, PacketSetEndsOpen.class, Side.SERVER);
		registerPacket(PacketSetWallThickness.Handler.class, PacketSetWallThickness.class, Side.SERVER);
		registerPacket(PacketSetBitStack.Handler.class, PacketSetBitStack.class, Side.SERVER);
		registerPacket(PacketSetSculptMode.Handler.class, PacketSetSculptMode.class, Side.SERVER);
		registerPacket(PacketSetModelAreaMode.Handler.class, PacketSetModelAreaMode.class, Side.SERVER);
		registerPacket(PacketSetModelSnapMode.Handler.class, PacketSetModelSnapMode.class, Side.SERVER);
		registerPacket(PacketSetModelGuiOpen.Handler.class, PacketSetModelGuiOpen.class, Side.SERVER);
		registerPacket(PacketAddBitMapping.Handler.class, PacketAddBitMapping.class, Side.SERVER);
		registerPacket(PacketCursorStack.Handler.class, PacketCursorStack.class, Side.SERVER);
		registerPacket(PacketSetTabAndStateBlockButton.Handler.class, PacketSetTabAndStateBlockButton.class, Side.SERVER);
		registerPacket(PacketReadBlockStates.Handler.class, PacketReadBlockStates.class, Side.SERVER);
		registerPacket(PacketCreateModel.Handler.class, PacketCreateModel.class, Side.SERVER);
		registerPacket(PacketUseWrench.Handler.class, PacketUseWrench.class, Side.SERVER);
		registerPacket(PacketBitMappingsPerTool.Handler.class, PacketBitMappingsPerTool.class, Side.SERVER);
		registerPacket(PacketClearStackBitMappings.Handler.class, PacketClearStackBitMappings.class, Side.SERVER);
		registerPacket(PacketOverwriteStackBitMappings.Handler.class, PacketOverwriteStackBitMappings.class, Side.SERVER);
		registerPacket(PacketOpenBitMappingGui.Handler.class, PacketOpenBitMappingGui.class, Side.SERVER);
		registerPacket(PacketSetWrechMode.Handler.class, PacketSetWrechMode.class, Side.SERVER);
		registerPacket(PacketSetDesign.Handler.class, PacketSetDesign.class, Side.SERVER);
		registerPacket(PacketThrowBit.Handler.class, PacketThrowBit.class, Side.SERVER);
		registerPacket(PacketBitParticles.Handler.class, PacketBitParticles.class, Side.CLIENT);
		registerPacket(PacketPlaceEntityBit.Handler.class, PacketPlaceEntityBit.class, Side.CLIENT);
		registerPacket(PacketSetArmorMode.Handler.class, PacketSetArmorMode.class, Side.SERVER);
		registerPacket(PacketSetArmorScale.Handler.class, PacketSetArmorScale.class, Side.SERVER);
		registerPacket(PacketSetTargetArmorBits.Handler.class, PacketSetTargetArmorBits.class, Side.SERVER);
		registerPacket(PacketSetArmorMovingPart.Handler.class, PacketSetArmorMovingPart.class, Side.SERVER);
		registerPacket(PacketSetCollectionBox.Handler.class, PacketSetCollectionBox.class, Side.SERVER);
		registerPacket(PacketCreateBodyPartTemplate.Handler.class, PacketCreateBodyPartTemplate.class, Side.SERVER);
		registerPacket(PacketCollectArmorBlocks.Handler.class, PacketCollectArmorBlocks.class, Side.SERVER);
		registerPacket(PacketOpenChiseledArmorGui.Handler.class, PacketOpenChiseledArmorGui.class, Side.SERVER);
		registerPacket(PacketChangeGlOperationList.Handler.class, PacketChangeGlOperationList.class, Side.BOTH);
		registerPacket(PacketChangeArmorItemList.Handler.class, PacketChangeArmorItemList.class, Side.BOTH);
		registerPacket(PacketSyncAllArmorSlotData.Handler.class, PacketSyncAllArmorSlotData.class, Side.CLIENT);
		registerPacket(PacketOpenInventoryGui.Handler.class, PacketOpenInventoryGui.class, Side.SERVER);
	}
	
	private static void registerPacket(Class handler, Class packet, Side side)
	{
		if (side != Side.CLIENT)
			registerPacket(handler, packet, net.minecraftforge.fml.relauncher.Side.SERVER);
		
		if (side != Side.SERVER)
			registerPacket(handler, packet, net.minecraftforge.fml.relauncher.Side.CLIENT);
	}
	
	private static void registerPacket(Class handler, Class packet, net.minecraftforge.fml.relauncher.Side side)
	{
		ExtraBitManipulation.packetNetwork.registerMessage(handler, packet, packetId++, side);
	}
	
}