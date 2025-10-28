package org.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ConfigManager;
import org.exceptions.DateAlreadySetException;
import org.menus.MenuState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.users.TelegramUser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
@Getter
@Setter
@NoArgsConstructor
public class DateInputHelper{
    private Integer year;
    private Integer month;
    private Integer day;
    private Integer hour;
    private Integer minute;
    private Integer step = 0;
    private Integer yearSelectPage = 0;
    private LocalDateTime date;

    public void goBackOneStep(){
        if(step > 0)
            step--;
        else
            throw new RuntimeException("No se puede retroceder mas");
    }

    public boolean respondTo(String message, TelegramUser user) {
        switch (step){
            case 0:
                if(Objects.equals(message, "/next")){
                    yearSelectPage++;
                    return false;
                }
                if(Objects.equals(message, "/prev")){
                    if(yearSelectPage == 0){
                        return false;
                    }
                    yearSelectPage--;
                    return false;
                }
                year = Integer.parseInt(message);
                step ++;
                break;
            case 1:
                if(message.startsWith("month")){
                    month = Integer.parseInt(message.substring(5));
                    if(month >12 || month< 1) {
                        return false;
                    }
                    step ++;
                }
                break;
            case 2:
                if(Objects.equals(message, "-")){
                    return false;
                }
                try {
                    day = Integer.parseInt(message);
                }catch (Exception e){
                    return false;
                }
                step ++;
                break;
            case 3:
                hour = Integer.parseInt(message);
                step ++;
                break;
            case 4:
                minute = Integer.parseInt(message.substring(message.indexOf(':')+1));
                step ++;
                date= LocalDateTime.of(year, month, day, hour, minute, 0, 0);
                return true;
            case 5:
                throw new DateAlreadySetException("Date already set");
        }
        return false;
    }



    public String getQuestion(TelegramUser user) {
        switch (step){
            case 0:
                return user.getLocalizedMessage("requestInputYear");
            case 1:
                return user.getLocalizedMessage("requestInputMonth");
            case 2:
                return user.getLocalizedMessage("requestInputDay");
            case 3:
                return user.getLocalizedMessage("requestInputHour");
            case 4:
                return user.getLocalizedMessage("requestInputMinute");
            case 5:
                return user.getLocalizedMessage("successfullDateInput");
        }
        System.err.println("Error question date");
        return user.getLocalizedMessage("unsuccessfullDateInput");
    }

    public SendMessage questionMessage(TelegramUser user) {
        switch (step){
            case 0:
                return yearSelectMenu(yearSelectPage, user);
            case 1:
                return monthSelectMenu(user);
            case 2:
                return daySelectMenu(user);
            case 3:
                return hourSelectMenu(user);
            case 4:
                return minuteSelectMenu(user);
        }
        throw new DateAlreadySetException("Date already set");
    }

    private SendMessage yearSelectMenu(Integer page, TelegramUser user){
        int start = LocalDate.now().getYear() + page * ConfigManager.getInstance().getOptionalInteger("input.date.year.limit").orElse(3);
        int finish = start + ConfigManager.getInstance().getOptionalInteger("input.date.year.limit").orElse(3);
        List<String> displayedYears = IntStream.rangeClosed(start, finish-1).mapToObj(String::valueOf)
                .collect(Collectors.toList());
        return InlineMenuBuilder.localizedMenu(user, getQuestion(user),List.of("/prev", "/next"), displayedYears);
    }
    private SendMessage monthSelectMenu(TelegramUser user){
        List<String> displayedMonths = IntStream.rangeClosed(1, 12).mapToObj(n -> "month" + n)
                .collect(Collectors.toList());
        return InlineMenuBuilder.localizedMenu(user, getQuestion(user), displayedMonths.subList(0, 3),displayedMonths.subList(3, 6), displayedMonths.subList(6, 9), displayedMonths.subList(9, 12));
    }
    private SendMessage daySelectMenu(TelegramUser user){
        assert this.month != null;
        assert this.year != null;
        List<String> calendarDays = new ArrayList<>();

        YearMonth ym = YearMonth.of(year, month);

        // Día de la semana del 1 del mes (1=Lunes,2=Martes...)
        int startDayOfWeek = ym.atDay(1).getDayOfWeek().getValue();

        int daysInMonth = ym.lengthOfMonth();

        // Añade "-" para los días vacíos antes del 1°
        for (int i = 1; i < startDayOfWeek; i++) {
            calendarDays.add("-");
        }

        // Añade los días del mes
        for (int day = 1; day <= daysInMonth; day++) {
            calendarDays.add(String.valueOf(day));
        }
        // Añade "-" al final para completar la última semana
        while (calendarDays.size() % 7 != 0) {
            calendarDays.add("-");
        }
        int totalSlots = (startDayOfWeek - 1) + daysInMonth;
        int weeks = (int) Math.ceil(totalSlots / 7.0); // cantidad de filas necesarias

        List<List<String>> rows = new ArrayList<>();
        for (int i = 0; i < weeks * 7; i += 7) {
            rows.add(calendarDays.subList(i, i + 7));
        }
        return InlineMenuBuilder.menu(getQuestion(user), rows.toArray(new List[0]));
    }
    private SendMessage hourSelectMenu(TelegramUser user){
        List<String> displayedHours = IntStream.rangeClosed(1, 24).mapToObj(String::valueOf)
                .collect(Collectors.toList());
        return InlineMenuBuilder.menu(getQuestion(user), displayedHours.subList(0, 8),displayedHours.subList(8, 16), displayedHours.subList(16, 24));
    }
    private SendMessage minuteSelectMenu(TelegramUser user){
        List<String> displayedMinutes = IntStream.rangeClosed(0, 3).mapToObj(n -> String.valueOf(hour) + ":" +(n==0? "00": String.valueOf(n * 15)))
                .collect(Collectors.toList());
        return InlineMenuBuilder.menu(getQuestion(user), displayedMinutes);
    }
}
