package com.google.appinventor.components.runtime;

import android.content.Context;
import android.os.Environment;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.YaVersion;
import com.google.appinventor.components.runtime.udoo.UdooFaceTracker;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A component that interfaces with Android Vision API
 *
 * @author harlem88.ant@gmail.com
 */
@DesignerComponent(version = YaVersion.UDOO_PROXIMITY_SENSOR_COMPONENT_VERSION,
  description = "A component that uses Google Vision frameworks.",
  category = ComponentCategory.MACHINELEARNING,
  nonVisible = true,
  iconName = "images/udooEye.png")
@SimpleObject
@UsesLibraries(libraries = "play-services-tasks.jar,play-services-base.jar,play-services-basement.jar,play-services-vision.jar")
public class UdooVision extends AndroidNonvisibleComponent implements OnDestroyListener, UdooFaceTracker.UDOOFaceTrackerEvent
{
  private final String TAG = "UdooVision";
  private CameraSource mCameraSource = null;
  private final AtomicBoolean isCameraStarted;
  private float mSmileProbability, mEyeLeftProbability, mRigthEyeProbability;

  public UdooVision(Form form) {
    super(form);
    isCameraStarted = new AtomicBoolean(false);
    form.registerForOnDestroy(this);
  }

  @SimpleFunction
  public void Start() {
    createCameraSource();
    startCameraSource();
  }

  @Override
  public void onDestroy() {
    Stop();
  }

  @SimpleFunction
  public void Stop() {
    if (mCameraSource != null) {
      mCameraSource.release();
      isCameraStarted.set(false);
    }
  }

  @SimpleProperty()
  public float LeftEye() {
    return mEyeLeftProbability;
  }

  @SimpleProperty()
  public float RigthEye() {
    return mRigthEyeProbability;
  }

  @SimpleProperty()
  public float Smile() {
    return mSmileProbability;
  }

  @Override
  public void onSmileProbabilityListener(float value) {
    mSmileProbability = value;
  }

  @Override
  public void onLeftEyeProbabilityListener(float value) {
    mEyeLeftProbability = value;
  }

  @Override
  public void onRightEyeProbabilityListener(float value) {
    mRigthEyeProbability = value;
  }

  @SimpleFunction
  public void TakePicture() {
    if (mCameraSource != null) {
      mCameraSource.takePicture(new CameraSource.ShutterCallback() {
        @Override
        public void onShutter() {}
      }, new CameraSource.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes) {
          FileOutputStream outStream;
          try {
            java.io.File imagesFolder = new java.io.File(Environment.getExternalStorageDirectory(), "/UDOO");
            imagesFolder.mkdirs();
            String fileName = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + ".jpg";
            java.io.File output = new java.io.File(imagesFolder, fileName);
            outStream = new FileOutputStream(output);
            outStream.write(bytes);
            outStream.close();
            AfterPicture(output.getAbsolutePath());
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
          } finally {}
        }
      });
    }
  }

  /**
   * Indicates that a photo was taken with the camera and provides the path to
   * the stored picture.
   */
  @SimpleEvent
  public void AfterPicture(String image) {
    EventDispatcher.dispatchEvent(this, "AfterPicture", image);
  }
  
  private void createCameraSource() {
    if (mCameraSource == null) {
      Context context = this.form.getBaseContext();
      FaceDetector detector = new FaceDetector.Builder(context)
        .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
        .build();

      UdooFaceTracker.UDOOFaceTrackerFactory udooFaceTrackerFactory = new UdooFaceTracker.UDOOFaceTrackerFactory();
      udooFaceTrackerFactory.setUDOOTrackEvent(this);
      detector.setProcessor(new MultiProcessor.Builder<Face>(udooFaceTrackerFactory)
        .build());

      mCameraSource = new CameraSource.Builder(context, detector)
        .setRequestedPreviewSize(640, 480)
        .setFacing(CameraSource.CAMERA_FACING_BACK)
        .setRequestedFps(30.0f)
        .build();
    }
  }

  private void startCameraSource() {
    if (mCameraSource != null || !isCameraStarted.get()) {
      try {
        mCameraSource.start();
        isCameraStarted.set(true);
      } catch (IOException e) {
        mCameraSource.release();
        mCameraSource = null;
        isCameraStarted.set(false);
      }
    }
  }
}
