package cn.kinkii.novice.framework.data;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SensitiveDataUtils {

    public static String sensitize(String value, List<Pattern> matchPatternList, String maskChar) {
        String maskedValue = value;
        if (matchPatternList != null && matchPatternList.size() > 0) {
            boolean isMatched = false;
            for (Pattern p : matchPatternList) {
                Matcher m = p.matcher(value);
                while (m.find()) {
                    isMatched = true;
                    for (int i = 1; i <= m.groupCount(); i++) {
                        maskedValue = maskedValue.substring(0, m.start(i)) + String.join("", Collections.nCopies(m.end(i) - m.start(i), maskChar)) + maskedValue.substring(m.end(i));
                    }
                }
            }
            if (isMatched) {
                return maskedValue;
            }
        }
        return String.join("", Collections.nCopies(value.length(), maskChar));
    }

}
