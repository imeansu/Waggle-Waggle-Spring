package soma.test.waggle.error.exception;

import soma.test.waggle.error.ErrorCode;

public class WorldRoomNotFoundException extends WaggleWaggleException{

    public WorldRoomNotFoundException(ErrorCode errorCode){
        super(errorCode);
    }
}
