package co.elkware.hetfoijajjatas.service;

import co.elkware.hetfoijajjatas.db.generated.tables.User;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;

@Service
public class UserService {

    @Autowired
    private DSLContext dslContext;

    public void addIfNotExists(String username) {
        if(dslContext
                .select(User.USER.USERNAME)
                .from(User.USER)
                .where(User.USER.USERNAME.equal(username))
                .execute() == 0) {
            dslContext.insertInto(User.USER, User.USER.USERNAME, User.USER.CREATED_AT).values(username, new Timestamp(new Date().getTime())).execute();
        }
    }

}
