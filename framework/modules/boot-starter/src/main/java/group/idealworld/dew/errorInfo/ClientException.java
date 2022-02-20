package group.idealworld.dew.errorInfo;

import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author nipeixuan
 */
public class ClientException extends AbstractException {

    private ErrorInfo errorCode;
    /**
     * 本地错误描述信息,其优先级高于{@link ErrorInfo}中的errorDesc,即如果该值不为空,则以该值为准
     */
    @Setter
    private String errorDesc;

    public ClientException(ClientErrorCodeMark errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public ClientException(ClientErrorCodeMark errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ClientException(ClientErrorCodeMark errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public ClientException(ClientErrorCodeMark errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    @Override
    public String getErrorCode() {
        return errorCode.getErrorCode();
    }

    @Override
    public String getErrorDesc() {
        if (!StringUtils.isEmpty(errorDesc)) {
            return errorDesc;
        }
        return errorCode.getErrorDesc();
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (StringUtils.isEmpty(message)) {
            message = this.getErrorDesc();
        }
        return message;
    }
}
