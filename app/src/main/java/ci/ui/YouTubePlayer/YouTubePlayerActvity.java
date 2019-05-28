package ci.ui.YouTubePlayer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.chinaairlines.mobile30.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import ci.ui.define.UiMessageDef;
import ci.ui.define.ViewScaleDef;

/**
 * Created by kevincheng on 2016/4/8.
 */
public class YouTubePlayerActvity extends YouTubeBaseActivity
    implements YouTubePlayer.OnInitializedListener{
    private static final int    RECOVERY_DIALOG_REQUEST = 1;
    private static final String DEF_YOUTUBE_ID          = "znQmmYnDJsU";
    private String              m_strYouTubeId          = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_player);
        Bundle bundle = getIntent().getExtras();
        if(null != bundle){
            m_strYouTubeId = bundle.getString(UiMessageDef.BUNDLE_ACTIVITY_DATA_YOUTUBE_ID);
        }
        m_strYouTubeId = m_strYouTubeId != null ? m_strYouTubeId : DEF_YOUTUBE_ID ;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(getString(R.string.youtube_api_key), this);
        ViewScaleDef scaleDef = ViewScaleDef.getInstance(this);
        scaleDef.selfAdjustAllView(findViewById(R.id.root));
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
            player.setFullscreen(true);
            player.setShowFullscreenButton(false);
            player.cueVideo(m_strYouTubeId);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(getString(R.string.error_player), errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(getString(R.string.youtube_api_key), this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider(){
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }

}
