package domain;

import java.util.Optional;

public class Male {
    private String name;
    private Optional<Female> girlfriend;

    public Male() {
    }


    public Male(String name) {
        this.name = name;
    }

    public Male(String name, Optional<Female> girlfriend) {
        this.name = name;
        this.girlfriend = girlfriend;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Optional<Female> getGirlfriend() {
        return girlfriend;
    }

    public void setGirlfriend(Optional<Female> girlfriend) {
        this.girlfriend = girlfriend;
    }
}
