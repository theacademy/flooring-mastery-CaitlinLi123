package com.mthree.ui;

import java.time.LocalDate;

public interface UserIO {
    public void print(String msg);

    public String readString(String msgPrompt);

    public int readInt(String msgPrompt);

    public int readInt(String msgPrompt, int min, int max);

    public long readLong(String msgPrompt);

    public long readLong(String msgPrompt, long min, long max);

    public float readFloat(String msgPrompt);

    public float readFloat(String msgPrompt, float min, float max);

    public double readDouble(String msgPrompt);

    public double readDouble(String msgPrompt, double min, double max);

    public LocalDate readDate(String s);
}
