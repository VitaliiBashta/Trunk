package l2trunk.commons.lang;

import java.lang.management.*;

public final class StatsUtils {
    private static final MemoryMXBean memMXbean = ManagementFactory.getMemoryMXBean();

    private static long getMemUsed() {
        return memMXbean.getHeapMemoryUsage().getUsed();
    }

    public static String getMemUsedMb() {
        return getMemUsed() / 0x100000 + " Mb";
    }

    public static CharSequence getMemUsage() {
        double maxMem = memMXbean.getHeapMemoryUsage().getMax() / 1024.; // maxMemory is the upper limit the jvm can use
        double allocatedMem = memMXbean.getHeapMemoryUsage().getCommitted() / 1024.; //totalMemory the size of the current allocation pool
        double usedMem = memMXbean.getHeapMemoryUsage().getUsed() / 1024.; // freeMemory the unused memory in the allocation pool
        double nonAllocatedMem = maxMem - allocatedMem; //non allocated memory till jvm limit
        double cachedMem = allocatedMem - usedMem; // really used memory
        double useableMem = maxMem - usedMem; //allocated, but non-used and non-allocated memory

        StringBuilder list = new StringBuilder();

        list.append("AllowedMemory: ........... ").append((int) maxMem).append(" KB").append("\n\r");
        list.append("     Allocated: .......... ").append((int) allocatedMem).append(" KB (").append(((double) Math.round(allocatedMem / maxMem * 1000000) / 10000)).append("%)").append("\n\r");
        list.append("     Non-Allocated: ...... ").append((int) nonAllocatedMem).append(" KB (").append((double) Math.round(nonAllocatedMem / maxMem * 1000000) / 10000).append("%)").append("\n\r");
        list.append("AllocatedMemory: ......... ").append((int) allocatedMem).append(" KB").append("\n");
        list.append("     Used: ............... ").append((int) usedMem).append(" KB (").append((double) Math.round(usedMem / maxMem * 1000000) / 10000).append("%)").append("\n\r");
        list.append("     Unused (cached): .... ").append((int) cachedMem).append(" KB (").append((double) Math.round(cachedMem / maxMem * 1000000) / 10000).append("%)").append("\n\r");
        list.append("UseableMemory: ........... ").append((int) useableMem).append(" KB (").append((double) Math.round(useableMem / maxMem * 1000000) / 10000).append("%)").append("\n\r");

        return list;
    }

}