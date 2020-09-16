package net.nodium.mcmods.blockcounter;

public class Position {
    int x;
    int y;
    int z;

    public Position(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Position loadPositionFromString(String string) {
        String[] strings = string.split(", ");
        Position position = new Position(0, 0, 0);
        position.x = Integer.parseInt(strings[0]);
        position.y = Integer.parseInt(strings[1]);
        position.z = Integer.parseInt(strings[2]);
        return position;
    }

    @Override
    public String toString() {
        return String.format("x%d y%d z%d", x, y, z);
    }
}
