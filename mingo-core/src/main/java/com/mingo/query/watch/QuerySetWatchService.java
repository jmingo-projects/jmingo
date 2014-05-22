/**
 * Copyright 2012-2013 The Mingo Team
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mingo.query.watch;


import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mingo.exceptions.WatchServiceException;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class QuerySetWatchService {

    ThreadFactory watcherNamedThreadFactory = new ThreadFactoryBuilder().setNameFormat("watcher-thread-%d").build();
    private final ExecutorService executorService = Executors.newFixedThreadPool(10, watcherNamedThreadFactory);
    private final Set<Path> registered = Sets.newConcurrentHashSet();
    private final WatchService watchService;
    private final WatchEventHandler watchEventHandler;
    private final EventBus eventBus;

    // global lock
    private Lock lock = new ReentrantLock();

    private static final Logger LOGGER = LoggerFactory.getLogger(QuerySetWatchService.class);

    public QuerySetWatchService(EventBus eventBus) {
        try {
            this.eventBus = eventBus;
            LOGGER.debug("create new watch service");
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
        Validate.notNull(path, "path to watch cannot be null");
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
                executorService.submit(watcher);
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

    public void shutdown() throws WatchServiceException {
        try {
            lock.lock();
            LOGGER.debug("shutdown watcher thread pool");
            executorService.shutdownNow();
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                throw new WatchServiceException("failed to terminate all watcher threads");
            }
            LOGGER.debug("close watch service");
            watchService.close();
            eventBus.unregister(this);
        } catch (ClosedWatchServiceException | InterruptedException e) {
                /*  Allow thread to exit  */
        } catch (IOException e) {
            throw new WatchServiceException(e);
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

        private Path dir;
        private WatchService service;
        private WatchEventHandler eventHandler;
        private static final Logger LOGGER = LoggerFactory.getLogger(Watcher.class);

        private Watcher(WatchService service, WatchEventHandler eventHandler, Path dir) {
            this.service = service;
            this.eventHandler = eventHandler;
            this.dir = dir;
        }

//        /**
//         * this method doesn't change interrupt status if current thread is executed in thread pool and
//         * this method is called on the instance directly.
//         * in the cause of using in a thread pool use shutdownNow() to interrupt thread.
//         */
//        public void stopWatching() {
//            // using interruption for cancellation.
//            LOGGER.debug("stop watcher for: " + dir);
//            interrupt();
//        }

        @Override
        public void run() {
            try {
                dir.register(service,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_DELETE);

                while (!Thread.currentThread().isInterrupted()) {
                    final WatchKey key = service.poll(1000, TimeUnit.MILLISECONDS);
                    if (key != null) {
                        key.pollEvents().forEach(event -> {
                            LOGGER.debug(event.kind() + ": " + event.context());
                            eventHandler.handle(event, key);
                        });

                        boolean valid = key.reset();
                        if (!valid) {
                            break;    // Exit if directory is deleted
                        }
                    }
                }
            } catch (InterruptedException | ClosedWatchServiceException e) {
                /*  Allow thread to exit  */
            } catch (IOException e) {
                throw Throwables.propagate(e);
            } finally {
                LOGGER.debug("terminate watcher: " + toString());
            }
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(Thread.currentThread().getName() + " {");
            sb.append("dir=").append(dir);
            sb.append('}');
            return sb.toString();
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
            Validate.notNull(path, "File name filter cannot be applied for null");
            return Iterables.tryFind(allowedPaths, allowedPath -> allowedPath.equals(path)).isPresent();
        }
    }

}
