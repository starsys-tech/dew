package group.idealworld.dew.core.hi.api.cluster;


import group.idealworld.dew.core.cluster.ClusterMQ;
import group.idealworld.dew.core.hi.process.Hi;

import java.util.UUID;

/**
 * 集群操作集合.
 *
 * @author gudaoxuri
 */
public class HiClusterAPI {

    /**
     * The constant instanceId.
     */
    protected static String instanceId = UUID.randomUUID().toString().replace("-", "");

    /**
     * MQ服务.
     */
    @Hi
    public ClusterMQ mq;


}
