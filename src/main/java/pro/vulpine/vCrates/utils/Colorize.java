package pro.vulpine.vCrates.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Colorize {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static String color(final String message) {
        return translateHexColorCodes(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static String[] color(final String[] message) {
        String[] colored = new String[message.length];
        for (int i = 0; i < message.length; i++) {
            colored[i] = color(colored[i]);
        }
        return colored;
    }

    public static List<String> color(final List<String> message) {
        message.replaceAll(Colorize::color);
        return message;
    }

    public static String translateHexColorCodes(final String message) {
        final char colorChar = ChatColor.COLOR_CHAR;

        final Matcher matcher = HEX_PATTERN.matcher(message);
        final StringBuilder buffer = new StringBuilder(message.length() + 4 * 8);

        while (matcher.find()) {
            final String group = matcher.group(1);

            matcher.appendReplacement(buffer, colorChar + "x"
                    + colorChar + group.charAt(0) + colorChar + group.charAt(1)
                    + colorChar + group.charAt(2) + colorChar + group.charAt(3)
                    + colorChar + group.charAt(4) + colorChar + group.charAt(5));
        }

        return matcher.appendTail(buffer).toString();
    }

}