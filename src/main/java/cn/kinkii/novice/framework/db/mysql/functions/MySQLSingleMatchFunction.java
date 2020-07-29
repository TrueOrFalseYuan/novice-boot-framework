package cn.kinkii.novice.framework.db.mysql.functions;

import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

public class MySQLSingleMatchFunction extends SQLFunctionTemplate {

    public MySQLSingleMatchFunction() {
        super(StandardBasicTypes.DOUBLE, "MATCH(?1) AGAINST(?2 IN BOOLEAN MODE)");
    }

}
