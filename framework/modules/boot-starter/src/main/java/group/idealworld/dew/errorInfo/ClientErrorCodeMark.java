package group.idealworld.dew.errorInfo;

import lombok.Getter;

/**
 * 客户端异常标记接口。
 * 用于标记{@link ClientException}对象的错误码参数类型,防止错误码串用.
 * @author nipeixuan
 */
public interface ClientErrorCodeMark extends ErrorInfo {

    @Getter
    enum Code implements ClientErrorCodeMark {

        PARAMETER_ERROR("errorCode 1", "parameter error"),

        FREQUENTLY("errorCode 2", "the operation is too frequent. Please try again later"),
        ;

        Code(String errorCode) {
            this.errorCode = errorCode;
        }
        Code(String errorCode, String errorDesc) {
            this.errorCode = errorCode;
            this.errorDesc = errorDesc;
        }

        /**
         * 错误码.
         */
        private String errorCode;
        /**
         * 错误码对应的外部描述信息,该信息是通过错误码自动获取,并且直接返回给调用方.
         */
        private String errorDesc;
    }

    /**
     * 快速构建一个自定义错误描述错误信息对象.
     */
    default ClientErrorCodeMark as(String desc) {
        ErrorInfo errorInfo = this;
        return new ClientErrorCodeMark() {
            @Override
            public String getErrorCode() {
                return errorInfo.getErrorCode();
            }

            @Override
            public String getErrorDesc() {
                return desc;
            }
        };
    }

}