package org.example;

public class Channel {
    public double getTnext() {
        return tnext;
    }

    public double tnext;
    public int state;
    public int id;

    public Channel(double tnext, int state, int id) {
        this.tnext = tnext;
        this.state = state;
        this.id = id;
    }
}
