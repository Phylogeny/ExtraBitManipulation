package com.phylogeny.extrabitmanipulation.init;

import net.minecraftforge.fml.relauncher.Side;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.packet.PacketCycleBitWrenchMode;
import com.phylogeny.extrabitmanipulation.packet.PacketSculpt;
import com.phylogeny.extrabitmanipulation.packet.PacketSetBitStack;
import com.phylogeny.extrabitmanipulation.packet.PacketSetHollowShape;
import com.phylogeny.extrabitmanipulation.packet.PacketSetEndsOpen;
import com.phylogeny.extrabitmanipulation.packet.PacketSetMode;
import com.phylogeny.extrabitmanipulation.packet.PacketSetSemiDiameter;
import com.phylogeny.extrabitmanipulation.packet.PacketSetShapeType;
import com.phylogeny.extrabitmanipulation.packet.PacketSetTargetBitGridVertexes;
import com.phylogeny.extrabitmanipulation.packet.PacketSetWallThickness;
import com.phylogeny.extrabitmanipulation.packet.PacketSyncAllSculptingData;
import com.phylogeny.extrabitmanipulation.packet.PacketSetRotation;

public class PacketRegistration
{
	public static int packetId = 0;
	
	public static void registerPackets()
	{
		registerPacket(PacketCycleBitWrenchMode.Handler.class, PacketCycleBitWrenchMode.class, Side.SERVER);
		registerPacket(PacketSculpt.Handler.class, PacketSculpt.class, Side.SERVER);
		registerPacket(PacketSyncAllSculptingData.Handler.class, PacketSyncAllSculptingData.class, Side.CLIENT);
		registerPacket(PacketSetRotation.Handler.class, PacketSetRotation.class, Side.SERVER);
		registerPacket(PacketSetShapeType.Handler.class, PacketSetShapeType.class, Side.SERVER);
		registerPacket(PacketSetTargetBitGridVertexes.Handler.class, PacketSetTargetBitGridVertexes.class, Side.SERVER);
		registerPacket(PacketSetSemiDiameter.Handler.class, PacketSetSemiDiameter.class, Side.SERVER);
		registerPacket(PacketSetHollowShape.Handler.class, PacketSetHollowShape.class, Side.SERVER);
		registerPacket(PacketSetEndsOpen.Handler.class, PacketSetEndsOpen.class, Side.SERVER);
		registerPacket(PacketSetWallThickness.Handler.class, PacketSetWallThickness.class, Side.SERVER);
		registerPacket(PacketSetBitStack.Handler.class, PacketSetBitStack.class, Side.SERVER);
		registerPacket(PacketSetMode.Handler.class, PacketSetMode.class, Side.SERVER);
	}
	
	private static void registerPacket(Class handler, Class packet, Side side)
	{
		ExtraBitManipulation.packetNetwork.registerMessage(handler, packet, packetId++, side);
	}
	
}