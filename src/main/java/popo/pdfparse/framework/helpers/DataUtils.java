package popo.pdfparse.framework.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataUtils {

    public static List<String> getMatchesDataList(String regEx, String text) {
        List<String> data = new ArrayList<>();
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            data.add(matcher.group());
        }
        return data;
    }

    public static double parseDouble(String amount) {
        return Double.parseDouble(amount.replace(",", ".").trim());
    }
}
