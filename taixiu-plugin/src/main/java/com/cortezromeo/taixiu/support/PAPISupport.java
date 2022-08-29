package com.cortezromeo.taixiu.support;

import com.cortezromeo.taixiu.TaiXiu;
import com.cortezromeo.taixiu.api.storage.ISession;
import com.cortezromeo.taixiu.manager.DatabaseManager;
import com.cortezromeo.taixiu.manager.TaiXiuManager;
import com.cortezromeo.taixiu.util.MessageUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PAPISupport extends PlaceholderExpansion {

    @Override
    public String getAuthor() {
        return "Cortez_Romeo";
    }

    @Override
    public String getIdentifier() {
        return "taixiu";
    }

    @Override
    public String getVersion() {
        return TaiXiu.plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String s) {

        if (s == null)
            return null;

        if (s.startsWith("result_phien_")) {
            ISession session = DatabaseManager.taiXiuData.get(Long.valueOf(s.replace("result_phien_", "")));
            if (session == null) {
                return "";
            }
            return String.valueOf(session.getResult());
        }

        if (s.startsWith("resultformat_phien_")) {
            ISession session = DatabaseManager.taiXiuData.get(Long.valueOf(s.replace("resultformat_phien_", "")));
            if (session == null) {
                return "";
            }
            return MessageUtil.getFormatName(session.getResult());
        }

        if (s.startsWith("taiplayers_phien_")) {
            ISession session = DatabaseManager.taiXiuData.get(Long.valueOf(s.replace("taiplayers_phien_", "")));
            if (session == null) {
                return "";
            }
            return String.valueOf(session.getTaiPlayers());
        }

        if (s.startsWith("xiuplayers_phien_")) {
            ISession session = DatabaseManager.taiXiuData.get(Long.valueOf(s.replace("xiuplayers_phien_", "")));
            if (session == null) {
                return "";
            }
            return String.valueOf(session.getXiuPlayers());
        }

        if (s.startsWith("taiplayers_bet_phien_")) {
            ISession session = DatabaseManager.taiXiuData.get(Long.valueOf(s.replace("taiplayers_bet_phien_", "")));
            if (session == null) {
                return "";
            }

            long sum = 0L;
            if (session.getTaiPlayers() != null) {
                for (long value : session.getTaiPlayers().values()) {
                    sum += value;
                }
            }

            return String.valueOf(sum);
        }

        if (s.startsWith("xiuplayers_bet_phien_")) {
            ISession session = DatabaseManager.taiXiuData.get(Long.valueOf(s.replace("xiuplayers_bet_phien_", "")));
            if (session == null) {
                return "";
            }

            long sum = 0L;
            if (session.getXiuPlayers() != null) {
                for (long value : session.getXiuPlayers().values()) {
                    sum += value;
                }
            }

            return String.valueOf(sum);
        }

        if (s.startsWith("totalbet_phien_")) {
            ISession session = DatabaseManager.taiXiuData.get(Long.valueOf(s.replace("totalbet_phien_", "")));
            if (session == null) {
                return "";
            }

            return String.valueOf(TaiXiuManager.getTotalBet(session));
        }

        return null;
    }
}
