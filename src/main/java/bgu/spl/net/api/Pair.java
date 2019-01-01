package bgu.spl.net.api;

public class Pair {

    private int first;
    private String second;

    public Pair(int first, String second) {
        this.first = first;
        this.second = second;
    }

    public int getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public void setSecond(String second) {
        this.second = second;
    }
}
