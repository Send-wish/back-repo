package link.sendwish.backend.service;

import link.sendwish.backend.common.exception.MemberNotFoundException;
import link.sendwish.backend.common.exception.ChatRoomNotFoundException;
import link.sendwish.backend.dtos.chat.ChatVoteEnterRequestDto;
import link.sendwish.backend.dtos.chat.ChatVoteEnterResponseDto;
import link.sendwish.backend.dtos.chat.ChatVoteRequestDto;
import link.sendwish.backend.dtos.chat.ChatVoteResponseDto;
import link.sendwish.backend.entity.*;
import link.sendwish.backend.repository.ChatRoomRepository;
import link.sendwish.backend.repository.ChatVoteMemberRepository;
import link.sendwish.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class VoteService {
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatVoteMemberRepository chatVoteMemberRepository;
    private final RedisTemplate<String, String> redisTemplate;


    @Transactional
    public ChatVoteEnterResponseDto enterVote(ChatVoteEnterRequestDto dto){
        Member member = memberRepository.findByNickname(dto.getNickname()).orElseThrow(MemberNotFoundException::new);
        ChatRoom chatRoom = chatRoomRepository.findById(dto.getRoomId()).orElseThrow(ChatRoomNotFoundException::new);

        ChatVoteMember find = chatVoteMemberRepository.findByMemberAndChatRoom(member, chatRoom).orElse(null);
        if(find == null){ // 투표에 참여한 적이 없다면
            find = ChatVoteMember.builder()
                    .member(member)
                    .chatRoom(chatRoom)
                    .build();
            chatVoteMemberRepository.save(find);
            chatRoom.addChatVoteMember(find);
            member.addChatVoteMember(find);
        }

        List<ChatVoteMember> voteMembers = chatVoteMemberRepository.findMemberByChatRoom(chatRoom).get();
        List<String> voteMemberIdList = voteMembers.stream().map(target -> target.getMember().getNickname()).toList();

        assert chatRoom.getId().equals(find.getChatRoom().getId());
        log.info("투표 입장 [roomId] : {}, [입장 인원] : {}", chatRoom.getId(), voteMemberIdList.size());
        return ChatVoteEnterResponseDto.builder()
                .memberIdList(voteMemberIdList)
                .build();
    }

    @Transactional
    public ChatVoteResponseDto like(ChatVoteRequestDto dto) {
        ValueOperations<String, String> like = redisTemplate.opsForValue();
        String key = dto.getRoomId() + ":" + dto.getItemId();
        String value = dto.getNickname();

        String find = like.get(key);
        if (dto.getIsLike()) { // 좋아요 누른 경우
            if (find == null) {
                like.set(key, value);
            } else {
                if (!find.contains(value)) {
                    if (find.equals("")) {
                        like.set(key, value);
                    } else {
                        like.set(key, find + "," + value);
                    }
                }
            }
        } else {
            if (find != null && find.contains(value)) {
                if(find.contains(",")) {
                    like.set(key, find.replace("," + value, ""));
                }else{
                    like.set(key, "");
                }
            }
        }

        String result = like.get(key);
        Integer count = 0;
        if (!Objects.equals(result, "")) {
            count = result.length()
                    - result.replace(",", "").length() + 1;
        }
        log.info("투표 [roomId] : {}, [itemId] : {}, [like count] : {}", dto.getRoomId(), dto.getItemId(), count);

        return ChatVoteResponseDto.builder()
                .itemId(dto.getItemId())
                .like(count)
                .build();
    }

    @Transactional
    public ChatVoteEnterResponseDto exitVote(ChatVoteEnterRequestDto dto){
        Member member = memberRepository.findByNickname(dto.getNickname()).orElseThrow(MemberNotFoundException::new);
        ChatRoom chatRoom = chatRoomRepository.findById(dto.getRoomId()).orElseThrow(ChatRoomNotFoundException::new);

        ChatVoteMember find = chatVoteMemberRepository.findByMemberAndChatRoom(member, chatRoom).orElse(null);
        if(find != null){
            chatVoteMemberRepository.delete(find);
            chatRoom.deleteChatVoteMember(find);
            member.deleteChatVoteMember(find);
        }

        List<ChatVoteMember> voteMembers = chatVoteMemberRepository.findMemberByChatRoom(chatRoom).orElse(null);
        if(voteMembers == null){
            log.info("투표 퇴장 [roomId] : {}, [남은 인원] : {}", chatRoom.getId(), 0);
            return ChatVoteEnterResponseDto.builder()
                    .memberIdList(null)
                    .build();
        }
        List<String> voteMemberIdList = voteMembers.stream().map(target -> target.getMember().getNickname()).toList();

        assert chatRoom.getId().equals(find.getChatRoom().getId());
        log.info("투표 퇴장 [roomId] : {}, [남은 인원] : {}", chatRoom.getId(), voteMemberIdList.size());
        return ChatVoteEnterResponseDto.builder()
                .memberIdList(voteMemberIdList)
                .build();
    }

}
