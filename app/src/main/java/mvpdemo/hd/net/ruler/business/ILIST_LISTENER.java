package mvpdemo.hd.net.ruler.business;

public interface ILIST_LISTENER {
    public int position = 0;

    public void select(int position);

    public void playBoy(int position);

    public void playGirl(int position);
}
