package com.mingo.query.watch;


import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class QuerySetWatchService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final Set<Path> registered = Sets.newConcurrentHashSet();
    private final List<Watcher> watchers = Lists.newArrayList();
    private final WatchService watchService;
    private final WatchEventHandler watchEventHandler;
    private final EventBus eventBus;

    // global lock
    private Lock lock = new ReentrantLock();

    private static final Logger LOGGER = LoggerFactory.getLogger(QuerySetWatchService.class);

    public QuerySetWatchService(EventBus eventBus) {
        try {
            this.eventBus = eventBus;
            LOGGER.debug("create new watch service" );
            watchService = FileSystems.getDefault().newWatchService();
            watchEventHandler = new EventBusWatchEventHandler(eventBus, registered);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     * Register watcher for specified path.
     * If path references to a file then the parent folder of this file is registered.
     *
     * @param path the path to file or folder to watch
     */
    public void regiser(Path path) {
        Validate.notNull(path, "path to watch cannot be null" );
        try {
            lock.lock();
            if (!path.isAbsolute()) {
                path = path.toAbsolutePath();
            }
            Path dir = path;
            // check if specified path is referencing to file
            if (Files.isRegularFile(path)) {
                dir = path.getParent();//takes parent dir to register in watch service
            }
            if (needToRegister(path)) {
                LOGGER.debug("create watcher for dir: {}", dir);
                Watcher watcher = new Watcher(watchService, watchEventHandler, dir);
                executorService.execute(watcher);
                watchers.add(watcher);
            } else {
                LOGGER.debug("a watcher for dir: {} is already created", dir);
            }
            // add path to the registered collection event if this path wasn't registered in watchService
            // because we need to know for which files the new event should be posted in event bus and filter altered files properly
            registered.add(path);
        } finally {
            lock.unlock();
        }
    }

    public void shutdown() {
        try {
            lock.lock();
            watchers.forEach(Watcher::stopWatching);
            executorService.shutdown();
            watchService.close();
            eventBus.unregister(this);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        } finally {
            lock.unlock();
        }
    }

    private boolean needToRegister(Path pathToRegister) {
        if (registered.contains(pathToRegister)) {
            return false;
        }
        for (Path registeredPath : registered) {
            if (getParentIfFile(registeredPath).equals(getParentIfFile(pathToRegister))) {
                return false;
            }
        }
        return true;
    }

    private static Path getParentIfFile(Path path) {
        if (Files.isRegularFile(path)) {
            return path.getParent();
        }
        return path;

    }

    private static class Watcher implements Runnable {

        private WatchService service;
        private Path dir;
        private boolean active = true;
        private WatchEventHandler eventHandler;
        private static final Logger LOGGER = LoggerFactory.getLogger(Watcher.class);

        private Watcher(WatchService service, WatchEventHandler eventHandler, Path dir) {
            this.service = service;
            this.eventHandler = eventHandler;
            this.dir = dir;
        }

        public void stopWatching() {
            active = false;
        }

        @Override
        public void run() {
            try {
                dir.register(service,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_DELETE);

                while (active) {
                    WatchKey key = service.take();
                    key.pollEvents().forEach(event -> {
                        LOGGER.debug(event.kind() + ": " + event.context());
                        eventHandler.handle(event, key);
                    });

                    boolean valid = key.reset();
                    if (!valid) {
                        break;    // Exit if directory is deleted
                    }
                }
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this).
                    append("dir", dir).
                    append("active", active).
                    toString();
        }
    }

    private class EventBusWatchEventHandler implements WatchEventHandler {
        private EventBus eventBus;
        private WatchEventContextFilter<Path> fileNameFilter;

        private EventBusWatchEventHandler(EventBus eventBus, Set<Path> registered) {
            this.eventBus = eventBus;
            this.fileNameFilter = new WatchEventFileNameFilter(registered);
            this.eventBus.register(this);
        }

        @Override
        public void handle(WatchEvent<?> watchEvent, WatchKey key) {
            if (watchEvent.context() instanceof Path) {
                Path dir = (Path) key.watchable();
                Path path = (Path) watchEvent.context();
                Path fullPath = dir.resolve(path);
                if (fileNameFilter.apply(fullPath)) {
                    eventBus.post(new QuerySetUpdateEvent(fullPath));
                } else {
                    LOGGER.debug("file {} was rejected because isn't registered in watch service", fullPath);
                }
            }
        }
    }

    /**
     * Filter is thread safe and can be shared between multiple threads.
     * There is no guaranties that a calling code gets all actual filters.
     */
    private class WatchEventFileNameFilter implements WatchEventContextFilter<Path> {

        private Set<Path> allowedPaths = Sets.newConcurrentHashSet();

        public WatchEventFileNameFilter(Set<Path> allowedPaths) {
            this.allowedPaths = allowedPaths;
        }

        @Override
        public boolean apply(Path path) {
            Validate.notNull(path, "File name filter cannot be applied for null" );
            return Iterables.tryFind(allowedPaths, allowedPath -> allowedPath.equals(path)).isPresent();
        }
    }

}
