package soma.test.waggle.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soma.test.waggle.dto.VivoxMemberInOutDto;
import soma.test.waggle.dto.WorldCreateRequestDto;
import soma.test.waggle.dto.WorldRoomResponseDto;
import soma.test.waggle.dto.photon.PhotonConversationDto;
import soma.test.waggle.dto.photon.PhotonMemberDto;
import soma.test.waggle.dto.photon.PhotonResponseDto;
import soma.test.waggle.dto.photon.PhotonRoomIdDto;
import soma.test.waggle.entity.*;
import soma.test.waggle.redis.repository.RedisSentenceRepository;
import soma.test.waggle.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorldRoomService {

    private final WorldRoomRepository worldRoomRepository;
    private final EntranceRoomRepository entranceRoomRepository;
    private final MemberRepository memberRepository;
    private final ConversationRepositoty conversationRepositoty;
    private final SentenceRepository sentenceRepository;
    private final RedisSentenceRepository redisSentenceRepository;
    private final ConversationService conversationService;


    @Transactional(readOnly = true)
    public List<WorldRoomResponseDto> openWorldRoomList(){
        return worldRoomRepository.findAllByCriteria(OnStatus.Y).stream()
                .map(WorldRoomResponseDto::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public WorldCreateRequestDto createWorldRoom(WorldCreateRequestDto worldCreateRequestDto) {
        return WorldCreateRequestDto.of(worldRoomRepository.save(worldCreateRequestDto.toWorldRoom()));
    }

    @Transactional
    public PhotonResponseDto pathCreateOrClose(PhotonRoomIdDto photonRoomIdDto, OnStatus onStatus) {
        Optional<WorldRoom> findRoom = worldRoomRepository.findById(photonRoomIdDto.getRoomId());
        if (findRoom.isEmpty()) {
            return new PhotonResponseDto(1, "No Room");
        } else {
            WorldRoom worldRoom = findRoom.get();
            worldRoom.setOnStatus(onStatus);
            return new PhotonResponseDto(0, "OK");
        }

    }

    @Transactional
    public PhotonResponseDto pathJoin(PhotonMemberDto photonMemberDto) {

        Member member = memberRepository.find(photonMemberDto.getMemberId());
        WorldRoom worldRoom = worldRoomRepository.find(photonMemberDto.getRoomId());

        EntranceRoom entranceRoom = EntranceRoom
                .builder()
                .worldRoom(worldRoom)
                .member(member)
                .isLast(OnStatus.Y)
                .joinTime(LocalDateTime.now())
                .build();
        entranceRoomRepository.save(entranceRoom);

        worldRoom.setPeople(worldRoom.getPeople()+1);
        member.setEntranceStatus(OnStatus.Y);

        // member가 방에 입장하였으므로 대화 관리(내 대화를 듣고 있는 사람, 토픽 추출을 위한 대화set 모음) 시작
        conversationService.joinRoom(photonMemberDto.getMemberId());

        return new PhotonResponseDto(0, "OK");
    }

    @Transactional
    public PhotonResponseDto pathLeave(PhotonMemberDto photonMemberDto) {

        Member member = memberRepository.find(photonMemberDto.getMemberId());
        WorldRoom worldRoom = worldRoomRepository.find(photonMemberDto.getRoomId());
        EntranceRoom entranceRoom = entranceRoomRepository.findByMemberId(photonMemberDto.getMemberId());

        entranceRoom.setIsLast(OnStatus.N);
        entranceRoom.setLeaveTime(LocalDateTime.now());
        member.setEntranceStatus(OnStatus.N);
        worldRoom.setPeople(worldRoom.getPeople()-1);

        // member가 방을 나갔으므로 대화 관리 제거
        conversationService.leaveRoom(photonMemberDto.getMemberId());

        return new PhotonResponseDto(0, "OK");
    }

    @Transactional
    public PhotonResponseDto pathEvent(PhotonConversationDto photonConversationDto) {
        // 대화 DB 저장 안함
//        Conversation conversation;
//        List<Conversation> findConversation = conversationRepositoty.findByVivoxId(photonConversationDto.getVivoxId());
//        if (findConversation.size() == 0){
//            conversation = Conversation.builder()
//                    .vivoxId(photonConversationDto.getVivoxId())
//                    .worldRoom(worldRoomRepository.findById(photonConversationDto.getRoomId()).get())
//                    .dateTime(LocalDateTime.now())
//                    .build();
//            conversationRepositoty.save(conversation);
//        } else {
//            conversation = findConversation.get(0);
//        }

        // sentence DB 저장 안함
//        Sentence sentence = Sentence.builder()
//                .conversation(conversation)
//                .sentence(photonConversationDto.getSentence())
//                .member(memberRepository.find(photonConversationDto.getMemberId()))
//                .dateTime(LocalDateTime.now())
//                .build();
//        redisSentenceRepository.addSentenceToRedis(Sentence.toRedisDto(sentence), conversation.getVivoxId());

        // 발화 문장이 들어오면 대화 관리를 위해 전달
        conversationService.sentence(photonConversationDto);

        return new PhotonResponseDto(0,"ok");
    }

    // 대화 그래프 관리를 위한 vivox in, out 처리
    public void vivoxMemberIn(VivoxMemberInOutDto vivoxMemberInOutDto){
        conversationService.vivoxMemberIn(vivoxMemberInOutDto);
    }

    public void vivoxMemberOut(VivoxMemberInOutDto vivoxMemberInOutDto){
        conversationService.vivoxMemberOut(vivoxMemberInOutDto);
    }
}
