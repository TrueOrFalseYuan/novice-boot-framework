package cn.kinkii.novice.framework.db.mysql;

import cn.kinkii.novice.framework.db.mysql.functions.MySQLSingleMatchFunction;
import lombok.Getter;
import org.hibernate.dialect.function.SQLFunction;

public enum KMySQLFunction {
    MATCH(new MySQLSingleMatchFunction());

    @Getter
    private final SQLFunction function;

    KMySQLFunction(SQLFunction function) {
        this.function = function;
    }

}
