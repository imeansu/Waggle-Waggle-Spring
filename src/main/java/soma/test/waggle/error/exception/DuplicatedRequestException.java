package soma.test.waggle.error.exception;

import soma.test.waggle.error.ErrorCode;

public class DuplicatedRequestException extends WaggleWaggleException{

    public DuplicatedRequestException(ErrorCode errorCode){
        super(errorCode);
    }
}
