import javafx.scene.media.MediaPlayer;
import javafx.scene.media.AudioClip;
import java.util.ArrayList;
import java.util.List;

public class SoundManager {
    private static double globalVolume = 0.5; // Giá trị âm lượng mặc định (0.0 đến 1.0)
    private static final List<MediaPlayer> mediaPlayers = new ArrayList<>();
    private static final List<AudioClip> audioClips = new ArrayList<>();

    public static double getGlobalVolume() {
        return globalVolume;
    }

    public static void setGlobalVolume(double volume) {
        if (volume < 0.0) {
            volume = 0.0;
        } else if (volume > 1.0) {
            volume = 1.0;
        }
        globalVolume = volume;
        // Cập nhật âm lượng cho tất cả MediaPlayer
        for (MediaPlayer player : mediaPlayers) {
            if (player != null) {
                player.setVolume(globalVolume);
            }
        }
        // AudioClip không cần cập nhật ở đây vì âm lượng được đặt khi play()
    }

    public static void registerMediaPlayer(MediaPlayer player) {
        if (player != null && !mediaPlayers.contains(player)) {
            mediaPlayers.add(player);
            player.setVolume(globalVolume);
        }
    }

    public static void unregisterMediaPlayer(MediaPlayer player) {
        mediaPlayers.remove(player);
    }

    public static void registerAudioClip(AudioClip clip) {
        if (clip != null && !audioClips.contains(clip)) {
            audioClips.add(clip);
        }
    }

    public static void unregisterAudioClip(AudioClip clip) {
        audioClips.remove(clip);
    }

    public static void stopAllSounds() {
        // Dừng và xóa tất cả MediaPlayer
        for (MediaPlayer player : new ArrayList<>(mediaPlayers)) {
            if (player != null) {
                player.stop();
                player.dispose(); // Giải phóng tài nguyên
            }
        }
        mediaPlayers.clear();

        // Dừng và xóa tất cả AudioClip
        for (AudioClip clip : new ArrayList<>(audioClips)) {
            if (clip != null) {
                clip.stop();
            }
        }
        audioClips.clear();
    }
}