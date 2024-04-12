package com.pandapulsestudios.pulseconfig.Events;

import com.pandapulsestudios.pulseconfig.Interface.PulseConfig;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PulseConfigSaveEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final PulseConfig pulseConfig;

    public PulseConfigSaveEvent(PulseConfig pulseConfig){
        this.pulseConfig = pulseConfig;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    public HandlerList getHandlers() {
        return handlers;
    }
    public boolean isCancelled() {
        return cancelled;
    }
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public PulseConfig getConfig(){return pulseConfig;}
}
