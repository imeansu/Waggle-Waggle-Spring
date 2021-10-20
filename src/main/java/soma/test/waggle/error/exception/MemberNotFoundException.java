package soma.test.waggle.error.exception;

import soma.test.waggle.error.ErrorCode;

public class MemberNotFoundException extends WaggleWaggleException{

    public MemberNotFoundException(String message){

        super(message, ErrorCode.MEMBER_NOT_FOUND);
    }
}
