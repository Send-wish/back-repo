package link.sendwish.backend.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Slf4j
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

    private static String DEFAULT_URL = "https://sendwish-img-bucket.s3.ap-northeast-2.amazonaws.com/collection_default.png";

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

    public String getDefaultURL() {
       int collectionItemsLength = this.collectionItems.size();

        if (this.collectionItems.isEmpty()) {
//            log.info("collectionItems is empty: {}", this.DEFAULT_URL);
            return this.DEFAULT_URL;
        } else {
//            log.info("collectionItems is not empty: {}", this.collectionItems.get(collectionItemsLength-1).getItem().getImgUrl());
            return this.collectionItems.get(collectionItemsLength-1).getItem().getImgUrl();
        }
    }
}
