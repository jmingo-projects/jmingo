package com.mingo.query.watch;

import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;

public interface WatchEventHandler {

    void handle(WatchEvent<?> watchEvent, WatchKey key);
}
