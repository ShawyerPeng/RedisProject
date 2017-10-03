package ranking.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ranking.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Set;

@Controller
@RequestMapping("/")
public class UserController {
    Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public String index(HttpServletRequest request, HttpServletResponse response) {
        return "index";
    }

    // http://localhost:8080/add/1/85
    @RequestMapping("/add/{id}/{score}")
    public void add(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") int id, @PathVariable("score") int score) {
        userService.addCore(id, score);
    }

    // http://localhost:8080/getTop/10
    /**
     * 获取排行榜前top名
     */
    @RequestMapping("/getTop/{top}")
    public String getTop(HttpServletRequest request, HttpServletResponse response, @PathVariable("top") int top) {
        Set set = userService.getTop(top);
        System.out.println(set);
        request.setAttribute("tops", set);
        return "top";
    }

    // http://localhost:8080/getTopScore/10
    /**
     * 获取排行榜前top名（包括分数）
     */
    @RequestMapping("/getTopScore/{top}")
    public String getTopScore(HttpServletRequest request, HttpServletResponse response, @PathVariable("top") int top) {
        Set set = userService.getTopWithScore(top);
        request.setAttribute("topScores", set);
        Iterator<DefaultTypedTuple> iterator = set.iterator();
        while (iterator.hasNext()) {
            DefaultTypedTuple defaultTypedTuple = iterator.next();
            System.out.println(defaultTypedTuple.getValue() + ":" + defaultTypedTuple.getScore());
        }
        return "topScore";
    }

    // http://localhost:8080/getTopScore/1/10
    @RequestMapping("/getTopScore/{start}/{limit}")
    public String getLimitTopScore(HttpServletRequest request, HttpServletResponse response, @PathVariable("start") int start, @PathVariable("limit") int limit) {
        Set set = userService.getTopWithScore(start, limit);
        request.setAttribute("topScores", set);
        Iterator<DefaultTypedTuple> iterator = set.iterator();
        return "topScore";
    }
}
