package com.outlook.gonzasosa.apps.touchingcontrols;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;


import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MyTouchEvent";
    private Canvas canvas;
    private Paint paint;
    private float down_x=0,down_y=0,move_x=0,move_y=0;
    public static int color_red=0,color_blue=0,color_green=0;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.miToolbar);
        toolbar.setTitle("Draw");
        setSupportActionBar(toolbar);

        final ImageView imageView = findViewById (R.id.imageView1);

        //Se obtiene el bitmap del imageView cargado
        Bitmap original = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

        // Como no se puede usar el bitmap original se tiene que hacer una copia para poder editar
        Bitmap mutable = original.copy(Bitmap.Config.ARGB_8888,true);

        //Se crea un lienzo para poder trabajar sobre el
        canvas = new Canvas(mutable);

        //Creamos un objeto de tipo paint para poder dibujar y se le establecen opciones de estética
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);

        //En esta linea es donde se va a recibir como parametro de la otra actividad el color elegido por el usuario
        paint.setARGB(255,color_red,color_green,color_blue);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(20);

        //Se dibuja el nuevo bitmap
        imageView.setImageBitmap(mutable);

        imageView.setOnTouchListener ((view, motionEvent) -> {
            //Como el image view es mas grande que la imagen, había un problema con las coordenadas x ,y
            //Se desfasaban y es por eso que se tiene que usar una Matriz
            paint.setARGB(255,color_red,color_green,color_blue);
            Matrix inverse = new Matrix();
            imageView.getImageMatrix().invert(inverse);
            float[] pts = {
                    motionEvent.getX(), motionEvent.getY()
            };
            inverse.mapPoints(pts);
            String message = "";

            switch (motionEvent.getAction ()) {
                case MotionEvent.ACTION_DOWN:
                    message = String.format (Locale.US, "Touch DOWN on (%.2f, %.2f)", motionEvent.getX (), motionEvent.getY ());
                    //Cuando sea DOWN se toman x, y actuales de la matriz
                    down_x=(float)Math.floor(pts[0]);
                    down_y=(float)Math.floor(pts[1]) ;
                    Log.i (TAG, message);

                    return true;
                case MotionEvent.ACTION_UP:
                    message = String.format (Locale.US, "Touch UP on (%.2f, %.2f)", motionEvent.getX (), motionEvent.getY ());
                    Log.i (TAG, message);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    message = String.format (Locale.US, "MOVING on (%.2f, %.2f)", motionEvent.getX (), motionEvent.getY ());
                    move_x=(float)Math.floor(pts[0]);
                    move_y=(float)Math.floor(pts[1]);
                    //Si se mueve el dedo y es desigual a la posicion original, se dibuja una linea y se actualizan los down
                    if(move_x != down_x & move_y!=down_y){
                        canvas.drawLine(down_x,down_y,move_x,move_y,paint);
                        down_x=move_x;
                        down_y=move_y;
                        imageView.invalidate();
                    }


                    Log.i (TAG, message);
                    break;
            }

            return super.onTouchEvent (motionEvent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.edit) {
            Intent otra = new Intent(this,Main2Activity.class);
            startActivity(otra);
        }
        return super.onOptionsItemSelected(item);
    }


}
