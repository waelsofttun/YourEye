package com.example.youreye;


import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.Locale;

public class color_detection extends AppCompatActivity implements View.OnTouchListener, CameraBridgeViewBase.CvCameraViewListener2 {
    //CameraBridgeViewBase basic class, implemente l'interface CvCameraViewListener2  qui assure l'interaction entre Camera et la librairie  OpenCV library
    private CameraBridgeViewBase mOpenCvCameraView;
    //
    private Mat mRgba;
    //Scalar Comme un array pour enregistrer les  valeurs des image multi channel HSV Hue Saturation Value (en français, Teinte Saturation Valeur)
    private Scalar mBlobColorHsv;
    //Un Scalar pour les images en mode RGB red green Bleu
    private Scalar mBlobColorRgba;
    //l'instance de classe Color Util
    private Color_util detectcolor ;
    // coordonnée X et Y de pixel touché
    TextView touch_coordinates;
    // couleur résultat  de pixel touchée
    TextView touch_color;
    //le text a prononcée
    TextToSpeech t1;

    // varibales de coordonnée initiale x et Y
    double x = -1;
    double y = -1;

    //Chargement de librairie Opencv
    //OpenCV Manager est un service Android destiné à gérer
    // les fichiers binaires de bibliothèque OpenCV sur les périphériques des utilisateurs finaux.
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    /*
                     Activer la camera juste aprés le chargement de librairie c 'est un callback

                     */
                    mOpenCvCameraView.enableView();
                    //setOnTouchListener = set listener to camera view
                    mOpenCvCameraView.setOnTouchListener(color_detection.this);
                }
                break;
                default: {
                    //Action Par defaut
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set  layout de l'activity
        setContentView(R.layout.activity_color_detection);
        //Pour empêcher l'écran d'étre desactivé
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //afficher la coordonnée X,Y du toucher
        touch_coordinates = (TextView) findViewById(R.id.touch_coordinates);
        //Couleur de l'endroit où vous touchez
        touch_color = (TextView) findViewById(R.id.touch_color);
        //lié opencv view object to xml component
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.opencv_tutorial_activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        //set listener pour opencvcamera view
        mOpenCvCameraView.setCvCameraViewListener(this);

        //Une instance TextToSpeech ne peut être utilisée pour synthétiser du texte qu'une fois
        // son initialisation terminée.
        // Implémentez TextToSpeech.OnInitListener pour être averti de la fin de l'initialisation.
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                     //set language
                    t1.setLanguage(Locale.UK);

                }
            }
        });
    }


    //Onpause on désactive CameraView
    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
  //On resume on reload opencv
    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }


    //on desactive camera view si l'activité n'existe plus
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
    /*
    Cette méthode est invoquée lorsque l'aperçu de la caméra a démarré.
     Après l'invocation de cette méthode, le cadre
    commencera à être livré au user via callback de onCameraFrame().
    width - -  width de frames affiché
    height - -  height de frames affiché
     */
    @Override
    public void onCameraViewStarted(int width, int height) {
        //initilisation de Matrice
        mRgba = new Mat();
        //8 bits par pixel intervalle [0:255].
        //Scalar color = new Scalar( 255 ) image en nuance noir
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
    }
    /*
    Cette méthode est invoquée lorsque l'aperçu de la caméra a été arrêté pour une raison quelconque.
    dans ce cas on intilise de nouveau la matrice et les couleurs
     */
    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }
    /*
    Methode invoké lors de laffichage de frame on modifie la matrice deja initilisé par une
    autre matrice qui est le résultat de la superposotion de 3 matrices en 1 matrice ou chaque pixel
    présente un objet RGB rgba(red, green, blue, alpha)
     */
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
    /*La valeur de couleur RGBA est spécifiée avec : rgba (rouge, vert, bleu, alpha). Le paramètre alpha est
      un nombre entre 0.0 (entièrement transparent) et 1.0 (entièrement opaque).
        */


        mRgba = inputFrame.rgba();
        return mRgba;
    }
   //Methode invoké lors de la toucher de l'image
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        /*
       Obtenir le nombre de lignes et de colonnes ou (-1, -1) lorsque la matrice a plus de 2 dimensions
         */
        int cols = mRgba.cols();
        int rows = mRgba.rows();
        /*
        getHeight() =  height de view, en pixel.
        getwidth()  =  Width de view, en pixel.

        on a pris une patie de l'ecran pour avoir y de début de limage et y de fin d'image
         */
        double yLow = (double)mOpenCvCameraView.getHeight() * 0.2401961;
        double yHigh = (double)mOpenCvCameraView.getHeight() * 0.7696078;
        //Trouver L'echelle  dans les directions X et Y
        double xScale = (double)cols / (double)mOpenCvCameraView.getWidth();
        double yScale = (double)rows / (yHigh - yLow);

        /*
        getX() and getY() pour avoir   X/Y de toucher dans l'echelle de screen.
         */
        x = event.getX();
        y = event.getY();
        y = y - yLow;
        //conversion de x et y vers l'echelle de matrice
        x = x * xScale;
        y = y * yScale;
        //si x et y  ne sont pas pas dans l'echelle
        if((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;
        //modifier le text view pour affiché les coordonnée exacte
        touch_coordinates.setText("X: " + Double.valueOf(x) + ", Y: " + Double.valueOf(y));

        // touchedRect est le rectangle touché de coordonnée x et y
      Rect  touchedRect= new Rect((int)x,(int)y,8,8);


        //Extraire  rectangle ou soumatrice de matrice  mrgba Mat to touchedRegionRgba
        Mat touchedRegionRgba = mRgba.submat(touchedRect);
        Mat touchedRegionHsv = new Mat();
        //conversion de image de RGBA a HSV
        /*
          HSV est ainsi nommé pour trois valeurs : Teinte, Saturation et Valeur. Cet espace colorimétrique décrit
          couleurs (teinte ou teinte) en fonction de leur nuance (saturation ou quantité de gris) et de leur
          valeur de luminosité.
          Le HSV est défini d'une manière similaire à la façon dont les humains perçoivent la couleur.
         */
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);
        // Calculate average color of touched region
       //technique d'identification de couleur
        mBlobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width * touchedRect.height;
        for (int i = 0; i < mBlobColorHsv.val.length; i++)
            mBlobColorHsv.val[i] /= pointCount;

        mBlobColorRgba = convertScalarHsv2Rgba(mBlobColorHsv);
        //modifier le texte en convertissant un tableau d'octets en hexadécimal
        /*touch_color.setText("Color: #" + String.format("%02X", (int)mBlobColorRgba.val[0])
                + String.format("%02X", (int)mBlobColorRgba.val[1])
                + String.format("%02X", (int)mBlobColorRgba.val[2]));
        */
        touch_color.setTextColor(Color.rgb((int) mBlobColorRgba.val[0],
                (int) mBlobColorRgba.val[1],
                (int) mBlobColorRgba.val[2]));
        touch_coordinates.setTextColor(Color.rgb((int)mBlobColorRgba.val[0],
                (int)mBlobColorRgba.val[1],
                (int)mBlobColorRgba.val[2]));
        detectcolor = new Color_util();
        //affichage de nom de couleur a partirl de Class Color Name
        String color = detectcolor.getColorNameFromRgb((int)mBlobColorRgba.val[0],(int)mBlobColorRgba.val[1],(int)mBlobColorRgba.val[2]);
        touch_color.setText(color);
        voice(color);
        return false;
    }

    //fonction pour prononcer le nom de couleur
    public void  voice(String x){
        if (x.matches("")) {
            return;
        }
        t1.speak(x, TextToSpeech.QUEUE_FLUSH, null);
    }
    //fonction permet la conversion entre HSV TO RGBA
    private Scalar convertScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        /*
        This is an overloaded member function, provided for convenienc.
        rows =	Number of rows in a 2D array.
        cols =	Number of columns in a 2D array.
        type =	Array type. Use CV_8UC1, ..., CV_64FC4 to create 1-4 channel matrices, or CV_8UC(n),
         ..., CV_64FC(n) to create multi-channel (up to CV_CN_MAX channels) matrices.
         scalar = 	An optional value to initialize each matrix element with.
         */
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        //converting HSV to RGB
        /*
          The function converts an input image from one color space to another. In case of a
          transformation to-from RGB color space, the order of the channels should be specified
          explicitly (RGB or BGR). Note that the default color format in OpenCV is often referred to
          as RGB but it is actually BGR (the bytes are reversed). So the first byte in a standard
         (24-bit) color image will be an 8-bit Blue component, the second byte will be Green, and
          the third byte will be Red. The fourth, fifth, and sixth bytes would then be the second
          pixel (Blue, then Green, then Red), and so on.
          ---here integer 4 = dstCn - Number of channels in the destination image. If the parameter is
          0, the number of the channels is derived automatically from src and code.
         */
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }
}