package cn.kinkii.noviceboot.framework.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QRtUUID is a Querydsl query type for RtUUID
 */
@Generated("com.querydsl.codegen.SupertypeSerializer")
public class QRtUUID extends EntityPathBase<RtUUID> {

    private static final long serialVersionUID = 567295042L;

    public static final QRtUUID rtUUID = new QRtUUID("rtUUID");

    public QRtUUID(String variable) {
        super(RtUUID.class, forVariable(variable));
    }

    public QRtUUID(Path<? extends RtUUID> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRtUUID(PathMetadata metadata) {
        super(RtUUID.class, metadata);
    }

}

