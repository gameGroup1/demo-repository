public class Path {
    public static final String highestScore = "highest_score.txt";
    public static final String ballPaddleSound = "/sound_and_music/ball_paddle_collision.wav";
    public static final String ballWallSound = "/sound_and_music/ball_wall_collision.wav";
    public static final String ballBrickSound = "/sound_and_music/ball_brick_collision.wav";
    public static final String brickBreakSound = "/sound_and_music/brick_break.wav";
    public static final String getPointSound = "/sound_and_music/get_point.wav";
    public static final String losePointSound = "/sound_and_music/lose_point.wav";
    public static final String loseLifeSound = "/sound_and_music/lose_life.wav";
    public static final String fireSound = "/sound_and_music/fire_effect.wav";
    public static final String fastSound = "/sound_and_music/fast_effect.wav";
    public static final String slowSound = "/sound_and_music/slow_effect.wav";
    public static final String explosionSound = "/sound_and_music/explosion.wav";
    public static final String powerUpSound = "/sound_and_music/power_up_effect.wav";
    public static final String transformSound = "/sound_and_music/transform_effect.wav";
    public static final String healingSound = "/sound_and_music/healing_effect.wav";
    public static final String menuMusic = "/sound_and_music/MenuMusic.wav";
    public static final String theEndMusic = "/sound_and_music/TheEnd.wav";
    public static final String backgroundMusic = "/sound_and_music/BackgroundMusic.wav";
    public static final String woodSprite = "/Sprite_Bricks/Wood/sprite.png";
    public static final String rockSprite = "/Sprite_Bricks/Rock/sprite.png";
    public static final String metalSprite = "/Sprite_Bricks/Metal/sprite.png";
    public static final String jewelSprite = "/Sprite_Bricks/Jewel/sprite.png";
    public static final String inc10PointCapsule = "/Image_Capsules/10+_point_capsule.png";
    public static final String dec10PointCapsule = "/Image_Capsules/10-_point_capsule.png";
    public static final String inc50PointCapsule = "/Image_Capsules/50+_point_capsule.png";
    public static final String dec50PointCapsule = "/Image_Capsules/50-_point_capsule.png";
    public static final String inc100PointCapsule = "/Image_Capsules/100+_point_capsule.png";
    public static final String dec100PointCapsule = "/Image_Capsules/100-_point_capsule.png";
    public static final String fastBallCapsule = "/Image_Capsules/fast_ball_capsule.png";
    public static final String slowBallCapsule = "/Image_Capsules/slow_ball_capsule.png";
    public static final String fireBallCapsule = "/Image_Capsules/fire_ball_capsule.png";
    public static final String powerBallCapsule = "/Image_Capsules/power_ball_capsule.png";
    public static final String expandPaddleCapsule = "/Image_Capsules/expand_paddle_capsule.png";
    public static final String shrinkPaddleCapsule = "/Image_Capsules/shrink_paddle_capsule.png";
    public static final String explosionCapsule = "/Image_Capsules/explosion_capsule.png";
    public static final String healthCapsule = "/Image_Capsules/health_capsule.png";
    public static final String mouseClick = "/sound_and_music/Mouse_Click.wav";
    public static final String gameOverImage = "/resources/gameover.png";
    public static final String greenButton = "/resources/green_button.png";
    public static final String greyButton = "/resources/grey_button.png";
    public static final String bestScore = "/resources/bestScoreMenu.png";
    //public static final String settingMenu = "/resources/SettingMenu.gif";
    public static String getFileURL(String relativePath) {
        return "file:///" + System.getProperty("user.dir").replace("\\", "/") + relativePath;
    }
}