package com.perceptiongames.engine.Handlers.Terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.perceptiongames.engine.Entities.AABB;
import com.perceptiongames.engine.Entities.Entity;
import com.perceptiongames.engine.Handlers.Animation;
import com.perceptiongames.engine.Handlers.Content;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import java.util.*;

public class TerrainGenerator {

    private enum RoomType {

        None(0, "None"),
        Standard(1, "Linear"),
        Down(2, "T"),
        Up(3, "UpsideDown T"),
        Cross(4, "Cross");

        public final int VALUE;
        public final String PATH;
        RoomType(int v, String path) {
            VALUE = v;
            PATH = path;
        }

        public static RoomType getEnum(int value) {
            for(RoomType t : RoomType.values()) {
                if(t.VALUE == value)
                    return t;
            }

            return null;
        }
    }

    public static final int ROOM_WIDTH = 10;
    public static final int ROOM_HEIGHT = 8;
    public static final int GRID_SIZE = 4;

    private Tile[][] terrain;
    private Texture[] textures;

    private static final Random random = new Random();

    private boolean left, down;

    public TerrainGenerator(Content content) {
        textures = new Texture[3];
        textures[0] = content.getTexture("Wall");
        textures[1] = content.getTexture("Ground");
        textures[2] = content.getTexture("Ladder");

        terrain = new Tile[GRID_SIZE * ROOM_WIDTH][GRID_SIZE * ROOM_HEIGHT];

        generate();
    }


    /**
     * Creates a new pseudo-generated world
     */
    public void generate() {

        for (int i = 0; i < (GRID_SIZE * ROOM_WIDTH); i++) {
            for (int j = 0; j < (GRID_SIZE * ROOM_HEIGHT); j++) {
                terrain[i][j] = null;
            }
        }

        RoomType[][] rooms = new RoomType[GRID_SIZE][GRID_SIZE];
        for(int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                rooms[i][j] = RoomType.None;
            }
        }

        left = down = false;
        int x = random.nextInt(GRID_SIZE), y = 0;
        while(true) {
            down = false;
            getDir();

            if(left) {
                x--;
            }
            else {
                x++;
            }

            if(x >= GRID_SIZE) {
                x--;
                down = true;
            }
            else if(x < 0) {
                x++;
                down = true;
            }

            if(down) {
                rooms[x][y] = RoomType.Down;
                y++;
                left = !left;
                if(y >= GRID_SIZE) {
                    rooms[x][y - 1] = RoomType.Standard;
                    break;
                }
            }

            if(!down) {
                rooms[x][y] = RoomType.Standard;
            }
            else {
                rooms[x][y] = RoomType.getEnum(random.nextInt(1) + 3);
            }
        }


