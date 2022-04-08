package group.idealworld.dew.errorInfo;

import lombok.Getter;

public interface ModuleErrorCodeMark extends ErrorInfo{

    @Getter
    enum Code implements ModuleErrorCodeMark {

        HAZELCAST_ERROR("errorCode8", "hazelcast error"),

        MQTT_ERROR("errorCode9", "mqtt error"),

        RABBIT_ERROR("errorCode10", "rabbit error"),

        RADIS_ERROR("errorCode11", "redis error"),

        ROCKET_ERROR("errorCode12", "rocket error"),

        DBUTILS_ERROR("errorCode13", "dbutils error"),

        HBASE_ERROR("errorCode13", "hbase error"),

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
    default ModuleErrorCodeMark as(String desc) {
        ErrorInfo errorInfo = this;
        return new ModuleErrorCodeMark() {
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
