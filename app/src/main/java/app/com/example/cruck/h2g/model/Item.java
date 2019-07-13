package app.com.example.cruck.h2g.model;

/**
 * Created by cruck on 10-06-2016.
 */
public class Item {
    public int icon;
    public String title;
    public String distance;
    public Item(){
        super();
    }

    public Item(int icon, String title) {
        super();
        this.icon = icon;
        this.title = title;
    }
}
