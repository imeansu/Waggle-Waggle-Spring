package soma.test.waggle.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soma.test.waggle.dto.PhotonMemberInOutDto;
import soma.test.waggle.dto.WorldRoomCreateRequestDto;
import soma.test.waggle.dto.WorldRoomListResponseDto;
import soma.test.waggle.dto.WorldRoomResponseDto;
import soma.test.waggle.dto.photon.*;
import soma.test.waggle.service.WorldRoomService;
import soma.test.waggle.service.WorldService;
import soma.test.waggle.type.OnStatusType;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/world")
public class WorldController {

    private final WorldService worldService;
    private final WorldRoomService worldRoomService;

    /**
     * worldRoom 단일 정보
     * */
    @GetMapping("/world-room/{worldRoomId}")
    public ResponseEntity<WorldRoomResponseDto> worldRoomInfo(@PathVariable Long worldRoomId){
        return ResponseEntity.ok(worldRoomService.getWorldRoomInfo(worldRoomId));
    }

    /**
     * Returns: open worldRoom list
     * */
    @GetMapping("/world-room/list")
    public ResponseEntity<WorldRoomListResponseDto> worldRoomList(){
        List<WorldRoomResponseDto> openWorldRoomList = worldRoomService.openWorldRoomList();
        return ResponseEntity.ok(new WorldRoomListResponseDto(openWorldRoomList.size(), openWorldRoomList));
    }

    /**
     * worldRoom 생성
     * */
    @PostMapping("/world-room/new")
    public ResponseEntity<WorldRoomCreateRequestDto> CreateWorld(@RequestBody WorldRoomCreateRequestDto worldRoomCreateRequestDto){
        return ResponseEntity.ok(worldRoomService.createWorldRoom(worldRoomCreateRequestDto));
    }

    /**
     * Pothon game room 생성 확인 webhook
     * */
    @PostMapping("/world-room/path-create")
    public ResponseEntity<PhotonResponseDto> pathCreate(@RequestBody PhotonRoomIdDto photonRoomIdDto){
        return ResponseEntity.ok(worldRoomService.pathCreateOrClose(photonRoomIdDto, OnStatusType.Y));
    }

    /**
     * Pothon game room close webhook
     * */
    @PostMapping("/world-room/path-close")
    public ResponseEntity<PhotonResponseDto> pathClose(@RequestBody PhotonRoomIdDto photonRoomIdDto){
        return ResponseEntity.ok(worldRoomService.pathCreateOrClose(photonRoomIdDto, OnStatusType.N));
    }

    /**
     * 멤버의 worldRoom 입장
     * */
    @PostMapping("/world-room/path-join")
    public ResponseEntity<PhotonResponseDto> pathJoin(@RequestBody PhotonMemberDto photonMemberDto){
        return ResponseEntity.ok(worldRoomService.pathJoin(photonMemberDto));
    }

    /**
     * 멤버의 worldRoom 퇴장
     * */
    @PostMapping("/world-room/path-leave")
    public ResponseEntity<PhotonResponseDto> pathLeave(@RequestBody PhotonMemberDto photonMemberDto){
        return ResponseEntity.ok(worldRoomService.pathLeave(photonMemberDto));
    }

    /**
     * 3가지 이벤트 분기 처리 예정
     *      1. vivoxMemberIn    : 내 영역에 다른 사람이 들어옴
     *      2. vivoxMemberOut   : 내 영역에서 다른 사람이 나감
     *      3. Sentence         : 내가 발화한 문장 전송
     * */
    @PostMapping("/world-room/path-event")
    public ResponseEntity<PhotonResponseDto> pathEvent(@RequestBody PhotonPathEventDto photonPathEventDto){
        ResponseEntity<PhotonResponseDto> responseEntity = null;
        switch (photonPathEventDto.getEventName()){
            case "Sentence":
                PhotonConversationDto photonConversationDto = photonPathEventDto.toConversation();
                responseEntity = ResponseEntity.ok(worldRoomService.pathEvent(photonConversationDto));
            case "MemberInOut":
                PhotonMemberInOutDto photonMemberInOutDto = photonPathEventDto.toMemberInOut();
                responseEntity = ResponseEntity.ok(worldRoomService.pathEvent(photonMemberInOutDto));
        }
        return responseEntity;
    }
}
