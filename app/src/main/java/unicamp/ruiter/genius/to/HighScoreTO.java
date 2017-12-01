package unicamp.ruiter.genius.to;

/**
 * Created by Ruiter on 30/11/2017.
 */

public class HighScoreTO {

    private String name;
    private int score;

    public HighScoreTO(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
