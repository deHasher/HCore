package ru.dehasher.hcore.libraries;

import java.util.Arrays;

public class Characters {

    private final static String[] a = {"Ａ", "ａ", "ᴀ", "⒜", "Ⓐ", "ⓐ"};
    private final static String[] b = {"Ｂ", "ｂ", "ʙ", "⒝", "Ⓑ", "ⓑ"};
    private final static String[] c = {"Ｃ", "ｃ", "ᴄ", "⒞", "Ⓒ", "ⓒ"};
    private final static String[] d = {"Ｄ", "ｄ", "ᴅ", "⒟", "Ⓓ", "ⓓ"};
    private final static String[] e = {"Ｅ", "ｅ", "ᴇ", "⒠", "Ⓔ", "ⓔ"};
    private final static String[] f = {"Ｆ", "ｆ", "ғ", "⒡", "Ⓕ", "ⓕ"};
    private final static String[] g = {"Ｇ", "ｇ", "ɢ", "⒢", "Ⓖ", "ⓖ"};
    private final static String[] h = {"Ｈ", "ｈ", "ʜ", "⒣", "Ⓗ", "ⓗ"};
    private final static String[] i = {"Ｉ", "ｉ", "ɪ", "⒤", "Ⓘ", "ⓘ"};
    private final static String[] j = {"Ｊ", "ｊ", "ᴊ", "⒥", "Ⓙ", "ⓙ"};
    private final static String[] k = {"Ｋ", "ｋ", "ᴋ", "⒦", "Ⓚ", "ⓚ"};
    private final static String[] l = {"Ｌ", "ｌ", "ʟ", "⒧", "Ⓛ", "ⓛ"};
    private final static String[] m = {"Ｍ", "ｍ", "ᴍ", "⒨", "Ⓜ", "ⓜ"};
    private final static String[] n = {"Ｎ", "ｎ", "ɴ", "⒩", "Ⓝ", "ⓝ"};
    private final static String[] o = {"Ｏ", "ｏ", "ᴏ", "⒪", "Ⓞ", "ⓞ"};
    private final static String[] p = {"Ｐ", "ｐ", "ᴘ", "⒫", "Ⓟ", "ⓟ"};
    private final static String[] q = {"Ｑ", "ｑ", "ǫ", "⒬", "Ⓠ", "ⓠ"};
    private final static String[] r = {"Ｒ", "ｒ", "ʀ", "⒭", "Ⓡ", "ⓡ"};
    private final static String[] s = {"Ｓ", "ｓ", "s", "⒮", "Ⓢ", "ⓢ"};
    private final static String[] t = {"Ｔ", "ｔ", "ᴛ", "⒯", "Ⓣ", "ⓣ"};
    private final static String[] u = {"Ｕ", "ｕ", "ᴜ", "⒰", "Ⓤ", "ⓤ"};
    private final static String[] v = {"Ｖ", "ｖ", "ᴠ", "⒱", "Ⓥ", "ⓥ"};
    private final static String[] w = {"Ｗ", "ｗ", "ᴡ", "⒲", "Ⓦ", "ⓦ"};
    private final static String[] x = {"Ｘ", "ｘ", "x", "⒳", "Ⓧ", "ⓧ"};
    private final static String[] y = {"Ｙ", "ｙ", "ʏ", "⒴", "Ⓨ", "ⓨ"};
    private final static String[] z = {"Ｚ", "ｚ", "ᴢ", "⒵", "Ⓩ", "ⓩ"};

    private final static String[] dots = {"∙", "•", ",", "◦"};

    public static String convert(String string) {
        StringBuilder sb = new StringBuilder();

        char[] chars = string.toCharArray();
        for (char symbol : chars) {
            if (Arrays.asList(a).contains(String.valueOf(symbol))) {
                sb.append("a");
                continue;
            }
            if (Arrays.asList(b).contains(String.valueOf(symbol))) {
                sb.append("b");
                continue;
            }
            if (Arrays.asList(c).contains(String.valueOf(symbol))) {
                sb.append("c");
                continue;
            }
            if (Arrays.asList(d).contains(String.valueOf(symbol))) {
                sb.append("d");
                continue;
            }
            if (Arrays.asList(e).contains(String.valueOf(symbol))) {
                sb.append("e");
                continue;
            }
            if (Arrays.asList(f).contains(String.valueOf(symbol))) {
                sb.append("f");
                continue;
            }
            if (Arrays.asList(g).contains(String.valueOf(symbol))) {
                sb.append("g");
                continue;
            }
            if (Arrays.asList(h).contains(String.valueOf(symbol))) {
                sb.append("h");
                continue;
            }
            if (Arrays.asList(i).contains(String.valueOf(symbol))) {
                sb.append("i");
                continue;
            }
            if (Arrays.asList(j).contains(String.valueOf(symbol))) {
                sb.append("j");
                continue;
            }
            if (Arrays.asList(k).contains(String.valueOf(symbol))) {
                sb.append("k");
                continue;
            }
            if (Arrays.asList(l).contains(String.valueOf(symbol))) {
                sb.append("l");
                continue;
            }
            if (Arrays.asList(m).contains(String.valueOf(symbol))) {
                sb.append("m");
                continue;
            }
            if (Arrays.asList(n).contains(String.valueOf(symbol))) {
                sb.append("n");
                continue;
            }
            if (Arrays.asList(o).contains(String.valueOf(symbol))) {
                sb.append("o");
                continue;
            }
            if (Arrays.asList(p).contains(String.valueOf(symbol))) {
                sb.append("p");
                continue;
            }
            if (Arrays.asList(q).contains(String.valueOf(symbol))) {
                sb.append("q");
                continue;
            }
            if (Arrays.asList(r).contains(String.valueOf(symbol))) {
                sb.append("r");
                continue;
            }
            if (Arrays.asList(s).contains(String.valueOf(symbol))) {
                sb.append("s");
                continue;
            }
            if (Arrays.asList(t).contains(String.valueOf(symbol))) {
                sb.append("t");
                continue;
            }
            if (Arrays.asList(u).contains(String.valueOf(symbol))) {
                sb.append("u");
                continue;
            }
            if (Arrays.asList(v).contains(String.valueOf(symbol))) {
                sb.append("v");
                continue;
            }
            if (Arrays.asList(w).contains(String.valueOf(symbol))) {
                sb.append("w");
                continue;
            }
            if (Arrays.asList(x).contains(String.valueOf(symbol))) {
                sb.append("x");
                continue;
            }
            if (Arrays.asList(y).contains(String.valueOf(symbol))) {
                sb.append("y");
                continue;
            }
            if (Arrays.asList(z).contains(String.valueOf(symbol))) {
                sb.append("z");
                continue;
            }
            if (Arrays.asList(dots).contains(String.valueOf(symbol))) {
                sb.append(".");
                continue;
            }
            sb.append(symbol);
        }
        String new_string = sb.toString();

        return (string.equals(new_string)) ? string : new_string;
    }
}
