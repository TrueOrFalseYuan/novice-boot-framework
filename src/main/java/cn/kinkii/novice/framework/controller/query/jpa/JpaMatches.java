package cn.kinkii.novice.framework.controller.query.jpa;

import cn.kinkii.novice.framework.controller.query.Match;
import org.hibernate.criterion.MatchMode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JpaMatches {

    private static final Map<Match, MatchMode> matchModeMap = new HashMap<>();

    static {
        Arrays.stream(Match.values()).forEach(match -> matchModeMap.put(match, MatchMode.valueOf(match.name())));
    }

    public static MatchMode by(Match match) {
        return matchModeMap.get(match);
    }
}
