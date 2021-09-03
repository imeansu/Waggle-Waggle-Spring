package soma.test.waggle.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soma.test.waggle.dto.WorldCreateRequestDto;
import soma.test.waggle.dto.WorldRoomListResponseDto;
import soma.test.waggle.dto.WorldRoomResponseDto;
import soma.test.waggle.service.WorldRoomService;
import soma.test.waggle.service.WorldService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/world")
public class WorldController {

    private final WorldService worldService;
    private final WorldRoomService worldRoomService;

    @GetMapping("/world_room/list")
    public ResponseEntity<WorldRoomListResponseDto> worldRoomList(){
        List<WorldRoomResponseDto> openWorldRoomList = worldRoomService.openWorldRoomList();
        return ResponseEntity.ok(new WorldRoomListResponseDto(openWorldRoomList.size(), openWorldRoomList));
    }

    @PostMapping("/world_room/new")
    public ResponseEntity<WorldCreateRequestDto> CreateWorld(@RequestBody WorldCreateRequestDto worldCreateRequestDto){
        return ResponseEntity.ok(worldRoomService.createWorld(worldCreateRequestDto));
    }
}
