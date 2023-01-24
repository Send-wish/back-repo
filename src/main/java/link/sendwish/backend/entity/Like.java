package link.sendwish.backend.entity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@RedisHash(value = "like", timeToLive = 30)
public class Like {

    @Id
    private String id;
    private String nickname;

}