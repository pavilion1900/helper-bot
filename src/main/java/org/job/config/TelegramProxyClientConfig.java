package org.job.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;

@Component
@Profile("proxy")
public class TelegramProxyClientConfig {

    @Bean
    public TelegramClient telegramClient(
            OkHttpClient okHttpClient,
            @Value("${telegram.bot.token}") String token
    ) {
        return new OkHttpTelegramClient(okHttpClient, token);
    }

    @Bean
    public TelegramBotsLongPollingApplication telegramBotsLongPollingApplication(OkHttpClient okHttpClient) {
        return new TelegramBotsLongPollingApplication(ObjectMapper::new, () -> okHttpClient);
    }

    @Bean
    public OkHttpClient okHttpClient(
            @Value("${proxy.host}") String proxyHost,
            @Value("${proxy.port}") Integer proxyPort,
            @Value("${proxy.user}") String user,
            @Value("${proxy.password}") String password
    ) {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));

        Authenticator proxyAuthenticator = (route, response) -> {
            String credential = Credentials.basic(user, password);
            return response.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();
        };

        return new OkHttpClient.Builder()
                .proxy(proxy)
                .proxyAuthenticator(proxyAuthenticator)
                .connectTimeout(Duration.ofSeconds(30))
                .readTimeout(Duration.ofSeconds(100))
                .writeTimeout(Duration.ofSeconds(30))
                .build();
    }
}
