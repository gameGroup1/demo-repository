public class Path {
    public static final String highestScore = "highest_score.txt";
    public static final String woodSound = "/sound_and_music/wood_break.mp3";
    public static final String rockSound = "/sound_and_music/rock_break.mp3";
    public static final String metalSound = "/sound_and_music/metal_break.mp3";
    public static final String jewelSound = "/sound_and_music/jewel_break.mp3";
    public static final String getPointSound = "/sound_and_music/get_point.mp3";
    public static final String losePointSound = "/sound_and_music/lose_point.mp3";
    public static final String fireSound = "/sound_and_music/fire_effect.mp3";
    public static final String toxicSound = "/sound_and_music/toxic_effect.mp3";
    public static final String fastSound = "/sound_and_music/fast_effect.mp3";
    public static final String slowSound = "/sound_and_music/slow_effect.mp3";
    public static final String powerUpSound = "/sound_and_music/power_up_effect.mp3";
    public static final String transformSound = "/sound_and_music/transform_effect.mp3";
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
    public static final String toxicBallCapsule = "/Image_Capsules/toxic_ball_capsule.png";
    public static final String powerBallCapsule = "/Image_Capsules/power_ball_capsule.png";
    public static final String multiBallCapsule = "/Image_Capsules/multi_ball_capsule.png";
    public static final String expandPaddleCapsule = "/Image_Capsules/expand_paddle_capsule.png";
    public static final String shrinkPaddleCapsule = "/Image_Capsules/shrink_paddle_capsule.png";

    public static String getFileURL(String relativePath) {
        return "file:///" + System.getProperty("user.dir").replace("\\", "/") + relativePath;
    }
}
