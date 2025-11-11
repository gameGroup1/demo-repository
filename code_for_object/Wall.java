package code_for_object;

import code_def_path.*;
import code_for_button.*;
import code_for_collision.*;
import code_for_levels.*;
import code_for_mainGame.*;
import code_for_manager.*;
import code_for_menu.*;
import code_for_update.*;
import javafx.scene.Group;
import javafx.scene.Node;

public class Wall extends GameObject {
    private Block[] blocks;
    private String direction;
    private Group blockGroup;// Group để chứa tất cả ImageView của các block

    public Wall(String direction, float x, float y, int width, int height, int blockSize) {
        super(x, y, width, height);
        this.direction = direction;
        int blockLength = direction.equals("top") ? width / blockSize : height / blockSize;
        blocks = new Block[blockLength];
        blockGroup = new Group(); // Tạo Group để ghép các block
        makeBlocks(blockSize);
    }

    private void makeBlocks(int blockSize){
        if (direction.equals("left")) {
            for(int i = 1; i < blocks.length; i++) {
                blocks[i] = new Block(getX(), i * blockSize, blockSize, blockSize);
                blockGroup.getChildren().add(blocks[i].getNode()); // Thêm ImageView vào Group
            }
        } else if (direction.equals("right")) {
            for(int i = 1; i < blocks.length; i++) {
                blocks[i] = new Block(getX(), i * blockSize, blockSize, blockSize);
                blockGroup.getChildren().add(blocks[i].getNode()); // Thêm ImageView vào Group
            }
        } else { // top wall
            for(int i = 0; i < blocks.length; i++) {
                blocks[i] = new Block(i * blockSize, 0, blockSize, blockSize);
                blockGroup.getChildren().add(blocks[i].getNode()); // Thêm ImageView vào Group
            }
        }
    }

    @Override
    public void render() {}

    // Phương thức hỗ trợ: Trả về Node (Group chứa tất cả ImageView của blocks) để thêm vào scene graph
    public Node getNode() {
        return blockGroup;
    }
}