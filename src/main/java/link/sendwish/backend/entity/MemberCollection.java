package link.sendwish.backend.entity;


import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class MemberCollection {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id")
    private Collection collection;

}
