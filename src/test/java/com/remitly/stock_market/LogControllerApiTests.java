package com.remitly.stock_market;

import com.remitly.stock_market.repository.WalletActionEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class LogControllerApiTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private WalletActionEntityRepository walletActionEntityRepository;

    private RestTestClient restTestClient;

    @BeforeEach
    void setUp() {
        restTestClient = RestTestClient.bindToApplicationContext(context).build();
        if (walletActionEntityRepository.count() > 0) {
            walletActionEntityRepository.deleteAll();
        }
    }

    @Test
    void getAllLogsReturnsEmptyList() {
        restTestClient.get()
                .uri("/log")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.logs.length()").isEqualTo(0);
    }

    @Test
    void getAllLogsReturnsLogsAfterWalletActions() {
        // Simulate a buy action
        setBankStocks("[{\"name\":\"AAPL\",\"quantity\":2}]");
        restTestClient.post()
                .uri("/wallets/log-wallet/stocks/AAPL")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"type\":\"buy\"}")
                .exchange()
                .expectStatus().isOk();

        restTestClient.get()
                .uri("/log")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.logs.length()").isEqualTo(1)
                .jsonPath("$.logs[0].type").isEqualTo("buy")
                .jsonPath("$.logs[0].wallet_id").isEqualTo("log-wallet")
                .jsonPath("$.logs[0].stock_name").isEqualTo("AAPL");
    }

    private void setBankStocks(String stocksJsonArray) {
        restTestClient.post()
                .uri("/stocks")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"stocks\":" + stocksJsonArray + "}")
                .exchange()
                .expectStatus().isOk();
    }
}
