package link.sendwish.backend.entity;

import lombok.*;
import javax.persistence.*;


@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class MemberFriend {

    @Id
    @Column(name = "key_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="friend_id")
    private Long friendId; // 친구가 되는 사람
}
