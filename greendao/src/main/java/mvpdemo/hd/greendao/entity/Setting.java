package mvpdemo.hd.greendao.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;

@Entity
public class Setting {
    public final static int ENABLE = 1;
    public final static int DISABLE = -1;
    public final static int DURATION = 5;

    @Id
    public long id;
    public int displaySpell = ENABLE;
    public int displayMeaning = ENABLE;
    public int intervals = DURATION;
    public int repeated = DISABLE;
    public int random = DISABLE;
    @Generated(hash = 1154902504)
    public Setting(long id, int displaySpell, int displayMeaning, int intervals,
            int repeated, int random) {
        this.id = id;
        this.displaySpell = displaySpell;
        this.displayMeaning = displayMeaning;
        this.intervals = intervals;
        this.repeated = repeated;
        this.random = random;
    }
    @Generated(hash = 909716735)
    public Setting() {
    }
    public int getDisplaySpell() {
        return this.displaySpell;
    }
    public void setDisplaySpell(int displaySpell) {
        this.displaySpell = displaySpell;
    }
    public int getDisplayMeaning() {
        return this.displayMeaning;
    }
    public void setDisplayMeaning(int displayMeaning) {
        this.displayMeaning = displayMeaning;
    }
    public int getIntervals() {
        return this.intervals;
    }
    public void setIntervals(int intervals) {
        this.intervals = intervals;
    }
    public int getRepeated() {
        return this.repeated;
    }
    public void setRepeated(int repeated) {
        this.repeated = repeated;
    }
    public int getRandom() {
        return this.random;
    }
    public void setRandom(int random) {
        this.random = random;
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
}
