package com.ym.event;
import org.junit.Before;
import org.junit.Test;

public class Client {

    private Event currentEvent;

    private Button button;


    @Before
    public void initComponent() {

        button = new Button();

        button.addEventListener(new ClickEventHandler() {
            @Override
            public void handleEvent(ClickEvent event) {
                System.out.println("Button was clicked!");
            }
        });

        button.addEventListener(new DblClickEventHandler() {
            @Override
            public void handleEvent(DblClickEvent event) {
                System.out.println("Button was double clicked!");
            }
        });

    }

    @Test
    public void testCommonEvents() {
        currentEvent = new ClickEvent();
        button.notifyListeners(currentEvent);

        currentEvent = new DblClickEvent();
        button.notifyListeners(currentEvent);
    }

}
