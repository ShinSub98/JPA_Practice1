package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jpabook.jpashop.service.openApi.OpenApiService;
import jpabook.jpashop.service.openApi.dto.GeoResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class OpenApiController {

    private final OpenApiService openApiService;

    @PostMapping("/api/v1/geo")
    public Mono<GeoResponse> getGeoV1(@RequestBody @Valid GeoRequest request) {
        return openApiService.getGeoWithDto(request.getAddress());
    }

    @Data
    static class GeoRequest {
        private String address;

        public GeoRequest() {}

        public GeoRequest(String address) {
            this.address = address;
        }
    }
}
