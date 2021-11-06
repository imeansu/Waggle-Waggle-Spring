package soma.test.waggle.error.exception;

import soma.test.waggle.error.ErrorCode;

public class PeopleOverLimitException extends WaggleWaggleException{

    public PeopleOverLimitException(ErrorCode errorCode) {
        super(errorCode);
    }
}
