package org.menus.adminMenu;

import org.eventServerClient.ApiClient;
import org.eventServerClient.dtos.StatsDTO;
import org.menus.MenuState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;
import org.utils.InlineMenuBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public class StatsMenu extends MenuState {
    StatsDTO statsDTO;
    @Override
    public String respondTo(String message) {
        statsDTO = user.getApiClient().getAdminStats();
        switch (message){
            case "/basicStats":
                return user.getLocalizedMessage("printBasicStats", user.localizeDate(LocalDateTime.now()), statsDTO.getEventsCount(), statsDTO.getRegistrationsCount(), statsDTO.getWaitlistPromotions(), statsDTO.getWaitlistConversionRate());
            case "/back":
                user.setMenu(new AdminMenu(user));
                return null;
            default:
                return user.getLocalizedMessage("wrongOption");
        }
    }

    @Override
    public String getQuestion() {
        statsDTO = user.getApiClient().getAdminStats();
        return user.getLocalizedMessage("adminStatsMenuQuestion", user.getLocalizedMessage("/basicStats"));
    }

    @Override
    public SendMessage questionMessage() {
        return InlineMenuBuilder.localizedVerticalMenu(user, getQuestion(), "/basicStats", "/back", "/start");
    }

    public StatsMenu(TelegramUser user) {
        super(user);
    }
}
