package com.mingo.document.id.generator;

import com.mingo.exceptions.IdGenerationException;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Generator of UNIQUE positive Long numbers based on Twitter snowflake, https://github.com/twitter/snowflake.
 * <p>
 * id is composed of:
 * TIME_BITS - 41 bits (millisecond precision w/ a custom EPOCH gives us 69 years)
 * MACHINE_ID_BITS - 10 bits - gives us up to 1024 machines
 * SEQUENCE_MAX_BITS - 12 bits - rolls over every 4096 per machine (with protection to avoid rollover in the same ms)
 * <p>
 * Requirements:
 * id must be greater than Integer.MAX_VALUE
 * minimum 10k ids per second per process
 * response rate 2ms (plus network latency)
 */
public class SnowflakeGenerator implements IdGenerator {
    private final long datacenterIdBits = 10L;
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    private final long timestampBits = 41L;

    private final long datacenterIdShift = 64L - datacenterIdBits;
    private final long timestampLeftShift = 64L - datacenterIdBits - timestampBits;
    private final long sequenceMax = 4096;
    private final long twepoch = 1288834974657L;
    private final long datacenterId;

    private volatile long lastTimestamp = -1L;
    private volatile long sequence = 0L;


    public SnowflakeGenerator() throws RuntimeException {
        datacenterId = getDatacenterId();
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new RuntimeException("datacenterId > maxDatacenterId");
        }
    }

    /**
     * Generates UNIQUE long value.
     *
     * @return UNIQUE long value
     * @throws IdGenerationException
     */
    @Override
    public synchronized Long generate() throws IdGenerationException {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            throw new IdGenerationException("Clock moved backwards.  Refusing to generate id for " + (
                    lastTimestamp - timestamp) + " milliseconds.");
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) % sequenceMax;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        Long id = ((timestamp - twepoch) << timestampLeftShift) |
                (datacenterId << datacenterIdShift) |
                sequence;
        return id;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    protected long getDatacenterId() throws IdGenerationException {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();
            long id = ((0x000000FF & (long) mac[mac.length - 1]) | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
            return id;
        } catch (SocketException | UnknownHostException e) {
            throw new IdGenerationException(e);
        }
    }

}
