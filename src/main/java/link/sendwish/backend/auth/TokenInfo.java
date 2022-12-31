package link.sendwish.backend.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class TokenInfo {
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
