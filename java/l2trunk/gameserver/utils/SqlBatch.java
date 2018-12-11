package l2trunk.gameserver.utils;

public final class SqlBatch {
    private final String header;
    private final String tail;
    private final StringBuilder result;
    private StringBuilder sb;
    private long limit = Long.MAX_VALUE;
    private boolean isEmpty = true;

    private SqlBatch(String header, String tail) {
        this.header = header + "\n";
        this.tail = tail != null && tail.length() > 0 ? " " + tail + ";\n" : ";\n";
        sb = new StringBuilder(this.header);
        result = new StringBuilder();
    }

    public SqlBatch(String header) {
        this(header, null);
    }

    public void writeStructure(String str) {
        result.append(str);
    }

    public void write(String str) {
        isEmpty = false;
        if (sb.length() + str.length() < limit - tail.length())
            sb.append(str).append(",\n");
        else {
            sb.append(str).append(tail);
            result.append(sb.toString());
            sb = new StringBuilder(header);
        }
    }

    private void writeBuffer() {
        String last = sb.toString();
        if (last.length() > 0)
            result.append(last, 0, last.length() - 2).append(tail);
        sb = new StringBuilder(header);
    }

    public String close() {
        if (sb.length() > header.length())
            writeBuffer();
        return result.toString();
    }

    public void setLimit(long l) {
        limit = l;
    }

    public boolean isEmpty() {
        return isEmpty;
    }
}