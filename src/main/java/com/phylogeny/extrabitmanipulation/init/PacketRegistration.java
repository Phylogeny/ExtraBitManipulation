package com.phylogeny.extrabitmanipulation.init;

import net.minecraftforge.fml.relauncher.Side;

import com.phylogeny.extrabitmanipulation.ExtraBitManipulation;
import com.phylogeny.extrabitmanipulation.packet.PacketCycleData;
import com.phylogeny.extrabitmanipulation.packet.PacketSculpt;

public class PacketRegistration
{
	public static int packetId = 0;
	
	public static void registerPackets()
	{
		registerPacket(PacketCycleData.Handler.class, PacketCycleData.class, Side.SERVER);
		registerPacket(PacketSculpt.Handler.class, PacketSculpt.class, Side.SERVER);
	}
	
	private static void registerPacket(Class handler, Class packet, Side side)
	{
		ExtraBitManipulation.packetNetwork.registerMessage(handler, packet, packetId++, Side.SERVER);
	}
	
}