package co.elkware.hetfoijajjatas.service;

import co.elkware.hetfoijajjatas.db.generated.tables.User;
import co.elkware.hetfoijajjatas.util.Utils;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;

@Service
public class UserService {


    public void addIfNotExists(String username) {
        try (Connection connection = Utils.getConnection()) {
            DSLContext dslContext = DSL.using(connection, SQLDialect.POSTGRES);
            if (dslContext
                    .select(User.USER.USERNAME)
                    .from(User.USER)
                    .where(User.USER.USERNAME.equal(username))
                    .execute() == 0) {
                dslContext.insertInto(User.USER, User.USER.USERNAME, User.USER.CREATED_AT).values(username, new Timestamp(new Date().getTime())).execute();
            }
            dslContext.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
