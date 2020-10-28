// 
// Decompiled by Procyon v0.5.36
// 

package com.NickC1211.korra.Stream;

import org.bukkit.event.EventHandler;
import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.BendingPlayer;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.Listener;

public class StreamListener implements Listener
{
    @EventHandler
    public void onClick(final PlayerAnimationEvent event) {
        if (event.isCancelled()) {
            return;
        }
        final BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(event.getPlayer());
        if (bPlayer != null && bPlayer.canBend(CoreAbility.getAbility("Stream"))) {
            new Stream(event.getPlayer());
        }
    }
}
