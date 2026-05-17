package org.job.config;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Component
@Profile("proxy")
public class TelegramProxyClientConfig {

    @Bean
    public TelegramClient telegramClient(
            @Value("${telegram.bot.token}") String token,
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

        OkHttpClient client = new OkHttpClient.Builder()
                .proxy(proxy)
                .proxyAuthenticator(proxyAuthenticator)
                .build();

        return new OkHttpTelegramClient(client, token);
    }
}
