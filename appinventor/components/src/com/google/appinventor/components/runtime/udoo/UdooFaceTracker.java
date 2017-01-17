package com.google.appinventor.components.runtime.udoo;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class UdooFaceTracker extends Tracker<Face> {
  
  private UDOOFaceTrackerEvent mEvent;

  public interface UDOOFaceTrackerEvent {
    void onSmileProbabilityListener(float value);
    void onLeftEyeProbabilityListener(float value);
    void onRightEyeProbabilityListener(float valuer);
  }

  public void setEvent(UDOOFaceTrackerEvent event) {
    mEvent = event;
  }
  
  @Override
  public void onNewItem(int faceId, Face item) {}

  @Override
  public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
    if (mEvent != null) {
      mEvent.onLeftEyeProbabilityListener(face.getIsLeftEyeOpenProbability());
      mEvent.onRightEyeProbabilityListener(face.getIsRightEyeOpenProbability());
      mEvent.onSmileProbabilityListener(face.getIsSmilingProbability());
    }
  }

  @Override
  public void onMissing(FaceDetector.Detections<Face> detectionResults) {}

  @Override
  public void onDone() {}

  public static class UDOOFaceTrackerFactory implements MultiProcessor.Factory<Face> {
    private UDOOFaceTrackerEvent mEvent;

    public void setUDOOTrackEvent(UDOOFaceTrackerEvent mEvent) {
      this.mEvent = mEvent;
    }

    @Override
    public Tracker<Face> create(Face face) {
      UdooFaceTracker udooFaceTracker = new UdooFaceTracker();
      udooFaceTracker.setEvent(mEvent);
      return udooFaceTracker;
    }
  }
}
