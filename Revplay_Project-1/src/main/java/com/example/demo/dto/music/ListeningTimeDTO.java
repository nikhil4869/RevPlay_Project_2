package com.example.demo.dto.music;

public class ListeningTimeDTO {

    private int seconds;
    private int minutes;
    private String formatted;

    public ListeningTimeDTO(int seconds) {
        this.seconds = seconds;
        this.minutes = seconds / 60;
        int remaining = seconds % 60;
        this.formatted = minutes + " min " + remaining + " sec";
    }

    public int getSeconds() { return seconds; }
    public int getMinutes() { return minutes; }
    public String getFormatted() { return formatted; }
}