package link.sendwish.backend.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Member implements UserDetails {

    @Id
    @Column(name = "key_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<MemberCollection> memberCollections = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<MemberItem> memberItems = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "member_id")
    private List<MemberFriend> friends = new ArrayList<>();

    public void addMemberCollection(MemberCollection memberCollection) {
        this.memberCollections.add(memberCollection);
    }

    public void deleteMemberCollection(MemberCollection memberCollection) {
        this.memberCollections.remove(memberCollection);
    }

    public void addMemberItem(MemberItem memberItem) {
        this.memberItems.add(memberItem);
    }

    public void deleteMemberItem(MemberItem memberItem) {
        this.memberItems.remove(memberItem);
    }

    public void addFriendInList(MemberFriend friend){
        this.friends.add(friend);
    }

    public void removeFriendInList(MemberFriend friend) {
        this.friends.remove(friend);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return nickname;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
