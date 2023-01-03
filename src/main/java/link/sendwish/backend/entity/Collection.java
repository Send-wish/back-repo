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
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "collection")
    private List<MemberCollection> memberCollections = new ArrayList<>();

    private String title;
}
