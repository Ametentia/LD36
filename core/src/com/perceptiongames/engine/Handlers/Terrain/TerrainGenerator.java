package com.perceptiongames.engine.Handlers.Terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.perceptiongames.engine.Entities.AABB;
import com.perceptiongames.engine.Entities.Enemy;
import com.perceptiongames.engine.Entities.Entity;
import com.perceptiongames.engine.Handlers.Animation;
import com.perceptiongames.engine.Handlers.Content;

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

    private List<Enemy> enemies;

    private Vector2 startPosition;
    private int endRoomX;
    private int endRoomY;

    private static final Random random = new Random();

    private boolean left, down;

    private Content content;

    public TerrainGenerator(Content content) {
        textures = new Texture[7];
        textures[0] = content.getTexture("BrokenWall1");
        textures[1] = content.getTexture("BrokenWall");
        textures[2] = content.getTexture("BrokenWall2");
        textures[3] = content.getTexture("Spikes");
        textures[4] = content.getTexture("SpearBlock");
        textures[5] = content.getTexture("Ladder");

        this.content = content;

        terrain = new Tile[GRID_SIZE * ROOM_WIDTH][GRID_SIZE * ROOM_HEIGHT];

        enemies = new ArrayList<Enemy>();

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
        startPosition = new Vector2();
        startPosition.y = 4 * Tile.SIZE;
        startPosition.x = ((x + 1) * ROOM_WIDTH * Tile.SIZE) + 5 * Tile.SIZE;

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
                    endRoomX = x;
                    endRoomY = y - 1;
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

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                System.out.print(rooms[i][j].VALUE + "  ");
                generateRoom(i, j, rooms[i][j]);
            }
             System.out.println("");
        }
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

        float xOffset = xStart * ROOM_WIDTH * Tile.SIZE + 40;
        float yOffset = yStart * ROOM_HEIGHT * Tile.SIZE + 40;

        int xIndex = (xStart * ROOM_WIDTH);
        int yIndex = (yStart * ROOM_HEIGHT);

        float halfSize = Tile.SIZE / 2;

        int room;
        if (type == RoomType.None) {
            room = 0;
        }
        else if(type == RoomType.Cross) {
            room = 4;
        }
        else {
            room = random.nextInt(5);
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
                        texture = random.nextInt(3);
                        break;
                    case '2':
                        texture = 3;
                        break;
                    case '9':
                        if (random.nextBoolean() )
                            texture = random.nextInt(3);
                        else
                            texture = -1;
                        break;
                    case '4':
                        terrain[xIndex + col][yIndex + row] = new FallingBlock(textures[random.nextInt(3)],
                                new AABB(xOffset + (Tile.SIZE * col), yOffset + (Tile.SIZE * row), halfSize, halfSize));
                        break;
                    case 'P':
                        terrain[xIndex + col][yIndex + row] = new Sensor(yIndex + row, xIndex + col,
                                new AABB(xOffset + (Tile.SIZE * col), yOffset + (Tile.SIZE * row), halfSize, halfSize));

                        ((Sensor)terrain[xIndex + col][yIndex + row]).setData("Player");
                        break;
                    case 'E':
                        terrain[xIndex + col][yIndex + row] = new Sensor(yIndex + row, xIndex + col,
                                new AABB(xOffset + (Tile.SIZE * col), yOffset + (Tile.SIZE * row), halfSize, halfSize));

                        ((Sensor)terrain[xIndex + col][yIndex + row]).setData("EnemyLeft");
                        break;
                    case 'R':
                        terrain[xIndex + col][yIndex + row] = new Sensor(yIndex + row, xIndex + col,
                                new AABB(xOffset + (Tile.SIZE * col), yOffset + (Tile.SIZE * row), halfSize, halfSize));

                        ((Sensor)terrain[xIndex + col][yIndex + row]).setData("EnemyRight");
                        break;
                    case '6':
                        terrain[xIndex + col][yIndex + row] = new SpearBlock(new Animation(textures[4], 1, 50, 0.028f),
                                new AABB(xOffset + (Tile.SIZE * col), yOffset + (Tile.SIZE * row), halfSize, halfSize), true);

                        break;
                    case '7':
                        terrain[xIndex + col][yIndex + row] = new SpearBlock(new Animation(textures[4], 1, 50, 0.028f),
                                new AABB(xOffset + (Tile.SIZE * col), yOffset + (Tile.SIZE * row), halfSize, halfSize), false);
                        break;
                    case 'L':
                        texture = 5;
                        break;
                    case 'S':
                        generateEnemy(new Vector2(xOffset + (Tile.SIZE * col), yOffset + (Tile.SIZE * row)));
                        break;
                    case 'T':
                        if(xStart == endRoomX && yStart == endRoomY) {
                            terrain[xIndex + col][yIndex + row] = new StandardTile(content.getTexture("EndDoor"),
                                    new AABB(xOffset + (Tile.SIZE * col), yOffset + (Tile.SIZE * row), halfSize, halfSize),
                                    false);

                            terrain[xIndex + col][yIndex + row].getAABB().setSensor(true);
                            terrain[xIndex + col][yIndex + row].setDamage(-4);
                        }
                        else {
                            texture = -1;
                        }
                }

                if (texture > -1) {
                    terrain[xIndex + col][yIndex + row] = new StandardTile(textures[texture],
                            new AABB(xOffset + (Tile.SIZE * col), yOffset + (Tile.SIZE * row), halfSize, halfSize),
                            texture == 5);

                    if(texture == 5) {
                        terrain[xIndex + col][yIndex + row].getAABB().setSensor(true);
                    }
                    if(texture == 3) terrain[xIndex + col][yIndex + row].setDamage(1);
                }


                if(terrain[xIndex + col][yIndex + row] != null) {
                    terrain[xIndex + col][yIndex + row].setRow(yIndex + row);
                    terrain[xIndex + col][yIndex + row].setColumn(xIndex + col);
                }

                col++;
            }
            row++;
            col = 0;
        }
    }

    private void generateEnemy(Vector2 pos) {
        Animation a = new Animation(content.getTexture("Enemy"),1,1, 10f);
        Enemy bad = new Enemy(a,"idle", new AABB(new Vector2(pos.x + 31, pos.y + 31),new Vector2(31,31)));
        bad.addAnimation("attack",new Animation(content.getTexture("EnemyAttack"),1,7, 0.08f));
        a =new Animation(content.getTexture("EnemyMove"),1,6,0.5f);
        bad.addAnimation("Right",a);
        a =new Animation(content.getTexture("EnemyMove"),1,6,0.5f);
        a.setFlipX(true);
        bad.addAnimation("Left",a);
        bad.setWeapon(new AABB(100, 100, 7f, 7f));
        enemies.add(bad);
    }

    public Tile[][] getTerrain() {
        return terrain;
    }
    public Vector2 getStartPosition() { return startPosition; }
    public List<Enemy> getEnemies() { return enemies; }
}
