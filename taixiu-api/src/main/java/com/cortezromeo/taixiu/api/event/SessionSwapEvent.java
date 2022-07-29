package com.cortezromeo.taixiu.api.event;

import com.cortezromeo.taixiu.api.storage.ISession;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SessionSwapEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final ISession oldSessionData;
    private final ISession newSessionData;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public SessionSwapEvent(ISession oldSessionData, ISession newSessionData) {
        this.oldSessionData = oldSessionData;
        this.newSessionData = newSessionData;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public ISession getOldSessionData() {
        return this.oldSessionData;
    }

    public ISession getNewSessionData() {
        return this.newSessionData;
    }

}