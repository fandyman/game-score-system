package king.game.score.system.infrastructure;

public enum HttpResponseCode {
    _200_OK(200), _403_FORBIDDEN(403), _404_NOT_FOUND(404);

    Integer httpCode;

    HttpResponseCode(Integer httpCode) {
        this.httpCode = httpCode;
    }

    public Integer getHttpCode() {
        return httpCode;
    }
}
