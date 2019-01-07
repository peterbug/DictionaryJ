package mvpdemo.hd.greendao.entity;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Word {
    @Id
    public Long id;
    public String spell;
    public String phoneti;
    public String meaning;
    public Long strangeLevel;
    public Date createDate;
    @Convert(converter = LongConverter.class, columnType = String.class)
    private List<Long> groupIds;

//    @Transient
//    public boolean selected;
    @Generated(hash = 1606958426)
    public Word(Long id, String spell, String phoneti, String meaning,
            Long strangeLevel, Date createDate, List<Long> groupIds) {
        this.id = id;
        this.spell = spell;
        this.phoneti = phoneti;
        this.meaning = meaning;
        this.strangeLevel = strangeLevel;
        this.createDate = createDate;
        this.groupIds = groupIds;
    }

    @Generated(hash = 3342184)
    public Word() {
    }
    public void setGroupIds(List<Long> groupIds) {
        this.groupIds = groupIds;
    }

//    public boolean isSelected() {
//        return selected;
//    }
//
//    public void setSelected(boolean selected) {
//        this.selected = selected;
//    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpell() {
        return this.spell;
    }

    public void setSpell(String spell) {
        this.spell = spell;
    }

    public String getPhoneti() {
        return this.phoneti;
    }

    public void setPhoneti(String phoneti) {
        this.phoneti = phoneti;
    }

    public String getMeaning() {
        return this.meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public Long getStrangeLevel() {
        return this.strangeLevel;
    }

    public void setStrangeLevel(Long strangeLevel) {
        this.strangeLevel = strangeLevel;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public List<Long> getGroupIds() {
        if(groupIds==null){
            groupIds = new ArrayList<>();
        }
        return this.groupIds;
    }
}
