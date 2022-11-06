package com.example.quizapplication.Models;

public class Score {
    private String userid;
    private String userName;
    private int score;
    private long time;

    public Score(){

    }

    public Score(String userid, String userName, int score, long time) {
        this.userid = userid;
        this.userName = userName;
        this.score = score;
        this.time = time;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
