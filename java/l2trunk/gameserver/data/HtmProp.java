package l2trunk.gameserver.data;

class HtmProp {
    private final String keyWord;
    private final String text;

    public HtmProp(String keyWord, String text) {
        this.keyWord = keyWord;
        this.text = text;
    }

    public String getKeyWord() {
        return this.keyWord;
    }

    public String getText() {
        return this.text;
    }
}