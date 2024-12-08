package com.saurabh.homepage;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.*;
import android.view.MenuItem;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.saurabh.homepage.yolohelper.BoundingBox;
import com.saurabh.homepage.yolohelper.Constants;
import com.saurabh.homepage.yolohelper.Detector;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity implements Detector.DetectorListener {

    final int REQUEST_IMAGE_PICK = 0;
    final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView information;
    private FrameLayout action_pick;
    private FrameLayout action_take;
    private LoadingDialog loadingDialog;
    private Button detect;
    int imageSize = 224;
    private Bitmap imageBitmap = null;
    private ResultDialog dialog;
    private int classId = -1;
    private Detector detector ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        }
        dialog = new ResultDialog(MainActivity.this);
        loadingDialog = new LoadingDialog(MainActivity.this);
        detector = new Detector(getApplicationContext(), Constants.MODEL_PATH, Constants.LABELS_PATH,this);
        detector.setup();
        initView();
        initListener();
    }

    private void initView() {
        information = findViewById(R.id.information_avatar);
        action_pick = findViewById(R.id.album_pick);
        action_take = findViewById(R.id.camera_pick);
        detect = (Button) findViewById(R.id.detect);
        dialog.initDialog();

    }

    private void initListener() {
        information.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                                               startActivity(intent);
                                           }
                                       }
        );
        action_take.setOnClickListener(new View.OnClickListener() {

                                           @Override
                                           public void onClick(View view) {

                                               Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                               try {
                                                   startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                               } catch (ActivityNotFoundException e) {
                                                   Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                               }
                                           }
                                       }
        );
        action_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE_PICK);
            }
        });
        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.show();
                if (imageBitmap == null) {
                    Toast.makeText(MainActivity.this, "Không tìm thấy ảnh", Toast.LENGTH_SHORT).show();
                    loadingDialog.hide();
                    return;
                }
                try {
                    detector.detect(imageBitmap);
                    loadingDialog.hide();
                } catch (Exception e) {
                    Log.d("TAG", e.toString());
                }
            }
        });
    }
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

