package com.mingo.query.watch;


import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuerySetWatchService {

    private final WatchService service;
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private List<Path> registered = Lists.newCopyOnWriteArrayList();
    private List<Watcher> watchers = Lists.newArrayList();

    public QuerySetWatchService() {
        try {
            service = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    public void regiser(Path querySetPath) {
        if (!isRegistered(querySetPath)) {
            executorService.execute(new Watcher(service, querySetPath));
            registered.add(querySetPath);
        }
    }

    public void shutdown() {
        watchers.forEach(Watcher::stopWatching);
        executorService.shutdown();
    }

    private boolean isRegistered(Path path) {
        for (Path registeredPath : registered) {
            if (path.equals(registeredPath) || path.startsWith(registeredPath)) {
                return true;
            }
        }
        return false;
    }

    private static class Watcher implements Runnable {

        private WatchService service;
        private Path dir;
        private boolean active = true;

        private Watcher(WatchService service, Path dir) {
            this.service = service;
            this.dir = dir;
        }

        public void stopWatching(){
            active = false;
        }

        @Override
        public void run() {
            try {
                dir.register(service,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_DELETE);    // Register the directory

                while (active) {
                    WatchKey key = service.take();    // retrieve the watchkey
                    for (WatchEvent event : key.pollEvents()) {
                        System.out.println(event.kind() + ": " + event.context());    // Display event and file name
                    }
                    boolean valid = key.reset();
                    if (!valid) {
                        break;    // Exit if directory is deleted
                    }
                }
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
        }
    }

}
