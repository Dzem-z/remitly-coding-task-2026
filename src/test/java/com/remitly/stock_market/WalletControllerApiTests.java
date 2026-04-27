package com.remitly.stock_market;

import com.remitly.stock_market.repository.WalletEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class WalletControllerApiTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private WalletEntityRepository walletEntityRepository;

    private RestTestClient restTestClient;

    @BeforeEach
    void setUp() {
        restTestClient = RestTestClient.bindToApplicationContext(context).build();
        if (walletEntityRepository.count() > 0) {
            walletEntityRepository.deleteAll();
        }
        setBankStocks("[]");
    }

    @Test
    void postBuyCreatesWalletWhenMissingAndReturnsOk() {
        setBankStocks("[{\"name\":\"AAPL\",\"quantity\":2}]");

        restTestClient.post()
                .uri("/wallets/wallet-create/stocks/AAPL")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"type\":\"buy\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("wallet-create")
                .jsonPath("$.stocks[0].name").isEqualTo("AAPL")
                .jsonPath("$.stocks[0].quantity").isEqualTo(1);

        restTestClient.get()
                .uri("/wallets/wallet-create")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("wallet-create")
                .jsonPath("$.stocks[0].name").isEqualTo("AAPL")
                .jsonPath("$.stocks[0].quantity").isEqualTo(1);
    }

    @Test
    void postBuyWithMissingStockReturnsNotFound() {
        setBankStocks("[{\"name\":\"TSLA\",\"quantity\":2}]");

        restTestClient.post()
                .uri("/wallets/wallet-missing-stock/stocks/AAPL")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"type\":\"buy\"}")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void postBuyWithNoStockInBankReturnsBadRequest() {
        setBankStocks("[{\"name\":\"AAPL\",\"quantity\":0}]");

        restTestClient.post()
                .uri("/wallets/wallet-no-bank-stock/stocks/AAPL")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"type\":\"buy\"}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void postSellWithNoStockInWalletReturnsBadRequest() {
        setBankStocks("[{\"name\":\"AAPL\",\"quantity\":3}]");

        restTestClient.post()
                .uri("/wallets/wallet-no-wallet-stock/stocks/AAPL")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"type\":\"sell\"}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void buyAndSellAffectBankStockQuantity() {
        setBankStocks("[{\"name\":\"AAPL\",\"quantity\":2}]");

        restTestClient.post()
                .uri("/wallets/wallet-bank-impact/stocks/AAPL")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"type\":\"buy\"}")
                .exchange()
                .expectStatus().isOk();

        restTestClient.get()
                .uri("/stocks")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.stocks[0].name").isEqualTo("AAPL")
                .jsonPath("$.stocks[0].quantity").isEqualTo(1);

        restTestClient.post()
                .uri("/wallets/wallet-bank-impact/stocks/AAPL")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"type\":\"sell\"}")
                .exchange()
                .expectStatus().isOk();

        restTestClient.get()
                .uri("/stocks")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.stocks[0].name").isEqualTo("AAPL")
                .jsonPath("$.stocks[0].quantity").isEqualTo(2);
    }

    @Test
    void getWalletReturnsCurrentState() {
        setBankStocks("[{\"name\":\"stock1\",\"quantity\":100},{\"name\":\"stock2\",\"quantity\":2}]");

        restTestClient.post()
                .uri("/wallets/wallet-state/stocks/stock1")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"type\":\"buy\"}")
                .exchange()
                .expectStatus().isOk();

        restTestClient.post()
                .uri("/wallets/wallet-state/stocks/stock2")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"type\":\"buy\"}")
                .exchange()
                .expectStatus().isOk();

        restTestClient.get()
                .uri("/wallets/wallet-state")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("wallet-state")
                .jsonPath("$.stocks.length()").isEqualTo(2)
                .jsonPath("$.stocks[0].name").isEqualTo("stock1")
                .jsonPath("$.stocks[0].quantity").isEqualTo(1)
                .jsonPath("$.stocks[1].name").isEqualTo("stock2")
                .jsonPath("$.stocks[1].quantity").isEqualTo(1);
    }

    @Test
    void getWalletStockReturnsSingleNumber() {
        setBankStocks("[{\"name\":\"AAPL\",\"quantity\":2}]");

        restTestClient.post()
                .uri("/wallets/wallet-quantity/stocks/AAPL")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"type\":\"buy\"}")
                .exchange()
                .expectStatus().isOk();

        restTestClient.get()
                .uri("/wallets/wallet-quantity/stocks/AAPL")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("1");
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
