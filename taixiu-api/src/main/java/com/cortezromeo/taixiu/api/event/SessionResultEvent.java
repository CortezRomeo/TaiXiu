package com.cortezromeo.taixiu.api.event;

import com.cortezromeo.taixiu.api.storage.ISession;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SessionResultEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final ISession sessionData;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public SessionResultEvent(ISession sessionData) {
        this.sessionData = sessionData;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public ISession getSessionData() {
        return this.sessionData;
    }

}