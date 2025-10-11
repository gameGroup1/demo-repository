import javafx.scene.Group;
import javafx.scene.Node;

public class Wall extends GameObject {
    private Block[] blocks;
    private String direction;
    private Group blockGroup; // Group để chứa tất cả ImageView của các block

    public Wall(String direction, double x, double y, int width, int height, int blockSize) {
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
    public void render() {
        // Vì wall tĩnh, không cần cập nhật, nhưng có thể gọi render cho từng block nếu cần
        /*for (Block block : blocks) {
            if (block != null) {
                block.render();
            }
        }*/
    }

    // Phương thức hỗ trợ: Trả về Node (Group chứa tất cả ImageView của blocks) để thêm vào scene graph
    public Node getNode() {
        return blockGroup;
    }
}