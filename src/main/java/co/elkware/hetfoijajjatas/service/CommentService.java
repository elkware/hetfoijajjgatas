package co.elkware.hetfoijajjatas.service;

import co.elkware.hetfoijajjatas.db.generated.tables.Comment;
import co.elkware.hetfoijajjatas.db.generated.tables.records.CommentRecord;
import co.elkware.hetfoijajjatas.util.Utils;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private UserService userService;

    public List<CommentRecord> listByWailId(Integer wailId, int offset) {
        return dslContext
                .select(Comment.COMMENT.ID, Comment.COMMENT.CONTENT, Comment.COMMENT.USER_ID, Comment.COMMENT.WAIL_ID, Comment.COMMENT.CREATED_AT)
                .from(Comment.COMMENT)
                .where(Comment.COMMENT.WAIL_ID.equal(wailId))
                .orderBy(Comment.COMMENT.CREATED_AT.desc())
                .limit(Utils.PAGE_SIZE)
                .offset(offset)
                .stream()
                .map(r -> new CommentRecord(r.value1(), r.value2(), r.value3(), r.value4(), r.value5()))
                .collect(Collectors.toList());
    }

    public void add(Integer wailId, String user, String content) {
        userService.addIfNotExists(user);
        dslContext.insertInto(Comment.COMMENT, Comment.COMMENT.CONTENT, Comment.COMMENT.WAIL_ID, Comment.COMMENT.USER_ID, Comment.COMMENT.CREATED_AT)
                .values(content, wailId, user, new Timestamp(new Date().getTime())).execute();
    }

    public Optional<CommentRecord> findOne(Integer id) {
        return dslContext.select(Comment.COMMENT.ID, Comment.COMMENT.CONTENT, Comment.COMMENT.USER_ID, Comment.COMMENT.WAIL_ID, Comment.COMMENT.CREATED_AT)
                .from(Comment.COMMENT).where(Comment.COMMENT.ID.equal(id))
                .stream()
                .map(r -> new CommentRecord(r.value1(), r.value2(), r.value3(), r.value4(), r.value5()))
                .findFirst();

    }

    public int countByWailId(Integer wailId) {
        return dslContext.select(Comment.COMMENT.ID).from(Comment.COMMENT).where(Comment.COMMENT.WAIL_ID.equal(wailId)).execute();
    }

}
