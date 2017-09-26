package com.ym.event;

import java.util.LinkedList;
import java.util.List;


public class Button implements EventSource {

    protected List<EventListener<? extends Event>> listeners = new LinkedList<EventListener<? extends Event>>();

    @Override
    public void addEventListener(EventListener<? extends Event> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeEventListener(EventListener<? extends Event> listener) {
        listeners.remove(listener);
    }

    @Override
    public void notifyListeners(Event event) {
        for (EventListener listener : listeners) {
            try {
                listener.handleEvent(event);
            } catch (ClassCastException e) {
            }
        }
    }

}