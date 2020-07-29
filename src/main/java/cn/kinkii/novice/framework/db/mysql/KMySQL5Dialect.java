package cn.kinkii.novice.framework.db.mysql;

import org.hibernate.dialect.MySQL5Dialect;

public class KMySQL5Dialect extends MySQL5Dialect {

    public KMySQL5Dialect() {
        for (KMySQLFunction s : KMySQLFunction.values()) {
            registerFunction(s.name(), s.getFunction());
        }
    }

}
