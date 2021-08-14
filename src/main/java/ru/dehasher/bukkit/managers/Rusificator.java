package ru.dehasher.bukkit.managers;

import java.util.HashMap;

public class Rusificator {

    private static final HashMap<Character, Character> map = new HashMap<>();

    public static String replace(String input) {
        if (map.isEmpty()) {
            map.put('q', 'й'); map.put('w', 'ц');
            map.put('e', 'у'); map.put('r', 'к');
            map.put('t', 'е'); map.put('y', 'н');
            map.put('u', 'г'); map.put('i', 'ш');
            map.put('o', 'щ'); map.put('p', 'з');
            map.put('a', 'ф'); map.put('s', 'ы');
            map.put('d', 'в'); map.put('f', 'а');
            map.put('g', 'п'); map.put('h', 'р');
            map.put('j', 'о'); map.put('k', 'л');
            map.put('l', 'д'); map.put('z', 'я');
            map.put('x', 'ч'); map.put('c', 'с');
            map.put('v', 'м'); map.put('b', 'и');
            map.put('n', 'т'); map.put('m', 'ь');

            map.put('Q', 'Й'); map.put('W', 'Ц');
            map.put('E', 'У'); map.put('R', 'К');
            map.put('T', 'Е'); map.put('Y', 'Н');
            map.put('U', 'Г'); map.put('I', 'Ш');
            map.put('O', 'Щ'); map.put('P', 'З');
            map.put('A', 'Ф'); map.put('S', 'Ы');
            map.put('D', 'В'); map.put('F', 'А');
            map.put('G', 'П'); map.put('H', 'Р');
            map.put('J', 'О'); map.put('K', 'Л');
            map.put('L', 'Д'); map.put('Z', 'Я');
            map.put('X', 'Ч'); map.put('C', 'С');
            map.put('V', 'М'); map.put('B', 'И');
            map.put('N', 'Т'); map.put('M', 'Ь');
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            builder.append(map.get(input.charAt(i)));
        }
        return builder.toString();
    }
}
