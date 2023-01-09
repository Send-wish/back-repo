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

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL)
    private List<CollectionItem> collectionItems = new ArrayList<>();

    private String title;

    @Builder.Default
    private int reference = 1;

    public void addMemberCollection(MemberCollection memberCollection) {
        this.memberCollections.add(memberCollection);
    }

    public void deleteMemberCollection(MemberCollection memberCollection) {
        this.memberCollections.remove(memberCollection);
    }

    public void addCollectionItem(CollectionItem collectionItem) {
        this.collectionItems.add(collectionItem);
    }

    public void deleteCollectionItem(CollectionItem collectionItem) {
        this.collectionItems.remove(collectionItem);
    }

    public void changeTitle(String newTitle) {
        this.title = newTitle;
    }

    public void addReference() {
        this.reference += 1;
    }

    public void subtractReference() {
        this.reference -= 1;
    }
}
