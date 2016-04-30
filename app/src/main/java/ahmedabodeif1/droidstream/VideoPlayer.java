package ahmedabodeif1.droidstream;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.VideoView;

public class VideoPlayer extends AppCompatActivity {
    VideoView vid;
    MediaController md;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        vid = (VideoView) findViewById(R.id.videoView);
        Uri uri = Uri.parse(url);
        vid.setVideoURI(uri);
        vid.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                vid.start();
            }
        });
        vid.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                vid.stopPlayback();
            }
        });
        vid.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });

    }

}
