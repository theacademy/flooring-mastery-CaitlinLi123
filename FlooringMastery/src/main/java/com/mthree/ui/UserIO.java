package com.mthree.ui;

import java.time.LocalDate;

public interface UserIO {
    public void print(String msg);

    public String readString(String msgPrompt);

    public int readInt(String msgPrompt);

    public int readInt(String msgPrompt, int min, int max);

    public LocalDate readDate(String s);
}