        /*generateRoom(0, 0, RoomType.None);
        generateRoom(1, 0, RoomType.Standard);
        generateRoom(2, 0, RoomType.Standard);
        generateRoom(3, 0, RoomType.Down);

        generateRoom(3, 1, RoomType.Up);
        generateRoom(2, 1, RoomType.Standard);
        generateRoom(1, 1, RoomType.Down);
        generateRoom(0, 1, RoomType.Down);


        generateRoom(0, 2, RoomType.None);
        generateRoom(1, 2, RoomType.Up);
        generateRoom(2, 2, RoomType.Standard);
        generateRoom(3, 2, RoomType.Down);

        generateRoom(0, 3, RoomType.None);
        generateRoom(1, 3, RoomType.None);
        generateRoom(2, 3, RoomType.None);
        generateRoom(3, 3, RoomType.Up);*/

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                System.out.print(rooms[i][j].VALUE + "  ");
                generateRoom(i, j, rooms[i][j]);
            }
            System.out.println("");
        }

        System.out.println("");
    }

    private void getDir() {
        boolean currentDir = left;

        int next = random.nextInt(5) + 1;
        if(next < 3) { left = true; }
        else if(next < 5) { left = false; }
        else { down = true; }

        if(!down && currentDir != left) left = currentDir;
    }

    private void generateRoom(int xStart, int yStart, RoomType type) {

        /*String file = Gdx.files.internal("Rooms/Cross/2").readString();

        float xOffset = 40; //xStart * ROOM_WIDTH * Tile.SIZE + 40;
        float yOffset = 40; // yStart * ROOM_HEIGHT * Tile.SIZE + 40;

        int xIndex = 0; //(xStart * ROOM_WIDTH);
        int yIndex = 0; //(yStart * ROOM_HEIGHT);

        float halfSize = Tile.SIZE / 2;

        int row = 0, col = 0;
        String[] rows = file.split("\n");
        for(String r : rows) {
            for(char tile : r.toCharArray()) {
                int texture = 0;
                switch (tile) {
                    case '0':
                        texture = -1;
                        break;
                    case '1':
                        texture = 0;
                        break;
                    case '2':
                        texture = 1;
                        break;
                }

                if(texture >= 0) {
                    terrain[xIndex + col][yIndex + row] = new Tile(new Animation(textures[0], 1, 1, 1f),
                            new AABB(xOffset + (Tile.SIZE * col), yOffset + (Tile.SIZE * row), halfSize, halfSize));
                }

                col++;
            }
            col = 0;
            row++;
        }

        for(char tile : file.toCharArray()) {
        }*/


       // if (type == RoomType.None) return;

        float xOffset = xStart * ROOM_WIDTH * Tile.SIZE + 40;
        float yOffset = yStart * ROOM_HEIGHT * Tile.SIZE + 40;

        int xIndex = (xStart * ROOM_WIDTH);
        int yIndex = (yStart * ROOM_HEIGHT);

        float halfSize = Tile.SIZE / 2;

        int room;
        if (type == RoomType.None) {
            room = 0;
        } else {
            room = random.nextInt(3);
        }
        String file = Gdx.files.internal("Rooms/" + type.PATH + "/" + room).readString();
        String[] rows = file.split("\n");

        int row = 0, col = 0;
        for (String r : rows) {
            for (char tile : r.toCharArray()) {
                int texture = -1;
                switch (tile) {
                    case '0':
                        texture = -1;
                        break;
                    case '1':
                        texture = 0;
                        break;
                    case '2':
                        if (random.nextBoolean())
                            texture = 0;
                        else
                            texture = -1;
                        break;
                    case '3':
                        texture = 2;
                        break;
                }

                if (texture > -1) {
                    terrain[xIndex + col][yIndex + row] = new Tile(new Animation(textures[0], 1, 1, 1f),
                            new AABB(xOffset + (Tile.SIZE * col), yOffset + (Tile.SIZE * row), halfSize, halfSize));
                }
                col++;
            }
            row++;
            col = 0;
        }
    }

   /*
     * Creates a new room base on the Room Type given
     * @param xStart Position on the grid width
     * @param yStart Position on the grid height
     * @param type Type of room to create

    private void generateRoom(int xStart, int yStart, RoomType type) {
        if(xStart >= GRID_SIZE || yStart >= GRID_SIZE)
            throw new ValueException("Error: xStart and yStart must be less than GRID SIZE\nxStart: " +
                    xStart + "\nyStart: " + yStart + "\nGRID_SIZE: " + GRID_SIZE);

        float xOffset = xStart * ROOM_WIDTH * Tile.SIZE + 40;
        float yOffset = yStart * ROOM_HEIGHT * Tile.SIZE + 40;

        int xIndex = (xStart * ROOM_WIDTH);
        int yIndex = (yStart * ROOM_HEIGHT);

        float halfSize = Tile.SIZE / 2;

        int seed;
        boolean createTile;
        for (int i = 0; i < ROOM_WIDTH; i++) {
            for (int j = 0; j < ROOM_HEIGHT; j++) {
                seed = 0; //random.nextInt(0);
                createTile = false;
                switch (type) {
                    case Standard:
                        if(j != 4 && seed < 15) { createTile = true; }
                        break;
                    case Down:
                        if(j < 4 && seed < 10) { createTile = true; }
                        else if(j != 4 && i != 5 && seed < 13) { createTile = true; }
                        break;
                    case Up:
                        if(j > 4 && seed < 17) { createTile = true; }
                        else if(j != 4 && i != 5 && seed < 14) { createTile = true; }
                        break;
                    case Cross:
                        if(j != 4 && i != 5 && seed < 15) { createTile = true; }
                        break;
                    case None:
                        if(seed < 15) { createTile = true; }
                        break;
                }

                if(createTile) {
                    terrain[xIndex + i][yIndex + j] = new Tile(new Animation(textures[0], 1, 1, 1f),
                            new AABB(xOffset + (Tile.SIZE * i), yOffset + (Tile.SIZE * j), halfSize, halfSize));
                }

            }
        }
    }*/

    /*public void update(float dt) {
        for (int i = 0; i < (GRID_SIZE * ROOM_WIDTH); i++) {
            for (int j = 0; j < (GRID_SIZE * ROOM_HEIGHT); j++) {
                if(terrain[i][j] != null)
                    terrain[i][j].update(dt);
            }
        }
    }
    public void render(SpriteBatch batch) {
        for (int i = 0; i < (GRID_SIZE * ROOM_WIDTH); i++) {
            for (int j = 0; j < (GRID_SIZE * ROOM_HEIGHT); j++) {
                if(terrain[i][j] != null)
                    terrain[i][j].render(batch);
            }
        }
    }

    public void debugRender(ShapeRenderer sr) {
        for (int i = 0; i < (GRID_SIZE * ROOM_WIDTH); i++) {
            for (int j = 0; j < (GRID_SIZE * ROOM_HEIGHT); j++) {
                if(terrain[i][j] != null)
                    terrain[i][j].getAABB().debugRender(sr);
            }
        }
    }*/

    public Tile[][] getTerrain() {
        return terrain;
    }
}
