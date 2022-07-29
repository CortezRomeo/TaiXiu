package com.cortezromeo.taixiu.api.event;

import com.cortezromeo.taixiu.api.TaiXiuResult;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerBetEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final TaiXiuResult bet;
    private final long money;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public PlayerBetEvent(Player player, TaiXiuResult bet, long money) {
        this.player = player;
        this.bet = bet;
        this.money = money;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return this.player;
    }

    public TaiXiuResult getBet() {
        return this.bet;
    }

    public long getMoney() {
        return this.money;
    }

}