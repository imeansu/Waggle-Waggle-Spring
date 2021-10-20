package soma.test.waggle.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soma.test.waggle.dto.photon.PhotonConversationDto;
import soma.test.waggle.dto.WorldCreateRequestDto;
import soma.test.waggle.dto.WorldRoomListResponseDto;
import soma.test.waggle.dto.WorldRoomResponseDto;
import soma.test.waggle.dto.photon.PhotonMemberDto;
import soma.test.waggle.dto.photon.PhotonResponseDto;
import soma.test.waggle.dto.photon.PhotonRoomIdDto;
import soma.test.waggle.type.OnStatusType;
import soma.test.waggle.service.WorldRoomService;
import soma.test.waggle.service.WorldService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/world")
public class WorldController {

    private final WorldService worldService;
    private final WorldRoomService worldRoomService;

    @GetMapping("/world-room/list")
    public ResponseEntity<WorldRoomListResponseDto> worldRoomList(){
        List<WorldRoomResponseDto> openWorldRoomList = worldRoomService.openWorldRoomList();
        return ResponseEntity.ok(new WorldRoomListResponseDto(openWorldRoomList.size(), openWorldRoomList));
    }

    @PostMapping("/world-room/new")
    public ResponseEntity<WorldCreateRequestDto> CreateWorld(@RequestBody WorldCreateRequestDto worldCreateRequestDto){
        return ResponseEntity.ok(worldRoomService.createWorldRoom(worldCreateRequestDto));
    }

    @PostMapping("/world-room/path-create")
    public ResponseEntity<PhotonResponseDto> pathCreate(@RequestBody PhotonRoomIdDto photonRoomIdDto){
        return ResponseEntity.ok(worldRoomService.pathCreateOrClose(photonRoomIdDto, OnStatusType.Y));
    }

    @PostMapping("/world-room/path-close")
    public ResponseEntity<PhotonResponseDto> pathClose(@RequestBody PhotonRoomIdDto photonRoomIdDto){
        return ResponseEntity.ok(worldRoomService.pathCreateOrClose(photonRoomIdDto, OnStatusType.N));
    }

    @PostMapping("/world-room/path-join")
    public ResponseEntity<PhotonResponseDto> pathJoin(@RequestBody PhotonMemberDto photonMemberDto){
        return ResponseEntity.ok(worldRoomService.pathJoin(photonMemberDto));
    }

    @PostMapping("/world-room/path-leave")
    public ResponseEntity<PhotonResponseDto> pathLeave(@RequestBody PhotonMemberDto photonMemberDto){
        return ResponseEntity.ok(worldRoomService.pathLeave(photonMemberDto));
    }

    @PostMapping("/world-room/path-event")
    public ResponseEntity<PhotonResponseDto> pathEvent(@RequestBody PhotonConversationDto photonConversationDto){
        return ResponseEntity.ok(worldRoomService.pathEvent(photonConversationDto));
    }
}
