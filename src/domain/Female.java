package domain;

import java.util.Optional;

public class Female {
    private String name;
    private Optional<Male> boyfriend;

    public Female() {
    }

    public Female(String name) {
        this.name = name;
    }

    public Female(String name, Optional<Male> boyfriend) {
        this.name = name;
        this.boyfriend = boyfriend;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Optional<Male> getBoyfriend() {
        return boyfriend;
    }

    public void setBoyfriend(Optional<Male> boyfriend) {
        this.boyfriend = boyfriend;
    }
}
