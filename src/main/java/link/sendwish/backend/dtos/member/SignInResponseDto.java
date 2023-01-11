package link.sendwish.backend.dtos.member;

import link.sendwish.backend.auth.TokenInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignInResponseDto {
    private TokenInfo tokenInfo;
    private String nickname;
}
