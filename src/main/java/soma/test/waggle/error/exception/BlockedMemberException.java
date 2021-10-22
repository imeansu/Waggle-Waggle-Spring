package soma.test.waggle.error.exception;

import soma.test.waggle.error.ErrorCode;

public class BlockedMemberException extends WaggleWaggleException{

    public BlockedMemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
