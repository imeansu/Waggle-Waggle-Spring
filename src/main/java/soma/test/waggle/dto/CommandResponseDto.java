package soma.test.waggle.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 반환값이 필요없는 명령에 대한 응답
 *      status: ok || no
 * */
@Data
@AllArgsConstructor
public class CommandResponseDto {
//    private String result;
    private String status;

}
