package group.idealworld.dew.core.hi;


import group.idealworld.dew.core.hi.api.cluster.HiClusterAPI;
import group.idealworld.dew.core.hi.process.HiBuilder;

/**
 * Hi标签入口.
 *
 * @author gudaoxuri
 */
public class Hi {

    /**
     * 初始化Hi标签.
     *
     * @param basicPackage Hi标签扫描的基础包
     */
    public static void init(String basicPackage) {
        HiBuilder.build(basicPackage);
    }

    /**
     * 集群操作集合.
     */
    @group.idealworld.dew.core.hi.process.Hi
    public static HiClusterAPI cluster;


}
