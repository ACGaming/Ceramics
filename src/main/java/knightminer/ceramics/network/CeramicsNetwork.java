package knightminer.ceramics.network;

import knightminer.ceramics.Ceramics;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class CeramicsNetwork {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Ceramics.modID);

	private static int id = 0;
	public static void registerPackets() {
		PacketHandler handler = new PacketHandler();

		// generic
		INSTANCE.registerMessage(handler, FluidUpdatePacket.class, id++, Side.CLIENT);

		// barrel
		INSTANCE.registerMessage(handler, BarrelSizeChangedPacket.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(handler, ExtensionMasterUpdatePacket.class, id++, Side.CLIENT);

		// channel
		INSTANCE.registerMessage(handler, ChannelConnectionPacket.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(handler, ChannelFlowPacket.class, id++, Side.CLIENT);
	}

	public static void sendToAllAround(World world, BlockPos pos, PacketBase message) {
		INSTANCE.sendToAllAround(message, new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 64));
	}

	public static void sendToClients(WorldServer world, BlockPos pos, PacketBase packet) {
		Chunk chunk = world.getChunkFromBlockCoords(pos);
		for(EntityPlayer player : world.playerEntities) {
			// only send to relevant players
			if(!(player instanceof EntityPlayerMP)) {
				continue;
			}
			EntityPlayerMP playerMP = (EntityPlayerMP) player;
			if(world.getPlayerChunkMap().isPlayerWatchingChunk(playerMP, chunk.x, chunk.z)) {
				INSTANCE.sendTo(packet, playerMP);
			}
		}
	}

	public static class PacketHandler implements IMessageHandler<PacketBase, IMessage> {
		@Override
		public IMessage onMessage(final PacketBase message, MessageContext ctx) {
			if (ctx.side.isClient()) {
				NetHandlerPlayClient handler = ctx.getClientHandler();
				FMLCommonHandler.instance().getWorldThread(handler).addScheduledTask(() -> message.handleClient(handler));
			} else {
				// do not use serverside packets yet, so for now just throw
				throw new UnsupportedOperationException("Serverside only packets not currently supported");
			}
			return null;
		}
	}
}
