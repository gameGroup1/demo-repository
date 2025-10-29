public class LevelDemo extends GameLevel {

    public LevelDemo(int wallThickness, int speedC) {
        // Gọi super() với chỉ 1 hàng, 1 cột, mỗi viên gạch có kích thước 90x30, khoảng cách 5
        super(1, 1, 90, 30, 5, wallThickness, speedC);
        generateMap();
    }

    @Override
    public void generateMap() {
        // Mỗi level cần tạo mảng bricks và capsules
        bricks = new Bricks[rowCount * colCount];
        capsules = new Capsule[rowCount * colCount];

        // Vị trí viên gạch duy nhất
        double brickX = wallThickness + 300; // đặt gần giữa màn hình
        double brickY = wallThickness + 200; // cao vừa phải

        int index = 0;

        // Viên gạch có độ cứng 1
        bricks[index] = new Bricks(brickX, brickY, brickWidth, brickHeight, 1);

        // Cho viên gạch này rơi ra một capsule nổ (demo hiệu ứng)
        capsules[index] = new Capsule(Path.explosionCapsule, Path.explosionSound);
        capsules[index].init(brickX, brickY, brickWidth, brickHeight, speedC, "explosion");
        capsules[index].setVisible(false);
        capsuleIndex.add(index);
    }
}
