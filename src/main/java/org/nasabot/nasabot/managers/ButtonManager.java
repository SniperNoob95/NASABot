package org.nasabot.nasabot.managers;

import org.nasabot.nasabot.objects.NASAImage;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ButtonManager {
    // <ButtonID, Pair<MessageID, NASAImage>>
    private final ConcurrentHashMap<String, NASAImage> buttonImageMap = new ConcurrentHashMap<>();
    // <MessageID, List<ButtonID>>
    private final ConcurrentHashMap<String, List<String>> messageButtonsMap = new ConcurrentHashMap<>();

    private ButtonManager() {
    }

    private static class ButtonSingleton {
        private static final ButtonManager INSTANCE = new ButtonManager();
    }

    public static ButtonManager getInstance() {
        return ButtonSingleton.INSTANCE;
    }

    public NASAImage getImageForButton(String buttonId) {
        return buttonImageMap.get(buttonId);
    }

    public void addButtonToMap(String buttonId, NASAImage nasaImage) {
        buttonImageMap.put(buttonId, nasaImage);
    }

    private void removeButtonFromMap(String buttonId) {
        buttonImageMap.remove(buttonId);
    }

    public List<String> getButtonsForMessage(String messageId) {
        return messageButtonsMap.get(messageId);
    }

    public void addButtonsToMap(String messageId, List<String> buttonIds) {
        messageButtonsMap.put(messageId, buttonIds);
    }

    public void removeButtonsFromMap(String messageId) {
        List<String> buttonIds = messageButtonsMap.get(messageId);
        for (String buttonId : buttonIds) {
            removeButtonFromMap(buttonId);
        }
        messageButtonsMap.remove(messageId);
    }
}
