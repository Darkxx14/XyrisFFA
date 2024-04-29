package dev.darkxx.ffa.settings;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import dev.darkxx.ffa.Main;
import net.minecraft.network.protocol.game.ClientboundHurtAnimationPacket;
import org.bukkit.entity.Player;

public class OldDamageTilt extends PacketAdapter {

    public OldDamageTilt(Main main) {
        super(main, ListenerPriority.NORMAL, PacketType.Play.Server.HURT_ANIMATION);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {}

    @Override
    public void onPacketSending(PacketEvent event) {
        if (!(event.getPacket().getType() == PacketType.Play.Server.HURT_ANIMATION)) {
            return;
        }

        Player player = event.getPlayer();
        if (!SettingsManager.hasEnabledSetting(player, "OldDamageTilt")) {
            return;
        }

        float modifiedYaw = -180.0f;
        ClientboundHurtAnimationPacket modifiedPacket = new ClientboundHurtAnimationPacket(0, modifiedYaw);
        event.setPacket(PacketContainer.fromPacket(modifiedPacket));
    }
}