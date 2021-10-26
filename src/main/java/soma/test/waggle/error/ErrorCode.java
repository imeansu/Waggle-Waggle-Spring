package soma.test.waggle.error;

import lombok.Getter;
import soma.test.waggle.error.exception.DuplicatedRequestException;

@Getter
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(400, "C001", " Invalid Input Value"),
    METHOD_NOT_ALLOWED(405, "C002", " Method Not Allowed"),
    ENTITY_NOT_FOUND(400, "C003", " Entity Not Found"),
    INTERNAL_SERVER_ERROR(500, "C004", "Server Error"),
    INVALID_TYPE_VALUE(400, "C005", " Invalid Type Value"),
    HANDLE_ACCESS_DENIED(403, "C006", "Access is Denied"),
    // Auth
    INVALID_FIREBASE_TOKEN(400, "F001", " Invalid Firebase Token"),
    // Member
    MEMBER_NOT_FOUND(404, "M001", "Member Not Found"),
        // following, blocking 중복 요청시
    DUPLICATED_REQUEST_EXCEPTION(400, "M002", "Request has done before"),
    BLOCKED_MEMBER(400, "M003", "Blocked Member"),
    // WorldRoom
    WORLDROOM_NOT_FOUND(404, "W001", "WorldRoom Not Found")

    ;

    private int status;
    private final String code;
    private final String message;

    ErrorCode(final int status, final String code, final String message){
        this.status = status;
        this.code = code;
        this.message = message;
    }


}
