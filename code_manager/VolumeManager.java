package code_manager;

import javafx.scene.media.MediaPlayer;
import javafx.scene.media.AudioClip;
import java.util.ArrayList;
import java.util.List;
public class VolumeManager {
    private static double backgroundVolume = 0.5; // Âm lượng mặc định cho nhạc nền (0.0 đến 1.0)
    private static double effectVolume = 0.5; // Âm lượng mặc định cho hiệu ứng (0.0 đến 1.0)
    private static final List<MediaPlayer> mediaPlayers = new ArrayList<>();
    private static final List<AudioClip> audioClips = new ArrayList<>();
    public static double getBackgroundVolume() {
        return backgroundVolume;
    }
    public static void setBackgroundVolume(double volume) {
        if (volume < 0.0) {
            volume = 0.0;
        } else if (volume > 1.0) {
            volume = 1.0;
        }
        backgroundVolume = volume;
        // Cập nhật âm lượng cho tất cả MediaPlayer
        for (MediaPlayer player : mediaPlayers) {
            if (player != null) {
                player.setVolume(backgroundVolume);
            }
        }
    }
    public static double getEffectVolume() {
        return effectVolume;
    }
    public static void setEffectVolume(double volume) {
        if (volume < 0.0) {
            volume = 0.0;
        } else if (volume > 1.0) {
            volume = 1.0;
        }
        effectVolume = volume;
    }
    public static void registerMediaPlayer(MediaPlayer player) {
        if (player != null && !mediaPlayers.contains(player)) {
            mediaPlayers.add(player);
            player.setVolume(backgroundVolume);
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