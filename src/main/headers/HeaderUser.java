package main.headers;

public final class HeaderUser extends Header {

    public final String name;
    public final long lastDate;
    public boolean rus;

    public HeaderUser(final String username, final long lastDate, final boolean rus) {
        this.name = username;
        this.lastDate = lastDate;
        this.rus = rus;
    }

    public static final int PARAMS = 2;
    @Override
    public Object getParam(int id) {
        switch (id) {
            case 0: return name;
            case 1: return lastDate;

            default:
                break;
        }
        return null;
    }
}
