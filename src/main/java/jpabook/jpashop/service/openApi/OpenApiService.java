package jpabook.jpashop.service.openApi;

import jakarta.annotation.PostConstruct;
import jpabook.jpashop.exception.CustomWebClientException;
import jpabook.jpashop.service.openApi.dto.GeoResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@Service
public class OpenApiService {

    @Value("${secret_key.kakao}")
    private String secretKey;
    private String geocordingApi = "https://dapi.kakao.com/v2";

    // 기본 WebClient 객체
    private WebClient webClient;

    /**
     * secretKey가 초기화 되기 전에 webClient가 먼저 생성되기 때문에<br>
     * 생성자 초기화 메소드를 사용하지 않으면 secretKey가 제대로 잡히지 않는다.<br>
     * 따라서 생성자 초기화 메소드를 작성해 secretKey가 초기화 된 후 webClient를 빌드하도록 해야 한다.
     */
    @PostConstruct
    private void init() {
        this.webClient = WebClient.builder()
                .baseUrl(geocordingApi)
                .defaultHeader("Authorization", "KakaoAK " + secretKey)
                .build();
    }


    public Mono<GeoResponse> getGeoWithDto(String address) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/local/search/address.json")
                        .queryParam("query", address)
                        .build())
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(GeoResponse.class);
                    } else {
                        return clientResponse.createException()
                                .flatMap(Mono::error);
                    }
                });
    }


    @Data
    static class AddressResponse {
        private List<Document> documents;

        @Data
        static class Document {
            private String x;
            private String y;
        }
    }

}
