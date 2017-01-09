package com.phylogeny.extrabitmanipulation.init;

import net.minecraftforge.fml.relauncher.Side;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.packet.PacketBitMappingsPerTool;
import com.phylogeny.extrabitmanipulation.packet.PacketClearStackBitMappings;
import com.phylogeny.extrabitmanipulation.packet.PacketCreateModel;
import com.phylogeny.extrabitmanipulation.packet.PacketCursorStack;
import com.phylogeny.extrabitmanipulation.packet.PacketCycleBitWrenchMode;
import com.phylogeny.extrabitmanipulation.packet.PacketAddBitMapping;
import com.phylogeny.extrabitmanipulation.packet.PacketOpenBitMappingGui;
import com.phylogeny.extrabitmanipulation.packet.PacketOverwriteStackBitMappings;
import com.phylogeny.extrabitmanipulation.packet.PacketReadBlockStates;
import com.phylogeny.extrabitmanipulation.packet.PacketSculpt;
import com.phylogeny.extrabitmanipulation.packet.PacketSetBitStack;
import com.phylogeny.extrabitmanipulation.packet.PacketSetDesign;
import com.phylogeny.extrabitmanipulation.packet.PacketSetHollowShape;
import com.phylogeny.extrabitmanipulation.packet.PacketSetEndsOpen;
import com.phylogeny.extrabitmanipulation.packet.PacketSetModelAreaMode;
import com.phylogeny.extrabitmanipulation.packet.PacketSetModelGuiOpen;
import com.phylogeny.extrabitmanipulation.packet.PacketSetModelSnapMode;
import com.phylogeny.extrabitmanipulation.packet.PacketSetSculptMode;
import com.phylogeny.extrabitmanipulation.packet.PacketSetSemiDiameter;
import com.phylogeny.extrabitmanipulation.packet.PacketSetShapeType;
import com.phylogeny.extrabitmanipulation.packet.PacketSetTabAndStateBlockButton;
import com.phylogeny.extrabitmanipulation.packet.PacketSetTargetBitGridVertexes;
import com.phylogeny.extrabitmanipulation.packet.PacketSetWallThickness;
import com.phylogeny.extrabitmanipulation.packet.PacketSetDirection;
import com.phylogeny.extrabitmanipulation.packet.PacketSetWrechMode;
import com.phylogeny.extrabitmanipulation.packet.PacketUseWrench;

public class PacketRegistration
{
	public static int packetId = 0;
	
	public static void registerPackets()
	{
		registerPacket(PacketCycleBitWrenchMode.Handler.class, PacketCycleBitWrenchMode.class, PacketSide.SERVER);
		registerPacket(PacketSculpt.Handler.class, PacketSculpt.class, PacketSide.BOTH);
		registerPacket(PacketSetDirection.Handler.class, PacketSetDirection.class, PacketSide.SERVER);
		registerPacket(PacketSetShapeType.Handler.class, PacketSetShapeType.class, PacketSide.SERVER);
		registerPacket(PacketSetTargetBitGridVertexes.Handler.class, PacketSetTargetBitGridVertexes.class, PacketSide.SERVER);
		registerPacket(PacketSetSemiDiameter.Handler.class, PacketSetSemiDiameter.class, PacketSide.SERVER);
		registerPacket(PacketSetHollowShape.Handler.class, PacketSetHollowShape.class, PacketSide.SERVER);
		registerPacket(PacketSetEndsOpen.Handler.class, PacketSetEndsOpen.class, PacketSide.SERVER);
		registerPacket(PacketSetWallThickness.Handler.class, PacketSetWallThickness.class, PacketSide.SERVER);
		registerPacket(PacketSetBitStack.Handler.class, PacketSetBitStack.class, PacketSide.SERVER);
		registerPacket(PacketSetSculptMode.Handler.class, PacketSetSculptMode.class, PacketSide.SERVER);
		registerPacket(PacketSetModelAreaMode.Handler.class, PacketSetModelAreaMode.class, PacketSide.SERVER);
		registerPacket(PacketSetModelSnapMode.Handler.class, PacketSetModelSnapMode.class, PacketSide.SERVER);
		registerPacket(PacketSetModelGuiOpen.Handler.class, PacketSetModelGuiOpen.class, PacketSide.SERVER);
		registerPacket(PacketAddBitMapping.Handler.class, PacketAddBitMapping.class, PacketSide.SERVER);
		registerPacket(PacketCursorStack.Handler.class, PacketCursorStack.class, PacketSide.SERVER);
		registerPacket(PacketSetTabAndStateBlockButton.Handler.class, PacketSetTabAndStateBlockButton.class, PacketSide.SERVER);
		registerPacket(PacketReadBlockStates.Handler.class, PacketReadBlockStates.class, PacketSide.SERVER);
		registerPacket(PacketCreateModel.Handler.class, PacketCreateModel.class, PacketSide.SERVER);
		registerPacket(PacketUseWrench.Handler.class, PacketUseWrench.class, PacketSide.CLIENT);
		registerPacket(PacketBitMappingsPerTool.Handler.class, PacketBitMappingsPerTool.class, PacketSide.SERVER);
		registerPacket(PacketClearStackBitMappings.Handler.class, PacketClearStackBitMappings.class, PacketSide.SERVER);
		registerPacket(PacketOverwriteStackBitMappings.Handler.class, PacketOverwriteStackBitMappings.class, PacketSide.SERVER);
		registerPacket(PacketOpenBitMappingGui.Handler.class, PacketOpenBitMappingGui.class, PacketSide.SERVER);
		registerPacket(PacketSetWrechMode.Handler.class, PacketSetWrechMode.class, PacketSide.SERVER);
		registerPacket(PacketSetDesign.Handler.class, PacketSetDesign.class, PacketSide.SERVER);
	}
	
	private static void registerPacket(Class handler, Class packet, PacketSide side)
	{
		if (side != PacketSide.CLIENT)
			ExtraBitManipulation.packetNetwork.registerMessage(handler, packet, packetId++, Side.SERVER);
		
		if (side != PacketSide.SERVER)
			ExtraBitManipulation.packetNetwork.registerMessage(handler, packet, packetId++, Side.CLIENT);
	}
	
	private static enum PacketSide
	{
		SERVER, CLIENT, BOTH;
	}
	
}