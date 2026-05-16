package org.job.consumer;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.job.utils.Constants.LONG_PROCESS;
import static org.job.utils.Constants.RANDOM;
import static org.job.utils.Constants.START;
import static org.job.utils.Constants.MY_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;

    @SneakyThrows
    @Override
    public void consume(Update update) {
        if (update.hasMessage()) {
            String textMessage = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            log.info("Get message {} from {}", textMessage, chatId);

            if (START.equals(textMessage)) {
                sendMainMenu(chatId);
            } else {
                sendMessage(chatId, "I don't understand you");
            }
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }
    }

    @SneakyThrows
    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getFrom().getId();
        User user = callbackQuery.getFrom();

        switch (data) {
            case MY_NAME -> sendMyName(chatId, user);
            case RANDOM -> sendRandom(chatId);
            case LONG_PROCESS -> sendImage(chatId);
            default -> sendMessage(chatId, "Unknown command");
        }
    }

    private void sendMainMenu(Long chatId) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .text("Welcome, please choose an action.")
                .chatId(chatId)
                .build();

        message.setReplyMarkup(buildInlineKeyboardMarkup());

        telegramClient.execute(message);
    }

    private InlineKeyboardMarkup buildInlineKeyboardMarkup() {
        InlineKeyboardButton button1 = InlineKeyboardButton.builder()
                .text("What's my name")
                .callbackData(MY_NAME)
                .build();

        InlineKeyboardButton button2 = InlineKeyboardButton.builder()
                .text("Random number")
                .callbackData(RANDOM)
                .build();

        InlineKeyboardButton button3 = InlineKeyboardButton.builder()
                .text("Load picture")
                .callbackData(LONG_PROCESS)
                .build();

        List<InlineKeyboardRow> keyboardButtons = List.of(
                new InlineKeyboardRow(button1),
                new InlineKeyboardRow(button2),
                new InlineKeyboardRow(button3)
        );

        return new InlineKeyboardMarkup(keyboardButtons);
    }

    private void sendMessage(Long chatId, String textMessage) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .text(textMessage)
                .chatId(chatId)
                .build();

        telegramClient.execute(message);
    }

    private void sendMyName(Long chatId, User user) throws TelegramApiException {
        String text = String.format(
                "Hello!\n\nYour name: %s\nYour username: @%s",
                user.getFirstName() + user.getLastName(),
                user.getUserName()
        );
        sendMessage(chatId, text);
    }

    private void sendRandom(Long chatId) throws TelegramApiException {
        int randomInt = ThreadLocalRandom.current().nextInt();;
        sendMessage(chatId, "Random number: " + randomInt);
    }

    private void sendImage(Long chatId) throws TelegramApiException {
        sendMessage(chatId, "Load Image");
        new Thread(() -> {
            String imageUrl = "https://random.imagecdn.app/500/150";
            try {
                URL url = new URL(imageUrl);
                InputStream inputStream = url.openStream();

                SendPhoto sendPhoto = SendPhoto.builder()
                        .chatId(chatId)
                        .photo(new InputFile(inputStream, "random.jpg"))
                        .caption("Your random picture ")
                        .build();

                telegramClient.execute(sendPhoto);
            } catch (TelegramApiException | IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
