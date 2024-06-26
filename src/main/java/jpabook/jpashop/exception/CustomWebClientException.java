package jpabook.jpashop.exception;

import org.springframework.http.HttpStatusCode;

public class CustomWebClientException extends RuntimeException {
    private final HttpStatusCode status;
    private final String errorBody;

    public CustomWebClientException(HttpStatusCode status, String errorBody) {
        super("Status: " + status + ", Error: " + errorBody);
        this.status = status;
        this.errorBody = errorBody;
    }

    public HttpStatusCode getStatus() {
        return status;
    }

    public String getErrorBody() {
        return errorBody;
    }
}
