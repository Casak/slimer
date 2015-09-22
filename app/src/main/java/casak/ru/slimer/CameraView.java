package casak.ru.slimer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.Gravity;
import android.view.TextureView;
import android.widget.FrameLayout;
import java.io.IOException;

public class CameraView extends TextureView implements TextureView.SurfaceTextureListener {
    private Camera mCamera;

    public CameraView(Context context){
        super(context);
        setSurfaceTextureListener(this);
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1, int arg2) {
        mCamera = Camera.open(1);
        setLayoutParams(new FrameLayout.LayoutParams(1280, 800, Gravity.CENTER));
        try {
            mCamera.setPreviewTexture(arg0);
        }
        catch (IOException t) {//TODO Write an exception handler
        }

        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(720, 480);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }


    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
        mCamera.stopPreview();
        mCamera.release();
        return true;
    }


    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1,
                                            int arg2) {
        // TODO Auto-generated method stub
    }


    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture arg0) {
        // TODO Auto-generated method stub
    }


}