//    public void runAsyncTask() {
//        // Run task in the background
//        executorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                // Background work
//                String result = classifier(imageBitmap);
//                // Post result back to the main thread
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        // UI work here, update the UI with the result
//                        loadingDialog.hide();
//                        if (result != null) {
//                            dialog.show(result);
//                        } else {
//                            Toast.makeText(MainActivity.this, "Bệnh khác", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        });
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MainActivity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK) {
                if (data != null) {
                    Uri uri = data.getData();
                    try {
                        imageBitmap = uriToBitmap(uri);
                        updateImage();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Lỗi chuyển đổi", Toast.LENGTH_SHORT).show();
                    }
                }

            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                try {
                    Bundle extras = data.getExtras();
                    imageBitmap = (Bitmap) extras.get("data");
                    updateImage();
                } catch (Exception e) {
                    Toast.makeText(this, "Null", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public Bitmap uriToBitmap(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        try {
            InputStream inputStream = contentResolver.openInputStream(uri);
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            return null;
        }
    }

    private void updateImage() {
        if (imageBitmap == null) return;
        else {
            ImageView a = (ImageView) findViewById(R.id.imageView5);
            a.setImageBitmap(imageBitmap);
        }
    }

    public void saveBitmap(Context context, Bitmap handBitmap) {
        if (Build.VERSION.SDK_INT >= 29) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + "SignLanguage");
            values.put(MediaStore.Images.Media.IS_PENDING, true);

            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri)) {
                    saveImageToStream(handBitmap, outputStream);
                    values.put(MediaStore.Images.Media.IS_PENDING, false);
                    context.getContentResolver().update(uri, values, null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            File directory = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "SignLanguage");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = System.currentTimeMillis() + ".png";
            File file = new File(directory, fileName);

            try (OutputStream outputStream = new FileOutputStream(file)) {
                saveImageToStream(handBitmap, outputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (file.getAbsolutePath() != null) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }
        }
    }

    private static void saveImageToStream(Bitmap bitmap, OutputStream outputStream) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private TensorImage preprocessInputImage(Bitmap image) {

        int size = Math.min(image.getWidth(), image.getHeight());
        ImageProcessor.Builder imageProcessor = new ImageProcessor.Builder();

        imageProcessor.add(new ResizeWithCropOrPadOp(size, size));
        imageProcessor.add(new ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR));

        ImageProcessor img = imageProcessor.build();

        TensorImage tensorImage = new TensorImage(DataType.FLOAT32);

        tensorImage.load(image);

        TensorImage a = img.process(tensorImage);


        return img.process(tensorImage);
    }

    @Override
    public void onEmptyDetect() {
        Log.d("TAG", "empty");
        dialog.classId = "None";
        dialog.show("None");
    }

    @Override
    public void onDetect(@NonNull List<BoundingBox> boundingBoxes, long inferenceTime) {
        if(boundingBoxes.isEmpty()){
            dialog.classId = "None";
            dialog.show("None");
        }
        boundingBoxes.sort(new Comparator<BoundingBox>() {
            @Override
            public int compare(BoundingBox o1, BoundingBox o2) {
                if(o1.getCnf() < o2.getCnf()){
                    return 1;
                }
                return -1;
            }
        });
        BoundingBox a = boundingBoxes.get(0);
        for(BoundingBox x : boundingBoxes){
            Log.d("TAG", x.getClsName() + x.getCnf());
        }
        Log.d("TAG", boundingBoxes.size()+"");
        String tm = a.getClsName();
        if(tm.equals("0")){
            tm = "Quả cam";
        }else if(tm.equals("1")){
            tm = "Quả cam";
        }else{
            tm = "Quả cam";
        }
        dialog.show(tm);
        dialog.classId = a.getClsName();

    }
//    private String classifyImage(Bitmap image) throws IOException {
//        if (image == null) {
//            return "Image Error";
//        }
//        image = cropBitmapTo224x224(image);
//        DragonfruiltFloat16 model = DragonfruiltFloat16.newInstance(getApplicationContext());
//
//        // Creates inputs for reference.
//        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1,224,224, 3}, DataType.FLOAT32);
//        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3);
//        byteBuffer.order(ByteOrder.nativeOrder());
//
//        //get 1d array of 224*224 pixels in image
////        int[] intValue = new int[imageSize * imageSize];
////        image.getPixels(intValue, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
////
////        // Iterate Over pixel and extract R,G,B value , add to bytebuffer
////        int pixel = 0;
////        for (int i = 0; i < imageSize; i++) {
////            for (int j = 0; j < imageSize; j++) {
////                int val = intValue[pixel++];//RGB
////                byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
////                byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
////                byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
////            }
////        }
////        inputFeature0.loadBuffer(byteBuffer);
//        DragonfruiltFloat16.Outputs outputs = model.process(TensorImage.fromBitmap(image));
//        List<Category> output = outputs.getOutputAsCategoryList();
////        System.out.println(output.size());
////        float[] confidence = outputFeatur////////////////////////////////////////////////////////////////////////////////////`/e0.getFloatArray();
////
////        System.out.println(confidence.length);
////        //find index of class with biggest confidence
////        int maxPos = 0;
////        float maxConfidence = 0;
////        for (int i = 0; i < confidence.length; i++) {
////            if (confidence[i] > maxConfidence) {
////                maxConfidence = confidence[i];
////                maxPos = i;
////            }
////        }
////        String[] classes = {
////                "Kiến",
////                "Bọ trĩ",
////                "Rệp sáp",
////                "Bọ cánh cứng",
////                "Sên trần",
////                "ốc sên",
////                "ốc ma"
////        };
//        model.close();
//        return "";
//    }
}