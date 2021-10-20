package soma.test.waggle.error.exception;

import soma.test.waggle.error.ErrorCode;

public class WaggleWaggleException extends RuntimeException {

    private ErrorCode errorCode;

    public WaggleWaggleException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public WaggleWaggleException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode(){
        return errorCode;
    }
}
