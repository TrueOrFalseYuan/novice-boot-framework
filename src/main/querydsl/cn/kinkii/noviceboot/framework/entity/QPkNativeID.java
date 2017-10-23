package cn.kinkii.noviceboot.framework.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QPkNativeID is a Querydsl query type for PkNativeID
 */
@Generated("com.querydsl.codegen.SupertypeSerializer")
public class QPkNativeID extends EntityPathBase<PkNativeID> {

    private static final long serialVersionUID = -616759470L;

    public static final QPkNativeID pkNativeID = new QPkNativeID("pkNativeID");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QPkNativeID(String variable) {
        super(PkNativeID.class, forVariable(variable));
    }

    public QPkNativeID(Path<? extends PkNativeID> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPkNativeID(PathMetadata metadata) {
        super(PkNativeID.class, metadata);
    }

}

