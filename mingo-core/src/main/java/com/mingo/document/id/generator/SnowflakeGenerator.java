package com.mingo.document.id.generator;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.GregorianCalendar;

import static java.net.InetAddress.getLocalHost;

/**
 * Generator of unique positive Long numbers based on Twitter snowflake, https://github.com/twitter/snowflake.
 * <p>
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
    private static final long MACHINE_ID_BITS = 10L;
    private static final long MAX_MACHINE_ID = -1L ^ (-1L << MACHINE_ID_BITS);

    private static final long TIME_BITS = 41L;

    private static final long TIMESTAMP_SHIFT = 64L - TIME_BITS;
    private static final long MACHINE_ID_SHIFT = 64L - TIME_BITS - MACHINE_ID_BITS;

    private static final long SEQUENCE_MAX_VALUE = 4096;
    private static final long EPOCH = new GregorianCalendar(2013, 0, 1).getTimeInMillis(); // 1357014600000

    private volatile long lastTimestamp = -1L;
    private volatile long sequence = 0L;
    /* init in constructor */
    private final long machineId;

    private static final long BAD_MACHINE_ID = 0L;

    /**
     * Default constructor.
     */
    private SnowflakeGenerator() {
        machineId = MachineIdentifier.MAC.getUniqueIdentifier();
        if (machineId > MAX_MACHINE_ID || machineId <= BAD_MACHINE_ID) {
            throw new RuntimeException("Failed creation of unique machine id");
        }
    }

    public static SnowflakeGenerator getInstance() {
        return Singleton.INSTANCE.create();
    }

    /**
     * Generates unique id.
     *
     * @return unique id
     */
    @Override
    public synchronized Long generate() {
        return (((getTimestamp() - EPOCH) << TIMESTAMP_SHIFT) | (machineId << MACHINE_ID_SHIFT) | sequence);
    }

    /**
     * Returns timestamp.
     * If current timestamp equal to lastTimestamp and current sequence less then SEQUENCE_MAX_VALUE than increase sequence.
     * If sequence achieved SEQUENCE_MAX_VALUE then try to shift timestamp on next millisecond.
     *
     * @return timestamp.
     */
    private long getTimestamp() {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id for "
                    + (lastTimestamp - timestamp) + " milliseconds.");
        }

        sequence = ++sequence % SEQUENCE_MAX_VALUE;

        if (lastTimestamp == timestamp && sequence == 0) {
            while (timestamp <= lastTimestamp) {
                timestamp = System.currentTimeMillis();
            }
        } else if (lastTimestamp != timestamp) {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        return timestamp;
    }

    private enum MachineIdentifier {
        /**
         * Creates unique identifier based on hardware address of network interface.
         */
        MAC {
            @Override
            public Long getUniqueIdentifier() {
                try {
                    NetworkInterface network = NetworkInterface.getByInetAddress(getLocalHost());
                    byte[] mac = network.getHardwareAddress();
                    return (((0x000000FF & (long) mac[mac.length - 1])
                            | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6);
                } catch (SocketException e) {
                    throw new RuntimeException(e);
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        /**
         * Creates unique identifier.
         */
        public abstract Long getUniqueIdentifier();
    }

    private enum Singleton {

        INSTANCE;

        private static final SnowflakeGenerator instance = new SnowflakeGenerator();

        public SnowflakeGenerator create() {
            return instance;
        }
    }
}
