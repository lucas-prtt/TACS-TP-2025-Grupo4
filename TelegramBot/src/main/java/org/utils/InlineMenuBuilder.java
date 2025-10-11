package org.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.users.TelegramUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InlineMenuBuilder {

    public static SendMessage localizedMenu(TelegramUser user, String text, List<String>... lines) {
        List<List<Button>> layout = new ArrayList<>();
        for (List<String> line: lines) {
            List<Button> row = new ArrayList<>();
            for (String option : line) {
                row.add(new Button(user.getLocalizedMessage(option), option)); // texto, comando
            }
            layout.add(row);
        }
        return menu(text, layout);
    }
    public static SendMessage localizedVerticalMenu(TelegramUser user, String text, String... lines) {
        List<List<Button>> layout = new ArrayList<>();
        for (String line: lines) {
            List<Button> row = new ArrayList<>();
            row.add(new Button(user.getLocalizedMessage(line), line));
            layout.add(row);
        }
        return menu(text, layout);
    }


    public static SendMessage menu(String text, List<String>... lines) {
        List<List<Button>> layout = new ArrayList<>();

        for (List<String> line : lines) {
            List<Button> row = new ArrayList<>();
            for (String option : line) {
                row.add(new Button(option, option)); // texto = callback_data
            }
            layout.add(row);
        }

        return menu(text, layout);
    }

    public static SendMessage menu(String text, Map<String, String>... lines) {
        List<List<Button>> layout = new ArrayList<>();
        for (Map<String, String> line : lines) {
            List<Button> row = new ArrayList<>();
            for (Map.Entry<String, String> entry : line.entrySet()) {
                row.add(new Button(entry.getKey(), entry.getValue()));
            }
            layout.add(row);
        }
        return menu(text, layout);
    }


    public static SendMessage menu(String text, List<List<Button>> layout) {
        SendMessage message = new SendMessage();
        message.setText(text);
        message.setReplyMarkup(toMarkup(layout));
        return message;
    }

    public static class Button {
        public final String label;
        public final String data;

        public Button(String label, String data) {
            this.label = label;
            this.data = data;
        }
    }

    private static InlineKeyboardMarkup toMarkup(List<List<Button>> layout) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (List<Button> row : layout) {
            List<InlineKeyboardButton> buttonRow = new ArrayList<>();
            for (Button b : row) {
                InlineKeyboardButton btn = new InlineKeyboardButton();
                btn.setText(b.label);
                btn.setCallbackData(b.data);
                buttonRow.add(btn);
            }
            keyboard.add(buttonRow);
        }
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }
}
