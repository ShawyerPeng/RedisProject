package ranking.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ranking.dao.UserDao;
import ranking.service.UserService;

import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private static String RANK_LIST_NAME = "rankList";

    @Autowired
    private UserDao userDao;

    @Override
    public void addCore(int id, int score) {
        userDao.addScore(RANK_LIST_NAME, id, score);
    }

    @Override
    public Set getTop(int top) {
        return userDao.getTop(RANK_LIST_NAME, top);
    }

    @Override
    public Set getTopWithScore(int top) {
        return userDao.getTopWithScore(RANK_LIST_NAME, top);
    }

    @Override
    public Set getTopWithScore(int start, int limit) {
        return userDao.getTopWithScore(RANK_LIST_NAME, start, limit);
    }
}
