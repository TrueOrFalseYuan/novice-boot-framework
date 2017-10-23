package cn.kinkii.noviceboot.framework.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QPkUUID is a Querydsl query type for PkUUID
 */
@Generated("com.querydsl.codegen.SupertypeSerializer")
public class QPkUUID extends EntityPathBase<PkUUID> {

    private static final long serialVersionUID = 501725051L;

    public static final QPkUUID pkUUID = new QPkUUID("pkUUID");

    public final StringPath id = createString("id");

    public QPkUUID(String variable) {
        super(PkUUID.class, forVariable(variable));
    }

    public QPkUUID(Path<? extends PkUUID> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPkUUID(PathMetadata metadata) {
        super(PkUUID.class, metadata);
    }

}

