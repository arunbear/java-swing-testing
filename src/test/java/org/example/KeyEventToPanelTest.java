package org.example;

import org.junit.jupiter.api.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class KeyEventToPanelTest {

    private JFrame frame;
    private JPanel panel;
    private CountDownLatch listenerLatch;
    private CountDownLatch dispatcherLatch;
    private KeyEventDispatcher dispatcher;

    @BeforeAll
    void setupFrame() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            frame = new JFrame("Key Event Test Frame");
            frame.setSize(300, 200);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            panel = new JPanel();
            panel.setFocusable(true);
            panel.requestFocusInWindow(); // Request focus
            frame.add(panel);

            frame.setVisible(true);
            frame.toFront(); // Bring to front just in case
        });

        SwingUtilities.invokeAndWait(() -> {}); // Wait for GUI setup
    }

    @BeforeEach
    void setupLatchesAndDispatcher() throws Exception {
        listenerLatch = new CountDownLatch(1);
        dispatcherLatch = new CountDownLatch(1);

        SwingUtilities.invokeAndWait(() -> {
            panel.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_B) {
                        listenerLatch.countDown();
                    }
                }
            });

            dispatcher = e -> {
                if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_B) {
                    dispatcherLatch.countDown();
                }
                return false;
            };

            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(dispatcher);
            panel.requestFocusInWindow();
        });
    }

    @AfterEach
    void removeDispatcher() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(dispatcher);
    }

    @AfterAll
    void tearDownFrame() throws Exception {
        SwingUtilities.invokeAndWait(() -> frame.dispose());
    }

    @Test
    void simulateKeyPressed_B_DispatcherAndListenerReceiveIt() throws Exception {
        // Delay to allow focus to stabilize
        Thread.sleep(50);

        SwingUtilities.invokeAndWait(() -> {
            KeyEvent keyEvent = new KeyEvent(
                    panel,
                    KeyEvent.KEY_PRESSED,
                    System.currentTimeMillis(),
                    0,
                    KeyEvent.VK_B,
                    'B'
            );
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(keyEvent);
        });

        assertTrue(dispatcherLatch.await(1, TimeUnit.SECONDS), "Dispatcher did not receive the key event");
        assertTrue(listenerLatch.await(1, TimeUnit.SECONDS), "KeyListener did not receive the key event");
    }
}
