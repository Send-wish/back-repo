package link.sendwish.backend.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Slf4j
public class Collection extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL)
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

    public List<CollectionItem> getReverseCollectionItems() {
        List<CollectionItem> reverseCollectionItems = new ArrayList<>(this.collectionItems);
        Collections.reverse(reverseCollectionItems);
        return reverseCollectionItems;
    }

    public List<String> getDefaultImgURL() {

        List<String> imgList = new ArrayList<>(4);
        int collectionItemsSize = this.collectionItems.size();
        int collectionItemsImgSize = collectionItemsSize > 4 ? 4 : collectionItemsSize;

        int defaultImgSize = 4 - collectionItemsImgSize;

        // 아이템의 개수에 따라 아이템과 디폴트 이미지를 합친 리스트 반환
        for (int i = 0; i < collectionItemsImgSize; i++) {
            imgList.add(collectionItems.get(collectionItemsSize-i-1).getItem().getImgUrl());
        }
        for (int i = 0; i < defaultImgSize; i++) {
            imgList.add(DEFAULT_URL);
        }

        return imgList;
    }
}
