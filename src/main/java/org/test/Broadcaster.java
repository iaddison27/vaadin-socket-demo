package org.test;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Broadcaster singleton that registers UIs and broadcasts messages to them safely
 */
public class Broadcaster {

	// Easiest and safest way to send message
	private static ExecutorService executorService = Executors.newSingleThreadExecutor();

	// List of BroadcastListeners
    private static LinkedList<BroadcastListener> listeners = new LinkedList<>();

    /**
     * Register the BroadcastListener
     * @param listener BroadcastListener to register
     */
    public static synchronized void register(BroadcastListener listener) {
        listeners.add(listener);
    }

    /**
     * Unregister the BroadcastListener
     * @param listener BroadcastListener to unregister
     */
    public static synchronized void unregister(BroadcastListener listener) {
        listeners.remove(listener);
    }

    /**
     * Broadcasts the given message to all listeners
     * @param message message to broadcast
     */
    public static synchronized void broadcast(final String message) {
        for (final BroadcastListener listener: listeners) {
            executorService.execute(() -> {
                listener.receiveBroadcast(message);
            });
        }
    }
}
