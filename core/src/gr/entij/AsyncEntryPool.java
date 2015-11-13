package gr.entij;

import java.util.concurrent.*;
import java.util.function.Function;


class AsyncEntryPool {
    
    public class AsyncEntry {
        private AsyncEntry next;
        // *Single* Thread Executor
        private final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1,
                                        0L, TimeUnit.MILLISECONDS,
                                        new LinkedBlockingQueue<>());

        public synchronized <T> Future<T> sumbitCode(Entity target, Function<? super Entity, T> code)
                throws IllegalStateException {
            if (isShutdown)
                throw new IllegalStateException("AsyncEntryPool has been shutdown and cannot accept new submissions");
            return executor.submit(() -> {
                try {
                    return code.apply(target);
                } finally {
                    // synchronized again because we are in another thread
                    synchronized (AsyncEntry.this) {
                        if (executor.getQueue().isEmpty()) {
                            target.signalAsyncQueueEmpty(); // assume that it does not throw an exception
                            if (isShutdownLater) {
                                executor.shutdown();
                            }
                        }
                        if (isShutdown) {
                            executor.shutdown();
                        }
                    }
                }
            });
        }

        public synchronized Future<Reaction> submitAction(Entity target, Object input)
                throws IllegalStateException {
            return sumbitCode(target, (t) -> {
                return t.react(input);
            });
        }
    }

    private AsyncEntry head;
    private boolean isShutdown;
    private boolean isShutdownLater;

    public synchronized AsyncEntry get() {
        if (head == null) {
            return new AsyncEntry();
        } else {
            AsyncEntry result = head;
            head = head.next;
            return result;
        }
    }

    public synchronized void putBack(AsyncEntry entry) {
        if (!entry.executor.isShutdown()) {
            entry.next = head;
            head = entry;
        }
    }

    public synchronized void shutdownNow() {
        isShutdown = true;
        shutdownLater();
    }
    
    public synchronized void shutdownLater() {
        isShutdownLater = true;
        for (AsyncEntry entry = head; entry != null; entry = entry.next) {
            entry.executor.shutdown();
        }
        head = null;
    }
}
