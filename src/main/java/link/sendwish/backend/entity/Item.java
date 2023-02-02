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

    @Column(nullable = false,length = 1000)
    private String imgUrl;

    @Column(nullable = false)
    private String originUrl;

    @Builder.Default
    private int reference = 1;

    private String category;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<CollectionItem> collectionItems = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<MemberItem> memberItems = new ArrayList<>();

    public void addCollectionItem(CollectionItem collectionItem) {
        this.collectionItems.add(collectionItem);
    }

    public void deleteCollectionItem(CollectionItem collectionItem) {
        this.collectionItems.remove(collectionItem);
    }

    public void addMemberItem(MemberItem memberItem) {
        this.memberItems.add(memberItem);
    }

    public void deleteMemberItem(MemberItem memberItem) {
        this.memberItems.remove(memberItem);
    }

    public void addReference() {
        this.reference += 1;
    }

    public void subtractReference() {
        this.reference -= 1;
    }

    public void updateCategory(String category) {
        this.category = category;
    }

    public void updatePrice(Integer price) {
        this.price = price;
    }
}
