package bgu.spl.net.api;

public class objectOfThree {

    private int first;
    private String second;
    private int third;

    public objectOfThree(int first, String second, int third) {
        this.first = first;
        this.second = second;
        this.third = third;
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

    public int getThird() {
        return third;
    }
}
