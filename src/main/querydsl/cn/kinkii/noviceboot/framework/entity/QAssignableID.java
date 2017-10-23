package cn.kinkii.noviceboot.framework.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QAssignableID is a Querydsl query type for AssignableID
 */
@Generated("com.querydsl.codegen.SupertypeSerializer")
public class QAssignableID extends EntityPathBase<AssignableID<? extends java.io.Serializable>> {

    private static final long serialVersionUID = -1030197975L;

    public static final QAssignableID assignableID = new QAssignableID("assignableID");

    public final SimplePath<java.io.Serializable> id = createSimple("id", java.io.Serializable.class);

    @SuppressWarnings({"all", "rawtypes", "unchecked"})
    public QAssignableID(String variable) {
        super((Class) AssignableID.class, forVariable(variable));
    }

    @SuppressWarnings({"all", "rawtypes", "unchecked"})
    public QAssignableID(Path<? extends AssignableID> path) {
        super((Class) path.getType(), path.getMetadata());
    }

    @SuppressWarnings({"all", "rawtypes", "unchecked"})
    public QAssignableID(PathMetadata metadata) {
        super((Class) AssignableID.class, metadata);
    }

}

