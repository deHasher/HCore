package ru.dehasher.bukkit.managers;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatFilter {

    private static final HashMap<UUID, MessageLog> spam = new HashMap<>();

    public static boolean isSpam(Player player, String input) {
        MessageLog messageLog = spam.get(player.getUniqueId());
        if (messageLog == null) {
            messageLog = new MessageLog();
            messageLog.addMessage(input);
            spam.put(player.getUniqueId(), messageLog);
            return false;
        }

        byte b = 0;
        for (Map.Entry<Long, String> entry : messageLog.getMessages().entrySet()) {
            double d = similarity(input, entry.getValue());
            if (d > 0.7D) {
                b++;
            }
        }

        if (b >= 2) return true;
        messageLog.addMessage(input);

        return false;
    }

    public static double similarity(String input1, String input2) {
        String str1 = input1, str2 = input2;
        if (input1.length() < input2.length()) {
            str1 = input2;
            str2 = input1;
        }
        int i = str1.length();
        if (i == 0) return 1.0D;
        return (i - editDistance(str1, str2)) / (double)i;
    }

    public static int editDistance(String input1, String input2) {
        input1 = input1.toLowerCase();
        input2 = input2.toLowerCase();
        final int[] array = new int[input2.length() + 1];
        for (int i = 0; i <= input1.length(); ++i) {
            int b = i;
            for (int j = 0; j <= input2.length(); ++j) {
                if (i == 0) {
                    array[j] = j;
                }
                else if (j > 0) {
                    int a = array[j - 1];
                    if (input1.charAt(i - 1) != input2.charAt(j - 1)) {
                        a = Math.min(Math.min(a, b), array[j]) + 1;
                    }
                    array[j - 1] = b;
                    b = a;
                }
            }
            if (i > 0) {
                array[input2.length()] = b;
            }
        }
        return array[input2.length()];
    }
    public static class MessageLog {
        private final HashMap<Long, String> messages = new HashMap<>();

        public void addMessage(String input) {
            this.messages.put(System.currentTimeMillis(), input);
        }

        private void removeOld() {
            ArrayList<Long> list = new ArrayList<>();

            for (Map.Entry<Long, String> entry : messages.entrySet()) {
                if (entry.getKey() + (5 * 5000) < System.currentTimeMillis()) {
                    list.add(entry.getKey());
                }
            }
            for (Long long_ : list) {
                messages.remove(long_);
            }
        }

        public HashMap<Long, String> getMessages() {
            removeOld();
            return messages;
        }
    }
}