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
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    private String imgUrl;

    private String originUrl;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<CollectionItem> collectionItems = new ArrayList<>();

    public void addCollectionItem(CollectionItem collectionItem) {
        this.collectionItems.add(collectionItem);
    }
}
