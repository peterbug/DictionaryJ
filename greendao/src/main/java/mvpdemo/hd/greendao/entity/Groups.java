package mvpdemo.hd.greendao.entity;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

import java.util.List;

@Entity
public class Groups {
    @Id
    public Long id;
    public String name;
    public String memo;
    @Property
    @Convert(converter = LongConverter.class, columnType = String.class)
    private List<Long> word_ids;

//    @Transient
//    public boolean selected;

    @Generated(hash = 2106693052)
    public Groups(Long id, String name, String memo, List<Long> word_ids) {
        this.id = id;
        this.name = name;
        this.memo = memo;
        this.word_ids = word_ids;
    }

    @Generated(hash = 893039872)
    public Groups() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public List<Long> getWord_ids() {
        return this.word_ids;
    }

    public void setWord_ids(List<Long> word_ids) {
        this.word_ids = word_ids;
    }

//    public boolean getSelected() {
//        return selected;
//    }
//
//    public void setSelected(boolean selected) {
//        this.selected = selected;
//    }

}
