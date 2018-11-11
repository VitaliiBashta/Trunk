package l2trunk.gameserver.utils;

public final class SqlBatch {
    private final String _header;
    private final String _tail;
    private StringBuilder _sb;
    private final StringBuilder _result;
    private long limit = Long.MAX_VALUE;
    private boolean isEmpty = true;

    private SqlBatch(String header, String tail) {
        _header = header + "\n";
        _tail = tail != null && tail.length() > 0 ? " " + tail + ";\n" : ";\n";
        _sb = new StringBuilder(_header);
        _result = new StringBuilder();
    }

    public SqlBatch(String header) {
        this(header, null);
    }

    public void writeStructure(String str) {
        _result.append(str);
    }

    public void write(String str) {
        isEmpty = false;
        if (_sb.length() + str.length() < limit - _tail.length())
            _sb.append(str).append(",\n");
        else {
            _sb.append(str).append(_tail);
            _result.append(_sb.toString());
            _sb = new StringBuilder(_header);
        }
    }

    private void writeBuffer() {
        String last = _sb.toString();
        if (last.length() > 0)
            _result.append(last, 0, last.length() - 2).append(_tail);
        _sb = new StringBuilder(_header);
    }

    public String close() {
        if (_sb.length() > _header.length())
            writeBuffer();
        return _result.toString();
    }

    public void setLimit(long l) {
        limit = l;
    }

    public boolean isEmpty() {
        return isEmpty;
    }
}