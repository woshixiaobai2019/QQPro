package com.me.Const;

import com.me.Interface.Message;

import java.io.Serializable;

public class SingleFileBackObj implements Message, Serializable {
    private String from;
    private String to;
    private boolean confirm;
    private long size;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public boolean isConfirm() {
        return confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "SingleFileBackObj{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", confirm=" + confirm +
                ", size=" + size +
                '}';
    }
}
