package com.hp.thermalshockreforged.client.dialogue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class DialogueText {
    private static final int MARGIN = 9;
    private static final int MIN_WIDTH = 16;
    private static final int CACHE_SIZE = 64;
    private static final String CLOSING = "，。！？、；：）」』】》〉”’…—,.!?;:)]}";
    private static final Map<String, List<String>> CACHE = new LinkedHashMap<>(CACHE_SIZE, .75F, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<String, List<String>> eldest) { return size() > CACHE_SIZE; }
    };

    private DialogueText() { }

    public static void draw(GuiGraphics graphics, Font font, Component text, int x, int y, int color, int imageWidth, int spacing) {
        int step = spacing;
        if (step <= 0) step = font.lineHeight + 2;
        int width = Math.max(MIN_WIDTH, imageWidth - x - MARGIN);
        int lineY = y;
        for (var line : lines(font, text.getString(), width)) {
            graphics.drawString(font, line, x, lineY, color, false);
            lineY += step;
        }
    }

    private static List<String> lines(Font font, String text, int width) {
        String key = width + "\u0000" + text;
        var cached = CACHE.get(key);
        if (cached != null) return cached;
        var result = new ArrayList<String>();
        for (String paragraph : text.split("\n", -1)) wrap(font, paragraph, width, result);
        CACHE.put(key, result);
        return result;
    }

    private static void wrap(Font font, String paragraph, int width, List<String> result) {
        String prefix = "";
        int start = 0;
        int lastBreak = -1;
        int index = 0;
        if (paragraph.isEmpty()) {
            result.add("");
            return;
        }
        while (index < paragraph.length()) {
            char current = paragraph.charAt(index);
            if (current == '§' && index + 1 < paragraph.length()) {
                index += 2;
                continue;
            }
            if (index > start && CLOSING.indexOf(current) < 0 && (cjk(current) || cjk(previous(paragraph, index)))) lastBreak = index;
            if (index > start && font.width(prefix + paragraph.substring(start, index + 1)) > width) {
                int cut = index;
                if (lastBreak > start) cut = lastBreak;
                String line = stripTrailing(paragraph.substring(start, cut));
                result.add(prefix + line);
                prefix = formatting(prefix + line);
                start = cut;
                while (start < paragraph.length() && paragraph.charAt(start) == ' ') start++;
                lastBreak = -1;
                index = start;
                continue;
            }
            if (current == ' ') lastBreak = index + 1;
            index++;
        }
        if (start < paragraph.length()) result.add(prefix + paragraph.substring(start));
    }

    private static char previous(String text, int index) {
        int at = index - 1;
        while (at >= 1 && text.charAt(at - 1) == '§') at -= 2;
        if (at < 0) return ' ';
        return text.charAt(at);
    }

    private static boolean cjk(char value) {
        var script = Character.UnicodeScript.of(value);
        if (script == Character.UnicodeScript.HAN || script == Character.UnicodeScript.HIRAGANA || script == Character.UnicodeScript.KATAKANA || script == Character.UnicodeScript.HANGUL) return true;
        var block = Character.UnicodeBlock.of(value);
        return block == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || block == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    private static String stripTrailing(String text) {
        int end = text.length();
        while (end > 0 && text.charAt(end - 1) == ' ') end--;
        return text.substring(0, end);
    }

    private static String formatting(String text) {
        var codes = new StringBuilder();
        for (int index = 0; index + 1 < text.length(); index++) {
            if (text.charAt(index) != '§') continue;
            char code = Character.toLowerCase(text.charAt(index + 1));
            if (code == 'r') codes.setLength(0);
            else if ((code >= '0' && code <= '9') || (code >= 'a' && code <= 'f')) {
                codes.setLength(0);
                codes.append('§').append(code);
            } else if (code >= 'k' && code <= 'o') codes.append('§').append(code);
            index++;
        }
        return codes.toString();
    }
}
