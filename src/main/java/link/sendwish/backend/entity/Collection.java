package link.sendwish.backend.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Collection extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "collection",cascade = CascadeType.ALL)
    private List<MemberCollection> memberCollections = new ArrayList<>();

    private String title;

    public void addMemberCollection(MemberCollection memberCollection) {
        this.memberCollections.add(memberCollection);
    }
}
