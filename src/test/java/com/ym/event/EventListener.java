package com.ym.event;


public interface EventListener<T extends Event> {

    public void handleEvent(T event);

}