package com.ym.event;

public interface EventSource {

    void addEventListener(EventListener<? extends Event> listener);

    void removeEventListener(EventListener<? extends Event> listener);

    void notifyListeners(Event event);

}