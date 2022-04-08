package group.idealworld.dew.errorInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 标记接口.
 * 用于标记{@link SystemException}对象的错误码参数类型,防止错误码串用.
 * @author nipeixuan
 */
public interface SystemErrorCodeMark extends ErrorInfo {

    /**
     * 我方系统异常错误码枚举
     */
    @Getter
    @AllArgsConstructor
    enum Code implements SystemErrorCodeMark {

        /**
         * 系统错误(本系统).
         */
        SYSTEM_ERROR_LOCAL("error Code3", "system error"),
        /**
         * 系统内部错误(其他服务引起的错误,如通道异常)
         */
        SYSTEM_ERROR_OTHER("error Code4", "system busy"),

        // -----------------------------------------------------------------------------------------------------------------

        /**
         * 系统内部错误
         */
        SYS_0001("error code5"),
        /**
         * API不存在
         */
        SYSTEM_NOT_API("error code6" , "API not exists"),
        /**
         * API不存在
         */
        HTTP_NOT_SUPPORTED("error code7" , "the request method is not supported"),
        ;

        Code(String errorCode) {
            this.errorCode = errorCode;
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
    default SystemErrorCodeMark as(String desc) {
        ErrorInfo errorInfo = this;
        return new SystemErrorCodeMark() {
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
